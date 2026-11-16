package ddd.shipping.application;

import ddd.shipping.domain.Shipment;
import ddd.shipping.domain.ShipmentRepository;

public class OrderPaidMessageHandler {

    private final ShipmentRepository shipmentRepository;

    public OrderPaidMessageHandler(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    public void handle(String message) {
        String[] fields = message.split("\\|");
        if (!fields[0].equals("ORDER_PAID")) {
            return;
        }
        long orderId = Long.parseLong(fields[1].substring("orderId=".length()));
        String address = fields[2].substring("address=".length());
        shipmentRepository.save(new Shipment(orderId, address));
    }
}
