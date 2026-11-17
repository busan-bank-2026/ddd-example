package ddd.catalog.application;

import ddd.catalog.domain.Product;
import ddd.catalog.domain.ProductRepository;
import ddd.order.application.EventHandler;
import ddd.order.domain.OrderCanceled;
import ddd.order.domain.OrderItem;

public class IncreaseStockHandler implements EventHandler {

    private final ProductRepository productRepository;

    public IncreaseStockHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void handle(Object event) {
        if (!(event instanceof OrderCanceled canceled)) {
            return;
        }
        for (OrderItem item : canceled.items()) {
            Product product = productRepository.findById(item.getProductId()).orElseThrow();
            product.increaseStock(item.getQuantity());
            productRepository.save(product);
        }
    }
}
