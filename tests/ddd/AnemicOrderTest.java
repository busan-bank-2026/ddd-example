package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

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
import ddd.order.domain.DiscountPolicy;
import ddd.order.domain.Order;
import ddd.order.domain.OrderLineFactory;
import ddd.order.infrastructure.MemoryOrderRepository;
import ddd.shared.Money;

class AnemicOrderTest {

    private static MemberRepository members() {
        MemberRepository members = new MemoryMemberRepository();
        members.save(new Member(1, "김철수", MemberGrade.BASIC));
        members.save(new Member(2, "이영희", MemberGrade.BASIC));
        return members;
    }

    private static OrderLineFactory orderLineFactory() {
        ProductRepository products = new MemoryProductRepository();
        products.save(new Product(1, "자바 책", new Money(15000), 10));
        products.save(new Product(2, "볼펜", new Money(1000), 10));
        products.save(new Product(3, "공책", new Money(2000), 10));
        return new OrderLineFactory(products);
    }

    private final OrderApplicationService service = new OrderApplicationService(new MemoryOrderRepository(), orderLineFactory(), members(), new DiscountPolicy(), new EventPublisher());

    private long newOrder() {
        return service.placeOrder(new PlaceOrderCommand(1, List.of(new OrderLineCommand(1, 2)), "서울시 강남구"));
    }

    @Test
    @DisplayName("S1 주문자 이름, 상품 이름, 가격, 수량, 배송 주소로 주문을 만들면 주문 금액은 가격 곱하기 수량이고 상태는 주문 접수다")
    void s1() {
        Order order = service.findOrder(newOrder());

        assertEquals(1L, order.getMemberId());
        assertEquals("자바 책", order.getOrderLines().get(0).productName());
        assertEquals(15000, order.getOrderLines().get(0).price().amount());
        assertEquals(2, order.getOrderLines().get(0).quantity());
        assertEquals("서울시 강남구", order.getAddress().value());
        assertEquals(30000, order.getTotalPrice().amount());
        assertEquals("주문 접수", order.getStatus().label());
    }

    @Test
    @DisplayName("S2 주문 접수인 주문을 결제하면 결제 완료가 된다")
    void s2() {
        long orderId = newOrder();

        service.pay(orderId);

        assertEquals("결제 완료", service.findOrder(orderId).getStatus().label());
    }

    @Test
    @DisplayName("S3 결제 완료인 주문을 배송하면 배송 중이 된다")
    void s3() {
        long orderId = newOrder();
        service.pay(orderId);

        service.ship(orderId);

        assertEquals("배송 중", service.findOrder(orderId).getStatus().label());
    }

    @Test
    @DisplayName("S4 결제하지 않은 주문을 배송하면 거절한다")
    void s4() {
        long orderId = newOrder();

        assertThrows(IllegalStateException.class, () -> service.ship(orderId));
        assertEquals("주문 접수", service.findOrder(orderId).getStatus().label());
    }

    @Test
    @DisplayName("S5 배송 전인 주문을 취소하면 취소가 된다")
    void s5() {
        long orderId = newOrder();
        service.pay(orderId);

        service.cancel(orderId);

        assertEquals("취소", service.findOrder(orderId).getStatus().label());
    }

    @Test
    @DisplayName("S6 배송 중인 주문을 취소하면 거절한다")
    void s6() {
        long orderId = newOrder();
        service.pay(orderId);
        service.ship(orderId);

        assertThrows(IllegalStateException.class, () -> service.cancel(orderId));
        assertEquals("배송 중", service.findOrder(orderId).getStatus().label());
    }

    @Test
    @DisplayName("S7 프로그램을 실행하면 주문부터 취소 시도까지 장면을 콘솔에 보여준다")
    void s7() {
        PrintStream original = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
        try {
            Main.main(new String[0]);
        } finally {
            System.setOut(original);
        }
        String printed = out.toString(StandardCharsets.UTF_8);

        assertTrue(printed.contains("김철수, 31000원, 주문 접수"));
        assertTrue(printed.contains("주문 항목: 자바 책 2개, 30000원"));
        assertTrue(printed.contains("결제: 결제 완료"));
        assertTrue(printed.contains("배송: 배송 중"));
        assertTrue(printed.contains("취소: 배송 중인 주문은 취소할 수 없습니다"));
    }
}
