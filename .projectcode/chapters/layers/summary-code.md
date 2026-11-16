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
