# order-query 작업

- [x] 테스트 작성
- [ ] T1 주문 요약으로 주문 목록 읽기
  - 근거: S1, S2, S3
  - 파일: ddd/src/ddd/order/domain/OrderRepository.java, ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java, ddd/src/ddd/order/application/OrderSummary.java, ddd/src/ddd/order/application/OrderQueryService.java, ddd/src/ddd/Main.java
  - 실행하면: 한도 넘은 결제 줄 다음에 주문 목록: 1번, 김철수, 항목 2개, 27900원, 배송 완료가 찍히고, order/application에 OrderQueryService.java와 OrderSummary.java가 보인다
  - Why: 바꾸는 쪽은 규칙을 지키는 주문 모양이 맞고, 읽는 쪽은 화면 한 줄 모양이 맞다. 조회를 따로 두면 화면이 바뀌어도 주문 규칙은 그대로다
  - 다른 장: order-repository, layers, optimistic-lock, bounded-contexts, project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, place-order, id-reference, order-factory, discount, free-shipping, order-events, shipping-context, payment-acl
- [ ] T2 용어집에 명령과 조회 나누기 적기
  - 근거: S4
  - 파일: ddd/glossary.md
  - 실행하면: 출력 서른두 줄이 그대로 나오고, 용어집 끝에 주문 요약, 주문 조회 서비스, 명령과 조회 나누기 줄이 보인다
  - Why: 어느 쪽이 바꾸고 어느 쪽이 읽기만 하는지 말로 정해 두어야 조회 서비스에 규칙을 넣지 않는다
  - 다른 장: ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, id-reference, order-factory, discount, free-shipping, order-events, optimistic-lock, shipping-context, payment-acl
- [ ] 수렴 점검
- [ ] 캡처
- [ ] 도식
