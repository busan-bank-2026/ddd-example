# 캡처

| 파일 | 장 | task | 커밋 | 찍은 화면 | 보이는 것 |
|---|---|---|---|---|---|
| T1-줄어-있는-재고.txt | order-saga | T1 | 7af2fac | 콘솔 실행 | 한도 넘은 결제 줄은 주문 접수이고, 주문 목록 줄 다음에 결제 실패 뒤: 노트북 재고 9개, 주문 접수가 찍힌다 |
| T2-재고-되돌리기.txt | order-saga | T2 | 9d58b02 | 콘솔 실행과 상품 응용 폴더 | 출력은 T1과 같아 결제 실패 뒤: 노트북 재고 9개, 주문 접수가 그대로 찍히고, 이어서 catalog/application에 DecreaseStockHandler.java와 IncreaseStockHandler.java가 보인다 |
| T3-주문-취소로-보상.txt | order-saga | T3 | 0e8599e | 콘솔 실행 | 한도 넘은 결제 줄이 결제 한도를 넘었습니다, 취소로 바뀌고, 마지막 줄이 결제 실패 뒤: 노트북 재고 10개, 취소다 |
| T4-규칙-두는-곳.txt | order-saga | T4 | d5177e6 | 콘솔 실행과 where-rules-live.md, context-map.md | 출력 서른세 줄이 그대로 나오고, 이어서 where-rules-live.md의 규칙 두는 곳 표와 사가 절, context-map.md의 재고 되돌리기 처리기와 주문 조회 서비스 줄이 보인다 |
| done-주문-장면.txt | order-saga | done | d5177e6 | 콘솔 실행 (이클립스 Run 대신) | 시작 글부터 결제 실패 뒤 줄까지 서른세 줄이 spec 실행 결과와 같게 찍힌다 |
