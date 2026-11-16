# shipping-context 코드 요약

## T1 결제 완료를 정해진 글자로 알리기
커밋: a207b1f

| 파일 | 변경 |
|---|---|
| ddd/context-map.md | 고침 |
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/order/application/OrderApplicationService.java | 고침 |
| ddd/src/ddd/order/application/OrderOpenHostService.java | 새로 |

```java
// ddd/src/ddd/order/application/OrderOpenHostService.java
package ddd.order.application;

import java.util.List;

import ddd.order.domain.Order;

public class OrderOpenHostService {

    private final EventPublisher eventPublisher;

    public OrderOpenHostService(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishOrderPaid(Order order) {
        String message = "ORDER_PAID|orderId=" + order.getId() + "|address=" + order.getAddress().value();
        eventPublisher.publish(List.of(message));
    }
}
```

```diff
diff --git a/ddd/context-map.md b/ddd/context-map.md
index bed918d..3ea2d06 100644
--- a/ddd/context-map.md
+++ b/ddd/context-map.md
@@ -32,3 +32,20 @@
 - 주문 컨텍스트의 주문 항목은 주문 때의 상품 이름과 가격을 갖는다
 - 지금은 다른 컨텍스트의 클래스를 import로 바로 쓴다
 - 컨텍스트 사이를 정해진 창구로 잇는 일은 뒤 장에서 한다
+
+## 공개 호스트 서비스와 발행된 언어
+
+- 공개 호스트 서비스(open host service)는 한 컨텍스트가 바깥에 열어 둔 정해진 창구다
+- 주문 컨텍스트의 창구는 OrderOpenHostService다. 결제하기가 끝나면 결제 완료 메시지를 이벤트 발행기에 넘긴다
+- 발행된 언어(published language)는 그 창구가 쓰는 정해진 메시지 모양이다
+- 메시지는 한 줄 글자다. 맨 앞은 종류, 그 뒤는 키=값이고 |로 나눈다
+- 예: ORDER_PAID|orderId=1|address=서울시 강남구
+
+| 칸 | 값 | 뜻 |
+|---|---|---|
+| 종류 | ORDER_PAID | 결제 완료 메시지 |
+| orderId | 숫자 | 결제한 주문 번호 |
+| address | 글자 | 배송 주소 |
+
+- 받는 쪽은 주문 클래스를 몰라도 이 모양만 알면 읽는다
+- 주문 컨텍스트는 안의 모델을 바꿔도 이 모양은 지킨다
diff --git a/ddd/glossary.md b/ddd/glossary.md
index 54306d8..d35be53 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -52,3 +52,5 @@
 | 이벤트 발행기 | EventPublisher | 도메인 이벤트를 받아 쌓아 두었다가, 이벤트를 처리할 때 그 이벤트를 구독한 곳에 보내는 곳. 주문 응용 서비스는 주문을 저장한 뒤 이벤트를 여기에 넘긴다 |
 | 재고 줄이기 | decreaseStock | 상품 재고를 주문 수량만큼 뺀다 |
 | 재고 줄이기 처리기 | DecreaseStockHandler | 주문 접수됨 이벤트를 구독해 주문 항목마다 상품 재고를 줄이고 저장하는 곳. 주문 저장과 따로 일해서 재고는 이벤트를 처리한 뒤에 맞춰진다 |
+| 공개 호스트 서비스 | OrderOpenHostService | 주문 컨텍스트가 바깥 컨텍스트에 알리는 정해진 창구. 결제하기가 끝나면 결제 완료 메시지를 이벤트 발행기에 넘긴다 |
+| 결제 완료 메시지 | ORDER_PAID | 결제한 주문을 바깥에 알리는 한 줄 글자. 종류 ORDER_PAID 뒤에 주문 번호 orderId와 배송 주소 address를 붙인다 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index f8900c1..369dc1b 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -189,5 +189,8 @@ public class Main {
         } catch (IllegalStateException e) {
             System.out.println("동시 수정: " + e.getMessage());
         }
+
+        eventPublisher.subscribe(String.class, message -> System.out.println("결제 완료 메시지: " + message));
+        eventPublisher.processEvents();
     }
 }
diff --git a/ddd/src/ddd/order/application/OrderApplicationService.java b/ddd/src/ddd/order/application/OrderApplicationService.java
index a222533..e7139d4 100644
--- a/ddd/src/ddd/order/application/OrderApplicationService.java
+++ b/ddd/src/ddd/order/application/OrderApplicationService.java
@@ -19,6 +19,7 @@ public class OrderApplicationService {
     private final MemberRepository memberRepository;
     private final DiscountPolicy discountPolicy;
     private final EventPublisher eventPublisher;
+    private final OrderOpenHostService orderOpenHostService;
     private long nextId = 1;
 
     public OrderApplicationService(OrderRepository orderRepository, OrderLineFactory orderLineFactory,
@@ -28,6 +29,7 @@ public class OrderApplicationService {
         this.memberRepository = memberRepository;
         this.discountPolicy = discountPolicy;
         this.eventPublisher = eventPublisher;
+        this.orderOpenHostService = new OrderOpenHostService(eventPublisher);
     }
 
     public long placeOrder(PlaceOrderCommand command) {
@@ -56,6 +58,7 @@ public class OrderApplicationService {
         Member member = memberRepository.findById(order.getMemberId()).orElseThrow();
         order.pay(discountPolicy.discount(member, order));
         orderRepository.save(order);
+        orderOpenHostService.publishOrderPaid(order);
     }
 
     public void ship(long orderId) {
```

