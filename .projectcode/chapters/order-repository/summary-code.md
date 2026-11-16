# order-repository 코드 요약

## T1 주문을 저장소에 넣고 번호로 꺼내기
커밋: 60382d4

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/MemoryOrderRepository.java | 새로 |
| ddd/src/ddd/OrderRepository.java | 새로 |
| ddd/src/ddd/OrderService.java | 고침 |

```java
// ddd/src/ddd/MemoryOrderRepository.java
package ddd;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MemoryOrderRepository implements OrderRepository {

    private final Map<Long, Order> orders = new HashMap<>();

    @Override
    public void save(Order order) {
        orders.put(order.getId(), order);
    }

    @Override
    public Optional<Order> findById(long id) {
        return Optional.ofNullable(orders.get(id));
    }
}
```

```java
// ddd/src/ddd/OrderRepository.java
package ddd;

import java.util.Optional;

public interface OrderRepository {

    void save(Order order);

    Optional<Order> findById(long id);
}
```

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index ef06780..7990a3c 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -23,3 +23,6 @@
 | 배송 중 | SHIPPING | 배송하기를 마친 상태 |
 | 배송 완료 | DELIVERED | 배송 완료하기를 마친 상태. 더 바뀌지 않는다 |
 | 취소 | CANCELED | 주문 취소하기를 마친 상태 |
