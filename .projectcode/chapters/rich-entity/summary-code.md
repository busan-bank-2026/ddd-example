# rich-entity 코드 요약

## T1 결제 없이 배송해 보기
커밋: 11acad4

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/Main.java | 고침 |

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 1a70226..f43d766 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -23,5 +23,9 @@ public class Main {
         } catch (IllegalStateException e) {
             System.out.println("취소: " + e.getMessage());
         }
+
+        Order order2 = orderService.placeOrder("이영희", "자바 책", 15000, 1, "부산시 해운대구");
+        order2.setStatus("배송 중");
+        System.out.println("결제 없이 배송: " + order2.getStatus());
     }
 }
```

## T2 주문 번호를 받는 생성자 만들기
커밋: d91ddcf

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/Order.java | 고침 |
| ddd/src/ddd/OrderService.java | 고침 |

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index dd84f13..d4ad2af 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -3,6 +3,7 @@
 | 업무 말 | 코드 이름 | 뜻 |
 |---|---|---|
 | 주문 | Order | 고객이 상품을 사겠다고 한 요청 |
+| 주문 번호 | id | 주문마다 다른 번호. 번호가 같으면 같은 주문이다 |
 | 주문자 | customerName | 주문한 고객의 이름 |
 | 배송 주소 | address | 상품을 받을 곳 |
 | 주문 금액 | totalPrice | 가격 곱하기 수량 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index f43d766..05cd258 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -8,8 +8,9 @@ public class Main {
         OrderService orderService = new OrderService();
 
         Order order = orderService.placeOrder("김철수", "자바 책", 15000, 2, "서울시 강남구");
-        System.out.println("주문: " + order.getCustomerName() + ", " + order.getProductName() + " "
-                + order.getQuantity() + "개, " + order.getTotalPrice() + "원, " + order.getStatus());
+        System.out.println("주문: " + order.getId() + "번, " + order.getCustomerName() + ", "
+                + order.getProductName() + " " + order.getQuantity() + "개, " + order.getTotalPrice() + "원, "
+                + order.getStatus());
 
         orderService.pay(order);
         System.out.println("결제: " + order.getStatus());
diff --git a/ddd/src/ddd/Order.java b/ddd/src/ddd/Order.java
index 595b49c..0cf890c 100644
--- a/ddd/src/ddd/Order.java
+++ b/ddd/src/ddd/Order.java
@@ -2,6 +2,7 @@ package ddd;
 
 public class Order {
 
+    private long id;
     private String customerName;
     private String productName;
     private int price;
@@ -10,54 +11,45 @@ public class Order {
     private int totalPrice;
     private String status;
 
-    public String getCustomerName() {
-        return customerName;
+    public Order(long id, String customerName, String productName, int price, int quantity, String address) {
+        this.id = id;
+        this.customerName = customerName;
+        this.productName = productName;
+        this.price = price;
+        this.quantity = quantity;
+        this.address = address;
+        this.totalPrice = price * quantity;
+        this.status = "주문 접수";
     }
 
-    public void setCustomerName(String customerName) {
-        this.customerName = customerName;
+    public long getId() {
+        return id;
     }
 
-    public String getProductName() {
-        return productName;
+    public String getCustomerName() {
+        return customerName;
     }
 
-    public void setProductName(String productName) {
-        this.productName = productName;
+    public String getProductName() {
+        return productName;
     }
 
     public int getPrice() {
         return price;
     }
 
-    public void setPrice(int price) {
-        this.price = price;
-    }
-
     public int getQuantity() {
         return quantity;
     }
 
-    public void setQuantity(int quantity) {
-        this.quantity = quantity;
-    }
-
     public String getAddress() {
         return address;
     }
 
-    public void setAddress(String address) {
-        this.address = address;
-    }
-
     public int getTotalPrice() {
         return totalPrice;
     }
 
-    public void setTotalPrice(int totalPrice) {
-        this.totalPrice = totalPrice;
-    }
-
     public String getStatus() {
         return status;
     }
@@ -65,4 +57,17 @@ public class Order {
     public void setStatus(String status) {
         this.status = status;
     }
+
+    @Override
+    public boolean equals(Object o) {
+        if (!(o instanceof Order other)) {
+            return false;
+        }
+        return id == other.id;
+    }
+
+    @Override
+    public int hashCode() {
+        return Long.hashCode(id);
+    }
 }
diff --git a/ddd/src/ddd/OrderService.java b/ddd/src/ddd/OrderService.java
index 292410f..5ffbc32 100644
--- a/ddd/src/ddd/OrderService.java
+++ b/ddd/src/ddd/OrderService.java
@@ -2,16 +2,10 @@ package ddd;
 
 public class OrderService {
 
+    private long nextId = 1;
+
     public Order placeOrder(String customerName, String productName, int price, int quantity, String address) {
-        Order order = new Order();
-        order.setCustomerName(customerName);
-        order.setProductName(productName);
-        order.setPrice(price);
-        order.setQuantity(quantity);
-        order.setAddress(address);
-        order.setTotalPrice(price * quantity);
-        order.setStatus("주문 접수");
-        return order;
+        return new Order(nextId++, customerName, productName, price, quantity, address);
     }
 
     public void pay(Order order) {
```

