# order-factory 코드 요약

## T1 팩토리로 주문 항목 만들기
커밋: 3694c95

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/application/OrderApplicationService.java | 고침 |
| ddd/src/ddd/application/OrderLineCommand.java | 고침 |
| ddd/src/ddd/domain/OrderLineFactory.java | 새로 |

```java
// ddd/src/ddd/domain/OrderLineFactory.java
package ddd.domain;

public class OrderLineFactory {

    private final ProductRepository productRepository;

    public OrderLineFactory(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public OrderLine create(long productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow();
        return new OrderLine(product.getId(), product.getName(), product.getPrice(), quantity);
    }
}
```

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index 2a742f7..003d0f0 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -28,9 +28,10 @@
 | 주문 번호로 찾기 | findById | 주문 번호로 주문 저장소에서 주문을 꺼낸다. 없으면 빈 결과다 |
 | 주문 응용 서비스 | OrderApplicationService | 주문 유스케이스를 하는 곳. 주문을 꺼내고, 주문에게 시키고, 저장한다. 규칙은 갖지 않는다 |
 | 주문하기 명령 | PlaceOrderCommand | 주문하기에 넣는 회원 번호, 주문 항목 명령 목록, 배송 주소 묶음 |
-| 주문 항목 명령 | OrderLineCommand | 주문 항목을 만들 상품 번호, 상품 이름, 가격, 수량 묶음 |
+| 주문 항목 명령 | OrderLineCommand | 주문 항목을 만들 상품 번호, 수량 묶음 |
 | 회원 | Member | 상품을 주문하는 사람. 회원 번호와 이름을 가진다. 회원 번호가 같으면 같은 회원이다 |
 | 회원 저장소 | MemberRepository | 회원을 넣어 두고 회원 번호로 다시 꺼내는 곳 |
 | 상품 | Product | 파는 물건. 상품 번호, 이름, 가격을 가진다. 상품 번호가 같으면 같은 상품이다 |
 | 상품 저장소 | ProductRepository | 상품을 넣어 두고 상품 번호로 다시 꺼내는 곳 |
 | 상품 번호 | productId | 주문 항목이 가리키는 상품의 번호. 주문 항목은 상품을 객체로 갖지 않고 이 번호로 가리킨다 |
+| 주문 항목 팩토리 | OrderLineFactory | 상품 번호와 수량을 받아 상품 저장소에서 상품을 찾고, 그 상품의 이름과 지금 가격으로 주문 항목을 만드는 곳 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 09587d6..3aaab0a 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -10,6 +10,7 @@ import ddd.domain.MemberRepository;
 import ddd.domain.Money;
 import ddd.domain.Order;
 import ddd.domain.OrderLine;
+import ddd.domain.OrderLineFactory;
 import ddd.domain.Product;
 import ddd.domain.ProductRepository;
 import ddd.infrastructure.MemoryMemberRepository;
@@ -30,11 +31,12 @@ public class Main {
         productRepository.save(new Product(2, "볼펜", new Money(1000)));
         productRepository.save(new Product(3, "공책", new Money(2000)));
 
-        OrderApplicationService orderService = new OrderApplicationService(new MemoryOrderRepository());
+        OrderApplicationService orderService = new OrderApplicationService(new MemoryOrderRepository(),
+                new OrderLineFactory(productRepository));
 
         List<OrderLineCommand> orderLines = List.of(
-                new OrderLineCommand(1, "자바 책", 15000, 2),
-                new OrderLineCommand(2, "볼펜", 1000, 1));
+                new OrderLineCommand(1, 2),
+                new OrderLineCommand(2, 1));
         long orderId = orderService.placeOrder(new PlaceOrderCommand(1, orderLines, "서울시 강남구"));
         Order order = orderService.findOrder(orderId);
         Member member = memberRepository.findById(order.getMemberId()).orElseThrow();
