from pathlib import Path
import re

root = Path(__file__).resolve().parent.parent
p = root / '.projectcode'
def write(path, text):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(text.strip() + '\n', encoding='utf-8')

road = (p/'roadmap.md').read_text(encoding='utf-8')
road = '\n'.join(line for line in road.splitlines() if not re.match(r'^\| (18|19|20|21|22) \|', line))
road = road.replace(', 부패 방지 계층', '').replace('계층 구조, 의존성 역전, 트랜잭션 경계, 최종 일관성, CQRS, 사가', '계층 구조, 의존성 역전, 낙관적 잠금, 동기 이벤트 처리').replace('이벤트, 사가, 응용 서비스', '이벤트, 응용 서비스')
road = road.replace('도메인 이벤트, 트랜잭션 경계, 최종 일관성 | | 따라치기, 발행기는 붙여넣기', '재고 변경, 응용 서비스의 직접 호출 | | 따라치기')
road = road.replace('하위 도메인, 바운디드 컨텍스트, 모듈 |', '하위 도메인, 바운디드 컨텍스트, 응용 서비스, 동기 도메인 이벤트 |')
road = road.replace('- 배송 컨텍스트가 배송 상태를 맡고 주문 상태와 맞추기\n', '').replace('- 같은 메시지를 두 번 받을 때 배송을 한 번만 만들기\n', '')
road = road.split('## 하지 않는 것')[0] + '''## 하지 않는 것

- 헥사고날 아키텍처
- Spring, JPA 같은 프레임워크
- 실제 DB와 메시지 큐, 분산 시스템
- 배송 컨텍스트와 문자열 메시지 교환
- 외부 결제 연결, CQRS, 사가, 소켓 통신
- 이벤트 대기 목록과 나중에 따로 실행하는 처리 명령
- 이벤트 소싱
- 쿠폰, 장바구니, 알림, 반품과 환불
- 콘솔 입력 메뉴. Main은 코드에 적은 장면을 실행해 출력한다
- 회원 등급으로 무료 배송 정하기
- 상품과 회원의 버전, 비관적 잠금, 낙관적 잠금 실패 뒤 자동 재시도
- 돈 값 객체와 공유 커널. 금액은 int로 둔다
- 자바 모듈로 컨텍스트 사이 접근 막기
- 용어집을 컨텍스트마다 나누기
- 주문 취소 때 재고 복원. 이번 강의는 주문 접수 뒤 재고 감소만 다룬다

## 따라치기 제목

- free-shipping T2: 무료 배송 명세에 제주 제외 조건 더하기
- order-events T1: 상품에 재고와 재고 감소 메서드 더하기
- order-events T2: 주문 저장 뒤 상품 재고 바로 줄이기
- bounded-contexts T7: 회원과 상품 서비스 만들기
- bounded-contexts T8: 주문 접수 이벤트로 재고 감소 연결하기
'''
for chapter in ('free-shipping','order-events','optimistic-lock','bounded-contexts'):
    road = re.sub(r'(\| '+chapter+r' \|[^\n]*\| )완료( \|)', r'\1수정 중\2', road)
write(p/'roadmap.md', road)

contract = (p/'contract.md').read_text(encoding='utf-8')
contract = '\n'.join(line for line in contract.splitlines() if not any(s in line for s in ('| 메시지 형식 |','| 소켓 포트 |','- 22장','| run 소켓 |')))
contract += '\n- 이벤트: 17장에서 OrderPlaced 하나를 같은 스레드에서 즉시 처리한다. 별도 서버와 포트가 없다\n'
write(p/'contract.md', contract)

write(p/'chapters/free-shipping/spec.md', '''# free-shipping 무료 배송 조건

종류: 기능
배울 것: 명세

## 할 일
- S1 무료 배송 명세는 주문을 받아 결제 금액이 50000원 이상인지 참 거짓으로 답한다. 주문을 바꾸지 않는다
- S2 주문은 무료 배송 명세가 참이면 배송비 0원, 거짓이면 3000원을 돌려준다
- S3 같은 명세의 조건에 제주 주소 제외를 더한다. 금액 조건과 지역 조건을 &&로 함께 검사한다
- S4 용어집에 무료 배송 명세와 배송비를 적는다

## 실행 결과
===== 무료 배송 조건 =====

[결제 금액으로 배송비 정하기]
30000원 주문 배송비: 3000원
60000원 주문 배송비: 0원

[제주 주소는 무료 배송에서 빼기]
서울시 마포구 60000원 주문 배송비: 0원
제주시 연동 60000원 주문 배송비: 3000원

## 흐름
배송비 묻기 -> 무료 배송 명세가 금액과 주소 확인 -> 참이면 0원, 거짓이면 3000원

## 그대로인 것
- 13장 할인 정책은 할인 금액을 계산한다. 14장 명세는 조건의 참 거짓을 판단한다
- 무료 배송 기준은 할인 뒤 결제 금액이다

## 안 하는 것
- 조건별 클래스와 명세 조합 인터페이스
- 배송비를 결제 금액에 더하기
''')

