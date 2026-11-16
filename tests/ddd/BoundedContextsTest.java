package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BoundedContextsTest {

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
            "동시 수정: 다른 곳에서 먼저 바꾼 주문입니다",
            "결제 완료 메시지: ORDER_PAID|orderId=1|address=서울시 강남구",
            "결제 완료 메시지: ORDER_PAID|orderId=4|address=광주시 북구",
            "배송 컨텍스트: 1번 주문, 서울시 강남구") + System.lineSeparator();

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
        assertEquals(EXPECTED, runMain());
    }

    @Test
    @DisplayName("S2 돈을 공유 커널 묶음 ddd.shared에 둔다. 컨텍스트들이 함께 쓰는 것은 돈 하나이고, 컨텍스트 맵에 공유 커널로 적는다")
    void s2() throws Exception {
        assertMoved("ddd.domain", "ddd.shared", "Money");
        assertEquals(List.of("Money.java"), names("src/ddd/shared"));
        String sharedKernel = section(contextMap(), "공유 커널");
        assertTrue(sharedKernel.contains("Money"));
        assertTrue(sharedKernel.contains("ddd.shared"));
        assertEquals(EXPECTED, runMain());
    }

    @Test
    @DisplayName("S3 회원, 회원 등급, 회원 저장소, 메모리 회원 저장소를 회원 컨텍스트 묶음 ddd.member 안의 domain, infrastructure에 둔다")
    void s3() throws Exception {
        for (String name : List.of("Member", "MemberGrade", "MemberRepository")) {
            assertMoved("ddd.domain", "ddd.member.domain", name);
        }
        assertMoved("ddd.infrastructure", "ddd.member.infrastructure", "MemoryMemberRepository");
        assertTrue(Class.forName("ddd.member.domain.MemberRepository")
                .isAssignableFrom(Class.forName("ddd.member.infrastructure.MemoryMemberRepository")));
        assertEquals(List.of("domain", "infrastructure"), names("src/ddd/member"));
        assertEquals(EXPECTED, runMain());
    }

    @Test
    @DisplayName("S4 상품, 상품 저장소, 재고 줄이기 처리기, 메모리 상품 저장소를 상품 컨텍스트 묶음 ddd.catalog 안의 domain, application, infrastructure에 둔다")
    void s4() throws Exception {
        for (String name : List.of("Product", "ProductRepository")) {
            assertMoved("ddd.domain", "ddd.catalog.domain", name);
        }
        assertMoved("ddd.application", "ddd.catalog.application", "DecreaseStockHandler");
        assertMoved("ddd.infrastructure", "ddd.catalog.infrastructure", "MemoryProductRepository");
        assertTrue(Class.forName("ddd.catalog.domain.ProductRepository")
                .isAssignableFrom(Class.forName("ddd.catalog.infrastructure.MemoryProductRepository")));
        assertEquals(List.of("application", "domain", "infrastructure"), names("src/ddd/catalog"));
        assertEquals(EXPECTED, runMain());
    }

    @Test
    @DisplayName("S5 남은 주문 쪽 클래스를 주문 컨텍스트 묶음 ddd.order 안의 domain, application, infrastructure에 둔다. src/ddd에는 Main과 네 묶음만 남는다")
    void s5() throws Exception {
        for (String name : List.of("Address", "DiscountPolicy", "FreeShippingAreaSpecification",
                "FreeShippingPriceSpecification", "FreeShippingSpecification", "Order", "OrderLine",
                "OrderLineFactory", "OrderPlaced", "OrderRepository", "OrderSpecification", "OrderStatus")) {
            assertMoved("ddd.domain", "ddd.order.domain", name);
        }
        for (String name : List.of("EventPublisher", "OrderApplicationService", "OrderLineCommand", "PlaceOrderCommand")) {
            assertMoved("ddd.application", "ddd.order.application", name);
        }
        assertMoved("ddd.infrastructure", "ddd.order.infrastructure", "MemoryOrderRepository");
        assertTrue(Class.forName("ddd.order.domain.OrderRepository")
                .isAssignableFrom(Class.forName("ddd.order.infrastructure.MemoryOrderRepository")));
        assertEquals(List.of("application", "domain", "infrastructure"), names("src/ddd/order"));
        assertEquals(List.of("Main.java", "catalog", "member", "order", "shared", "shipping"), names("src/ddd"));
        assertEquals(EXPECTED, runMain());
    }

    @Test
    @DisplayName("S6 컨텍스트 안에서도 도메인은 응용과 인프라를 쓰지 않는다. 컨텍스트 사이에 가져다 쓰는 곳(주문이 상품과 회원을 읽고, 상품이 주문 접수됨을 받는다)을 컨텍스트 맵에 적는다")
    void s6() throws Exception {
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
        assertEquals(17, domainFiles);
        String orderLineFactory = Files.readString(Path.of("src/ddd/order/domain/OrderLineFactory.java"));
        assertTrue(orderLineFactory.contains("import ddd.catalog.domain.ProductRepository;"));
        String discountPolicy = Files.readString(Path.of("src/ddd/order/domain/DiscountPolicy.java"));
        assertTrue(discountPolicy.contains("import ddd.member.domain.Member;"));
        String handler = Files.readString(Path.of("src/ddd/catalog/application/DecreaseStockHandler.java"));
        assertTrue(handler.contains("import ddd.order.domain.OrderPlaced;"));

        String between = section(contextMap(), "컨텍스트 사이");
        assertTrue(hasLine(between, "| 주문 |", "| 상품 |", "주문 항목 팩토리"));
        assertTrue(hasLine(between, "| 주문 |", "| 회원 |", "할인 정책"));
        assertTrue(hasLine(between, "| 상품 |", "| 주문 |", "주문 접수됨"));
        assertEquals(EXPECTED, runMain());
    }
}
