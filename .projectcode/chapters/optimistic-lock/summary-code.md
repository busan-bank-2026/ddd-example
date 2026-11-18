# optimistic-lock 코드 요약

현재 따라치기 이력에 고침을 녹인 코드다.
Source는 원래 task와 반영한 fix 커밋이다.

## T1 같은 주문을 두 번 꺼내 덮어쓰기
커밋: c82bd61
Source: 001c774 a312669 170e451

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/Main.java | 고침 |

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index a87d514..dd82af2 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -11,6 +11,7 @@ import ddd.order.domain.MemberGrade;
 import ddd.order.domain.MemberRepository;
 import ddd.order.domain.Order;
 import ddd.order.domain.OrderItemFactory;
+import ddd.order.domain.OrderRepository;
 import ddd.order.domain.Product;
 import ddd.order.domain.ProductRepository;
 import ddd.order.infrastructure.MemoryMemberRepository;
@@ -20,7 +21,7 @@ import ddd.order.infrastructure.MemoryProductRepository;
 public class Main {
 
     public static void main(String[] args) {
-        System.out.println("===== 주문 뒤 재고 줄이기 =====");
+        System.out.println("===== 동시 수정 막기 =====");
 
         MemberRepository memberRepository = new MemoryMemberRepository();
         memberRepository.save(new Member(1, "김철수", MemberGrade.BASIC));
@@ -28,18 +29,25 @@ public class Main {
         ProductRepository productRepository = new MemoryProductRepository();
         productRepository.save(new Product(1, "자바 책", 15000, 10));
 
-        OrderService orderService = new OrderService(new MemoryOrderRepository(),
+        OrderRepository orderRepository = new MemoryOrderRepository();
+        OrderService orderService = new OrderService(orderRepository,
                 new OrderItemFactory(productRepository), memberRepository, new DiscountPolicy(), productRepository);
 
         System.out.println();
-        System.out.println("[주문 저장 후 재고 줄이기]");
-        int stockBefore = productRepository.findById(1).orElseThrow().getStock();
+        System.out.println("[같은 주문을 두 번 꺼내 저장하기]");
         long orderId = orderService.placeOrder(new OrderRequest(1,
                 List.of(new OrderItemRequest(1, 2)), "서울시 강남구"));
-        Order order = orderService.findOrder(orderId);
-        int stockAfterOrder = productRepository.findById(1).orElseThrow().getStock();
-        System.out.println("자바 책 2개 주문: " + orderId + "번 주문, " + order.getStatus().label());
-        System.out.println("자바 책 재고: " + stockBefore + "개 -> " + stockAfterOrder + "개");
-
+        orderService.pay(orderId);
+        Order first = orderService.findOrder(orderId);
+        Order second = orderService.findOrder(orderId);
+        String statusBefore = first.getStatus().label();
+        first.ship();
+        orderRepository.save(first);
+        String statusAfterShip = orderService.findOrder(orderId).getStatus().label();
+        System.out.println("첫째로 꺼내 배송: " + statusBefore + " -> " + statusAfterShip);
+        second.cancel();
+        orderRepository.save(second);
+        System.out.println("둘째로 꺼내 취소: " + statusAfterShip + " -> "
+                + orderService.findOrder(orderId).getStatus().label());
     }
 }
```

## T2 저장할 때마다 주문 버전 올리기
커밋: 57f682b
Source: 1c6f7bb a312669 170e451

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/order/domain/Order.java | 고침 |
| ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java | 고침 |

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index ff954d0..d09dd86 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -25,6 +25,7 @@
 | 주문 저장소 | OrderRepository | 주문을 넣어 두고 주문 번호로 다시 꺼내는 곳 |
 | 저장하기 | save | 주문을 주문 저장소에 넣는다. 같은 번호가 있으면 새 주문으로 바꾼다 |
 | 주문 번호로 찾기 | findById | 주문 번호로 주문 저장소에서 주문을 꺼낸다. 없으면 빈 결과다 |
+| 버전 | version | 주문이 주문 저장소에 몇 번 저장됐는지. 새 주문은 0이고 저장할 때마다 1씩 오른다. 꺼낸 주문은 저장된 버전을 가진다 |
 | 주문 응용 서비스 | OrderService | 주문 유스케이스를 하는 곳. 주문을 꺼내고, 주문에게 시키고, 저장한다. 규칙은 갖지 않는다 |
 | 주문 요청 | OrderRequest | 주문하기에 넣는 회원 번호, 주문 항목 요청 목록, 배송 주소 묶음 |
 | 주문 항목 요청 | OrderItemRequest | 주문 항목을 만들 상품 번호, 수량 묶음 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index dd82af2..a5d41d4 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -49,5 +49,13 @@ public class Main {
         orderRepository.save(second);
         System.out.println("둘째로 꺼내 취소: " + statusAfterShip + " -> "
                 + orderService.findOrder(orderId).getStatus().label());
+
+        System.out.println();
+        System.out.println("[저장할 때마다 버전 올리기]");
+        long newOrderId = orderService.placeOrder(new OrderRequest(1,
+                List.of(new OrderItemRequest(1, 1)), "서울시 강남구"));
+        long versionBefore = orderService.findOrder(newOrderId).getVersion();
+        orderService.pay(newOrderId);
+        System.out.println("결제하고 저장: 버전 " + versionBefore + " -> " + orderService.findOrder(newOrderId).getVersion());
     }
 }
diff --git a/ddd/src/ddd/order/domain/Order.java b/ddd/src/ddd/order/domain/Order.java
index 1dd6192..e277d08 100644
--- a/ddd/src/ddd/order/domain/Order.java
+++ b/ddd/src/ddd/order/domain/Order.java
@@ -12,6 +12,7 @@ public class Order {
     private int totalPrice;
     private int discountPrice;
     private OrderStatus status;
+    private long version;
 
     public Order(long id, long memberId, List<OrderItem> items, Address address) {
         if (items.isEmpty()) {
@@ -37,6 +38,7 @@ public class Order {
         this.totalPrice = other.totalPrice;
         this.discountPrice = other.discountPrice;
         this.status = other.status;
+        this.version = other.version;
     }
 
     public long getId() {
@@ -78,6 +80,14 @@ public class Order {
         return status;
     }
 
+    public long getVersion() {
+        return version;
+    }
+
+    public void increaseVersion() {
+        version++;
+    }
+
     public void addItem(OrderItem item) {
         if (status != OrderStatus.ORDERED) {
             throw new IllegalStateException("주문 접수인 주문에만 항목을 더할 수 있습니다");
diff --git a/ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java b/ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java
index cffbd2b..33cb9c0 100644
--- a/ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java
+++ b/ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java
@@ -13,6 +13,7 @@ public class MemoryOrderRepository implements OrderRepository {
 
     @Override
     public void save(Order order) {
+        order.increaseVersion();
         orders.put(order.getId(), new Order(order));
     }
```

## T3 버전이 다르면 저장 거절하기
커밋: 0afaada
Source: 2a1cb4e a312669 170e451

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java | 고침 |

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index d09dd86..03bb81e 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -23,7 +23,7 @@
 | 배송 완료 | DELIVERED | 배송 완료하기를 마친 상태. 더 바뀌지 않는다 |
 | 취소 | CANCELED | 주문 취소하기를 마친 상태 |
 | 주문 저장소 | OrderRepository | 주문을 넣어 두고 주문 번호로 다시 꺼내는 곳 |
-| 저장하기 | save | 주문을 주문 저장소에 넣는다. 같은 번호가 있으면 새 주문으로 바꾼다 |
+| 저장하기 | save | 주문을 주문 저장소에 넣는다. 같은 번호가 있으면 새 주문으로 바꾼다. 저장된 주문과 버전이 다르면 다른 곳에서 먼저 바꾼 주문이라 거절한다 |
 | 주문 번호로 찾기 | findById | 주문 번호로 주문 저장소에서 주문을 꺼낸다. 없으면 빈 결과다 |
 | 버전 | version | 주문이 주문 저장소에 몇 번 저장됐는지. 새 주문은 0이고 저장할 때마다 1씩 오른다. 꺼낸 주문은 저장된 버전을 가진다 |
 | 주문 응용 서비스 | OrderService | 주문 유스케이스를 하는 곳. 주문을 꺼내고, 주문에게 시키고, 저장한다. 규칙은 갖지 않는다 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index a5d41d4..5d55141 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -45,10 +45,15 @@ public class Main {
         orderRepository.save(first);
         String statusAfterShip = orderService.findOrder(orderId).getStatus().label();
         System.out.println("첫째로 꺼내 배송: " + statusBefore + " -> " + statusAfterShip);
-        second.cancel();
-        orderRepository.save(second);
-        System.out.println("둘째로 꺼내 취소: " + statusAfterShip + " -> "
-                + orderService.findOrder(orderId).getStatus().label());
+        try {
+            second.cancel();
+            orderRepository.save(second);
+            System.out.println("둘째로 꺼내 취소: " + statusAfterShip + " -> "
+                    + orderService.findOrder(orderId).getStatus().label());
+        } catch (IllegalStateException e) {
+            System.out.println("둘째로 꺼내 취소: " + e.getMessage());
+        }
+        System.out.println("저장된 주문 상태: " + orderService.findOrder(orderId).getStatus().label());
 
         System.out.println();
         System.out.println("[저장할 때마다 버전 올리기]");
diff --git a/ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java b/ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java
index 33cb9c0..b56e377 100644
--- a/ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java
+++ b/ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java
@@ -13,6 +13,10 @@ public class MemoryOrderRepository implements OrderRepository {
 
     @Override
     public void save(Order order) {
+        Order saved = orders.get(order.getId());
+        if (saved != null && saved.getVersion() != order.getVersion()) {
+            throw new IllegalStateException("다른 곳에서 먼저 바꾼 주문입니다");
+        }
         order.increaseVersion();
         orders.put(order.getId(), new Order(order));
     }
```

## T5 직접 재고 감소 흐름에서 낙관적 잠금 유지하기
커밋: 170e451
- 위 task에 고침을 녹였다. 학생에게 별도 수정 단계로 보이지 않는다
- done 태그는 옮기지 않고 fixes patch로 수정된 장 끝 코드를 재현한다
