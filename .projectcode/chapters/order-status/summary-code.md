# order-status 코드 요약

## T1 주문 상태를 한 묶음으로 정하기
커밋: ac2d522

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/Order.java | 고침 |
| ddd/src/ddd/OrderStatus.java | 새로 |

```java
// ddd/src/ddd/OrderStatus.java
package ddd;

public enum OrderStatus {
    ORDERED("주문 접수"),
    PAID("결제 완료"),
    SHIPPING("배송 중"),
    CANCELED("취소");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
```

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index 11b5a96..0824c2d 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -16,7 +16,8 @@
 | 결제하기 | pay | 주문 금액을 치러 결제 완료가 된다 |
 | 배송하기 | ship | 결제 완료인 주문을 보내 배송 중이 된다 |
 | 주문 취소하기 | cancel | 배송 전 주문을 취소 상태로 둔다. 지우지 않는다 |
-| 주문 접수 | 주문 접수 | 주문하기를 마친 상태 |
-| 결제 완료 | 결제 완료 | 결제하기를 마친 상태 |
-| 배송 중 | 배송 중 | 배송하기를 마친 상태 |
-| 취소 | 취소 | 주문 취소하기를 마친 상태 |
+| 주문 상태 | OrderStatus | 주문이 지금 어디까지 왔는지. 정해진 상태 중 하나다 |
+| 주문 접수 | ORDERED | 주문하기를 마친 상태 |
+| 결제 완료 | PAID | 결제하기를 마친 상태 |
+| 배송 중 | SHIPPING | 배송하기를 마친 상태 |
+| 취소 | CANCELED | 주문 취소하기를 마친 상태 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 96b7124..794771f 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -14,21 +14,21 @@ public class Main {
                 new OrderLine("볼펜", new Money(1000), 1));
         Order order = orderService.placeOrder("김철수", orderLines, "서울시 강남구");
         System.out.println("주문: " + order.getId() + "번, " + order.getCustomerName() + ", "
-                + order.getTotalPrice().amount() + "원, " + order.getStatus());
+                + order.getTotalPrice().amount() + "원, " + order.getStatus().label());
         for (OrderLine orderLine : order.getOrderLines()) {
             System.out.println("주문 항목: " + orderLine.productName() + " " + orderLine.quantity() + "개, "
                     + orderLine.amount().amount() + "원");
         }
 
         orderService.pay(order);
-        System.out.println("결제: " + order.getStatus());
+        System.out.println("결제: " + order.getStatus().label());
 
         orderService.ship(order);
-        System.out.println("배송: " + order.getStatus());
+        System.out.println("배송: " + order.getStatus().label());
 
         try {
             orderService.cancel(order);
-            System.out.println("취소: " + order.getStatus());
+            System.out.println("취소: " + order.getStatus().label());
         } catch (IllegalStateException e) {
             System.out.println("취소: " + e.getMessage());
         }
@@ -37,7 +37,7 @@ public class Main {
                 "부산시 해운대구");
         try {
             order2.ship();
-            System.out.println("결제 없이 배송: " + order2.getStatus());
+            System.out.println("결제 없이 배송: " + order2.getStatus().label());
         } catch (IllegalStateException e) {
             System.out.println("결제 없이 배송: " + e.getMessage());
         }
