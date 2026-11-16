# payment-acl 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/legacypay/LegacyPayGateway.java | 새로 | 외부 결제 모듈. 가게 키와 원 단위 int를 받아 코드 글자를 돌려준다. 붙여넣기 |
| ddd/src/ddd/order/domain/PaymentGateway.java | 새로 | 결제 창구 인터페이스. 우리 말로 정한 결제 |
| ddd/src/ddd/order/domain/PaymentResult.java | 새로 | 결제 결과 record. 승인인지와 거절 이유 |
| ddd/src/ddd/order/infrastructure/LegacyPaymentAdapter.java | 새로 | 결제 어댑터. 부패 방지 계층. 외부 모듈을 불러 코드 글자를 결제 결과로 번역한다 |
| ddd/src/ddd/order/application/OrderApplicationService.java | 고침 | 결제 창구를 받고, 결제하기에서 결제 금액을 맡기고 거절이면 거절한다 |
| ddd/src/ddd/Main.java | 고침 | 외부 결제 응답 장면, 결제 어댑터 연결, 한도 넘은 결제 장면 |
| ddd/glossary.md | 고침 | 결제 창구, 결제 결과, 결제 어댑터, 부패 방지 계층 |
| ddd/context-map.md | 고침 | 부패 방지 계층 절, 하위 도메인 결제 줄, 컨텍스트 사이 외부 결제 줄 |
| tests/ddd/PaymentAclTest.java | 새로 | S1부터 S6까지 행동 테스트 |
| tests/ddd 아래 앞 장 테스트 열일곱 개 | 고침 | 주문 응용 서비스 생성자에 결제 어댑터, 기대 출력에 두 줄, 생성자 매개변수 목록, 주문 도메인 파일 수 |

## 코드 계획
LegacyPayGateway.java (T1, 붙여넣기, 묶음 legacypay)
- 묶음을 ddd 밖 legacypay에 두는 이유: 우리가 만들지 않은 외부 모듈이다. 고칠 수 없는 남의 코드로 다룬다
- public String approve(String merchantKey, int amountWon)
- merchantKey가 "DDD-SHOP"이 아니면 "E201", amountWon이 100000보다 크면 "E101", 아니면 "0000"
- 우리 말과 일부러 다르다. 영어 이름, 원 단위 int, 코드 글자

PaymentGateway.java (T2, 주문 도메인 계층)
- PaymentResult pay(Money amount)
- 도메인에 두는 이유: 주문 저장소처럼 도메인이 필요한 일을 우리 말로 정하고 인프라가 구현한다

PaymentResult.java (T2, 주문 도메인 계층)
- record PaymentResult(boolean approved, String reason)
- 승인이면 reason은 빈 글자다

LegacyPaymentAdapter.java (T2, 주문 인프라 계층)
- implements PaymentGateway. 생성자에서 LegacyPayGateway를 받는다
- 가게 키는 private static final String MERCHANT_KEY = "DDD-SHOP"으로 어댑터만 안다
- pay(Money amount): legacyPayGateway.approve(MERCHANT_KEY, amount.amount())로 코드 글자를 받는다
- "0000"이면 new PaymentResult(true, ""), "E101"이면 new PaymentResult(false, "결제 한도를 넘었습니다"), 그 밖은 new PaymentResult(false, "결제에 실패했습니다")
- 인프라에 두는 이유: 외부 모듈을 import하는 곳은 이 파일 하나다. 외부 말이 여기서 멈춘다

OrderApplicationService.java (T2)
- private final PaymentGateway paymentGateway 칸. 생성자 마지막 매개변수로 받는다
- pay(orderId): order.pay(할인) 다음에 PaymentResult result = paymentGateway.pay(order.getPaymentPrice())
- result.approved()가 아니면 throw new IllegalStateException(result.reason()). 저장하지 않아서 주문은 주문 접수로 남는다
- 승인이면 지금처럼 저장하고 결제 완료 메시지를 넘긴다
- order.pay를 먼저 부르는 이유: 두 번 결제처럼 주문 규칙에 걸리는 결제는 외부에 맡기기 전에 거절한다

Main.java
- T1: 배송 컨텍스트 줄 다음에 new LegacyPayGateway()를 만들고 approve("DDD-SHOP", 27900)과 approve("DDD-SHOP", 108000)을 찍는다. 외부 결제 응답: 27900원 0000, 108000원 E101
- T2: 주문 응용 서비스를 만들 때 마지막에 new LegacyPaymentAdapter(new LegacyPayGateway())를 넘긴다
- T2: 맨 끝에 이영희가 자바 책 6개(가격 바뀐 뒤 18000원, 108000원)를 서울시 송파구로 주문하고 결제한다. IllegalStateException을 잡아 한도 넘은 결제: 거절 이유, 주문 상태를 찍는다

