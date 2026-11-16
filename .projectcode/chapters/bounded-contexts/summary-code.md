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
+| 상품 | 주문 | 재고 줄이기 처리기가 주문 접수됨 이벤트를 받아 재고를 줄인다 |
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
