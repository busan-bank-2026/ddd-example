package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ddd.catalog.application.DecreaseStockHandler;
import ddd.catalog.domain.Product;
import ddd.catalog.domain.ProductRepository;
import ddd.catalog.infrastructure.MemoryProductRepository;
import ddd.member.domain.Member;
import ddd.member.domain.MemberGrade;
import ddd.member.domain.MemberRepository;
import ddd.member.infrastructure.MemoryMemberRepository;
import ddd.order.application.EventPublisher;
import ddd.order.application.OrderService;
import ddd.order.application.OrderItemRequest;
import ddd.order.application.OrderRequest;
import ddd.order.domain.Address;
import ddd.order.domain.DiscountPolicy;
import ddd.order.domain.Order;
import ddd.order.domain.OrderItem;
import ddd.order.domain.OrderItemFactory;
import ddd.order.domain.OrderPlaced;
import ddd.order.domain.OrderRepository;
import ddd.order.domain.OrderStatus;
import ddd.order.infrastructure.LegacyPaymentAdapter;
import ddd.order.infrastructure.MemoryOrderRepository;
import legacypay.LegacyPayGateway;

class OrderEventsTest {

    private static ProductRepository products() {
        ProductRepository products = new MemoryProductRepository();
        products.save(new Product(1, "자바 책", 15000, 10));
        products.save(new Product(2, "볼펜", 1000, 10));
        return products;
    }

    private static MemberRepository members() {
        MemberRepository members = new MemoryMemberRepository();
        members.save(new Member(1, "김철수", MemberGrade.VIP));
        return members;
    }

    private static List<OrderItemRequest> bookAndPen() {
        return List.of(new OrderItemRequest(1, 2), new OrderItemRequest(2, 1));
    }

    @Test
    @DisplayName("S1 상품은 재고를 가진다. 상품을 만들 때 재고를 정하고 물으면 답한다. Main은 자바 책을 재고 10개로 저장한다")
    void s1() {
        Product book = new Product(1, "자바 책", 15000, 10);
        assertEquals(10, book.getStock());
        assertEquals(3, new Product(2, "볼펜", 1000, 3).getStock());
    }

    @Test
    @DisplayName("S2 주문은 만들어질 때 주문 접수됨 이벤트를 남긴다. 이벤트는 주문 번호와 주문 항목을 담는다. 주문은 상품 재고를 직접 줄이지 않는다. Main이 자바 책 2개 주문 뒤 남긴 이벤트 수와 자바 책 재고를 전 -> 후로 보여준다")
    void s2() {
        List<OrderItem> orderLines = List.of(new OrderItem(1, "자바 책", 15000, 2),
                new OrderItem(2, "볼펜", 1000, 1));
        Order order = new Order(7, 1, orderLines, new Address("서울시 강남구"));

        assertEquals(1, order.getEvents().size());
        OrderPlaced event = (OrderPlaced) order.getEvents().get(0);
        assertEquals(7L, event.orderId());
        assertEquals(orderLines, event.items());
        assertEquals(new OrderPlaced(7, orderLines), event);
        assertTrue(OrderPlaced.class.isRecord());
        assertEquals("ddd.order.domain", OrderPlaced.class.getPackageName());

        List<Class<?>> fieldTypes = Arrays.stream(Order.class.getDeclaredFields()).map(Field::getType).toList();
        assertFalse(fieldTypes.contains(Product.class));
        assertFalse(fieldTypes.contains(ProductRepository.class));
    }

