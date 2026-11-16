# socket-queue 코드 요약

## T1 소켓으로 메시지를 받는 배송 앱
커밋: dbf254d

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/ShippingApp.java | 새로 |
| ddd/src/ddd/shipping/infrastructure/SocketMessageReceiver.java | 새로 |

```java
// ddd/src/ddd/ShippingApp.java
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
```

```java
// ddd/src/ddd/shipping/infrastructure/SocketMessageReceiver.java
package ddd.shipping.infrastructure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketMessageReceiver {

    private final ServerSocket serverSocket;

    public SocketMessageReceiver(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public String receive() {
        try (ServerSocket server = serverSocket;
                Socket socket = server.accept();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
```

## T2 소켓 발행기로 바꿔 끼운 주문 앱
커밋: 2799af1

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/OrderApp.java | 새로 |
| ddd/src/ddd/order/application/SocketEventPublisher.java | 새로 |

```java
// ddd/src/ddd/OrderApp.java
package ddd;

import java.util.List;

import ddd.catalog.domain.Product;
import ddd.catalog.domain.ProductRepository;
import ddd.catalog.infrastructure.MemoryProductRepository;
import ddd.member.domain.Member;
import ddd.member.domain.MemberGrade;
import ddd.member.domain.MemberRepository;
import ddd.member.infrastructure.MemoryMemberRepository;
import ddd.order.application.EventPublisher;
import ddd.order.application.OrderApplicationService;
import ddd.order.application.OrderLineCommand;
import ddd.order.application.PlaceOrderCommand;
import ddd.order.application.SocketEventPublisher;
import ddd.order.domain.DiscountPolicy;
import ddd.order.domain.OrderLineFactory;
import ddd.order.infrastructure.LegacyPaymentAdapter;
import ddd.order.infrastructure.MemoryOrderRepository;
import ddd.shared.Money;
import legacypay.LegacyPayGateway;

public class OrderApp {

    public static void main(String[] args) {
        System.out.println("주문 앱을 시작합니다");

        MemberRepository memberRepository = new MemoryMemberRepository();
        memberRepository.save(new Member(1, "김철수", MemberGrade.VIP));

        ProductRepository productRepository = new MemoryProductRepository();
        productRepository.save(new Product(1, "자바 책", new Money(15000), 10));
        productRepository.save(new Product(2, "볼펜", new Money(1000), 10));

        EventPublisher eventPublisher = new SocketEventPublisher("localhost", 9090);
        OrderApplicationService orderService = new OrderApplicationService(new MemoryOrderRepository(),
                new OrderLineFactory(productRepository), memberRepository, new DiscountPolicy(), eventPublisher,
                new LegacyPaymentAdapter(new LegacyPayGateway()));

        List<OrderLineCommand> orderLines = List.of(
                new OrderLineCommand(1, 2),
                new OrderLineCommand(2, 1));
        long orderId = orderService.placeOrder(new PlaceOrderCommand(1, orderLines, "서울시 강남구"));
        orderService.pay(orderId);
        System.out.println("결제: " + orderId + "번 주문, " + orderService.findOrder(orderId).getStatus().label());
        System.out.println("결제 완료 메시지를 배송 앱으로 보냈습니다");
    }
}
```

```java
// ddd/src/ddd/order/application/SocketEventPublisher.java
package ddd.order.application;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SocketEventPublisher extends EventPublisher {

    private final String host;
    private final int port;

    public SocketEventPublisher(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void publish(List<?> events) {
        for (Object event : events) {
            if (event instanceof String message) {
                send(message);
            } else {
                super.publish(List.of(event));
            }
        }
    }

    private void send(String message) {
        try (Socket socket = new Socket(host, port);
                PrintWriter writer = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            writer.println(message);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
```
