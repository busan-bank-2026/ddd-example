# order-repository 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/OrderRepository.java | 새로 | 주문 저장소가 할 일. 저장하기와 주문 번호로 찾기 |
| ddd/src/ddd/MemoryOrderRepository.java | 새로 | HashMap에 주문을 넣어 두는 주문 저장소. 저장과 꺼낼 때 복사본을 쓴다 |
| ddd/src/ddd/OrderService.java | 고침 | 주문 저장소를 받는다. 주문 번호로 꺼내 주문에게 시키고 다시 저장한다 |
| ddd/src/ddd/Order.java | 고침 | 복사할 수 있게 Serializable을 붙인다 |
| ddd/src/ddd/OrderItem.java | 고침 | 복사할 수 있게 Serializable을 붙인다 |
| ddd/src/ddd/Money.java | 고침 | 복사할 수 있게 Serializable을 붙인다 |
| ddd/src/ddd/Address.java | 고침 | 복사할 수 있게 Serializable을 붙인다 |
| ddd/src/ddd/Main.java | 고침 | 주문 번호로 서비스를 부르는 모양으로 바꾸고, 없는 주문 결제와 저장 안 한 취소 장면을 더한다 |
| ddd/glossary.md | 고침 | 주문 저장소, 저장하기, 주문 번호로 찾기를 더한다 |
| tests/ddd/AnemicOrderTest.java | 고침 | 주문 번호로 서비스를 부르고 저장소에서 꺼내 확인한다 |
| tests/ddd/UbiquitousLanguageTest.java | 고침 | 주문 번호로 서비스를 부르고 저장소에서 꺼내 확인한다 |
| tests/ddd/RichEntityTest.java | 고침 | 주문 번호로 서비스를 부르고 저장소에서 꺼내 확인한다 |
| tests/ddd/ValueObjectTest.java | 고침 | 주문 번호로 서비스를 부르고 저장소에서 꺼내 확인한다 |
| tests/ddd/OrderAggregateTest.java | 고침 | 주문 번호로 서비스를 부르고 저장소에서 꺼내 확인한다 |
| tests/ddd/OrderStatusTest.java | 고침 | 주문 번호로 서비스를 부르고 저장소에서 꺼내 확인한다 |
| tests/ddd/OrderRepositoryTest.java | 새로 | S1부터 S6까지 행동 테스트 |

## 코드 계획
OrderRepository.java
- interface로 쓴다. void save(Order order), Optional<Order> findById(long id) 두 개다
- 인터페이스로 두는 이유: 서비스는 주문을 어디에 두는지 몰라도 된다. 뒤 장에서 오라클 저장소로 바꿔 끼운다

MemoryOrderRepository.java
- OrderRepository를 구현한다. Map<Long, Order> orders = new HashMap<>()에 주문 번호를 키로 넣는다
- T1: save는 orders.put(order.getId(), order), findById는 Optional.ofNullable(orders.get(id))
- T3(붙여넣기): private Order copy(Order order)를 더한다. ObjectOutputStream으로 바이트로 바꾸고 ObjectInputStream으로 다시 객체로 만든다. IOException, ClassNotFoundException은 IllegalStateException으로 감싼다
- T3: save는 copy(order)를 넣는다. findById는 없으면 Optional.empty(), 있으면 Optional.of(copy(order))

OrderService.java
- T1: private final OrderRepository orderRepository를 두고 생성자 OrderService(OrderRepository orderRepository)로 받는다
- T1: placeOrder는 주문을 만들어 저장하고 order.getId()를 long으로 돌려준다
- T1: public Order findOrder(long orderId)는 orderRepository.findById(orderId).orElseThrow()로 꺼낸다. Main이 주문을 찍을 때도 쓴다
- T1: pay, ship, deliver, cancel은 long orderId를 받는다. findOrder로 꺼내고, 주문에게 시키고, orderRepository.save(order)로 다시 저장한다
- T1: addItem(long orderId, OrderItem orderLine)를 더한다. 같은 모양이다
- T2: findOrder가 orElseThrow(() -> new IllegalArgumentException("주문이 없습니다"))로 거절한다

Order.java, OrderItem.java, Money.java, Address.java
- T3(붙여넣기): implements Serializable을 붙이고 java.io.Serializable을 import 한다
- Order에만 private static final long serialVersionUID = 1L을 더한다. 이클립스 경고를 없앤다

