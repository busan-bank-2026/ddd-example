# place-order 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/OrderRequest.java | 새로 | 주문 요청. 주문자, 주문 항목 요청 목록, 배송 주소를 묶는다 |
| ddd/src/ddd/OrderItemRequest.java | 새로 | 주문 항목 요청. 상품 이름, 가격, 수량을 묶는다 |
| ddd/src/ddd/OrderService.java | 고침 | 주문 응용 서비스. 주문 요청과 주문 항목 요청을 받아 주문 항목과 주소로 바꾼다 |
| ddd/src/ddd/Main.java | 고침 | 앞 장 장면을 지우고 주문하기와 주문 항목 더하기를 요청으로 부르는 장면, 규칙에 맞지 않는 요청 장면을 찍는다 |
| ddd/glossary.md | 고침 | 주문 응용 서비스, 주문 요청, 주문 항목 요청을 더한다 |
| tests/ddd/AnemicOrderTest.java | 고침 | 주문 요청으로 주문한다 |
| tests/ddd/UbiquitousLanguageTest.java | 고침 | 주문 요청으로 주문한다 |
| tests/ddd/RichEntityTest.java | 고침 | 주문 요청으로 주문한다 |
| tests/ddd/ValueObjectTest.java | 고침 | 주문 요청으로 주문한다 |
| tests/ddd/OrderAggregateTest.java | 고침 | 주문 요청으로 주문한다. 받은 목록을 복사하는지는 주문을 직접 만들어 본다 |
| tests/ddd/OrderStatusTest.java | 고침 | 주문 요청으로 주문한다 |
| tests/ddd/OrderRepositoryTest.java | 고침 | 주문 요청으로 주문한다 |
| tests/ddd/PlaceOrderTest.java | 새로 | S1부터 S6까지 행동 테스트 |

## 코드 계획
OrderRequest.java
- record OrderRequest(String customerName, List<OrderItemRequest> items, String address)
- 값은 밖에서 들어온 그대로 둔다. 글자와 숫자만 담고 도메인 객체를 담지 않는다

OrderItemRequest.java
- record OrderItemRequest(String productName, int price, int quantity)
- 주문 항목으로 바꾸는 일은 응용 서비스가 한다

OrderService.java
- T1: placeOrder(OrderRequest request)로 바꾼다. for 문으로 주문 항목 요청마다 new OrderItem(상품 이름, 가격, 수량)을 만들어 목록에 담고, new Address(request.address())와 함께 new Order(nextId++, …)로 만든다. 저장하고 번호를 돌려준다
- T1: addItem(long orderId, OrderItemRequest request)로 바꾼다. 꺼내고, new OrderItem(…)을 주문에게 더하게 하고, 저장한다
- 검사나 if는 넣지 않는다. 틀린 값은 Address, OrderItem, Order가 거절한다
- 클래스 이름은 OrderService 그대로 둔다. 주문 응용 서비스라는 것은 용어집에 적는다

Main.java
- 첫 줄에 ===== 주문하기 유스케이스 =====를 찍고 앞 장 장면을 지운다. 장면마다 빈 줄과 [장면 이름] 줄로 나눈다
- T1 [주문 요청으로 주문하기]: new OrderRequest("김철수", List.of(new OrderItemRequest("자바 책", 15000, 2), new OrderItemRequest("볼펜", 1000, 1)), "서울시 강남구")로 주문한다. 꺼낸 주문의 번호, 금액, 상태와 항목 줄을 찍는다
- T1 [항목 요청으로 항목 더하기]: orderService.addItem(orderId, new OrderItemRequest("공책", 2000, 5)) 전후의 "항목 수: "와 "주문 금액: "을 전 -> 후로 찍는다
- T1 [규칙에 맞지 않는 요청 보내기]: 0개 항목 요청과 빈 주소 요청을 하나씩 try catch로 감싸 거절 말을 찍는다

glossary.md
- T2: 맨 끝에 주문 응용 서비스 OrderService, 주문 요청 OrderRequest, 주문 항목 요청 OrderItemRequest 세 줄을 더한다

PlaceOrderTest.java
- S1은 주문 요청과 주문 항목 요청이 담은 값을 보고, 그 요청으로 주문할 수 있는지 본다
- S2는 요청으로 두 번 주문하면 1, 2 번호가 오고 저장소에서 꺼낸 주문의 주문자, 주문 항목, 주소, 주문 금액이 요청과 같은지, Main의 주문 줄을 본다
- S3은 주문 항목 요청으로 항목을 더하면 저장소의 주문이 바뀌는지, Main의 항목 더하기 줄을 본다
- S4는 규칙에 맞지 않는 요청이 도메인 거절 말 그대로 거절되고 저장소가 비어 있는지 본다. Main의 거절 줄을 본다
- S5는 주문 응용 서비스로 결제, 배송, 배송 완료, 취소를 번호로 부른 뒤 저장소의 상태를 보고, 결제 없이 배송하면 주문의 거절 말 그대로 거절되는지 본다
- S6은 glossary.md에 세 줄이 있는지 본다

앞 장 테스트
- placeOrder(이름, List.of(new OrderItem(…)), 주소)를 placeOrder(new OrderRequest(이름, List.of(new OrderItemRequest(…)), 주소))로 바꾼다
- service.addItem(번호, new OrderItem(…))을 service.addItem(번호, new OrderItemRequest(…))로 바꾼다
- OrderAggregateTest S3의 받은 목록 확인은 new Order(…, given, …)로 주문을 직접 만들어 본다. 요청을 거치면 응용 서비스가 새 목록을 만들기 때문이다
- 주문에게 직접 시키는 확인은 그대로 둔다

## 핵심 코드
public record OrderRequest(String customerName, List<OrderItemRequest> items, String address) {
}

public record OrderItemRequest(String productName, int price, int quantity) {
}

public long placeOrder(OrderRequest request) {
    List<OrderItem> items = new ArrayList<>();
    for (OrderItemRequest itemRequest : request.items()) {
        items.add(new OrderItem(itemRequest.productName(), itemRequest.price(), itemRequest.quantity()));
    }
    Order order = new Order(nextId++, request.customerName(), items, new Address(request.address()));
    orderRepository.save(order);
    return order.getId();
}

public void addItem(long orderId, OrderItemRequest request) {
    Order order = findOrder(orderId);
    order.addItem(new OrderItem(request.productName(), request.price(), request.quantity()));
    orderRepository.save(order);
}

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | OrderRequest.java, OrderItemRequest.java, OrderService.java, Main.java |
| S2 | OrderService.java, Main.java |
| S3 | OrderService.java, Main.java |
| S4 | OrderService.java, Main.java |
| S5 | OrderService.java, glossary.md |
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
