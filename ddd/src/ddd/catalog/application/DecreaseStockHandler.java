package ddd.catalog.application;

import ddd.catalog.domain.Product;
import ddd.catalog.domain.ProductRepository;
import ddd.domain.OrderLine;
import ddd.domain.OrderPlaced;

public class DecreaseStockHandler {

    private final ProductRepository productRepository;

    public DecreaseStockHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void handle(OrderPlaced event) {
        for (OrderLine orderLine : event.orderLines()) {
            Product product = productRepository.findById(orderLine.productId()).orElseThrow();
            product.decreaseStock(orderLine.quantity());
            productRepository.save(product);
        }
    }
}
