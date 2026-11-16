# order-events 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/domain/Product.java | 고침 | 재고를 가지고, 수량만큼 재고를 줄인다 |
| ddd/src/ddd/domain/OrderPlaced.java | 새로 | 주문 접수됨 이벤트. 주문 번호와 주문 항목 |
| ddd/src/ddd/domain/Order.java | 고침 | 만들어질 때 주문 접수됨 이벤트를 남기고 내준다 |
| ddd/src/ddd/application/EventPublisher.java | 새로 | 이벤트 발행기. 구독, 쌓기, 처리하기. 붙여넣기 |
| ddd/src/ddd/application/OrderApplicationService.java | 고침 | 주문을 저장한 뒤 이벤트를 발행기에 넘긴다 |
| ddd/src/ddd/application/DecreaseStockHandler.java | 새로 | 주문 접수됨 이벤트를 받아 재고를 줄이고 저장한다 |
| ddd/src/ddd/Main.java | 고침 | 상품 재고 10개, 발행기와 처리기 잇기, 주문 뒤 줄과 이벤트 처리 뒤 줄 |
| ddd/glossary.md | 고침 | 재고, 주문 접수됨, 이벤트 발행기, 재고 줄이기, 재고 줄이기 처리기, 상품 줄 |
| tests/ddd/OrderEventsTest.java | 새로 | S1부터 S5까지 행동 테스트 |
| tests/ddd/AnemicOrderTest.java | 고침 | 상품에 재고, 주문 응용 서비스에 발행기를 넘긴다 |
| tests/ddd/UbiquitousLanguageTest.java | 고침 | 상품에 재고, 주문 응용 서비스에 발행기를 넘긴다 |
| tests/ddd/RichEntityTest.java | 고침 | 상품에 재고, 주문 응용 서비스에 발행기를 넘긴다 |
| tests/ddd/ValueObjectTest.java | 고침 | 상품에 재고, 주문 응용 서비스에 발행기를 넘긴다 |
| tests/ddd/OrderAggregateTest.java | 고침 | 상품에 재고, 주문 응용 서비스에 발행기를 넘긴다 |
| tests/ddd/OrderStatusTest.java | 고침 | 상품에 재고, 주문 응용 서비스에 발행기를 넘긴다 |
| tests/ddd/OrderRepositoryTest.java | 고침 | 상품에 재고, 주문 응용 서비스에 발행기를 넘긴다 |
| tests/ddd/PlaceOrderTest.java | 고침 | 상품에 재고, 주문 응용 서비스에 발행기를 넘긴다 |
| tests/ddd/LayersTest.java | 고침 | 생성자 매개변수에 발행기, 기대 출력에 두 줄 |
| tests/ddd/IdReferenceTest.java | 고침 | 상품에 재고, 주문 응용 서비스에 발행기, Main 상품 글자에 재고 |
| tests/ddd/OrderFactoryTest.java | 고침 | 상품에 재고, 생성자 매개변수에 발행기, 기대 출력에 두 줄 |
| tests/ddd/DiscountTest.java | 고침 | 상품에 재고, 생성자 매개변수에 발행기, 기대 출력에 두 줄 |
| tests/ddd/FreeShippingTest.java | 고침 | 기대 출력에 두 줄 |

## 코드 계획
Product.java (도메인 계층)
- T1: private int stock 칸을 더한다. 다른 칸은 final 그대로이고 stock만 바뀌는 칸이다
- T1: 생성자를 (id, name, price, stock)으로 바꾸고 getStock()을 더한다
- T3: decreaseStock(int quantity)는 stock에서 quantity를 뺀다. 모자란 재고 막기는 미룸이라 검사하지 않는다

OrderPlaced.java (T1, 도메인 계층)
- record OrderPlaced(long orderId, List<OrderLine> orderLines) implements Serializable
- 따로 두는 이유: 주문에 일어난 일을 이름 붙은 값으로 남겨야 주문이 모르는 곳(재고)이 그 일을 듣고 따로 움직일 수 있다
- 주문이 복사 저장되므로 Serializable을 붙인다

Order.java (T1)
- private List<Object> events 칸을 더한다. 생성자 끝에서 new ArrayList<>()로 만들고 new OrderPlaced(id, List.copyOf(orderLines))를 넣는다
- getEvents()는 List.copyOf(events)를 돌려준다
- 주문은 상품 저장소를 받지 않는다. 재고를 줄이지 않고 일어난 일만 남긴다
- 이벤트 목록 타입을 Object로 두는 이유: 주문이 남기는 이벤트가 하나로 정해져 있지 않다. 발행기도 이벤트 모양을 묻지 않는다

EventPublisher.java (T2, 응용 계층, 붙여넣기)
- 구독자 Map<Class<?>, List<Consumer<Object>>>와 쌓인 이벤트 List<Object>를 가진다
- subscribe(Class<T> eventType, Consumer<T> subscriber): 그 이벤트 타입의 구독자로 넣는다
- publish(List<?> events): 쌓아 두기만 한다. 구독자를 부르지 않는다
- processEvents(): 쌓인 이벤트를 꺼내 비우고, 이벤트마다 그 타입의 구독자에게 한 번씩 보낸다
- 응용 계층에 두는 이유: 응용 서비스가 쓰고, 응용 계층은 인프라 계층을 쓰지 않는다
- 쌓았다가 나중에 보내는 이유: 메시지 큐처럼 주문 저장과 재고 줄이기를 다른 때에 하는 것을 콘솔 한 줄씩 보이게 한다