## T3 결제, 배송, 취소를 주문에게 시키기
커밋: a043e4d

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/Order.java | 고침 |
| ddd/src/ddd/OrderService.java | 고침 |

```diff
diff --git a/ddd/src/ddd/Order.java b/ddd/src/ddd/Order.java
index 0cf890c..cfb016a 100644
--- a/ddd/src/ddd/Order.java
+++ b/ddd/src/ddd/Order.java
@@ -58,6 +58,24 @@ public class Order {
         this.status = status;
     }
 
+    public void pay() {
+        status = "결제 완료";
+    }
+
+    public void ship() {
+        if (!status.equals("결제 완료")) {
+            throw new IllegalStateException("결제하지 않은 주문은 배송할 수 없습니다");
+        }
+        status = "배송 중";
+    }
+
+    public void cancel() {
+        if (status.equals("배송 중")) {
+            throw new IllegalStateException("배송 중인 주문은 취소할 수 없습니다");
+        }
+        status = "취소";
+    }
+
     @Override
     public boolean equals(Object o) {
         if (!(o instanceof Order other)) {
diff --git a/ddd/src/ddd/OrderService.java b/ddd/src/ddd/OrderService.java
index 5ffbc32..c0c7ec5 100644
--- a/ddd/src/ddd/OrderService.java
+++ b/ddd/src/ddd/OrderService.java
@@ -9,20 +9,14 @@ public class OrderService {
     }
 
     public void pay(Order order) {
-        order.setStatus("결제 완료");
+        order.pay();
     }
 
     public void ship(Order order) {
-        if (!order.getStatus().equals("결제 완료")) {
-            throw new IllegalStateException("결제하지 않은 주문은 배송할 수 없습니다");
-        }
-        order.setStatus("배송 중");
+        order.ship();
     }
 
     public void cancel(Order order) {
-        if (order.getStatus().equals("배송 중")) {
-            throw new IllegalStateException("배송 중인 주문은 취소할 수 없습니다");
-        }
-        order.setStatus("취소");
+        order.cancel();
     }
 }
```

## T4 상태를 바꾸는 길 막기
커밋: 234c32c

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/Order.java | 고침 |

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 05cd258..a71c720 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -26,7 +26,11 @@ public class Main {
         }
 
         Order order2 = orderService.placeOrder("이영희", "자바 책", 15000, 1, "부산시 해운대구");
-        order2.setStatus("배송 중");
-        System.out.println("결제 없이 배송: " + order2.getStatus());
+        try {
+            order2.ship();
+            System.out.println("결제 없이 배송: " + order2.getStatus());
+        } catch (IllegalStateException e) {
+            System.out.println("결제 없이 배송: " + e.getMessage());
+        }
     }
 }
diff --git a/ddd/src/ddd/Order.java b/ddd/src/ddd/Order.java
index cfb016a..c1704d5 100644
--- a/ddd/src/ddd/Order.java
+++ b/ddd/src/ddd/Order.java
@@ -54,10 +54,6 @@ public class Order {
         return status;
     }
 
-    public void setStatus(String status) {
-        this.status = status;
-    }
-
     public void pay() {
         status = "결제 완료";
     }
```
