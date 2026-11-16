# order-saga 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/Main.java | 고침 | 결제 실패 뒤 장면, 재고 되돌리기 처리기 구독 |
| ddd/src/ddd/order/domain/OrderCanceled.java | 새로 | 주문 취소됨 이벤트 record |
| ddd/src/ddd/order/application/OrderApplicationService.java | 고침 | 취소하기가 주문 취소됨을 넘기고, 결제하기가 거절되면 취소하기로 보상한다 |
| ddd/src/ddd/catalog/domain/Product.java | 고침 | 재고 되돌리기 |
| ddd/src/ddd/catalog/application/IncreaseStockHandler.java | 새로 | 재고 되돌리기 처리기 |
| ddd/glossary.md | 고침 | 주문 취소됨, 재고 되돌리기, 재고 되돌리기 처리기, 사가, 보상, 재고 줄, 주문 취소하기 줄 |
| ddd/where-rules-live.md | 새로 | 학생 문서. 규칙 두는 곳 정리와 사가의 단계와 보상 |
| ddd/context-map.md | 고침 | 상품과 주문 줄에 재고 되돌리기, 주문과 회원 줄에 주문 조회 서비스 |
| tests/ddd/OrderSagaTest.java | 새로 | S1부터 S7까지 행동 테스트 |
| tests/ddd 아래 앞 장 테스트 열 개 | 고침 | 기대 출력의 한도 넘은 결제 줄과 마지막 한 줄, 주문 도메인 파일 수, 결제 거절 뒤 주문 상태 |

## 코드 계획
Main.java (T1)
- 주문 목록 줄 다음에 노트북 상품을 new Product(5, "노트북", new Money(120000), 10)으로 저장한다
- 이영희가 노트북 1개를 서울시 서초구로 주문하고 결제한다
- IllegalStateException을 잡은 곳에서 eventPublisher.processEvents()를 하고 결제 실패 뒤: 노트북 재고, 주문 상태를 찍는다
- 상품 번호 5를 쓰는 이유: 4번은 음수 금액 장면에서 쓴 번호다

OrderCanceled.java (T2, 주문 도메인 계층)
- record OrderCanceled(long orderId, List<OrderLine> orderLines)
- 주문 접수됨과 같은 모양이다. 받는 쪽이 무엇을 되돌릴지 안다

Product.java (T2)
- increaseStock(int quantity): stock = stock + quantity

IncreaseStockHandler.java (T2, 상품 응용 계층)
- 재고 줄이기 처리기와 같은 모양이다. 생성자에서 ProductRepository를 받는다
- handle(OrderCanceled event): 주문 항목마다 상품을 꺼내 increaseStock(수량)을 하고 저장한다
- 따로 두는 이유: 재고 줄이기와 되돌리기는 받는 이벤트가 다르다. 하나씩 구독한다

OrderApplicationService.java
- T2 cancel(orderId): order.cancel()과 저장 다음에 eventPublisher.publish(List.of(new OrderCanceled(order.getId(), order.getOrderLines())))
- T2 주문 취소됨을 응용 서비스가 만드는 이유: 꺼낸 주문의 이벤트 목록에는 앞에서 남긴 주문 접수됨이 함께 들어 있다. 다시 넘기면 재고를 또 줄인다. 취소하기는 이번에 일어난 일 하나만 넘긴다
- T3 pay(orderId): 결제 결과가 승인이 아니면 cancel(orderId)를 부른 뒤 거절 이유로 throw new IllegalStateException
- T3 cancel(orderId)를 부르는 이유: 보상은 앞 단계를 되돌리는 또 하나의 일이다. 주문 취소하기는 이미 있고, 취소가 주문 취소됨을 넘기면 재고 되돌리기가 따라온다
- T3 저장된 주문은 아직 주문 접수다. 결제하기 안의 order는 저장하지 않은 채 버린다

Main.java (T2)
- 재고 줄이기 처리기 구독 다음 줄에 IncreaseStockHandler를 만들어 OrderCanceled.class로 구독한다

glossary.md
- T2: 주문 취소됨 OrderCanceled, 재고 되돌리기 increaseStock, 재고 되돌리기 처리기 IncreaseStockHandler 줄. 재고 줄에 주문을 취소하면 나중에 되돌아온다를, 주문 취소하기 줄에 주문 취소됨을 남긴다를 넣는다
- T3: 사가 saga, 보상 compensation 줄