OrderApplicationService.java (T2)
- 생성자 끝에 EventPublisher eventPublisher를 더한다
- placeOrder는 orderRepository.save(order) 다음 줄에서 eventPublisher.publish(order.getEvents())를 부른다
- 저장 뒤에 넘기는 이유: 저장한 주문의 일만 바깥에 알린다. 재고 줄이기는 이 메서드 안에서 하지 않는다

DecreaseStockHandler.java (T3, 응용 계층)
- ProductRepository를 받는다
- handle(OrderPlaced event): 주문 항목마다 상품을 꺼내 decreaseStock(수량)을 시키고 저장한다
- 따로 두는 이유: 재고 줄이기는 주문과 다른 일이다. 주문 응용 서비스가 아니라 이벤트를 듣는 곳이 한다

Main.java
- T1: 상품 저장 세 줄과 할인 책 줄에 재고 10을 더한다. 가격 바뀐 뒤 장면의 상품 저장 줄에도 재고 10을 더한다
- T1: 첫 주문 항목 반복 다음에 order.getEvents().size()와 1번 상품의 getStock()을 주문 뒤: 이벤트 1개, 자바 책 재고 10개로 찍는다
- T2: EventPublisher eventPublisher를 만들어 주문 응용 서비스에 넘긴다. 출력은 T1과 같다
- T3: DecreaseStockHandler를 만들어 eventPublisher.subscribe(OrderPlaced.class, decreaseStockHandler::handle)로 잇는다
- T3: 주문 뒤 줄 다음에 eventPublisher.processEvents()를 부르고 이벤트 처리 뒤: 자바 책 재고 8개를 찍는다

glossary.md
- T1: 재고 stock, 주문 접수됨 OrderPlaced 줄을 더하고 상품 줄에 재고를 넣는다
- T2: 이벤트 발행기 EventPublisher 줄을 더한다
- T3: 재고 줄이기 decreaseStock, 재고 줄이기 처리기 DecreaseStockHandler 줄을 더한다

OrderEventsTest.java
- 패키지는 ddd다. 도메인과 응용 클래스는 import로 부른다
- S1은 재고 10개 상품의 getStock()이 10인지, Main 글자에 자바 책 재고 10 저장이 첫 주문 전에 있는지 본다
- S2는 새 주문의 이벤트가 하나이고 주문 번호와 주문 항목을 담은 주문 접수됨인지, 주문 접수됨이 ddd.domain의 record인지, 주문 칸에 상품이나 상품 저장소가 없는지, Main 출력에서 볼펜 항목 줄 다음이 주문 뒤 줄인지 본다
- S3은 발행기가 publish 때는 구독자를 부르지 않고 processEvents 때 한 번만 부르는지, 다른 타입 구독자는 부르지 않는지, 주문 응용 서비스 생성자 끝이 발행기인지, 주문하기 뒤 주문은 저장소에 있고 처리 뒤 구독자가 그 주문 번호의 주문 접수됨을 받는지 본다
- S4는 decreaseStock으로 재고가 줄어드는지, 처리기가 항목마다 재고를 줄이고 저장하는지, 주문하기 뒤 처리 전에는 재고가 그대로이고 처리 뒤 맞춰지는지, Main 출력 전체가 실행 결과와 같은지 본다
- S5는 용어집 줄을 본다

앞 장 테스트
- new Product(번호, 이름, 가격)에 재고 10을 더한다
- new OrderApplicationService(...)에 new EventPublisher()를 더하고 import ddd.application.EventPublisher를 넣는다
- 생성자 매개변수 목록을 보는 DiscountTest, OrderFactoryTest, LayersTest에 EventPublisher를 더한다
- IdReferenceTest가 보는 Main 글자를 new Product(1, "자바 책", new Money(15000), 10)으로 바꾼다
- 기대 출력 전체를 보는 DiscountTest, LayersTest, OrderFactoryTest, FreeShippingTest에 두 줄을 넣는다

## 핵심 코드
this.events = new ArrayList<>();
events.add(new OrderPlaced(id, List.copyOf(orderLines)));

orderRepository.save(order);
eventPublisher.publish(order.getEvents());

public void handle(OrderPlaced event) {
    for (OrderLine orderLine : event.orderLines()) {
        Product product = productRepository.findById(orderLine.productId()).orElseThrow();
        product.decreaseStock(orderLine.quantity());
        productRepository.save(product);
    }
}

eventPublisher.subscribe(OrderPlaced.class, decreaseStockHandler::handle);
eventPublisher.processEvents();

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | Product.java, Main.java |
| S2 | OrderPlaced.java, Order.java, Main.java |
| S3 | EventPublisher.java, OrderApplicationService.java, Main.java |
| S4 | Product.java, DecreaseStockHandler.java, Main.java |
| S5 | glossary.md |

## 라이브러리
- 추가 없음. java.util.function.Consumer는 자바 기본이다

## 다른 장 파일
- ddd/src/ddd/domain/Product.java: id-reference
- ddd/src/ddd/domain/Order.java: anemic-order, rich-entity, value-object, order-aggregate, order-status, order-repository, layers, id-reference, discount, free-shipping
- ddd/src/ddd/application/OrderApplicationService.java: anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference, order-factory, discount
- ddd/src/ddd/Main.java: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference, order-factory, discount, free-shipping
- ddd/glossary.md: ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, id-reference, order-factory, discount, free-shipping
- tests/ddd 아래 앞 장 테스트 13개: anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference, order-factory, discount, free-shipping
