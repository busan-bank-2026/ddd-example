package ddd.order.application;

import java.util.List;

public record OrderRequest(long memberId, List<OrderItemRequest> items, String address) {
}
