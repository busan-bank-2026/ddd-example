package ddd.order.domain;

public class FreeShippingPriceSpecification implements OrderSpecification {

    @Override
    public boolean isSatisfiedBy(Order order) {
        return order.getPaymentPrice().amount() >= 50000;
    }
}
