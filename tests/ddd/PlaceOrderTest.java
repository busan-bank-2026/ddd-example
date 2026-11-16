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

import ddd.application.EventPublisher;
import ddd.application.OrderApplicationService;
import ddd.application.OrderLineCommand;
import ddd.application.PlaceOrderCommand;
import ddd.domain.Address;
import ddd.domain.DiscountPolicy;
import ddd.domain.Member;
import ddd.domain.MemberGrade;
import ddd.domain.MemberRepository;
import ddd.domain.Money;
import ddd.domain.Order;
import ddd.domain.OrderLine;
import ddd.domain.OrderLineFactory;
import ddd.domain.OrderRepository;
import ddd.domain.OrderStatus;
import ddd.domain.Product;
import ddd.domain.ProductRepository;
import ddd.infrastructure.MemoryMemberRepository;
import ddd.infrastructure.MemoryOrderRepository;
import ddd.infrastructure.MemoryProductRepository;

class PlaceOrderTest {

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

    @Test
    @DisplayName("S1 주문하기는 주문하기 명령 하나를 받는다. 주문하기 명령은 주문자, 주문 항목 명령 목록, 배송 주소를 담고, 주문 항목 명령은 상품 이름, 가격, 수량을 담는다")
    void s1() {
        OrderLineCommand book = new OrderLineCommand(1, 2);
        PlaceOrderCommand command = new PlaceOrderCommand(1, List.of(book), "서울시 강남구");

        assertEquals(1L, book.productId());
        assertEquals(2, book.quantity());
        assertEquals(1L, command.memberId());
        assertEquals(List.of(book), command.orderLines());
        assertEquals("서울시 강남구", command.address());

        OrderApplicationService service = new OrderApplicationService(new MemoryOrderRepository(), orderLineFactory(), members(), new DiscountPolicy(), new EventPublisher());
        long orderId = service.placeOrder(command);
        assertEquals(1L, service.findOrder(orderId).getMemberId());
    }

    @Test
    @DisplayName("S2 주문하기는 주문하기 명령으로 주문을 만들어 주문 저장소에 저장하고 주문 번호를 돌려준다")
    void s2() {
        OrderRepository repository = new MemoryOrderRepository();
        OrderApplicationService service = new OrderApplicationService(repository, orderLineFactory(), members(), new DiscountPolicy(), new EventPublisher());

        long firstId = service.placeOrder(new PlaceOrderCommand(1, List.of(
                new OrderLineCommand(1, 2),
                new OrderLineCommand(2, 1)), "서울시 강남구"));
        long secondId = service.placeOrder(new PlaceOrderCommand(2,
                List.of(new OrderLineCommand(3, 3)), "부산시 해운대구"));

        assertEquals(1, firstId);
        assertEquals(2, secondId);
        Order first = repository.findById(firstId).orElseThrow();
        assertEquals(1L, first.getMemberId());
        assertEquals(List.of(new OrderLine(1, "자바 책", new Money(15000), 2), new OrderLine(2, "볼펜", new Money(1000), 1)),
                first.getOrderLines());
        assertEquals(new Address("서울시 강남구"), first.getAddress());
        assertEquals(new Money(31000), first.getTotalPrice());
        assertEquals(OrderStatus.ORDERED, first.getStatus());
        assertEquals(new Money(6000), repository.findById(secondId).orElseThrow().getTotalPrice());

        assertTrue(runMain().contains("주문: 1번, 김철수, 31000원, 주문 접수"));
    }

    @Test
    @DisplayName("S3 주문 항목 더하기는 주문 번호와 주문 항목 명령을 받아 주문을 꺼내 주문에게 시키고 다시 저장한다")
    void s3() {
        OrderRepository repository = new MemoryOrderRepository();
        OrderApplicationService service = new OrderApplicationService(repository, orderLineFactory(), members(), new DiscountPolicy(), new EventPublisher());
        long orderId = service.placeOrder(new PlaceOrderCommand(1,
                List.of(new OrderLineCommand(1, 2)), "서울시 강남구"));

        service.addOrderLine(orderId, new OrderLineCommand(3, 5));

        Order saved = repository.findById(orderId).orElseThrow();
        assertEquals(2, saved.getOrderLines().size());
        assertEquals(new OrderLine(3, "공책", new Money(2000), 5), saved.getOrderLines().get(1));
        assertEquals(new Money(40000), saved.getTotalPrice());

        assertTrue(runMain().contains("항목 더하기: 항목 3개, 주문 금액 41000원"));
    }

