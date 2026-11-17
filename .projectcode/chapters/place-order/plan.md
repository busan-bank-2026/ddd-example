# place-order 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/OrderRequest.java | 새로 | 주문 요청. 주문자, 주문 항목 요청 목록, 배송 주소를 묶는다 |
| ddd/src/ddd/OrderItemRequest.java | 새로 | 주문 항목 요청. 상품 이름, 가격, 수량을 묶는다 |
| ddd/src/ddd/OrderService.java | 고침 | T1에서 명령을 받는다. T2에서 OrderApplicationService.java로 이름을 바꾼다 |
| ddd/src/ddd/OrderApplicationService.java | 새로 | OrderService.java의 새 이름. 꺼내기, 주문에게 시키기, 저장하기만 하는 주문 응용 서비스 |
| ddd/src/ddd/Main.java | 고침 | 주문하기와 주문 항목 더하기를 명령으로 부르고, 주문 응용 서비스를 쓴다 |
| ddd/glossary.md | 고침 | 주문 응용 서비스, 주문 요청, 주문 항목 요청을 더한다 |
| tests/ddd/AnemicOrderTest.java | 고침 | 주문 응용 서비스와 명령으로 주문한다 |
| tests/ddd/UbiquitousLanguageTest.java | 고침 | 주문 응용 서비스와 명령으로 주문한다 |
| tests/ddd/RichEntityTest.java | 고침 | 주문 응용 서비스와 명령으로 주문한다 |
| tests/ddd/ValueObjectTest.java | 고침 | 주문 응용 서비스와 명령으로 주문한다 |
| tests/ddd/OrderAggregateTest.java | 고침 | 주문 응용 서비스와 명령으로 주문한다. 받은 목록을 복사하는지는 주문을 직접 만들어 본다 |
| tests/ddd/OrderStatusTest.java | 고침 | 주문 응용 서비스와 명령으로 주문한다 |
| tests/ddd/OrderRepositoryTest.java | 고침 | 주문 응용 서비스와 명령으로 주문한다 |
| tests/ddd/PlaceOrderTest.java | 새로 | S1부터 S6까지 행동 테스트 |

## 코드 계획
OrderRequest.java
- record OrderRequest(String customerName, List<OrderItemRequest> items, String address)
- 값은 밖에서 들어온 그대로 둔다. 글자와 숫자만 담고 도메인 객체를 담지 않는다

OrderItemRequest.java
- record OrderItemRequest(String productName, int price, int quantity)
- 가격은 int로 받는다. 돈으로 바꾸는 일은 응용 서비스가 한다

OrderService.java
- T1: placeOrder(OrderRequest command)로 바꾼다. 주문 항목 요청마다 주문 항목으로 바꿔 목록에 담고, new Address(command.address())와 함께 new Order(nextId++, …)로 만든다. 저장하고 번호를 돌려준다
- T1: addItem(long orderId, OrderItemRequest command)로 바꾼다. 꺼내고, toOrderItem(command)를 주문에게 더하게 하고, 저장한다
- T1: private OrderItem toOrderItem(OrderItemRequest command)를 더한다. 주문하기와 주문 항목 더하기가 같은 바꾸기를 쓰므로 나눈다
- 검사나 if는 넣지 않는다. 틀린 값은 Money, Address, OrderItem, Order가 거절한다
- T2: git mv로 OrderApplicationService.java로 바꾸고 클래스 이름과 생성자 이름을 OrderApplicationService로 바꾼다. 내용은 그대로다
- 이름을 바꾸는 이유: discount 장에서 도메인 서비스가 생긴다. 서비스라는 이름만으로는 어느 쪽인지 모른다

Main.java
- T1: 첫 주문의 items를 List<OrderItemRequest>로 바꾸고 new OrderItemRequest("자바 책", 15000, 2)처럼 쓴다
- T1: 모든 placeOrder를 orderService.placeOrder(new OrderRequest(이름, 항목 명령 목록, 주소))로 부른다
- T1: 두 번의 addItem을 new OrderItemRequest("공책", 2000, 5)처럼 명령으로 부른다
- T2: OrderApplicationService orderService = new OrderApplicationService(new MemoryOrderRepository())로 바꾼다. 변수 이름 orderService는 그대로 둔다
- 출력 글자와 장면은 앞 장과 같다. 새 장면은 더하지 않는다

glossary.md
- T2: 맨 끝에 주문 응용 서비스 OrderApplicationService, 주문 요청 OrderRequest, 주문 항목 요청 OrderItemRequest 세 줄을 더한다

PlaceOrderTest.java
- S1은 주문 요청과 주문 항목 요청이 담은 값을 보고, 그 명령으로 주문할 수 있는지 본다
- S2는 명령으로 두 번 주문하면 1, 2 번호가 오고 저장소에서 꺼낸 주문의 주문자, 주문 항목, 주소, 주문 금액이 명령과 같은지, Main의 주문 줄을 본다
- S3은 주문 항목 요청으로 항목을 더하면 저장소의 주문이 바뀌는지, Main의 항목 더하기 줄을 본다
- S4는 음수 가격, 빈 주소, 빈 항목, 0개 항목 명령이 도메인 거절 말 그대로 거절되고 저장소가 비어 있는지 본다. Main의 네 거절 줄을 본다
- S5는 주문 응용 서비스로 결제, 배송, 배송 완료, 취소를 번호로 부른 뒤 저장소의 상태를 보고, 결제 없이 배송하면 주문의 거절 말 그대로 거절되는지 본다
- S6은 glossary.md에 세 줄이 있는지 본다

앞 장 테스트
- new OrderService(…)를 new OrderApplicationService(…)로 바꾼다
- placeOrder(이름, List.of(new OrderItem(…)), 주소)를 placeOrder(new OrderRequest(이름, List.of(new OrderItemRequest(…)), 주소))로 바꾼다
- service.addItem(번호, new OrderItem(…))을 service.addItem(번호, new OrderItemRequest(…))로 바꾼다
- OrderAggregateTest S3의 받은 목록 확인은 new Order(…, given, …)로 주문을 직접 만들어 본다. 명령을 거치면 응용 서비스가 새 목록을 만들기 때문이다
- 주문에게 직접 시키는 확인은 그대로 둔다

## 핵심 코드
public record OrderRequest(String customerName, List<OrderItemRequest> items, String address) {
}

public long placeOrder(OrderRequest command) {
    List<OrderItem> items = new ArrayList<>();
    for (OrderItemRequest orderLineCommand : command.items()) {
        items.add(toOrderItem(orderLineCommand));
    }
    Order order = new Order(nextId++, command.customerName(), items, new Address(command.address()));
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
| S1 | OrderRequest.java, OrderItemRequest.java, OrderService.java, Main.java |
| S2 | OrderService.java, Main.java |
| S3 | OrderService.java, Main.java |
| S4 | OrderService.java, Main.java |
| S5 | OrderApplicationService.java, Main.java |
| S6 | glossary.md |

## 라이브러리
- 추가 없음

## 다른 장 파일
- ddd/src/ddd/Main.java: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository
- ddd/src/ddd/OrderService.java: anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository
- ddd/glossary.md: ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository
- tests/ddd/AnemicOrderTest.java: anemic-order
- tests/ddd/UbiquitousLanguageTest.java: ubiquitous-language
- tests/ddd/RichEntityTest.java: rich-entity
- tests/ddd/ValueObjectTest.java: value-object
- tests/ddd/OrderAggregateTest.java: order-aggregate
- tests/ddd/OrderStatusTest.java: order-status
- tests/ddd/OrderRepositoryTest.java: order-repository
