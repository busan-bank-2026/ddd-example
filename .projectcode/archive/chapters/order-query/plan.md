# order-query 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/order/domain/OrderRepository.java | 고침 | 회원 번호로 주문 꺼내기를 더한다 |
| ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java | 고침 | 회원 번호가 같은 주문을 복사본으로 모아 돌려준다 |
| ddd/src/ddd/order/application/OrderSummary.java | 새로 | 주문 요약 record. 조회 쪽 읽기 모델 |
| ddd/src/ddd/order/application/OrderQueryService.java | 새로 | 주문 조회 서비스. 회원 번호로 주문 요약 목록을 돌려준다 |
| ddd/src/ddd/Main.java | 고침 | 이 장 장면 둘을 찍는다 |
| ddd/glossary.md | 고침 | 주문 요약, 주문 조회 서비스, 명령과 조회 나누기 |
| tests/ddd/OrderQueryTest.java | 새로 | S1부터 S4까지 행동 테스트 |

## 코드 계획
OrderRepository.java (T1)
- List<Order> findByMemberId(long memberId)
- 도메인 인터페이스에 더하는 이유: 조회 서비스도 저장된 주문을 읽어야 하고, 주문을 넣어 둔 곳은 주문 저장소 하나다

MemoryOrderRepository.java (T1)
- findByMemberId: orders.values()를 돌며 getMemberId()가 같은 주문을 복사본으로 모은다
- 복사본은 복사 생성자 new Order(order)로 만든다. 버전 검사나 저장은 하지 않는다

OrderSummary.java (T1, 주문 응용 계층)
- record OrderSummary(long orderId, String memberName, int itemCount, int paymentPrice, String status)
- 칸은 숫자와 글자뿐이다. 주문 상태, 주문 항목 같은 도메인 타입을 쓰지 않는다
- 따로 두는 이유: 화면은 주문 한 건을 한 줄로 본다. 규칙을 지키는 주문 모양과 보여주는 모양이 다르다
- 응용 계층에 두는 이유: 도메인 규칙이 아니라 유스케이스가 돌려주는 결과다

OrderQueryService.java (T1, 주문 응용 계층)
- 생성자에서 OrderRepository와 MemberRepository만 받는다
- findOrderSummaries(long memberId): orderRepository.findByMemberId로 꺼낸 주문마다 회원 이름을 찾아 OrderSummary로 옮겨 담아 돌려준다
- if, throw, save가 없다. 읽기만 한다
- 주문 응용 서비스와 나누는 이유: 바꾸는 쪽은 규칙을 지키며 저장하고, 읽는 쪽은 규칙 없이 화면 모양으로 옮기기만 한다. 둘은 바뀌는 이유가 다르다

Main.java (T1)
- 첫 줄을 ===== 주문 목록 조회 =====로 바꾸고 앞 장 장면을 지운다
- 회원 1 김철수, 2 이영희와 상품 1 자바 책 15000원, 2 볼펜 1000원을 저장한다
- 주문 저장소를 OrderRepository orderRepository로 따로 만들어 주문 서비스와 new OrderQueryService(orderRepository, memberRepository)에 함께 넘긴다
- 장면 [주문하기]는 김철수가 자바 책 2개와 볼펜 1개를 주문하고 결제, 볼펜 3개를 주문하고, 이영희가 자바 책 1개를 주문한다. 회원마다 주문 번호를 찍는다
- 장면 [김철수의 주문 목록]은 findOrderSummaries(1)를 돌며 주문 번호, 회원 이름, 항목 수, 결제 금액, 상태를 한 줄씩 찍는다. 이영희의 3번 주문은 나오지 않는다

glossary.md (T2)
- 주문 요약 OrderSummary, 주문 조회 서비스 OrderQueryService, 명령과 조회 나누기 CQRS 줄

OrderQueryTest.java
- 패키지는 ddd다. 주문 클래스는 import로 부른다
- S1은 두 회원이 주문한 메모리 저장소에서 회원 번호로 꺼낸 주문 번호, 다른 회원 빈 목록, 꺼낸 주문을 바꿔도 저장된 주문이 그대로인지 본다
- S2는 주문 요약이 응용 계층의 record이고 칸 다섯 개의 이름과 타입이 long, String, int, int, String인지 본다
- S3은 결제한 주문과 결제 안 한 주문의 요약, 조회 뒤 주문 버전이 그대로인지, 주문이 없는 회원은 빈 목록인지, 생성자 매개변수가 주문 저장소와 회원 저장소뿐인지, 주문 응용 서비스와 다른 클래스인지, Main 출력 전체를 본다
- S4는 용어집 세 줄을 본다

## 핵심 코드
public record OrderSummary(long orderId, String memberName, int itemCount, int paymentPrice, String status) {
}

public List<OrderSummary> findOrderSummaries(long memberId) {
    List<OrderSummary> summaries = new ArrayList<>();
    for (Order order : orderRepository.findByMemberId(memberId)) {
        Member member = memberRepository.findById(order.getMemberId()).orElseThrow();
        summaries.add(new OrderSummary(order.getId(), member.getName(), order.getItems().size(),
                order.getPaymentPrice(), order.getStatus().label()));
    }
    return summaries;
}

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | OrderRepository.java, MemoryOrderRepository.java |
| S2 | OrderSummary.java |
| S3 | OrderQueryService.java, Main.java |
| S4 | glossary.md |

## 라이브러리
- 추가 없음

## 다른 장 파일
- ddd/src/ddd/order/domain/OrderRepository.java: order-repository, layers, bounded-contexts
- ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java: order-repository, layers, optimistic-lock, bounded-contexts
- ddd/src/ddd/Main.java: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference, order-factory, discount, free-shipping, order-events, optimistic-lock, bounded-contexts, shipping-context, payment-acl
- ddd/glossary.md: ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, id-reference, order-factory, discount, free-shipping, order-events, optimistic-lock, shipping-context, payment-acl
