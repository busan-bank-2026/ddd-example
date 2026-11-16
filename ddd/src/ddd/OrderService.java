package ddd;

public class OrderService {

    private long nextId = 1;

    public Order placeOrder(String customerName, String productName, int price, int quantity, String address) {
        return new Order(nextId++, customerName, productName, price, quantity, address);
    }

    public void pay(Order order) {
        order.pay();
    }

    public void ship(Order order) {
        order.ship();
    }

    public void cancel(Order order) {
        order.cancel();
    }
}