where-rules-live.md (T4)
- 제목 규칙 두는 곳
- 규칙 두는 곳 표: 두는 곳, 어떤 규칙, 이 프로그램의 예. 값 객체 Money Address, 애그리거트 Order OrderStatus, 도메인 서비스 DiscountPolicy, 명세 FreeShippingSpecification, 이벤트 OrderPlaced OrderCanceled, 사가 결제하기의 보상과 처리기, 응용 서비스 OrderApplicationService
- 고르는 순서 절: 값 하나면 값 객체, 한 애그리거트 안이면 애그리거트, 둘 이상을 읽고 계산만 하면 도메인 서비스, 참 거짓 조건이면 명세, 다른 애그리거트가 나중에 맞추면 이벤트, 여러 단계가 실패 때 되돌려야 하면 사가, 꺼내고 시키고 저장하는 순서만 응용 서비스
- 사가 절: 단계와 보상 표, 보상은 지우지 않고 반대 일을 한 번 더 하는 것

context-map.md (T4)
- 상품과 주문 줄: 재고 줄이기 처리기와 재고 되돌리기 처리기가 주문 접수됨과 주문 취소됨 이벤트를 받아 재고를 줄이고 되돌린다
- 주문과 회원 줄: 할인 정책과 주문 응용 서비스, 주문 조회 서비스가 회원과 회원 등급, 회원 이름을 읽는다

OrderSagaTest.java
- 패키지는 ddd다. 주문과 상품 클래스는 import로 부른다
- S1은 Main 소스에 노트북 상품이 있고 출력 마지막 줄이 결제 실패 뒤: 노트북 재고 로 시작하는지 본다
- S2는 increaseStock, 취소하기 뒤 발행된 주문 취소됨 하나의 주문 번호와 주문 항목, 배송 중 주문의 취소 거절 뒤 이벤트가 없는지 본다
- S3은 두 처리기를 구독한 발행기로 주문하고 처리한 재고, 취소한 뒤 처리 전 재고, 처리 뒤 재고를 본다
- S4는 거절하는 결제 창구로 결제한 뒤 거절 이유, 주문 상태 취소, 처리 뒤 재고, 결제 완료 메시지 없음, Main 출력 전체를 본다
- S5는 용어집 일곱 줄을 본다
- S6은 규칙 두는 곳 문서의 일곱 줄과 사가 절의 보상 짝을 본다
- S7은 컨텍스트 맵 두 줄을 본다

앞 장 테스트
- 기대 출력 전체를 보는 열 개 테스트(BoundedContexts, Discount, FreeShipping, Layers, OptimisticLock, OrderEvents, OrderFactory, OrderQuery, PaymentAcl, ShippingContext)의 한도 넘은 결제 줄을 취소로 바꾸고 마지막에 한 줄을 더한다
- BoundedContextsTest는 도메인 파일 수를 20으로 바꾼다
- PaymentAclTest S4는 거절 뒤 주문 상태를 취소로 본다

## 핵심 코드
public void cancel(long orderId) {
    Order order = findOrder(orderId);
    order.cancel();
    orderRepository.save(order);
    eventPublisher.publish(List.of(new OrderCanceled(order.getId(), order.getOrderLines())));
}

PaymentResult result = paymentGateway.pay(order.getPaymentPrice());
if (!result.approved()) {
    cancel(orderId);
    throw new IllegalStateException(result.reason());
}

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | Main.java |
| S2 | OrderCanceled.java, Product.java, OrderApplicationService.java |
| S3 | IncreaseStockHandler.java, Main.java |
| S4 | OrderApplicationService.java |
| S5 | glossary.md |
| S6 | where-rules-live.md |
| S7 | context-map.md |

## 라이브러리
- 추가 없음

## 다른 장 파일
- ddd/src/ddd/Main.java: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference, order-factory, discount, free-shipping, order-events, optimistic-lock, bounded-contexts, shipping-context, payment-acl, order-query
- ddd/src/ddd/order/application/OrderApplicationService.java: anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference, order-factory, discount, order-events, bounded-contexts, shipping-context, payment-acl
- ddd/src/ddd/catalog/domain/Product.java: id-reference, order-events, bounded-contexts
- ddd/glossary.md: ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, id-reference, order-factory, discount, free-shipping, order-events, optimistic-lock, shipping-context, payment-acl, order-query
- ddd/context-map.md: bounded-contexts, shipping-context, payment-acl
- tests/ddd 아래 앞 장 테스트: bounded-contexts, discount, free-shipping, layers, optimistic-lock, order-events, order-factory, order-query, payment-acl, shipping-context
