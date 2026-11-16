# 폴더 구조

비교 기준: done/order-query. 새로 생긴 파일과 고친 파일을 표시했다.

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
          IncreaseStockHandler.java   새로
        domain/
          Product.java   고침
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
          OrderApplicationService.java   고침
          OrderLineCommand.java
          OrderOpenHostService.java
          OrderQueryService.java
          OrderSummary.java
          PlaceOrderCommand.java
        domain/
          Address.java
          DiscountPolicy.java
          FreeShippingAreaSpecification.java
          FreeShippingPriceSpecification.java
          FreeShippingSpecification.java
          Order.java
          OrderCanceled.java   새로
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
      Main.java   고침
    legacypay/
      LegacyPayGateway.java
  .classpath
  .project
  context-map.md   고침
  glossary.md   고침
  where-rules-live.md   새로
test-lib/
  junit-platform-console-standalone.jar
```

