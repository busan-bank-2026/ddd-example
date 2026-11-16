package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.RecordComponent;
import java.nio.charset.StandardCharsets;
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
import ddd.order.application.OrderApplicationService;
import ddd.order.application.OrderLineCommand;
import ddd.order.application.OrderQueryService;
import ddd.order.application.OrderSummary;
import ddd.order.application.PlaceOrderCommand;
import ddd.order.domain.Address;
import ddd.order.domain.DiscountPolicy;
import ddd.order.domain.Order;
import ddd.order.domain.OrderLine;
import ddd.order.domain.OrderLineFactory;
import ddd.order.domain.OrderRepository;
import ddd.order.domain.PaymentResult;
import ddd.order.infrastructure.MemoryOrderRepository;
import ddd.shared.Money;

class OrderQueryTest {

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

    private static Order order(long id, long memberId) {
        return new Order(id, memberId, List.of(new OrderLine(1, "자바 책", new Money(15000), 2)), new Address("서울시 강남구"));
    }

    @Test
    @DisplayName("S1 주문 저장소는 회원 번호로 그 회원의 주문을 모두 꺼낸다. 다른 회원의 주문은 꺼내지 않고, 주문이 없는 회원은 빈 목록이다")
    void s1() {
        OrderRepository repository = new MemoryOrderRepository();
        repository.save(order(1, 1));
        repository.save(order(2, 2));
        repository.save(order(3, 1));

        List<Order> found = repository.findByMemberId(1);
        assertEquals(List.of(1L, 3L), found.stream().map(Order::getId).sorted().toList());
        assertEquals(List.of(2L), repository.findByMemberId(2).stream().map(Order::getId).toList());
        assertEquals(List.of(), repository.findByMemberId(9));

        found.get(0).cancel();
        assertEquals("주문 접수", repository.findById(found.get(0).getId()).orElseThrow().getStatus().label());
    }

    @Test
    @DisplayName("S2 주문 요약은 주문 목록 화면 한 줄에 맞춘 읽기 모델이다. 주문 번호, 회원 이름, 항목 수, 결제 금액, 상태 글자를 숫자와 글자로만 가진다")
    void s2() {
        assertEquals("ddd.order.application", OrderSummary.class.getPackageName());
        assertTrue(OrderSummary.class.isRecord());
        RecordComponent[] components = OrderSummary.class.getRecordComponents();
        assertEquals(List.of("orderId", "memberName", "orderLineCount", "paymentPrice", "status"),
                Arrays.stream(components).map(RecordComponent::getName).toList());
        assertEquals(List.of(long.class, String.class, int.class, int.class, String.class),
                Arrays.stream(components).map(RecordComponent::getType).toList());

        OrderSummary summary = new OrderSummary(1, "김철수", 2, 27900, "결제 완료");
        assertEquals(new OrderSummary(1, "김철수", 2, 27900, "결제 완료"), summary);
        assertEquals(27900, summary.paymentPrice());
    }

    @Test
    @DisplayName("S3 주문 조회 서비스는 회원 번호로 그 회원의 주문 요약 목록을 돌려준다. 주문을 바꾸거나 저장하지 않는다. 주문 응용 서비스와 따로 있고 결제 창구와 발행기를 모른다")
    void s3() throws Exception {
        ProductRepository products = new MemoryProductRepository();
        products.save(new Product(1, "자바 책", new Money(15000), 10));
        products.save(new Product(2, "볼펜", new Money(1000), 10));
        MemberRepository members = new MemoryMemberRepository();
        members.save(new Member(1, "김철수", MemberGrade.VIP));
        members.save(new Member(2, "이영희", MemberGrade.BASIC));
        OrderRepository orders = new MemoryOrderRepository();
        OrderApplicationService orderService = new OrderApplicationService(orders, new OrderLineFactory(products),
                members, new DiscountPolicy(), new EventPublisher(), amount -> new PaymentResult(true, ""));

        long paidId = orderService.placeOrder(new PlaceOrderCommand(1,
                List.of(new OrderLineCommand(1, 2), new OrderLineCommand(2, 1)), "서울시 강남구"));
        orderService.pay(paidId);
        long otherId = orderService.placeOrder(new PlaceOrderCommand(2, List.of(new OrderLineCommand(2, 3)), "부산시 해운대구"));
        long waitingId = orderService.placeOrder(new PlaceOrderCommand(1, List.of(new OrderLineCommand(1, 1)), "제주시 연동"));
        long paidVersion = orders.findById(paidId).orElseThrow().getVersion();

        OrderQueryService queryService = new OrderQueryService(orders, members);
        List<OrderSummary> summaries = queryService.findOrderSummaries(1).stream()
                .sorted((a, b) -> Long.compare(a.orderId(), b.orderId())).toList();
        assertEquals(List.of(
                new OrderSummary(paidId, "김철수", 2, 27900, "결제 완료"),
                new OrderSummary(waitingId, "김철수", 1, 15000, "주문 접수")), summaries);
        assertEquals(List.of(new OrderSummary(otherId, "이영희", 1, 3000, "주문 접수")), queryService.findOrderSummaries(2));
        assertEquals(List.of(), queryService.findOrderSummaries(9));
        assertEquals(paidVersion, orders.findById(paidId).orElseThrow().getVersion());

        assertEquals("ddd.order.application", OrderQueryService.class.getPackageName());
        assertNotEquals(OrderApplicationService.class, OrderQueryService.class);
        assertEquals(List.of(OrderRepository.class, MemberRepository.class),
                List.of(OrderQueryService.class.getConstructors()[0].getParameterTypes()));
        String source = Files.readString(Path.of("src/ddd/order/application/OrderQueryService.java"));
        assertTrue(!source.contains(".save(") && !source.contains("throw "));

        assertEquals(EXPECTED, runMain());
    }

    @Test
    @DisplayName("S4 용어집에 주문 요약, 주문 조회 서비스, 명령과 조회 나누기를 더한다")
    void s4() throws Exception {
        String glossary = Files.readString(Path.of("glossary.md"));
        assertTrue(glossary.contains("| 주문 요약 | OrderSummary |"));
        assertTrue(glossary.contains("| 주문 조회 서비스 | OrderQueryService |"));
        assertTrue(glossary.contains("| 명령과 조회 나누기 | CQRS |"));
    }
}
