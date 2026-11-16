package ddd;

import java.util.List;

public record PlaceOrderCommand(String customerName, List<OrderLineCommand> orderLines, String address) {
}
