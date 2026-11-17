# 캡처

| 파일 | 장 | task | 커밋 | 찍은 화면 | 보이는 것 |
|---|---|---|---|---|---|
| T1-회원과-상품-저장소.txt | id-reference | T1 | f0cce9d | 콘솔 실행과 src 폴더 | ===== 회원과 상품 ===== 아래 [회원과 상품 저장하기]에 1번 회원: 김철수와 1번 상품: 자바 책 15000원이 찍히고, 이어서 src/ddd/order/domain에 Member, MemberRepository, Product, ProductRepository가, src/ddd/order/infrastructure에 MemoryMemberRepository, MemoryProductRepository가 더 보인다 |
| T2-번호로-가리키기.txt | id-reference | T2 | 017c81a | 콘솔 실행 | [회원 번호와 상품 번호로 주문하기]에 주문: 1번 주문, 1번 회원 김철수, 31000원과 상품 번호가 붙은 주문 항목 두 줄이 찍힌다. 김철수는 주문의 회원 번호 1로 회원 저장소에서 찾은 이름이다 |
| T3-가격-바뀐-뒤.txt | id-reference | T3 | 311d2b7 | 콘솔 실행 | [상품 가격이 바뀐 뒤 주문 보기]에 자바 책 상품 가격: 15000원 -> 18000원, 자바 책 주문 항목 가격: 15000원 -> 15000원, 주문 금액: 31000원 -> 31000원이 찍힌다 |
| done-주문-장면.txt | id-reference | done | 311d2b7 | 콘솔 실행 (이클립스 Run 대신) | ===== 회원과 상품 =====부터 주문 금액 줄까지 spec 실행 결과와 같게 찍힌다 |
