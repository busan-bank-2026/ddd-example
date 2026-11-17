# layers 코드 요약

## T1 도메인 계층 만들기
커밋: 5d69234

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/MemoryOrderRepository.java | 고침 |
| ddd/src/ddd/OrderApplicationService.java | 고침 |
| ddd/src/ddd/domain/Address.java | 옮김. ddd/src/ddd/Address.java에서 옮기고 package 줄을 바꿈 |
| ddd/src/ddd/domain/Money.java | 옮김. ddd/src/ddd/Money.java에서 옮기고 package 줄을 바꿈 |
| ddd/src/ddd/domain/Order.java | 옮김. ddd/src/ddd/Order.java에서 옮기고 package 줄을 바꿈 |
| ddd/src/ddd/domain/OrderLine.java | 옮김. ddd/src/ddd/OrderLine.java에서 옮기고 package 줄을 바꿈 |
| ddd/src/ddd/domain/OrderRepository.java | 옮김. ddd/src/ddd/OrderRepository.java에서 옮기고 package 줄을 바꿈 |
| ddd/src/ddd/domain/OrderStatus.java | 옮김. ddd/src/ddd/OrderStatus.java에서 옮기고 package 줄을 바꿈 |

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index ccd7f8e..8d9f64d 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -2,6 +2,9 @@ package ddd;
 
 import java.util.List;
 
