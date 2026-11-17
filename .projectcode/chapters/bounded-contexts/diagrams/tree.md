# 폴더 구조

비교 기준: done/optimistic-lock에 고침 patch를 붙인 상태(eeeec9d). 새로 생긴 파일과 고친 파일을 표시했다.

```
ddd/
  .settings/
    org.eclipse.core.resources.prefs
    org.eclipse.jdt.core.prefs
  src/
    ddd/
      catalog/
        application/
          DecreaseStockHandler.java   새로
        domain/
          Product.java   새로
          ProductRepository.java   새로
        infrastructure/
          MemoryProductRepository.java   새로
      member/
        domain/
          Member.java   새로
          MemberGrade.java   새로
          MemberRepository.java   새로
        infrastructure/
          MemoryMemberRepository.java   새로
      order/
        application/
          EventHandler.java
          EventPublisher.java
          OrderItemRequest.java
          OrderRequest.java
          OrderService.java   고침
        domain/
          Address.java
          DiscountPolicy.java   고침
          FreeShippingAreaSpecification.java
          FreeShippingPriceSpecification.java
          FreeShippingSpecification.java
          Order.java
          OrderItem.java
          OrderItemFactory.java   고침
          OrderPlaced.java
          OrderRepository.java
          OrderSpecification.java
          OrderStatus.java
        infrastructure/
          MemoryOrderRepository.java
      Main.java   고침
  .classpath
  .project
  context-map.md   새로
  glossary.md
```

