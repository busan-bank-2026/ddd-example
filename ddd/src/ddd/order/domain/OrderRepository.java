package ddd.order.domain;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    void save(Order order);

    Optional<Order> findById(long id);

    List<Order> findByMemberId(long memberId);
}
