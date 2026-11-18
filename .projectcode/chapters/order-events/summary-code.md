# order-events 코드 요약

현재 따라치기 이력에 고침을 녹인 코드다.
Source는 원래 task와 반영한 fix 커밋이다.

## T1 상품에 재고 더하기
커밋: d5b77ce
Source: 8148820 a312669 170e451

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/order/domain/Product.java | 고침 |

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index a36056c..02208be 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -30,7 +30,7 @@
 | 주문 항목 요청 | OrderItemRequest | 주문 항목을 만들 상품 번호, 수량 묶음 |
 | 회원 | Member | 상품을 주문하는 사람. 회원 번호, 이름, 회원 등급을 가진다. 회원 번호가 같으면 같은 회원이다 |
 | 회원 저장소 | MemberRepository | 회원을 넣어 두고 회원 번호로 다시 꺼내는 곳 |
-| 상품 | Product | 파는 물건. 상품 번호, 이름, 가격을 가진다. 상품 번호가 같으면 같은 상품이다 |
+| 상품 | Product | 파는 물건. 상품 번호, 이름, 가격, 재고를 가진다. 상품 번호가 같으면 같은 상품이다 |
 | 상품 저장소 | ProductRepository | 상품을 넣어 두고 상품 번호로 다시 꺼내는 곳 |
 | 상품 번호 | productId | 주문 항목이 가리키는 상품의 번호. 주문 항목은 상품을 객체로 갖지 않고 이 번호로 가리킨다 |
 | 주문 항목 팩토리 | OrderItemFactory | 상품 번호와 수량을 받아 상품 저장소에서 상품을 찾고, 그 상품의 이름과 지금 가격으로 주문 항목을 만드는 곳 |
@@ -42,3 +42,4 @@
 | 결제 금액 | paymentPrice | 주문 금액에서 할인 금액을 뺀 돈. 실제로 치르는 돈이다 |
 | 무료 배송 명세 | FreeShippingSpecification | 결제 금액이 50000원 이상이고 주소가 제주로 시작하지 않는지 참 거짓으로 답한다 |
 | 배송비 | shippingFee | 주문을 보내는 데 받는 돈. 결제 금액과 따로 받는다. 무료 배송 명세를 만족하면 0원, 아니면 3000원이다 |
