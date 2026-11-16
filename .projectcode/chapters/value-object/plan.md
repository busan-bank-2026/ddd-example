# value-object 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/Main.java | 고침 | 음수 금액 주문 문제 장면을 더하고, 고친 뒤에는 거절 메시지와 빈 주소 거절 장면을 보여준다 |
| ddd/src/ddd/Money.java | 새로 | 0원 이상인 돈. 곱하기와 더하기는 새 돈을 돌려준다 |
| ddd/src/ddd/Address.java | 새로 | 비어 있지 않은 배송 주소 |
| ddd/src/ddd/Order.java | 고침 | 가격과 주문 금액을 Money로, 배송 주소를 Address로 갖는다 |
| ddd/src/ddd/OrderService.java | 고침 | 받은 가격과 주소로 Money와 Address를 만들어 주문에 넘긴다 |
| ddd/glossary.md | 고침 | 돈 Money, 주소 Address를 더한다 |
| tests/ddd/AnemicOrderTest.java | 고침 | 가격, 주문 금액, 주소를 값 객체에서 꺼내 확인한다 |
| tests/ddd/UbiquitousLanguageTest.java | 고침 | 주문 금액을 값 객체에서 꺼내 확인한다 |
| tests/ddd/RichEntityTest.java | 고침 | 주문 생성자에 Money와 Address를 넘기고, 값 객체에서 꺼내 확인한다 |
| tests/ddd/ValueObjectTest.java | 새로 | S1부터 S6까지 행동 테스트 |

## 코드 계획
Money.java
- record Money(int amount)로 쓴다. record는 칸을 바꿀 수 없고 같은 값이면 equals가 같다
- 간결한 생성자에서 0 미만이면 IllegalArgumentException("금액은 0원 이상이어야 합니다")을 던진다
- times(int count)는 new Money(amount * count)를 돌려준다
- plus(Money other)는 new Money(amount + other.amount)를 돌려준다
- Money를 따로 두는 이유: 금액 검사를 돈이 쓰이는 모든 곳에서 되풀이하지 않고 돈 한 곳에 둔다

Address.java
- record Address(String value)로 쓴다
- 간결한 생성자에서 null이거나 비어 있으면 IllegalArgumentException("배송 주소를 입력해야 합니다")을 던진다
- Address를 따로 두는 이유: 주소 검사를 주소 한 곳에 둔다

Order.java
- price와 totalPrice 칸을 Money로 바꾼다. 생성자는 Money price를 받고 totalPrice는 price.times(quantity)다
- getPrice와 getTotalPrice는 Money를 돌려준다
- address 칸을 Address로 바꾼다. 생성자는 Address address를 받고 getAddress는 Address를 돌려준다
- 생성자 순서는 그대로다: (id, customerName, productName, price, quantity, address)

OrderService.java
- placeOrder가 받는 값은 그대로 int price, String address다
- T2에서 new Money(price)를, T3에서 new Address(address)를 만들어 new Order에 넘긴다

Main.java
- 앞 장 장면 뒤에 세 번째 주문을 가격 -15000, 수량 2로 주문한다
- 문제 장면: "음수 금액 주문: " 뒤에 주문 금액과 "원"을 찍는다
- 돈으로 바꾼 뒤: 주문 줄과 음수 금액 줄은 getTotalPrice().amount()를 찍는다. 음수 금액 주문은 try catch로 감싸 IllegalArgumentException의 메시지를 찍는다
- 주소로 바꾼 뒤: 빈 주소 ""로 주문하는 장면을 더한다. 같은 모양으로 "빈 주소 주문: " 뒤에 상태나 거절 메시지를 찍는다

glossary.md
- 주문 금액 줄 아래에 돈 Money 줄을 더한다
- 배송 주소 줄 아래에 주소 Address 줄을 더한다

ValueObjectTest.java
- S1은 Main을 실행해 "음수 금액 주문: " 줄이 찍히는지 본다
- S2는 new Money(-1)이 거절 메시지와 함께 거절되고 new Money(0)은 만들어지는지 본다
- S3은 times와 plus의 결과가 새 돈이고 원래 돈이 그대로인지, 금액이 같은 두 돈이 같은지 본다
- S4는 주문하기로 만든 주문의 가격과 주문 금액이 Money인지, 음수 가격 주문하기가 거절되는지, Main의 음수 금액 줄이 거절 메시지인지 본다
- S5는 빈 주소와 null 주소가 거절되는지, 같은 글자의 두 주소가 같은지, 주문의 주소가 Address인지, Main의 빈 주소 줄이 거절 메시지인지 본다
- S6은 glossary.md에 돈 Money, 주소 Address 줄이 있는지 본다

앞 장 테스트
- AnemicOrderTest S1: getPrice().amount(), getTotalPrice().amount(), getAddress().value()로 확인한다
- UbiquitousLanguageTest S1: getTotalPrice().amount()로 확인한다
- RichEntityTest: new Order에 new Money(...)와 new Address(...)를 넘기고, S3은 getTotalPrice().amount()와 getAddress().value()로 확인한다

## 핵심 코드
public record Money(int amount) {

    public Money {
        if (amount < 0) {
            throw new IllegalArgumentException("금액은 0원 이상이어야 합니다");
        }
    }

    public Money times(int count) {
        return new Money(amount * count);
    }
}

public record Address(String value) {

    public Address {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("배송 주소를 입력해야 합니다");
        }
    }
}

this.totalPrice = price.times(quantity);

return new Order(nextId++, customerName, productName, new Money(price), quantity, new Address(address));

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | Main.java |
| S2 | Money.java |
| S3 | Money.java |
| S4 | Order.java, OrderService.java, Main.java |
| S5 | Address.java, Order.java, OrderService.java, Main.java |
| S6 | glossary.md |

## 라이브러리
- 추가 없음

## 다른 장 파일
- ddd/src/ddd/Main.java: project-setup, anemic-order, ubiquitous-language, rich-entity
- ddd/src/ddd/Order.java: anemic-order, rich-entity
- ddd/src/ddd/OrderService.java: anemic-order, ubiquitous-language, rich-entity
- ddd/glossary.md: ubiquitous-language, rich-entity
- tests/ddd/AnemicOrderTest.java: anemic-order
- tests/ddd/UbiquitousLanguageTest.java: ubiquitous-language
- tests/ddd/RichEntityTest.java: rich-entity
