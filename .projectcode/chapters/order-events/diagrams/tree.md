# 폴더 구조

비교 기준: done/free-shipping. 새로 생긴 파일과 고친 파일을 표시했다.

```
ddd/
  .settings/
    org.eclipse.core.resources.prefs
    org.eclipse.jdt.core.prefs
  src/
    ddd/
      application/
        DecreaseStockHandler.java   새로
        EventPublisher.java   새로
        OrderApplicationService.java   고침
        OrderLineCommand.java
        PlaceOrderCommand.java
      domain/
        Address.java
        DiscountPolicy.java
        FreeShippingAreaSpecification.java
        FreeShippingPriceSpecification.java
        FreeShippingSpecification.java
        Member.java
        MemberGrade.java
        MemberRepository.java
        Money.java
        Order.java   고침
        OrderLine.java
        OrderLineFactory.java
        OrderPlaced.java   새로
        OrderRepository.java
        OrderSpecification.java
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
test-lib/
  junit-platform-console-standalone.jar
```

