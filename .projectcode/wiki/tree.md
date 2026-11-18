# 폴더 구조

비교 기준: done/optimistic-lock의 수정을 반영한 앞 장 끝. 앞 장 수정 반영 시점: 0afaadab4cc0d32f6949d0cffefe8d60fc3cdb84. 새로 생긴 파일과 고친 파일을 표시했다.

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
          ProductService.java   새로
        domain/
          Product.java   새로
          ProductRepository.java   새로
        infrastructure/
          MemoryProductRepository.java   새로
      member/
        application/
          MemberService.java   새로
        domain/
          Member.java   새로
          MemberGrade.java   새로
          MemberRepository.java   새로
        infrastructure/
          MemoryMemberRepository.java   새로
      order/
        application/
          EventPublisher.java   새로
          OrderItemRequest.java
          OrderRequest.java
          OrderService.java   고침
        domain/
          Address.java
          DiscountPolicy.java   고침
          FreeShippingSpecification.java
          Order.java
          OrderItem.java
          OrderItemFactory.java   고침
          OrderPlaced.java   새로
          OrderRepository.java
          OrderStatus.java
        infrastructure/
          MemoryOrderRepository.java   고침
      Main.java   고침
  .classpath
  .project
  context-map.md   새로
  glossary.md   고침
  where-rules-live.md   새로
```

