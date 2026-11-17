package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.RecordComponent;
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
import ddd.order.application.OrderQueryService;
import ddd.order.application.OrderSummary;
import ddd.order.application.OrderRequest;
import ddd.order.domain.Address;
import ddd.order.domain.DiscountPolicy;
import ddd.order.domain.Order;
import ddd.order.domain.OrderItem;
import ddd.order.domain.OrderItemFactory;
import ddd.order.domain.OrderRepository;
import ddd.order.domain.PaymentResult;
import ddd.order.infrastructure.MemoryOrderRepository;

class OrderQueryTest {

    private static Order order(long id, long memberId) {
        return new Order(id, memberId, List.of(new OrderItem(1, "자바 책", 15000, 2)), new Address("서울시 강남구"));
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
    @DisplayName("S2 주문 요약은 주문 목록 화면 한 줄에 맞춘 읽기 모델이다. 주문 번호, 회원 이름, 항목 수, 결제 금액, 상태 글자를 숫자와 글자로만 가진다. 바꾸는 동작이 없다")
    void s2() {
        assertEquals("ddd.order.application", OrderSummary.class.getPackageName());
        assertTrue(OrderSummary.class.isRecord());
        RecordComponent[] components = OrderSummary.class.getRecordComponents();
        assertEquals(List.of("orderId", "memberName", "itemCount", "paymentPrice", "status"),
                Arrays.stream(components).map(RecordComponent::getName).toList());
        assertEquals(List.of(long.class, String.class, int.class, int.class, String.class),
                Arrays.stream(components).map(RecordComponent::getType).toList());

        OrderSummary summary = new OrderSummary(1, "김철수", 2, 27900, "결제 완료");
        assertEquals(new OrderSummary(1, "김철수", 2, 27900, "결제 완료"), summary);
        assertEquals(27900, summary.paymentPrice());
    }

    @Test
    @DisplayName("S3 주문 조회 서비스는 회원 번호로 그 회원의 주문 요약 목록을 돌려준다. 주문을 바꾸거나 저장하지 않는다. 주문 응용 서비스와 따로 있고 결제 창구와 발행기를 모른다. Main은 김철수의 주문 목록을 보여준다")
    void s3() throws Exception {
        ProductRepository products = new MemoryProductRepository();
        products.save(new Product(1, "자바 책", 15000, 10));
        products.save(new Product(2, "볼펜", 1000, 10));
        MemberRepository members = new MemoryMemberRepository();
        members.save(new Member(1, "김철수", MemberGrade.VIP));
        members.save(new Member(2, "이영희", MemberGrade.BASIC));
        OrderRepository orders = new MemoryOrderRepository();
        OrderService orderService = new OrderService(orders, new OrderItemFactory(products),
                members, new DiscountPolicy(), new EventPublisher(), amount -> new PaymentResult(true, ""), message -> {});

        long paidId = orderService.placeOrder(new OrderRequest(1,
                List.of(new OrderItemRequest(1, 2), new OrderItemRequest(2, 1)), "서울시 강남구"));
        orderService.pay(paidId);
        long otherId = orderService.placeOrder(new OrderRequest(2, List.of(new OrderItemRequest(2, 3)), "부산시 해운대구"));
        long waitingId = orderService.placeOrder(new OrderRequest(1, List.of(new OrderItemRequest(1, 1)), "제주시 연동"));
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
        assertNotEquals(OrderService.class, OrderQueryService.class);
        assertEquals(List.of(OrderRepository.class, MemberRepository.class),
                List.of(OrderQueryService.class.getConstructors()[0].getParameterTypes()));
        String source = Files.readString(Path.of("src/ddd/order/application/OrderQueryService.java"));
        assertTrue(!source.contains(".save(") && !source.contains("throw "));
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
