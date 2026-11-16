# order-query 코드 요약

## T1 주문 요약으로 주문 목록 읽기
커밋: 6487de2

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/order/application/OrderQueryService.java | 새로 |
| ddd/src/ddd/order/application/OrderSummary.java | 새로 |
| ddd/src/ddd/order/domain/OrderRepository.java | 고침 |
| ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java | 고침 |

```java
// ddd/src/ddd/order/application/OrderSummary.java
package ddd.order.application;

public record OrderSummary(long orderId, String memberName, int orderLineCount, int paymentPrice, String status) {
}
```

```java
// ddd/src/ddd/order/application/OrderQueryService.java
package ddd.order.application;

import java.util.ArrayList;
import java.util.List;

import ddd.member.domain.Member;
import ddd.member.domain.MemberRepository;
import ddd.order.domain.Order;
import ddd.order.domain.OrderRepository;

public class OrderQueryService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;

    public OrderQueryService(OrderRepository orderRepository, MemberRepository memberRepository) {
        this.orderRepository = orderRepository;
        this.memberRepository = memberRepository;
    }

    public List<OrderSummary> findOrderSummaries(long memberId) {
        List<OrderSummary> summaries = new ArrayList<>();
        for (Order order : orderRepository.findByMemberId(memberId)) {
            Member member = memberRepository.findById(order.getMemberId()).orElseThrow();
            summaries.add(new OrderSummary(order.getId(), member.getName(), order.getOrderLines().size(),
                    order.getPaymentPrice().amount(), order.getStatus().label()));
        }
        return summaries;
    }
}
```

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index dba8187..0e52de6 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -13,6 +13,8 @@ import ddd.member.infrastructure.MemoryMemberRepository;
 import ddd.order.application.EventPublisher;
 import ddd.order.application.OrderApplicationService;
 import ddd.order.application.OrderLineCommand;
+import ddd.order.application.OrderQueryService;
+import ddd.order.application.OrderSummary;
 import ddd.order.application.PlaceOrderCommand;
 import ddd.order.domain.DiscountPolicy;
 import ddd.order.domain.Order;
@@ -218,5 +220,11 @@ public class Main {
             System.out.println("한도 넘은 결제: " + e.getMessage() + ", "
                     + orderService.findOrder(bigOrderId).getStatus().label());
         }
+
+        OrderQueryService orderQueryService = new OrderQueryService(orderRepository, memberRepository);
+        for (OrderSummary summary : orderQueryService.findOrderSummaries(1)) {
+            System.out.println("주문 목록: " + summary.orderId() + "번, " + summary.memberName() + ", 항목 "
+                    + summary.orderLineCount() + "개, " + summary.paymentPrice() + "원, " + summary.status());
+        }
     }
 }
diff --git a/ddd/src/ddd/order/domain/OrderRepository.java b/ddd/src/ddd/order/domain/OrderRepository.java
index 8aa47c5..f0ac7f6 100644
--- a/ddd/src/ddd/order/domain/OrderRepository.java
+++ b/ddd/src/ddd/order/domain/OrderRepository.java
@@ -1,5 +1,6 @@
 package ddd.order.domain;
 
+import java.util.List;
 import java.util.Optional;
 
 public interface OrderRepository {
@@ -7,4 +8,6 @@ public interface OrderRepository {
     void save(Order order);
 
     Optional<Order> findById(long id);
+
+    List<Order> findByMemberId(long memberId);
 }
diff --git a/ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java b/ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java
index 1f490a2..6f92f2b 100644
--- a/ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java
+++ b/ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java
@@ -5,7 +5,9 @@ import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
+import java.util.ArrayList;
 import java.util.HashMap;
+import java.util.List;
 import java.util.Map;
 import java.util.Optional;
 
@@ -35,6 +37,17 @@ public class MemoryOrderRepository implements OrderRepository {
         return Optional.of(copy(order));
     }
 
+    @Override
+    public List<Order> findByMemberId(long memberId) {
+        List<Order> found = new ArrayList<>();
+        for (Order order : orders.values()) {
+            if (order.getMemberId() == memberId) {
+                found.add(copy(order));
+            }
+        }
+        return found;
+    }
+
     private Order copy(Order order) {
         try {
             ByteArrayOutputStream bytes = new ByteArrayOutputStream();
```

## T2 용어집에 명령과 조회 나누기 적기
커밋: 0fb420d

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index 5ed9dbd..8f77590 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -61,3 +61,6 @@
 | 결제 결과 | PaymentResult | 결제 창구가 돌려주는 답. 승인인지와 거절 이유를 가진다 |
 | 결제 어댑터 | LegacyPaymentAdapter | 결제 창구를 외부 결제 모듈로 구현한 곳. 돈을 외부 모듈의 금액으로 바꿔 맡기고, 받은 답을 결제 결과로 번역한다 |
 | 부패 방지 계층 | LegacyPaymentAdapter | 바깥 모델의 말이 우리 모델로 새어 들지 않게 경계에서 번역하는 층. 주문 컨텍스트에서는 결제 어댑터가 맡는다 |
+| 주문 요약 | OrderSummary | 주문 목록 화면 한 줄에 맞춘 읽기 모델. 주문 번호, 회원 이름, 항목 수, 결제 금액, 상태 글자를 숫자와 글자로만 가진다. 바꾸는 동작이 없다 |
+| 주문 조회 서비스 | OrderQueryService | 회원 번호로 주문 요약 목록을 돌려주는 곳. 주문을 바꾸거나 저장하지 않고 읽기만 한다 |
+| 명령과 조회 나누기 | CQRS | 바꾸는 쪽(명령)과 읽는 쪽(조회)을 다른 모델로 나누는 것. 명령은 주문 응용 서비스가 주문 애그리거트로 규칙을 지키며 하고, 조회는 주문 조회 서비스가 주문 요약으로 한다 |
```
