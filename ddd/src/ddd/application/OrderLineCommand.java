package ddd.application;

public record OrderLineCommand(long productId, String productName, int price, int quantity) {
}
