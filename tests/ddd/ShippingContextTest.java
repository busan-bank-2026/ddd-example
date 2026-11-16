package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
import ddd.order.application.OrderApplicationService;
import ddd.order.application.OrderLineCommand;
import ddd.order.application.PlaceOrderCommand;
import ddd.order.domain.DiscountPolicy;
import ddd.order.domain.OrderLineFactory;
import ddd.order.domain.OrderPlaced;
import ddd.order.infrastructure.LegacyPaymentAdapter;
import ddd.order.infrastructure.MemoryOrderRepository;
import ddd.shared.Money;
import ddd.shipping.application.OrderPaidMessageHandler;
import ddd.shipping.domain.Shipment;
import ddd.shipping.domain.ShipmentRepository;
import ddd.shipping.infrastructure.MemoryShipmentRepository;
import legacypay.LegacyPayGateway;

class ShippingContextTest {

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
            "한도 넘은 결제: 결제 한도를 넘었습니다, 취소",
            "주문 목록: 1번, 김철수, 항목 2개, 27900원, 배송 완료",
            "결제 실패 뒤: 노트북 재고 10개, 취소") + System.lineSeparator();

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

    private static OrderApplicationService service(EventPublisher publisher) {
        ProductRepository products = new MemoryProductRepository();
        products.save(new Product(1, "자바 책", new Money(15000), 10));
        MemberRepository members = new MemoryMemberRepository();
        members.save(new Member(1, "김철수", MemberGrade.BASIC));
        return new OrderApplicationService(new MemoryOrderRepository(), new OrderLineFactory(products), members,
                new DiscountPolicy(), publisher, new LegacyPaymentAdapter(new LegacyPayGateway()));
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
    @DisplayName("S1 결제하기가 끝나면 주문 컨텍스트의 공개 호스트 서비스가 결제 완료 메시지를 정해진 한 줄 글자로 만들어 이벤트 발행기에 넘긴다. Main은 받은 메시지를 보여준다")
    void s1() throws Exception {
        assertEquals("ddd.order.application",
                Class.forName("ddd.order.application.OrderOpenHostService").getPackageName());

        EventPublisher publisher = new EventPublisher();
        List<String> messages = new ArrayList<>();
        List<OrderPlaced> placed = new ArrayList<>();
        publisher.subscribe(String.class, messages::add);
        publisher.subscribe(OrderPlaced.class, placed::add);
        OrderApplicationService service = service(publisher);

        long orderId = service.placeOrder(new PlaceOrderCommand(1, List.of(new OrderLineCommand(1, 2)), "서울시 강남구"));
        publisher.processEvents();
        assertEquals(List.of(), messages);
        assertEquals(1, placed.size());

        service.pay(orderId);
        assertEquals(List.of(), messages);
        publisher.processEvents();
        assertEquals(List.of("ORDER_PAID|orderId=" + orderId + "|address=서울시 강남구"), messages);
        assertEquals(1, placed.size());

        List<String> lines = List.of(runMain().split(System.lineSeparator()));
        int lockLine = lines.indexOf("동시 수정: 다른 곳에서 먼저 바꾼 주문입니다");
        assertTrue(lockLine > 0);
        assertEquals("결제 완료 메시지: ORDER_PAID|orderId=1|address=서울시 강남구", lines.get(lockLine + 1));
        assertEquals("결제 완료 메시지: ORDER_PAID|orderId=4|address=광주시 북구", lines.get(lockLine + 2));
    }

    @Test
    @DisplayName("S2 컨텍스트 맵에 공개 호스트 서비스와 발행된 언어 절을 더해 결제 완료 메시지의 모양을 적고, 용어집에 공개 호스트 서비스와 결제 완료 메시지를 더한다")
    void s2() throws Exception {
        String host = section(Files.readString(Path.of("context-map.md")), "공개 호스트 서비스와 발행된 언어");
        assertTrue(host.contains("OrderOpenHostService"));
        assertTrue(host.contains("ORDER_PAID|orderId=1|address=서울시 강남구"));
        assertTrue(hasLine(host, "| ORDER_PAID |"));
        assertTrue(hasLine(host, "| orderId |", "주문 번호"));
        assertTrue(hasLine(host, "| address |", "배송 주소"));

        String glossary = Files.readString(Path.of("glossary.md"));
        assertTrue(glossary.contains("| 공개 호스트 서비스 | OrderOpenHostService |"));
        assertTrue(glossary.contains("| 결제 완료 메시지 | ORDER_PAID |"));
    }

    @Test
    @DisplayName("S3 배송 컨텍스트 묶음 ddd.shipping의 domain에 배송과 배송 저장소, infrastructure에 메모리 배송 저장소를 둔다. 배송은 주문 번호와 배송 주소를 가진다")
    void s3() throws Exception {
        assertEquals("ddd.shipping.domain", Shipment.class.getPackageName());
        assertEquals("ddd.shipping.domain", ShipmentRepository.class.getPackageName());
        assertTrue(ShipmentRepository.class.isInterface());
        assertEquals("ddd.shipping.infrastructure", MemoryShipmentRepository.class.getPackageName());

        Shipment shipment = new Shipment(7, "부산시 해운대구");
        assertEquals(7, shipment.getOrderId());
        assertEquals("부산시 해운대구", shipment.getAddress());
        assertEquals(new Shipment(7, "다른 주소"), shipment);

        ShipmentRepository shipments = new MemoryShipmentRepository();
        assertTrue(shipments.findByOrderId(7).isEmpty());
        shipments.save(shipment);
        assertEquals("부산시 해운대구", shipments.findByOrderId(7).orElseThrow().getAddress());
    }

    @Test
    @DisplayName("S4 배송 컨텍스트의 결제 완료 메시지 처리기는 메시지 글자만 읽어 배송을 만들고 저장한다. 결제 완료가 아닌 메시지는 넘긴다. 배송 컨텍스트는 주문 컨텍스트의 클래스를 가져다 쓰지 않는다")
    void s4() throws Exception {
        assertEquals("ddd.shipping.application", OrderPaidMessageHandler.class.getPackageName());
        ShipmentRepository shipments = new MemoryShipmentRepository();
        OrderPaidMessageHandler handler = new OrderPaidMessageHandler(shipments);

        handler.handle("ORDER_PAID|orderId=7|address=부산시 해운대구");
        handler.handle("ORDER_CANCELED|orderId=8|address=대구시 수성구");
        assertEquals("부산시 해운대구", shipments.findByOrderId(7).orElseThrow().getAddress());
        assertTrue(shipments.findByOrderId(8).isEmpty());

        EventPublisher publisher = new EventPublisher();
        ShipmentRepository serviceShipments = new MemoryShipmentRepository();
        publisher.subscribe(String.class, new OrderPaidMessageHandler(serviceShipments)::handle);
        OrderApplicationService service = service(publisher);
        long orderId = service.placeOrder(new PlaceOrderCommand(1, List.of(new OrderLineCommand(1, 1)), "제주시 연동"));
        service.pay(orderId);
        assertTrue(serviceShipments.findByOrderId(orderId).isEmpty());
        publisher.processEvents();
        assertEquals("제주시 연동", serviceShipments.findByOrderId(orderId).orElseThrow().getAddress());

        int files = 0;
        try (Stream<Path> paths = Files.walk(Path.of("src/ddd/shipping"))) {
            for (Path path : paths.filter(Files::isRegularFile).toList()) {
                files++;
                String source = Files.readString(path);
                assertTrue(source.startsWith("package ddd.shipping."));
                assertFalse(source.contains("ddd.order"));
            }
        }
        assertEquals(4, files);
        assertEquals(EXPECTED, runMain());
    }

    @Test
    @DisplayName("S5 컨텍스트 맵의 하위 도메인 표에 배송을, 컨텍스트 사이 표에 배송이 주문의 결제 완료 메시지를 읽는 줄을 더하고, 용어집에 배송, 배송 저장소, 결제 완료 메시지 처리기를 더한다")
    void s5() throws Exception {
        String contextMap = Files.readString(Path.of("context-map.md"));
        assertTrue(hasLine(section(contextMap, "하위 도메인"), "| 배송 |", "배송 컨텍스트", "ddd.shipping"));
        assertTrue(hasLine(section(contextMap, "컨텍스트 사이"), "| 배송 |", "| 주문 |", "결제 완료 메시지"));

        String glossary = Files.readString(Path.of("glossary.md"));
        assertTrue(glossary.contains("| 배송 | Shipment |"));
        assertTrue(glossary.contains("| 배송 저장소 | ShipmentRepository |"));
        assertTrue(glossary.contains("| 결제 완료 메시지 처리기 | OrderPaidMessageHandler |"));
    }
}
