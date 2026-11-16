package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AnemicOrderTest {

    private final OrderService service = new OrderService();

    private Order newOrder() {
        return service.createOrder("김철수", "자바 책", 15000, 2, "서울시 강남구");
    }

    @Test
    @DisplayName("S1 주문자 이름, 상품 이름, 가격, 수량, 배송 주소로 주문을 만들면 주문 금액은 가격 곱하기 수량이고 상태는 주문 접수다")
    void s1() {
        Order order = newOrder();

        assertEquals("김철수", order.getCustomerName());
        assertEquals("자바 책", order.getProductName());
        assertEquals(15000, order.getPrice());
        assertEquals(2, order.getQuantity());
        assertEquals("서울시 강남구", order.getAddress());
        assertEquals(30000, order.getTotalPrice());
        assertEquals("주문 접수", order.getStatus());
    }

    @Test
    @DisplayName("S2 주문 접수인 주문을 결제하면 결제 완료가 된다")
    void s2() {
        Order order = newOrder();

        service.updatePayment(order);

        assertEquals("결제 완료", order.getStatus());
    }

    @Test
    @DisplayName("S3 결제 완료인 주문을 배송하면 배송 중이 된다")
    void s3() {
        Order order = newOrder();
        service.updatePayment(order);

        service.updateDelivery(order);

        assertEquals("배송 중", order.getStatus());
    }

    @Test
    @DisplayName("S4 결제하지 않은 주문을 배송하면 거절한다")
    void s4() {
        Order order = newOrder();

        assertThrows(IllegalStateException.class, () -> service.updateDelivery(order));
        assertEquals("주문 접수", order.getStatus());
    }

    @Test
    @DisplayName("S5 배송 전인 주문을 취소하면 취소가 된다")
    void s5() {
        Order order = newOrder();
        service.updatePayment(order);

        service.deleteOrder(order);

        assertEquals("취소", order.getStatus());
    }

    @Test
    @DisplayName("S6 배송 중인 주문을 취소하면 거절한다")
    void s6() {
        Order order = newOrder();
        service.updatePayment(order);
        service.updateDelivery(order);

        assertThrows(IllegalStateException.class, () -> service.deleteOrder(order));
        assertEquals("배송 중", order.getStatus());
    }

    @Test
    @DisplayName("S7 프로그램을 실행하면 주문부터 취소 시도까지 장면을 콘솔에 보여준다")
    void s7() {
        PrintStream original = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
        try {
            Main.main(new String[0]);
        } finally {
            System.setOut(original);
        }
        String printed = out.toString(StandardCharsets.UTF_8);

        assertTrue(printed.contains("주문: 김철수, 자바 책 2개, 30000원, 주문 접수"));
        assertTrue(printed.contains("결제: 결제 완료"));
        assertTrue(printed.contains("배송: 배송 중"));
        assertTrue(printed.contains("취소: 배송 중인 주문은 취소할 수 없습니다"));
    }
}
