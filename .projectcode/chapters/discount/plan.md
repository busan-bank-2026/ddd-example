# discount 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/domain/MemberGrade.java | 새로 | 회원 등급 BASIC, VIP |
| ddd/src/ddd/domain/Member.java | 고침 | 회원 등급 칸을 더한다 |
| ddd/src/ddd/domain/DiscountPolicy.java | 새로 | 회원과 주문으로 할인 금액을 계산하는 도메인 서비스 |
| ddd/src/ddd/domain/Money.java | 고침 | 빼기 minus를 더한다 |
| ddd/src/ddd/domain/Order.java | 고침 | 결제할 때 할인 금액을 받아 기억하고 결제 금액을 내준다 |
| ddd/src/ddd/application/OrderApplicationService.java | 고침 | 결제하기에서 회원을 찾아 할인 정책에게 계산을 시키고 주문에게 결제를 시킨다 |
| ddd/src/ddd/Main.java | 고침 | 회원을 등급과 함께 저장하고, 할인 정책을 넘기고, 할인 장면을 더한다 |
| ddd/glossary.md | 고침 | 회원 등급, 일반, VIP, 할인 정책, 할인 금액, 결제 금액, 회원 |
| tests/ddd/AnemicOrderTest.java | 고침 | 응용 서비스에 회원 저장소와 할인 정책을 넘긴다 |
| tests/ddd/UbiquitousLanguageTest.java | 고침 | 응용 서비스에 회원 저장소와 할인 정책을 넘긴다 |
| tests/ddd/RichEntityTest.java | 고침 | 응용 서비스에 회원 저장소와 할인 정책을 넘기고, 주문 결제를 할인 금액과 함께 부른다 |
| tests/ddd/ValueObjectTest.java | 고침 | 응용 서비스에 회원 저장소와 할인 정책을 넘긴다 |
| tests/ddd/OrderAggregateTest.java | 고침 | 응용 서비스에 회원 저장소와 할인 정책을 넘긴다 |
| tests/ddd/OrderStatusTest.java | 고침 | 응용 서비스에 회원 저장소와 할인 정책을 넘긴다 |
| tests/ddd/OrderRepositoryTest.java | 고침 | 응용 서비스에 회원 저장소와 할인 정책을 넘기고, 주문 결제를 할인 금액과 함께 부른다 |
| tests/ddd/PlaceOrderTest.java | 고침 | 응용 서비스에 회원 저장소와 할인 정책을 넘긴다 |
| tests/ddd/LayersTest.java | 고침 | 응용 서비스 생성자 칸과 기대 출력에 할인 줄을 맞춘다 |
| tests/ddd/IdReferenceTest.java | 고침 | 회원을 등급과 함께 만들고 응용 서비스에 회원 저장소와 할인 정책을 넘긴다 |
| tests/ddd/OrderFactoryTest.java | 고침 | 응용 서비스 생성자 칸과 기대 출력에 할인 줄을 맞춘다 |
| tests/ddd/DiscountTest.java | 새로 | S1부터 S5까지 행동 테스트 |

## 코드 계획
MemberGrade.java (도메인 계층)
- enum MemberGrade { BASIC, VIP }
- 할인율은 넣지 않는다. 등급은 회원이 누구인지만 말하고, 얼마를 깎을지는 할인 정책이 정한다

Member.java
- 칸 final MemberGrade grade를 더한다. 생성자는 (id, name, grade)다
- getGrade()를 더한다. equals와 hashCode는 그대로 번호로 본다

DiscountPolicy.java (도메인 계층, 도메인 서비스)
- 칸이 없다. discount(Member member, Order order) 하나만 둔다
- VIP면 new Money(주문 금액 * 10 / 100), 아니면 new Money(0)을 돌려준다
- 따로 두는 이유: 할인은 회원 등급과 주문 금액 둘 다 봐야 한다. 회원에 두면 회원이 주문을 알아야 하고, 주문에 두면 주문이 회원 애그리거트를 알아야 한다. 어느 쪽에도 속하지 않는 규칙이라 도메인 서비스에 둔다
- 도메인 계층에 두는 이유: VIP는 10% 할인이라는 것은 업무 규칙이다. 응용 서비스와 달리 저장소를 꺼내지도 저장하지도 않고 계산만 한다
- 주문을 바꾸지 않는다. 계산한 돈을 돌려줄 뿐이다

Money.java
- minus(Money other)는 new Money(amount - other.amount)다. 0원보다 작으면 돈이 거절한다

Order.java
- 칸 private Money discountPrice를 더한다. 생성자에서 new Money(0)으로 둔다
- pay()를 pay(Money discountPrice)로 바꾼다. 상태 확인은 그대로 하고, 통과하면 할인 금액을 기억하고 결제 완료가 된다
- getDiscountPrice()와 getPaymentPrice()를 더한다. 결제 금액은 totalPrice.minus(discountPrice)로 그때 계산한다
- 결제 금액을 칸으로 두지 않는 이유: 결제 전에 항목을 더하면 주문 금액이 바뀐다. 계산해서 내주면 따로 맞출 것이 없다

