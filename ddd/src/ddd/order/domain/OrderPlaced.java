package ddd.order.domain;

import java.util.List;

public record OrderPlaced(long orderId, List<OrderItem> items) {
}
