package ddd;

import java.util.List;

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
import ddd.order.infrastructure.LegacyPaymentAdapter;
import ddd.order.infrastructure.MemoryOrderRepository;
import ddd.order.infrastructure.SocketMessageSender;
import legacypay.LegacyPayGateway;

public class OrderApp {

    public static void main(String[] args) {
        System.out.println("===== 주문 앱 =====");

        MemberRepository memberRepository = new MemoryMemberRepository();
        memberRepository.save(new Member(1, "김철수", MemberGrade.VIP));

        ProductRepository productRepository = new MemoryProductRepository();
        productRepository.save(new Product(1, "자바 책", 15000, 10));
        productRepository.save(new Product(2, "볼펜", 1000, 10));

        EventPublisher eventPublisher = new EventPublisher();
        OrderService orderService = new OrderService(new MemoryOrderRepository(),
                new OrderItemFactory(productRepository), memberRepository, new DiscountPolicy(), eventPublisher,
                new LegacyPaymentAdapter(new LegacyPayGateway()), new SocketMessageSender("localhost", 9090));

        List<OrderItemRequest> items = List.of(
                new OrderItemRequest(1, 2),
                new OrderItemRequest(2, 1));
        long orderId = orderService.placeOrder(new OrderRequest(1, items, "서울시 강남구"));
        orderService.pay(orderId);
        System.out.println("결제: " + orderId + "번 주문, " + orderService.findOrder(orderId).getStatus().label());
        System.out.println("결제 완료 메시지를 배송 앱으로 보냈습니다");
    }
}
