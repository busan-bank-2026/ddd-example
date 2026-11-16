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
import ddd.order.application.PlaceOrderCommand;
import ddd.order.domain.Address;
import ddd.order.domain.DiscountPolicy;
import ddd.order.domain.Order;
import ddd.order.domain.OrderLine;
import ddd.order.domain.OrderLineFactory;
import ddd.order.domain.OrderRepository;
import ddd.order.domain.OrderStatus;
import ddd.order.infrastructure.MemoryOrderRepository;
import ddd.shared.Money;

class DiscountTest {

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
            "동시 수정: 다른 곳에서 먼저 바꾼 주문입니다") + System.lineSeparator();

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

    private static Order newOrder() {
        return new Order(1, 1, List.of(
                new OrderLine(1, "자바 책", new Money(15000), 2),
                new OrderLine(2, "볼펜", new Money(1000), 1)), new Address("서울시 강남구"));
    }

    @Test
    @DisplayName("S1 회원은 회원 번호, 이름과 함께 회원 등급을 가진다. 등급은 일반과 VIP 둘이다. Main은 김철수를 VIP로, 이영희를 일반으로 저장하고, 앞 장 출력 스무 줄은 그대로다")
    void s1() throws Exception {
        Member vip = new Member(1, "김철수", MemberGrade.VIP);
        assertEquals(1L, vip.getId());
        assertEquals("김철수", vip.getName());
        assertEquals(MemberGrade.VIP, vip.getGrade());
        assertEquals(MemberGrade.BASIC, new Member(2, "이영희", MemberGrade.BASIC).getGrade());
        assertEquals(vip, new Member(1, "김철수", MemberGrade.BASIC));
        assertEquals(List.of(MemberGrade.BASIC, MemberGrade.VIP), List.of(MemberGrade.values()));
        assertEquals("ddd.member.domain", MemberGrade.class.getPackageName());

        String main = Files.readString(Path.of("src/ddd/Main.java"));
        assertTrue(main.contains("new Member(1, \"김철수\", MemberGrade.VIP)"));
        assertTrue(main.contains("new Member(2, \"이영희\", MemberGrade.BASIC)"));
        String printed = runMain();
        assertTrue(printed.contains("주문: 1번, 김철수, 31000원, 주문 접수"));
        assertTrue(printed.contains("결제: 결제 완료"));
        assertTrue(printed.contains("두 번 결제: 결제 완료인 주문은 결제할 수 없습니다"));
    }

    @Test
    @DisplayName("S2 할인 정책은 회원과 주문을 받아 할인 금액을 계산한다. VIP는 주문 금액의 10%, 일반은 0원이다. 할인 정책은 도메인 계층에 두고, 저장소를 쓰지 않고, 주문을 바꾸지 않는다")
    void s2() {
        DiscountPolicy policy = new DiscountPolicy();
        Order order = newOrder();

        assertEquals(new Money(3100), policy.discount(new Member(1, "김철수", MemberGrade.VIP), order));
        assertEquals(new Money(0), policy.discount(new Member(2, "이영희", MemberGrade.BASIC), order));

        assertEquals(OrderStatus.ORDERED, order.getStatus());
        assertEquals(new Money(0), order.getDiscountPrice());
        assertEquals(new Money(31000), order.getTotalPrice());

        assertEquals("ddd.order.domain", DiscountPolicy.class.getPackageName());
        assertEquals(0, DiscountPolicy.class.getDeclaredFields().length);
    }

    @Test
    @DisplayName("S3 주문은 결제할 때 할인 금액을 받아 기억한다. 결제 금액은 주문 금액에서 할인 금액을 뺀 돈이다. 결제 전 할인 금액은 0원이다")
    void s3() {
        Order order = newOrder();
        assertEquals(new Money(0), order.getDiscountPrice());
        assertEquals(new Money(31000), order.getPaymentPrice());

        order.pay(new Money(3100));
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(new Money(3100), order.getDiscountPrice());
        assertEquals(new Money(27900), order.getPaymentPrice());
        assertEquals(new Money(31000), order.getTotalPrice());

        IllegalStateException error = assertThrows(IllegalStateException.class, () -> order.pay(new Money(0)));
        assertEquals("결제 완료인 주문은 결제할 수 없습니다", error.getMessage());
        assertEquals(new Money(3100), order.getDiscountPrice());

        assertEquals(new Money(900), new Money(1000).minus(new Money(100)));
        assertThrows(IllegalArgumentException.class, () -> new Money(100).minus(new Money(1000)));
    }

    @Test
    @DisplayName("S4 주문 응용 서비스는 결제하기에서 주문을 꺼내고, 회원을 찾고, 할인 정책에게 할인 금액을 계산하게 하고, 주문에게 결제를 시키고, 저장한다. Main이 첫 주문의 할인 장면을 보여준다")
    void s4() {
        assertEquals(List.of(OrderRepository.class, OrderLineFactory.class, MemberRepository.class, DiscountPolicy.class, EventPublisher.class),
                List.of(OrderApplicationService.class.getConstructors()[0].getParameterTypes()));

        ProductRepository products = new MemoryProductRepository();
        products.save(new Product(1, "자바 책", new Money(15000), 10));
        products.save(new Product(2, "볼펜", new Money(1000), 10));
        MemberRepository members = new MemoryMemberRepository();
        members.save(new Member(1, "김철수", MemberGrade.VIP));
        members.save(new Member(2, "이영희", MemberGrade.BASIC));
        OrderRepository orders = new MemoryOrderRepository();
        OrderApplicationService service = new OrderApplicationService(orders, new OrderLineFactory(products),
                members, new DiscountPolicy(), new EventPublisher());

        List<OrderLineCommand> lines = List.of(new OrderLineCommand(1, 2), new OrderLineCommand(2, 1));
        long vipOrderId = service.placeOrder(new PlaceOrderCommand(1, lines, "서울시 강남구"));
        long basicOrderId = service.placeOrder(new PlaceOrderCommand(2, lines, "부산시 해운대구"));
        assertEquals(new Money(0), orders.findById(vipOrderId).orElseThrow().getDiscountPrice());

        service.pay(vipOrderId);
        service.pay(basicOrderId);

        Order vipOrder = orders.findById(vipOrderId).orElseThrow();
        assertEquals(OrderStatus.PAID, vipOrder.getStatus());
        assertEquals(new Money(3100), vipOrder.getDiscountPrice());
        assertEquals(new Money(27900), vipOrder.getPaymentPrice());
        Order basicOrder = orders.findById(basicOrderId).orElseThrow();
        assertEquals(new Money(0), basicOrder.getDiscountPrice());
        assertEquals(new Money(31000), basicOrder.getPaymentPrice());

        assertEquals(EXPECTED, runMain());
    }

    @Test
    @DisplayName("S5 용어집에 회원 등급, 일반, VIP, 할인 정책, 할인 금액, 결제 금액을 더하고 회원의 뜻에 회원 등급을 넣는다")
    void s5() throws Exception {
        String glossary = Files.readString(Path.of("glossary.md"));

        assertTrue(glossary.contains("| 회원 등급 | MemberGrade |"));
        assertTrue(glossary.contains("| 일반 | BASIC |"));
        assertTrue(glossary.contains("| VIP | VIP |"));
        assertTrue(glossary.contains("| 할인 정책 | DiscountPolicy |"));
        assertTrue(glossary.contains("| 할인 금액 | discountPrice |"));
        assertTrue(glossary.contains("| 결제 금액 | paymentPrice |"));
        assertTrue(glossary.contains("| 회원 | Member | 상품을 주문하는 사람. 회원 번호, 이름, 회원 등급을 가진다"));
        assertFalse(glossary.contains("회원 번호와 이름을 가진다"));
    }
}
