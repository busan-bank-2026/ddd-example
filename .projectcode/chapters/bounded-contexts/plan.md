# bounded-contexts 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/member/application/MemberService.java | 새로 | 회원 조회와 목록 |
| ddd/src/ddd/catalog/application/ProductService.java | 새로 | 상품 조회와 목록, 재고 감소 |
| ddd/src/ddd/member/domain/MemberRepository.java | 고침 | 목록 조회 계약 |
| ddd/src/ddd/catalog/domain/ProductRepository.java | 고침 | 목록 조회 계약 |
| ddd/src/ddd/member/infrastructure/MemoryMemberRepository.java | 고침 | 목록 조회 구현 |
| ddd/src/ddd/catalog/infrastructure/MemoryProductRepository.java | 고침 | 목록 조회 구현 |
| ddd/src/ddd/order/domain/OrderPlaced.java | 고침 | 주문 접수 사실 |
| ddd/src/ddd/order/application/EventPublisher.java | 고침 | 구체 처리기 즉시 호출 |
| ddd/src/ddd/catalog/application/DecreaseStockHandler.java | 고침 | 상품 서비스에 재고 감소 요청 |
| ddd/src/ddd/order/application/OrderService.java | 고침 | 회원 서비스 조회와 주문 접수 발행 |
| ddd/src/ddd/Main.java | 고침 | 회원과 상품 목록, 주문 재고 변화 |
| ddd/context-map.md | 고침 | 현재 세 컨텍스트와 의존 관계 |
| ddd/glossary.md | 고침 | 서비스와 동기 이벤트 |
| ddd/where-rules-live.md | 고침 | 이번 강의 규칙 위치 |
| tests/ddd | 고침 | 1~17장 계약과 목록, 즉시 처리 검증 |

## 코드 계획
- spec의 할 일만 구현한다
- 학생 코드에 주석을 쓰지 않는다
- 기존 task 순서의 코드 차이를 따라치기에 녹인다
- 뒤 장의 구현을 앞 장에 미리 넣지 않는다

## 라이브러리
- 추가 없음
