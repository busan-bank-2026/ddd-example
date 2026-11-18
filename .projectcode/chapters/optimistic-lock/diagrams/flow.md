# optimistic-lock 흐름

```mermaid
flowchart LR
  A[같은 주문 두 번 조회] --> B[첫째 배송 후 저장]
  B --> C[저장된 버전 증가]
  C --> D[둘째 취소 후 저장]
  D --> E{버전이 같은가}
  E -->|다름| F[저장 거절]
```
