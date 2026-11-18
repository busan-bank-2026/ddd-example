package ddd.catalog.application;

import java.util.List;

import ddd.catalog.domain.Product;
import ddd.catalog.domain.ProductRepository;

public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product findProduct(long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다"));
    }

    public List<Product> listProducts() {
        return productRepository.findAll();
    }

    public void decreaseStock(long productId, int quantity) {
        Product product = findProduct(productId);
        product.decreaseStock(quantity);
        productRepository.save(product);
    }
}
