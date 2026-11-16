# order-saga 코드 요약

## T1 결제가 실패해도 줄어 있는 재고 보기
커밋: 7af2fac

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/Main.java | 고침 |

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 0e52de6..e62a54f 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -226,5 +226,16 @@ public class Main {
             System.out.println("주문 목록: " + summary.orderId() + "번, " + summary.memberName() + ", 항목 "
                     + summary.orderLineCount() + "개, " + summary.paymentPrice() + "원, " + summary.status());
         }
+
+        productRepository.save(new Product(5, "노트북", new Money(120000), 10));
+        long laptopOrderId = orderService.placeOrder(new PlaceOrderCommand(2,
+                List.of(new OrderLineCommand(5, 1)), "서울시 서초구"));
+        try {
+            orderService.pay(laptopOrderId);
+        } catch (IllegalStateException e) {
+            eventPublisher.processEvents();
+            System.out.println("결제 실패 뒤: 노트북 재고 " + productRepository.findById(5).orElseThrow().getStock() + "개, "
+                    + orderService.findOrder(laptopOrderId).getStatus().label());
+        }
     }
 }
```

## T2 주문을 취소하면 재고 되돌리기
커밋: 9d58b02

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/catalog/application/IncreaseStockHandler.java | 새로 |
| ddd/src/ddd/catalog/domain/Product.java | 고침 |
| ddd/src/ddd/order/application/OrderApplicationService.java | 고침 |
| ddd/src/ddd/order/domain/OrderCanceled.java | 새로 |

```java
// ddd/src/ddd/catalog/application/IncreaseStockHandler.java
package ddd.catalog.application;

import ddd.catalog.domain.Product;
import ddd.catalog.domain.ProductRepository;
import ddd.order.domain.OrderCanceled;
import ddd.order.domain.OrderLine;

public class IncreaseStockHandler {

    private final ProductRepository productRepository;

    public IncreaseStockHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void handle(OrderCanceled event) {
        for (OrderLine orderLine : event.orderLines()) {
            Product product = productRepository.findById(orderLine.productId()).orElseThrow();
            product.increaseStock(orderLine.quantity());
            productRepository.save(product);
        }
    }
}
```

```java
// ddd/src/ddd/order/domain/OrderCanceled.java
package ddd.order.domain;

import java.util.List;

