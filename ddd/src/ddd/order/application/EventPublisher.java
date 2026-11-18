package ddd.order.application;

import ddd.catalog.application.DecreaseStockHandler;
import ddd.order.domain.OrderPlaced;

public class EventPublisher {

    private final DecreaseStockHandler handler;

    public EventPublisher(DecreaseStockHandler handler) {
        this.handler = handler;
    }

    public void publish(OrderPlaced event) {
        handler.handle(event);
    }
}
