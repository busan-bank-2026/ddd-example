# 폴더 구조

비교 기준: done/order-events에 고침 patch를 붙인 상태(5f7a0a3). 새로 생긴 파일과 고친 파일을 표시했다.

```
ddd/
  .settings/
    org.eclipse.core.resources.prefs
    org.eclipse.jdt.core.prefs
  src/
    ddd/
      order/
        application/
          DecreaseStockHandler.java
          EventHandler.java
          EventPublisher.java
          OrderItemRequest.java
          OrderRequest.java
          OrderService.java
        domain/
          Address.java
          DiscountPolicy.java
          FreeShippingAreaSpecification.java
          FreeShippingPriceSpecification.java
          FreeShippingSpecification.java
          Member.java
          MemberGrade.java
          MemberRepository.java
          Order.java   고침
          OrderItem.java
          OrderItemFactory.java
          OrderPlaced.java
          OrderRepository.java
          OrderSpecification.java
          OrderStatus.java
          Product.java
          ProductRepository.java
        infrastructure/
          MemoryMemberRepository.java
          MemoryOrderRepository.java   고침
          MemoryProductRepository.java
      Main.java   고침
  .classpath
  .project
  glossary.md   고침
```

