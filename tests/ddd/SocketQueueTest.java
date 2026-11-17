package ddd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
import ddd.order.application.OrderService;
import ddd.order.application.OrderItemRequest;
import ddd.order.application.OrderRequest;
import ddd.order.domain.DiscountPolicy;
import ddd.order.domain.OrderItemFactory;
import ddd.order.domain.OrderPlaced;
import ddd.order.infrastructure.LegacyPaymentAdapter;
import ddd.order.infrastructure.MemoryOrderRepository;
import ddd.order.infrastructure.SocketMessageSender;
import ddd.shipping.infrastructure.SocketMessageReceiver;
import legacypay.LegacyPayGateway;

class SocketQueueTest {

    private static final String SHIPPING_TITLE = "===== 배송 앱 =====";
    private static final String WAITING = "배송 앱이 9090번 포트에서 메시지를 기다립니다";
    private static final String RECEIVED = "받은 메시지: ORDER_PAID|orderId=1|address=서울시 강남구";
    private static final String SHIPMENT = "배송 컨텍스트: 1번 주문, 서울시 강남구";

    private static OrderService service(EventPublisher publisher, MessageSender messageSender) {
        ProductRepository products = new MemoryProductRepository();
        products.save(new Product(1, "자바 책", 15000, 10));
        MemberRepository members = new MemoryMemberRepository();
        members.save(new Member(1, "김철수", MemberGrade.VIP));
        return new OrderService(new MemoryOrderRepository(), new OrderItemFactory(products), members,
                new DiscountPolicy(), publisher, new LegacyPaymentAdapter(new LegacyPayGateway()), messageSender);
    }

