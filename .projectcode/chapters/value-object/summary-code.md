# value-object 코드 요약

## T1 음수 금액으로 주문해 보기
커밋: 6fd0f16

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/Main.java | 고침 |

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index a71c720..ff8aba8 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -32,5 +32,8 @@ public class Main {
         } catch (IllegalStateException e) {
             System.out.println("결제 없이 배송: " + e.getMessage());
         }
+
+        Order order3 = orderService.placeOrder("박민수", "자바 책", -15000, 2, "대구시 수성구");
+        System.out.println("음수 금액 주문: " + order3.getTotalPrice() + "원");
     }
 }
```

## T2 금액을 돈 값 객체로 바꾸기
커밋: 813dc45

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/Money.java | 새로 |
| ddd/src/ddd/Order.java | 고침 |
| ddd/src/ddd/OrderService.java | 고침 |

```java
// ddd/src/ddd/Money.java
package ddd;

public record Money(int amount) {

    public Money {
        if (amount < 0) {
            throw new IllegalArgumentException("금액은 0원 이상이어야 합니다");
        }
    }

    public Money times(int count) {
        return new Money(amount * count);
    }

    public Money plus(Money other) {
        return new Money(amount + other.amount);
    }
}
```

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index d4ad2af..f6a24ca 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -7,6 +7,7 @@
 | 주문자 | customerName | 주문한 고객의 이름 |
 | 배송 주소 | address | 상품을 받을 곳 |
 | 주문 금액 | totalPrice | 가격 곱하기 수량 |
+| 돈 | Money | 0원 이상인 금액. 금액이 같으면 같은 돈이다 |
 | 주문하기 | placeOrder | 주문자, 상품, 수량, 배송 주소로 주문을 접수한다 |
 | 결제하기 | pay | 주문 금액을 치러 결제 완료가 된다 |
 | 배송하기 | ship | 결제 완료인 주문을 보내 배송 중이 된다 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index ff8aba8..6ad1223 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -9,8 +9,8 @@ public class Main {
 
         Order order = orderService.placeOrder("김철수", "자바 책", 15000, 2, "서울시 강남구");
         System.out.println("주문: " + order.getId() + "번, " + order.getCustomerName() + ", "
-                + order.getProductName() + " " + order.getQuantity() + "개, " + order.getTotalPrice() + "원, "
-                + order.getStatus());
+                + order.getProductName() + " " + order.getQuantity() + "개, " + order.getTotalPrice().amount()
+                + "원, " + order.getStatus());
 
         orderService.pay(order);
         System.out.println("결제: " + order.getStatus());
@@ -33,7 +33,11 @@ public class Main {
             System.out.println("결제 없이 배송: " + e.getMessage());
         }
 
-        Order order3 = orderService.placeOrder("박민수", "자바 책", -15000, 2, "대구시 수성구");
-        System.out.println("음수 금액 주문: " + order3.getTotalPrice() + "원");
+        try {
+            Order order3 = orderService.placeOrder("박민수", "자바 책", -15000, 2, "대구시 수성구");
+            System.out.println("음수 금액 주문: " + order3.getTotalPrice().amount() + "원");
+        } catch (IllegalArgumentException e) {
+            System.out.println("음수 금액 주문: " + e.getMessage());
+        }
     }
 }
diff --git a/ddd/src/ddd/Order.java b/ddd/src/ddd/Order.java
index c1704d5..fdda862 100644
--- a/ddd/src/ddd/Order.java
+++ b/ddd/src/ddd/Order.java
@@ -5,20 +5,20 @@ public class Order {
     private long id;
     private String customerName;
     private String productName;
-    private int price;
+    private Money price;
     private int quantity;
     private String address;
-    private int totalPrice;
+    private Money totalPrice;
     private String status;
 
-    public Order(long id, String customerName, String productName, int price, int quantity, String address) {
+    public Order(long id, String customerName, String productName, Money price, int quantity, String address) {
         this.id = id;
         this.customerName = customerName;
         this.productName = productName;
         this.price = price;
         this.quantity = quantity;
         this.address = address;
-        this.totalPrice = price * quantity;
+        this.totalPrice = price.times(quantity);
         this.status = "주문 접수";
     }
 
@@ -34,7 +34,7 @@ public class Order {
         return productName;
     }
 
-    public int getPrice() {
+    public Money getPrice() {
         return price;
     }
 
@@ -46,7 +46,7 @@ public class Order {
         return address;
     }
 
-    public int getTotalPrice() {
+    public Money getTotalPrice() {
         return totalPrice;
     }
 
diff --git a/ddd/src/ddd/OrderService.java b/ddd/src/ddd/OrderService.java
index c0c7ec5..fdc6850 100644
--- a/ddd/src/ddd/OrderService.java
+++ b/ddd/src/ddd/OrderService.java
@@ -5,7 +5,7 @@ public class OrderService {
     private long nextId = 1;
 
     public Order placeOrder(String customerName, String productName, int price, int quantity, String address) {
-        return new Order(nextId++, customerName, productName, price, quantity, address);
+        return new Order(nextId++, customerName, productName, new Money(price), quantity, address);
     }
 
     public void pay(Order order) {
```

