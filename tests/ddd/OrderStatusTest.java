package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

class OrderStatusTest {

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

    private long newOrder() {
        return service.placeOrder(new PlaceOrderCommand("김철수", List.of(new OrderLineCommand("자바 책", 15000, 2)), "서울시 강남구"));
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

        String printed = runMain();
        assertTrue(printed.contains("주문: 1번, 김철수, 31000원, 주문 접수"));
        assertTrue(printed.contains("결제: 결제 완료"));
        assertTrue(printed.contains("배송: 배송 중"));
    }

    @Test
    @DisplayName("S2 상태마다 바뀔 수 있는 다음 상태가 정해져 있다. 주문 접수는 결제 완료나 취소로, 결제 완료는 배송 중이나 취소로만 바뀐다. 취소는 다른 상태로 바뀌지 않는다")
    void s2() {
        assertTrue(OrderStatus.ORDERED.canChangeTo(OrderStatus.PAID));
        assertTrue(OrderStatus.ORDERED.canChangeTo(OrderStatus.CANCELED));
        assertFalse(OrderStatus.ORDERED.canChangeTo(OrderStatus.SHIPPING));
        assertFalse(OrderStatus.ORDERED.canChangeTo(OrderStatus.ORDERED));

        assertTrue(OrderStatus.PAID.canChangeTo(OrderStatus.SHIPPING));
        assertTrue(OrderStatus.PAID.canChangeTo(OrderStatus.CANCELED));
        assertFalse(OrderStatus.PAID.canChangeTo(OrderStatus.PAID));
        assertFalse(OrderStatus.PAID.canChangeTo(OrderStatus.ORDERED));

        assertFalse(OrderStatus.SHIPPING.canChangeTo(OrderStatus.CANCELED));

        for (OrderStatus next : OrderStatus.values()) {
            assertFalse(OrderStatus.CANCELED.canChangeTo(next));
        }

        long canceledId = newOrder();
        service.cancel(canceledId);
        assertThrows(IllegalStateException.class, () -> service.pay(canceledId));
        assertEquals(OrderStatus.CANCELED, service.findOrder(canceledId).getStatus());
    }

    @Test
    @DisplayName("S3 바꿀 수 없는 상태로 바꾸라고 하면 지금 상태를 넣은 말로 거절한다")
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

        assertTrue(runMain().contains("결제 없이 배송: 주문 접수인 주문은 배송할 수 없습니다"));
    }

    @Test
    @DisplayName("S4 결제 완료인 주문을 다시 결제하면 거절한다")
    void s4() {
        long orderId = newOrder();
        service.pay(orderId);

        IllegalStateException error = assertThrows(IllegalStateException.class, () -> service.pay(orderId));

        assertEquals("결제 완료인 주문은 결제할 수 없습니다", error.getMessage());
        assertEquals(OrderStatus.PAID, service.findOrder(orderId).getStatus());
        assertTrue(runMain().contains("두 번 결제: 결제 완료인 주문은 결제할 수 없습니다"));
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
        assertTrue(OrderStatus.SHIPPING.canChangeTo(OrderStatus.DELIVERED));
        for (OrderStatus next : OrderStatus.values()) {
            assertFalse(OrderStatus.DELIVERED.canChangeTo(next));
        }
        IllegalStateException cancelError = assertThrows(IllegalStateException.class, () -> service.cancel(orderId));
        assertEquals("배송 완료인 주문은 취소할 수 없습니다", cancelError.getMessage());

        long unpaidId = newOrder();
        IllegalStateException deliverError = assertThrows(IllegalStateException.class, () -> service.deliver(unpaidId));
        assertEquals("주문 접수인 주문은 배송 완료할 수 없습니다", deliverError.getMessage());

        assertTrue(runMain().contains("배송 완료: 배송 완료"));
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