public record OrderCanceled(long orderId, List<OrderLine> orderLines) {
}
```

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index 8f77590..5a08336 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -16,7 +16,7 @@
 | 결제하기 | pay | 주문 금액을 치러 결제 완료가 된다 |
 | 배송하기 | ship | 결제 완료인 주문을 보내 배송 중이 된다 |
 | 배송 완료하기 | deliver | 배송 중인 주문이 고객에게 닿아 배송 완료가 된다 |
-| 주문 취소하기 | cancel | 배송 전 주문을 취소 상태로 둔다. 지우지 않는다 |
+| 주문 취소하기 | cancel | 배송 전 주문을 취소 상태로 둔다. 지우지 않는다. 저장한 뒤 주문 취소됨을 남긴다 |
 | 주문 상태 | OrderStatus | 주문이 지금 어디까지 왔는지. 정해진 상태 중 하나다 |
 | 주문 접수 | ORDERED | 주문하기를 마친 상태 |
 | 결제 완료 | PAID | 결제하기를 마친 상태 |
@@ -47,7 +47,7 @@
 | 주문 명세 | OrderSpecification | 주문 하나를 받아 조건을 만족하는지 참 거짓으로만 답하는 것. and로 두 명세를 묶으면 다시 주문 명세가 된다 |
 | 무료 배송 금액 명세 | FreeShippingPriceSpecification | 결제 금액이 50000원 이상인지 답하는 명세 |
 | 무료 배송 지역 명세 | FreeShippingAreaSpecification | 배송 주소가 제주가 아닌지 답하는 명세 |
-| 재고 | stock | 상품이 지금 몇 개 남았는지. 주문하면 나중에 주문 수량만큼 줄어든다 |
+| 재고 | stock | 상품이 지금 몇 개 남았는지. 주문하면 나중에 주문 수량만큼 줄어들고, 주문을 취소하면 나중에 되돌아온다 |
 | 주문 접수됨 | OrderPlaced | 주문이 만들어질 때 주문이 남기는 도메인 이벤트. 주문 번호와 주문 항목을 담는다. 이미 일어난 일이라 바뀌지 않는다 |
 | 이벤트 발행기 | EventPublisher | 도메인 이벤트를 받아 쌓아 두었다가, 이벤트를 처리할 때 그 이벤트를 구독한 곳에 보내는 곳. 주문 응용 서비스는 주문을 저장한 뒤 이벤트를 여기에 넘긴다 |
 | 재고 줄이기 | decreaseStock | 상품 재고를 주문 수량만큼 뺀다 |
@@ -64,3 +64,6 @@
 | 주문 요약 | OrderSummary | 주문 목록 화면 한 줄에 맞춘 읽기 모델. 주문 번호, 회원 이름, 항목 수, 결제 금액, 상태 글자를 숫자와 글자로만 가진다. 바꾸는 동작이 없다 |
 | 주문 조회 서비스 | OrderQueryService | 회원 번호로 주문 요약 목록을 돌려주는 곳. 주문을 바꾸거나 저장하지 않고 읽기만 한다 |
 | 명령과 조회 나누기 | CQRS | 바꾸는 쪽(명령)과 읽는 쪽(조회)을 다른 모델로 나누는 것. 명령은 주문 응용 서비스가 주문 애그리거트로 규칙을 지키며 하고, 조회는 주문 조회 서비스가 주문 요약으로 한다 |
+| 주문 취소됨 | OrderCanceled | 주문을 취소했을 때 남는 도메인 이벤트. 주문 번호와 주문 항목을 담는다. 주문 취소하기가 이벤트 발행기에 넘긴다 |
+| 재고 되돌리기 | increaseStock | 상품 재고를 주문 수량만큼 더해 줄이기 전으로 돌린다 |
+| 재고 되돌리기 처리기 | IncreaseStockHandler | 주문 취소됨 이벤트를 구독해 주문 항목마다 상품 재고를 되돌리고 저장하는 곳 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index e62a54f..67ac49e 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -3,6 +3,7 @@ package ddd;
 import java.util.List;
 
 import ddd.catalog.application.DecreaseStockHandler;
+import ddd.catalog.application.IncreaseStockHandler;
 import ddd.catalog.domain.Product;
 import ddd.catalog.domain.ProductRepository;
 import ddd.catalog.infrastructure.MemoryProductRepository;
@@ -18,6 +19,7 @@ import ddd.order.application.OrderSummary;
 import ddd.order.application.PlaceOrderCommand;
 import ddd.order.domain.DiscountPolicy;
 import ddd.order.domain.Order;
+import ddd.order.domain.OrderCanceled;
 import ddd.order.domain.OrderLine;
 import ddd.order.domain.OrderLineFactory;
 import ddd.order.domain.OrderPlaced;
@@ -48,6 +50,8 @@ public class Main {
         EventPublisher eventPublisher = new EventPublisher();
         DecreaseStockHandler decreaseStockHandler = new DecreaseStockHandler(productRepository);
         eventPublisher.subscribe(OrderPlaced.class, decreaseStockHandler::handle);
+        IncreaseStockHandler increaseStockHandler = new IncreaseStockHandler(productRepository);
+        eventPublisher.subscribe(OrderCanceled.class, increaseStockHandler::handle);
         OrderRepository orderRepository = new MemoryOrderRepository();
         OrderApplicationService orderService = new OrderApplicationService(orderRepository,
                 new OrderLineFactory(productRepository), memberRepository, new DiscountPolicy(), eventPublisher,
diff --git a/ddd/src/ddd/catalog/domain/Product.java b/ddd/src/ddd/catalog/domain/Product.java
index b14d4bd..8c2623f 100644
--- a/ddd/src/ddd/catalog/domain/Product.java
+++ b/ddd/src/ddd/catalog/domain/Product.java
@@ -36,6 +36,10 @@ public class Product {
         stock = stock - quantity;
     }
 
+    public void increaseStock(int quantity) {
+        stock = stock + quantity;
+    }
+
     @Override
     public boolean equals(Object o) {
         if (!(o instanceof Product other)) {
diff --git a/ddd/src/ddd/order/application/OrderApplicationService.java b/ddd/src/ddd/order/application/OrderApplicationService.java
index f6b6d24..868259f 100644
--- a/ddd/src/ddd/order/application/OrderApplicationService.java
+++ b/ddd/src/ddd/order/application/OrderApplicationService.java
@@ -8,6 +8,7 @@ import ddd.member.domain.MemberRepository;
 import ddd.order.domain.Address;
 import ddd.order.domain.DiscountPolicy;
 import ddd.order.domain.Order;
+import ddd.order.domain.OrderCanceled;
 import ddd.order.domain.OrderLine;
 import ddd.order.domain.OrderLineFactory;
 import ddd.order.domain.OrderRepository;
@@ -86,5 +87,6 @@ public class OrderApplicationService {
         Order order = findOrder(orderId);
         order.cancel();
         orderRepository.save(order);
+        eventPublisher.publish(List.of(new OrderCanceled(order.getId(), order.getOrderLines())));
     }
 }
```

