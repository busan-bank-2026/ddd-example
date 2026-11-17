# order-status 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/OrderStatus.java | 새로 | 주문 상태 다섯 가지와 업무 말, 상태 전이 규칙 |
| ddd/src/ddd/Order.java | 고침 | 상태를 글자 대신 주문 상태로 갖는다. 결제, 배송, 취소, 배송 완료를 상태 전이 규칙으로 확인한다 |
| ddd/src/ddd/OrderService.java | 고침 | 배송 완료하기를 주문에게 시킨다 |
| ddd/src/ddd/Main.java | 고침 | 상태를 업무 말로 찍고, 두 번 결제와 배송 완료 장면을 더한다 |
| ddd/glossary.md | 고침 | 주문 상태, 배송 완료하기, 배송 완료를 더하고 상태의 코드 이름을 고친다 |
| tests/ddd/AnemicOrderTest.java | 고침 | 상태를 업무 말로 꺼내 확인한다 |
| tests/ddd/UbiquitousLanguageTest.java | 고침 | 상태를 업무 말로 꺼내 확인한다 |
| tests/ddd/RichEntityTest.java | 고침 | 상태를 업무 말로 꺼내 확인하고 결제 안 한 주문의 배송 거절 말을 새 모양으로 본다 |
| tests/ddd/OrderAggregateTest.java | 고침 | 상태를 업무 말로 꺼내 확인한다 |
| tests/ddd/OrderStatusTest.java | 새로 | S1부터 S6까지 행동 테스트 |

## 코드 계획
OrderStatus.java
- enum OrderStatus로 쓴다. 상태마다 업무 말 label을 생성자로 받는다
- T1은 ORDERED("주문 접수"), PAID("결제 완료"), SHIPPING("배송 중"), CANCELED("취소") 네 개와 label()이다
- T2에서 canChangeTo(OrderStatus next)를 더한다. switch로 지금 상태마다 갈 수 있는 상태를 돌려준다
- T2의 규칙: ORDERED는 PAID, CANCELED로. PAID는 SHIPPING, CANCELED로. SHIPPING, CANCELED는 false
- T3에서 SHIPPING과 CANCELED 사이에 DELIVERED("배송 완료")를 더한다. SHIPPING은 DELIVERED로. DELIVERED는 false
- OrderStatus를 따로 두는 이유: 상태가 바뀌는 규칙이 결제, 배송, 취소에 흩어져 있었다. 상태 쪽에 모으면 한곳에서 읽힌다

Order.java
- status 칸을 OrderStatus로 바꾼다. 생성자는 OrderStatus.ORDERED로 둔다
- getStatus()는 OrderStatus를 돌려준다
- T1: 글자 비교를 enum 비교로 바꾼다. addItem은 status != OrderStatus.ORDERED, ship은 status != OrderStatus.PAID, cancel은 status == OrderStatus.SHIPPING. 말과 동작은 그대로다
- T2: pay, ship, cancel의 첫 줄을 status.canChangeTo(다음 상태)로 확인한다. 안 되면 IllegalStateException(status.label() + "인 주문은 결제할 수 없습니다")처럼 지금 상태를 넣어 거절한다. 배송은 배송할, 취소는 취소할이다
- T3: deliver()를 더한다. 같은 모양으로 DELIVERED로 바꾸고 거절 말은 배송 완료할이다
- addItem은 상태 전이가 아니라서 status != OrderStatus.ORDERED 비교를 그대로 둔다

OrderService.java
- T3에서 deliver(Order order)를 더한다. order.deliver()만 부른다

Main.java
- T1: order.getStatus()를 찍는 곳을 모두 order.getStatus().label()로 바꾼다. 출력은 그대로다
- T2: 맨 끝에 두 번 결제 장면을 더한다. 앞에서 결제한 order5를 orderService.pay로 다시 결제하고 try catch로 감싸 "두 번 결제: " 거절 말을 찍는다
- T2부터 결제 없이 배송 줄의 거절 말이 주문 접수인 주문은 배송할 수 없습니다로 바뀐다
- T3: 맨 끝에 배송 중인 첫 주문을 orderService.deliver로 배송 완료하고 "배송 완료: " 상태를 찍는다

