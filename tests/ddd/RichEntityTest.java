package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
import ddd.order.application.OrderItemRequest;
import ddd.order.application.OrderRequest;
import ddd.order.domain.Address;
import ddd.order.domain.DiscountPolicy;
import ddd.order.domain.Order;
import ddd.order.domain.OrderItem;
import ddd.order.domain.OrderItemFactory;
import ddd.order.infrastructure.MemoryOrderRepository;

class RichEntityTest {

    private static MemberRepository members() {
        MemberRepository members = new MemoryMemberRepository();
        members.save(new Member(1, "김철수", MemberGrade.BASIC));
        members.save(new Member(2, "이영희", MemberGrade.BASIC));
        return members;
    }

    private static OrderItemFactory orderItemFactory() {
        ProductRepository products = new MemoryProductRepository();
        products.save(new Product(1, "자바 책", 15000, 10));
        products.save(new Product(2, "볼펜", 1000, 10));
        products.save(new Product(3, "공책", 2000, 10));
        return new OrderItemFactory(products);
    }

    @Test
    @DisplayName("S1 결제하지 않은 주문의 상태를 서비스를 거치지 않고 배송 중으로 바꾸면 그대로 바뀌는 문제를 실행해 보여준다")
    void s1() {

    }

    @Test
    @DisplayName("S2 주문은 만들 때 주문 번호를 받고, 같은 번호면 같은 주문으로 본다")
    void s2() {
        Order first = new Order(1, 1, List.of(new OrderItem(1, "자바 책", 15000, 2)), new Address("서울시 강남구"));
        Order sameNumber = new Order(1, 2, List.of(new OrderItem(4, "연필", 500, 3)), new Address("부산시 해운대구"));
        Order otherNumber = new Order(2, 1, List.of(new OrderItem(1, "자바 책", 15000, 2)), new Address("서울시 강남구"));

        assertEquals(1, first.getId());
        assertEquals(first, sameNumber);
        assertEquals(first.hashCode(), sameNumber.hashCode());
        assertNotEquals(first, otherNumber);

        OrderService service = new OrderService(new MemoryOrderRepository(), orderItemFactory(), new ddd.member.application.MemberService(members()), new DiscountPolicy(), TestServices.publisher());
        long placed1 = service.placeOrder(new OrderRequest(1, List.of(new OrderItemRequest(1, 2)), "서울시 강남구"));
        long placed2 = service.placeOrder(new OrderRequest(1, List.of(new OrderItemRequest(1, 2)), "서울시 강남구"));
        assertEquals(1, placed1);
        assertEquals(2, placed2);
        assertEquals(1, service.findOrder(placed1).getId());
        assertNotEquals(service.findOrder(placed1), service.findOrder(placed2));
    }

    @Test
    @DisplayName("S3 주문을 만들 때 주문 금액과 주문 접수 상태를 주문이 스스로 정한다")
    void s3() {
        Order order = new Order(1, 1, List.of(new OrderItem(1, "자바 책", 15000, 2)), new Address("서울시 강남구"));

        assertEquals(30000, order.getTotalPrice());
        assertEquals("주문 접수", order.getStatus().label());
        assertEquals(1L, order.getMemberId());
        assertEquals("서울시 강남구", order.getAddress().value());
    }

    @Test
    @DisplayName("S4 결제, 배송, 취소 규칙을 주문이 스스로 지킨다. 서비스는 주문에게 시키기만 한다")
    void s4() {
        Order order = new Order(1, 1, List.of(new OrderItem(1, "자바 책", 15000, 2)), new Address("서울시 강남구"));

        order.pay(0);
        assertEquals("결제 완료", order.getStatus().label());
        order.ship();
        assertEquals("배송 중", order.getStatus().label());
        IllegalStateException cancelError = assertThrows(IllegalStateException.class, order::cancel);
        assertEquals("배송 중인 주문은 취소할 수 없습니다", cancelError.getMessage());
        assertEquals("배송 중", order.getStatus().label());

        Order unpaid = new Order(2, 1, List.of(new OrderItem(1, "자바 책", 15000, 2)), new Address("서울시 강남구"));
        IllegalStateException shipError = assertThrows(IllegalStateException.class, unpaid::ship);
        assertEquals("주문 접수인 주문은 배송할 수 없습니다", shipError.getMessage());
        unpaid.cancel();
        assertEquals("취소", unpaid.getStatus().label());

        OrderService service = new OrderService(new MemoryOrderRepository(), orderItemFactory(), new ddd.member.application.MemberService(members()), new DiscountPolicy(), TestServices.publisher());
        long placed = service.placeOrder(new OrderRequest(1, List.of(new OrderItemRequest(1, 2)), "서울시 강남구"));
        service.pay(placed);
        service.ship(placed);
        assertEquals("배송 중", service.findOrder(placed).getStatus().label());
        assertThrows(IllegalStateException.class, () -> service.cancel(placed));
    }

    @Test
    @DisplayName("S5 주문 밖에서는 상태를 직접 바꿀 수 없다. 결제 안 한 주문을 배송하라고 하면 주문이 거절한다")
    void s5() {
        boolean hasSetter = Arrays.stream(Order.class.getMethods())
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .map(Method::getName)
                .anyMatch(name -> name.startsWith("set"));

        assertTrue(!hasSetter);
    }

    @Test
    @DisplayName("S6 용어집에 주문 번호를 더한다")
    void s6() throws Exception {
        String glossary = Files.readString(Path.of("glossary.md"));

        assertTrue(glossary.contains("| 주문 번호 | id |"));
    }
}