Main.java
- T1: new OrderService(new MemoryOrderRepository())로 만든다
- T1: placeOrder가 돌려준 번호를 orderId, order2Id처럼 받는다. 찍을 때는 orderService.findOrder(번호)로 꺼낸다
- T1: pay, ship, cancel, deliver를 번호로 부른다. 결제 없이 배송은 orderService.ship(order2Id)로 부른다
- T1: 항목 더하기와 결제 뒤 항목 더하기는 orderService.addItem(order5Id, …)로 부른다
- T1: 출력 글자는 앞 장과 같다
- T2: 맨 끝에 없는 주문 결제 장면을 더한다. orderService.pay(100)을 try catch로 감싸 "없는 주문 결제: " 거절 말을 찍는다
- T3: 맨 끝에 저장 안 한 취소 장면을 더한다. orderService.findOrder(order2Id)로 꺼낸 주문을 cancel() 하고 저장하지 않는다. 다시 꺼내 "저장 안 한 취소: " 상태를 찍는다

glossary.md
- T1: 주문 상태 묶음 뒤, 맨 끝에 주문 저장소 OrderRepository, 저장하기 save, 주문 번호로 찾기 findById 세 줄을 더한다

OrderRepositoryTest.java
- S1은 MemoryOrderRepository에 주문 두 개를 저장하고 번호로 꺼내 같은 주문인지, 없는 번호는 빈 결과인지 본다
- S2는 저장한 뒤 원래 주문을 바꿔도, 꺼낸 주문을 바꿔도 다시 꺼낸 주문이 그대로인지 보고, 저장하면 바뀐 것이 남는지 본다. Main의 저장 안 한 취소 줄을 본다
- S3은 placeOrder가 1, 2 번호를 돌려주고 저장소에서 그 번호로 꺼낼 수 있는지, Main의 주문 줄을 본다
- S4는 결제, 배송, 배송 완료, 취소, 항목 더하기를 번호로 부른 뒤 저장소에서 다시 꺼낸 주문의 상태와 항목을 본다
- S5는 없는 번호로 결제하면 IllegalArgumentException 주문이 없습니다인지, Main의 없는 주문 결제 줄을 본다
- S6은 glossary.md에 주문 저장소 OrderRepository, 저장하기 save, 주문 번호로 찾기 findById 줄이 있는지 본다

앞 장 테스트
- new OrderService()를 new OrderService(new MemoryOrderRepository())로 바꾼다
- placeOrder의 결과를 주문 번호로 받고, 서비스는 번호로 부르고, 상태는 service.findOrder(번호)로 다시 꺼내 본다
- 주문에게 직접 시키는 확인(addItem, 생성자)은 그대로 둔다

## 핵심 코드
public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(long id);
}

public long placeOrder(String customerName, List<OrderItem> items, String address) {
    Order order = new Order(nextId++, customerName, items, new Address(address));
    orderRepository.save(order);
    return order.getId();
}

public void pay(long orderId) {
    Order order = findOrder(orderId);
    order.pay();
    orderRepository.save(order);
}

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | OrderRepository.java, MemoryOrderRepository.java |
| S2 | MemoryOrderRepository.java, Order.java, OrderItem.java, Money.java, Address.java, Main.java |
| S3 | OrderService.java, Main.java |
| S4 | OrderService.java, Main.java |
| S5 | OrderService.java, Main.java |
| S6 | glossary.md |

## 라이브러리
- 추가 없음. 복사는 자바 기본 java.io를 쓴다

## 다른 장 파일
- ddd/src/ddd/Main.java: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status
- ddd/src/ddd/OrderService.java: anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status
- ddd/src/ddd/Order.java: anemic-order, rich-entity, value-object, order-aggregate, order-status
- ddd/src/ddd/OrderItem.java: order-aggregate
- ddd/src/ddd/Money.java, ddd/src/ddd/Address.java: value-object
- ddd/glossary.md: ubiquitous-language, rich-entity, value-object, order-aggregate, order-status
- tests/ddd/AnemicOrderTest.java: anemic-order
- tests/ddd/UbiquitousLanguageTest.java: ubiquitous-language
- tests/ddd/RichEntityTest.java: rich-entity
- tests/ddd/ValueObjectTest.java: value-object
- tests/ddd/OrderAggregateTest.java: order-aggregate
- tests/ddd/OrderStatusTest.java: order-status
