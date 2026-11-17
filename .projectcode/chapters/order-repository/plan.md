# order-repository 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/OrderRepository.java | 새로 | 주문 저장소가 할 일. 저장하기와 주문 번호로 찾기 |
| ddd/src/ddd/MemoryOrderRepository.java | 새로 | HashMap에 주문을 넣어 두는 주문 저장소. 저장과 꺼낼 때 복사본을 쓴다 |
| ddd/src/ddd/OrderService.java | 고침 | 주문 저장소를 받는다. 주문 번호로 꺼내 주문에게 시키고 다시 저장한다 |
| ddd/src/ddd/Order.java | 고침 | 주문을 복사하는 복사 생성자를 더한다 |
| ddd/src/ddd/Main.java | 고침 | 앞 장 장면을 지우고 주문 번호로 서비스를 부르는 장면, 없는 주문 결제와 꺼낸 주문만 바꾸기 장면을 찍는다 |
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
- T3: save는 new Order(order) 복사본을 넣는다. findById는 없으면 Optional.empty(), 있으면 Optional.of(new Order(order))

OrderService.java
- T1: private final OrderRepository orderRepository를 두고 생성자 OrderService(OrderRepository orderRepository)로 받는다
- T1: placeOrder는 주문을 만들어 저장하고 order.getId()를 long으로 돌려준다
- T1: public Order findOrder(long orderId)는 orderRepository.findById(orderId).orElseThrow()로 꺼낸다. Main이 주문을 찍을 때도 쓴다
- T1: pay, ship, deliver, cancel은 long orderId를 받는다. findOrder로 꺼내고, 주문에게 시키고, orderRepository.save(order)로 다시 저장한다
- T1: addItem(long orderId, OrderItem item)를 더한다. 같은 모양이다
- T2: findOrder가 orElseThrow(() -> new IllegalArgumentException("주문이 없습니다"))로 거절한다

Order.java
- T3: 복사 생성자 Order(Order other)를 더한다. 칸을 하나씩 옮기고 항목 목록은 new ArrayList<>(other.items)로 새로 만든다
- 주문 항목과 주소는 바꿀 수 없는 값이라 그대로 같이 쓴다
- 복사 생성자를 두는 이유: 무엇을 복사하는지 눈에 보이고 학생이 따라 칠 수 있다

Main.java
- 첫 줄에 ===== 주문 저장소 =====를 찍고 앞 장 장면을 지운다. 장면마다 빈 줄과 [장면 이름] 줄로 나눈다
- T1: new OrderService(new MemoryOrderRepository())로 만든다
- T1 [주문하고 번호로 꺼내기]: placeOrder가 돌려준 번호를 orderId로 받아 "주문하기: " 번호 "번 주문 저장"을 찍고, findOrder로 꺼내 금액과 상태를 찍는다
- T1 [주문 번호로 결제하기]: orderService.pay(orderId) 전후 상태를 "주문 상태: " 전 -> 후로 찍는다
- T2 [없는 주문 결제하기]: orderService.pay(100)을 try catch로 감싸 "없는 주문 결제: " 거절 말을 찍는다
- T3 [꺼낸 주문만 바꾸기]: 이영희 주문을 꺼내 cancel() 하고 저장하지 않는다. "꺼낸 주문 상태: " 전 -> 후와 다시 꺼낸 "저장소 주문 상태: "를 찍는다

glossary.md
- T1: 주문 상태 묶음 뒤, 맨 끝에 주문 저장소 OrderRepository, 저장하기 save, 주문 번호로 찾기 findById 세 줄을 더한다

OrderRepositoryTest.java
- S1은 MemoryOrderRepository에 주문 두 개를 저장하고 번호로 꺼내 같은 주문인지, 없는 번호는 빈 결과인지 본다
- S2는 저장한 뒤 원래 주문을 바꿔도, 꺼낸 주문을 바꿔도 다시 꺼낸 주문이 그대로인지 보고, 저장하면 바뀐 것이 남는지 본다. Main의 저장소 주문 상태 줄을 본다
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

public Order(Order other) {
    this.id = other.id;
    this.customerName = other.customerName;
    this.items = new ArrayList<>(other.items);
    this.address = other.address;
    this.totalPrice = other.totalPrice;
    this.status = other.status;
}

@Override
public void save(Order order) {
    orders.put(order.getId(), new Order(order));
}

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | OrderRepository.java, MemoryOrderRepository.java |
| S2 | MemoryOrderRepository.java, Order.java, Main.java |
| S3 | OrderService.java, Main.java |
| S4 | OrderService.java, Main.java |
| S5 | OrderService.java, Main.java |
| S6 | glossary.md |

## 라이브러리
- 추가 없음

## 다른 장 파일
- ddd/src/ddd/Main.java: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status
- ddd/src/ddd/OrderService.java: anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status
- ddd/src/ddd/Order.java: anemic-order, rich-entity, value-object, order-aggregate, order-status
- ddd/glossary.md: ubiquitous-language, rich-entity, value-object, order-aggregate, order-status
- tests/ddd/AnemicOrderTest.java: anemic-order
- tests/ddd/UbiquitousLanguageTest.java: ubiquitous-language
- tests/ddd/RichEntityTest.java: rich-entity
- tests/ddd/ValueObjectTest.java: value-object
- tests/ddd/OrderAggregateTest.java: order-aggregate
- tests/ddd/OrderStatusTest.java: order-status
