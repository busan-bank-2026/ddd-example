package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.RecordComponent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ddd.catalog.domain.Product;
import ddd.catalog.domain.ProductRepository;
import ddd.catalog.infrastructure.MemoryProductRepository;
import ddd.member.domain.Member;
import ddd.member.domain.MemberGrade;
import ddd.member.domain.MemberRepository;
import ddd.member.infrastructure.MemoryMemberRepository;
import ddd.order.application.EventPublisher;
import ddd.order.application.OrderService;
import ddd.order.application.MessageSender;
import ddd.order.application.OrderItemRequest;
import ddd.order.application.OrderRequest;
import ddd.order.domain.DiscountPolicy;
import ddd.order.domain.Order;
import ddd.order.domain.OrderItem;
import ddd.order.domain.OrderItemFactory;
import ddd.order.domain.OrderRepository;
import ddd.order.domain.PaymentGateway;
import ddd.order.infrastructure.LegacyPaymentAdapter;
import ddd.order.infrastructure.MemoryOrderRepository;
import legacypay.LegacyPayGateway;

class OrderFactoryTest {

    private static MemberRepository members() {
        MemberRepository members = new MemoryMemberRepository();
        members.save(new Member(1, "김철수", MemberGrade.BASIC));
        members.save(new Member(2, "이영희", MemberGrade.BASIC));
        return members;
    }

    private static ProductRepository products() {
        ProductRepository products = new MemoryProductRepository();
        products.save(new Product(1, "자바 책", 15000, 10));
        products.save(new Product(2, "볼펜", 1000, 10));
        products.save(new Product(3, "공책", 2000, 10));
        return products;
    }

    private static String text(OrderItem item) {
        return item.getProductId() + " " + item.getProductName() + " " + item.getPrice() + " " + item.getQuantity();
    }

    @Test
    @DisplayName("S1 주문 항목 요청은 상품 번호와 수량만 담는다. 상품 이름과 가격은 요청에 넣지 않는다")
    void s1() {
        OrderItemRequest command = new OrderItemRequest(2, 3);
        assertEquals(2L, command.productId());
        assertEquals(3, command.quantity());

        RecordComponent[] components = OrderItemRequest.class.getRecordComponents();
        assertEquals(List.of("productId", "quantity"),
                Arrays.stream(components).map(RecordComponent::getName).toList());
        assertEquals(List.of(long.class, int.class),
                Arrays.stream(components).map(RecordComponent::getType).toList());
    }

    @Test
    @DisplayName("S2 주문 항목 팩토리는 상품 번호와 수량을 받아 상품 저장소에서 상품을 찾고, 그 상품의 이름과 지금 가격을 담은 주문 항목을 만든다. 팩토리는 도메인 계층에 둔다")
    void s2() {
        ProductRepository products = products();
        OrderItemFactory factory = new OrderItemFactory(products);

        assertEquals("1 자바 책 15000 2", text(factory.create(1, 2)));
        assertEquals("2 볼펜 1000 1", text(factory.create(2, 1)));

        products.save(new Product(1, "자바 책", 18000, 10));
        assertEquals("1 자바 책 18000 2", text(factory.create(1, 2)));

        assertEquals("ddd.order.domain", OrderItemFactory.class.getPackageName());
    }

    @Test
    @DisplayName("S3 주문 응용 서비스는 주문하기와 주문 항목 더하기에서 주문 항목 팩토리에게 주문 항목을 만들게 한다. Main은 팩토리를 만들어 주문 응용 서비스에 넘기고, 앞 장 출력 열아홉 줄은 그대로다")
    void s3() {
        assertEquals(List.of(OrderRepository.class, OrderItemFactory.class, MemberRepository.class, DiscountPolicy.class, EventPublisher.class,
                PaymentGateway.class, MessageSender.class),
                List.of(OrderService.class.getConstructors()[0].getParameterTypes()));

        OrderRepository orders = new MemoryOrderRepository();
        OrderService service = new OrderService(orders, new OrderItemFactory(products()), members(), new DiscountPolicy(), new EventPublisher(), new LegacyPaymentAdapter(new LegacyPayGateway()), message -> {});
        long orderId = service.placeOrder(new OrderRequest(1, List.of(
                new OrderItemRequest(1, 2),
                new OrderItemRequest(2, 1)), "서울시 강남구"));
        service.addItem(orderId, new OrderItemRequest(3, 5));

        Order saved = orders.findById(orderId).orElseThrow();
        assertEquals(List.of("1 자바 책 15000 2", "2 볼펜 1000 1", "3 공책 2000 5"),
                saved.getItems().stream().map(OrderFactoryTest::text).toList());
        assertEquals(41000, saved.getTotalPrice());
    }

    @Test
    @DisplayName("S4 없는 상품 번호로 주문하거나 항목을 더하면 상품이 없습니다로 거절하고 아무것도 저장하지 않는다. Main이 없는 상품 주문 장면을 보여준다")
    void s4() {
        OrderRepository orders = new MemoryOrderRepository();
        OrderService service = new OrderService(orders, new OrderItemFactory(products()), members(), new DiscountPolicy(), new EventPublisher(), new LegacyPaymentAdapter(new LegacyPayGateway()), message -> {});

        IllegalArgumentException placing = assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(new OrderRequest(1, List.of(new OrderItemRequest(100, 1)), "서울시 강남구")));
        assertEquals("상품이 없습니다", placing.getMessage());
        assertTrue(orders.findById(1).isEmpty());

        long orderId = service.placeOrder(new OrderRequest(1, List.of(new OrderItemRequest(1, 2)), "서울시 강남구"));
        IllegalArgumentException adding = assertThrows(IllegalArgumentException.class,
                () -> service.addItem(orderId, new OrderItemRequest(100, 1)));
        assertEquals("상품이 없습니다", adding.getMessage());
        assertEquals(1, orders.findById(orderId).orElseThrow().getItems().size());
    }

    @Test
    @DisplayName("S5 용어집에 주문 항목 팩토리를 더하고 주문 항목 요청의 뜻을 상품 번호와 수량으로 바꾼다")
    void s5() throws Exception {
        String glossary = Files.readString(Path.of("glossary.md"));

        assertTrue(glossary.contains("| 주문 항목 팩토리 | OrderItemFactory |"));
        assertTrue(glossary.contains("| 주문 항목 요청 | OrderItemRequest | 주문 항목을 만들 상품 번호, 수량 묶음 |"));
    }
}
