package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import ddd.order.application.MessageSender;
import ddd.order.application.OrderService;
import ddd.order.application.OrderItemRequest;
import ddd.order.application.OrderRequest;
import ddd.order.domain.DiscountPolicy;
import ddd.order.domain.OrderItemFactory;
import ddd.order.domain.PaymentGateway;
import ddd.order.domain.PaymentResult;
import ddd.order.infrastructure.LegacyPaymentAdapter;
import ddd.order.infrastructure.MemoryOrderRepository;
import legacypay.LegacyPayGateway;

class PaymentAclTest {

    private static OrderService service(PaymentGateway paymentGateway, MessageSender messageSender) {
        ProductRepository products = new MemoryProductRepository();
        products.save(new Product(1, "자바 책", 15000, 10));
        MemberRepository members = new MemoryMemberRepository();
        members.save(new Member(1, "김철수", MemberGrade.BASIC));
        return new OrderService(new MemoryOrderRepository(), new OrderItemFactory(products), members,
                new DiscountPolicy(), new EventPublisher(), paymentGateway, messageSender);
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
    @DisplayName("S1 외부 결제 모듈은 우리 말과 다른 모양으로 말한다. 가게 키와 원 단위 정수 금액을 받아 승인이면 0000, 10만 원을 넘으면 E101, 가게 키가 틀리면 E201 코드 글자를 돌려준다. 외부 모듈은 ddd 밖 묶음에 둔다. Main은 27900원과 108000원을 직접 맡겨 받은 코드 글자를 보여준다")
    void s1() throws Exception {
        assertEquals("legacypay", LegacyPayGateway.class.getPackageName());
        assertTrue(Files.exists(Path.of("src/legacypay/LegacyPayGateway.java")));

        LegacyPayGateway gateway = new LegacyPayGateway();
        assertEquals("0000", gateway.approve("DDD-SHOP", 27900));
        assertEquals("0000", gateway.approve("DDD-SHOP", 100000));
        assertEquals("E101", gateway.approve("DDD-SHOP", 100001));
        assertEquals("E201", gateway.approve("OTHER-SHOP", 1000));
    }

    @Test
    @DisplayName("S2 주문 컨텍스트는 결제를 우리 말로 정한다. 결제 창구는 결제 금액을 받아 결제 결과를 돌려준다. 결제 결과는 승인인지와 거절 이유를 가진다. 주문 도메인과 응용 계층은 외부 결제 모듈을 모른다")
    void s2() throws Exception {
        assertEquals("ddd.order.domain", PaymentGateway.class.getPackageName());
        assertTrue(PaymentGateway.class.isInterface());
        assertEquals(PaymentResult.class, PaymentGateway.class.getMethod("pay", int.class).getReturnType());

        assertEquals("ddd.order.domain", PaymentResult.class.getPackageName());
        assertTrue(PaymentResult.class.isRecord());
        PaymentResult rejected = new PaymentResult(false, "카드가 막혔습니다");
        assertFalse(rejected.approved());
        assertEquals("카드가 막혔습니다", rejected.reason());
        PaymentGateway gateway = amount -> new PaymentResult(amount <= 5000, "");
        assertTrue(gateway.pay(5000).approved());

        List<String> users = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Path.of("src/ddd"))) {
            for (Path path : paths.filter(Files::isRegularFile).toList()) {
                if (Files.readString(path).contains("legacypay")) {
                    users.add(path.getFileName().toString());
                }
            }
        }
        assertEquals(List.of("LegacyPaymentAdapter.java", "Main.java", "OrderApp.java"), users.stream().sorted().toList());
    }

    @Test
    @DisplayName("S3 결제 어댑터는 결제 창구를 구현한다. 결제 금액에 가게 키를 붙여 외부 모듈에 맡기고, 받은 코드 글자를 결제 결과로 번역한다. 0000은 승인, E101은 거절 이유 결제 한도를 넘었습니다, 다른 코드는 거절 이유 결제에 실패했습니다다")
    void s3() {
        assertEquals("ddd.order.infrastructure", LegacyPaymentAdapter.class.getPackageName());
        assertTrue(PaymentGateway.class.isAssignableFrom(LegacyPaymentAdapter.class));

        PaymentGateway adapter = new LegacyPaymentAdapter(new LegacyPayGateway());
        assertEquals(new PaymentResult(true, ""), adapter.pay(27900));
        assertEquals(new PaymentResult(false, "결제 한도를 넘었습니다"), adapter.pay(108000));

        List<String> calls = new ArrayList<>();
        PaymentGateway broken = new LegacyPaymentAdapter(new LegacyPayGateway() {
            @Override
            public String approve(String merchantKey, int amountWon) {
                calls.add(merchantKey + " " + amountWon);
                return "E999";
            }
        });
        assertEquals(new PaymentResult(false, "결제에 실패했습니다"), broken.pay(41000));
        assertEquals(List.of("DDD-SHOP 41000"), calls);
    }

    @Test
    @DisplayName("S4 결제하기는 결제 금액을 결제 창구에 맡긴다. 승인이면 저장하고 결제 완료 메시지를 넘긴다. 거절이면 거절 이유로 결제를 거절하고, 주문은 주문 접수로 남고, 결제 완료 메시지도 없다. Main은 자바 책 2개 주문의 승인과 자바 책 8개 주문의 거절 이유와 주문 상태를 보여준다")
    void s4() throws Exception {
        List<String> messages = new ArrayList<>();
        OrderService service = service(new LegacyPaymentAdapter(new LegacyPayGateway()), messages::add);

        long smallId = service.placeOrder(new OrderRequest(1, List.of(new OrderItemRequest(1, 2)), "서울시 강남구"));
        service.pay(smallId);
        assertEquals("결제 완료", service.findOrder(smallId).getStatus().label());

        long bigId = service.placeOrder(new OrderRequest(1, List.of(new OrderItemRequest(1, 7)), "부산시 해운대구"));
        IllegalStateException limit = assertThrows(IllegalStateException.class, () -> service.pay(bigId));
        assertEquals("결제 한도를 넘었습니다", limit.getMessage());
        assertEquals("취소", service.findOrder(bigId).getStatus().label());
        assertEquals(List.of("ORDER_PAID|orderId=" + smallId + "|address=서울시 강남구"), messages);

        List<Integer> asked = new ArrayList<>();
        OrderService rejecting = service(amount -> {
            asked.add(amount);
            return new PaymentResult(false, "카드가 막혔습니다");
        }, message -> {});
        long orderId = rejecting.placeOrder(new OrderRequest(1, List.of(new OrderItemRequest(1, 1)), "제주시 연동"));
        IllegalStateException card = assertThrows(IllegalStateException.class, () -> rejecting.pay(orderId));
        assertEquals("카드가 막혔습니다", card.getMessage());
        assertEquals(List.of(15000), asked);
        assertEquals("취소", rejecting.findOrder(orderId).getStatus().label());
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
    @DisplayName("S6 컨텍스트 맵에 부패 방지 계층 절을 더해 외부 결제 모듈의 말과 우리 말을 표로 잇고, 하위 도메인 표에 결제를, 컨텍스트 사이 표에 주문이 외부 결제를 결제 어댑터로 쓰는 줄을 더한다. 용어집에 부패 방지 계층을 더한다")
    void s6() throws Exception {
        String contextMap = Files.readString(Path.of("context-map.md"));
        String acl = section(contextMap, "부패 방지 계층");
        assertTrue(acl.contains("LegacyPaymentAdapter"));
        assertTrue(acl.contains("PaymentGateway"));
        assertTrue(acl.contains("PaymentResult"));
        assertTrue(hasLine(acl, "| amountWon |", "결제 금액 paymentPrice"));
        assertTrue(hasLine(acl, "| approve(merchantKey, amountWon) |", "결제 창구의 pay(결제 금액)"));
        assertTrue(hasLine(acl, "| 0000 |", "승인"));
        assertTrue(hasLine(acl, "| E101 |", "결제 한도를 넘었습니다"));
        assertTrue(hasLine(acl, "결제에 실패했습니다"));

        assertTrue(hasLine(section(contextMap, "하위 도메인"), "| 결제 |", "일반", "legacypay"));
        assertTrue(hasLine(section(contextMap, "컨텍스트 사이"), "| 주문 |", "| 외부 결제 |", "결제 어댑터"));

        String glossary = Files.readString(Path.of("glossary.md"));
        assertTrue(glossary.contains("| 부패 방지 계층 | LegacyPaymentAdapter |"));
    }
}
