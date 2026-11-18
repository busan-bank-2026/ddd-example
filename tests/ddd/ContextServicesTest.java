package ddd;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import ddd.catalog.application.ProductService;
import ddd.catalog.domain.Product;
import ddd.catalog.domain.ProductRepository;
import ddd.catalog.infrastructure.MemoryProductRepository;
import ddd.member.application.MemberService;
import ddd.member.domain.Member;
import ddd.member.domain.MemberGrade;
import ddd.member.infrastructure.MemoryMemberRepository;

class ContextServicesTest {

    private static class CopyingProductRepository implements ProductRepository {

        private final MemoryProductRepository storage = new MemoryProductRepository();

        private Product copy(Product product) {
            return new Product(product.getId(), product.getName(), product.getPrice(), product.getStock());
        }

        @Override
        public void save(Product product) {
            storage.save(copy(product));
        }

        @Override
        public Optional<Product> findById(long id) {
            return storage.findById(id).map(this::copy);
        }

        @Override
        public List<Product> findAll() {
            return storage.findAll().stream().map(this::copy).toList();
        }
    }

    @Test
    void memberServiceListsAndFindsMembers() {
        var repository = new MemoryMemberRepository();
        var service = new MemberService(repository);
        assertTrue(service.listMembers().isEmpty());
        repository.save(new Member(1, "김철수", MemberGrade.BASIC));
        repository.save(new Member(2, "이영희", MemberGrade.VIP));
        assertEquals(2, service.listMembers().size());
        assertEquals("김철수", service.findMember(1).getName());
        assertThrows(IllegalArgumentException.class, () -> service.findMember(99));
    }

    @Test
    void productServiceListsFindsAndSavesDecreasedStock() {
        var repository = new MemoryProductRepository();
        var service = new ProductService(repository);
        assertTrue(service.listProducts().isEmpty());
        repository.save(new Product(1, "자바 책", 15000, 10));
        repository.save(new Product(2, "볼펜", 1000, 20));
        assertEquals(2, service.listProducts().size());
        service.decreaseStock(1, 3);
        assertEquals(7, service.findProduct(1).getStock());
        assertEquals(20, service.findProduct(2).getStock());
        assertThrows(IllegalArgumentException.class, () -> service.findProduct(99));
    }

    @Test
    void productServiceSavesStockAfterChangingARepositoryCopy() {
        var repository = new CopyingProductRepository();
        repository.save(new Product(1, "자바 책", 15000, 10));
        var service = new ProductService(repository);

        service.decreaseStock(1, 3);

        assertEquals(7, repository.findById(1).orElseThrow().getStock());
    }
}
