# 폴더 구조

비교 기준: done/bounded-contexts에 고침 patch를 붙인 상태(61b08b1). 새로 생긴 파일과 고친 파일을 표시했다.

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
          MessageSender.java   새로
          OrderItemRequest.java
          OrderPaidNotifier.java   새로
          OrderRequest.java
          OrderService.java   고침
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
          OrderRepository.java
          OrderSpecification.java
          OrderStatus.java
        infrastructure/
          MemoryOrderRepository.java
      shipping/
        application/
          OrderPaidMessageHandler.java   새로
        domain/
          Shipment.java   새로
          ShipmentRepository.java   새로
        infrastructure/
          MemoryShipmentRepository.java   새로
      Main.java   고침
  .classpath
  .project
  context-map.md   고침
  glossary.md   고침
```

