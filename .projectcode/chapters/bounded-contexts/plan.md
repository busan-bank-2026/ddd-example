# bounded-contexts 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/context-map.md | 새로 | 하위 도메인과 바운디드 컨텍스트, 공유 커널, 컨텍스트 사이를 적는 학생 문서 |
| ddd/src/ddd/shared/Money.java | 옮김 | ddd/src/ddd/domain/Money.java를 공유 커널로 옮긴다 |
| ddd/src/ddd/member/domain/Member.java | 옮김 | ddd/src/ddd/domain/Member.java를 회원 컨텍스트로 옮긴다 |
| ddd/src/ddd/member/domain/MemberGrade.java | 옮김 | ddd/src/ddd/domain/MemberGrade.java를 회원 컨텍스트로 옮긴다 |
| ddd/src/ddd/member/domain/MemberRepository.java | 옮김 | ddd/src/ddd/domain/MemberRepository.java를 회원 컨텍스트로 옮긴다 |
| ddd/src/ddd/member/infrastructure/MemoryMemberRepository.java | 옮김 | ddd/src/ddd/infrastructure/MemoryMemberRepository.java를 회원 컨텍스트로 옮긴다 |
| ddd/src/ddd/catalog/domain/Product.java | 옮김 | ddd/src/ddd/domain/Product.java를 상품 컨텍스트로 옮긴다 |
| ddd/src/ddd/catalog/domain/ProductRepository.java | 옮김 | ddd/src/ddd/domain/ProductRepository.java를 상품 컨텍스트로 옮긴다 |
| ddd/src/ddd/catalog/application/DecreaseStockHandler.java | 옮김 | ddd/src/ddd/application/DecreaseStockHandler.java를 상품 컨텍스트로 옮긴다. 재고를 바꾸는 일이라 상품 쪽이다 |
| ddd/src/ddd/catalog/infrastructure/MemoryProductRepository.java | 옮김 | ddd/src/ddd/infrastructure/MemoryProductRepository.java를 상품 컨텍스트로 옮긴다 |
| ddd/src/ddd/order/domain/ 열두 파일 | 옮김 | ddd/src/ddd/domain의 Address, DiscountPolicy, FreeShippingAreaSpecification, FreeShippingPriceSpecification, FreeShippingSpecification, Order, OrderItem, OrderItemFactory, OrderPlaced, OrderRepository, OrderSpecification, OrderStatus를 주문 컨텍스트로 옮긴다 |
| ddd/src/ddd/order/application/ 네 파일 | 옮김 | ddd/src/ddd/application의 EventPublisher, OrderApplicationService, OrderItemRequest, OrderRequest를 주문 컨텍스트로 옮긴다 |
| ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java | 옮김 | ddd/src/ddd/infrastructure/MemoryOrderRepository.java를 주문 컨텍스트로 옮긴다 |
| ddd/src/ddd/domain/ 옮기지 않은 파일 | 고침 | 옮긴 클래스를 쓰는 Order, OrderItem, DiscountPolicy, Product, OrderItemFactory에 import가 생긴다 |
| ddd/src/ddd/application/OrderApplicationService.java | 고침 | 회원 컨텍스트로 옮긴 회원과 회원 저장소를 import로 쓴다 |
| ddd/src/ddd/Main.java | 고침 | 옮긴 클래스를 import로 가져다 쓴다 |
| tests/ddd/BoundedContextsTest.java | 새로 | S1부터 S6까지 행동 테스트 |
| tests/ddd 아래 앞 장 테스트 열다섯 개 | 고침 | 옮긴 클래스를 새 묶음 import로 부르고, 묶음 이름을 보는 줄을 새 묶음으로 바꾼다 |

