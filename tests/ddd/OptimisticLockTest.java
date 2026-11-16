package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import ddd.catalog.domain.Product;
import ddd.catalog.domain.ProductRepository;
import ddd.catalog.infrastructure.MemoryProductRepository;
import ddd.member.domain.Member;
import ddd.member.domain.MemberGrade;
import ddd.member.domain.MemberRepository;
import ddd.member.infrastructure.MemoryMemberRepository;
import ddd.order.application.EventPublisher;
import ddd.order.application.OrderApplicationService;
import ddd.order.application.OrderLineCommand;
import ddd.order.application.PlaceOrderCommand;
import ddd.order.domain.Address;
import ddd.order.domain.DiscountPolicy;
import ddd.order.domain.Order;
import ddd.order.domain.OrderLine;
import ddd.order.domain.OrderLineFactory;
import ddd.order.domain.OrderRepository;
import ddd.order.domain.OrderStatus;
import ddd.order.infrastructure.LegacyPaymentAdapter;
import ddd.order.infrastructure.MemoryOrderRepository;
import ddd.shared.Money;
import legacypay.LegacyPayGateway;

class OptimisticLockTest {

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
            "배송 컨텍스트: 1번 주문, 서울시 강남구",
            "외부 결제 응답: 27900원 0000, 108000원 E101",
            "한도 넘은 결제: 결제 한도를 넘었습니다, 주문 접수") + System.lineSeparator();

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

    private static Order newOrder() {
        return new Order(1, 1, List.of(new OrderLine(1, "자바 책", new Money(15000), 2)), new Address("서울시 강남구"));
    }

    private static ProductRepository products() {
        ProductRepository products = new MemoryProductRepository();
        products.save(new Product(1, "자바 책", new Money(15000), 10));
        return products;
    }

    private static MemberRepository members() {
        MemberRepository members = new MemoryMemberRepository();
        members.save(new Member(1, "김철수", MemberGrade.BASIC));
        return members;
    }

    @Test
    @DisplayName("S1 결제 완료인 같은 주문을 두 번 꺼내 하나로 배송하고 저장한 뒤, 옛 상태인 다른 하나로 취소하고 저장하면 배송 중인 주문이 취소로 덮이는 문제를 실행해 보여준다")
    void s1() {
        List<String> lines = List.of(runMain().split(System.lineSeparator()));
        int priceLine = lines.indexOf("가격 바뀐 뒤: 1번 상품 18000원, 주문 항목 15000원, 주문 금액 31000원");
        assertTrue(priceLine > 0);
        assertTrue(lines.size() > priceLine + 1);
        assertTrue(lines.get(priceLine + 1).startsWith("동시 수정: "));
    }

    @Test
    @DisplayName("S2 주문은 버전을 가진다. 새 주문은 버전 0이고, 주문 저장소에 저장할 때마다 버전이 1씩 오른다. 꺼낸 주문은 저장된 버전을 가진다")
    void s2() {
        Order order = newOrder();
        assertEquals(0, order.getVersion());

        OrderRepository orders = new MemoryOrderRepository();
        orders.save(order);
        assertEquals(1, order.getVersion());
        Order found = orders.findById(1).orElseThrow();
        assertEquals(1, found.getVersion());
        orders.save(found);
        assertEquals(2, orders.findById(1).orElseThrow().getVersion());

        OrderRepository serviceOrders = new MemoryOrderRepository();
        OrderApplicationService service = new OrderApplicationService(serviceOrders, new OrderLineFactory(products()),
                members(), new DiscountPolicy(), new EventPublisher(), new LegacyPaymentAdapter(new LegacyPayGateway()));
        long orderId = service.placeOrder(new PlaceOrderCommand(1, List.of(new OrderLineCommand(1, 2)), "서울시 강남구"));
        assertEquals(1, serviceOrders.findById(orderId).orElseThrow().getVersion());
        service.pay(orderId);
        assertEquals(2, serviceOrders.findById(orderId).orElseThrow().getVersion());
    }

    @Test
    @DisplayName("S3 주문 저장소는 저장된 주문과 저장하려는 주문의 버전이 다르면 다른 곳에서 먼저 바꾼 주문이라고 저장을 거절한다. 거절되면 저장된 주문은 그대로다. Main의 동시 수정 장면은 거절 메시지를 보여준다")
    void s3() {
        OrderRepository orders = new MemoryOrderRepository();
        Order order = newOrder();
        order.pay(new Money(0));
        orders.save(order);

        Order orderA = orders.findById(1).orElseThrow();
        Order orderB = orders.findById(1).orElseThrow();
        orderA.ship();
        orders.save(orderA);
        orderB.cancel();
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> orders.save(orderB));
        assertEquals("다른 곳에서 먼저 바꾼 주문입니다", e.getMessage());
        assertEquals(OrderStatus.SHIPPING, orders.findById(1).orElseThrow().getStatus());

        assertThrows(IllegalStateException.class, () -> orders.save(newOrder()));
        assertEquals(OrderStatus.SHIPPING, orders.findById(1).orElseThrow().getStatus());

        assertEquals(EXPECTED, runMain());
    }

    @Test
    @DisplayName("S4 용어집에 버전을 더하고 저장하기 줄에 버전 확인을 넣는다")
    void s4() throws Exception {
        String glossary = Files.readString(Path.of("glossary.md"));

        assertTrue(glossary.contains("| 버전 | version |"));
        String saveLine = glossary.lines().filter(line -> line.startsWith("| 저장하기 | save |")).findFirst().orElseThrow();
        assertTrue(saveLine.contains("버전"));
    }
}
