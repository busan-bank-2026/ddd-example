# rich-entity 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/Main.java | 고침 | 결제 없이 배송하는 문제 장면을 더하고, 고친 뒤에는 주문이 거절하는 장면으로 바꾼다 |
| ddd/src/ddd/Order.java | 고침 | 주문 번호, 생성자, 결제하기, 배송하기, 주문 취소하기를 가진 엔티티가 된다. setter를 모두 없앤다 |
| ddd/src/ddd/OrderService.java | 고침 | 주문 번호를 붙여 주문을 만들고, 주문에게 시키기만 한다 |
| ddd/glossary.md | 고침 | 주문 번호 id를 더한다 |
| tests/ddd/AnemicOrderTest.java | 고침 | 주문 줄에 주문 번호가 붙어도 통과하게 확인할 글자를 줄인다 |
| tests/ddd/RichEntityTest.java | 새로 | S1부터 S6까지 행동 테스트 |

## 코드 계획
Main.java
- 앞 장 장면 다섯 줄 뒤에 두 번째 주문을 주문하기로 만든다
- 문제 장면: order2.setStatus("배송 중")으로 서비스를 거치지 않고 바꾸고 "결제 없이 배송: " 뒤에 상태를 찍는다
- 주문 줄 맨 앞에 주문 번호를 "1번, " 모양으로 찍는다
- 고친 뒤: order2.ship()을 try catch로 감싸 거절 메시지를 찍는다. 취소 장면과 같은 모양이다

Order.java
- long id 칸을 더한다
- 생성자(id, customerName, productName, price, quantity, address)가 칸을 채우고 totalPrice는 price * quantity, status는 "주문 접수"로 정한다
- 생성자가 생기면 customerName부터 totalPrice까지의 setter를 지운다. 쓰는 곳이 없다
- pay(), ship(), cancel()에 OrderService의 규칙과 예외 메시지를 그대로 옮긴다
- 마지막으로 setStatus를 지운다. 상태는 행동 메서드로만 바뀐다
- getId를 더한다. 다른 getter는 그대로 둔다
- equals와 hashCode는 id만 본다. 같은 번호면 같은 주문이다

OrderService.java
- long nextId = 1 칸을 둔다
- placeOrder는 new Order(nextId++, ...)를 돌려준다
- pay, ship, cancel은 order.pay(), order.ship(), order.cancel()만 부른다

glossary.md
- 주문 줄 아래에 주문 번호 id 줄을 더한다

RichEntityTest.java
- S1은 Main을 실행해 "결제 없이 배송: " 줄이 찍히는지 본다
- S2는 번호가 같고 내용이 다른 두 주문이 같고, 번호가 다르면 다른지 본다. 주문하기를 두 번 하면 번호가 1, 2인지 본다
- S3은 생성자로 만든 주문의 주문 금액과 상태를 본다
- S4는 주문에게 직접 결제하기, 배송하기, 주문 취소하기를 시켜 상태와 거절 메시지를 본다
- S5는 Order에 set으로 시작하는 public 메서드가 없는지, Main의 결제 없이 배송 줄이 거절 메시지인지 본다
- S6은 glossary.md에 주문 번호 id 줄이 있는지 본다

AnemicOrderTest.java
- S7이 확인하는 주문 줄을 "김철수, 자바 책 2개, 30000원, 주문 접수"로 줄인다. 앞 장 시점과 이 장 시점 모두 통과한다

## 핵심 코드
public Order(long id, String customerName, String productName, int price, int quantity, String address) {
    this.id = id;
    this.totalPrice = price * quantity;
    this.status = "주문 접수";
}

public void ship() {
    if (!status.equals("결제 완료")) {
        throw new IllegalStateException("결제하지 않은 주문은 배송할 수 없습니다");
    }
    status = "배송 중";
}

public boolean equals(Object o) {
    if (!(o instanceof Order other)) {
        return false;
    }
    return id == other.id;
}

public void pay(Order order) {
    order.pay();
}

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | Main.java |
| S2 | Order.java, OrderService.java, Main.java |
| S3 | Order.java, OrderService.java |
| S4 | Order.java, OrderService.java |
| S5 | Order.java, Main.java |
| S6 | glossary.md |

## 라이브러리
- 추가 없음

## 다른 장 파일
- ddd/src/ddd/Main.java: project-setup, anemic-order, ubiquitous-language
- ddd/src/ddd/Order.java: anemic-order
- ddd/src/ddd/OrderService.java: anemic-order, ubiquitous-language
- ddd/glossary.md: ubiquitous-language
- tests/ddd/AnemicOrderTest.java: anemic-order
