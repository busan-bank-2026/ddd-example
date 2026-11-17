# value-object 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/Main.java | 고침 | 앞 장 장면을 지우고 빈 주소 주문 장면을 찍는다. 고친 뒤에는 거절 메시지를 보여준다 |
| ddd/src/ddd/Address.java | 새로 | 비어 있지 않은 배송 주소 |
| ddd/src/ddd/Order.java | 고침 | 배송 주소를 Address로 갖는다 |
| ddd/src/ddd/OrderService.java | 고침 | 받은 주소로 Address를 만들어 주문에 넘긴다 |
| ddd/glossary.md | 고침 | 주소 Address를 더한다 |
| tests/ddd/AnemicOrderTest.java | 고침 | 주소를 값 객체에서 꺼내 확인한다 |
| tests/ddd/RichEntityTest.java | 고침 | 주문 생성자에 Address를 넘기고, 값 객체에서 꺼내 확인한다 |
| tests/ddd/ValueObjectTest.java | 새로 | S1부터 S5까지 행동 테스트 |

## 코드 계획
Address.java
- record Address(String value)로 쓴다. record는 칸을 바꿀 수 없고 같은 값이면 equals가 같다
- 간결한 생성자에서 null이거나 비어 있으면 IllegalArgumentException("배송 주소를 입력해야 합니다")을 던진다
- Address를 따로 두는 이유: 주소 검사를 주소 한 곳에 둔다

Order.java
- address 칸을 Address로 바꾼다. 생성자는 Address address를 받고 getAddress는 Address를 돌려준다
- 가격과 주문 금액은 int 그대로다
- 생성자 순서는 그대로다: (id, customerName, productName, price, quantity, address)

OrderService.java
- placeOrder가 받는 값은 그대로 int price, String address다
- T2에서 new Address(address)를 만들어 new Order에 넘긴다

Main.java
- 시작 줄을 찍고 앞 장 장면을 지운다
- 주문 하나를 만들어 주문 줄을 찍는다
- 문제 장면: 빈 주소 ""로 주문하고 "빈 주소 주문: " 뒤에 상태를 찍는다
- 주소로 바꾼 뒤: 빈 주소 주문을 try catch로 감싸 IllegalArgumentException의 메시지를 찍는다

glossary.md
- 배송 주소 줄 아래에 주소 Address 줄을 더한다

ValueObjectTest.java
- S1은 빈 주소 주문이 만들어지던 문제 장면이 이제 거절되는지 본다
- S2는 빈 주소와 null 주소가 거절 메시지와 함께 거절되는지 본다
- S3은 같은 글자의 두 주소가 같은지, 주소에 바꾸는 메서드가 없는지 본다
- S4는 주문의 주소가 Address인지, Main의 빈 주소 줄이 거절 메시지인지 본다
- S5는 glossary.md에 주소 Address 줄이 있는지 본다

앞 장 테스트
- AnemicOrderTest S1: getAddress().value()로 확인한다
- RichEntityTest: new Order에 new Address(...)를 넘기고, S3은 getAddress().value()로 확인한다

## 핵심 코드
public record Address(String value) {

    public Address {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("배송 주소를 입력해야 합니다");
        }
    }
}

return new Order(nextId++, customerName, productName, price, quantity, new Address(address));

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | Main.java |
| S2 | Address.java |
| S3 | Address.java |
| S4 | Order.java, OrderService.java, Main.java |
| S5 | glossary.md |

## 라이브러리
- 추가 없음

## 다른 장 파일
- ddd/src/ddd/Main.java: project-setup, anemic-order, ubiquitous-language, rich-entity
- ddd/src/ddd/Order.java: anemic-order, rich-entity
- ddd/src/ddd/OrderService.java: anemic-order, ubiquitous-language, rich-entity
- ddd/glossary.md: ubiquitous-language, rich-entity
- tests/ddd/AnemicOrderTest.java: anemic-order
- tests/ddd/RichEntityTest.java: rich-entity
