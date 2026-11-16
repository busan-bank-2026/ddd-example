package ddd.order.application;

public record OrderLineCommand(long productId, int quantity) {
}
