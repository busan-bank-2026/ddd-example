# bounded-contexts 코드 요약

## T1 하위 도메인과 컨텍스트 정하기
커밋: 8a46784

| 파일 | 변경 |
|---|---|
| ddd/context-map.md | 새로 |

````markdown
<!-- ddd/context-map.md -->
# 컨텍스트 맵

## 하위 도메인

| 하위 도메인 | 종류 | 왜 | 바운디드 컨텍스트 | 묶음 |
|---|---|---|---|---|
| 주문 | 핵심 | 주문, 결제, 배송, 취소 규칙이 이 가게가 잘해야 하는 일이다 | 주문 컨텍스트 | ddd.order |
| 상품 | 지원 | 주문에 쓸 상품과 재고를 맡는다. 가게에 맞게 직접 만든다 | 상품 컨텍스트 | ddd.catalog |
| 회원 | 일반 | 이름과 등급만 맡는다. 어느 가게나 비슷해서 사다 써도 된다 | 회원 컨텍스트 | ddd.member |

- 하위 도메인은 업무를 나눈 것이다
- 바운디드 컨텍스트(bounded context)는 한 모델과 한 말이 통하는 코드의 경계다
- 이 프로그램은 하위 도메인 하나에 컨텍스트 하나를 둔다
- 컨텍스트 안에는 domain, application, infrastructure 계층을 둔다
````

## T2 공유 커널로 돈 옮기기
커밋: c6354db

| 파일 | 변경 |
|---|---|
| ddd/context-map.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/domain/DiscountPolicy.java | 고침 |
| ddd/src/ddd/domain/Order.java | 고침 |
| ddd/src/ddd/domain/OrderLine.java | 고침 |
| ddd/src/ddd/domain/Product.java | 고침 |
| ddd/src/ddd/shared/Money.java | 옮김. ddd/src/ddd/domain/Money.java에서 옮기고 package 줄을 바꿈 |

```diff
diff --git a/ddd/context-map.md b/ddd/context-map.md
index bb80ed3..6a2bf20 100644
--- a/ddd/context-map.md
+++ b/ddd/context-map.md
@@ -12,3 +12,9 @@
 - 바운디드 컨텍스트(bounded context)는 한 모델과 한 말이 통하는 코드의 경계다
 - 이 프로그램은 하위 도메인 하나에 컨텍스트 하나를 둔다
 - 컨텍스트 안에는 domain, application, infrastructure 계층을 둔다
+
+## 공유 커널
+
+- 돈 Money는 공유 커널(shared kernel) 묶음 ddd.shared에 둔다
+- 주문과 상품 컨텍스트가 함께 쓴다
+- 바꾸려면 쓰는 컨텍스트가 모두 합의해야 한다. 그래서 작게 둔다
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 6f7982b..9cf6bce 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -11,7 +11,6 @@ import ddd.domain.DiscountPolicy;
 import ddd.domain.Member;
 import ddd.domain.MemberGrade;
 import ddd.domain.MemberRepository;
-import ddd.domain.Money;
 import ddd.domain.Order;
 import ddd.domain.OrderLine;
 import ddd.domain.OrderLineFactory;
@@ -22,6 +21,7 @@ import ddd.domain.ProductRepository;
 import ddd.infrastructure.MemoryMemberRepository;
 import ddd.infrastructure.MemoryOrderRepository;
 import ddd.infrastructure.MemoryProductRepository;
+import ddd.shared.Money;
 
 public class Main {
 
diff --git a/ddd/src/ddd/domain/DiscountPolicy.java b/ddd/src/ddd/domain/DiscountPolicy.java
index 8db2d32..a97d258 100644
--- a/ddd/src/ddd/domain/DiscountPolicy.java
+++ b/ddd/src/ddd/domain/DiscountPolicy.java
@@ -1,5 +1,7 @@
 package ddd.domain;
 
+import ddd.shared.Money;
+
 public class DiscountPolicy {
 
     public Money discount(Member member, Order order) {
diff --git a/ddd/src/ddd/domain/Order.java b/ddd/src/ddd/domain/Order.java
index 91b39ac..9182c4e 100644
--- a/ddd/src/ddd/domain/Order.java
+++ b/ddd/src/ddd/domain/Order.java
@@ -4,6 +4,8 @@ import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.List;
 
+import ddd.shared.Money;
+
 public class Order implements Serializable {
 
     private static final long serialVersionUID = 1L;
diff --git a/ddd/src/ddd/domain/OrderLine.java b/ddd/src/ddd/domain/OrderLine.java
index a2de83e..42378f7 100644
--- a/ddd/src/ddd/domain/OrderLine.java
+++ b/ddd/src/ddd/domain/OrderLine.java
@@ -2,6 +2,8 @@ package ddd.domain;
 
 import java.io.Serializable;
 
+import ddd.shared.Money;
+
 public record OrderLine(long productId, String productName, Money price, int quantity) implements Serializable {
 
     public OrderLine {
diff --git a/ddd/src/ddd/domain/Product.java b/ddd/src/ddd/domain/Product.java
index 34f4a9f..79c82bb 100644
--- a/ddd/src/ddd/domain/Product.java
+++ b/ddd/src/ddd/domain/Product.java
@@ -1,5 +1,7 @@
 package ddd.domain;
 
+import ddd.shared.Money;
+
 public class Product {
 
     private final long id;
diff --git a/ddd/src/ddd/domain/Money.java b/ddd/src/ddd/shared/Money.java
similarity index 96%
rename from ddd/src/ddd/domain/Money.java
rename to ddd/src/ddd/shared/Money.java
index ba8f995..f5843ee 100644
--- a/ddd/src/ddd/domain/Money.java
+++ b/ddd/src/ddd/shared/Money.java
@@ -1,4 +1,4 @@
-package ddd.domain;
+package ddd.shared;
 
 import java.io.Serializable;
 
```

## T3 회원 컨텍스트 만들기
커밋: e19673d

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/application/OrderApplicationService.java | 고침 |
| ddd/src/ddd/domain/DiscountPolicy.java | 고침 |
| ddd/src/ddd/member/domain/Member.java | 옮김. ddd/src/ddd/domain/Member.java에서 옮기고 package 줄을 바꿈 |
| ddd/src/ddd/member/domain/MemberGrade.java | 옮김. ddd/src/ddd/domain/MemberGrade.java에서 옮기고 package 줄을 바꿈 |
| ddd/src/ddd/member/domain/MemberRepository.java | 옮김. ddd/src/ddd/domain/MemberRepository.java에서 옮기고 package 줄을 바꿈 |
| ddd/src/ddd/member/infrastructure/MemoryMemberRepository.java | 옮김. ddd/src/ddd/infrastructure/MemoryMemberRepository.java에서 옮기고 package 줄과 import 줄을 바꿈 |

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 9cf6bce..ea719da 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -8,9 +8,6 @@ import ddd.application.OrderApplicationService;
 import ddd.application.OrderLineCommand;
 import ddd.application.PlaceOrderCommand;
 import ddd.domain.DiscountPolicy;
-import ddd.domain.Member;
-import ddd.domain.MemberGrade;
-import ddd.domain.MemberRepository;
 import ddd.domain.Order;
 import ddd.domain.OrderLine;
 import ddd.domain.OrderLineFactory;
@@ -18,9 +15,12 @@ import ddd.domain.OrderPlaced;
 import ddd.domain.OrderRepository;
 import ddd.domain.Product;
 import ddd.domain.ProductRepository;
-import ddd.infrastructure.MemoryMemberRepository;
 import ddd.infrastructure.MemoryOrderRepository;
 import ddd.infrastructure.MemoryProductRepository;