@@ -59,7 +61,7 @@ public class Main {
         }
 
         long order2Id = orderService.placeOrder(new PlaceOrderCommand(2,
-                List.of(new OrderLineCommand(1, "자바 책", 15000, 1)), "부산시 해운대구"));
+                List.of(new OrderLineCommand(1, 1)), "부산시 해운대구"));
         try {
             orderService.ship(order2Id);
             System.out.println("결제 없이 배송: " + orderService.findOrder(order2Id).getStatus().label());
@@ -68,8 +70,9 @@ public class Main {
         }
 
         try {
+            productRepository.save(new Product(4, "할인 책", new Money(-15000)));
             long order3Id = orderService.placeOrder(new PlaceOrderCommand(2,
-                    List.of(new OrderLineCommand(1, "자바 책", -15000, 2)), "대구시 수성구"));
+                    List.of(new OrderLineCommand(4, 2)), "대구시 수성구"));
             System.out.println("음수 금액 주문: " + orderService.findOrder(order3Id).getTotalPrice().amount() + "원");
         } catch (IllegalArgumentException e) {
             System.out.println("음수 금액 주문: " + e.getMessage());
@@ -77,23 +80,23 @@ public class Main {
 
         try {
             long order4Id = orderService.placeOrder(new PlaceOrderCommand(2,
-                    List.of(new OrderLineCommand(1, "자바 책", 15000, 1)), ""));
+                    List.of(new OrderLineCommand(1, 1)), ""));
             System.out.println("빈 주소 주문: " + orderService.findOrder(order4Id).getStatus().label());
         } catch (IllegalArgumentException e) {
             System.out.println("빈 주소 주문: " + e.getMessage());
         }
 
         long order5Id = orderService.placeOrder(new PlaceOrderCommand(2, List.of(
-                new OrderLineCommand(1, "자바 책", 15000, 2),
-                new OrderLineCommand(2, "볼펜", 1000, 1)), "광주시 북구"));
-        orderService.addOrderLine(order5Id, new OrderLineCommand(3, "공책", 2000, 5));
+                new OrderLineCommand(1, 2),
+                new OrderLineCommand(2, 1)), "광주시 북구"));
+        orderService.addOrderLine(order5Id, new OrderLineCommand(3, 5));
         Order order5 = orderService.findOrder(order5Id);
         System.out.println("항목 더하기: 항목 " + order5.getOrderLines().size() + "개, 주문 금액 "
                 + order5.getTotalPrice().amount() + "원");
 
         orderService.pay(order5Id);
         try {
-            orderService.addOrderLine(order5Id, new OrderLineCommand(3, "공책", 2000, 1));
+            orderService.addOrderLine(order5Id, new OrderLineCommand(3, 1));
             System.out.println("결제 뒤 항목 더하기: 항목 " + orderService.findOrder(order5Id).getOrderLines().size() + "개");
         } catch (IllegalStateException e) {
             System.out.println("결제 뒤 항목 더하기: " + e.getMessage());
@@ -108,7 +111,7 @@ public class Main {
 
         try {
             long order7Id = orderService.placeOrder(new PlaceOrderCommand(2,
-                    List.of(new OrderLineCommand(1, "자바 책", 15000, 0)), "대전시 유성구"));
+                    List.of(new OrderLineCommand(1, 0)), "대전시 유성구"));
             System.out.println("0개 항목 주문: " + orderService.findOrder(order7Id).getTotalPrice().amount() + "원");
         } catch (IllegalArgumentException e) {
             System.out.println("0개 항목 주문: " + e.getMessage());
diff --git a/ddd/src/ddd/application/OrderApplicationService.java b/ddd/src/ddd/application/OrderApplicationService.java
index 7e77fe6..135cbc0 100644
--- a/ddd/src/ddd/application/OrderApplicationService.java
+++ b/ddd/src/ddd/application/OrderApplicationService.java
@@ -4,24 +4,26 @@ import java.util.ArrayList;
 import java.util.List;
 
 import ddd.domain.Address;
-import ddd.domain.Money;
 import ddd.domain.Order;
 import ddd.domain.OrderLine;
+import ddd.domain.OrderLineFactory;
 import ddd.domain.OrderRepository;
 
 public class OrderApplicationService {
 
     private final OrderRepository orderRepository;
+    private final OrderLineFactory orderLineFactory;
     private long nextId = 1;
 
-    public OrderApplicationService(OrderRepository orderRepository) {
+    public OrderApplicationService(OrderRepository orderRepository, OrderLineFactory orderLineFactory) {
         this.orderRepository = orderRepository;
+        this.orderLineFactory = orderLineFactory;
     }
 
     public long placeOrder(PlaceOrderCommand command) {
         List<OrderLine> orderLines = new ArrayList<>();
         for (OrderLineCommand orderLineCommand : command.orderLines()) {
-            orderLines.add(toOrderLine(orderLineCommand));
+            orderLines.add(orderLineFactory.create(orderLineCommand.productId(), orderLineCommand.quantity()));
         }
         Order order = new Order(nextId++, command.memberId(), orderLines, new Address(command.address()));
         orderRepository.save(order);
@@ -34,7 +36,7 @@ public class OrderApplicationService {
 
     public void addOrderLine(long orderId, OrderLineCommand command) {
         Order order = findOrder(orderId);
-        order.addOrderLine(toOrderLine(command));
+        order.addOrderLine(orderLineFactory.create(command.productId(), command.quantity()));
         orderRepository.save(order);
     }
 
@@ -61,8 +63,4 @@ public class OrderApplicationService {
         order.cancel();
         orderRepository.save(order);
     }
-
-    private OrderLine toOrderLine(OrderLineCommand command) {
-        return new OrderLine(command.productId(), command.productName(), new Money(command.price()), command.quantity());
-    }
 }
diff --git a/ddd/src/ddd/application/OrderLineCommand.java b/ddd/src/ddd/application/OrderLineCommand.java
index 0d16766..1be8bc9 100644
--- a/ddd/src/ddd/application/OrderLineCommand.java
+++ b/ddd/src/ddd/application/OrderLineCommand.java
@@ -1,4 +1,4 @@
 package ddd.application;
 
-public record OrderLineCommand(long productId, String productName, int price, int quantity) {
+public record OrderLineCommand(long productId, int quantity) {
 }
```

## T2 없는 상품으로 주문 막기
커밋: a85a1b1

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/domain/OrderLineFactory.java | 고침 |

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 3aaab0a..b847513 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -134,6 +134,14 @@ public class Main {
             System.out.println("없는 주문 결제: " + e.getMessage());
         }
 
+        try {
+            long order8Id = orderService.placeOrder(new PlaceOrderCommand(2,
+                    List.of(new OrderLineCommand(100, 1)), "울산시 남구"));
+            System.out.println("없는 상품 주문: " + orderService.findOrder(order8Id).getStatus().label());
+        } catch (IllegalArgumentException e) {
+            System.out.println("없는 상품 주문: " + e.getMessage());
+        }
+
         Order notSaved = orderService.findOrder(order2Id);
         notSaved.cancel();
         System.out.println("저장 안 한 취소: " + orderService.findOrder(order2Id).getStatus().label());
diff --git a/ddd/src/ddd/domain/OrderLineFactory.java b/ddd/src/ddd/domain/OrderLineFactory.java
index 7cd6114..02fd86d 100644
--- a/ddd/src/ddd/domain/OrderLineFactory.java
+++ b/ddd/src/ddd/domain/OrderLineFactory.java
@@ -9,7 +9,8 @@ public class OrderLineFactory {
     }
 
     public OrderLine create(long productId, int quantity) {
-        Product product = productRepository.findById(productId).orElseThrow();
+        Product product = productRepository.findById(productId)
+                .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다"));
         return new OrderLine(product.getId(), product.getName(), product.getPrice(), quantity);
     }
 }
```
