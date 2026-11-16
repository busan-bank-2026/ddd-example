# 폴더 구조

비교 기준: done/optimistic-lock. 새로 생긴 파일과 고친 파일을 표시했다.

```
ddd/
  .settings/
    org.eclipse.core.resources.prefs
    org.eclipse.jdt.core.prefs
  src/
    ddd/
      catalog/
        application/
          DecreaseStockHandler.java   새로
        domain/
          Product.java   새로
          ProductRepository.java   새로
        infrastructure/
          MemoryProductRepository.java   새로
      member/
        domain/
          Member.java   새로
          MemberGrade.java   새로
          MemberRepository.java   새로
        infrastructure/
          MemoryMemberRepository.java   새로
      order/
        application/
          EventPublisher.java   새로
          OrderApplicationService.java   새로
          OrderLineCommand.java   새로
          PlaceOrderCommand.java   새로
        domain/
          Address.java   새로
          DiscountPolicy.java   새로
          FreeShippingAreaSpecification.java   새로
          FreeShippingPriceSpecification.java   새로
          FreeShippingSpecification.java   새로
          Order.java   새로
          OrderLine.java   새로
          OrderLineFactory.java   새로
          OrderPlaced.java   새로
          OrderRepository.java   새로
          OrderSpecification.java   새로
          OrderStatus.java   새로
        infrastructure/
          MemoryOrderRepository.java   새로
      shared/
        Money.java   새로
      Main.java   고침
  .classpath
  .project
  context-map.md   새로
  glossary.md
test-lib/
  junit-platform-console-standalone.jar
```

옮긴 파일도 새로로 표시된다. 스물여섯 파일은 src/ddd의 domain, application, infrastructure에서 옮겨 왔고 package와 import 줄만 바뀌었다.
