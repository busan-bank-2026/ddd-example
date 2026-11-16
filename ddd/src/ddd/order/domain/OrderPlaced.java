package ddd.order.domain;

import java.io.Serializable;
import java.util.List;

public record OrderPlaced(long orderId, List<OrderLine> orderLines) implements Serializable {
}
