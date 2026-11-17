# order-events 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/order/domain/Product.java | 고침 | 재고를 가지고, 수량만큼 재고를 줄인다 |
| ddd/src/ddd/order/domain/OrderPlaced.java | 새로 | 주문 접수됨 이벤트. 주문 번호와 주문 항목 |
| ddd/src/ddd/order/domain/Order.java | 고침 | 만들어질 때 주문 접수됨 이벤트를 남기고 내준다 |
| ddd/src/ddd/order/application/EventHandler.java | 새로 | 이벤트 처리기 인터페이스. 이벤트를 받는 메서드 하나 |
| ddd/src/ddd/order/application/EventPublisher.java | 새로 | 이벤트 발행기. 처리기 구독, 이벤트 쌓기, 처리하기 |
| ddd/src/ddd/order/application/OrderService.java | 고침 | 주문을 저장한 뒤 이벤트를 발행기에 넘긴다 |
| ddd/src/ddd/order/application/DecreaseStockHandler.java | 새로 | 주문 접수됨 이벤트를 골라 재고를 줄이고 저장한다 |
| ddd/src/ddd/Main.java | 고침 | 이 장 장면 둘을 찍는다 |
| ddd/glossary.md | 고침 | 재고, 주문 접수됨, 이벤트 발행기, 이벤트 처리기, 재고 줄이기, 재고 줄이기 처리기, 상품 줄 |
| tests/ddd/OrderEventsTest.java | 새로 | S1부터 S5까지 행동 테스트 |

## 코드 계획
Product.java (도메인 계층)
- T1: private int stock 칸을 더한다. 다른 칸은 final 그대로이고 stock만 바뀌는 칸이다
- T1: 생성자를 (id, name, price, stock)으로 바꾸고 getStock()을 더한다
- T3: decreaseStock(int quantity)는 stock에서 quantity를 뺀다. 모자란 재고 막기는 미룸이라 검사하지 않는다

OrderPlaced.java (T1, 도메인 계층)
- record OrderPlaced(long orderId, List<OrderItem> items)
- 따로 두는 이유: 주문에 일어난 일을 이름 붙은 값으로 남겨야 주문이 모르는 곳(재고)이 그 일을 듣고 따로 움직일 수 있다

Order.java (T1)
- private List<Object> events 칸을 더한다. 생성자 끝에서 new ArrayList<>()로 만들고 new OrderPlaced(id, List.copyOf(items))를 넣는다
- 복사 생성자는 new ArrayList<>(other.events)로 이벤트도 복사한다
- getEvents()는 List.copyOf(events)를 돌려준다
- 주문은 상품 저장소를 받지 않는다. 재고를 줄이지 않고 일어난 일만 남긴다
- 이벤트 목록 타입을 Object로 두는 이유: 주문이 남기는 이벤트가 하나로 정해져 있지 않다

EventHandler.java (T2, 응용 계층)
- interface EventHandler { void handle(Object event); }
- 따로 두는 이유: 발행기는 누가 이벤트를 받는지 몰라도 된다. 처리기 인터페이스 하나만 알면 된다

EventPublisher.java (T2, 응용 계층)
- 처리기 목록 List<EventHandler>와 쌓인 이벤트 List<Object>를 가진다
- subscribe(EventHandler handler): 처리기를 목록에 넣는다
- publish(List<Object> events): 쌓아 두기만 한다. 처리기를 부르지 않는다
- processEvents(): 쌓인 이벤트를 꺼내 비우고, 이벤트마다 모든 처리기의 handle을 한 번씩 부른다
- 응용 계층에 두는 이유: 응용 서비스가 쓰고, 응용 계층은 인프라 계층을 쓰지 않는다
- 쌓았다가 나중에 보내는 이유: 메시지 큐처럼 주문 저장과 재고 줄이기를 다른 때에 하는 것을 콘솔에 보이게 한다

OrderService.java (T2)
- 생성자 끝에 EventPublisher eventPublisher를 더한다
- placeOrder는 orderRepository.save(order) 다음 줄에서 eventPublisher.publish(order.getEvents())를 부른다
- 저장 뒤에 넘기는 이유: 저장한 주문의 일만 바깥에 알린다. 재고 줄이기는 이 메서드 안에서 하지 않는다

