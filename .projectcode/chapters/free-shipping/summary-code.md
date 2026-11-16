# free-shipping 코드 요약

## T1 무료 배송 명세로 배송비 정하기
커밋: 15ffe4f

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/domain/FreeShippingSpecification.java | 새로 |
| ddd/src/ddd/domain/Order.java | 고침 |

```java
// ddd/src/ddd/domain/FreeShippingSpecification.java
package ddd.domain;

public class FreeShippingSpecification {

    public boolean isSatisfiedBy(Order order) {
        return order.getPaymentPrice().amount() >= 50000;
    }
}
```

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index 094cd44..fa8daaf 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -41,3 +41,5 @@
 | 할인 정책 | DiscountPolicy | 회원 등급과 주문 금액으로 할인 금액을 계산하는 도메인 서비스. VIP는 주문 금액의 10%, 일반은 0원이다. 계산만 하고 주문을 바꾸지 않는다 |
 | 할인 금액 | discountPrice | 결제할 때 주문 금액에서 깎는 돈. 결제 전에는 0원이다 |
 | 결제 금액 | paymentPrice | 주문 금액에서 할인 금액을 뺀 돈. 실제로 치르는 돈이다 |
+| 무료 배송 명세 | FreeShippingSpecification | 주문이 무료 배송인지 참 거짓으로만 답하는 명세. 결제 금액이 50000원 이상이면 참이다 |
+| 배송비 | shippingFee | 주문을 보내는 데 받는 돈. 결제 금액과 따로 받는다. 무료 배송 명세를 만족하면 0원, 아니면 3000원이다 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 03ce50d..7b167f0 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -54,6 +54,7 @@ public class Main {
         Order paidOrder = orderService.findOrder(orderId);
         System.out.println("할인: " + member.getGrade() + " " + paidOrder.getDiscountPrice().amount() + "원, 결제 금액 "
                 + paidOrder.getPaymentPrice().amount() + "원");
+        System.out.println("배송비: " + paidOrder.getShippingFee().amount() + "원");
 
         orderService.ship(orderId);
         System.out.println("배송: " + orderService.findOrder(orderId).getStatus().label());
diff --git a/ddd/src/ddd/domain/Order.java b/ddd/src/ddd/domain/Order.java
index 6231b8c..d7b4c20 100644
--- a/ddd/src/ddd/domain/Order.java
+++ b/ddd/src/ddd/domain/Order.java
@@ -60,6 +60,13 @@ public class Order implements Serializable {
         return totalPrice.minus(discountPrice);
     }
 
+    public Money getShippingFee() {
+        if (new FreeShippingSpecification().isSatisfiedBy(this)) {
+            return new Money(0);
+        }
+        return new Money(3000);
+    }
+
     public OrderStatus getStatus() {
         return status;
     }
```

## T2 금액 명세와 지역 명세를 and로 묶기
커밋: 7834719

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/domain/FreeShippingAreaSpecification.java | 새로 |
| ddd/src/ddd/domain/FreeShippingPriceSpecification.java | 새로 |
| ddd/src/ddd/domain/FreeShippingSpecification.java | 고침 |
| ddd/src/ddd/domain/OrderSpecification.java | 새로 |

```java
// ddd/src/ddd/domain/FreeShippingAreaSpecification.java
package ddd.domain;

public class FreeShippingAreaSpecification implements OrderSpecification {

    @Override
    public boolean isSatisfiedBy(Order order) {
        return !order.getAddress().value().startsWith("제주");
    }
}
```

```java
// ddd/src/ddd/domain/FreeShippingPriceSpecification.java
package ddd.domain;

public class FreeShippingPriceSpecification implements OrderSpecification {

    @Override
    public boolean isSatisfiedBy(Order order) {
        return order.getPaymentPrice().amount() >= 50000;
    }
}
```

```java
// ddd/src/ddd/domain/OrderSpecification.java
package ddd.domain;

public interface OrderSpecification {

    boolean isSatisfiedBy(Order order);

    default OrderSpecification and(OrderSpecification other) {
        return order -> isSatisfiedBy(order) && other.isSatisfiedBy(order);
    }
}
```

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index fa8daaf..ca5fcf0 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -41,5 +41,8 @@
 | 할인 정책 | DiscountPolicy | 회원 등급과 주문 금액으로 할인 금액을 계산하는 도메인 서비스. VIP는 주문 금액의 10%, 일반은 0원이다. 계산만 하고 주문을 바꾸지 않는다 |
 | 할인 금액 | discountPrice | 결제할 때 주문 금액에서 깎는 돈. 결제 전에는 0원이다 |
 | 결제 금액 | paymentPrice | 주문 금액에서 할인 금액을 뺀 돈. 실제로 치르는 돈이다 |
-| 무료 배송 명세 | FreeShippingSpecification | 주문이 무료 배송인지 참 거짓으로만 답하는 명세. 결제 금액이 50000원 이상이면 참이다 |
+| 무료 배송 명세 | FreeShippingSpecification | 주문이 무료 배송인지 참 거짓으로만 답하는 명세. 무료 배송 금액 명세와 무료 배송 지역 명세를 and로 묶었다. 둘 다 참이면 참이다 |
 | 배송비 | shippingFee | 주문을 보내는 데 받는 돈. 결제 금액과 따로 받는다. 무료 배송 명세를 만족하면 0원, 아니면 3000원이다 |
+| 주문 명세 | OrderSpecification | 주문 하나를 받아 조건을 만족하는지 참 거짓으로만 답하는 것. and로 두 명세를 묶으면 다시 주문 명세가 된다 |
+| 무료 배송 금액 명세 | FreeShippingPriceSpecification | 결제 금액이 50000원 이상인지 답하는 명세 |
+| 무료 배송 지역 명세 | FreeShippingAreaSpecification | 배송 주소가 제주가 아닌지 답하는 명세 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 7b167f0..613cd72 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -152,6 +152,13 @@ public class Main {
         notSaved.cancel();
         System.out.println("저장 안 한 취소: " + orderService.findOrder(order2Id).getStatus().label());
 
+        long seoulOrderId = orderService.placeOrder(new PlaceOrderCommand(2,
+                List.of(new OrderLineCommand(1, 4)), "서울시 마포구"));
+        long jejuOrderId = orderService.placeOrder(new PlaceOrderCommand(2,
+                List.of(new OrderLineCommand(1, 4)), "제주시 연동"));
+        System.out.println("6만원 주문 배송비: 서울 " + orderService.findOrder(seoulOrderId).getShippingFee().amount()
+                + "원, 제주 " + orderService.findOrder(jejuOrderId).getShippingFee().amount() + "원");
+
         productRepository.save(new Product(1, "자바 책", new Money(18000)));
         Order firstOrder = orderService.findOrder(orderId);
         OrderLine bookLine = firstOrder.getOrderLines().get(0);
diff --git a/ddd/src/ddd/domain/FreeShippingSpecification.java b/ddd/src/ddd/domain/FreeShippingSpecification.java
index e788be3..4a6e683 100644
--- a/ddd/src/ddd/domain/FreeShippingSpecification.java
+++ b/ddd/src/ddd/domain/FreeShippingSpecification.java
@@ -1,8 +1,9 @@
 package ddd.domain;
 
-public class FreeShippingSpecification {
+public class FreeShippingSpecification implements OrderSpecification {
 
+    @Override
     public boolean isSatisfiedBy(Order order) {
-        return order.getPaymentPrice().amount() >= 50000;
+        return new FreeShippingPriceSpecification().and(new FreeShippingAreaSpecification()).isSatisfiedBy(order);
     }
 }
```
