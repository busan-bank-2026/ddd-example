package ddd.application;

import java.util.List;

public record PlaceOrderCommand(long memberId, List<OrderLineCommand> orderLines, String address) {
}
