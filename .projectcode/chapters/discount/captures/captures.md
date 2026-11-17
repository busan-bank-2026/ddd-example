# 캡처

| 파일 | 장 | task | 커밋 | 찍은 화면 | 보이는 것 |
|---|---|---|---|---|---|
| T1-회원-등급.txt | discount | T1 | 31a7a4b | 콘솔 실행과 domain 폴더 | ===== 회원 등급 할인 ===== 아래 [회원 등급 정하기]에 김철수 회원 등급: VIP와 이영희 회원 등급: BASIC이 찍히고, 이어서 src/ddd/order/domain에 MemberGrade가 더 보인다 |
| T2-할인-정책.txt | discount | T2 | f9ece31 | 콘솔 실행과 domain 폴더 | [할인 정책으로 결제하기]에 김철수 VIP 결제 금액: 31000원 -> 27900원, 할인 3100원과 이영희 BASIC 결제 금액: 31000원 -> 31000원, 할인 0원이 찍히고, 이어서 src/ddd/order/domain에 DiscountPolicy가 더 보인다 |
| done-주문-장면.txt | discount | done | f9ece31 | 콘솔 실행 (이클립스 Run 대신) | ===== 회원 등급 할인 =====부터 이영희 결제 금액 줄까지 spec 실행 결과와 같게 찍힌다 |