+import ddd.domain.Order;
+import ddd.domain.OrderLine;
+
 public class Main {
 
     public static void main(String[] args) {
diff --git a/ddd/src/ddd/MemoryOrderRepository.java b/ddd/src/ddd/MemoryOrderRepository.java
index 3c35f9e..c851cea 100644
--- a/ddd/src/ddd/MemoryOrderRepository.java
+++ b/ddd/src/ddd/MemoryOrderRepository.java
@@ -9,6 +9,9 @@ import java.util.HashMap;
 import java.util.Map;
 import java.util.Optional;
 
+import ddd.domain.Order;
+import ddd.domain.OrderRepository;
+
 public class MemoryOrderRepository implements OrderRepository {
 
     private final Map<Long, Order> orders = new HashMap<>();
diff --git a/ddd/src/ddd/OrderApplicationService.java b/ddd/src/ddd/OrderApplicationService.java
index ab216fe..c54cba4 100644
--- a/ddd/src/ddd/OrderApplicationService.java
+++ b/ddd/src/ddd/OrderApplicationService.java
@@ -3,6 +3,12 @@ package ddd;
 import java.util.ArrayList;
 import java.util.List;
 
+import ddd.domain.Address;
+import ddd.domain.Money;
+import ddd.domain.Order;
+import ddd.domain.OrderLine;
+import ddd.domain.OrderRepository;
+
 public class OrderApplicationService {
 
     private final OrderRepository orderRepository;
diff --git a/ddd/src/ddd/Address.java b/ddd/src/ddd/domain/Address.java
similarity index 93%
rename from ddd/src/ddd/Address.java
rename to ddd/src/ddd/domain/Address.java
index 9c40d83..5cfd21e 100644
--- a/ddd/src/ddd/Address.java
+++ b/ddd/src/ddd/domain/Address.java
@@ -1,4 +1,4 @@
-package ddd;
+package ddd.domain;
 
 import java.io.Serializable;
 
diff --git a/ddd/src/ddd/Money.java b/ddd/src/ddd/domain/Money.java
similarity index 95%
rename from ddd/src/ddd/Money.java
rename to ddd/src/ddd/domain/Money.java
index 5bd488e..b17acdb 100644
--- a/ddd/src/ddd/Money.java
+++ b/ddd/src/ddd/domain/Money.java
@@ -1,4 +1,4 @@
-package ddd;
+package ddd.domain;
 
 import java.io.Serializable;
 
diff --git a/ddd/src/ddd/Order.java b/ddd/src/ddd/domain/Order.java
similarity index 99%
rename from ddd/src/ddd/Order.java
rename to ddd/src/ddd/domain/Order.java
index 54d7ba4..3432038 100644
--- a/ddd/src/ddd/Order.java
+++ b/ddd/src/ddd/domain/Order.java
@@ -1,4 +1,4 @@
-package ddd;
+package ddd.domain;
 
 import java.io.Serializable;
 import java.util.ArrayList;
diff --git a/ddd/src/ddd/OrderLine.java b/ddd/src/ddd/domain/OrderLine.java
similarity index 94%
rename from ddd/src/ddd/OrderLine.java
rename to ddd/src/ddd/domain/OrderLine.java
index 0ad0972..8011bd9 100644
--- a/ddd/src/ddd/OrderLine.java
+++ b/ddd/src/ddd/domain/OrderLine.java
@@ -1,4 +1,4 @@
-package ddd;
+package ddd.domain;
 
 import java.io.Serializable;
 
diff --git a/ddd/src/ddd/OrderRepository.java b/ddd/src/ddd/domain/OrderRepository.java
similarity index 87%
rename from ddd/src/ddd/OrderRepository.java
rename to ddd/src/ddd/domain/OrderRepository.java
index 4bac976..f013c21 100644
--- a/ddd/src/ddd/OrderRepository.java
+++ b/ddd/src/ddd/domain/OrderRepository.java
@@ -1,4 +1,4 @@
-package ddd;
+package ddd.domain;
 
 import java.util.Optional;
 
diff --git a/ddd/src/ddd/OrderStatus.java b/ddd/src/ddd/domain/OrderStatus.java
similarity index 97%
rename from ddd/src/ddd/OrderStatus.java
rename to ddd/src/ddd/domain/OrderStatus.java
index 3c6629d..486e63f 100644
--- a/ddd/src/ddd/OrderStatus.java
+++ b/ddd/src/ddd/domain/OrderStatus.java
@@ -1,4 +1,4 @@
-package ddd;
+package ddd.domain;
 
 public enum OrderStatus {
     ORDERED("주문 접수"),
```

## T2 응용 계층 만들기
커밋: 8aee741

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/application/OrderApplicationService.java | 옮김. ddd/src/ddd/OrderApplicationService.java에서 옮기고 package 줄을 바꿈 |
| ddd/src/ddd/application/OrderLineCommand.java | 옮김. ddd/src/ddd/OrderLineCommand.java에서 옮기고 package 줄을 바꿈 |
| ddd/src/ddd/application/PlaceOrderCommand.java | 옮김. ddd/src/ddd/PlaceOrderCommand.java에서 옮기고 package 줄을 바꿈 |

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 8d9f64d..0bb484b 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -2,6 +2,9 @@ package ddd;
 
 import java.util.List;
 
+import ddd.application.OrderApplicationService;
+import ddd.application.OrderLineCommand;
+import ddd.application.PlaceOrderCommand;
 import ddd.domain.Order;
 import ddd.domain.OrderLine;
 
diff --git a/ddd/src/ddd/OrderApplicationService.java b/ddd/src/ddd/application/OrderApplicationService.java
similarity index 98%
rename from ddd/src/ddd/OrderApplicationService.java
rename to ddd/src/ddd/application/OrderApplicationService.java
index c54cba4..e930d02 100644
--- a/ddd/src/ddd/OrderApplicationService.java
+++ b/ddd/src/ddd/application/OrderApplicationService.java
@@ -1,4 +1,4 @@
-package ddd;
+package ddd.application;
 
 import java.util.ArrayList;
 import java.util.List;
diff --git a/ddd/src/ddd/OrderLineCommand.java b/ddd/src/ddd/application/OrderLineCommand.java
similarity index 76%
rename from ddd/src/ddd/OrderLineCommand.java
rename to ddd/src/ddd/application/OrderLineCommand.java
index a6ebc2a..c74c436 100644
--- a/ddd/src/ddd/OrderLineCommand.java
+++ b/ddd/src/ddd/application/OrderLineCommand.java
@@ -1,4 +1,4 @@
-package ddd;
+package ddd.application;
 
 public record OrderLineCommand(String productName, int price, int quantity) {
 }
diff --git a/ddd/src/ddd/PlaceOrderCommand.java b/ddd/src/ddd/application/PlaceOrderCommand.java
similarity index 84%
rename from ddd/src/ddd/PlaceOrderCommand.java
rename to ddd/src/ddd/application/PlaceOrderCommand.java
index 7f93dbc..8a38563 100644
--- a/ddd/src/ddd/PlaceOrderCommand.java
+++ b/ddd/src/ddd/application/PlaceOrderCommand.java
@@ -1,4 +1,4 @@
-package ddd;
+package ddd.application;
 
 import java.util.List;
 
```

## T3 인프라 계층 만들기
커밋: 11aea06

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/infrastructure/MemoryOrderRepository.java | 옮김. ddd/src/ddd/MemoryOrderRepository.java에서 옮기고 package 줄을 바꿈 |

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 0bb484b..062a5ab 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -7,6 +7,7 @@ import ddd.application.OrderLineCommand;
 import ddd.application.PlaceOrderCommand;
 import ddd.domain.Order;
 import ddd.domain.OrderLine;
+import ddd.infrastructure.MemoryOrderRepository;
 
 public class Main {
 
diff --git a/ddd/src/ddd/MemoryOrderRepository.java b/ddd/src/ddd/infrastructure/MemoryOrderRepository.java
similarity index 97%
rename from ddd/src/ddd/MemoryOrderRepository.java
rename to ddd/src/ddd/infrastructure/MemoryOrderRepository.java
index c851cea..94cb6a0 100644
--- a/ddd/src/ddd/MemoryOrderRepository.java
+++ b/ddd/src/ddd/infrastructure/MemoryOrderRepository.java
@@ -1,4 +1,4 @@
-package ddd;
+package ddd.infrastructure;
 
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
```

## T4 주문 컨텍스트 모양으로 계층 나누기
커밋: a312669. 이 장 시점 코드에 얹은 patch는 fixes/a312669.patch

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/application/OrderLineCommand.java | 지움 |
| ddd/src/ddd/application/PlaceOrderCommand.java | 지움 |
| ddd/src/ddd/domain/Money.java | 지움 |
| ddd/src/ddd/domain/OrderLine.java | 지움 |
| ddd/src/ddd/domain/OrderStatus.java | 지움 |
| ddd/src/ddd/infrastructure/MemoryOrderRepository.java | 지움 |
| ddd/src/ddd/order/application/OrderItemRequest.java | 새로 |
| ddd/src/ddd/order/application/OrderRequest.java | 새로 |
| ddd/src/ddd/order/application/OrderService.java | 옮김. ddd/src/ddd/application/OrderApplicationService.java에서 옮기고 고침 |
| ddd/src/ddd/order/domain/Address.java | 옮김. ddd/src/ddd/domain/Address.java에서 옮기고 고침 |
| ddd/src/ddd/order/domain/Order.java | 옮김. ddd/src/ddd/domain/Order.java에서 옮기고 고침 |
| ddd/src/ddd/order/domain/OrderItem.java | 새로 |
| ddd/src/ddd/order/domain/OrderRepository.java | 옮김. ddd/src/ddd/domain/OrderRepository.java에서 옮기고 고침 |
| ddd/src/ddd/order/domain/OrderStatus.java | 새로 |
| ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java | 새로 |

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index 16ca2e3..a7a4d3d 100644
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
@@ -26,6 +25,6 @@
 | 주문 저장소 | OrderRepository | 주문을 넣어 두고 주문 번호로 다시 꺼내는 곳 |
 | 저장하기 | save | 주문을 주문 저장소에 넣는다. 같은 번호가 있으면 새 주문으로 바꾼다 |
 | 주문 번호로 찾기 | findById | 주문 번호로 주문 저장소에서 주문을 꺼낸다. 없으면 빈 결과다 |
-| 주문 응용 서비스 | OrderApplicationService | 주문 유스케이스를 하는 곳. 주문을 꺼내고, 주문에게 시키고, 저장한다. 규칙은 갖지 않는다 |
-| 주문하기 명령 | PlaceOrderCommand | 주문하기에 넣는 주문자, 주문 항목 명령 목록, 배송 주소 묶음 |
-| 주문 항목 명령 | OrderLineCommand | 주문 항목을 만들 상품 이름, 가격, 수량 묶음 |
+| 주문 응용 서비스 | OrderService | 주문 유스케이스를 하는 곳. 주문을 꺼내고, 주문에게 시키고, 저장한다. 규칙은 갖지 않는다 |
+| 주문 요청 | OrderRequest | 주문하기에 넣는 주문자, 주문 항목 요청 목록, 배송 주소 묶음 |
+| 주문 항목 요청 | OrderItemRequest | 주문 항목을 만들 상품 이름, 가격, 수량 묶음 |
```

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 062a5ab..10bd470 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -2,120 +2,28 @@ package ddd;
 
 import java.util.List;
 
-import ddd.application.OrderApplicationService;
-import ddd.application.OrderLineCommand;
-import ddd.application.PlaceOrderCommand;
-import ddd.domain.Order;
-import ddd.domain.OrderLine;
-import ddd.infrastructure.MemoryOrderRepository;
+import ddd.order.application.OrderItemRequest;
+import ddd.order.application.OrderRequest;
+import ddd.order.application.OrderService;
+import ddd.order.domain.Order;
+import ddd.order.infrastructure.MemoryOrderRepository;
 
 public class Main {
 
     public static void main(String[] args) {
-        System.out.println("온라인 주문 프로그램을 시작합니다");
+        System.out.println("===== 계층 나누기 =====");
 
-        OrderApplicationService orderService = new OrderApplicationService(new MemoryOrderRepository());
+        OrderService orderService = new OrderService(new MemoryOrderRepository());
 
-        List<OrderLineCommand> orderLines = List.of(
-                new OrderLineCommand("자바 책", 15000, 2),
-                new OrderLineCommand("볼펜", 1000, 1));
-        long orderId = orderService.placeOrder(new PlaceOrderCommand("김철수", orderLines, "서울시 강남구"));
+        System.out.println();
+        System.out.println("[계층을 거쳐 주문하고 결제하기]");
+        long orderId = orderService.placeOrder(new OrderRequest("김철수", List.of(
+                new OrderItemRequest("자바 책", 15000, 2),
+                new OrderItemRequest("볼펜", 1000, 1)), "서울시 강남구"));
         Order order = orderService.findOrder(orderId);
-        System.out.println("주문: " + order.getId() + "번, " + order.getCustomerName() + ", "
-                + order.getTotalPrice().amount() + "원, " + order.getStatus().label());
-        for (OrderLine orderLine : order.getOrderLines()) {
-            System.out.println("주문 항목: " + orderLine.productName() + " " + orderLine.quantity() + "개, "
-                    + orderLine.amount().amount() + "원");
-        }
-
+        System.out.println("주문: " + orderId + "번 주문, " + order.getTotalPrice() + "원, " + order.getStatus().label());
+        String statusBefore = order.getStatus().label();
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
-        long order2Id = orderService.placeOrder(new PlaceOrderCommand("이영희",
-                List.of(new OrderLineCommand("자바 책", 15000, 1)), "부산시 해운대구"));
-        try {
-            orderService.ship(order2Id);
-            System.out.println("결제 없이 배송: " + orderService.findOrder(order2Id).getStatus().label());
-        } catch (IllegalStateException e) {
-            System.out.println("결제 없이 배송: " + e.getMessage());
-        }
-
-        try {
-            long order3Id = orderService.placeOrder(new PlaceOrderCommand("박민수",
-                    List.of(new OrderLineCommand("자바 책", -15000, 2)), "대구시 수성구"));
-            System.out.println("음수 금액 주문: " + orderService.findOrder(order3Id).getTotalPrice().amount() + "원");
-        } catch (IllegalArgumentException e) {
-            System.out.println("음수 금액 주문: " + e.getMessage());
-        }
-
-        try {
-            long order4Id = orderService.placeOrder(new PlaceOrderCommand("최지우",
-                    List.of(new OrderLineCommand("자바 책", 15000, 1)), ""));
-            System.out.println("빈 주소 주문: " + orderService.findOrder(order4Id).getStatus().label());
-        } catch (IllegalArgumentException e) {
-            System.out.println("빈 주소 주문: " + e.getMessage());
-        }
-
-        long order5Id = orderService.placeOrder(new PlaceOrderCommand("정하늘", List.of(
-                new OrderLineCommand("자바 책", 15000, 2),
-                new OrderLineCommand("볼펜", 1000, 1)), "광주시 북구"));
-        orderService.addOrderLine(order5Id, new OrderLineCommand("공책", 2000, 5));
-        Order order5 = orderService.findOrder(order5Id);
-        System.out.println("항목 더하기: 항목 " + order5.getOrderLines().size() + "개, 주문 금액 "
-                + order5.getTotalPrice().amount() + "원");
-
-        orderService.pay(order5Id);
-        try {
-            orderService.addOrderLine(order5Id, new OrderLineCommand("공책", 2000, 1));
-            System.out.println("결제 뒤 항목 더하기: 항목 " + orderService.findOrder(order5Id).getOrderLines().size() + "개");
-        } catch (IllegalStateException e) {
-            System.out.println("결제 뒤 항목 더하기: " + e.getMessage());
-        }
-
-        try {
-            long order6Id = orderService.placeOrder(new PlaceOrderCommand("강민준", List.of(), "인천시 연수구"));
-            System.out.println("빈 주문: " + orderService.findOrder(order6Id).getStatus().label());
-        } catch (IllegalArgumentException e) {
-            System.out.println("빈 주문: " + e.getMessage());
-        }
-
-        try {
-            long order7Id = orderService.placeOrder(new PlaceOrderCommand("윤서연",
-                    List.of(new OrderLineCommand("자바 책", 15000, 0)), "대전시 유성구"));
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
-
-        try {
-            orderService.pay(100);
-            System.out.println("없는 주문 결제: " + orderService.findOrder(100).getStatus().label());
-        } catch (IllegalArgumentException e) {
-            System.out.println("없는 주문 결제: " + e.getMessage());
-        }
-
-        Order notSaved = orderService.findOrder(order2Id);
-        notSaved.cancel();
-        System.out.println("저장 안 한 취소: " + orderService.findOrder(order2Id).getStatus().label());
+        System.out.println("주문 상태: " + statusBefore + " -> " + orderService.findOrder(orderId).getStatus().label());
     }
 }
```

```diff
diff --git a/ddd/src/ddd/application/OrderLineCommand.java b/ddd/src/ddd/application/OrderLineCommand.java
deleted file mode 100644
index c74c436..0000000
--- a/ddd/src/ddd/application/OrderLineCommand.java
+++ /dev/null
@@ -1,4 +0,0 @@
-package ddd.application;
-
-public record OrderLineCommand(String productName, int price, int quantity) {
-}
```

```diff
diff --git a/ddd/src/ddd/application/PlaceOrderCommand.java b/ddd/src/ddd/application/PlaceOrderCommand.java
deleted file mode 100644
index 8a38563..0000000
--- a/ddd/src/ddd/application/PlaceOrderCommand.java
+++ /dev/null
@@ -1,6 +0,0 @@
-package ddd.application;
-
-import java.util.List;
-
-public record PlaceOrderCommand(String customerName, List<OrderLineCommand> orderLines, String address) {
-}
```

```diff
diff --git a/ddd/src/ddd/domain/Money.java b/ddd/src/ddd/domain/Money.java
deleted file mode 100644
index b17acdb..0000000
--- a/ddd/src/ddd/domain/Money.java
+++ /dev/null
@@ -1,20 +0,0 @@
-package ddd.domain;
-
-import java.io.Serializable;
-
-public record Money(int amount) implements Serializable {
-
-    public Money {
-        if (amount < 0) {
-            throw new IllegalArgumentException("금액은 0원 이상이어야 합니다");
-        }
-    }
-
-    public Money times(int count) {
-        return new Money(amount * count);
-    }
-
-    public Money plus(Money other) {
-        return new Money(amount + other.amount);
-    }
-}
```

```diff
diff --git a/ddd/src/ddd/domain/OrderLine.java b/ddd/src/ddd/domain/OrderLine.java
deleted file mode 100644
index 8011bd9..0000000
--- a/ddd/src/ddd/domain/OrderLine.java
+++ /dev/null
@@ -1,16 +0,0 @@
-package ddd.domain;
-
-import java.io.Serializable;
-
-public record OrderLine(String productName, Money price, int quantity) implements Serializable {
-
-    public OrderLine {
-        if (quantity < 1) {
-            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다");
-        }
-    }
-
-    public Money amount() {
-        return price.times(quantity);
-    }
-}
```

```diff
diff --git a/ddd/src/ddd/domain/OrderStatus.java b/ddd/src/ddd/domain/OrderStatus.java
deleted file mode 100644
index 486e63f..0000000
--- a/ddd/src/ddd/domain/OrderStatus.java
+++ /dev/null
@@ -1,28 +0,0 @@
-package ddd.domain;
-
-public enum OrderStatus {
-    ORDERED("주문 접수"),
-    PAID("결제 완료"),
-    SHIPPING("배송 중"),
-    DELIVERED("배송 완료"),
-    CANCELED("취소");
-
-    private final String label;
-
-    OrderStatus(String label) {
-        this.label = label;
-    }
-
-    public String label() {
-        return label;
-    }
-
-    public boolean canChangeTo(OrderStatus next) {
-        return switch (this) {
-            case ORDERED -> next == PAID || next == CANCELED;
-            case PAID -> next == SHIPPING || next == CANCELED;
-            case SHIPPING -> next == DELIVERED;
-            case DELIVERED, CANCELED -> false;
-        };
-    }
-}
```

```diff
diff --git a/ddd/src/ddd/infrastructure/MemoryOrderRepository.java b/ddd/src/ddd/infrastructure/MemoryOrderRepository.java
deleted file mode 100644
index 94cb6a0..0000000
--- a/ddd/src/ddd/infrastructure/MemoryOrderRepository.java
+++ /dev/null
@@ -1,44 +0,0 @@
-package ddd.infrastructure;
-
-import java.io.ByteArrayInputStream;
-import java.io.ByteArrayOutputStream;
-import java.io.IOException;
-import java.io.ObjectInputStream;
-import java.io.ObjectOutputStream;
-import java.util.HashMap;
-import java.util.Map;
-import java.util.Optional;
-
-import ddd.domain.Order;
-import ddd.domain.OrderRepository;
-
-public class MemoryOrderRepository implements OrderRepository {
-
-    private final Map<Long, Order> orders = new HashMap<>();
-
-    @Override
-    public void save(Order order) {
-        orders.put(order.getId(), copy(order));
-    }
-
-    @Override
-    public Optional<Order> findById(long id) {
-        Order order = orders.get(id);
-        if (order == null) {
-            return Optional.empty();
-        }
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
-    }
-}
```

```diff
diff --git a/ddd/src/ddd/order/application/OrderItemRequest.java b/ddd/src/ddd/order/application/OrderItemRequest.java
new file mode 100644
index 0000000..de3ab20
--- /dev/null
+++ b/ddd/src/ddd/order/application/OrderItemRequest.java
@@ -0,0 +1,4 @@
+package ddd.order.application;
+
+public record OrderItemRequest(String productName, int price, int quantity) {
+}
```

```diff
diff --git a/ddd/src/ddd/order/application/OrderRequest.java b/ddd/src/ddd/order/application/OrderRequest.java
new file mode 100644
index 0000000..0d79319
--- /dev/null
+++ b/ddd/src/ddd/order/application/OrderRequest.java
@@ -0,0 +1,6 @@
+package ddd.order.application;
+
+import java.util.List;
+
+public record OrderRequest(String customerName, List<OrderItemRequest> items, String address) {
+}
```

```diff
diff --git a/ddd/src/ddd/application/OrderApplicationService.java b/ddd/src/ddd/order/application/OrderService.java
similarity index 54%
rename from ddd/src/ddd/application/OrderApplicationService.java
rename to ddd/src/ddd/order/application/OrderService.java
index e930d02..eab735c 100644
--- a/ddd/src/ddd/application/OrderApplicationService.java
+++ b/ddd/src/ddd/order/application/OrderService.java
@@ -1,29 +1,28 @@
-package ddd.application;
+package ddd.order.application;
 
 import java.util.ArrayList;
 import java.util.List;
 
-import ddd.domain.Address;
-import ddd.domain.Money;
-import ddd.domain.Order;
-import ddd.domain.OrderLine;
-import ddd.domain.OrderRepository;
+import ddd.order.domain.Address;
+import ddd.order.domain.Order;
+import ddd.order.domain.OrderItem;
+import ddd.order.domain.OrderRepository;
 
-public class OrderApplicationService {
+public class OrderService {
 
     private final OrderRepository orderRepository;
     private long nextId = 1;
 
-    public OrderApplicationService(OrderRepository orderRepository) {
+    public OrderService(OrderRepository orderRepository) {
         this.orderRepository = orderRepository;
     }
 
-    public long placeOrder(PlaceOrderCommand command) {
-        List<OrderLine> orderLines = new ArrayList<>();
-        for (OrderLineCommand orderLineCommand : command.orderLines()) {
-            orderLines.add(toOrderLine(orderLineCommand));
+    public long placeOrder(OrderRequest request) {
+        List<OrderItem> items = new ArrayList<>();
+        for (OrderItemRequest itemRequest : request.items()) {
+            items.add(new OrderItem(itemRequest.productName(), itemRequest.price(), itemRequest.quantity()));
         }
-        Order order = new Order(nextId++, command.customerName(), orderLines, new Address(command.address()));
+        Order order = new Order(nextId++, request.customerName(), items, new Address(request.address()));
         orderRepository.save(order);
         return order.getId();
     }
@@ -32,9 +31,9 @@ public class OrderApplicationService {
         return orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("주문이 없습니다"));
     }
 
-    public void addOrderLine(long orderId, OrderLineCommand command) {
+    public void addItem(long orderId, OrderItemRequest request) {
         Order order = findOrder(orderId);
-        order.addOrderLine(toOrderLine(command));
+        order.addItem(new OrderItem(request.productName(), request.price(), request.quantity()));
         orderRepository.save(order);
     }
 
@@ -61,8 +60,4 @@ public class OrderApplicationService {
         order.cancel();
         orderRepository.save(order);
     }
-
-    private OrderLine toOrderLine(OrderLineCommand command) {
-        return new OrderLine(command.productName(), new Money(command.price()), command.quantity());
-    }
 }
```

```diff
diff --git a/ddd/src/ddd/domain/Address.java b/ddd/src/ddd/order/domain/Address.java
similarity index 61%
rename from ddd/src/ddd/domain/Address.java
rename to ddd/src/ddd/order/domain/Address.java
index 5cfd21e..2a69b63 100644
--- a/ddd/src/ddd/domain/Address.java
+++ b/ddd/src/ddd/order/domain/Address.java
@@ -1,8 +1,6 @@
-package ddd.domain;
+package ddd.order.domain;
 
-import java.io.Serializable;
-
-public record Address(String value) implements Serializable {
+public record Address(String value) {
 
     public Address {
         if (value == null || value.isBlank()) {
```

```diff
diff --git a/ddd/src/ddd/domain/Order.java b/ddd/src/ddd/order/domain/Order.java
similarity index 63%
rename from ddd/src/ddd/domain/Order.java
rename to ddd/src/ddd/order/domain/Order.java
index 3432038..366038b 100644
--- a/ddd/src/ddd/domain/Order.java
+++ b/ddd/src/ddd/order/domain/Order.java
@@ -1,35 +1,41 @@
-package ddd.domain;
+package ddd.order.domain;
 
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

```diff
diff --git a/ddd/src/ddd/order/domain/OrderItem.java b/ddd/src/ddd/order/domain/OrderItem.java
new file mode 100644
index 0000000..916a0bd
--- /dev/null
+++ b/ddd/src/ddd/order/domain/OrderItem.java
@@ -0,0 +1,36 @@
+package ddd.order.domain;
+
+public class OrderItem {
+
+    private final String productName;
+    private final int price;
+    private final int quantity;
+
+    public OrderItem(String productName, int price, int quantity) {
+        if (price < 0) {
+            throw new IllegalArgumentException("가격은 0원 이상이어야 합니다");
+        }
+        if (quantity < 1) {
+            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다");
+        }
+        this.productName = productName;
+        this.price = price;
+        this.quantity = quantity;
+    }
+
+    public String getProductName() {
+        return productName;
+    }
+
+    public int getPrice() {
+        return price;
+    }
+
+    public int getQuantity() {
+        return quantity;
+    }
+
+    public int getAmount() {
+        return price * quantity;
+    }
+}
```

```diff
diff --git a/ddd/src/ddd/domain/OrderRepository.java b/ddd/src/ddd/order/domain/OrderRepository.java
similarity index 83%
rename from ddd/src/ddd/domain/OrderRepository.java
rename to ddd/src/ddd/order/domain/OrderRepository.java
index f013c21..8aa47c5 100644
--- a/ddd/src/ddd/domain/OrderRepository.java
+++ b/ddd/src/ddd/order/domain/OrderRepository.java
@@ -1,4 +1,4 @@
-package ddd.domain;
+package ddd.order.domain;
 
 import java.util.Optional;
 
```

```diff
diff --git a/ddd/src/ddd/order/domain/OrderStatus.java b/ddd/src/ddd/order/domain/OrderStatus.java
new file mode 100644
index 0000000..d8965da
--- /dev/null
+++ b/ddd/src/ddd/order/domain/OrderStatus.java
@@ -0,0 +1,19 @@
+package ddd.order.domain;
+
+public enum OrderStatus {
+    ORDERED("주문 접수"),
+    PAID("결제 완료"),
+    SHIPPING("배송 중"),
+    DELIVERED("배송 완료"),
+    CANCELED("취소");
+
+    private final String label;
+
+    OrderStatus(String label) {
+        this.label = label;
+    }
+
+    public String label() {
+        return label;
+    }
+}
```

```diff
diff --git a/ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java b/ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java
new file mode 100644
index 0000000..cffbd2b
--- /dev/null
+++ b/ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java
@@ -0,0 +1,27 @@
+package ddd.order.infrastructure;
+
+import java.util.HashMap;
+import java.util.Map;
+import java.util.Optional;
+
+import ddd.order.domain.Order;
+import ddd.order.domain.OrderRepository;
+
+public class MemoryOrderRepository implements OrderRepository {
+
+    private final Map<Long, Order> orders = new HashMap<>();
+
+    @Override
+    public void save(Order order) {
+        orders.put(order.getId(), new Order(order));
+    }
+
+    @Override
+    public Optional<Order> findById(long id) {
+        Order order = orders.get(id);
+        if (order == null) {
+            return Optional.empty();
+        }
+        return Optional.of(new Order(order));
+    }
+}
```
