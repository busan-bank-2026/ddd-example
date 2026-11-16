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
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LayersTest {

    private static final String EXPECTED = String.join(System.lineSeparator(),
            "온라인 주문 프로그램을 시작합니다",
            "주문: 1번, 김철수, 31000원, 주문 접수",
            "주문 항목: 자바 책 2개, 30000원",
            "주문 항목: 볼펜 1개, 1000원",
            "주문 뒤: 이벤트 1개, 자바 책 재고 10개",
            "이벤트 처리 뒤: 자바 책 재고 8개",
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
            "가격 바뀐 뒤: 1번 상품 18000원, 주문 항목 15000원, 주문 금액 31000원",
            "동시 수정: 다른 곳에서 먼저 바꾼 주문입니다") + System.lineSeparator();

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

    private void assertMoved(String layer, String name) throws Exception {
        assertEquals("ddd." + layer, Class.forName("ddd." + layer + "." + name).getPackageName());
        assertTrue(Files.exists(Path.of("src/ddd/" + layer + "/" + name + ".java")));
        assertFalse(Files.exists(Path.of("src/ddd/" + name + ".java")));
    }

    private List<String> sources(String layer) throws Exception {
        try (Stream<Path> files = Files.list(Path.of("src/ddd/" + layer))) {
            return files.map(file -> {
                try {
                    return Files.readString(file);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }).toList();
        }
    }

    @Test
    @DisplayName("S1 주문, 주문 항목, 돈, 주소, 주문 상태, 주문 저장소를 도메인 계층 묶음 ddd.domain에 둔다")
    void s1() throws Exception {
        for (String name : List.of("Order", "OrderLine", "Money", "Address", "OrderStatus", "OrderRepository")) {
            assertMoved("domain", name);
        }
        assertTrue(Class.forName("ddd.domain.OrderRepository").isInterface());
        assertEquals(EXPECTED, runMain());
    }

    @Test
    @DisplayName("S2 주문 응용 서비스, 주문하기 명령, 주문 항목 명령을 응용 계층 묶음 ddd.application에 둔다")
    void s2() throws Exception {
        for (String name : List.of("OrderApplicationService", "PlaceOrderCommand", "OrderLineCommand")) {
            assertMoved("application", name);
        }
        Class<?> service = Class.forName("ddd.application.OrderApplicationService");
        assertEquals(List.of(Class.forName("ddd.domain.OrderRepository"), Class.forName("ddd.domain.OrderLineFactory"),
                Class.forName("ddd.domain.MemberRepository"), Class.forName("ddd.domain.DiscountPolicy"), Class.forName("ddd.application.EventPublisher")),
                List.of(service.getConstructors()[0].getParameterTypes()));
        assertEquals(EXPECTED, runMain());
    }

    @Test
    @DisplayName("S3 메모리 주문 저장소를 인프라 계층 묶음 ddd.infrastructure에 두고, 도메인 계층의 주문 저장소를 구현한다")
    void s3() throws Exception {
        assertMoved("infrastructure", "MemoryOrderRepository");
        Class<?> memory = Class.forName("ddd.infrastructure.MemoryOrderRepository");
        assertTrue(Class.forName("ddd.domain.OrderRepository").isAssignableFrom(memory));
        assertEquals(EXPECTED, runMain());
    }

    @Test
    @DisplayName("S4 도메인 계층은 응용 계층과 인프라 계층을 쓰지 않고, 응용 계층은 인프라 계층을 쓰지 않는다. 셋을 잇는 일은 ddd 묶음의 Main만 한다")
    void s4() throws Exception {
        for (String source : sources("domain")) {
            assertTrue(source.startsWith("package ddd.domain;"));
            assertFalse(source.contains("import ddd.application"));
            assertFalse(source.contains("import ddd.infrastructure"));
            assertFalse(source.contains("import ddd.Main"));
        }
        for (String source : sources("application")) {
            assertTrue(source.startsWith("package ddd.application;"));
            assertFalse(source.contains("import ddd.infrastructure"));
        }
        for (String source : sources("infrastructure")) {
            assertTrue(source.startsWith("package ddd.infrastructure;"));
            assertFalse(source.contains("import ddd.application"));
        }

        try (Stream<Path> files = Files.list(Path.of("src/ddd"))) {
            assertEquals(List.of("Main.java", "application", "domain", "infrastructure"),
                    files.map(file -> file.getFileName().toString()).sorted().toList());
        }
        String main = Files.readString(Path.of("src/ddd/Main.java"));
        assertTrue(main.startsWith("package ddd;"));
        assertTrue(main.contains("import ddd.application.OrderApplicationService;"));
        assertTrue(main.contains("import ddd.domain.Order;"));
        assertTrue(main.contains("import ddd.infrastructure.MemoryOrderRepository;"));
        assertEquals(EXPECTED, runMain());
    }
}
