# bounded-contexts 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/context-map.md | 새로 | 하위 도메인과 바운디드 컨텍스트, 컨텍스트 사이를 적는 학생 문서 |
| ddd/src/ddd/member/domain/Member.java | 옮김 | ddd/src/ddd/order/domain/Member.java를 회원 컨텍스트로 옮긴다 |
| ddd/src/ddd/member/domain/MemberGrade.java | 옮김 | ddd/src/ddd/order/domain/MemberGrade.java를 회원 컨텍스트로 옮긴다 |
| ddd/src/ddd/member/domain/MemberRepository.java | 옮김 | ddd/src/ddd/order/domain/MemberRepository.java를 회원 컨텍스트로 옮긴다 |
| ddd/src/ddd/member/infrastructure/MemoryMemberRepository.java | 옮김 | ddd/src/ddd/order/infrastructure/MemoryMemberRepository.java를 회원 컨텍스트로 옮긴다 |
| ddd/src/ddd/catalog/domain/Product.java | 옮김 | ddd/src/ddd/order/domain/Product.java를 상품 컨텍스트로 옮긴다 |
| ddd/src/ddd/catalog/domain/ProductRepository.java | 옮김 | ddd/src/ddd/order/domain/ProductRepository.java를 상품 컨텍스트로 옮긴다 |
| ddd/src/ddd/catalog/application/DecreaseStockHandler.java | 옮김 | ddd/src/ddd/order/application/DecreaseStockHandler.java를 상품 컨텍스트로 옮긴다. 재고를 바꾸는 일이라 상품 쪽이다 |
| ddd/src/ddd/catalog/infrastructure/MemoryProductRepository.java | 옮김 | ddd/src/ddd/order/infrastructure/MemoryProductRepository.java를 상품 컨텍스트로 옮긴다 |
| ddd/src/ddd/order/domain/DiscountPolicy.java | 고침 | 회원 컨텍스트의 회원과 회원 등급을 import로 쓴다 |
| ddd/src/ddd/order/domain/OrderItemFactory.java | 고침 | 상품 컨텍스트의 상품과 상품 저장소를 import로 쓴다 |
| ddd/src/ddd/order/application/OrderService.java | 고침 | 회원 컨텍스트의 회원과 회원 저장소를 import로 쓴다 |
| ddd/src/ddd/Main.java | 고침 | 이 장 장면 하나를 찍고, 옮긴 클래스를 import로 가져다 쓴다 |
| tests/ddd/BoundedContextsTest.java | 새로 | S1부터 S5까지 행동 테스트 |

## 코드 계획
이클립스에서 옮기기
- 패키지 탐색기에서 src를 오른쪽 클릭하고 New, Package로 묶음을 만든다. 이름은 ddd.member.domain처럼 한 번에 쓴다
- 옮길 클래스를 골라 Refactor, Move(Alt+Shift+V)로 새 묶음을 고르거나 끌어 놓는다
- 이클립스가 옮긴 파일의 package 줄과 쓰는 쪽의 import 줄을 함께 바꾼다. 같은 묶음이던 클래스끼리 떨어지면 import가 새로 생긴다
- import 순서가 다르면 Source, Organize Imports(Ctrl+Shift+O)로 맞춘다. java 묶음 아래에 빈 줄을 두고 ddd 묶음을 이름 순서로 적는다
- 클래스 안의 코드는 바꾸지 않는다. 모든 클래스와 쓰는 메서드가 이미 public이라 import만 있으면 된다
- 클래스가 없는 계층 묶음은 만들지 않는다. 회원 컨텍스트에는 응용 계층이 없다

context-map.md (학생 문서)
- T1: 하위 도메인 절. 하위 도메인, 종류, 이유, 바운디드 컨텍스트, 묶음 표를 쓴다. 주문은 핵심, 상품은 지원, 회원은 일반
- T1: 하위 도메인은 업무를 나눈 것이고 바운디드 컨텍스트는 한 모델과 말이 통하는 코드의 경계라고 한 줄씩 쓴다. 이 프로그램은 하위 도메인 하나에 컨텍스트 하나를 두고, 컨텍스트 안에 세 계층을 둔다
- T5: 컨텍스트 사이 절. 쓰는 쪽, 쓰이는 쪽, 무엇 표를 쓴다. 주문이 상품을 읽고(주문 항목 팩토리), 주문이 회원을 읽고(할인 정책, 주문 응용 서비스), 상품이 주문 접수됨을 받는다(재고 줄이기 처리기)
- T5: 같은 상품이라도 상품 컨텍스트의 상품은 지금 가격과 재고를, 주문 컨텍스트의 주문 항목은 주문 때의 이름과 가격을 갖는다고 쓴다. 지금은 다른 컨텍스트의 클래스를 바로 쓰고, 정해진 창구로 잇는 것은 뒤 장에서 한다고 쓴다

