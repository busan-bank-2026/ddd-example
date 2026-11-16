package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

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
import ddd.domain.OrderLineFactory;
import ddd.domain.Product;
import ddd.domain.ProductRepository;
import ddd.infrastructure.MemoryOrderRepository;
import ddd.infrastructure.MemoryProductRepository;

class RichEntityTest {

    private static OrderLineFactory orderLineFactory() {
        ProductRepository products = new MemoryProductRepository();
        products.save(new Product(1, "자바 책", new Money(15000)));
        products.save(new Product(2, "볼펜", new Money(1000)));
        products.save(new Product(3, "공책", new Money(2000)));
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
    @DisplayName("S1 결제하지 않은 주문의 상태를 서비스를 거치지 않고 배송 중으로 바꾸면 그대로 바뀌는 문제를 실행해 보여준다")
    void s1() {
        String printed = runMain();

        assertTrue(printed.contains("결제 없이 배송: "));
    }

    @Test
    @DisplayName("S2 주문은 만들 때 주문 번호를 받고, 같은 번호면 같은 주문으로 본다")
    void s2() {
        Order first = new Order(1, 1, List.of(new OrderLine(1, "자바 책", new Money(15000), 2)), new Address("서울시 강남구"));
        Order sameNumber = new Order(1, 2, List.of(new OrderLine(4, "연필", new Money(500), 3)), new Address("부산시 해운대구"));
        Order otherNumber = new Order(2, 1, List.of(new OrderLine(1, "자바 책", new Money(15000), 2)), new Address("서울시 강남구"));

        assertEquals(1, first.getId());
        assertEquals(first, sameNumber);
        assertEquals(first.hashCode(), sameNumber.hashCode());
        assertNotEquals(first, otherNumber);

        OrderApplicationService service = new OrderApplicationService(new MemoryOrderRepository(), orderLineFactory());
        long placed1 = service.placeOrder(new PlaceOrderCommand(1, List.of(new OrderLineCommand(1, 2)), "서울시 강남구"));
        long placed2 = service.placeOrder(new PlaceOrderCommand(1, List.of(new OrderLineCommand(1, 2)), "서울시 강남구"));
        assertEquals(1, placed1);
        assertEquals(2, placed2);
        assertEquals(1, service.findOrder(placed1).getId());
        assertNotEquals(service.findOrder(placed1), service.findOrder(placed2));
        assertTrue(runMain().contains("주문: 1번, 김철수, 31000원, 주문 접수"));
    }

    @Test
    @DisplayName("S3 주문을 만들 때 주문 금액과 주문 접수 상태를 주문이 스스로 정한다")
    void s3() {
        Order order = new Order(1, 1, List.of(new OrderLine(1, "자바 책", new Money(15000), 2)), new Address("서울시 강남구"));

        assertEquals(30000, order.getTotalPrice().amount());
        assertEquals("주문 접수", order.getStatus().label());
        assertEquals(1L, order.getMemberId());
        assertEquals("서울시 강남구", order.getAddress().value());
    }

    @Test
    @DisplayName("S4 결제, 배송, 취소 규칙을 주문이 스스로 지킨다. 서비스는 주문에게 시키기만 한다")
    void s4() {
        Order order = new Order(1, 1, List.of(new OrderLine(1, "자바 책", new Money(15000), 2)), new Address("서울시 강남구"));

        order.pay();
        assertEquals("결제 완료", order.getStatus().label());
        order.ship();
        assertEquals("배송 중", order.getStatus().label());
        IllegalStateException cancelError = assertThrows(IllegalStateException.class, order::cancel);
        assertEquals("배송 중인 주문은 취소할 수 없습니다", cancelError.getMessage());
        assertEquals("배송 중", order.getStatus().label());

        Order unpaid = new Order(2, 1, List.of(new OrderLine(1, "자바 책", new Money(15000), 2)), new Address("서울시 강남구"));
        IllegalStateException shipError = assertThrows(IllegalStateException.class, unpaid::ship);
        assertEquals("주문 접수인 주문은 배송할 수 없습니다", shipError.getMessage());
        unpaid.cancel();
        assertEquals("취소", unpaid.getStatus().label());

        OrderApplicationService service = new OrderApplicationService(new MemoryOrderRepository(), orderLineFactory());
        long placed = service.placeOrder(new PlaceOrderCommand(1, List.of(new OrderLineCommand(1, 2)), "서울시 강남구"));
        service.pay(placed);
        service.ship(placed);
        assertEquals("배송 중", service.findOrder(placed).getStatus().label());
        assertThrows(IllegalStateException.class, () -> service.cancel(placed));
    }

    @Test
    @DisplayName("S5 주문 밖에서는 상태를 직접 바꿀 수 없다. 결제 안 한 주문을 배송하라고 하면 주문이 거절한다")
    void s5() {
        boolean hasSetter = Arrays.stream(Order.class.getMethods())
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .map(Method::getName)
                .anyMatch(name -> name.startsWith("set"));

        assertTrue(!hasSetter);
        assertTrue(runMain().contains("결제 없이 배송: 주문 접수인 주문은 배송할 수 없습니다"));
    }

    @Test
    @DisplayName("S6 용어집에 주문 번호를 더한다")
    void s6() throws Exception {
        String glossary = Files.readString(Path.of("glossary.md"));

        assertTrue(glossary.contains("| 주문 번호 | id |"));
    }
}