## T2 배송 컨텍스트가 메시지 읽어 배송 만들기
커밋: 285dbb7

| 파일 | 변경 |
|---|---|
| ddd/context-map.md | 고침 |
| ddd/glossary.md | 고침 |
| ddd/src/ddd/Main.java | 고침 |
| ddd/src/ddd/shipping/application/OrderPaidMessageHandler.java | 새로 |
| ddd/src/ddd/shipping/domain/Shipment.java | 새로 |
| ddd/src/ddd/shipping/domain/ShipmentRepository.java | 새로 |
| ddd/src/ddd/shipping/infrastructure/MemoryShipmentRepository.java | 새로 |

```java
// ddd/src/ddd/shipping/application/OrderPaidMessageHandler.java
package ddd.shipping.application;

import ddd.shipping.domain.Shipment;
import ddd.shipping.domain.ShipmentRepository;

public class OrderPaidMessageHandler {

    private final ShipmentRepository shipmentRepository;

    public OrderPaidMessageHandler(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    public void handle(String message) {
        String[] fields = message.split("\\|");
        if (!fields[0].equals("ORDER_PAID")) {
            return;
        }
        long orderId = Long.parseLong(fields[1].substring("orderId=".length()));
        String address = fields[2].substring("address=".length());
        shipmentRepository.save(new Shipment(orderId, address));
    }
}
```

```java
// ddd/src/ddd/shipping/domain/Shipment.java
package ddd.shipping.domain;

public class Shipment {

    private final long orderId;
    private final String address;

    public Shipment(long orderId, String address) {
        this.orderId = orderId;
        this.address = address;
    }

    public long getOrderId() {
        return orderId;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Shipment other)) {
            return false;
        }
        return orderId == other.orderId;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(orderId);
    }
}
```

```java
// ddd/src/ddd/shipping/domain/ShipmentRepository.java
package ddd.shipping.domain;

import java.util.Optional;

public interface ShipmentRepository {

    void save(Shipment shipment);

    Optional<Shipment> findByOrderId(long orderId);
}
```

```java
// ddd/src/ddd/shipping/infrastructure/MemoryShipmentRepository.java
package ddd.shipping.infrastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ddd.shipping.domain.Shipment;
import ddd.shipping.domain.ShipmentRepository;

public class MemoryShipmentRepository implements ShipmentRepository {

    private final Map<Long, Shipment> shipments = new HashMap<>();

    @Override
    public void save(Shipment shipment) {
        shipments.put(shipment.getOrderId(), shipment);
    }

    @Override
    public Optional<Shipment> findByOrderId(long orderId) {
        return Optional.ofNullable(shipments.get(orderId));
    }
}
```

