# 폴더 구조

비교 기준: done/discount에 고침 patch를 붙인 상태(f9ece31). 새로 생긴 파일과 고친 파일을 표시했다.

```
ddd/
  .settings/
    org.eclipse.core.resources.prefs
    org.eclipse.jdt.core.prefs
  src/
    ddd/
      order/
        application/
          OrderItemRequest.java
          OrderRequest.java
          OrderService.java
        domain/
          Address.java
          DiscountPolicy.java
          FreeShippingAreaSpecification.java   새로
          FreeShippingPriceSpecification.java   새로
          FreeShippingSpecification.java   새로
          Member.java
          MemberGrade.java
          MemberRepository.java
          Order.java   고침
          OrderItem.java
          OrderItemFactory.java
          OrderRepository.java
          OrderSpecification.java   새로
          OrderStatus.java
          Product.java
          ProductRepository.java
        infrastructure/
          MemoryMemberRepository.java
          MemoryOrderRepository.java
          MemoryProductRepository.java
      Main.java   고침
  .classpath
  .project
  glossary.md   고침
```

