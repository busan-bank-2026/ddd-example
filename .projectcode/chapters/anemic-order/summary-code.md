# anemic-order 코드 요약

## T1 주문 만들기
커밋: 93dd337

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/Order.java | 새로 |
| ddd/src/ddd/OrderService.java | 새로 |
| ddd/src/ddd/Main.java | 고침 |

```java
// ddd/src/ddd/Order.java
package ddd;

public class Order {

    private String customerName;
    private String productName;
    private int price;
    private int quantity;
    private String address;
    private int totalPrice;
    private String status;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
```

```java
// ddd/src/ddd/OrderService.java
package ddd;

public class OrderService {

    public Order createOrder(String customerName, String productName, int price, int quantity, String address) {
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setProductName(productName);
        order.setPrice(price);
        order.setQuantity(quantity);
        order.setAddress(address);
        order.setTotalPrice(price * quantity);
        order.setStatus("주문 접수");
        return order;
    }
}
```

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 1f85397..c43a91a 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -4,5 +4,11 @@ public class Main {
 
     public static void main(String[] args) {
         System.out.println("온라인 주문 프로그램을 시작합니다");
+
+        OrderService orderService = new OrderService();
+
+        Order order = orderService.createOrder("김철수", "자바 책", 15000, 2, "서울시 강남구");
+        System.out.println("주문: " + order.getCustomerName() + ", " + order.getProductName() + " "
+                + order.getQuantity() + "개, " + order.getTotalPrice() + "원, " + order.getStatus());
     }
 }
```

## T2 주문 결제하기
커밋: 3881965

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/OrderService.java | 고침 |
| ddd/src/ddd/Main.java | 고침 |

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index c43a91a..fa38042 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -10,5 +10,8 @@ public class Main {
         Order order = orderService.createOrder("김철수", "자바 책", 15000, 2, "서울시 강남구");
         System.out.println("주문: " + order.getCustomerName() + ", " + order.getProductName() + " "
                 + order.getQuantity() + "개, " + order.getTotalPrice() + "원, " + order.getStatus());
+
+        orderService.updatePayment(order);
+        System.out.println("결제: " + order.getStatus());
     }
 }
diff --git a/ddd/src/ddd/OrderService.java b/ddd/src/ddd/OrderService.java
index 14a8e6b..071b943 100644
--- a/ddd/src/ddd/OrderService.java
+++ b/ddd/src/ddd/OrderService.java
@@ -13,4 +13,8 @@ public class OrderService {
         order.setStatus("주문 접수");
         return order;
     }
+
+    public void updatePayment(Order order) {
+        order.setStatus("결제 완료");
+    }
 }
```

## T3 주문 배송하기
커밋: b43a9eb

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/OrderService.java | 고침 |
| ddd/src/ddd/Main.java | 고침 |

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index fa38042..340b125 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -13,5 +13,8 @@ public class Main {
 
         orderService.updatePayment(order);
         System.out.println("결제: " + order.getStatus());
+
+        orderService.updateDelivery(order);
+        System.out.println("배송: " + order.getStatus());
     }
 }
diff --git a/ddd/src/ddd/OrderService.java b/ddd/src/ddd/OrderService.java
index 071b943..1875443 100644
--- a/ddd/src/ddd/OrderService.java
+++ b/ddd/src/ddd/OrderService.java
@@ -17,4 +17,11 @@ public class OrderService {
     public void updatePayment(Order order) {
         order.setStatus("결제 완료");
     }
+
+    public void updateDelivery(Order order) {
+        if (!order.getStatus().equals("결제 완료")) {
+            throw new IllegalStateException("결제하지 않은 주문은 배송할 수 없습니다");
+        }
+        order.setStatus("배송 중");
+    }
 }
```

## T4 주문 취소하기
커밋: e48bf5d

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/OrderService.java | 고침 |
| ddd/src/ddd/Main.java | 고침 |

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 340b125..06efcc3 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -16,5 +16,12 @@ public class Main {
 
         orderService.updateDelivery(order);
         System.out.println("배송: " + order.getStatus());
+
+        try {
+            orderService.deleteOrder(order);
+            System.out.println("취소: " + order.getStatus());
+        } catch (IllegalStateException e) {
+            System.out.println("취소: " + e.getMessage());
+        }
     }
 }
diff --git a/ddd/src/ddd/OrderService.java b/ddd/src/ddd/OrderService.java
index 1875443..a538fd1 100644
--- a/ddd/src/ddd/OrderService.java
+++ b/ddd/src/ddd/OrderService.java
@@ -24,4 +24,11 @@ public class OrderService {
         }
         order.setStatus("배송 중");
     }
+
+    public void deleteOrder(Order order) {
+        if (order.getStatus().equals("배송 중")) {
+            throw new IllegalStateException("배송 중인 주문은 취소할 수 없습니다");
+        }
+        order.setStatus("취소");
+    }
 }
```