```diff
diff --git a/ddd/context-map.md b/ddd/context-map.md
index 3ea2d06..bbba310 100644
--- a/ddd/context-map.md
+++ b/ddd/context-map.md
@@ -7,6 +7,7 @@
 | 주문 | 핵심 | 주문, 결제, 배송, 취소 규칙이 이 가게가 잘해야 하는 일이다 | 주문 컨텍스트 | ddd.order |
 | 상품 | 지원 | 주문에 쓸 상품과 재고를 맡는다. 가게에 맞게 직접 만든다 | 상품 컨텍스트 | ddd.catalog |
 | 회원 | 일반 | 이름과 등급만 맡는다. 어느 가게나 비슷해서 사다 써도 된다 | 회원 컨텍스트 | ddd.member |
+| 배송 | 지원 | 결제한 주문을 받을 곳으로 보낸다. 주문을 돕는다 | 배송 컨텍스트 | ddd.shipping |
 
 - 하위 도메인은 업무를 나눈 것이다
 - 바운디드 컨텍스트(bounded context)는 한 모델과 한 말이 통하는 코드의 경계다
@@ -26,12 +27,14 @@
 | 주문 | 상품 | 주문 항목 팩토리가 상품 저장소에서 상품 이름과 가격을 읽는다 |
 | 주문 | 회원 | 할인 정책과 주문 응용 서비스가 회원과 회원 등급을 읽는다 |
 | 상품 | 주문 | 재고 줄이기 처리기가 주문 접수됨 이벤트를 받아 재고를 줄인다 |
+| 배송 | 주문 | 결제 완료 메시지 처리기가 결제 완료 메시지 글자를 읽어 배송을 만든다 |
 | 주문, 상품 | 공유 커널 | 돈 |
 
 - 같은 상품이라도 상품 컨텍스트의 상품은 지금 가격과 재고를 갖는다
 - 주문 컨텍스트의 주문 항목은 주문 때의 상품 이름과 가격을 갖는다
-- 지금은 다른 컨텍스트의 클래스를 import로 바로 쓴다
-- 컨텍스트 사이를 정해진 창구로 잇는 일은 뒤 장에서 한다
+- 주문과 상품은 다른 컨텍스트의 클래스를 import로 바로 쓴다
+- 배송은 주문 컨텍스트의 클래스를 import하지 않는다. 주문의 공개 호스트 서비스가 보낸 메시지 글자만 읽는다
+- 배송 컨텍스트의 배송은 주문 번호와 배송 주소를 자기 모양(숫자와 글자)으로 갖는다
 
 ## 공개 호스트 서비스와 발행된 언어
 
diff --git a/ddd/glossary.md b/ddd/glossary.md
index d35be53..b209b58 100644
--- a/ddd/glossary.md
+++ b/ddd/glossary.md
@@ -54,3 +54,6 @@
 | 재고 줄이기 처리기 | DecreaseStockHandler | 주문 접수됨 이벤트를 구독해 주문 항목마다 상품 재고를 줄이고 저장하는 곳. 주문 저장과 따로 일해서 재고는 이벤트를 처리한 뒤에 맞춰진다 |
 | 공개 호스트 서비스 | OrderOpenHostService | 주문 컨텍스트가 바깥 컨텍스트에 알리는 정해진 창구. 결제하기가 끝나면 결제 완료 메시지를 이벤트 발행기에 넘긴다 |
 | 결제 완료 메시지 | ORDER_PAID | 결제한 주문을 바깥에 알리는 한 줄 글자. 종류 ORDER_PAID 뒤에 주문 번호 orderId와 배송 주소 address를 붙인다 |
+| 배송 | Shipment | 배송 컨텍스트가 결제한 주문을 보내려고 만드는 것. 주문 번호와 배송 주소를 가진다. 주문 번호가 같으면 같은 배송이다 |
+| 배송 저장소 | ShipmentRepository | 배송을 넣어 두고 주문 번호로 다시 꺼내는 곳 |
+| 결제 완료 메시지 처리기 | OrderPaidMessageHandler | 결제 완료 메시지 글자를 받아 주문 번호와 배송 주소를 읽고 배송을 만들어 저장하는 곳. 결제 완료가 아닌 메시지는 넘긴다 |
diff --git a/ddd/src/ddd/Main.java b/ddd/src/ddd/Main.java
index 369dc1b..01b6268 100644
--- a/ddd/src/ddd/Main.java
+++ b/ddd/src/ddd/Main.java
@@ -22,6 +22,10 @@ import ddd.order.domain.OrderPlaced;
 import ddd.order.domain.OrderRepository;
 import ddd.order.infrastructure.MemoryOrderRepository;
 import ddd.shared.Money;
+import ddd.shipping.application.OrderPaidMessageHandler;
+import ddd.shipping.domain.Shipment;
+import ddd.shipping.domain.ShipmentRepository;
+import ddd.shipping.infrastructure.MemoryShipmentRepository;
 
 public class Main {
 
@@ -190,7 +194,12 @@ public class Main {
             System.out.println("동시 수정: " + e.getMessage());
         }
 
+        ShipmentRepository shipmentRepository = new MemoryShipmentRepository();
+        OrderPaidMessageHandler orderPaidMessageHandler = new OrderPaidMessageHandler(shipmentRepository);
         eventPublisher.subscribe(String.class, message -> System.out.println("결제 완료 메시지: " + message));
+        eventPublisher.subscribe(String.class, orderPaidMessageHandler::handle);
         eventPublisher.processEvents();
+        Shipment shipment = shipmentRepository.findByOrderId(orderId).orElseThrow();
+        System.out.println("배송 컨텍스트: " + shipment.getOrderId() + "번 주문, " + shipment.getAddress());
     }
 }
```
