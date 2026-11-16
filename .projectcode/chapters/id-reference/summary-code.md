# id-reference 코드 요약

## T1 회원과 상품 저장하기
커밋: ed3f66a

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/domain/Member.java | 새로 |
| ddd/src/ddd/domain/MemberRepository.java | 새로 |
| ddd/src/ddd/domain/Product.java | 새로 |
| ddd/src/ddd/domain/ProductRepository.java | 새로 |
| ddd/src/ddd/infrastructure/MemoryMemberRepository.java | 새로 |
| ddd/src/ddd/infrastructure/MemoryProductRepository.java | 새로 |

```java
// ddd/src/ddd/domain/Member.java
package ddd.domain;

public class Member {

    private final long id;
    private final String name;

    public Member(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Member other)) {
            return false;
        }
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
```

```java
// ddd/src/ddd/domain/MemberRepository.java
package ddd.domain;

import java.util.Optional;

public interface MemberRepository {

    void save(Member member);

    Optional<Member> findById(long id);
}
```

```java
// ddd/src/ddd/domain/Product.java
package ddd.domain;

public class Product {

    private final long id;
    private final String name;
    private final Money price;

    public Product(long id, String name, Money price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Product other)) {
            return false;
        }
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
```

```java
// ddd/src/ddd/domain/ProductRepository.java
package ddd.domain;

import java.util.Optional;

public interface ProductRepository {

    void save(Product product);

    Optional<Product> findById(long id);
}
```

```java
// ddd/src/ddd/infrastructure/MemoryMemberRepository.java
package ddd.infrastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ddd.domain.Member;
import ddd.domain.MemberRepository;

public class MemoryMemberRepository implements MemberRepository {

    private final Map<Long, Member> members = new HashMap<>();

    @Override
    public void save(Member member) {
        members.put(member.getId(), member);
    }

    @Override
    public Optional<Member> findById(long id) {
        return Optional.ofNullable(members.get(id));
    }
}
```

```java
// ddd/src/ddd/infrastructure/MemoryProductRepository.java
package ddd.infrastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ddd.domain.Product;
import ddd.domain.ProductRepository;

public class MemoryProductRepository implements ProductRepository {

    private final Map<Long, Product> products = new HashMap<>();

    @Override
    public void save(Product product) {
        products.put(product.getId(), product);
    }

    @Override
    public Optional<Product> findById(long id) {
        return Optional.ofNullable(products.get(id));
    }
}
```

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index 16ca2e3..67cfc2f 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -29,3 +29,7 @@
 | 주문 응용 서비스 | OrderApplicationService | 주문 유스케이스를 하는 곳. 주문을 꺼내고, 주문에게 시키고, 저장한다. 규칙은 갖지 않는다 |
 | 주문하기 명령 | PlaceOrderCommand | 주문하기에 넣는 주문자, 주문 항목 명령 목록, 배송 주소 묶음 |
 | 주문 항목 명령 | OrderLineCommand | 주문 항목을 만들 상품 이름, 가격, 수량 묶음 |
+| 회원 | Member | 상품을 주문하는 사람. 회원 번호와 이름을 가진다. 회원 번호가 같으면 같은 회원이다 |
+| 회원 저장소 | MemberRepository | 회원을 넣어 두고 회원 번호로 다시 꺼내는 곳 |
+| 상품 | Product | 파는 물건. 상품 번호, 이름, 가격을 가진다. 상품 번호가 같으면 같은 상품이다 |
+| 상품 저장소 | ProductRepository | 상품을 넣어 두고 상품 번호로 다시 꺼내는 곳 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 062a5ab..de5bf1b 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -5,15 +5,31 @@ import java.util.List;
 import ddd.application.OrderApplicationService;
 import ddd.application.OrderLineCommand;
 import ddd.application.PlaceOrderCommand;
+import ddd.domain.Member;
+import ddd.domain.MemberRepository;
+import ddd.domain.Money;
 import ddd.domain.Order;
 import ddd.domain.OrderLine;
