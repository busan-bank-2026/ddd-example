# order-events 코드 요약

## T1 주문이 주문 접수됨 이벤트 남기기
커밋: 8148820

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/domain/Order.java | 고침 |
| ddd/src/ddd/domain/OrderPlaced.java | 새로 |
| ddd/src/ddd/domain/Product.java | 고침 |

```java
// ddd/src/ddd/domain/OrderPlaced.java
package ddd.domain;

import java.io.Serializable;
import java.util.List;

public record OrderPlaced(long orderId, List<OrderLine> orderLines) implements Serializable {
}
```

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index ca5fcf0..51a1f99 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -31,7 +31,7 @@
 | 주문 항목 명령 | OrderLineCommand | 주문 항목을 만들 상품 번호, 수량 묶음 |
 | 회원 | Member | 상품을 주문하는 사람. 회원 번호, 이름, 회원 등급을 가진다. 회원 번호가 같으면 같은 회원이다 |
 | 회원 저장소 | MemberRepository | 회원을 넣어 두고 회원 번호로 다시 꺼내는 곳 |
-| 상품 | Product | 파는 물건. 상품 번호, 이름, 가격을 가진다. 상품 번호가 같으면 같은 상품이다 |
+| 상품 | Product | 파는 물건. 상품 번호, 이름, 가격, 재고를 가진다. 상품 번호가 같으면 같은 상품이다 |
 | 상품 저장소 | ProductRepository | 상품을 넣어 두고 상품 번호로 다시 꺼내는 곳 |
 | 상품 번호 | productId | 주문 항목이 가리키는 상품의 번호. 주문 항목은 상품을 객체로 갖지 않고 이 번호로 가리킨다 |
 | 주문 항목 팩토리 | OrderLineFactory | 상품 번호와 수량을 받아 상품 저장소에서 상품을 찾고, 그 상품의 이름과 지금 가격으로 주문 항목을 만드는 곳 |
@@ -46,3 +46,5 @@
 | 주문 명세 | OrderSpecification | 주문 하나를 받아 조건을 만족하는지 참 거짓으로만 답하는 것. and로 두 명세를 묶으면 다시 주문 명세가 된다 |
 | 무료 배송 금액 명세 | FreeShippingPriceSpecification | 결제 금액이 50000원 이상인지 답하는 명세 |
 | 무료 배송 지역 명세 | FreeShippingAreaSpecification | 배송 주소가 제주가 아닌지 답하는 명세 |
