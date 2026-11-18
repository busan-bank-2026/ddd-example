package ddd;

import java.util.List;

import ddd.catalog.application.DecreaseStockHandler;
import ddd.catalog.application.ProductService;
import ddd.catalog.domain.Product;
import ddd.catalog.domain.ProductRepository;
import ddd.catalog.infrastructure.MemoryProductRepository;
import ddd.member.application.MemberService;
import ddd.member.domain.Member;
import ddd.member.domain.MemberGrade;
import ddd.member.domain.MemberRepository;
import ddd.member.infrastructure.MemoryMemberRepository;
import ddd.order.application.EventPublisher;
import ddd.order.application.OrderItemRequest;
import ddd.order.application.OrderRequest;
import ddd.order.application.OrderService;
import ddd.order.domain.DiscountPolicy;
import ddd.order.domain.OrderItemFactory;
import ddd.order.infrastructure.MemoryOrderRepository;

public class Main {

    public static void main(String[] args) {
        System.out.println("===== 컨텍스트 나누기 =====");

        MemberRepository memberRepository = new MemoryMemberRepository();
        memberRepository.save(new Member(1, "김철수", MemberGrade.BASIC));
        MemberService memberService = new MemberService(memberRepository);

        ProductRepository productRepository = new MemoryProductRepository();
        productRepository.save(new Product(1, "자바 책", 15000, 10));
        ProductService productService = new ProductService(productRepository);

        EventPublisher eventPublisher = new EventPublisher(new DecreaseStockHandler(productService));
        OrderService orderService = new OrderService(new MemoryOrderRepository(),
                new OrderItemFactory(productRepository), memberService, new DiscountPolicy(), eventPublisher);

        System.out.println();
        System.out.println("[회원 목록]");
        for (Member member : memberService.listMembers()) {
            System.out.println(member.getId() + "번 " + member.getName() + ", " + member.getGrade());
        }

        System.out.println();
        System.out.println("[상품 목록]");
        for (Product product : productService.listProducts()) {
            System.out.println(product.getId() + "번 " + product.getName() + ", "
                    + product.getPrice() + "원, 재고 " + product.getStock() + "개");
        }

        System.out.println();
        System.out.println("[주문 접수 이벤트로 재고 줄이기]");
        int stockBefore = productService.findProduct(1).getStock();
        long orderId = orderService.placeOrder(new OrderRequest(1,
                List.of(new OrderItemRequest(1, 2)), "서울시 강남구"));
        System.out.println("주문 컨텍스트: " + orderId + "번 주문, "
                + orderService.findOrder(orderId).getTotalPrice() + "원, "
                + orderService.findOrder(orderId).getStatus().label());
        System.out.println("상품 컨텍스트: 자바 책 재고 " + stockBefore
                + "개 -> " + productService.findProduct(1).getStock() + "개");
    }
}
