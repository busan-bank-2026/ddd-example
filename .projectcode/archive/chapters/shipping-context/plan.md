# shipping-context 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/order/application/MessageSender.java | 새로 | 메시지 보내기 인터페이스. 글자 메시지 하나를 보낸다 |
| ddd/src/ddd/order/application/OrderPaidNotifier.java | 새로 | 주문 컨텍스트의 공개 호스트 서비스. 결제 완료 메시지 글자를 만들어 메시지 보내기에 넘긴다 |
| ddd/src/ddd/order/application/OrderService.java | 고침 | 메시지 보내기를 받아 결제 완료 알리미를 두고, 결제하기에서 저장 뒤 결제 완료를 알린다 |
| ddd/src/ddd/shipping/domain/Shipment.java | 새로 | 배송 애그리거트. 주문 번호와 배송 주소 |
| ddd/src/ddd/shipping/domain/ShipmentRepository.java | 새로 | 배송 저장소 인터페이스 |
| ddd/src/ddd/shipping/infrastructure/MemoryShipmentRepository.java | 새로 | HashMap 배송 저장소 |
| ddd/src/ddd/shipping/application/OrderPaidMessageHandler.java | 새로 | 결제 완료 메시지 글자를 읽어 배송을 만들고 저장한다 |
| ddd/src/ddd/Main.java | 고침 | 이 장 장면 둘을 찍는다. 메시지 보내기를 람다로 만들어 배송 컨텍스트에 잇는다 |
| ddd/context-map.md | 고침 | 공개 호스트 서비스와 발행된 언어 절, 배송 하위 도메인 줄, 컨텍스트 사이 줄 |
| ddd/glossary.md | 고침 | 결제 완료 알리미, 메시지 보내기, 결제 완료 메시지, 배송, 배송 저장소, 결제 완료 메시지 처리기 |
| tests/ddd/ShippingContextTest.java | 새로 | S1부터 S5까지 행동 테스트 |

## 코드 계획
MessageSender.java (T1, 주문 응용 계층)
- interface MessageSender { void send(String message); }
- 따로 두는 이유: 주문 컨텍스트는 메시지를 어디로 보내는지 몰라도 된다. 지금은 Main이 람다로 만들고, 뒤 장에서 소켓으로 보내는 구현으로 바꾼다

OrderPaidNotifier.java (T1, 주문 응용 계층)
- 생성자에서 MessageSender를 받는다
- notifyPaid(Order order): "ORDER_PAID|orderId=" + 주문 번호 + "|address=" + 배송 주소 글자로 메시지를 만들고 messageSender.send(message)로 넘긴다
- 따로 두는 이유: 바깥 컨텍스트에 무엇을 어떤 모양으로 알리는지 한 곳에서 정한다. 이 클래스가 주문 컨텍스트의 열린 창구다
- 메시지를 글자로 만드는 이유: 받는 쪽이 주문 클래스를 몰라도 읽는다. 메시지 모양이 발행된 언어다
- 응용 계층에 두는 이유: 주문 도메인은 바깥 알림을 모른다

OrderService.java (T1)
- 생성자 끝에 MessageSender messageSender를 더한다
- private final OrderPaidNotifier orderPaidNotifier 칸을 두고 생성자에서 new OrderPaidNotifier(messageSender)로 만든다
- pay(orderId)는 orderRepository.save(order) 다음 줄에서 orderPaidNotifier.notifyPaid(order)를 부른다
- 이벤트 발행기와 주문 접수됨은 그대로다. 결제 완료는 이벤트 발행기에 섞지 않고 메시지 보내기로 따로 보낸다

Shipment.java (T2, 배송 도메인 계층)
- long orderId, String address를 final 칸으로 가진다. getOrderId(), getAddress()
- 배송은 주문 번호로 구별한다. equals와 hashCode는 orderId로 한다
- 주소를 String으로 두는 이유: 주문 컨텍스트의 주소 Address를 가져다 쓰지 않는다. 배송 컨텍스트는 자기 모델을 가진다

ShipmentRepository.java, MemoryShipmentRepository.java (T2)
- save(Shipment), Optional<Shipment> findByOrderId(long orderId)
- 메모리 배송 저장소는 HashMap<Long, Shipment>에 주문 번호로 넣는다. 메모리 상품 저장소와 같은 모양이다

OrderPaidMessageHandler.java (T2, 배송 응용 계층)
- ShipmentRepository를 받는다
- handle(String message): message.split("\\|")로 나눈다. 첫 칸이 ORDER_PAID가 아니면 돌아간다
- 둘째 칸에서 "orderId=" 뒤를 숫자로, 셋째 칸에서 "address=" 뒤를 글자로 읽어 new Shipment(orderId, address)를 저장한다
- import는 ddd.shipping뿐이다. 주문 컨텍스트 클래스를 쓰지 않는다

