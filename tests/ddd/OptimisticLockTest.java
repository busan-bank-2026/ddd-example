package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import ddd.order.application.OrderItemRequest;
import ddd.order.application.OrderRequest;
import ddd.order.domain.Address;
import ddd.order.domain.DiscountPolicy;
import ddd.order.domain.Order;
import ddd.order.domain.OrderItem;
import ddd.order.domain.OrderItemFactory;
import ddd.order.domain.OrderRepository;
import ddd.order.domain.OrderStatus;
import ddd.order.infrastructure.LegacyPaymentAdapter;
import ddd.order.infrastructure.MemoryOrderRepository;
import legacypay.LegacyPayGateway;

class OptimisticLockTest {

    private static Order newOrder() {
        return new Order(1, 1, List.of(new OrderItem(1, "자바 책", 15000, 2)), new Address("서울시 강남구"));
    }

    private static ProductRepository products() {
        ProductRepository products = new MemoryProductRepository();
        products.save(new Product(1, "자바 책", 15000, 10));
        return products;
    }

    private static MemberRepository members() {
        MemberRepository members = new MemoryMemberRepository();
        members.save(new Member(1, "김철수", MemberGrade.BASIC));
        return members;
    }

    @Test
    @DisplayName("S1 결제 완료인 같은 주문을 두 번 꺼내 첫째로 배송하고 저장한 뒤, 옛 상태인 둘째로 취소하고 저장하면 배송 중인 주문이 취소로 덮이는 문제를 실행해 보여준다")
    void s1() {
        OrderRepository orders = new MemoryOrderRepository();
        Order order = newOrder();
        order.pay(0);
        orders.save(order);

        Order orderA = orders.findById(1).orElseThrow();
        Order orderB = orders.findById(1).orElseThrow();
        orderA.ship();
        assertEquals(OrderStatus.SHIPPING, orderA.getStatus());
        assertEquals(OrderStatus.PAID, orderB.getStatus());
        orderB.cancel();
        assertEquals(OrderStatus.CANCELED, orderB.getStatus());
    }

    @Test
    @DisplayName("S2 주문은 버전을 가진다. 새 주문은 버전 0이고, 주문 저장소에 저장할 때마다 버전이 1씩 오른다. 꺼낸 주문은 저장된 버전을 가진다. Main이 결제하고 저장한 주문의 버전을 전 -> 후로 보여준다")
    void s2() {
        Order order = newOrder();
        assertEquals(0, order.getVersion());

        OrderRepository orders = new MemoryOrderRepository();
        orders.save(order);
        assertEquals(1, order.getVersion());
        Order found = orders.findById(1).orElseThrow();
        assertEquals(1, found.getVersion());
        orders.save(found);
        assertEquals(2, orders.findById(1).orElseThrow().getVersion());

        OrderRepository serviceOrders = new MemoryOrderRepository();
        OrderService service = new OrderService(serviceOrders, new OrderItemFactory(products()),
                members(), new DiscountPolicy(), new EventPublisher(), new LegacyPaymentAdapter(new LegacyPayGateway()), message -> {});
        long orderId = service.placeOrder(new OrderRequest(1, List.of(new OrderItemRequest(1, 2)), "서울시 강남구"));
        assertEquals(1, serviceOrders.findById(orderId).orElseThrow().getVersion());
        service.pay(orderId);
        assertEquals(2, serviceOrders.findById(orderId).orElseThrow().getVersion());
    }

    @Test
    @DisplayName("S3 주문 저장소는 저장된 주문과 저장하려는 주문의 버전이 다르면 다른 곳에서 먼저 바꾼 주문이라고 저장을 거절한다. 거절되면 저장된 주문은 그대로다. Main의 동시 수정 장면은 거절 메시지와 저장된 주문 상태를 보여준다")
    void s3() {
        OrderRepository orders = new MemoryOrderRepository();
        Order order = newOrder();
        order.pay(0);
        orders.save(order);

        Order orderA = orders.findById(1).orElseThrow();
        Order orderB = orders.findById(1).orElseThrow();
        orderA.ship();
        orders.save(orderA);
        orderB.cancel();
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> orders.save(orderB));
        assertEquals("다른 곳에서 먼저 바꾼 주문입니다", e.getMessage());
        assertEquals(OrderStatus.SHIPPING, orders.findById(1).orElseThrow().getStatus());

        assertThrows(IllegalStateException.class, () -> orders.save(newOrder()));
        assertEquals(OrderStatus.SHIPPING, orders.findById(1).orElseThrow().getStatus());
    }

    @Test
    @DisplayName("S4 용어집에 버전을 더하고 저장하기 줄에 버전 확인을 넣는다")
    void s4() throws Exception {
        String glossary = Files.readString(Path.of("glossary.md"));

        assertTrue(glossary.contains("| 버전 | version |"));
        String saveLine = glossary.lines().filter(line -> line.startsWith("| 저장하기 | save |")).findFirst().orElseThrow();
        assertTrue(saveLine.contains("버전"));
    }
}
