# free-shipping 코드 요약

현재 따라치기 이력에 고침을 녹인 코드다.
Source는 원래 task와 반영한 fix 커밋이다.

## T1 무료 배송 명세로 배송비 정하기
커밋: 710602e
Source: 15ffe4f a312669

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/order/domain/FreeShippingSpecification.java | 새로 |
| ddd/src/ddd/order/domain/Order.java | 고침 |

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index a60ccba..c2ed3a1 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -40,3 +40,5 @@
 | 할인 정책 | DiscountPolicy | 회원 등급과 주문 금액으로 할인 금액을 계산하는 도메인 서비스. VIP는 주문 금액의 10%, 일반은 0원이다. 계산만 하고 주문을 바꾸지 않는다 |
 | 할인 금액 | discountPrice | 결제할 때 주문 금액에서 깎는 돈. 결제 전에는 0원이다 |
 | 결제 금액 | paymentPrice | 주문 금액에서 할인 금액을 뺀 돈. 실제로 치르는 돈이다 |
+| 무료 배송 명세 | FreeShippingSpecification | 주문이 무료 배송인지 참 거짓으로만 답하는 명세. 결제 금액이 50000원 이상이면 참이다 |
+| 배송비 | shippingFee | 주문을 보내는 데 받는 돈. 결제 금액과 따로 받는다. 무료 배송 명세를 만족하면 0원, 아니면 3000원이다 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 630c9f2..1a2b0c7 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -20,44 +20,26 @@ import ddd.order.infrastructure.MemoryProductRepository;
 public class Main {
 
     public static void main(String[] args) {
-        System.out.println("===== 회원 등급 할인 =====");
+        System.out.println("===== 무료 배송 조건 =====");
 
         MemberRepository memberRepository = new MemoryMemberRepository();
-        memberRepository.save(new Member(1, "김철수", MemberGrade.VIP));
-        memberRepository.save(new Member(2, "이영희", MemberGrade.BASIC));
+        memberRepository.save(new Member(1, "김철수", MemberGrade.BASIC));
 
         ProductRepository productRepository = new MemoryProductRepository();
         productRepository.save(new Product(1, "자바 책", 15000));
-        productRepository.save(new Product(2, "볼펜", 1000));
 
         OrderService orderService = new OrderService(new MemoryOrderRepository(),
                 new OrderItemFactory(productRepository), memberRepository, new DiscountPolicy());
 
         System.out.println();
-        System.out.println("[회원 등급 정하기]");
-        Member vip = memberRepository.findById(1).orElseThrow();
-        System.out.println(vip.getName() + " 회원 등급: " + vip.getGrade());
-        Member basic = memberRepository.findById(2).orElseThrow();
-        System.out.println(basic.getName() + " 회원 등급: " + basic.getGrade());
-
-        System.out.println();
-        System.out.println("[할인 정책으로 결제하기]");
-        long vipOrderId = orderService.placeOrder(new OrderRequest(1, List.of(
-                new OrderItemRequest(1, 2),
-                new OrderItemRequest(2, 1)), "서울시 강남구"));
-        int vipPriceBefore = orderService.findOrder(vipOrderId).getPaymentPrice();
-        orderService.pay(vipOrderId);
-        Order vipOrder = orderService.findOrder(vipOrderId);
-        System.out.println("김철수 VIP 결제 금액: " + vipPriceBefore + "원 -> " + vipOrder.getPaymentPrice() + "원, 할인 "
-                + vipOrder.getDiscountPrice() + "원");
-
-        long basicOrderId = orderService.placeOrder(new OrderRequest(2, List.of(
-                new OrderItemRequest(1, 2),
-                new OrderItemRequest(2, 1)), "부산시 해운대구"));
-        int basicPriceBefore = orderService.findOrder(basicOrderId).getPaymentPrice();
-        orderService.pay(basicOrderId);
-        Order basicOrder = orderService.findOrder(basicOrderId);
-        System.out.println("이영희 BASIC 결제 금액: " + basicPriceBefore + "원 -> " + basicOrder.getPaymentPrice()
-                + "원, 할인 " + basicOrder.getDiscountPrice() + "원");
+        System.out.println("[결제 금액으로 배송비 정하기]");
+        long smallOrderId = orderService.placeOrder(new OrderRequest(1,
+                List.of(new OrderItemRequest(1, 2)), "서울시 강남구"));
+        Order smallOrder = orderService.findOrder(smallOrderId);
+        System.out.println(smallOrder.getPaymentPrice() + "원 주문 배송비: " + smallOrder.getShippingFee() + "원");
+        long bigOrderId = orderService.placeOrder(new OrderRequest(1,
+                List.of(new OrderItemRequest(1, 4)), "서울시 강남구"));
+        Order bigOrder = orderService.findOrder(bigOrderId);
+        System.out.println(bigOrder.getPaymentPrice() + "원 주문 배송비: " + bigOrder.getShippingFee() + "원");
     }
 }