glossary.md
- T1: 주문 아래 줄들 뒤, 주문 접수 줄 앞에 주문 상태 OrderStatus 줄을 더한다
- T1: 상태 네 줄의 코드 이름을 ORDERED, PAID, SHIPPING, CANCELED로 고친다
- T3: 배송하기 아래에 배송 완료하기 deliver 줄, 배송 중 아래에 배송 완료 DELIVERED 줄을 더한다

OrderStatusTest.java
- S1은 OrderStatus 값이 네 개 이상이고 새 주문의 상태가 ORDERED, label이 주문 접수인지, Main의 주문 줄을 본다
- S2는 canChangeTo로 주문 접수, 결제 완료, 취소에서 갈 수 있는 상태와 갈 수 없는 상태를 본다. 취소한 주문의 결제가 거절되는지 본다
- S3은 결제 안 한 주문의 배송, 배송 중 주문의 취소, 취소한 주문의 취소 거절 말과 Main의 결제 없이 배송 줄을 본다
- S4는 결제한 주문의 두 번째 결제가 거절되고 상태가 결제 완료로 남는지, Main의 두 번 결제 줄을 본다
- S5는 배송 중 주문의 배송 완료, 결제 안 한 주문의 배송 완료 거절, DELIVERED에서 어디로도 못 가는지, Main의 배송 완료 줄을 본다
- S6은 glossary.md에 주문 상태, 배송 완료하기, 배송 완료, 주문 접수 ORDERED 줄이 있는지 본다

앞 장 테스트
- AnemicOrderTest, UbiquitousLanguageTest, RichEntityTest, OrderAggregateTest: getStatus()를 getStatus().label()로 바꾼다
- RichEntityTest: 결제 안 한 주문의 배송 거절 말과 Main 줄을 주문 접수인 주문은 배송할 수 없습니다로 바꾼다

## 핵심 코드
public enum OrderStatus {
    ORDERED("주문 접수"),
    PAID("결제 완료"),
    SHIPPING("배송 중"),
    DELIVERED("배송 완료"),
    CANCELED("취소");

    public boolean canChangeTo(OrderStatus next) {
        return switch (this) {
            case ORDERED -> next == PAID || next == CANCELED;
            case PAID -> next == SHIPPING || next == CANCELED;
            case SHIPPING -> next == DELIVERED;
            case DELIVERED, CANCELED -> false;
        };
    }
}

public void pay() {
    if (!status.canChangeTo(OrderStatus.PAID)) {
        throw new IllegalStateException(status.label() + "인 주문은 결제할 수 없습니다");
    }
    status = OrderStatus.PAID;
}

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | OrderStatus.java, Order.java, Main.java |
| S2 | OrderStatus.java, Order.java |
| S3 | Order.java, Main.java |
| S4 | Order.java, Main.java |
| S5 | OrderStatus.java, Order.java, OrderService.java, Main.java |
| S6 | glossary.md |

## 라이브러리
- 추가 없음

## 다른 장 파일
- ddd/src/ddd/Main.java: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate
- ddd/src/ddd/Order.java: anemic-order, rich-entity, value-object, order-aggregate
- ddd/src/ddd/OrderService.java: anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate
- ddd/glossary.md: ubiquitous-language, rich-entity, value-object, order-aggregate
- tests/ddd/AnemicOrderTest.java: anemic-order
- tests/ddd/UbiquitousLanguageTest.java: ubiquitous-language
- tests/ddd/RichEntityTest.java: rich-entity
- tests/ddd/OrderAggregateTest.java: order-aggregate
