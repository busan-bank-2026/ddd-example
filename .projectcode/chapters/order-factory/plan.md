# order-factory 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/domain/OrderLineFactory.java | 새로 | 상품 번호와 수량으로 상품 저장소에서 상품을 찾아 주문 항목을 만든다 |
| ddd/src/ddd/application/OrderLineCommand.java | 고침 | 상품 번호와 수량만 담는다 |
| ddd/src/ddd/application/OrderApplicationService.java | 고침 | 주문 항목 팩토리를 받아 주문 항목을 만들게 한다 |
| ddd/src/ddd/Main.java | 고침 | 팩토리를 만들어 넘기고, 명령을 상품 번호와 수량으로 바꾸고, 없는 상품 주문 장면을 더한다 |
| ddd/glossary.md | 고침 | 주문 항목 팩토리, 주문 항목 명령 |
| tests/ddd/AnemicOrderTest.java | 고침 | 팩토리를 넘기고 명령을 상품 번호와 수량으로 부른다 |
| tests/ddd/UbiquitousLanguageTest.java | 고침 | 팩토리를 넘기고 명령을 상품 번호와 수량으로 부른다 |
| tests/ddd/RichEntityTest.java | 고침 | 팩토리를 넘기고 명령을 상품 번호와 수량으로 부른다 |
| tests/ddd/ValueObjectTest.java | 고침 | 팩토리를 넘기고, 음수 가격 거절은 상품의 돈에서 확인한다 |
| tests/ddd/OrderAggregateTest.java | 고침 | 팩토리를 넘기고 명령을 상품 번호와 수량으로 부른다 |
| tests/ddd/OrderStatusTest.java | 고침 | 팩토리를 넘기고 명령을 상품 번호와 수량으로 부른다 |
| tests/ddd/OrderRepositoryTest.java | 고침 | 팩토리를 넘기고 명령을 상품 번호와 수량으로 부른다 |
| tests/ddd/PlaceOrderTest.java | 고침 | 팩토리를 넘기고, 명령 칸 확인과 음수 가격 거절을 지금 모양에 맞춘다 |
| tests/ddd/LayersTest.java | 고침 | 응용 서비스 생성자 칸과 기대 출력에 없는 상품 주문 줄을 맞춘다 |
| tests/ddd/IdReferenceTest.java | 고침 | 팩토리를 넘기고 명령을 상품 번호와 수량으로 부른다 |
| tests/ddd/OrderFactoryTest.java | 새로 | S1부터 S5까지 행동 테스트 |

## 코드 계획
OrderLineFactory.java (도메인 계층)
- 칸은 final ProductRepository 하나다. 생성자로 받는다
- create(long productId, int quantity)가 상품을 찾아 new OrderLine(상품 번호, 상품 이름, 상품 가격, 수량)을 돌려준다
- T1은 findById(productId).orElseThrow()로 찾는다. T2에서 orElseThrow(() -> new IllegalArgumentException("상품이 없습니다"))로 바꾼다
- 따로 두는 이유: 주문 항목을 만들려면 다른 애그리거트인 상품을 저장소에서 꺼내야 한다. 주문 항목 record와 주문은 저장소를 모르게 두고, 만드는 일만 팩토리가 맡는다
- 도메인 계층에 두는 이유: 상품의 이름과 지금 가격으로 주문 항목을 만든다는 것은 업무 규칙이다. 저장소 약속 ProductRepository만 쓰고 인프라는 모른다

OrderLineCommand.java
- OrderLineCommand(long productId, int quantity)

OrderApplicationService.java
- 칸 final OrderLineFactory orderLineFactory를 더한다. 생성자는 (OrderRepository, OrderLineFactory)다
- placeOrder와 addOrderLine에서 orderLineFactory.create(command.productId(), command.quantity())를 부른다
- private toOrderLine을 지운다. 만드는 일은 팩토리가 한다. Money import도 지운다
- 주문 번호 nextId와 new Order는 그대로 둔다

