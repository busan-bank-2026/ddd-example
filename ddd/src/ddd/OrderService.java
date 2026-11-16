package ddd;

import java.util.List;

public class OrderService {

    private final OrderRepository orderRepository;
    private long nextId = 1;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public long placeOrder(String customerName, List<OrderLine> orderLines, String address) {
        Order order = new Order(nextId++, customerName, orderLines, new Address(address));
        orderRepository.save(order);
        return order.getId();
    }

    public Order findOrder(long orderId) {
        return orderRepository.findById(orderId).orElseThrow();
    }

    public void addOrderLine(long orderId, OrderLine orderLine) {
        Order order = findOrder(orderId);
        order.addOrderLine(orderLine);
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
}
