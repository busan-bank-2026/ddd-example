package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import ddd.order.application.OrderService;
import ddd.order.application.MessageSender;
import ddd.order.application.OrderItemRequest;
import ddd.order.application.OrderRequest;
import ddd.order.domain.Address;
import ddd.order.domain.DiscountPolicy;
import ddd.order.domain.Order;
import ddd.order.domain.OrderItem;
import ddd.order.domain.OrderItemFactory;
import ddd.order.domain.OrderRepository;
import ddd.order.domain.OrderStatus;
import ddd.order.domain.PaymentGateway;
import ddd.order.infrastructure.LegacyPaymentAdapter;
import ddd.order.infrastructure.MemoryOrderRepository;
import legacypay.LegacyPayGateway;

class DiscountTest {

    private static Order newOrder() {
        return new Order(1, 1, List.of(
                new OrderItem(1, "자바 책", 15000, 2),
                new OrderItem(2, "볼펜", 1000, 1)), new Address("서울시 강남구"));
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
    }

    @Test
    @DisplayName("S2 할인 정책은 회원과 주문을 받아 할인 금액을 계산한다. VIP는 주문 금액의 10%, 일반은 0원이다. 할인 정책은 도메인 계층에 두고, 저장소를 쓰지 않고, 주문을 바꾸지 않는다")
    void s2() {
        DiscountPolicy policy = new DiscountPolicy();
        Order order = newOrder();

        assertEquals(3100, policy.discount(new Member(1, "김철수", MemberGrade.VIP), order));
        assertEquals(0, policy.discount(new Member(2, "이영희", MemberGrade.BASIC), order));

        assertEquals(OrderStatus.ORDERED, order.getStatus());
        assertEquals(0, order.getDiscountPrice());
        assertEquals(31000, order.getTotalPrice());

        assertEquals("ddd.order.domain", DiscountPolicy.class.getPackageName());
        assertEquals(0, DiscountPolicy.class.getDeclaredFields().length);
    }

    @Test
    @DisplayName("S3 주문은 결제할 때 할인 금액을 받아 기억한다. 결제 금액은 주문 금액에서 할인 금액을 뺀 금액이다. 결제 전 할인 금액은 0원이다")
    void s3() {
        Order order = newOrder();
        assertEquals(0, order.getDiscountPrice());
        assertEquals(31000, order.getPaymentPrice());

        order.pay(3100);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(3100, order.getDiscountPrice());
        assertEquals(27900, order.getPaymentPrice());
        assertEquals(31000, order.getTotalPrice());

        IllegalStateException error = assertThrows(IllegalStateException.class, () -> order.pay(0));
        assertEquals("결제 완료인 주문은 결제할 수 없습니다", error.getMessage());
        assertEquals(3100, order.getDiscountPrice());
    }

    @Test
    @DisplayName("S4 주문 응용 서비스는 결제하기에서 주문을 꺼내고, 회원을 찾고, 할인 정책에게 할인 금액을 계산하게 하고, 주문에게 결제를 시키고, 저장한다. Main이 첫 주문의 할인 장면을 보여준다")
    void s4() {
        assertEquals(List.of(OrderRepository.class, OrderItemFactory.class, MemberRepository.class, DiscountPolicy.class, EventPublisher.class,
                PaymentGateway.class, MessageSender.class),
                List.of(OrderService.class.getConstructors()[0].getParameterTypes()));

        ProductRepository products = new MemoryProductRepository();
        products.save(new Product(1, "자바 책", 15000, 10));
        products.save(new Product(2, "볼펜", 1000, 10));
        MemberRepository members = new MemoryMemberRepository();
        members.save(new Member(1, "김철수", MemberGrade.VIP));
        members.save(new Member(2, "이영희", MemberGrade.BASIC));
        OrderRepository orders = new MemoryOrderRepository();
        OrderService service = new OrderService(orders, new OrderItemFactory(products),
                members, new DiscountPolicy(), new EventPublisher(), new LegacyPaymentAdapter(new LegacyPayGateway()), message -> {});

        List<OrderItemRequest> lines = List.of(new OrderItemRequest(1, 2), new OrderItemRequest(2, 1));
        long vipOrderId = service.placeOrder(new OrderRequest(1, lines, "서울시 강남구"));
        long basicOrderId = service.placeOrder(new OrderRequest(2, lines, "부산시 해운대구"));
        assertEquals(0, orders.findById(vipOrderId).orElseThrow().getDiscountPrice());

        service.pay(vipOrderId);
        service.pay(basicOrderId);

        Order vipOrder = orders.findById(vipOrderId).orElseThrow();
        assertEquals(OrderStatus.PAID, vipOrder.getStatus());
        assertEquals(3100, vipOrder.getDiscountPrice());
        assertEquals(27900, vipOrder.getPaymentPrice());
        Order basicOrder = orders.findById(basicOrderId).orElseThrow();
        assertEquals(0, basicOrder.getDiscountPrice());
        assertEquals(31000, basicOrder.getPaymentPrice());
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
