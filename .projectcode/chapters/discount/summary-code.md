# discount 코드 요약

## T1 회원에 등급 더하기
커밋: 79f568c

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/domain/Member.java | 고침 |
| ddd/src/ddd/domain/MemberGrade.java | 새로 |

```java
// ddd/src/ddd/domain/MemberGrade.java
package ddd.domain;

public enum MemberGrade {
    BASIC,
    VIP
}
```

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index 003d0f0..64f4c77 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -29,9 +29,12 @@
 | 주문 응용 서비스 | OrderApplicationService | 주문 유스케이스를 하는 곳. 주문을 꺼내고, 주문에게 시키고, 저장한다. 규칙은 갖지 않는다 |
 | 주문하기 명령 | PlaceOrderCommand | 주문하기에 넣는 회원 번호, 주문 항목 명령 목록, 배송 주소 묶음 |
 | 주문 항목 명령 | OrderLineCommand | 주문 항목을 만들 상품 번호, 수량 묶음 |
-| 회원 | Member | 상품을 주문하는 사람. 회원 번호와 이름을 가진다. 회원 번호가 같으면 같은 회원이다 |
+| 회원 | Member | 상품을 주문하는 사람. 회원 번호, 이름, 회원 등급을 가진다. 회원 번호가 같으면 같은 회원이다 |
 | 회원 저장소 | MemberRepository | 회원을 넣어 두고 회원 번호로 다시 꺼내는 곳 |
 | 상품 | Product | 파는 물건. 상품 번호, 이름, 가격을 가진다. 상품 번호가 같으면 같은 상품이다 |
 | 상품 저장소 | ProductRepository | 상품을 넣어 두고 상품 번호로 다시 꺼내는 곳 |
 | 상품 번호 | productId | 주문 항목이 가리키는 상품의 번호. 주문 항목은 상품을 객체로 갖지 않고 이 번호로 가리킨다 |
 | 주문 항목 팩토리 | OrderLineFactory | 상품 번호와 수량을 받아 상품 저장소에서 상품을 찾고, 그 상품의 이름과 지금 가격으로 주문 항목을 만드는 곳 |
+| 회원 등급 | MemberGrade | 회원이 어떤 회원인지 나눈 것. 일반과 VIP 중 하나다 |
+| 일반 | BASIC | 할인이 없는 회원 등급 |
+| VIP | VIP | 할인을 받는 회원 등급 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index b847513..b66bd8e 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -6,6 +6,7 @@ import ddd.application.OrderApplicationService;
 import ddd.application.OrderLineCommand;
 import ddd.application.PlaceOrderCommand;
 import ddd.domain.Member;
+import ddd.domain.MemberGrade;
 import ddd.domain.MemberRepository;
 import ddd.domain.Money;
 import ddd.domain.Order;
