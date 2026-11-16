# socket-queue 작업

진행: 붙여넣기

- [x] 테스트 작성
- [x] T1 소켓으로 메시지를 받는 배송 앱
  - 근거: S2, S3
  - 파일: ddd/src/ddd/shipping/infrastructure/SocketMessageReceiver.java, ddd/src/ddd/ShippingApp.java
  - 실행하면: 이클립스에서 ShippingApp을 Run하면 배송 앱이 9090번 포트에서 메시지를 기다립니다가 찍히고 프로그램이 끝나지 않고 기다린다. Main 출력은 서른세 줄 그대로다
  - Why: 소켓은 받는 쪽이 포트를 먼저 열어 두어야 보내는 쪽이 붙을 수 있다. 배송 컨텍스트는 받은 글자를 지금의 처리기에 넘기기만 한다
- [ ] T2 소켓 발행기로 바꿔 끼운 주문 앱
  - 근거: S1, S4
  - 파일: ddd/src/ddd/order/application/SocketEventPublisher.java, ddd/src/ddd/OrderApp.java
  - 실행하면: 이클립스에서 ShippingApp을 먼저 Run하고 기다립니다 줄이 보이면 OrderApp을 Run한다. 주문 앱 콘솔에 결제: 1번 주문, 결제 완료와 결제 완료 메시지를 배송 앱으로 보냈습니다가, 배송 앱 콘솔에 받은 메시지: ORDER_PAID|orderId=1|address=서울시 강남구와 배송 컨텍스트: 1번 주문, 서울시 강남구가 찍힌다
  - Why: 발행기 자리에 소켓 발행기를 넣기만 해도 같은 메시지가 다른 프로그램으로 간다. 주문과 배송의 코드는 그대로라서 메시지를 나르는 방법은 바꿔 끼울 수 있는 기술이라는 것이 드러난다
- [ ] 수렴 점검
- [ ] 캡처
- [ ] 도식
