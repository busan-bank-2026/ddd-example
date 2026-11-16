package ddd.catalog.application;

import ddd.catalog.domain.Product;
import ddd.catalog.domain.ProductRepository;
import ddd.order.domain.OrderCanceled;
import ddd.order.domain.OrderLine;

public class IncreaseStockHandler {

    private final ProductRepository productRepository;

    public IncreaseStockHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void handle(OrderCanceled event) {
        for (OrderLine orderLine : event.orderLines()) {
            Product product = productRepository.findById(orderLine.productId()).orElseThrow();
            product.increaseStock(orderLine.quantity());
            productRepository.save(product);
        }
    }
}
