# optimistic-lock 코드 요약

## T1 같은 주문을 두 번 꺼내 덮어쓰기
커밋: 001c774

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/Main.java | 고침 |

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index d31bbb4..a8b1db1 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -16,6 +16,7 @@ import ddd.domain.Order;
 import ddd.domain.OrderLine;
 import ddd.domain.OrderLineFactory;
 import ddd.domain.OrderPlaced;
+import ddd.domain.OrderRepository;
 import ddd.domain.Product;
 import ddd.domain.ProductRepository;
 import ddd.infrastructure.MemoryMemberRepository;
@@ -39,7 +40,8 @@ public class Main {
         EventPublisher eventPublisher = new EventPublisher();
         DecreaseStockHandler decreaseStockHandler = new DecreaseStockHandler(productRepository);
         eventPublisher.subscribe(OrderPlaced.class, decreaseStockHandler::handle);
-        OrderApplicationService orderService = new OrderApplicationService(new MemoryOrderRepository(),
+        OrderRepository orderRepository = new MemoryOrderRepository();
+        OrderApplicationService orderService = new OrderApplicationService(orderRepository,
                 new OrderLineFactory(productRepository), memberRepository, new DiscountPolicy(), eventPublisher);
 
         List<OrderLineCommand> orderLines = List.of(
@@ -175,5 +177,13 @@ public class Main {
         Product book = productRepository.findById(bookLine.productId()).orElseThrow();
         System.out.println("가격 바뀐 뒤: " + book.getId() + "번 상품 " + book.getPrice().amount() + "원, 주문 항목 "
                 + bookLine.price().amount() + "원, 주문 금액 " + firstOrder.getTotalPrice().amount() + "원");
+
+        Order orderA = orderService.findOrder(order5Id);
+        Order orderB = orderService.findOrder(order5Id);
+        orderA.ship();
+        orderRepository.save(orderA);
+        orderB.cancel();
+        orderRepository.save(orderB);
+        System.out.println("동시 수정: " + orderService.findOrder(order5Id).getStatus().label());
     }
 }
```

## T2 저장할 때마다 주문 버전 올리기
커밋: 1c6f7bb

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/domain/Order.java | 고침 |
| ddd/src/ddd/infrastructure/MemoryOrderRepository.java | 고침 |

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index b9b8532..0154b5f 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -26,6 +26,7 @@
 | 주문 저장소 | OrderRepository | 주문을 넣어 두고 주문 번호로 다시 꺼내는 곳 |
 | 저장하기 | save | 주문을 주문 저장소에 넣는다. 같은 번호가 있으면 새 주문으로 바꾼다 |
 | 주문 번호로 찾기 | findById | 주문 번호로 주문 저장소에서 주문을 꺼낸다. 없으면 빈 결과다 |
+| 버전 | version | 주문이 주문 저장소에 몇 번 저장됐는지. 새 주문은 0이고 저장할 때마다 1씩 오른다. 꺼낸 주문은 저장된 버전을 가진다 |
 | 주문 응용 서비스 | OrderApplicationService | 주문 유스케이스를 하는 곳. 주문을 꺼내고, 주문에게 시키고, 저장한다. 규칙은 갖지 않는다 |
 | 주문하기 명령 | PlaceOrderCommand | 주문하기에 넣는 회원 번호, 주문 항목 명령 목록, 배송 주소 묶음 |
 | 주문 항목 명령 | OrderLineCommand | 주문 항목을 만들 상품 번호, 수량 묶음 |
diff --git a/ddd/src/ddd/domain/Order.java b/ddd/src/ddd/domain/Order.java
index 843e359..91b39ac 100644
--- a/ddd/src/ddd/domain/Order.java
+++ b/ddd/src/ddd/domain/Order.java
@@ -16,6 +16,7 @@ public class Order implements Serializable {
     private Money discountPrice;
     private OrderStatus status;
     private List<Object> events;
+    private long version;
 
     public Order(long id, long memberId, List<OrderLine> orderLines, Address address) {
         if (orderLines.isEmpty()) {
@@ -78,6 +79,14 @@ public class Order implements Serializable {
         return List.copyOf(events);
     }
 
+    public long getVersion() {
+        return version;
+    }
+
+    public void increaseVersion() {
+        version++;
+    }
+
     public void addOrderLine(OrderLine orderLine) {
         if (status != OrderStatus.ORDERED) {
             throw new IllegalStateException("주문 접수인 주문에만 항목을 더할 수 있습니다");
diff --git a/ddd/src/ddd/infrastructure/MemoryOrderRepository.java b/ddd/src/ddd/infrastructure/MemoryOrderRepository.java
index 94cb6a0..6375bd6 100644
--- a/ddd/src/ddd/infrastructure/MemoryOrderRepository.java
+++ b/ddd/src/ddd/infrastructure/MemoryOrderRepository.java
@@ -18,6 +18,7 @@ public class MemoryOrderRepository implements OrderRepository {
 
     @Override
     public void save(Order order) {
+        order.increaseVersion();
         orders.put(order.getId(), copy(order));
     }
 
```

## T3 버전이 다르면 저장 거절하기
커밋: 2a1cb4e

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/infrastructure/MemoryOrderRepository.java | 고침 |

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index 0154b5f..54306d8 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -24,7 +24,7 @@
 | 배송 완료 | DELIVERED | 배송 완료하기를 마친 상태. 더 바뀌지 않는다 |
 | 취소 | CANCELED | 주문 취소하기를 마친 상태 |
 | 주문 저장소 | OrderRepository | 주문을 넣어 두고 주문 번호로 다시 꺼내는 곳 |
-| 저장하기 | save | 주문을 주문 저장소에 넣는다. 같은 번호가 있으면 새 주문으로 바꾼다 |
+| 저장하기 | save | 주문을 주문 저장소에 넣는다. 같은 번호가 있으면 새 주문으로 바꾼다. 저장된 주문과 버전이 다르면 다른 곳에서 먼저 바꾼 주문이라 거절한다 |
 | 주문 번호로 찾기 | findById | 주문 번호로 주문 저장소에서 주문을 꺼낸다. 없으면 빈 결과다 |
 | 버전 | version | 주문이 주문 저장소에 몇 번 저장됐는지. 새 주문은 0이고 저장할 때마다 1씩 오른다. 꺼낸 주문은 저장된 버전을 가진다 |
 | 주문 응용 서비스 | OrderApplicationService | 주문 유스케이스를 하는 곳. 주문을 꺼내고, 주문에게 시키고, 저장한다. 규칙은 갖지 않는다 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index a8b1db1..6f7982b 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -182,8 +182,12 @@ public class Main {
         Order orderB = orderService.findOrder(order5Id);
         orderA.ship();
         orderRepository.save(orderA);
-        orderB.cancel();
-        orderRepository.save(orderB);
-        System.out.println("동시 수정: " + orderService.findOrder(order5Id).getStatus().label());
+        try {
+            orderB.cancel();
+            orderRepository.save(orderB);
+            System.out.println("동시 수정: " + orderService.findOrder(order5Id).getStatus().label());
+        } catch (IllegalStateException e) {
+            System.out.println("동시 수정: " + e.getMessage());
+        }
     }
 }
diff --git a/ddd/src/ddd/infrastructure/MemoryOrderRepository.java b/ddd/src/ddd/infrastructure/MemoryOrderRepository.java
index 6375bd6..4eb0106 100644
--- a/ddd/src/ddd/infrastructure/MemoryOrderRepository.java
+++ b/ddd/src/ddd/infrastructure/MemoryOrderRepository.java
@@ -18,6 +18,10 @@ public class MemoryOrderRepository implements OrderRepository {
 
     @Override
     public void save(Order order) {
+        Order saved = orders.get(order.getId());
+        if (saved != null && saved.getVersion() != order.getVersion()) {
+            throw new IllegalStateException("다른 곳에서 먼저 바꾼 주문입니다");
+        }
         order.increaseVersion();
         orders.put(order.getId(), copy(order));
     }
```
