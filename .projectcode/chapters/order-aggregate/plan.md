# order-aggregate 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/OrderItem.java | 새로 | 상품 이름, 가격, 수량을 가진 주문 항목. 항목 금액을 계산한다 |
| ddd/src/ddd/Order.java | 고침 | 상품 칸 대신 주문 항목 목록을 갖는다. 주문 금액은 항목 금액의 합이다. 항목 더하기와 불변식을 지킨다 |
| ddd/src/ddd/OrderService.java | 고침 | 주문자, 주문 항목 목록, 주소 글자로 주문한다 |
| ddd/src/ddd/Main.java | 고침 | 앞 장 장면을 지우고 여러 항목 주문, 항목 더하기 문제 장면, 고친 뒤 거절 장면을 찍는다 |
| ddd/glossary.md | 고침 | 주문 항목, 항목 금액, 주문 항목 더하기를 더하고 주문 금액, 주문하기, 주문 항목의 뜻을 고친다 |
| tests/ddd/AnemicOrderTest.java | 고침 | 주문 항목 목록으로 주문하고 항목에서 상품 값을 꺼내 확인한다 |
| tests/ddd/UbiquitousLanguageTest.java | 고침 | 주문 항목 목록으로 주문한다 |
| tests/ddd/RichEntityTest.java | 고침 | 주문 생성자에 주문 항목 목록을 넘긴다 |
| tests/ddd/ValueObjectTest.java | 고침 | 주문 항목 목록으로 주문한다 |
| tests/ddd/OrderAggregateTest.java | 새로 | S1부터 S8까지 행동 테스트 |

## 코드 계획
OrderItem.java
- 클래스 OrderItem으로 쓴다. productName, price, quantity를 private final 칸으로 갖는다
- 생성자는 (String productName, int price, int quantity)다
- getProductName, getPrice, getQuantity와 항목 금액 getAmount()를 둔다. getAmount는 price * quantity다
- T3에서 생성자 첫 줄에 검사를 더한다. price가 0 미만이면 IllegalArgumentException("가격은 0원 이상이어야 합니다"), quantity가 1 미만이면 IllegalArgumentException("수량은 1개 이상이어야 합니다")
- OrderItem을 따로 두는 이유: 주문 하나에 상품이 여러 개라서 상품 하나의 값을 묶을 곳이 필요하다

Order.java
- productName, price, quantity 칸과 그 getter를 지운다
- List<OrderItem> items 칸을 더한다. 주문 금액은 int 그대로다
- 생성자는 (long id, String customerName, List<OrderItem> items, Address address)다
- 생성자는 받은 목록을 new ArrayList<>(items)로 옮겨 담는다. 뒤에서 항목을 더할 수 있는 목록이다
- 주문 금액은 0에서 시작해 for 문으로 항목마다 item.getAmount()를 더한다
- T1의 getItems()는 items를 그대로 돌려준다. 이것이 문제다
- T2에서 getItems()는 List.copyOf(items)를 돌려준다. 꺼낸 목록에 더하면 UnsupportedOperationException이 난다
- T2에서 addItem(OrderItem item)을 더한다. 상태가 주문 접수가 아니면 IllegalStateException("주문 접수인 주문에만 항목을 더할 수 있습니다"). 목록에 더하고 totalPrice = totalPrice + item.getAmount()
- T3에서 생성자 첫 줄에 항목이 비었으면 IllegalArgumentException("주문 항목이 하나 이상 있어야 합니다")
- 주문 금액 칸은 그대로 둔다. 항목과 주문 금액이 함께 맞는지는 루트인 주문이 지킨다

OrderService.java
- placeOrder(String customerName, List<OrderItem> items, String address)로 바꾼다
- new Order(nextId++, customerName, items, new Address(address))를 돌려준다