Main.java
- T1: new OrderApplicationService(new MemoryOrderRepository(), new OrderLineFactory(productRepository))
- T1: 모든 OrderLineCommand를 (상품 번호, 수량)으로 바꾼다. 출력 글자는 그대로다
- T1: 음수 금액 주문 장면은 try 안에서 productRepository.save(new Product(4, "할인 책", new Money(-15000)))를 먼저 하고 상품 4번 2개를 주문한다. 돈이 거절해 출력은 그대로다
- T2: 없는 주문 결제 장면 다음에 상품 100번 1개를 주문하는 장면을 더한다. 거절 메시지를 찍는다

glossary.md
- T1: 주문 항목 팩토리 OrderLineFactory 줄을 더하고, 주문 항목 명령 뜻을 상품 번호, 수량 묶음으로 바꾼다

OrderFactoryTest.java
- 패키지는 ddd다. 도메인, 응용, 인프라 클래스는 import로 부른다
- S1은 주문 항목 명령의 칸이 productId long, quantity int 둘뿐인지 본다
- S2는 상품 저장소로 만든 팩토리가 이름과 가격을 담은 주문 항목을 만드는지, 상품을 새 가격으로 다시 저장하면 새 가격으로 만드는지, 팩토리가 ddd.domain에 있는지 본다
- S3은 응용 서비스 생성자 칸이 주문 저장소와 팩토리인지, 주문하기와 항목 더하기가 상품 저장소의 이름과 가격을 담는지, Main 소스가 팩토리를 만드는지, Main 출력에 첫 주문 줄이 그대로인지 본다
- S4는 없는 상품으로 주문하기와 항목 더하기가 상품이 없습니다로 거절되고 저장되지 않는지, Main 출력 전체가 실행 결과와 같은지 본다
- S5는 용어집 줄을 본다

앞 장 테스트
- 파일마다 상품 1 자바 책 15000, 2 볼펜 1000, 3 공책 2000을 저장한 팩토리를 만드는 private static 메서드를 두고 응용 서비스에 넘긴다
- OrderLineCommand(번호, 이름, 가격, 수량)을 OrderLineCommand(번호, 수량)으로 바꾼다
- 음수 가격 명령 거절은 new Money(-15000)를 담은 상품 만들기 거절로, 음수 가격 항목 더하기 거절은 0개 항목 더하기 거절로 바꾼다
- 명령의 이름과 가격 확인은 상품 번호와 수량 확인으로 바꾼다
- LayersTest는 생성자 칸에 팩토리를 더하고 기대 출력에 없는 상품 주문 줄을 넣는다

## 핵심 코드
public OrderLine create(long productId, int quantity) {
    Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다"));
    return new OrderLine(product.getId(), product.getName(), product.getPrice(), quantity);
}

orderLines.add(orderLineFactory.create(orderLineCommand.productId(), orderLineCommand.quantity()));

OrderApplicationService orderService = new OrderApplicationService(new MemoryOrderRepository(),
        new OrderLineFactory(productRepository));

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | OrderLineCommand.java, Main.java |
| S2 | OrderLineFactory.java |
| S3 | OrderApplicationService.java, Main.java |
| S4 | OrderLineFactory.java, Main.java |
| S5 | glossary.md |

## 라이브러리
- 추가 없음

## 다른 장 파일
- ddd/src/ddd/Main.java: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference
- ddd/src/ddd/application/OrderLineCommand.java: place-order, layers, id-reference
- ddd/src/ddd/application/OrderApplicationService.java: anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference
- ddd/glossary.md: ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, id-reference
- tests/ddd/AnemicOrderTest.java: anemic-order
- tests/ddd/UbiquitousLanguageTest.java: ubiquitous-language
- tests/ddd/RichEntityTest.java: rich-entity
- tests/ddd/ValueObjectTest.java: value-object
- tests/ddd/OrderAggregateTest.java: order-aggregate
- tests/ddd/OrderStatusTest.java: order-status
- tests/ddd/OrderRepositoryTest.java: order-repository
- tests/ddd/PlaceOrderTest.java: place-order
- tests/ddd/LayersTest.java: layers
- tests/ddd/IdReferenceTest.java: id-reference
