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

## T4 복사 생성자로 주문 복사하기
커밋: a312669. 이 장 시점 코드에 얹은 patch는 fixes/a312669.patch

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Address.java | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/MemoryOrderRepository.java | 고침 |
| ddd/src/ddd/Money.java | 지움 |
| ddd/src/ddd/Order.java | 고침 |
| ddd/src/ddd/OrderItem.java | 새로 |
| ddd/src/ddd/OrderLine.java | 지움 |
| ddd/src/ddd/OrderService.java | 고침 |
| ddd/src/ddd/OrderStatus.java | 고침 |

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index 7990a3c..08fe411 100644
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
diff --git a/ddd/src/ddd/Address.java b/ddd/src/ddd/Address.java
index 9c40d83..3ef0d0b 100644
--- a/ddd/src/ddd/Address.java
+++ b/ddd/src/ddd/Address.java
@@ -1,8 +1,6 @@
 package ddd;
 
-import java.io.Serializable;
-
-public record Address(String value) implements Serializable {
+public record Address(String value) {
 
     public Address {
         if (value == null || value.isBlank()) {
```

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index d41c0dc..b09457a 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -5,100 +5,27 @@ import java.util.List;
 public class Main {
 
     public static void main(String[] args) {
-        System.out.println("온라인 주문 프로그램을 시작합니다");
+        System.out.println("===== 주문 저장소 =====");
 
         OrderService orderService = new OrderService(new MemoryOrderRepository());
 
-        List<OrderLine> orderLines = List.of(
-                new OrderLine("자바 책", new Money(15000), 2),
-                new OrderLine("볼펜", new Money(1000), 1));
-        long orderId = orderService.placeOrder("김철수", orderLines, "서울시 강남구");
+        System.out.println();
+        System.out.println("[주문하고 번호로 꺼내기]");
+        long orderId = orderService.placeOrder("김철수", List.of(
+                new OrderItem("자바 책", 15000, 2),
+                new OrderItem("볼펜", 1000, 1)), "서울시 강남구");
+        System.out.println("주문하기: " + orderId + "번 주문 저장");
         Order order = orderService.findOrder(orderId);
-        System.out.println("주문: " + order.getId() + "번, " + order.getCustomerName() + ", "
-                + order.getTotalPrice().amount() + "원, " + order.getStatus().label());
-        for (OrderLine orderLine : order.getOrderLines()) {
-            System.out.println("주문 항목: " + orderLine.productName() + " " + orderLine.quantity() + "개, "
-                    + orderLine.amount().amount() + "원");
-        }
+        System.out.println(orderId + "번 꺼내기: " + order.getTotalPrice() + "원, " + order.getStatus().label());
 
+        System.out.println();
+        System.out.println("[주문 번호로 결제하기]");
+        String statusBefore = first.getStatus().label();
         orderService.pay(orderId);
-        System.out.println("결제: " + orderService.findOrder(orderId).getStatus().label());
-
-        orderService.ship(orderId);
-        System.out.println("배송: " + orderService.findOrder(orderId).getStatus().label());
-
-        try {
-            orderService.cancel(orderId);
-            System.out.println("취소: " + orderService.findOrder(orderId).getStatus().label());
-        } catch (IllegalStateException e) {
-            System.out.println("취소: " + e.getMessage());
-        }
-
-        long order2Id = orderService.placeOrder("이영희", List.of(new OrderLine("자바 책", new Money(15000), 1)),
-                "부산시 해운대구");
-        try {
-            orderService.ship(order2Id);
-            System.out.println("결제 없이 배송: " + orderService.findOrder(order2Id).getStatus().label());
-        } catch (IllegalStateException e) {
-            System.out.println("결제 없이 배송: " + e.getMessage());
-        }
-
-        try {
-            long order3Id = orderService.placeOrder("박민수", List.of(new OrderLine("자바 책", new Money(-15000), 2)),
-                    "대구시 수성구");
-            System.out.println("음수 금액 주문: " + orderService.findOrder(order3Id).getTotalPrice().amount() + "원");
-        } catch (IllegalArgumentException e) {
-            System.out.println("음수 금액 주문: " + e.getMessage());
-        }
-
-        try {
-            long order4Id = orderService.placeOrder("최지우", List.of(new OrderLine("자바 책", new Money(15000), 1)), "");
-            System.out.println("빈 주소 주문: " + orderService.findOrder(order4Id).getStatus().label());
-        } catch (IllegalArgumentException e) {
-            System.out.println("빈 주소 주문: " + e.getMessage());
-        }
-
-        long order5Id = orderService.placeOrder("정하늘", List.of(
-                new OrderLine("자바 책", new Money(15000), 2),
-                new OrderLine("볼펜", new Money(1000), 1)), "광주시 북구");
-        orderService.addOrderLine(order5Id, new OrderLine("공책", new Money(2000), 5));
-        Order order5 = orderService.findOrder(order5Id);
-        System.out.println("항목 더하기: 항목 " + order5.getOrderLines().size() + "개, 주문 금액 "
-                + order5.getTotalPrice().amount() + "원");
-
-        orderService.pay(order5Id);
-        try {
-            orderService.addOrderLine(order5Id, new OrderLine("공책", new Money(2000), 1));
-            System.out.println("결제 뒤 항목 더하기: 항목 " + orderService.findOrder(order5Id).getOrderLines().size() + "개");
-        } catch (IllegalStateException e) {
-            System.out.println("결제 뒤 항목 더하기: " + e.getMessage());
-        }
-
-        try {
-            long order6Id = orderService.placeOrder("강민준", List.of(), "인천시 연수구");
-            System.out.println("빈 주문: " + orderService.findOrder(order6Id).getStatus().label());
-        } catch (IllegalArgumentException e) {
-            System.out.println("빈 주문: " + e.getMessage());
-        }
-
-        try {
-            long order7Id = orderService.placeOrder("윤서연", List.of(new OrderLine("자바 책", new Money(15000), 0)),
-                    "대전시 유성구");
-            System.out.println("0개 항목 주문: " + orderService.findOrder(order7Id).getTotalPrice().amount() + "원");
-        } catch (IllegalArgumentException e) {
-            System.out.println("0개 항목 주문: " + e.getMessage());
-        }
-
-        try {
-            orderService.pay(order5Id);
-            System.out.println("두 번 결제: " + orderService.findOrder(order5Id).getStatus().label());
-        } catch (IllegalStateException e) {
-            System.out.println("두 번 결제: " + e.getMessage());
-        }
-
-        orderService.deliver(orderId);
-        System.out.println("배송 완료: " + orderService.findOrder(orderId).getStatus().label());
+        System.out.println("주문 상태: " + statusBefore + " -> " + orderService.findOrder(orderId).getStatus().label());
 
+        System.out.println();
+        System.out.println("[없는 주문 결제하기]");
         try {
             orderService.pay(100);
             System.out.println("없는 주문 결제: " + orderService.findOrder(100).getStatus().label());
@@ -106,8 +33,13 @@ public class Main {
             System.out.println("없는 주문 결제: " + e.getMessage());
         }
 
+        System.out.println();
+        System.out.println("[꺼낸 주문만 바꾸기]");
+        long order2Id = orderService.placeOrder("이영희", List.of(new OrderItem("자바 책", 15000, 1)), "부산시 해운대구");
         Order notSaved = orderService.findOrder(order2Id);
+        String notSavedBefore = notSaved.getStatus().label();
         notSaved.cancel();
-        System.out.println("저장 안 한 취소: " + orderService.findOrder(order2Id).getStatus().label());
+        System.out.println("꺼낸 주문 상태: " + notSavedBefore + " -> " + notSaved.getStatus().label());
+        System.out.println("저장소 주문 상태: " + orderService.findOrder(order2Id).getStatus().label());
     }
 }
```

```diff
diff --git a/ddd/src/ddd/MemoryOrderRepository.java b/ddd/src/ddd/MemoryOrderRepository.java
index 3c35f9e..339a89b 100644
--- a/ddd/src/ddd/MemoryOrderRepository.java
+++ b/ddd/src/ddd/MemoryOrderRepository.java
@@ -1,10 +1,5 @@
 package ddd;
 
-import java.io.ByteArrayInputStream;
-import java.io.ByteArrayOutputStream;
-import java.io.IOException;
-import java.io.ObjectInputStream;
-import java.io.ObjectOutputStream;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Optional;
@@ -15,7 +10,7 @@ public class MemoryOrderRepository implements OrderRepository {
 
     @Override
     public void save(Order order) {
-        orders.put(order.getId(), copy(order));
+        orders.put(order.getId(), new Order(order));
     }
 
     @Override
@@ -24,18 +19,6 @@ public class MemoryOrderRepository implements OrderRepository {
         if (order == null) {
             return Optional.empty();
         }
-        return Optional.of(copy(order));
-    }
-
-    private Order copy(Order order) {
-        try {
-            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
-            ObjectOutputStream out = new ObjectOutputStream(bytes);
-            out.writeObject(order);
-            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
-            return (Order) in.readObject();
-        } catch (IOException | ClassNotFoundException e) {
-            throw new IllegalStateException(e);
-        }
+        return Optional.of(new Order(order));
     }
 }
```

```diff
diff --git a/ddd/src/ddd/Order.java b/ddd/src/ddd/Order.java
index 54d7ba4..abce97a 100644
--- a/ddd/src/ddd/Order.java
+++ b/ddd/src/ddd/Order.java
@@ -1,35 +1,41 @@
 package ddd;
 
-import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.List;
 
-public class Order implements Serializable {
-
-    private static final long serialVersionUID = 1L;
+public class Order {
 
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
 
+    public Order(Order other) {
+        this.id = other.id;
+        this.customerName = other.customerName;
+        this.items = new ArrayList<>(other.items);
+        this.address = other.address;
+        this.totalPrice = other.totalPrice;
+        this.status = other.status;
+    }
+
     public long getId() {
         return id;
     }
@@ -38,15 +44,15 @@ public class Order implements Serializable {
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
 
@@ -54,37 +60,37 @@ public class Order implements Serializable {
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
index 72184e4..f1d6324 100644
--- a/ddd/src/ddd/OrderService.java
+++ b/ddd/src/ddd/OrderService.java
@@ -11,8 +11,8 @@ public class OrderService {
         this.orderRepository = orderRepository;
     }
 
-    public long placeOrder(String customerName, List<OrderLine> orderLines, String address) {
-        Order order = new Order(nextId++, customerName, orderLines, new Address(address));
+    public long placeOrder(String customerName, List<OrderItem> items, String address) {
+        Order order = new Order(nextId++, customerName, items, new Address(address));
         orderRepository.save(order);
         return order.getId();
     }
@@ -21,9 +21,9 @@ public class OrderService {
         return orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("주문이 없습니다"));
     }
 
-    public void addOrderLine(long orderId, OrderLine orderLine) {
+    public void addItem(long orderId, OrderItem item) {
         Order order = findOrder(orderId);
-        order.addOrderLine(orderLine);
+        order.addItem(item);
         orderRepository.save(order);
     }
 
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
