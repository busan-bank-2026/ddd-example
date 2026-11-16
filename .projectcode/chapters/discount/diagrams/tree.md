# 폴더 구조

비교 기준: done/order-factory. 새로 생긴 파일과 고친 파일을 표시했다.

```
ddd/
  .settings/
    org.eclipse.core.resources.prefs
    org.eclipse.jdt.core.prefs
  src/
    ddd/
      application/
        OrderApplicationService.java   고침
        OrderLineCommand.java
        PlaceOrderCommand.java
      domain/
        Address.java
        DiscountPolicy.java   새로
        Member.java   고침
        MemberGrade.java   새로
        MemberRepository.java
        Money.java   고침
        Order.java   고침
        OrderLine.java
        OrderLineFactory.java
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

