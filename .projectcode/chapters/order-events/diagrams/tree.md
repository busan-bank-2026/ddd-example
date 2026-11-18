# 폴더 구조

비교 기준: done/free-shipping의 수정을 반영한 앞 장 끝. 앞 장 수정 반영 시점: 7f4dbaf6b65b21523e9db3c1598ff58abba1430d. 새로 생긴 파일과 고친 파일을 표시했다.

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
          OrderService.java   고침
        domain/
          Address.java
          DiscountPolicy.java
          FreeShippingSpecification.java
          Member.java
          MemberGrade.java
          MemberRepository.java
          Order.java
          OrderItem.java
          OrderItemFactory.java
          OrderRepository.java
          OrderStatus.java
          Product.java   고침
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

