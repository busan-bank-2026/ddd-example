package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.RecordComponent;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
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
import ddd.order.domain.DiscountPolicy;
import ddd.order.domain.Order;
import ddd.order.domain.OrderLine;
import ddd.order.domain.OrderLineFactory;
import ddd.order.domain.OrderRepository;
import ddd.order.domain.PaymentGateway;
import ddd.order.infrastructure.LegacyPaymentAdapter;
import ddd.order.infrastructure.MemoryOrderRepository;
import ddd.shared.Money;
import legacypay.LegacyPayGateway;

class OrderFactoryTest {

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
            "한도 넘은 결제: 결제 한도를 넘었습니다, 취소",
            "주문 목록: 1번, 김철수, 항목 2개, 27900원, 배송 완료",
            "결제 실패 뒤: 노트북 재고 10개, 취소") + System.lineSeparator();

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

    private static MemberRepository members() {
        MemberRepository members = new MemoryMemberRepository();
        members.save(new Member(1, "김철수", MemberGrade.BASIC));
        members.save(new Member(2, "이영희", MemberGrade.BASIC));
        return members;
    }

    private static ProductRepository products() {
        ProductRepository products = new MemoryProductRepository();
        products.save(new Product(1, "자바 책", new Money(15000), 10));
        products.save(new Product(2, "볼펜", new Money(1000), 10));
        products.save(new Product(3, "공책", new Money(2000), 10));
        return products;
    }

    @Test
    @DisplayName("S1 주문 항목 명령은 상품 번호와 수량만 담는다. 상품 이름과 가격은 명령에 넣지 않는다")
    void s1() {
        OrderLineCommand command = new OrderLineCommand(2, 3);
        assertEquals(2L, command.productId());
        assertEquals(3, command.quantity());

        RecordComponent[] components = OrderLineCommand.class.getRecordComponents();
        assertEquals(List.of("productId", "quantity"),
                Arrays.stream(components).map(RecordComponent::getName).toList());
        assertEquals(List.of(long.class, int.class),
                Arrays.stream(components).map(RecordComponent::getType).toList());
    }

    @Test
    @DisplayName("S2 주문 항목 팩토리는 상품 번호와 수량을 받아 상품 저장소에서 상품을 찾고, 그 상품의 이름과 지금 가격을 담은 주문 항목을 만든다. 팩토리는 도메인 계층에 둔다")
    void s2() {
        ProductRepository products = products();
        OrderLineFactory factory = new OrderLineFactory(products);

        assertEquals(new OrderLine(1, "자바 책", new Money(15000), 2), factory.create(1, 2));
        assertEquals(new OrderLine(2, "볼펜", new Money(1000), 1), factory.create(2, 1));

        products.save(new Product(1, "자바 책", new Money(18000), 10));
        assertEquals(new OrderLine(1, "자바 책", new Money(18000), 2), factory.create(1, 2));

        assertEquals("ddd.order.domain", OrderLineFactory.class.getPackageName());
    }

    @Test
    @DisplayName("S3 주문 응용 서비스는 주문하기와 주문 항목 더하기에서 주문 항목 팩토리에게 주문 항목을 만들게 한다. Main은 팩토리를 만들어 주문 응용 서비스에 넘기고, 앞 장 출력 열아홉 줄은 그대로다")
    void s3() throws Exception {
        assertEquals(List.of(OrderRepository.class, OrderLineFactory.class, MemberRepository.class, DiscountPolicy.class, EventPublisher.class,
                PaymentGateway.class),
                List.of(OrderApplicationService.class.getConstructors()[0].getParameterTypes()));

        OrderRepository orders = new MemoryOrderRepository();
        OrderApplicationService service = new OrderApplicationService(orders, new OrderLineFactory(products()), members(), new DiscountPolicy(), new EventPublisher(), new LegacyPaymentAdapter(new LegacyPayGateway()));
        long orderId = service.placeOrder(new PlaceOrderCommand(1, List.of(
                new OrderLineCommand(1, 2),
                new OrderLineCommand(2, 1)), "서울시 강남구"));
        service.addOrderLine(orderId, new OrderLineCommand(3, 5));

        Order saved = orders.findById(orderId).orElseThrow();
        assertEquals(List.of(
                new OrderLine(1, "자바 책", new Money(15000), 2),
                new OrderLine(2, "볼펜", new Money(1000), 1),
                new OrderLine(3, "공책", new Money(2000), 5)), saved.getOrderLines());
        assertEquals(new Money(41000), saved.getTotalPrice());

        String main = Files.readString(Path.of("src/ddd/Main.java"));
        assertTrue(main.contains("new OrderLineFactory(productRepository)"));
        String printed = runMain();
        assertTrue(printed.contains("주문: 1번, 김철수, 31000원, 주문 접수"));
        assertTrue(printed.contains("주문 항목: 자바 책 2개, 30000원"));
        assertTrue(printed.contains("주문 항목: 볼펜 1개, 1000원"));
        assertTrue(printed.contains("항목 더하기: 항목 3개, 주문 금액 41000원"));
    }

    @Test
    @DisplayName("S4 없는 상품 번호로 주문하거나 항목을 더하면 상품이 없습니다로 거절하고 아무것도 저장하지 않는다. Main이 없는 상품 주문 장면을 보여준다")
    void s4() {
        OrderRepository orders = new MemoryOrderRepository();
        OrderApplicationService service = new OrderApplicationService(orders, new OrderLineFactory(products()), members(), new DiscountPolicy(), new EventPublisher(), new LegacyPaymentAdapter(new LegacyPayGateway()));

        IllegalArgumentException placing = assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(new PlaceOrderCommand(1, List.of(new OrderLineCommand(100, 1)), "서울시 강남구")));
        assertEquals("상품이 없습니다", placing.getMessage());
        assertTrue(orders.findById(1).isEmpty());

        long orderId = service.placeOrder(new PlaceOrderCommand(1, List.of(new OrderLineCommand(1, 2)), "서울시 강남구"));
        IllegalArgumentException adding = assertThrows(IllegalArgumentException.class,
                () -> service.addOrderLine(orderId, new OrderLineCommand(100, 1)));
        assertEquals("상품이 없습니다", adding.getMessage());
        assertEquals(1, orders.findById(orderId).orElseThrow().getOrderLines().size());

        assertEquals(EXPECTED, runMain());
    }

    @Test
    @DisplayName("S5 용어집에 주문 항목 팩토리를 더하고 주문 항목 명령의 뜻을 상품 번호와 수량으로 바꾼다")
    void s5() throws Exception {
        String glossary = Files.readString(Path.of("glossary.md"));

        assertTrue(glossary.contains("| 주문 항목 팩토리 | OrderLineFactory |"));
        assertTrue(glossary.contains("| 주문 항목 명령 | OrderLineCommand | 주문 항목을 만들 상품 번호, 수량 묶음 |"));
    }
}
