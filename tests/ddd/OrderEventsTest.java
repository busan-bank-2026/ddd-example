package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
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
import ddd.order.application.OrderApplicationService;
import ddd.order.application.OrderLineCommand;
import ddd.order.application.PlaceOrderCommand;
import ddd.order.domain.Address;
import ddd.order.domain.DiscountPolicy;
import ddd.order.domain.Order;
import ddd.order.domain.OrderLine;
import ddd.order.domain.OrderLineFactory;
import ddd.order.domain.OrderPlaced;
import ddd.order.domain.OrderRepository;
import ddd.order.domain.OrderStatus;
import ddd.order.infrastructure.LegacyPaymentAdapter;
import ddd.order.infrastructure.MemoryOrderRepository;
import ddd.shared.Money;
import legacypay.LegacyPayGateway;

class OrderEventsTest {

    private static final String EXPECTED = String.join(System.lineSeparator(),
            "온라인 주문 프로그램을 시작합니다",
            "주문: 1번, 김철수, 31000원, 주문 접수",
            "주문 항목: 자바 책 2개, 30000원",
            "주문 항목: 볼펜 1개, 1000원",
            "주문 뒤: 이벤트 1개, 자바 책 재고 10개",
            "이벤트 처리 뒤: 자바 책 재고 8개",
            "결제: 결제 완료",
            "할인: VIP 3100원, 결제 금액 27900원",
            "배송비: 3000원",
            "배송: 배송 중",
            "취소: 배송 중인 주문은 취소할 수 없습니다",
            "결제 없이 배송: 주문 접수인 주문은 배송할 수 없습니다",
            "음수 금액 주문: 금액은 0원 이상이어야 합니다",
            "빈 주소 주문: 배송 주소를 입력해야 합니다",
            "항목 더하기: 항목 3개, 주문 금액 41000원",
            "결제 뒤 항목 더하기: 주문 접수인 주문에만 항목을 더할 수 있습니다",
            "빈 주문: 주문 항목이 하나 이상 있어야 합니다",
            "0개 항목 주문: 수량은 1개 이상이어야 합니다",
            "두 번 결제: 결제 완료인 주문은 결제할 수 없습니다",
            "배송 완료: 배송 완료",
            "없는 주문 결제: 주문이 없습니다",
            "없는 상품 주문: 상품이 없습니다",
            "저장 안 한 취소: 주문 접수",
            "6만원 주문 배송비: 서울 0원, 제주 3000원",
            "가격 바뀐 뒤: 1번 상품 18000원, 주문 항목 15000원, 주문 금액 31000원",
            "동시 수정: 다른 곳에서 먼저 바꾼 주문입니다",
            "결제 완료 메시지: ORDER_PAID|orderId=1|address=서울시 강남구",
            "결제 완료 메시지: ORDER_PAID|orderId=4|address=광주시 북구",
            "배송 컨텍스트: 1번 주문, 서울시 강남구",
            "외부 결제 응답: 27900원 0000, 108000원 E101",
            "한도 넘은 결제: 결제 한도를 넘었습니다, 주문 접수") + System.lineSeparator();

    private String runMain() {
        PrintStream original = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
        try {
            Main.main(new String[0]);
        } finally {
            System.setOut(original);
        }
        return out.toString(StandardCharsets.UTF_8);
    }

    private static ProductRepository products() {
        ProductRepository products = new MemoryProductRepository();
        products.save(new Product(1, "자바 책", new Money(15000), 10));
        products.save(new Product(2, "볼펜", new Money(1000), 10));
        return products;
    }

    private static MemberRepository members() {
        MemberRepository members = new MemoryMemberRepository();
        members.save(new Member(1, "김철수", MemberGrade.VIP));
        return members;
    }

    private static List<OrderLineCommand> bookAndPen() {
        return List.of(new OrderLineCommand(1, 2), new OrderLineCommand(2, 1));
    }

    @Test
    @DisplayName("S1 상품은 재고를 가진다. 상품을 만들 때 재고를 정하고 물으면 답한다. Main은 상품마다 재고 10개로 저장한다")
    void s1() throws Exception {
        Product book = new Product(1, "자바 책", new Money(15000), 10);
        assertEquals(10, book.getStock());
        assertEquals(3, new Product(2, "볼펜", new Money(1000), 3).getStock());

        String main = Files.readString(Path.of("src/ddd/Main.java"));
        int firstOrder = main.indexOf("placeOrder(");
        for (String product : List.of("new Product(1, \"자바 책\", new Money(15000), 10)",
                "new Product(2, \"볼펜\", new Money(1000), 10)", "new Product(3, \"공책\", new Money(2000), 10)")) {
            assertTrue(main.indexOf(product) > 0);
            assertTrue(main.indexOf(product) < firstOrder);
        }
    }

