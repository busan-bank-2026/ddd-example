package ddd;

import ddd.shipping.application.OrderPaidMessageHandler;
import ddd.shipping.domain.Shipment;
import ddd.shipping.domain.ShipmentRepository;
import ddd.shipping.infrastructure.MemoryShipmentRepository;
import ddd.shipping.infrastructure.SocketMessageReceiver;

public class ShippingApp {

    public static void main(String[] args) {
        ShipmentRepository shipmentRepository = new MemoryShipmentRepository();
        OrderPaidMessageHandler orderPaidMessageHandler = new OrderPaidMessageHandler(shipmentRepository);

        SocketMessageReceiver receiver = new SocketMessageReceiver(9090);
        System.out.println("배송 앱이 9090번 포트에서 메시지를 기다립니다");
        String message = receiver.receive();
        System.out.println("받은 메시지: " + message);
        orderPaidMessageHandler.handle(message);

        Shipment shipment = shipmentRepository.findByOrderId(1).orElseThrow();
        System.out.println("배송 컨텍스트: " + shipment.getOrderId() + "번 주문, " + shipment.getAddress());
    }
}
