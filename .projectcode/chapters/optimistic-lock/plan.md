# optimistic-lock 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/Main.java | 고침 | 이 장 장면 둘을 찍는다. 주문 저장소를 변수로 두고 같은 주문을 두 번 꺼내 배송과 취소를 저장한다 |
| ddd/src/ddd/order/domain/Order.java | 고침 | 버전을 가지고 내주고, 버전을 1 올린다. 복사 생성자가 버전도 복사한다 |
| ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java | 고침 | 저장할 때 버전을 비교해 다르면 거절하고, 같으면 버전을 올려 저장한다 |
| ddd/glossary.md | 고침 | 버전 줄을 더하고 저장하기 줄에 버전 확인을 넣는다 |
| tests/ddd/OptimisticLockTest.java | 새로 | S1부터 S4까지 행동 테스트 |

## 코드 계획
Main.java
- T1: 첫 줄을 ===== 동시 수정 막기 =====로 바꾸고 앞 장 장면을 지운다
- T1: new MemoryOrderRepository()를 OrderRepository orderRepository 변수로 꺼내 주문 응용 서비스에 넘긴다. 발행기는 new EventPublisher()로 바로 넘긴다
- T1: 장면 [같은 주문을 두 번 꺼내 저장하기]는 자바 책 2개를 주문하고 결제한 뒤 orderService.findOrder로 두 번 꺼내 first, second에 둔다
- T1: first.ship()을 하고 orderRepository.save(first)를 한 뒤 첫째로 꺼내 배송: 결제 완료 -> 배송 중을 찍는다
- T1: second.cancel()을 하고 orderRepository.save(second)를 한 뒤 둘째로 꺼내 취소: 배송 중 -> 취소를 찍는다. 이것이 문제다
- T2: 장면 [저장할 때마다 버전 올리기]는 새 주문을 하고 결제한 뒤 결제하고 저장: 버전 1 -> 2를 찍는다
- T3: second.cancel()부터 찍기까지를 try catch로 감싸 IllegalStateException이면 둘째로 꺼내 취소: 거절 메시지를 찍는다. 이어서 저장된 주문 상태를 찍는다
- 저장소를 직접 쓰는 이유: 주문 응용 서비스는 일마다 꺼내고 바로 저장해서 두 사람이 같은 주문을 들고 있는 때를 보이지 못한다

Order.java (도메인 계층)
- T2: private long version 칸을 더한다. 생성자에서 정하지 않아 새 주문은 0이다
- T2: 복사 생성자에 this.version = other.version을 더한다
- T2: getVersion()과 increaseVersion()을 더한다. increaseVersion()은 version을 1 올린다
- 주문이 버전을 들고 있는 이유: 꺼낸 주문이 언제 저장된 주문을 보고 바뀌었는지 저장할 때 알 수 있다

MemoryOrderRepository.java (인프라 계층)
- T2: save 첫 줄에서 order.increaseVersion()을 부르고 new Order(order)로 복사해 넣는다. 꺼낸 주문은 복사본이라 저장된 버전을 그대로 가진다
- T3: save에서 저장된 주문을 꺼내, 있고 버전이 다르면 IllegalStateException("다른 곳에서 먼저 바꾼 주문입니다")을 던진다. 비교는 버전 올리기 전에 한다
- 저장소에서 비교하는 이유: 저장된 버전은 저장소만 안다. 꺼낸 주문끼리는 서로를 모른다
- findById는 그대로다

glossary.md
- T2: 버전 version 줄을 주문 번호로 찾기 줄 다음에 더한다
- T3: 저장하기 줄에 버전이 다르면 거절한다는 뜻을 넣는다

OptimisticLockTest.java
- 패키지는 ddd다. 도메인, 응용, 인프라 클래스는 import로 부른다
- S1은 Main 출력에 [같은 주문을 두 번 꺼내 저장하기] 장면과 첫째로 꺼내 배송 줄이 있는지 본다
- S2는 새 주문의 getVersion()이 0이고, 저장하면 1, 꺼낸 주문도 1, 다시 저장하면 2인지, 주문 응용 서비스로 주문하고 결제하면 저장된 버전이 2인지 본다
- S3은 같은 주문을 두 번 꺼내 하나를 저장한 뒤 다른 하나를 저장하면 메시지와 함께 IllegalStateException이 나고, 저장된 주문은 앞 저장의 상태와 버전 그대로인지 본다. Main 출력 전체가 실행 결과와 같은지 본다
- S4는 용어집의 버전 줄과 저장하기 줄의 버전을 본다

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
orders.put(order.getId(), new Order(order));

Order first = orderService.findOrder(orderId);
Order second = orderService.findOrder(orderId);
first.ship();
orderRepository.save(first);
second.cancel();
orderRepository.save(second);

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | Main.java |
| S2 | Order.java, MemoryOrderRepository.java, Main.java |
| S3 | MemoryOrderRepository.java, Main.java |
| S4 | glossary.md |

## 라이브러리
- 추가 없음

## 다른 장 파일
- ddd/src/ddd/Main.java: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference, order-factory, discount, free-shipping, order-events
- ddd/src/ddd/order/domain/Order.java: anemic-order, rich-entity, value-object, order-aggregate, order-status, order-repository, layers, id-reference, discount, free-shipping, order-events
- ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java: order-repository, layers
- ddd/glossary.md: ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, id-reference, order-factory, discount, free-shipping, order-events
