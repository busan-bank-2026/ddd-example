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

class BoundedContextsTest {

    private String contextMap() throws Exception {
        return Files.readString(Path.of("context-map.md"));
    }

    private boolean hasLine(String text, String... words) {
        return text.lines().anyMatch(line -> List.of(words).stream().allMatch(line::contains));
    }

    private String section(String text, String heading) {
        int start = text.indexOf("## " + heading);
        assertTrue(start >= 0, heading + " 절이 없습니다");
        int end = text.indexOf("\n## ", start + 1);
        return end < 0 ? text.substring(start) : text.substring(start, end);
    }

    private void assertMoved(String oldPackage, String newPackage, String name) throws Exception {
        assertEquals(newPackage, Class.forName(newPackage + "." + name).getPackageName());
        assertTrue(Files.exists(Path.of("src/" + newPackage.replace('.', '/') + "/" + name + ".java")));
        assertFalse(Files.exists(Path.of("src/" + oldPackage.replace('.', '/') + "/" + name + ".java")));
    }

    private List<String> names(String folder) throws Exception {
        try (Stream<Path> files = Files.list(Path.of(folder))) {
            return files.map(file -> file.getFileName().toString()).sorted().toList();
        }
    }

    private List<String> sources(String context, String layer) throws Exception {
        Path folder = Path.of("src/ddd/" + context + "/" + layer);
        List<String> sources = new ArrayList<>();
        if (!Files.isDirectory(folder)) {
            return sources;
        }
        try (Stream<Path> files = Files.list(folder)) {
            for (Path file : files.toList()) {
                sources.add(Files.readString(file));
            }
        }
        return sources;
    }

    @Test
    @DisplayName("S1 업무를 주문(핵심), 상품(지원), 회원(일반) 하위 도메인으로 나누고, 하위 도메인마다 맡는 바운디드 컨텍스트와 묶음 이름을 학생 문서 컨텍스트 맵에 적는다")
    void s1() throws Exception {
        String subdomains = section(contextMap(), "하위 도메인");
        assertTrue(hasLine(subdomains, "| 주문 |", "핵심", "주문 컨텍스트", "ddd.order"));
        assertTrue(hasLine(subdomains, "| 상품 |", "지원", "상품 컨텍스트", "ddd.catalog"));
        assertTrue(hasLine(subdomains, "| 회원 |", "일반", "회원 컨텍스트", "ddd.member"));
        assertTrue(subdomains.contains("바운디드 컨텍스트"));
    }

    @Test
    @DisplayName("S2 회원, 회원 등급, 회원 저장소, 메모리 회원 저장소를 회원 컨텍스트 묶음 ddd.member 안의 domain, infrastructure에 둔다")
    void s2() throws Exception {
        for (String name : List.of("Member", "MemberGrade", "MemberRepository")) {
            assertMoved("ddd.domain", "ddd.member.domain", name);
        }
        assertMoved("ddd.infrastructure", "ddd.member.infrastructure", "MemoryMemberRepository");
        assertTrue(Class.forName("ddd.member.domain.MemberRepository")
                .isAssignableFrom(Class.forName("ddd.member.infrastructure.MemoryMemberRepository")));
        assertEquals(List.of("application", "domain", "infrastructure"), names("src/ddd/member"));
    }

    @Test
    @DisplayName("S3 상품, 상품 저장소, 재고 줄이기 처리기, 메모리 상품 저장소를 상품 컨텍스트 묶음 ddd.catalog 안의 domain, application, infrastructure에 둔다")
    void s3() throws Exception {
        for (String name : List.of("Product", "ProductRepository")) {
            assertMoved("ddd.domain", "ddd.catalog.domain", name);
        }
        assertMoved("ddd.application", "ddd.catalog.application", "DecreaseStockHandler");
        assertMoved("ddd.infrastructure", "ddd.catalog.infrastructure", "MemoryProductRepository");
        assertTrue(Class.forName("ddd.catalog.domain.ProductRepository")
                .isAssignableFrom(Class.forName("ddd.catalog.infrastructure.MemoryProductRepository")));
        assertEquals(List.of("application", "domain", "infrastructure"), names("src/ddd/catalog"));
    }

    @Test
    @DisplayName("S4 주문 쪽 클래스는 layers 장부터 주문 컨텍스트 묶음 ddd.order 안의 domain, application, infrastructure에 있다. 회원과 상품을 옮기면 src/ddd에는 Main과 세 묶음만 남는다")
    void s4() throws Exception {
        for (String name : List.of("Address", "DiscountPolicy", "FreeShippingSpecification", "Order", "OrderItem",
                "OrderItemFactory", "OrderPlaced", "OrderRepository", "OrderStatus")) {
            assertMoved("ddd.domain", "ddd.order.domain", name);
        }
        for (String name : List.of("EventPublisher", "OrderService", "OrderItemRequest", "OrderRequest")) {
            assertMoved("ddd.application", "ddd.order.application", name);
        }
        assertMoved("ddd.infrastructure", "ddd.order.infrastructure", "MemoryOrderRepository");
        assertTrue(Class.forName("ddd.order.domain.OrderRepository")
                .isAssignableFrom(Class.forName("ddd.order.infrastructure.MemoryOrderRepository")));
        assertEquals(List.of("application", "domain", "infrastructure"), names("src/ddd/order"));
        assertEquals(List.of("Main.java", "catalog", "member", "order"), names("src/ddd"));
        assertFalse(Files.exists(Path.of("src/ddd/shared")));
    }

    @Test
    @DisplayName("S5 컨텍스트 안에서도 도메인은 응용과 인프라를 쓰지 않는다. 컨텍스트 사이에 가져다 쓰는 곳(주문이 상품과 회원을 읽고, 상품이 주문 접수됨을 받는다)을 컨텍스트 맵에 적는다")
    void s5() throws Exception {
        int domainFiles = 0;
        for (String context : List.of("order", "catalog", "member")) {
            for (String source : sources(context, "domain")) {
                domainFiles++;
                assertTrue(source.startsWith("package ddd." + context + ".domain;"));
                assertFalse(source.matches("(?s).*import ddd\\.\\w+\\.application\\..*"));
                assertFalse(source.matches("(?s).*import ddd\\.\\w+\\.infrastructure\\..*"));
            }
            for (String source : sources(context, "application")) {
                assertTrue(source.startsWith("package ddd." + context + ".application;"));
                assertFalse(source.matches("(?s).*import ddd\\.\\w+\\.infrastructure\\..*"));
            }
        }
        assertEquals(14, domainFiles);
        String orderItemFactory = Files.readString(Path.of("src/ddd/order/domain/OrderItemFactory.java"));
        assertTrue(orderItemFactory.contains("import ddd.catalog.domain.ProductRepository;"));
        String discountPolicy = Files.readString(Path.of("src/ddd/order/domain/DiscountPolicy.java"));
        assertTrue(discountPolicy.contains("import ddd.member.domain.Member;"));
        String handler = Files.readString(Path.of("src/ddd/catalog/application/DecreaseStockHandler.java"));
        assertTrue(handler.contains("import ddd.order.domain.OrderPlaced;"));

        String between = section(contextMap(), "컨텍스트 사이");
        assertTrue(hasLine(between, "| 주문 |", "| 상품 |", "주문 항목 팩토리"));
        assertTrue(hasLine(between, "| 주문 |", "| 회원 |", "할인 정책"));
        assertTrue(hasLine(between, "| 상품 |", "| 주문 |", "주문 접수됨"));
    }
}

