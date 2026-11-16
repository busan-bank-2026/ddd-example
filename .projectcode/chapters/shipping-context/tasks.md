# shipping-context 작업

- [x] 테스트 작성
- [x] T1 결제 완료를 정해진 글자로 알리기
  - 근거: S1, S2
  - 파일: ddd/src/ddd/order/application/OrderOpenHostService.java, ddd/src/ddd/order/application/OrderApplicationService.java, ddd/src/ddd/Main.java, ddd/context-map.md, ddd/glossary.md
  - 실행하면: 동시 수정 줄 다음에 결제 완료 메시지: ORDER_PAID|orderId=1|address=서울시 강남구와 4번 주문 메시지가 찍힌다
  - Why: 바깥 컨텍스트에 알릴 것을 한 창구에서 정해진 글자 모양으로 내보내야 받는 쪽이 주문 클래스를 몰라도 읽는다
  - 다른 장: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference, order-factory, discount, free-shipping, order-events, optimistic-lock, bounded-contexts
- [x] T2 배송 컨텍스트가 메시지 읽어 배송 만들기
  - 근거: S3, S4, S5
  - 파일: ddd/src/ddd/shipping/domain/Shipment.java, ddd/src/ddd/shipping/domain/ShipmentRepository.java, ddd/src/ddd/shipping/infrastructure/MemoryShipmentRepository.java, ddd/src/ddd/shipping/application/OrderPaidMessageHandler.java, ddd/src/ddd/Main.java, ddd/context-map.md, ddd/glossary.md
  - 실행하면: 결제 완료 메시지 두 줄 다음에 배송 컨텍스트: 1번 주문, 서울시 강남구가 찍히고 src/ddd에 shipping 폴더가 보인다
  - Why: 배송 컨텍스트가 발행된 언어의 글자만 읽어 자기 모델로 바꾸면 주문 컨텍스트가 안을 바꿔도 메시지 모양만 지키면 된다
  - 다른 장: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference, order-factory, discount, free-shipping, order-events, optimistic-lock, bounded-contexts
- [ ] 수렴 점검
- [ ] 캡처
- [ ] 도식
