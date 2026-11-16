# id-reference 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/domain/Member.java | 새로 | 회원 애그리거트. 회원 번호와 이름 |
| ddd/src/ddd/domain/Product.java | 새로 | 상품 애그리거트. 상품 번호, 이름, 가격 |
| ddd/src/ddd/domain/MemberRepository.java | 새로 | 회원 저장소 약속 |
| ddd/src/ddd/domain/ProductRepository.java | 새로 | 상품 저장소 약속 |
| ddd/src/ddd/infrastructure/MemoryMemberRepository.java | 새로 | HashMap 회원 저장소 |
| ddd/src/ddd/infrastructure/MemoryProductRepository.java | 새로 | HashMap 상품 저장소 |
| ddd/src/ddd/domain/Order.java | 고침 | 주문자 이름 대신 회원 번호를 갖는다 |
| ddd/src/ddd/domain/OrderLine.java | 고침 | 상품 번호를 더한다 |
| ddd/src/ddd/application/PlaceOrderCommand.java | 고침 | 주문자 이름 대신 회원 번호를 담는다 |
| ddd/src/ddd/application/OrderLineCommand.java | 고침 | 상품 번호를 더한다 |
| ddd/src/ddd/application/OrderApplicationService.java | 고침 | 명령의 회원 번호와 상품 번호를 주문과 주문 항목에 넣는다 |
| ddd/src/ddd/Main.java | 고침 | 회원과 상품을 먼저 저장하고, 번호로 주문하고, 상품 가격이 바뀐 뒤 장면을 더한다 |
| ddd/glossary.md | 고침 | 회원, 상품, 두 저장소, 회원 번호, 상품 번호 |
| tests/ddd/AnemicOrderTest.java | 고침 | 이름 대신 회원 번호, 상품 번호로 부른다 |
| tests/ddd/UbiquitousLanguageTest.java | 고침 | 이름 대신 회원 번호, 상품 번호로 부른다 |
| tests/ddd/RichEntityTest.java | 고침 | 이름 대신 회원 번호, 상품 번호로 부른다 |
| tests/ddd/ValueObjectTest.java | 고침 | 이름 대신 회원 번호, 상품 번호로 부른다 |
| tests/ddd/OrderAggregateTest.java | 고침 | 이름 대신 회원 번호, 상품 번호로 부른다 |
| tests/ddd/OrderStatusTest.java | 고침 | 이름 대신 회원 번호, 상품 번호로 부른다 |
| tests/ddd/OrderRepositoryTest.java | 고침 | 이름 대신 회원 번호, 상품 번호로 부른다 |
| tests/ddd/PlaceOrderTest.java | 고침 | 이름 대신 회원 번호, 상품 번호로 부른다 |
| tests/ddd/LayersTest.java | 고침 | 기대 출력에 가격 바뀐 뒤 줄을 더한다 |
| tests/ddd/IdReferenceTest.java | 새로 | S1부터 S6까지 행동 테스트 |

## 코드 계획
Member.java, Product.java (도메인 계층)
- 주문과 같은 엔티티다. 번호를 갖고 equals와 hashCode를 번호로 비교한다
- 칸은 final로 두고 getter만 둔다. 이 장에는 회원과 상품을 고치는 동작이 없다
- 상품 가격은 돈 Money다
- 회원과 상품을 따로 두는 이유: 주문과 따로 만들어지고 따로 바뀐다. 각자 저장소를 갖는 애그리거트다

MemberRepository.java, ProductRepository.java (도메인 계층)
- 주문 저장소와 같은 모양이다. save와 findById(long)가 Optional을 돌려준다

MemoryMemberRepository.java, MemoryProductRepository.java (인프라 계층)
- HashMap<Long, Member>, HashMap<Long, Product>에 번호로 넣는다. 같은 번호로 다시 저장하면 바뀐다
- findById는 Optional.ofNullable로 돌려준다
- 주문 저장소처럼 복사하지 않는다. 회원과 상품에는 바꾸는 메서드가 없어 꺼낸 것을 고칠 수 없다

Order.java
- String customerName 칸을 long memberId로 바꾼다. getCustomerName은 getMemberId가 된다
- 생성자는 (id, memberId, orderLines, address)다
- Member를 import 하지 않는다. 주문이 회원 객체를 쥐면 주문을 꺼낼 때 회원까지 딸려 오고, 주문을 저장할 때 회원도 함께 바뀔 수 있다

OrderLine.java
- record 칸 맨 앞에 long productId를 더한다. productName과 price는 그대로 둔다
- 상품 이름과 가격은 주문 때 값을 복사해 담는다. 상품 가격이 바뀌어도 주문 항목 금액은 그대로다

PlaceOrderCommand.java, OrderLineCommand.java
- PlaceOrderCommand(long memberId, List<OrderLineCommand> orderLines, String address)
- OrderLineCommand(long productId, String productName, int price, int quantity)
- 이 장에서는 상품 이름과 가격을 명령에 그대로 받는다. 상품 저장소에서 꺼내 채우는 일은 order-factory 장이다

