package ddd;

import java.util.List;

import ddd.application.OrderApplicationService;
import ddd.application.OrderLineCommand;
import ddd.application.PlaceOrderCommand;
import ddd.domain.Member;
import ddd.domain.MemberRepository;
import ddd.domain.Money;
import ddd.domain.Order;
import ddd.domain.OrderLine;
import ddd.domain.Product;
import ddd.domain.ProductRepository;
import ddd.infrastructure.MemoryMemberRepository;
import ddd.infrastructure.MemoryOrderRepository;
import ddd.infrastructure.MemoryProductRepository;

public class Main {

    public static void main(String[] args) {
        System.out.println("온라인 주문 프로그램을 시작합니다");

        MemberRepository memberRepository = new MemoryMemberRepository();
        memberRepository.save(new Member(1, "김철수"));
        memberRepository.save(new Member(2, "이영희"));

        ProductRepository productRepository = new MemoryProductRepository();
        productRepository.save(new Product(1, "자바 책", new Money(15000)));
        productRepository.save(new Product(2, "볼펜", new Money(1000)));
        productRepository.save(new Product(3, "공책", new Money(2000)));

        OrderApplicationService orderService = new OrderApplicationService(new MemoryOrderRepository());

        List<OrderLineCommand> orderLines = List.of(
                new OrderLineCommand(1, "자바 책", 15000, 2),
                new OrderLineCommand(2, "볼펜", 1000, 1));
        long orderId = orderService.placeOrder(new PlaceOrderCommand(1, orderLines, "서울시 강남구"));
        Order order = orderService.findOrder(orderId);
        Member member = memberRepository.findById(order.getMemberId()).orElseThrow();
        System.out.println("주문: " + order.getId() + "번, " + member.getName() + ", "
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

        long order2Id = orderService.placeOrder(new PlaceOrderCommand(2,
                List.of(new OrderLineCommand(1, "자바 책", 15000, 1)), "부산시 해운대구"));
        try {
            orderService.ship(order2Id);
            System.out.println("결제 없이 배송: " + orderService.findOrder(order2Id).getStatus().label());
        } catch (IllegalStateException e) {
            System.out.println("결제 없이 배송: " + e.getMessage());
        }

        try {
            long order3Id = orderService.placeOrder(new PlaceOrderCommand(2,
                    List.of(new OrderLineCommand(1, "자바 책", -15000, 2)), "대구시 수성구"));
            System.out.println("음수 금액 주문: " + orderService.findOrder(order3Id).getTotalPrice().amount() + "원");
        } catch (IllegalArgumentException e) {
            System.out.println("음수 금액 주문: " + e.getMessage());
        }

        try {
            long order4Id = orderService.placeOrder(new PlaceOrderCommand(2,
                    List.of(new OrderLineCommand(1, "자바 책", 15000, 1)), ""));
            System.out.println("빈 주소 주문: " + orderService.findOrder(order4Id).getStatus().label());
        } catch (IllegalArgumentException e) {
            System.out.println("빈 주소 주문: " + e.getMessage());
        }

        long order5Id = orderService.placeOrder(new PlaceOrderCommand(2, List.of(
                new OrderLineCommand(1, "자바 책", 15000, 2),
                new OrderLineCommand(2, "볼펜", 1000, 1)), "광주시 북구"));
        orderService.addOrderLine(order5Id, new OrderLineCommand(3, "공책", 2000, 5));
        Order order5 = orderService.findOrder(order5Id);
        System.out.println("항목 더하기: 항목 " + order5.getOrderLines().size() + "개, 주문 금액 "
                + order5.getTotalPrice().amount() + "원");

        orderService.pay(order5Id);
        try {
            orderService.addOrderLine(order5Id, new OrderLineCommand(3, "공책", 2000, 1));
            System.out.println("결제 뒤 항목 더하기: 항목 " + orderService.findOrder(order5Id).getOrderLines().size() + "개");
        } catch (IllegalStateException e) {
            System.out.println("결제 뒤 항목 더하기: " + e.getMessage());
        }

        try {
            long order6Id = orderService.placeOrder(new PlaceOrderCommand(2, List.of(), "인천시 연수구"));
            System.out.println("빈 주문: " + orderService.findOrder(order6Id).getStatus().label());
        } catch (IllegalArgumentException e) {
            System.out.println("빈 주문: " + e.getMessage());
        }

        try {
            long order7Id = orderService.placeOrder(new PlaceOrderCommand(2,
                    List.of(new OrderLineCommand(1, "자바 책", 15000, 0)), "대전시 유성구"));
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

        Order notSaved = orderService.findOrder(order2Id);
        notSaved.cancel();
        System.out.println("저장 안 한 취소: " + orderService.findOrder(order2Id).getStatus().label());

        productRepository.save(new Product(1, "자바 책", new Money(18000)));
        Order firstOrder = orderService.findOrder(orderId);
        OrderLine bookLine = firstOrder.getOrderLines().get(0);
        Product book = productRepository.findById(bookLine.productId()).orElseThrow();
        System.out.println("가격 바뀐 뒤: " + book.getId() + "번 상품 " + book.getPrice().amount() + "원, 주문 항목 "
                + bookLine.price().amount() + "원, 주문 금액 " + firstOrder.getTotalPrice().amount() + "원");
    }
}
