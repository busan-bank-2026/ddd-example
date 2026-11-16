package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ddd.domain.Address;
import ddd.domain.FreeShippingAreaSpecification;
import ddd.domain.FreeShippingPriceSpecification;
import ddd.domain.FreeShippingSpecification;
import ddd.domain.Money;
import ddd.domain.Order;
import ddd.domain.OrderLine;
import ddd.domain.OrderSpecification;
import ddd.domain.OrderStatus;

class FreeShippingTest {

    private static final String EXPECTED = String.join(System.lineSeparator(),
            "온라인 주문 프로그램을 시작합니다",
            "주문: 1번, 김철수, 31000원, 주문 접수",
            "주문 항목: 자바 책 2개, 30000원",
            "주문 항목: 볼펜 1개, 1000원",
            "결제: 결제 완료",
            "할인: VIP 3100원, 결제 금액 27900원",
            "배송비: 3000원",
            "배송: 배송 중",
            "취소: 배송 중인 주문은 취소할 수 없습니다",
            "결제 없이 배송: 주문 접수인 주문은 배송할 수 없습니다",
            "음수 금액 주문: 금액은 0원 이상이어야 합니다",
            "빈 주소 주문: 배송 주소를 입력해야 합니다",
            "항목 더하기: 항목 3개, 주문 금액 41000원",
            "결제 뒤 항목 더하기: 주문 접수인 주문에만 항목을 더할 수 있습니다",
            "빈 주문: 주문 항목이 하나 이상 있어야 합니다",
            "0개 항목 주문: 수량은 1개 이상이어야 합니다",
            "두 번 결제: 결제 완료인 주문은 결제할 수 없습니다",
            "배송 완료: 배송 완료",
            "없는 주문 결제: 주문이 없습니다",
            "없는 상품 주문: 상품이 없습니다",
            "저장 안 한 취소: 주문 접수",
            "6만원 주문 배송비: 서울 0원, 제주 3000원",
            "가격 바뀐 뒤: 1번 상품 18000원, 주문 항목 15000원, 주문 금액 31000원") + System.lineSeparator();

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

    private static Order newOrder(int price, String address) {
        return new Order(1, 2, List.of(new OrderLine(1, "자바 책", new Money(price), 1)), new Address(address));
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
        discounted.pay(new Money(5200));
        assertFalse(spec.isSatisfiedBy(discounted));
        assertEquals(OrderStatus.PAID, discounted.getStatus());
        assertEquals(new Money(5200), discounted.getDiscountPrice());
        assertEquals(new Money(46800), discounted.getPaymentPrice());

        assertEquals(boolean.class, FreeShippingSpecification.class.getMethod("isSatisfiedBy", Order.class).getReturnType());
        assertEquals(0, FreeShippingSpecification.class.getDeclaredFields().length);
        assertEquals("ddd.domain", FreeShippingSpecification.class.getPackageName());
    }

    @Test
    @DisplayName("S2 주문은 배송비를 가진다. 무료 배송 명세를 만족하면 0원, 아니면 3000원이다. Main이 첫 주문의 할인 줄 다음에 배송비를 보여준다")
    void s2() {
        assertEquals(new Money(3000), newOrder(30000, "서울시 마포구").getShippingFee());
        assertEquals(new Money(0), newOrder(60000, "서울시 마포구").getShippingFee());

        Order discounted = newOrder(52000, "서울시 마포구");
        assertEquals(new Money(0), discounted.getShippingFee());
        discounted.pay(new Money(5200));
        assertEquals(new Money(3000), discounted.getShippingFee());
        assertEquals(new Money(52000), discounted.getTotalPrice());

        List<String> lines = List.of(runMain().split(System.lineSeparator()));
        int discountLine = lines.indexOf("할인: VIP 3100원, 결제 금액 27900원");
        assertTrue(discountLine > 0);
        assertEquals("배송비: 3000원", lines.get(discountLine + 1));
    }

    @Test
    @DisplayName("S3 배송 주소가 제주인 주문은 결제 금액이 50000원 이상이어도 무료 배송이 아니다. 무료 배송 명세는 무료 배송 금액 명세와 무료 배송 지역 명세를 and로 묶어 만든다. Main이 6만원 주문의 서울과 제주 배송비를 보여준다")
    void s3() {
        Order jeju = newOrder(60000, "제주시 연동");
        Order seoul = newOrder(60000, "서울시 마포구");
        Order cheapJeju = newOrder(30000, "제주시 연동");

        assertFalse(new FreeShippingSpecification().isSatisfiedBy(jeju));
        assertEquals(new Money(3000), jeju.getShippingFee());
        assertEquals(new Money(0), seoul.getShippingFee());

        OrderSpecification price = new FreeShippingPriceSpecification();
        OrderSpecification area = new FreeShippingAreaSpecification();
        assertTrue(price.isSatisfiedBy(jeju));
        assertFalse(price.isSatisfiedBy(cheapJeju));
        assertFalse(area.isSatisfiedBy(jeju));
        assertTrue(area.isSatisfiedBy(seoul));

        assertTrue(OrderSpecification.class.isInterface());
        assertTrue(OrderSpecification.class.isAssignableFrom(FreeShippingSpecification.class));
        OrderSpecification both = price.and(area);
        for (Order order : List.of(jeju, seoul, cheapJeju, newOrder(30000, "서울시 마포구"))) {
            assertEquals(new FreeShippingSpecification().isSatisfiedBy(order), both.isSatisfiedBy(order));
        }
        assertTrue(both.isSatisfiedBy(seoul));
        assertFalse(both.isSatisfiedBy(jeju));

        assertEquals(EXPECTED, runMain());
    }

    @Test
    @DisplayName("S4 용어집에 주문 명세, 무료 배송 명세, 무료 배송 금액 명세, 무료 배송 지역 명세, 배송비를 더한다")
    void s4() throws Exception {
        String glossary = Files.readString(Path.of("glossary.md"));

        assertTrue(glossary.contains("| 주문 명세 | OrderSpecification |"));
        assertTrue(glossary.contains("| 무료 배송 명세 | FreeShippingSpecification |"));
        assertTrue(glossary.contains("| 무료 배송 금액 명세 | FreeShippingPriceSpecification |"));
        assertTrue(glossary.contains("| 무료 배송 지역 명세 | FreeShippingAreaSpecification |"));
        assertTrue(glossary.contains("| 배송비 | shippingFee |"));
    }
}
