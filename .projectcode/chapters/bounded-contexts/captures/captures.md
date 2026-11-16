# 캡처

| 파일 | 장 | task | 커밋 | 찍은 화면 | 보이는 것 |
|---|---|---|---|---|---|
| T1-하위-도메인.txt | bounded-contexts | T1 | 8a46784 | 콘솔 실행과 context-map.md | 출력 스물여섯 줄이 그대로 나오고, 이어서 context-map.md에 주문 핵심, 상품 지원, 회원 일반 하위 도메인과 컨텍스트 묶음 표가 보인다 |
| T2-공유-커널.txt | bounded-contexts | T2 | c6354db | 콘솔 실행과 src 폴더 | 출력은 그대로이고, 이어서 src/ddd에 shared 폴더가 생기고 src/ddd/shared에 Money.java가 보인다 |
| T3-회원-컨텍스트.txt | bounded-contexts | T3 | e19673d | 콘솔 실행과 src 폴더 | 출력은 그대로이고, 이어서 src/ddd에 member 폴더가 생기고 그 아래 domain에 세 파일, infrastructure에 MemoryMemberRepository.java가 보인다 |
| T4-상품-컨텍스트.txt | bounded-contexts | T4 | 27bbabf | 콘솔 실행과 src 폴더 | 출력은 그대로이고, 이어서 src/ddd에 catalog 폴더가 생기고 그 아래 domain, application, infrastructure에 네 파일이 보인다 |
| T5-주문-컨텍스트.txt | bounded-contexts | T5 | ced0444 | 콘솔 실행과 src 폴더와 context-map.md | 출력은 그대로이고, 이어서 src/ddd에는 Main.java와 catalog, member, order, shared만 남고, context-map.md에 컨텍스트 사이 표가 보인다 |
| done-주문-장면.txt | bounded-contexts | done | ced0444 | 콘솔 실행 (이클립스 Run 대신) | 시작 글부터 동시 수정 거절까지 스물여섯 줄이 spec 실행 결과와 같게 찍힌다 |
