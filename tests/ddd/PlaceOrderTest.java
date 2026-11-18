package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import ddd.order.application.OrderService;
import ddd.order.application.OrderItemRequest;
import ddd.order.application.OrderRequest;
import ddd.order.domain.Address;
import ddd.order.domain.DiscountPolicy;
import ddd.order.domain.Order;
import ddd.order.domain.OrderItem;
import ddd.order.domain.OrderItemFactory;
import ddd.order.domain.OrderRepository;
import ddd.order.domain.OrderStatus;
import ddd.order.infrastructure.MemoryOrderRepository;

class PlaceOrderTest {

    private static MemberRepository members() {
        MemberRepository members = new MemoryMemberRepository();
        members.save(new Member(1, "김철수", MemberGrade.BASIC));
        members.save(new Member(2, "이영희", MemberGrade.BASIC));
        return members;
    }

    private static OrderItemFactory orderItemFactory() {
        ProductRepository products = new MemoryProductRepository();
        products.save(new Product(1, "자바 책", 15000, 10));
        products.save(new Product(2, "볼펜", 1000, 10));
        products.save(new Product(3, "공책", 2000, 10));
        products.save(new Product(4, "할인 책", -15000, 10));
        return new OrderItemFactory(products);
    }

    private static String text(OrderItem item) {
        return item.getProductId() + " " + item.getProductName() + " " + item.getPrice() + " " + item.getQuantity();
    }

    @Test
    @DisplayName("S1 주문하기는 주문 요청 하나를 받는다. 주문 요청은 주문자, 주문 항목 요청 목록, 배송 주소를 담고, 주문 항목 요청은 상품 이름, 가격, 수량을 담는다. 요청은 밖에서 들어오는 값 묶음(DTO)이다")
    void s1() {
        OrderItemRequest book = new OrderItemRequest(1, 2);
        OrderRequest command = new OrderRequest(1, List.of(book), "서울시 강남구");

        assertEquals(1L, book.productId());
        assertEquals(2, book.quantity());
        assertEquals(1L, command.memberId());
        assertEquals(List.of(book), command.items());
        assertEquals("서울시 강남구", command.address());

        OrderService service = new OrderService(new MemoryOrderRepository(), orderItemFactory(), new ddd.member.application.MemberService(members()), new DiscountPolicy(), TestServices.publisher());
        long orderId = service.placeOrder(command);
        assertEquals(1L, service.findOrder(orderId).getMemberId());
    }

    @Test
    @DisplayName("S2 주문하기는 주문 요청으로 주문을 만들어 주문 저장소에 저장하고 주문 번호를 돌려준다")
    void s2() {
        OrderRepository repository = new MemoryOrderRepository();
        OrderService service = new OrderService(repository, orderItemFactory(), new ddd.member.application.MemberService(members()), new DiscountPolicy(), TestServices.publisher());

        long firstId = service.placeOrder(new OrderRequest(1, List.of(
                new OrderItemRequest(1, 2),
                new OrderItemRequest(2, 1)), "서울시 강남구"));
        long secondId = service.placeOrder(new OrderRequest(2,
                List.of(new OrderItemRequest(3, 3)), "부산시 해운대구"));

        assertEquals(1, firstId);
        assertEquals(2, secondId);
        Order first = repository.findById(firstId).orElseThrow();
        assertEquals(1L, first.getMemberId());
        assertEquals(List.of("1 자바 책 15000 2", "2 볼펜 1000 1"),
                first.getItems().stream().map(PlaceOrderTest::text).toList());
        assertEquals(new Address("서울시 강남구"), first.getAddress());
        assertEquals(31000, first.getTotalPrice());
        assertEquals(OrderStatus.ORDERED, first.getStatus());
        assertEquals(6000, repository.findById(secondId).orElseThrow().getTotalPrice());
    }

    @Test
    @DisplayName("S3 주문 항목 더하기는 주문 번호와 주문 항목 요청을 받아 주문을 꺼내 주문에게 시키고 다시 저장한다")
    void s3() {
        OrderRepository repository = new MemoryOrderRepository();
        OrderService service = new OrderService(repository, orderItemFactory(), new ddd.member.application.MemberService(members()), new DiscountPolicy(), TestServices.publisher());
        long orderId = service.placeOrder(new OrderRequest(1,
                List.of(new OrderItemRequest(1, 2)), "서울시 강남구"));

        service.addItem(orderId, new OrderItemRequest(3, 5));

        Order saved = repository.findById(orderId).orElseThrow();
        assertEquals(2, saved.getItems().size());
        assertEquals("3 공책 2000 5", text(saved.getItems().get(1)));
        assertEquals(40000, saved.getTotalPrice());
    }

