# optimistic-lock 코드 요약

## T1 같은 주문을 두 번 꺼내 덮어쓰기
커밋: 001c774

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/Main.java | 고침 |

```diff
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index d31bbb4..a8b1db1 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -16,6 +16,7 @@ import ddd.domain.Order;
 import ddd.domain.OrderLine;
 import ddd.domain.OrderLineFactory;
 import ddd.domain.OrderPlaced;
+import ddd.domain.OrderRepository;
 import ddd.domain.Product;
 import ddd.domain.ProductRepository;
 import ddd.infrastructure.MemoryMemberRepository;
@@ -39,7 +40,8 @@ public class Main {
         EventPublisher eventPublisher = new EventPublisher();
         DecreaseStockHandler decreaseStockHandler = new DecreaseStockHandler(productRepository);
         eventPublisher.subscribe(OrderPlaced.class, decreaseStockHandler::handle);
-        OrderApplicationService orderService = new OrderApplicationService(new MemoryOrderRepository(),
+        OrderRepository orderRepository = new MemoryOrderRepository();
+        OrderApplicationService orderService = new OrderApplicationService(orderRepository,
                 new OrderLineFactory(productRepository), memberRepository, new DiscountPolicy(), eventPublisher);
 
         List<OrderLineCommand> orderLines = List.of(
@@ -175,5 +177,13 @@ public class Main {
         Product book = productRepository.findById(bookLine.productId()).orElseThrow();
         System.out.println("가격 바뀐 뒤: " + book.getId() + "번 상품 " + book.getPrice().amount() + "원, 주문 항목 "
                 + bookLine.price().amount() + "원, 주문 금액 " + firstOrder.getTotalPrice().amount() + "원");
+
+        Order orderA = orderService.findOrder(order5Id);
+        Order orderB = orderService.findOrder(order5Id);
+        orderA.ship();
+        orderRepository.save(orderA);
+        orderB.cancel();
+        orderRepository.save(orderB);
+        System.out.println("동시 수정: " + orderService.findOrder(order5Id).getStatus().label());
     }
 }
```

## T2 저장할 때마다 주문 버전 올리기
커밋: 1c6f7bb

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/domain/Order.java | 고침 |
| ddd/src/ddd/infrastructure/MemoryOrderRepository.java | 고침 |

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index b9b8532..0154b5f 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -26,6 +26,7 @@
 | 주문 저장소 | OrderRepository | 주문을 넣어 두고 주문 번호로 다시 꺼내는 곳 |
 | 저장하기 | save | 주문을 주문 저장소에 넣는다. 같은 번호가 있으면 새 주문으로 바꾼다 |
 | 주문 번호로 찾기 | findById | 주문 번호로 주문 저장소에서 주문을 꺼낸다. 없으면 빈 결과다 |
+| 버전 | version | 주문이 주문 저장소에 몇 번 저장됐는지. 새 주문은 0이고 저장할 때마다 1씩 오른다. 꺼낸 주문은 저장된 버전을 가진다 |
 | 주문 응용 서비스 | OrderApplicationService | 주문 유스케이스를 하는 곳. 주문을 꺼내고, 주문에게 시키고, 저장한다. 규칙은 갖지 않는다 |
 | 주문하기 명령 | PlaceOrderCommand | 주문하기에 넣는 회원 번호, 주문 항목 명령 목록, 배송 주소 묶음 |
 | 주문 항목 명령 | OrderLineCommand | 주문 항목을 만들 상품 번호, 수량 묶음 |
