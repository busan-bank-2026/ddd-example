# 캡처

| 파일 | 장 | task | 커밋 | 찍은 화면 | 보이는 것 |
|---|---|---|---|---|---|
| T1-같은 주문을 두 번 꺼내 덮어쓰기.txt | optimistic-lock | T1 | c82bd618cb29c0fb7d488b64a27137d41ef8f0dd | 콘솔 실행 | 같은 주문을 두 번 꺼내 덮어쓰기. 둘째로 꺼내 취소: 배송 중 -> 취소 |
| T2-저장할 때마다 주문 버전 올리기.txt | optimistic-lock | T2 | 57f682b71ae32c02768851cdc835e51ff8c5d959 | 콘솔 실행 | 저장할 때마다 주문 버전 올리기. 결제하고 저장: 버전 1 -> 2 |
| T3-버전이 다르면 저장 거절하기.txt | optimistic-lock | T3 | 0afaadab4cc0d32f6949d0cffefe8d60fc3cdb84 | 콘솔 실행 | 버전이 다르면 저장 거절하기. 결제하고 저장: 버전 1 -> 2 |
| done-주문-장면.txt | optimistic-lock | done | 0afaadab4cc0d32f6949d0cffefe8d60fc3cdb84 | 장 끝 콘솔 실행 | 장 끝 실행 결과. 결제하고 저장: 버전 1 -> 2 |
