# 폴더 구조

비교 기준: done/order-events. 새로 생긴 파일과 고친 파일을 표시했다.

```
ddd/
  .settings/
    org.eclipse.core.resources.prefs
    org.eclipse.jdt.core.prefs
  src/
    ddd/
      application/
        DecreaseStockHandler.java
        EventPublisher.java
        OrderApplicationService.java
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
        OrderPlaced.java
        OrderRepository.java
        OrderSpecification.java
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
test-lib/
  junit-platform-console-standalone.jar
```

