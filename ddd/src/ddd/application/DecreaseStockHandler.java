package ddd.application;

import ddd.domain.OrderLine;
import ddd.domain.OrderPlaced;
import ddd.domain.Product;
import ddd.domain.ProductRepository;

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
