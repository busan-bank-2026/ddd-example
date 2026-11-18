# bounded-contexts 코드 요약

현재 따라치기 이력에 고침을 녹인 코드다.
Source는 원래 task와 반영한 fix 커밋이다.

## T1 하위 도메인과 컨텍스트 정하기
커밋: f6abcd3
Source: 8a46784 a312669 170e451

| 파일 | 변경 |
|---|---|
| ddd/context-map.md | 새로 |
| ddd/src/ddd/Main.java | 고침 |

```diff
diff --git a/ddd/context-map.md b/ddd/context-map.md
new file mode 100644
index 0000000..0593b48
--- /dev/null
+++ b/ddd/context-map.md
@@ -0,0 +1,16 @@
+# 컨텍스트 맵
+
+## 하위 도메인
+
+| 하위 도메인 | 종류 | 왜 | 바운디드 컨텍스트 | 묶음 |
+|---|---|---|---|---|
+| 주문 | 핵심 | 주문, 결제, 배송, 취소 규칙이 이 가게가 잘해야 하는 일이다 | 주문 컨텍스트 | ddd.order |
+| 상품 | 지원 | 주문에 쓸 상품과 재고를 맡는다. 가게에 맞게 직접 만든다 | 상품 컨텍스트 | ddd.catalog |
+| 회원 | 일반 | 이름과 등급만 맡는다. 어느 가게나 비슷해서 사다 써도 된다 | 회원 컨텍스트 | ddd.member |
+
+- 하위 도메인은 업무를 나눈 것이다
+- 바운디드 컨텍스트(bounded context)는 한 모델과 한 말이 통하는 코드의 경계다
+- 이 프로그램은 하위 도메인 하나에 컨텍스트 하나를 둔다
+- 컨텍스트 안에는 domain, application, infrastructure 계층을 둔다
+
+- 주문 응용 서비스는 상품 저장소에서 상품을 찾아 재고를 줄인다
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 5d55141..2b8cf6e 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -11,7 +11,6 @@ import ddd.order.domain.MemberGrade;
 import ddd.order.domain.MemberRepository;
 import ddd.order.domain.Order;
 import ddd.order.domain.OrderItemFactory;
-import ddd.order.domain.OrderRepository;
 import ddd.order.domain.Product;
 import ddd.order.domain.ProductRepository;
 import ddd.order.infrastructure.MemoryMemberRepository;
@@ -21,7 +20,7 @@ import ddd.order.infrastructure.MemoryProductRepository;
 public class Main {
 
     public static void main(String[] args) {
-        System.out.println("===== 동시 수정 막기 =====");
+        System.out.println("===== 컨텍스트 나누기 =====");
 
         MemberRepository memberRepository = new MemoryMemberRepository();
         memberRepository.save(new Member(1, "김철수", MemberGrade.BASIC));
@@ -29,38 +28,20 @@ public class Main {
         ProductRepository productRepository = new MemoryProductRepository();
         productRepository.save(new Product(1, "자바 책", 15000, 10));
 
-        OrderRepository orderRepository = new MemoryOrderRepository();
-        OrderService orderService = new OrderService(orderRepository,
+        OrderService orderService = new OrderService(new MemoryOrderRepository(),
                 new OrderItemFactory(productRepository), memberRepository, new DiscountPolicy(), productRepository);
 
         System.out.println();
-        System.out.println("[같은 주문을 두 번 꺼내 저장하기]");
+        System.out.println("[세 컨텍스트를 거쳐 주문하기]");
+        int stockBefore = productRepository.findById(1).orElseThrow().getStock();
         long orderId = orderService.placeOrder(new OrderRequest(1,
                 List.of(new OrderItemRequest(1, 2)), "서울시 강남구"));
-        orderService.pay(orderId);
-        Order first = orderService.findOrder(orderId);
-        Order second = orderService.findOrder(orderId);
-        String statusBefore = first.getStatus().label();
-        first.ship();
-        orderRepository.save(first);
-        String statusAfterShip = orderService.findOrder(orderId).getStatus().label();
-        System.out.println("첫째로 꺼내 배송: " + statusBefore + " -> " + statusAfterShip);
-        try {
-            second.cancel();
-            orderRepository.save(second);
-            System.out.println("둘째로 꺼내 취소: " + statusAfterShip + " -> "
-                    + orderService.findOrder(orderId).getStatus().label());
-        } catch (IllegalStateException e) {
-            System.out.println("둘째로 꺼내 취소: " + e.getMessage());
-        }
-        System.out.println("저장된 주문 상태: " + orderService.findOrder(orderId).getStatus().label());
-
-        System.out.println();
-        System.out.println("[저장할 때마다 버전 올리기]");
-        long newOrderId = orderService.placeOrder(new OrderRequest(1,
-                List.of(new OrderItemRequest(1, 1)), "서울시 강남구"));
-        long versionBefore = orderService.findOrder(newOrderId).getVersion();
-        orderService.pay(newOrderId);
-        System.out.println("결제하고 저장: 버전 " + versionBefore + " -> " + orderService.findOrder(newOrderId).getVersion());
+        Order order = orderService.findOrder(orderId);
+        Member member = memberRepository.findById(order.getMemberId()).orElseThrow();
+        int stockAfter = productRepository.findById(1).orElseThrow().getStock();
+        System.out.println("주문 컨텍스트: " + orderId + "번 주문, " + order.getTotalPrice() + "원, "
+                + order.getStatus().label());
+        System.out.println("회원 컨텍스트: " + member.getName() + ", " + member.getGrade());
+        System.out.println("상품 컨텍스트: 자바 책 재고 " + stockBefore + "개 -> " + stockAfter + "개");
     }
 }
```

## T3 회원 컨텍스트 만들기
커밋: 1bbc6ea
Source: e19673d a312669 170e451

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/member/domain/Member.java | 새로 |
| ddd/src/ddd/member/domain/MemberGrade.java | 새로 |
| ddd/src/ddd/member/domain/MemberRepository.java | 새로 |
| ddd/src/ddd/member/infrastructure/MemoryMemberRepository.java | 새로 |
| ddd/src/ddd/order/application/OrderService.java | 고침 |
| ddd/src/ddd/order/domain/DiscountPolicy.java | 고침 |
| ddd/src/ddd/order/domain/Member.java | 지움 |
| ddd/src/ddd/order/domain/MemberGrade.java | 지움 |
| ddd/src/ddd/order/domain/MemberRepository.java | 지움 |
| ddd/src/ddd/order/infrastructure/MemoryMemberRepository.java | 지움 |

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 2b8cf6e..a863ed5 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -2,18 +2,18 @@ package ddd;
 
 import java.util.List;
 
