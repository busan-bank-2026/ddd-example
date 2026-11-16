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
import ddd.order.application.OrderApplicationService;
import ddd.order.application.OrderLineCommand;
import ddd.order.application.PlaceOrderCommand;
import ddd.order.application.SocketEventPublisher;
import ddd.order.domain.DiscountPolicy;
import ddd.order.domain.OrderLineFactory;
import ddd.order.infrastructure.LegacyPaymentAdapter;
import ddd.order.infrastructure.MemoryOrderRepository;
import ddd.shared.Money;
import legacypay.LegacyPayGateway;

public class OrderApp {

    public static void main(String[] args) {
        System.out.println("주문 앱을 시작합니다");

        MemberRepository memberRepository = new MemoryMemberRepository();
        memberRepository.save(new Member(1, "김철수", MemberGrade.VIP));

        ProductRepository productRepository = new MemoryProductRepository();
        productRepository.save(new Product(1, "자바 책", new Money(15000), 10));
        productRepository.save(new Product(2, "볼펜", new Money(1000), 10));

        EventPublisher eventPublisher = new SocketEventPublisher("localhost", 9090);
        OrderApplicationService orderService = new OrderApplicationService(new MemoryOrderRepository(),
                new OrderLineFactory(productRepository), memberRepository, new DiscountPolicy(), eventPublisher,
                new LegacyPaymentAdapter(new LegacyPayGateway()));

        List<OrderLineCommand> orderLines = List.of(
                new OrderLineCommand(1, 2),
                new OrderLineCommand(2, 1));
        long orderId = orderService.placeOrder(new PlaceOrderCommand(1, orderLines, "서울시 강남구"));
        orderService.pay(orderId);
        System.out.println("결제: " + orderId + "번 주문, " + orderService.findOrder(orderId).getStatus().label());
        System.out.println("결제 완료 메시지를 배송 앱으로 보냈습니다");
    }
}
