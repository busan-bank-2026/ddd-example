# 캡처

| 파일 | 장 | task | 커밋 | 찍은 화면 | 보이는 것 |
|---|---|---|---|---|---|
| T1-주문-접수됨-이벤트.txt | order-events | T1 | 8148820 | 콘솔 실행과 domain 폴더 | 볼펜 항목 줄 다음에 주문 뒤: 이벤트 1개, 자바 책 재고 10개가 찍히고, 이어서 src/ddd/domain에 OrderPlaced가 더 보인다 |
| T2-발행기에-넘기기.txt | order-events | T2 | 388255f | 콘솔 실행과 application 폴더 | 출력은 T1과 같아 자바 책 재고는 그대로 10개이고, 이어서 src/ddd/application에 EventPublisher가 더 보인다 |
| T3-재고-맞추기.txt | order-events | T3 | 3d27ec7 | 콘솔 실행과 application 폴더 | 주문 뒤 줄 다음에 이벤트 처리 뒤: 자바 책 재고 8개가 찍히고, 이어서 src/ddd/application에 DecreaseStockHandler가 더 보인다 |
| done-주문-장면.txt | order-events | done | 3d27ec7 | 콘솔 실행 (이클립스 Run 대신) | 시작 글부터 가격 바뀐 뒤까지 스물다섯 줄이 spec 실행 결과와 같게 찍힌다 |
