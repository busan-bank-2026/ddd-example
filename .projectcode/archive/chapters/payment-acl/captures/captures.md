# 캡처

| 파일 | 장 | task | 커밋 | 찍은 화면 | 보이는 것 |
|---|---|---|---|---|---|
| T1-외부-결제-응답.txt | payment-acl | T1 | 0068b2e | 콘솔 실행과 src 폴더 | ===== 외부 결제 연결 ===== 아래 [외부 결제 모듈에 직접 맡기기]에 27900원 맡기기: 0000과 108000원 맡기기: E101이 찍히고, 이어서 src에 ddd와 나란히 legacypay 폴더와 그 안의 LegacyPayGateway.java가 보인다 |
| T2-결제-어댑터.txt | payment-acl | T2 | a807458 | 콘솔 실행과 주문 폴더 | [결제 어댑터로 결제하기]에 주문 상태: 주문 접수 -> 결제 완료가, [한도 넘은 결제]에 자바 책 8개 결제: 결제 한도를 넘었습니다와 주문 상태: 주문 접수 -> 주문 접수가 찍히고, 이어서 order/domain에 PaymentGateway.java와 PaymentResult.java, order/infrastructure에 LegacyPaymentAdapter.java가 보인다 |
| T3-부패-방지-계층.txt | payment-acl | T3 | d194ff1 | 콘솔 실행과 context-map.md | 출력은 T2와 같고, 이어서 context-map.md에 결제 하위 도메인 줄, 주문과 외부 결제 줄, 부패 방지 계층 절과 외부 말과 우리 말 표가 보인다 |
| done-주문-장면.txt | payment-acl | done | d194ff1 | 콘솔 실행 (이클립스 Run 대신) | ===== 외부 결제 연결 =====부터 한도 넘은 결제의 주문 상태 줄까지 spec 실행 결과와 같게 찍힌다 |
