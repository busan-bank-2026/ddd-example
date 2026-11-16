# payment-acl 코드 요약

## T1 외부 결제 모듈 붙여넣기
커밋: 36f5c3d

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/legacypay/LegacyPayGateway.java | 새로 |

```java
// ddd/src/legacypay/LegacyPayGateway.java
package legacypay;

public class LegacyPayGateway {

    public String approve(String merchantKey, int amountWon) {
        if (!merchantKey.equals("DDD-SHOP")) {
            return "E201";
        }
        if (amountWon > 100000) {
            return "E101";
        }
        return "0000";
    }
}
```

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 01b6268..2514dbc 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -26,6 +26,7 @@ import ddd.shipping.application.OrderPaidMessageHandler;
 import ddd.shipping.domain.Shipment;
 import ddd.shipping.domain.ShipmentRepository;
 import ddd.shipping.infrastructure.MemoryShipmentRepository;
+import legacypay.LegacyPayGateway;
 
 public class Main {
 
@@ -201,5 +202,9 @@ public class Main {
         eventPublisher.processEvents();
         Shipment shipment = shipmentRepository.findByOrderId(orderId).orElseThrow();
         System.out.println("배송 컨텍스트: " + shipment.getOrderId() + "번 주문, " + shipment.getAddress());
+
+        LegacyPayGateway legacyPayGateway = new LegacyPayGateway();
+        System.out.println("외부 결제 응답: 27900원 " + legacyPayGateway.approve("DDD-SHOP", 27900) + ", 108000원 "
+                + legacyPayGateway.approve("DDD-SHOP", 108000));
     }
 }
```

## T2 결제 어댑터로 외부 말을 우리 말로 바꾸기
커밋: bd3c89b

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/order/application/OrderApplicationService.java | 고침 |
| ddd/src/ddd/order/domain/PaymentGateway.java | 새로 |
| ddd/src/ddd/order/domain/PaymentResult.java | 새로 |
| ddd/src/ddd/order/infrastructure/LegacyPaymentAdapter.java | 새로 |

```java
// ddd/src/ddd/order/domain/PaymentGateway.java
package ddd.order.domain;

import ddd.shared.Money;

public interface PaymentGateway {

    PaymentResult pay(Money amount);
}
```

```java
// ddd/src/ddd/order/domain/PaymentResult.java
package ddd.order.domain;

public record PaymentResult(boolean approved, String reason) {
}
```

```java
// ddd/src/ddd/order/infrastructure/LegacyPaymentAdapter.java
package ddd.order.infrastructure;

import ddd.order.domain.PaymentGateway;
import ddd.order.domain.PaymentResult;
import ddd.shared.Money;
import legacypay.LegacyPayGateway;

public class LegacyPaymentAdapter implements PaymentGateway {

    private static final String MERCHANT_KEY = "DDD-SHOP";

    private final LegacyPayGateway legacyPayGateway;

    public LegacyPaymentAdapter(LegacyPayGateway legacyPayGateway) {
        this.legacyPayGateway = legacyPayGateway;
    }

