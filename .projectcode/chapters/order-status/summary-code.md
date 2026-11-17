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

## T4 주문 메서드에서 지금 상태 바로 확인하기
커밋: a312669. 이 장 시점 코드에 얹은 patch는 fixes/a312669.patch

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/Money.java | 지움 |
| ddd/src/ddd/Order.java | 고침 |
| ddd/src/ddd/OrderItem.java | 새로 |
| ddd/src/ddd/OrderLine.java | 지움 |
| ddd/src/ddd/OrderService.java | 고침 |
| ddd/src/ddd/OrderStatus.java | 고침 |

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index ef06780..7765812 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -3,16 +3,15 @@
 | 업무 말 | 코드 이름 | 뜻 |
 |---|---|---|
 | 주문 | Order | 고객이 상품을 사겠다고 한 요청 |
-| 주문 항목 | OrderLine | 주문한 상품 하나의 상품 이름, 가격, 수량 |
+| 주문 항목 | OrderItem | 주문한 상품 하나의 상품 이름, 가격, 수량. 가격은 0원 이상, 수량은 1개 이상이다 |
 | 항목 금액 | amount | 주문 항목의 가격 곱하기 수량 |
 | 주문 번호 | id | 주문마다 다른 번호. 번호가 같으면 같은 주문이다 |
 | 주문자 | customerName | 주문한 고객의 이름 |
 | 배송 주소 | address | 상품을 받을 곳 |
 | 주소 | Address | 비어 있지 않은 배송 주소. 글자가 같으면 같은 주소다 |
 | 주문 금액 | totalPrice | 주문 항목마다 항목 금액을 더한 합 |
-| 돈 | Money | 0원 이상인 금액. 금액이 같으면 같은 돈이다 |
 | 주문하기 | placeOrder | 주문자, 주문 항목, 배송 주소로 주문을 접수한다 |
-| 주문 항목 더하기 | addOrderLine | 주문 접수인 주문에 주문 항목을 더하고 주문 금액도 함께 바꾼다 |
+| 주문 항목 더하기 | addItem | 주문 접수인 주문에 주문 항목을 더하고 주문 금액도 함께 바꾼다 |
 | 결제하기 | pay | 주문 금액을 치러 결제 완료가 된다 |
 | 배송하기 | ship | 결제 완료인 주문을 보내 배송 중이 된다 |
 | 배송 완료하기 | deliver | 배송 중인 주문이 고객에게 닿아 배송 완료가 된다 |
