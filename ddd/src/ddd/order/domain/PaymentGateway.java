package ddd.order.domain;

public interface PaymentGateway {

    PaymentResult pay(int amount);
}
