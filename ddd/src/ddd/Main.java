package ddd;

public class Main {

    public static void main(String[] args) {
        System.out.println("온라인 주문 프로그램을 시작합니다");

        OrderService orderService = new OrderService();

        Order order = orderService.placeOrder("김철수", "자바 책", 15000, 2, "서울시 강남구");
        System.out.println("주문: " + order.getId() + "번, " + order.getCustomerName() + ", "
                + order.getProductName() + " " + order.getQuantity() + "개, " + order.getTotalPrice().amount()
                + "원, " + order.getStatus());

        orderService.pay(order);
        System.out.println("결제: " + order.getStatus());

        orderService.ship(order);
        System.out.println("배송: " + order.getStatus());

        try {
            orderService.cancel(order);
            System.out.println("취소: " + order.getStatus());
        } catch (IllegalStateException e) {
            System.out.println("취소: " + e.getMessage());
        }

        Order order2 = orderService.placeOrder("이영희", "자바 책", 15000, 1, "부산시 해운대구");
        try {
            order2.ship();
            System.out.println("결제 없이 배송: " + order2.getStatus());
        } catch (IllegalStateException e) {
            System.out.println("결제 없이 배송: " + e.getMessage());
        }

        try {
            Order order3 = orderService.placeOrder("박민수", "자바 책", -15000, 2, "대구시 수성구");
            System.out.println("음수 금액 주문: " + order3.getTotalPrice().amount() + "원");
        } catch (IllegalArgumentException e) {
            System.out.println("음수 금액 주문: " + e.getMessage());
        }

        try {
            Order order4 = orderService.placeOrder("최지우", "자바 책", 15000, 1, "");
            System.out.println("빈 주소 주문: " + order4.getStatus());
        } catch (IllegalArgumentException e) {
            System.out.println("빈 주소 주문: " + e.getMessage());
        }
    }
}