## 코드 계획
이클립스에서 옮기기
- 패키지 탐색기에서 src를 오른쪽 클릭하고 New, Package로 묶음을 만든다. 이름은 ddd.member.domain처럼 한 번에 쓴다
- 옮길 클래스를 골라 Refactor, Move(Alt+Shift+V)로 새 묶음을 고르거나 끌어 놓는다
- 이클립스가 옮긴 파일의 package 줄과 쓰는 쪽의 import 줄을 함께 바꾼다. 같은 묶음이던 클래스끼리 떨어지면 import가 새로 생긴다
- import 순서가 다르면 Source, Organize Imports(Ctrl+Shift+O)로 맞춘다. java 묶음 아래에 빈 줄을 두고 ddd 묶음을 이름 순서로 적는다
- 클래스 안의 코드는 바꾸지 않는다. 모든 클래스와 쓰는 메서드가 이미 public이라 import만 있으면 된다
- 클래스가 없는 계층 묶음은 만들지 않는다. 회원 컨텍스트에는 응용 계층이 없다
- 다 옮겨 빈 ddd.domain, ddd.application, ddd.infrastructure 묶음은 지운다

context-map.md (학생 문서)
- T1: 하위 도메인 절. 하위 도메인, 종류, 이유, 바운디드 컨텍스트, 묶음 표를 쓴다. 주문은 핵심, 상품은 지원, 회원은 일반
- T1: 하위 도메인은 업무를 나눈 것이고 바운디드 컨텍스트는 한 모델과 말이 통하는 코드의 경계라고 한 줄씩 쓴다. 이 프로그램은 하위 도메인 하나에 컨텍스트 하나를 둔다
- T2: 공유 커널 절. 돈을 ddd.shared에 두고 주문과 상품이 함께 쓴다. 바꾸려면 쓰는 컨텍스트가 모두 합의해야 해서 작게 둔다
- T5: 컨텍스트 사이 절. 쓰는 쪽, 쓰이는 쪽, 무엇 표를 쓴다. 주문이 상품을 읽고(주문 항목 팩토리), 주문이 회원을 읽고(할인 정책, 주문 응용 서비스), 상품이 주문 접수됨을 받는다(재고 줄이기 처리기)
- T5: 같은 상품이라도 상품 컨텍스트의 상품은 지금 가격과 재고를, 주문 컨텍스트의 주문 항목은 주문 때의 이름과 가격을 갖는다고 쓴다. 지금은 다른 컨텍스트의 클래스를 바로 쓰고, 정해진 창구로 잇는 것은 뒤 장에서 한다고 쓴다

공유 커널 ddd.shared
- T2: ddd.shared 묶음을 만들고 Money를 옮긴다
- Money를 쓰는 Order, OrderItem, DiscountPolicy, Product에 import ddd.shared.Money가 생긴다. Main의 Money import도 바뀐다
- 공유 커널에 계층 묶음을 두지 않는 이유: 함께 쓰는 모델 조각 하나뿐이고 응용과 인프라가 없다

회원 컨텍스트 ddd.member
- T3: ddd.member.domain에 Member, MemberGrade, MemberRepository, ddd.member.infrastructure에 MemoryMemberRepository를 옮긴다
- DiscountPolicy, OrderApplicationService, Main에 ddd.member import가 생긴다

상품 컨텍스트 ddd.catalog
- T4: ddd.catalog.domain에 Product, ProductRepository, ddd.catalog.application에 DecreaseStockHandler, ddd.catalog.infrastructure에 MemoryProductRepository를 옮긴다
- DecreaseStockHandler를 상품에 두는 이유: 주문 접수됨을 받아 상품 재고를 줄이고 저장하는 상품 쪽 유스케이스다
- OrderItemFactory와 Main에 ddd.catalog import가 생긴다. 주문 컨텍스트가 상품 컨텍스트를 읽는 것이 import로 드러난다

주문 컨텍스트 ddd.order
- T5: ddd.domain에 남은 열두 파일을 ddd.order.domain, ddd.application에 남은 네 파일을 ddd.order.application, MemoryOrderRepository를 ddd.order.infrastructure로 옮긴다
- EventPublisher를 주문 응용 계층에 두는 이유: 주문 응용 서비스가 저장 뒤 이벤트를 넘기는 곳이다. 상품 컨텍스트는 Main에서 구독만 한다
- DecreaseStockHandler의 주문 접수됨, 주문 항목 import가 ddd.order.domain으로 바뀐다
- 빈 ddd.domain, ddd.application, ddd.infrastructure 묶음을 지운다. src/ddd에는 Main.java와 catalog, member, order, shared만 남는다

