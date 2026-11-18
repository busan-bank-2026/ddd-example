# order-events 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/order/domain/Product.java | 고침 | 재고와 재고 감소 |
| ddd/src/ddd/order/application/OrderService.java | 고침 | 주문 저장 뒤 상품 재고 직접 감소 |
| ddd/src/ddd/order/domain/Order.java | 고침 | 이벤트 누적 제거 |
| ddd/src/ddd/Main.java | 고침 | 주문 뒤 재고 출력 |
| ddd/glossary.md | 고침 | 재고 용어 |
| tests/ddd/OrderEventsTest.java | 고침 | 주문 뒤 재고 검증 |

## 코드 계획
- spec의 할 일만 구현한다
- 학생 코드에 주석을 쓰지 않는다
- 기존 task 순서의 코드 차이를 따라치기에 녹인다
- 뒤 장의 구현을 앞 장에 미리 넣지 않는다

## 라이브러리
- 추가 없음

## 현재 따라치기 파일 목록

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/order/application/OrderService.java | 고침 |
| ddd/src/ddd/order/domain/Product.java | 고침 |
