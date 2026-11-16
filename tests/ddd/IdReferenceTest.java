package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
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
import ddd.order.infrastructure.MemoryOrderRepository;
import ddd.shared.Money;

class IdReferenceTest {

    private static MemberRepository members() {
        MemberRepository members = new MemoryMemberRepository();
        members.save(new Member(1, "김철수", MemberGrade.BASIC));
        members.save(new Member(2, "이영희", MemberGrade.BASIC));
        return members;
    }

    private static OrderLineFactory orderLineFactory() {
        ProductRepository products = new MemoryProductRepository();
        products.save(new Product(1, "자바 책", new Money(15000), 10));
        products.save(new Product(2, "볼펜", new Money(1000), 10));
        products.save(new Product(3, "공책", new Money(2000), 10));
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
    @DisplayName("S1 회원은 회원 번호와 이름을, 상품은 상품 번호, 이름, 가격을 가진다. 번호가 같으면 같은 회원, 같은 상품이다")
    void s1() {
        Member member = new Member(1, "김철수", MemberGrade.BASIC);
        assertEquals(1L, member.getId());
        assertEquals("김철수", member.getName());
        assertEquals(member, new Member(1, "김영수", MemberGrade.BASIC));
        assertEquals(member.hashCode(), new Member(1, "김영수", MemberGrade.BASIC).hashCode());
        assertNotEquals(member, new Member(2, "김철수", MemberGrade.BASIC));

        Product product = new Product(1, "자바 책", new Money(15000), 10);
        assertEquals(1L, product.getId());
        assertEquals("자바 책", product.getName());
        assertEquals(new Money(15000), product.getPrice());
        assertEquals(product, new Product(1, "자바 책", new Money(18000), 10));
        assertEquals(product.hashCode(), new Product(1, "자바 책", new Money(18000), 10).hashCode());
        assertNotEquals(product, new Product(2, "자바 책", new Money(15000), 10));
    }

    @Test
    @DisplayName("S2 회원 저장소와 상품 저장소에 회원과 상품을 저장하고 번호로 꺼낸다. 저장소 약속은 도메인 계층에, 메모리 구현은 인프라 계층에 두고, Main이 주문 전에 회원과 상품을 먼저 저장한다")
    void s2() throws Exception {
        MemberRepository members = new MemoryMemberRepository();
        members.save(new Member(1, "김철수", MemberGrade.BASIC));
        members.save(new Member(2, "이영희", MemberGrade.BASIC));
        assertEquals("김철수", members.findById(1).orElseThrow().getName());
        assertEquals("이영희", members.findById(2).orElseThrow().getName());
        assertTrue(members.findById(3).isEmpty());

        ProductRepository products = new MemoryProductRepository();
        products.save(new Product(1, "자바 책", new Money(15000), 10));
        assertEquals(new Money(15000), products.findById(1).orElseThrow().getPrice());
        assertTrue(products.findById(2).isEmpty());
        products.save(new Product(1, "자바 책", new Money(18000), 10));
        assertEquals(new Money(18000), products.findById(1).orElseThrow().getPrice());

        assertTrue(MemberRepository.class.isInterface());
        assertTrue(ProductRepository.class.isInterface());
        assertEquals("ddd.member.domain", MemberRepository.class.getPackageName());
        assertEquals("ddd.catalog.domain", ProductRepository.class.getPackageName());
        assertEquals("ddd.member.infrastructure", MemoryMemberRepository.class.getPackageName());
        assertEquals("ddd.catalog.infrastructure", MemoryProductRepository.class.getPackageName());

        String main = Files.readString(Path.of("src/ddd/Main.java"));
        int firstOrder = main.indexOf("placeOrder(");
        assertTrue(main.indexOf("new Member(1, \"김철수\", MemberGrade.VIP)") > 0);
        assertTrue(main.indexOf("new Member(1, \"김철수\", MemberGrade.VIP)") < firstOrder);
        assertTrue(main.indexOf("new Product(1, \"자바 책\", new Money(15000), 10)") > 0);
        assertTrue(main.indexOf("new Product(1, \"자바 책\", new Money(15000), 10)") < firstOrder);
    }

    @Test
    @DisplayName("S3 주문은 회원을 객체로 갖지 않고 회원 번호로 가리킨다. 주문하기 명령은 주문자 이름 대신 회원 번호를 담고, Main은 주문의 회원 번호로 회원 저장소에서 회원을 찾아 이름을 보여준다")
    void s3() throws Exception {
        OrderApplicationService service = new OrderApplicationService(new MemoryOrderRepository(), orderLineFactory(), members(), new DiscountPolicy(), new EventPublisher());
        PlaceOrderCommand command = new PlaceOrderCommand(7,
                List.of(new OrderLineCommand(1, 2)), "서울시 강남구");
        assertEquals(7L, command.memberId());

        Order order = service.findOrder(service.placeOrder(command));
        assertEquals(7L, order.getMemberId());

        List<Class<?>> fieldTypes = Arrays.stream(Order.class.getDeclaredFields()).map(Field::getType).toList();
        assertFalse(fieldTypes.contains(Member.class));
        assertFalse(Arrays.stream(Order.class.getDeclaredFields()).anyMatch(field -> field.getName().equals("customerName")));

        String main = Files.readString(Path.of("src/ddd/Main.java"));
        assertTrue(main.contains("memberRepository.findById(order.getMemberId())"));
        assertTrue(runMain().contains("주문: 1번, 김철수, 31000원, 주문 접수"));
    }

    @Test
    @DisplayName("S4 주문 항목은 상품을 객체로 갖지 않고 상품 번호로 가리킨다. 주문 항목 명령과 주문 항목은 상품 번호, 주문 때의 상품 이름, 가격, 수량을 담는다")
    void s4() {
        OrderLineCommand command = new OrderLineCommand(2, 3);
        assertEquals(2L, command.productId());

        OrderApplicationService service = new OrderApplicationService(new MemoryOrderRepository(), orderLineFactory(), members(), new DiscountPolicy(), new EventPublisher());
        long orderId = service.placeOrder(new PlaceOrderCommand(1, List.of(command), "서울시 강남구"));
        service.addOrderLine(orderId, new OrderLineCommand(3, 5));

        List<OrderLine> orderLines = service.findOrder(orderId).getOrderLines();
        assertEquals(new OrderLine(2, "볼펜", new Money(1000), 3), orderLines.get(0));
        assertEquals(2L, orderLines.get(0).productId());
        assertEquals(3L, orderLines.get(1).productId());
        assertEquals("공책", orderLines.get(1).productName());

        List<Class<?>> componentTypes = Arrays.stream(OrderLine.class.getRecordComponents())
                .map(RecordComponent::getType).toList();
        assertEquals(List.of(long.class, String.class, Money.class, int.class), componentTypes);
        assertFalse(componentTypes.contains(Product.class));
    }

    @Test
    @DisplayName("S5 주문 뒤 상품 가격이 바뀌어도 주문 항목의 가격과 주문 금액은 주문 때 그대로다")
    void s5() {
        ProductRepository products = new MemoryProductRepository();
        products.save(new Product(1, "자바 책", new Money(15000), 10));
        Product book = products.findById(1).orElseThrow();

        OrderRepository orders = new MemoryOrderRepository();
        OrderApplicationService service = new OrderApplicationService(orders, new OrderLineFactory(products), members(), new DiscountPolicy(), new EventPublisher());
        long orderId = service.placeOrder(new PlaceOrderCommand(1, List.of(
                new OrderLineCommand(book.getId(), 2)), "서울시 강남구"));

        products.save(new Product(1, "자바 책", new Money(18000), 10));

        Order saved = orders.findById(orderId).orElseThrow();
        assertEquals(new Money(18000), products.findById(1).orElseThrow().getPrice());
        assertEquals(new Money(15000), saved.getOrderLines().get(0).price());
        assertEquals(new Money(30000), saved.getTotalPrice());

        String[] lines = runMain().split(System.lineSeparator());
        assertTrue(List.of(lines).contains("가격 바뀐 뒤: 1번 상품 18000원, 주문 항목 15000원, 주문 금액 31000원"));
    }

    @Test
    @DisplayName("S6 용어집에 회원, 회원 저장소, 상품, 상품 저장소, 회원 번호, 상품 번호를 더하고 주문자 줄을 회원 번호로 바꾼다")
    void s6() throws Exception {
        String glossary = Files.readString(Path.of("glossary.md"));

        assertTrue(glossary.contains("| 회원 | Member |"));
        assertTrue(glossary.contains("| 회원 저장소 | MemberRepository |"));
        assertTrue(glossary.contains("| 상품 | Product |"));
        assertTrue(glossary.contains("| 상품 저장소 | ProductRepository |"));
        assertTrue(glossary.contains("| 회원 번호 | memberId |"));
        assertTrue(glossary.contains("| 상품 번호 | productId |"));
        assertFalse(glossary.contains("customerName"));
    }
}
