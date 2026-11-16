# 캡처

| 파일 | 장 | task | 커밋 | 찍은 화면 | 보이는 것 |
|---|---|---|---|---|---|
| T1-배송-앱-기다리기.txt | socket-queue | T1 | dbf254d | 콘솔 실행 (Main, 이어서 ShippingApp) | Main 출력 서른세 줄이 그대로 나오고, 이어서 ShippingApp이 배송 앱이 9090번 포트에서 메시지를 기다립니다를 찍고 끝나지 않고 기다린다. 사진은 3초 기다린 뒤 끊었다 |
| T2-소켓으로-보내기.txt | socket-queue | T2 | 2799af1 | 콘솔 실행 (ShippingApp 먼저, 이어서 OrderApp) | OrderApp이 주문 앱을 시작합니다, 결제: 1번 주문, 결제 완료, 결제 완료 메시지를 배송 앱으로 보냈습니다를 찍고, ShippingApp이 기다립니다, 받은 메시지: ORDER_PAID 뒤에 1번 주문 서울시 강남구가 붙은 줄, 배송 컨텍스트: 1번 주문, 서울시 강남구를 찍고 끝난다 |
| done-소켓-장면.txt | socket-queue | done | 2799af1 | 콘솔 실행 (이클립스에서 ShippingApp, OrderApp 순서로 Run 대신) | 주문 앱 세 줄과 배송 앱 세 줄이 spec 실행 결과와 같게 찍힌다 |
| done-주문-장면.txt | socket-queue | done | 2799af1 | 콘솔 실행 (이클립스 Main Run 대신) | 시작 글부터 결제 실패 뒤 줄까지 서른세 줄이 앞 장과 같게 찍힌다 |
