package ddd;

public class OrderService {

    private long nextId = 1;

    public Order placeOrder(String customerName, String productName, int price, int quantity, String address) {
        return new Order(nextId++, customerName, productName, price, quantity, address);
    }

    public void pay(Order order) {
        order.setStatus("결제 완료");
    }

    public void ship(Order order) {
        if (!order.getStatus().equals("결제 완료")) {
            throw new IllegalStateException("결제하지 않은 주문은 배송할 수 없습니다");
        }
        order.setStatus("배송 중");
    }

    public void cancel(Order order) {
        if (order.getStatus().equals("배송 중")) {
            throw new IllegalStateException("배송 중인 주문은 취소할 수 없습니다");
        }
        order.setStatus("취소");
    }
}