## T3 결제가 거절되면 주문 취소로 보상하기
커밋: 0e8599e

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/order/application/OrderApplicationService.java | 고침 |

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index 5a08336..a1c8e18 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -67,3 +67,5 @@
 | 주문 취소됨 | OrderCanceled | 주문을 취소했을 때 남는 도메인 이벤트. 주문 번호와 주문 항목을 담는다. 주문 취소하기가 이벤트 발행기에 넘긴다 |
 | 재고 되돌리기 | increaseStock | 상품 재고를 주문 수량만큼 더해 줄이기 전으로 돌린다 |
 | 재고 되돌리기 처리기 | IncreaseStockHandler | 주문 취소됨 이벤트를 구독해 주문 항목마다 상품 재고를 되돌리고 저장하는 곳 |
+| 사가 | saga | 여러 번 따로 저장하는 일을 단계로 잇는 것. 한 단계가 실패하면 앞에서 끝난 단계마다 보상을 해서 처음과 맞춘다. 결제하기가 거절되면 주문 취소하기를 하고, 주문 취소됨으로 재고를 되돌린다 |
+| 보상 | compensation | 끝난 단계를 지우지 않고 반대 일을 한 번 더 해서 되돌리는 것. 주문 접수의 보상은 주문 취소하기, 재고 줄이기의 보상은 재고 되돌리기다 |
diff --git a/ddd/src/ddd/order/application/OrderApplicationService.java b/ddd/src/ddd/order/application/OrderApplicationService.java
index 868259f..ade60e2 100644
--- a/ddd/src/ddd/order/application/OrderApplicationService.java
+++ b/ddd/src/ddd/order/application/OrderApplicationService.java
@@ -65,6 +65,7 @@ public class OrderApplicationService {
         order.pay(discountPolicy.discount(member, order));
         PaymentResult result = paymentGateway.pay(order.getPaymentPrice());
         if (!result.approved()) {
+            cancel(orderId);
             throw new IllegalStateException(result.reason());
         }
         orderRepository.save(order);
```

## T4 규칙 두는 곳 정리하기
커밋: d5177e6

| 파일 | 변경 |
|---|---|
| ddd/context-map.md | 고침 |
| ddd/where-rules-live.md | 새로 |

```md
<!-- ddd/where-rules-live.md -->
# 규칙 두는 곳 정리

새 규칙이 생기면 이 문서에서 둘 곳을 고른다.

## 규칙 두는 곳

| 두는 곳 | 두는 규칙 | 이 프로그램의 예 |
|---|---|---|
| 값 객체 | 값 하나가 늘 지켜야 하는 것 | Money는 0원 이상이다. Address는 비어 있지 않다 |
| 애그리거트 | 한 애그리거트 안에서 늘 함께 맞아야 하는 것 | Order는 항목이 하나 이상이고 주문 금액이 항목 합과 같다. OrderStatus가 상태가 바뀌는 길을 정한다 |
| 도메인 서비스 | 두 애그리거트 이상을 읽고 계산만 하는 것 | DiscountPolicy는 회원 등급과 주문 금액으로 할인 금액을 낸다 |
| 명세 | 참 거짓으로 답하는 조건 | FreeShippingSpecification은 50000원 이상이고 제주가 아니면 무료 배송이다 |
| 이벤트 | 다른 애그리거트가 나중에 따로 맞추는 것 | OrderPlaced를 받아 재고를 줄이고 OrderCanceled를 받아 되돌린다 |
| 사가 | 따로 저장한 단계가 실패하면 앞 단계를 되돌리는 것 | OrderApplicationService의 결제하기가 거절되면 주문 취소하기로 보상한다 |
| 응용 서비스 | 꺼내고 시키고 저장하는 순서 | OrderApplicationService는 주문을 꺼내 주문에게 시키고 저장한다. 규칙은 갖지 않는다 |

## 고르는 순서

- 값 하나만 보면 되면 값 객체에 둔다
- 한 애그리거트의 칸만 보면 되면 애그리거트에 둔다
- 두 애그리거트 이상을 읽고 계산만 하면 도메인 서비스에 둔다
- 참 거짓 조건을 다른 조건과 묶어 쓰면 명세로 뺀다
- 다른 애그리거트가 따로 저장해도 되면 이벤트로 알린다
- 따로 저장한 단계를 실패 때 되돌려야 하면 사가로 보상을 잇는다
- 남는 것은 순서뿐이고 그 순서를 응용 서비스가 맡는다

## 사가

결제하기는 주문, 재고, 결제를 따로 저장한다.
그래서 한 번에 되돌릴 수 없다.

| 단계 | 맡는 곳 | 보상 | 보상을 맡는 곳 |
|---|---|---|---|
| 주문 접수 | OrderApplicationService의 주문하기 | 주문 취소 | OrderApplicationService의 주문 취소하기 |
| 재고 줄이기 | DecreaseStockHandler | 재고 되돌리기 | IncreaseStockHandler |
| 결제 | PaymentGateway | 실패하면 위 두 보상을 한다 | OrderApplicationService의 결제하기 |

- 보상은 끝난 일을 지우지 않고 반대 일을 한 번 더 한다
- 결제가 거절된 주문은 취소로 남는다
- 재고는 이벤트를 처리한 뒤에 맞춰진다
```

```diff
diff --git a/ddd/context-map.md b/ddd/context-map.md
index d6019c4..082a1ab 100644
--- a/ddd/context-map.md
+++ b/ddd/context-map.md
@@ -27,8 +27,8 @@
 | 쓰는 쪽 | 쓰이는 쪽 | 무엇 |
 |---|---|---|
 | 주문 | 상품 | 주문 항목 팩토리가 상품 저장소에서 상품 이름과 가격을 읽는다 |
-| 주문 | 회원 | 할인 정책과 주문 응용 서비스가 회원과 회원 등급을 읽는다 |
-| 상품 | 주문 | 재고 줄이기 처리기가 주문 접수됨 이벤트를 받아 재고를 줄인다 |
+| 주문 | 회원 | 할인 정책과 주문 응용 서비스가 회원과 회원 등급을 읽고, 주문 조회 서비스가 회원 이름을 읽는다 |
+| 상품 | 주문 | 재고 줄이기 처리기가 주문 접수됨 이벤트를 받아 재고를 줄이고, 재고 되돌리기 처리기가 주문 취소됨 이벤트를 받아 재고를 되돌린다 |
 | 배송 | 주문 | 결제 완료 메시지 처리기가 결제 완료 메시지 글자를 읽어 배송을 만든다 |
 | 주문 | 외부 결제 | 결제 어댑터가 결제 금액을 외부 결제 모듈에 맡기고 받은 코드 글자를 결제 결과로 번역한다 |
 | 주문, 상품 | 공유 커널 | 돈 |
```
