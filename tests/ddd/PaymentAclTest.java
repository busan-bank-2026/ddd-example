package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import ddd.order.domain.PaymentGateway;
import ddd.order.domain.PaymentResult;
import ddd.order.infrastructure.LegacyPaymentAdapter;
import ddd.order.infrastructure.MemoryOrderRepository;
import ddd.shared.Money;
import legacypay.LegacyPayGateway;

class PaymentAclTest {

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

    private static OrderApplicationService service(EventPublisher publisher, PaymentGateway paymentGateway) {
        ProductRepository products = new MemoryProductRepository();
        products.save(new Product(1, "자바 책", new Money(15000), 10));
        MemberRepository members = new MemoryMemberRepository();
        members.save(new Member(1, "김철수", MemberGrade.BASIC));
        return new OrderApplicationService(new MemoryOrderRepository(), new OrderLineFactory(products), members,
                new DiscountPolicy(), publisher, paymentGateway);
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
    @DisplayName("S1 외부 결제 모듈은 가게 키와 원 단위 정수 금액을 받아 0000, E101, E201 코드 글자를 돌려준다. 외부 모듈은 ddd 밖 묶음에 둔다. Main은 받은 코드 글자를 보여준다")
    void s1() throws Exception {
        assertEquals("legacypay", LegacyPayGateway.class.getPackageName());
        assertTrue(Files.exists(Path.of("src/legacypay/LegacyPayGateway.java")));

        LegacyPayGateway gateway = new LegacyPayGateway();
        assertEquals("0000", gateway.approve("DDD-SHOP", 27900));
        assertEquals("0000", gateway.approve("DDD-SHOP", 100000));
        assertEquals("E101", gateway.approve("DDD-SHOP", 100001));
        assertEquals("E201", gateway.approve("OTHER-SHOP", 1000));

        List<String> lines = List.of(runMain().split(System.lineSeparator()));
        int shipmentLine = lines.indexOf("배송 컨텍스트: 1번 주문, 서울시 강남구");
        assertTrue(shipmentLine > 0);
        assertEquals("외부 결제 응답: 27900원 0000, 108000원 E101", lines.get(shipmentLine + 1));
    }

    @Test
    @DisplayName("S2 주문 컨텍스트는 결제 창구와 결제 결과를 우리 말로 정한다. 주문 도메인과 응용 계층은 외부 결제 모듈을 모른다")
    void s2() throws Exception {
        assertEquals("ddd.order.domain", PaymentGateway.class.getPackageName());
        assertTrue(PaymentGateway.class.isInterface());
        assertEquals(PaymentResult.class, PaymentGateway.class.getMethod("pay", Money.class).getReturnType());

        assertEquals("ddd.order.domain", PaymentResult.class.getPackageName());
        assertTrue(PaymentResult.class.isRecord());
        PaymentResult rejected = new PaymentResult(false, "카드가 막혔습니다");
        assertFalse(rejected.approved());
        assertEquals("카드가 막혔습니다", rejected.reason());
        PaymentGateway gateway = amount -> new PaymentResult(amount.amount() <= 5000, "");
        assertTrue(gateway.pay(new Money(5000)).approved());

        List<String> users = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Path.of("src/ddd"))) {
            for (Path path : paths.filter(Files::isRegularFile).toList()) {
                if (Files.readString(path).contains("legacypay")) {
                    users.add(path.getFileName().toString());
                }
            }
        }
        assertEquals(List.of("LegacyPaymentAdapter.java", "Main.java"), users.stream().sorted().toList());
    }

    @Test
    @DisplayName("S3 결제 어댑터는 결제 창구를 구현하고, 돈을 원 단위 정수로 바꾸고 가게 키를 붙여 외부 모듈에 맡기고, 받은 코드 글자를 결제 결과로 번역한다")
    void s3() {
        assertEquals("ddd.order.infrastructure", LegacyPaymentAdapter.class.getPackageName());
        assertTrue(PaymentGateway.class.isAssignableFrom(LegacyPaymentAdapter.class));

        PaymentGateway adapter = new LegacyPaymentAdapter(new LegacyPayGateway());
        assertEquals(new PaymentResult(true, ""), adapter.pay(new Money(27900)));
        assertEquals(new PaymentResult(false, "결제 한도를 넘었습니다"), adapter.pay(new Money(108000)));

        List<String> calls = new ArrayList<>();
        PaymentGateway broken = new LegacyPaymentAdapter(new LegacyPayGateway() {
            @Override
            public String approve(String merchantKey, int amountWon) {
                calls.add(merchantKey + " " + amountWon);
                return "E999";
            }
        });
        assertEquals(new PaymentResult(false, "결제에 실패했습니다"), broken.pay(new Money(41000)));
        assertEquals(List.of("DDD-SHOP 41000"), calls);
    }

    @Test
    @DisplayName("S4 결제하기는 결제 금액을 결제 창구에 맡긴다. 승인이면 저장하고 결제 완료 메시지를 넘기고, 거절이면 거절 이유로 거절하고 주문은 주문 접수로 남는다")
    void s4() throws Exception {
        EventPublisher publisher = new EventPublisher();
        List<String> messages = new ArrayList<>();
        publisher.subscribe(String.class, messages::add);
        OrderApplicationService service = service(publisher, new LegacyPaymentAdapter(new LegacyPayGateway()));

        long smallId = service.placeOrder(new PlaceOrderCommand(1, List.of(new OrderLineCommand(1, 2)), "서울시 강남구"));
        service.pay(smallId);
        assertEquals("결제 완료", service.findOrder(smallId).getStatus().label());

        long bigId = service.placeOrder(new PlaceOrderCommand(1, List.of(new OrderLineCommand(1, 7)), "부산시 해운대구"));
        IllegalStateException limit = assertThrows(IllegalStateException.class, () -> service.pay(bigId));
        assertEquals("결제 한도를 넘었습니다", limit.getMessage());
        assertEquals("주문 접수", service.findOrder(bigId).getStatus().label());
        publisher.processEvents();
        assertEquals(List.of("ORDER_PAID|orderId=" + smallId + "|address=서울시 강남구"), messages);

        List<Money> asked = new ArrayList<>();
        OrderApplicationService rejecting = service(new EventPublisher(), amount -> {
            asked.add(amount);
            return new PaymentResult(false, "카드가 막혔습니다");
        });
        long orderId = rejecting.placeOrder(new PlaceOrderCommand(1, List.of(new OrderLineCommand(1, 1)), "제주시 연동"));
        IllegalStateException card = assertThrows(IllegalStateException.class, () -> rejecting.pay(orderId));
        assertEquals("카드가 막혔습니다", card.getMessage());
        assertEquals(List.of(new Money(15000)), asked);
        assertEquals("주문 접수", rejecting.findOrder(orderId).getStatus().label());

        assertEquals(EXPECTED, runMain());
    }

    @Test
    @DisplayName("S5 용어집에 결제 창구, 결제 결과, 결제 어댑터를 더한다. 외부 모듈의 코드 글자와 가게 키는 용어집에 올리지 않는다")
    void s5() throws Exception {
        String glossary = Files.readString(Path.of("glossary.md"));
        assertTrue(glossary.contains("| 결제 창구 | PaymentGateway |"));
        assertTrue(glossary.contains("| 결제 결과 | PaymentResult |"));
        assertTrue(glossary.contains("| 결제 어댑터 | LegacyPaymentAdapter |"));
        assertFalse(glossary.contains("E101"));
        assertFalse(glossary.contains("merchantKey"));
    }

    @Test
    @DisplayName("S6 컨텍스트 맵에 부패 방지 계층 절과 외부 말과 우리 말 표, 하위 도메인 결제 줄, 컨텍스트 사이 외부 결제 줄을 더하고 용어집에 부패 방지 계층을 더한다")
    void s6() throws Exception {
        String contextMap = Files.readString(Path.of("context-map.md"));
        String acl = section(contextMap, "부패 방지 계층");
        assertTrue(acl.contains("LegacyPaymentAdapter"));
        assertTrue(acl.contains("PaymentGateway"));
        assertTrue(acl.contains("PaymentResult"));
        assertTrue(hasLine(acl, "amountWon", "Money"));
        assertTrue(hasLine(acl, "| 0000 |", "승인"));
        assertTrue(hasLine(acl, "| E101 |", "결제 한도를 넘었습니다"));
        assertTrue(hasLine(acl, "결제에 실패했습니다"));

        assertTrue(hasLine(section(contextMap, "하위 도메인"), "| 결제 |", "일반", "legacypay"));
        assertTrue(hasLine(section(contextMap, "컨텍스트 사이"), "| 주문 |", "| 외부 결제 |", "결제 어댑터"));

        String glossary = Files.readString(Path.of("glossary.md"));
        assertTrue(glossary.contains("| 부패 방지 계층 | LegacyPaymentAdapter |"));
    }
}
