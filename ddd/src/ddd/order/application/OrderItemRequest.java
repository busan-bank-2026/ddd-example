package ddd.order.application;

public record OrderItemRequest(long productId, int quantity) {
}
