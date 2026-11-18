package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ddd.order.domain.Address;
import ddd.order.domain.FreeShippingSpecification;
import ddd.order.domain.Order;
import ddd.order.domain.OrderItem;
import ddd.order.domain.OrderStatus;

class FreeShippingTest {

    private static Order newOrder(int price, String address) {
        return new Order(1, 2, List.of(new OrderItem(1, "자바 책", price, 1)), new Address(address));
    }

    @Test
    @DisplayName("S1 무료 배송 명세는 주문을 받아 무료 배송인지 참 거짓으로만 답한다. 결제 금액이 50000원 이상이면 참, 아니면 거짓이다. 명세는 도메인 계층에 두고, 칸이 없고, 주문을 바꾸지 않는다")
    void s1() throws Exception {
        FreeShippingSpecification spec = new FreeShippingSpecification();

        assertTrue(spec.isSatisfiedBy(newOrder(60000, "서울시 마포구")));
        assertTrue(spec.isSatisfiedBy(newOrder(50000, "서울시 마포구")));
        assertFalse(spec.isSatisfiedBy(newOrder(30000, "서울시 마포구")));

        Order discounted = newOrder(52000, "서울시 마포구");
        assertTrue(spec.isSatisfiedBy(discounted));
        discounted.pay(5200);
        assertFalse(spec.isSatisfiedBy(discounted));
        assertEquals(OrderStatus.PAID, discounted.getStatus());
        assertEquals(5200, discounted.getDiscountPrice());
        assertEquals(46800, discounted.getPaymentPrice());

        assertEquals(boolean.class, FreeShippingSpecification.class.getMethod("isSatisfiedBy", Order.class).getReturnType());
        assertEquals(0, FreeShippingSpecification.class.getDeclaredFields().length);
        assertEquals("ddd.order.domain", FreeShippingSpecification.class.getPackageName());
    }

    @Test
    @DisplayName("S2 주문은 배송비를 가진다. 무료 배송 명세를 만족하면 0원, 아니면 3000원이다. Main이 30000원 주문과 60000원 주문의 배송비를 보여준다")
    void s2() {
        assertEquals(3000, newOrder(30000, "서울시 마포구").getShippingFee());
        assertEquals(0, newOrder(60000, "서울시 마포구").getShippingFee());

        Order discounted = newOrder(52000, "서울시 마포구");
        assertEquals(0, discounted.getShippingFee());
        discounted.pay(5200);
        assertEquals(3000, discounted.getShippingFee());
        assertEquals(52000, discounted.getTotalPrice());
    }

    @Test
    @DisplayName("S3 배송 주소가 제주인 주문은 결제 금액이 50000원 이상이어도 무료 배송이 아니다. 무료 배송 명세 한 클래스에서 금액과 지역을 함께 판단한다. Main이 6만원 주문의 서울과 제주 배송비를 보여준다")
    void s3() {
        Order jeju = newOrder(60000, "제주시 연동");
        Order seoul = newOrder(60000, "서울시 마포구");
        Order cheapJeju = newOrder(30000, "제주시 연동");

        assertFalse(new FreeShippingSpecification().isSatisfiedBy(jeju));
        assertEquals(3000, jeju.getShippingFee());
        assertEquals(0, seoul.getShippingFee());

        assertFalse(new FreeShippingSpecification().isSatisfiedBy(cheapJeju));
        assertFalse(new FreeShippingSpecification().isSatisfiedBy(newOrder(30000, "서울시 마포구")));

    }

    @Test
    @DisplayName("S4 용어집에 무료 배송 명세와 배송비를 더한다")
    void s4() throws Exception {
        String glossary = Files.readString(Path.of("glossary.md"));

        assertTrue(glossary.contains("| 무료 배송 명세 | FreeShippingSpecification |"));
        assertTrue(glossary.contains("| 배송비 | shippingFee |"));
    }
}
