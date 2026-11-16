# 캡처

| 파일 | 장 | task | 커밋 | 찍은 화면 | 보이는 것 |
|---|---|---|---|---|---|
| T1-외부-결제-응답.txt | payment-acl | T1 | 36f5c3d | 콘솔 실행과 src 폴더 | 배송 컨텍스트 줄 다음에 외부 결제 응답: 27900원 0000, 108000원 E101이 찍히고, 이어서 src에 ddd와 나란히 legacypay 폴더와 그 안의 LegacyPayGateway.java가 보인다 |
| T2-결제-어댑터.txt | payment-acl | T2 | bd3c89b | 콘솔 실행과 주문 폴더 | 앞 결제 두 건은 그대로 결제 완료이고, 외부 결제 응답 줄 다음에 한도 넘은 결제: 결제 한도를 넘었습니다, 주문 접수가 찍히고, 이어서 order/domain에 PaymentGateway.java와 PaymentResult.java, order/infrastructure에 LegacyPaymentAdapter.java가 보인다 |
| T3-부패-방지-계층.txt | payment-acl | T3 | 75f9390 | 콘솔 실행과 context-map.md | 출력 서른한 줄이 그대로 나오고, 이어서 context-map.md에 결제 하위 도메인 줄, 주문과 외부 결제 줄, 부패 방지 계층 절과 외부 말과 우리 말 표가 보인다 |
| done-주문-장면.txt | payment-acl | done | 75f9390 | 콘솔 실행 (이클립스 Run 대신) | 시작 글부터 한도 넘은 결제 줄까지 서른한 줄이 spec 실행 결과와 같게 찍힌다 |
