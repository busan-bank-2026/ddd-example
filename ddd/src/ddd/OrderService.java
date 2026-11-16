package ddd;

public class OrderService {

    public Order createOrder(String customerName, String productName, int price, int quantity, String address) {
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setProductName(productName);
        order.setPrice(price);
        order.setQuantity(quantity);
        order.setAddress(address);
        order.setTotalPrice(price * quantity);
        order.setStatus("주문 접수");
        return order;
    }
}