## T3 주소를 주소 값 객체로 바꾸기
커밋: 2271683

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Address.java | 새로 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/Order.java | 고침 |
| ddd/src/ddd/OrderService.java | 고침 |

```java
// ddd/src/ddd/Address.java
package ddd;

public record Address(String value) {

    public Address {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("배송 주소를 입력해야 합니다");
        }
    }
}
```

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index f6a24ca..bd9d0c7 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -6,6 +6,7 @@
 | 주문 번호 | id | 주문마다 다른 번호. 번호가 같으면 같은 주문이다 |
 | 주문자 | customerName | 주문한 고객의 이름 |
 | 배송 주소 | address | 상품을 받을 곳 |
+| 주소 | Address | 비어 있지 않은 배송 주소. 글자가 같으면 같은 주소다 |
 | 주문 금액 | totalPrice | 가격 곱하기 수량 |
 | 돈 | Money | 0원 이상인 금액. 금액이 같으면 같은 돈이다 |
 | 주문하기 | placeOrder | 주문자, 상품, 수량, 배송 주소로 주문을 접수한다 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 6ad1223..31984cd 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -39,5 +39,12 @@ public class Main {
         } catch (IllegalArgumentException e) {
             System.out.println("음수 금액 주문: " + e.getMessage());
         }
+
+        try {
+            Order order4 = orderService.placeOrder("최지우", "자바 책", 15000, 1, "");
+            System.out.println("빈 주소 주문: " + order4.getStatus());
+        } catch (IllegalArgumentException e) {
+            System.out.println("빈 주소 주문: " + e.getMessage());
+        }
     }
 }
