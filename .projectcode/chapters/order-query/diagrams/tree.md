# 폴더 구조

비교 기준: done/payment-acl에 고침 patch를 붙인 상태(d194ff1). 새로 생긴 파일과 고친 파일을 표시했다.

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
          EventHandler.java
          EventPublisher.java
          MessageSender.java
          OrderItemRequest.java
          OrderPaidNotifier.java
          OrderQueryService.java   새로
          OrderRequest.java
          OrderService.java
          OrderSummary.java   새로
        domain/
          Address.java
          DiscountPolicy.java
          FreeShippingAreaSpecification.java
          FreeShippingPriceSpecification.java
          FreeShippingSpecification.java
          Order.java
          OrderItem.java
          OrderItemFactory.java
          OrderPlaced.java
          OrderRepository.java   고침
          OrderSpecification.java
          OrderStatus.java
          PaymentGateway.java
          PaymentResult.java
        infrastructure/
          LegacyPaymentAdapter.java
          MemoryOrderRepository.java   고침
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
  context-map.md
  glossary.md   고침
```

