package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ddd.catalog.application.DecreaseStockHandler;
import ddd.catalog.application.IncreaseStockHandler;
import ddd.catalog.domain.Product;
import ddd.catalog.domain.ProductRepository;
import ddd.catalog.infrastructure.MemoryProductRepository;
import ddd.member.domain.Member;
import ddd.member.domain.MemberGrade;
import ddd.member.domain.MemberRepository;
import ddd.member.infrastructure.MemoryMemberRepository;
import ddd.order.application.EventPublisher;
import ddd.order.application.MessageSender;
import ddd.order.application.OrderService;
import ddd.order.application.OrderItemRequest;
import ddd.order.application.OrderRequest;
import ddd.order.domain.DiscountPolicy;
import ddd.order.domain.OrderCanceled;
import ddd.order.domain.OrderItem;
import ddd.order.domain.OrderItemFactory;
import ddd.order.domain.PaymentGateway;
import ddd.order.domain.PaymentResult;
import ddd.order.infrastructure.LegacyPaymentAdapter;
import ddd.order.infrastructure.MemoryOrderRepository;
import legacypay.LegacyPayGateway;

class OrderSagaTest {

    private static final String EXPECTED = String.join(System.lineSeparator(),
            "===== 결제 실패 되돌리기 =====",
            "",
            "[주문하기]",
            "노트북 1개 주문: 1번 주문, 주문 접수",
            "노트북 재고: 10개 -> 9개",
            "",
            "[결제하기]",
            "결제 거절: 결제 한도를 넘었습니다",
            "주문 상태: 주문 접수 -> 취소",
            "노트북 재고: 9개 -> 10개") + System.lineSeparator();

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
        products.save(new Product(1, "자바 책", 15000, 10));
        products.save(new Product(2, "볼펜", 1000, 10));
        return products;
    }

    private static OrderService service(ProductRepository products, EventPublisher publisher,
            PaymentGateway paymentGateway, MessageSender messageSender) {
        MemberRepository members = new MemoryMemberRepository();
        members.save(new Member(1, "김철수", MemberGrade.BASIC));
        return new OrderService(new MemoryOrderRepository(), new OrderItemFactory(products), members,
                new DiscountPolicy(), publisher, paymentGateway, messageSender);
    }

    private static OrderService service(ProductRepository products, EventPublisher publisher) {
        return service(products, publisher, new LegacyPaymentAdapter(new LegacyPayGateway()), message -> {});
    }

    private static List<OrderItemRequest> bookAndPen() {
        return List.of(new OrderItemRequest(1, 2), new OrderItemRequest(2, 1));
    }

    private static int stock(ProductRepository products, long productId) {
        return products.findById(productId).orElseThrow().getStock();
    }

    private String section(String text, String heading) {
        int start = text.indexOf("## " + heading);
        assertTrue(start >= 0, heading + " 절이 없습니다");
        int end = text.indexOf("\n## ", start + 1);
        return end < 0 ? text.substring(start) : text.substring(start, end);
    }

    private boolean hasLine(String text, String... words) {
        return text.lines().anyMatch(line -> List.of(words).stream().allMatch(line::contains));
    }

    @Test
    @DisplayName("S1 결제가 거절된 주문도 이벤트를 처리하면 재고가 줄어 있고 주문은 주문 접수로 남는 문제를 실행해 보여준다. Main은 노트북(120000원, 재고 10개) 1개를 이영희가 주문하고 결제해 거절되면, 이벤트를 처리한 뒤 노트북 재고와 주문 상태를 보여준다")
    void s1() throws Exception {
        String main = Files.readString(Path.of("src/ddd/Main.java"));
        assertTrue(main.contains("new Product(1, \"노트북\", 120000, 10)"));
        List<String> lines = runMain().lines().toList();
        assertTrue(lines.contains("노트북 재고: 10개 -> 9개"));
        assertTrue(lines.contains("결제 거절: 결제 한도를 넘었습니다"));
    }

    @Test
    @DisplayName("S2 상품은 재고를 수량만큼 되돌린다. 주문 취소하기는 주문을 취소로 저장한 뒤 주문 취소됨 이벤트를 이벤트 발행기에 넘긴다. 이벤트는 주문 번호와 주문 항목을 담는다. 취소할 수 없는 주문이면 이벤트를 넘기지 않는다")
    void s2() throws Exception {
        Product book = new Product(1, "자바 책", 15000, 10);
        book.decreaseStock(3);
        book.increaseStock(2);
        assertEquals(9, book.getStock());

        EventPublisher publisher = new EventPublisher();
        List<OrderCanceled> canceled = new ArrayList<>();
        publisher.subscribe(event -> {
            if (event instanceof OrderCanceled orderCanceled) {
                canceled.add(orderCanceled);
            }
        });
        OrderService service = service(products(), publisher);

        long orderId = service.placeOrder(new OrderRequest(1, bookAndPen(), "서울시 강남구"));
        service.cancel(orderId);
        assertEquals("취소", service.findOrder(orderId).getStatus().label());
        assertTrue(canceled.isEmpty());
        publisher.processEvents();
        assertEquals(1, canceled.size());
        assertEquals(orderId, canceled.get(0).orderId());
        assertEquals(List.of("1 자바 책 15000 2", "2 볼펜 1000 1"), canceled.get(0).items().stream()
                .map(item -> item.getProductId() + " " + item.getProductName() + " " + item.getPrice() + " "
                        + item.getQuantity())
                .toList());

        long shippingId = service.placeOrder(new OrderRequest(1, bookAndPen(), "부산시 해운대구"));
        service.pay(shippingId);
        service.ship(shippingId);
        assertThrows(IllegalStateException.class, () -> service.cancel(shippingId));
        publisher.processEvents();
        assertEquals(1, canceled.size());
        assertEquals("ddd.order.domain", OrderCanceled.class.getPackageName());
    }

    @Test
    @DisplayName("S3 재고 되돌리기 처리기는 주문 취소됨 이벤트를 받아 주문 항목마다 상품 재고를 수량만큼 되돌리고 저장한다. 이벤트를 처리하기 전에는 재고가 그대로이고, 처리한 뒤에 되돌아온다")
    void s3() throws Exception {
        ProductRepository products = products();
        EventPublisher publisher = new EventPublisher();
        publisher.subscribe(new DecreaseStockHandler(products));
        publisher.subscribe(new IncreaseStockHandler(products));
        OrderService service = service(products, publisher);

        long orderId = service.placeOrder(new OrderRequest(1, bookAndPen(), "서울시 강남구"));
        publisher.processEvents();
        assertEquals(8, stock(products, 1));
        assertEquals(9, stock(products, 2));

        service.cancel(orderId);
        assertEquals(8, stock(products, 1));
        assertEquals(9, stock(products, 2));
        publisher.processEvents();
        assertEquals(10, stock(products, 1));
        assertEquals(10, stock(products, 2));

        ProductRepository handlerProducts = products();
        new IncreaseStockHandler(handlerProducts).handle(new OrderCanceled(7, List.of(
                new OrderItem(1, "자바 책", 15000, 4))));
        assertEquals(14, stock(handlerProducts, 1));
        assertEquals("ddd.catalog.application", IncreaseStockHandler.class.getPackageName());
    }

    @Test
    @DisplayName("S4 결제하기가 결제 창구에서 거절되면 보상으로 주문 취소하기를 하고 거절 이유로 거절한다. 주문은 취소가 되고, 이벤트를 처리하면 재고는 주문 전과 같다. 결제 완료 메시지는 없다. Main의 한도 넘은 결제와 결제 실패 뒤 줄이 취소를 보여준다")
    void s4() throws Exception {
        ProductRepository products = products();
        EventPublisher publisher = new EventPublisher();
        publisher.subscribe(new DecreaseStockHandler(products));
        publisher.subscribe(new IncreaseStockHandler(products));
        List<String> messages = new ArrayList<>();
        OrderService service = service(products, publisher,
                amount -> new PaymentResult(false, "카드가 막혔습니다"), messages::add);

        long orderId = service.placeOrder(new OrderRequest(1, bookAndPen(), "서울시 강남구"));
        publisher.processEvents();
        assertEquals(8, stock(products, 1));

        IllegalStateException error = assertThrows(IllegalStateException.class, () -> service.pay(orderId));
        assertEquals("카드가 막혔습니다", error.getMessage());
        assertEquals("취소", service.findOrder(orderId).getStatus().label());
        assertEquals(8, stock(products, 1));
        publisher.processEvents();
        assertEquals(10, stock(products, 1));
        assertEquals(10, stock(products, 2));
        assertTrue(messages.isEmpty());

        OrderService approving = service(products, publisher,
                new LegacyPaymentAdapter(new LegacyPayGateway()), messages::add);
        long paidId = approving.placeOrder(new OrderRequest(1, bookAndPen(), "부산시 해운대구"));
        approving.pay(paidId);
        publisher.processEvents();
        assertEquals("결제 완료", approving.findOrder(paidId).getStatus().label());
        assertEquals(8, stock(products, 1));
        assertEquals(1, messages.size());

        assertEquals(EXPECTED, runMain());
    }

    @Test
    @DisplayName("S5 용어집에 주문 취소됨, 재고 되돌리기, 재고 되돌리기 처리기, 사가, 보상을 더하고, 재고 줄과 주문 취소하기 줄에 되돌리기를 넣는다")
    void s5() throws Exception {
        String glossary = Files.readString(Path.of("glossary.md"));
        assertTrue(glossary.contains("| 주문 취소됨 | OrderCanceled |"));
        assertTrue(glossary.contains("| 재고 되돌리기 | increaseStock |"));
        assertTrue(glossary.contains("| 재고 되돌리기 처리기 | IncreaseStockHandler |"));
        assertTrue(glossary.contains("| 사가 | saga |"));
        assertTrue(glossary.contains("| 보상 | compensation |"));
        assertTrue(hasLine(glossary, "| 재고 | stock |", "취소", "되돌아"));
        assertTrue(hasLine(glossary, "| 주문 취소하기 | cancel |", "주문 취소됨"));
    }

    @Test
    @DisplayName("S6 학생 문서 규칙 두는 곳에 값 객체, 애그리거트, 도메인 서비스, 명세, 이벤트, 사가, 응용 서비스마다 어떤 규칙을 두는지와 이 프로그램의 예를 표로 적고, 사가 절에 단계와 보상을 짝지어 적는다")
    void s6() throws Exception {
        String doc = Files.readString(Path.of("where-rules-live.md"));
        String table = section(doc, "규칙 두는 곳");
        assertTrue(hasLine(table, "| 값 객체 |", "Address"));
        assertTrue(hasLine(table, "| 애그리거트 |", "Order"));
        assertTrue(hasLine(table, "| 도메인 서비스 |", "DiscountPolicy"));
        assertTrue(hasLine(table, "| 명세 |", "FreeShippingSpecification"));
        assertTrue(hasLine(table, "| 이벤트 |", "OrderPlaced"));
        assertTrue(hasLine(table, "| 사가 |", "OrderService"));
        assertTrue(hasLine(table, "| 응용 서비스 |", "OrderService"));
        String saga = section(doc, "사가");
        assertTrue(hasLine(saga, "주문 접수", "주문 취소"));
        assertTrue(hasLine(saga, "재고 줄이기", "재고 되돌리기"));
        assertTrue(saga.contains("IncreaseStockHandler"));
    }

    @Test
    @DisplayName("S7 컨텍스트 맵의 상품과 주문 줄에 재고 되돌리기 처리기가 주문 취소됨을 받는다는 것을, 주문과 회원 줄에 주문 조회 서비스도 회원 이름을 읽는다는 것을 더한다")
    void s7() throws Exception {
        String between = section(Files.readString(Path.of("context-map.md")), "컨텍스트 사이");
        assertTrue(hasLine(between, "| 상품 |", "| 주문 |", "주문 접수됨", "재고 되돌리기 처리기", "주문 취소됨"));
        assertTrue(hasLine(between, "| 주문 |", "| 회원 |", "할인 정책", "주문 조회 서비스", "회원 이름"));
    }
}