write(p/'chapters/order-events/spec.md', '''# order-events 주문 뒤 재고 줄이기

종류: 기능
배울 것: 상품 재고, 응용 서비스의 직접 호출

## 할 일
- S1 상품은 재고를 가지고 수량만큼 재고를 줄인다
- S2 주문 응용 서비스는 주문을 저장한 뒤 각 상품을 찾아 수량만큼 재고를 줄이고 저장한다
- S3 Main은 재고 10개인 자바 책을 2개 주문한 뒤 재고가 8개인지 보여준다
- S4 용어집에 재고와 재고 감소를 적는다

## 흐름
주문하기 -> 주문 저장 -> 상품 조회 -> 상품 재고 감소 -> 상품 저장 -> 주문 번호 반환

## 그대로인 것
- 상품의 재고 규칙은 Product.decreaseStock에 둔다
- 응용 서비스는 상품을 찾아 규칙을 실행하고 저장한다

## 안 하는 것
- 이벤트, 발행기, 처리기. 17장에서 직접 호출과 비교하며 도입한다
- 재고 부족 거절과 주문 취소 때 재고 복원
- 주문과 상품 저장의 원자성을 보장하는 실제 DB 트랜잭션
''')

write(p/'chapters/bounded-contexts/spec.md', '''# bounded-contexts 컨텍스트 나누기

종류: 리팩터
배울 것: 하위 도메인, 바운디드 컨텍스트, 응용 서비스, 동기 도메인 이벤트

## 할 일
- S1 주문, 상품, 회원의 업무와 모델이 책임지는 범위를 컨텍스트 맵에 적는다
- S2 회원 모델과 저장소는 ddd.member, 상품 모델과 저장소는 ddd.catalog로 옮긴다. 주문은 ddd.order에 둔다
- S3 MemberService는 회원 조회와 회원 목록 조회를 맡는다
- S4 ProductService는 상품 조회, 상품 목록 조회, 상품을 찾아 재고를 줄이고 저장하는 일을 맡는다
- S5 먼저 주문 서비스가 상품 서비스를 직접 호출해 재고를 줄인다
- S6 주문 저장 뒤 OrderPlaced 이벤트를 발행하도록 바꾼다. EventPublisher는 DecreaseStockHandler 하나를 즉시 호출하고, 처리기는 ProductService에 재고 감소를 요청한다
- S7 Main은 회원 목록, 상품 목록과 주문 뒤 재고 변화를 보여준다. 이벤트 처리까지 끝난 뒤 주문 번호를 반환한다
- S8 컨텍스트 맵과 용어집에 서비스와 이벤트의 의존 관계를 적는다

## 흐름
주문 저장 -> OrderPlaced 발행 -> 재고 감소 처리기 -> 상품 서비스 -> 상품 재고 감소와 저장 -> 주문 번호 반환

## 그대로인 것
- 무료 배송 명세는 하나다
- 낙관적 잠금은 주문 저장소에서 유지한다
- 회원, 상품, 주문은 같은 프로그램에서 동작한다

## 안 하는 것
- 이벤트 종류를 여러 개 만들기, 공통 이벤트 인터페이스, Object와 instanceof 분기
- 대기 목록, processEvents, 스레드, 문자열 메시지, 소켓
- 이벤트 처리 실패의 재시도와 보상
- 동기 호출만으로 주문 저장과 상품 저장이 원자적으로 묶인다고 설명하기

## 선택 이유
- 15장은 주문 응용 서비스가 재고 감소까지 직접 지시한다
- 17장은 주문 접수 사실에 대한 반응을 상품 쪽 처리기로 옮긴다
- 이벤트는 DDD의 필수 조건이 아니다. 후속 반응을 분리하려고 선택한다
- publish는 핸들러를 바로 호출한다. 핸들러의 예외는 호출자에게 전달된다
- 이 발행기는 구체적인 처리기 하나를 안다. 여러 구독자를 위한 범용 구조는 만들지 않는다
''')

