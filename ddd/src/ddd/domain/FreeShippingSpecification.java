package ddd.domain;

public class FreeShippingSpecification {

    public boolean isSatisfiedBy(Order order) {
        return order.getPaymentPrice().amount() >= 50000;
    }
}
