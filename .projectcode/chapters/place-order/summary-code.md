# place-order 코드 요약

## T1 주문하기 명령으로 주문하기
커밋: 69ad13f

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/OrderLineCommand.java | 새로 |
| ddd/src/ddd/OrderService.java | 고침 |
| ddd/src/ddd/PlaceOrderCommand.java | 새로 |

```java
// ddd/src/ddd/OrderLineCommand.java
package ddd;

public record OrderLineCommand(String productName, int price, int quantity) {
}
```

```java
// ddd/src/ddd/PlaceOrderCommand.java
package ddd;

import java.util.List;

public record PlaceOrderCommand(String customerName, List<OrderLineCommand> orderLines, String address) {
}
```

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index d41c0dc..d4e35d9 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -9,10 +9,10 @@ public class Main {
 
         OrderService orderService = new OrderService(new MemoryOrderRepository());
 
-        List<OrderLine> orderLines = List.of(
-                new OrderLine("자바 책", new Money(15000), 2),
-                new OrderLine("볼펜", new Money(1000), 1));
-        long orderId = orderService.placeOrder("김철수", orderLines, "서울시 강남구");
+        List<OrderLineCommand> orderLines = List.of(
+                new OrderLineCommand("자바 책", 15000, 2),
+                new OrderLineCommand("볼펜", 1000, 1));
+        long orderId = orderService.placeOrder(new PlaceOrderCommand("김철수", orderLines, "서울시 강남구"));
         Order order = orderService.findOrder(orderId);
         System.out.println("주문: " + order.getId() + "번, " + order.getCustomerName() + ", "
                 + order.getTotalPrice().amount() + "원, " + order.getStatus().label());
@@ -34,8 +34,8 @@ public class Main {
             System.out.println("취소: " + e.getMessage());
         }
 
-        long order2Id = orderService.placeOrder("이영희", List.of(new OrderLine("자바 책", new Money(15000), 1)),
-                "부산시 해운대구");
+        long order2Id = orderService.placeOrder(new PlaceOrderCommand("이영희",
+                List.of(new OrderLineCommand("자바 책", 15000, 1)), "부산시 해운대구"));
         try {
             orderService.ship(order2Id);
             System.out.println("결제 없이 배송: " + orderService.findOrder(order2Id).getStatus().label());
@@ -44,46 +44,47 @@ public class Main {
         }
 
         try {
-            long order3Id = orderService.placeOrder("박민수", List.of(new OrderLine("자바 책", new Money(-15000), 2)),
-                    "대구시 수성구");
+            long order3Id = orderService.placeOrder(new PlaceOrderCommand("박민수",
+                    List.of(new OrderLineCommand("자바 책", -15000, 2)), "대구시 수성구"));
             System.out.println("음수 금액 주문: " + orderService.findOrder(order3Id).getTotalPrice().amount() + "원");
         } catch (IllegalArgumentException e) {
             System.out.println("음수 금액 주문: " + e.getMessage());
         }
 
         try {
-            long order4Id = orderService.placeOrder("최지우", List.of(new OrderLine("자바 책", new Money(15000), 1)), "");
+            long order4Id = orderService.placeOrder(new PlaceOrderCommand("최지우",
+                    List.of(new OrderLineCommand("자바 책", 15000, 1)), ""));
             System.out.println("빈 주소 주문: " + orderService.findOrder(order4Id).getStatus().label());
         } catch (IllegalArgumentException e) {
             System.out.println("빈 주소 주문: " + e.getMessage());
         }
 
-        long order5Id = orderService.placeOrder("정하늘", List.of(
-                new OrderLine("자바 책", new Money(15000), 2),
-                new OrderLine("볼펜", new Money(1000), 1)), "광주시 북구");
-        orderService.addOrderLine(order5Id, new OrderLine("공책", new Money(2000), 5));
+        long order5Id = orderService.placeOrder(new PlaceOrderCommand("정하늘", List.of(
+                new OrderLineCommand("자바 책", 15000, 2),
+                new OrderLineCommand("볼펜", 1000, 1)), "광주시 북구"));
+        orderService.addOrderLine(order5Id, new OrderLineCommand("공책", 2000, 5));
         Order order5 = orderService.findOrder(order5Id);
         System.out.println("항목 더하기: 항목 " + order5.getOrderLines().size() + "개, 주문 금액 "
                 + order5.getTotalPrice().amount() + "원");
 
         orderService.pay(order5Id);
         try {
-            orderService.addOrderLine(order5Id, new OrderLine("공책", new Money(2000), 1));
+            orderService.addOrderLine(order5Id, new OrderLineCommand("공책", 2000, 1));
             System.out.println("결제 뒤 항목 더하기: 항목 " + orderService.findOrder(order5Id).getOrderLines().size() + "개");
         } catch (IllegalStateException e) {
             System.out.println("결제 뒤 항목 더하기: " + e.getMessage());
         }
 
         try {
-            long order6Id = orderService.placeOrder("강민준", List.of(), "인천시 연수구");
+            long order6Id = orderService.placeOrder(new PlaceOrderCommand("강민준", List.of(), "인천시 연수구"));
             System.out.println("빈 주문: " + orderService.findOrder(order6Id).getStatus().label());
         } catch (IllegalArgumentException e) {
             System.out.println("빈 주문: " + e.getMessage());
         }
 
         try {
-            long order7Id = orderService.placeOrder("윤서연", List.of(new OrderLine("자바 책", new Money(15000), 0)),
-                    "대전시 유성구");
+            long order7Id = orderService.placeOrder(new PlaceOrderCommand("윤서연",
+                    List.of(new OrderLineCommand("자바 책", 15000, 0)), "대전시 유성구"));
             System.out.println("0개 항목 주문: " + orderService.findOrder(order7Id).getTotalPrice().amount() + "원");
         } catch (IllegalArgumentException e) {
             System.out.println("0개 항목 주문: " + e.getMessage());
diff --git a/ddd/src/ddd/OrderService.java b/ddd/src/ddd/OrderService.java
index 72184e4..c3cd224 100644
--- a/ddd/src/ddd/OrderService.java
+++ b/ddd/src/ddd/OrderService.java
@@ -1,5 +1,6 @@
 package ddd;
 
+import java.util.ArrayList;
 import java.util.List;
 
 public class OrderService {
@@ -11,8 +12,12 @@ public class OrderService {
         this.orderRepository = orderRepository;
     }
 
-    public long placeOrder(String customerName, List<OrderLine> orderLines, String address) {
-        Order order = new Order(nextId++, customerName, orderLines, new Address(address));
+    public long placeOrder(PlaceOrderCommand command) {
+        List<OrderLine> orderLines = new ArrayList<>();
+        for (OrderLineCommand orderLineCommand : command.orderLines()) {
+            orderLines.add(toOrderLine(orderLineCommand));
+        }
+        Order order = new Order(nextId++, command.customerName(), orderLines, new Address(command.address()));
         orderRepository.save(order);
         return order.getId();
     }
@@ -21,9 +26,9 @@ public class OrderService {
         return orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("주문이 없습니다"));
     }
 
-    public void addOrderLine(long orderId, OrderLine orderLine) {
+    public void addOrderLine(long orderId, OrderLineCommand command) {
         Order order = findOrder(orderId);
-        order.addOrderLine(orderLine);
+        order.addOrderLine(toOrderLine(command));
         orderRepository.save(order);
     }
 
@@ -50,4 +55,8 @@ public class OrderService {
         order.cancel();
         orderRepository.save(order);
     }
+
+    private OrderLine toOrderLine(OrderLineCommand command) {
+        return new OrderLine(command.productName(), new Money(command.price()), command.quantity());
+    }
 }
```

## T2 주문 응용 서비스로 이름 짓기
커밋: d7dda27

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/OrderService.java | 지움. OrderApplicationService.java로 이름을 바꿈 |
| ddd/src/ddd/OrderApplicationService.java | 새로 |

```java
// ddd/src/ddd/OrderApplicationService.java
package ddd;

import java.util.ArrayList;
import java.util.List;

public class OrderApplicationService {

    private final OrderRepository orderRepository;
    private long nextId = 1;

    public OrderApplicationService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public long placeOrder(PlaceOrderCommand command) {
        List<OrderLine> orderLines = new ArrayList<>();
        for (OrderLineCommand orderLineCommand : command.orderLines()) {
            orderLines.add(toOrderLine(orderLineCommand));
        }
        Order order = new Order(nextId++, command.customerName(), orderLines, new Address(command.address()));
        orderRepository.save(order);
        return order.getId();
    }

    public Order findOrder(long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("주문이 없습니다"));
    }

    public void addOrderLine(long orderId, OrderLineCommand command) {
        Order order = findOrder(orderId);
        order.addOrderLine(toOrderLine(command));
        orderRepository.save(order);
    }

    public void pay(long orderId) {
        Order order = findOrder(orderId);
        order.pay();
        orderRepository.save(order);
    }

    public void ship(long orderId) {
        Order order = findOrder(orderId);
        order.ship();
        orderRepository.save(order);
    }

    public void deliver(long orderId) {
        Order order = findOrder(orderId);
        order.deliver();
        orderRepository.save(order);
    }

    public void cancel(long orderId) {
        Order order = findOrder(orderId);
        order.cancel();
        orderRepository.save(order);
    }

    private OrderLine toOrderLine(OrderLineCommand command) {
        return new OrderLine(command.productName(), new Money(command.price()), command.quantity());
    }
}
```

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index 7990a3c..16ca2e3 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -26,3 +26,6 @@
 | 주문 저장소 | OrderRepository | 주문을 넣어 두고 주문 번호로 다시 꺼내는 곳 |
 | 저장하기 | save | 주문을 주문 저장소에 넣는다. 같은 번호가 있으면 새 주문으로 바꾼다 |
 | 주문 번호로 찾기 | findById | 주문 번호로 주문 저장소에서 주문을 꺼낸다. 없으면 빈 결과다 |
+| 주문 응용 서비스 | OrderApplicationService | 주문 유스케이스를 하는 곳. 주문을 꺼내고, 주문에게 시키고, 저장한다. 규칙은 갖지 않는다 |
+| 주문하기 명령 | PlaceOrderCommand | 주문하기에 넣는 주문자, 주문 항목 명령 목록, 배송 주소 묶음 |
+| 주문 항목 명령 | OrderLineCommand | 주문 항목을 만들 상품 이름, 가격, 수량 묶음 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index d4e35d9..ccd7f8e 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -7,7 +7,7 @@ public class Main {
     public static void main(String[] args) {
         System.out.println("온라인 주문 프로그램을 시작합니다");
 
-        OrderService orderService = new OrderService(new MemoryOrderRepository());
+        OrderApplicationService orderService = new OrderApplicationService(new MemoryOrderRepository());
 
         List<OrderLineCommand> orderLines = List.of(
                 new OrderLineCommand("자바 책", 15000, 2),
diff --git a/ddd/src/ddd/OrderService.java b/ddd/src/ddd/OrderApplicationService.java
similarity index 94%
rename from ddd/src/ddd/OrderService.java
rename to ddd/src/ddd/OrderApplicationService.java
index c3cd224..ab216fe 100644
--- a/ddd/src/ddd/OrderService.java
+++ b/ddd/src/ddd/OrderApplicationService.java
@@ -3,12 +3,12 @@ package ddd;
 import java.util.ArrayList;
 import java.util.List;
 
-public class OrderService {
+public class OrderApplicationService {
 
     private final OrderRepository orderRepository;
     private long nextId = 1;
 
-    public OrderService(OrderRepository orderRepository) {
+    public OrderApplicationService(OrderRepository orderRepository) {
         this.orderRepository = orderRepository;
     }
 
```