+import ddd.member.domain.Member;
+import ddd.member.domain.MemberGrade;
+import ddd.member.domain.MemberRepository;
+import ddd.member.infrastructure.MemoryMemberRepository;
 import ddd.shared.Money;
 
 public class Main {
diff --git a/ddd/src/ddd/application/OrderApplicationService.java b/ddd/src/ddd/application/OrderApplicationService.java
index 54f77bb..add0491 100644
--- a/ddd/src/ddd/application/OrderApplicationService.java
+++ b/ddd/src/ddd/application/OrderApplicationService.java
@@ -5,12 +5,12 @@ import java.util.List;
 
 import ddd.domain.Address;
 import ddd.domain.DiscountPolicy;
-import ddd.domain.Member;
-import ddd.domain.MemberRepository;
 import ddd.domain.Order;
 import ddd.domain.OrderLine;
 import ddd.domain.OrderLineFactory;
 import ddd.domain.OrderRepository;
+import ddd.member.domain.Member;
+import ddd.member.domain.MemberRepository;
 
 public class OrderApplicationService {
 
diff --git a/ddd/src/ddd/domain/DiscountPolicy.java b/ddd/src/ddd/domain/DiscountPolicy.java
index a97d258..c1a4dac 100644
--- a/ddd/src/ddd/domain/DiscountPolicy.java
+++ b/ddd/src/ddd/domain/DiscountPolicy.java
@@ -1,5 +1,7 @@
 package ddd.domain;
 
+import ddd.member.domain.Member;
+import ddd.member.domain.MemberGrade;
 import ddd.shared.Money;
 
 public class DiscountPolicy {
diff --git a/ddd/src/ddd/domain/Member.java b/ddd/src/ddd/member/domain/Member.java
similarity index 96%
rename from ddd/src/ddd/domain/Member.java
rename to ddd/src/ddd/member/domain/Member.java
index 90209c9..d8938ba 100644
--- a/ddd/src/ddd/domain/Member.java
+++ b/ddd/src/ddd/member/domain/Member.java
@@ -1,4 +1,4 @@
-package ddd.domain;
+package ddd.member.domain;
 
 public class Member {
 
diff --git a/ddd/src/ddd/domain/MemberGrade.java b/ddd/src/ddd/member/domain/MemberGrade.java
similarity index 64%
rename from ddd/src/ddd/domain/MemberGrade.java
rename to ddd/src/ddd/member/domain/MemberGrade.java
index 412ba28..3ff7824 100644
--- a/ddd/src/ddd/domain/MemberGrade.java
+++ b/ddd/src/ddd/member/domain/MemberGrade.java
@@ -1,4 +1,4 @@
-package ddd.domain;
+package ddd.member.domain;
 
 public enum MemberGrade {
     BASIC,
diff --git a/ddd/src/ddd/domain/MemberRepository.java b/ddd/src/ddd/member/domain/MemberRepository.java
similarity index 83%
rename from ddd/src/ddd/domain/MemberRepository.java
rename to ddd/src/ddd/member/domain/MemberRepository.java
index da4c6c8..0a60ef2 100644
--- a/ddd/src/ddd/domain/MemberRepository.java
+++ b/ddd/src/ddd/member/domain/MemberRepository.java
@@ -1,4 +1,4 @@
-package ddd.domain;
+package ddd.member.domain;
 
 import java.util.Optional;
 
diff --git a/ddd/src/ddd/infrastructure/MemoryMemberRepository.java b/ddd/src/ddd/member/infrastructure/MemoryMemberRepository.java
similarity index 79%
rename from ddd/src/ddd/infrastructure/MemoryMemberRepository.java
rename to ddd/src/ddd/member/infrastructure/MemoryMemberRepository.java
index 60f1c50..7380ea8 100644
--- a/ddd/src/ddd/infrastructure/MemoryMemberRepository.java
+++ b/ddd/src/ddd/member/infrastructure/MemoryMemberRepository.java
@@ -1,11 +1,11 @@
-package ddd.infrastructure;
+package ddd.member.infrastructure;
 
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Optional;
 
-import ddd.domain.Member;
-import ddd.domain.MemberRepository;
+import ddd.member.domain.Member;
+import ddd.member.domain.MemberRepository;
 
 public class MemoryMemberRepository implements MemberRepository {
 
```

## T4 상품 컨텍스트 만들기
커밋: 27bbabf

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/catalog/application/DecreaseStockHandler.java | 옮김. ddd/src/ddd/application/DecreaseStockHandler.java에서 옮기고 package 줄과 import 줄을 바꿈 |
| ddd/src/ddd/catalog/domain/Product.java | 옮김. ddd/src/ddd/domain/Product.java에서 옮기고 package 줄을 바꿈 |
| ddd/src/ddd/catalog/domain/ProductRepository.java | 옮김. ddd/src/ddd/domain/ProductRepository.java에서 옮기고 package 줄을 바꿈 |
| ddd/src/ddd/catalog/infrastructure/MemoryProductRepository.java | 옮김. ddd/src/ddd/infrastructure/MemoryProductRepository.java에서 옮기고 package 줄과 import 줄을 바꿈 |
| ddd/src/ddd/domain/OrderLineFactory.java | 고침 |

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index ea719da..d101e00 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -2,21 +2,21 @@ package ddd;
 
 import java.util.List;
 
-import ddd.application.DecreaseStockHandler;
 import ddd.application.EventPublisher;
 import ddd.application.OrderApplicationService;
 import ddd.application.OrderLineCommand;
 import ddd.application.PlaceOrderCommand;
+import ddd.catalog.application.DecreaseStockHandler;
+import ddd.catalog.domain.Product;
+import ddd.catalog.domain.ProductRepository;
+import ddd.catalog.infrastructure.MemoryProductRepository;
 import ddd.domain.DiscountPolicy;
 import ddd.domain.Order;
 import ddd.domain.OrderLine;
 import ddd.domain.OrderLineFactory;
 import ddd.domain.OrderPlaced;
 import ddd.domain.OrderRepository;
-import ddd.domain.Product;
-import ddd.domain.ProductRepository;
 import ddd.infrastructure.MemoryOrderRepository;
-import ddd.infrastructure.MemoryProductRepository;
 import ddd.member.domain.Member;
 import ddd.member.domain.MemberGrade;
 import ddd.member.domain.MemberRepository;
diff --git a/ddd/src/ddd/application/DecreaseStockHandler.java b/ddd/src/ddd/catalog/application/DecreaseStockHandler.java
similarity index 84%
rename from ddd/src/ddd/application/DecreaseStockHandler.java
rename to ddd/src/ddd/catalog/application/DecreaseStockHandler.java
index 6076d41..4b901db 100644
--- a/ddd/src/ddd/application/DecreaseStockHandler.java
+++ b/ddd/src/ddd/catalog/application/DecreaseStockHandler.java
@@ -1,9 +1,9 @@
-package ddd.application;
+package ddd.catalog.application;
 
+import ddd.catalog.domain.Product;
+import ddd.catalog.domain.ProductRepository;
 import ddd.domain.OrderLine;
 import ddd.domain.OrderPlaced;
-import ddd.domain.Product;
-import ddd.domain.ProductRepository;
 
 public class DecreaseStockHandler {
 
diff --git a/ddd/src/ddd/domain/Product.java b/ddd/src/ddd/catalog/domain/Product.java
similarity index 97%
rename from ddd/src/ddd/domain/Product.java
rename to ddd/src/ddd/catalog/domain/Product.java
index 79c82bb..b14d4bd 100644
--- a/ddd/src/ddd/domain/Product.java
+++ b/ddd/src/ddd/catalog/domain/Product.java
@@ -1,4 +1,4 @@
-package ddd.domain;
+package ddd.catalog.domain;
 
 import ddd.shared.Money;
 
diff --git a/ddd/src/ddd/domain/ProductRepository.java b/ddd/src/ddd/catalog/domain/ProductRepository.java
similarity index 83%
rename from ddd/src/ddd/domain/ProductRepository.java
rename to ddd/src/ddd/catalog/domain/ProductRepository.java
index 9221d84..f39d2f6 100644
--- a/ddd/src/ddd/domain/ProductRepository.java
+++ b/ddd/src/ddd/catalog/domain/ProductRepository.java
@@ -1,4 +1,4 @@
-package ddd.domain;
+package ddd.catalog.domain;
 
 import java.util.Optional;
 
diff --git a/ddd/src/ddd/infrastructure/MemoryProductRepository.java b/ddd/src/ddd/catalog/infrastructure/MemoryProductRepository.java
similarity index 79%
rename from ddd/src/ddd/infrastructure/MemoryProductRepository.java
rename to ddd/src/ddd/catalog/infrastructure/MemoryProductRepository.java
index f13b139..eb62220 100644
--- a/ddd/src/ddd/infrastructure/MemoryProductRepository.java
+++ b/ddd/src/ddd/catalog/infrastructure/MemoryProductRepository.java
@@ -1,11 +1,11 @@
-package ddd.infrastructure;
+package ddd.catalog.infrastructure;
 
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Optional;
 
-import ddd.domain.Product;
-import ddd.domain.ProductRepository;
+import ddd.catalog.domain.Product;
+import ddd.catalog.domain.ProductRepository;
 
 public class MemoryProductRepository implements ProductRepository {
 
diff --git a/ddd/src/ddd/domain/OrderLineFactory.java b/ddd/src/ddd/domain/OrderLineFactory.java
index 02fd86d..4eec622 100644
--- a/ddd/src/ddd/domain/OrderLineFactory.java
+++ b/ddd/src/ddd/domain/OrderLineFactory.java
@@ -1,5 +1,8 @@
 package ddd.domain;
 
+import ddd.catalog.domain.Product;
+import ddd.catalog.domain.ProductRepository;
+
 public class OrderLineFactory {
 
     private final ProductRepository productRepository;
```

## T5 주문 컨텍스트 만들기
커밋: ced0444

| 파일 | 변경 |
|---|---|
| ddd/context-map.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/catalog/application/DecreaseStockHandler.java | 고침 |
| ddd/src/ddd/order/application/EventPublisher.java | 옮김. ddd/src/ddd/application/EventPublisher.java에서 옮기고 package 줄을 바꿈 |
| ddd/src/ddd/order/application/OrderApplicationService.java | 옮김. ddd/src/ddd/application/OrderApplicationService.java에서 옮기고 package 줄과 import 줄을 바꿈 |
| ddd/src/ddd/order/application/OrderLineCommand.java | 옮김. ddd/src/ddd/application/OrderLineCommand.java에서 옮기고 package 줄을 바꿈 |
| ddd/src/ddd/order/application/PlaceOrderCommand.java | 옮김. ddd/src/ddd/application/PlaceOrderCommand.java에서 옮기고 package 줄을 바꿈 |
| ddd/src/ddd/order/domain/Address.java | 옮김. ddd/src/ddd/domain/Address.java에서 옮기고 package 줄을 바꿈 |
| ddd/src/ddd/order/domain/DiscountPolicy.java | 옮김. ddd/src/ddd/domain/DiscountPolicy.java에서 옮기고 package 줄을 바꿈 |
| ddd/src/ddd/order/domain/FreeShippingAreaSpecification.java | 옮김. ddd/src/ddd/domain/FreeShippingAreaSpecification.java에서 옮기고 package 줄을 바꿈 |
| ddd/src/ddd/order/domain/FreeShippingPriceSpecification.java | 옮김. ddd/src/ddd/domain/FreeShippingPriceSpecification.java에서 옮기고 package 줄을 바꿈 |
| ddd/src/ddd/order/domain/FreeShippingSpecification.java | 옮김. ddd/src/ddd/domain/FreeShippingSpecification.java에서 옮기고 package 줄을 바꿈 |
| ddd/src/ddd/order/domain/Order.java | 옮김. ddd/src/ddd/domain/Order.java에서 옮기고 package 줄을 바꿈 |
| ddd/src/ddd/order/domain/OrderLine.java | 옮김. ddd/src/ddd/domain/OrderLine.java에서 옮기고 package 줄을 바꿈 |
| ddd/src/ddd/order/domain/OrderLineFactory.java | 옮김. ddd/src/ddd/domain/OrderLineFactory.java에서 옮기고 package 줄을 바꿈 |
| ddd/src/ddd/order/domain/OrderPlaced.java | 옮김. ddd/src/ddd/domain/OrderPlaced.java에서 옮기고 package 줄을 바꿈 |
| ddd/src/ddd/order/domain/OrderRepository.java | 옮김. ddd/src/ddd/domain/OrderRepository.java에서 옮기고 package 줄을 바꿈 |
| ddd/src/ddd/order/domain/OrderSpecification.java | 옮김. ddd/src/ddd/domain/OrderSpecification.java에서 옮기고 package 줄을 바꿈 |
| ddd/src/ddd/order/domain/OrderStatus.java | 옮김. ddd/src/ddd/domain/OrderStatus.java에서 옮기고 package 줄을 바꿈 |
| ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java | 옮김. ddd/src/ddd/infrastructure/MemoryOrderRepository.java에서 옮기고 package 줄과 import 줄을 바꿈 |

```diff
diff --git a/ddd/context-map.md b/ddd/context-map.md
index 6a2bf20..bed918d 100644
--- a/ddd/context-map.md
+++ b/ddd/context-map.md
@@ -18,3 +18,17 @@
 - 돈 Money는 공유 커널(shared kernel) 묶음 ddd.shared에 둔다
 - 주문과 상품 컨텍스트가 함께 쓴다
 - 바꾸려면 쓰는 컨텍스트가 모두 합의해야 한다. 그래서 작게 둔다
+
+## 컨텍스트 사이
+
+| 쓰는 쪽 | 쓰이는 쪽 | 무엇 |
+|---|---|---|
+| 주문 | 상품 | 주문 항목 팩토리가 상품 저장소에서 상품 이름과 가격을 읽는다 |
+| 주문 | 회원 | 할인 정책과 주문 응용 서비스가 회원과 회원 등급을 읽는다 |
+| 상품 | 주문 | 주문의 이벤트 처리기 EventHandler를 구현한 재고 줄이기 처리기가 주문 접수됨 이벤트를 받아 재고를 줄인다 |
+| 주문, 상품 | 공유 커널 | 돈 |
+
+- 같은 상품이라도 상품 컨텍스트의 상품은 지금 가격과 재고를 갖는다
+- 주문 컨텍스트의 주문 항목은 주문 때의 상품 이름과 가격을 갖는다
+- 지금은 다른 컨텍스트의 클래스를 import로 바로 쓴다
+- 컨텍스트 사이를 정해진 창구로 잇는 일은 뒤 장에서 한다
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index d101e00..f8900c1 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -2,25 +2,25 @@ package ddd;
 
 import java.util.List;
 
-import ddd.application.EventPublisher;
-import ddd.application.OrderApplicationService;
-import ddd.application.OrderLineCommand;
-import ddd.application.PlaceOrderCommand;
 import ddd.catalog.application.DecreaseStockHandler;
 import ddd.catalog.domain.Product;
 import ddd.catalog.domain.ProductRepository;
 import ddd.catalog.infrastructure.MemoryProductRepository;
-import ddd.domain.DiscountPolicy;
-import ddd.domain.Order;
-import ddd.domain.OrderLine;
-import ddd.domain.OrderLineFactory;
-import ddd.domain.OrderPlaced;
-import ddd.domain.OrderRepository;
-import ddd.infrastructure.MemoryOrderRepository;
 import ddd.member.domain.Member;
 import ddd.member.domain.MemberGrade;
 import ddd.member.domain.MemberRepository;
 import ddd.member.infrastructure.MemoryMemberRepository;
+import ddd.order.application.EventPublisher;
+import ddd.order.application.OrderApplicationService;
+import ddd.order.application.OrderLineCommand;
+import ddd.order.application.PlaceOrderCommand;
+import ddd.order.domain.DiscountPolicy;
+import ddd.order.domain.Order;
+import ddd.order.domain.OrderLine;
+import ddd.order.domain.OrderLineFactory;
+import ddd.order.domain.OrderPlaced;
+import ddd.order.domain.OrderRepository;
+import ddd.order.infrastructure.MemoryOrderRepository;
 import ddd.shared.Money;
 
 public class Main {
diff --git a/ddd/src/ddd/catalog/application/DecreaseStockHandler.java b/ddd/src/ddd/catalog/application/DecreaseStockHandler.java
index 4b901db..94676f5 100644
--- a/ddd/src/ddd/catalog/application/DecreaseStockHandler.java
+++ b/ddd/src/ddd/catalog/application/DecreaseStockHandler.java
@@ -2,8 +2,8 @@ package ddd.catalog.application;
 
 import ddd.catalog.domain.Product;
 import ddd.catalog.domain.ProductRepository;
-import ddd.domain.OrderLine;
-import ddd.domain.OrderPlaced;
+import ddd.order.domain.OrderLine;
+import ddd.order.domain.OrderPlaced;
 
 public class DecreaseStockHandler {
 
diff --git a/ddd/src/ddd/application/EventPublisher.java b/ddd/src/ddd/order/application/EventPublisher.java
similarity index 96%
rename from ddd/src/ddd/application/EventPublisher.java
rename to ddd/src/ddd/order/application/EventPublisher.java
index 14fa5c4..8f0141c 100644
--- a/ddd/src/ddd/application/EventPublisher.java
+++ b/ddd/src/ddd/order/application/EventPublisher.java
@@ -1,4 +1,4 @@
-package ddd.application;
+package ddd.order.application;
 
 import java.util.ArrayList;
 import java.util.HashMap;
diff --git a/ddd/src/ddd/application/OrderApplicationService.java b/ddd/src/ddd/order/application/OrderApplicationService.java
similarity index 91%
rename from ddd/src/ddd/application/OrderApplicationService.java
rename to ddd/src/ddd/order/application/OrderApplicationService.java
index add0491..a222533 100644
--- a/ddd/src/ddd/application/OrderApplicationService.java
+++ b/ddd/src/ddd/order/application/OrderApplicationService.java
@@ -1,16 +1,16 @@
-package ddd.application;
+package ddd.order.application;
 
 import java.util.ArrayList;
 import java.util.List;
 
-import ddd.domain.Address;
-import ddd.domain.DiscountPolicy;
-import ddd.domain.Order;
-import ddd.domain.OrderLine;
-import ddd.domain.OrderLineFactory;
-import ddd.domain.OrderRepository;
 import ddd.member.domain.Member;
 import ddd.member.domain.MemberRepository;
+import ddd.order.domain.Address;
+import ddd.order.domain.DiscountPolicy;
+import ddd.order.domain.Order;
+import ddd.order.domain.OrderLine;
+import ddd.order.domain.OrderLineFactory;
+import ddd.order.domain.OrderRepository;
 
 public class OrderApplicationService {
 
diff --git a/ddd/src/ddd/application/OrderLineCommand.java b/ddd/src/ddd/order/application/OrderLineCommand.java
similarity index 68%
rename from ddd/src/ddd/application/OrderLineCommand.java
rename to ddd/src/ddd/order/application/OrderLineCommand.java
index 1be8bc9..984a822 100644
--- a/ddd/src/ddd/application/OrderLineCommand.java
+++ b/ddd/src/ddd/order/application/OrderLineCommand.java
@@ -1,4 +1,4 @@
-package ddd.application;
+package ddd.order.application;
 
 public record OrderLineCommand(long productId, int quantity) {
 }
diff --git a/ddd/src/ddd/application/PlaceOrderCommand.java b/ddd/src/ddd/order/application/PlaceOrderCommand.java
similarity index 80%
rename from ddd/src/ddd/application/PlaceOrderCommand.java
rename to ddd/src/ddd/order/application/PlaceOrderCommand.java
index aff512e..20ab470 100644
--- a/ddd/src/ddd/application/PlaceOrderCommand.java
+++ b/ddd/src/ddd/order/application/PlaceOrderCommand.java
@@ -1,4 +1,4 @@
-package ddd.application;
+package ddd.order.application;
 
 import java.util.List;
 
diff --git a/ddd/src/ddd/domain/Address.java b/ddd/src/ddd/order/domain/Address.java
similarity index 91%
rename from ddd/src/ddd/domain/Address.java
rename to ddd/src/ddd/order/domain/Address.java
index 5cfd21e..9438d6f 100644
--- a/ddd/src/ddd/domain/Address.java
+++ b/ddd/src/ddd/order/domain/Address.java
@@ -1,4 +1,4 @@
-package ddd.domain;
+package ddd.order.domain;
 
 import java.io.Serializable;
 
diff --git a/ddd/src/ddd/domain/DiscountPolicy.java b/ddd/src/ddd/order/domain/DiscountPolicy.java
similarity index 93%
rename from ddd/src/ddd/domain/DiscountPolicy.java
rename to ddd/src/ddd/order/domain/DiscountPolicy.java
index c1a4dac..4a09e7e 100644
--- a/ddd/src/ddd/domain/DiscountPolicy.java
+++ b/ddd/src/ddd/order/domain/DiscountPolicy.java
@@ -1,4 +1,4 @@
-package ddd.domain;
+package ddd.order.domain;
 
 import ddd.member.domain.Member;
 import ddd.member.domain.MemberGrade;
diff --git a/ddd/src/ddd/domain/FreeShippingAreaSpecification.java b/ddd/src/ddd/order/domain/FreeShippingAreaSpecification.java
similarity index 89%
rename from ddd/src/ddd/domain/FreeShippingAreaSpecification.java
rename to ddd/src/ddd/order/domain/FreeShippingAreaSpecification.java
index ff3a793..f161ab5 100644
--- a/ddd/src/ddd/domain/FreeShippingAreaSpecification.java
+++ b/ddd/src/ddd/order/domain/FreeShippingAreaSpecification.java
@@ -1,4 +1,4 @@
-package ddd.domain;
+package ddd.order.domain;
 
 public class FreeShippingAreaSpecification implements OrderSpecification {
 
diff --git a/ddd/src/ddd/domain/FreeShippingPriceSpecification.java b/ddd/src/ddd/order/domain/FreeShippingPriceSpecification.java
similarity index 88%
rename from ddd/src/ddd/domain/FreeShippingPriceSpecification.java
rename to ddd/src/ddd/order/domain/FreeShippingPriceSpecification.java
index 55788b8..478bba7 100644
--- a/ddd/src/ddd/domain/FreeShippingPriceSpecification.java
+++ b/ddd/src/ddd/order/domain/FreeShippingPriceSpecification.java
@@ -1,4 +1,4 @@
-package ddd.domain;
+package ddd.order.domain;
 
 public class FreeShippingPriceSpecification implements OrderSpecification {
 
diff --git a/ddd/src/ddd/domain/FreeShippingSpecification.java b/ddd/src/ddd/order/domain/FreeShippingSpecification.java
similarity index 90%
rename from ddd/src/ddd/domain/FreeShippingSpecification.java
rename to ddd/src/ddd/order/domain/FreeShippingSpecification.java
index 4a6e683..5553203 100644
--- a/ddd/src/ddd/domain/FreeShippingSpecification.java
+++ b/ddd/src/ddd/order/domain/FreeShippingSpecification.java
@@ -1,4 +1,4 @@
-package ddd.domain;
+package ddd.order.domain;
 
 public class FreeShippingSpecification implements OrderSpecification {
 
diff --git a/ddd/src/ddd/domain/Order.java b/ddd/src/ddd/order/domain/Order.java
similarity index 99%
rename from ddd/src/ddd/domain/Order.java
rename to ddd/src/ddd/order/domain/Order.java
index 9182c4e..ccb18c4 100644
--- a/ddd/src/ddd/domain/Order.java
+++ b/ddd/src/ddd/order/domain/Order.java
@@ -1,4 +1,4 @@
-package ddd.domain;
+package ddd.order.domain;
 
 import java.io.Serializable;
 import java.util.ArrayList;
diff --git a/ddd/src/ddd/domain/OrderLine.java b/ddd/src/ddd/order/domain/OrderLine.java
similarity index 93%
rename from ddd/src/ddd/domain/OrderLine.java
rename to ddd/src/ddd/order/domain/OrderLine.java
index 42378f7..3cce35f 100644
--- a/ddd/src/ddd/domain/OrderLine.java
+++ b/ddd/src/ddd/order/domain/OrderLine.java
@@ -1,4 +1,4 @@
-package ddd.domain;
+package ddd.order.domain;
 
 import java.io.Serializable;
 
diff --git a/ddd/src/ddd/domain/OrderLineFactory.java b/ddd/src/ddd/order/domain/OrderLineFactory.java
similarity index 95%
rename from ddd/src/ddd/domain/OrderLineFactory.java
rename to ddd/src/ddd/order/domain/OrderLineFactory.java
index 4eec622..bb94bfe 100644
--- a/ddd/src/ddd/domain/OrderLineFactory.java
+++ b/ddd/src/ddd/order/domain/OrderLineFactory.java
@@ -1,4 +1,4 @@
-package ddd.domain;
+package ddd.order.domain;
 
 import ddd.catalog.domain.Product;
 import ddd.catalog.domain.ProductRepository;
diff --git a/ddd/src/ddd/domain/OrderPlaced.java b/ddd/src/ddd/order/domain/OrderPlaced.java
similarity index 85%
rename from ddd/src/ddd/domain/OrderPlaced.java
rename to ddd/src/ddd/order/domain/OrderPlaced.java
index 9a22909..2b0c251 100644
--- a/ddd/src/ddd/domain/OrderPlaced.java
+++ b/ddd/src/ddd/order/domain/OrderPlaced.java
@@ -1,4 +1,4 @@
-package ddd.domain;
+package ddd.order.domain;
 
 import java.io.Serializable;
 import java.util.List;
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
 
diff --git a/ddd/src/ddd/domain/OrderSpecification.java b/ddd/src/ddd/order/domain/OrderSpecification.java
similarity index 89%
rename from ddd/src/ddd/domain/OrderSpecification.java
rename to ddd/src/ddd/order/domain/OrderSpecification.java
index 18baf69..256d7f3 100644
--- a/ddd/src/ddd/domain/OrderSpecification.java
+++ b/ddd/src/ddd/order/domain/OrderSpecification.java
@@ -1,4 +1,4 @@
-package ddd.domain;
+package ddd.order.domain;
 
 public interface OrderSpecification {
 
diff --git a/ddd/src/ddd/domain/OrderStatus.java b/ddd/src/ddd/order/domain/OrderStatus.java
similarity index 96%
rename from ddd/src/ddd/domain/OrderStatus.java
rename to ddd/src/ddd/order/domain/OrderStatus.java
index 486e63f..173344c 100644
--- a/ddd/src/ddd/domain/OrderStatus.java
+++ b/ddd/src/ddd/order/domain/OrderStatus.java
@@ -1,4 +1,4 @@
-package ddd.domain;
+package ddd.order.domain;
 
 public enum OrderStatus {
     ORDERED("주문 접수"),
diff --git a/ddd/src/ddd/infrastructure/MemoryOrderRepository.java b/ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java
similarity index 93%
rename from ddd/src/ddd/infrastructure/MemoryOrderRepository.java
rename to ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java
index 4eb0106..1f490a2 100644
--- a/ddd/src/ddd/infrastructure/MemoryOrderRepository.java
+++ b/ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java
@@ -1,4 +1,4 @@
-package ddd.infrastructure;
+package ddd.order.infrastructure;
 
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
@@ -9,8 +9,8 @@ import java.util.HashMap;
 import java.util.Map;
 import java.util.Optional;
 
-import ddd.domain.Order;
-import ddd.domain.OrderRepository;
+import ddd.order.domain.Order;
+import ddd.order.domain.OrderRepository;
 
 public class MemoryOrderRepository implements OrderRepository {
 
```

## T6 공유 커널 빼고 회원과 상품만 옮기기
커밋: a312669. 이 장 시점 코드에 얹은 patch는 fixes/a312669.patch

| 파일 | 변경 |
|---|---|
| ddd/context-map.md | 고침 |
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/catalog/application/DecreaseStockHandler.java | 고침 |
| ddd/src/ddd/catalog/domain/Product.java | 고침 |
| ddd/src/ddd/order/application/EventHandler.java | 새로 |
| ddd/src/ddd/order/application/EventPublisher.java | 고침 |
| ddd/src/ddd/order/application/OrderItemRequest.java | 새로 |
| ddd/src/ddd/order/application/OrderLineCommand.java | 지움 |
| ddd/src/ddd/order/application/OrderRequest.java | 새로 |
| ddd/src/ddd/order/application/OrderService.java | 옮김. ddd/src/ddd/order/application/OrderApplicationService.java에서 옮기고 고침 |
| ddd/src/ddd/order/application/PlaceOrderCommand.java | 지움 |
| ddd/src/ddd/order/domain/Address.java | 고침 |
| ddd/src/ddd/order/domain/DiscountPolicy.java | 고침 |
| ddd/src/ddd/order/domain/FreeShippingPriceSpecification.java | 고침 |
| ddd/src/ddd/order/domain/Order.java | 고침 |
| ddd/src/ddd/order/domain/OrderItem.java | 새로 |
| ddd/src/ddd/order/domain/OrderItemFactory.java | 옮김. ddd/src/ddd/order/domain/OrderLineFactory.java에서 옮기고 고침 |
| ddd/src/ddd/order/domain/OrderLine.java | 지움 |
| ddd/src/ddd/order/domain/OrderPlaced.java | 고침 |
| ddd/src/ddd/order/domain/OrderStatus.java | 고침 |
| ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java | 고침 |
| ddd/src/ddd/shared/Money.java | 지움 |

```diff
diff --git a/ddd/context-map.md b/ddd/context-map.md
index bed918d..b08ad82 100644
--- a/ddd/context-map.md
+++ b/ddd/context-map.md
@@ -13,12 +13,6 @@
 - 이 프로그램은 하위 도메인 하나에 컨텍스트 하나를 둔다
 - 컨텍스트 안에는 domain, application, infrastructure 계층을 둔다
 
-## 공유 커널
-
-- 돈 Money는 공유 커널(shared kernel) 묶음 ddd.shared에 둔다
-- 주문과 상품 컨텍스트가 함께 쓴다
-- 바꾸려면 쓰는 컨텍스트가 모두 합의해야 한다. 그래서 작게 둔다
-
 ## 컨텍스트 사이
 
 | 쓰는 쪽 | 쓰이는 쪽 | 무엇 |
@@ -26,7 +20,6 @@
 | 주문 | 상품 | 주문 항목 팩토리가 상품 저장소에서 상품 이름과 가격을 읽는다 |
 | 주문 | 회원 | 할인 정책과 주문 응용 서비스가 회원과 회원 등급을 읽는다 |
 | 상품 | 주문 | 주문의 이벤트 처리기 EventHandler를 구현한 재고 줄이기 처리기가 주문 접수됨 이벤트를 받아 재고를 줄인다 |
-| 주문, 상품 | 공유 커널 | 돈 |
 
 - 같은 상품이라도 상품 컨텍스트의 상품은 지금 가격과 재고를 갖는다
 - 주문 컨텍스트의 주문 항목은 주문 때의 상품 이름과 가격을 갖는다
```

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index 54306d8..cd26b4e 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -3,16 +3,15 @@
 | 업무 말 | 코드 이름 | 뜻 |
 |---|---|---|
 | 주문 | Order | 고객이 상품을 사겠다고 한 요청 |
-| 주문 항목 | OrderLine | 주문한 상품 하나의 상품 번호와 주문 때의 상품 이름, 가격, 수량 |
+| 주문 항목 | OrderItem | 주문한 상품 하나의 상품 번호와 주문 때의 상품 이름, 가격, 수량. 가격은 0원 이상, 수량은 1개 이상이다 |
 | 항목 금액 | amount | 주문 항목의 가격 곱하기 수량 |
 | 주문 번호 | id | 주문마다 다른 번호. 번호가 같으면 같은 주문이다 |
 | 회원 번호 | memberId | 주문한 회원의 번호. 주문은 회원을 객체로 갖지 않고 이 번호로 가리킨다 |
 | 배송 주소 | address | 상품을 받을 곳 |
 | 주소 | Address | 비어 있지 않은 배송 주소. 글자가 같으면 같은 주소다 |
 | 주문 금액 | totalPrice | 주문 항목마다 항목 금액을 더한 합 |
-| 돈 | Money | 0원 이상인 금액. 금액이 같으면 같은 돈이다 |
 | 주문하기 | placeOrder | 회원 번호, 주문 항목, 배송 주소로 주문을 접수한다 |
-| 주문 항목 더하기 | addOrderLine | 주문 접수인 주문에 주문 항목을 더하고 주문 금액도 함께 바꾼다 |
+| 주문 항목 더하기 | addItem | 주문 접수인 주문에 주문 항목을 더하고 주문 금액도 함께 바꾼다 |
 | 결제하기 | pay | 주문 금액을 치러 결제 완료가 된다 |
 | 배송하기 | ship | 결제 완료인 주문을 보내 배송 중이 된다 |
 | 배송 완료하기 | deliver | 배송 중인 주문이 고객에게 닿아 배송 완료가 된다 |
@@ -27,15 +26,15 @@
 | 저장하기 | save | 주문을 주문 저장소에 넣는다. 같은 번호가 있으면 새 주문으로 바꾼다. 저장된 주문과 버전이 다르면 다른 곳에서 먼저 바꾼 주문이라 거절한다 |
 | 주문 번호로 찾기 | findById | 주문 번호로 주문 저장소에서 주문을 꺼낸다. 없으면 빈 결과다 |
 | 버전 | version | 주문이 주문 저장소에 몇 번 저장됐는지. 새 주문은 0이고 저장할 때마다 1씩 오른다. 꺼낸 주문은 저장된 버전을 가진다 |
-| 주문 응용 서비스 | OrderApplicationService | 주문 유스케이스를 하는 곳. 주문을 꺼내고, 주문에게 시키고, 저장한다. 규칙은 갖지 않는다 |
-| 주문하기 명령 | PlaceOrderCommand | 주문하기에 넣는 회원 번호, 주문 항목 명령 목록, 배송 주소 묶음 |
-| 주문 항목 명령 | OrderLineCommand | 주문 항목을 만들 상품 번호, 수량 묶음 |
+| 주문 응용 서비스 | OrderService | 주문 유스케이스를 하는 곳. 주문을 꺼내고, 주문에게 시키고, 저장한다. 규칙은 갖지 않는다 |
+| 주문 요청 | OrderRequest | 주문하기에 넣는 회원 번호, 주문 항목 요청 목록, 배송 주소 묶음 |
+| 주문 항목 요청 | OrderItemRequest | 주문 항목을 만들 상품 번호, 수량 묶음 |
 | 회원 | Member | 상품을 주문하는 사람. 회원 번호, 이름, 회원 등급을 가진다. 회원 번호가 같으면 같은 회원이다 |
 | 회원 저장소 | MemberRepository | 회원을 넣어 두고 회원 번호로 다시 꺼내는 곳 |
 | 상품 | Product | 파는 물건. 상품 번호, 이름, 가격, 재고를 가진다. 상품 번호가 같으면 같은 상품이다 |
 | 상품 저장소 | ProductRepository | 상품을 넣어 두고 상품 번호로 다시 꺼내는 곳 |
 | 상품 번호 | productId | 주문 항목이 가리키는 상품의 번호. 주문 항목은 상품을 객체로 갖지 않고 이 번호로 가리킨다 |
-| 주문 항목 팩토리 | OrderLineFactory | 상품 번호와 수량을 받아 상품 저장소에서 상품을 찾고, 그 상품의 이름과 지금 가격으로 주문 항목을 만드는 곳 |
+| 주문 항목 팩토리 | OrderItemFactory | 상품 번호와 수량을 받아 상품 저장소에서 상품을 찾고, 그 상품의 이름과 지금 가격으로 주문 항목을 만드는 곳 |
 | 회원 등급 | MemberGrade | 회원이 어떤 회원인지 나눈 것. 일반과 VIP 중 하나다 |
 | 일반 | BASIC | 할인이 없는 회원 등급 |
 | VIP | VIP | 할인을 받는 회원 등급 |
@@ -49,6 +48,7 @@
 | 무료 배송 지역 명세 | FreeShippingAreaSpecification | 배송 주소가 제주가 아닌지 답하는 명세 |
 | 재고 | stock | 상품이 지금 몇 개 남았는지. 주문하면 나중에 주문 수량만큼 줄어든다 |
 | 주문 접수됨 | OrderPlaced | 주문이 만들어질 때 주문이 남기는 도메인 이벤트. 주문 번호와 주문 항목을 담는다. 이미 일어난 일이라 바뀌지 않는다 |
-| 이벤트 발행기 | EventPublisher | 도메인 이벤트를 받아 쌓아 두었다가, 이벤트를 처리할 때 그 이벤트를 구독한 곳에 보내는 곳. 주문 응용 서비스는 주문을 저장한 뒤 이벤트를 여기에 넘긴다 |
+| 이벤트 발행기 | EventPublisher | 도메인 이벤트를 받아 쌓아 두었다가, 이벤트를 처리할 때 구독한 처리기마다 보내는 곳. 주문 응용 서비스는 주문을 저장한 뒤 이벤트를 여기에 넘긴다 |
 | 재고 줄이기 | decreaseStock | 상품 재고를 주문 수량만큼 뺀다 |
+| 이벤트 처리기 | EventHandler | 이벤트 발행기에 구독하는 곳. 받은 이벤트 중 자기 것만 골라 처리한다 |
 | 재고 줄이기 처리기 | DecreaseStockHandler | 주문 접수됨 이벤트를 구독해 주문 항목마다 상품 재고를 줄이고 저장하는 곳. 주문 저장과 따로 일해서 재고는 이벤트를 처리한 뒤에 맞춰진다 |
```

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index f8900c1..3369146 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -11,183 +11,42 @@ import ddd.member.domain.MemberGrade;
 import ddd.member.domain.MemberRepository;
 import ddd.member.infrastructure.MemoryMemberRepository;
 import ddd.order.application.EventPublisher;
-import ddd.order.application.OrderApplicationService;
-import ddd.order.application.OrderLineCommand;
-import ddd.order.application.PlaceOrderCommand;
+import ddd.order.application.OrderItemRequest;
+import ddd.order.application.OrderRequest;
+import ddd.order.application.OrderService;
 import ddd.order.domain.DiscountPolicy;
 import ddd.order.domain.Order;
-import ddd.order.domain.OrderLine;
-import ddd.order.domain.OrderLineFactory;
-import ddd.order.domain.OrderPlaced;
-import ddd.order.domain.OrderRepository;
+import ddd.order.domain.OrderItemFactory;
 import ddd.order.infrastructure.MemoryOrderRepository;
-import ddd.shared.Money;
 
 public class Main {
 
     public static void main(String[] args) {
-        System.out.println("온라인 주문 프로그램을 시작합니다");
+        System.out.println("===== 컨텍스트 나누기 =====");
 
         MemberRepository memberRepository = new MemoryMemberRepository();
-        memberRepository.save(new Member(1, "김철수", MemberGrade.VIP));
-        memberRepository.save(new Member(2, "이영희", MemberGrade.BASIC));
+        memberRepository.save(new Member(1, "김철수", MemberGrade.BASIC));
 
         ProductRepository productRepository = new MemoryProductRepository();
-        productRepository.save(new Product(1, "자바 책", new Money(15000), 10));
-        productRepository.save(new Product(2, "볼펜", new Money(1000), 10));
-        productRepository.save(new Product(3, "공책", new Money(2000), 10));
+        productRepository.save(new Product(1, "자바 책", 15000, 10));
 
         EventPublisher eventPublisher = new EventPublisher();
-        DecreaseStockHandler decreaseStockHandler = new DecreaseStockHandler(productRepository);
-        eventPublisher.subscribe(OrderPlaced.class, decreaseStockHandler::handle);
-        OrderRepository orderRepository = new MemoryOrderRepository();
-        OrderApplicationService orderService = new OrderApplicationService(orderRepository,
-                new OrderLineFactory(productRepository), memberRepository, new DiscountPolicy(), eventPublisher);
-
-        List<OrderLineCommand> orderLines = List.of(
-                new OrderLineCommand(1, 2),
-                new OrderLineCommand(2, 1));
-        long orderId = orderService.placeOrder(new PlaceOrderCommand(1, orderLines, "서울시 강남구"));
+        eventPublisher.subscribe(new DecreaseStockHandler(productRepository));
+        OrderService orderService = new OrderService(new MemoryOrderRepository(),
+                new OrderItemFactory(productRepository), memberRepository, new DiscountPolicy(), eventPublisher);
+
+        System.out.println();
+        System.out.println("[세 컨텍스트를 거쳐 주문하기]");
+        int stockBefore = productRepository.findById(1).orElseThrow().getStock();
+        long orderId = orderService.placeOrder(new OrderRequest(1,
+                List.of(new OrderItemRequest(1, 2)), "서울시 강남구"));
+        eventPublisher.processEvents();
         Order order = orderService.findOrder(orderId);
         Member member = memberRepository.findById(order.getMemberId()).orElseThrow();
-        System.out.println("주문: " + order.getId() + "번, " + member.getName() + ", "
-                + order.getTotalPrice().amount() + "원, " + order.getStatus().label());
-        for (OrderLine orderLine : order.getOrderLines()) {
-            System.out.println("주문 항목: " + orderLine.productName() + " " + orderLine.quantity() + "개, "
-                    + orderLine.amount().amount() + "원");
-        }
-        System.out.println("주문 뒤: 이벤트 " + order.getEvents().size() + "개, 자바 책 재고 "
-                + productRepository.findById(1).orElseThrow().getStock() + "개");
-        eventPublisher.processEvents();
-        System.out.println("이벤트 처리 뒤: 자바 책 재고 " + productRepository.findById(1).orElseThrow().getStock() + "개");
-
-        orderService.pay(orderId);
-        System.out.println("결제: " + orderService.findOrder(orderId).getStatus().label());
-        Order paidOrder = orderService.findOrder(orderId);
-        System.out.println("할인: " + member.getGrade() + " " + paidOrder.getDiscountPrice().amount() + "원, 결제 금액 "
-                + paidOrder.getPaymentPrice().amount() + "원");
-        System.out.println("배송비: " + paidOrder.getShippingFee().amount() + "원");
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
-        long order2Id = orderService.placeOrder(new PlaceOrderCommand(2,
-                List.of(new OrderLineCommand(1, 1)), "부산시 해운대구"));
-        try {
-            orderService.ship(order2Id);
-            System.out.println("결제 없이 배송: " + orderService.findOrder(order2Id).getStatus().label());
-        } catch (IllegalStateException e) {
-            System.out.println("결제 없이 배송: " + e.getMessage());
-        }
-
-        try {
-            productRepository.save(new Product(4, "할인 책", new Money(-15000), 10));
-            long order3Id = orderService.placeOrder(new PlaceOrderCommand(2,
-                    List.of(new OrderLineCommand(4, 2)), "대구시 수성구"));
-            System.out.println("음수 금액 주문: " + orderService.findOrder(order3Id).getTotalPrice().amount() + "원");
-        } catch (IllegalArgumentException e) {
-            System.out.println("음수 금액 주문: " + e.getMessage());
-        }
-
-        try {
-            long order4Id = orderService.placeOrder(new PlaceOrderCommand(2,
-                    List.of(new OrderLineCommand(1, 1)), ""));
-            System.out.println("빈 주소 주문: " + orderService.findOrder(order4Id).getStatus().label());
-        } catch (IllegalArgumentException e) {
-            System.out.println("빈 주소 주문: " + e.getMessage());
-        }
-
-        long order5Id = orderService.placeOrder(new PlaceOrderCommand(2, List.of(
-                new OrderLineCommand(1, 2),
-                new OrderLineCommand(2, 1)), "광주시 북구"));
-        orderService.addOrderLine(order5Id, new OrderLineCommand(3, 5));
-        Order order5 = orderService.findOrder(order5Id);
-        System.out.println("항목 더하기: 항목 " + order5.getOrderLines().size() + "개, 주문 금액 "
-                + order5.getTotalPrice().amount() + "원");
-
-        orderService.pay(order5Id);
-        try {
-            orderService.addOrderLine(order5Id, new OrderLineCommand(3, 1));
-            System.out.println("결제 뒤 항목 더하기: 항목 " + orderService.findOrder(order5Id).getOrderLines().size() + "개");
-        } catch (IllegalStateException e) {
-            System.out.println("결제 뒤 항목 더하기: " + e.getMessage());
-        }
-
-        try {
-            long order6Id = orderService.placeOrder(new PlaceOrderCommand(2, List.of(), "인천시 연수구"));
-            System.out.println("빈 주문: " + orderService.findOrder(order6Id).getStatus().label());
-        } catch (IllegalArgumentException e) {
-            System.out.println("빈 주문: " + e.getMessage());
-        }
-
-        try {
-            long order7Id = orderService.placeOrder(new PlaceOrderCommand(2,
-                    List.of(new OrderLineCommand(1, 0)), "대전시 유성구"));
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
-        try {
-            long order8Id = orderService.placeOrder(new PlaceOrderCommand(2,
-                    List.of(new OrderLineCommand(100, 1)), "울산시 남구"));
-            System.out.println("없는 상품 주문: " + orderService.findOrder(order8Id).getStatus().label());
-        } catch (IllegalArgumentException e) {
-            System.out.println("없는 상품 주문: " + e.getMessage());
-        }
-
-        Order notSaved = orderService.findOrder(order2Id);
-        notSaved.cancel();
-        System.out.println("저장 안 한 취소: " + orderService.findOrder(order2Id).getStatus().label());
-
-        long seoulOrderId = orderService.placeOrder(new PlaceOrderCommand(2,
-                List.of(new OrderLineCommand(1, 4)), "서울시 마포구"));
-        long jejuOrderId = orderService.placeOrder(new PlaceOrderCommand(2,
-                List.of(new OrderLineCommand(1, 4)), "제주시 연동"));
-        System.out.println("6만원 주문 배송비: 서울 " + orderService.findOrder(seoulOrderId).getShippingFee().amount()
-                + "원, 제주 " + orderService.findOrder(jejuOrderId).getShippingFee().amount() + "원");
-
-        productRepository.save(new Product(1, "자바 책", new Money(18000), 10));
-        Order firstOrder = orderService.findOrder(orderId);
-        OrderLine bookLine = firstOrder.getOrderLines().get(0);
-        Product book = productRepository.findById(bookLine.productId()).orElseThrow();
-        System.out.println("가격 바뀐 뒤: " + book.getId() + "번 상품 " + book.getPrice().amount() + "원, 주문 항목 "
-                + bookLine.price().amount() + "원, 주문 금액 " + firstOrder.getTotalPrice().amount() + "원");
-
-        Order orderA = orderService.findOrder(order5Id);
-        Order orderB = orderService.findOrder(order5Id);
-        orderA.ship();
-        orderRepository.save(orderA);
-        try {
-            orderB.cancel();
-            orderRepository.save(orderB);
-            System.out.println("동시 수정: " + orderService.findOrder(order5Id).getStatus().label());
-        } catch (IllegalStateException e) {
-            System.out.println("동시 수정: " + e.getMessage());
-        }
+        int stockAfter = productRepository.findById(1).orElseThrow().getStock();
+        System.out.println("주문 컨텍스트: " + orderId + "번 주문, " + order.getTotalPrice() + "원, "
+                + order.getStatus().label());
+        System.out.println("회원 컨텍스트: " + member.getName() + ", " + member.getGrade());
+        System.out.println("상품 컨텍스트: 자바 책 재고 " + stockBefore + "개 -> " + stockAfter + "개");
     }
 }
```

```diff
diff --git a/ddd/src/ddd/catalog/application/DecreaseStockHandler.java b/ddd/src/ddd/catalog/application/DecreaseStockHandler.java
index 94676f5..23ee84a 100644
--- a/ddd/src/ddd/catalog/application/DecreaseStockHandler.java
+++ b/ddd/src/ddd/catalog/application/DecreaseStockHandler.java
@@ -2,10 +2,11 @@ package ddd.catalog.application;
 
 import ddd.catalog.domain.Product;
 import ddd.catalog.domain.ProductRepository;
-import ddd.order.domain.OrderLine;
+import ddd.order.application.EventHandler;
+import ddd.order.domain.OrderItem;
 import ddd.order.domain.OrderPlaced;
 
-public class DecreaseStockHandler {
+public class DecreaseStockHandler implements EventHandler {
 
     private final ProductRepository productRepository;
 
@@ -13,10 +14,14 @@ public class DecreaseStockHandler {
         this.productRepository = productRepository;
     }
 
-    public void handle(OrderPlaced event) {
-        for (OrderLine orderLine : event.orderLines()) {
-            Product product = productRepository.findById(orderLine.productId()).orElseThrow();
-            product.decreaseStock(orderLine.quantity());
+    @Override
+    public void handle(Object event) {
+        if (!(event instanceof OrderPlaced placed)) {
+            return;
+        }
+        for (OrderItem item : placed.items()) {
+            Product product = productRepository.findById(item.getProductId()).orElseThrow();
+            product.decreaseStock(item.getQuantity());
             productRepository.save(product);
         }
     }
```

```diff
diff --git a/ddd/src/ddd/catalog/domain/Product.java b/ddd/src/ddd/catalog/domain/Product.java
index b14d4bd..71c4a96 100644
--- a/ddd/src/ddd/catalog/domain/Product.java
+++ b/ddd/src/ddd/catalog/domain/Product.java
@@ -1,15 +1,13 @@
 package ddd.catalog.domain;
 
-import ddd.shared.Money;
-
 public class Product {
 
     private final long id;
     private final String name;
-    private final Money price;
+    private final int price;
     private int stock;
 
-    public Product(long id, String name, Money price, int stock) {
+    public Product(long id, String name, int price, int stock) {
         this.id = id;
         this.name = name;
         this.price = price;
@@ -24,7 +22,7 @@ public class Product {
         return name;
     }
 
-    public Money getPrice() {
+    public int getPrice() {
         return price;
     }
 
```

```diff
diff --git a/ddd/src/ddd/order/application/EventHandler.java b/ddd/src/ddd/order/application/EventHandler.java
new file mode 100644
index 0000000..b47b16b
--- /dev/null
+++ b/ddd/src/ddd/order/application/EventHandler.java
@@ -0,0 +1,6 @@
+package ddd.order.application;
+
+public interface EventHandler {
+
+    void handle(Object event);
+}
```

```diff
diff --git a/ddd/src/ddd/order/application/EventPublisher.java b/ddd/src/ddd/order/application/EventPublisher.java
index 8f0141c..cc45d9e 100644
--- a/ddd/src/ddd/order/application/EventPublisher.java
+++ b/ddd/src/ddd/order/application/EventPublisher.java
@@ -1,22 +1,18 @@
 package ddd.order.application;
 
 import java.util.ArrayList;
-import java.util.HashMap;
 import java.util.List;
-import java.util.Map;
-import java.util.function.Consumer;
 
 public class EventPublisher {
 
-    private final Map<Class<?>, List<Consumer<Object>>> subscribers = new HashMap<>();
+    private final List<EventHandler> handlers = new ArrayList<>();
     private final List<Object> waitingEvents = new ArrayList<>();
 
-    public <T> void subscribe(Class<T> eventType, Consumer<T> subscriber) {
-        subscribers.computeIfAbsent(eventType, type -> new ArrayList<>())
-                .add(event -> subscriber.accept(eventType.cast(event)));
+    public void subscribe(EventHandler handler) {
+        handlers.add(handler);
     }
 
-    public void publish(List<?> events) {
+    public void publish(List<Object> events) {
         waitingEvents.addAll(events);
     }
 
@@ -24,8 +20,8 @@ public class EventPublisher {
         List<Object> events = new ArrayList<>(waitingEvents);
         waitingEvents.clear();
         for (Object event : events) {
-            for (Consumer<Object> subscriber : subscribers.getOrDefault(event.getClass(), List.of())) {
-                subscriber.accept(event);
+            for (EventHandler handler : handlers) {
+                handler.handle(event);
             }
         }
     }
```

```diff
diff --git a/ddd/src/ddd/order/application/OrderItemRequest.java b/ddd/src/ddd/order/application/OrderItemRequest.java
new file mode 100644
index 0000000..4fd18a5
--- /dev/null
+++ b/ddd/src/ddd/order/application/OrderItemRequest.java
@@ -0,0 +1,4 @@
+package ddd.order.application;
+
+public record OrderItemRequest(long productId, int quantity) {
+}
```

```diff
diff --git a/ddd/src/ddd/order/application/OrderLineCommand.java b/ddd/src/ddd/order/application/OrderLineCommand.java
deleted file mode 100644
index 984a822..0000000
--- a/ddd/src/ddd/order/application/OrderLineCommand.java
+++ /dev/null
@@ -1,4 +0,0 @@
-package ddd.order.application;
-
-public record OrderLineCommand(long productId, int quantity) {
-}
```

```diff
diff --git a/ddd/src/ddd/order/application/OrderRequest.java b/ddd/src/ddd/order/application/OrderRequest.java
new file mode 100644
index 0000000..adc71a1
--- /dev/null
+++ b/ddd/src/ddd/order/application/OrderRequest.java
@@ -0,0 +1,6 @@
+package ddd.order.application;
+
+import java.util.List;
+
+public record OrderRequest(long memberId, List<OrderItemRequest> items, String address) {
+}
```

```diff
diff --git a/ddd/src/ddd/order/application/OrderApplicationService.java b/ddd/src/ddd/order/application/OrderService.java
similarity index 68%
rename from ddd/src/ddd/order/application/OrderApplicationService.java
rename to ddd/src/ddd/order/application/OrderService.java
index a222533..7f74276 100644
--- a/ddd/src/ddd/order/application/OrderApplicationService.java
+++ b/ddd/src/ddd/order/application/OrderService.java
@@ -8,34 +8,34 @@ import ddd.member.domain.MemberRepository;
 import ddd.order.domain.Address;
 import ddd.order.domain.DiscountPolicy;
 import ddd.order.domain.Order;
-import ddd.order.domain.OrderLine;
-import ddd.order.domain.OrderLineFactory;
+import ddd.order.domain.OrderItem;
+import ddd.order.domain.OrderItemFactory;
 import ddd.order.domain.OrderRepository;
 
-public class OrderApplicationService {
+public class OrderService {
 
     private final OrderRepository orderRepository;
-    private final OrderLineFactory orderLineFactory;
+    private final OrderItemFactory orderItemFactory;
     private final MemberRepository memberRepository;
     private final DiscountPolicy discountPolicy;
     private final EventPublisher eventPublisher;
     private long nextId = 1;
 
-    public OrderApplicationService(OrderRepository orderRepository, OrderLineFactory orderLineFactory,
+    public OrderService(OrderRepository orderRepository, OrderItemFactory orderItemFactory,
             MemberRepository memberRepository, DiscountPolicy discountPolicy, EventPublisher eventPublisher) {
         this.orderRepository = orderRepository;
-        this.orderLineFactory = orderLineFactory;
+        this.orderItemFactory = orderItemFactory;
         this.memberRepository = memberRepository;
         this.discountPolicy = discountPolicy;
         this.eventPublisher = eventPublisher;
     }
 
-    public long placeOrder(PlaceOrderCommand command) {
-        List<OrderLine> orderLines = new ArrayList<>();
-        for (OrderLineCommand orderLineCommand : command.orderLines()) {
-            orderLines.add(orderLineFactory.create(orderLineCommand.productId(), orderLineCommand.quantity()));
+    public long placeOrder(OrderRequest request) {
+        List<OrderItem> items = new ArrayList<>();
+        for (OrderItemRequest itemRequest : request.items()) {
+            items.add(orderItemFactory.create(itemRequest.productId(), itemRequest.quantity()));
         }
-        Order order = new Order(nextId++, command.memberId(), orderLines, new Address(command.address()));
+        Order order = new Order(nextId++, request.memberId(), items, new Address(request.address()));
         orderRepository.save(order);
         eventPublisher.publish(order.getEvents());
         return order.getId();
@@ -45,9 +45,9 @@ public class OrderApplicationService {
         return orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("주문이 없습니다"));
     }
 
-    public void addOrderLine(long orderId, OrderLineCommand command) {
+    public void addItem(long orderId, OrderItemRequest request) {
         Order order = findOrder(orderId);
-        order.addOrderLine(orderLineFactory.create(command.productId(), command.quantity()));
+        order.addItem(orderItemFactory.create(request.productId(), request.quantity()));
         orderRepository.save(order);
     }
 
```

```diff
diff --git a/ddd/src/ddd/order/application/PlaceOrderCommand.java b/ddd/src/ddd/order/application/PlaceOrderCommand.java
deleted file mode 100644
index 20ab470..0000000
--- a/ddd/src/ddd/order/application/PlaceOrderCommand.java
+++ /dev/null
@@ -1,6 +0,0 @@
-package ddd.order.application;
-
-import java.util.List;
-
-public record PlaceOrderCommand(long memberId, List<OrderLineCommand> orderLines, String address) {
-}
```

```diff
diff --git a/ddd/src/ddd/order/domain/Address.java b/ddd/src/ddd/order/domain/Address.java
index 9438d6f..2a69b63 100644
--- a/ddd/src/ddd/order/domain/Address.java
+++ b/ddd/src/ddd/order/domain/Address.java
@@ -1,8 +1,6 @@
 package ddd.order.domain;
 
-import java.io.Serializable;
-
-public record Address(String value) implements Serializable {
+public record Address(String value) {
 
     public Address {
         if (value == null || value.isBlank()) {
```

```diff
diff --git a/ddd/src/ddd/order/domain/DiscountPolicy.java b/ddd/src/ddd/order/domain/DiscountPolicy.java
index 4a09e7e..a718d33 100644
--- a/ddd/src/ddd/order/domain/DiscountPolicy.java
+++ b/ddd/src/ddd/order/domain/DiscountPolicy.java
@@ -2,14 +2,13 @@ package ddd.order.domain;
 
 import ddd.member.domain.Member;
 import ddd.member.domain.MemberGrade;
-import ddd.shared.Money;
 
 public class DiscountPolicy {
 
-    public Money discount(Member member, Order order) {
+    public int discount(Member member, Order order) {
         if (member.getGrade() == MemberGrade.VIP) {
-            return new Money(order.getTotalPrice().amount() * 10 / 100);
+            return order.getTotalPrice() * 10 / 100;
         }
-        return new Money(0);
+        return 0;
     }
 }
```

```diff
diff --git a/ddd/src/ddd/order/domain/FreeShippingPriceSpecification.java b/ddd/src/ddd/order/domain/FreeShippingPriceSpecification.java
index 478bba7..c019b08 100644
--- a/ddd/src/ddd/order/domain/FreeShippingPriceSpecification.java
+++ b/ddd/src/ddd/order/domain/FreeShippingPriceSpecification.java
@@ -4,6 +4,6 @@ public class FreeShippingPriceSpecification implements OrderSpecification {
 
     @Override
     public boolean isSatisfiedBy(Order order) {
-        return order.getPaymentPrice().amount() >= 50000;
+        return order.getPaymentPrice() >= 50000;
     }
 }
```

```diff
diff --git a/ddd/src/ddd/order/domain/Order.java b/ddd/src/ddd/order/domain/Order.java
index ccb18c4..2cf2f08 100644
--- a/ddd/src/ddd/order/domain/Order.java
+++ b/ddd/src/ddd/order/domain/Order.java
@@ -1,41 +1,48 @@
 package ddd.order.domain;
 
-import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.List;
 
-import ddd.shared.Money;
-
-public class Order implements Serializable {
-
-    private static final long serialVersionUID = 1L;
+public class Order {
 
     private long id;
     private long memberId;
-    private List<OrderLine> orderLines;
+    private List<OrderItem> items;
     private Address address;
-    private Money totalPrice;
-    private Money discountPrice;
+    private int totalPrice;
+    private int discountPrice;
     private OrderStatus status;
     private List<Object> events;
     private long version;
 
-    public Order(long id, long memberId, List<OrderLine> orderLines, Address address) {
-        if (orderLines.isEmpty()) {
+    public Order(long id, long memberId, List<OrderItem> items, Address address) {
+        if (items.isEmpty()) {
             throw new IllegalArgumentException("주문 항목이 하나 이상 있어야 합니다");
         }
         this.id = id;
         this.memberId = memberId;
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
-        this.discountPrice = new Money(0);
+        this.discountPrice = 0;
         this.status = OrderStatus.ORDERED;
         this.events = new ArrayList<>();
-        events.add(new OrderPlaced(id, List.copyOf(orderLines)));
+        events.add(new OrderPlaced(id, List.copyOf(items)));
+    }
+
+    public Order(Order other) {
+        this.id = other.id;
+        this.memberId = other.memberId;
+        this.items = new ArrayList<>(other.items);
+        this.address = other.address;
+        this.totalPrice = other.totalPrice;
+        this.discountPrice = other.discountPrice;
+        this.status = other.status;
+        this.events = new ArrayList<>(other.events);
+        this.version = other.version;
     }
 
     public long getId() {
@@ -46,31 +53,31 @@ public class Order implements Serializable {
         return memberId;
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
 
-    public Money getDiscountPrice() {
+    public int getDiscountPrice() {
         return discountPrice;
     }
 
-    public Money getPaymentPrice() {
-        return totalPrice.minus(discountPrice);
+    public int getPaymentPrice() {
+        return totalPrice - discountPrice;
     }
 
-    public Money getShippingFee() {
+    public int getShippingFee() {
         if (new FreeShippingSpecification().isSatisfiedBy(this)) {
-            return new Money(0);
+            return 0;
         }
-        return new Money(3000);
+        return 3000;
     }
 
     public OrderStatus getStatus() {
@@ -89,16 +96,16 @@ public class Order implements Serializable {
         version++;
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
 
-    public void pay(Money discountPrice) {
-        if (!status.canChangeTo(OrderStatus.PAID)) {
+    public void pay(int discountPrice) {
+        if (status != OrderStatus.ORDERED) {
             throw new IllegalStateException(status.label() + "인 주문은 결제할 수 없습니다");
         }
         this.discountPrice = discountPrice;
@@ -106,21 +113,21 @@ public class Order implements Serializable {
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
index 0000000..8f19677
--- /dev/null
+++ b/ddd/src/ddd/order/domain/OrderItem.java
@@ -0,0 +1,42 @@
+package ddd.order.domain;
+
+public class OrderItem {
+
+    private final long productId;
+    private final String productName;
+    private final int price;
+    private final int quantity;
+
+    public OrderItem(long productId, String productName, int price, int quantity) {
+        if (price < 0) {
+            throw new IllegalArgumentException("가격은 0원 이상이어야 합니다");
+        }
+        if (quantity < 1) {
+            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다");
+        }
+        this.productId = productId;
+        this.productName = productName;
+        this.price = price;
+        this.quantity = quantity;
+    }
+
+    public long getProductId() {
+        return productId;
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
diff --git a/ddd/src/ddd/order/domain/OrderLineFactory.java b/ddd/src/ddd/order/domain/OrderItemFactory.java
similarity index 65%
rename from ddd/src/ddd/order/domain/OrderLineFactory.java
rename to ddd/src/ddd/order/domain/OrderItemFactory.java
index bb94bfe..4763836 100644
--- a/ddd/src/ddd/order/domain/OrderLineFactory.java
+++ b/ddd/src/ddd/order/domain/OrderItemFactory.java
@@ -3,17 +3,17 @@ package ddd.order.domain;
 import ddd.catalog.domain.Product;
 import ddd.catalog.domain.ProductRepository;
 
-public class OrderLineFactory {
+public class OrderItemFactory {
 
     private final ProductRepository productRepository;
 
-    public OrderLineFactory(ProductRepository productRepository) {
+    public OrderItemFactory(ProductRepository productRepository) {
         this.productRepository = productRepository;
     }
 
-    public OrderLine create(long productId, int quantity) {
+    public OrderItem create(long productId, int quantity) {
         Product product = productRepository.findById(productId)
                 .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다"));
-        return new OrderLine(product.getId(), product.getName(), product.getPrice(), quantity);
+        return new OrderItem(product.getId(), product.getName(), product.getPrice(), quantity);
     }
 }
```

```diff
diff --git a/ddd/src/ddd/order/domain/OrderLine.java b/ddd/src/ddd/order/domain/OrderLine.java
deleted file mode 100644
index 3cce35f..0000000
--- a/ddd/src/ddd/order/domain/OrderLine.java
+++ /dev/null
@@ -1,18 +0,0 @@
-package ddd.order.domain;
-
-import java.io.Serializable;
-
-import ddd.shared.Money;
-
-public record OrderLine(long productId, String productName, Money price, int quantity) implements Serializable {
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
diff --git a/ddd/src/ddd/order/domain/OrderPlaced.java b/ddd/src/ddd/order/domain/OrderPlaced.java
index 2b0c251..4318e5c 100644
--- a/ddd/src/ddd/order/domain/OrderPlaced.java
+++ b/ddd/src/ddd/order/domain/OrderPlaced.java
@@ -1,7 +1,6 @@
 package ddd.order.domain;
 
-import java.io.Serializable;
 import java.util.List;
 
-public record OrderPlaced(long orderId, List<OrderLine> orderLines) implements Serializable {
+public record OrderPlaced(long orderId, List<OrderItem> items) {
 }
```

```diff
diff --git a/ddd/src/ddd/order/domain/OrderStatus.java b/ddd/src/ddd/order/domain/OrderStatus.java
index 173344c..d8965da 100644
--- a/ddd/src/ddd/order/domain/OrderStatus.java
+++ b/ddd/src/ddd/order/domain/OrderStatus.java
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

```diff
diff --git a/ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java b/ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java
index 1f490a2..b56e377 100644
--- a/ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java
+++ b/ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java
@@ -1,10 +1,5 @@
 package ddd.order.infrastructure;
 
-import java.io.ByteArrayInputStream;
-import java.io.ByteArrayOutputStream;
-import java.io.IOException;
-import java.io.ObjectInputStream;
-import java.io.ObjectOutputStream;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Optional;
@@ -23,7 +18,7 @@ public class MemoryOrderRepository implements OrderRepository {
             throw new IllegalStateException("다른 곳에서 먼저 바꾼 주문입니다");
         }
         order.increaseVersion();
-        orders.put(order.getId(), copy(order));
+        orders.put(order.getId(), new Order(order));
     }
 
     @Override
@@ -32,18 +27,6 @@ public class MemoryOrderRepository implements OrderRepository {
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
diff --git a/ddd/src/ddd/shared/Money.java b/ddd/src/ddd/shared/Money.java
deleted file mode 100644
index f5843ee..0000000
--- a/ddd/src/ddd/shared/Money.java
+++ /dev/null
@@ -1,24 +0,0 @@
-package ddd.shared;
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
-
-    public Money minus(Money other) {
-        return new Money(amount - other.amount);
-    }
-}
```
