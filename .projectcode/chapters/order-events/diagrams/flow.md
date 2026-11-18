# order-events 흐름

```mermaid
flowchart LR
  A[주문 요청] --> B[주문 항목 만들기]
  B --> C[주문 저장]
  C --> D[상품 찾기]
  D --> E[재고 감소]
  E --> F[상품 저장]
  F --> G[주문 번호 반환]
```
