# order-aggregate 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/OrderLine.java | 새로 | 상품 이름, 가격, 수량을 가진 주문 항목. 항목 금액을 계산한다 |
| ddd/src/ddd/Order.java | 고침 | 상품 칸 대신 주문 항목 목록을 갖는다. 주문 금액은 항목 금액의 합이다. 항목 더하기와 불변식을 지킨다 |
| ddd/src/ddd/OrderService.java | 고침 | 주문자, 주문 항목 목록, 주소 글자로 주문한다 |
| ddd/src/ddd/Main.java | 고침 | 여러 항목 주문을 보여주고, 항목 더하기 문제 장면과 고친 뒤 거절 장면을 더한다 |
| ddd/glossary.md | 고침 | 주문 항목, 항목 금액, 주문 항목 더하기를 더하고 주문 금액과 주문하기의 뜻을 고친다 |
| tests/ddd/AnemicOrderTest.java | 고침 | 주문 항목 목록으로 주문하고 항목에서 상품 값을 꺼내 확인한다 |
| tests/ddd/UbiquitousLanguageTest.java | 고침 | 주문 항목 목록으로 주문한다 |
| tests/ddd/RichEntityTest.java | 고침 | 주문 생성자에 주문 항목 목록을 넘기고 Main 첫 줄을 새 모양으로 확인한다 |
| tests/ddd/ValueObjectTest.java | 고침 | 주문 항목 목록으로 주문하고 가격을 항목에서 꺼내 확인한다 |
| tests/ddd/OrderAggregateTest.java | 새로 | S1부터 S8까지 행동 테스트 |

## 코드 계획
OrderLine.java
- record OrderLine(String productName, Money price, int quantity)로 쓴다
- amount()는 price.times(quantity)를 돌려준다
- T3에서 간결한 생성자를 더한다. quantity가 1 미만이면 IllegalArgumentException("수량은 1개 이상이어야 합니다")
- OrderLine을 따로 두는 이유: 주문 하나에 상품이 여러 개라서 상품 하나의 값을 묶을 곳이 필요하다

Order.java
- productName, price, quantity 칸과 그 getter를 지운다
- List<OrderLine> orderLines 칸을 더한다
- 생성자는 (long id, String customerName, List<OrderLine> orderLines, Address address)다
- 생성자는 받은 목록을 new ArrayList<>(orderLines)로 옮겨 담는다. 뒤에서 항목을 더할 수 있는 목록이다
- 주문 금액은 new Money(0)에서 시작해 항목마다 plus(orderLine.amount())로 더한다
- T1의 getOrderLines()는 orderLines를 그대로 돌려준다. 이것이 문제다
- T2에서 getOrderLines()는 List.copyOf(orderLines)를 돌려준다. 꺼낸 목록에 더하면 UnsupportedOperationException이 난다
- T2에서 addOrderLine(OrderLine orderLine)을 더한다. 상태가 주문 접수가 아니면 IllegalStateException("주문 접수인 주문에만 항목을 더할 수 있습니다"). 목록에 더하고 totalPrice = totalPrice.plus(orderLine.amount())
- T3에서 생성자 첫 줄에 항목이 비었으면 IllegalArgumentException("주문 항목이 하나 이상 있어야 합니다")
- 주문 금액 칸은 그대로 둔다. 항목과 주문 금액이 함께 맞는지는 루트인 주문이 지킨다

OrderService.java
- placeOrder(String customerName, List<OrderLine> orderLines, String address)로 바꾼다
- new Order(nextId++, customerName, orderLines, new Address(address))를 돌려준다

Main.java
- 첫 주문은 자바 책 15000원 2개와 볼펜 1000원 1개다. List.of로 항목 두 개를 만든다
- 주문 줄은 "주문: " 번호, 주문자, 주문 금액, 상태를 찍는다. 이어서 항목마다 for 문으로 "주문 항목: " 상품 이름, 수량, 항목 금액을 찍는다
- 앞 장의 두 번째, 세 번째, 네 번째 주문은 항목 하나짜리 목록으로 바꾼다. 출력은 그대로다
- T1 문제 장면: 다섯 번째 주문(정하늘, 자바 책 2개와 볼펜 1개, 광주시 북구)을 만들고 getOrderLines().add로 공책 2000원 5개를 더한다. "항목 더하기: 항목 " 개수 "개, 주문 금액 " 금액 "원"을 찍는다
- T2: 문제 장면의 getOrderLines().add를 order5.addOrderLine으로 바꾼다. 이어서 order5를 결제하고 다시 addOrderLine을 try catch로 감싸 "결제 뒤 항목 더하기: " 거절 메시지를 찍는다
- T3: 항목 없는 주문(강민준, List.of(), 인천시 연수구)과 0개 항목 주문(윤서연, 자바 책 0개, 대전시 유성구)을 try catch로 감싸 거절 메시지를 찍는다

