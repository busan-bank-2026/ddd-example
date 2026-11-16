# shipping-context 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/order/application/OrderOpenHostService.java | 새로 | 주문 컨텍스트의 공개 호스트 서비스. 결제 완료 메시지 글자를 만들어 발행기에 넘긴다 |
| ddd/src/ddd/order/application/OrderApplicationService.java | 고침 | 공개 호스트 서비스를 두고, 결제하기에서 저장 뒤 결제 완료 메시지를 넘긴다 |
| ddd/src/ddd/shipping/domain/Shipment.java | 새로 | 배송 애그리거트. 주문 번호와 배송 주소 |
| ddd/src/ddd/shipping/domain/ShipmentRepository.java | 새로 | 배송 저장소 인터페이스 |
| ddd/src/ddd/shipping/infrastructure/MemoryShipmentRepository.java | 새로 | HashMap 배송 저장소 |
| ddd/src/ddd/shipping/application/OrderPaidMessageHandler.java | 새로 | 결제 완료 메시지 글자를 읽어 배송을 만들고 저장한다 |
| ddd/src/ddd/Main.java | 고침 | 마지막에 결제 완료 메시지 장면과 배송 컨텍스트 장면 |
| ddd/context-map.md | 고침 | 공개 호스트 서비스와 발행된 언어 절, 배송 하위 도메인 줄, 컨텍스트 사이 줄 |
| ddd/glossary.md | 고침 | 공개 호스트 서비스, 결제 완료 메시지, 배송, 배송 저장소, 결제 완료 메시지 처리기 |
| tests/ddd/ShippingContextTest.java | 새로 | S1부터 S5까지 행동 테스트 |
| tests/ddd 아래 앞 장 테스트 일곱 개 | 고침 | 기대 출력에 세 줄, src/ddd 목록에 shipping |

## 코드 계획
OrderOpenHostService.java (T1, 주문 응용 계층)
- 생성자에서 EventPublisher를 받는다
- publishOrderPaid(Order order): "ORDER_PAID|orderId=" + 주문 번호 + "|address=" + 배송 주소 글자로 메시지를 만들고 eventPublisher.publish(List.of(message))로 넘긴다
- 따로 두는 이유: 바깥 컨텍스트에 무엇을 어떤 모양으로 알리는지 한 곳에서 정한다. 이 클래스가 주문 컨텍스트의 열린 창구다
- 메시지를 글자로 만드는 이유: 받는 쪽이 주문 클래스를 몰라도 읽는다. 메시지 모양이 발행된 언어다
- 응용 계층에 두는 이유: 발행기를 쓰는 곳이고, 주문 도메인은 바깥 알림을 모른다

OrderApplicationService.java (T1)
- private final OrderOpenHostService orderOpenHostService 칸을 더한다
- 생성자 끝에서 new OrderOpenHostService(eventPublisher)로 만든다. 생성자 매개변수는 그대로다
- pay(orderId)는 orderRepository.save(order) 다음 줄에서 orderOpenHostService.publishOrderPaid(order)를 부른다
- 주문 이벤트 목록(order.getEvents())은 결제 때 넘기지 않는다. 앞 이벤트가 두 번 나가지 않는다

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
- import는 java와 ddd.shipping뿐이다. 주문 컨텍스트 클래스를 쓰지 않는다

Main.java
- T1: 동시 수정 장면 다음에 eventPublisher.subscribe(String.class, message -> System.out.println("결제 완료 메시지: " + message))로 받은 메시지를 찍게 하고 eventPublisher.processEvents()를 부른다
- T1: 앞에서 결제한 1번 주문과 4번 주문(order5)의 메시지 두 줄이 찍힌다. 처리 때 남아 있던 주문 접수됨 이벤트로 재고도 줄지만 찍지 않는다
- T2: 메모리 배송 저장소와 결제 완료 메시지 처리기를 만들고 eventPublisher.subscribe(String.class, orderPaidMessageHandler::handle)로 잇는다. processEvents() 앞에 둔다
- T2: 처리 뒤 shipmentRepository.findByOrderId(orderId)로 1번 주문의 배송을 꺼내 배송 컨텍스트: 1번 주문, 서울시 강남구로 찍는다