OrderApplicationService.java
- new Order(nextId++, command.memberId(), ...)로 회원 번호를 넣는다
- toOrderLine에서 command.productId()를 주문 항목 맨 앞에 넣는다
- 생성자는 그대로 주문 저장소 하나만 받는다

Main.java
- T1: 시작 글 다음에 회원 저장소와 상품 저장소를 만들고 회원 둘(1 김철수, 2 이영희), 상품 셋(1 자바 책 15000, 2 볼펜 1000, 3 공책 2000)을 저장한다. 변수 타입은 도메인의 저장소 인터페이스다
- T2: 주문하기 명령의 이름을 회원 번호로 바꾼다. 김철수 주문은 1, 나머지 장면은 2다. 주문 항목 명령 앞에 상품 번호를 넣는다
- T2: 첫 주문 줄의 이름은 memberRepository.findById(order.getMemberId())로 찾은 회원의 이름이다. 출력 글자는 그대로다
- T3: 맨 끝에 같은 번호 1로 자바 책을 18000원에 다시 저장하고, 첫 주문을 다시 꺼내 첫 주문 항목의 상품 번호로 상품을 찾아 지금 가격, 주문 항목 가격, 주문 금액을 한 줄로 보여준다

glossary.md
- T1: 회원 Member, 회원 저장소 MemberRepository, 상품 Product, 상품 저장소 ProductRepository 줄을 더한다
- T2: 주문자 customerName 줄을 회원 번호 memberId 줄로 바꾸고, 상품 번호 productId 줄을 더한다. 주문 항목, 주문하기, 주문하기 명령, 주문 항목 명령의 뜻을 번호에 맞게 고친다

IdReferenceTest.java
- 패키지는 ddd다. 도메인, 응용, 인프라 클래스는 import로 부른다
- S1은 회원과 상품을 만들어 칸을 보고, 번호가 같으면 같고 다르면 다른지 본다
- S2는 두 메모리 저장소에 저장하고 번호로 꺼내는지, 없는 번호는 빈 결과인지, 같은 번호로 다시 저장하면 바뀌는지 보고, 저장소 인터페이스가 ddd.domain에, 구현이 ddd.infrastructure에 있는지, Main이 회원과 상품을 저장하는지 본다
- S3은 주문하기 명령의 회원 번호가 주문에 들어가는지, 주문 칸에 Member 타입이 없는지, Main 출력에 김철수 주문 줄이 그대로인지 본다
- S4는 주문 항목 명령의 상품 번호, 이름, 가격, 수량이 주문 항목에 들어가는지, 주문 항목 칸에 Product 타입이 없는지 본다
- S5는 주문 뒤 같은 번호의 상품을 새 가격으로 저장해도 주문 항목 가격과 주문 금액이 그대로인지, Main 출력 마지막 줄을 본다
- S6은 용어집 줄을 본다

앞 장 테스트
- 주문하기 명령과 주문의 주문자 이름을 회원 번호로 바꾼다. 김철수는 1, 나머지는 2다
- 주문 항목 명령과 주문 항목 앞에 상품 번호를 넣는다. 자바 책 1, 볼펜 2, 공책 3, 연필 4다
- getCustomerName 확인은 getMemberId 확인으로 바꾼다
- LayersTest의 기대 출력에 마지막 줄을 더한다

## 핵심 코드
public class Order implements Serializable {

    private long id;
    private long memberId;

public record OrderLine(long productId, String productName, Money price, int quantity) implements Serializable {

Order order = orderService.findOrder(orderId);
Member member = memberRepository.findById(order.getMemberId()).orElseThrow();

productRepository.save(new Product(1, "자바 책", new Money(18000)));

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | Member.java, Product.java |
| S2 | MemberRepository.java, ProductRepository.java, MemoryMemberRepository.java, MemoryProductRepository.java, Main.java |
| S3 | Order.java, PlaceOrderCommand.java, OrderApplicationService.java, Main.java |
| S4 | OrderLine.java, OrderLineCommand.java, OrderApplicationService.java, Main.java |
| S5 | Main.java |
| S6 | glossary.md |

## 라이브러리
- 추가 없음

## 다른 장 파일
- ddd/src/ddd/Main.java: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers
- ddd/src/ddd/domain/Order.java: anemic-order, rich-entity, value-object, order-aggregate, order-status, order-repository, layers
- ddd/src/ddd/domain/OrderLine.java: order-aggregate, order-repository, layers
- ddd/src/ddd/application/PlaceOrderCommand.java: place-order, layers
- ddd/src/ddd/application/OrderLineCommand.java: place-order, layers
- ddd/src/ddd/application/OrderApplicationService.java: anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers
- ddd/glossary.md: ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order
- tests/ddd/AnemicOrderTest.java: anemic-order
- tests/ddd/UbiquitousLanguageTest.java: ubiquitous-language
- tests/ddd/RichEntityTest.java: rich-entity
- tests/ddd/ValueObjectTest.java: value-object
- tests/ddd/OrderAggregateTest.java: order-aggregate
- tests/ddd/OrderStatusTest.java: order-status
- tests/ddd/OrderRepositoryTest.java: order-repository
- tests/ddd/PlaceOrderTest.java: place-order
- tests/ddd/LayersTest.java: layers
