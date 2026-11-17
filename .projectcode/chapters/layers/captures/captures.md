# 캡처

| 파일 | 장 | task | 커밋 | 찍은 화면 | 보이는 것 |
|---|---|---|---|---|---|
| T1-도메인-계층.txt | layers | T1 | 9c1fda8 | 콘솔 실행과 src 폴더 | ===== 계층 나누기 ===== 아래에 주문: 1번 주문, 31000원, 주문 접수와 주문 상태: 주문 접수 -> 결제 완료가 찍히고, 이어서 src/ddd에 order 폴더가 생기고 src/ddd/order/domain에 다섯 파일이 보인다 |
| T2-응용-계층.txt | layers | T2 | d41eeae | 콘솔 실행과 src 폴더 | 출력은 그대로이고, 이어서 src/ddd/order에 application 폴더가 생기고 src/ddd/order/application에 세 파일이 보인다 |
| T3-인프라-계층.txt | layers | T3 | 9b15f03 | 콘솔 실행과 src 폴더 | 출력은 그대로이고, 이어서 src/ddd에는 Main.java와 order만, src/ddd/order에는 application, domain, infrastructure만 남고 src/ddd/order/infrastructure에 MemoryOrderRepository.java가 보인다 |
| done-주문-장면.txt | layers | done | 9b15f03 | 콘솔 실행 (이클립스 Run 대신) | ===== 계층 나누기 =====부터 주문 상태 줄까지 spec 실행 결과와 같게 찍힌다 |
