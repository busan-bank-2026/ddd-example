package ddd;

public record OrderLine(String productName, Money price, int quantity) {

    public Money amount() {
        return price.times(quantity);
    }
}