diff --git a/ddd/src/ddd/order/domain/FreeShippingSpecification.java b/ddd/src/ddd/order/domain/FreeShippingSpecification.java
new file mode 100644
index 0000000..603d765
--- /dev/null
+++ b/ddd/src/ddd/order/domain/FreeShippingSpecification.java
@@ -0,0 +1,8 @@
+package ddd.order.domain;
+
+public class FreeShippingSpecification {
+
+    public boolean isSatisfiedBy(Order order) {
+        return order.getPaymentPrice() >= 50000;
+    }
+}
diff --git a/ddd/src/ddd/order/domain/Order.java b/ddd/src/ddd/order/domain/Order.java
index 110568f..1dd6192 100644
--- a/ddd/src/ddd/order/domain/Order.java
+++ b/ddd/src/ddd/order/domain/Order.java
@@ -67,6 +67,13 @@ public class Order {
         return totalPrice - discountPrice;
     }
 
+    public int getShippingFee() {
+        if (new FreeShippingSpecification().isSatisfiedBy(this)) {
+            return 0;
+        }
+        return 3000;
+    }
+
     public OrderStatus getStatus() {
         return status;
     }
```

## T2 무료 배송 명세에 제주 조건 더하기
커밋: 7f4dbaf
Source: 7834719 a312669 7574f96

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/order/domain/FreeShippingSpecification.java | 고침 |

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index c2ed3a1..a36056c 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -40,5 +40,5 @@
 | 할인 정책 | DiscountPolicy | 회원 등급과 주문 금액으로 할인 금액을 계산하는 도메인 서비스. VIP는 주문 금액의 10%, 일반은 0원이다. 계산만 하고 주문을 바꾸지 않는다 |
 | 할인 금액 | discountPrice | 결제할 때 주문 금액에서 깎는 돈. 결제 전에는 0원이다 |
 | 결제 금액 | paymentPrice | 주문 금액에서 할인 금액을 뺀 돈. 실제로 치르는 돈이다 |
-| 무료 배송 명세 | FreeShippingSpecification | 주문이 무료 배송인지 참 거짓으로만 답하는 명세. 결제 금액이 50000원 이상이면 참이다 |
+| 무료 배송 명세 | FreeShippingSpecification | 결제 금액이 50000원 이상이고 주소가 제주로 시작하지 않는지 참 거짓으로 답한다 |
 | 배송비 | shippingFee | 주문을 보내는 데 받는 돈. 결제 금액과 따로 받는다. 무료 배송 명세를 만족하면 0원, 아니면 3000원이다 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 1a2b0c7..6b2007f 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -41,5 +41,18 @@ public class Main {
                 List.of(new OrderItemRequest(1, 4)), "서울시 강남구"));
         Order bigOrder = orderService.findOrder(bigOrderId);
         System.out.println(bigOrder.getPaymentPrice() + "원 주문 배송비: " + bigOrder.getShippingFee() + "원");
+
+        System.out.println();
+        System.out.println("[제주 주소는 무료 배송에서 빼기]");
+        long seoulOrderId = orderService.placeOrder(new OrderRequest(1,
+                List.of(new OrderItemRequest(1, 4)), "서울시 마포구"));
+        Order seoulOrder = orderService.findOrder(seoulOrderId);
+        System.out.println(seoulOrder.getAddress().value() + " " + seoulOrder.getPaymentPrice() + "원 주문 배송비: "
+                + seoulOrder.getShippingFee() + "원");
+        long jejuOrderId = orderService.placeOrder(new OrderRequest(1,
+                List.of(new OrderItemRequest(1, 4)), "제주시 연동"));
+        Order jejuOrder = orderService.findOrder(jejuOrderId);
+        System.out.println(jejuOrder.getAddress().value() + " " + jejuOrder.getPaymentPrice() + "원 주문 배송비: "
+                + jejuOrder.getShippingFee() + "원");
     }
 }
diff --git a/ddd/src/ddd/order/domain/FreeShippingSpecification.java b/ddd/src/ddd/order/domain/FreeShippingSpecification.java
index 603d765..479c71d 100644
--- a/ddd/src/ddd/order/domain/FreeShippingSpecification.java
+++ b/ddd/src/ddd/order/domain/FreeShippingSpecification.java
@@ -3,6 +3,7 @@ package ddd.order.domain;
 public class FreeShippingSpecification {
 
     public boolean isSatisfiedBy(Order order) {
-        return order.getPaymentPrice() >= 50000;
+        return order.getPaymentPrice() >= 50000
+                && !order.getAddress().value().startsWith("제주");
     }
 }
```

## T4 무료 배송 조건을 한 명세로 합치기
커밋: 7574f96
- 위 task에 고침을 녹였다. 학생에게 별도 수정 단계로 보이지 않는다
- done 태그는 옮기지 않고 fixes patch로 수정된 장 끝 코드를 재현한다
