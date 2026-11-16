# anemic-order 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/Order.java | 새로 | 주문 데이터. 칸과 getter, setter만 있다 |
| ddd/src/ddd/OrderService.java | 새로 | 주문 규칙. 만들기, 결제, 배송, 취소 |
| ddd/src/ddd/Main.java | 고침 | 주문부터 취소 시도까지 장면을 실행해 출력한다 |
| tests/ddd/AnemicOrderTest.java | 새로 | S1부터 S7까지 행동 테스트 |

Order와 OrderService를 나누는 이유는 데이터와 규칙이 따로 사는 모습이 이 장의 배울 것이기 때문이다.

## 코드 계획
Order.java
- 칸은 customerName, productName, price, quantity, address, totalPrice, status다
- price, quantity, totalPrice는 int, 나머지는 String이다
- 칸마다 getter와 setter를 둔다. 다른 메서드는 없다

OrderService.java
- 이름은 CRUD 말로 짓는다. 3장 ubiquitous-language에서 업무 말로 바꾼다
- createOrder는 Order를 new로 만들고 setter로 칸을 채운다. totalPrice는 price * quantity, status는 주문 접수다
- updatePayment는 status를 결제 완료로 바꾼다
- updateDelivery는 status가 결제 완료가 아니면 IllegalStateException을 던진다. 메시지는 결제하지 않은 주문은 배송할 수 없습니다다. 맞으면 배송 중으로 바꾼다
- deleteOrder는 status가 배송 중이면 IllegalStateException을 던진다. 메시지는 배송 중인 주문은 취소할 수 없습니다다. 아니면 취소로 바꾼다
- 상태는 글자 그대로 쓴다. 주문 접수, 결제 완료, 배송 중, 취소

Main.java
- 시작 글 다음에 OrderService를 만든다
- 김철수, 자바 책, 15000원, 2개, 서울시 강남구로 주문을 만들고 한 줄로 찍는다
- 결제, 배송 뒤마다 상태를 찍는다
- 취소는 try catch로 감싼다. 취소되면 취소: 와 상태를, 거절되면 취소: 와 예외 메시지를 찍는다

AnemicOrderTest.java
- S1부터 S6은 OrderService를 직접 불러 상태와 예외를 확인한다
- S7은 System.out을 바꿔 Main 출력에 장면 다섯 줄이 있는지 확인한다

## 핵심 코드
public void updateDelivery(Order order) {
    if (!order.getStatus().equals("결제 완료")) {
        throw new IllegalStateException("결제하지 않은 주문은 배송할 수 없습니다");
    }
    order.setStatus("배송 중");
}

order.setTotalPrice(price * quantity);
order.setStatus("주문 접수");

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | Order.java, OrderService.java |
| S2 | OrderService.java |
| S3 | OrderService.java |
| S4 | OrderService.java |
| S5 | OrderService.java |
| S6 | OrderService.java |
| S7 | Main.java |

## 라이브러리
- 추가 없음

## 다른 장 파일
- ddd/src/ddd/Main.java: project-setup
