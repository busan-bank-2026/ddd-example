package ddd.shipping.domain;

import java.util.Optional;

public interface ShipmentRepository {

    void save(Shipment shipment);

    Optional<Shipment> findByOrderId(long orderId);
}
