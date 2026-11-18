package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import ddd.order.domain.DiscountPolicy;
import ddd.order.domain.OrderItemFactory;
import ddd.order.infrastructure.MemoryOrderRepository;

class UbiquitousLanguageTest {

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
        return new OrderItemFactory(products);
    }

    private final OrderService service = new OrderService(new MemoryOrderRepository(), orderItemFactory(), new ddd.member.application.MemberService(members()), new DiscountPolicy(), TestServices.publisher());

    @Test
    @DisplayName("S1 주문을 만드는 일을 업무 말인 주문하기로 부른다")
    void s1() {
        long orderId = service.placeOrder(new OrderRequest(1, List.of(new OrderItemRequest(1, 2)), "서울시 강남구"));

        assertEquals("주문 접수", service.findOrder(orderId).getStatus().label());
        assertEquals(30000, service.findOrder(orderId).getTotalPrice());
    }

    @Test
    @DisplayName("S2 결제 상태를 바꾸는 일을 결제하기로 부른다")
    void s2() {
        long orderId = service.placeOrder(new OrderRequest(1, List.of(new OrderItemRequest(1, 2)), "서울시 강남구"));

        service.pay(orderId);

        assertEquals("결제 완료", service.findOrder(orderId).getStatus().label());
    }

    @Test
    @DisplayName("S3 배송 상태를 바꾸는 일을 배송하기로 부른다")
    void s3() {
        long orderId = service.placeOrder(new OrderRequest(1, List.of(new OrderItemRequest(1, 2)), "서울시 강남구"));
        service.pay(orderId);

        service.ship(orderId);

        assertEquals("배송 중", service.findOrder(orderId).getStatus().label());
    }

    @Test
    @DisplayName("S4 주문을 지우는 일을 주문 취소하기로 부른다. 주문은 지워지지 않고 취소 상태가 된다")
    void s4() {
        long orderId = service.placeOrder(new OrderRequest(1, List.of(new OrderItemRequest(1, 2)), "서울시 강남구"));

        service.cancel(orderId);

        assertEquals("취소", service.findOrder(orderId).getStatus().label());
        assertEquals(1L, service.findOrder(orderId).getMemberId());
    }

    @Test
    @DisplayName("S5 업무 말과 코드 이름을 나란히 적은 용어집을 학생 프로젝트에 둔다")
    void s5() throws Exception {
        String glossary = Files.readString(Path.of("glossary.md"));

        assertTrue(glossary.contains("| 주문하기 | placeOrder |"));
        assertTrue(glossary.contains("| 결제하기 | pay |"));
        assertTrue(glossary.contains("| 배송하기 | ship |"));
        assertTrue(glossary.contains("| 주문 취소하기 | cancel |"));
        assertTrue(glossary.contains("| 주문 금액 | totalPrice |"));
    }
}