glossary.md
- T1: 주문 금액의 뜻을 항목 금액의 합으로 고친다. 주문하기의 뜻을 주문자, 주문 항목, 배송 주소로 고친다
- T1: 주문 아래에 주문 항목 OrderLine, 항목 금액 amount 줄을 더한다
- T2: 주문하기 아래에 주문 항목 더하기 addOrderLine 줄을 더한다

OrderAggregateTest.java
- S1은 항목 금액, 두 항목 주문의 항목 수와 주문 금액, Main의 주문 줄과 항목 줄을 본다
- S2는 Main을 실행해 "항목 더하기: 항목 3개, 주문 금액 " 줄이 찍히는지 본다
- S3은 꺼낸 목록에 add 하면 UnsupportedOperationException이 나고 항목 수와 주문 금액이 그대로인지, 넘긴 목록을 밖에서 바꿔도 주문이 그대로인지 본다
- S4는 addOrderLine 뒤 항목 수와 주문 금액이 함께 바뀌는지, Main의 항목 더하기 줄이 41000원인지 본다
- S5는 결제한 주문의 addOrderLine이 거절 메시지와 함께 거절되고 항목과 금액이 그대로인지, Main의 결제 뒤 항목 더하기 줄을 본다
- S6은 빈 목록으로 주문하기와 new Order가 거절되는지, Main의 빈 주문 줄을 본다
- S7은 수량 0개 항목이 거절되고 1개는 만들어지는지, Main의 0개 항목 주문 줄을 본다
- S8은 glossary.md에 세 줄이 있는지 본다

앞 장 테스트
- AnemicOrderTest: placeOrder에 자바 책 2개 항목 하나를 넘긴다. S1은 getOrderLines().get(0)에서 상품 이름, 가격, 수량을 꺼낸다. S7은 Main의 새 주문 줄과 항목 줄을 본다
- UbiquitousLanguageTest: placeOrder에 항목 목록을 넘긴다
- RichEntityTest: new Order에 항목 목록을 넘긴다. S2는 Main의 새 주문 줄을 본다
- ValueObjectTest: placeOrder에 항목 목록을 넘긴다. S4는 가격을 항목에서 꺼내고 Main의 새 주문 줄을 본다. 음수 가격 거절은 new OrderLine에 new Money(-15000)을 넘길 때 본다

## 핵심 코드
public record OrderLine(String productName, Money price, int quantity) {

    public Money amount() {
        return price.times(quantity);
    }
}

this.orderLines = new ArrayList<>(orderLines);
this.totalPrice = new Money(0);
for (OrderLine orderLine : orderLines) {
    totalPrice = totalPrice.plus(orderLine.amount());
}

public List<OrderLine> getOrderLines() {
    return List.copyOf(orderLines);
}

public void addOrderLine(OrderLine orderLine) {
    if (!status.equals("주문 접수")) {
        throw new IllegalStateException("주문 접수인 주문에만 항목을 더할 수 있습니다");
    }
    orderLines.add(orderLine);
    totalPrice = totalPrice.plus(orderLine.amount());
}

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | OrderLine.java, Order.java, OrderService.java, Main.java |
| S2 | Order.java, Main.java |
| S3 | Order.java |
| S4 | Order.java, Main.java |
| S5 | Order.java, Main.java |
| S6 | Order.java, Main.java |
| S7 | OrderLine.java, Main.java |
| S8 | glossary.md |

## 라이브러리
- 추가 없음. java.util.List, ArrayList는 자바 기본이다

## 다른 장 파일
- ddd/src/ddd/Main.java: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object
- ddd/src/ddd/Order.java: anemic-order, rich-entity, value-object
- ddd/src/ddd/OrderService.java: anemic-order, ubiquitous-language, rich-entity, value-object
- ddd/glossary.md: ubiquitous-language, rich-entity, value-object
- tests/ddd/AnemicOrderTest.java: anemic-order
- tests/ddd/UbiquitousLanguageTest.java: ubiquitous-language
- tests/ddd/RichEntityTest.java: rich-entity
- tests/ddd/ValueObjectTest.java: value-object
