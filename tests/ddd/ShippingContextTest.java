package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ddd.catalog.domain.Product;
import ddd.catalog.domain.ProductRepository;
import ddd.catalog.infrastructure.MemoryProductRepository;
import ddd.member.domain.Member;
import ddd.member.domain.MemberGrade;
import ddd.member.domain.MemberRepository;
import ddd.member.infrastructure.MemoryMemberRepository;
import ddd.order.application.EventPublisher;
import ddd.order.application.MessageSender;
import ddd.order.application.OrderPaidNotifier;
import ddd.order.application.OrderService;
import ddd.order.application.OrderItemRequest;
import ddd.order.application.OrderRequest;
import ddd.order.domain.Address;
import ddd.order.domain.DiscountPolicy;
import ddd.order.domain.Order;
import ddd.order.domain.OrderItem;
import ddd.order.domain.OrderItemFactory;
import ddd.order.domain.OrderPlaced;
import ddd.order.infrastructure.LegacyPaymentAdapter;
import ddd.order.infrastructure.MemoryOrderRepository;
import ddd.shipping.application.OrderPaidMessageHandler;
import ddd.shipping.domain.Shipment;
import ddd.shipping.domain.ShipmentRepository;
import ddd.shipping.infrastructure.MemoryShipmentRepository;
import legacypay.LegacyPayGateway;

class ShippingContextTest {

    private static OrderService service(EventPublisher publisher, MessageSender messageSender) {
        ProductRepository products = new MemoryProductRepository();
        products.save(new Product(1, "자바 책", 15000, 10));
        MemberRepository members = new MemoryMemberRepository();
        members.save(new Member(1, "김철수", MemberGrade.BASIC));
        return new OrderService(new MemoryOrderRepository(), new OrderItemFactory(products), members,
                new DiscountPolicy(), publisher, new LegacyPaymentAdapter(new LegacyPayGateway()), messageSender);
    }

    private String section(String text, String heading) {
        int start = text.indexOf("## " + heading);
        assertTrue(start >= 0, heading + " 절이 없습니다");
        int end = text.indexOf("\n## ", start + 1);
        return end < 0 ? text.substring(start) : text.substring(start, end);
    }

    private boolean hasLine(String text, String... words) {
        return text.lines().anyMatch(line -> List.of(words).stream().allMatch(line::contains));
    }

    @Test
    @DisplayName("S1 결제하기가 끝나면 주문 컨텍스트의 공개 호스트 서비스인 결제 완료 알리미가 결제 완료 메시지를 정해진 한 줄 글자 ORDER_PAID|orderId=주문 번호|address=배송 주소로 만들어 메시지 보내기에 넘긴다. Main은 보낸 메시지와 주문 상태를 전 -> 후로 보여준다")
    void s1() throws Exception {
        assertEquals("ddd.order.application",
                Class.forName("ddd.order.application.OrderPaidNotifier").getPackageName());

        EventPublisher publisher = new EventPublisher();
        List<String> messages = new ArrayList<>();
        List<OrderPlaced> placed = new ArrayList<>();
        publisher.subscribe(event -> {
            if (event instanceof OrderPlaced orderPlaced) {
                placed.add(orderPlaced);
            }
        });
        OrderService service = service(publisher, messages::add);

        long orderId = service.placeOrder(new OrderRequest(1, List.of(new OrderItemRequest(1, 2)), "서울시 강남구"));
        publisher.processEvents();
        assertEquals(List.of(), messages);
        assertEquals(1, placed.size());

        service.pay(orderId);
        assertEquals(List.of("ORDER_PAID|orderId=" + orderId + "|address=서울시 강남구"), messages);
        publisher.processEvents();
        assertEquals(1, messages.size());
        assertEquals(1, placed.size());

        List<String> notified = new ArrayList<>();
        Order order = new Order(4, 1, List.of(new OrderItem(1, "자바 책", 15000, 1)), new Address("광주시 북구"));
        new OrderPaidNotifier(notified::add).notifyPaid(order);
        assertEquals(List.of("ORDER_PAID|orderId=4|address=광주시 북구"), notified);
    }

    @Test
    @DisplayName("S2 컨텍스트 맵에 공개 호스트 서비스와 발행된 언어 절을 더해 결제 완료 메시지의 모양을 적고, 용어집에 결제 완료 알리미, 메시지 보내기, 결제 완료 메시지를 더한다")
    void s2() throws Exception {
        String host = section(Files.readString(Path.of("context-map.md")), "공개 호스트 서비스와 발행된 언어");
        assertTrue(host.contains("OrderPaidNotifier"));
        assertTrue(host.contains("MessageSender"));
        assertTrue(host.contains("ORDER_PAID|orderId=1|address=서울시 강남구"));
        assertTrue(hasLine(host, "| ORDER_PAID |"));
        assertTrue(hasLine(host, "| orderId |", "주문 번호"));
        assertTrue(hasLine(host, "| address |", "배송 주소"));

        String glossary = Files.readString(Path.of("glossary.md"));
        assertTrue(glossary.contains("| 결제 완료 알리미 | OrderPaidNotifier |"));
        assertTrue(glossary.contains("| 메시지 보내기 | MessageSender |"));
        assertFalse(glossary.contains("OrderOpenHostService"));
        assertTrue(glossary.contains("| 결제 완료 메시지 | ORDER_PAID |"));
    }