    @Override
    public PaymentResult pay(Money amount) {
        String code = legacyPayGateway.approve(MERCHANT_KEY, amount.amount());
        if (code.equals("0000")) {
            return new PaymentResult(true, "");
        }
        if (code.equals("E101")) {
            return new PaymentResult(false, "결제 한도를 넘었습니다");
        }
        return new PaymentResult(false, "결제에 실패했습니다");
    }
}
```

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index b209b58..261b76b 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -57,3 +57,6 @@
 | 배송 | Shipment | 배송 컨텍스트가 결제한 주문을 보내려고 만드는 것. 주문 번호와 배송 주소를 가진다. 주문 번호가 같으면 같은 배송이다 |
 | 배송 저장소 | ShipmentRepository | 배송을 넣어 두고 주문 번호로 다시 꺼내는 곳 |
 | 결제 완료 메시지 처리기 | OrderPaidMessageHandler | 결제 완료 메시지 글자를 받아 주문 번호와 배송 주소를 읽고 배송을 만들어 저장하는 곳. 결제 완료가 아닌 메시지는 넘긴다 |
+| 결제 창구 | PaymentGateway | 주문 컨텍스트가 결제를 맡기는 곳. 돈을 받아 결제 결과를 돌려준다. 우리 말로 정하고 바깥 결제는 어댑터가 이어 준다 |
+| 결제 결과 | PaymentResult | 결제 창구가 돌려주는 답. 승인인지와 거절 이유를 가진다 |
+| 결제 어댑터 | LegacyPaymentAdapter | 결제 창구를 외부 결제 모듈로 구현한 곳. 돈을 외부 모듈의 금액으로 바꿔 맡기고, 받은 답을 결제 결과로 번역한다 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 2514dbc..dba8187 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -20,6 +20,7 @@ import ddd.order.domain.OrderLine;
 import ddd.order.domain.OrderLineFactory;
 import ddd.order.domain.OrderPlaced;
 import ddd.order.domain.OrderRepository;
+import ddd.order.infrastructure.LegacyPaymentAdapter;
 import ddd.order.infrastructure.MemoryOrderRepository;
 import ddd.shared.Money;
 import ddd.shipping.application.OrderPaidMessageHandler;
@@ -47,7 +48,8 @@ public class Main {
         eventPublisher.subscribe(OrderPlaced.class, decreaseStockHandler::handle);
         OrderRepository orderRepository = new MemoryOrderRepository();
         OrderApplicationService orderService = new OrderApplicationService(orderRepository,
-                new OrderLineFactory(productRepository), memberRepository, new DiscountPolicy(), eventPublisher);
+                new OrderLineFactory(productRepository), memberRepository, new DiscountPolicy(), eventPublisher,
+                new LegacyPaymentAdapter(new LegacyPayGateway()));
 
         List<OrderLineCommand> orderLines = List.of(
                 new OrderLineCommand(1, 2),
@@ -206,5 +208,15 @@ public class Main {
         LegacyPayGateway legacyPayGateway = new LegacyPayGateway();
         System.out.println("외부 결제 응답: 27900원 " + legacyPayGateway.approve("DDD-SHOP", 27900) + ", 108000원 "
                 + legacyPayGateway.approve("DDD-SHOP", 108000));
+
+        long bigOrderId = orderService.placeOrder(new PlaceOrderCommand(2,
+                List.of(new OrderLineCommand(1, 6)), "서울시 송파구"));
+        try {
+            orderService.pay(bigOrderId);
+            System.out.println("한도 넘은 결제: " + orderService.findOrder(bigOrderId).getStatus().label());
+        } catch (IllegalStateException e) {
+            System.out.println("한도 넘은 결제: " + e.getMessage() + ", "
+                    + orderService.findOrder(bigOrderId).getStatus().label());
+        }
     }
 }
diff --git a/ddd/src/ddd/order/application/OrderApplicationService.java b/ddd/src/ddd/order/application/OrderApplicationService.java
index e7139d4..f6b6d24 100644
--- a/ddd/src/ddd/order/application/OrderApplicationService.java
+++ b/ddd/src/ddd/order/application/OrderApplicationService.java
@@ -11,6 +11,8 @@ import ddd.order.domain.Order;
 import ddd.order.domain.OrderLine;
 import ddd.order.domain.OrderLineFactory;
 import ddd.order.domain.OrderRepository;
+import ddd.order.domain.PaymentGateway;
+import ddd.order.domain.PaymentResult;
 
 public class OrderApplicationService {
 
@@ -20,16 +22,19 @@ public class OrderApplicationService {
     private final DiscountPolicy discountPolicy;
     private final EventPublisher eventPublisher;
     private final OrderOpenHostService orderOpenHostService;
+    private final PaymentGateway paymentGateway;
     private long nextId = 1;
 
     public OrderApplicationService(OrderRepository orderRepository, OrderLineFactory orderLineFactory,
-            MemberRepository memberRepository, DiscountPolicy discountPolicy, EventPublisher eventPublisher) {
+            MemberRepository memberRepository, DiscountPolicy discountPolicy, EventPublisher eventPublisher,
+            PaymentGateway paymentGateway) {
         this.orderRepository = orderRepository;
         this.orderLineFactory = orderLineFactory;
         this.memberRepository = memberRepository;
         this.discountPolicy = discountPolicy;
         this.eventPublisher = eventPublisher;
         this.orderOpenHostService = new OrderOpenHostService(eventPublisher);
+        this.paymentGateway = paymentGateway;
     }
 
     public long placeOrder(PlaceOrderCommand command) {
@@ -57,6 +62,10 @@ public class OrderApplicationService {
         Order order = findOrder(orderId);
         Member member = memberRepository.findById(order.getMemberId()).orElseThrow();
         order.pay(discountPolicy.discount(member, order));
+        PaymentResult result = paymentGateway.pay(order.getPaymentPrice());
+        if (!result.approved()) {
+            throw new IllegalStateException(result.reason());
+        }
         orderRepository.save(order);
         orderOpenHostService.publishOrderPaid(order);
     }
```

## T3 컨텍스트 맵에 부패 방지 계층 적기
커밋: 75f9390

| 파일 | 변경 |
|---|---|
| ddd/context-map.md | 고침 |
| ddd/glossary.md | 고침 |