@@ -52,7 +52,7 @@ public class Main {
 
         try {
             Order order4 = orderService.placeOrder("최지우", List.of(new OrderLine("자바 책", new Money(15000), 1)), "");
-            System.out.println("빈 주소 주문: " + order4.getStatus());
+            System.out.println("빈 주소 주문: " + order4.getStatus().label());
         } catch (IllegalArgumentException e) {
             System.out.println("빈 주소 주문: " + e.getMessage());
         }
@@ -74,7 +74,7 @@ public class Main {
 
         try {
             Order order6 = orderService.placeOrder("강민준", List.of(), "인천시 연수구");
-            System.out.println("빈 주문: " + order6.getStatus());
+            System.out.println("빈 주문: " + order6.getStatus().label());
         } catch (IllegalArgumentException e) {
             System.out.println("빈 주문: " + e.getMessage());
         }
diff --git a/ddd/src/ddd/Order.java b/ddd/src/ddd/Order.java
index 12cf1f9..eebb426 100644
--- a/ddd/src/ddd/Order.java
+++ b/ddd/src/ddd/Order.java
@@ -10,7 +10,7 @@ public class Order {
     private List<OrderLine> orderLines;
     private Address address;
     private Money totalPrice;
-    private String status;
+    private OrderStatus status;
 
     public Order(long id, String customerName, List<OrderLine> orderLines, Address address) {
         if (orderLines.isEmpty()) {
@@ -24,7 +24,7 @@ public class Order {
         for (OrderLine orderLine : orderLines) {
             totalPrice = totalPrice.plus(orderLine.amount());
         }
-        this.status = "주문 접수";
+        this.status = OrderStatus.ORDERED;
     }
 
     public long getId() {
@@ -47,12 +47,12 @@ public class Order {
         return totalPrice;
     }
 
-    public String getStatus() {
+    public OrderStatus getStatus() {
         return status;
     }
 
     public void addOrderLine(OrderLine orderLine) {
-        if (!status.equals("주문 접수")) {
+        if (status != OrderStatus.ORDERED) {
             throw new IllegalStateException("주문 접수인 주문에만 항목을 더할 수 있습니다");
         }
         orderLines.add(orderLine);
@@ -60,21 +60,21 @@ public class Order {
     }
 
     public void pay() {
-        status = "결제 완료";
+        status = OrderStatus.PAID;
     }
 
     public void ship() {
-        if (!status.equals("결제 완료")) {
+        if (status != OrderStatus.PAID) {
             throw new IllegalStateException("결제하지 않은 주문은 배송할 수 없습니다");
         }
-        status = "배송 중";
+        status = OrderStatus.SHIPPING;
     }
 
     public void cancel() {
-        if (status.equals("배송 중")) {
+        if (status == OrderStatus.SHIPPING) {
             throw new IllegalStateException("배송 중인 주문은 취소할 수 없습니다");
         }
-        status = "취소";
+        status = OrderStatus.CANCELED;
     }
 
     @Override
```

## T2 상태 전이 규칙을 주문 상태에 모으기
커밋: aa4242e

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/Order.java | 고침 |
| ddd/src/ddd/OrderStatus.java | 고침 |

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 794771f..05590d2 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -86,5 +86,12 @@ public class Main {
         } catch (IllegalArgumentException e) {
             System.out.println("0개 항목 주문: " + e.getMessage());
         }
+
+        try {
+            orderService.pay(order5);
+            System.out.println("두 번 결제: " + order5.getStatus().label());
+        } catch (IllegalStateException e) {
+            System.out.println("두 번 결제: " + e.getMessage());
+        }
     }
 }
diff --git a/ddd/src/ddd/Order.java b/ddd/src/ddd/Order.java
index eebb426..1a234d3 100644
--- a/ddd/src/ddd/Order.java
+++ b/ddd/src/ddd/Order.java
@@ -60,19 +60,22 @@ public class Order {
     }
 
     public void pay() {
+        if (!status.canChangeTo(OrderStatus.PAID)) {
+            throw new IllegalStateException(status.label() + "인 주문은 결제할 수 없습니다");
+        }
         status = OrderStatus.PAID;
     }
 
     public void ship() {
-        if (status != OrderStatus.PAID) {
-            throw new IllegalStateException("결제하지 않은 주문은 배송할 수 없습니다");
+        if (!status.canChangeTo(OrderStatus.SHIPPING)) {
+            throw new IllegalStateException(status.label() + "인 주문은 배송할 수 없습니다");
         }
         status = OrderStatus.SHIPPING;
     }
 
     public void cancel() {
-        if (status == OrderStatus.SHIPPING) {
-            throw new IllegalStateException("배송 중인 주문은 취소할 수 없습니다");
+        if (!status.canChangeTo(OrderStatus.CANCELED)) {
+            throw new IllegalStateException(status.label() + "인 주문은 취소할 수 없습니다");
         }
         status = OrderStatus.CANCELED;
     }
diff --git a/ddd/src/ddd/OrderStatus.java b/ddd/src/ddd/OrderStatus.java
index 56e8c99..85e7b08 100644
--- a/ddd/src/ddd/OrderStatus.java
+++ b/ddd/src/ddd/OrderStatus.java
@@ -15,4 +15,12 @@ public enum OrderStatus {
     public String label() {
         return label;
     }
+
+    public boolean canChangeTo(OrderStatus next) {
+        return switch (this) {
+            case ORDERED -> next == PAID || next == CANCELED;
+            case PAID -> next == SHIPPING || next == CANCELED;
+            case SHIPPING, CANCELED -> false;
+        };
+    }
 }
```

## T3 배송 완료하기
커밋: 0a70e42

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/Order.java | 고침 |
| ddd/src/ddd/OrderService.java | 고침 |
| ddd/src/ddd/OrderStatus.java | 고침 |

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index 0824c2d..ef06780 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -15,9 +15,11 @@
 | 주문 항목 더하기 | addOrderLine | 주문 접수인 주문에 주문 항목을 더하고 주문 금액도 함께 바꾼다 |
 | 결제하기 | pay | 주문 금액을 치러 결제 완료가 된다 |
 | 배송하기 | ship | 결제 완료인 주문을 보내 배송 중이 된다 |
+| 배송 완료하기 | deliver | 배송 중인 주문이 고객에게 닿아 배송 완료가 된다 |
 | 주문 취소하기 | cancel | 배송 전 주문을 취소 상태로 둔다. 지우지 않는다 |
 | 주문 상태 | OrderStatus | 주문이 지금 어디까지 왔는지. 정해진 상태 중 하나다 |
 | 주문 접수 | ORDERED | 주문하기를 마친 상태 |
 | 결제 완료 | PAID | 결제하기를 마친 상태 |
 | 배송 중 | SHIPPING | 배송하기를 마친 상태 |
+| 배송 완료 | DELIVERED | 배송 완료하기를 마친 상태. 더 바뀌지 않는다 |
 | 취소 | CANCELED | 주문 취소하기를 마친 상태 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 05590d2..f7e4ea7 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -93,5 +93,8 @@ public class Main {
         } catch (IllegalStateException e) {
             System.out.println("두 번 결제: " + e.getMessage());
         }
+
+        orderService.deliver(order);
+        System.out.println("배송 완료: " + order.getStatus().label());
     }
 }
diff --git a/ddd/src/ddd/Order.java b/ddd/src/ddd/Order.java
index 1a234d3..586a756 100644
--- a/ddd/src/ddd/Order.java
+++ b/ddd/src/ddd/Order.java
@@ -73,6 +73,13 @@ public class Order {
         status = OrderStatus.SHIPPING;
     }
 
+    public void deliver() {
+        if (!status.canChangeTo(OrderStatus.DELIVERED)) {
+            throw new IllegalStateException(status.label() + "인 주문은 배송 완료할 수 없습니다");
+        }
+        status = OrderStatus.DELIVERED;
+    }
+
     public void cancel() {
         if (!status.canChangeTo(OrderStatus.CANCELED)) {
             throw new IllegalStateException(status.label() + "인 주문은 취소할 수 없습니다");
diff --git a/ddd/src/ddd/OrderService.java b/ddd/src/ddd/OrderService.java
index 57dc0f4..5a0dcb7 100644
--- a/ddd/src/ddd/OrderService.java
+++ b/ddd/src/ddd/OrderService.java
@@ -18,6 +18,10 @@ public class OrderService {
         order.ship();
     }
 
+    public void deliver(Order order) {
+        order.deliver();
+    }
+
     public void cancel(Order order) {
         order.cancel();
     }
diff --git a/ddd/src/ddd/OrderStatus.java b/ddd/src/ddd/OrderStatus.java
index 85e7b08..3c6629d 100644
--- a/ddd/src/ddd/OrderStatus.java
+++ b/ddd/src/ddd/OrderStatus.java
@@ -4,6 +4,7 @@ public enum OrderStatus {
     ORDERED("주문 접수"),
     PAID("결제 완료"),
     SHIPPING("배송 중"),
+    DELIVERED("배송 완료"),
     CANCELED("취소");
 
     private final String label;
@@ -20,7 +21,8 @@ public enum OrderStatus {
         return switch (this) {
             case ORDERED -> next == PAID || next == CANCELED;
             case PAID -> next == SHIPPING || next == CANCELED;
-            case SHIPPING, CANCELED -> false;
+            case SHIPPING -> next == DELIVERED;
+            case DELIVERED, CANCELED -> false;
         };
     }
 }
```
