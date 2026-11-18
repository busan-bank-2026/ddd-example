package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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

class OrderAggregateTest {

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

    private final OrderService service = new OrderService(new MemoryOrderRepository(), orderItemFactory(), new ddd.member.application.MemberService(members()), new DiscountPolicy(), TestServices.publisher());

    private Order newOrder() {
        return service.findOrder(newOrderId());
    }

    private long newOrderId() {
        return service.placeOrder(new OrderRequest(1, List.of(
                new OrderItemRequest(1, 2),
                new OrderItemRequest(2, 1)), "서울시 강남구"));
    }

    @Test
    @DisplayName("S1 주문 항목은 상품 이름, 가격, 수량을 갖고 항목 금액은 가격 곱하기 수량이다. 주문은 주문 항목 여러 개로 만들고 주문 금액은 항목 금액의 합이다")
    void s1() {
        OrderItem book = new OrderItem(1, "자바 책", 15000, 2);
        assertEquals("자바 책", book.getProductName());
        assertEquals(15000, book.getPrice());
        assertEquals(2, book.getQuantity());
        assertEquals(30000, book.getAmount());

        Order order = newOrder();
        assertEquals(2, order.getItems().size());
        assertEquals("볼펜", order.getItems().get(1).getProductName());
        assertEquals(31000, order.getTotalPrice());
        assertEquals("주문 접수", order.getStatus().label());
    }

    @Test
    @DisplayName("S2 주문에서 꺼낸 항목 목록에 밖에서 항목을 더하면 주문 금액이 항목과 맞지 않는 문제를 실행해 보여준다")
    void s2() {
        Order order = newOrder();

        assertThrows(UnsupportedOperationException.class,
                () -> order.getItems().add(new OrderItem(3, "공책", 2000, 5)));
        assertEquals(2, order.getItems().size());
        assertEquals(31000, order.getTotalPrice());
    }

    @Test
    @DisplayName("S3 주문 밖에서는 주문의 항목 목록을 바꿀 수 없다")
    void s3() {
        Order order = newOrder();

        assertThrows(UnsupportedOperationException.class,
                () -> order.getItems().add(new OrderItem(3, "공책", 2000, 5)));
        assertEquals(2, order.getItems().size());
        assertEquals(31000, order.getTotalPrice());

        List<OrderItem> given = new ArrayList<>();
        given.add(new OrderItem(1, "자바 책", 15000, 2));
        Order fromGiven = new Order(1, 1, given, new Address("서울시 강남구"));
        given.add(new OrderItem(3, "공책", 2000, 5));
        assertEquals(1, fromGiven.getItems().size());
        assertEquals(30000, fromGiven.getTotalPrice());
    }

    @Test
    @DisplayName("S4 항목 더하기는 주문에게 시킨다. 항목을 더하면 주문 금액도 함께 바뀐다")
    void s4() {
        Order order = newOrder();

        order.addItem(new OrderItem(3, "공책", 2000, 5));

        assertEquals(3, order.getItems().size());
        assertEquals("공책", order.getItems().get(2).getProductName());
        assertEquals(41000, order.getTotalPrice());
    }

    @Test
    @DisplayName("S5 주문 접수가 아닌 주문에는 항목을 더할 수 없다. 결제한 주문에 항목을 더하면 거절한다")
    void s5() {
        long orderId = newOrderId();
        service.pay(orderId);
        Order order = service.findOrder(orderId);

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> order.addItem(new OrderItem(3, "공책", 2000, 5)));

        assertEquals("주문 접수인 주문에만 항목을 더할 수 있습니다", error.getMessage());
        assertEquals(2, order.getItems().size());
        assertEquals(31000, order.getTotalPrice());
    }

    @Test
    @DisplayName("S6 주문은 항목이 하나 이상이어야 한다. 항목 없이 주문하면 거절한다")
    void s6() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(new OrderRequest(2, List.of(), "인천시 연수구")));
        assertEquals("주문 항목이 하나 이상 있어야 합니다", error.getMessage());
        assertThrows(IllegalArgumentException.class,
                () -> new Order(1, 2, List.of(), new Address("인천시 연수구")));
    }

    @Test
    @DisplayName("S7 주문 항목의 가격은 0원 이상, 수량은 1개 이상이다. 음수 가격이나 0개 항목으로 주문하면 거절한다")
    void s7() {
        IllegalArgumentException negative = assertThrows(IllegalArgumentException.class,
                () -> new OrderItem(1, "자바 책", -15000, 1));
        assertEquals("가격은 0원 이상이어야 합니다", negative.getMessage());
        assertEquals(0, new OrderItem(1, "자바 책", 0, 1).getPrice());

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> new OrderItem(1, "자바 책", 15000, 0));
        assertEquals("수량은 1개 이상이어야 합니다", error.getMessage());
        assertEquals(1, new OrderItem(1, "자바 책", 15000, 1).getQuantity());
    }

    @Test
    @DisplayName("S8 용어집에 주문 항목, 항목 금액, 주문 항목 더하기를 더한다")
    void s8() throws Exception {
        String glossary = Files.readString(Path.of("glossary.md"));

        assertTrue(glossary.contains("| 주문 항목 | OrderItem |"));
        assertTrue(glossary.contains("| 항목 금액 | amount |"));
        assertTrue(glossary.contains("| 주문 항목 더하기 | addItem |"));
    }
}