Main.java
- T1: 첫 줄을 ===== 컨텍스트 나누기 =====로 바꾸고 앞 장 장면을 지운다
- T1: 발행기에 재고 줄이기 처리기를 구독시키고 주문 응용 서비스에 넘긴다
- T1: 장면 [세 컨텍스트를 거쳐 주문하기]는 자바 책 2개를 주문하고 이벤트를 처리한 뒤 주문 컨텍스트 줄(주문 번호, 금액, 상태), 회원 컨텍스트 줄(이름, 등급), 상품 컨텍스트 줄(자바 책 재고 전 -> 후)을 찍는다
- T3, T4: 바뀌는 것은 import 줄뿐이다. 출력은 T1과 같다

회원 컨텍스트 ddd.member
- T3: ddd.member.domain에 Member, MemberGrade, MemberRepository, ddd.member.infrastructure에 MemoryMemberRepository를 옮긴다
- DiscountPolicy, OrderService, Main에 ddd.member import가 생긴다

상품 컨텍스트 ddd.catalog
- T4: ddd.catalog.domain에 Product, ProductRepository, ddd.catalog.application에 DecreaseStockHandler, ddd.catalog.infrastructure에 MemoryProductRepository를 옮긴다
- DecreaseStockHandler를 상품에 두는 이유: 주문 접수됨을 받아 상품 재고를 줄이고 저장하는 상품 쪽 유스케이스다
- DecreaseStockHandler에 주문 컨텍스트의 EventHandler, OrderItem, OrderPlaced import가 생긴다
- OrderItemFactory와 Main에 ddd.catalog import가 생긴다. 주문 컨텍스트가 상품 컨텍스트를 읽는 것이 import로 드러난다

주문 컨텍스트 ddd.order
- 주문 쪽 클래스는 layers 장부터 ddd.order 아래에 있어서 옮기지 않는다
- 회원과 상품을 옮기고 나면 src/ddd에는 Main.java와 catalog, member, order만 남는다
- EventPublisher와 EventHandler는 주문 응용 계층에 그대로 둔다. 주문 응용 서비스가 저장 뒤 이벤트를 넘기는 곳이다. 상품 컨텍스트는 Main에서 구독만 한다

BoundedContextsTest.java
- 테스트 패키지는 ddd다. 새 묶음은 Class.forName과 소스 파일 경로로 확인해, 옮기기 전에도 테스트가 컴파일된다
- S1은 context-map.md의 표에 주문 핵심 ddd.order, 상품 지원 ddd.catalog, 회원 일반 ddd.member 줄이 있는지 본다
- S2, S3은 클래스가 새 묶음에 있고 파일이 옮겨졌고 옛 파일이 없는지, 저장소 구현이 같은 컨텍스트의 저장소를 구현하는지 본다
- S4는 src/ddd가 Main.java, catalog, member, order뿐인지 본다
- S5는 컨텍스트마다 domain 파일에 application, infrastructure import가 없고 application 파일에 infrastructure import가 없는지, 컨텍스트 맵의 컨텍스트 사이 절에 주문 항목 팩토리, 할인 정책, 주문 접수됨이 있는지 본다
- Main 출력 전체가 실행 결과와 같은지 본다

## 핵심 코드
package ddd.order.domain;

import ddd.catalog.domain.Product;
import ddd.catalog.domain.ProductRepository;

public class OrderItemFactory {

package ddd.catalog.application;

import ddd.catalog.domain.Product;
import ddd.catalog.domain.ProductRepository;
import ddd.order.application.EventHandler;
import ddd.order.domain.OrderItem;
import ddd.order.domain.OrderPlaced;

public class DecreaseStockHandler implements EventHandler {

package ddd;

import ddd.catalog.application.DecreaseStockHandler;
import ddd.member.domain.Member;
import ddd.order.application.OrderService;
import ddd.order.domain.Order;

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | context-map.md |
| S2 | member의 네 파일, DiscountPolicy.java, OrderService.java, Main.java |
| S3 | catalog의 네 파일, OrderItemFactory.java, Main.java |
| S4 | Main.java |
| S5 | context-map.md |

## 라이브러리
- 추가 없음

## 다른 장 파일
- ddd/src/ddd/Main.java: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference, order-factory, discount, free-shipping, order-events, optimistic-lock
- ddd/src/ddd/order/domain 아래 회원과 상품 다섯 파일: id-reference, discount, order-events
- ddd/src/ddd/order/application/DecreaseStockHandler.java: order-events
- ddd/src/ddd/order/infrastructure 아래 두 파일: id-reference
- ddd/src/ddd/order/domain/DiscountPolicy.java: discount
- ddd/src/ddd/order/domain/OrderItemFactory.java: order-factory
- ddd/src/ddd/order/application/OrderService.java: anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order, layers, id-reference, order-factory, discount, order-events
