package ddd;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import ddd.catalog.application.DecreaseStockHandler;
import ddd.catalog.application.ProductService;
import ddd.catalog.domain.Product;
import ddd.catalog.domain.ProductRepository;
import ddd.catalog.infrastructure.MemoryProductRepository;
import ddd.member.application.MemberService;
import ddd.member.domain.Member;
import ddd.member.domain.MemberGrade;
import ddd.member.infrastructure.MemoryMemberRepository;
import ddd.order.application.EventPublisher;
import ddd.order.application.OrderItemRequest;
import ddd.order.application.OrderRequest;
import ddd.order.application.OrderService;
import ddd.order.domain.DiscountPolicy;
import ddd.order.domain.OrderItem;
import ddd.order.domain.OrderItemFactory;
import ddd.order.domain.OrderPlaced;
import ddd.order.infrastructure.MemoryOrderRepository;

class OrderEventsTest {

    @Test
    void productDecreasesItsOwnStock() {
        Product product = new Product(1, "자바 책", 15000, 10);
        product.decreaseStock(2);
        assertEquals(8, product.getStock());
    }

    @Test
    void placeOrderReturnsAfterAllStockHasDecreased() {
        ProductRepository products = new MemoryProductRepository();
        products.save(new Product(1, "자바 책", 15000, 10));
        products.save(new Product(2, "볼펜", 1000, 20));
        var members = new MemoryMemberRepository();
        members.save(new Member(1, "김철수", MemberGrade.BASIC));
        var orders = new MemoryOrderRepository();
        var publisher = new EventPublisher(new DecreaseStockHandler(new ProductService(products)));
        var service = new OrderService(orders, new OrderItemFactory(products),
                new MemberService(members), new DiscountPolicy(), publisher);

        long id = service.placeOrder(new OrderRequest(1,
                List.of(new OrderItemRequest(1, 2), new OrderItemRequest(2, 3)), "서울시 강남구"));

        assertTrue(orders.findById(id).isPresent());
        assertEquals(8, products.findById(1).orElseThrow().getStock());
        assertEquals(17, products.findById(2).orElseThrow().getStock());
        service.pay(id);
        service.ship(id);
        assertEquals(8, products.findById(1).orElseThrow().getStock());
    }

    @Test
    void publisherCallsHandlerImmediatelyAndPropagatesFailure() {
        var products = new ProductService(new MemoryProductRepository());
        var publisher = new EventPublisher(new DecreaseStockHandler(products));
        var event = new OrderPlaced(7, List.of(new OrderItem(99, "없는 상품", 1000, 1)));
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> publisher.publish(event));
        assertEquals("상품이 없습니다", error.getMessage());
    }
}
