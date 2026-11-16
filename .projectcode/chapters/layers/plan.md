# layers 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/domain/Order.java | 옮김 | ddd/src/ddd/Order.java를 도메인 계층으로 옮긴다 |
| ddd/src/ddd/domain/OrderLine.java | 옮김 | ddd/src/ddd/OrderLine.java를 도메인 계층으로 옮긴다 |
| ddd/src/ddd/domain/Money.java | 옮김 | ddd/src/ddd/Money.java를 도메인 계층으로 옮긴다 |
| ddd/src/ddd/domain/Address.java | 옮김 | ddd/src/ddd/Address.java를 도메인 계층으로 옮긴다 |
| ddd/src/ddd/domain/OrderStatus.java | 옮김 | ddd/src/ddd/OrderStatus.java를 도메인 계층으로 옮긴다 |
| ddd/src/ddd/domain/OrderRepository.java | 옮김 | ddd/src/ddd/OrderRepository.java를 도메인 계층으로 옮긴다. 저장소 인터페이스는 도메인이 갖는다 |
| ddd/src/ddd/application/OrderApplicationService.java | 옮김 | ddd/src/ddd/OrderApplicationService.java를 응용 계층으로 옮긴다 |
| ddd/src/ddd/application/PlaceOrderCommand.java | 옮김 | ddd/src/ddd/PlaceOrderCommand.java를 응용 계층으로 옮긴다 |
| ddd/src/ddd/application/OrderLineCommand.java | 옮김 | ddd/src/ddd/OrderLineCommand.java를 응용 계층으로 옮긴다 |
| ddd/src/ddd/infrastructure/MemoryOrderRepository.java | 옮김 | ddd/src/ddd/MemoryOrderRepository.java를 인프라 계층으로 옮긴다 |
| ddd/src/ddd/Main.java | 고침 | 옮긴 클래스를 import로 가져다 쓴다 |
| tests/ddd/AnemicOrderTest.java | 고침 | 옮긴 클래스를 import로 부른다 |
| tests/ddd/UbiquitousLanguageTest.java | 고침 | 옮긴 클래스를 import로 부른다 |
| tests/ddd/RichEntityTest.java | 고침 | 옮긴 클래스를 import로 부른다 |
| tests/ddd/ValueObjectTest.java | 고침 | 옮긴 클래스를 import로 부른다 |
| tests/ddd/OrderAggregateTest.java | 고침 | 옮긴 클래스를 import로 부른다 |
| tests/ddd/OrderStatusTest.java | 고침 | 옮긴 클래스를 import로 부른다 |
| tests/ddd/OrderRepositoryTest.java | 고침 | 옮긴 클래스를 import로 부른다 |
| tests/ddd/PlaceOrderTest.java | 고침 | 옮긴 클래스를 import로 부른다 |
| tests/ddd/LayersTest.java | 새로 | S1부터 S4까지 행동 테스트 |

## 코드 계획
이클립스에서 옮기기
- 패키지 탐색기에서 src를 오른쪽 클릭하고 New, Package로 묶음을 만든다. 이름은 ddd.domain처럼 쓴다
- 옮길 클래스를 골라 새 묶음으로 끌어 놓거나 Refactor, Move(Alt+Shift+V)를 쓴다
- 이클립스가 옮긴 파일의 package 줄과 쓰는 쪽의 import 줄을 함께 바꾼다
- 클래스 안의 코드는 바꾸지 않는다. 모든 클래스와 쓰는 메서드가 이미 public이라 import만 있으면 된다
- import는 이클립스가 넣는 모양 그대로 하나씩 쓴다. java 묶음 아래에 빈 줄을 두고 ddd 묶음을 적는다

도메인 계층 ddd.domain
- T1: Order, OrderLine, Money, Address, OrderStatus, OrderRepository 여섯 파일을 옮긴다
- 첫 줄만 package ddd.domain;으로 바뀐다. 여섯은 서로만 쓰므로 import가 생기지 않는다
- 쓰는 쪽인 Main, OrderApplicationService, MemoryOrderRepository에 ddd.domain import가 생긴다
- OrderRepository를 도메인에 두는 이유: 주문을 어떻게 저장하고 꺼낼지는 도메인이 정하는 약속이다. 구현은 바깥이 한다

