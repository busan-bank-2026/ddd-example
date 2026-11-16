# 폴더 구조

비교 기준: done/bounded-contexts. 새로 생긴 파일과 고친 파일을 표시했다.

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
          OrderOpenHostService.java   새로
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
        infrastructure/
          MemoryOrderRepository.java
      shared/
        Money.java
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
test-lib/
  junit-platform-console-standalone.jar
```

