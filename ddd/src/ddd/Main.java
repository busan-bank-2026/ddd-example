package ddd;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("온라인 주문 프로그램을 시작합니다");

        OrderService orderService = new OrderService(new MemoryOrderRepository());

        List<OrderLine> orderLines = List.of(
                new OrderLine("자바 책", new Money(15000), 2),
                new OrderLine("볼펜", new Money(1000), 1));
        long orderId = orderService.placeOrder("김철수", orderLines, "서울시 강남구");
        Order order = orderService.findOrder(orderId);
        System.out.println("주문: " + order.getId() + "번, " + order.getCustomerName() + ", "
                + order.getTotalPrice().amount() + "원, " + order.getStatus().label());
        for (OrderLine orderLine : order.getOrderLines()) {
            System.out.println("주문 항목: " + orderLine.productName() + " " + orderLine.quantity() + "개, "
                    + orderLine.amount().amount() + "원");
        }

        orderService.pay(orderId);
        System.out.println("결제: " + orderService.findOrder(orderId).getStatus().label());

        orderService.ship(orderId);
        System.out.println("배송: " + orderService.findOrder(orderId).getStatus().label());

        try {
            orderService.cancel(orderId);
            System.out.println("취소: " + orderService.findOrder(orderId).getStatus().label());
        } catch (IllegalStateException e) {
            System.out.println("취소: " + e.getMessage());
        }

        long order2Id = orderService.placeOrder("이영희", List.of(new OrderLine("자바 책", new Money(15000), 1)),
                "부산시 해운대구");
        try {
            orderService.ship(order2Id);
            System.out.println("결제 없이 배송: " + orderService.findOrder(order2Id).getStatus().label());
        } catch (IllegalStateException e) {
            System.out.println("결제 없이 배송: " + e.getMessage());
        }

        try {
            long order3Id = orderService.placeOrder("박민수", List.of(new OrderLine("자바 책", new Money(-15000), 2)),
                    "대구시 수성구");
            System.out.println("음수 금액 주문: " + orderService.findOrder(order3Id).getTotalPrice().amount() + "원");
        } catch (IllegalArgumentException e) {
            System.out.println("음수 금액 주문: " + e.getMessage());
        }

        try {
            long order4Id = orderService.placeOrder("최지우", List.of(new OrderLine("자바 책", new Money(15000), 1)), "");
            System.out.println("빈 주소 주문: " + orderService.findOrder(order4Id).getStatus().label());
        } catch (IllegalArgumentException e) {
            System.out.println("빈 주소 주문: " + e.getMessage());
        }

        long order5Id = orderService.placeOrder("정하늘", List.of(
                new OrderLine("자바 책", new Money(15000), 2),
                new OrderLine("볼펜", new Money(1000), 1)), "광주시 북구");
        orderService.addOrderLine(order5Id, new OrderLine("공책", new Money(2000), 5));
        Order order5 = orderService.findOrder(order5Id);
        System.out.println("항목 더하기: 항목 " + order5.getOrderLines().size() + "개, 주문 금액 "
                + order5.getTotalPrice().amount() + "원");

        orderService.pay(order5Id);
        try {
            orderService.addOrderLine(order5Id, new OrderLine("공책", new Money(2000), 1));
            System.out.println("결제 뒤 항목 더하기: 항목 " + orderService.findOrder(order5Id).getOrderLines().size() + "개");
        } catch (IllegalStateException e) {
            System.out.println("결제 뒤 항목 더하기: " + e.getMessage());
        }

        try {
            long order6Id = orderService.placeOrder("강민준", List.of(), "인천시 연수구");
            System.out.println("빈 주문: " + orderService.findOrder(order6Id).getStatus().label());
        } catch (IllegalArgumentException e) {
            System.out.println("빈 주문: " + e.getMessage());
        }

        try {
            long order7Id = orderService.placeOrder("윤서연", List.of(new OrderLine("자바 책", new Money(15000), 0)),
                    "대전시 유성구");
            System.out.println("0개 항목 주문: " + orderService.findOrder(order7Id).getTotalPrice().amount() + "원");
        } catch (IllegalArgumentException e) {
            System.out.println("0개 항목 주문: " + e.getMessage());
        }

        try {
            orderService.pay(order5Id);
            System.out.println("두 번 결제: " + orderService.findOrder(order5Id).getStatus().label());
        } catch (IllegalStateException e) {
            System.out.println("두 번 결제: " + e.getMessage());
        }

        orderService.deliver(orderId);
        System.out.println("배송 완료: " + orderService.findOrder(orderId).getStatus().label());

        try {
            orderService.pay(100);
            System.out.println("없는 주문 결제: " + orderService.findOrder(100).getStatus().label());
        } catch (IllegalArgumentException e) {
            System.out.println("없는 주문 결제: " + e.getMessage());
        }
    }
}
