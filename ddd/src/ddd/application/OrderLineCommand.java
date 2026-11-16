package ddd.application;

public record OrderLineCommand(long productId, int quantity) {
}
