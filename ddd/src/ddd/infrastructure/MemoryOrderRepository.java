package ddd.infrastructure;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ddd.domain.Order;
import ddd.domain.OrderRepository;

public class MemoryOrderRepository implements OrderRepository {

    private final Map<Long, Order> orders = new HashMap<>();

    @Override
    public void save(Order order) {
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