diff --git a/ddd/src/ddd/domain/Order.java b/ddd/src/ddd/domain/Order.java
index 843e359..91b39ac 100644
--- a/ddd/src/ddd/domain/Order.java
+++ b/ddd/src/ddd/domain/Order.java
@@ -16,6 +16,7 @@ public class Order implements Serializable {
     private Money discountPrice;
     private OrderStatus status;
     private List<Object> events;
+    private long version;
 
     public Order(long id, long memberId, List<OrderLine> orderLines, Address address) {
         if (orderLines.isEmpty()) {
@@ -78,6 +79,14 @@ public class Order implements Serializable {
         return List.copyOf(events);
     }
 
+    public long getVersion() {
+        return version;
+    }
+
+    public void increaseVersion() {
+        version++;
+    }
+
     public void addOrderLine(OrderLine orderLine) {
         if (status != OrderStatus.ORDERED) {
             throw new IllegalStateException("주문 접수인 주문에만 항목을 더할 수 있습니다");
diff --git a/ddd/src/ddd/infrastructure/MemoryOrderRepository.java b/ddd/src/ddd/infrastructure/MemoryOrderRepository.java
index 94cb6a0..6375bd6 100644
--- a/ddd/src/ddd/infrastructure/MemoryOrderRepository.java
+++ b/ddd/src/ddd/infrastructure/MemoryOrderRepository.java
@@ -18,6 +18,7 @@ public class MemoryOrderRepository implements OrderRepository {
 
     @Override
     public void save(Order order) {
+        order.increaseVersion();
         orders.put(order.getId(), copy(order));
     }
 
```

## T3 버전이 다르면 저장 거절하기
커밋: 2a1cb4e

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/infrastructure/MemoryOrderRepository.java | 고침 |

```diff
diff --git a/ddd/glossary.md b/ddd/glossary.md
index 0154b5f..54306d8 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -24,7 +24,7 @@
 | 배송 완료 | DELIVERED | 배송 완료하기를 마친 상태. 더 바뀌지 않는다 |
 | 취소 | CANCELED | 주문 취소하기를 마친 상태 |
 | 주문 저장소 | OrderRepository | 주문을 넣어 두고 주문 번호로 다시 꺼내는 곳 |
-| 저장하기 | save | 주문을 주문 저장소에 넣는다. 같은 번호가 있으면 새 주문으로 바꾼다 |
+| 저장하기 | save | 주문을 주문 저장소에 넣는다. 같은 번호가 있으면 새 주문으로 바꾼다. 저장된 주문과 버전이 다르면 다른 곳에서 먼저 바꾼 주문이라 거절한다 |
 | 주문 번호로 찾기 | findById | 주문 번호로 주문 저장소에서 주문을 꺼낸다. 없으면 빈 결과다 |
 | 버전 | version | 주문이 주문 저장소에 몇 번 저장됐는지. 새 주문은 0이고 저장할 때마다 1씩 오른다. 꺼낸 주문은 저장된 버전을 가진다 |
 | 주문 응용 서비스 | OrderApplicationService | 주문 유스케이스를 하는 곳. 주문을 꺼내고, 주문에게 시키고, 저장한다. 규칙은 갖지 않는다 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index a8b1db1..6f7982b 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -182,8 +182,12 @@ public class Main {
         Order orderB = orderService.findOrder(order5Id);
         orderA.ship();
         orderRepository.save(orderA);
-        orderB.cancel();
-        orderRepository.save(orderB);
-        System.out.println("동시 수정: " + orderService.findOrder(order5Id).getStatus().label());
+        try {
+            orderB.cancel();
+            orderRepository.save(orderB);
+            System.out.println("동시 수정: " + orderService.findOrder(order5Id).getStatus().label());
+        } catch (IllegalStateException e) {
+            System.out.println("동시 수정: " + e.getMessage());
+        }
     }
 }
diff --git a/ddd/src/ddd/infrastructure/MemoryOrderRepository.java b/ddd/src/ddd/infrastructure/MemoryOrderRepository.java
index 6375bd6..4eb0106 100644
--- a/ddd/src/ddd/infrastructure/MemoryOrderRepository.java
+++ b/ddd/src/ddd/infrastructure/MemoryOrderRepository.java
@@ -18,6 +18,10 @@ public class MemoryOrderRepository implements OrderRepository {
 
     @Override
     public void save(Order order) {
+        Order saved = orders.get(order.getId());
+        if (saved != null && saved.getVersion() != order.getVersion()) {
+            throw new IllegalStateException("다른 곳에서 먼저 바꾼 주문입니다");
+        }
         order.increaseVersion();
         orders.put(order.getId(), copy(order));
     }
```

## T4 복사 생성자로 버전도 복사하기
커밋: a312669. 이 장 시점 코드에 얹은 patch는 fixes/a312669.patch

| 파일 | 변경 |
|---|---|
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/application/DecreaseStockHandler.java | 지움 |
| ddd/src/ddd/application/EventPublisher.java | 지움 |
| ddd/src/ddd/application/OrderLineCommand.java | 지움 |
| ddd/src/ddd/application/PlaceOrderCommand.java | 지움 |
| ddd/src/ddd/domain/DiscountPolicy.java | 지움 |
| ddd/src/ddd/domain/Money.java | 지움 |
| ddd/src/ddd/domain/OrderLine.java | 지움 |
| ddd/src/ddd/domain/OrderPlaced.java | 지움 |
| ddd/src/ddd/domain/OrderStatus.java | 지움 |
| ddd/src/ddd/infrastructure/MemoryOrderRepository.java | 지움 |
| ddd/src/ddd/order/application/DecreaseStockHandler.java | 새로 |
| ddd/src/ddd/order/application/EventHandler.java | 새로 |
| ddd/src/ddd/order/application/EventPublisher.java | 새로 |
| ddd/src/ddd/order/application/OrderItemRequest.java | 새로 |
| ddd/src/ddd/order/application/OrderRequest.java | 새로 |
| ddd/src/ddd/order/application/OrderService.java | 옮김. ddd/src/ddd/application/OrderApplicationService.java에서 옮기고 고침 |
| ddd/src/ddd/order/domain/Address.java | 옮김. ddd/src/ddd/domain/Address.java에서 옮기고 고침 |
| ddd/src/ddd/order/domain/DiscountPolicy.java | 새로 |
| ddd/src/ddd/order/domain/FreeShippingAreaSpecification.java | 옮김. ddd/src/ddd/domain/FreeShippingAreaSpecification.java에서 옮기고 고침 |
| ddd/src/ddd/order/domain/FreeShippingPriceSpecification.java | 옮김. ddd/src/ddd/domain/FreeShippingPriceSpecification.java에서 옮기고 고침 |
| ddd/src/ddd/order/domain/FreeShippingSpecification.java | 옮김. ddd/src/ddd/domain/FreeShippingSpecification.java에서 옮기고 고침 |
| ddd/src/ddd/order/domain/Member.java | 옮김. ddd/src/ddd/domain/Member.java에서 옮기고 고침 |
| ddd/src/ddd/order/domain/MemberGrade.java | 옮김. ddd/src/ddd/domain/MemberGrade.java에서 옮기고 고침 |
| ddd/src/ddd/order/domain/MemberRepository.java | 옮김. ddd/src/ddd/domain/MemberRepository.java에서 옮기고 고침 |
| ddd/src/ddd/order/domain/Order.java | 옮김. ddd/src/ddd/domain/Order.java에서 옮기고 고침 |
| ddd/src/ddd/order/domain/OrderItem.java | 새로 |
| ddd/src/ddd/order/domain/OrderItemFactory.java | 옮김. ddd/src/ddd/domain/OrderLineFactory.java에서 옮기고 고침 |
| ddd/src/ddd/order/domain/OrderPlaced.java | 새로 |
| ddd/src/ddd/order/domain/OrderRepository.java | 옮김. ddd/src/ddd/domain/OrderRepository.java에서 옮기고 고침 |
| ddd/src/ddd/order/domain/OrderSpecification.java | 옮김. ddd/src/ddd/domain/OrderSpecification.java에서 옮기고 고침 |
| ddd/src/ddd/order/domain/OrderStatus.java | 새로 |
| ddd/src/ddd/order/domain/Product.java | 옮김. ddd/src/ddd/domain/Product.java에서 옮기고 고침 |
| ddd/src/ddd/order/domain/ProductRepository.java | 옮김. ddd/src/ddd/domain/ProductRepository.java에서 옮기고 고침 |
| ddd/src/ddd/order/infrastructure/MemoryMemberRepository.java | 옮김. ddd/src/ddd/infrastructure/MemoryMemberRepository.java에서 옮기고 고침 |
| ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java | 새로 |
| ddd/src/ddd/order/infrastructure/MemoryProductRepository.java | 옮김. ddd/src/ddd/infrastructure/MemoryProductRepository.java에서 옮기고 고침 |

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
index 6f7982b..5a25cbe 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -2,192 +2,66 @@ package ddd;
 
 import java.util.List;
 
-import ddd.application.DecreaseStockHandler;
-import ddd.application.EventPublisher;
-import ddd.application.OrderApplicationService;
-import ddd.application.OrderLineCommand;
-import ddd.application.PlaceOrderCommand;
-import ddd.domain.DiscountPolicy;
-import ddd.domain.Member;
-import ddd.domain.MemberGrade;
-import ddd.domain.MemberRepository;
-import ddd.domain.Money;
-import ddd.domain.Order;
-import ddd.domain.OrderLine;
-import ddd.domain.OrderLineFactory;
-import ddd.domain.OrderPlaced;
-import ddd.domain.OrderRepository;
-import ddd.domain.Product;
-import ddd.domain.ProductRepository;
-import ddd.infrastructure.MemoryMemberRepository;
-import ddd.infrastructure.MemoryOrderRepository;
-import ddd.infrastructure.MemoryProductRepository;
+import ddd.order.application.EventPublisher;
+import ddd.order.application.OrderItemRequest;
+import ddd.order.application.OrderRequest;
+import ddd.order.application.OrderService;
+import ddd.order.domain.DiscountPolicy;
+import ddd.order.domain.Member;
+import ddd.order.domain.MemberGrade;
+import ddd.order.domain.MemberRepository;
+import ddd.order.domain.Order;
+import ddd.order.domain.OrderItemFactory;
+import ddd.order.domain.OrderRepository;
+import ddd.order.domain.Product;
+import ddd.order.domain.ProductRepository;
+import ddd.order.infrastructure.MemoryMemberRepository;
+import ddd.order.infrastructure.MemoryOrderRepository;
+import ddd.order.infrastructure.MemoryProductRepository;
 
 public class Main {
 
     public static void main(String[] args) {
-        System.out.println("온라인 주문 프로그램을 시작합니다");
+        System.out.println("===== 동시 수정 막기 =====");
 
         MemberRepository memberRepository = new MemoryMemberRepository();
-        memberRepository.save(new Member(1, "김철수", MemberGrade.VIP));
-        memberRepository.save(new Member(2, "이영희", MemberGrade.BASIC));
+        memberRepository.save(new Member(1, "김철수", MemberGrade.BASIC));
 
         ProductRepository productRepository = new MemoryProductRepository();
-        productRepository.save(new Product(1, "자바 책", new Money(15000), 10));
-        productRepository.save(new Product(2, "볼펜", new Money(1000), 10));
-        productRepository.save(new Product(3, "공책", new Money(2000), 10));
+        productRepository.save(new Product(1, "자바 책", 15000, 10));
 
-        EventPublisher eventPublisher = new EventPublisher();
-        DecreaseStockHandler decreaseStockHandler = new DecreaseStockHandler(productRepository);
-        eventPublisher.subscribe(OrderPlaced.class, decreaseStockHandler::handle);
         OrderRepository orderRepository = new MemoryOrderRepository();
-        OrderApplicationService orderService = new OrderApplicationService(orderRepository,
-                new OrderLineFactory(productRepository), memberRepository, new DiscountPolicy(), eventPublisher);
-
-        List<OrderLineCommand> orderLines = List.of(
-                new OrderLineCommand(1, 2),
-                new OrderLineCommand(2, 1));
-        long orderId = orderService.placeOrder(new PlaceOrderCommand(1, orderLines, "서울시 강남구"));
-        Order order = orderService.findOrder(orderId);
-        Member member = memberRepository.findById(order.getMemberId()).orElseThrow();
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
+        OrderService orderService = new OrderService(orderRepository,
+                new OrderItemFactory(productRepository), memberRepository, new DiscountPolicy(), new EventPublisher());
 
+        System.out.println();
+        System.out.println("[같은 주문을 두 번 꺼내 저장하기]");
+        long orderId = orderService.placeOrder(new OrderRequest(1,
+                List.of(new OrderItemRequest(1, 2)), "서울시 강남구"));
         orderService.pay(orderId);
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
+        Order first = orderService.findOrder(orderId);
+        Order second = orderService.findOrder(orderId);
+        String statusBefore = first.getStatus().label();
+        first.ship();
+        orderRepository.save(first);
+        String statusAfterShip = orderService.findOrder(orderId).getStatus().label();
+        System.out.println("첫째로 꺼내 배송: " + statusBefore + " -> " + statusAfterShip);
         try {
-            orderB.cancel();
-            orderRepository.save(orderB);
-            System.out.println("동시 수정: " + orderService.findOrder(order5Id).getStatus().label());
+            second.cancel();
+            orderRepository.save(second);
+            System.out.println("둘째로 꺼내 취소: " + statusAfterShip + " -> "
+                    + orderService.findOrder(orderId).getStatus().label());
         } catch (IllegalStateException e) {
-            System.out.println("동시 수정: " + e.getMessage());
+            System.out.println("둘째로 꺼내 취소: " + e.getMessage());
         }
+        System.out.println("저장된 주문 상태: " + orderService.findOrder(orderId).getStatus().label());
+
+        System.out.println();
+        System.out.println("[저장할 때마다 버전 올리기]");
+        long newOrderId = orderService.placeOrder(new OrderRequest(1,
+                List.of(new OrderItemRequest(1, 1)), "서울시 강남구"));
+        long versionBefore = orderService.findOrder(newOrderId).getVersion();
+        orderService.pay(newOrderId);
+        System.out.println("결제하고 저장: 버전 " + versionBefore + " -> " + orderService.findOrder(newOrderId).getVersion());
     }
 }
```

```diff
diff --git a/ddd/src/ddd/application/DecreaseStockHandler.java b/ddd/src/ddd/application/DecreaseStockHandler.java
deleted file mode 100644
index 6076d41..0000000
--- a/ddd/src/ddd/application/DecreaseStockHandler.java
+++ /dev/null
@@ -1,23 +0,0 @@
-package ddd.application;
-
-import ddd.domain.OrderLine;
-import ddd.domain.OrderPlaced;
-import ddd.domain.Product;
-import ddd.domain.ProductRepository;
-
-public class DecreaseStockHandler {
-
-    private final ProductRepository productRepository;
-
-    public DecreaseStockHandler(ProductRepository productRepository) {
-        this.productRepository = productRepository;
-    }
-
-    public void handle(OrderPlaced event) {
-        for (OrderLine orderLine : event.orderLines()) {
-            Product product = productRepository.findById(orderLine.productId()).orElseThrow();
-            product.decreaseStock(orderLine.quantity());
-            productRepository.save(product);
-        }
-    }
-}
```

```diff
diff --git a/ddd/src/ddd/application/EventPublisher.java b/ddd/src/ddd/application/EventPublisher.java
deleted file mode 100644
index 14fa5c4..0000000
--- a/ddd/src/ddd/application/EventPublisher.java
+++ /dev/null
@@ -1,32 +0,0 @@
-package ddd.application;
-
-import java.util.ArrayList;
-import java.util.HashMap;
-import java.util.List;
-import java.util.Map;
-import java.util.function.Consumer;
-
-public class EventPublisher {
-
-    private final Map<Class<?>, List<Consumer<Object>>> subscribers = new HashMap<>();
-    private final List<Object> waitingEvents = new ArrayList<>();
-
-    public <T> void subscribe(Class<T> eventType, Consumer<T> subscriber) {
-        subscribers.computeIfAbsent(eventType, type -> new ArrayList<>())
-                .add(event -> subscriber.accept(eventType.cast(event)));
-    }
-
-    public void publish(List<?> events) {
-        waitingEvents.addAll(events);
-    }
-
-    public void processEvents() {
-        List<Object> events = new ArrayList<>(waitingEvents);
-        waitingEvents.clear();
-        for (Object event : events) {
-            for (Consumer<Object> subscriber : subscribers.getOrDefault(event.getClass(), List.of())) {
-                subscriber.accept(event);
-            }
-        }
-    }
-}
```

```diff
diff --git a/ddd/src/ddd/application/OrderLineCommand.java b/ddd/src/ddd/application/OrderLineCommand.java
deleted file mode 100644
index 1be8bc9..0000000
--- a/ddd/src/ddd/application/OrderLineCommand.java
+++ /dev/null
@@ -1,4 +0,0 @@
-package ddd.application;
-
-public record OrderLineCommand(long productId, int quantity) {
-}
```

```diff
diff --git a/ddd/src/ddd/application/PlaceOrderCommand.java b/ddd/src/ddd/application/PlaceOrderCommand.java
deleted file mode 100644
index aff512e..0000000
--- a/ddd/src/ddd/application/PlaceOrderCommand.java
+++ /dev/null
@@ -1,6 +0,0 @@
-package ddd.application;
-
-import java.util.List;
-
-public record PlaceOrderCommand(long memberId, List<OrderLineCommand> orderLines, String address) {
-}
```

```diff
diff --git a/ddd/src/ddd/domain/DiscountPolicy.java b/ddd/src/ddd/domain/DiscountPolicy.java
deleted file mode 100644
index 8db2d32..0000000
--- a/ddd/src/ddd/domain/DiscountPolicy.java
+++ /dev/null
@@ -1,11 +0,0 @@
-package ddd.domain;
-
-public class DiscountPolicy {
-
-    public Money discount(Member member, Order order) {
-        if (member.getGrade() == MemberGrade.VIP) {
-            return new Money(order.getTotalPrice().amount() * 10 / 100);
-        }
-        return new Money(0);
-    }
-}
```

```diff
diff --git a/ddd/src/ddd/domain/Money.java b/ddd/src/ddd/domain/Money.java
deleted file mode 100644
index ba8f995..0000000
--- a/ddd/src/ddd/domain/Money.java
+++ /dev/null
@@ -1,24 +0,0 @@
-package ddd.domain;
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

```diff
diff --git a/ddd/src/ddd/domain/OrderLine.java b/ddd/src/ddd/domain/OrderLine.java
deleted file mode 100644
index a2de83e..0000000
--- a/ddd/src/ddd/domain/OrderLine.java
+++ /dev/null
@@ -1,16 +0,0 @@
-package ddd.domain;
-
-import java.io.Serializable;
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
diff --git a/ddd/src/ddd/domain/OrderPlaced.java b/ddd/src/ddd/domain/OrderPlaced.java
deleted file mode 100644
index 9a22909..0000000
--- a/ddd/src/ddd/domain/OrderPlaced.java
+++ /dev/null
@@ -1,7 +0,0 @@
-package ddd.domain;
-
-import java.io.Serializable;
-import java.util.List;
-
-public record OrderPlaced(long orderId, List<OrderLine> orderLines) implements Serializable {
-}
```

```diff
diff --git a/ddd/src/ddd/domain/OrderStatus.java b/ddd/src/ddd/domain/OrderStatus.java
deleted file mode 100644
index 486e63f..0000000
--- a/ddd/src/ddd/domain/OrderStatus.java
+++ /dev/null
@@ -1,28 +0,0 @@
-package ddd.domain;
-
-public enum OrderStatus {
-    ORDERED("주문 접수"),
-    PAID("결제 완료"),
-    SHIPPING("배송 중"),
-    DELIVERED("배송 완료"),
-    CANCELED("취소");
-
-    private final String label;
-
-    OrderStatus(String label) {
-        this.label = label;
-    }
-
-    public String label() {
-        return label;
-    }
-
-    public boolean canChangeTo(OrderStatus next) {
-        return switch (this) {
-            case ORDERED -> next == PAID || next == CANCELED;
-            case PAID -> next == SHIPPING || next == CANCELED;
-            case SHIPPING -> next == DELIVERED;
-            case DELIVERED, CANCELED -> false;
-        };
-    }
-}
```

```diff
diff --git a/ddd/src/ddd/infrastructure/MemoryOrderRepository.java b/ddd/src/ddd/infrastructure/MemoryOrderRepository.java
deleted file mode 100644
index 4eb0106..0000000
--- a/ddd/src/ddd/infrastructure/MemoryOrderRepository.java
+++ /dev/null
@@ -1,49 +0,0 @@
-package ddd.infrastructure;
-
-import java.io.ByteArrayInputStream;
-import java.io.ByteArrayOutputStream;
-import java.io.IOException;
-import java.io.ObjectInputStream;
-import java.io.ObjectOutputStream;
-import java.util.HashMap;
-import java.util.Map;
-import java.util.Optional;
-
-import ddd.domain.Order;
-import ddd.domain.OrderRepository;
-
-public class MemoryOrderRepository implements OrderRepository {
-
-    private final Map<Long, Order> orders = new HashMap<>();
-
-    @Override
-    public void save(Order order) {
-        Order saved = orders.get(order.getId());
-        if (saved != null && saved.getVersion() != order.getVersion()) {
-            throw new IllegalStateException("다른 곳에서 먼저 바꾼 주문입니다");
-        }
-        order.increaseVersion();
-        orders.put(order.getId(), copy(order));
-    }
-
-    @Override
-    public Optional<Order> findById(long id) {
-        Order order = orders.get(id);
-        if (order == null) {
-            return Optional.empty();
-        }
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
-    }
-}
```

```diff
diff --git a/ddd/src/ddd/order/application/DecreaseStockHandler.java b/ddd/src/ddd/order/application/DecreaseStockHandler.java
new file mode 100644
index 0000000..ae37aad
--- /dev/null
+++ b/ddd/src/ddd/order/application/DecreaseStockHandler.java
@@ -0,0 +1,27 @@
+package ddd.order.application;
+
+import ddd.order.domain.OrderItem;
+import ddd.order.domain.OrderPlaced;
+import ddd.order.domain.Product;
+import ddd.order.domain.ProductRepository;
+
+public class DecreaseStockHandler implements EventHandler {
+
+    private final ProductRepository productRepository;
+
+    public DecreaseStockHandler(ProductRepository productRepository) {
+        this.productRepository = productRepository;
+    }
+
+    @Override
+    public void handle(Object event) {
+        if (!(event instanceof OrderPlaced placed)) {
+            return;
+        }
+        for (OrderItem item : placed.items()) {
+            Product product = productRepository.findById(item.getProductId()).orElseThrow();
+            product.decreaseStock(item.getQuantity());
+            productRepository.save(product);
+        }
+    }
+}
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
new file mode 100644
index 0000000..cc45d9e
--- /dev/null
+++ b/ddd/src/ddd/order/application/EventPublisher.java
@@ -0,0 +1,28 @@
+package ddd.order.application;
+
+import java.util.ArrayList;
+import java.util.List;
+
+public class EventPublisher {
+
+    private final List<EventHandler> handlers = new ArrayList<>();
+    private final List<Object> waitingEvents = new ArrayList<>();
+
+    public void subscribe(EventHandler handler) {
+        handlers.add(handler);
+    }
+
+    public void publish(List<Object> events) {
+        waitingEvents.addAll(events);
+    }
+
+    public void processEvents() {
+        List<Object> events = new ArrayList<>(waitingEvents);
+        waitingEvents.clear();
+        for (Object event : events) {
+            for (EventHandler handler : handlers) {
+                handler.handle(event);
+            }
+        }
+    }
+}
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
diff --git a/ddd/src/ddd/application/OrderApplicationService.java b/ddd/src/ddd/order/application/OrderService.java
similarity index 61%
rename from ddd/src/ddd/application/OrderApplicationService.java
rename to ddd/src/ddd/order/application/OrderService.java
index 54f77bb..07e154e 100644
--- a/ddd/src/ddd/application/OrderApplicationService.java
+++ b/ddd/src/ddd/order/application/OrderService.java
@@ -1,41 +1,41 @@
-package ddd.application;
+package ddd.order.application;
 
 import java.util.ArrayList;
 import java.util.List;
 
-import ddd.domain.Address;
-import ddd.domain.DiscountPolicy;
-import ddd.domain.Member;
-import ddd.domain.MemberRepository;
-import ddd.domain.Order;
-import ddd.domain.OrderLine;
-import ddd.domain.OrderLineFactory;
-import ddd.domain.OrderRepository;
+import ddd.order.domain.Address;
+import ddd.order.domain.DiscountPolicy;
+import ddd.order.domain.Member;
+import ddd.order.domain.MemberRepository;
+import ddd.order.domain.Order;
+import ddd.order.domain.OrderItem;
+import ddd.order.domain.OrderItemFactory;
+import ddd.order.domain.OrderRepository;
 
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
diff --git a/ddd/src/ddd/domain/Address.java b/ddd/src/ddd/order/domain/Address.java
similarity index 61%
rename from ddd/src/ddd/domain/Address.java
rename to ddd/src/ddd/order/domain/Address.java
index 5cfd21e..2a69b63 100644
--- a/ddd/src/ddd/domain/Address.java
+++ b/ddd/src/ddd/order/domain/Address.java
@@ -1,8 +1,6 @@
-package ddd.domain;
+package ddd.order.domain;
 
-import java.io.Serializable;
-
-public record Address(String value) implements Serializable {
+public record Address(String value) {
 
     public Address {
         if (value == null || value.isBlank()) {
```

```diff
diff --git a/ddd/src/ddd/order/domain/DiscountPolicy.java b/ddd/src/ddd/order/domain/DiscountPolicy.java
new file mode 100644
index 0000000..5ddb3cc
--- /dev/null
+++ b/ddd/src/ddd/order/domain/DiscountPolicy.java
@@ -0,0 +1,11 @@
+package ddd.order.domain;
+
+public class DiscountPolicy {
+
+    public int discount(Member member, Order order) {
+        if (member.getGrade() == MemberGrade.VIP) {
+            return order.getTotalPrice() * 10 / 100;
+        }
+        return 0;
+    }
+}
```

```diff
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
 
```

```diff
diff --git a/ddd/src/ddd/domain/FreeShippingPriceSpecification.java b/ddd/src/ddd/order/domain/FreeShippingPriceSpecification.java
similarity index 65%
rename from ddd/src/ddd/domain/FreeShippingPriceSpecification.java
rename to ddd/src/ddd/order/domain/FreeShippingPriceSpecification.java
index 55788b8..c019b08 100644
--- a/ddd/src/ddd/domain/FreeShippingPriceSpecification.java
+++ b/ddd/src/ddd/order/domain/FreeShippingPriceSpecification.java
@@ -1,9 +1,9 @@
-package ddd.domain;
+package ddd.order.domain;
 
 public class FreeShippingPriceSpecification implements OrderSpecification {
 
     @Override
     public boolean isSatisfiedBy(Order order) {
-        return order.getPaymentPrice().amount() >= 50000;
+        return order.getPaymentPrice() >= 50000;
     }
 }
```

```diff
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
 
```

```diff
diff --git a/ddd/src/ddd/domain/Member.java b/ddd/src/ddd/order/domain/Member.java
similarity index 96%
rename from ddd/src/ddd/domain/Member.java
rename to ddd/src/ddd/order/domain/Member.java
index 90209c9..f33b745 100644
--- a/ddd/src/ddd/domain/Member.java
+++ b/ddd/src/ddd/order/domain/Member.java
@@ -1,4 +1,4 @@
-package ddd.domain;
+package ddd.order.domain;
 
 public class Member {
 
```

```diff
diff --git a/ddd/src/ddd/domain/MemberGrade.java b/ddd/src/ddd/order/domain/MemberGrade.java
similarity index 64%
rename from ddd/src/ddd/domain/MemberGrade.java
rename to ddd/src/ddd/order/domain/MemberGrade.java
index 412ba28..b7dae0a 100644
--- a/ddd/src/ddd/domain/MemberGrade.java
+++ b/ddd/src/ddd/order/domain/MemberGrade.java
@@ -1,4 +1,4 @@
-package ddd.domain;
+package ddd.order.domain;
 
 public enum MemberGrade {
     BASIC,
```

```diff
diff --git a/ddd/src/ddd/domain/MemberRepository.java b/ddd/src/ddd/order/domain/MemberRepository.java
similarity index 84%
rename from ddd/src/ddd/domain/MemberRepository.java
rename to ddd/src/ddd/order/domain/MemberRepository.java
index da4c6c8..2f1a103 100644
--- a/ddd/src/ddd/domain/MemberRepository.java
+++ b/ddd/src/ddd/order/domain/MemberRepository.java
@@ -1,4 +1,4 @@
-package ddd.domain;
+package ddd.order.domain;
 
 import java.util.Optional;
 
```

```diff
diff --git a/ddd/src/ddd/domain/Order.java b/ddd/src/ddd/order/domain/Order.java
similarity index 59%
rename from ddd/src/ddd/domain/Order.java
rename to ddd/src/ddd/order/domain/Order.java
index 91b39ac..2cf2f08 100644
--- a/ddd/src/ddd/domain/Order.java
+++ b/ddd/src/ddd/order/domain/Order.java
@@ -1,39 +1,48 @@
-package ddd.domain;
+package ddd.order.domain;
 
-import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.List;
 
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
@@ -44,31 +53,31 @@ public class Order implements Serializable {
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
@@ -87,16 +96,16 @@ public class Order implements Serializable {
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
@@ -104,21 +113,21 @@ public class Order implements Serializable {
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
diff --git a/ddd/src/ddd/domain/OrderLineFactory.java b/ddd/src/ddd/order/domain/OrderItemFactory.java
similarity index 56%
rename from ddd/src/ddd/domain/OrderLineFactory.java
rename to ddd/src/ddd/order/domain/OrderItemFactory.java
index 02fd86d..2fb2a68 100644
--- a/ddd/src/ddd/domain/OrderLineFactory.java
+++ b/ddd/src/ddd/order/domain/OrderItemFactory.java
@@ -1,16 +1,16 @@
-package ddd.domain;
+package ddd.order.domain;
 
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
```

```diff
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
 
```

```diff
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
 
```

```diff
diff --git a/ddd/src/ddd/order/domain/OrderStatus.java b/ddd/src/ddd/order/domain/OrderStatus.java
new file mode 100644
index 0000000..d8965da
--- /dev/null
+++ b/ddd/src/ddd/order/domain/OrderStatus.java
@@ -0,0 +1,19 @@
+package ddd.order.domain;
+
+public enum OrderStatus {
+    ORDERED("주문 접수"),
+    PAID("결제 완료"),
+    SHIPPING("배송 중"),
+    DELIVERED("배송 완료"),
+    CANCELED("취소");
+
+    private final String label;
+
+    OrderStatus(String label) {
+        this.label = label;
+    }
+
+    public String label() {
+        return label;
+    }
+}
```

```diff
diff --git a/ddd/src/ddd/domain/Product.java b/ddd/src/ddd/order/domain/Product.java
similarity index 83%
rename from ddd/src/ddd/domain/Product.java
rename to ddd/src/ddd/order/domain/Product.java
index 34f4a9f..d375797 100644
--- a/ddd/src/ddd/domain/Product.java
+++ b/ddd/src/ddd/order/domain/Product.java
@@ -1,13 +1,13 @@
-package ddd.domain;
+package ddd.order.domain;
 
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
@@ -22,7 +22,7 @@ public class Product {
         return name;
     }
 
-    public Money getPrice() {
+    public int getPrice() {
         return price;
     }
 
```

```diff
diff --git a/ddd/src/ddd/domain/ProductRepository.java b/ddd/src/ddd/order/domain/ProductRepository.java
similarity index 84%
rename from ddd/src/ddd/domain/ProductRepository.java
rename to ddd/src/ddd/order/domain/ProductRepository.java
index 9221d84..0d1caa6 100644
--- a/ddd/src/ddd/domain/ProductRepository.java
+++ b/ddd/src/ddd/order/domain/ProductRepository.java
@@ -1,4 +1,4 @@
-package ddd.domain;
+package ddd.order.domain;
 
 import java.util.Optional;
 
```

```diff
diff --git a/ddd/src/ddd/infrastructure/MemoryMemberRepository.java b/ddd/src/ddd/order/infrastructure/MemoryMemberRepository.java
similarity index 80%
rename from ddd/src/ddd/infrastructure/MemoryMemberRepository.java
rename to ddd/src/ddd/order/infrastructure/MemoryMemberRepository.java
index 60f1c50..cf7d00e 100644
--- a/ddd/src/ddd/infrastructure/MemoryMemberRepository.java
+++ b/ddd/src/ddd/order/infrastructure/MemoryMemberRepository.java
@@ -1,11 +1,11 @@
-package ddd.infrastructure;
+package ddd.order.infrastructure;
 
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Optional;
 
-import ddd.domain.Member;
-import ddd.domain.MemberRepository;
+import ddd.order.domain.Member;
+import ddd.order.domain.MemberRepository;
 
 public class MemoryMemberRepository implements MemberRepository {
 
```

```diff
diff --git a/ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java b/ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java
new file mode 100644
index 0000000..b56e377
--- /dev/null
+++ b/ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java
@@ -0,0 +1,32 @@
+package ddd.order.infrastructure;
+
+import java.util.HashMap;
+import java.util.Map;
+import java.util.Optional;
+
+import ddd.order.domain.Order;
+import ddd.order.domain.OrderRepository;
+
+public class MemoryOrderRepository implements OrderRepository {
+
+    private final Map<Long, Order> orders = new HashMap<>();
+
+    @Override
+    public void save(Order order) {
+        Order saved = orders.get(order.getId());
+        if (saved != null && saved.getVersion() != order.getVersion()) {
+            throw new IllegalStateException("다른 곳에서 먼저 바꾼 주문입니다");
+        }
+        order.increaseVersion();
+        orders.put(order.getId(), new Order(order));
+    }
+
+    @Override
+    public Optional<Order> findById(long id) {
+        Order order = orders.get(id);
+        if (order == null) {
+            return Optional.empty();
+        }
+        return Optional.of(new Order(order));
+    }
+}
```

```diff
diff --git a/ddd/src/ddd/infrastructure/MemoryProductRepository.java b/ddd/src/ddd/order/infrastructure/MemoryProductRepository.java
similarity index 80%
rename from ddd/src/ddd/infrastructure/MemoryProductRepository.java
rename to ddd/src/ddd/order/infrastructure/MemoryProductRepository.java
index f13b139..9f16401 100644
--- a/ddd/src/ddd/infrastructure/MemoryProductRepository.java
+++ b/ddd/src/ddd/order/infrastructure/MemoryProductRepository.java
@@ -1,11 +1,11 @@
-package ddd.infrastructure;
+package ddd.order.infrastructure;
 
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Optional;
 
-import ddd.domain.Product;
-import ddd.domain.ProductRepository;
+import ddd.order.domain.Product;
+import ddd.order.domain.ProductRepository;
 
 public class MemoryProductRepository implements ProductRepository {
 
```
