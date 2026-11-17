# 캡처

| 파일 | 장 | task | 커밋 | 찍은 화면 | 보이는 것 |
|---|---|---|---|---|---|
| T1-무료-배송-명세.txt | free-shipping | T1 | 710602e | 콘솔 실행과 domain 폴더 | ===== 무료 배송 조건 ===== 아래 [결제 금액으로 배송비 정하기]에 30000원 주문 배송비: 3000원과 60000원 주문 배송비: 0원이 찍히고, 이어서 src/ddd/order/domain에 FreeShippingSpecification이 더 보인다 |
| T2-명세-and로-묶기.txt | free-shipping | T2 | ee660c2 | 콘솔 실행과 domain 폴더 | [제주 주소는 무료 배송에서 빼기]에 서울시 마포구 60000원 주문 배송비: 0원과 제주시 연동 60000원 주문 배송비: 3000원이 찍히고, 이어서 src/ddd/order/domain에 OrderSpecification, FreeShippingPriceSpecification, FreeShippingAreaSpecification이 더 보인다 |
| done-주문-장면.txt | free-shipping | done | ee660c2 | 콘솔 실행 (이클립스 Run 대신) | ===== 무료 배송 조건 =====부터 제주시 연동 배송비 줄까지 spec 실행 결과와 같게 찍힌다 |
