package ddd.order.infrastructure;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        orders.put(order.getId(), copy(order));
    }

    @Override
    public Optional<Order> findById(long id) {
        Order order = orders.get(id);
        if (order == null) {
            return Optional.empty();
        }
        return Optional.of(copy(order));
    }

    @Override
    public List<Order> findByMemberId(long memberId) {
        List<Order> found = new ArrayList<>();
        for (Order order : orders.values()) {
            if (order.getMemberId() == memberId) {
                found.add(copy(order));
            }
        }
        return found;
    }

    private Order copy(Order order) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bytes);
            out.writeObject(order);
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
            return (Order) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