    @Test
    @DisplayName("S4 요청의 값이 규칙에 맞지 않으면 주문, 주문 항목, 주소가 정한 거절 말 그대로 거절하고 아무것도 저장하지 않는다")
    void s4() {
        OrderRepository repository = new MemoryOrderRepository();
        OrderService service = new OrderService(repository, orderItemFactory(), new ddd.member.application.MemberService(members()), new DiscountPolicy(), TestServices.publisher());

        IllegalArgumentException negative = assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(new OrderRequest(2,
                        List.of(new OrderItemRequest(4, 1)), "광주시 북구")));
        assertEquals("가격은 0원 이상이어야 합니다", negative.getMessage());

        IllegalArgumentException blank = assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(new OrderRequest(2,
                        List.of(new OrderItemRequest(1, 1)), "")));
        assertEquals("배송 주소를 입력해야 합니다", blank.getMessage());

        IllegalArgumentException empty = assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(new OrderRequest(2, List.of(), "인천시 연수구")));
        assertEquals("주문 항목이 하나 이상 있어야 합니다", empty.getMessage());

        IllegalArgumentException zero = assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(new OrderRequest(2,
                        List.of(new OrderItemRequest(1, 0)), "대전시 유성구")));
        assertEquals("수량은 1개 이상이어야 합니다", zero.getMessage());

        for (long id = 1; id <= 4; id++) {
            assertTrue(repository.findById(id).isEmpty());
        }

        long orderId = service.placeOrder(new OrderRequest(1,
                List.of(new OrderItemRequest(1, 2)), "서울시 강남구"));
        assertThrows(IllegalArgumentException.class,
                () -> service.addItem(orderId, new OrderItemRequest(3, 0)));
        assertEquals(1, repository.findById(orderId).orElseThrow().getItems().size());
    }

    @Test
    @DisplayName("S5 주문 응용 서비스가 결제하기, 배송하기, 배송 완료하기, 주문 취소하기를 주문 번호로 받아 주문을 꺼내 주문에게 시키고 다시 저장한다")
    void s5() {
        OrderRepository repository = new MemoryOrderRepository();
        OrderService service = new OrderService(repository, orderItemFactory(), new ddd.member.application.MemberService(members()), new DiscountPolicy(), TestServices.publisher());
        long orderId = service.placeOrder(new OrderRequest(1,
                List.of(new OrderItemRequest(1, 2)), "서울시 강남구"));

        IllegalStateException unpaid = assertThrows(IllegalStateException.class, () -> service.ship(orderId));
        assertEquals("주문 접수인 주문은 배송할 수 없습니다", unpaid.getMessage());
        assertEquals(OrderStatus.ORDERED, repository.findById(orderId).orElseThrow().getStatus());

        service.pay(orderId);
        assertEquals(OrderStatus.PAID, repository.findById(orderId).orElseThrow().getStatus());
        service.ship(orderId);
        assertEquals(OrderStatus.SHIPPING, repository.findById(orderId).orElseThrow().getStatus());
        service.deliver(orderId);
        assertEquals(OrderStatus.DELIVERED, repository.findById(orderId).orElseThrow().getStatus());

        long canceledId = service.placeOrder(new OrderRequest(2,
                List.of(new OrderItemRequest(2, 1)), "부산시 해운대구"));
        service.cancel(canceledId);
        assertEquals(OrderStatus.CANCELED, repository.findById(canceledId).orElseThrow().getStatus());
    }

    @Test
    @DisplayName("S6 용어집에 주문 응용 서비스, 주문 요청, 주문 항목 요청을 더한다")
    void s6() throws Exception {
        String glossary = Files.readString(Path.of("glossary.md"));

        assertTrue(glossary.contains("| 주문 응용 서비스 | OrderService |"));
        assertTrue(glossary.contains("| 주문 요청 | OrderRequest |"));
        assertTrue(glossary.contains("| 주문 항목 요청 | OrderItemRequest |"));
    }
}
