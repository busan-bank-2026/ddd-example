package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import ddd.order.infrastructure.LegacyPaymentAdapter;
import ddd.order.infrastructure.MemoryOrderRepository;
import ddd.shared.Money;
import legacypay.LegacyPayGateway;

class OrderRepositoryTest {

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

    private Order newOrder(long id) {
        return new Order(id, 1, List.of(new OrderLine(1, "자바 책", new Money(15000), 2)), new Address("서울시 강남구"));
    }

    @Test
    @DisplayName("S1 주문 저장소에 주문을 저장하고 주문 번호로 다시 꺼낼 수 있다. 없는 번호로 찾으면 빈 결과를 돌려준다")
    void s1() {
        OrderRepository repository = new MemoryOrderRepository();
        repository.save(newOrder(1));
        repository.save(new Order(2, 2, List.of(new OrderLine(2, "볼펜", new Money(1000), 3)), new Address("부산시 해운대구")));

        Order first = repository.findById(1).orElseThrow();
        Order second = repository.findById(2).orElseThrow();

        assertEquals(1, first.getId());
        assertEquals(1L, first.getMemberId());
        assertEquals(new Money(30000), first.getTotalPrice());
        assertEquals(2, second.getId());
        assertEquals(2L, second.getMemberId());
        assertEquals(new Address("부산시 해운대구"), second.getAddress());
        assertTrue(repository.findById(3).isEmpty());
    }

    @Test
    @DisplayName("S2 주문 저장소는 저장할 때와 꺼낼 때 복사본을 쓴다. 꺼낸 주문을 바꿔도 다시 저장하기 전에는 저장소의 주문이 그대로다")
    void s2() {
        OrderRepository repository = new MemoryOrderRepository();
        Order order = newOrder(1);
        repository.save(order);

        order.pay(new Money(0));
        assertEquals(OrderStatus.ORDERED, repository.findById(1).orElseThrow().getStatus());

        Order found = repository.findById(1).orElseThrow();
        found.addOrderLine(new OrderLine(3, "공책", new Money(2000), 5));
        found.cancel();
        Order again = repository.findById(1).orElseThrow();
        assertEquals(OrderStatus.ORDERED, again.getStatus());
        assertEquals(1, again.getOrderLines().size());
        assertEquals(new Money(30000), again.getTotalPrice());

        repository.save(found);
        Order saved = repository.findById(1).orElseThrow();
        assertEquals(OrderStatus.CANCELED, saved.getStatus());
        assertEquals(2, saved.getOrderLines().size());
        assertEquals(new Money(40000), saved.getTotalPrice());

        assertTrue(runMain().contains("저장 안 한 취소: 주문 접수"));
    }

    @Test
    @DisplayName("S3 주문하기는 주문을 주문 저장소에 저장하고 주문 번호를 돌려준다")
    void s3() {
        OrderRepository repository = new MemoryOrderRepository();
        OrderApplicationService service = new OrderApplicationService(repository, orderLineFactory(), members(), new DiscountPolicy(), new EventPublisher(), new LegacyPaymentAdapter(new LegacyPayGateway()));

        long firstId = service.placeOrder(new PlaceOrderCommand(1, List.of(new OrderLineCommand(1, 2)), "서울시 강남구"));
        long secondId = service.placeOrder(new PlaceOrderCommand(2, List.of(new OrderLineCommand(2, 1)), "부산시 해운대구"));

        assertEquals(1, firstId);
        assertEquals(2, secondId);
        assertEquals(1L, repository.findById(firstId).orElseThrow().getMemberId());
        assertEquals(OrderStatus.ORDERED, repository.findById(firstId).orElseThrow().getStatus());
        assertEquals(2L, repository.findById(secondId).orElseThrow().getMemberId());
        assertEquals(2L, service.findOrder(secondId).getMemberId());

        assertTrue(runMain().contains("주문: 1번, 김철수, 31000원, 주문 접수"));
    }

    @Test
    @DisplayName("S4 결제하기, 배송하기, 배송 완료하기, 주문 취소하기, 주문 항목 더하기는 주문 번호를 받아 저장소에서 주문을 꺼내 주문에게 시키고 다시 저장한다")
    void s4() {
        OrderRepository repository = new MemoryOrderRepository();
        OrderApplicationService service = new OrderApplicationService(repository, orderLineFactory(), members(), new DiscountPolicy(), new EventPublisher(), new LegacyPaymentAdapter(new LegacyPayGateway()));
        long orderId = service.placeOrder(new PlaceOrderCommand(1, List.of(new OrderLineCommand(1, 2)), "서울시 강남구"));

        service.addOrderLine(orderId, new OrderLineCommand(3, 5));
        assertEquals(2, repository.findById(orderId).orElseThrow().getOrderLines().size());
        assertEquals(new Money(40000), repository.findById(orderId).orElseThrow().getTotalPrice());

        service.pay(orderId);
        assertEquals(OrderStatus.PAID, repository.findById(orderId).orElseThrow().getStatus());
        service.ship(orderId);
        assertEquals(OrderStatus.SHIPPING, repository.findById(orderId).orElseThrow().getStatus());
        service.deliver(orderId);
        assertEquals(OrderStatus.DELIVERED, repository.findById(orderId).orElseThrow().getStatus());

        long canceledId = service.placeOrder(new PlaceOrderCommand(2, List.of(new OrderLineCommand(2, 1)), "부산시 해운대구"));
        service.cancel(canceledId);
        assertEquals(OrderStatus.CANCELED, repository.findById(canceledId).orElseThrow().getStatus());

        String printed = runMain();
        assertTrue(printed.contains("결제: 결제 완료"));
        assertTrue(printed.contains("배송: 배송 중"));
        assertTrue(printed.contains("항목 더하기: 항목 3개, 주문 금액 41000원"));
        assertTrue(printed.contains("배송 완료: 배송 완료"));
    }

    @Test
    @DisplayName("S5 없는 주문 번호로 결제하면 주문이 없습니다로 거절한다")
    void s5() {
        OrderApplicationService service = new OrderApplicationService(new MemoryOrderRepository(), orderLineFactory(), members(), new DiscountPolicy(), new EventPublisher(), new LegacyPaymentAdapter(new LegacyPayGateway()));
        service.placeOrder(new PlaceOrderCommand(1, List.of(new OrderLineCommand(1, 2)), "서울시 강남구"));

        IllegalArgumentException payError = assertThrows(IllegalArgumentException.class, () -> service.pay(100));
        assertEquals("주문이 없습니다", payError.getMessage());
        IllegalArgumentException findError = assertThrows(IllegalArgumentException.class, () -> service.findOrder(100));
        assertEquals("주문이 없습니다", findError.getMessage());

        assertTrue(runMain().contains("없는 주문 결제: 주문이 없습니다"));
    }

    @Test
    @DisplayName("S6 용어집에 주문 저장소, 저장하기, 주문 번호로 찾기를 더한다")
    void s6() throws Exception {
        String glossary = Files.readString(Path.of("glossary.md"));

        assertTrue(glossary.contains("| 주문 저장소 | OrderRepository |"));
        assertTrue(glossary.contains("| 저장하기 | save |"));
        assertTrue(glossary.contains("| 주문 번호로 찾기 | findById |"));
    }
}
