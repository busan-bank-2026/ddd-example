# free-shipping 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/order/domain/FreeShippingSpecification.java | 고침 | 금액과 주소를 한 메서드에서 검사 |
| ddd/src/ddd/order/domain/OrderSpecification.java | 지움 | 조합 인터페이스 제거 |
| ddd/src/ddd/order/domain/FreeShippingPriceSpecification.java | 지움 | 금액 조건을 무료 배송 명세에 합침 |
| ddd/src/ddd/order/domain/FreeShippingAreaSpecification.java | 지움 | 지역 조건을 무료 배송 명세에 합침 |
| ddd/glossary.md | 고침 | 무료 배송 용어 정리 |
| tests/ddd/FreeShippingTest.java | 고침 | 금액 경계, 할인 뒤 금액, 제주 주소 검증 |

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
| ddd/src/ddd/order/domain/FreeShippingSpecification.java | 고침 |
| ddd/src/ddd/order/domain/Order.java | 고침 |
