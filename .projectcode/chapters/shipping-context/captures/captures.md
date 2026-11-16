# 캡처

| 파일 | 장 | task | 커밋 | 찍은 화면 | 보이는 것 |
|---|---|---|---|---|---|
| T1-결제-완료-메시지.txt | shipping-context | T1 | a207b1f | 콘솔 실행 | 동시 수정 줄 다음에 결제 완료 메시지 두 줄이 찍힌다. ORDER_PAID 뒤에 1번 주문 서울시 강남구, 4번 주문 광주시 북구가 붙는다 |
| T2-배송-컨텍스트.txt | shipping-context | T2 | 285dbb7 | 콘솔 실행과 src 폴더 | 결제 완료 메시지 두 줄 다음에 배송 컨텍스트: 1번 주문, 서울시 강남구가 찍히고, 이어서 src/ddd에 shipping 폴더와 그 아래 application, domain, infrastructure가 보인다 |
| done-주문-장면.txt | shipping-context | done | 285dbb7 | 콘솔 실행 (이클립스 Run 대신) | 시작 글부터 배송 컨텍스트 줄까지 스물아홉 줄이 spec 실행 결과와 같게 찍힌다 |