    @Test
    @DisplayName("S3 주문 응용 서비스는 주문을 저장한 뒤 주문이 남긴 이벤트를 이벤트 발행기에 넘긴다. 이벤트 발행기는 받은 이벤트를 쌓아 두었다가, 이벤트를 처리할 때 구독한 이벤트 처리기마다 한 번씩 보낸다. 이벤트 처리기는 받은 이벤트 중 자기 것만 골라 처리한다")
    void s3() {
        EventPublisher publisher = new EventPublisher();
        List<Object> received = new ArrayList<>();
        publisher.subscribe(received::add);

        OrderPlaced event = new OrderPlaced(3, List.of(new OrderItem(1, "자바 책", 15000, 1)));
        publisher.publish(List.of(event));
        assertTrue(received.isEmpty());
        publisher.processEvents();
        assertEquals(List.of(event), received);
        publisher.processEvents();
        assertEquals(1, received.size());
        assertEquals("ddd.order.application", EventPublisher.class.getPackageName());

        Class<?>[] parameters = OrderService.class.getConstructors()[0].getParameterTypes();
        assertEquals(EventPublisher.class, parameters[4]);

        EventPublisher servicePublisher = new EventPublisher();
        List<OrderPlaced> placed = new ArrayList<>();
        servicePublisher.subscribe(published -> {
            if (published instanceof OrderPlaced orderPlaced) {
                placed.add(orderPlaced);
            }
        });
        OrderRepository orders = new MemoryOrderRepository();
        OrderService service = new OrderService(orders, new OrderItemFactory(products()),
                members(), new DiscountPolicy(), servicePublisher, new LegacyPaymentAdapter(new LegacyPayGateway()), message -> {});
        long orderId = service.placeOrder(new OrderRequest(1, bookAndPen(), "서울시 강남구"));

        assertTrue(orders.findById(orderId).isPresent());
        assertTrue(placed.isEmpty());
        servicePublisher.processEvents();
        assertEquals(1, placed.size());
        assertEquals(orderId, placed.get(0).orderId());
        assertEquals(2, placed.get(0).items().size());
    }

    @Test
    @DisplayName("S4 재고 줄이기 처리기는 주문 접수됨 이벤트를 받아 주문 항목마다 상품 재고를 수량만큼 줄이고 저장한다. 이벤트를 처리하기 전에는 주문만 저장되고 재고는 그대로이고, 처리한 뒤에 재고가 맞춰진다. Main이 이벤트 처리 전 -> 후 자바 책 재고를 보여준다")
    void s4() {
        Product book = new Product(1, "자바 책", 15000, 10);
        book.decreaseStock(3);
        assertEquals(7, book.getStock());

        ProductRepository handlerProducts = products();
        new DecreaseStockHandler(handlerProducts).handle(new OrderPlaced(1, List.of(
                new OrderItem(1, "자바 책", 15000, 4), new OrderItem(2, "볼펜", 1000, 1))));
        assertEquals(6, handlerProducts.findById(1).orElseThrow().getStock());
        assertEquals(9, handlerProducts.findById(2).orElseThrow().getStock());
        assertEquals("ddd.catalog.application", DecreaseStockHandler.class.getPackageName());

        ProductRepository products = products();
        EventPublisher publisher = new EventPublisher();
        DecreaseStockHandler handler = new DecreaseStockHandler(products);
        publisher.subscribe(handler);
        OrderRepository orders = new MemoryOrderRepository();
        OrderService service = new OrderService(orders, new OrderItemFactory(products),
                members(), new DiscountPolicy(), publisher, new LegacyPaymentAdapter(new LegacyPayGateway()), message -> {});
        long orderId = service.placeOrder(new OrderRequest(1, bookAndPen(), "서울시 강남구"));

        assertEquals(OrderStatus.ORDERED, orders.findById(orderId).orElseThrow().getStatus());
        assertEquals(10, products.findById(1).orElseThrow().getStock());
        assertEquals(10, products.findById(2).orElseThrow().getStock());
        publisher.processEvents();
        assertEquals(8, products.findById(1).orElseThrow().getStock());
        assertEquals(9, products.findById(2).orElseThrow().getStock());
        assertEquals(OrderStatus.ORDERED, orders.findById(orderId).orElseThrow().getStatus());
    }

    @Test
    @DisplayName("S5 용어집에 재고, 주문 접수됨, 이벤트 발행기, 이벤트 처리기, 재고 줄이기, 재고 줄이기 처리기를 더하고 상품 줄에 재고를 넣는다")
    void s5() throws Exception {
        String glossary = Files.readString(Path.of("glossary.md"));

        assertTrue(glossary.contains("| 재고 | stock |"));
        assertTrue(glossary.contains("| 주문 접수됨 | OrderPlaced |"));
        assertTrue(glossary.contains("| 이벤트 발행기 | EventPublisher |"));
        assertTrue(glossary.contains("| 재고 줄이기 | decreaseStock |"));
        assertTrue(glossary.contains("| 재고 줄이기 처리기 | DecreaseStockHandler |"));
        String productLine = glossary.lines().filter(line -> line.startsWith("| 상품 | Product |")).findFirst().orElseThrow();
        assertTrue(productLine.contains("재고"));
    }
}
