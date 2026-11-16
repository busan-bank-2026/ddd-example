package ddd.order.domain;

public class FreeShippingAreaSpecification implements OrderSpecification {

    @Override
    public boolean isSatisfiedBy(Order order) {
        return !order.getAddress().value().startsWith("제주");
    }
}
