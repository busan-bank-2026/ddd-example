# 폴더 구조

비교 기준: done/place-order. 새로 생긴 파일과 고친 파일을 표시했다.

```
ddd/
  .settings/
    org.eclipse.core.resources.prefs
    org.eclipse.jdt.core.prefs
  src/
    ddd/
      application/
        OrderApplicationService.java   새로
        OrderLineCommand.java   새로
        PlaceOrderCommand.java   새로
      domain/
        Address.java   새로
        Money.java   새로
        Order.java   새로
        OrderLine.java   새로
        OrderRepository.java   새로
        OrderStatus.java   새로
      infrastructure/
        MemoryOrderRepository.java   새로
      Main.java   고침
  .classpath
  .project
  glossary.md
test-lib/
  junit-platform-console-standalone.jar
```

옮긴 파일도 새로로 표시된다. 열 파일은 src/ddd에서 옮겨 왔고 package와 import 줄만 바뀌었다.
