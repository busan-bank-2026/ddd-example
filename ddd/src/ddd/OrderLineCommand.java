package ddd;

public record OrderLineCommand(String productName, int price, int quantity) {
}