+| 재고 | stock | 상품이 지금 몇 개 남았는지. 주문하면 나중에 주문 수량만큼 줄어든다 |
+| 주문 접수됨 | OrderPlaced | 주문이 만들어질 때 주문이 남기는 도메인 이벤트. 주문 번호와 주문 항목을 담는다. 이미 일어난 일이라 바뀌지 않는다 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 613cd72..e53d289 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -29,9 +29,9 @@ public class Main {
         memberRepository.save(new Member(2, "이영희", MemberGrade.BASIC));
 
         ProductRepository productRepository = new MemoryProductRepository();
-        productRepository.save(new Product(1, "자바 책", new Money(15000)));
-        productRepository.save(new Product(2, "볼펜", new Money(1000)));
-        productRepository.save(new Product(3, "공책", new Money(2000)));
+        productRepository.save(new Product(1, "자바 책", new Money(15000), 10));
+        productRepository.save(new Product(2, "볼펜", new Money(1000), 10));
+        productRepository.save(new Product(3, "공책", new Money(2000), 10));
 
         OrderApplicationService orderService = new OrderApplicationService(new MemoryOrderRepository(),
                 new OrderLineFactory(productRepository), memberRepository, new DiscountPolicy());
@@ -48,6 +48,8 @@ public class Main {
             System.out.println("주문 항목: " + orderLine.productName() + " " + orderLine.quantity() + "개, "
                     + orderLine.amount().amount() + "원");
         }
+        System.out.println("주문 뒤: 이벤트 " + order.getEvents().size() + "개, 자바 책 재고 "
+                + productRepository.findById(1).orElseThrow().getStock() + "개");
 
         orderService.pay(orderId);
         System.out.println("결제: " + orderService.findOrder(orderId).getStatus().label());
@@ -76,7 +78,7 @@ public class Main {
         }
 
         try {
-            productRepository.save(new Product(4, "할인 책", new Money(-15000)));
+            productRepository.save(new Product(4, "할인 책", new Money(-15000), 10));
             long order3Id = orderService.placeOrder(new PlaceOrderCommand(2,
                     List.of(new OrderLineCommand(4, 2)), "대구시 수성구"));
             System.out.println("음수 금액 주문: " + orderService.findOrder(order3Id).getTotalPrice().amount() + "원");
@@ -159,7 +161,7 @@ public class Main {
         System.out.println("6만원 주문 배송비: 서울 " + orderService.findOrder(seoulOrderId).getShippingFee().amount()
                 + "원, 제주 " + orderService.findOrder(jejuOrderId).getShippingFee().amount() + "원");
 
-        productRepository.save(new Product(1, "자바 책", new Money(18000)));
+        productRepository.save(new Product(1, "자바 책", new Money(18000), 10));
         Order firstOrder = orderService.findOrder(orderId);
         OrderLine bookLine = firstOrder.getOrderLines().get(0);
         Product book = productRepository.findById(bookLine.productId()).orElseThrow();
diff --git a/ddd/src/ddd/domain/Order.java b/ddd/src/ddd/domain/Order.java
index d7b4c20..843e359 100644
--- a/ddd/src/ddd/domain/Order.java
+++ b/ddd/src/ddd/domain/Order.java
@@ -15,6 +15,7 @@ public class Order implements Serializable {
     private Money totalPrice;
     private Money discountPrice;
     private OrderStatus status;
+    private List<Object> events;
 
     public Order(long id, long memberId, List<OrderLine> orderLines, Address address) {
         if (orderLines.isEmpty()) {
@@ -30,6 +31,8 @@ public class Order implements Serializable {
         }
         this.discountPrice = new Money(0);
         this.status = OrderStatus.ORDERED;
+        this.events = new ArrayList<>();
+        events.add(new OrderPlaced(id, List.copyOf(orderLines)));
     }
 
     public long getId() {
@@ -71,6 +74,10 @@ public class Order implements Serializable {
         return status;
     }
 
+    public List<Object> getEvents() {
+        return List.copyOf(events);
+    }
+
     public void addOrderLine(OrderLine orderLine) {
         if (status != OrderStatus.ORDERED) {
             throw new IllegalStateException("주문 접수인 주문에만 항목을 더할 수 있습니다");
diff --git a/ddd/src/ddd/domain/Product.java b/ddd/src/ddd/domain/Product.java
index 4e47b21..7278a04 100644
--- a/ddd/src/ddd/domain/Product.java
+++ b/ddd/src/ddd/domain/Product.java
@@ -5,11 +5,13 @@ public class Product {
     private final long id;
     private final String name;
     private final Money price;
+    private int stock;
 
-    public Product(long id, String name, Money price) {
+    public Product(long id, String name, Money price, int stock) {
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

## T2 저장 뒤 이벤트를 발행기에 넘기기
커밋: 388255f

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/application/EventPublisher.java | 새로 |
| ddd/src/ddd/application/OrderApplicationService.java | 고침 |

```java
// ddd/src/ddd/application/EventPublisher.java
package ddd.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventPublisher {

    private final Map<Class<?>, List<Consumer<Object>>> subscribers = new HashMap<>();
    private final List<Object> waitingEvents = new ArrayList<>();

    public <T> void subscribe(Class<T> eventType, Consumer<T> subscriber) {
        subscribers.computeIfAbsent(eventType, type -> new ArrayList<>())
                .add(event -> subscriber.accept(eventType.cast(event)));
    }

    public void publish(List<?> events) {
        waitingEvents.addAll(events);
    }

    public void processEvents() {
        List<Object> events = new ArrayList<>(waitingEvents);
        waitingEvents.clear();
        for (Object event : events) {
            for (Consumer<Object> subscriber : subscribers.getOrDefault(event.getClass(), List.of())) {
                subscriber.accept(event);
            }
        }
    }
}
```

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index 51a1f99..a01bb53 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -48,3 +48,4 @@
 | 무료 배송 지역 명세 | FreeShippingAreaSpecification | 배송 주소가 제주가 아닌지 답하는 명세 |
 | 재고 | stock | 상품이 지금 몇 개 남았는지. 주문하면 나중에 주문 수량만큼 줄어든다 |
 | 주문 접수됨 | OrderPlaced | 주문이 만들어질 때 주문이 남기는 도메인 이벤트. 주문 번호와 주문 항목을 담는다. 이미 일어난 일이라 바뀌지 않는다 |
+| 이벤트 발행기 | EventPublisher | 도메인 이벤트를 받아 쌓아 두었다가, 이벤트를 처리할 때 그 이벤트를 구독한 곳에 보내는 곳. 주문 응용 서비스는 주문을 저장한 뒤 이벤트를 여기에 넘긴다 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index e53d289..2dc60a9 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -2,6 +2,7 @@ package ddd;
 
 import java.util.List;
 
+import ddd.application.EventPublisher;
 import ddd.application.OrderApplicationService;
 import ddd.application.OrderLineCommand;
 import ddd.application.PlaceOrderCommand;
@@ -33,8 +34,9 @@ public class Main {
         productRepository.save(new Product(2, "볼펜", new Money(1000), 10));
         productRepository.save(new Product(3, "공책", new Money(2000), 10));
 
+        EventPublisher eventPublisher = new EventPublisher();
         OrderApplicationService orderService = new OrderApplicationService(new MemoryOrderRepository(),
-                new OrderLineFactory(productRepository), memberRepository, new DiscountPolicy());
+                new OrderLineFactory(productRepository), memberRepository, new DiscountPolicy(), eventPublisher);
 
         List<OrderLineCommand> orderLines = List.of(
                 new OrderLineCommand(1, 2),
diff --git a/ddd/src/ddd/application/OrderApplicationService.java b/ddd/src/ddd/application/OrderApplicationService.java
index ce29a3f..54f77bb 100644
--- a/ddd/src/ddd/application/OrderApplicationService.java
+++ b/ddd/src/ddd/application/OrderApplicationService.java
@@ -18,14 +18,16 @@ public class OrderApplicationService {
     private final OrderLineFactory orderLineFactory;
     private final MemberRepository memberRepository;
     private final DiscountPolicy discountPolicy;
+    private final EventPublisher eventPublisher;
     private long nextId = 1;
 
     public OrderApplicationService(OrderRepository orderRepository, OrderLineFactory orderLineFactory,
-            MemberRepository memberRepository, DiscountPolicy discountPolicy) {
+            MemberRepository memberRepository, DiscountPolicy discountPolicy, EventPublisher eventPublisher) {
         this.orderRepository = orderRepository;
         this.orderLineFactory = orderLineFactory;
         this.memberRepository = memberRepository;
         this.discountPolicy = discountPolicy;
+        this.eventPublisher = eventPublisher;
     }
 
     public long placeOrder(PlaceOrderCommand command) {
@@ -35,6 +37,7 @@ public class OrderApplicationService {
         }
         Order order = new Order(nextId++, command.memberId(), orderLines, new Address(command.address()));
         orderRepository.save(order);
+        eventPublisher.publish(order.getEvents());
         return order.getId();
     }
 
```

## T3 재고 줄이기 처리기로 재고 맞추기
커밋: 3d27ec7

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/application/DecreaseStockHandler.java | 새로 |
| ddd/src/ddd/domain/Product.java | 고침 |

```java
// ddd/src/ddd/application/DecreaseStockHandler.java
package ddd.application;

import ddd.domain.OrderLine;
import ddd.domain.OrderPlaced;
import ddd.domain.Product;
import ddd.domain.ProductRepository;

public class DecreaseStockHandler {

    private final ProductRepository productRepository;

    public DecreaseStockHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void handle(OrderPlaced event) {
        for (OrderLine orderLine : event.orderLines()) {
            Product product = productRepository.findById(orderLine.productId()).orElseThrow();
            product.decreaseStock(orderLine.quantity());
            productRepository.save(product);
        }
    }
}
```

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index a01bb53..b9b8532 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -49,3 +49,5 @@
 | 재고 | stock | 상품이 지금 몇 개 남았는지. 주문하면 나중에 주문 수량만큼 줄어든다 |
 | 주문 접수됨 | OrderPlaced | 주문이 만들어질 때 주문이 남기는 도메인 이벤트. 주문 번호와 주문 항목을 담는다. 이미 일어난 일이라 바뀌지 않는다 |
 | 이벤트 발행기 | EventPublisher | 도메인 이벤트를 받아 쌓아 두었다가, 이벤트를 처리할 때 그 이벤트를 구독한 곳에 보내는 곳. 주문 응용 서비스는 주문을 저장한 뒤 이벤트를 여기에 넘긴다 |
+| 재고 줄이기 | decreaseStock | 상품 재고를 주문 수량만큼 뺀다 |
+| 재고 줄이기 처리기 | DecreaseStockHandler | 주문 접수됨 이벤트를 구독해 주문 항목마다 상품 재고를 줄이고 저장하는 곳. 주문 저장과 따로 일해서 재고는 이벤트를 처리한 뒤에 맞춰진다 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 2dc60a9..d31bbb4 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -2,6 +2,7 @@ package ddd;
 
 import java.util.List;
 
+import ddd.application.DecreaseStockHandler;
 import ddd.application.EventPublisher;
 import ddd.application.OrderApplicationService;
 import ddd.application.OrderLineCommand;
@@ -14,6 +15,7 @@ import ddd.domain.Money;
 import ddd.domain.Order;
 import ddd.domain.OrderLine;
 import ddd.domain.OrderLineFactory;
+import ddd.domain.OrderPlaced;
 import ddd.domain.Product;
 import ddd.domain.ProductRepository;
 import ddd.infrastructure.MemoryMemberRepository;
@@ -35,6 +37,8 @@ public class Main {
         productRepository.save(new Product(3, "공책", new Money(2000), 10));
 
         EventPublisher eventPublisher = new EventPublisher();
+        DecreaseStockHandler decreaseStockHandler = new DecreaseStockHandler(productRepository);
+        eventPublisher.subscribe(OrderPlaced.class, decreaseStockHandler::handle);
         OrderApplicationService orderService = new OrderApplicationService(new MemoryOrderRepository(),
                 new OrderLineFactory(productRepository), memberRepository, new DiscountPolicy(), eventPublisher);
 
@@ -52,6 +56,8 @@ public class Main {
         }
         System.out.println("주문 뒤: 이벤트 " + order.getEvents().size() + "개, 자바 책 재고 "
                 + productRepository.findById(1).orElseThrow().getStock() + "개");
+        eventPublisher.processEvents();
+        System.out.println("이벤트 처리 뒤: 자바 책 재고 " + productRepository.findById(1).orElseThrow().getStock() + "개");
 
         orderService.pay(orderId);
         System.out.println("결제: " + orderService.findOrder(orderId).getStatus().label());
diff --git a/ddd/src/ddd/domain/Product.java b/ddd/src/ddd/domain/Product.java
index 7278a04..34f4a9f 100644
--- a/ddd/src/ddd/domain/Product.java
+++ b/ddd/src/ddd/domain/Product.java
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
