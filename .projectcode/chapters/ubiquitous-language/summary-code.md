# ubiquitous-language 코드 요약

## T1 용어집 만들기
커밋: 235f8bb

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 새로 |

```markdown
# 용어집

| 업무 말 | 코드 이름 | 뜻 |
|---|---|---|
| 주문 | Order | 고객이 상품을 사겠다고 한 요청 |
| 주문자 | customerName | 주문한 고객의 이름 |
| 배송 주소 | address | 상품을 받을 곳 |
| 주문 금액 | totalPrice | 가격 곱하기 수량 |
| 주문하기 | placeOrder | 주문자, 상품, 수량, 배송 주소로 주문을 접수한다 |
| 결제하기 | pay | 주문 금액을 치러 결제 완료가 된다 |
| 배송하기 | ship | 결제 완료인 주문을 보내 배송 중이 된다 |
| 주문 취소하기 | cancel | 배송 전 주문을 취소 상태로 둔다. 지우지 않는다 |
| 주문 접수 | 주문 접수 | 주문하기를 마친 상태 |
| 결제 완료 | 결제 완료 | 결제하기를 마친 상태 |
| 배송 중 | 배송 중 | 배송하기를 마친 상태 |
| 취소 | 취소 | 주문 취소하기를 마친 상태 |
```

## T2 주문하기로 이름 바꾸기
커밋: 6a2ddc8

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/OrderService.java | 고침 |
| ddd/src/ddd/Main.java | 고침 |

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 06efcc3..d2fb1c6 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -7,7 +7,7 @@ public class Main {
 
         OrderService orderService = new OrderService();
 
-        Order order = orderService.createOrder("김철수", "자바 책", 15000, 2, "서울시 강남구");
+        Order order = orderService.placeOrder("김철수", "자바 책", 15000, 2, "서울시 강남구");
         System.out.println("주문: " + order.getCustomerName() + ", " + order.getProductName() + " "
                 + order.getQuantity() + "개, " + order.getTotalPrice() + "원, " + order.getStatus());
 
diff --git a/ddd/src/ddd/OrderService.java b/ddd/src/ddd/OrderService.java
index a538fd1..c4d6a99 100644
--- a/ddd/src/ddd/OrderService.java
+++ b/ddd/src/ddd/OrderService.java
@@ -2,7 +2,7 @@ package ddd;
 
 public class OrderService {
 
-    public Order createOrder(String customerName, String productName, int price, int quantity, String address) {
+    public Order placeOrder(String customerName, String productName, int price, int quantity, String address) {
         Order order = new Order();
         order.setCustomerName(customerName);
         order.setProductName(productName);
```

## T3 결제하기와 배송하기로 이름 바꾸기
커밋: 6d854d2

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/OrderService.java | 고침 |
| ddd/src/ddd/Main.java | 고침 |

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index d2fb1c6..d9f0db8 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -11,10 +11,10 @@ public class Main {
         System.out.println("주문: " + order.getCustomerName() + ", " + order.getProductName() + " "
                 + order.getQuantity() + "개, " + order.getTotalPrice() + "원, " + order.getStatus());
 
-        orderService.updatePayment(order);
+        orderService.pay(order);
         System.out.println("결제: " + order.getStatus());
 
-        orderService.updateDelivery(order);
+        orderService.ship(order);
         System.out.println("배송: " + order.getStatus());
 
         try {
diff --git a/ddd/src/ddd/OrderService.java b/ddd/src/ddd/OrderService.java
index c4d6a99..7f25a50 100644
--- a/ddd/src/ddd/OrderService.java
+++ b/ddd/src/ddd/OrderService.java
@@ -14,11 +14,11 @@ public class OrderService {
         return order;
     }
 
-    public void updatePayment(Order order) {
+    public void pay(Order order) {
         order.setStatus("결제 완료");
     }
 
-    public void updateDelivery(Order order) {
+    public void ship(Order order) {
         if (!order.getStatus().equals("결제 완료")) {
             throw new IllegalStateException("결제하지 않은 주문은 배송할 수 없습니다");
         }
```

## T4 주문 취소하기로 이름 바꾸기
커밋: a95cd26

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/OrderService.java | 고침 |
| ddd/src/ddd/Main.java | 고침 |

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index d9f0db8..1a70226 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -18,7 +18,7 @@ public class Main {
         System.out.println("배송: " + order.getStatus());
 
         try {
-            orderService.deleteOrder(order);
+            orderService.cancel(order);
             System.out.println("취소: " + order.getStatus());
         } catch (IllegalStateException e) {
             System.out.println("취소: " + e.getMessage());
diff --git a/ddd/src/ddd/OrderService.java b/ddd/src/ddd/OrderService.java
index 7f25a50..292410f 100644
--- a/ddd/src/ddd/OrderService.java
+++ b/ddd/src/ddd/OrderService.java
@@ -25,7 +25,7 @@ public class OrderService {
         order.setStatus("배송 중");
     }
 
-    public void deleteOrder(Order order) {
+    public void cancel(Order order) {
         if (order.getStatus().equals("배송 중")) {
             throw new IllegalStateException("배송 중인 주문은 취소할 수 없습니다");
         }
```

