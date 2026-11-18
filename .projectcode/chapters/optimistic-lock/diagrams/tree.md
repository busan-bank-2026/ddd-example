# 폴더 구조

비교 기준: done/order-events의 수정을 반영한 앞 장 끝. 앞 장 수정 반영 시점: 428e684c8308cef04df19be3c8e37a979084f5df. 새로 생긴 파일과 고친 파일을 표시했다.

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
          FreeShippingSpecification.java
          Member.java
          MemberGrade.java
          MemberRepository.java
          Order.java   고침
          OrderItem.java
          OrderItemFactory.java
          OrderRepository.java
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

