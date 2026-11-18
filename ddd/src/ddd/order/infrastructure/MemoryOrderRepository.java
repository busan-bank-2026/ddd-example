package ddd.order.infrastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ddd.order.domain.Order;
import ddd.order.domain.OrderRepository;

public class MemoryOrderRepository implements OrderRepository {

    private final Map<Long, Order> orders = new HashMap<>();

    @Override
    public void save(Order order) {
        Order saved = orders.get(order.getId());
        if (saved != null && saved.getVersion() != order.getVersion()) {
            throw new IllegalStateException("다른 곳에서 먼저 바꾼 주문입니다");
        }
        order.increaseVersion();
        orders.put(order.getId(), new Order(order));
    }

    @Override
    public Optional<Order> findById(long id) {
        Order order = orders.get(id);
        if (order == null) {
            return Optional.empty();
        }
        return Optional.of(new Order(order));
    }

}
