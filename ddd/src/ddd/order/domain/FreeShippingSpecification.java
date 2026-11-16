package ddd.order.domain;

public class FreeShippingSpecification implements OrderSpecification {

    @Override
    public boolean isSatisfiedBy(Order order) {
        return new FreeShippingPriceSpecification().and(new FreeShippingAreaSpecification()).isSatisfiedBy(order);
    }
}
