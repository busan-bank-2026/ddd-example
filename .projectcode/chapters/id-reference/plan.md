# id-reference 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/order/domain/Member.java | 새로 | 회원 애그리거트. 회원 번호와 이름 |
| ddd/src/ddd/order/domain/Product.java | 새로 | 상품 애그리거트. 상품 번호, 이름, 가격 |
| ddd/src/ddd/order/domain/MemberRepository.java | 새로 | 회원 저장소 약속 |
| ddd/src/ddd/order/domain/ProductRepository.java | 새로 | 상품 저장소 약속 |
| ddd/src/ddd/order/infrastructure/MemoryMemberRepository.java | 새로 | HashMap 회원 저장소 |
| ddd/src/ddd/order/infrastructure/MemoryProductRepository.java | 새로 | HashMap 상품 저장소 |
| ddd/src/ddd/order/domain/Order.java | 고침 | 주문자 이름 대신 회원 번호를 갖는다 |
| ddd/src/ddd/order/domain/OrderItem.java | 고침 | 상품 번호를 더한다 |
| ddd/src/ddd/order/application/OrderRequest.java | 고침 | 주문자 이름 대신 회원 번호를 담는다 |
| ddd/src/ddd/order/application/OrderItemRequest.java | 고침 | 상품 번호를 더한다 |
| ddd/src/ddd/order/application/OrderService.java | 고침 | 요청의 회원 번호와 상품 번호를 주문과 주문 항목에 넣는다 |
| ddd/src/ddd/Main.java | 고침 | 이 장 장면 셋을 찍는다 |
| ddd/glossary.md | 고침 | 회원, 상품, 두 저장소, 회원 번호, 상품 번호 |
| tests/ddd/IdReferenceTest.java | 새로 | S1부터 S6까지 행동 테스트 |

## 코드 계획
Member.java, Product.java (도메인 계층)
- 주문과 같은 엔티티다. 번호를 갖고 equals와 hashCode를 번호로 비교한다
- 칸은 final로 두고 getter만 둔다. 이 장에는 회원과 상품을 고치는 동작이 없다
- 상품 가격은 int다
- 회원과 상품을 따로 두는 이유: 주문과 따로 만들어지고 따로 바뀐다. 각자 저장소를 갖는 애그리거트다
- 지금은 주문 묶음 ddd.order.domain에 둔다. 컨텍스트로 나누는 일은 bounded-contexts 장이다

MemberRepository.java, ProductRepository.java (도메인 계층)
- 주문 저장소와 같은 모양이다. save와 findById(long)가 Optional을 돌려준다

MemoryMemberRepository.java, MemoryProductRepository.java (인프라 계층)
- HashMap<Long, Member>, HashMap<Long, Product>에 번호로 넣는다. 같은 번호로 다시 저장하면 바뀐다
- findById는 Optional.ofNullable로 돌려준다
- 주문 저장소처럼 복사하지 않는다. 회원과 상품에는 바꾸는 메서드가 없어 꺼낸 것을 고칠 수 없다

Order.java
- String customerName 칸을 long memberId로 바꾼다. getCustomerName은 getMemberId가 된다
- 생성자는 (id, memberId, items, address)다. 복사 생성자도 memberId를 복사한다
- Member를 import 하지 않는다. 주문이 회원 객체를 쥐면 주문을 꺼낼 때 회원까지 딸려 오고, 주문을 저장할 때 회원도 함께 바뀔 수 있다

OrderItem.java
- 칸 맨 앞에 final long productId를 더하고 getProductId()를 둔다. productName과 price는 그대로 둔다
- 생성자는 (productId, productName, price, quantity)다
- 상품 이름과 가격은 주문 때 값을 복사해 담는다. 상품 가격이 바뀌어도 주문 항목 금액은 그대로다

OrderRequest.java, OrderItemRequest.java
- OrderRequest(long memberId, List<OrderItemRequest> items, String address)
- OrderItemRequest(long productId, String productName, int price, int quantity)
- 이 장에서는 상품 이름과 가격을 요청에 그대로 받는다. 상품 저장소에서 꺼내 채우는 일은 order-factory 장이다

