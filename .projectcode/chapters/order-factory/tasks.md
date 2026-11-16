# order-factory 작업

- [x] 테스트 작성
- [x] T1 팩토리로 주문 항목 만들기
  - 근거: S1, S2, S3, S5
  - 파일: ddd/src/ddd/domain/OrderLineFactory.java, ddd/src/ddd/application/OrderLineCommand.java, ddd/src/ddd/application/OrderApplicationService.java, ddd/src/ddd/Main.java, ddd/glossary.md
  - 실행하면: 명령에 상품 번호와 수량만 넣었는데도 출력 열아홉 줄이 그대로이고, 주문 항목의 자바 책과 볼펜 이름과 가격은 상품 저장소에서 온 것이다
  - Why: 주문 항목을 만들려면 상품 저장소에서 상품을 꺼내야 하는데, 이 일을 주문이나 응용 서비스가 아닌 팩토리 한 곳에 모아야 만드는 규칙이 흩어지지 않는다
  - 다른 장: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference
- [ ] T2 없는 상품으로 주문 막기
  - 근거: S4
  - 파일: ddd/src/ddd/domain/OrderLineFactory.java, ddd/src/ddd/Main.java
  - 실행하면: 없는 주문 결제 줄 다음에 없는 상품 주문: 상품이 없습니다가 찍힌다
  - Why: 팩토리가 상품을 찾는 곳이라 없는 상품을 알아채고 업무 말로 거절하는 것도 팩토리가 맡는다
  - 다른 장: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference
- [ ] 수렴 점검
- [ ] 캡처
- [ ] 도식