Main.java
- ddd 묶음에 그대로 둔다. 장면과 출력은 앞 장과 같다
- 바뀌는 것은 import 줄뿐이다

BoundedContextsTest.java
- 테스트 패키지는 ddd다. 새 묶음은 Class.forName과 소스 파일 경로로 확인해, 옮기기 전에도 테스트가 컴파일된다
- 모든 테스트가 Main 출력 스물여섯 줄이 그대로인지 본다
- S1은 context-map.md의 표에 주문 핵심 ddd.order, 상품 지원 ddd.catalog, 회원 일반 ddd.member 줄이 있는지 본다
- S2는 Money가 ddd.shared에 있고 src/ddd/shared에는 Money.java만 있으며 옛 파일이 없는지, 컨텍스트 맵에 공유 커널과 Money가 있는지 본다
- S3, S4, S5는 클래스가 새 묶음에 있고 파일이 옮겨졌고 옛 파일이 없는지, 저장소 구현이 같은 컨텍스트의 저장소를 구현하는지 본다
- S5는 src/ddd가 Main.java, catalog, member, order, shared뿐인지 본다
- S6은 컨텍스트마다 domain 파일에 application, infrastructure import가 없고 application 파일에 infrastructure import가 없는지, 컨텍스트 맵의 컨텍스트 사이 절에 주문 항목 팩토리, 할인 정책, 주문 접수됨이 있는지 본다

앞 장 테스트
- package ddd;는 그대로 두고 import 줄을 새 묶음으로 바꾼다. 이름 순서로 다시 적는다
- 묶음 이름을 보는 줄(getPackageName)은 새 묶음 이름으로 바꾼다
- LayersTest는 계층 묶음이 컨텍스트 안으로 들어간 지금 모양을 본다. 옮긴 곳은 ddd.order.domain처럼 컨텍스트 묶음으로 보고, src/ddd 목록은 Main.java와 네 묶음으로 본다. 계층 import 규칙은 모든 컨텍스트에서 본다

## 핵심 코드
package ddd.shared;

public record Money(int amount) implements Serializable {

package ddd.order.domain;

import ddd.catalog.domain.Product;
import ddd.catalog.domain.ProductRepository;

public class OrderItemFactory {

package ddd.catalog.application;

import ddd.catalog.domain.Product;
import ddd.catalog.domain.ProductRepository;
import ddd.order.domain.OrderItem;
import ddd.order.domain.OrderPlaced;

package ddd;

import ddd.catalog.application.DecreaseStockHandler;
import ddd.member.domain.Member;
import ddd.order.application.OrderApplicationService;
import ddd.order.domain.Order;
import ddd.shared.Money;

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | context-map.md |
| S2 | shared/Money.java, Order.java, OrderItem.java, DiscountPolicy.java, Product.java, Main.java, context-map.md |
| S3 | member의 네 파일, DiscountPolicy.java, OrderApplicationService.java, Main.java |
| S4 | catalog의 네 파일, OrderItemFactory.java, Main.java |
| S5 | order의 열일곱 파일, DecreaseStockHandler.java, Main.java |
| S6 | context-map.md |

## 라이브러리
- 추가 없음

## 다른 장 파일
- ddd/src/ddd/Main.java: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference, order-factory, discount, free-shipping, order-events, optimistic-lock
- ddd/src/ddd/domain 아래 열여덟 파일: value-object, order-repository, layers, discount, anemic-order, rich-entity, order-aggregate, order-status, id-reference, free-shipping, order-events, optimistic-lock, order-factory
- ddd/src/ddd/application 아래 다섯 파일: anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference, order-factory, discount, order-events
- ddd/src/ddd/infrastructure 아래 세 파일: order-repository, layers, id-reference, optimistic-lock
- tests/ddd 아래 앞 장 테스트 열다섯 개: anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference, order-factory, discount, free-shipping, order-events, optimistic-lock