plans = {
'free-shipping': [('ddd/src/ddd/order/domain/FreeShippingSpecification.java','고침','금액과 주소를 한 메서드에서 검사'),('ddd/src/ddd/order/domain/OrderSpecification.java','지움','조합 인터페이스 제거'),('ddd/src/ddd/order/domain/FreeShippingPriceSpecification.java','지움','금액 조건을 무료 배송 명세에 합침'),('ddd/src/ddd/order/domain/FreeShippingAreaSpecification.java','지움','지역 조건을 무료 배송 명세에 합침'),('ddd/glossary.md','고침','무료 배송 용어 정리'),('tests/ddd/FreeShippingTest.java','고침','금액 경계, 할인 뒤 금액, 제주 주소 검증')],
'order-events': [('ddd/src/ddd/order/domain/Product.java','고침','재고와 재고 감소'),('ddd/src/ddd/order/application/OrderService.java','고침','주문 저장 뒤 상품 재고 직접 감소'),('ddd/src/ddd/order/domain/Order.java','고침','이벤트 누적 제거'),('ddd/src/ddd/Main.java','고침','주문 뒤 재고 출력'),('ddd/glossary.md','고침','재고 용어'),('tests/ddd/OrderEventsTest.java','고침','주문 뒤 재고 검증')],
'bounded-contexts': [('ddd/src/ddd/member/application/MemberService.java','새로','회원 조회와 목록'),('ddd/src/ddd/catalog/application/ProductService.java','새로','상품 조회와 목록, 재고 감소'),('ddd/src/ddd/member/domain/MemberRepository.java','고침','목록 조회 계약'),('ddd/src/ddd/catalog/domain/ProductRepository.java','고침','목록 조회 계약'),('ddd/src/ddd/member/infrastructure/MemoryMemberRepository.java','고침','목록 조회 구현'),('ddd/src/ddd/catalog/infrastructure/MemoryProductRepository.java','고침','목록 조회 구현'),('ddd/src/ddd/order/domain/OrderPlaced.java','고침','주문 접수 사실'),('ddd/src/ddd/order/application/EventPublisher.java','고침','구체 처리기 즉시 호출'),('ddd/src/ddd/catalog/application/DecreaseStockHandler.java','고침','상품 서비스에 재고 감소 요청'),('ddd/src/ddd/order/application/OrderService.java','고침','회원 서비스 조회와 주문 접수 발행'),('ddd/src/ddd/Main.java','고침','회원과 상품 목록, 주문 재고 변화'),('ddd/context-map.md','고침','현재 세 컨텍스트와 의존 관계'),('ddd/glossary.md','고침','서비스와 동기 이벤트'),('ddd/where-rules-live.md','고침','이번 강의 규칙 위치'),('tests/ddd','고침','1~17장 계약과 목록, 즉시 처리 검증')]
}
for chapter, files in plans.items():
    write(p/f'chapters/{chapter}/plan.md', '# '+chapter+' 계획\n\n## 파일\n| 파일 | 새로 또는 고침 | 하는 일 |\n|---|---|---|\n'+'\n'.join('| '+' | '.join(row)+' |' for row in files)+'''\n\n## 코드 계획
- spec의 할 일만 구현한다
- 학생 코드에 주석을 쓰지 않는다
- 기존 task 순서의 코드 차이를 따라치기에 녹인다
- 뒤 장의 구현을 앞 장에 미리 넣지 않는다

## 라이브러리
- 추가 없음
''')

fixes = [('free-shipping','T4','무료 배송 조건을 한 명세로 합치기','T2','7834719','금액과 지역의 두 조건을 한눈에 읽게 한다'),('order-events','T5','이벤트 대신 주문 뒤 재고 바로 줄이기','T1, T2, T3','8148820','재고 감소의 기본 흐름을 먼저 배운다'),('optimistic-lock','T5','직접 재고 감소 흐름에서 낙관적 잠금 유지하기','T1, T2, T3','139b911','이벤트를 도입하기 전에도 동시 수정 예제를 실행한다'),('bounded-contexts','T9','서비스와 즉시 처리 이벤트로 강의 마무리하기','T1, T3, T4, T5','8a46784','컨텍스트 책임과 이벤트 반응을 짧은 호출 흐름으로 보인다')]
for chapter, task, title, old, commit, why in fixes:
    file = p/f'chapters/{chapter}/tasks.md'
    oldtext = file.read_text(encoding='utf-8')
    if f'- [ ] {task} ' not in oldtext:
        oldtext += f'\n- [ ] {task} {title}\n  - 근거: 고침 {old}\n  - 파일: plan.md의 파일 목록과 해당 장 시점 Main\n  - 실행하면: spec의 현재 실행 흐름과 같다\n  - Why: {why}\n'
    write(file, oldtext)

# 제외한 장 자료는 이력과 함께 보관하고 현재 장 폴더에서는 뺀다.
for chapter in ('shipping-context','payment-acl','order-query','order-saga','socket-queue'):
    src = p/f'chapters/{chapter}'
    dst = p/f'archive/chapters/{chapter}'
    if src.exists():
        assert root in src.resolve().parents and root in dst.resolve().parents
        dst.parent.mkdir(parents=True, exist_ok=True)
        src.rename(dst)
write(p/'archive/README.md', '''# 이전 강의 자료

- 18장부터 22장은 현재 강의에서 제외했다
- 이 폴더는 이전 자료 보관용이며 현재 따라치기와 배포 목차에 포함하지 않는다
- 기존 커밋과 done 태그는 그대로 보존한다
''')
print('수정 계획과 현재 17장 목차 저장 완료')