```

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index f7e4ea7..5890f34 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -5,96 +5,50 @@ import java.util.List;
 public class Main {
 
     public static void main(String[] args) {
-        System.out.println("온라인 주문 프로그램을 시작합니다");
+        System.out.println("===== 주문 상태 =====");
 
         OrderService orderService = new OrderService();
+        Order order = orderService.placeOrder("김철수", List.of(new OrderItem("자바 책", 15000, 2)), "서울시 강남구");
 
-        List<OrderLine> orderLines = List.of(
-                new OrderLine("자바 책", new Money(15000), 2),
-                new OrderLine("볼펜", new Money(1000), 1));
-        Order order = orderService.placeOrder("김철수", orderLines, "서울시 강남구");
-        System.out.println("주문: " + order.getId() + "번, " + order.getCustomerName() + ", "
-                + order.getTotalPrice().amount() + "원, " + order.getStatus().label());
-        for (OrderLine orderLine : order.getOrderLines()) {
-            System.out.println("주문 항목: " + orderLine.productName() + " " + orderLine.quantity() + "개, "
-                    + orderLine.amount().amount() + "원");
-        }
-
+        System.out.println();
+        System.out.println("[결제하고 배송하기]");
+        String statusBefore = order.getStatus().label();
         orderService.pay(order);
-        System.out.println("결제: " + order.getStatus().label());
-
+        System.out.println("주문 상태: " + statusBefore + " -> " + order.getStatus().label());
+        statusBefore = order.getStatus().label();
         orderService.ship(order);
-        System.out.println("배송: " + order.getStatus().label());
+        System.out.println("주문 상태: " + statusBefore + " -> " + order.getStatus().label());
 
+        System.out.println();
+        System.out.println("[할 수 없는 일 시키기]");
         try {
             orderService.cancel(order);
-            System.out.println("취소: " + order.getStatus().label());
+            System.out.println("배송 중 취소: " + order.getStatus().label());
         } catch (IllegalStateException e) {
-            System.out.println("취소: " + e.getMessage());
+            System.out.println("배송 중 취소: " + e.getMessage());
         }
-
-        Order order2 = orderService.placeOrder("이영희", List.of(new OrderLine("자바 책", new Money(15000), 1)),
-                "부산시 해운대구");
+        Order order2 = orderService.placeOrder("이영희", List.of(new OrderItem("자바 책", 15000, 1)), "부산시 해운대구");
         try {
-            order2.ship();
+            orderService.ship(order2);
             System.out.println("결제 없이 배송: " + order2.getStatus().label());
         } catch (IllegalStateException e) {
             System.out.println("결제 없이 배송: " + e.getMessage());
         }
 
+        System.out.println();
+        System.out.println("[두 번 결제하기]");
+        orderService.pay(order2);
         try {
-            Order order3 = orderService.placeOrder("박민수", List.of(new OrderLine("자바 책", new Money(-15000), 2)),
-                    "대구시 수성구");
-            System.out.println("음수 금액 주문: " + order3.getTotalPrice().amount() + "원");
-        } catch (IllegalArgumentException e) {
-            System.out.println("음수 금액 주문: " + e.getMessage());
-        }
-
-        try {
-            Order order4 = orderService.placeOrder("최지우", List.of(new OrderLine("자바 책", new Money(15000), 1)), "");
-            System.out.println("빈 주소 주문: " + order4.getStatus().label());
-        } catch (IllegalArgumentException e) {
-            System.out.println("빈 주소 주문: " + e.getMessage());
-        }
-
-        Order order5 = orderService.placeOrder("정하늘", List.of(
-                new OrderLine("자바 책", new Money(15000), 2),
-                new OrderLine("볼펜", new Money(1000), 1)), "광주시 북구");
-        order5.addOrderLine(new OrderLine("공책", new Money(2000), 5));
-        System.out.println("항목 더하기: 항목 " + order5.getOrderLines().size() + "개, 주문 금액 "
-                + order5.getTotalPrice().amount() + "원");
-
-        orderService.pay(order5);
-        try {
-            order5.addOrderLine(new OrderLine("공책", new Money(2000), 1));
-            System.out.println("결제 뒤 항목 더하기: 항목 " + order5.getOrderLines().size() + "개");
-        } catch (IllegalStateException e) {
-            System.out.println("결제 뒤 항목 더하기: " + e.getMessage());
-        }
-
-        try {
-            Order order6 = orderService.placeOrder("강민준", List.of(), "인천시 연수구");
-            System.out.println("빈 주문: " + order6.getStatus().label());
-        } catch (IllegalArgumentException e) {
-            System.out.println("빈 주문: " + e.getMessage());
-        }
-
-        try {
-            Order order7 = orderService.placeOrder("윤서연", List.of(new OrderLine("자바 책", new Money(15000), 0)),
-                    "대전시 유성구");
-            System.out.println("0개 항목 주문: " + order7.getTotalPrice().amount() + "원");
-        } catch (IllegalArgumentException e) {
-            System.out.println("0개 항목 주문: " + e.getMessage());
-        }
-
-        try {
-            orderService.pay(order5);
-            System.out.println("두 번 결제: " + order5.getStatus().label());
+            orderService.pay(order2);
+            System.out.println("두 번 결제: " + order2.getStatus().label());
         } catch (IllegalStateException e) {
             System.out.println("두 번 결제: " + e.getMessage());
         }
 
+        System.out.println();
+        System.out.println("[배송 완료하기]");
+        statusBefore = order.getStatus().label();
         orderService.deliver(order);
-        System.out.println("배송 완료: " + order.getStatus().label());
+        System.out.println("주문 상태: " + statusBefore + " -> " + order.getStatus().label());
     }
 }
```

