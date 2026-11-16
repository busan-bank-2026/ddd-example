package ddd.order.application;

import java.util.ArrayList;
import java.util.List;

import ddd.member.domain.Member;
import ddd.member.domain.MemberRepository;
import ddd.order.domain.Address;
import ddd.order.domain.DiscountPolicy;
import ddd.order.domain.Order;
import ddd.order.domain.OrderCanceled;
import ddd.order.domain.OrderLine;
import ddd.order.domain.OrderLineFactory;
import ddd.order.domain.OrderRepository;
import ddd.order.domain.PaymentGateway;
import ddd.order.domain.PaymentResult;

public class OrderApplicationService {

    private final OrderRepository orderRepository;
    private final OrderLineFactory orderLineFactory;
    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;
    private final EventPublisher eventPublisher;
    private final OrderOpenHostService orderOpenHostService;
    private final PaymentGateway paymentGateway;
    private long nextId = 1;

    public OrderApplicationService(OrderRepository orderRepository, OrderLineFactory orderLineFactory,
            MemberRepository memberRepository, DiscountPolicy discountPolicy, EventPublisher eventPublisher,
            PaymentGateway paymentGateway) {
        this.orderRepository = orderRepository;
        this.orderLineFactory = orderLineFactory;
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
        this.eventPublisher = eventPublisher;
        this.orderOpenHostService = new OrderOpenHostService(eventPublisher);
        this.paymentGateway = paymentGateway;
    }

    public long placeOrder(PlaceOrderCommand command) {
        List<OrderLine> orderLines = new ArrayList<>();
        for (OrderLineCommand orderLineCommand : command.orderLines()) {
            orderLines.add(orderLineFactory.create(orderLineCommand.productId(), orderLineCommand.quantity()));
        }
        Order order = new Order(nextId++, command.memberId(), orderLines, new Address(command.address()));
        orderRepository.save(order);
        eventPublisher.publish(order.getEvents());
        return order.getId();
    }

    public Order findOrder(long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("주문이 없습니다"));
    }

    public void addOrderLine(long orderId, OrderLineCommand command) {
        Order order = findOrder(orderId);
        order.addOrderLine(orderLineFactory.create(command.productId(), command.quantity()));
        orderRepository.save(order);
    }

    public void pay(long orderId) {
        Order order = findOrder(orderId);
        Member member = memberRepository.findById(order.getMemberId()).orElseThrow();
        order.pay(discountPolicy.discount(member, order));
        PaymentResult result = paymentGateway.pay(order.getPaymentPrice());
        if (!result.approved()) {
            throw new IllegalStateException(result.reason());
        }
        orderRepository.save(order);
        orderOpenHostService.publishOrderPaid(order);
    }

    public void ship(long orderId) {
        Order order = findOrder(orderId);
        order.ship();
        orderRepository.save(order);
    }

    public void deliver(long orderId) {
        Order order = findOrder(orderId);
        order.deliver();
        orderRepository.save(order);
    }

    public void cancel(long orderId) {
        Order order = findOrder(orderId);
        order.cancel();
        orderRepository.save(order);
        eventPublisher.publish(List.of(new OrderCanceled(order.getId(), order.getOrderLines())));
    }
}
