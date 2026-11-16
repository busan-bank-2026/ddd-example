package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UbiquitousLanguageTest {

    private final OrderService service = new OrderService();

    @Test
    @DisplayName("S1 주문을 만드는 일을 업무 말인 주문하기로 부른다")
    void s1() {
        Order order = service.placeOrder("김철수", List.of(new OrderLine("자바 책", new Money(15000), 2)), "서울시 강남구");

        assertEquals("주문 접수", order.getStatus());
        assertEquals(30000, order.getTotalPrice().amount());
    }

    @Test
    @DisplayName("S2 결제 상태를 바꾸는 일을 결제하기로 부른다")
    void s2() {
        Order order = service.placeOrder("김철수", List.of(new OrderLine("자바 책", new Money(15000), 2)), "서울시 강남구");

        service.pay(order);

        assertEquals("결제 완료", order.getStatus());
    }

    @Test
    @DisplayName("S3 배송 상태를 바꾸는 일을 배송하기로 부른다")
    void s3() {
        Order order = service.placeOrder("김철수", List.of(new OrderLine("자바 책", new Money(15000), 2)), "서울시 강남구");
        service.pay(order);

        service.ship(order);

        assertEquals("배송 중", order.getStatus());
    }

    @Test
    @DisplayName("S4 주문을 지우는 일을 주문 취소하기로 부른다. 주문은 지워지지 않고 취소 상태가 된다")
    void s4() {
        Order order = service.placeOrder("김철수", List.of(new OrderLine("자바 책", new Money(15000), 2)), "서울시 강남구");

        service.cancel(order);

        assertEquals("취소", order.getStatus());
        assertEquals("김철수", order.getCustomerName());
    }

    @Test
    @DisplayName("S5 업무 말과 코드 이름을 나란히 적은 용어집을 학생 프로젝트에 둔다")
    void s5() throws Exception {
        String glossary = Files.readString(Path.of("glossary.md"));

        assertTrue(glossary.contains("| 주문하기 | placeOrder |"));
        assertTrue(glossary.contains("| 결제하기 | pay |"));
        assertTrue(glossary.contains("| 배송하기 | ship |"));
        assertTrue(glossary.contains("| 주문 취소하기 | cancel |"));
        assertTrue(glossary.contains("| 주문 금액 | totalPrice |"));
    }
}