```diff
diff --git a/ddd/src/ddd/Order.java b/ddd/src/ddd/Order.java
index 586a756..0158a33 100644
--- a/ddd/src/ddd/Order.java
+++ b/ddd/src/ddd/Order.java
@@ -7,22 +7,22 @@ public class Order {
 
     private long id;
     private String customerName;
-    private List<OrderLine> orderLines;
+    private List<OrderItem> items;
     private Address address;
-    private Money totalPrice;
+    private int totalPrice;
     private OrderStatus status;
 
-    public Order(long id, String customerName, List<OrderLine> orderLines, Address address) {
-        if (orderLines.isEmpty()) {
+    public Order(long id, String customerName, List<OrderItem> items, Address address) {
+        if (items.isEmpty()) {
             throw new IllegalArgumentException("주문 항목이 하나 이상 있어야 합니다");
         }
         this.id = id;
         this.customerName = customerName;
-        this.orderLines = new ArrayList<>(orderLines);
+        this.items = new ArrayList<>(items);
         this.address = address;
-        this.totalPrice = new Money(0);
-        for (OrderLine orderLine : orderLines) {
-            totalPrice = totalPrice.plus(orderLine.amount());
+        this.totalPrice = 0;
+        for (OrderItem item : items) {
+            totalPrice = totalPrice + item.getAmount();
         }
         this.status = OrderStatus.ORDERED;
     }
@@ -35,15 +35,15 @@ public class Order {
         return customerName;
     }
 
-    public List<OrderLine> getOrderLines() {
-        return List.copyOf(orderLines);
+    public List<OrderItem> getItems() {
+        return List.copyOf(items);
     }
 
     public Address getAddress() {
         return address;
     }
 
-    public Money getTotalPrice() {
+    public int getTotalPrice() {
         return totalPrice;
     }
 
@@ -51,37 +51,37 @@ public class Order {
         return status;
     }
 
-    public void addOrderLine(OrderLine orderLine) {
+    public void addItem(OrderItem item) {
         if (status != OrderStatus.ORDERED) {
             throw new IllegalStateException("주문 접수인 주문에만 항목을 더할 수 있습니다");
         }
-        orderLines.add(orderLine);
-        totalPrice = totalPrice.plus(orderLine.amount());
+        items.add(item);
+        totalPrice = totalPrice + item.getAmount();
     }
 
     public void pay() {
-        if (!status.canChangeTo(OrderStatus.PAID)) {
+        if (status != OrderStatus.ORDERED) {
             throw new IllegalStateException(status.label() + "인 주문은 결제할 수 없습니다");
         }
         status = OrderStatus.PAID;
     }
 
     public void ship() {
-        if (!status.canChangeTo(OrderStatus.SHIPPING)) {
+        if (status != OrderStatus.PAID) {
             throw new IllegalStateException(status.label() + "인 주문은 배송할 수 없습니다");
         }
         status = OrderStatus.SHIPPING;
     }
 
     public void deliver() {
-        if (!status.canChangeTo(OrderStatus.DELIVERED)) {
+        if (status != OrderStatus.SHIPPING) {
             throw new IllegalStateException(status.label() + "인 주문은 배송 완료할 수 없습니다");
         }
         status = OrderStatus.DELIVERED;
     }
 
     public void cancel() {
-        if (!status.canChangeTo(OrderStatus.CANCELED)) {
+        if (status != OrderStatus.ORDERED && status != OrderStatus.PAID) {
             throw new IllegalStateException(status.label() + "인 주문은 취소할 수 없습니다");
         }
         status = OrderStatus.CANCELED;
```

```java
// ddd/src/ddd/OrderItem.java
package ddd;

public class OrderItem {

    private final String productName;
    private final int price;
    private final int quantity;

    public OrderItem(String productName, int price, int quantity) {
        if (price < 0) {
            throw new IllegalArgumentException("가격은 0원 이상이어야 합니다");
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다");
        }
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getAmount() {
        return price * quantity;
    }
}
```

```diff
diff --git a/ddd/src/ddd/OrderService.java b/ddd/src/ddd/OrderService.java
index 5a0dcb7..8befb06 100644
--- a/ddd/src/ddd/OrderService.java
+++ b/ddd/src/ddd/OrderService.java
@@ -6,8 +6,8 @@ public class OrderService {
 
     private long nextId = 1;
 
-    public Order placeOrder(String customerName, List<OrderLine> orderLines, String address) {
-        return new Order(nextId++, customerName, orderLines, new Address(address));
+    public Order placeOrder(String customerName, List<OrderItem> items, String address) {
+        return new Order(nextId++, customerName, items, new Address(address));
     }
 
     public void pay(Order order) {
```

```diff
diff --git a/ddd/src/ddd/OrderStatus.java b/ddd/src/ddd/OrderStatus.java
index 3c6629d..68c0b7b 100644
--- a/ddd/src/ddd/OrderStatus.java
+++ b/ddd/src/ddd/OrderStatus.java
@@ -16,13 +16,4 @@ public enum OrderStatus {
     public String label() {
         return label;
     }
-
-    public boolean canChangeTo(OrderStatus next) {
-        return switch (this) {
-            case ORDERED -> next == PAID || next == CANCELED;
-            case PAID -> next == SHIPPING || next == CANCELED;
-            case SHIPPING -> next == DELIVERED;
-            case DELIVERED, CANCELED -> false;
-        };
-    }
 }
```
