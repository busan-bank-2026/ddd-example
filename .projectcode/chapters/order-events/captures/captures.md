# 캡처

| 파일 | 장 | task | 커밋 | 찍은 화면 | 보이는 것 |
|---|---|---|---|---|---|
| T1-주문-접수됨-이벤트.txt | order-events | T1 | 394ed04 | 콘솔 실행과 domain 폴더 | ===== 주문 뒤 재고 줄이기 ===== 아래 [주문이 주문 접수됨 이벤트 남기기]에 자바 책 2개 주문: 1번 주문, 이벤트 1개와 자바 책 재고: 10개 -> 10개가 찍히고, 이어서 src/ddd/order/domain에 OrderPlaced가 더 보인다 |
| T2-발행기에-넘기기.txt | order-events | T2 | c04b065 | 콘솔 실행과 application 폴더 | 출력은 T1과 같아 자바 책 재고는 그대로 10개이고, 이어서 src/ddd/order/application에 EventHandler와 EventPublisher가 더 보인다 |
| T3-재고-맞추기.txt | order-events | T3 | 5f7a0a3 | 콘솔 실행과 application 폴더 | [이벤트를 처리해 재고 줄이기]에 자바 책 재고: 10개 -> 8개가 찍히고, 이어서 src/ddd/order/application에 DecreaseStockHandler가 더 보인다 |
| done-주문-장면.txt | order-events | done | 5f7a0a3 | 콘솔 실행 (이클립스 Run 대신) | ===== 주문 뒤 재고 줄이기 =====부터 자바 책 재고: 10개 -> 8개 줄까지 spec 실행 결과와 같게 찍힌다 |
