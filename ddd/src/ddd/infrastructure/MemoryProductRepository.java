package ddd.infrastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ddd.domain.Product;
import ddd.domain.ProductRepository;

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
