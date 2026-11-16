# 캡처

| 파일 | 장 | task | 커밋 | 찍은 화면 | 보이는 것 |
|---|---|---|---|---|---|
| T1-나중-저장-덮어쓰기.txt | optimistic-lock | T1 | 001c774 | 콘솔 실행 | 가격 바뀐 뒤 줄 다음에 동시 수정: 취소가 찍힌다 |
| T2-주문-버전.txt | optimistic-lock | T2 | 1c6f7bb | 콘솔 실행과 glossary.md | 출력은 T1과 같아 동시 수정: 취소가 그대로 찍히고, 이어서 용어집에 버전 version 줄이 보인다 |
| T3-버전-다르면-거절.txt | optimistic-lock | T3 | 2a1cb4e | 콘솔 실행과 glossary.md | 마지막 줄이 동시 수정: 다른 곳에서 먼저 바꾼 주문입니다로 바뀌고, 이어서 용어집 저장하기 줄에 버전이 다르면 거절한다는 뜻이 보인다 |
| done-주문-장면.txt | optimistic-lock | done | 2a1cb4e | 콘솔 실행 (이클립스 Run 대신) | 시작 글부터 동시 수정 거절까지 스물여섯 줄이 spec 실행 결과와 같게 찍힌다 |
