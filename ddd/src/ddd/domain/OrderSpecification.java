package ddd.domain;

public interface OrderSpecification {

    boolean isSatisfiedBy(Order order);

    default OrderSpecification and(OrderSpecification other) {
        return order -> isSatisfiedBy(order) && other.isSatisfiedBy(order);
    }
}
