package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ddd.application.OrderApplicationService;
import ddd.application.OrderLineCommand;
import ddd.application.PlaceOrderCommand;
import ddd.domain.Address;
import ddd.domain.Money;
import ddd.domain.Order;
import ddd.domain.OrderLine;
import ddd.infrastructure.MemoryOrderRepository;

class OrderAggregateTest {

    private final OrderApplicationService service = new OrderApplicationService(new MemoryOrderRepository());

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

    private Order newOrder() {
        return service.findOrder(newOrderId());
    }

    private long newOrderId() {
        return service.placeOrder(new PlaceOrderCommand(1, List.of(
                new OrderLineCommand(1, "자바 책", 15000, 2),
                new OrderLineCommand(2, "볼펜", 1000, 1)), "서울시 강남구"));
    }

    @Test
    @DisplayName("S1 주문 항목은 상품 이름, 가격, 수량을 갖고 항목 금액은 가격 곱하기 수량이다. 주문은 주문 항목 여러 개로 만들고 주문 금액은 항목 금액의 합이다")
    void s1() {
        OrderLine book = new OrderLine(1, "자바 책", new Money(15000), 2);
        assertEquals("자바 책", book.productName());
        assertEquals(new Money(15000), book.price());
        assertEquals(2, book.quantity());
        assertEquals(new Money(30000), book.amount());

        Order order = newOrder();
        assertEquals(2, order.getOrderLines().size());
        assertEquals("볼펜", order.getOrderLines().get(1).productName());
        assertEquals(new Money(31000), order.getTotalPrice());
        assertEquals("주문 접수", order.getStatus().label());

        String printed = runMain();
        assertTrue(printed.contains("주문: 1번, 김철수, 31000원, 주문 접수"));
        assertTrue(printed.contains("주문 항목: 자바 책 2개, 30000원"));
        assertTrue(printed.contains("주문 항목: 볼펜 1개, 1000원"));
    }

    @Test
    @DisplayName("S2 주문에서 꺼낸 항목 목록에 밖에서 항목을 더하면 주문 금액이 항목과 맞지 않는 문제를 실행해 보여준다")
    void s2() {
        String printed = runMain();

        assertTrue(printed.contains("항목 더하기: 항목 3개, 주문 금액 "));
    }

    @Test
    @DisplayName("S3 주문 밖에서는 주문의 항목 목록을 바꿀 수 없다")
    void s3() {
        Order order = newOrder();

        assertThrows(UnsupportedOperationException.class,
                () -> order.getOrderLines().add(new OrderLine(3, "공책", new Money(2000), 5)));
        assertEquals(2, order.getOrderLines().size());
        assertEquals(new Money(31000), order.getTotalPrice());

        List<OrderLine> given = new ArrayList<>();
        given.add(new OrderLine(1, "자바 책", new Money(15000), 2));
        Order fromGiven = new Order(1, 1, given, new Address("서울시 강남구"));
        given.add(new OrderLine(3, "공책", new Money(2000), 5));
        assertEquals(1, fromGiven.getOrderLines().size());
        assertEquals(new Money(30000), fromGiven.getTotalPrice());
    }

    @Test
    @DisplayName("S4 항목 더하기는 주문에게 시킨다. 항목을 더하면 주문 금액도 함께 바뀐다")
    void s4() {
        Order order = newOrder();

        order.addOrderLine(new OrderLine(3, "공책", new Money(2000), 5));

        assertEquals(3, order.getOrderLines().size());
        assertEquals("공책", order.getOrderLines().get(2).productName());
        assertEquals(new Money(41000), order.getTotalPrice());
        assertTrue(runMain().contains("항목 더하기: 항목 3개, 주문 금액 41000원"));
    }

    @Test
    @DisplayName("S5 주문 접수가 아닌 주문에는 항목을 더할 수 없다. 결제한 주문에 항목을 더하면 거절한다")
    void s5() {
        long orderId = newOrderId();
        service.pay(orderId);
        Order order = service.findOrder(orderId);

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> order.addOrderLine(new OrderLine(3, "공책", new Money(2000), 5)));

        assertEquals("주문 접수인 주문에만 항목을 더할 수 있습니다", error.getMessage());
        assertEquals(2, order.getOrderLines().size());
        assertEquals(new Money(31000), order.getTotalPrice());
        assertTrue(runMain().contains("결제 뒤 항목 더하기: 주문 접수인 주문에만 항목을 더할 수 있습니다"));
    }

    @Test
    @DisplayName("S6 주문은 항목이 하나 이상이어야 한다. 항목 없이 주문하면 거절한다")
    void s6() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(new PlaceOrderCommand(2, List.of(), "인천시 연수구")));
        assertEquals("주문 항목이 하나 이상 있어야 합니다", error.getMessage());
        assertThrows(IllegalArgumentException.class,
                () -> new Order(1, 2, List.of(), new Address("인천시 연수구")));

        assertTrue(runMain().contains("빈 주문: 주문 항목이 하나 이상 있어야 합니다"));
    }

    @Test
    @DisplayName("S7 주문 항목의 수량은 1개 이상이다. 0개 항목으로 주문하면 거절한다")
    void s7() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> new OrderLine(1, "자바 책", new Money(15000), 0));
        assertEquals("수량은 1개 이상이어야 합니다", error.getMessage());
        assertEquals(1, new OrderLine(1, "자바 책", new Money(15000), 1).quantity());

        assertTrue(runMain().contains("0개 항목 주문: 수량은 1개 이상이어야 합니다"));
    }

    @Test
    @DisplayName("S8 용어집에 주문 항목, 항목 금액, 주문 항목 더하기를 더한다")
    void s8() throws Exception {
        String glossary = Files.readString(Path.of("glossary.md"));

        assertTrue(glossary.contains("| 주문 항목 | OrderLine |"));
        assertTrue(glossary.contains("| 항목 금액 | amount |"));
        assertTrue(glossary.contains("| 주문 항목 더하기 | addOrderLine |"));
    }
}