diff --git a/ddd/src/ddd/Order.java b/ddd/src/ddd/Order.java
index fdda862..76d6fd8 100644
--- a/ddd/src/ddd/Order.java
+++ b/ddd/src/ddd/Order.java
@@ -7,11 +7,11 @@ public class Order {
     private String productName;
     private Money price;
     private int quantity;
-    private String address;
+    private Address address;
     private Money totalPrice;
     private String status;
 
-    public Order(long id, String customerName, String productName, Money price, int quantity, String address) {
+    public Order(long id, String customerName, String productName, Money price, int quantity, Address address) {
         this.id = id;
         this.customerName = customerName;
         this.productName = productName;
@@ -42,7 +42,7 @@ public class Order {
         return quantity;
     }
 
-    public String getAddress() {
+    public Address getAddress() {
         return address;
     }
 
diff --git a/ddd/src/ddd/OrderService.java b/ddd/src/ddd/OrderService.java
index fdc6850..1d042d6 100644
--- a/ddd/src/ddd/OrderService.java
+++ b/ddd/src/ddd/OrderService.java
@@ -5,7 +5,7 @@ public class OrderService {
     private long nextId = 1;
 
     public Order placeOrder(String customerName, String productName, int price, int quantity, String address) {
-        return new Order(nextId++, customerName, productName, new Money(price), quantity, address);
+        return new Order(nextId++, customerName, productName, new Money(price), quantity, new Address(address));
     }
 
     public void pay(Order order) {
```

## T4 돈 값 객체 빼고 주소 하나로 보여 주기
커밋: a312669. 이 장 시점 코드에 얹은 patch는 fixes/a312669.patch

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/Money.java | 지움 |
| ddd/src/ddd/Order.java | 고침 |
| ddd/src/ddd/OrderService.java | 고침 |

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index bd9d0c7..f6151ef 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -8,7 +8,6 @@
 | 배송 주소 | address | 상품을 받을 곳 |
 | 주소 | Address | 비어 있지 않은 배송 주소. 글자가 같으면 같은 주소다 |
 | 주문 금액 | totalPrice | 가격 곱하기 수량 |
-| 돈 | Money | 0원 이상인 금액. 금액이 같으면 같은 돈이다 |
 | 주문하기 | placeOrder | 주문자, 상품, 수량, 배송 주소로 주문을 접수한다 |
 | 결제하기 | pay | 주문 금액을 치러 결제 완료가 된다 |
 | 배송하기 | ship | 결제 완료인 주문을 보내 배송 중이 된다 |
```

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 31984cd..068f944 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -3,46 +3,20 @@ package ddd;
 public class Main {
 
     public static void main(String[] args) {
-        System.out.println("온라인 주문 프로그램을 시작합니다");
+        System.out.println("===== 주소 =====");
 
         OrderService orderService = new OrderService();
 
+        System.out.println();
+        System.out.println("[주소로 주문하기]");
         Order order = orderService.placeOrder("김철수", "자바 책", 15000, 2, "서울시 강남구");
-        System.out.println("주문: " + order.getId() + "번, " + order.getCustomerName() + ", "
-                + order.getProductName() + " " + order.getQuantity() + "개, " + order.getTotalPrice().amount()
-                + "원, " + order.getStatus());
-
-        orderService.pay(order);
-        System.out.println("결제: " + order.getStatus());
-
-        orderService.ship(order);
-        System.out.println("배송: " + order.getStatus());
-
-        try {
-            orderService.cancel(order);
-            System.out.println("취소: " + order.getStatus());
-        } catch (IllegalStateException e) {
-            System.out.println("취소: " + e.getMessage());
-        }
-
-        Order order2 = orderService.placeOrder("이영희", "자바 책", 15000, 1, "부산시 해운대구");
-        try {
-            order2.ship();
-            System.out.println("결제 없이 배송: " + order2.getStatus());
-        } catch (IllegalStateException e) {
-            System.out.println("결제 없이 배송: " + e.getMessage());
-        }
-
-        try {
-            Order order3 = orderService.placeOrder("박민수", "자바 책", -15000, 2, "대구시 수성구");
-            System.out.println("음수 금액 주문: " + order3.getTotalPrice().amount() + "원");
-        } catch (IllegalArgumentException e) {
-            System.out.println("음수 금액 주문: " + e.getMessage());
-        }
+        System.out.println("주문: " + order.getId() + "번 주문, 배송 주소 " + order.getAddress().value());
 
+        System.out.println();
+        System.out.println("[빈 주소로 주문하기]");
         try {
-            Order order4 = orderService.placeOrder("최지우", "자바 책", 15000, 1, "");
-            System.out.println("빈 주소 주문: " + order4.getStatus());
+            Order emptyAddressOrder = orderService.placeOrder("최지우", "자바 책", 15000, 1, "");
+            System.out.println("빈 주소 주문: " + emptyAddressOrder.getId() + "번 주문, " + emptyAddressOrder.getStatus());
         } catch (IllegalArgumentException e) {
             System.out.println("빈 주소 주문: " + e.getMessage());
         }
```

```diff
diff --git a/ddd/src/ddd/Order.java b/ddd/src/ddd/Order.java
index 76d6fd8..5ef7f71 100644
--- a/ddd/src/ddd/Order.java
+++ b/ddd/src/ddd/Order.java
@@ -5,20 +5,20 @@ public class Order {
     private long id;
     private String customerName;
     private String productName;
-    private Money price;
+    private int price;
     private int quantity;
     private Address address;
-    private Money totalPrice;
+    private int totalPrice;
     private String status;
 
-    public Order(long id, String customerName, String productName, Money price, int quantity, Address address) {
+    public Order(long id, String customerName, String productName, int price, int quantity, Address address) {
         this.id = id;
         this.customerName = customerName;
         this.productName = productName;
         this.price = price;
         this.quantity = quantity;
         this.address = address;
-        this.totalPrice = price.times(quantity);
+        this.totalPrice = price * quantity;
         this.status = "주문 접수";
     }
 
@@ -34,7 +34,7 @@ public class Order {
         return productName;
     }
 
-    public Money getPrice() {
+    public int getPrice() {
         return price;
     }
 
@@ -46,7 +46,7 @@ public class Order {
         return address;
     }
 
-    public Money getTotalPrice() {
+    public int getTotalPrice() {
         return totalPrice;
     }
 
```

```diff
diff --git a/ddd/src/ddd/OrderService.java b/ddd/src/ddd/OrderService.java
index 1d042d6..a0c021c 100644
--- a/ddd/src/ddd/OrderService.java
+++ b/ddd/src/ddd/OrderService.java
@@ -5,7 +5,7 @@ public class OrderService {
     private long nextId = 1;
 
     public Order placeOrder(String customerName, String productName, int price, int quantity, String address) {
-        return new Order(nextId++, customerName, productName, new Money(price), quantity, new Address(address));
+        return new Order(nextId++, customerName, productName, price, quantity, new Address(address));
     }
 
     public void pay(Order order) {
```
