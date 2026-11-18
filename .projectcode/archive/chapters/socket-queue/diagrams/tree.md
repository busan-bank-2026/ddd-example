# 폴더 구조

비교 기준: done/order-saga에 고침 patch를 붙인 상태(a9d5010). 새로 생긴 파일과 고친 파일을 표시했다.

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
          EventHandler.java
          EventPublisher.java
          MessageSender.java
          OrderItemRequest.java
          OrderPaidNotifier.java
          OrderQueryService.java
          OrderRequest.java
          OrderService.java
          OrderSummary.java
        domain/
          Address.java
          DiscountPolicy.java
          FreeShippingAreaSpecification.java
          FreeShippingPriceSpecification.java
          FreeShippingSpecification.java
          Order.java
          OrderCanceled.java
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
          SocketMessageSender.java   새로
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
```

