# order-events 흐름

```mermaid
flowchart LR
  A["주문하기 (placeOrder)"] --> B[**주문 접수됨 남김**]
  B --> C[주문 저장]
  C --> D["발행기에 쌓기 (publish)"]
  D -.-> E["나중에 처리 (processEvents)"]
  E --> F[재고 줄이기 처리기]
  F --> G[재고 줄이고 저장]
```

```mermaid
flowchart LR
  S[OrderApplicationService] --> O[Order]
  O --> P[**OrderPlaced**]
  S --> E[EventPublisher]
  E --> H[DecreaseStockHandler]
  H --> R[Product]
```

위 그림은 주문 하나가 재고까지 닿는 흐름이다. 주문 응용 서비스가 주문을 만들면 주문은 주문 접수됨 이벤트를 남긴다. 주문 응용 서비스는 주문을 저장하고, 주문이 남긴 이벤트를 발행기에 넘긴 뒤 끝난다. 여기까지가 한 번의 일이라 재고는 아직 그대로다. 점선 뒤는 다른 일이다. 발행기가 쌓인 이벤트를 처리하면 재고 줄이기 처리기가 주문 항목마다 상품 재고를 줄이고 저장해서 재고가 나중에 맞춰진다. 아래 그림은 누가 누구를 쓰는지다. 주문은 상품을 모르고 이벤트만 남긴다. 상품을 바꾸는 것은 이벤트를 구독한 재고 줄이기 처리기다.