Main.java
- T1: 첫 줄을 ===== 배송 컨텍스트 =====로 바꾸고 앞 장 장면을 지운다
- T1: 주문 응용 서비스 마지막 인자로 message -> System.out.println("보낸 메시지: " + message) 람다를 넘긴다
- T1: 장면 [결제 완료를 메시지로 알리기]는 자바 책 2개를 주문하고 결제한다. 결제 중에 보낸 메시지 줄이 찍히고, 이어서 주문 상태: 주문 접수 -> 결제 완료를 찍는다
- T2: 메모리 배송 저장소와 결제 완료 메시지 처리기를 만든다. 람다가 보낸 메시지를 찍은 뒤 orderPaidMessageHandler.handle(message)를 부른다
- T2: 장면 [배송 컨텍스트가 메시지 읽어 배송 만들기]는 shipmentRepository.findByOrderId(orderId)로 배송을 꺼내 배송 컨텍스트: 1번 주문, 서울시 강남구로 찍는다

context-map.md
- T1: 공개 호스트 서비스와 발행된 언어 절을 더한다. 공개 호스트 서비스는 한 컨텍스트가 바깥에 열어 둔 정해진 창구이고, 주문 컨텍스트의 창구는 결제 완료 알리미 OrderPaidNotifier라고 쓴다. 발행된 언어는 그 창구가 쓰는 정해진 메시지 모양이라고 쓴다
- T1: 결제 완료 메시지 표: 칸, 값, 뜻. 종류 ORDER_PAID, orderId 주문 번호, address 배송 주소. 예 ORDER_PAID|orderId=1|address=서울시 강남구
- T2: 하위 도메인 표에 배송 줄(지원, 배송 컨텍스트, ddd.shipping)을 더한다
- T2: 컨텍스트 사이 표에 배송, 주문, 결제 완료 메시지 처리기가 결제 완료 메시지 글자를 읽어 배송을 만든다 줄을 더한다
- T2: 컨텍스트 사이 절 아래 줄을 지금에 맞춘다. 주문과 상품은 다른 컨텍스트 클래스를 import로 쓰고, 배송은 주문 클래스를 import하지 않고 메시지 글자만 읽으며, 배송은 주문 번호와 배송 주소를 자기 모양으로 갖는다고 쓴다

glossary.md
- T1: 결제 완료 알리미 OrderPaidNotifier, 메시지 보내기 MessageSender, 결제 완료 메시지 ORDER_PAID 줄
- T2: 배송 Shipment, 배송 저장소 ShipmentRepository, 결제 완료 메시지 처리기 OrderPaidMessageHandler 줄

ShippingContextTest.java
- 패키지는 ddd다. 주문과 배송 클래스는 import로 부른다
- S1은 결제하기 전에는 보낸 메시지가 없고, 결제하면 메시지 보내기가 ORDER_PAID|orderId=1|address=서울시 강남구 하나를 받는지 본다
- S2는 컨텍스트 맵의 공개 호스트 서비스와 발행된 언어 절에 ORDER_PAID, orderId, address, OrderPaidNotifier가 있는지, 용어집 세 줄을 본다
- S3은 배송, 배송 저장소가 ddd.shipping.domain, 메모리 배송 저장소가 ddd.shipping.infrastructure에 있고 저장하고 주문 번호로 꺼내지는지 본다
- S4는 처리기가 결제 완료 메시지 글자로 배송을 만들고 다른 종류 메시지는 넘기는지, src/ddd/shipping 아래 모든 파일에 ddd.order가 없는지, Main 출력 전체가 실행 결과와 같은지 본다
- S5는 컨텍스트 맵 두 표의 배송 줄과 용어집 세 줄을 본다

## 핵심 코드
public void notifyPaid(Order order) {
    String message = "ORDER_PAID|orderId=" + order.getId() + "|address=" + order.getAddress().value();
    messageSender.send(message);
}

orderRepository.save(order);
orderPaidNotifier.notifyPaid(order);

String[] fields = message.split("\\|");
if (!fields[0].equals("ORDER_PAID")) {
    return;
}
long orderId = Long.parseLong(fields[1].substring("orderId=".length()));
String address = fields[2].substring("address=".length());
shipmentRepository.save(new Shipment(orderId, address));

message -> {
    System.out.println("보낸 메시지: " + message);
    orderPaidMessageHandler.handle(message);
}

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | MessageSender.java, OrderPaidNotifier.java, OrderService.java, Main.java |
| S2 | context-map.md, glossary.md |
| S3 | Shipment.java, ShipmentRepository.java, MemoryShipmentRepository.java |
| S4 | OrderPaidMessageHandler.java, Main.java |
| S5 | context-map.md, glossary.md |

## 라이브러리
- 추가 없음

## 다른 장 파일
- ddd/src/ddd/order/application/OrderService.java: anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference, order-factory, discount, order-events, bounded-contexts
- ddd/src/ddd/Main.java: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference, order-factory, discount, free-shipping, order-events, optimistic-lock, bounded-contexts
- ddd/context-map.md: bounded-contexts
- ddd/glossary.md: ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, id-reference, order-factory, discount, free-shipping, order-events, optimistic-lock
