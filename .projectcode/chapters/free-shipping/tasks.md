# free-shipping 작업

- [x] 테스트 작성
- [x] T1 무료 배송 명세로 배송비 정하기
  - 근거: S1, S2, S4
  - 파일: ddd/src/ddd/domain/FreeShippingSpecification.java, ddd/src/ddd/domain/Order.java, ddd/src/ddd/Main.java, ddd/glossary.md
  - 실행하면: 할인: VIP 3100원, 결제 금액 27900원 줄 다음에 배송비: 3000원이 찍히고, src/ddd/domain에 FreeShippingSpecification이 더 보인다
  - Why: 무료 배송 조건을 주문 안의 if로 두지 않고 이름 붙은 객체로 만들면 무엇을 묻는지 보인다. 명세는 참 거짓만 답하고 배송비 돈은 주문이 정한다
  - 다른 장: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference, order-factory, discount
- [x] T2 금액 명세와 지역 명세를 and로 묶기
  - 근거: S3, S4
  - 파일: ddd/src/ddd/domain/OrderSpecification.java, ddd/src/ddd/domain/FreeShippingPriceSpecification.java, ddd/src/ddd/domain/FreeShippingAreaSpecification.java, ddd/src/ddd/domain/FreeShippingSpecification.java, ddd/src/ddd/Main.java, ddd/glossary.md
  - 실행하면: 가격 바뀐 뒤 줄 앞에 6만원 주문 배송비: 서울 0원, 제주 3000원이 찍히고, src/ddd/domain에 OrderSpecification, FreeShippingPriceSpecification, FreeShippingAreaSpecification이 더 보인다
  - Why: 제주는 무료 배송에서 빠진다는 조건이 하나 더 생겼다. 조건마다 명세를 만들고 and로 묶으면 주문은 그대로 무료 배송 명세에게만 묻는다
  - 다른 장: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference, order-factory, discount
- [ ] 수렴 점검
- [ ] 캡처
- [ ] 도식
