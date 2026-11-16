package ddd.catalog.infrastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ddd.catalog.domain.Product;
import ddd.catalog.domain.ProductRepository;

public class MemoryProductRepository implements ProductRepository {

    private final Map<Long, Product> products = new HashMap<>();

    @Override
    public void save(Product product) {
        products.put(product.getId(), product);
    }

    @Override
    public Optional<Product> findById(long id) {
        return Optional.ofNullable(products.get(id));
    }
}
