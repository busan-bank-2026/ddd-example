# layers 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/order/domain/Order.java | 옮김 | ddd/src/ddd/Order.java를 도메인 계층으로 옮긴다 |
| ddd/src/ddd/order/domain/OrderItem.java | 옮김 | ddd/src/ddd/OrderItem.java를 도메인 계층으로 옮긴다 |
| ddd/src/ddd/order/domain/Address.java | 옮김 | ddd/src/ddd/Address.java를 도메인 계층으로 옮긴다 |
| ddd/src/ddd/order/domain/OrderStatus.java | 옮김 | ddd/src/ddd/OrderStatus.java를 도메인 계층으로 옮긴다 |
| ddd/src/ddd/order/domain/OrderRepository.java | 옮김 | ddd/src/ddd/OrderRepository.java를 도메인 계층으로 옮긴다. 저장소 인터페이스는 도메인이 갖는다 |
| ddd/src/ddd/order/application/OrderService.java | 옮김 | ddd/src/ddd/OrderService.java를 응용 계층으로 옮긴다 |
| ddd/src/ddd/order/application/OrderRequest.java | 옮김 | ddd/src/ddd/OrderRequest.java를 응용 계층으로 옮긴다 |
| ddd/src/ddd/order/application/OrderItemRequest.java | 옮김 | ddd/src/ddd/OrderItemRequest.java를 응용 계층으로 옮긴다 |
| ddd/src/ddd/order/infrastructure/MemoryOrderRepository.java | 옮김 | ddd/src/ddd/MemoryOrderRepository.java를 인프라 계층으로 옮긴다 |
| ddd/src/ddd/Main.java | 고침 | 이 장 장면을 찍고, 옮긴 클래스를 import로 가져다 쓴다 |
| tests/ddd/LayersTest.java | 새로 | S1부터 S4까지 행동 테스트 |

## 코드 계획
이클립스에서 옮기기
- 패키지 탐색기에서 src를 오른쪽 클릭하고 New, Package로 묶음을 만든다. 이름은 ddd.order.domain처럼 쓴다
- 옮길 클래스를 골라 새 묶음으로 끌어 놓거나 Refactor, Move(Alt+Shift+V)를 쓴다
- 이클립스가 옮긴 파일의 package 줄과 쓰는 쪽의 import 줄을 함께 바꾼다
- 클래스 안의 코드는 바꾸지 않는다. 모든 클래스와 쓰는 메서드가 이미 public이라 import만 있으면 된다
- import는 이클립스가 넣는 모양 그대로 하나씩 쓴다. java 묶음 아래에 빈 줄을 두고 ddd 묶음을 적는다

ddd.order 아래에 두는 이유
- 계층은 주문 묶음 ddd.order 아래에 둔다
- bounded-contexts 장에서 컨텍스트로 나눌 때 주문 계층을 다시 옮기지 않아도 된다

도메인 계층 ddd.order.domain
- T1: Order, OrderItem, Address, OrderStatus, OrderRepository 다섯 파일을 옮긴다
- 첫 줄만 package ddd.order.domain;으로 바뀐다. 다섯은 서로만 쓰므로 import가 생기지 않는다
- 쓰는 쪽인 Main, OrderService, MemoryOrderRepository에 ddd.order.domain import가 생긴다
- OrderRepository를 도메인에 두는 이유: 주문을 어떻게 저장하고 꺼낼지는 도메인이 정하는 약속이다. 구현은 바깥이 한다

응용 계층 ddd.order.application
- T2: OrderService, OrderRequest, OrderItemRequest 세 파일을 함께 옮긴다
- 세 파일이 같은 묶음에 있으므로 요청 import는 생기지 않는다. OrderService의 ddd.order.domain import는 그대로다
- Main에 ddd.order.application import 세 줄이 생긴다

인프라 계층 ddd.order.infrastructure
- T3: MemoryOrderRepository를 옮긴다
- implements OrderRepository는 그대로다. 인프라가 도메인의 인터페이스를 구현해 의존 방향이 도메인 쪽을 향한다
- Main에 ddd.order.infrastructure import 한 줄이 생긴다. new OrderService(new MemoryOrderRepository())로 둘을 잇는 곳은 Main 한 곳이다

Main.java
- ddd 묶음에 그대로 둔다
- T1: 첫 줄을 ===== 계층 나누기 =====로 바꾸고, 앞 장 장면을 지우고 장면 하나만 둔다
- 장면 [계층을 거쳐 주문하고 결제하기]: 김철수가 자바 책 2개, 볼펜 1개를 주문하고, 주문 줄을 찍고, 결제한 뒤 상태를 전 -> 후로 찍는다
- T2, T3에서는 import 줄만 바뀐다. 출력은 세 task 모두 같다

LayersTest.java
- 테스트 패키지는 ddd다. 새 묶음은 Class.forName과 소스 파일 경로로 확인해, 옮기기 전에도 테스트가 컴파일된다
- S1은 다섯 클래스가 ddd.order.domain에 있고 src/ddd/order/domain에 파일이 있으며 src/ddd에 옛 파일이 없는지 본다
- S2는 세 클래스가 ddd.order.application에 있고 파일이 옮겨졌는지, 주문 응용 서비스가 ddd.order.domain의 주문 저장소를 받는지 본다
- S3은 메모리 주문 저장소가 ddd.order.infrastructure에 있고 ddd.order.domain의 주문 저장소를 구현하는지, 파일이 옮겨졌는지 본다
- S4는 domain 파일에 application, infrastructure import가 없고, application 파일에 infrastructure import가 없는지, 세 묶음을 모두 쓰는 곳이 Main인지 본다

## 핵심 코드
package ddd.order.domain;

public interface OrderRepository {

package ddd.order.infrastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ddd.order.domain.Order;
import ddd.order.domain.OrderRepository;

public class MemoryOrderRepository implements OrderRepository {

package ddd;

import java.util.List;

import ddd.order.application.OrderItemRequest;
import ddd.order.application.OrderRequest;
import ddd.order.application.OrderService;
import ddd.order.domain.Order;
import ddd.order.infrastructure.MemoryOrderRepository;

OrderService orderService = new OrderService(new MemoryOrderRepository());

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | domain의 다섯 파일, OrderService.java, MemoryOrderRepository.java, Main.java |
| S2 | application의 세 파일, Main.java |
| S3 | infrastructure/MemoryOrderRepository.java, Main.java |
| S4 | Main.java |

## 라이브러리
- 추가 없음

## 다른 장 파일
- ddd/src/ddd/Main.java: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order
- ddd/src/ddd/Order.java: anemic-order, rich-entity, value-object, order-aggregate, order-status, order-repository
- ddd/src/ddd/OrderItem.java: order-aggregate
- ddd/src/ddd/Address.java: value-object
- ddd/src/ddd/OrderStatus.java: order-status
- ddd/src/ddd/OrderRepository.java: order-repository
- ddd/src/ddd/MemoryOrderRepository.java: order-repository
- ddd/src/ddd/OrderService.java: anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order
- ddd/src/ddd/OrderRequest.java: place-order
- ddd/src/ddd/OrderItemRequest.java: place-order
