package ddd.catalog.domain;

import java.util.Optional;

public interface ProductRepository {

    void save(Product product);

    Optional<Product> findById(long id);
}