+import ddd.domain.Product;
+import ddd.domain.ProductRepository;
+import ddd.infrastructure.MemoryMemberRepository;
 import ddd.infrastructure.MemoryOrderRepository;
+import ddd.infrastructure.MemoryProductRepository;
 
 public class Main {
 
     public static void main(String[] args) {
         System.out.println("온라인 주문 프로그램을 시작합니다");
 
+        MemberRepository memberRepository = new MemoryMemberRepository();
+        memberRepository.save(new Member(1, "김철수"));
+        memberRepository.save(new Member(2, "이영희"));
+
+        ProductRepository productRepository = new MemoryProductRepository();
+        productRepository.save(new Product(1, "자바 책", new Money(15000)));
+        productRepository.save(new Product(2, "볼펜", new Money(1000)));
+        productRepository.save(new Product(3, "공책", new Money(2000)));
+
         OrderApplicationService orderService = new OrderApplicationService(new MemoryOrderRepository());
 
         List<OrderLineCommand> orderLines = List.of(
```

## T2 주문이 회원과 상품을 번호로 가리키기
커밋: 7777c45

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/application/OrderApplicationService.java | 고침 |
| ddd/src/ddd/application/OrderLineCommand.java | 고침 |
| ddd/src/ddd/application/PlaceOrderCommand.java | 고침 |
| ddd/src/ddd/domain/Order.java | 고침 |
| ddd/src/ddd/domain/OrderLine.java | 고침 |

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index 67cfc2f..2a742f7 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -3,15 +3,15 @@
 | 업무 말 | 코드 이름 | 뜻 |
 |---|---|---|
 | 주문 | Order | 고객이 상품을 사겠다고 한 요청 |
-| 주문 항목 | OrderLine | 주문한 상품 하나의 상품 이름, 가격, 수량 |
+| 주문 항목 | OrderLine | 주문한 상품 하나의 상품 번호와 주문 때의 상품 이름, 가격, 수량 |
 | 항목 금액 | amount | 주문 항목의 가격 곱하기 수량 |
 | 주문 번호 | id | 주문마다 다른 번호. 번호가 같으면 같은 주문이다 |
-| 주문자 | customerName | 주문한 고객의 이름 |
+| 회원 번호 | memberId | 주문한 회원의 번호. 주문은 회원을 객체로 갖지 않고 이 번호로 가리킨다 |
 | 배송 주소 | address | 상품을 받을 곳 |
 | 주소 | Address | 비어 있지 않은 배송 주소. 글자가 같으면 같은 주소다 |
 | 주문 금액 | totalPrice | 주문 항목마다 항목 금액을 더한 합 |
 | 돈 | Money | 0원 이상인 금액. 금액이 같으면 같은 돈이다 |
-| 주문하기 | placeOrder | 주문자, 주문 항목, 배송 주소로 주문을 접수한다 |
+| 주문하기 | placeOrder | 회원 번호, 주문 항목, 배송 주소로 주문을 접수한다 |
 | 주문 항목 더하기 | addOrderLine | 주문 접수인 주문에 주문 항목을 더하고 주문 금액도 함께 바꾼다 |
 | 결제하기 | pay | 주문 금액을 치러 결제 완료가 된다 |
 | 배송하기 | ship | 결제 완료인 주문을 보내 배송 중이 된다 |
@@ -27,9 +27,10 @@
 | 저장하기 | save | 주문을 주문 저장소에 넣는다. 같은 번호가 있으면 새 주문으로 바꾼다 |
 | 주문 번호로 찾기 | findById | 주문 번호로 주문 저장소에서 주문을 꺼낸다. 없으면 빈 결과다 |
 | 주문 응용 서비스 | OrderApplicationService | 주문 유스케이스를 하는 곳. 주문을 꺼내고, 주문에게 시키고, 저장한다. 규칙은 갖지 않는다 |
-| 주문하기 명령 | PlaceOrderCommand | 주문하기에 넣는 주문자, 주문 항목 명령 목록, 배송 주소 묶음 |
-| 주문 항목 명령 | OrderLineCommand | 주문 항목을 만들 상품 이름, 가격, 수량 묶음 |
+| 주문하기 명령 | PlaceOrderCommand | 주문하기에 넣는 회원 번호, 주문 항목 명령 목록, 배송 주소 묶음 |
+| 주문 항목 명령 | OrderLineCommand | 주문 항목을 만들 상품 번호, 상품 이름, 가격, 수량 묶음 |
 | 회원 | Member | 상품을 주문하는 사람. 회원 번호와 이름을 가진다. 회원 번호가 같으면 같은 회원이다 |
 | 회원 저장소 | MemberRepository | 회원을 넣어 두고 회원 번호로 다시 꺼내는 곳 |
 | 상품 | Product | 파는 물건. 상품 번호, 이름, 가격을 가진다. 상품 번호가 같으면 같은 상품이다 |
 | 상품 저장소 | ProductRepository | 상품을 넣어 두고 상품 번호로 다시 꺼내는 곳 |
+| 상품 번호 | productId | 주문 항목이 가리키는 상품의 번호. 주문 항목은 상품을 객체로 갖지 않고 이 번호로 가리킨다 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index de5bf1b..c3ab3c4 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -33,11 +33,12 @@ public class Main {
         OrderApplicationService orderService = new OrderApplicationService(new MemoryOrderRepository());
 
         List<OrderLineCommand> orderLines = List.of(
-                new OrderLineCommand("자바 책", 15000, 2),
-                new OrderLineCommand("볼펜", 1000, 1));
-        long orderId = orderService.placeOrder(new PlaceOrderCommand("김철수", orderLines, "서울시 강남구"));
+                new OrderLineCommand(1, "자바 책", 15000, 2),
+                new OrderLineCommand(2, "볼펜", 1000, 1));
+        long orderId = orderService.placeOrder(new PlaceOrderCommand(1, orderLines, "서울시 강남구"));
         Order order = orderService.findOrder(orderId);
-        System.out.println("주문: " + order.getId() + "번, " + order.getCustomerName() + ", "
+        Member member = memberRepository.findById(order.getMemberId()).orElseThrow();
+        System.out.println("주문: " + order.getId() + "번, " + member.getName() + ", "
                 + order.getTotalPrice().amount() + "원, " + order.getStatus().label());
         for (OrderLine orderLine : order.getOrderLines()) {
             System.out.println("주문 항목: " + orderLine.productName() + " " + orderLine.quantity() + "개, "
@@ -57,8 +58,8 @@ public class Main {
             System.out.println("취소: " + e.getMessage());
         }
 
-        long order2Id = orderService.placeOrder(new PlaceOrderCommand("이영희",
-                List.of(new OrderLineCommand("자바 책", 15000, 1)), "부산시 해운대구"));
+        long order2Id = orderService.placeOrder(new PlaceOrderCommand(2,
+                List.of(new OrderLineCommand(1, "자바 책", 15000, 1)), "부산시 해운대구"));
         try {
             orderService.ship(order2Id);
             System.out.println("결제 없이 배송: " + orderService.findOrder(order2Id).getStatus().label());
@@ -67,47 +68,47 @@ public class Main {
         }
 
         try {
-            long order3Id = orderService.placeOrder(new PlaceOrderCommand("박민수",
-                    List.of(new OrderLineCommand("자바 책", -15000, 2)), "대구시 수성구"));
+            long order3Id = orderService.placeOrder(new PlaceOrderCommand(2,
+                    List.of(new OrderLineCommand(1, "자바 책", -15000, 2)), "대구시 수성구"));
             System.out.println("음수 금액 주문: " + orderService.findOrder(order3Id).getTotalPrice().amount() + "원");
         } catch (IllegalArgumentException e) {
             System.out.println("음수 금액 주문: " + e.getMessage());
         }
 
         try {
-            long order4Id = orderService.placeOrder(new PlaceOrderCommand("최지우",
-                    List.of(new OrderLineCommand("자바 책", 15000, 1)), ""));
+            long order4Id = orderService.placeOrder(new PlaceOrderCommand(2,
+                    List.of(new OrderLineCommand(1, "자바 책", 15000, 1)), ""));
             System.out.println("빈 주소 주문: " + orderService.findOrder(order4Id).getStatus().label());
         } catch (IllegalArgumentException e) {
             System.out.println("빈 주소 주문: " + e.getMessage());
         }
 
-        long order5Id = orderService.placeOrder(new PlaceOrderCommand("정하늘", List.of(
-                new OrderLineCommand("자바 책", 15000, 2),
-                new OrderLineCommand("볼펜", 1000, 1)), "광주시 북구"));
-        orderService.addOrderLine(order5Id, new OrderLineCommand("공책", 2000, 5));
+        long order5Id = orderService.placeOrder(new PlaceOrderCommand(2, List.of(
+                new OrderLineCommand(1, "자바 책", 15000, 2),
+                new OrderLineCommand(2, "볼펜", 1000, 1)), "광주시 북구"));
+        orderService.addOrderLine(order5Id, new OrderLineCommand(3, "공책", 2000, 5));
         Order order5 = orderService.findOrder(order5Id);
         System.out.println("항목 더하기: 항목 " + order5.getOrderLines().size() + "개, 주문 금액 "
                 + order5.getTotalPrice().amount() + "원");
 
         orderService.pay(order5Id);
         try {
-            orderService.addOrderLine(order5Id, new OrderLineCommand("공책", 2000, 1));
+            orderService.addOrderLine(order5Id, new OrderLineCommand(3, "공책", 2000, 1));
             System.out.println("결제 뒤 항목 더하기: 항목 " + orderService.findOrder(order5Id).getOrderLines().size() + "개");
         } catch (IllegalStateException e) {
             System.out.println("결제 뒤 항목 더하기: " + e.getMessage());
         }
 
         try {
-            long order6Id = orderService.placeOrder(new PlaceOrderCommand("강민준", List.of(), "인천시 연수구"));
+            long order6Id = orderService.placeOrder(new PlaceOrderCommand(2, List.of(), "인천시 연수구"));
             System.out.println("빈 주문: " + orderService.findOrder(order6Id).getStatus().label());
         } catch (IllegalArgumentException e) {
             System.out.println("빈 주문: " + e.getMessage());
         }
 
         try {
-            long order7Id = orderService.placeOrder(new PlaceOrderCommand("윤서연",
-                    List.of(new OrderLineCommand("자바 책", 15000, 0)), "대전시 유성구"));
+            long order7Id = orderService.placeOrder(new PlaceOrderCommand(2,
+                    List.of(new OrderLineCommand(1, "자바 책", 15000, 0)), "대전시 유성구"));
             System.out.println("0개 항목 주문: " + orderService.findOrder(order7Id).getTotalPrice().amount() + "원");
         } catch (IllegalArgumentException e) {
             System.out.println("0개 항목 주문: " + e.getMessage());
diff --git a/ddd/src/ddd/application/OrderApplicationService.java b/ddd/src/ddd/application/OrderApplicationService.java
index e930d02..7e77fe6 100644
--- a/ddd/src/ddd/application/OrderApplicationService.java
+++ b/ddd/src/ddd/application/OrderApplicationService.java
@@ -23,7 +23,7 @@ public class OrderApplicationService {
         for (OrderLineCommand orderLineCommand : command.orderLines()) {
             orderLines.add(toOrderLine(orderLineCommand));
         }
-        Order order = new Order(nextId++, command.customerName(), orderLines, new Address(command.address()));
+        Order order = new Order(nextId++, command.memberId(), orderLines, new Address(command.address()));
         orderRepository.save(order);
         return order.getId();
     }
@@ -63,6 +63,6 @@ public class OrderApplicationService {
     }
 
     private OrderLine toOrderLine(OrderLineCommand command) {
-        return new OrderLine(command.productName(), new Money(command.price()), command.quantity());
+        return new OrderLine(command.productId(), command.productName(), new Money(command.price()), command.quantity());
     }
 }
diff --git a/ddd/src/ddd/application/OrderLineCommand.java b/ddd/src/ddd/application/OrderLineCommand.java
index c74c436..0d16766 100644
--- a/ddd/src/ddd/application/OrderLineCommand.java
+++ b/ddd/src/ddd/application/OrderLineCommand.java
@@ -1,4 +1,4 @@
 package ddd.application;
 
-public record OrderLineCommand(String productName, int price, int quantity) {
+public record OrderLineCommand(long productId, String productName, int price, int quantity) {
 }
diff --git a/ddd/src/ddd/application/PlaceOrderCommand.java b/ddd/src/ddd/application/PlaceOrderCommand.java
index 8a38563..aff512e 100644
--- a/ddd/src/ddd/application/PlaceOrderCommand.java
+++ b/ddd/src/ddd/application/PlaceOrderCommand.java
@@ -2,5 +2,5 @@ package ddd.application;
 
 import java.util.List;
 
-public record PlaceOrderCommand(String customerName, List<OrderLineCommand> orderLines, String address) {
+public record PlaceOrderCommand(long memberId, List<OrderLineCommand> orderLines, String address) {
 }
diff --git a/ddd/src/ddd/domain/Order.java b/ddd/src/ddd/domain/Order.java
index 3432038..b67d386 100644
--- a/ddd/src/ddd/domain/Order.java
+++ b/ddd/src/ddd/domain/Order.java
@@ -9,18 +9,18 @@ public class Order implements Serializable {
     private static final long serialVersionUID = 1L;
 
     private long id;
-    private String customerName;
+    private long memberId;
     private List<OrderLine> orderLines;
     private Address address;
     private Money totalPrice;
     private OrderStatus status;
 
-    public Order(long id, String customerName, List<OrderLine> orderLines, Address address) {
+    public Order(long id, long memberId, List<OrderLine> orderLines, Address address) {
         if (orderLines.isEmpty()) {
             throw new IllegalArgumentException("주문 항목이 하나 이상 있어야 합니다");
         }
         this.id = id;
-        this.customerName = customerName;
+        this.memberId = memberId;
         this.orderLines = new ArrayList<>(orderLines);
         this.address = address;
         this.totalPrice = new Money(0);
@@ -34,8 +34,8 @@ public class Order implements Serializable {
         return id;
     }
 
-    public String getCustomerName() {
-        return customerName;
+    public long getMemberId() {
+        return memberId;
     }
 
     public List<OrderLine> getOrderLines() {
diff --git a/ddd/src/ddd/domain/OrderLine.java b/ddd/src/ddd/domain/OrderLine.java
index 8011bd9..a2de83e 100644
--- a/ddd/src/ddd/domain/OrderLine.java
+++ b/ddd/src/ddd/domain/OrderLine.java
@@ -2,7 +2,7 @@ package ddd.domain;
 
 import java.io.Serializable;
 
-public record OrderLine(String productName, Money price, int quantity) implements Serializable {
+public record OrderLine(long productId, String productName, Money price, int quantity) implements Serializable {
 
     public OrderLine {
         if (quantity < 1) {
```

## T3 상품 가격이 바뀐 뒤 주문 보기
커밋: 8b27c56

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/Main.java | 고침 |

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index c3ab3c4..09587d6 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -134,5 +134,12 @@ public class Main {
         Order notSaved = orderService.findOrder(order2Id);
         notSaved.cancel();
         System.out.println("저장 안 한 취소: " + orderService.findOrder(order2Id).getStatus().label());
+
+        productRepository.save(new Product(1, "자바 책", new Money(18000)));
+        Order firstOrder = orderService.findOrder(orderId);
+        OrderLine bookLine = firstOrder.getOrderLines().get(0);
+        Product book = productRepository.findById(bookLine.productId()).orElseThrow();
+        System.out.println("가격 바뀐 뒤: " + book.getId() + "번 상품 " + book.getPrice().amount() + "원, 주문 항목 "
+                + bookLine.price().amount() + "원, 주문 금액 " + firstOrder.getTotalPrice().amount() + "원");
     }
 }
```