    private static int freePort() throws Exception {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    private static void send(int port, String message) throws Exception {
        try (Socket socket = new Socket("localhost", port);
                PrintWriter writer = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            writer.println(message);
        }
    }

    private static List<String> lines(ByteArrayOutputStream out) {
        return List.of(out.toString(StandardCharsets.UTF_8).split(System.lineSeparator()));
    }

    private static void waitForLine(ByteArrayOutputStream out, String line) throws Exception {
        for (int i = 0; i < 250; i++) {
            if (lines(out).contains(line)) {
                return;
            }
            Thread.sleep(20);
        }
        throw new AssertionError(line + " 줄이 나오지 않았습니다");
    }

    private static Thread startShippingApp(AtomicReference<Throwable> failure) {
        Thread server = new Thread(() -> ShippingApp.main(new String[0]));
        server.setDaemon(true);
        server.setUncaughtExceptionHandler((thread, error) -> failure.set(error));
        server.start();
        return server;
    }

    private static List<String> only(List<String> lines, List<String> wanted) {
        return lines.stream().filter(wanted::contains).toList();
    }

    @Test
    @DisplayName("S1 소켓 발행기는 이벤트 발행기 자리에 그대로 들어간다. 결제 완료 메시지 글자는 받는 쪽 포트로 소켓을 열어 한 줄로 보내고, 주문 접수됨 같은 다른 이벤트는 지금처럼 발행기 안에 모아 두었다가 처리할 때 구독자에게 넘긴다")
    void s1() throws Exception {
        assertTrue(MessageSender.class.isAssignableFrom(SocketMessageSender.class));
        assertEquals("ddd.order.infrastructure", SocketMessageSender.class.getPackageName());
        try (ServerSocket server = new ServerSocket(0)) {
            EventPublisher publisher = new EventPublisher();
            List<OrderPlaced> placed = new ArrayList<>();
            publisher.subscribe(event -> {
                if (event instanceof OrderPlaced orderPlaced) {
                    placed.add(orderPlaced);
                }
            });
            OrderService service = service(publisher, new SocketMessageSender("localhost", server.getLocalPort()));

            long orderId = service.placeOrder(new OrderRequest(1, List.of(new OrderItemRequest(1, 2)), "서울시 강남구"));
            assertEquals(List.of(), placed);
            publisher.processEvents();
            assertEquals(1, placed.size());
            assertEquals(orderId, placed.get(0).orderId());

            service.pay(orderId);
            server.setSoTimeout(5000);
            try (Socket socket = server.accept();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
                assertEquals("ORDER_PAID|orderId=" + orderId + "|address=서울시 강남구", reader.readLine());
                assertNull(reader.readLine());
            }
            server.setSoTimeout(300);
            assertThrows(SocketTimeoutException.class, server::accept);
            publisher.processEvents();
            assertEquals(1, placed.size());
        }
    }

    @Test
    @DisplayName("S2 소켓 받는 곳은 정해진 포트를 열고 기다리다가 들어온 메시지 한 줄을 돌려주고 포트를 닫는다")
    void s2() throws Exception {
        int port = freePort();
        SocketMessageReceiver receiver = new SocketMessageReceiver(port);
        send(port, "ORDER_PAID|orderId=7|address=제주시 연동");
        assertEquals("ORDER_PAID|orderId=7|address=제주시 연동", receiver.receive());
        try (ServerSocket again = new ServerSocket(port)) {
            assertEquals(port, again.getLocalPort());
        }
    }

    @Test
    @DisplayName("S3 배송 앱(받는 쪽 main)은 9090번 포트에서 기다린다고 알리고, 받은 메시지를 보여준 뒤 지금의 결제 완료 메시지 처리기에 넘겨 배송을 만들고 1번 주문의 배송을 보여준다")
    void s3() throws Exception {
        PrintStream original = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
        AtomicReference<Throwable> failure = new AtomicReference<>();
        try {
            Thread server = startShippingApp(failure);
            waitForLine(out, WAITING);
            assertTrue(server.isAlive());
            send(9090, "ORDER_PAID|orderId=1|address=서울시 강남구");
            server.join(5000);
            assertFalse(server.isAlive());
        } finally {
            System.setOut(original);
        }
        assertNull(failure.get());
        assertEquals(List.of(SHIPPING_TITLE, WAITING, RECEIVED, SHIPMENT), lines(out));

        String source = Files.readString(Path.of("src/ddd/ShippingApp.java"));
        assertTrue(source.contains("OrderPaidMessageHandler"));
        assertTrue(source.contains("new SocketMessageReceiver(9090)"));
        assertFalse(source.contains("ddd.order"));
    }

    @Test
    @DisplayName("S4 주문 앱(보내는 쪽 main)은 이벤트 발행기 자리에 9090번 포트로 보내는 소켓 발행기를 넣어 주문 응용 서비스를 만들고, 김철수가 자바 책 2개와 볼펜 1개를 서울시 강남구로 주문하고 결제한다. 결제 완료 메시지는 다른 프로그램인 배송 앱이 받는다")
    void s4() throws Exception {
        PrintStream original = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
        AtomicReference<Throwable> failure = new AtomicReference<>();
        try {
            Thread server = startShippingApp(failure);
            waitForLine(out, WAITING);
            OrderApp.main(new String[0]);
            server.join(5000);
            assertFalse(server.isAlive());
        } finally {
            System.setOut(original);
        }
        assertNull(failure.get());
        List<String> lines = lines(out);
        assertEquals(7, lines.size());
        assertEquals(List.of(SHIPPING_TITLE, WAITING, RECEIVED, SHIPMENT),
                only(lines, List.of(SHIPPING_TITLE, WAITING, RECEIVED, SHIPMENT)));
        List<String> orderApp = List.of("===== 주문 앱 =====", "결제: 1번 주문, 결제 완료", "결제 완료 메시지를 배송 앱으로 보냈습니다");
        assertEquals(orderApp, only(lines, orderApp));

        String source = Files.readString(Path.of("src/ddd/OrderApp.java"));
        assertTrue(source.contains("EventPublisher eventPublisher = new EventPublisher();"));
        assertTrue(source.contains("new SocketMessageSender(\"localhost\", 9090)"));
        assertFalse(source.contains("processEvents"));
    }
}
