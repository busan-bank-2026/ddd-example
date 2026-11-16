package ddd.order.application;

import java.util.List;

import ddd.order.domain.Order;

public class OrderOpenHostService {

    private final EventPublisher eventPublisher;

    public OrderOpenHostService(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishOrderPaid(Order order) {
        String message = "ORDER_PAID|orderId=" + order.getId() + "|address=" + order.getAddress().value();
        eventPublisher.publish(List.of(message));
    }
}