    @Test
    @DisplayName("S4 명령의 값이 규칙에 맞지 않으면 주문, 주문 항목, 돈, 주소가 정한 거절 말 그대로 거절하고 아무것도 저장하지 않는다")
    void s4() {
        OrderRepository repository = new MemoryOrderRepository();
        OrderApplicationService service = new OrderApplicationService(repository, orderLineFactory(), members(), new DiscountPolicy(), new EventPublisher());

        IllegalArgumentException negative = assertThrows(IllegalArgumentException.class,
                () -> new Product(4, "할인 책", new Money(-15000), 10));
        assertEquals("금액은 0원 이상이어야 합니다", negative.getMessage());

        IllegalArgumentException blank = assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(new PlaceOrderCommand(2,
                        List.of(new OrderLineCommand(1, 1)), "")));
        assertEquals("배송 주소를 입력해야 합니다", blank.getMessage());

        IllegalArgumentException empty = assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(new PlaceOrderCommand(2, List.of(), "인천시 연수구")));
        assertEquals("주문 항목이 하나 이상 있어야 합니다", empty.getMessage());

        IllegalArgumentException zero = assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(new PlaceOrderCommand(2,
                        List.of(new OrderLineCommand(1, 0)), "대전시 유성구")));
        assertEquals("수량은 1개 이상이어야 합니다", zero.getMessage());

        for (long id = 1; id <= 4; id++) {
            assertTrue(repository.findById(id).isEmpty());
        }

        long orderId = service.placeOrder(new PlaceOrderCommand(1,
                List.of(new OrderLineCommand(1, 2)), "서울시 강남구"));
        assertThrows(IllegalArgumentException.class,
                () -> service.addOrderLine(orderId, new OrderLineCommand(3, 0)));
        assertEquals(1, repository.findById(orderId).orElseThrow().getOrderLines().size());

        String printed = runMain();
        assertTrue(printed.contains("음수 금액 주문: 금액은 0원 이상이어야 합니다"));
        assertTrue(printed.contains("빈 주소 주문: 배송 주소를 입력해야 합니다"));
        assertTrue(printed.contains("빈 주문: 주문 항목이 하나 이상 있어야 합니다"));
        assertTrue(printed.contains("0개 항목 주문: 수량은 1개 이상이어야 합니다"));
    }

    @Test
    @DisplayName("S5 주문 응용 서비스가 결제하기, 배송하기, 배송 완료하기, 주문 취소하기를 주문 번호로 받아 주문을 꺼내 주문에게 시키고 다시 저장한다")
    void s5() {
        OrderRepository repository = new MemoryOrderRepository();
        OrderApplicationService service = new OrderApplicationService(repository, orderLineFactory(), members(), new DiscountPolicy(), new EventPublisher());
        long orderId = service.placeOrder(new PlaceOrderCommand(1,
                List.of(new OrderLineCommand(1, 2)), "서울시 강남구"));

        IllegalStateException unpaid = assertThrows(IllegalStateException.class, () -> service.ship(orderId));
        assertEquals("주문 접수인 주문은 배송할 수 없습니다", unpaid.getMessage());
        assertEquals(OrderStatus.ORDERED, repository.findById(orderId).orElseThrow().getStatus());

        service.pay(orderId);
        assertEquals(OrderStatus.PAID, repository.findById(orderId).orElseThrow().getStatus());
        service.ship(orderId);
        assertEquals(OrderStatus.SHIPPING, repository.findById(orderId).orElseThrow().getStatus());
        service.deliver(orderId);
        assertEquals(OrderStatus.DELIVERED, repository.findById(orderId).orElseThrow().getStatus());

        long canceledId = service.placeOrder(new PlaceOrderCommand(2,
                List.of(new OrderLineCommand(2, 1)), "부산시 해운대구"));
        service.cancel(canceledId);
        assertEquals(OrderStatus.CANCELED, repository.findById(canceledId).orElseThrow().getStatus());

        String printed = runMain();
        assertTrue(printed.contains("결제 없이 배송: 주문 접수인 주문은 배송할 수 없습니다"));
        assertTrue(printed.contains("배송 완료: 배송 완료"));
    }

    @Test
    @DisplayName("S6 용어집에 주문 응용 서비스, 주문하기 명령, 주문 항목 명령을 더한다")
    void s6() throws Exception {
        String glossary = Files.readString(Path.of("glossary.md"));

        assertTrue(glossary.contains("| 주문 응용 서비스 | OrderApplicationService |"));
        assertTrue(glossary.contains("| 주문하기 명령 | PlaceOrderCommand |"));
        assertTrue(glossary.contains("| 주문 항목 명령 | OrderLineCommand |"));
    }
}
