package ddd;

import java.util.List;

public class OrderService {

    private long nextId = 1;

    public Order placeOrder(String customerName, List<OrderLine> orderLines, String address) {
        return new Order(nextId++, customerName, orderLines, new Address(address));
    }

    public void pay(Order order) {
        order.pay();
    }

    public void ship(Order order) {
        order.ship();
    }

    public void deliver(Order order) {
        order.deliver();
    }

    public void cancel(Order order) {
        order.cancel();
    }
}
