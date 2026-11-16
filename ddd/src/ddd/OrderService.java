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

    public void updatePayment(Order order) {
        order.setStatus("결제 완료");
    }

    public void updateDelivery(Order order) {
        if (!order.getStatus().equals("결제 완료")) {
            throw new IllegalStateException("결제하지 않은 주문은 배송할 수 없습니다");
        }
        order.setStatus("배송 중");
    }
}
