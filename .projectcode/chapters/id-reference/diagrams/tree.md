# 폴더 구조

비교 기준: done/layers. 새로 생긴 파일과 고친 파일을 표시했다.

```
ddd/
  .settings/
    org.eclipse.core.resources.prefs
    org.eclipse.jdt.core.prefs
  src/
    ddd/
      application/
        OrderApplicationService.java   고침
        OrderLineCommand.java   고침
        PlaceOrderCommand.java   고침
      domain/
        Address.java
        Member.java   새로
        MemberRepository.java   새로
        Money.java
        Order.java   고침
        OrderLine.java   고침
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
test-lib/
  junit-platform-console-standalone.jar
```

