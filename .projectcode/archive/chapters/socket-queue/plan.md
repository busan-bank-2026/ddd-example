# socket-queue 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/shipping/infrastructure/SocketMessageReceiver.java | 새로 | 소켓 받는 곳. 포트를 열고 메시지 한 줄을 받는다 |
| ddd/src/ddd/ShippingApp.java | 새로 | 배송 앱. 받는 쪽 main |
| ddd/src/ddd/order/infrastructure/SocketMessageSender.java | 새로 | 소켓 메시지 보내기. 메시지 보내기를 구현해 결제 완료 메시지를 소켓으로 보낸다 |
| ddd/src/ddd/OrderApp.java | 새로 | 주문 앱. 보내는 쪽 main |
| tests/ddd/SocketQueueTest.java | 새로 | S1부터 S4까지 행동 테스트 |

## 코드 계획
모든 파일이 붙여넣기 코드다. 학생은 완성본을 복사하고, 강의는 바꿔 끼운 자리만 읽는다.

SocketMessageReceiver.java (T1, 배송 인프라 계층)
- 생성자(int port)에서 new ServerSocket(port)를 연다. 만들고 나면 바로 기다리는 중이다
- receive(): try 자원으로 서버 소켓, accept 한 소켓, UTF-8 BufferedReader를 열고 readLine 한 줄을 돌려준다. 끝나면 포트가 닫힌다
- IOException은 UncheckedIOException으로 감싼다
- 인프라에 두는 이유: 소켓은 바깥과 잇는 기술이다. 배송 도메인과 결제 완료 메시지 처리기는 소켓을 모른다

ShippingApp.java (T1, ddd 묶음)
- 첫 줄에 ===== 배송 앱 =====을 찍는다
- 메모리 배송 저장소와 결제 완료 메시지 처리기를 만든다
- new SocketMessageReceiver(9090)을 만든 뒤 배송 앱이 9090번 포트에서 메시지를 기다립니다를 찍는다
- receive()로 받은 글자를 받은 메시지: 로 찍고 orderPaidMessageHandler.handle(message)에 넘긴다
- findByOrderId(1)로 배송을 꺼내 배송 컨텍스트: 1번 주문, 서울시 강남구 모양으로 찍는다
- main을 따로 두는 이유: 받는 쪽과 보내는 쪽이 다른 프로그램(다른 JVM)이어야 소켓으로 잇는 것이 보인다

SocketMessageSender.java (T2, 주문 인프라 계층)
- implements MessageSender. 생성자(String host, int port)
- send(message): try 자원으로 new Socket(host, port)와 UTF-8 PrintWriter를 열고 println 한 줄을 보낸다. IOException은 UncheckedIOException으로 감싼다
- 구현하는 이유: 주문 서비스는 메시지 보내기 MessageSender를 받는다. 구현을 바꿔 넣기만 하면 주문 서비스와 결제 완료 알리미는 고치지 않는다
- 인프라에 두는 이유: 소켓은 바깥과 잇는 기술이다. 응용 계층의 MessageSender를 인프라가 구현하는 것은 결제 어댑터가 결제 창구를 구현하는 것과 같은 방향이다
- 글자만 보내는 이유: 소켓 너머로는 발행된 언어(결제 완료 메시지 글자)만 보낸다. 주문 접수됨 같은 자바 객체는 같은 프로그램 안 이벤트 발행기에 남는다

OrderApp.java (T2, ddd 묶음)
- 첫 줄에 ===== 주문 앱 =====을 찍는다
- 회원 1 김철수 VIP, 상품 1 자바 책 15000원과 2 볼펜 1000원, 메모리 주문 저장소, 이벤트 발행기를 만든다
- 주문 서비스의 마지막 인자, 메시지 보내기 자리에 new SocketMessageSender("localhost", 9090)을 넣는다. 결제 창구는 new LegacyPaymentAdapter(new LegacyPayGateway())다
- 자바 책 2개와 볼펜 1개를 서울시 강남구로 주문하고 결제한 뒤 결제: 1번 주문, 결제 완료와 결제 완료 메시지를 배송 앱으로 보냈습니다를 찍는다

SocketQueueTest.java
- 패키지는 ddd다
- S1은 소켓 메시지 보내기가 MessageSender를 구현하고 ddd.order.infrastructure에 있는지 본다. 테스트가 연 서버 소켓(빈 포트)으로 보내는 소켓 메시지 보내기를 주문 서비스에 넣고, 주문하면 주문 접수됨은 보내지 않고 결제하면 서버 소켓이 결제 완료 메시지 한 줄을 받는지 본다
- S2는 빈 포트로 소켓 받는 곳을 만들고 클라이언트 소켓으로 한 줄을 보내면 receive가 그 글자를 돌려주고, 그 뒤 같은 포트를 다시 열 수 있는지 본다
- S3은 배송 앱 main을 서버 스레드로 띄우고 기다립니다 줄이 나온 뒤 클라이언트 소켓으로 결제 완료 메시지를 보내 출력을 본다. 배송 앱 소스가 ddd.order를 쓰지 않는지 본다
- S4는 배송 앱을 서버 스레드로 띄운 뒤 주문 앱 main을 실행해 두 앱의 줄을 보고, 주문 앱 소스에 new SocketMessageSender("localhost", 9090)이 있는지 본다
- 출력은 System.setOut으로 모은다. 두 앱이 함께 찍으므로 줄 순서는 앱마다 따로 본다

## 핵심 코드
OrderService orderService = new OrderService(new MemoryOrderRepository(),
        new OrderItemFactory(productRepository), memberRepository, new DiscountPolicy(), eventPublisher,
        new LegacyPaymentAdapter(new LegacyPayGateway()), new SocketMessageSender("localhost", 9090));

@Override
public void send(String message) {
    try (Socket socket = new Socket(host, port);
            PrintWriter writer = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
        writer.println(message);
    } catch (IOException e) {
        throw new UncheckedIOException(e);
    }
}

String message = receiver.receive();
orderPaidMessageHandler.handle(message);

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | SocketMessageSender.java |
| S2 | SocketMessageReceiver.java |
| S3 | ShippingApp.java |
| S4 | OrderApp.java |

## 라이브러리
- 추가 없음. java.net의 Socket과 ServerSocket은 자바 기본이다

## 포트와 실행
- 포트는 9090 하나다. contract.md 기술 표에 적는다
- 이클립스에서 ShippingApp을 먼저 Run하고, 기다립니다 줄이 보이면 OrderApp을 Run한다. contract.md 이클립스 절에 적는다
- Main에서 닿지 않는 main이 둘 생긴다. contract.md 테스트 명령은 src 아래 자바 파일을 모두 컴파일한다

## 다른 장 파일
- 없음. 학생 코드는 새 파일 넷뿐이다
