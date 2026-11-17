package ddd;

import java.util.List;

import ddd.catalog.application.DecreaseStockHandler;
import ddd.catalog.application.IncreaseStockHandler;
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
import legacypay.LegacyPayGateway;

public class Main {

    public static void main(String[] args) {
        System.out.println("===== 결제 실패 되돌리기 =====");

        MemberRepository memberRepository = new MemoryMemberRepository();
        memberRepository.save(new Member(1, "김철수", MemberGrade.BASIC));

        ProductRepository productRepository = new MemoryProductRepository();
        productRepository.save(new Product(1, "노트북", 120000, 10));

        EventPublisher eventPublisher = new EventPublisher();
        eventPublisher.subscribe(new DecreaseStockHandler(productRepository));
        eventPublisher.subscribe(new IncreaseStockHandler(productRepository));
        OrderService orderService = new OrderService(new MemoryOrderRepository(),
                new OrderItemFactory(productRepository), memberRepository, new DiscountPolicy(), eventPublisher,
                new LegacyPaymentAdapter(new LegacyPayGateway()), message -> System.out.println("보낸 메시지: " + message));

        System.out.println();
        System.out.println("[주문하기]");
        int stockBefore = productRepository.findById(1).orElseThrow().getStock();
        long orderId = orderService.placeOrder(new OrderRequest(1,
                List.of(new OrderItemRequest(1, 1)), "서울시 서초구"));
        eventPublisher.processEvents();
        int stockAfterOrder = productRepository.findById(1).orElseThrow().getStock();
        System.out.println("노트북 1개 주문: " + orderId + "번 주문, "
                + orderService.findOrder(orderId).getStatus().label());
        System.out.println("노트북 재고: " + stockBefore + "개 -> " + stockAfterOrder + "개");

        System.out.println();
        System.out.println("[결제하기]");
        String statusBefore = orderService.findOrder(orderId).getStatus().label();
        try {
            orderService.pay(orderId);
        } catch (IllegalStateException e) {
            System.out.println("결제 거절: " + e.getMessage());
        }
        eventPublisher.processEvents();
        int stockAfterPay = productRepository.findById(1).orElseThrow().getStock();
        System.out.println("주문 상태: " + statusBefore + " -> " + orderService.findOrder(orderId).getStatus().label());
        System.out.println("노트북 재고: " + stockAfterOrder + "개 -> " + stockAfterPay + "개");
    }
}
