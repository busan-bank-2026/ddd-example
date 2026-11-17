# 폴더 구조

비교 기준: done/layers에 고침 patch를 붙인 상태(9b15f03). 새로 생긴 파일과 고친 파일을 표시했다.

```
ddd/
  .settings/
    org.eclipse.core.resources.prefs
    org.eclipse.jdt.core.prefs
  src/
    ddd/
      order/
        application/
          OrderItemRequest.java   고침
          OrderRequest.java   고침
          OrderService.java   고침
        domain/
          Address.java
          Member.java   새로
          MemberRepository.java   새로
          Order.java   고침
          OrderItem.java   고침
          OrderRepository.java
          OrderStatus.java
          Product.java   새로
          ProductRepository.java   새로
        infrastructure/
          MemoryMemberRepository.java   새로
          MemoryOrderRepository.java
          MemoryProductRepository.java   새로
      Main.java   고침
  .classpath
  .project
  glossary.md   고침
```

