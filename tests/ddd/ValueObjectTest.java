package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
import ddd.order.application.OrderItemRequest;
import ddd.order.application.OrderRequest;
import ddd.order.domain.Address;
import ddd.order.domain.DiscountPolicy;
import ddd.order.domain.Order;
import ddd.order.domain.OrderItem;
import ddd.order.domain.OrderItemFactory;
import ddd.order.infrastructure.MemoryOrderRepository;

class ValueObjectTest {

    private static OrderService service() {
        MemberRepository members = new MemoryMemberRepository();
        members.save(new Member(1, "김철수", MemberGrade.BASIC));
        members.save(new Member(2, "이영희", MemberGrade.BASIC));
        ProductRepository products = new MemoryProductRepository();
        products.save(new Product(1, "자바 책", 15000, 10));
        return new OrderService(new MemoryOrderRepository(), new OrderItemFactory(products), new ddd.member.application.MemberService(members),
                new DiscountPolicy(), TestServices.publisher());
    }

    @Test
    @DisplayName("S1 빈 주소로 주문하면 받을 곳 없는 주문이 그대로 만들어지는 문제를 실행해 보여준다")
    void s1() {
        assertEquals(Address.class, Order.class.getConstructors()[0].getParameterTypes()[3]);
        assertThrows(IllegalArgumentException.class, () -> new Address(""));
        assertEquals("서울시 강남구",
                new Order(1, 1, List.of(new OrderItem(1, "자바 책", 15000, 1)), new Address("서울시 강남구"))
                        .getAddress().value());
    }

    @Test
    @DisplayName("S2 배송 주소는 비어 있을 수 없다. 빈 주소로 주소를 만들면 거절한다")
    void s2() {
        IllegalArgumentException blank = assertThrows(IllegalArgumentException.class, () -> new Address(" "));
        assertEquals("배송 주소를 입력해야 합니다", blank.getMessage());
        assertThrows(IllegalArgumentException.class, () -> new Address(""));
        assertThrows(IllegalArgumentException.class, () -> new Address(null));
        assertEquals("서울시 강남구", new Address("서울시 강남구").value());
    }

    @Test
    @DisplayName("S3 글자가 같으면 같은 주소다. 주소는 만든 뒤 바꿀 수 없다")
    void s3() {
        assertEquals(new Address("서울시 강남구"), new Address("서울시 강남구"));
        assertEquals(new Address("서울시 강남구").hashCode(), new Address("서울시 강남구").hashCode());
        assertNotEquals(new Address("부산시 해운대구"), new Address("서울시 강남구"));

        for (Field field : Address.class.getDeclaredFields()) {
            assertTrue(Modifier.isFinal(field.getModifiers()));
        }
        for (Method method : Address.class.getDeclaredMethods()) {
            assertFalse(method.getName().startsWith("set"));
        }
    }

    @Test
    @DisplayName("S4 주문의 배송 주소는 주소다. 빈 주소로 주문하면 주문이 만들어지지 않고 거절 메시지를 보여준다")
    void s4() throws Exception {
        OrderService service = service();
        Order order = service.findOrder(service.placeOrder(
                new OrderRequest(1, List.of(new OrderItemRequest(1, 2)), "서울시 강남구")));
        assertEquals(Address.class, Order.class.getMethod("getAddress").getReturnType());
        assertEquals(new Address("서울시 강남구"), order.getAddress());

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(new OrderRequest(2, List.of(new OrderItemRequest(1, 1)), "")));
        assertEquals("배송 주소를 입력해야 합니다", error.getMessage());
        assertThrows(IllegalArgumentException.class, () -> service.findOrder(2));
    }

    @Test
    @DisplayName("S5 용어집에 주소를 더한다")
    void s5() throws Exception {
        String glossary = Files.readString(Path.of("glossary.md"));

        assertTrue(glossary.contains("| 주소 | Address |"));
        assertFalse(glossary.contains("| 돈 | Money |"));
    }
}