+| 재고 | stock | 상품이 지금 몇 개 남았는지 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 6b2007f..ec3e68f 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -20,39 +20,25 @@ import ddd.order.infrastructure.MemoryProductRepository;
 public class Main {
 
     public static void main(String[] args) {
-        System.out.println("===== 무료 배송 조건 =====");
+        System.out.println("===== 주문 뒤 재고 줄이기 =====");
 
         MemberRepository memberRepository = new MemoryMemberRepository();
         memberRepository.save(new Member(1, "김철수", MemberGrade.BASIC));
 
         ProductRepository productRepository = new MemoryProductRepository();
-        productRepository.save(new Product(1, "자바 책", 15000));
+        productRepository.save(new Product(1, "자바 책", 15000, 10));
 
         OrderService orderService = new OrderService(new MemoryOrderRepository(),
                 new OrderItemFactory(productRepository), memberRepository, new DiscountPolicy());
 
         System.out.println();
-        System.out.println("[결제 금액으로 배송비 정하기]");
-        long smallOrderId = orderService.placeOrder(new OrderRequest(1,
+        System.out.println("[주문과 재고 확인하기]");
+        int stockBefore = productRepository.findById(1).orElseThrow().getStock();
+        long orderId = orderService.placeOrder(new OrderRequest(1,
                 List.of(new OrderItemRequest(1, 2)), "서울시 강남구"));
-        Order smallOrder = orderService.findOrder(smallOrderId);
-        System.out.println(smallOrder.getPaymentPrice() + "원 주문 배송비: " + smallOrder.getShippingFee() + "원");
-        long bigOrderId = orderService.placeOrder(new OrderRequest(1,
-                List.of(new OrderItemRequest(1, 4)), "서울시 강남구"));
-        Order bigOrder = orderService.findOrder(bigOrderId);
-        System.out.println(bigOrder.getPaymentPrice() + "원 주문 배송비: " + bigOrder.getShippingFee() + "원");
-
-        System.out.println();
-        System.out.println("[제주 주소는 무료 배송에서 빼기]");
-        long seoulOrderId = orderService.placeOrder(new OrderRequest(1,
-                List.of(new OrderItemRequest(1, 4)), "서울시 마포구"));
-        Order seoulOrder = orderService.findOrder(seoulOrderId);
-        System.out.println(seoulOrder.getAddress().value() + " " + seoulOrder.getPaymentPrice() + "원 주문 배송비: "
-                + seoulOrder.getShippingFee() + "원");
-        long jejuOrderId = orderService.placeOrder(new OrderRequest(1,
-                List.of(new OrderItemRequest(1, 4)), "제주시 연동"));
-        Order jejuOrder = orderService.findOrder(jejuOrderId);
-        System.out.println(jejuOrder.getAddress().value() + " " + jejuOrder.getPaymentPrice() + "원 주문 배송비: "
-                + jejuOrder.getShippingFee() + "원");
+        Order order = orderService.findOrder(orderId);
+        int stockAfterOrder = productRepository.findById(1).orElseThrow().getStock();
+        System.out.println("자바 책 2개 주문: " + orderId + "번 주문, " + order.getStatus().label());
+        System.out.println("자바 책 재고: " + stockBefore + "개 -> " + stockAfterOrder + "개");
     }
 }
diff --git a/ddd/src/ddd/order/domain/Product.java b/ddd/src/ddd/order/domain/Product.java
index b06240d..ae7e950 100644
--- a/ddd/src/ddd/order/domain/Product.java
+++ b/ddd/src/ddd/order/domain/Product.java
@@ -5,11 +5,13 @@ public class Product {
     private final long id;
     private final String name;
     private final int price;
+    private int stock;
 
-    public Product(long id, String name, int price) {
+    public Product(long id, String name, int price, int stock) {
         this.id = id;
         this.name = name;
         this.price = price;
+        this.stock = stock;
     }
 
     public long getId() {
@@ -24,6 +26,10 @@ public class Product {
         return price;
     }
 
+    public int getStock() {
+        return stock;
+    }
+
     @Override
     public boolean equals(Object o) {
         if (!(o instanceof Product other)) {
```

## T2 주문 저장 뒤 상품 재고 바로 줄이기
커밋: 428e684
Source: 388255f 3d27ec7 a312669 170e451

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/order/application/OrderService.java | 고침 |
| ddd/src/ddd/order/domain/Product.java | 고침 |

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index 02208be..ff954d0 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -42,4 +42,5 @@
 | 결제 금액 | paymentPrice | 주문 금액에서 할인 금액을 뺀 돈. 실제로 치르는 돈이다 |
 | 무료 배송 명세 | FreeShippingSpecification | 결제 금액이 50000원 이상이고 주소가 제주로 시작하지 않는지 참 거짓으로 답한다 |
 | 배송비 | shippingFee | 주문을 보내는 데 받는 돈. 결제 금액과 따로 받는다. 무료 배송 명세를 만족하면 0원, 아니면 3000원이다 |
-| 재고 | stock | 상품이 지금 몇 개 남았는지 |
+| 재고 | stock | 상품이 지금 몇 개 남았는지. 주문하기가 끝나기 전에 주문 수량만큼 줄어든다 |
+| 재고 줄이기 | decreaseStock | 상품 재고를 주문 수량만큼 뺀다 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index ec3e68f..a87d514 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -29,10 +29,10 @@ public class Main {
         productRepository.save(new Product(1, "자바 책", 15000, 10));
 
         OrderService orderService = new OrderService(new MemoryOrderRepository(),
-                new OrderItemFactory(productRepository), memberRepository, new DiscountPolicy());
+                new OrderItemFactory(productRepository), memberRepository, new DiscountPolicy(), productRepository);
 
         System.out.println();
-        System.out.println("[주문과 재고 확인하기]");
+        System.out.println("[주문 저장 후 재고 줄이기]");
         int stockBefore = productRepository.findById(1).orElseThrow().getStock();
         long orderId = orderService.placeOrder(new OrderRequest(1,
                 List.of(new OrderItemRequest(1, 2)), "서울시 강남구"));
@@ -40,5 +40,6 @@ public class Main {
         int stockAfterOrder = productRepository.findById(1).orElseThrow().getStock();
         System.out.println("자바 책 2개 주문: " + orderId + "번 주문, " + order.getStatus().label());
         System.out.println("자바 책 재고: " + stockBefore + "개 -> " + stockAfterOrder + "개");
+
     }
 }
diff --git a/ddd/src/ddd/order/application/OrderService.java b/ddd/src/ddd/order/application/OrderService.java
index 46d74e1..ce2e862 100644
--- a/ddd/src/ddd/order/application/OrderService.java
+++ b/ddd/src/ddd/order/application/OrderService.java
@@ -3,6 +3,8 @@ package ddd.order.application;
 import java.util.ArrayList;
 import java.util.List;
 
+import ddd.order.domain.Product;
+import ddd.order.domain.ProductRepository;
 import ddd.order.domain.Address;
 import ddd.order.domain.DiscountPolicy;
 import ddd.order.domain.Member;
@@ -18,14 +20,16 @@ public class OrderService {
     private final OrderItemFactory orderItemFactory;
     private final MemberRepository memberRepository;
     private final DiscountPolicy discountPolicy;
+    private final ProductRepository productRepository;
     private long nextId = 1;
 
     public OrderService(OrderRepository orderRepository, OrderItemFactory orderItemFactory,
-            MemberRepository memberRepository, DiscountPolicy discountPolicy) {
+            MemberRepository memberRepository, DiscountPolicy discountPolicy, ProductRepository productRepository) {
         this.orderRepository = orderRepository;
         this.orderItemFactory = orderItemFactory;
         this.memberRepository = memberRepository;
         this.discountPolicy = discountPolicy;
+        this.productRepository = productRepository;
     }
 
     public long placeOrder(OrderRequest request) {
@@ -35,6 +39,11 @@ public class OrderService {
         }
         Order order = new Order(nextId++, request.memberId(), items, new Address(request.address()));
         orderRepository.save(order);
+        for (OrderItem item : order.getItems()) {
+            Product product = productRepository.findById(item.getProductId()).orElseThrow();
+            product.decreaseStock(item.getQuantity());
+            productRepository.save(product);
+        }
         return order.getId();
     }
 
diff --git a/ddd/src/ddd/order/domain/Product.java b/ddd/src/ddd/order/domain/Product.java
index ae7e950..d375797 100644
--- a/ddd/src/ddd/order/domain/Product.java
+++ b/ddd/src/ddd/order/domain/Product.java
@@ -30,6 +30,10 @@ public class Product {
         return stock;
     }
 
+    public void decreaseStock(int quantity) {
+        stock = stock - quantity;
+    }
+
     @Override
     public boolean equals(Object o) {
         if (!(o instanceof Product other)) {
```

## T5 이벤트 대신 주문 뒤 재고 바로 줄이기
커밋: 170e451
- 위 task에 고침을 녹였다. 학생에게 별도 수정 단계로 보이지 않는다
- done 태그는 옮기지 않고 fixes patch로 수정된 장 끝 코드를 재현한다
