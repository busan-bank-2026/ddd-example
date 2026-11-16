# ubiquitous-language 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/OrderService.java | 고침 | 메서드 이름을 업무 말로 바꾼다 |
| ddd/src/ddd/Main.java | 고침 | 바뀐 이름을 부른다 |
| ddd/glossary.md | 새로 | 업무 말, 코드 이름, 뜻을 나란히 적은 용어집 |
| tests/ddd/AnemicOrderTest.java | 고침 | 옛 이름 대신 새 이름을 부른다 |
| tests/ddd/UbiquitousLanguageTest.java | 새로 | S1부터 S5까지 행동 테스트 |

## 코드 계획
OrderService.java
- createOrder는 placeOrder, updatePayment는 pay, updateDelivery는 ship, deleteOrder는 cancel로 바꾼다
- 메서드 안의 규칙과 예외 메시지는 그대로 둔다

Main.java
- 바뀐 메서드 이름만 고친다. 출력은 그대로다
- 이클립스의 Rename(Alt+Shift+R)으로 바꾸면 OrderService와 Main이 함께 바뀐다

glossary.md
- 표 하나다. 칸은 업무 말, 코드 이름, 뜻이다
- 주문 Order, 주문하기 placeOrder, 결제하기 pay, 배송하기 ship, 주문 취소하기 cancel, 주문 금액 totalPrice
- 주문자 customerName, 배송 주소 address
- 상태 네 가지 주문 접수, 결제 완료, 배송 중, 취소
- 파일 이름은 영어로 짓는다. 한글 파일 이름은 도구에서 깨질 때가 있다

UbiquitousLanguageTest.java
- S1부터 S4는 새 이름을 불러 상태가 바뀌는지 확인한다. S4는 취소한 주문이 남아 있고 상태가 취소인지 본다
- S5는 glossary.md에 업무 말과 코드 이름 다섯 쌍이 있는지 확인한다

## 핵심 코드
public Order placeOrder(String customerName, String productName, int price, int quantity, String address) {

public void cancel(Order order) {
    if (order.getStatus().equals("배송 중")) {
        throw new IllegalStateException("배송 중인 주문은 취소할 수 없습니다");
    }
    order.setStatus("취소");
}

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | OrderService.java, Main.java |
| S2 | OrderService.java, Main.java |
| S3 | OrderService.java, Main.java |
| S4 | OrderService.java, Main.java |
| S5 | glossary.md |

## 라이브러리
- 추가 없음

## 다른 장 파일
- ddd/src/ddd/OrderService.java: anemic-order
- ddd/src/ddd/Main.java: project-setup, anemic-order
- tests/ddd/AnemicOrderTest.java: anemic-order
