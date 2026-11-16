# order-aggregate 코드 요약

## T1 주문 항목 여러 개로 주문하기
커밋: 217a0fb

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/Order.java | 고침 |
| ddd/src/ddd/OrderLine.java | 새로 |
| ddd/src/ddd/OrderService.java | 고침 |

```java
// ddd/src/ddd/OrderLine.java
package ddd;

public record OrderLine(String productName, Money price, int quantity) {

    public Money amount() {
        return price.times(quantity);
    }
}
```

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index bd9d0c7..ea1f487 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -3,13 +3,15 @@
 | 업무 말 | 코드 이름 | 뜻 |
 |---|---|---|
 | 주문 | Order | 고객이 상품을 사겠다고 한 요청 |
+| 주문 항목 | OrderLine | 주문한 상품 하나의 상품 이름, 가격, 수량 |
+| 항목 금액 | amount | 주문 항목의 가격 곱하기 수량 |
 | 주문 번호 | id | 주문마다 다른 번호. 번호가 같으면 같은 주문이다 |
 | 주문자 | customerName | 주문한 고객의 이름 |
 | 배송 주소 | address | 상품을 받을 곳 |
 | 주소 | Address | 비어 있지 않은 배송 주소. 글자가 같으면 같은 주소다 |
-| 주문 금액 | totalPrice | 가격 곱하기 수량 |
+| 주문 금액 | totalPrice | 주문 항목마다 항목 금액을 더한 합 |
 | 돈 | Money | 0원 이상인 금액. 금액이 같으면 같은 돈이다 |
-| 주문하기 | placeOrder | 주문자, 상품, 수량, 배송 주소로 주문을 접수한다 |
+| 주문하기 | placeOrder | 주문자, 주문 항목, 배송 주소로 주문을 접수한다 |
 | 결제하기 | pay | 주문 금액을 치러 결제 완료가 된다 |
 | 배송하기 | ship | 결제 완료인 주문을 보내 배송 중이 된다 |
 | 주문 취소하기 | cancel | 배송 전 주문을 취소 상태로 둔다. 지우지 않는다 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 31984cd..6741a24 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -1,5 +1,7 @@
 package ddd;
 
