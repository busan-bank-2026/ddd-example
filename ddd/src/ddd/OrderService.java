package ddd;

import java.util.ArrayList;
import java.util.List;

public class OrderService {

    private final OrderRepository orderRepository;
    private long nextId = 1;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public long placeOrder(PlaceOrderCommand command) {
        List<OrderLine> orderLines = new ArrayList<>();
        for (OrderLineCommand orderLineCommand : command.orderLines()) {
            orderLines.add(toOrderLine(orderLineCommand));
        }
        Order order = new Order(nextId++, command.customerName(), orderLines, new Address(command.address()));
        orderRepository.save(order);
        return order.getId();
    }

    public Order findOrder(long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("주문이 없습니다"));
    }

    public void addOrderLine(long orderId, OrderLineCommand command) {
        Order order = findOrder(orderId);
        order.addOrderLine(toOrderLine(command));
        orderRepository.save(order);
    }

    public void pay(long orderId) {
        Order order = findOrder(orderId);
        order.pay();
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

    private OrderLine toOrderLine(OrderLineCommand command) {
        return new OrderLine(command.productName(), new Money(command.price()), command.quantity());
    }
}
