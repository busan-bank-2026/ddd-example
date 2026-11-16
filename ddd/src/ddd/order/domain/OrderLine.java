package ddd.order.domain;

import java.io.Serializable;

import ddd.shared.Money;

public record OrderLine(long productId, String productName, Money price, int quantity) implements Serializable {

    public OrderLine {
        if (quantity < 1) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다");
        }
    }

    public Money amount() {
        return price.times(quantity);
    }
}
