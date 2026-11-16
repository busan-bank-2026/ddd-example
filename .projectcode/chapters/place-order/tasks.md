# place-order 작업

- [ ] 테스트 작성
- [ ] T1 주문하기 명령으로 주문하기
  - 근거: S1, S2, S3, S4
  - 파일: ddd/src/ddd/PlaceOrderCommand.java, ddd/src/ddd/OrderLineCommand.java, ddd/src/ddd/OrderService.java, ddd/src/ddd/Main.java
  - 실행하면: 출력은 앞 장 열여덟 줄과 같고, Main은 글자와 숫자만 담은 명령으로 주문하고 항목을 더한다
  - Why: 밖에서 들어온 값을 명령 하나로 묶으면 유스케이스 입력이 한눈에 보이고, 도메인 객체로 바꾸는 일은 응용 서비스 한 곳에 모인다
  - 다른 장: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository
- [ ] T2 주문 응용 서비스로 이름 짓기
  - 근거: S5, S6
  - 파일: ddd/src/ddd/OrderService.java, ddd/src/ddd/OrderApplicationService.java, ddd/src/ddd/Main.java, ddd/glossary.md
  - 실행하면: 출력은 그대로이고, 용어집에 주문 응용 서비스, 주문하기 명령, 주문 항목 명령 줄이 보인다
  - Why: 꺼내고 시키고 저장하기만 하는 클래스라는 것을 이름에 담아야 뒤에 올 도메인 서비스와 헷갈리지 않는다
  - 다른 장: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository
- [ ] 수렴 점검
- [ ] 캡처
- [ ] 도식
