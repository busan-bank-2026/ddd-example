package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LayersTest {

    private void assertMoved(String layer, String name) throws Exception {
        assertEquals("ddd." + layer, Class.forName("ddd." + layer + "." + name).getPackageName());
        assertTrue(Files.exists(Path.of("src/ddd/" + layer.replace('.', '/') + "/" + name + ".java")));
        assertFalse(Files.exists(Path.of("src/ddd/" + name + ".java")));
    }

    private List<String> sources(String layer) throws Exception {
        List<String> sources = new ArrayList<>();
        for (String context : List.of("order", "catalog", "member")) {
            Path folder = Path.of("src/ddd/" + context + "/" + layer);
            if (!Files.isDirectory(folder)) {
                continue;
            }
            try (Stream<Path> files = Files.list(folder)) {
                files.forEach(file -> {
                    try {
                        sources.add(Files.readString(file));
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                });
            }
        }
        return sources;
    }

    @Test
    @DisplayName("S1 주문, 주문 항목, 주소, 주문 상태, 주문 저장소를 도메인 계층 묶음 ddd.order.domain에 둔다")
    void s1() throws Exception {
        for (String name : List.of("Order", "OrderItem", "Address", "OrderStatus", "OrderRepository")) {
            assertMoved("order.domain", name);
        }
        assertTrue(Class.forName("ddd.order.domain.OrderRepository").isInterface());
    }

    @Test
    @DisplayName("S2 주문 응용 서비스, 주문 요청, 주문 항목 요청을 응용 계층 묶음 ddd.order.application에 둔다")
    void s2() throws Exception {
        for (String name : List.of("OrderService", "OrderRequest", "OrderItemRequest")) {
            assertMoved("order.application", name);
        }
        Class<?> service = Class.forName("ddd.order.application.OrderService");
        assertEquals(List.of(Class.forName("ddd.order.domain.OrderRepository"), Class.forName("ddd.order.domain.OrderItemFactory"),
                Class.forName("ddd.member.application.MemberService"), Class.forName("ddd.order.domain.DiscountPolicy"), Class.forName("ddd.order.application.EventPublisher")),
                List.of(service.getConstructors()[0].getParameterTypes()));
    }

    @Test
    @DisplayName("S3 메모리 주문 저장소를 인프라 계층 묶음 ddd.order.infrastructure에 두고, 도메인 계층의 주문 저장소를 구현한다")
    void s3() throws Exception {
        assertMoved("order.infrastructure", "MemoryOrderRepository");
        Class<?> memory = Class.forName("ddd.order.infrastructure.MemoryOrderRepository");
        assertTrue(Class.forName("ddd.order.domain.OrderRepository").isAssignableFrom(memory));
    }

    @Test
    @DisplayName("S4 도메인 계층은 응용 계층과 인프라 계층을 쓰지 않고, 응용 계층은 인프라 계층을 쓰지 않는다. 셋을 잇는 일은 ddd 묶음의 Main만 한다")
    void s4() throws Exception {
        for (String source : sources("domain")) {
            assertTrue(source.matches("(?s)package ddd\\.\\w+\\.domain;.*"));
            assertFalse(source.matches("(?s).*import ddd\\.\\w+\\.application.*"));
            assertFalse(source.matches("(?s).*import ddd\\.\\w+\\.infrastructure.*"));
            assertFalse(source.contains("import ddd.Main"));
        }
        for (String source : sources("application")) {
            assertTrue(source.matches("(?s)package ddd\\.\\w+\\.application;.*"));
            assertFalse(source.matches("(?s).*import ddd\\.\\w+\\.infrastructure.*"));
        }
        for (String source : sources("infrastructure")) {
            assertTrue(source.matches("(?s)package ddd\\.\\w+\\.infrastructure;.*"));
        }

        try (Stream<Path> files = Files.list(Path.of("src/ddd"))) {
            assertEquals(List.of("Main.java", "catalog", "member", "order"),
                    files.map(file -> file.getFileName().toString()).sorted().toList());
        }
        String main = Files.readString(Path.of("src/ddd/Main.java"));
        assertTrue(main.startsWith("package ddd;"));
        assertTrue(main.contains("import ddd.order.application.OrderService;"));
        assertTrue(main.contains("import ddd.order.domain.OrderItemFactory;"));
        assertTrue(main.contains("import ddd.order.infrastructure.MemoryOrderRepository;"));
    }
}
