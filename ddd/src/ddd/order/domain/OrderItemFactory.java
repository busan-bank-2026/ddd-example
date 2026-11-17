package ddd.order.domain;

import ddd.catalog.domain.Product;
import ddd.catalog.domain.ProductRepository;

public class OrderItemFactory {

    private final ProductRepository productRepository;

    public OrderItemFactory(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public OrderItem create(long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다"));
        return new OrderItem(product.getId(), product.getName(), product.getPrice(), quantity);
    }
}