OrderApplicationService.java
- 칸 final MemberRepository memberRepository, final DiscountPolicy discountPolicy를 더한다
- 생성자는 (OrderRepository, OrderLineFactory, MemberRepository, DiscountPolicy)다
- pay(orderId)는 주문을 꺼내고, memberRepository.findById(order.getMemberId()).orElseThrow()로 회원을 찾고, order.pay(discountPolicy.discount(member, order))를 부르고, 저장한다
- 응용 서비스는 꺼내기, 시키기, 저장하기만 한다. 10%라는 숫자는 여기에 없다
- 주문하기, 항목 더하기, 배송하기, 배송 완료하기, 취소하기는 그대로다

Main.java
- T1: 회원을 new Member(1, "김철수", MemberGrade.VIP), new Member(2, "이영희", MemberGrade.BASIC)로 저장한다. 출력은 그대로다
- T2: new OrderApplicationService(new MemoryOrderRepository(), new OrderLineFactory(productRepository), memberRepository, new DiscountPolicy())
- T2: 결제 줄 다음에 꺼낸 주문의 할인 금액과 결제 금액을 찍는다. 등급은 member.getGrade()를 그대로 찍는다

glossary.md
- T1: 회원 등급 MemberGrade, 일반 BASIC, VIP VIP 줄을 더하고 회원 뜻에 회원 등급을 넣는다
- T2: 할인 정책 DiscountPolicy, 할인 금액 discountPrice, 결제 금액 paymentPrice 줄을 더한다

DiscountTest.java
- 패키지는 ddd다. 도메인, 응용, 인프라 클래스는 import로 부른다
- S1은 회원이 등급을 갖는지, 등급이 BASIC과 VIP 둘인지, 등급이 달라도 번호가 같으면 같은 회원인지, Main 소스가 김철수를 VIP로 저장하는지, Main 출력에 첫 주문 줄이 그대로인지 본다
- S2는 VIP 회원의 31000원 주문에 3100원, 일반 회원에 0원을 계산하는지, 계산 뒤 주문 상태와 할인 금액이 그대로인지, 할인 정책이 ddd.domain에 있고 칸이 없는지 본다
- S3은 결제 전 할인 금액 0원과 결제 금액이 주문 금액인지, 3100원을 받아 결제하면 할인 금액 3100원, 결제 금액 27900원, 결제 완료인지, 두 번 결제하면 거절하고 할인 금액이 그대로인지 본다
- S4는 응용 서비스 생성자 칸, VIP와 일반 회원 주문을 결제한 뒤 저장소의 할인 금액과 결제 금액, Main 출력 전체가 실행 결과와 같은지 본다
- S5는 용어집 줄을 본다

앞 장 테스트
- 파일마다 회원 1 김철수, 2 이영희를 일반 등급으로 저장한 회원 저장소를 만드는 private static 메서드를 두고 응용 서비스에 넘긴다. 할인 정책은 new DiscountPolicy()로 넘긴다
- 주문을 바로 결제하는 order.pay()는 order.pay(new Money(0))으로 바꾼다
- IdReferenceTest는 new Member(번호, 이름)을 등급을 넣어 부르고, Main 소스의 회원 저장 확인을 등급이 든 모양으로 바꾼다
- LayersTest와 OrderFactoryTest는 생성자 칸에 회원 저장소와 할인 정책을 더하고 기대 출력에 할인 줄을 넣는다

## 핵심 코드
public Money discount(Member member, Order order) {
    if (member.getGrade() == MemberGrade.VIP) {
        return new Money(order.getTotalPrice().amount() * 10 / 100);
    }
    return new Money(0);
}

public void pay(Money discountPrice) {
    if (!status.canChangeTo(OrderStatus.PAID)) {
        throw new IllegalStateException(status.label() + "인 주문은 결제할 수 없습니다");
    }
    this.discountPrice = discountPrice;
    status = OrderStatus.PAID;
}

Order order = findOrder(orderId);
Member member = memberRepository.findById(order.getMemberId()).orElseThrow();
order.pay(discountPolicy.discount(member, order));
orderRepository.save(order);

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | MemberGrade.java, Member.java, Main.java |
| S2 | DiscountPolicy.java |
| S3 | Order.java, Money.java |
| S4 | OrderApplicationService.java, Main.java |
| S5 | glossary.md |

## 라이브러리
- 추가 없음

## 다른 장 파일
- ddd/src/ddd/domain/Member.java: id-reference
- ddd/src/ddd/domain/Money.java: value-object, order-repository, layers
- ddd/src/ddd/domain/Order.java: anemic-order, rich-entity, value-object, order-aggregate, order-status, order-repository, layers, id-reference
- ddd/src/ddd/application/OrderApplicationService.java: anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference, order-factory
- ddd/src/ddd/Main.java: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference, order-factory
- ddd/glossary.md: ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, id-reference, order-factory
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
- tests/ddd/OrderFactoryTest.java: order-factory