glossary.md
- T2: 결제 창구 PaymentGateway, 결제 결과 PaymentResult, 결제 어댑터 LegacyPaymentAdapter 줄
- T3: 부패 방지 계층 줄. 외부 코드 글자와 가게 키는 쓰지 않는다

context-map.md
- T3: 하위 도메인 표에 결제 줄(일반, 외부 결제 모듈, legacypay)
- T3: 컨텍스트 사이 표에 주문, 외부 결제, 결제 어댑터가 결제 금액을 맡기고 코드 글자를 결제 결과로 번역한다 줄. 아래에 주문 도메인과 응용 계층은 legacypay를 import하지 않는다는 줄
- T3: 부패 방지 계층 절. 뜻, 맡는 곳 LegacyPaymentAdapter, 주문이 아는 것 PaymentGateway와 PaymentResult, 외부 말과 우리 말 표

PaymentAclTest.java
- 패키지는 ddd다. 외부 모듈과 주문 클래스는 import로 부른다
- S1은 approve의 네 경우와 Main 출력에서 배송 컨텍스트 줄 다음 줄을 본다
- S2는 결제 창구가 ddd.order.domain의 인터페이스이고 pay(Money)가 결제 결과를 돌려주는지, 결제 결과 record의 두 칸, src/ddd 아래에서 legacypay를 쓰는 파일이 Main과 결제 어댑터뿐인지 본다
- S3은 결제 어댑터가 인프라에 있고 결제 창구를 구현하는지, 승인과 한도 거절, 외부 모듈을 바꿔 끼운 다른 코드 글자의 거절, 외부 모듈이 받은 가게 키와 금액을 본다
- S4는 결제 어댑터와 람다 결제 창구로 결제하기의 승인과 거절, 거절 뒤 주문 상태와 결제 완료 메시지 없음, Main 출력 전체를 본다
- S5는 용어집 세 줄과 E101, merchantKey가 없는지 본다
- S6은 컨텍스트 맵 세 곳과 용어집 부패 방지 계층 줄을 본다

앞 장 테스트
- 주문 응용 서비스를 만드는 열네 개 테스트는 마지막 인자로 new LegacyPaymentAdapter(new LegacyPayGateway())를 넘긴다
- 기대 출력 전체를 보는 여덟 개 테스트에 두 줄을 더한다
- LayersTest는 생성자 매개변수 목록에 PaymentGateway를 더한다
- BoundedContextsTest는 도메인 파일 수를 19로 바꾼다

## 핵심 코드
String code = legacyPayGateway.approve(MERCHANT_KEY, amount.amount());
if (code.equals("0000")) {
    return new PaymentResult(true, "");
}
if (code.equals("E101")) {
    return new PaymentResult(false, "결제 한도를 넘었습니다");
}
return new PaymentResult(false, "결제에 실패했습니다");

PaymentResult result = paymentGateway.pay(order.getPaymentPrice());
if (!result.approved()) {
    throw new IllegalStateException(result.reason());
}

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | LegacyPayGateway.java, Main.java |
| S2 | PaymentGateway.java, PaymentResult.java |
| S3 | LegacyPaymentAdapter.java |
| S4 | OrderApplicationService.java, Main.java |
| S5 | glossary.md |
| S6 | context-map.md, glossary.md |

## 라이브러리
- 추가 없음

## 다른 장 파일
- ddd/src/ddd/order/application/OrderApplicationService.java: anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference, order-factory, discount, order-events, bounded-contexts, shipping-context
- ddd/src/ddd/Main.java: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference, order-factory, discount, free-shipping, order-events, optimistic-lock, bounded-contexts, shipping-context
- ddd/context-map.md: bounded-contexts, shipping-context
- ddd/glossary.md: ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, id-reference, order-factory, discount, free-shipping, order-events, optimistic-lock, shipping-context
- tests/ddd 아래 앞 장 테스트: anemic-order, bounded-contexts, discount, free-shipping, id-reference, layers, optimistic-lock, order-aggregate, order-events, order-factory, order-repository, order-status, place-order, rich-entity, shipping-context, ubiquitous-language, value-object