+import java.util.List;
+
 public class Main {
 
     public static void main(String[] args) {
@@ -7,10 +9,16 @@ public class Main {
 
         OrderService orderService = new OrderService();
 
-        Order order = orderService.placeOrder("김철수", "자바 책", 15000, 2, "서울시 강남구");
+        List<OrderLine> orderLines = List.of(
+                new OrderLine("자바 책", new Money(15000), 2),
+                new OrderLine("볼펜", new Money(1000), 1));
+        Order order = orderService.placeOrder("김철수", orderLines, "서울시 강남구");
         System.out.println("주문: " + order.getId() + "번, " + order.getCustomerName() + ", "
-                + order.getProductName() + " " + order.getQuantity() + "개, " + order.getTotalPrice().amount()
-                + "원, " + order.getStatus());
+                + order.getTotalPrice().amount() + "원, " + order.getStatus());
+        for (OrderLine orderLine : order.getOrderLines()) {
+            System.out.println("주문 항목: " + orderLine.productName() + " " + orderLine.quantity() + "개, "
+                    + orderLine.amount().amount() + "원");
+        }
 
         orderService.pay(order);
         System.out.println("결제: " + order.getStatus());
@@ -25,7 +33,8 @@ public class Main {
             System.out.println("취소: " + e.getMessage());
         }
 
-        Order order2 = orderService.placeOrder("이영희", "자바 책", 15000, 1, "부산시 해운대구");
+        Order order2 = orderService.placeOrder("이영희", List.of(new OrderLine("자바 책", new Money(15000), 1)),
+                "부산시 해운대구");
         try {
             order2.ship();
             System.out.println("결제 없이 배송: " + order2.getStatus());
@@ -34,17 +43,25 @@ public class Main {
         }
 
         try {
-            Order order3 = orderService.placeOrder("박민수", "자바 책", -15000, 2, "대구시 수성구");
+            Order order3 = orderService.placeOrder("박민수", List.of(new OrderLine("자바 책", new Money(-15000), 2)),
+                    "대구시 수성구");
             System.out.println("음수 금액 주문: " + order3.getTotalPrice().amount() + "원");
         } catch (IllegalArgumentException e) {
             System.out.println("음수 금액 주문: " + e.getMessage());
         }
 
         try {
-            Order order4 = orderService.placeOrder("최지우", "자바 책", 15000, 1, "");
+            Order order4 = orderService.placeOrder("최지우", List.of(new OrderLine("자바 책", new Money(15000), 1)), "");
             System.out.println("빈 주소 주문: " + order4.getStatus());
         } catch (IllegalArgumentException e) {
             System.out.println("빈 주소 주문: " + e.getMessage());
         }
+
+        Order order5 = orderService.placeOrder("정하늘", List.of(
+                new OrderLine("자바 책", new Money(15000), 2),
+                new OrderLine("볼펜", new Money(1000), 1)), "광주시 북구");
+        order5.getOrderLines().add(new OrderLine("공책", new Money(2000), 5));
+        System.out.println("항목 더하기: 항목 " + order5.getOrderLines().size() + "개, 주문 금액 "
+                + order5.getTotalPrice().amount() + "원");
     }
 }
diff --git a/ddd/src/ddd/Order.java b/ddd/src/ddd/Order.java
index 76d6fd8..d0edb32 100644
--- a/ddd/src/ddd/Order.java
+++ b/ddd/src/ddd/Order.java
@@ -1,24 +1,26 @@
 package ddd;
 
+import java.util.ArrayList;
+import java.util.List;
+
 public class Order {
 
     private long id;
     private String customerName;
-    private String productName;
-    private Money price;
-    private int quantity;
+    private List<OrderLine> orderLines;
     private Address address;
     private Money totalPrice;
     private String status;
 
-    public Order(long id, String customerName, String productName, Money price, int quantity, Address address) {
+    public Order(long id, String customerName, List<OrderLine> orderLines, Address address) {
         this.id = id;
         this.customerName = customerName;
-        this.productName = productName;
-        this.price = price;
-        this.quantity = quantity;
+        this.orderLines = new ArrayList<>(orderLines);
         this.address = address;
-        this.totalPrice = price.times(quantity);
+        this.totalPrice = new Money(0);
+        for (OrderLine orderLine : orderLines) {
+            totalPrice = totalPrice.plus(orderLine.amount());
+        }
         this.status = "주문 접수";
     }
 
@@ -30,16 +32,8 @@ public class Order {
         return customerName;
     }
 
-    public String getProductName() {
-        return productName;
-    }
-
-    public Money getPrice() {
-        return price;
-    }
-
-    public int getQuantity() {
-        return quantity;
+    public List<OrderLine> getOrderLines() {
+        return orderLines;
     }
 
     public Address getAddress() {
diff --git a/ddd/src/ddd/OrderService.java b/ddd/src/ddd/OrderService.java
index 1d042d6..57dc0f4 100644
--- a/ddd/src/ddd/OrderService.java
+++ b/ddd/src/ddd/OrderService.java
@@ -1,11 +1,13 @@
 package ddd;
 
+import java.util.List;
+
 public class OrderService {
 
     private long nextId = 1;
 
-    public Order placeOrder(String customerName, String productName, int price, int quantity, String address) {
-        return new Order(nextId++, customerName, productName, new Money(price), quantity, new Address(address));
+    public Order placeOrder(String customerName, List<OrderLine> orderLines, String address) {
+        return new Order(nextId++, customerName, orderLines, new Address(address));
     }
 
     public void pay(Order order) {
```

## T2 항목 더하기를 주문에게 시키기
커밋: faa2746

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/Order.java | 고침 |

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index ea1f487..11b5a96 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -12,6 +12,7 @@
 | 주문 금액 | totalPrice | 주문 항목마다 항목 금액을 더한 합 |
 | 돈 | Money | 0원 이상인 금액. 금액이 같으면 같은 돈이다 |
 | 주문하기 | placeOrder | 주문자, 주문 항목, 배송 주소로 주문을 접수한다 |
+| 주문 항목 더하기 | addOrderLine | 주문 접수인 주문에 주문 항목을 더하고 주문 금액도 함께 바꾼다 |
 | 결제하기 | pay | 주문 금액을 치러 결제 완료가 된다 |
 | 배송하기 | ship | 결제 완료인 주문을 보내 배송 중이 된다 |
 | 주문 취소하기 | cancel | 배송 전 주문을 취소 상태로 둔다. 지우지 않는다 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 6741a24..c437d61 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -60,8 +60,16 @@ public class Main {
         Order order5 = orderService.placeOrder("정하늘", List.of(
                 new OrderLine("자바 책", new Money(15000), 2),
                 new OrderLine("볼펜", new Money(1000), 1)), "광주시 북구");
-        order5.getOrderLines().add(new OrderLine("공책", new Money(2000), 5));
+        order5.addOrderLine(new OrderLine("공책", new Money(2000), 5));
         System.out.println("항목 더하기: 항목 " + order5.getOrderLines().size() + "개, 주문 금액 "
                 + order5.getTotalPrice().amount() + "원");
+
+        orderService.pay(order5);
+        try {
+            order5.addOrderLine(new OrderLine("공책", new Money(2000), 1));
+            System.out.println("결제 뒤 항목 더하기: 항목 " + order5.getOrderLines().size() + "개");
+        } catch (IllegalStateException e) {
+            System.out.println("결제 뒤 항목 더하기: " + e.getMessage());
+        }
     }
 }
diff --git a/ddd/src/ddd/Order.java b/ddd/src/ddd/Order.java
index d0edb32..67db28a 100644
--- a/ddd/src/ddd/Order.java
+++ b/ddd/src/ddd/Order.java
@@ -33,7 +33,7 @@ public class Order {
     }
 
     public List<OrderLine> getOrderLines() {
-        return orderLines;
+        return List.copyOf(orderLines);
     }
 
     public Address getAddress() {
@@ -48,6 +48,14 @@ public class Order {
         return status;
     }
 
+    public void addOrderLine(OrderLine orderLine) {
+        if (!status.equals("주문 접수")) {
+            throw new IllegalStateException("주문 접수인 주문에만 항목을 더할 수 있습니다");
+        }
+        orderLines.add(orderLine);
+        totalPrice = totalPrice.plus(orderLine.amount());
+    }
+
     public void pay() {
         status = "결제 완료";
     }
```

## T3 빈 주문과 0개 항목 막기
커밋: f30ef51

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/Order.java | 고침 |
| ddd/src/ddd/OrderLine.java | 고침 |

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index c437d61..96b7124 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -71,5 +71,20 @@ public class Main {
         } catch (IllegalStateException e) {
             System.out.println("결제 뒤 항목 더하기: " + e.getMessage());
         }
+
+        try {
+            Order order6 = orderService.placeOrder("강민준", List.of(), "인천시 연수구");
+            System.out.println("빈 주문: " + order6.getStatus());
+        } catch (IllegalArgumentException e) {
+            System.out.println("빈 주문: " + e.getMessage());
+        }
+
+        try {
+            Order order7 = orderService.placeOrder("윤서연", List.of(new OrderLine("자바 책", new Money(15000), 0)),
+                    "대전시 유성구");
+            System.out.println("0개 항목 주문: " + order7.getTotalPrice().amount() + "원");
+        } catch (IllegalArgumentException e) {
+            System.out.println("0개 항목 주문: " + e.getMessage());
+        }
     }
 }
diff --git a/ddd/src/ddd/Order.java b/ddd/src/ddd/Order.java
index 67db28a..12cf1f9 100644
--- a/ddd/src/ddd/Order.java
+++ b/ddd/src/ddd/Order.java
@@ -13,6 +13,9 @@ public class Order {
     private String status;
 
     public Order(long id, String customerName, List<OrderLine> orderLines, Address address) {
+        if (orderLines.isEmpty()) {
+            throw new IllegalArgumentException("주문 항목이 하나 이상 있어야 합니다");
+        }
         this.id = id;
         this.customerName = customerName;
         this.orderLines = new ArrayList<>(orderLines);
diff --git a/ddd/src/ddd/OrderLine.java b/ddd/src/ddd/OrderLine.java
index c14d488..c07a632 100644
--- a/ddd/src/ddd/OrderLine.java
+++ b/ddd/src/ddd/OrderLine.java
@@ -2,6 +2,12 @@ package ddd;
 
 public record OrderLine(String productName, Money price, int quantity) {
 
+    public OrderLine {
+        if (quantity < 1) {
+            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다");
+        }
+    }
+
     public Money amount() {
         return price.times(quantity);
     }
```
