# 폴더 구조

비교 기준: done/order-query에 고침 patch를 붙인 상태(2a42779). 새로 생긴 파일과 고친 파일을 표시했다.

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
          EventHandler.java
          EventPublisher.java
          MessageSender.java
          OrderItemRequest.java
          OrderPaidNotifier.java
          OrderQueryService.java
          OrderRequest.java
          OrderService.java   고침
          OrderSummary.java
        domain/
          Address.java
          DiscountPolicy.java
          FreeShippingAreaSpecification.java
          FreeShippingPriceSpecification.java
          FreeShippingSpecification.java
          Order.java
          OrderCanceled.java   새로
          OrderItem.java
          OrderItemFactory.java
          OrderPlaced.java
          OrderRepository.java
          OrderSpecification.java
          OrderStatus.java
          PaymentGateway.java
          PaymentResult.java
        infrastructure/
          LegacyPaymentAdapter.java
          MemoryOrderRepository.java
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
```

