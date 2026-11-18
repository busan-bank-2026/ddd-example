package ddd.order.domain;

public class FreeShippingSpecification {

    public boolean isSatisfiedBy(Order order) {
        return order.getPaymentPrice() >= 50000
                && !order.getAddress().value().startsWith("제주");
    }
}
