# order-factory 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/order/domain/OrderItemFactory.java | 새로 | 상품 번호와 수량으로 상품 저장소에서 상품을 찾아 주문 항목을 만든다 |
| ddd/src/ddd/order/application/OrderItemRequest.java | 고침 | 상품 번호와 수량만 담는다 |
| ddd/src/ddd/order/application/OrderService.java | 고침 | 주문 항목 팩토리를 받아 주문 항목을 만들게 한다 |
| ddd/src/ddd/Main.java | 고침 | 이 장 장면 둘을 찍는다 |
| ddd/glossary.md | 고침 | 주문 항목 팩토리, 주문 항목 요청 |
| tests/ddd/OrderFactoryTest.java | 새로 | S1부터 S5까지 행동 테스트 |

## 코드 계획
OrderItemFactory.java (도메인 계층)
- 칸은 final ProductRepository 하나다. 생성자로 받는다
- create(long productId, int quantity)가 상품을 찾아 new OrderItem(상품 번호, 상품 이름, 상품 가격, 수량)을 돌려준다
- T1은 findById(productId).orElseThrow()로 찾는다. T2에서 orElseThrow(() -> new IllegalArgumentException("상품이 없습니다"))로 바꾼다
- 따로 두는 이유: 주문 항목을 만들려면 다른 애그리거트인 상품을 저장소에서 꺼내야 한다. 주문 항목과 주문은 저장소를 모르게 두고, 만드는 일만 팩토리가 맡는다
- 도메인 계층에 두는 이유: 상품의 이름과 지금 가격으로 주문 항목을 만든다는 것은 업무 규칙이다. 저장소 약속 ProductRepository만 쓰고 인프라는 모른다

OrderItemRequest.java
- OrderItemRequest(long productId, int quantity)

OrderService.java
- 칸 final OrderItemFactory orderItemFactory를 더한다. 생성자는 (OrderRepository, OrderItemFactory)다
- placeOrder와 addItem에서 orderItemFactory.create(request.productId(), request.quantity())를 부른다
- 주문 번호 nextId와 new Order는 그대로 둔다

Main.java
- T1: 첫 줄을 ===== 상품으로 주문 항목 만들기 =====로 바꾸고 앞 장 장면을 지운다. 회원 저장소는 이 장 장면에 쓰지 않아 지운다
- T1: 상품 저장소에 1 자바 책 15000, 2 볼펜 1000을 저장하고 new OrderService(new MemoryOrderRepository(), new OrderItemFactory(productRepository))로 잇는다
- T1: 장면 [상품 번호와 수량으로 주문하기]는 상품 1을 2개, 상품 2를 1개 주문하고 주문 줄과 주문 항목 줄을 찍는다. 주문 항목 줄에 상품 저장소에서 온 이름과 가격을 보인다
- T2: 장면 [없는 상품으로 주문하기]는 상품 100번 1개를 주문하고 거절 메시지를 찍는다

glossary.md
- T1: 주문 항목 팩토리 OrderItemFactory 줄을 더하고, 주문 항목 요청 뜻을 상품 번호, 수량 묶음으로 바꾼다

OrderFactoryTest.java
- 패키지는 ddd다. 도메인, 응용, 인프라 클래스는 import로 부른다
- S1은 주문 항목 요청의 칸이 productId long, quantity int 둘뿐인지 본다
- S2는 상품 저장소로 만든 팩토리가 이름과 가격을 담은 주문 항목을 만드는지, 상품을 새 가격으로 다시 저장하면 새 가격으로 만드는지, 팩토리가 ddd.order.domain에 있는지 본다
- S3은 응용 서비스 생성자 칸이 주문 저장소와 팩토리인지, 주문하기와 항목 더하기가 상품 저장소의 이름과 가격을 담는지, Main 소스가 팩토리를 만드는지 본다
- S4는 없는 상품으로 주문하기와 항목 더하기가 상품이 없습니다로 거절되고 저장되지 않는지, Main 출력 전체가 실행 결과와 같은지 본다
- S5는 용어집 줄을 본다

## 핵심 코드
public OrderItem create(long productId, int quantity) {
    Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다"));
    return new OrderItem(product.getId(), product.getName(), product.getPrice(), quantity);
}

items.add(orderItemFactory.create(itemRequest.productId(), itemRequest.quantity()));

OrderService orderService = new OrderService(new MemoryOrderRepository(),
        new OrderItemFactory(productRepository));

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | OrderItemRequest.java, Main.java |
| S2 | OrderItemFactory.java |
| S3 | OrderService.java, Main.java |
| S4 | OrderItemFactory.java, Main.java |
| S5 | glossary.md |

## 라이브러리
- 추가 없음

## 다른 장 파일
- ddd/src/ddd/Main.java: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference
- ddd/src/ddd/order/application/OrderItemRequest.java: place-order, layers, id-reference
- ddd/src/ddd/order/application/OrderService.java: anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference
- ddd/glossary.md: ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, id-reference
