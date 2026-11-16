package ddd;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("온라인 주문 프로그램을 시작합니다");

        OrderService orderService = new OrderService();

        List<OrderLine> orderLines = List.of(
                new OrderLine("자바 책", new Money(15000), 2),
                new OrderLine("볼펜", new Money(1000), 1));
        Order order = orderService.placeOrder("김철수", orderLines, "서울시 강남구");
        System.out.println("주문: " + order.getId() + "번, " + order.getCustomerName() + ", "
                + order.getTotalPrice().amount() + "원, " + order.getStatus().label());
        for (OrderLine orderLine : order.getOrderLines()) {
            System.out.println("주문 항목: " + orderLine.productName() + " " + orderLine.quantity() + "개, "
                    + orderLine.amount().amount() + "원");
        }

        orderService.pay(order);
        System.out.println("결제: " + order.getStatus().label());

        orderService.ship(order);
        System.out.println("배송: " + order.getStatus().label());

        try {
            orderService.cancel(order);
            System.out.println("취소: " + order.getStatus().label());
        } catch (IllegalStateException e) {
            System.out.println("취소: " + e.getMessage());
        }

        Order order2 = orderService.placeOrder("이영희", List.of(new OrderLine("자바 책", new Money(15000), 1)),
                "부산시 해운대구");
        try {
            order2.ship();
            System.out.println("결제 없이 배송: " + order2.getStatus().label());
        } catch (IllegalStateException e) {
            System.out.println("결제 없이 배송: " + e.getMessage());
        }

        try {
            Order order3 = orderService.placeOrder("박민수", List.of(new OrderLine("자바 책", new Money(-15000), 2)),
                    "대구시 수성구");
            System.out.println("음수 금액 주문: " + order3.getTotalPrice().amount() + "원");
        } catch (IllegalArgumentException e) {
            System.out.println("음수 금액 주문: " + e.getMessage());
        }

        try {
            Order order4 = orderService.placeOrder("최지우", List.of(new OrderLine("자바 책", new Money(15000), 1)), "");
            System.out.println("빈 주소 주문: " + order4.getStatus().label());
        } catch (IllegalArgumentException e) {
            System.out.println("빈 주소 주문: " + e.getMessage());
        }

        Order order5 = orderService.placeOrder("정하늘", List.of(
                new OrderLine("자바 책", new Money(15000), 2),
                new OrderLine("볼펜", new Money(1000), 1)), "광주시 북구");
        order5.addOrderLine(new OrderLine("공책", new Money(2000), 5));
        System.out.println("항목 더하기: 항목 " + order5.getOrderLines().size() + "개, 주문 금액 "
                + order5.getTotalPrice().amount() + "원");

        orderService.pay(order5);
        try {
            order5.addOrderLine(new OrderLine("공책", new Money(2000), 1));
            System.out.println("결제 뒤 항목 더하기: 항목 " + order5.getOrderLines().size() + "개");
        } catch (IllegalStateException e) {
            System.out.println("결제 뒤 항목 더하기: " + e.getMessage());
        }

        try {
            Order order6 = orderService.placeOrder("강민준", List.of(), "인천시 연수구");
            System.out.println("빈 주문: " + order6.getStatus().label());
        } catch (IllegalArgumentException e) {
            System.out.println("빈 주문: " + e.getMessage());
        }

        try {
            Order order7 = orderService.placeOrder("윤서연", List.of(new OrderLine("자바 책", new Money(15000), 0)),
                    "대전시 유성구");
            System.out.println("0개 항목 주문: " + order7.getTotalPrice().amount() + "원");
        } catch (IllegalArgumentException e) {
            System.out.println("0개 항목 주문: " + e.getMessage());
        }
    }
}
