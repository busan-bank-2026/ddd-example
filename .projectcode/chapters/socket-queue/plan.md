# socket-queue 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/shipping/infrastructure/SocketMessageReceiver.java | 새로 | 소켓 받는 곳. 포트를 열고 메시지 한 줄을 받는다 |
| ddd/src/ddd/ShippingApp.java | 새로 | 배송 앱. 받는 쪽 main |
| ddd/src/ddd/order/application/SocketEventPublisher.java | 새로 | 소켓 발행기. 이벤트 발행기를 이어받아 결제 완료 메시지를 소켓으로 보낸다 |
| ddd/src/ddd/OrderApp.java | 새로 | 주문 앱. 보내는 쪽 main |
| tests/ddd/SocketQueueTest.java | 새로 | S1부터 S4까지 행동 테스트 |
| tests/ddd 아래 앞 장 테스트 네 개 | 고침 | ddd 묶음의 파일 목록, 배송 컨텍스트 파일 수, legacypay를 쓰는 파일 목록 |

## 코드 계획
모든 파일이 붙여넣기 코드다. 학생은 완성본을 복사하고, 강의는 바꿔 끼운 자리만 읽는다.

SocketMessageReceiver.java (T1, 배송 인프라 계층)
- 생성자(int port)에서 new ServerSocket(port)를 연다. 만들고 나면 바로 기다리는 중이다
- receive(): try 자원으로 서버 소켓, accept 한 소켓, UTF-8 BufferedReader를 열고 readLine 한 줄을 돌려준다. 끝나면 포트가 닫힌다
- IOException은 UncheckedIOException으로 감싼다
- 인프라에 두는 이유: 소켓은 바깥과 잇는 기술이다. 배송 도메인과 결제 완료 메시지 처리기는 소켓을 모른다

ShippingApp.java (T1, ddd 묶음)
- 메모리 배송 저장소와 결제 완료 메시지 처리기를 Main과 같은 모양으로 만든다
- new SocketMessageReceiver(9090)을 만든 뒤 배송 앱이 9090번 포트에서 메시지를 기다립니다를 찍는다
- receive()로 받은 글자를 받은 메시지: 로 찍고 orderPaidMessageHandler.handle(message)에 넘긴다
- findByOrderId(1)로 배송을 꺼내 배송 컨텍스트: 줄을 Main과 같은 모양으로 찍는다
- main을 따로 두는 이유: 받는 쪽과 보내는 쪽이 다른 프로그램(다른 JVM)이어야 소켓으로 잇는 것이 보인다

SocketEventPublisher.java (T2, 주문 응용 계층)
- extends EventPublisher. 생성자(String host, int port)
- publish(List<?> events)를 고쳐 쓴다. 이벤트마다 글자면 send(message), 아니면 super.publish(List.of(event))
- send(message): try 자원으로 new Socket(host, port)와 UTF-8 PrintWriter를 열고 println 한 줄을 보낸다. IOException은 UncheckedIOException으로 감싼다
- 이어받는 이유: 주문 응용 서비스와 공개 호스트 서비스는 EventPublisher를 받는다. 이어받은 발행기는 그 자리에 그대로 들어가서 두 클래스를 고치지 않는다
- 이벤트 발행기 옆(응용 계층)에 두는 이유: 인프라에 두면 응용 계층의 이벤트 발행기를 import해야 하고, 인프라는 응용을 쓰지 않는다는 layers 장 규칙에 걸린다
- 글자만 보내는 이유: 소켓 너머로는 발행된 언어(결제 완료 메시지 글자)만 보낸다. 주문 접수됨 같은 자바 객체는 같은 프로그램 안에서만 쓴다

OrderApp.java (T2, ddd 묶음)
- 주문 앱을 시작합니다를 찍는다
- 회원 김철수(VIP), 상품 자바 책과 볼펜, 메모리 주문 저장소를 Main과 같은 모양으로 만든다
- EventPublisher eventPublisher = new SocketEventPublisher("localhost", 9090)
- 주문 응용 서비스를 Main과 같은 인자로 만든다. 다른 것은 발행기 한 줄뿐이다
- 자바 책 2개와 볼펜 1개를 서울시 강남구로 주문하고 결제한 뒤 결제: 줄과 결제 완료 메시지를 배송 앱으로 보냈습니다를 찍는다

SocketQueueTest.java
- 패키지는 ddd다
- S1은 테스트가 연 서버 소켓(빈 포트)으로 보내는 소켓 발행기를 주문 응용 서비스에 넣는다. 주문하면 주문 접수됨은 보내지 않고 처리할 때 구독자가 받는다. 결제하면 서버 소켓이 결제 완료 메시지 한 줄을 받는다
- S2는 빈 포트로 소켓 받는 곳을 만들고 클라이언트 소켓으로 한 줄을 보내면 receive가 그 글자를 돌려주고, 그 뒤 같은 포트를 다시 열 수 있는지 본다
- S3은 배송 앱 main을 서버 스레드로 띄우고 기다립니다 줄이 나온 뒤 클라이언트 소켓으로 결제 완료 메시지를 보내 세 줄을 본다. 배송 앱 소스가 ddd.order를 쓰지 않는지 본다
- S4는 배송 앱을 서버 스레드로 띄운 뒤 주문 앱 main을 실행해 두 앱의 줄을 보고, 주문 앱 소스에 발행기 한 줄이 있는지, Main 출력이 앞 장 그대로인지 본다
- 출력은 System.setOut으로 모은다. 두 앱이 함께 찍으므로 줄 순서는 앱마다 따로 본다

앞 장 테스트
- LayersTest S4와 BoundedContextsTest S5의 ddd 묶음 파일 목록에 OrderApp.java와 ShippingApp.java를 더한다
- ShippingContextTest S4의 배송 컨텍스트 파일 수를 5로 바꾼다
- PaymentAclTest S2의 legacypay를 쓰는 파일 목록에 OrderApp.java를 더한다

## 핵심 코드
EventPublisher eventPublisher = new SocketEventPublisher("localhost", 9090);
OrderApplicationService orderService = new OrderApplicationService(new MemoryOrderRepository(),
        new OrderItemFactory(productRepository), memberRepository, new DiscountPolicy(), eventPublisher,
        new LegacyPaymentAdapter(new LegacyPayGateway()));

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

String message = receiver.receive();
orderPaidMessageHandler.handle(message);

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | SocketEventPublisher.java |
| S2 | SocketMessageReceiver.java |
| S3 | ShippingApp.java |
| S4 | OrderApp.java |

## 라이브러리
- 추가 없음. java.net의 Socket과 ServerSocket은 자바 기본이다

## 포트와 실행
- 포트는 9090 하나다. contract.md 기술 표에 적는다
- 이클립스에서 ShippingApp을 먼저 Run하고, 기다립니다 줄이 보이면 OrderApp을 Run한다. contract.md 이클립스 절에 적는다
- Main에서 닿지 않는 main이 둘 생긴다. contract.md 테스트 명령의 메인 소스 컴파일을 src/ddd/*.java로 바꾼다. [projectcode] 커밋으로 한다

## 다른 장 파일
- tests/ddd/LayersTest.java: layers
- tests/ddd/BoundedContextsTest.java: bounded-contexts
- tests/ddd/ShippingContextTest.java: shipping-context
- tests/ddd/PaymentAclTest.java: payment-acl
