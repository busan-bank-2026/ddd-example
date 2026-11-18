# free-shipping 흐름

```mermaid
flowchart LR
  A[배송비 묻기] --> B[무료 배송 명세]
  B --> C{5만원 이상이고 제주 밖}
  C -->|참| D[배송비 0원]
  C -->|거짓| E[배송비 3000원]
```
