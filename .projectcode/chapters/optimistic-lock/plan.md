# optimistic-lock 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/Main.java | 고침 | 주문 저장소를 변수로 두고, 같은 주문을 두 번 꺼내 배송과 취소를 저장하는 동시 수정 장면 |
| ddd/src/ddd/domain/Order.java | 고침 | 버전을 가지고 내주고, 버전을 1 올린다 |
| ddd/src/ddd/infrastructure/MemoryOrderRepository.java | 고침 | 저장할 때 버전을 비교해 다르면 거절하고, 같으면 버전을 올려 저장한다 |
| ddd/glossary.md | 고침 | 버전 줄을 더하고 저장하기 줄에 버전 확인을 넣는다 |
| tests/ddd/OptimisticLockTest.java | 새로 | S1부터 S4까지 행동 테스트 |
| tests/ddd/DiscountTest.java | 고침 | 기대 출력에 동시 수정 줄 |
| tests/ddd/FreeShippingTest.java | 고침 | 기대 출력에 동시 수정 줄 |
| tests/ddd/LayersTest.java | 고침 | 기대 출력에 동시 수정 줄 |
| tests/ddd/OrderEventsTest.java | 고침 | 기대 출력에 동시 수정 줄 |
| tests/ddd/OrderFactoryTest.java | 고침 | 기대 출력에 동시 수정 줄 |

## 코드 계획
Main.java
- T1: new MemoryOrderRepository()를 OrderRepository orderRepository 변수로 꺼내 주문 응용 서비스에 넘긴다
- T1: 가격 바뀐 뒤 줄 다음에 문제 장면을 더한다. 결제 완료인 order5를 orderService.findOrder로 두 번 꺼내 orderA, orderB에 둔다
- T1: orderA.ship()을 하고 orderRepository.save(orderA), 이어서 orderB.cancel()을 하고 orderRepository.save(orderB)를 한다
- T1: 다시 꺼낸 주문의 상태를 동시 수정: 취소로 찍는다. 이것이 문제다
- T3: orderB.cancel()부터 찍기까지를 try catch로 감싸 IllegalStateException이면 동시 수정: 거절 메시지를 찍는다
- 저장소를 직접 쓰는 이유: 주문 응용 서비스는 일마다 꺼내고 바로 저장해서 두 사람이 같은 주문을 들고 있는 때를 보이지 못한다

Order.java (도메인 계층)
- T2: private long version 칸을 더한다. 생성자에서 정하지 않아 새 주문은 0이다
- T2: getVersion()과 increaseVersion()을 더한다. increaseVersion()은 version을 1 올린다
- 주문이 버전을 들고 있는 이유: 꺼낸 주문이 언제 저장된 주문을 보고 바뀌었는지 저장할 때 알 수 있다

MemoryOrderRepository.java (인프라 계층)
- T2: save 첫 줄에서 order.increaseVersion()을 부르고 복사해 넣는다. 꺼낸 주문은 복사본이라 저장된 버전을 그대로 가진다
- T3: save에서 저장된 주문을 꺼내, 있고 버전이 다르면 IllegalStateException("다른 곳에서 먼저 바꾼 주문입니다")을 던진다. 비교는 버전 올리기 전에 한다
- 저장소에서 비교하는 이유: 저장된 버전은 저장소만 안다. 꺼낸 주문끼리는 서로를 모른다
- 복사 부분(copy)은 그대로다

glossary.md
- T2: 버전 version 줄을 주문 저장소 줄들 다음에 더한다
- T3: 저장하기 줄에 버전이 다르면 거절한다는 뜻을 넣는다

OptimisticLockTest.java
- 패키지는 ddd다. 도메인, 응용, 인프라 클래스는 import로 부른다
- S1은 Main 출력의 가격 바뀐 뒤 줄 다음 줄이 동시 수정:으로 시작하는지 본다
- S2는 새 주문의 getVersion()이 0이고, 저장하면 1, 꺼낸 주문도 1, 다시 저장하면 2인지, 주문 응용 서비스로 주문하고 결제하면 저장된 버전이 2인지 본다
- S3은 같은 주문을 두 번 꺼내 하나를 저장한 뒤 다른 하나를 저장하면 메시지와 함께 IllegalStateException이 나고, 저장된 주문은 앞 저장의 상태와 버전 그대로인지 본다. 같은 번호의 새 주문을 저장해도 거절되는지 본다. Main 출력 전체가 실행 결과와 같은지 본다
- S4는 용어집의 버전 줄과 저장하기 줄의 버전을 본다

앞 장 테스트
- 기대 출력 전체를 보는 DiscountTest, FreeShippingTest, LayersTest, OrderEventsTest, OrderFactoryTest의 마지막에 동시 수정: 다른 곳에서 먼저 바꾼 주문입니다 줄을 더한다

## 핵심 코드
private long version;

public void increaseVersion() {
    version++;
}

Order saved = orders.get(order.getId());
if (saved != null && saved.getVersion() != order.getVersion()) {
    throw new IllegalStateException("다른 곳에서 먼저 바꾼 주문입니다");
}
order.increaseVersion();
orders.put(order.getId(), copy(order));

Order orderA = orderService.findOrder(order5Id);
Order orderB = orderService.findOrder(order5Id);
orderA.ship();
orderRepository.save(orderA);
orderB.cancel();
orderRepository.save(orderB);

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | Main.java |
| S2 | Order.java, MemoryOrderRepository.java |
| S3 | MemoryOrderRepository.java, Main.java |
| S4 | glossary.md |

## 라이브러리
- 추가 없음

## 다른 장 파일
- ddd/src/ddd/Main.java: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference, order-factory, discount, free-shipping, order-events
- ddd/src/ddd/domain/Order.java: anemic-order, rich-entity, value-object, order-aggregate, order-status, order-repository, layers, id-reference, discount, free-shipping, order-events
- ddd/src/ddd/infrastructure/MemoryOrderRepository.java: order-repository, layers
- ddd/glossary.md: ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, id-reference, order-factory, discount, free-shipping, order-events
- tests/ddd 아래 앞 장 테스트 5개: discount, free-shipping, layers, order-events, order-factory