+| 주문 저장소 | OrderRepository | 주문을 넣어 두고 주문 번호로 다시 꺼내는 곳 |
+| 저장하기 | save | 주문을 주문 저장소에 넣는다. 같은 번호가 있으면 새 주문으로 바꾼다 |
+| 주문 번호로 찾기 | findById | 주문 번호로 주문 저장소에서 주문을 꺼낸다. 없으면 빈 결과다 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index f7e4ea7..a2dc17e 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -7,12 +7,13 @@ public class Main {
     public static void main(String[] args) {
         System.out.println("온라인 주문 프로그램을 시작합니다");
 
-        OrderService orderService = new OrderService();
+        OrderService orderService = new OrderService(new MemoryOrderRepository());
 
         List<OrderLine> orderLines = List.of(
                 new OrderLine("자바 책", new Money(15000), 2),
                 new OrderLine("볼펜", new Money(1000), 1));
-        Order order = orderService.placeOrder("김철수", orderLines, "서울시 강남구");
+        long orderId = orderService.placeOrder("김철수", orderLines, "서울시 강남구");
+        Order order = orderService.findOrder(orderId);
         System.out.println("주문: " + order.getId() + "번, " + order.getCustomerName() + ", "
                 + order.getTotalPrice().amount() + "원, " + order.getStatus().label());
         for (OrderLine orderLine : order.getOrderLines()) {
@@ -20,81 +21,82 @@ public class Main {
                     + orderLine.amount().amount() + "원");
         }
 
-        orderService.pay(order);
-        System.out.println("결제: " + order.getStatus().label());
+        orderService.pay(orderId);
+        System.out.println("결제: " + orderService.findOrder(orderId).getStatus().label());
 
-        orderService.ship(order);
-        System.out.println("배송: " + order.getStatus().label());
+        orderService.ship(orderId);
+        System.out.println("배송: " + orderService.findOrder(orderId).getStatus().label());
 
         try {
-            orderService.cancel(order);
-            System.out.println("취소: " + order.getStatus().label());
+            orderService.cancel(orderId);
+            System.out.println("취소: " + orderService.findOrder(orderId).getStatus().label());
         } catch (IllegalStateException e) {
             System.out.println("취소: " + e.getMessage());
         }
 
-        Order order2 = orderService.placeOrder("이영희", List.of(new OrderLine("자바 책", new Money(15000), 1)),
+        long order2Id = orderService.placeOrder("이영희", List.of(new OrderLine("자바 책", new Money(15000), 1)),
                 "부산시 해운대구");
         try {
-            order2.ship();
-            System.out.println("결제 없이 배송: " + order2.getStatus().label());
+            orderService.ship(order2Id);
+            System.out.println("결제 없이 배송: " + orderService.findOrder(order2Id).getStatus().label());
         } catch (IllegalStateException e) {
             System.out.println("결제 없이 배송: " + e.getMessage());
         }
 
         try {
-            Order order3 = orderService.placeOrder("박민수", List.of(new OrderLine("자바 책", new Money(-15000), 2)),
+            long order3Id = orderService.placeOrder("박민수", List.of(new OrderLine("자바 책", new Money(-15000), 2)),
                     "대구시 수성구");
-            System.out.println("음수 금액 주문: " + order3.getTotalPrice().amount() + "원");
+            System.out.println("음수 금액 주문: " + orderService.findOrder(order3Id).getTotalPrice().amount() + "원");
         } catch (IllegalArgumentException e) {
             System.out.println("음수 금액 주문: " + e.getMessage());
         }
 
         try {
-            Order order4 = orderService.placeOrder("최지우", List.of(new OrderLine("자바 책", new Money(15000), 1)), "");
-            System.out.println("빈 주소 주문: " + order4.getStatus().label());
+            long order4Id = orderService.placeOrder("최지우", List.of(new OrderLine("자바 책", new Money(15000), 1)), "");
+            System.out.println("빈 주소 주문: " + orderService.findOrder(order4Id).getStatus().label());
         } catch (IllegalArgumentException e) {
             System.out.println("빈 주소 주문: " + e.getMessage());
         }
 
-        Order order5 = orderService.placeOrder("정하늘", List.of(
+        long order5Id = orderService.placeOrder("정하늘", List.of(
                 new OrderLine("자바 책", new Money(15000), 2),
                 new OrderLine("볼펜", new Money(1000), 1)), "광주시 북구");
-        order5.addOrderLine(new OrderLine("공책", new Money(2000), 5));
+        orderService.addOrderLine(order5Id, new OrderLine("공책", new Money(2000), 5));
+        Order order5 = orderService.findOrder(order5Id);
         System.out.println("항목 더하기: 항목 " + order5.getOrderLines().size() + "개, 주문 금액 "
                 + order5.getTotalPrice().amount() + "원");
 
-        orderService.pay(order5);
+        orderService.pay(order5Id);
         try {
-            order5.addOrderLine(new OrderLine("공책", new Money(2000), 1));
-            System.out.println("결제 뒤 항목 더하기: 항목 " + order5.getOrderLines().size() + "개");
+            orderService.addOrderLine(order5Id, new OrderLine("공책", new Money(2000), 1));
+            System.out.println("결제 뒤 항목 더하기: 항목 " + orderService.findOrder(order5Id).getOrderLines().size() + "개");
         } catch (IllegalStateException e) {
             System.out.println("결제 뒤 항목 더하기: " + e.getMessage());
         }
 
         try {
-            Order order6 = orderService.placeOrder("강민준", List.of(), "인천시 연수구");
-            System.out.println("빈 주문: " + order6.getStatus().label());
+            long order6Id = orderService.placeOrder("강민준", List.of(), "인천시 연수구");
+            System.out.println("빈 주문: " + orderService.findOrder(order6Id).getStatus().label());
         } catch (IllegalArgumentException e) {
             System.out.println("빈 주문: " + e.getMessage());
         }
 
         try {
-            Order order7 = orderService.placeOrder("윤서연", List.of(new OrderLine("자바 책", new Money(15000), 0)),
+            long order7Id = orderService.placeOrder("윤서연", List.of(new OrderLine("자바 책", new Money(15000), 0)),
                     "대전시 유성구");
-            System.out.println("0개 항목 주문: " + order7.getTotalPrice().amount() + "원");
+            System.out.println("0개 항목 주문: " + orderService.findOrder(order7Id).getTotalPrice().amount() + "원");
         } catch (IllegalArgumentException e) {
             System.out.println("0개 항목 주문: " + e.getMessage());
         }
 
         try {
-            orderService.pay(order5);
-            System.out.println("두 번 결제: " + order5.getStatus().label());
+            orderService.pay(order5Id);
+            System.out.println("두 번 결제: " + orderService.findOrder(order5Id).getStatus().label());
         } catch (IllegalStateException e) {
             System.out.println("두 번 결제: " + e.getMessage());
         }
 
-        orderService.deliver(order);
-        System.out.println("배송 완료: " + order.getStatus().label());
+        orderService.deliver(orderId);
+        System.out.println("배송 완료: " + orderService.findOrder(orderId).getStatus().label());
     }
 }
diff --git a/ddd/src/ddd/OrderService.java b/ddd/src/ddd/OrderService.java
index 5a0dcb7..00660ec 100644
--- a/ddd/src/ddd/OrderService.java
+++ b/ddd/src/ddd/OrderService.java
@@ -4,25 +4,50 @@ import java.util.List;
 
 public class OrderService {
 
+    private final OrderRepository orderRepository;
     private long nextId = 1;
 
-    public Order placeOrder(String customerName, List<OrderLine> orderLines, String address) {
-        return new Order(nextId++, customerName, orderLines, new Address(address));
+    public OrderService(OrderRepository orderRepository) {
+        this.orderRepository = orderRepository;
     }
 
-    public void pay(Order order) {
+    public long placeOrder(String customerName, List<OrderLine> orderLines, String address) {
+        Order order = new Order(nextId++, customerName, orderLines, new Address(address));
+        orderRepository.save(order);
+        return order.getId();
+    }
+
+    public Order findOrder(long orderId) {
+        return orderRepository.findById(orderId).orElseThrow();
+    }
+
+    public void addOrderLine(long orderId, OrderLine orderLine) {
+        Order order = findOrder(orderId);
+        order.addOrderLine(orderLine);
+        orderRepository.save(order);
+    }
+
+    public void pay(long orderId) {
+        Order order = findOrder(orderId);
         order.pay();
+        orderRepository.save(order);
     }
 
-    public void ship(Order order) {
+    public void ship(long orderId) {
+        Order order = findOrder(orderId);
         order.ship();
+        orderRepository.save(order);
     }
 
-    public void deliver(Order order) {
+    public void deliver(long orderId) {
+        Order order = findOrder(orderId);
         order.deliver();
+        orderRepository.save(order);
     }
 
-    public void cancel(Order order) {
+    public void cancel(long orderId) {
+        Order order = findOrder(orderId);
         order.cancel();
+        orderRepository.save(order);
     }
 }
```

## T2 없는 주문 번호 거절하기
커밋: b033e70

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/OrderService.java | 고침 |

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index a2dc17e..d393992 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -98,5 +98,12 @@ public class Main {
 
         orderService.deliver(orderId);
         System.out.println("배송 완료: " + orderService.findOrder(orderId).getStatus().label());
+
+        try {
+            orderService.pay(100);
+            System.out.println("없는 주문 결제: " + orderService.findOrder(100).getStatus().label());
+        } catch (IllegalArgumentException e) {
+            System.out.println("없는 주문 결제: " + e.getMessage());
+        }
     }
 }
diff --git a/ddd/src/ddd/OrderService.java b/ddd/src/ddd/OrderService.java
index 00660ec..72184e4 100644
--- a/ddd/src/ddd/OrderService.java
+++ b/ddd/src/ddd/OrderService.java
@@ -18,7 +18,7 @@ public class OrderService {
     }
 
     public Order findOrder(long orderId) {
-        return orderRepository.findById(orderId).orElseThrow();
+        return orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("주문이 없습니다"));
     }
 
     public void addOrderLine(long orderId, OrderLine orderLine) {
```

## T3 복사본으로 저장하기
커밋: 00ae372

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/Address.java | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/MemoryOrderRepository.java | 고침 |
| ddd/src/ddd/Money.java | 고침 |
| ddd/src/ddd/Order.java | 고침 |
| ddd/src/ddd/OrderLine.java | 고침 |

```diff
diff --git a/ddd/src/ddd/Address.java b/ddd/src/ddd/Address.java
index 3ef0d0b..9c40d83 100644
--- a/ddd/src/ddd/Address.java
+++ b/ddd/src/ddd/Address.java
@@ -1,6 +1,8 @@
 package ddd;
 
-public record Address(String value) {
+import java.io.Serializable;
+
+public record Address(String value) implements Serializable {
 
     public Address {
         if (value == null || value.isBlank()) {
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index d393992..d41c0dc 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -105,5 +105,9 @@ public class Main {
         } catch (IllegalArgumentException e) {
             System.out.println("없는 주문 결제: " + e.getMessage());
         }
+
+        Order notSaved = orderService.findOrder(order2Id);
+        notSaved.cancel();
+        System.out.println("저장 안 한 취소: " + orderService.findOrder(order2Id).getStatus().label());
     }
 }
diff --git a/ddd/src/ddd/MemoryOrderRepository.java b/ddd/src/ddd/MemoryOrderRepository.java
index 9b6ae0c..3c35f9e 100644
--- a/ddd/src/ddd/MemoryOrderRepository.java
+++ b/ddd/src/ddd/MemoryOrderRepository.java
@@ -1,5 +1,10 @@
 package ddd;
 
+import java.io.ByteArrayInputStream;
+import java.io.ByteArrayOutputStream;
+import java.io.IOException;
+import java.io.ObjectInputStream;
+import java.io.ObjectOutputStream;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Optional;
@@ -10,11 +15,27 @@ public class MemoryOrderRepository implements OrderRepository {
 
     @Override
     public void save(Order order) {
-        orders.put(order.getId(), order);
+        orders.put(order.getId(), copy(order));
     }
 
     @Override
     public Optional<Order> findById(long id) {
-        return Optional.ofNullable(orders.get(id));
+        Order order = orders.get(id);
+        if (order == null) {
+            return Optional.empty();
+        }
+        return Optional.of(copy(order));
+    }
+
+    private Order copy(Order order) {
+        try {
+            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
+            ObjectOutputStream out = new ObjectOutputStream(bytes);
+            out.writeObject(order);
+            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
+            return (Order) in.readObject();
+        } catch (IOException | ClassNotFoundException e) {
+            throw new IllegalStateException(e);
+        }
     }
 }
diff --git a/ddd/src/ddd/Money.java b/ddd/src/ddd/Money.java
index 3d9c001..5bd488e 100644
--- a/ddd/src/ddd/Money.java
+++ b/ddd/src/ddd/Money.java
@@ -1,6 +1,8 @@
 package ddd;
 
-public record Money(int amount) {
+import java.io.Serializable;
+
+public record Money(int amount) implements Serializable {
 
     public Money {
         if (amount < 0) {
diff --git a/ddd/src/ddd/Order.java b/ddd/src/ddd/Order.java
index 586a756..54d7ba4 100644
--- a/ddd/src/ddd/Order.java
+++ b/ddd/src/ddd/Order.java
@@ -1,9 +1,12 @@
 package ddd;
 
+import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.List;
 
-public class Order {
+public class Order implements Serializable {
+
+    private static final long serialVersionUID = 1L;
 
     private long id;
     private String customerName;
diff --git a/ddd/src/ddd/OrderLine.java b/ddd/src/ddd/OrderLine.java
index c07a632..0ad0972 100644
--- a/ddd/src/ddd/OrderLine.java
+++ b/ddd/src/ddd/OrderLine.java
@@ -1,6 +1,8 @@
 package ddd;
 
-public record OrderLine(String productName, Money price, int quantity) {
+import java.io.Serializable;
+
+public record OrderLine(String productName, Money price, int quantity) implements Serializable {
 
     public OrderLine {
         if (quantity < 1) {
```
