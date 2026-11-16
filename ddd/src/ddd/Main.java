package ddd;

public class Main {

    public static void main(String[] args) {
        System.out.println("온라인 주문 프로그램을 시작합니다");

        OrderService orderService = new OrderService();

        Order order = orderService.createOrder("김철수", "자바 책", 15000, 2, "서울시 강남구");
        System.out.println("주문: " + order.getCustomerName() + ", " + order.getProductName() + " "
                + order.getQuantity() + "개, " + order.getTotalPrice() + "원, " + order.getStatus());

        orderService.updatePayment(order);
        System.out.println("결제: " + order.getStatus());

        orderService.updateDelivery(order);
        System.out.println("배송: " + order.getStatus());

        try {
            orderService.deleteOrder(order);
            System.out.println("취소: " + order.getStatus());
        } catch (IllegalStateException e) {
            System.out.println("취소: " + e.getMessage());
        }
    }
}
