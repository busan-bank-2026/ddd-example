package ddd.order.domain;

import java.util.List;

public record OrderCanceled(long orderId, List<OrderItem> items) {
}
