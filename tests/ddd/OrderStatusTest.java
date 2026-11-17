package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

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
import ddd.order.domain.OrderStatus;
import ddd.order.infrastructure.LegacyPaymentAdapter;
import ddd.order.infrastructure.MemoryOrderRepository;
import legacypay.LegacyPayGateway;

class OrderStatusTest {

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

    private final OrderService service = new OrderService(new MemoryOrderRepository(), orderItemFactory(), members(), new DiscountPolicy(), new EventPublisher(), new LegacyPaymentAdapter(new LegacyPayGateway()), message -> {});

    private long newOrder() {
        return service.placeOrder(new OrderRequest(1, List.of(new OrderItemRequest(1, 2)), "서울시 강남구"));
    }

    private static Order orderIn(OrderStatus status) {
        Order order = new Order(1, 1, List.of(new OrderItem(1, "자바 책", 15000, 2)), new Address("서울시 강남구"));
        if (status == OrderStatus.CANCELED) {
            order.cancel();
            return order;
        }
        if (status != OrderStatus.ORDERED) {
            order.pay(0);
        }
        if (status == OrderStatus.SHIPPING || status == OrderStatus.DELIVERED) {
            order.ship();
        }
        if (status == OrderStatus.DELIVERED) {
            order.deliver();
        }
        return order;
    }

    private static boolean changes(OrderStatus status, Consumer<Order> action) {
        Order order = orderIn(status);
        try {
            action.accept(order);
            return true;
        } catch (IllegalStateException e) {
            assertEquals(status, order.getStatus());
            return false;
        }
    }

    @Test
    @DisplayName("S1 주문 상태는 주문 접수, 결제 완료, 배송 중, 취소 중 하나로 정해져 있고 출력에는 업무 말로 보여준다")
    void s1() {
        long orderId = newOrder();

        assertEquals(OrderStatus.ORDERED, service.findOrder(orderId).getStatus());
        assertEquals("주문 접수", OrderStatus.ORDERED.label());
        assertEquals("결제 완료", OrderStatus.PAID.label());
        assertEquals("배송 중", OrderStatus.SHIPPING.label());
        assertEquals("취소", OrderStatus.CANCELED.label());

        service.pay(orderId);
        assertEquals(OrderStatus.PAID, service.findOrder(orderId).getStatus());
        service.ship(orderId);
        assertEquals(OrderStatus.SHIPPING, service.findOrder(orderId).getStatus());

        long canceledId = newOrder();
        service.cancel(canceledId);
        assertEquals(OrderStatus.CANCELED, service.findOrder(canceledId).getStatus());
    }

    @Test
    @DisplayName("S2 결제, 배송, 취소는 주문이 지금 상태를 보고 정한다. 결제는 주문 접수에서만, 배송은 결제 완료에서만, 취소는 주문 접수나 결제 완료에서만 된다. 취소는 다른 상태로 바뀌지 않는다")
    void s2() {
        for (OrderStatus status : List.of(OrderStatus.ORDERED, OrderStatus.PAID, OrderStatus.SHIPPING,
                OrderStatus.CANCELED)) {
            assertEquals(status == OrderStatus.ORDERED, changes(status, order -> order.pay(0)));
            assertEquals(status == OrderStatus.PAID, changes(status, Order::ship));
            assertEquals(status == OrderStatus.ORDERED || status == OrderStatus.PAID, changes(status, Order::cancel));
        }

        long canceledId = newOrder();
        service.cancel(canceledId);
        assertThrows(IllegalStateException.class, () -> service.pay(canceledId));
        assertEquals(OrderStatus.CANCELED, service.findOrder(canceledId).getStatus());
    }

    @Test
    @DisplayName("S3 바꿀 수 없는 상태로 바꾸라고 하면 지금 상태를 넣은 말로 거절한다. 예: 주문 접수인 주문은 배송할 수 없습니다")
    void s3() {
        long unpaidId = newOrder();
        IllegalStateException shipError = assertThrows(IllegalStateException.class, () -> service.ship(unpaidId));
        assertEquals("주문 접수인 주문은 배송할 수 없습니다", shipError.getMessage());

        long shippingId = newOrder();
        service.pay(shippingId);
        service.ship(shippingId);
        IllegalStateException cancelError = assertThrows(IllegalStateException.class, () -> service.cancel(shippingId));
        assertEquals("배송 중인 주문은 취소할 수 없습니다", cancelError.getMessage());

        long canceledId = newOrder();
        service.cancel(canceledId);
        IllegalStateException againError = assertThrows(IllegalStateException.class, () -> service.cancel(canceledId));
        assertEquals("취소인 주문은 취소할 수 없습니다", againError.getMessage());
    }

    @Test
    @DisplayName("S4 결제 완료인 주문을 다시 결제하면 거절한다")
    void s4() {
        long orderId = newOrder();
        service.pay(orderId);

        IllegalStateException error = assertThrows(IllegalStateException.class, () -> service.pay(orderId));

        assertEquals("결제 완료인 주문은 결제할 수 없습니다", error.getMessage());
        assertEquals(OrderStatus.PAID, service.findOrder(orderId).getStatus());
    }

    @Test
    @DisplayName("S5 배송 중인 주문을 배송 완료하면 배송 완료가 된다. 배송 완료인 주문은 다른 상태로 바뀌지 않는다")
    void s5() {
        long orderId = newOrder();
        service.pay(orderId);
        service.ship(orderId);

        service.deliver(orderId);

        assertEquals(OrderStatus.DELIVERED, service.findOrder(orderId).getStatus());
        assertEquals("배송 완료", service.findOrder(orderId).getStatus().label());
        assertTrue(changes(OrderStatus.SHIPPING, Order::deliver));
        assertFalse(changes(OrderStatus.DELIVERED, order -> order.pay(0)));
        assertFalse(changes(OrderStatus.DELIVERED, Order::ship));
        assertFalse(changes(OrderStatus.DELIVERED, Order::deliver));
        assertFalse(changes(OrderStatus.DELIVERED, Order::cancel));
        IllegalStateException cancelError = assertThrows(IllegalStateException.class, () -> service.cancel(orderId));
        assertEquals("배송 완료인 주문은 취소할 수 없습니다", cancelError.getMessage());

        long unpaidId = newOrder();
        IllegalStateException deliverError = assertThrows(IllegalStateException.class, () -> service.deliver(unpaidId));
        assertEquals("주문 접수인 주문은 배송 완료할 수 없습니다", deliverError.getMessage());
    }

    @Test
    @DisplayName("S6 용어집에 주문 상태, 배송 완료하기, 배송 완료를 더하고 상태마다 코드 이름을 적는다")
    void s6() throws Exception {
        String glossary = Files.readString(Path.of("glossary.md"));

        assertTrue(glossary.contains("| 주문 상태 | OrderStatus |"));
        assertTrue(glossary.contains("| 주문 접수 | ORDERED |"));
        assertTrue(glossary.contains("| 결제 완료 | PAID |"));
        assertTrue(glossary.contains("| 배송 중 | SHIPPING |"));
        assertTrue(glossary.contains("| 취소 | CANCELED |"));
        assertTrue(glossary.contains("| 배송 완료하기 | deliver |"));
        assertTrue(glossary.contains("| 배송 완료 | DELIVERED |"));
    }
}
