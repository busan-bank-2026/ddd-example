package ddd.application;

public record OrderLineCommand(String productName, int price, int quantity) {
}