+import ddd.member.domain.Member;
+import ddd.member.domain.MemberGrade;
+import ddd.member.domain.MemberRepository;
+import ddd.member.infrastructure.MemoryMemberRepository;
 import ddd.order.application.OrderItemRequest;
 import ddd.order.application.OrderRequest;
 import ddd.order.application.OrderService;
 import ddd.order.domain.DiscountPolicy;
-import ddd.order.domain.Member;
-import ddd.order.domain.MemberGrade;
-import ddd.order.domain.MemberRepository;
 import ddd.order.domain.Order;
 import ddd.order.domain.OrderItemFactory;
 import ddd.order.domain.Product;
 import ddd.order.domain.ProductRepository;
-import ddd.order.infrastructure.MemoryMemberRepository;
 import ddd.order.infrastructure.MemoryOrderRepository;
 import ddd.order.infrastructure.MemoryProductRepository;
 
diff --git a/ddd/src/ddd/order/domain/Member.java b/ddd/src/ddd/member/domain/Member.java
similarity index 96%
rename from ddd/src/ddd/order/domain/Member.java
rename to ddd/src/ddd/member/domain/Member.java
index f33b745..d8938ba 100644
--- a/ddd/src/ddd/order/domain/Member.java
+++ b/ddd/src/ddd/member/domain/Member.java
@@ -1,4 +1,4 @@
-package ddd.order.domain;
+package ddd.member.domain;
 
 public class Member {
 
diff --git a/ddd/src/ddd/order/domain/MemberGrade.java b/ddd/src/ddd/member/domain/MemberGrade.java
similarity index 64%
rename from ddd/src/ddd/order/domain/MemberGrade.java
rename to ddd/src/ddd/member/domain/MemberGrade.java
index b7dae0a..3ff7824 100644
--- a/ddd/src/ddd/order/domain/MemberGrade.java
+++ b/ddd/src/ddd/member/domain/MemberGrade.java
@@ -1,4 +1,4 @@
-package ddd.order.domain;
+package ddd.member.domain;
 
 public enum MemberGrade {
     BASIC,
diff --git a/ddd/src/ddd/order/domain/MemberRepository.java b/ddd/src/ddd/member/domain/MemberRepository.java
similarity index 83%
rename from ddd/src/ddd/order/domain/MemberRepository.java
rename to ddd/src/ddd/member/domain/MemberRepository.java
index 2f1a103..0a60ef2 100644
--- a/ddd/src/ddd/order/domain/MemberRepository.java
+++ b/ddd/src/ddd/member/domain/MemberRepository.java
@@ -1,4 +1,4 @@
-package ddd.order.domain;
+package ddd.member.domain;
 
 import java.util.Optional;
 
diff --git a/ddd/src/ddd/order/infrastructure/MemoryMemberRepository.java b/ddd/src/ddd/member/infrastructure/MemoryMemberRepository.java
similarity index 79%
rename from ddd/src/ddd/order/infrastructure/MemoryMemberRepository.java
rename to ddd/src/ddd/member/infrastructure/MemoryMemberRepository.java
index cf7d00e..7380ea8 100644
--- a/ddd/src/ddd/order/infrastructure/MemoryMemberRepository.java
+++ b/ddd/src/ddd/member/infrastructure/MemoryMemberRepository.java
@@ -1,11 +1,11 @@
-package ddd.order.infrastructure;
+package ddd.member.infrastructure;
 
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Optional;
 
-import ddd.order.domain.Member;
-import ddd.order.domain.MemberRepository;
+import ddd.member.domain.Member;
+import ddd.member.domain.MemberRepository;
 
 public class MemoryMemberRepository implements MemberRepository {
 
diff --git a/ddd/src/ddd/order/application/OrderService.java b/ddd/src/ddd/order/application/OrderService.java
index ce2e862..69e2d4b 100644
--- a/ddd/src/ddd/order/application/OrderService.java
+++ b/ddd/src/ddd/order/application/OrderService.java
@@ -3,12 +3,12 @@ package ddd.order.application;
 import java.util.ArrayList;
 import java.util.List;
 
+import ddd.member.domain.Member;
+import ddd.member.domain.MemberRepository;
 import ddd.order.domain.Product;
 import ddd.order.domain.ProductRepository;
 import ddd.order.domain.Address;
 import ddd.order.domain.DiscountPolicy;
-import ddd.order.domain.Member;
-import ddd.order.domain.MemberRepository;
 import ddd.order.domain.Order;
 import ddd.order.domain.OrderItem;
 import ddd.order.domain.OrderItemFactory;
diff --git a/ddd/src/ddd/order/domain/DiscountPolicy.java b/ddd/src/ddd/order/domain/DiscountPolicy.java
index 5ddb3cc..a718d33 100644
--- a/ddd/src/ddd/order/domain/DiscountPolicy.java
+++ b/ddd/src/ddd/order/domain/DiscountPolicy.java
@@ -1,5 +1,8 @@
 package ddd.order.domain;
 
+import ddd.member.domain.Member;
+import ddd.member.domain.MemberGrade;
+
 public class DiscountPolicy {
 
     public int discount(Member member, Order order) {
```

## T4 상품 컨텍스트 만들기
커밋: 0c20331
Source: 27bbabf a312669 170e451

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/catalog/domain/Product.java | 새로 |
| ddd/src/ddd/catalog/domain/ProductRepository.java | 새로 |
| ddd/src/ddd/catalog/infrastructure/MemoryProductRepository.java | 새로 |
| ddd/src/ddd/order/application/OrderService.java | 고침 |
| ddd/src/ddd/order/domain/OrderItemFactory.java | 고침 |
| ddd/src/ddd/order/domain/Product.java | 지움 |
| ddd/src/ddd/order/domain/ProductRepository.java | 지움 |
| ddd/src/ddd/order/infrastructure/MemoryProductRepository.java | 지움 |

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index a863ed5..6949575 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -2,6 +2,9 @@ package ddd;
 
 import java.util.List;
 
+import ddd.catalog.domain.Product;
+import ddd.catalog.domain.ProductRepository;
+import ddd.catalog.infrastructure.MemoryProductRepository;
 import ddd.member.domain.Member;
 import ddd.member.domain.MemberGrade;
 import ddd.member.domain.MemberRepository;
@@ -12,10 +15,7 @@ import ddd.order.application.OrderService;
 import ddd.order.domain.DiscountPolicy;
 import ddd.order.domain.Order;
 import ddd.order.domain.OrderItemFactory;
-import ddd.order.domain.Product;
-import ddd.order.domain.ProductRepository;
 import ddd.order.infrastructure.MemoryOrderRepository;
-import ddd.order.infrastructure.MemoryProductRepository;
 
 public class Main {
 
diff --git a/ddd/src/ddd/order/domain/Product.java b/ddd/src/ddd/catalog/domain/Product.java
similarity index 96%
rename from ddd/src/ddd/order/domain/Product.java
rename to ddd/src/ddd/catalog/domain/Product.java
index d375797..71c4a96 100644
--- a/ddd/src/ddd/order/domain/Product.java
+++ b/ddd/src/ddd/catalog/domain/Product.java
@@ -1,4 +1,4 @@
-package ddd.order.domain;
+package ddd.catalog.domain;
 
 public class Product {
 
diff --git a/ddd/src/ddd/order/domain/ProductRepository.java b/ddd/src/ddd/catalog/domain/ProductRepository.java
similarity index 83%
rename from ddd/src/ddd/order/domain/ProductRepository.java
rename to ddd/src/ddd/catalog/domain/ProductRepository.java
index 0d1caa6..f39d2f6 100644
--- a/ddd/src/ddd/order/domain/ProductRepository.java
+++ b/ddd/src/ddd/catalog/domain/ProductRepository.java
@@ -1,4 +1,4 @@
-package ddd.order.domain;
+package ddd.catalog.domain;
 
 import java.util.Optional;
 
diff --git a/ddd/src/ddd/order/infrastructure/MemoryProductRepository.java b/ddd/src/ddd/catalog/infrastructure/MemoryProductRepository.java
similarity index 79%
rename from ddd/src/ddd/order/infrastructure/MemoryProductRepository.java
rename to ddd/src/ddd/catalog/infrastructure/MemoryProductRepository.java
index 9f16401..eb62220 100644
--- a/ddd/src/ddd/order/infrastructure/MemoryProductRepository.java
+++ b/ddd/src/ddd/catalog/infrastructure/MemoryProductRepository.java
@@ -1,11 +1,11 @@
-package ddd.order.infrastructure;
+package ddd.catalog.infrastructure;
 
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Optional;
 
-import ddd.order.domain.Product;
-import ddd.order.domain.ProductRepository;
+import ddd.catalog.domain.Product;
+import ddd.catalog.domain.ProductRepository;
 
 public class MemoryProductRepository implements ProductRepository {
 
diff --git a/ddd/src/ddd/order/application/OrderService.java b/ddd/src/ddd/order/application/OrderService.java
index 69e2d4b..702e2e8 100644
--- a/ddd/src/ddd/order/application/OrderService.java
+++ b/ddd/src/ddd/order/application/OrderService.java
@@ -5,8 +5,8 @@ import java.util.List;
 
 import ddd.member.domain.Member;
 import ddd.member.domain.MemberRepository;
-import ddd.order.domain.Product;
-import ddd.order.domain.ProductRepository;
+import ddd.catalog.domain.Product;
+import ddd.catalog.domain.ProductRepository;
 import ddd.order.domain.Address;
 import ddd.order.domain.DiscountPolicy;
 import ddd.order.domain.Order;
diff --git a/ddd/src/ddd/order/domain/OrderItemFactory.java b/ddd/src/ddd/order/domain/OrderItemFactory.java
index 2fb2a68..4763836 100644
--- a/ddd/src/ddd/order/domain/OrderItemFactory.java
+++ b/ddd/src/ddd/order/domain/OrderItemFactory.java
@@ -1,5 +1,8 @@
 package ddd.order.domain;
 
+import ddd.catalog.domain.Product;
+import ddd.catalog.domain.ProductRepository;
+
 public class OrderItemFactory {
 
     private final ProductRepository productRepository;
```

## T5 컨텍스트 사이에 쓰는 곳 적기
커밋: f0a5e3a
Source: ced0444 a312669 170e451

| 파일 | 변경 |
|---|---|
| ddd/context-map.md | 고침 |

```diff
diff --git a/ddd/context-map.md b/ddd/context-map.md
index 0593b48..04ab736 100644
--- a/ddd/context-map.md
+++ b/ddd/context-map.md
@@ -13,4 +13,16 @@
 - 이 프로그램은 하위 도메인 하나에 컨텍스트 하나를 둔다
 - 컨텍스트 안에는 domain, application, infrastructure 계층을 둔다
 
+## 컨텍스트 사이
+
+| 쓰는 쪽 | 쓰이는 쪽 | 무엇 |
+|---|---|---|
+| 주문 | 상품 | 주문 항목 팩토리가 상품 저장소에서 상품 이름과 가격을 읽는다 |
+| 주문 | 회원 | 할인 정책과 주문 응용 서비스가 회원과 회원 등급을 읽는다 |
+
+- 같은 상품이라도 상품 컨텍스트의 상품은 지금 가격과 재고를 갖는다
+- 주문 컨텍스트의 주문 항목은 주문 때의 상품 이름과 가격을 갖는다
+- 지금은 다른 컨텍스트의 클래스를 import로 바로 쓴다
+- 같은 프로그램 안에서 메서드를 호출해 협력한다
+
 - 주문 응용 서비스는 상품 저장소에서 상품을 찾아 재고를 줄인다
```

## T7 회원과 상품 서비스 만들기
커밋: 71113e3
Source: 170e451

| 파일 | 변경 |
|---|---|
| ddd/context-map.md | 고침 |
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/catalog/application/ProductService.java | 새로 |
| ddd/src/ddd/catalog/domain/ProductRepository.java | 고침 |
| ddd/src/ddd/catalog/infrastructure/MemoryProductRepository.java | 고침 |
| ddd/src/ddd/member/application/MemberService.java | 새로 |
| ddd/src/ddd/member/domain/MemberRepository.java | 고침 |
| ddd/src/ddd/member/infrastructure/MemoryMemberRepository.java | 고침 |
| ddd/src/ddd/order/application/OrderService.java | 고침 |
| ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java | 고침 |
| ddd/where-rules-live.md | 새로 |

```diff
diff --git a/ddd/context-map.md b/ddd/context-map.md
index 04ab736..cead8de 100644
--- a/ddd/context-map.md
+++ b/ddd/context-map.md
@@ -1,28 +1,23 @@
 # 컨텍스트 맵
 
-## 하위 도메인
+## 하위 도메인과 컨텍스트
 
-| 하위 도메인 | 종류 | 왜 | 바운디드 컨텍스트 | 묶음 |
-|---|---|---|---|---|
-| 주문 | 핵심 | 주문, 결제, 배송, 취소 규칙이 이 가게가 잘해야 하는 일이다 | 주문 컨텍스트 | ddd.order |
-| 상품 | 지원 | 주문에 쓸 상품과 재고를 맡는다. 가게에 맞게 직접 만든다 | 상품 컨텍스트 | ddd.catalog |
-| 회원 | 일반 | 이름과 등급만 맡는다. 어느 가게나 비슷해서 사다 써도 된다 | 회원 컨텍스트 | ddd.member |
+| 업무 | 종류 | 책임 | 묶음 |
+|---|---|---|---|
+| 주문 | 핵심 | 주문, 결제, 배송, 취소 규칙 | ddd.order |
+| 상품 | 지원 | 상품 가격과 재고 | ddd.catalog |
+| 회원 | 일반 | 회원 이름과 등급 | ddd.member |
 
 - 하위 도메인은 업무를 나눈 것이다
-- 바운디드 컨텍스트(bounded context)는 한 모델과 한 말이 통하는 코드의 경계다
-- 이 프로그램은 하위 도메인 하나에 컨텍스트 하나를 둔다
+- 바운디드 컨텍스트는 한 모델과 업무 말이 통하는 경계다
 - 컨텍스트 안에는 domain, application, infrastructure 계층을 둔다
+- 주문 항목은 주문 당시의 상품 이름과 가격을 갖는다
+- 상품은 현재 가격과 재고를 갖는다
 
-## 컨텍스트 사이
+## 서비스로 협력하기
 
-| 쓰는 쪽 | 쓰이는 쪽 | 무엇 |
-|---|---|---|
-| 주문 | 상품 | 주문 항목 팩토리가 상품 저장소에서 상품 이름과 가격을 읽는다 |
-| 주문 | 회원 | 할인 정책과 주문 응용 서비스가 회원과 회원 등급을 읽는다 |
-
-- 같은 상품이라도 상품 컨텍스트의 상품은 지금 가격과 재고를 갖는다
-- 주문 컨텍스트의 주문 항목은 주문 때의 상품 이름과 가격을 갖는다
-- 지금은 다른 컨텍스트의 클래스를 import로 바로 쓴다
-- 같은 프로그램 안에서 메서드를 호출해 협력한다
-
-- 주문 응용 서비스는 상품 저장소에서 상품을 찾아 재고를 줄인다
+- MemberService는 회원 조회와 목록 조회를 맡는다
+- ProductService는 상품 조회와 목록 조회, 재고 감소를 맡는다
+- OrderService는 주문을 저장한 뒤 ProductService에 재고 감소를 요청한다
+- 주문 항목 팩토리는 상품 저장소에서 상품 이름과 가격을 읽는다
+- 각 서비스는 같은 프로그램 안에서 일반 메서드 호출로 협력한다
diff --git a/ddd/glossary.md b/ddd/glossary.md
index 03bb81e..806ad32 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -41,7 +41,9 @@
 | 할인 정책 | DiscountPolicy | 회원 등급과 주문 금액으로 할인 금액을 계산하는 도메인 서비스. VIP는 주문 금액의 10%, 일반은 0원이다. 계산만 하고 주문을 바꾸지 않는다 |
 | 할인 금액 | discountPrice | 결제할 때 주문 금액에서 깎는 돈. 결제 전에는 0원이다 |
 | 결제 금액 | paymentPrice | 주문 금액에서 할인 금액을 뺀 돈. 실제로 치르는 돈이다 |
-| 무료 배송 명세 | FreeShippingSpecification | 결제 금액이 50000원 이상이고 주소가 제주로 시작하지 않는지 참 거짓으로 답한다 |
+| 무료 배송 명세 | FreeShippingSpecification | 주문이 무료 배송인지 참 거짓으로만 답하는 명세. 결제 금액이 50000원 이상이고 배송 주소가 제주가 아니면 참이다 |
 | 배송비 | shippingFee | 주문을 보내는 데 받는 돈. 결제 금액과 따로 받는다. 무료 배송 명세를 만족하면 0원, 아니면 3000원이다 |
 | 재고 | stock | 상품이 지금 몇 개 남았는지. 주문하기가 끝나기 전에 주문 수량만큼 줄어든다 |
 | 재고 줄이기 | decreaseStock | 상품 재고를 주문 수량만큼 뺀다 |
+| 회원 서비스 | MemberService | 회원 조회와 회원 목록 조회를 맡는 응용 서비스 |
+| 상품 서비스 | ProductService | 상품 조회, 상품 목록 조회, 재고 감소를 맡는 응용 서비스. 재고 규칙은 상품이 지킨다 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 6949575..1826f84 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -2,9 +2,11 @@ package ddd;
 
 import java.util.List;
 
+import ddd.catalog.application.ProductService;
 import ddd.catalog.domain.Product;
 import ddd.catalog.domain.ProductRepository;
 import ddd.catalog.infrastructure.MemoryProductRepository;
+import ddd.member.application.MemberService;
 import ddd.member.domain.Member;
 import ddd.member.domain.MemberGrade;
 import ddd.member.domain.MemberRepository;
@@ -13,7 +15,6 @@ import ddd.order.application.OrderItemRequest;
 import ddd.order.application.OrderRequest;
 import ddd.order.application.OrderService;
 import ddd.order.domain.DiscountPolicy;
-import ddd.order.domain.Order;
 import ddd.order.domain.OrderItemFactory;
 import ddd.order.infrastructure.MemoryOrderRepository;
 
@@ -24,24 +25,37 @@ public class Main {
 
         MemberRepository memberRepository = new MemoryMemberRepository();
         memberRepository.save(new Member(1, "김철수", MemberGrade.BASIC));
+        MemberService memberService = new MemberService(memberRepository);
 
         ProductRepository productRepository = new MemoryProductRepository();
         productRepository.save(new Product(1, "자바 책", 15000, 10));
+        ProductService productService = new ProductService(productRepository);
 
         OrderService orderService = new OrderService(new MemoryOrderRepository(),
-                new OrderItemFactory(productRepository), memberRepository, new DiscountPolicy(), productRepository);
+                new OrderItemFactory(productRepository), memberService, new DiscountPolicy(), productService);
 
         System.out.println();
-        System.out.println("[세 컨텍스트를 거쳐 주문하기]");
-        int stockBefore = productRepository.findById(1).orElseThrow().getStock();
+        System.out.println("[회원 목록]");
+        for (Member member : memberService.listMembers()) {
+            System.out.println(member.getId() + "번 " + member.getName() + ", " + member.getGrade());
+        }
+
+        System.out.println();
+        System.out.println("[상품 목록]");
+        for (Product product : productService.listProducts()) {
+            System.out.println(product.getId() + "번 " + product.getName() + ", "
+                    + product.getPrice() + "원, 재고 " + product.getStock() + "개");
+        }
+
+        System.out.println();
+        System.out.println("[상품 서비스로 재고 줄이기]");
+        int stockBefore = productService.findProduct(1).getStock();
         long orderId = orderService.placeOrder(new OrderRequest(1,
                 List.of(new OrderItemRequest(1, 2)), "서울시 강남구"));
-        Order order = orderService.findOrder(orderId);
-        Member member = memberRepository.findById(order.getMemberId()).orElseThrow();
-        int stockAfter = productRepository.findById(1).orElseThrow().getStock();
-        System.out.println("주문 컨텍스트: " + orderId + "번 주문, " + order.getTotalPrice() + "원, "
-                + order.getStatus().label());
-        System.out.println("회원 컨텍스트: " + member.getName() + ", " + member.getGrade());
-        System.out.println("상품 컨텍스트: 자바 책 재고 " + stockBefore + "개 -> " + stockAfter + "개");
+        System.out.println("주문 컨텍스트: " + orderId + "번 주문, "
+                + orderService.findOrder(orderId).getTotalPrice() + "원, "
+                + orderService.findOrder(orderId).getStatus().label());
+        System.out.println("상품 컨텍스트: 자바 책 재고 " + stockBefore
+                + "개 -> " + productService.findProduct(1).getStock() + "개");
     }
 }
diff --git a/ddd/src/ddd/catalog/application/ProductService.java b/ddd/src/ddd/catalog/application/ProductService.java
new file mode 100644
index 0000000..b260e2d
--- /dev/null
+++ b/ddd/src/ddd/catalog/application/ProductService.java
@@ -0,0 +1,30 @@
+package ddd.catalog.application;
+
+import java.util.List;
+
+import ddd.catalog.domain.Product;
+import ddd.catalog.domain.ProductRepository;
+
+public class ProductService {
+
+    private final ProductRepository productRepository;
+
+    public ProductService(ProductRepository productRepository) {
+        this.productRepository = productRepository;
+    }
+
+    public Product findProduct(long productId) {
+        return productRepository.findById(productId)
+                .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다"));
+    }
+
+    public List<Product> listProducts() {
+        return productRepository.findAll();
+    }
+
+    public void decreaseStock(long productId, int quantity) {
+        Product product = findProduct(productId);
+        product.decreaseStock(quantity);
+        productRepository.save(product);
+    }
+}
diff --git a/ddd/src/ddd/catalog/domain/ProductRepository.java b/ddd/src/ddd/catalog/domain/ProductRepository.java
index f39d2f6..1ef2a51 100644
--- a/ddd/src/ddd/catalog/domain/ProductRepository.java
+++ b/ddd/src/ddd/catalog/domain/ProductRepository.java
@@ -1,5 +1,6 @@
 package ddd.catalog.domain;
 
+import java.util.List;
 import java.util.Optional;
 
 public interface ProductRepository {
@@ -7,4 +8,5 @@ public interface ProductRepository {
     void save(Product product);
 
     Optional<Product> findById(long id);
+    List<Product> findAll();
 }
diff --git a/ddd/src/ddd/catalog/infrastructure/MemoryProductRepository.java b/ddd/src/ddd/catalog/infrastructure/MemoryProductRepository.java
index eb62220..aabca73 100644
--- a/ddd/src/ddd/catalog/infrastructure/MemoryProductRepository.java
+++ b/ddd/src/ddd/catalog/infrastructure/MemoryProductRepository.java
@@ -1,5 +1,7 @@
 package ddd.catalog.infrastructure;
 
+import java.util.ArrayList;
+import java.util.List;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Optional;
@@ -20,4 +22,8 @@ public class MemoryProductRepository implements ProductRepository {
     public Optional<Product> findById(long id) {
         return Optional.ofNullable(products.get(id));
     }
+    @Override
+    public List<Product> findAll() {
+        return new ArrayList<>(products.values());
+    }
 }
diff --git a/ddd/src/ddd/member/application/MemberService.java b/ddd/src/ddd/member/application/MemberService.java
new file mode 100644
index 0000000..6cb519b
--- /dev/null
+++ b/ddd/src/ddd/member/application/MemberService.java
@@ -0,0 +1,24 @@
+package ddd.member.application;
+
+import java.util.List;
+
+import ddd.member.domain.Member;
+import ddd.member.domain.MemberRepository;
+
+public class MemberService {
+
+    private final MemberRepository memberRepository;
+
+    public MemberService(MemberRepository memberRepository) {
+        this.memberRepository = memberRepository;
+    }
+
+    public Member findMember(long memberId) {
+        return memberRepository.findById(memberId)
+                .orElseThrow(() -> new IllegalArgumentException("회원이 없습니다"));
+    }
+
+    public List<Member> listMembers() {
+        return memberRepository.findAll();
+    }
+}
diff --git a/ddd/src/ddd/member/domain/MemberRepository.java b/ddd/src/ddd/member/domain/MemberRepository.java
index 0a60ef2..c6e8aae 100644
--- a/ddd/src/ddd/member/domain/MemberRepository.java
+++ b/ddd/src/ddd/member/domain/MemberRepository.java
@@ -1,5 +1,6 @@
 package ddd.member.domain;
 
+import java.util.List;
 import java.util.Optional;
 
 public interface MemberRepository {
@@ -7,4 +8,5 @@ public interface MemberRepository {
     void save(Member member);
 
     Optional<Member> findById(long id);
+    List<Member> findAll();
 }
diff --git a/ddd/src/ddd/member/infrastructure/MemoryMemberRepository.java b/ddd/src/ddd/member/infrastructure/MemoryMemberRepository.java
index 7380ea8..229e743 100644
--- a/ddd/src/ddd/member/infrastructure/MemoryMemberRepository.java
+++ b/ddd/src/ddd/member/infrastructure/MemoryMemberRepository.java
@@ -1,5 +1,7 @@
 package ddd.member.infrastructure;
 
+import java.util.ArrayList;
+import java.util.List;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Optional;
@@ -20,4 +22,8 @@ public class MemoryMemberRepository implements MemberRepository {
     public Optional<Member> findById(long id) {
         return Optional.ofNullable(members.get(id));
     }
+    @Override
+    public List<Member> findAll() {
+        return new ArrayList<>(members.values());
+    }
 }
diff --git a/ddd/src/ddd/order/application/OrderService.java b/ddd/src/ddd/order/application/OrderService.java
index 702e2e8..3c55950 100644
--- a/ddd/src/ddd/order/application/OrderService.java
+++ b/ddd/src/ddd/order/application/OrderService.java
@@ -3,10 +3,9 @@ package ddd.order.application;
 import java.util.ArrayList;
 import java.util.List;
 
+import ddd.catalog.application.ProductService;
 import ddd.member.domain.Member;
-import ddd.member.domain.MemberRepository;
-import ddd.catalog.domain.Product;
-import ddd.catalog.domain.ProductRepository;
+import ddd.member.application.MemberService;
 import ddd.order.domain.Address;
 import ddd.order.domain.DiscountPolicy;
 import ddd.order.domain.Order;
@@ -18,18 +17,18 @@ public class OrderService {
 
     private final OrderRepository orderRepository;
     private final OrderItemFactory orderItemFactory;
-    private final MemberRepository memberRepository;
+    private final MemberService memberService;
     private final DiscountPolicy discountPolicy;
-    private final ProductRepository productRepository;
+    private final ProductService productService;
     private long nextId = 1;
 
     public OrderService(OrderRepository orderRepository, OrderItemFactory orderItemFactory,
-            MemberRepository memberRepository, DiscountPolicy discountPolicy, ProductRepository productRepository) {
+            MemberService memberService, DiscountPolicy discountPolicy, ProductService productService) {
         this.orderRepository = orderRepository;
         this.orderItemFactory = orderItemFactory;
-        this.memberRepository = memberRepository;
+        this.memberService = memberService;
         this.discountPolicy = discountPolicy;
-        this.productRepository = productRepository;
+        this.productService = productService;
     }
 
     public long placeOrder(OrderRequest request) {
@@ -40,9 +39,7 @@ public class OrderService {
         Order order = new Order(nextId++, request.memberId(), items, new Address(request.address()));
         orderRepository.save(order);
         for (OrderItem item : order.getItems()) {
-            Product product = productRepository.findById(item.getProductId()).orElseThrow();
-            product.decreaseStock(item.getQuantity());
-            productRepository.save(product);
+            productService.decreaseStock(item.getProductId(), item.getQuantity());
         }
         return order.getId();
     }
@@ -59,7 +56,7 @@ public class OrderService {
 
     public void pay(long orderId) {
         Order order = findOrder(orderId);
-        Member member = memberRepository.findById(order.getMemberId()).orElseThrow();
+        Member member = memberService.findMember(order.getMemberId());
         order.pay(discountPolicy.discount(member, order));
         orderRepository.save(order);
     }
diff --git a/ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java b/ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java
index b56e377..d80dff5 100644
--- a/ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java
+++ b/ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java
@@ -29,4 +29,5 @@ public class MemoryOrderRepository implements OrderRepository {
         }
         return Optional.of(new Order(order));
     }
+
 }
diff --git a/ddd/where-rules-live.md b/ddd/where-rules-live.md
new file mode 100644
index 0000000..821d24d
--- /dev/null
+++ b/ddd/where-rules-live.md
@@ -0,0 +1,12 @@
+# 규칙 두는 곳 정리
+
+| 두는 곳 | 두는 규칙 | 이 프로그램의 예 |
+|---|---|---|
+| 값 객체 | 값 하나가 지키는 것 | Address는 비어 있지 않다 |
+| 애그리거트 | 한 모델 안에서 함께 맞아야 하는 것 | Order는 항목 합계와 상태 전이를 지킨다 |
+| 도메인 서비스 | 여러 모델을 읽고 계산하는 것 | DiscountPolicy는 회원 등급과 주문 금액으로 할인 금액을 낸다 |
+| 명세 | 참 거짓으로 답하는 업무 조건 | FreeShippingSpecification은 결제 금액과 주소로 무료 배송인지 답한다 |
+| 응용 서비스 | 꺼내고 시키고 저장하는 순서 | ProductService는 상품을 찾아 재고 감소를 시키고 저장한다 |
+
+- 15장은 주문 응용 서비스가 재고 감소를 직접 호출한다
+- 주문 취소의 재고 복원과 실패 보상은 이번 강의에서 다루지 않는다
```

## T8 주문 접수 이벤트로 재고 감소 연결하기
커밋: 4f83e10
Source: 170e451

| 파일 | 변경 |
|---|---|
| ddd/context-map.md | 고침 |
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/catalog/application/DecreaseStockHandler.java | 새로 |
| ddd/src/ddd/order/application/EventPublisher.java | 새로 |
| ddd/src/ddd/order/application/OrderService.java | 고침 |
| ddd/src/ddd/order/domain/OrderPlaced.java | 새로 |
| ddd/where-rules-live.md | 고침 |

```diff
diff --git a/ddd/context-map.md b/ddd/context-map.md
index cead8de..3136175 100644
--- a/ddd/context-map.md
+++ b/ddd/context-map.md
@@ -1,23 +1,35 @@
 # 컨텍스트 맵
 
-## 하위 도메인과 컨텍스트
+## 하위 도메인
 
-| 업무 | 종류 | 책임 | 묶음 |
+하위 도메인은 업무를 나눈 것이고 바운디드 컨텍스트는 그 업무의 모델과 용어가 통하는 경계다.
+
+| 하위 도메인 | 구분 | 바운디드 컨텍스트 | 묶음 |
 |---|---|---|---|
-| 주문 | 핵심 | 주문, 결제, 배송, 취소 규칙 | ddd.order |
-| 상품 | 지원 | 상품 가격과 재고 | ddd.catalog |
-| 회원 | 일반 | 회원 이름과 등급 | ddd.member |
-
-- 하위 도메인은 업무를 나눈 것이다
-- 바운디드 컨텍스트는 한 모델과 업무 말이 통하는 경계다
-- 컨텍스트 안에는 domain, application, infrastructure 계층을 둔다
-- 주문 항목은 주문 당시의 상품 이름과 가격을 갖는다
-- 상품은 현재 가격과 재고를 갖는다
-
-## 서비스로 협력하기
-
-- MemberService는 회원 조회와 목록 조회를 맡는다
-- ProductService는 상품 조회와 목록 조회, 재고 감소를 맡는다
-- OrderService는 주문을 저장한 뒤 ProductService에 재고 감소를 요청한다
-- 주문 항목 팩토리는 상품 저장소에서 상품 이름과 가격을 읽는다
-- 각 서비스는 같은 프로그램 안에서 일반 메서드 호출로 협력한다
+| 주문 | 핵심 | 주문 컨텍스트 | ddd.order |
+| 상품 | 지원 | 상품 컨텍스트 | ddd.catalog |
+| 회원 | 일반 | 회원 컨텍스트 | ddd.member |
+
+## 컨텍스트 사이
+
+| 쓰는 곳 | 제공하는 곳 | 연결 |
+|---|---|---|
+| 주문 | 상품 | 주문 항목 팩토리가 상품 저장소에서 상품을 읽어 주문 당시 이름과 가격을 복사한다 |
+| 주문 | 회원 | 회원 서비스가 회원을 찾고 할인 정책이 회원 등급으로 할인 금액을 계산한다 |
+| 상품 | 주문 | 재고 줄이기 처리기가 주문 접수됨 이벤트를 받아 상품 서비스에 재고 감소를 요청한다 |
+
+## 실행 순서
+
+- Main이 세 컨텍스트의 서비스와 저장소를 연결한다
+- 주문 응용 서비스가 주문을 저장한 뒤 주문 접수됨 이벤트를 발행한다
+- 발행기는 재고 줄이기 처리기를 즉시 호출하고 처리기는 상품 서비스에 재고 감소를 요청한다
+- 주문하기가 정상적으로 끝났을 때 재고 감소도 끝나 있다
+- 별도 서버, 문자열 메시지, 대기 목록은 없다
+- 메서드를 동기로 호출해도 여러 저장이 자동으로 하나의 트랜잭션이 되지는 않는다
+
+## 경계의 범위
+
+- 각 컨텍스트는 자기 모델과 규칙을 맡고 내부에서 도메인, 응용, 인프라 계층을 구분한다
+- 이 예제는 같은 자바 프로그램에서 협력한다
+- 주문 항목 팩토리와 할인 정책은 다른 컨텍스트의 모델을 직접 읽으므로 완전히 독립된 경계는 아니다
+- 발행기는 재고 줄이기 처리기 하나를 직접 안다. 여러 이벤트를 다루는 범용 기반은 만들지 않는다
diff --git a/ddd/glossary.md b/ddd/glossary.md
index 806ad32..421a801 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -44,6 +44,9 @@
 | 무료 배송 명세 | FreeShippingSpecification | 주문이 무료 배송인지 참 거짓으로만 답하는 명세. 결제 금액이 50000원 이상이고 배송 주소가 제주가 아니면 참이다 |
 | 배송비 | shippingFee | 주문을 보내는 데 받는 돈. 결제 금액과 따로 받는다. 무료 배송 명세를 만족하면 0원, 아니면 3000원이다 |
 | 재고 | stock | 상품이 지금 몇 개 남았는지. 주문하기가 끝나기 전에 주문 수량만큼 줄어든다 |
+| 주문 접수됨 | OrderPlaced | 주문 저장 뒤 주문 응용 서비스가 발행하는 도메인 이벤트. 주문 번호와 주문 항목을 담는다. 이미 일어난 일이라 바뀌지 않는다 |
+| 이벤트 발행기 | EventPublisher | 주문 접수됨 이벤트를 받아 재고 줄이기 처리기를 그 자리에서 호출하는 곳 |
 | 재고 줄이기 | decreaseStock | 상품 재고를 주문 수량만큼 뺀다 |
+| 재고 줄이기 처리기 | DecreaseStockHandler | 주문 접수됨 이벤트를 받아 상품 서비스에 항목별 재고 감소를 요청하는 곳 |
 | 회원 서비스 | MemberService | 회원 조회와 회원 목록 조회를 맡는 응용 서비스 |
 | 상품 서비스 | ProductService | 상품 조회, 상품 목록 조회, 재고 감소를 맡는 응용 서비스. 재고 규칙은 상품이 지킨다 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 1826f84..54d2872 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -2,6 +2,7 @@ package ddd;
 
 import java.util.List;
 
+import ddd.catalog.application.DecreaseStockHandler;
 import ddd.catalog.application.ProductService;
 import ddd.catalog.domain.Product;
 import ddd.catalog.domain.ProductRepository;
@@ -11,6 +12,7 @@ import ddd.member.domain.Member;
 import ddd.member.domain.MemberGrade;
 import ddd.member.domain.MemberRepository;
 import ddd.member.infrastructure.MemoryMemberRepository;
+import ddd.order.application.EventPublisher;
 import ddd.order.application.OrderItemRequest;
 import ddd.order.application.OrderRequest;
 import ddd.order.application.OrderService;
@@ -31,8 +33,9 @@ public class Main {
         productRepository.save(new Product(1, "자바 책", 15000, 10));
         ProductService productService = new ProductService(productRepository);
 
+        EventPublisher eventPublisher = new EventPublisher(new DecreaseStockHandler(productService));
         OrderService orderService = new OrderService(new MemoryOrderRepository(),
-                new OrderItemFactory(productRepository), memberService, new DiscountPolicy(), productService);
+                new OrderItemFactory(productRepository), memberService, new DiscountPolicy(), eventPublisher);
 
         System.out.println();
         System.out.println("[회원 목록]");
@@ -48,7 +51,7 @@ public class Main {
         }
 
         System.out.println();
-        System.out.println("[상품 서비스로 재고 줄이기]");
+        System.out.println("[주문 접수 이벤트로 재고 줄이기]");
         int stockBefore = productService.findProduct(1).getStock();
         long orderId = orderService.placeOrder(new OrderRequest(1,
                 List.of(new OrderItemRequest(1, 2)), "서울시 강남구"));
diff --git a/ddd/src/ddd/catalog/application/DecreaseStockHandler.java b/ddd/src/ddd/catalog/application/DecreaseStockHandler.java
new file mode 100644
index 0000000..9b33634
--- /dev/null
+++ b/ddd/src/ddd/catalog/application/DecreaseStockHandler.java
@@ -0,0 +1,19 @@
+package ddd.catalog.application;
+
+import ddd.order.domain.OrderItem;
+import ddd.order.domain.OrderPlaced;
+
+public class DecreaseStockHandler {
+
+    private final ProductService productService;
+
+    public DecreaseStockHandler(ProductService productService) {
+        this.productService = productService;
+    }
+
+    public void handle(OrderPlaced event) {
+        for (OrderItem item : event.items()) {
+            productService.decreaseStock(item.getProductId(), item.getQuantity());
+        }
+    }
+}
diff --git a/ddd/src/ddd/order/application/EventPublisher.java b/ddd/src/ddd/order/application/EventPublisher.java
new file mode 100644
index 0000000..107289d
--- /dev/null
+++ b/ddd/src/ddd/order/application/EventPublisher.java
@@ -0,0 +1,17 @@
+package ddd.order.application;
+
+import ddd.catalog.application.DecreaseStockHandler;
+import ddd.order.domain.OrderPlaced;
+
+public class EventPublisher {
+
+    private final DecreaseStockHandler handler;
+
+    public EventPublisher(DecreaseStockHandler handler) {
+        this.handler = handler;
+    }
+
+    public void publish(OrderPlaced event) {
+        handler.handle(event);
+    }
+}
diff --git a/ddd/src/ddd/order/application/OrderService.java b/ddd/src/ddd/order/application/OrderService.java
index 3c55950..b22dc37 100644
--- a/ddd/src/ddd/order/application/OrderService.java
+++ b/ddd/src/ddd/order/application/OrderService.java
@@ -3,12 +3,12 @@ package ddd.order.application;
 import java.util.ArrayList;
 import java.util.List;
 
-import ddd.catalog.application.ProductService;
 import ddd.member.domain.Member;
 import ddd.member.application.MemberService;
 import ddd.order.domain.Address;
 import ddd.order.domain.DiscountPolicy;
 import ddd.order.domain.Order;
+import ddd.order.domain.OrderPlaced;
 import ddd.order.domain.OrderItem;
 import ddd.order.domain.OrderItemFactory;
 import ddd.order.domain.OrderRepository;
@@ -19,16 +19,16 @@ public class OrderService {
     private final OrderItemFactory orderItemFactory;
     private final MemberService memberService;
     private final DiscountPolicy discountPolicy;
-    private final ProductService productService;
+    private final EventPublisher eventPublisher;
     private long nextId = 1;
 
     public OrderService(OrderRepository orderRepository, OrderItemFactory orderItemFactory,
-            MemberService memberService, DiscountPolicy discountPolicy, ProductService productService) {
+            MemberService memberService, DiscountPolicy discountPolicy, EventPublisher eventPublisher) {
         this.orderRepository = orderRepository;
         this.orderItemFactory = orderItemFactory;
         this.memberService = memberService;
         this.discountPolicy = discountPolicy;
-        this.productService = productService;
+        this.eventPublisher = eventPublisher;
     }
 
     public long placeOrder(OrderRequest request) {
@@ -38,9 +38,7 @@ public class OrderService {
         }
         Order order = new Order(nextId++, request.memberId(), items, new Address(request.address()));
         orderRepository.save(order);
-        for (OrderItem item : order.getItems()) {
-            productService.decreaseStock(item.getProductId(), item.getQuantity());
-        }
+        eventPublisher.publish(new OrderPlaced(order.getId(), order.getItems()));
         return order.getId();
     }
 
diff --git a/ddd/src/ddd/order/domain/OrderPlaced.java b/ddd/src/ddd/order/domain/OrderPlaced.java
new file mode 100644
index 0000000..4318e5c
--- /dev/null
+++ b/ddd/src/ddd/order/domain/OrderPlaced.java
@@ -0,0 +1,6 @@
+package ddd.order.domain;
+
+import java.util.List;
+
+public record OrderPlaced(long orderId, List<OrderItem> items) {
+}
diff --git a/ddd/where-rules-live.md b/ddd/where-rules-live.md
index 821d24d..72d7e01 100644
--- a/ddd/where-rules-live.md
+++ b/ddd/where-rules-live.md
@@ -7,6 +7,10 @@
 | 도메인 서비스 | 여러 모델을 읽고 계산하는 것 | DiscountPolicy는 회원 등급과 주문 금액으로 할인 금액을 낸다 |
 | 명세 | 참 거짓으로 답하는 업무 조건 | FreeShippingSpecification은 결제 금액과 주소로 무료 배송인지 답한다 |
 | 응용 서비스 | 꺼내고 시키고 저장하는 순서 | ProductService는 상품을 찾아 재고 감소를 시키고 저장한다 |
+| 이벤트 처리기 | 일어난 일에 따른 후속 반응 | DecreaseStockHandler는 주문 접수됨을 받아 상품 서비스에 재고 감소를 요청한다 |
 
 - 15장은 주문 응용 서비스가 재고 감소를 직접 호출한다
+- 17장은 같은 동작을 주문 접수 이벤트와 처리기로 나눈다
+- 이벤트는 프로그램을 나누거나 나중에 처리해야만 쓸 수 있는 것이 아니다
+- 이 예제의 이벤트는 같은 스레드에서 즉시 처리한다
 - 주문 취소의 재고 복원과 실패 보상은 이번 강의에서 다루지 않는다
```

## T9 서비스와 즉시 처리 이벤트로 강의 마무리하기
커밋: 170e451
- 위 task에 고침을 녹였다. 학생에게 별도 수정 단계로 보이지 않는다
- done 태그는 옮기지 않고 fixes patch로 수정된 장 끝 코드를 재현한다