응용 계층 ddd.application
- T2: OrderApplicationService, PlaceOrderCommand, OrderLineCommand 세 파일을 함께 옮긴다
- 세 파일이 같은 묶음에 있으므로 명령 import는 생기지 않는다. OrderApplicationService의 ddd.domain import는 그대로다
- Main에 ddd.application import 세 줄이 생긴다

인프라 계층 ddd.infrastructure
- T3: MemoryOrderRepository를 옮긴다
- implements OrderRepository는 그대로다. 인프라가 도메인의 인터페이스를 구현해 의존 방향이 도메인 쪽을 향한다
- Main에 ddd.infrastructure import 한 줄이 생긴다. new OrderApplicationService(new MemoryOrderRepository())로 둘을 잇는 곳은 Main 한 곳이다

Main.java
- ddd 묶음에 그대로 둔다. 장면과 출력은 앞 장과 같다
- 바뀌는 것은 import 줄뿐이다

LayersTest.java
- 테스트 패키지는 ddd다. 새 묶음은 Class.forName과 소스 파일 경로로 확인해, 옮기기 전에도 테스트가 컴파일된다
- S1은 여섯 클래스가 ddd.domain에 있고 src/ddd/domain에 파일이 있으며 src/ddd에 옛 파일이 없는지 본다
- S2는 세 클래스가 ddd.application에 있고 파일이 옮겨졌는지, 주문 응용 서비스가 ddd.domain의 주문 저장소를 받는지 본다
- S3은 메모리 주문 저장소가 ddd.infrastructure에 있고 ddd.domain의 주문 저장소를 구현하는지, 파일이 옮겨졌는지 본다
- S4는 src/ddd/domain 파일에 ddd.application, ddd.infrastructure import가 없고, src/ddd/application 파일에 ddd.infrastructure import가 없는지, 세 묶음을 모두 쓰는 곳이 Main인지, Main 출력 열여덟 줄이 그대로인지 본다

앞 장 테스트
- package ddd;는 그대로 두고 쓰는 클래스만 import ddd.domain.Order;처럼 더한다

## 핵심 코드
package ddd.domain;

public interface OrderRepository {

package ddd.infrastructure;

import ddd.domain.Order;
import ddd.domain.OrderRepository;

public class MemoryOrderRepository implements OrderRepository {

package ddd;

import java.util.List;

import ddd.application.OrderApplicationService;
import ddd.application.OrderLineCommand;
import ddd.application.PlaceOrderCommand;
import ddd.domain.Order;
import ddd.domain.OrderLine;
import ddd.infrastructure.MemoryOrderRepository;

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | domain의 여섯 파일, OrderApplicationService.java, MemoryOrderRepository.java, Main.java |
| S2 | application의 세 파일, Main.java |
| S3 | infrastructure/MemoryOrderRepository.java, Main.java |
| S4 | Main.java |

## 라이브러리
- 추가 없음

## 다른 장 파일
- ddd/src/ddd/Main.java: project-setup, anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order
- ddd/src/ddd/Order.java: anemic-order, rich-entity, value-object, order-aggregate, order-status, order-repository
- ddd/src/ddd/OrderLine.java: order-aggregate, order-repository
- ddd/src/ddd/Money.java: value-object, order-repository
- ddd/src/ddd/Address.java: value-object, order-repository
- ddd/src/ddd/OrderStatus.java: order-status
- ddd/src/ddd/OrderRepository.java: order-repository
- ddd/src/ddd/MemoryOrderRepository.java: order-repository
- ddd/src/ddd/OrderApplicationService.java: anemic-order, ubiquitous-language, rich-entity, value-object, order-aggregate, order-status, order-repository, place-order
- ddd/src/ddd/PlaceOrderCommand.java: place-order
- ddd/src/ddd/OrderLineCommand.java: place-order
- tests/ddd/AnemicOrderTest.java: anemic-order
- tests/ddd/UbiquitousLanguageTest.java: ubiquitous-language
- tests/ddd/RichEntityTest.java: rich-entity
- tests/ddd/ValueObjectTest.java: value-object
- tests/ddd/OrderAggregateTest.java: order-aggregate
- tests/ddd/OrderStatusTest.java: order-status
- tests/ddd/OrderRepositoryTest.java: order-repository
- tests/ddd/PlaceOrderTest.java: place-order
