package ddd.catalog.application;

import ddd.order.domain.OrderItem;
import ddd.order.domain.OrderPlaced;

public class DecreaseStockHandler {

    private final ProductService productService;

    public DecreaseStockHandler(ProductService productService) {
        this.productService = productService;
    }

    public void handle(OrderPlaced event) {
        for (OrderItem item : event.items()) {
            productService.decreaseStock(item.getProductId(), item.getQuantity());
        }
    }
}
