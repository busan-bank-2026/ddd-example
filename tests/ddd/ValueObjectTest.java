package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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

class ValueObjectTest {

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
    @DisplayName("S1 가격을 음수로 주문하면 음수 금액 주문이 그대로 만들어지는 문제를 실행해 보여준다")
    void s1() {
        String printed = runMain();

        assertTrue(printed.contains("음수 금액 주문: "));
    }

    @Test
    @DisplayName("S2 돈은 0원 이상이다. 음수 금액으로 돈을 만들면 거절한다")
    void s2() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> new Money(-1));

        assertEquals("금액은 0원 이상이어야 합니다", error.getMessage());
        assertEquals(0, new Money(0).amount());
    }

    @Test
    @DisplayName("S3 돈에 수량을 곱하거나 돈을 더하면 새 돈을 돌려주고 원래 돈은 그대로다. 금액이 같으면 같은 돈이다")
    void s3() {
        Money price = new Money(15000);

        Money total = price.times(2);
        Money sum = price.plus(new Money(500));

        assertEquals(30000, total.amount());
        assertEquals(15500, sum.amount());
        assertEquals(15000, price.amount());
        assertEquals(new Money(15000), price);
        assertEquals(new Money(15000).hashCode(), price.hashCode());
        assertNotEquals(new Money(14000), price);
    }

    @Test
    @DisplayName("S4 주문의 가격과 주문 금액은 돈이다. 음수 가격으로 주문하면 주문이 만들어지지 않고 거절 메시지를 보여준다")
    void s4() {
        OrderService service = new OrderService(new MemoryOrderRepository());
        Order order = service.findOrder(service.placeOrder("김철수", List.of(new OrderLine("자바 책", new Money(15000), 2)), "서울시 강남구"));

        assertEquals(new Money(15000), order.getOrderLines().get(0).price());
        assertEquals(new Money(30000), order.getTotalPrice());
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder("박민수", List.of(new OrderLine("자바 책", new Money(-15000), 2)), "대구시 수성구"));
        assertEquals("금액은 0원 이상이어야 합니다", error.getMessage());

        String printed = runMain();
        assertTrue(printed.contains("주문: 1번, 김철수, 31000원, 주문 접수"));
        assertTrue(printed.contains("음수 금액 주문: 금액은 0원 이상이어야 합니다"));
    }

    @Test
    @DisplayName("S5 배송 주소는 비어 있을 수 없다. 글자가 같으면 같은 주소다. 빈 주소로 주문하면 거절 메시지를 보여준다")
    void s5() {
        IllegalArgumentException blank = assertThrows(IllegalArgumentException.class, () -> new Address(" "));
        assertEquals("배송 주소를 입력해야 합니다", blank.getMessage());
        assertThrows(IllegalArgumentException.class, () -> new Address(null));
        assertEquals(new Address("서울시 강남구"), new Address("서울시 강남구"));
        assertNotEquals(new Address("부산시 해운대구"), new Address("서울시 강남구"));

        OrderService service = new OrderService(new MemoryOrderRepository());
        Order order = service.findOrder(service.placeOrder("김철수", List.of(new OrderLine("자바 책", new Money(15000), 2)), "서울시 강남구"));
        assertEquals(new Address("서울시 강남구"), order.getAddress());
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder("최지우", List.of(new OrderLine("자바 책", new Money(15000), 1)), ""));
        assertEquals("배송 주소를 입력해야 합니다", error.getMessage());

        assertTrue(runMain().contains("빈 주소 주문: 배송 주소를 입력해야 합니다"));
    }

    @Test
    @DisplayName("S6 용어집에 돈과 주소를 더한다")
    void s6() throws Exception {
        String glossary = Files.readString(Path.of("glossary.md"));

        assertTrue(glossary.contains("| 돈 | Money |"));
        assertTrue(glossary.contains("| 주소 | Address |"));
    }
}
