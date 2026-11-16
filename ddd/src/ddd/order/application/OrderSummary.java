package ddd.order.application;

public record OrderSummary(long orderId, String memberName, int orderLineCount, int paymentPrice, String status) {
}
