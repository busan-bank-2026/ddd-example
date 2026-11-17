# order-status 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/OrderStatus.java | 새로 | 주문 상태 다섯 가지와 업무 말 |
| ddd/src/ddd/Order.java | 고침 | 상태를 글자 대신 주문 상태로 갖는다. 결제, 배송, 취소, 배송 완료가 지금 상태를 보고 정한다 |
| ddd/src/ddd/OrderService.java | 고침 | 배송 완료하기를 주문에게 시킨다 |
| ddd/src/ddd/Main.java | 고침 | 앞 장 장면을 지우고 상태 전후를 업무 말로 찍는다. 두 번 결제와 배송 완료 장면을 더한다 |
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
- T3에서 SHIPPING과 CANCELED 사이에 DELIVERED("배송 완료")를 더한다
- OrderStatus를 따로 두는 이유: 상태를 글자로 두면 오타도 상태가 된다. 있을 수 있는 상태를 한 묶음으로 정해 둔다

Order.java
- status 칸을 OrderStatus로 바꾼다. 생성자는 OrderStatus.ORDERED로 둔다
- getStatus()는 OrderStatus를 돌려준다
- T1: 글자 비교를 enum 비교로 바꾼다. addItem은 status != OrderStatus.ORDERED, ship은 status != OrderStatus.PAID, cancel은 status == OrderStatus.SHIPPING. 말과 동작은 그대로다
- T2: pay, ship, cancel의 첫 줄에서 지금 상태를 if 하나로 바로 확인한다. 결제는 ORDERED일 때만, 배송은 PAID일 때만, 취소는 ORDERED나 PAID일 때만 된다
- T2: 안 되면 IllegalStateException(status.label() + "인 주문은 결제할 수 없습니다")처럼 지금 상태를 넣어 거절한다. 배송은 배송할, 취소는 취소할이다
- T3: deliver()를 더한다. SHIPPING일 때만 DELIVERED로 바꾸고 거절 말은 배송 완료할이다
- 배송 완료와 취소는 어느 메서드의 if도 통과하지 못하므로 더 바뀌지 않는다

OrderService.java
- T3에서 deliver(Order order)를 더한다. order.deliver()만 부른다

Main.java
- 첫 줄에 ===== 주문 상태 =====를 찍고 앞 장 장면을 지운다. 장면마다 빈 줄과 [장면 이름] 줄로 나눈다
- 상태는 order.getStatus().label()로 업무 말을 찍는다. 바뀐 상태는 "주문 상태: " 전 -> 후로 찍는다
- T1 [결제하고 배송하기]: 김철수 주문을 결제하고 배송하며 상태 전후를 두 줄 찍는다
- T1 [할 수 없는 일 시키기]: 배송 중인 주문의 취소와 이영희 주문의 결제 없이 배송을 try catch로 감싸 거절 말을 찍는다
- T2부터 결제 없이 배송 줄의 거절 말이 주문 접수인 주문은 배송할 수 없습니다로 바뀐다
- T2 [두 번 결제하기]: 이영희 주문을 결제한 뒤 다시 결제하고 try catch로 감싸 "두 번 결제: " 거절 말을 찍는다
- T3 [배송 완료하기]: 배송 중인 김철수 주문을 orderService.deliver로 배송 완료하고 상태 전후를 찍는다

glossary.md
- T1: 상태 줄들 앞에 주문 상태 OrderStatus 줄을 더한다
- T1: 상태 네 줄의 코드 이름을 ORDERED, PAID, SHIPPING, CANCELED로 고친다
- T3: 배송하기 아래에 배송 완료하기 deliver 줄, 배송 중 아래에 배송 완료 DELIVERED 줄을 더한다

OrderStatusTest.java
- S1은 OrderStatus 값과 새 주문의 상태가 ORDERED, label이 주문 접수인지 본다
- S2는 주문 접수, 결제 완료, 취소인 주문에 결제, 배송, 취소를 시켜 되는 것과 거절되는 것을 본다
- S3은 결제 안 한 주문의 배송, 배송 중 주문의 취소, 취소한 주문의 취소 거절 말과 Main의 결제 없이 배송 줄을 본다
- S4는 결제한 주문의 두 번째 결제가 거절되고 상태가 결제 완료로 남는지, Main의 두 번 결제 줄을 본다
- S5는 배송 중 주문의 배송 완료, 결제 안 한 주문의 배송 완료 거절, 배송 완료인 주문이 더 바뀌지 않는지, Main의 배송 완료 장면을 본다
- S6은 glossary.md에 주문 상태, 배송 완료하기, 배송 완료, 주문 접수 ORDERED 줄이 있는지 본다

앞 장 테스트
- AnemicOrderTest, UbiquitousLanguageTest, RichEntityTest, OrderAggregateTest: getStatus()를 getStatus().label()로 바꾼다
- RichEntityTest: 결제 안 한 주문의 배송 거절 말을 주문 접수인 주문은 배송할 수 없습니다로 바꾼다

## 핵심 코드
public enum OrderStatus {
    ORDERED("주문 접수"),
    PAID("결제 완료"),
    SHIPPING("배송 중"),
    DELIVERED("배송 완료"),
    CANCELED("취소");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}

public void pay() {
    if (status != OrderStatus.ORDERED) {
        throw new IllegalStateException(status.label() + "인 주문은 결제할 수 없습니다");
    }
    status = OrderStatus.PAID;
}

public void cancel() {
    if (status != OrderStatus.ORDERED && status != OrderStatus.PAID) {
        throw new IllegalStateException(status.label() + "인 주문은 취소할 수 없습니다");
    }
    status = OrderStatus.CANCELED;
}

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | OrderStatus.java, Order.java, Main.java |
| S2 | Order.java |
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