Main.java
- 첫 줄에 ===== 주문과 주문 항목 =====을 찍고 앞 장 장면을 지운다. 장면마다 빈 줄과 [장면 이름] 줄로 나눈다
- T1 [여러 항목으로 주문하기]: 김철수가 자바 책 15000원 2개와 볼펜 1000원 1개를 List.of로 주문한다. "주문: " 번호 "번 주문, 주문 금액 " 금액 "원"을 찍고, 항목마다 for 문으로 "주문 항목: " 상품 이름, 수량, 항목 금액을 찍는다
- T1 [항목 더하기] 문제 장면: 항목 수와 주문 금액을 기억해 두고 getItems().add로 공책 2000원 5개를 더한다. "항목 수: " 전 -> 후, "주문 금액: " 전 -> 후를 찍는다
- T2: 문제 장면의 getItems().add를 order.addItem으로 바꾼다. [결제 뒤 항목 더하기]에서 결제한 뒤 addItem을 try catch로 감싸 "결제 뒤 항목 더하기: " 거절 메시지를 찍는다
- T3 [잘못된 주문하기]: 빈 주문(강민준, List.of()), 음수 가격 주문(박민수, 자바 책 -15000원), 0개 항목 주문(윤서연, 자바 책 0개)을 하나씩 try catch로 감싸 거절 메시지를 찍는다

glossary.md
- T1: 주문 아래에 주문 항목 OrderItem, 항목 금액 amount 줄을 더한다
- T1: 주문 금액의 뜻을 항목 금액의 합으로, 주문하기의 뜻을 주문자, 주문 항목, 배송 주소로 고친다
- T2: 주문하기 아래에 주문 항목 더하기 addItem 줄을 더한다
- T3: 주문 항목의 뜻에 가격은 0원 이상, 수량은 1개 이상을 더한다

OrderAggregateTest.java
- S1은 항목 금액, 두 항목 주문의 항목 수와 주문 금액, Main의 주문 줄과 항목 줄을 본다
- S2는 Main을 실행해 항목 더하기 장면이 찍히는지 본다
- S3은 꺼낸 목록에 add 하면 UnsupportedOperationException이 나고 항목 수와 주문 금액이 그대로인지, 넘긴 목록을 밖에서 바꿔도 주문이 그대로인지 본다
- S4는 addItem 뒤 항목 수와 주문 금액이 함께 바뀌는지, Main의 주문 금액 줄이 41000원으로 바뀌는지 본다
- S5는 결제한 주문의 addItem이 거절 메시지와 함께 거절되고 항목과 금액이 그대로인지, Main의 결제 뒤 항목 더하기 줄을 본다
- S6은 빈 목록으로 주문하기와 new Order가 거절되는지, Main의 빈 주문 줄을 본다
- S7은 음수 가격과 수량 0개 항목이 거절되고 1개는 만들어지는지, Main의 음수 가격 주문 줄과 0개 항목 주문 줄을 본다
- S8은 glossary.md에 세 줄이 있는지 본다

앞 장 테스트
- 앞 장 테스트는 주문할 때 상품 값 대신 주문 항목 목록을 넘기고, 상품 값은 getItems().get(0)에서 꺼낸다

## 핵심 코드
public class OrderItem {

    private final String productName;
    private final int price;
    private final int quantity;

    public OrderItem(String productName, int price, int quantity) {
        if (price < 0) {
            throw new IllegalArgumentException("가격은 0원 이상이어야 합니다");
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다");
        }
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    public int getAmount() {
        return price * quantity;
    }
}

this.items = new ArrayList<>(items);
this.totalPrice = 0;
for (OrderItem item : items) {
    totalPrice = totalPrice + item.getAmount();
}

public List<OrderItem> getItems() {
    return List.copyOf(items);
}

public void addItem(OrderItem item) {
    if (!status.equals("주문 접수")) {
        throw new IllegalStateException("주문 접수인 주문에만 항목을 더할 수 있습니다");
    }
    items.add(item);
    totalPrice = totalPrice + item.getAmount();
}

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | OrderItem.java, Order.java, OrderService.java, Main.java |
| S2 | Order.java, Main.java |
| S3 | Order.java |
| S4 | Order.java, Main.java |
| S5 | Order.java, Main.java |
| S6 | Order.java, Main.java |
| S7 | OrderItem.java, Main.java |
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