    @Test
    @DisplayName("S3 배송 하위 도메인을 맡는 배송 컨텍스트 묶음 ddd.shipping의 domain에 배송과 배송 저장소, infrastructure에 메모리 배송 저장소를 둔다. 배송은 주문 번호와 배송 주소를 가진다")
    void s3() throws Exception {
        assertEquals("ddd.shipping.domain", Shipment.class.getPackageName());
        assertEquals("ddd.shipping.domain", ShipmentRepository.class.getPackageName());
        assertTrue(ShipmentRepository.class.isInterface());
        assertEquals("ddd.shipping.infrastructure", MemoryShipmentRepository.class.getPackageName());

        Shipment shipment = new Shipment(7, "부산시 해운대구");
        assertEquals(7, shipment.getOrderId());
        assertEquals("부산시 해운대구", shipment.getAddress());
        assertEquals(new Shipment(7, "다른 주소"), shipment);

        ShipmentRepository shipments = new MemoryShipmentRepository();
        assertTrue(shipments.findByOrderId(7).isEmpty());
        shipments.save(shipment);
        assertEquals("부산시 해운대구", shipments.findByOrderId(7).orElseThrow().getAddress());
    }

    @Test
    @DisplayName("S4 배송 컨텍스트의 결제 완료 메시지 처리기는 메시지 글자만 읽어 배송을 만들고 저장한다. 결제 완료가 아닌 메시지는 넘긴다. 배송 컨텍스트는 주문 컨텍스트의 클래스를 가져다 쓰지 않는다. Main은 1번 주문의 배송을 보여준다")
    void s4() throws Exception {
        assertEquals("ddd.shipping.application", OrderPaidMessageHandler.class.getPackageName());
        ShipmentRepository shipments = new MemoryShipmentRepository();
        OrderPaidMessageHandler handler = new OrderPaidMessageHandler(shipments);

        handler.handle("ORDER_PAID|orderId=7|address=부산시 해운대구");
        handler.handle("ORDER_CANCELED|orderId=8|address=대구시 수성구");
        assertEquals("부산시 해운대구", shipments.findByOrderId(7).orElseThrow().getAddress());
        assertTrue(shipments.findByOrderId(8).isEmpty());

        ShipmentRepository serviceShipments = new MemoryShipmentRepository();
        OrderService service = service(new EventPublisher(), new OrderPaidMessageHandler(serviceShipments)::handle);
        long orderId = service.placeOrder(new OrderRequest(1, List.of(new OrderItemRequest(1, 1)), "제주시 연동"));
        assertTrue(serviceShipments.findByOrderId(orderId).isEmpty());
        service.pay(orderId);
        assertEquals("제주시 연동", serviceShipments.findByOrderId(orderId).orElseThrow().getAddress());

        int files = 0;
        try (Stream<Path> paths = Files.walk(Path.of("src/ddd/shipping"))) {
            for (Path path : paths.filter(Files::isRegularFile).toList()) {
                files++;
                String source = Files.readString(path);
                assertTrue(source.startsWith("package ddd.shipping."));
                assertFalse(source.contains("ddd.order"));
            }
        }
        assertEquals(5, files);
    }

    @Test
    @DisplayName("S5 컨텍스트 맵의 하위 도메인 표에 배송을, 컨텍스트 사이 표에 배송이 주문의 결제 완료 메시지를 읽는 줄을 더하고, 용어집에 배송, 배송 저장소, 결제 완료 메시지 처리기를 더한다")
    void s5() throws Exception {
        String contextMap = Files.readString(Path.of("context-map.md"));
        assertTrue(hasLine(section(contextMap, "하위 도메인"), "| 배송 |", "배송 컨텍스트", "ddd.shipping"));
        assertTrue(hasLine(section(contextMap, "컨텍스트 사이"), "| 배송 |", "| 주문 |", "결제 완료 메시지"));

        String glossary = Files.readString(Path.of("glossary.md"));
        assertTrue(glossary.contains("| 배송 | Shipment |"));
        assertTrue(glossary.contains("| 배송 저장소 | ShipmentRepository |"));
        assertTrue(glossary.contains("| 결제 완료 메시지 처리기 | OrderPaidMessageHandler |"));
    }
}
