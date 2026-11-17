package ddd.catalog.application;

import ddd.catalog.domain.Product;
import ddd.catalog.domain.ProductRepository;
import ddd.order.application.EventHandler;
import ddd.order.domain.OrderItem;
import ddd.order.domain.OrderPlaced;

public class DecreaseStockHandler implements EventHandler {

    private final ProductRepository productRepository;

    public DecreaseStockHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void handle(Object event) {
        if (!(event instanceof OrderPlaced placed)) {
            return;
        }
        for (OrderItem item : placed.items()) {
            Product product = productRepository.findById(item.getProductId()).orElseThrow();
            product.decreaseStock(item.getQuantity());
            productRepository.save(product);
        }
    }
}