DecreaseStockHandler.java (T3, 응용 계층)
- EventHandler를 구현하고 ProductRepository를 받는다
- handle(Object event): 주문 접수됨이 아니면 돌아간다. 주문 접수됨이면 주문 항목마다 상품을 꺼내 decreaseStock(수량)을 시키고 저장한다
- 따로 두는 이유: 재고 줄이기는 주문과 다른 일이다. 주문 응용 서비스가 아니라 이벤트를 듣는 곳이 한다

Main.java
- T1: 첫 줄을 ===== 주문 뒤 재고 줄이기 =====로 바꾸고 앞 장 장면을 지운다
- T1: 김철수 회원과 재고 10개 자바 책을 저장한다
- T1: 장면 [주문이 주문 접수됨 이벤트 남기기]는 자바 책 2개를 주문하고 주문 번호, 이벤트 수, 자바 책 재고 전 -> 후를 찍는다
- T2: EventPublisher를 만들어 주문 응용 서비스에 넘긴다. 출력은 T1과 같다
- T3: new DecreaseStockHandler(productRepository)를 eventPublisher.subscribe로 잇는다
- T3: 장면 [이벤트를 처리해 재고 줄이기]는 eventPublisher.processEvents()를 부르고 자바 책 재고 전 -> 후를 찍는다

glossary.md
- T1: 재고 stock, 주문 접수됨 OrderPlaced 줄을 더하고 상품 줄에 재고를 넣는다
- T2: 이벤트 발행기 EventPublisher, 이벤트 처리기 EventHandler 줄을 더한다
- T3: 재고 줄이기 decreaseStock, 재고 줄이기 처리기 DecreaseStockHandler 줄을 더한다

OrderEventsTest.java
- 패키지는 ddd다. 도메인, 응용, 인프라 클래스는 import로 부른다
- S1은 재고 10개 상품의 getStock()이 10인지, Main 소스가 자바 책을 재고 10개로 저장하는지 본다
- S2는 새 주문의 이벤트가 하나이고 주문 번호와 주문 항목을 담은 주문 접수됨인지, 주문 칸에 상품이나 상품 저장소가 없는지 본다
- S3은 발행기가 publish 때는 처리기를 부르지 않고 processEvents 때 한 번만 부르는지, 주문 응용 서비스 생성자 끝이 발행기인지 본다
- S4는 decreaseStock으로 재고가 줄어드는지, 처리기가 다른 이벤트는 넘기는지, 주문하기 뒤 처리 전에는 재고가 그대로이고 처리 뒤 맞춰지는지, Main 출력 전체가 실행 결과와 같은지 본다
- S5는 용어집 줄을 본다

## 핵심 코드
this.events = new ArrayList<>();
events.add(new OrderPlaced(id, List.copyOf(items)));

orderRepository.save(order);
eventPublisher.publish(order.getEvents());

public void processEvents() {
    List<Object> events = new ArrayList<>(waitingEvents);
    waitingEvents.clear();
    for (Object event : events) {
        for (EventHandler handler : handlers) {
            handler.handle(event);
        }
    }
}

@Override
public void handle(Object event) {
    if (!(event instanceof OrderPlaced placed)) {
        return;
    }
    for (OrderItem item : placed.items()) {
        Product product = productRepository.findById(item.getProductId()).orElseThrow();
        product.decreaseStock(item.getQuantity());
        productRepository.save(product);
    }
}

eventPublisher.subscribe(new DecreaseStockHandler(productRepository));
eventPublisher.processEvents();

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | Product.java, Main.java |
| S2 | OrderPlaced.java, Order.java, Main.java |
| S3 | EventHandler.java, EventPublisher.java, OrderService.java, Main.java |
| S4 | Product.java, DecreaseStockHandler.java, Main.java |
| S5 | glossary.md |

## 라이브러리
- 추가 없음

## 다른 장 파일
- ddd/src/ddd/order/domain/Product.java: id-reference
- ddd/src/ddd/order/domain/Order.java: anemic-order, rich-entity, value-object, order-aggregate, order-status, order-repository, layers, id-reference, discount, free-shipping
- ddd/src/ddd/order/application/OrderService.java: anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference, order-factory, discount
- ddd/src/ddd/Main.java: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference, order-factory, discount, free-shipping
- ddd/glossary.md: ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, id-reference, order-factory, discount, free-shipping
