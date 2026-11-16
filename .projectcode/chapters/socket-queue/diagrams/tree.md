# 폴더 구조

비교 기준: done/order-saga. 새로 생긴 파일과 고친 파일을 표시했다.

```
ddd/
  .settings/
    org.eclipse.core.resources.prefs
    org.eclipse.jdt.core.prefs
  src/
    ddd/
      catalog/
        application/
          DecreaseStockHandler.java
          IncreaseStockHandler.java
        domain/
          Product.java
          ProductRepository.java
        infrastructure/
          MemoryProductRepository.java
      member/
        domain/
          Member.java
          MemberGrade.java
          MemberRepository.java
        infrastructure/
          MemoryMemberRepository.java
      order/
        application/
          EventPublisher.java
          OrderApplicationService.java
          OrderLineCommand.java
          OrderOpenHostService.java
          OrderQueryService.java
          OrderSummary.java
          PlaceOrderCommand.java
          SocketEventPublisher.java   새로
        domain/
          Address.java
          DiscountPolicy.java
          FreeShippingAreaSpecification.java
          FreeShippingPriceSpecification.java
          FreeShippingSpecification.java
          Order.java
          OrderCanceled.java
          OrderLine.java
          OrderLineFactory.java
          OrderPlaced.java
          OrderRepository.java
          OrderSpecification.java
          OrderStatus.java
          PaymentGateway.java
          PaymentResult.java
        infrastructure/
          LegacyPaymentAdapter.java
          MemoryOrderRepository.java
      shared/
        Money.java
      shipping/
        application/
          OrderPaidMessageHandler.java
        domain/
          Shipment.java
          ShipmentRepository.java
        infrastructure/
          MemoryShipmentRepository.java
          SocketMessageReceiver.java   새로
      Main.java
      OrderApp.java   새로
      ShippingApp.java   새로
    legacypay/
      LegacyPayGateway.java
  .classpath
  .project
  context-map.md
  glossary.md
  where-rules-live.md
test-lib/
  junit-platform-console-standalone.jar
```