OrderService.java
- new Order(nextId++, request.memberId(), items, ...)로 회원 번호를 넣는다
- 주문하기와 항목 더하기에서 new OrderItem(request.productId(), ...)로 상품 번호를 맨 앞에 넣는다
- 생성자는 그대로 주문 저장소 하나만 받는다

Main.java
- T1: 첫 줄을 ===== 회원과 상품 =====로 바꾸고 앞 장 장면을 지운다
- T1: 회원 저장소에 회원 둘(1 김철수, 2 이영희), 상품 저장소에 상품 둘(1 자바 책 15000, 2 볼펜 1000)을 저장한다. 변수 타입은 도메인의 저장소 인터페이스다
- T1: 장면 [회원과 상품 저장하기]는 1번 회원과 1번 상품을 꺼내 이름과 가격을 찍는다
- T2: 장면 [회원 번호와 상품 번호로 주문하기]는 회원 1이 상품 1 자바 책 2개, 상품 2 볼펜 1개를 주문한다
- T2: 주문 줄의 이름은 memberRepository.findById(order.getMemberId())로 찾은 회원의 이름이다. 주문 항목 줄에 상품 번호를 붙인다
- T3: 장면 [상품 가격이 바뀐 뒤 주문 보기]는 같은 번호 1로 자바 책을 18000원에 다시 저장하고, 상품 가격, 첫 주문 항목 가격, 주문 금액을 전 -> 후로 찍는다

glossary.md
- T1: 회원 Member, 회원 저장소 MemberRepository, 상품 Product, 상품 저장소 ProductRepository 줄을 더한다
- T2: 주문자 customerName 줄을 회원 번호 memberId 줄로 바꾸고, 상품 번호 productId 줄을 더한다. 주문 항목, 주문하기, 주문 요청, 주문 항목 요청의 뜻을 번호에 맞게 고친다

IdReferenceTest.java
- 패키지는 ddd다. 도메인, 응용, 인프라 클래스는 import로 부른다
- S1은 회원과 상품을 만들어 칸을 보고, 번호가 같으면 같고 다르면 다른지 본다
- S2는 두 메모리 저장소에 저장하고 번호로 꺼내는지, 없는 번호는 빈 결과인지, 같은 번호로 다시 저장하면 바뀌는지 보고, 저장소 인터페이스가 ddd.order.domain에, 구현이 ddd.order.infrastructure에 있는지, Main이 회원과 상품을 저장하는지 본다
- S3은 주문 요청의 회원 번호가 주문에 들어가는지, 주문 칸에 Member 타입이 없는지, Main 출력에 1번 회원 김철수 주문 줄이 있는지 본다
- S4는 주문 항목 요청의 상품 번호, 이름, 가격, 수량이 주문 항목에 들어가는지, 주문 항목 칸에 Product 타입이 없는지 본다
- S5는 주문 뒤 같은 번호의 상품을 새 가격으로 저장해도 주문 항목 가격과 주문 금액이 그대로인지, Main 출력의 가격 바뀐 뒤 장면을 본다
- S6은 용어집 줄을 본다

## 핵심 코드
public class Order {

    private long id;
    private long memberId;

public OrderItem(long productId, String productName, int price, int quantity) {

Order order = orderService.findOrder(orderId);
Member orderMember = memberRepository.findById(order.getMemberId()).orElseThrow();

productRepository.save(new Product(1, "자바 책", 18000));

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | Member.java, Product.java |
| S2 | MemberRepository.java, ProductRepository.java, MemoryMemberRepository.java, MemoryProductRepository.java, Main.java |
| S3 | Order.java, OrderRequest.java, OrderService.java, Main.java |
| S4 | OrderItem.java, OrderItemRequest.java, OrderService.java, Main.java |
| S5 | Main.java |
| S6 | glossary.md |

## 라이브러리
- 추가 없음

## 다른 장 파일
- ddd/src/ddd/Main.java: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers
- ddd/src/ddd/order/domain/Order.java: anemic-order, rich-entity, value-object, order-aggregate, order-status, order-repository, layers
- ddd/src/ddd/order/domain/OrderItem.java: order-aggregate, layers
- ddd/src/ddd/order/application/OrderRequest.java: place-order, layers
- ddd/src/ddd/order/application/OrderItemRequest.java: place-order, layers
- ddd/src/ddd/order/application/OrderService.java: anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers
- ddd/glossary.md: ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order