@@ -23,8 +24,8 @@ public class Main {
         System.out.println("온라인 주문 프로그램을 시작합니다");
 
         MemberRepository memberRepository = new MemoryMemberRepository();
-        memberRepository.save(new Member(1, "김철수"));
-        memberRepository.save(new Member(2, "이영희"));
+        memberRepository.save(new Member(1, "김철수", MemberGrade.VIP));
+        memberRepository.save(new Member(2, "이영희", MemberGrade.BASIC));
 
         ProductRepository productRepository = new MemoryProductRepository();
         productRepository.save(new Product(1, "자바 책", new Money(15000)));
diff --git a/ddd/src/ddd/domain/Member.java b/ddd/src/ddd/domain/Member.java
index 5f0a7f9..90209c9 100644
--- a/ddd/src/ddd/domain/Member.java
+++ b/ddd/src/ddd/domain/Member.java
@@ -4,10 +4,12 @@ public class Member {
 
     private final long id;
     private final String name;
+    private final MemberGrade grade;
 
-    public Member(long id, String name) {
+    public Member(long id, String name, MemberGrade grade) {
         this.id = id;
         this.name = name;
+        this.grade = grade;
     }
 
     public long getId() {
@@ -18,6 +20,10 @@ public class Member {
         return name;
     }
 
+    public MemberGrade getGrade() {
+        return grade;
+    }
+
     @Override
     public boolean equals(Object o) {
         if (!(o instanceof Member other)) {
```

## T2 할인 정책으로 결제 금액 정하기
커밋: fd26f78

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/application/OrderApplicationService.java | 고침 |
| ddd/src/ddd/domain/DiscountPolicy.java | 새로 |
| ddd/src/ddd/domain/Money.java | 고침 |
| ddd/src/ddd/domain/Order.java | 고침 |

```java
// ddd/src/ddd/domain/DiscountPolicy.java
package ddd.domain;

public class DiscountPolicy {

    public Money discount(Member member, Order order) {
        if (member.getGrade() == MemberGrade.VIP) {
            return new Money(order.getTotalPrice().amount() * 10 / 100);
        }
        return new Money(0);
    }
}
```

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index 64f4c77..094cd44 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -38,3 +38,6 @@
 | 회원 등급 | MemberGrade | 회원이 어떤 회원인지 나눈 것. 일반과 VIP 중 하나다 |
 | 일반 | BASIC | 할인이 없는 회원 등급 |
 | VIP | VIP | 할인을 받는 회원 등급 |
+| 할인 정책 | DiscountPolicy | 회원 등급과 주문 금액으로 할인 금액을 계산하는 도메인 서비스. VIP는 주문 금액의 10%, 일반은 0원이다. 계산만 하고 주문을 바꾸지 않는다 |
+| 할인 금액 | discountPrice | 결제할 때 주문 금액에서 깎는 돈. 결제 전에는 0원이다 |
+| 결제 금액 | paymentPrice | 주문 금액에서 할인 금액을 뺀 돈. 실제로 치르는 돈이다 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index b66bd8e..03ce50d 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -5,6 +5,7 @@ import java.util.List;
 import ddd.application.OrderApplicationService;
 import ddd.application.OrderLineCommand;
 import ddd.application.PlaceOrderCommand;
+import ddd.domain.DiscountPolicy;
 import ddd.domain.Member;
 import ddd.domain.MemberGrade;
 import ddd.domain.MemberRepository;
@@ -33,7 +34,7 @@ public class Main {
         productRepository.save(new Product(3, "공책", new Money(2000)));
 
         OrderApplicationService orderService = new OrderApplicationService(new MemoryOrderRepository(),
-                new OrderLineFactory(productRepository));
+                new OrderLineFactory(productRepository), memberRepository, new DiscountPolicy());
 
         List<OrderLineCommand> orderLines = List.of(
                 new OrderLineCommand(1, 2),
@@ -50,6 +51,9 @@ public class Main {
 
         orderService.pay(orderId);
         System.out.println("결제: " + orderService.findOrder(orderId).getStatus().label());
+        Order paidOrder = orderService.findOrder(orderId);
+        System.out.println("할인: " + member.getGrade() + " " + paidOrder.getDiscountPrice().amount() + "원, 결제 금액 "
+                + paidOrder.getPaymentPrice().amount() + "원");
 
         orderService.ship(orderId);
         System.out.println("배송: " + orderService.findOrder(orderId).getStatus().label());
diff --git a/ddd/src/ddd/application/OrderApplicationService.java b/ddd/src/ddd/application/OrderApplicationService.java
index 135cbc0..ce29a3f 100644
--- a/ddd/src/ddd/application/OrderApplicationService.java
+++ b/ddd/src/ddd/application/OrderApplicationService.java
@@ -4,6 +4,9 @@ import java.util.ArrayList;
 import java.util.List;
 
 import ddd.domain.Address;
+import ddd.domain.DiscountPolicy;
+import ddd.domain.Member;
+import ddd.domain.MemberRepository;
 import ddd.domain.Order;
 import ddd.domain.OrderLine;
 import ddd.domain.OrderLineFactory;
@@ -13,11 +16,16 @@ public class OrderApplicationService {
 
     private final OrderRepository orderRepository;
     private final OrderLineFactory orderLineFactory;
+    private final MemberRepository memberRepository;
+    private final DiscountPolicy discountPolicy;
     private long nextId = 1;
 
-    public OrderApplicationService(OrderRepository orderRepository, OrderLineFactory orderLineFactory) {
+    public OrderApplicationService(OrderRepository orderRepository, OrderLineFactory orderLineFactory,
+            MemberRepository memberRepository, DiscountPolicy discountPolicy) {
         this.orderRepository = orderRepository;
         this.orderLineFactory = orderLineFactory;
+        this.memberRepository = memberRepository;
+        this.discountPolicy = discountPolicy;
     }
 
     public long placeOrder(PlaceOrderCommand command) {
@@ -42,7 +50,8 @@ public class OrderApplicationService {
 
     public void pay(long orderId) {
         Order order = findOrder(orderId);
-        order.pay();
+        Member member = memberRepository.findById(order.getMemberId()).orElseThrow();
+        order.pay(discountPolicy.discount(member, order));
         orderRepository.save(order);
     }
 
diff --git a/ddd/src/ddd/domain/Money.java b/ddd/src/ddd/domain/Money.java
index b17acdb..ba8f995 100644
--- a/ddd/src/ddd/domain/Money.java
+++ b/ddd/src/ddd/domain/Money.java
@@ -17,4 +17,8 @@ public record Money(int amount) implements Serializable {
     public Money plus(Money other) {
         return new Money(amount + other.amount);
     }
+
+    public Money minus(Money other) {
+        return new Money(amount - other.amount);
+    }
 }
diff --git a/ddd/src/ddd/domain/Order.java b/ddd/src/ddd/domain/Order.java
index b67d386..6231b8c 100644
--- a/ddd/src/ddd/domain/Order.java
+++ b/ddd/src/ddd/domain/Order.java
@@ -13,6 +13,7 @@ public class Order implements Serializable {
     private List<OrderLine> orderLines;
     private Address address;
     private Money totalPrice;
+    private Money discountPrice;
     private OrderStatus status;
 
     public Order(long id, long memberId, List<OrderLine> orderLines, Address address) {
@@ -27,6 +28,7 @@ public class Order implements Serializable {
         for (OrderLine orderLine : orderLines) {
             totalPrice = totalPrice.plus(orderLine.amount());
         }
+        this.discountPrice = new Money(0);
         this.status = OrderStatus.ORDERED;
     }
 
@@ -50,6 +52,14 @@ public class Order implements Serializable {
         return totalPrice;
     }
 
+    public Money getDiscountPrice() {
+        return discountPrice;
+    }
+
+    public Money getPaymentPrice() {
+        return totalPrice.minus(discountPrice);
+    }
+
     public OrderStatus getStatus() {
         return status;
     }
@@ -62,10 +72,11 @@ public class Order implements Serializable {
         totalPrice = totalPrice.plus(orderLine.amount());
     }
 
-    public void pay() {
+    public void pay(Money discountPrice) {
         if (!status.canChangeTo(OrderStatus.PAID)) {
             throw new IllegalStateException(status.label() + "인 주문은 결제할 수 없습니다");
         }
+        this.discountPrice = discountPrice;
         status = OrderStatus.PAID;
     }
 
```
