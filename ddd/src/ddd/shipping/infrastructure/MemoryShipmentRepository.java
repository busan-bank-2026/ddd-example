package ddd.shipping.infrastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ddd.shipping.domain.Shipment;
import ddd.shipping.domain.ShipmentRepository;

public class MemoryShipmentRepository implements ShipmentRepository {

    private final Map<Long, Shipment> shipments = new HashMap<>();

    @Override
    public void save(Shipment shipment) {
        shipments.put(shipment.getOrderId(), shipment);
    }

    @Override
    public Optional<Shipment> findByOrderId(long orderId) {
        return Optional.ofNullable(shipments.get(orderId));
    }
}
