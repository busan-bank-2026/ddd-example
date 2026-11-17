# 폴더 구조

비교 기준: done/id-reference에 고침 patch를 붙인 상태(311d2b7). 새로 생긴 파일과 고친 파일을 표시했다.

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
          OrderRequest.java
          OrderService.java   고침
        domain/
          Address.java
          Member.java
          MemberRepository.java
          Order.java
          OrderItem.java
          OrderItemFactory.java   새로
          OrderRepository.java
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

