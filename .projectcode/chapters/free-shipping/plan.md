# free-shipping 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/order/domain/FreeShippingSpecification.java | 새로 | 주문이 무료 배송인지 참 거짓으로 답하는 명세 |
| ddd/src/ddd/order/domain/OrderSpecification.java | 새로 | 주문 명세의 모양. isSatisfiedBy와 and |
| ddd/src/ddd/order/domain/FreeShippingPriceSpecification.java | 새로 | 결제 금액이 50000원 이상인지 답하는 명세 |
| ddd/src/ddd/order/domain/FreeShippingAreaSpecification.java | 새로 | 배송 주소가 제주가 아닌지 답하는 명세 |
| ddd/src/ddd/order/domain/Order.java | 고침 | 무료 배송 명세에게 물어 배송비를 내준다 |
| ddd/src/ddd/Main.java | 고침 | 이 장 장면 둘을 찍는다 |
| ddd/glossary.md | 고침 | 주문 명세, 무료 배송 명세, 무료 배송 금액 명세, 무료 배송 지역 명세, 배송비 |
| tests/ddd/FreeShippingTest.java | 새로 | S1부터 S4까지 행동 테스트 |

## 코드 계획
FreeShippingSpecification.java (도메인 계층, 명세)
- T1: 칸이 없다. boolean isSatisfiedBy(Order order) 하나만 둔다. 결제 금액이 50000원 이상이면 true다
- T2: OrderSpecification을 구현한다. 몸통은 new FreeShippingPriceSpecification().and(new FreeShippingAreaSpecification())에게 묻는 한 줄로 바뀐다
- 따로 두는 이유: 무료 배송 조건은 업무 사람이 이름으로 부르는 규칙이다. 조건을 객체로 만들어 이름을 붙이면 주문 안의 if 글자를 읽지 않아도 무엇을 묻는지 보인다
- 도메인 서비스와 다른 점: 할인 정책은 금액을 계산해 돌려줬다. 명세는 참 거짓만 답하고 금액은 모른다. 0원과 3000원은 주문이 정한다

OrderSpecification.java (T2, 도메인 계층)
- interface. boolean isSatisfiedBy(Order order)
- default OrderSpecification and(OrderSpecification other)는 두 명세가 모두 참인지 묻는 명세를 람다로 만들어 돌려준다
- 따로 두는 이유: 금액 명세와 지역 명세가 같은 모양이어야 and로 묶을 수 있고, 묶은 것도 다시 명세가 된다
- or는 쓰는 곳이 없어서 두지 않는다

FreeShippingPriceSpecification.java (T2, 도메인 계층)
- OrderSpecification을 구현한다. T1의 FreeShippingSpecification 몸통(결제 금액 50000원 이상)을 그대로 옮긴다

FreeShippingAreaSpecification.java (T2, 도메인 계층)
- OrderSpecification을 구현한다. 배송 주소 글자가 제주로 시작하지 않으면 true다

Order.java
- T1: int getShippingFee()를 더한다. new FreeShippingSpecification().isSatisfiedBy(this)면 0, 아니면 3000을 돌려준다
- 배송비를 칸으로 두지 않는 이유: 결제 금액처럼 그때 계산하면 할인이나 항목 더하기 뒤에 따로 맞출 것이 없다
- 생성자, pay, 다른 메서드는 그대로다

Main.java
- T1: 첫 줄을 ===== 무료 배송 조건 =====로 바꾸고 앞 장 장면을 지운다
- T1: 회원 1 김철수를 일반 등급으로, 상품 1 자바 책 15000을 저장하고 주문 응용 서비스를 앞 장처럼 잇는다. 일반 등급이라 할인이 없어 결제 금액이 주문 금액과 같다
- T1: 장면 [결제 금액으로 배송비 정하기]는 서울시 강남구로 자바 책 2개와 4개를 주문하고, 결제 금액과 배송비를 한 줄씩 찍는다
- T2: 장면 [제주 주소는 무료 배송에서 빼기]는 자바 책 4개를 서울시 마포구와 제주시 연동으로 주문하고, 주소, 결제 금액, 배송비를 한 줄씩 찍는다

glossary.md
- T1: 무료 배송 명세 FreeShippingSpecification(결제 금액 50000원 이상인지 답하는 명세), 배송비 shippingFee 줄을 더한다
- T2: 주문 명세 OrderSpecification, 무료 배송 금액 명세 FreeShippingPriceSpecification, 무료 배송 지역 명세 FreeShippingAreaSpecification 줄을 더하고 무료 배송 명세의 뜻을 두 명세를 and로 묶은 것으로 바꾼다

FreeShippingTest.java
- 패키지는 ddd다. 도메인 클래스는 import로 부른다
- S1은 서울 주소의 60000원, 50000원 주문에 참, 30000원 주문에 거짓, 52000원 주문을 5200원 할인으로 결제하면 거짓인지 본다. isSatisfiedBy의 돌려주는 값이 boolean인지, 칸이 없는지, ddd.order.domain에 있는지, 물은 뒤 주문이 그대로인지 본다
- S2는 30000원 주문 3000원, 60000원 주문 0원, 할인 뒤 50000원 아래로 내려간 주문 3000원인지 보고, Main 출력에 30000원 주문과 60000원 주문의 배송비 줄이 있는지 본다
- S3은 제주 60000원 주문이 무료 배송 명세에 거짓이고 배송비 3000원인지, 금액 명세에는 참이고 지역 명세에는 거짓인지, 두 명세와 무료 배송 명세가 주문 명세이고 주문 명세가 interface인지, 금액 명세 and 지역 명세가 무료 배송 명세와 같게 답하는지, Main 출력 전체가 실행 결과와 같은지 본다
- S4는 용어집 줄을 본다

## 핵심 코드
public boolean isSatisfiedBy(Order order) {
    return order.getPaymentPrice() >= 50000;
}

public int getShippingFee() {
    if (new FreeShippingSpecification().isSatisfiedBy(this)) {
        return 0;
    }
    return 3000;
}

default OrderSpecification and(OrderSpecification other) {
    return order -> isSatisfiedBy(order) && other.isSatisfiedBy(order);
}

public boolean isSatisfiedBy(Order order) {
    return new FreeShippingPriceSpecification().and(new FreeShippingAreaSpecification()).isSatisfiedBy(order);
}

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | FreeShippingSpecification.java |
| S2 | Order.java, Main.java |
| S3 | OrderSpecification.java, FreeShippingPriceSpecification.java, FreeShippingAreaSpecification.java, FreeShippingSpecification.java, Main.java |
| S4 | glossary.md |

## 라이브러리
- 추가 없음

## 다른 장 파일
- ddd/src/ddd/order/domain/Order.java: anemic-order, rich-entity, value-object, order-aggregate, order-status, order-repository, layers, id-reference, discount
- ddd/src/ddd/Main.java: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference, order-factory, discount
- ddd/glossary.md: ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, id-reference, order-factory, discount
