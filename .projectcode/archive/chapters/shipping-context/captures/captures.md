# 캡처

| 파일 | 장 | task | 커밋 | 찍은 화면 | 보이는 것 |
|---|---|---|---|---|---|
| T1-결제-완료-메시지.txt | shipping-context | T1 | 76bdd81 | 콘솔 실행과 application 폴더 | ===== 배송 컨텍스트 ===== 아래 [결제 완료를 메시지로 알리기]에 보낸 메시지: ORDER_PAID|orderId=1|address=서울시 강남구와 주문 상태: 주문 접수 -> 결제 완료가 찍히고, 이어서 src/ddd/order/application에 MessageSender와 OrderPaidNotifier가 더 보인다 |
| T2-배송-컨텍스트.txt | shipping-context | T2 | 15092b3 | 콘솔 실행과 src 폴더 | [배송 컨텍스트가 메시지 읽어 배송 만들기]에 배송 컨텍스트: 1번 주문, 서울시 강남구가 찍히고, 이어서 src/ddd에 shipping 폴더와 그 아래 application, domain, infrastructure가 보인다 |
| done-주문-장면.txt | shipping-context | done | 15092b3 | 콘솔 실행 (이클립스 Run 대신) | ===== 배송 컨텍스트 =====부터 배송 컨텍스트: 1번 주문, 서울시 강남구 줄까지 spec 실행 결과와 같게 찍힌다 |
