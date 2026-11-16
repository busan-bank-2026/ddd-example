package ddd.order.domain;

import ddd.shared.Money;

public interface PaymentGateway {

    PaymentResult pay(Money amount);
}