```diff
diff --git a/ddd/context-map.md b/ddd/context-map.md
index bbba310..d6019c4 100644
--- a/ddd/context-map.md
+++ b/ddd/context-map.md
@@ -8,10 +8,12 @@
 | 상품 | 지원 | 주문에 쓸 상품과 재고를 맡는다. 가게에 맞게 직접 만든다 | 상품 컨텍스트 | ddd.catalog |
 | 회원 | 일반 | 이름과 등급만 맡는다. 어느 가게나 비슷해서 사다 써도 된다 | 회원 컨텍스트 | ddd.member |
 | 배송 | 지원 | 결제한 주문을 받을 곳으로 보낸다. 주문을 돕는다 | 배송 컨텍스트 | ddd.shipping |
+| 결제 | 일반 | 돈을 받는 일은 어느 가게나 같다. 외부 결제 모듈을 사다 쓴다 | 외부 결제 모듈 | legacypay |
 
 - 하위 도메인은 업무를 나눈 것이다
 - 바운디드 컨텍스트(bounded context)는 한 모델과 한 말이 통하는 코드의 경계다
 - 이 프로그램은 하위 도메인 하나에 컨텍스트 하나를 둔다
+- 결제는 우리가 만들지 않는다. 외부 결제 모듈이 맡는다
 - 컨텍스트 안에는 domain, application, infrastructure 계층을 둔다
 
 ## 공유 커널
@@ -28,6 +30,7 @@
 | 주문 | 회원 | 할인 정책과 주문 응용 서비스가 회원과 회원 등급을 읽는다 |
 | 상품 | 주문 | 재고 줄이기 처리기가 주문 접수됨 이벤트를 받아 재고를 줄인다 |
 | 배송 | 주문 | 결제 완료 메시지 처리기가 결제 완료 메시지 글자를 읽어 배송을 만든다 |
+| 주문 | 외부 결제 | 결제 어댑터가 결제 금액을 외부 결제 모듈에 맡기고 받은 코드 글자를 결제 결과로 번역한다 |
 | 주문, 상품 | 공유 커널 | 돈 |
 
 - 같은 상품이라도 상품 컨텍스트의 상품은 지금 가격과 재고를 갖는다
@@ -35,6 +38,7 @@
 - 주문과 상품은 다른 컨텍스트의 클래스를 import로 바로 쓴다
 - 배송은 주문 컨텍스트의 클래스를 import하지 않는다. 주문의 공개 호스트 서비스가 보낸 메시지 글자만 읽는다
 - 배송 컨텍스트의 배송은 주문 번호와 배송 주소를 자기 모양(숫자와 글자)으로 갖는다
+- 주문 도메인과 응용 계층은 legacypay를 import하지 않는다. 외부 결제는 결제 어댑터로만 쓴다
 
 ## 공개 호스트 서비스와 발행된 언어
 
@@ -52,3 +56,21 @@
 
 - 받는 쪽은 주문 클래스를 몰라도 이 모양만 알면 읽는다
 - 주문 컨텍스트는 안의 모델을 바꿔도 이 모양은 지킨다
+
+## 부패 방지 계층
+
+- 외부 결제 모듈 legacypay는 우리가 만들지 않았고 고칠 수 없다
+- 외부 모듈은 우리와 다른 말을 쓴다. 영어 이름, 원 단위 int, 코드 글자
+- 부패 방지 계층(anticorruption layer)은 바깥 모델의 말이 우리 모델로 새어 들지 않게 경계에서 번역하는 층이다
+- 주문 컨텍스트의 부패 방지 계층은 결제 어댑터 LegacyPaymentAdapter다
+- 주문 도메인과 응용 계층은 결제 창구 PaymentGateway와 결제 결과 PaymentResult만 안다
+- 외부 모듈이 바뀌면 결제 어댑터만 고친다
+
+| 외부 결제 모듈의 말 | 주문 컨텍스트의 말 |
+|---|---|
+| approve(merchantKey, amountWon) | 결제 창구의 pay(돈) |
+| merchantKey 가게 키 | 결제 어댑터만 안다 |
+| amountWon 원 단위 int | 돈 Money |
+| 0000 | 승인 |
+| E101 | 거절. 결제 한도를 넘었습니다 |
+| 그 밖의 코드 | 거절. 결제에 실패했습니다 |
diff --git a/ddd/glossary.md b/ddd/glossary.md
index 261b76b..5ed9dbd 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -60,3 +60,4 @@
 | 결제 창구 | PaymentGateway | 주문 컨텍스트가 결제를 맡기는 곳. 돈을 받아 결제 결과를 돌려준다. 우리 말로 정하고 바깥 결제는 어댑터가 이어 준다 |
 | 결제 결과 | PaymentResult | 결제 창구가 돌려주는 답. 승인인지와 거절 이유를 가진다 |
 | 결제 어댑터 | LegacyPaymentAdapter | 결제 창구를 외부 결제 모듈로 구현한 곳. 돈을 외부 모듈의 금액으로 바꿔 맡기고, 받은 답을 결제 결과로 번역한다 |
+| 부패 방지 계층 | LegacyPaymentAdapter | 바깥 모델의 말이 우리 모델로 새어 들지 않게 경계에서 번역하는 층. 주문 컨텍스트에서는 결제 어댑터가 맡는다 |
```