context-map.md
- T1: 공개 호스트 서비스와 발행된 언어 절을 더한다. 공개 호스트 서비스는 주문 컨텍스트가 바깥에 알리는 정해진 창구(OrderOpenHostService), 발행된 언어는 그 창구가 쓰는 정해진 메시지 모양이라고 쓴다
- T1: 결제 완료 메시지 표: 칸, 값, 뜻. 종류 ORDER_PAID, orderId 주문 번호, address 배송 주소. 예 ORDER_PAID|orderId=1|address=서울시 강남구
- T2: 하위 도메인 표에 배송 줄(지원, 배송 컨텍스트, ddd.shipping)을 더한다
- T2: 컨텍스트 사이 표에 배송, 주문, 결제 완료 메시지 처리기가 결제 완료 메시지 글자를 읽어 배송을 만든다 줄을 더한다
- T2: 배송 컨텍스트는 주문 클래스를 import하지 않고 메시지 글자만 읽는다고 쓴다

glossary.md
- T1: 공개 호스트 서비스 OrderOpenHostService, 결제 완료 메시지 ORDER_PAID 줄
- T2: 배송 Shipment, 배송 저장소 ShipmentRepository, 결제 완료 메시지 처리기 OrderPaidMessageHandler 줄

ShippingContextTest.java
- 패키지는 ddd다. 주문과 배송 클래스는 import로 부른다
- S1은 결제하기 전에는 글자 메시지가 없고, 결제하고 처리하면 구독자가 ORDER_PAID|orderId=1|address=서울시 강남구 하나를 받는지, 처리 전에는 받지 않는지, Main 출력에서 동시 수정 줄 다음 두 줄이 결제 완료 메시지인지 본다
- S2는 컨텍스트 맵의 공개 호스트 서비스와 발행된 언어 절에 ORDER_PAID, orderId, address, OrderOpenHostService가 있는지, 용어집 두 줄을 본다
- S3은 배송, 배송 저장소가 ddd.shipping.domain, 메모리 배송 저장소가 ddd.shipping.infrastructure에 있고 저장하고 주문 번호로 꺼내지는지 본다
- S4는 처리기가 결제 완료 메시지 글자로 배송을 만들고 다른 종류 메시지는 넘기는지, src/ddd/shipping 아래 모든 파일에 ddd.order가 없는지, Main 출력 전체가 실행 결과와 같은지 본다
- S5는 컨텍스트 맵 두 표의 배송 줄과 용어집 세 줄을 본다

앞 장 테스트
- 기대 출력 전체를 보는 BoundedContextsTest, DiscountTest, FreeShippingTest, LayersTest, OptimisticLockTest, OrderEventsTest, OrderFactoryTest에 세 줄을 더한다
- src/ddd 목록을 보는 BoundedContextsTest, LayersTest에 shipping을 더한다

## 핵심 코드
String message = "ORDER_PAID|orderId=" + order.getId() + "|address=" + order.getAddress().value();
eventPublisher.publish(List.of(message));

orderRepository.save(order);
orderOpenHostService.publishOrderPaid(order);

String[] fields = message.split("\\|");
if (!fields[0].equals("ORDER_PAID")) {
    return;
}
long orderId = Long.parseLong(fields[1].substring("orderId=".length()));
String address = fields[2].substring("address=".length());
shipmentRepository.save(new Shipment(orderId, address));

eventPublisher.subscribe(String.class, orderPaidMessageHandler::handle);

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | OrderOpenHostService.java, OrderApplicationService.java, Main.java |
| S2 | context-map.md, glossary.md |
| S3 | Shipment.java, ShipmentRepository.java, MemoryShipmentRepository.java |
| S4 | OrderPaidMessageHandler.java, Main.java |
| S5 | context-map.md, glossary.md |

## 라이브러리
- 추가 없음

## 다른 장 파일
- ddd/src/ddd/order/application/OrderApplicationService.java: anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference, order-factory, discount, order-events, bounded-contexts
- ddd/src/ddd/Main.java: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference, order-factory, discount, free-shipping, order-events, optimistic-lock, bounded-contexts
- ddd/context-map.md: bounded-contexts
- ddd/glossary.md: ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, id-reference, order-factory, discount, free-shipping, order-events, optimistic-lock
- tests/ddd 아래 앞 장 테스트 일곱 개: bounded-contexts, discount, free-shipping, layers, optimistic-lock, order-events, order-factory
