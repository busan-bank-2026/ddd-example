package ddd.order.application;

public record OrderSummary(long orderId, String memberName, int itemCount, int paymentPrice, String status) {
}
