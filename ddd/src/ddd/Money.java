package ddd;

import java.io.Serializable;

public record Money(int amount) implements Serializable {

    public Money {
        if (amount < 0) {
            throw new IllegalArgumentException("금액은 0원 이상이어야 합니다");
        }
    }

    public Money times(int count) {
        return new Money(amount * count);
    }

    public Money plus(Money other) {
        return new Money(amount + other.amount);
    }
}