    @Test
    @DisplayName("S2 주문은 만들어질 때 주문 접수됨 이벤트를 남긴다. 이벤트는 주문 번호와 주문 항목을 담는다. 주문은 상품 재고를 직접 줄이지 않는다. Main이 첫 주문 항목 다음에 남긴 이벤트 수와 자바 책 재고를 보여준다")
    void s2() {
        List<OrderLine> orderLines = List.of(new OrderLine(1, "자바 책", new Money(15000), 2),
                new OrderLine(2, "볼펜", new Money(1000), 1));
        Order order = new Order(7, 1, orderLines, new Address("서울시 강남구"));

        assertEquals(1, order.getEvents().size());
        OrderPlaced event = (OrderPlaced) order.getEvents().get(0);
        assertEquals(7L, event.orderId());
        assertEquals(orderLines, event.orderLines());
        assertEquals(new OrderPlaced(7, orderLines), event);
        assertTrue(OrderPlaced.class.isRecord());
        assertEquals("ddd.order.domain", OrderPlaced.class.getPackageName());

        List<Class<?>> fieldTypes = Arrays.stream(Order.class.getDeclaredFields()).map(Field::getType).toList();
        assertFalse(fieldTypes.contains(Product.class));
        assertFalse(fieldTypes.contains(ProductRepository.class));

        List<String> lines = List.of(runMain().split(System.lineSeparator()));
        int penLine = lines.indexOf("주문 항목: 볼펜 1개, 1000원");
        assertTrue(penLine > 0);
        assertEquals("주문 뒤: 이벤트 1개, 자바 책 재고 10개", lines.get(penLine + 1));
    }

    @Test
    @DisplayName("S3 주문 응용 서비스는 주문을 저장한 뒤 주문이 남긴 이벤트를 이벤트 발행기에 넘긴다. 이벤트 발행기는 받은 이벤트를 쌓아 두었다가, 이벤트를 처리할 때 그 이벤트를 구독한 곳에 한 번씩 보낸다")
    void s3() {
        EventPublisher publisher = new EventPublisher();
        List<OrderPlaced> received = new ArrayList<>();
        List<String> others = new ArrayList<>();
        publisher.subscribe(OrderPlaced.class, received::add);
        publisher.subscribe(String.class, others::add);

        OrderPlaced event = new OrderPlaced(3, List.of(new OrderLine(1, "자바 책", new Money(15000), 1)));
        publisher.publish(List.of(event));
        assertTrue(received.isEmpty());
        publisher.processEvents();
        assertEquals(List.of(event), received);
        assertTrue(others.isEmpty());
        publisher.processEvents();
        assertEquals(1, received.size());
        assertEquals("ddd.order.application", EventPublisher.class.getPackageName());

        Class<?>[] parameters = OrderApplicationService.class.getConstructors()[0].getParameterTypes();
        assertEquals(EventPublisher.class, parameters[parameters.length - 1]);

        EventPublisher servicePublisher = new EventPublisher();
        List<OrderPlaced> placed = new ArrayList<>();
        servicePublisher.subscribe(OrderPlaced.class, placed::add);
        OrderRepository orders = new MemoryOrderRepository();
        OrderApplicationService service = new OrderApplicationService(orders, new OrderLineFactory(products()),
                members(), new DiscountPolicy(), servicePublisher, new LegacyPaymentAdapter(new LegacyPayGateway()));
        long orderId = service.placeOrder(new PlaceOrderCommand(1, bookAndPen(), "서울시 강남구"));

        assertTrue(orders.findById(orderId).isPresent());
        assertTrue(placed.isEmpty());
        servicePublisher.processEvents();
        assertEquals(1, placed.size());
        assertEquals(orderId, placed.get(0).orderId());
        assertEquals(2, placed.get(0).orderLines().size());
    }

    @Test
    @DisplayName("S4 재고 줄이기 처리기는 주문 접수됨 이벤트를 받아 주문 항목마다 상품 재고를 수량만큼 줄이고 저장한다. 이벤트를 처리하기 전에는 주문만 저장되고 재고는 그대로이고, 처리한 뒤에 재고가 맞춰진다. Main이 이벤트 처리 뒤 자바 책 재고를 보여준다")
    void s4() {
        Product book = new Product(1, "자바 책", new Money(15000), 10);
        book.decreaseStock(3);
        assertEquals(7, book.getStock());

        ProductRepository handlerProducts = products();
        new DecreaseStockHandler(handlerProducts).handle(new OrderPlaced(1, List.of(
                new OrderLine(1, "자바 책", new Money(15000), 4), new OrderLine(2, "볼펜", new Money(1000), 1))));
        assertEquals(6, handlerProducts.findById(1).orElseThrow().getStock());
        assertEquals(9, handlerProducts.findById(2).orElseThrow().getStock());
        assertEquals("ddd.catalog.application", DecreaseStockHandler.class.getPackageName());

        ProductRepository products = products();
        EventPublisher publisher = new EventPublisher();
        DecreaseStockHandler handler = new DecreaseStockHandler(products);
        publisher.subscribe(OrderPlaced.class, handler::handle);
        OrderRepository orders = new MemoryOrderRepository();
        OrderApplicationService service = new OrderApplicationService(orders, new OrderLineFactory(products),
                members(), new DiscountPolicy(), publisher, new LegacyPaymentAdapter(new LegacyPayGateway()));
        long orderId = service.placeOrder(new PlaceOrderCommand(1, bookAndPen(), "서울시 강남구"));

        assertEquals(OrderStatus.ORDERED, orders.findById(orderId).orElseThrow().getStatus());
        assertEquals(10, products.findById(1).orElseThrow().getStock());
        assertEquals(10, products.findById(2).orElseThrow().getStock());
        publisher.processEvents();
        assertEquals(8, products.findById(1).orElseThrow().getStock());
        assertEquals(9, products.findById(2).orElseThrow().getStock());
        assertEquals(OrderStatus.ORDERED, orders.findById(orderId).orElseThrow().getStatus());

        assertEquals(EXPECTED, runMain());
    }

    @Test
    @DisplayName("S5 용어집에 재고, 주문 접수됨, 이벤트 발행기, 재고 줄이기, 재고 줄이기 처리기를 더하고 상품 줄에 재고를 넣는다")
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
