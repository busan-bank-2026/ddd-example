package ddd.order.application;

import java.util.ArrayList;
import java.util.List;

import ddd.member.domain.Member;
import ddd.member.application.MemberService;
import ddd.order.domain.Address;
import ddd.order.domain.DiscountPolicy;
import ddd.order.domain.Order;
import ddd.order.domain.OrderPlaced;
import ddd.order.domain.OrderItem;
import ddd.order.domain.OrderItemFactory;
import ddd.order.domain.OrderRepository;

public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemFactory orderItemFactory;
    private final MemberService memberService;
    private final DiscountPolicy discountPolicy;
    private final EventPublisher eventPublisher;
    private long nextId = 1;

    public OrderService(OrderRepository orderRepository, OrderItemFactory orderItemFactory,
            MemberService memberService, DiscountPolicy discountPolicy, EventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.orderItemFactory = orderItemFactory;
        this.memberService = memberService;
        this.discountPolicy = discountPolicy;
        this.eventPublisher = eventPublisher;
    }

    public long placeOrder(OrderRequest request) {
        List<OrderItem> items = new ArrayList<>();
        for (OrderItemRequest itemRequest : request.items()) {
            items.add(orderItemFactory.create(itemRequest.productId(), itemRequest.quantity()));
        }
        Order order = new Order(nextId++, request.memberId(), items, new Address(request.address()));
        orderRepository.save(order);
        eventPublisher.publish(new OrderPlaced(order.getId(), order.getItems()));
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
        Member member = memberService.findMember(order.getMemberId());
        order.pay(discountPolicy.discount(member, order));
        orderRepository.save(order);
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
    }
}
