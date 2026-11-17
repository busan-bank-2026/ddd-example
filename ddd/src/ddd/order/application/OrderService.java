package ddd.order.application;

import java.util.ArrayList;
import java.util.List;

import ddd.member.domain.Member;
import ddd.member.domain.MemberRepository;
import ddd.order.domain.Address;
import ddd.order.domain.DiscountPolicy;
import ddd.order.domain.Order;
import ddd.order.domain.OrderCanceled;
import ddd.order.domain.OrderItem;
import ddd.order.domain.OrderItemFactory;
import ddd.order.domain.OrderRepository;
import ddd.order.domain.PaymentGateway;
import ddd.order.domain.PaymentResult;

public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemFactory orderItemFactory;
    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;
    private final EventPublisher eventPublisher;
    private final PaymentGateway paymentGateway;
    private final OrderPaidNotifier orderPaidNotifier;
    private long nextId = 1;

    public OrderService(OrderRepository orderRepository, OrderItemFactory orderItemFactory,
            MemberRepository memberRepository, DiscountPolicy discountPolicy, EventPublisher eventPublisher,
            PaymentGateway paymentGateway, MessageSender messageSender) {
        this.orderRepository = orderRepository;
        this.orderItemFactory = orderItemFactory;
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
        this.eventPublisher = eventPublisher;
        this.paymentGateway = paymentGateway;
        this.orderPaidNotifier = new OrderPaidNotifier(messageSender);
    }

    public long placeOrder(OrderRequest request) {
        List<OrderItem> items = new ArrayList<>();
        for (OrderItemRequest itemRequest : request.items()) {
            items.add(orderItemFactory.create(itemRequest.productId(), itemRequest.quantity()));
        }
        Order order = new Order(nextId++, request.memberId(), items, new Address(request.address()));
        orderRepository.save(order);
        eventPublisher.publish(order.getEvents());
        return order.getId();
    }

    public Order findOrder(long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("주문이 없습니다"));
    }

    public void addItem(long orderId, OrderItemRequest request) {
        Order order = findOrder(orderId);
        order.addItem(orderItemFactory.create(request.productId(), request.quantity()));
        orderRepository.save(order);
    }

    public void pay(long orderId) {
        Order order = findOrder(orderId);
        Member member = memberRepository.findById(order.getMemberId()).orElseThrow();
        order.pay(discountPolicy.discount(member, order));
        PaymentResult result = paymentGateway.pay(order.getPaymentPrice());
        if (!result.approved()) {
            cancel(orderId);
            throw new IllegalStateException(result.reason());
        }
        orderRepository.save(order);
        orderPaidNotifier.notifyPaid(order);
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
        eventPublisher.publish(List.of(new OrderCanceled(order.getId(), order.getItems())));
    }
}
