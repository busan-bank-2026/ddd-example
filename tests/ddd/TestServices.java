package ddd;

import ddd.catalog.application.DecreaseStockHandler;
import ddd.catalog.application.ProductService;
import ddd.catalog.infrastructure.MemoryProductRepository;
import ddd.order.application.EventPublisher;
import ddd.order.domain.OrderPlaced;

final class TestServices {

    static EventPublisher publisher() {
        return new EventPublisher(new DecreaseStockHandler(new ProductService(new MemoryProductRepository())) {
            @Override
            public void handle(OrderPlaced event) {
            }
        });
    }
}
