package ddd.domain;

public class OrderLineFactory {

    private final ProductRepository productRepository;

    public OrderLineFactory(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public OrderLine create(long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다"));
        return new OrderLine(product.getId(), product.getName(), product.getPrice(), quantity);
    }
}
