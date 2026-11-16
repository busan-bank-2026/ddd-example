# discount 작업

- [x] 테스트 작성
- [x] T1 회원에 등급 더하기
  - 근거: S1, S5
  - 파일: ddd/src/ddd/domain/MemberGrade.java, ddd/src/ddd/domain/Member.java, ddd/src/ddd/Main.java, ddd/glossary.md
  - 실행하면: 김철수를 VIP로, 이영희를 일반으로 저장했는데 출력 스무 줄은 그대로이고, src/ddd/domain에 MemberGrade가 더 보인다
  - Why: 할인을 정하려면 회원이 어떤 등급인지 먼저 알아야 한다. 등급은 회원이 누구인지를 말하므로 회원이 갖는다
  - 다른 장: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference, order-factory
- [x] T2 할인 정책으로 결제 금액 정하기
  - 근거: S2, S3, S4, S5
  - 파일: ddd/src/ddd/domain/DiscountPolicy.java, ddd/src/ddd/domain/Money.java, ddd/src/ddd/domain/Order.java, ddd/src/ddd/application/OrderApplicationService.java, ddd/src/ddd/Main.java, ddd/glossary.md
  - 실행하면: 결제: 결제 완료 줄 다음에 할인: VIP 3100원, 결제 금액 27900원이 찍힌다
  - Why: 할인은 회원 등급과 주문 금액을 함께 봐야 해서 회원에도 주문에도 속하지 않는다. 계산은 도메인 서비스가 하고, 할인 금액을 기억하며 결제 완료가 되는 일은 주문이 한다
  - 다른 장: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference, order-factory
- [x] 수렴 점검
- [x] 캡처
- [x] 도식
