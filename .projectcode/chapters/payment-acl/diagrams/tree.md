# 폴더 구조

비교 기준: done/shipping-context. 새로 생긴 파일과 고친 파일을 표시했다.

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
          OrderApplicationService.java   고침
          OrderLineCommand.java
          OrderOpenHostService.java
          PlaceOrderCommand.java
        domain/
          Address.java
          DiscountPolicy.java
          FreeShippingAreaSpecification.java
          FreeShippingPriceSpecification.java
          FreeShippingSpecification.java
          Order.java
          OrderLine.java
          OrderLineFactory.java
          OrderPlaced.java
          OrderRepository.java
          OrderSpecification.java
          OrderStatus.java
          PaymentGateway.java   새로
          PaymentResult.java   새로
        infrastructure/
          LegacyPaymentAdapter.java   새로
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
      LegacyPayGateway.java   새로
  .classpath
  .project
  context-map.md   고침
  glossary.md   고침
test-lib/
  junit-platform-console-standalone.jar
```

