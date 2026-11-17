# 폴더 구조

비교 기준: done/place-order에 고침 patch를 붙인 상태(da10a07). 새로 생긴 파일과 고친 파일을 표시했다.

```
ddd/
  .settings/
    org.eclipse.core.resources.prefs
    org.eclipse.jdt.core.prefs
  src/
    ddd/
      order/
        application/
          OrderItemRequest.java   새로
          OrderRequest.java   새로
          OrderService.java   새로
        domain/
          Address.java   새로
          Order.java   새로
          OrderItem.java   새로
          OrderRepository.java   새로
          OrderStatus.java   새로
        infrastructure/
          MemoryOrderRepository.java   새로
      Main.java   고침
  .classpath
  .project
  glossary.md
```


옮긴 파일도 새로로 표시된다. 아홉 파일은 src/ddd에서 옮겨 왔고 package와 import 줄만 바뀌었다.
