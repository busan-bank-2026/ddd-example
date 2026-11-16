# 폴더 구조

비교 기준: done/id-reference. 새로 생긴 파일과 고친 파일을 표시했다.

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
        PlaceOrderCommand.java
      domain/
        Address.java
        Member.java
        MemberRepository.java
        Money.java
        Order.java
        OrderLine.java
        OrderLineFactory.java   새로
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
test-lib/
  junit-platform-console-standalone.jar
```

