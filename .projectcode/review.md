# 따라치기 코드 리뷰

## project-setup T1

### src/ddd/Main.java

#### 개념 폴더 구조
- 자바 프로젝트는 코드를 두는 src 폴더와 컴파일 결과가 쌓이는 bin 폴더로 나뉜다.
- src 아래 폴더 이름은 패키지 이름과 같아야 한다. 그래서 src/ddd 폴더의 파일은 첫 줄이 package ddd다.
- 폴더와 패키지가 어긋나면 이클립스가 빨간 줄로 알려 주고 실행되지 않는다.
- 패키지는 클래스를 묶는 이름표이고, 폴더는 그 이름표가 디스크에 놓인 자리다. 둘은 같은 것을 두 쪽에서 본 모습이다.
- 이 강의는 ddd 폴더 하나가 이클립스 프로젝트 하나이고, 학생 코드는 모두 src/ddd 아래에 쌓인다.

#### 코드
- 이클립스에서는 src 폴더를 오른쪽 클릭하고 New, Class를 고른 뒤 Package에 ddd, Name에 Main을 넣으면 이 파일이 생긴다.
- public static void main(String[] args)는 프로그램이 시작하는 자리다. 이 메서드가 있어야 Run으로 실행할 수 있다.
- System.out.println은 콘솔에 한 줄을 찍는다. 먼저 프로그램이 도는지만 확인한다.
- 이클립스에서는 Main.java를 연 채 Ctrl+F11을 누르거나 Run As, Java Application을 고르면 아래 Console 창에 글이 찍힌다.

## project-setup T2

### .classpath

#### 개념 이클립스 실행
- 이클립스는 폴더를 그냥 열지 않고, 폴더 안의 .project와 .classpath를 읽어 자바 프로젝트로 알아본다.
- 이 두 파일이 없으면 이클립스는 ddd 폴더를 자바 프로젝트로 보지 않아 컴파일도 실행도 하지 않는다.
- 이 파일들은 학생이 직접 치는 코드가 아니라, 이클립스에서 New, Java Project로 새 프로젝트를 만들면 자동으로 생기는 설정 파일과 같다.
- 받은 폴더를 열 때는 File, Import, General, Existing Projects into Workspace를 고르고 ddd 폴더를 고른다.
- 이름이 점으로 시작하는 파일은 Package Explorer에서 숨겨진다. 보려면 Project Explorer나 Navigator 창을 쓴다.

#### 코드
- 이 파일은 붙여넣기다. 이클립스에게 코드 폴더, 자바 버전, 결과 폴더를 알려 주는 덩어리다.
- kind="src" path="src"는 src 폴더를 코드 폴더로 쓴다는 뜻이다.
- kind="con" 줄은 JavaSE-21 자바로 컴파일하고 실행한다는 뜻이다. PC에 자바 21이 깔려 있어야 한다.
- kind="output" path="bin"은 컴파일한 .class 파일을 bin 폴더에 둔다는 뜻이다.

### .project

#### 코드
- 이 파일은 붙여넣기다. 프로젝트 이름과 이 프로젝트가 자바 프로젝트라는 것을 알려 주는 덩어리다.
- name이 ddd라서 이클립스 Package Explorer에 ddd 프로젝트로 보인다.
- javabuilder와 javanature는 저장할 때마다 자바 코드를 자동으로 컴파일하라는 뜻이다. 그래서 따로 컴파일 명령 없이 Run만 누르면 된다.

### .settings/org.eclipse.core.resources.prefs

#### 코드
- 이 파일은 붙여넣기다. 프로젝트 파일의 글자 인코딩을 UTF-8로 정한다.
- 윈도우 이클립스는 기본 인코딩이 MS949일 때가 있다. 그대로 두면 한글 출력이 깨지므로 UTF-8로 묶어 둔다.

### .settings/org.eclipse.jdt.core.prefs

#### 코드
- 이 파일은 붙여넣기다. 자바 컴파일러 버전과 들여쓰기 모양을 정한다.
- compliance, source, targetPlatform이 21이라서 record, switch 식 같은 자바 21 문법을 쓸 수 있다.
- tabulation.char=space와 size=4는 Tab 키를 눌러도 공백 네 칸으로 들여쓴다는 뜻이다. 강의 코드와 들여쓰기가 같아진다.
- Ctrl+Shift+F로 정렬하면 이 설정대로 코드 모양이 맞춰진다.

## anemic-order T1

### src/ddd/Main.java

#### 코드
- 새 장면은 김철수가 자바 책 15000원을 2개 주문하고, 주문 내용을 한 줄로 찍는 것이다.
- OrderService를 new로 만들고 createOrder에 값을 넘기면 Order가 돌아온다. 주문 데이터는 Order, 주문 일은 OrderService가 맡는 모양을 본다.
- 같은 ddd 패키지의 클래스라서 import 없이 Order와 OrderService를 쓴다.

### src/ddd/Order.java

#### 개념 빈약한 도메인 모델
- 빈약한 도메인 모델(Anemic Domain Model)은 객체에 데이터 칸과 getter, setter만 있고 업무 규칙은 하나도 없는 모양이다.
- 규칙은 서비스 클래스가 모두 들고, 객체는 서비스가 값을 넣고 빼는 상자로만 쓰인다.
- 처음에는 단순해 보이지만, setter가 열려 있어서 서비스를 거치지 않으면 누구나 상태를 아무 값으로 바꿀 수 있다.
- 그래서 결제 안 한 주문이 배송 중이 되는 것 같은 규칙 위반을 막을 곳이 없다.
- 기능이 늘수록 같은 검사가 여러 서비스에 흩어지고, 주문 규칙을 알려면 주문이 아니라 서비스 코드를 뒤져야 한다.
- 단순한 데이터 전달용 클래스와 겉모습은 같지만, 여기서 Order는 업무의 중심 개념인데도 규칙을 갖지 않는다는 점이 문제다.
- 이 예제에서는 Order가 상자이고 OrderService가 규칙을 든다. 4장 rich-entity에서 규칙을 Order 안으로 옮겨 고친다.

#### 코드
- 칸은 모두 private이고 칸마다 public getter와 setter가 있다. 칸을 감췄지만 setter로 무엇이든 바꿀 수 있어서 사실상 열려 있다.
- 상태 status는 주문 접수, 결제 완료 같은 글자 그대로 둔다. 글자 상태를 enum으로 바꾸는 것은 뒤 장 order-status에서 한다.
- getter와 setter는 이클립스의 Source, Generate Getters and Setters(Alt+Shift+S, R)로 한 번에 만들 수 있다.

### src/ddd/OrderService.java

#### 코드
- createOrder는 빈 Order를 new로 만들고 setter로 칸을 하나씩 채운다.
- 주문 금액 price * quantity와 처음 상태 주문 접수를 정하는 규칙이 Order가 아니라 서비스에 있다.
- 이름은 create처럼 데이터를 다루는 말로 지었다. 업무 말로 바꾸는 것은 다음 장이다.

## anemic-order T2

### src/ddd/Main.java

#### 코드
- 새 장면은 주문을 결제하고 바뀐 상태를 찍는 것이다.

### src/ddd/OrderService.java

#### 코드
- updatePayment는 setStatus로 상태를 결제 완료로 바꾼다. 서비스가 주문의 상태를 밖에서 직접 고친다.

## anemic-order T3

### src/ddd/Main.java

#### 코드
- 새 장면은 결제한 주문을 배송하고 상태를 찍는 것이다. 결제를 했으므로 거절되지 않는다.

### src/ddd/OrderService.java

#### 코드
- updateDelivery는 상태가 결제 완료가 아니면 배송을 거절한다. 결제했는지 보는 규칙이 서비스 안에 들어간다.
- 글자 비교는 == 대신 equals로 한다. ==는 같은 객체인지, equals는 글자 내용이 같은지 본다.
- IllegalStateException은 지금 상태로는 그 일을 할 수 없다는 뜻의 자바 기본 예외다. 메시지에 거절 이유를 담는다.

## anemic-order T4

### src/ddd/Main.java

#### 코드
- 새 장면은 배송 중인 주문을 취소해 보는 것이다. 거절될 줄 알기에 try catch로 감싼다.
- catch에서 e.getMessage()로 서비스가 담은 거절 이유를 꺼내 찍는다. 예외가 나도 프로그램이 멈추지 않는다.

### src/ddd/OrderService.java

#### 코드
- deleteOrder는 배송 중인 주문이면 취소를 거절하고, 아니면 상태를 취소로 바꾼다.
- 이것으로 주문 규칙 세 개가 모두 서비스에 쌓였다. Order는 여전히 규칙을 모른다.

## ubiquitous-language T1

### glossary.md

#### 개념 유비쿼터스 언어
- 유비쿼터스 언어(Ubiquitous Language)는 업무 담당자와 개발자가 회의, 문서, 코드에서 똑같이 쓰는 하나의 말이다.
- 앞 장 코드는 createOrder, updatePayment, deleteOrder처럼 데이터를 고치는 말로 이름을 지었다.
- 업무에서는 주문을 만든다, 지운다고 하지 않고 주문한다, 취소한다고 한다. 코드와 업무 말이 다르면 서로 번역해야 하고 그 사이에 오해가 생긴다.
- 특히 deleteOrder는 주문이 지워진다고 읽히지만 실제로는 취소 상태로 남는다. 이름이 업무와 다른 뜻을 말한다.
- 그냥 영어 이름을 잘 짓는 것과 다르다. 업무 쪽 사람이 쓰는 말을 먼저 정하고, 코드 이름을 그 말에 맞춘다.
- 이 예제에서는 glossary.md 용어집에 업무 말과 코드 이름을 나란히 적고, 다음 단계부터 메서드 이름을 그대로 바꾼다.

#### 코드
- 표의 칸은 업무 말, 코드 이름, 뜻 세 개다. 업무 말 하나가 코드 이름 하나와 짝을 이룬다.
- 상태 네 줄은 코드 이름도 한글 그대로다. 지금 코드에서 상태가 글자로 쓰이기 때문이다.
- 이클립스에서는 ddd 프로젝트를 오른쪽 클릭하고 New, File을 골라 이름에 glossary.md를 넣는다. src 밖, 프로젝트 바로 아래에 둔다.

## ubiquitous-language T2

### src/ddd/Main.java

#### 코드
- 부르는 이름만 createOrder에서 placeOrder로 바뀐다. 출력은 그대로다.

### src/ddd/OrderService.java

#### 코드
- createOrder를 용어집의 주문하기 placeOrder로 바꾼다. 안의 코드는 그대로다.
- 메서드 이름에 커서를 두고 Alt+Shift+R(Refactor, Rename)로 바꾸면, 이 메서드를 부르는 Main까지 한 번에 바뀐다.

## ubiquitous-language T3

### src/ddd/OrderService.java

#### 코드
- updatePayment는 결제하기 pay로, updateDelivery는 배송하기 ship으로 바꾼다. 상태를 고친다는 말 대신 업무에서 하는 일 이름이다.
- 이름만 바뀌고 규칙과 예외 메시지는 그대로다. 규칙은 여전히 서비스에 있다.

## ubiquitous-language T4

### src/ddd/OrderService.java

#### 코드
- deleteOrder를 주문 취소하기 cancel로 바꾼다. 주문은 지워지지 않고 취소 상태로 남으므로 delete는 업무와 다르게 읽힌다.

## rich-entity T1

### src/ddd/Main.java

#### 코드
- 새 장면은 이영희의 주문을 결제하지 않고 setStatus로 바로 배송 중으로 바꾸는 것이다.
- 서비스의 ship을 거치지 않았으므로 결제 검사가 돌지 않고, 결제 없이 배송: 배송 중이 찍힌다. 이 장에서 고칠 문제다.

## rich-entity T2

### glossary.md

#### 코드
- 주문 번호 id 줄을 더한다. 번호가 같으면 같은 주문이라는 뜻까지 적어 둔다.

### src/ddd/Main.java

#### 코드
- 주문 줄 맨 앞에 getId()로 주문 번호를 찍는다.

### src/ddd/Order.java

#### 개념 엔티티와 식별자
- 엔티티(Entity)는 식별자(Identifier)로 알아보는 객체다. 내용이 바뀌어도 식별자가 같으면 같은 것으로 본다.
- 주문은 결제, 배송을 거치며 상태가 계속 바뀐다. 내용으로 같은지를 보면 상태가 바뀐 순간 다른 주문이 되어 버린다.
- 그래서 주문마다 바뀌지 않는 번호를 주고, 같은지는 번호로만 본다.
- 앞 장의 Order는 번호가 없어 같은 주문인지 가릴 방법이 없었고, 빈 객체를 만든 뒤 setter로 채워서 칸이 빈 주문도 생길 수 있었다.
- 다음 장의 값 객체는 반대로 내용이 같으면 같다. 같음의 기준이 엔티티와 값 객체를 가르는 첫 번째 차이다.
- 이 예제에서는 Order가 엔티티이고 long id가 식별자다. equals와 hashCode가 id만 본다.

#### 코드
- 생성자가 생겨서 주문은 만들 때부터 모든 칸이 채워진다. 주문 금액과 주문 접수 상태도 주문이 스스로 정한다.
- 생성자로 칸을 채우므로 customerName부터 totalPrice까지의 setter를 지운다. setStatus는 아직 쓰는 곳이 있어 남는다.
- equals를 재정의해 id가 같으면 같은 주문으로 본다. @Override는 부모 메서드를 덮어쓴다는 표시로, 이름을 틀리면 컴파일러가 잡아 준다.
- o instanceof Order other는 o가 Order이면 other라는 이름으로 바로 꺼내 쓰는 문법이다. 형 변환 줄을 따로 쓰지 않는다.
- equals를 바꾸면 hashCode도 같은 기준으로 바꿔야 한다. 그래야 HashSet이나 HashMap에서 같은 주문이 같은 자리를 찾는다.
- Long.hashCode(id)는 long 값으로 hashCode를 만드는 자바 기본 메서드다.
- 생성자와 equals, hashCode는 이클립스 Source 메뉴의 Generate Constructor using Fields, Generate hashCode() and equals()로도 만들 수 있다. 다만 equals는 id만 고른다.

### src/ddd/OrderService.java

#### 코드
- long nextId = 1 칸으로 주문 번호를 1부터 매긴다.
- nextId++는 지금 값을 넘긴 뒤 1을 더한다. 첫 주문은 1번, 다음 주문은 2번이 된다.
- setter로 채우던 여덟 줄이 생성자 한 줄로 줄었다.

## rich-entity T3

### src/ddd/Order.java

#### 개념 행동 메서드
- 행동 메서드는 엔티티가 자기 상태를 바꾸는 업무 일을 하는 메서드다. 그 일의 규칙도 함께 지킨다.
- 앞 장까지는 규칙이 OrderService에 있어서, 서비스를 거치지 않고 setStatus를 부르면 결제 없이 배송이 되었다.
- 규칙을 그 규칙이 지키는 데이터 곁인 Order 안에 두면, 어느 길로 오든 주문이 스스로 거절한다.
- setter와 다르다. setter는 값을 그대로 넣지만 행동 메서드는 업무 이름을 갖고, 지금 상태에서 해도 되는 일인지 먼저 본다.
- 이 예제에서는 pay, ship, cancel이 행동 메서드다. 메서드 이름은 용어집의 업무 말 그대로다.

#### 코드
- OrderService에 있던 규칙과 예외 메시지를 그대로 옮긴다. order.getStatus() 대신 자기 칸 status를 바로 읽는다.

### src/ddd/OrderService.java

#### 코드
- pay, ship, cancel은 이제 주문에게 시키기만 한다. 규칙은 서비스에서 사라졌다.

## rich-entity T4

### src/ddd/Main.java

#### 코드
- 결제 없이 배송하는 장면을 setStatus 대신 order2.ship()으로 바꾼다. 서비스 없이 주문을 직접 불러도 주문이 거절한다.
- 거절될 줄 알기에 try catch로 감싸 거절 메시지를 찍는다.

### src/ddd/Order.java

#### 코드
- 마지막 setter인 setStatus를 지운다. 이제 상태는 pay, ship, cancel로만 바뀐다.
- 지우고 나면 setStatus를 부르는 곳이 남았는지 이클립스가 빨간 줄로 알려 준다. Main을 먼저 고친 이유다.

## value-object T1

### src/ddd/Main.java

#### 코드
- 새 장면은 박민수가 가격 -15000원으로 주문하는 것이다.
- 금액이 그냥 int라서 아무도 막지 않고 음수 금액 주문: -30000원이 찍힌다. 이 장에서 고칠 문제다.

## value-object T2

### glossary.md

#### 코드
- 돈 Money 줄을 더한다. 0원 이상이라는 규칙과 금액이 같으면 같은 돈이라는 기준을 뜻에 적는다.

### src/ddd/Main.java

#### 코드
- 주문 금액이 Money가 되어 getTotalPrice().amount()로 숫자를 꺼내 찍는다.
- 음수 금액 주문을 try catch로 감싼다. 이번에는 IllegalArgumentException을 잡는다.

### src/ddd/Money.java

#### 개념 값 객체
- 값 객체(Value Object)는 식별자 없이 담긴 값으로만 알아보는 객체다. 금액이 같은 두 돈은 같은 돈이다.
- 앞 단계에서는 금액이 그냥 int라서 음수도 들어갔다. 막으려면 금액을 쓰는 모든 곳에서 같은 검사를 되풀이해야 한다.
- 돈을 객체로 만들고 만들 때 스스로 0원 이상인지 검사하면, 돈을 쓰는 어디에서도 음수 금액이 생기지 않는다.
- 엔티티와 같음의 기준이 다르다. 주문은 번호로 알아보지만, 돈에는 번호가 없고 금액이 같으면 바꿔 써도 된다.
- 그냥 int를 감싼 포장과도 다르다. 0원 이상이라는 규칙과 곱하기, 더하기 같은 돈의 일을 함께 갖는다.
- 이 예제에서는 Money가 값 객체이고, 주문의 가격과 주문 금액이 Money다.

#### 개념 불변
- 불변(Immutable)은 한 번 만든 객체의 값을 다시 바꿀 수 없다는 뜻이다.
- 돈이 바뀔 수 있으면 두 주문이 같은 돈 객체를 나눠 쓸 때 한쪽에서 바꾼 금액이 다른 주문까지 바꿔 버린다.
- 그래서 곱하기와 더하기는 원래 돈을 고치지 않고 새 돈을 만들어 돌려준다.
- 엔티티 주문은 상태가 바뀌는 것이 자연스럽지만, 값 객체는 바뀌는 대신 새것으로 갈아 끼운다.
- 이 예제에서는 times와 plus가 새 Money를 돌려주고, 주문은 그 새 돈을 칸에 넣는다.

#### 코드
- record는 칸을 final로 두고 생성자, getter, equals, hashCode, toString을 자바가 만들어 주는 클래스다. 값 객체를 짧게 쓰기 좋다.
- record의 getter는 get을 붙이지 않고 칸 이름 그대로다. 그래서 amount()로 꺼낸다.
- record가 만든 equals는 모든 칸을 비교한다. 따로 쓰지 않아도 금액이 같으면 같은 돈이다.
- public Money { } 는 간결한 생성자다. 괄호와 칸 대입을 생략하고 검사만 쓴다. 칸 대입은 검사가 끝난 뒤 자바가 한다.
- IllegalArgumentException은 넘긴 값 자체가 잘못되었다는 뜻의 예외다. 상태 때문에 거절하는 IllegalStateException과 구분한다.
- times(int count)는 수량을 곱한 새 돈, plus(Money other)는 더한 새 돈을 돌려준다. new Money를 거치므로 결과도 다시 검사된다.
- 이클립스에서는 New, Record로 record 파일을 만든다.

### src/ddd/Order.java

#### 코드
- price와 totalPrice 칸, 생성자 인자, getter의 타입을 int에서 Money로 바꾼다.
- 주문 금액은 price * quantity 대신 price.times(quantity)로 돈에게 계산을 시킨다.

### src/ddd/OrderService.java

#### 코드
- placeOrder가 받는 값은 그대로 int 가격이다. 주문을 만들 때 new Money(price)로 돈을 만들어 넘긴다. 음수면 여기서 거절된다.

## value-object T3

### glossary.md

#### 코드
- 주소 Address 줄을 더한다. 비어 있을 수 없다는 규칙과 글자가 같으면 같은 주소라는 기준을 적는다.

### src/ddd/Address.java

#### 코드
- Money와 같은 모양의 값 객체다. 배송 주소도 그냥 글자가 아니라 규칙을 가진 업무의 값이다.
- value == null || value.isBlank()로 없거나 비었거나 공백뿐인 주소를 거절한다. isBlank는 공백만 있는 글자도 비었다고 본다.
- null 검사를 앞에 둔다. ||는 앞이 참이면 뒤를 보지 않아서 null에 isBlank를 부르는 오류가 나지 않는다.

### src/ddd/Main.java

#### 코드
- 새 장면은 최지우가 빈 주소로 주문하는 것이다. 주소가 거절 메시지를 낸다.

### src/ddd/Order.java

#### 코드
- address 칸, 생성자 인자, getter의 타입을 String에서 Address로 바꾼다.

### src/ddd/OrderService.java

#### 코드
- new Address(address)로 주소를 만들어 넘긴다. 빈 주소면 주문이 만들어지기 전에 거절된다.

## order-aggregate T1

### glossary.md

#### 코드
- 주문 항목 OrderLine과 항목 금액 amount 줄을 더한다.
- 주문 금액은 항목 금액의 합으로, 주문하기는 주문 항목으로 주문하는 것으로 뜻을 고친다. 모델이 바뀌면 용어집도 함께 바뀐다.

### src/ddd/Main.java

#### 코드
- 첫 주문은 자바 책 2개와 볼펜 1개, 항목 두 개로 주문한다. for 문으로 항목마다 한 줄씩 찍는다.
- List.of는 고칠 수 없는 목록을 한 줄로 만든다. 항목을 넘길 때만 쓰는 목록이라 이것으로 충분하다.
- java.util 패키지의 List를 쓰므로 import java.util.List가 필요하다. Ctrl+Shift+O를 누르면 이클립스가 import를 채운다.
- 앞 장의 두 번째부터 네 번째 주문은 항목 하나짜리 목록으로 바꾼다. 출력은 그대로다.
- 마지막 새 장면은 꺼낸 항목 목록에 getOrderLines().add로 공책 5개를 직접 더하는 것이다.
- 항목은 3개가 되었는데 주문 금액은 31000원 그대로 찍힌다. 항목과 금액이 맞지 않는 이 장의 문제다.

### src/ddd/Order.java

#### 개념 애그리거트와 루트
- 애그리거트(Aggregate)는 함께 바뀌어야 하는 객체들을 한 묶음으로 보는 단위다. 묶음 밖에서는 이 묶음을 하나처럼 다룬다.
- 루트(Aggregate Root)는 그 묶음의 대표 엔티티다. 밖에서는 루트만 부를 수 있고, 안쪽 객체는 루트를 거쳐서만 바뀐다.
- 주문 하나에 항목이 여러 개가 되면, 항목 목록과 주문 금액이 늘 함께 맞아야 한다. 이 단계 코드처럼 목록을 그대로 내주면 밖에서 항목만 더해 합계가 틀어진다.
- 앞 장의 엔티티 행동 메서드는 자기 칸 하나의 규칙을 지켰다. 루트는 그보다 넓게 안쪽 객체들까지 묶어 서로 맞는지 지킨다.
- 애그리거트 안쪽 객체는 업무 영역을 나누는 하위 도메인과 다르다. 안쪽 객체는 한 묶음 안의 부품이고, 하위 도메인은 뒤 장에서 배울 주문, 배송 같은 업무 영역이다.
- 이 예제에서는 주문과 주문 항목이 한 애그리거트이고, Order가 루트, OrderLine이 안쪽 객체다.

#### 코드
- productName, price, quantity 칸을 지우고 List<OrderLine> orderLines 칸을 둔다. 상품 하나의 값은 이제 주문 항목이 갖는다.
- new ArrayList<>(orderLines)로 받은 목록을 새 목록에 옮겨 담는다. 넘긴 쪽 목록과 끊어지고, 뒤에서 항목을 더할 수 있는 목록이 된다.
- 주문 금액은 new Money(0)에서 시작해 항목마다 plus로 더한다. 돈이 불변이라 plus가 돌려준 새 돈을 다시 칸에 넣는다.
- getOrderLines()는 안쪽 목록을 그대로 돌려준다. 밖에서 이 목록에 add 하면 주문 안 목록이 바뀐다. 다음 단계에서 막을 길이다.
- java.util의 ArrayList와 List를 import한다.

### src/ddd/OrderLine.java

#### 코드
- 주문 항목은 record다. 상품 이름, 가격, 수량을 묶은 값이고 따로 번호로 알아볼 일이 없다.
- amount()는 가격에 수량을 곱한 항목 금액이다. 계산은 앞 장의 Money.times에게 시킨다.
- 출력에서 orderLine.amount().amount()처럼 두 번 부르는 것은 항목 금액 Money를 받은 뒤 그 안의 숫자를 꺼내기 때문이다.

### src/ddd/OrderService.java

#### 코드
- placeOrder는 상품 이름, 가격, 수량 대신 주문 항목 목록을 받는다. 가격을 Money로 바꾸는 일은 이제 항목을 만드는 쪽이 한다.

## order-aggregate T2

### glossary.md

#### 코드
- 주문 항목 더하기 addOrderLine 줄을 더한다. 주문 금액도 함께 바뀐다는 것까지 뜻에 적는다.

### src/ddd/Main.java

#### 코드
- 문제 장면의 getOrderLines().add를 order5.addOrderLine으로 바꾼다. 이제 항목 더하기: 항목 3개, 주문 금액 41000원이 찍힌다.
- 새 장면은 결제한 주문에 항목을 더해 보는 것이다. 주문이 거절 메시지를 낸다.

### src/ddd/Order.java

#### 개념 불변식
- 불변식(Invariant)은 객체가 어떤 순간에도 늘 참이어야 하는 조건이다.
- 주문의 불변식은 주문 금액이 항목 금액의 합과 같다는 것, 그리고 주문 접수인 주문에만 항목이 더해진다는 것이다.
- 앞 단계에서는 목록을 밖에 내주어 이 조건이 깨졌다. 지키는 방법은 목록을 바꾸는 길을 루트 한 곳으로 모으는 것이다.
- 앞 장의 불변과 이름이 비슷하지만 다르다. 불변은 값이 안 바뀌는 성질이고, 불변식은 값이 바뀌어도 지켜져야 하는 조건이다.
- 이 예제에서는 addOrderLine이 상태를 검사하고, 목록에 더하는 일과 금액을 더하는 일을 한 메서드에서 함께 한다.

#### 코드
- getOrderLines()는 List.copyOf(orderLines)를 돌려준다. List.copyOf는 고칠 수 없는 복사본을 만든다.
- 꺼낸 목록에 add 하면 UnsupportedOperationException이 난다. 복사본이라 주문 안 목록에는 닿지도 않는다.
- addOrderLine은 상태가 주문 접수가 아니면 거절하고, 맞으면 항목과 주문 금액을 함께 바꾼다.

## order-aggregate T3

### src/ddd/Main.java

#### 코드
- 새 장면은 항목 없이 주문하는 것과 수량 0개 항목으로 주문하는 것이다. 둘 다 거절 메시지가 찍힌다.
- List.of()는 빈 목록이다.

### src/ddd/Order.java

#### 코드
- 생성자 첫 줄에서 항목 목록이 비었으면 거절한다. 항목이 없는 주문은 주문이 아니므로 만들어지는 순간부터 불변식을 지킨다.
- 검사를 칸을 채우기 전에 둔다. 잘못된 주문은 반쯤 만들어지지도 않는다.

### src/ddd/OrderLine.java

#### 코드
- record에 간결한 생성자를 더해 수량이 1 미만이면 거절한다. 수량 규칙은 주문 항목 한 개의 규칙이라 항목이 스스로 지킨다.
- 항목 하나로 판단할 수 있는 규칙은 항목에, 항목 여러 개를 함께 봐야 하는 규칙은 루트인 주문에 둔다.

## order-status T1

### glossary.md

- 주문 상태 OrderStatus 줄을 더하고, 상태 네 줄의 코드 이름을 글자에서 ORDERED, PAID, SHIPPING, CANCELED로 바꾼다
- 업무 말은 그대로 두고 코드 이름만 enum 상수로 맞춘다. 화면에 보이는 말과 코드의 이름이 따로 있다는 것이 용어집에 드러난다

### src/ddd/Main.java

- getStatus()가 이제 글자가 아니라 주문 상태를 돌려주므로 찍는 곳마다 .label()을 붙인다
- 출력 글자는 앞 장과 같다. 바뀐 것은 상태를 담는 그릇뿐이다

### src/ddd/Order.java

- status 칸의 타입을 String에서 OrderStatus로 바꾼다. 이제 "주문 접숫"처럼 오타 난 글자는 상태가 될 수 없고 컴파일에서 막힌다
- 생성자의 처음 상태는 OrderStatus.ORDERED다
- 글자 비교 equals를 enum 비교 ==, !=로 바꾼다. enum 상수는 프로그램에 하나씩만 있어서 ==로 비교해도 된다
- addOrderLine은 상태를 바꾸는 일이 아니라서 status != OrderStatus.ORDERED 비교를 그대로 둔다

### src/ddd/OrderStatus.java

- 이클립스에서 패키지를 오른쪽 클릭하고 New, Enum으로 만든다
- enum은 값이 정해진 몇 개뿐인 타입이다. 주문 상태는 다섯 가지 밖으로 나가지 않으므로 enum이 맞다
- ORDERED("주문 접수")처럼 상수 뒤 괄호의 값은 enum 생성자로 들어간다. 상수마다 업무 말을 하나씩 붙인다
- enum 생성자는 private이 기본이라 접근 제한자를 쓰지 않는다. 밖에서 new OrderStatus(...)로 새 상태를 만들 수 없다
- label()은 화면에 보여 줄 업무 말을 돌려준다. 코드 이름 ORDERED와 업무 말 주문 접수를 나눠 둔다

## order-status T2

### src/ddd/Main.java

- 이미 결제한 order5를 다시 결제하는 장면을 더한다. 이전에는 아무 말 없이 결제 완료로 남았는데 이제 거절 말이 나온다
- 이 단계부터 결제 없이 배송 줄의 말이 주문 접수인 주문은 배송할 수 없습니다로 바뀐다

### src/ddd/Order.java

- pay, ship, cancel의 첫 줄이 모두 같은 모양이 된다. status.canChangeTo(다음 상태)를 묻고, 안 되면 거절한다
- 거절 말에 status.label()을 넣어 지금 상태를 알려 준다. 메서드마다 따로 쓰던 말이 한 모양으로 모인다
- 이전 pay에는 확인이 없었다. 규칙을 한곳에 모으자 빠져 있던 두 번 결제 막기가 저절로 생긴다

### src/ddd/OrderStatus.java

#### 개념 상태 전이
- 상태 전이(State Transition)는 지금 상태에서 갈 수 있는 다음 상태가 정해져 있다는 규칙이다
- 앞 장까지는 배송은 결제 완료인지, 취소는 배송 중이 아닌지를 메서드마다 따로 확인했다
- 그래서 결제에는 확인이 아예 없었고, 결제 완료인 주문을 또 결제해도 막히지 않았다
- 규칙을 상태 쪽 한 표에 모으면 어느 화살표가 빠졌는지 한눈에 보인다
- 행동 메서드와 다르다. 행동 메서드는 결제하라는 일을 받는 입구이고, 상태 전이는 그 일이 지금 상태에서 허락되는지 답하는 표다
- 불변식과도 다르다. 불변식은 주문 금액이 항목 합과 같다처럼 늘 참이어야 하는 것이고, 상태 전이는 바뀌는 순간에만 따지는 규칙이다
- 이 예제에서는 canChangeTo의 switch가 상태 전이 표다

#### 코드
- canChangeTo(OrderStatus next)는 지금 상태 this에서 next로 갈 수 있는지 참 거짓으로 답한다
- return switch (this) { ... }는 switch 식이다. 경우마다 값을 하나 돌려주고, 그 값을 바로 return 한다
- case ORDERED -> ...의 화살표 모양은 break가 필요 없다. 아래 경우로 흘러 내려가지 않는다
- case SHIPPING, CANCELED -> false처럼 쉼표로 여러 상수를 한 경우에 묶는다
- enum을 switch 식으로 돌리면 상수를 하나라도 빠뜨렸을 때 컴파일 오류가 난다. 새 상태를 더하면 규칙도 꼭 정하게 된다
- switch 안에서는 OrderStatus.PAID 대신 PAID라고만 쓴다. 같은 enum 안이라 이름만으로 부른다

## order-status T3

### glossary.md

- 배송 완료하기 deliver와 배송 완료 DELIVERED 줄을 더한다. 배송 완료는 더 바뀌지 않는 마지막 상태라고 적는다

### src/ddd/Main.java

- 첫 주문을 배송 완료하는 장면을 더한다. 배송 중에서 배송 완료로 가는 새 화살표를 보여 준다

### src/ddd/Order.java

- deliver()는 pay, ship, cancel과 똑같은 모양이다. 다음 상태만 DELIVERED이고 거절 말은 배송 완료할이다

### src/ddd/OrderService.java

- deliver(Order order)는 주문에게 deliver를 시키기만 한다. 규칙은 주문과 주문 상태에 있다

### src/ddd/OrderStatus.java

- DELIVERED("배송 완료")를 SHIPPING과 CANCELED 사이에 더한다. 상수 순서는 업무가 흘러가는 순서다
- 상수를 더하면 switch 식이 컴파일 오류를 낸다. 그래서 case SHIPPING -> next == DELIVERED와 case DELIVERED, CANCELED -> false로 규칙을 함께 고친다
- 새 상태 하나에 고칠 곳이 상수 한 줄, 규칙 한 줄이다. 규칙을 모아 둔 덕이다

## order-repository T1

### glossary.md

- 주문 저장소, 저장하기, 주문 번호로 찾기 세 줄을 더한다. 저장소도 업무 말로 부른다

### src/ddd/Main.java

- new OrderService(new MemoryOrderRepository())로 서비스에 저장소를 넣어 만든다
- placeOrder가 주문 대신 주문 번호 long을 돌려준다. Main은 번호만 들고 다닌다
- 결제, 배송, 취소를 번호로 부르고, 상태를 볼 때는 orderService.findOrder(번호)로 다시 꺼낸다

### src/ddd/MemoryOrderRepository.java

- implements OrderRepository로 주문 저장소 약속을 지키는 메모리 구현이다
- Map<Long, Order>에 주문 번호를 키로 주문을 넣는다. 번호로 바로 찾기 위해서다
- 키 타입은 long이 아니라 Long이다. 컬렉션에는 기본형을 못 넣어서 감싼 타입을 쓴다
- orders.put은 같은 번호가 있으면 새 주문으로 바꾼다. 그래서 저장하기 하나로 새로 넣기와 고친 것 저장하기를 함께 한다
- Optional.ofNullable(orders.get(id))는 없는 번호면 null 대신 빈 Optional을 돌려준다
- @Override는 인터페이스의 메서드를 구현한다는 표시다. 이름을 틀리면 컴파일러가 알려 준다

### src/ddd/OrderRepository.java

#### 개념 리포지터리
- 리포지터리(Repository)는 애그리거트를 넣어 두고 식별자로 다시 꺼내는 곳이다
- 앞 장까지 Main은 주문 객체를 변수로 들고 다녔다. 프로그램이 주문을 잊지 않으려면 누군가 계속 쥐고 있어야 했다
- 실제 쇼핑몰은 주문 번호만 받고 주문을 다시 찾아 일을 시킨다. 그 찾는 곳이 리포지터리다
- 테이블마다 만드는 DAO와 다르다. 리포지터리는 애그리거트 루트마다 하나다. 주문 항목 저장소는 따로 없고 주문을 통째로 넣고 꺼낸다
- 그냥 컬렉션과도 다르다. 어디에 어떻게 두는지는 숨기고 저장하기, 번호로 찾기라는 약속만 보여 준다
- 이 예제에서는 OrderRepository 인터페이스가 약속이고 MemoryOrderRepository가 메모리에 두는 구현이다

#### 코드
- 이클립스에서 New, Interface로 만든다. 몸통 없는 메서드 두 개만 쓴다
- findById는 Order가 아니라 Optional<Order>를 돌려준다. 없는 번호일 수 있다는 것을 타입으로 알린다
- 인터페이스로 두면 서비스는 주문이 메모리에 있는지 DB에 있는지 몰라도 된다. 구현은 바꿔 끼울 수 있다

### src/ddd/OrderService.java

- private final OrderRepository orderRepository는 생성자로 받는다. 서비스가 저장소를 직접 new 하지 않아서 Main이 어떤 구현을 줄지 고른다
- 칸 타입은 구현 MemoryOrderRepository가 아니라 인터페이스 OrderRepository다
- placeOrder는 주문을 만들고 저장한 뒤 번호를 돌려준다
- findOrder는 orderRepository.findById(orderId).orElseThrow()로 꺼낸다. Optional이 비었으면 예외를 던진다
- pay, ship, deliver, cancel, addOrderLine이 모두 꺼내기, 주문에게 시키기, 다시 저장하기 세 줄이다
- 시킨 뒤 저장하는 줄을 빼먹으면 안 된다. 진짜 DB라면 바뀐 것이 남지 않는다. 이 점은 T3에서 눈으로 본다

## order-repository T2

### src/ddd/Main.java

- 없는 번호 100으로 결제하는 장면을 더한다. 번호는 밖에서 들어오므로 틀릴 수 있다

### src/ddd/OrderService.java

- orElseThrow(() -> new IllegalArgumentException("주문이 없습니다"))는 비었을 때 던질 예외를 람다로 넘긴다
- 람다로 넘기는 이유는 예외 객체를 없을 때만 만들기 위해서다
- 인자 없는 orElseThrow는 NoSuchElementException을 던진다. 업무 말이 아니라서 무엇이 틀렸는지 모른다

## order-repository T3

### src/ddd/Address.java

- 붙여넣기 단계다. Address, Money, OrderLine, Order 네 파일에 implements Serializable을 붙인다
- Serializable은 객체를 바이트로 바꿀 수 있다는 표시다. 메서드는 없다
- 주문을 바이트로 바꾸려면 주문이 담은 주소, 돈, 주문 항목도 모두 바꿀 수 있어야 한다. 그래서 네 파일 모두에 붙인다

### src/ddd/Main.java

- 저장 안 한 취소 장면을 더한다. 꺼낸 주문을 취소만 하고 저장하지 않으면 다시 꺼냈을 때 주문 접수 그대로다
- 진짜 DB처럼 저장해야 바뀐 것이 남는다는 것을 보여 준다

### src/ddd/MemoryOrderRepository.java

- 붙여넣기 덩어리다. copy는 주문을 바이트로 썼다가 다시 읽어 똑같은 새 주문 객체를 만든다
- save는 복사본을 넣고 findById는 복사본을 꺼낸다. 저장소 안의 주문과 밖의 주문이 서로 다른 객체가 된다
- 복사하지 않으면 꺼낸 주문과 저장소 안 주문이 같은 객체라 save를 빼먹어도 바뀐 것이 남는다. 그러면 저장 빠뜨린 버그가 숨는다
- findById는 없으면 Optional.empty(), 있으면 Optional.of(copy(order))로 나눈다. null을 copy에 넣지 않기 위해서다
- catch (IOException | ClassNotFoundException e)는 두 예외를 한 catch로 받는다. 메모리 복사라 실제로는 나지 않아 IllegalStateException으로 감싼다

### src/ddd/Order.java

- private static final long serialVersionUID = 1L은 직렬화한 클래스의 판 번호다. 적지 않으면 이클립스가 노란 경고를 띄워서 적는다
- record인 Address, Money, OrderLine에는 적지 않아도 경고가 없다

## place-order T1

### src/ddd/Main.java

- 주문 항목을 OrderLine 대신 OrderLineCommand로 만든다. 가격은 new Money(15000)가 아니라 그냥 15000이다
- 모든 주문하기가 new PlaceOrderCommand(이름, 항목 명령 목록, 주소) 하나를 넘긴다
- Main은 이제 돈, 주소, 주문 항목 같은 도메인 객체를 만들지 않는다. 밖에서 들어온 글자와 숫자만 넘긴다
- 출력은 앞 장과 같다. 음수 가격, 빈 주소 같은 틀린 값도 도메인이 앞 장과 같은 말로 거절한다

### src/ddd/OrderLineCommand.java

- 이클립스에서 New, Record로 만든다. 몸통은 비어 있다
- 명령(Command)은 유스케이스에 넣는 값 묶음이다. 규칙도 메서드도 없이 값만 나른다
- price가 Money가 아니라 int다. 명령은 밖에서 온 모양 그대로 두고, 돈으로 바꾸는 일은 응용 서비스가 한다
- 주문 항목 OrderLine과 이름이 비슷하지만 다르다. OrderLine은 수량 1개 이상이라는 규칙을 가진 도메인 객체이고, 명령은 아직 검사 전의 입력이다

### src/ddd/OrderService.java

#### 개념 응용 서비스
- 응용 서비스(Application Service)는 유스케이스 하나를 처음부터 끝까지 순서대로 진행하는 클래스다
- 하는 일은 셋뿐이다. 저장소에서 꺼내고, 도메인 객체에게 시키고, 저장한다. 업무 규칙은 갖지 않는다
- 앞 장의 OrderService는 이미 이 모양이었지만 밖에서 도메인 객체를 받았다. 그래서 Main이 돈과 주소를 직접 만들어야 했다
- 이제 명령을 받아 도메인 객체로 바꾸는 일까지 응용 서비스가 한다. 유스케이스의 입구가 한 곳이 된다
- 엔티티의 행동 메서드와 다르다. 결제할 수 있는지는 주문이 판단하고, 응용 서비스는 결제하라고 시키기만 한다
- 애그리거트 루트와도 다르다. 루트는 주문 항목과 주문 금액이 서로 맞게 지키고, 응용 서비스는 루트를 꺼내고 저장하는 바깥 일만 한다
- 도메인 서비스(Domain Service)와도 다르다. 도메인 서비스는 한 애그리거트에 넣기 어색한 규칙을 계산만 하고, 꺼내기와 저장하기는 하지 않는다. discount 장에서 나온다
- 이 예제에서는 placeOrder가 명령을 주문 항목과 주소로 바꿔 주문을 만들고 저장하는 것이 응용 서비스의 일이다

#### 코드
- placeOrder(PlaceOrderCommand command)는 값 세 개 대신 명령 하나를 받는다. 유스케이스의 입력이 한눈에 보인다
- command.orderLines(), command.customerName()처럼 record의 칸 이름 메서드로 값을 꺼낸다
- for 문으로 주문 항목 명령을 하나씩 주문 항목으로 바꿔 새 ArrayList에 담는다
- private toOrderLine은 주문 항목 명령을 주문 항목으로 바꾼다. 주문하기와 주문 항목 더하기가 같은 바꾸기를 써서 나눈다
- 값을 검사하는 if는 없다. 틀린 값은 Money, Address, OrderLine, Order가 각자 거절한다. 검사를 여기에 또 쓰면 규칙이 두 곳에 생긴다

### src/ddd/PlaceOrderCommand.java

- 주문하기 명령은 주문자, 주문 항목 명령 목록, 배송 주소를 담는다
- 목록 칸도 List<OrderLine>이 아니라 List<OrderLineCommand>다. 명령 안에는 도메인 객체를 담지 않는다

## place-order T2

### glossary.md

- 주문 응용 서비스, 주문하기 명령, 주문 항목 명령 세 줄을 더한다. 응용 서비스 뜻에 규칙은 갖지 않는다고 적는다

### src/ddd/Main.java

- 타입과 생성자 이름만 OrderApplicationService로 바뀐다. 변수 이름 orderService는 그대로 둔다

### src/ddd/OrderApplicationService.java

- OrderService.java를 지우고 같은 내용의 새 파일을 만드는 대신 이클립스의 Refactor, Rename(Alt+Shift+R)을 쓴다
- Rename은 파일 이름, 클래스 이름, 생성자 이름, Main의 쓰는 곳을 한 번에 바꾼다
- 이름만 바꾸고 내용은 그대로다. 서비스라는 이름만으로는 뒤에 올 도메인 서비스와 구분되지 않아서 응용을 붙인다

## layers T1

### src/ddd/Main.java

- ddd.domain의 Order, OrderLine을 import 한다. 다른 패키지로 옮긴 클래스는 import가 있어야 쓴다
- import 줄은 이클립스가 넣는 모양 그대로다. java 묶음 아래에 빈 줄을 두고 ddd 묶음을 적는다. Ctrl+Shift+O로 한 번에 정리할 수 있다

### src/ddd/MemoryOrderRepository.java

- ddd.domain의 Order, OrderRepository를 import 한다. 아직 ddd 패키지에 남아 있고 T3에서 옮긴다

### src/ddd/OrderApplicationService.java

- 쓰는 도메인 클래스 다섯 개를 import 한다. 코드 내용은 바뀌지 않는다

### src/ddd/domain/Order.java

#### 개념 계층 구조
- 계층 구조(Layered Architecture)는 코드를 맡은 일에 따라 층으로 나누고, 위층이 아래층만 쓰게 하는 구조다
- 지금까지는 모든 클래스가 ddd 패키지 하나에 섞여 있었다. 어느 것이 업무 규칙이고 어느 것이 저장 방법인지 이름으로만 짐작해야 했다
- 이 장의 층은 셋이다. 도메인 계층은 업무 규칙, 응용 계층은 유스케이스 순서, 인프라 계층은 저장 같은 기술을 맡는다
- 도메인 계층은 다른 층을 모른다. 그래서 저장 방법이나 유스케이스가 바뀌어도 업무 규칙은 그대로다
- 하위 도메인과 다르다. 하위 도메인은 주문, 배송처럼 업무 영역으로 나누는 것이고, 계층은 한 영역 안을 하는 일의 종류로 나누는 것이다
- 이 예제에서는 ddd.domain, ddd.application, ddd.infrastructure 세 패키지가 세 층이고, 이 단계에서 도메인 계층부터 만든다

#### 코드
- 패키지 탐색기에서 src를 오른쪽 클릭하고 New, Package로 ddd.domain을 만든다
- 옮길 클래스 여섯 개를 골라 ddd.domain으로 끌어 놓거나 Refactor, Move(Alt+Shift+V)를 쓴다
- 이클립스가 첫 줄을 package ddd.domain;으로 바꾸고 쓰는 쪽의 import도 함께 넣는다
- 여섯 클래스는 서로만 쓰므로 안에 import가 새로 생기지 않는다. 도메인이 바깥을 모른다는 표시다

### src/ddd/domain/OrderRepository.java

- 주문 저장소 인터페이스는 도메인 계층에 둔다. 주문을 저장하고 번호로 꺼낸다는 약속은 도메인이 정하는 것이기 때문이다
- 구현인 MemoryOrderRepository는 도메인에 두지 않는다. T3에서 인프라 계층으로 간다

## layers T2

### src/ddd/Main.java

- ddd.application의 세 클래스를 import 한다

### src/ddd/application/OrderApplicationService.java

- New, Package로 ddd.application을 만들고 응용 서비스와 명령 두 개를 함께 옮긴다
- 응용 서비스와 명령이 같은 패키지라서 명령 import는 생기지 않는다
- ddd.domain import는 그대로다. 응용 계층이 도메인 계층을 쓰는 것은 아래층을 쓰는 방향이라 괜찮다

## layers T3

### src/ddd/Main.java

- ddd.infrastructure의 MemoryOrderRepository를 import 한다
- 세 계층을 모두 import 하는 곳은 Main 하나다. new OrderApplicationService(new MemoryOrderRepository())로 응용과 인프라를 잇는 일도 Main만 한다

### src/ddd/infrastructure/MemoryOrderRepository.java

#### 개념 의존성 역전
- 의존성 역전(Dependency Inversion Principle)은 규칙을 가진 쪽이 약속을 정하고, 기술을 가진 쪽이 그 약속을 구현하게 하는 것이다
- 보통 순서대로 짜면 업무 코드가 저장 코드를 부른다. 그러면 저장 방법을 바꿀 때 업무 코드까지 고쳐야 한다
- 여기서는 도메인 계층이 주문 저장소 인터페이스를 갖고, 인프라 계층이 implements OrderRepository로 따른다
- 그래서 import 화살표가 인프라에서 도메인으로 향한다. 도메인과 응용 계층에는 ddd.infrastructure import가 하나도 없다
- 그냥 인터페이스를 쓰는 것과 다르다. 인터페이스를 어느 계층에 두느냐가 핵심이다. 인터페이스가 인프라에 있으면 도메인이 여전히 인프라를 알아야 한다
- 이 예제에서는 저장소를 DB로 바꿔도 인프라 계층에 새 구현을 더하고 Main의 new 한 줄만 바꾸면 된다

#### 코드
- New, Package로 ddd.infrastructure를 만들고 이 파일을 옮긴다. 코드에서 바뀌는 것은 package 줄뿐이다

## id-reference T1

### glossary.md

- 회원, 회원 저장소, 상품, 상품 저장소 네 줄을 더한다. 번호가 같으면 같은 회원, 같은 상품이라고 적는다

### src/ddd/Main.java

- 주문 전에 회원 둘, 상품 셋을 저장소에 넣는다
- 변수 타입은 MemberRepository, ProductRepository 인터페이스다. 구현 이름은 new 뒤에만 나온다

### src/ddd/domain/Member.java

- 회원은 주문과 같은 엔티티다. 번호를 갖고 equals와 hashCode를 번호로 비교한다
- 회원은 주문 안에 들어가지 않는 따로 된 애그리거트다. 주문과 따로 만들어지고 따로 바뀌어서 자기 저장소를 갖는다
- 칸이 final이고 getter만 있다. 이 장에는 회원을 고치는 동작이 없다
- Long.hashCode(id)는 long 번호로 해시값을 만든다. equals가 번호로 같으면 hashCode도 같아야 Map과 Set에서 맞게 찾는다

### src/ddd/domain/MemberRepository.java

- 주문 저장소와 같은 모양이다. 애그리거트마다 저장소가 하나씩 생긴다

### src/ddd/domain/Product.java

- 상품도 번호로 비교하는 엔티티이자 따로 된 애그리거트다. 가격은 돈 Money다

### src/ddd/domain/ProductRepository.java

- 상품 저장소 약속도 도메인 계층에 둔다. layers 장에서 정한 자리다

### src/ddd/infrastructure/MemoryMemberRepository.java

- 메모리 회원 저장소는 인프라 계층에 두고 도메인의 회원 저장소를 구현한다
- 주문 저장소처럼 복사하지 않는다. 회원에는 바꾸는 메서드가 없어서 꺼낸 것을 고칠 수 없다
- 가격이 바뀐 상품은 같은 번호로 다시 저장한다. put이 같은 키를 덮어써서 새 상품으로 바뀐다

## id-reference T2

### glossary.md

- 주문자 customerName 줄이 회원 번호 memberId 줄로 바뀌고 상품 번호 productId 줄이 더해진다
- 주문 항목 뜻에 주문 때의 상품 이름, 가격이라는 말이 들어간다

### src/ddd/Main.java

- 주문하기 명령에 이름 대신 회원 번호를, 주문 항목 명령 맨 앞에 상품 번호를 넣는다
- 주문에는 이름이 없으므로 memberRepository.findById(order.getMemberId())로 회원을 찾아 이름을 찍는다
- 주문을 보여 주는 쪽이 필요한 애그리거트를 번호로 따로 꺼내 모은다. 출력은 앞 장과 같다

### src/ddd/application/OrderApplicationService.java

- 주문을 만들 때 command.memberId()를, 주문 항목을 만들 때 command.productId()를 넘긴다. 응용 서비스는 여전히 주문 저장소 하나만 받는다

### src/ddd/application/OrderLineCommand.java

- 상품 번호 productId를 맨 앞에 더한다. 상품 이름과 가격은 아직 명령으로 받는다. 상품 저장소에서 꺼내 채우는 일은 order-factory 장이다

### src/ddd/application/PlaceOrderCommand.java

- 주문자 이름 customerName이 회원 번호 memberId로 바뀐다

### src/ddd/domain/Order.java

#### 개념 애그리거트 간 ID 참조
- 애그리거트 간 ID 참조는 한 애그리거트가 다른 애그리거트를 객체로 쥐지 않고 번호만 들고 있는 것이다
- 주문이 Member 객체를 칸으로 쥐면 주문을 꺼낼 때 회원까지 딸려 오고, 주문을 저장할 때 회원도 함께 저장될 수 있다
- 그러면 주문 애그리거트의 경계가 회원까지 번져 어디까지가 한 번에 꺼내고 저장하는 단위인지 흐려진다
- 애그리거트 안쪽 객체와 다르다. 주문 항목은 주문 안에 들어가 주문과 함께 저장되고, 회원과 상품은 밖에 있어서 번호로만 가리킨다
- 회원 정보가 필요하면 쓰는 쪽이 회원 저장소에서 번호로 따로 꺼낸다
- 이 예제에서는 주문의 memberId와 주문 항목의 productId가 ID 참조다. 주문 코드에는 Member와 Product import가 없다

#### 코드
- customerName 칸과 getCustomerName을 memberId 칸과 getMemberId로 바꾼다
- 이클립스의 Refactor, Rename(Alt+Shift+R)으로 칸 이름을 바꾸면 쓰는 곳이 함께 바뀐다. 타입이 String에서 long으로 바뀌는 것은 손으로 고친다

### src/ddd/domain/OrderLine.java

- record 칸 맨 앞에 long productId를 더한다. 상품은 객체가 아니라 번호로 가리킨다
- productName과 price는 그대로 둔다. 상품을 가리키는 번호와 별도로 주문 때의 이름과 가격을 복사해 담는다
- 상품 가격은 나중에 바뀔 수 있지만 이미 한 주문의 가격은 바뀌면 안 된다. 그래서 가격은 상품에서 매번 읽지 않고 주문 항목이 들고 있다

## id-reference T3

### src/ddd/Main.java

- 1번 상품 자바 책을 18000원으로 다시 저장한 뒤 첫 주문을 꺼내 비교한다
- 상품은 18000원이지만 주문 항목은 15000원, 주문 금액은 31000원 그대로다. 주문 항목이 주문 때 가격을 담아 두었기 때문이다
- firstOrder.getOrderLines().get(0)으로 첫 주문 항목을 꺼내고, 그 항목의 productId로 상품 저장소에서 지금 상품을 찾는다

## order-factory T1

### src/ddd/Main.java

- 주문 항목 팩토리를 상품 저장소와 함께 만들어 주문 응용 서비스에 넘긴다. 팩토리가 상품을 찾을 곳이 이 저장소다
- 주문 항목 명령에는 이제 상품 번호와 수량만 넣는다. 상품 이름과 가격은 팩토리가 상품에서 가져온다
- 음수 금액 주문 장면은 명령에 가격을 넣을 수 없어서 음수 가격 상품을 만드는 모양으로 바뀐다. new Money(-15000)에서 돈이 먼저 거절해서 출력 글자는 그대로다

### src/ddd/application/OrderApplicationService.java

- 생성자가 주문 저장소와 함께 주문 항목 팩토리를 받는다. 응용 서비스는 팩토리를 만들지 않고 받아서 쓴다
- 주문하기와 주문 항목 더하기 모두 orderLineFactory.create로 주문 항목을 만든다. 만드는 방법이 한 곳에 모인다
- 앞 장의 toOrderLine 메서드와 Money import가 빠진다. 명령의 숫자로 돈과 주문 항목을 조립하던 일이 응용 서비스에서 사라진다

### src/ddd/application/OrderLineCommand.java

- record 칸이 상품 번호와 수량 둘로 줄어든다
- 밖에서 가격을 넣을 수 없으니 주문하는 사람이 가격을 마음대로 정할 수 없다

### src/ddd/domain/OrderLineFactory.java

#### 개념 팩토리(Factory)
- 팩토리는 객체를 만드는 일이 복잡할 때 그 만드는 일만 맡는 도메인 객체다
- 앞 장에서는 응용 서비스가 명령의 이름과 가격 숫자로 주문 항목을 조립해서, 주문하는 쪽이 아무 가격이나 넣을 수 있었다
- 주문 항목을 바르게 만들려면 다른 애그리거트인 상품을 저장소에서 꺼내 지금 이름과 가격을 읽어야 한다
- 생성자와 다르다. 생성자는 받은 값으로 자기 칸만 채우고 저장소를 모른다. 주문 항목 record가 저장소를 알게 되면 값 객체가 무거워진다
- 응용 서비스와도 다르다. 상품의 지금 가격으로 주문 항목을 만든다는 것은 업무 규칙이라 도메인 계층에 둔다
- 이 예제에서는 OrderLineFactory가 상품 번호와 수량을 받아 상품을 찾고 주문 항목을 만든다

#### 코드
- 칸 타입이 MemoryProductRepository가 아니라 ProductRepository다. 도메인 계층의 약속만 알아서 인프라 계층을 모른다
- create는 상품 번호와 수량을 받아 OrderLine을 돌려준다
- product.getPrice()를 그대로 주문 항목에 담는다. 이 순간의 가격이 주문 항목에 굳어서, 나중에 상품 가격이 바뀌어도 주문은 그대로다
- 상품이 없을 때는 아직 업무 말 없이 멈춘다. 다음 단계에서 고친다

## order-factory T2

### src/ddd/Main.java

- 없는 상품 번호 100으로 주문하는 장면을 더한다. 주문 번호를 받지 못하고 상품이 없습니다가 찍힌다

### src/ddd/domain/OrderLineFactory.java

- orElseThrow에 예외를 만드는 람다를 넘겨 상품이 없습니다라는 업무 말로 거절한다
- 상품을 찾는 곳이 팩토리라서 없는 상품을 알아채는 일도 팩토리가 맡는다
- 주문 항목을 만들기 전에 거절하므로 주문은 만들어지지 않고 아무것도 저장되지 않는다

## discount T1

### src/ddd/Main.java

- 김철수를 VIP, 이영희를 일반 등급으로 저장한다. 출력은 아직 그대로다

### src/ddd/domain/Member.java

- 칸 final MemberGrade grade와 getGrade()를 더한다
- equals와 hashCode는 그대로 회원 번호만 본다. 등급이 달라도 번호가 같으면 같은 회원이다

### src/ddd/domain/MemberGrade.java

- 등급 이름 BASIC과 VIP만 가진 enum이다
- 할인율 10% 같은 숫자는 여기에 넣지 않는다. 등급은 회원이 어떤 회원인지만 말하고, 얼마를 깎을지는 다음 단계의 할인 정책이 정한다

## discount T2

### src/ddd/Main.java

- 주문 응용 서비스에 회원 저장소와 할인 정책을 더 넘긴다
- 결제 줄 다음에 할인 금액과 결제 금액을 찍는다. member.getGrade()는 enum 이름 VIP가 그대로 글자로 찍힌다

### src/ddd/application/OrderApplicationService.java

- 생성자가 회원 저장소와 할인 정책을 더 받는다
- 결제하기가 주문을 꺼내고, 주문의 회원 번호로 회원을 꺼낸다. ID로 가리키던 회원을 실제 객체로 바꾸는 일은 응용 서비스가 한다
- discountPolicy.discount(member, order)로 할인 금액을 계산하게 하고, 그 돈을 order.pay에 넘겨 결제를 시키고, 저장한다
- 10%라는 숫자도, 결제할 수 있는지 따지는 if도 여기에는 없다. 꺼내기, 시키기, 저장하기 순서만 있다
- 회원이 없으면 orElseThrow()로 업무 말 없이 멈춘다. 없는 회원 막기는 미룬 일이다

### src/ddd/domain/DiscountPolicy.java

#### 개념 도메인 서비스(Domain Service)
- 도메인 서비스는 업무 규칙인데 어느 엔티티나 애그리거트 하나에 넣기 어색한 규칙을 계산하는 도메인 객체다
- VIP는 주문 금액의 10%를 깎는다는 규칙은 회원 등급과 주문 금액을 함께 봐야 한다
- 회원에 두면 회원이 주문을 알아야 하고, 주문에 두면 주문이 회원 애그리거트를 꺼내 들고 있어야 한다. ID로만 가리키기로 한 11장 약속이 깨진다
- 그래서 둘 중 어디에도 넣지 않고 따로 이름 붙인 객체에 둔다
- 도메인 서비스는 계산만 한다. 저장소를 쓰지 않고, 받은 주문을 바꾸지 않고, 할인 금액이라는 값을 돌려줄 뿐이다
- 칸이 없어 기억하는 상태도 없다. 같은 회원과 주문을 넣으면 늘 같은 돈이 나온다
- 이 예제에서는 DiscountPolicy의 discount가 도메인 서비스다

#### 개념 규칙을 두는 네 자리
- 엔티티 행동 메서드는 자기 상태를 자기 규칙으로 바꾼다. Order.pay가 결제할 수 있는 상태인지 따지고 결제 완료가 되는 것이다
- 애그리거트 루트는 안쪽 객체들이 함께 맞도록 지킨다. Order가 주문 항목을 더할 때 주문 금액을 같이 바꾸는 것이다
- 도메인 서비스는 애그리거트 하나에 넣기 어색한 규칙을 계산만 한다. DiscountPolicy가 회원과 주문을 보고 할인 금액을 내는 것이다
- 응용 서비스는 규칙 없이 꺼내기, 시키기, 저장하기 순서만 정한다. OrderApplicationService.pay가 주문과 회원을 꺼내고 할인 정책과 주문에게 시키고 저장하는 것이다
- 도메인 서비스와 응용 서비스는 이름이 비슷하지만 다르다. 도메인 서비스는 도메인 계층에 있고 업무 규칙을 갖는다. 응용 서비스는 응용 계층에 있고 규칙을 갖지 않는다
- 구분하는 질문은 하나다. 이 규칙을 업무 사람이 말하는가, 그리고 한 애그리거트가 혼자 답할 수 있는가

#### 코드
- 칸도 생성자도 없는 클래스다. new DiscountPolicy()를 몇 번 만들어도 똑같이 동작한다
- member.getGrade() == MemberGrade.VIP처럼 enum 값은 ==로 비교한다. enum 값은 하나씩만 있어서 equals를 쓰지 않아도 된다
- order.getTotalPrice().amount() * 10 / 100은 int 계산이라 소수점 아래를 버린다. 31000원이면 3100원이다
- 돌려주는 타입이 int가 아니라 Money다. 할인 금액도 0원 이상이라는 돈의 규칙을 그대로 받는다
- order는 읽기만 한다. pay 같은 상태를 바꾸는 메서드는 부르지 않는다

### src/ddd/domain/Money.java

- minus는 plus처럼 새 Money를 돌려준다. 값 객체라서 자기 금액을 바꾸지 않는다
- 빼서 음수가 되면 Money 생성자가 금액은 0원 이상이어야 합니다로 거절한다

### src/ddd/domain/Order.java

- 칸 discountPrice를 더하고 생성자에서 0원으로 둔다. 결제 전 할인 금액은 0원이다
- pay가 할인 금액을 받는다. 상태 확인을 먼저 하고, 통과해야만 할인 금액을 기억한다. 두 번 결제하면 할인 금액도 그대로다
- 주문은 할인 금액이 어떻게 나왔는지 모른다. 회원 등급도, 10%도 주문 안에 없다
- getPaymentPrice는 칸이 아니라 주문 금액에서 할인 금액을 빼서 그때 계산한다. 결제 전에 항목을 더해 주문 금액이 바뀌어도 따로 맞출 칸이 없다

## free-shipping T1

### src/ddd/Main.java

- 할인 줄 다음에 배송비를 찍는다. 첫 주문은 결제 금액이 27900원이라 무료 배송이 아니고 3000원이다

### src/ddd/domain/FreeShippingSpecification.java

#### 개념 명세(Specification)
- 명세는 객체 하나가 어떤 조건을 만족하는지 참 거짓으로만 답하는 도메인 객체다
- 무료 배송 조건을 주문 안의 if 한 줄로 두면 그 숫자를 읽어야 무엇을 묻는지 안다. 조건이 늘면 if가 길어진다
- 조건에 업무 말로 이름을 붙여 객체로 만들면 무료 배송인가라는 질문 하나로 읽힌다
- 도메인 서비스와 다르다. 도메인 서비스는 할인 금액 같은 값을 계산해 돌려주고, 명세는 참 거짓만 돌려준다
- 그래서 0원이나 3000원 같은 돈은 명세가 아니라 질문한 쪽이 정한다
- 이 예제에서는 FreeShippingSpecification이 주문을 받아 결제 금액이 50000원 이상인지 답한다

#### 코드
- 메서드 이름 isSatisfiedBy는 만족하는가라는 뜻이다. 명세에서 흔히 쓰는 이름이다
- 칸이 없고 주문을 읽기만 한다
- 주문 금액이 아니라 할인 뒤의 결제 금액으로 따진다

### src/ddd/domain/Order.java

- getShippingFee는 무료 배송 명세에게 this, 즉 자기 자신을 넘겨 묻는다
- 명세가 참이면 0원, 거짓이면 3000원이다. 돈을 정하는 일은 주문이 한다
- 결제 금액처럼 칸으로 두지 않고 물을 때마다 계산한다. 할인이나 항목 더하기 뒤에 따로 맞출 것이 없다

## free-shipping T2

### src/ddd/Main.java

- 일반 회원이 자바 책 4권, 60000원을 서울과 제주로 주문한다. 금액은 같아도 서울은 0원, 제주는 3000원이 찍힌다

### src/ddd/domain/FreeShippingAreaSpecification.java

- 배송 주소가 제주로 시작하지 않는지 답한다. startsWith는 글자의 앞부분이 같은지 본다
- 앞의 !는 결과를 뒤집는다. 제주가 아니어야 참이다
- implements OrderSpecification으로 주문 명세 모양을 따르고 isSatisfiedBy에 @Override를 붙인다

### src/ddd/domain/FreeShippingPriceSpecification.java

- T1의 무료 배송 명세에 있던 50000원 조건을 그대로 옮겨 이름을 따로 붙인다

### src/ddd/domain/FreeShippingSpecification.java

- 이제 조건을 직접 갖지 않고 금액 명세와 지역 명세를 and로 묶어 답한다
- 주문의 getShippingFee는 그대로다. 조건이 늘어도 주문은 여전히 무료 배송 명세에게만 묻는다

### src/ddd/domain/OrderSpecification.java

#### 개념 명세 조합
- 명세가 같은 모양을 따르면 작은 명세 여러 개를 묶어 새 명세를 만들 수 있다
- 제주 조건을 금액 if 옆에 덧붙였다면 두 조건이 한 메서드에 엉킨다
- 조건마다 명세를 하나씩 두고 and로 묶으면 각 조건은 따로 읽히고 묶은 결과도 다시 명세다
- 이 예제에서는 OrderSpecification이 모양이고, 무료 배송 명세가 금액 명세 and 지역 명세다

#### 코드
- 추상 메서드가 isSatisfiedBy 하나뿐인 인터페이스다. 이런 인터페이스는 람다로 구현을 바로 만들 수 있다
- default 메서드는 인터페이스 안에서 몸통까지 가진 메서드다. 구현하는 클래스마다 and를 다시 쓰지 않아도 모두 and를 갖는다
- and는 주문을 받아 두 명세가 모두 참인지 답하는 람다를 돌려준다. 이 람다가 곧 새 OrderSpecification이다
- &&는 앞이 거짓이면 뒤를 묻지 않는다

## order-events T1

### src/ddd/Main.java

- 상품을 재고 10개로 저장한다
- 주문 뒤 이벤트 수와 자바 책 재고를 찍는다. 이벤트는 1개인데 재고는 아직 10개다. 주문이 재고를 건드리지 않았다는 뜻이다

### src/ddd/domain/Order.java

- 칸 List<Object> events를 더한다. 주문이 남길 이벤트가 하나로 정해져 있지 않아 Object로 담는다
- 생성자 끝에서 OrderPlaced를 하나 남긴다. 주문 항목은 List.copyOf로 복사해 담아서 나중에 주문이 바뀌어도 이벤트 안 내용은 그대로다
- getEvents도 List.copyOf로 돌려준다. 밖에서 이벤트 목록을 고치지 못한다
- 주문은 상품 저장소도 상품도 모른다. 재고를 줄이라고 시키지 않고, 주문 접수됨이라는 일만 남긴다

### src/ddd/domain/OrderPlaced.java

#### 개념 도메인 이벤트(Domain Event)
- 도메인 이벤트는 도메인에서 이미 일어난 일을 이름 붙은 값으로 남긴 것이다
- 주문하면 재고가 줄어야 하는데, 주문이 상품을 꺼내 재고를 줄이면 주문 애그리거트가 상품 애그리거트까지 바꾼다
- 주문은 주문 접수됨이라는 사실만 남기고, 재고는 그 사실을 들은 쪽이 따로 줄인다
- 명령과 다르다. 주문하기 명령은 해 달라는 부탁이라 거절될 수 있고, 이벤트는 이미 일어난 일이라 거절하거나 바꾸지 않는다
- 그래서 이름을 과거형으로 짓는다. OrderPlaced는 주문이 접수되었다는 뜻이다
- 이 예제에서는 주문이 만들어질 때 OrderPlaced가 주문 번호와 주문 항목을 담아 남는다

#### 코드
- record라서 만든 뒤 칸을 바꿀 수 없다. 일어난 일은 바뀌지 않는다는 뜻과 맞다
- 주문의 events 칸에 들어가서 주문 저장소가 주문을 직렬화로 복사할 때 함께 복사된다. 그래서 Serializable을 구현한다

### src/ddd/domain/Product.java

- 칸 stock은 final이 아니다. 재고는 바뀌는 값이다
- 상품을 만들 때 재고를 정하고 getStock으로 묻는다

## order-events T2

### src/ddd/Main.java

- 이벤트 발행기를 만들어 주문 응용 서비스에 넘긴다. 출력은 T1과 같다

### src/ddd/application/EventPublisher.java

- 붙여넣기 코드다. 메시지 큐 역할을 콘솔에서 흉내 낸다
- subscribe는 이벤트 종류마다 받을 곳을 적어 두고, publish는 받은 이벤트를 쌓아 두기만 하고, processEvents는 쌓인 이벤트를 받을 곳에 한 번씩 보낸다
- publish와 processEvents가 나뉘어 있어서 주문 저장과 재고 줄이기가 다른 때에 일어나는 것을 출력 한 줄씩 볼 수 있다

### src/ddd/application/OrderApplicationService.java

#### 개념 트랜잭션 경계(Transaction Boundary)
- 트랜잭션은 함께 성공하거나 함께 실패해야 하는 일 한 묶음이고, 경계는 그 묶음이 어디서 끝나는지다
- 주문 저장과 재고 줄이기를 한 메서드에서 하면 주문과 상품 두 애그리거트를 한 번에 바꾼다. 하나가 실패하면 둘 다 되돌려야 해서 묶음이 커진다
- 애그리거트 하나를 한 트랜잭션에서 바꾸는 것이 기본이다
- 이 예제에서 주문하기의 경계는 주문을 저장하는 데서 끝난다. 이벤트는 그 뒤에 발행기로 넘기기만 한다
- 재고 줄이기는 이 경계 밖에서 따로 일한다. 애그리거트 경계가 일관성을 지키는 범위였다면, 트랜잭션 경계는 한 번에 저장하는 범위다

#### 코드
- 생성자가 이벤트 발행기를 더 받는다
- orderRepository.save 다음 줄에서 eventPublisher.publish(order.getEvents())를 부른다. 저장한 주문의 일만 바깥에 알린다
- 순서가 거꾸로면 저장이 실패해도 이벤트는 나가서, 없는 주문 때문에 재고가 줄 수 있다

## order-events T3

### src/ddd/Main.java

- decreaseStockHandler::handle은 메서드 참조다. 처리기의 handle 메서드를 람다처럼 넘긴다
- OrderPlaced.class는 OrderPlaced 타입 자체를 나타내는 값이다. 이 종류의 이벤트가 오면 handle을 부르라고 구독한다
- eventPublisher.processEvents()를 부른 뒤에야 재고가 8개가 된다. 주문 뒤 줄과 이벤트 처리 뒤 줄 사이가 재고가 아직 안 맞은 때다

### src/ddd/application/DecreaseStockHandler.java

#### 개념 최종 일관성(Eventual Consistency)
- 최종 일관성은 지금 이 순간에는 데이터가 서로 안 맞아도 조금 뒤에는 반드시 맞춰지는 것이다
- 주문 저장과 재고 줄이기를 다른 트랜잭션으로 나누면 그 사이에 주문은 있는데 재고는 그대로인 때가 생긴다
- 즉시 일관성과 다르다. 애그리거트 안의 주문 금액과 주문 항목은 언제 봐도 맞아야 하지만, 주문과 재고는 잠깐 어긋나도 된다고 업무에서 정한 것이다
- 대신 어긋난 것을 이벤트로 맞추고, 맞추다 실패하면 되돌리는 방법이 따로 필요하다. 그 방법은 사가 장에서 배운다
- 이 예제에서는 재고 줄이기 처리기가 주문 접수됨 이벤트를 받아 나중에 재고를 맞춘다

#### 코드
- 응용 계층에 둔다. 이벤트를 받아 상품을 꺼내고, 상품에게 시키고, 저장하는 응용 서비스와 같은 일을 한다
- handle은 OrderPlaced를 받는다. 이벤트에 담긴 주문 항목의 상품 번호와 수량만으로 일해서 주문을 다시 꺼내지 않는다
- 상품마다 꺼내기, decreaseStock 시키기, 저장하기를 한다

### src/ddd/domain/Product.java

- decreaseStock은 재고를 수량만큼 뺀다. 재고를 바꾸는 일은 상품 자신의 행동 메서드다
- 재고가 모자란지는 아직 따지지 않는다. 주문과 재고가 따로 맞춰져서 모자란 것을 나중에야 알게 되고, 되돌리기는 사가에서 다룬다

## optimistic-lock T1

### src/ddd/Main.java

- 주문 저장소를 변수로 꺼내 둔다. 주문 응용 서비스는 일마다 꺼내고 바로 저장해서 두 곳이 같은 주문을 들고 있는 때를 보일 수 없다
- 결제 완료인 다섯째 주문을 orderA와 orderB로 두 번 꺼낸다. 저장소가 복사해서 주므로 서로 다른 객체다
- orderA로 배송하고 저장한 뒤, 아직 결제 완료라고 알고 있는 orderB로 취소하고 저장한다
- orderB의 취소는 자기 안에서는 규칙에 맞아서 통과한다. 나중 저장이 앞 저장을 덮어 배송 중인 주문이 취소가 된다

## optimistic-lock T2

### src/ddd/domain/Order.java

#### 개념 낙관적 잠금(Optimistic Lock)
- 낙관적 잠금은 꺼낼 때는 잠그지 않고, 저장할 때 그사이 누가 먼저 바꿨는지 확인해 낡은 저장을 거절하는 방법이다
- 앞 단계처럼 같은 주문을 두 곳에서 들고 있으면 나중에 저장한 쪽이 먼저 저장한 내용을 말없이 지운다
- 주문의 규칙은 주문 객체 하나 안에서만 지켜진다. 두 복사본이 서로를 모르면 애그리거트도 이 덮어쓰기를 막지 못한다
- 비관적 잠금과 다르다. 비관적 잠금은 꺼낼 때 잠가서 다른 쪽이 기다리게 하고, 낙관적 잠금은 충돌이 드물다고 보고 저장할 때만 비교한다
- 비교에 쓰는 것이 버전이다. 저장할 때마다 1씩 올라서, 꺼낸 주문이 몇 번째 저장을 보고 바뀌었는지 알려 준다
- 이 예제에서는 주문이 버전을 들고, 주문 저장소가 다음 단계에서 버전을 비교한다

#### 코드
- 칸 long version은 생성자에서 값을 주지 않아 0으로 시작한다. 새 주문은 버전 0이다
- increaseVersion은 업무 행동이 아니라 저장소가 부르는 메서드다. 다른 패키지의 저장소가 불러야 해서 public이다
- version도 직렬화되는 칸이라 꺼낸 주문은 저장된 버전을 그대로 가진다

### src/ddd/infrastructure/MemoryOrderRepository.java

- 저장할 때 order.increaseVersion()을 copy보다 먼저 부른다
- 그래서 저장소에 넣는 복사본과 넘겨받은 주문이 같은 새 버전을 가진다. 응용 서비스가 같은 주문 객체로 다시 저장해도 버전이 맞는다

## optimistic-lock T3

### src/ddd/Main.java

- 취소와 저장을 try로 감싼다. orderB.cancel()은 통과하고 save에서 IllegalStateException으로 거절된다
- 거절 메시지 다른 곳에서 먼저 바꾼 주문입니다가 찍히고, 저장소의 주문은 배송 중 그대로다

### src/ddd/infrastructure/MemoryOrderRepository.java

- 저장된 주문을 먼저 꺼내 버전을 비교한다. 저장된 버전은 저장소만 알고, 꺼낸 주문끼리는 서로를 모르기 때문이다
- saved가 null이면 처음 저장하는 새 주문이라 비교하지 않는다
- orderA가 저장해 버전이 올랐으니, 옛 버전을 든 orderB는 버전이 달라 거절된다
- 버전 올리기와 넣기보다 앞에서 던지므로 거절되면 저장된 주문은 바뀌지 않는다

## bounded-contexts T1

### context-map.md

#### 개념 하위 도메인
- 하위 도메인(Subdomain)은 가게의 업무를 나눈 것이다. 코드가 아니라 업무에서 먼저 정한다.
- 핵심(Core)은 이 가게가 남보다 잘해야 하는 업무, 지원(Supporting)은 핵심을 돕지만 가게에 맞게 직접 만드는 업무, 일반(Generic)은 어느 가게나 비슷해서 사다 써도 되는 업무다.
- 종류를 나누는 이유는 힘을 어디에 쏟을지 정하기 위해서다. 핵심인 주문에는 앞 장들처럼 규칙을 공들여 넣고, 일반인 회원은 이름과 등급만 둔다.
- 하위 도메인은 애그리거트 같은 객체 묶음이 아니다. 주문 하위 도메인 안에 주문 애그리거트, 할인 정책, 무료 배송 명세가 모두 들어 있다.
- 이 예제에서는 주문이 핵심, 상품이 지원, 회원이 일반이다.

#### 개념 바운디드 컨텍스트
- 바운디드 컨텍스트(Bounded Context)는 한 모델과 한 말이 같은 뜻으로 통하는 코드의 경계다.
- 앞 장까지는 ddd.domain 한 묶음에 회원, 상품, 주문이 섞여 있어서 어느 클래스가 어느 업무의 말인지 코드만 보고는 알 수 없었다.
- 같은 말도 경계마다 뜻이 다르다. 상품 쪽의 상품은 지금 가격과 재고를 갖고, 주문 쪽의 주문 항목은 주문 때의 이름과 가격을 갖는다. order-factory 장의 가격 바뀐 뒤 장면이 그 차이다.
- 하위 도메인과 헷갈리기 쉽다. 하위 도메인은 업무를 나눈 것(문제 쪽)이고, 바운디드 컨텍스트는 그 업무를 푸는 모델의 경계(코드 쪽)다.
- 둘이 늘 하나씩 짝지어지지는 않는다. 이 프로그램은 알아보기 쉽게 하위 도메인 하나에 컨텍스트 하나를 둔다.
- 이 예제에서는 주문 컨텍스트 ddd.order, 상품 컨텍스트 ddd.catalog, 회원 컨텍스트 ddd.member가 경계다.

#### 코드
- context-map.md는 학생이 직접 쓰는 문서다. 이 장부터 컨텍스트를 나누고 이을 때마다 표에 한 줄씩 더한다.
- 표의 묶음 칸은 다음 단계부터 만들 자바 패키지 이름이다. 문서에서 먼저 경계를 정하고 코드를 옮긴다.
- 컨텍스트 안에는 layers 장에서 배운 domain, application, infrastructure 계층을 그대로 둔다. 계층은 컨텍스트 안쪽의 나눔이고 컨텍스트는 바깥쪽의 나눔이다.

## bounded-contexts T2

### context-map.md

#### 코드
- 공유 커널 절을 더한다. 무엇을 함께 쓰는지와 바꿀 때 합의가 필요하다는 약속을 문서에 남긴다.

### src/ddd/shared/Money.java

#### 개념 공유 커널
- 공유 커널(Shared Kernel)은 두 컨텍스트 이상이 함께 갖기로 합의한 작은 모델 조각이다.
- 돈은 주문 금액에도 상품 가격에도 쓰인다. 컨텍스트마다 돈을 따로 만들면 0원 이상이라는 규칙이 두 군데로 갈라진다.
- 그래서 돈 하나만 따로 떼어 함께 쓰고, 바꾸려면 쓰는 컨텍스트가 모두 합의한다.
- 아무 클래스나 모아 두는 공통 폴더와 다르다. 함께 쓸수록 컨텍스트끼리 단단히 묶이므로 가능한 한 작게 둔다.
- 이 예제에서는 ddd.shared 묶음의 Money 하나가 공유 커널이다.

#### 코드
- 이클립스 패키지 탐색기에서 src를 오른쪽 클릭하고 New, Package로 ddd.shared를 만든다.
- Money.java를 골라 Refactor, Move(Alt+Shift+V)로 ddd.shared를 고르거나 끌어 놓는다. 이클립스가 package 줄과 쓰는 쪽의 import 줄을 함께 바꾼다.
- 클래스 안의 코드는 한 줄도 바꾸지 않는다. 바뀌는 것은 package 줄 하나다.
- 같은 묶음이라 import 없이 쓰던 Order, OrderLine, DiscountPolicy, Product에 import ddd.shared.Money가 새로 생긴다.
- import 순서가 다르면 Source, Organize Imports(Ctrl+Shift+O)로 맞춘다. java 묶음 아래에 빈 줄을 두고 ddd 묶음을 이름 순서로 적는다.
- 공유 커널에는 계층 묶음을 두지 않는다. 함께 쓰는 모델 조각 하나뿐이고 응용과 인프라가 없다.

## bounded-contexts T3

### src/ddd/domain/DiscountPolicy.java

#### 코드
- import ddd.member.domain이 생긴다. 주문 쪽 할인 정책이 회원 컨텍스트의 회원과 회원 등급을 읽는다는 것이 import로 드러난다.

### src/ddd/member/domain/Member.java

#### 개념 모듈
- 모듈(Module)은 함께 바뀌는 개념을 이름 하나로 묶는 단위다. 자바에서는 패키지가 모듈이다.
- layers 장에서는 기술 계층으로만 묶어서 ddd.domain에 회원과 주문이 섞여 있었다. 회원 하나를 고치려면 주문 클래스 사이를 뒤져야 했다.
- 이제는 업무 묶음 ddd.member를 먼저 두고 그 안에 계층 묶음을 둔다.
- 바운디드 컨텍스트와 다르다. 컨텍스트는 모델과 말의 경계이고, 모듈은 그 경계를 코드에 담는 그릇이다. 한 컨텍스트가 ddd.member.domain, ddd.member.infrastructure처럼 모듈 여러 개를 가진다.
- 자바 9의 module-info 모듈 시스템과도 다르다. 이 강의는 다른 컨텍스트의 클래스를 못 쓰게 막지 않고 패키지로만 나눈다.
- 이 예제에서는 ddd.member, ddd.catalog, ddd.order, ddd.shared 네 묶음이 모듈이다.

#### 코드
- New, Package에서 이름을 ddd.member.domain처럼 점으로 한 번에 쓰면 중간 폴더까지 함께 생긴다.
- Member, MemberGrade, MemberRepository를 함께 골라 한 번에 옮길 수 있다.
- 회원 컨텍스트에는 application 묶음을 만들지 않는다. 회원은 저장하고 꺼내기만 해서 응용 서비스가 없다. 클래스가 없는 계층 묶음은 만들지 않는다.

### src/ddd/member/infrastructure/MemoryMemberRepository.java

#### 코드
- 저장소 구현은 같은 컨텍스트의 infrastructure로 간다. 회원 저장소 인터페이스와 구현이 한 컨텍스트 안에 함께 있다.

## bounded-contexts T4

### src/ddd/catalog/application/DecreaseStockHandler.java

#### 코드
- 재고 줄이기 처리기는 주문 접수됨을 받지만 상품 컨텍스트로 간다. 하는 일이 상품 재고를 바꾸고 저장하는 상품 쪽 유스케이스이기 때문이다.
- 이 단계에서는 주문 클래스가 아직 ddd.domain에 있어 import ddd.domain.OrderPlaced가 남는다. 다음 단계에서 ddd.order로 바뀐다.

### src/ddd/domain/OrderLineFactory.java

#### 코드
- import ddd.catalog.domain이 생긴다. 주문 항목 팩토리가 상품 컨텍스트의 상품 저장소에서 이름과 가격을 읽는 것이 import로 드러난다.

## bounded-contexts T5

### context-map.md

#### 코드
- 컨텍스트 사이 절은 쓰는 쪽, 쓰이는 쪽, 무엇을 표로 적는다. 앞 단계들에서 생긴 다른 컨텍스트 import를 모은 것이다.
- 상품과 주문 사이는 두 방향이다. 주문은 상품을 직접 읽고, 상품은 주문이 남긴 이벤트를 받는다.
- 아래 줄은 같은 상품이라도 컨텍스트마다 다른 모양으로 갖는다는 것을 적는다. 상품의 상품은 지금 값, 주문의 주문 항목은 주문 때 값이다.
- 지금은 다른 컨텍스트의 클래스를 import로 바로 쓴다. 정해진 창구로 잇는 법은 shipping-context 장과 payment-acl 장에서 배운다.

### src/ddd/Main.java

#### 코드
- 바뀌는 것은 import 줄뿐이다. 실행 출력 스물여섯 줄은 앞 장과 같다. 컨텍스트를 나눠도 동작은 그대로인 리팩터다.
- Main은 ddd 묶음에 그대로 남아 네 컨텍스트의 객체를 만들고 잇는다.

### src/ddd/catalog/application/DecreaseStockHandler.java

#### 코드
- 주문 접수됨과 주문 항목 import가 ddd.order.domain으로 바뀐다. 상품 컨텍스트가 주문 컨텍스트의 이벤트를 받는 자리다.

### src/ddd/order/application/EventPublisher.java

#### 코드
- 이벤트 발행기는 주문 응용 계층으로 간다. 주문 응용 서비스가 저장 뒤 이벤트를 넘기는 곳이고, 상품 컨텍스트는 Main에서 구독만 한다.
- 다 옮긴 뒤 빈 ddd.domain, ddd.application, ddd.infrastructure 묶음은 오른쪽 클릭, Delete로 지운다. src/ddd에는 Main.java와 catalog, member, order, shared만 남는다.

## shipping-context T1

### context-map.md

#### 코드
- 결제 완료 메시지 표는 칸마다 값과 뜻을 적는다. 받는 쪽은 이 표만 보고 메시지를 읽는다.
- 마지막 두 줄이 약속이다. 받는 쪽은 주문 클래스를 몰라도 되고, 주문 컨텍스트는 안을 바꿔도 이 모양은 지킨다.

### glossary.md

#### 코드
- 결제 완료 메시지는 클래스가 아니라 글자 모양이라 영어 칸에 메시지 종류 ORDER_PAID를 적는다.

### src/ddd/Main.java

#### 코드
- subscribe(String.class, ...)는 글자 이벤트만 받는 구독자를 단다. 이벤트 발행기는 이벤트의 클래스로 구독자를 고르므로 주문 접수됨은 여기로 오지 않는다.
- 결제 완료 메시지 두 줄이 한꺼번에 찍힌다. 앞에서 결제한 1번과 4번 주문의 메시지가 발행기에 쌓여 있다가 processEvents 때 처리되기 때문이다.
- 같은 processEvents에서 남아 있던 주문 접수됨도 처리되어 재고가 줄지만 찍지는 않는다.

### src/ddd/order/application/OrderApplicationService.java

#### 코드
- 공개 호스트 서비스는 생성자 안에서 new로 만든다. 같은 발행기를 쓰므로 생성자 매개변수는 그대로다.
- 결제하기만 저장 다음 줄에서 publishOrderPaid를 부른다. 저장이 끝난 결제만 바깥에 알린다.
- order.getEvents()는 결제 때 넘기지 않는다. 다시 넘기면 앞에서 남긴 주문 접수됨이 한 번 더 나가 재고가 또 준다.

### src/ddd/order/application/OrderOpenHostService.java

#### 개념 공개 호스트 서비스
- 공개 호스트 서비스(Open Host Service)는 한 컨텍스트가 여러 바깥 컨텍스트에 열어 두는 정해진 창구다.
- 앞 장의 컨텍스트 사이 표에서는 상품이 주문 클래스를 import로 바로 썼다. 그러면 주문이 클래스 안을 바꿀 때마다 쓰는 쪽도 함께 깨진다.
- 창구를 하나 두면 바깥에 무엇을 어떤 모양으로 알릴지 이 클래스 한 곳에서 정하고, 주문 안쪽 모델은 숨긴다.
- 주문 응용 서비스와 다르다. 응용 서비스는 우리 컨텍스트 안의 유스케이스 순서를 맡고, 공개 호스트 서비스는 바깥에 내보낼 것만 맡는다.
- 이 예제에서는 OrderOpenHostService가 주문 컨텍스트의 창구이고, 결제가 끝나면 결제 완료 메시지를 내보낸다.

#### 개념 발행된 언어
- 발행된 언어(Published Language)는 창구가 바깥과 주고받는 정해진 메시지 모양이다. 문서로 공개해 두고 지킨다.
- 공개 호스트 서비스는 어디로 알리는지(창구), 발행된 언어는 무슨 모양으로 알리는지(말)다. 둘은 짝으로 쓰인다.
- 도메인 이벤트와 다르다. 주문 접수됨은 같은 프로그램 안에서 주고받는 자바 객체이고, 결제 완료 메시지는 주문 클래스를 모르는 쪽도 읽는 글자다.
- 공유 커널과도 다르다. 공유 커널은 코드를 함께 갖고 바꿀 때 모두 합의하지만, 발행된 언어는 메시지 모양만 함께 알아서 묶임이 훨씬 약하다.
- 이 예제에서는 ORDER_PAID|orderId=1|address=서울시 강남구처럼 종류 뒤에 키=값을 |로 이은 한 줄 글자가 발행된 언어다.

#### 코드
- 메시지를 String 하나로 만든다. 받는 쪽이 주문, 주소 같은 주문 클래스를 몰라도 글자만 나누면 읽는다.
- order.getAddress().value()로 주소 값 객체에서 글자만 꺼낸다. 값 객체는 주문 컨텍스트 밖으로 내보내지 않는다.
- eventPublisher.publish(List.of(message))로 글자 하나를 이벤트 목록처럼 넘긴다. 발행기는 객체의 종류를 가리지 않으므로 글자도 이벤트로 넘길 수 있다.
- 응용 계층에 두는 이유는 발행기를 쓰는 곳이기 때문이다. 주문 도메인은 바깥 알림을 모른다.

## shipping-context T2

### context-map.md

#### 코드
- 배송은 지원 하위 도메인이다. 주문을 돕지만 가게 방식에 맞게 직접 만든다.
- 배송과 주문 사이 줄은 import가 아니라 메시지 글자로 잇는다. 같은 표 안에서 import로 잇는 상품, 회원 줄과 비교해 본다.
- 앞 장의 뒤 장에서 한다는 줄을 지금 모습으로 바꾼다. 컨텍스트 맵은 최신 모습만 적는다.

### src/ddd/Main.java

#### 코드
- 메모리 배송 저장소와 결제 완료 메시지 처리기를 만들어 String.class로 하나 더 구독한다. 한 종류에 구독자를 여럿 달 수 있다.
- 처리 뒤 배송 저장소에서 1번 주문의 배송을 꺼내 찍는다. 배송 컨텍스트가 메시지만으로 배송을 만들었다는 확인이다.

### src/ddd/shipping/application/OrderPaidMessageHandler.java

#### 코드
- import는 ddd.shipping뿐이다. 배송 컨텍스트는 주문 컨텍스트의 클래스를 하나도 쓰지 않는다.
- message.split("\\|")는 글자를 |에서 잘라 배열로 만든다. split은 정규식을 받는데 |는 정규식에서 또는이라는 뜻이라 \\로 글자 그대로라고 알린다.
- 첫 칸이 ORDER_PAID가 아니면 return으로 넘긴다. 같은 창구에서 다른 종류의 메시지가 와도 처리기는 자기 것만 읽는다.
- substring("orderId=".length())는 앞의 orderId= 글자 수만큼 건너뛰고 뒤의 값만 남긴다.
- Long.parseLong은 글자 1을 숫자 1로 바꾼다. 메시지는 글자라서 받는 쪽이 자기 타입으로 바꾼다.

### src/ddd/shipping/domain/Shipment.java

#### 개념 바운디드 컨텍스트
- 더해진 것은 같은 말이 경계마다 다른 모델이 된다는 실제 예다.
- 주문 컨텍스트의 배송은 주문 상태 배송 중일 뿐이고, 배송 컨텍스트의 배송은 주문 번호와 주소를 가진 자기 객체다.
- 주소도 주문의 Address 값 객체를 가져다 쓰지 않고 String으로 갖는다. 배송 컨텍스트는 자기 모델을 자기 말로 만든다.
- 그래서 주문 컨텍스트가 Address를 바꿔도 배송 컨텍스트는 메시지 모양만 같으면 그대로다.

#### 코드
- 배송은 주문 번호로 구별하는 엔티티라 equals와 hashCode를 orderId로 만든다. 주문 번호가 같으면 같은 배송이다.

### src/ddd/shipping/infrastructure/MemoryShipmentRepository.java

#### 코드
- 메모리 상품 저장소와 같은 모양이다. 주문 번호를 키로 HashMap에 넣고 Optional.ofNullable로 꺼낸다.

## payment-acl T1

### src/ddd/Main.java

#### 코드
- 외부 모듈에 가게 키 DDD-SHOP과 원 단위 int를 직접 넣어 응답 코드를 찍는다. 우리가 번역해야 할 바깥 말을 먼저 들어 보는 장면이다.
- 108000원은 10만 원을 넘어 E101이 나온다. 다음 단계의 한도 넘은 결제 장면과 같은 금액이다.

### src/legacypay/LegacyPayGateway.java

#### 코드
- 붙여넣기 코드다. 우리가 만들지 않았고 고칠 수 없는 외부 결제 모듈로 다룬다.
- 묶음을 ddd 밖 legacypay에 둔다. 이클립스에서 src 아래에 legacypay 패키지를 따로 만든다.
- 일부러 우리 말과 다르게 말한다. 영어 이름, 돈 대신 원 단위 int, 승인 0000과 한도 초과 E101과 가게 키 오류 E201 같은 코드 글자다.

## payment-acl T2

### glossary.md

#### 코드
- 용어집에는 결제 창구, 결제 결과, 결제 어댑터처럼 우리 말만 올린다. 0000, E101, 가게 키 같은 외부 말은 올리지 않는다.

### src/ddd/Main.java

#### 코드
- 주문 응용 서비스의 마지막 인자로 new LegacyPaymentAdapter(new LegacyPayGateway())를 넘긴다. 외부 모듈을 어댑터로 감싼 것을 결제 창구 자리에 끼운다.
- 맨 끝 장면은 이영희가 자바 책 6개 108000원을 결제한다. 거절 이유와 함께 주문 상태가 주문 접수로 남는 것을 찍는다.

### src/ddd/order/application/OrderApplicationService.java

#### 코드
- 결제 창구는 PaymentGateway 인터페이스로 받는다. 응용 서비스는 legacypay를 import하지 않는다.
- order.pay를 결제 창구보다 먼저 부른다. 두 번 결제처럼 주문 규칙에 걸리는 결제는 바깥에 맡기기 전에 거절한다.
- 결과가 승인이 아니면 거절 이유로 IllegalStateException을 던지고 저장하지 않는다. 저장된 주문은 주문 접수 그대로이고 결제 완료 메시지도 나가지 않는다.

### src/ddd/order/domain/PaymentGateway.java

#### 코드
- 주문 저장소처럼 도메인이 필요한 일을 우리 말로 정한 인터페이스다. 돈을 받아 결제 결과를 돌려준다.
- 도메인에 두고 구현은 인프라에 둔다. layers 장의 의존성 역전과 같은 모양이다.

### src/ddd/order/domain/PaymentResult.java

#### 코드
- record PaymentResult(boolean approved, String reason)는 승인인지와 거절 이유를 담는 결과 값이다. 승인이면 reason은 빈 글자다.
- 외부의 코드 글자 대신 이 record를 돌려주어 주문 쪽은 0000이 무슨 뜻인지 몰라도 된다.

### src/ddd/order/infrastructure/LegacyPaymentAdapter.java

#### 개념 부패 방지 계층
- 부패 방지 계층(Anticorruption Layer)은 바깥 모델의 말이 우리 모델로 새어 들지 않게 경계에서 번역하는 층이다.
- 외부 모듈을 주문 응용 서비스에서 바로 부르면 주문 코드에 if (code.equals("E101")) 같은 바깥 말이 퍼진다. 외부 모듈이 코드를 바꾸면 주문 코드를 여기저기 고쳐야 한다.
- 공개 호스트 서비스와 방향이 반대다. 공개 호스트 서비스는 내보내는 쪽이 정한 창구이고, 부패 방지 계층은 받아 쓰는 쪽이 자기를 지키려고 세우는 번역기다.
- 저장소 구현 같은 그냥 어댑터와도 다르다. 인터페이스를 구현하는 모양은 같지만, 부패 방지 계층은 금액 단위와 응답 코드처럼 모델 자체를 번역한다.
- 이 예제에서는 LegacyPaymentAdapter가 부패 방지 계층이다. legacypay를 import하는 주문 쪽 클래스는 이 파일 하나다.

#### 코드
- private static final String MERCHANT_KEY는 가게 키 상수다. 외부 모듈만 쓰는 값이라 어댑터만 알고 주문은 모른다.
- amount.amount()로 돈 값 객체를 외부가 쓰는 원 단위 int로 바꾼다.
- 받은 코드 글자를 if로 하나씩 결제 결과로 번역한다. 0000은 승인, E101은 결제 한도를 넘었습니다, 그 밖은 모두 결제에 실패했습니다다.
- 모르는 코드도 마지막 return에서 거절로 받는다. 외부가 새 코드를 더해도 주문 쪽은 승인 아니면 거절 둘만 본다.

## payment-acl T3

### context-map.md

#### 개념 컨텍스트 맵
- 컨텍스트 맵(Context Map)은 바운디드 컨텍스트들이 서로 어떤 관계로 이어지는지 그린 지도다.
- 컨텍스트만 나누고 관계를 적지 않으면 누가 누구 모델을 따르고 누가 번역하는지 코드를 다 읽어야 안다. 다음 사람이 외부 말을 주문 안으로 끌어들이기 쉽다.
- 클래스 그림이나 폴더 구조도와 다르다. 클래스가 아니라 컨텍스트 사이의 관계 종류를 적는다.
- 이 지도에는 관계가 네 가지 있다. 공유 커널(돈), 상대 클래스를 그대로 가져다 쓰는 관계(주문이 상품과 회원을 읽음), 공개 호스트 서비스와 발행된 언어(주문이 배송에 메시지), 부패 방지 계층(주문이 외부 결제를 번역)이다.
- 앞에서 뒤로 갈수록 서로 덜 묶인다. 같은 코드를 쓰는 공유 커널이 가장 단단하고, 번역으로 막는 부패 방지 계층이 가장 느슨하다.
- 이 예제에서는 context-map.md 한 장이 컨텍스트 맵이다.

#### 코드
- 결제는 일반 하위 도메인이다. 돈을 받는 일은 어느 가게나 같아서 실제로 외부 모듈을 사다 쓴다.
- 결제 줄의 바운디드 컨텍스트 칸은 우리 묶음이 아니라 외부 결제 모듈 legacypay다. 우리가 만들지 않은 컨텍스트도 지도에 올린다.
- 외부 말과 우리 말 표는 결제 어댑터가 하는 번역을 한 장에 적은 것이다. 외부가 바뀌면 이 표와 어댑터만 고친다.

### glossary.md

#### 코드
- 부패 방지 계층 줄의 영어 칸은 이 예제에서 그 일을 맡는 클래스 LegacyPaymentAdapter다.

## order-query T1

### src/ddd/Main.java

#### 코드
- 주문 조회 서비스는 주문 저장소와 회원 저장소만 받아 만든다. 결제 창구와 발행기는 넘기지 않는다.
- 김철수의 주문 목록을 주문 요약으로 한 줄씩 찍는다. summary.orderId()처럼 record의 칸 이름이 곧 꺼내는 메서드다.

### src/ddd/order/application/OrderQueryService.java

#### 개념 CQRS
- CQRS(Command Query Responsibility Segregation, 명령과 조회 나누기)는 바꾸는 쪽(명령)과 읽는 쪽(조회)을 다른 모델로 나누는 것이다.
- 주문 애그리거트는 규칙을 지키며 바꾸기에 맞춘 모양이다. 목록 화면 한 줄을 만들려고 주문 응용 서비스에 메서드를 더하면 화면이 바뀔 때마다 규칙을 지키는 클래스를 건드린다.
- 그래서 명령은 주문 응용 서비스가 주문으로 하고, 조회는 주문 조회 서비스가 주문 요약으로 따로 한다. 둘은 바뀌는 이유가 다르다.
- 응용 서비스를 그냥 둘로 쪼갠 것과 다르다. 조회 쪽은 규칙도 저장도 없고, 돌려주는 모델도 화면에 맞춘 다른 모양이다.
- 읽기 전용 저장소를 따로 두고 이벤트로 채우는 큰 CQRS도 있다. 이 예제는 저장소는 하나로 두고 모델만 나눈 가장 작은 모양이다.
- 이 예제에서는 OrderQueryService와 OrderSummary가 조회 쪽, OrderApplicationService와 Order가 명령 쪽이다.

#### 코드
- if, throw, save가 없다. 꺼내서 옮겨 담기만 한다.
- 주문마다 회원 저장소에서 회원 이름을 찾아 요약에 넣는다. 주문은 회원 번호만 가지므로 이름은 조회 때 붙인다.
- 돈은 amount(), 주문 상태는 label()로 꺼내 숫자와 글자로 바꿔 담는다.

### src/ddd/order/application/OrderSummary.java

#### 코드
- 칸이 long, String, int, int, String뿐이다. 돈, 주문 상태 같은 도메인 타입을 쓰지 않는 것이 읽기 모델이라는 표시다.
- 바꾸는 메서드가 없는 record다. 읽기 모델은 보여주기만 한다.
- 응용 계층에 둔다. 도메인 규칙이 아니라 조회 유스케이스가 돌려주는 결과이기 때문이다.

### src/ddd/order/domain/OrderRepository.java

#### 코드
- findByMemberId(long memberId)는 회원 번호로 주문 여러 개를 List로 돌려준다. 없으면 null이 아니라 빈 목록이다.
- 주문이 들어 있는 곳은 주문 저장소 하나라서 조회도 여기서 꺼낸다.

### src/ddd/order/infrastructure/MemoryOrderRepository.java

#### 코드
- orders.values()로 저장된 주문을 모두 돌며 회원 번호가 같은 것만 모은다.
- 꺼낼 때 findById처럼 copy로 복사해 담는다. 조회한 주문을 바꿔도 저장된 주문은 그대로다.

## order-query T2

### glossary.md

#### 코드
- 명령과 조회 나누기 줄에 어느 클래스가 바꾸고 어느 클래스가 읽기만 하는지 적는다. 말로 정해 두어야 조회 서비스에 규칙을 넣지 않는다.

## order-saga T1

### src/ddd/Main.java

#### 코드
- 문제를 먼저 실행해 보는 장면이다. 이영희가 120000원 노트북 1개를 주문하고 결제하면 외부 결제가 한도로 거절한다.
- 거절을 잡은 뒤 processEvents를 하면 결제 실패 뒤: 노트북 재고 9개, 주문 접수가 찍힌다. 결제는 실패했는데 재고는 줄어 있고 주문도 살아 있다.
- 주문 저장, 재고 줄이기, 결제가 따로 저장되는 일이라 뒤 단계가 실패해도 앞 단계는 그대로 남는다.
- 상품 번호 5를 쓰는 이유는 4번이 음수 금액 장면에서 쓴 번호이기 때문이다.

## order-saga T2

### glossary.md

#### 코드
- 재고 줄과 주문 취소하기 줄에 되돌리기를 덧붙인다. 새 말뿐 아니라 뜻이 넓어진 옛 말도 함께 고친다.

### src/ddd/Main.java

#### 코드
- 재고 되돌리기 처리기를 만들어 OrderCanceled.class로 구독한다. 재고 줄이기 처리기 바로 아래에 짝으로 둔다.

### src/ddd/catalog/application/IncreaseStockHandler.java

#### 코드
- 재고 줄이기 처리기와 같은 모양이다. 주문 항목마다 상품을 꺼내 재고를 되돌리고 저장한다.
- 줄이기와 되돌리기는 받는 이벤트가 달라서 처리기를 따로 두고 하나씩 구독한다.

### src/ddd/catalog/domain/Product.java

#### 코드
- increaseStock은 재고를 수량만큼 더한다. 재고 줄이기의 반대 일이다. 반대 일이 먼저 있어야 실패 때 되돌릴 수 있다.

### src/ddd/order/application/OrderApplicationService.java

#### 코드
- 주문 취소하기는 저장 다음 줄에서 주문 취소됨을 발행기에 넘긴다.
- order.getEvents()를 넘기지 않고 이벤트를 여기서 새로 만든다. 꺼낸 주문의 이벤트 목록에는 앞에서 남긴 주문 접수됨이 들어 있어 다시 넘기면 재고가 또 준다.
- order.cancel()이 배송 중 같은 이유로 예외를 던지면 다음 줄로 가지 않으므로 이벤트도 나가지 않는다.

### src/ddd/order/domain/OrderCanceled.java

#### 코드
- record OrderCanceled(long orderId, List<OrderLine> orderLines)는 주문 접수됨과 같은 모양이다. 받는 쪽이 무엇을 얼마나 되돌릴지 주문 항목으로 안다.

## order-saga T3

### glossary.md

#### 코드
- 사가와 보상 줄에 이 프로그램의 단계와 보상 짝을 함께 적는다.

### src/ddd/order/application/OrderApplicationService.java

#### 개념 사가
- 사가(Saga)는 따로 저장되는 여러 단계를 하나의 업무 흐름으로 잇고, 한 단계가 실패하면 앞에서 끝난 단계를 하나씩 되돌리는 방법이다.
- 앞 단계의 문제는 결제가 거절되어도 주문 접수와 재고 줄이기가 이미 저장되어 남는 것이었다.
- 한 트랜잭션의 롤백과 다르다. order-events 장에서 주문 저장과 재고 줄이기를 다른 트랜잭션으로 나눴기 때문에 한 번에 없던 일로 돌릴 수 없다.
- 최종 일관성만으로는 부족하다. 최종 일관성은 성공한 일이 나중에 맞춰지는 것이고, 사가는 실패했을 때 되돌리는 길까지 정한다.
- 사가를 맡는 조정 클래스를 따로 두는 방법도 있다. 이 예제는 결제하기가 취소하기를 부르고, 나머지는 이벤트로 이어지게 했다.
- 이 예제에서는 주문 접수, 재고 줄이기, 결제가 사가의 단계다.

#### 개념 보상
- 보상(Compensation)은 끝난 단계를 지우지 않고 반대 일을 한 번 더 해서 되돌리는 것이다.
- 주문을 지우면 주문이 있었다는 사실도 사라진다. 그래서 취소라는 새 상태로 남긴다.
- 롤백은 없던 일로 만들지만 보상은 흔적을 남긴다. 취소된 주문과 주문 취소됨 이벤트가 그 흔적이다.
- 이 예제에서는 주문 접수의 보상이 주문 취소하기, 재고 줄이기의 보상이 재고 되돌리기다.

#### 코드
- 거절이면 cancel(orderId)를 부른 뒤 거절 이유로 예외를 던진다. 보상은 이미 있는 주문 취소하기를 그대로 쓴다.
- cancel은 저장소에서 주문을 다시 꺼낸다. 저장된 주문은 아직 주문 접수라서 취소할 수 있고, 결제하기 안에서 order.pay로 바꾼 order는 저장하지 않은 채 버린다.
- 취소가 주문 취소됨을 넘기므로 재고 되돌리기는 따로 부르지 않아도 이벤트를 처리할 때 따라온다.
- 이제 결제 실패 뒤 줄은 노트북 재고 10개, 취소로 바뀌고, 한도 넘은 결제 줄의 상태도 취소로 바뀐다.

## order-saga T4

### context-map.md

#### 코드
- 상품과 주문 줄에 재고 되돌리기 처리기를, 주문과 회원 줄에 주문 조회 서비스를 더한다. 앞 장들에서 생긴 컨텍스트 사이 쓰임을 지금 모습으로 맞춘다.

### where-rules-live.md

#### 개념 규칙 두는 곳
- 이 문서는 지금까지 배운 자리마다 어떤 규칙을 두는지 한 장에 모은 것이다. 새 규칙이 생기면 위에서부터 차례로 물어 둘 곳을 고른다.
- 고르는 기준이 없으면 새 규칙은 가장 쓰기 쉬운 응용 서비스에 쌓인다. 그러면 anemic-order 장의 빈약한 주문으로 돌아간다.
- 애그리거트 칸에는 엔티티의 행동 메서드도 들어 있다. 엔티티는 자기 상태와 규칙을 지키고, 루트는 그 안쪽 객체들이 함께 맞는지를 지킨다.
- 도메인 서비스와 명세는 둘 다 여러 객체를 보지만 답이 다르다. 도메인 서비스는 할인 금액 같은 값을 계산하고, 명세는 참 거짓만 답한다.
- 이벤트와 사가도 다르다. 이벤트는 다른 애그리거트가 나중에 맞추게 알리는 것이고, 사가는 그 이음이 실패했을 때 보상으로 되돌리는 것이다.
- 응용 서비스는 규칙을 갖지 않는다. 꺼내기, 시키기, 저장하기 순서만 맡는다.
- 이 예제의 모든 규칙이 표의 일곱 줄 중 하나에 들어간다.

#### 코드
- 학생이 직접 쓰는 문서다. 이 프로그램의 클래스 이름으로 예를 적어 표만 보고도 코드를 찾아갈 수 있게 한다.
- 사가 절의 표는 단계마다 보상과 보상을 맡는 곳을 짝지어 적는다. 결제 단계는 되돌릴 것이 없고 실패하면 앞 두 보상을 일으킨다.
- 재고는 이벤트를 처리한 뒤에 맞춰진다는 줄은 보상도 최종 일관성으로 이루어진다는 뜻이다.

## socket-queue T1

### src/ddd/ShippingApp.java

#### 코드
- 붙여넣기 코드다. 메시지를 받는 쪽인 배송 앱의 main이다. 이클립스에서 이 파일을 Run As, Java Application으로 먼저 띄운다.
- 배송 저장소와 결제 완료 메시지 처리기는 Main과 같은 것을 그대로 쓴다. 메시지를 받아 오는 곳만 소켓이다.
- import에 ddd.order가 없다. 다른 프로그램이 된 배송 컨텍스트는 발행된 언어 글자 하나로만 주문과 이어진다.

### src/ddd/shipping/infrastructure/SocketMessageReceiver.java

#### 코드
- 붙여넣기 코드다. 생성자에서 ServerSocket으로 포트를 열어 두고, receive가 들어온 연결에서 한 줄을 읽어 돌려준 뒤 포트를 닫는다.
- try (...) 괄호 안에 연 자원은 블록이 끝나면 자바가 알아서 닫는다(try with resources). 서버 소켓, 연결, 읽기 도구를 한 번에 닫는다.
- IOException은 UncheckedIOException으로 감싸 던진다. 부르는 쪽이 throws를 적지 않아도 된다.
- 인프라에 둔다. 소켓은 바깥과 잇는 기술이고 배송 도메인과 처리기는 소켓을 모른다.

## socket-queue T2

### src/ddd/OrderApp.java

#### 코드
- 붙여넣기 코드다. 보내는 쪽인 주문 앱의 main이다. 배송 앱이 기다린다는 줄을 띄운 뒤에 Run As로 실행한다.
- Main과 다른 곳은 new SocketEventPublisher("localhost", 9090) 한 줄뿐이다. 주문 응용 서비스는 같은 인자로 만든다.
- 결제하면 주문 앱 콘솔에는 보냈다는 줄이, 배송 앱 콘솔에는 받은 메시지와 배송 컨텍스트 줄이 찍힌다. 이클립스 콘솔 창의 화면 바꾸기 버튼으로 두 콘솔을 오가며 본다.

### src/ddd/order/application/SocketEventPublisher.java

#### 개념 발행기 바꿔 끼우기
- 메시지를 어떻게 나를지는 도메인이 아니라 바꿔 끼울 수 있는 기술이다.
- 지금까지는 결제 완료 메시지가 같은 프로그램 안의 발행기를 거쳐 갔다. 배송을 다른 프로그램으로 떼어 내도 주문과 배송 코드를 고치지 않을 수 있는지가 문제였다.
- 발행기 자리에 소켓 발행기를 넣기만 하면 같은 글자가 다른 프로그램으로 간다. 공개 호스트 서비스와 발행된 언어가 그대로라서 가능하다.
- 저장소를 바꿔 끼우던 것과 같은 생각이다. 쓰는 쪽은 자리만 알고 그 자리에 무엇이 들어오는지는 Main이나 OrderApp이 정한다.
- 실제 메시지 큐 제품과 다르다. 소켓은 받는 쪽이 꺼져 있으면 메시지가 사라지고, 메시지 큐는 모아 두었다가 다시 보낸다.
- 이 예제에서는 EventPublisher 자리에 SocketEventPublisher를 넣는다.

#### 코드
- 붙여넣기 코드다. extends EventPublisher로 이벤트 발행기를 이어받아, 주문 응용 서비스가 EventPublisher를 받는 자리에 그대로 들어간다.
- publish를 @Override로 고쳐 쓴다. 글자 이벤트는 send로 소켓에 보내고, 주문 접수됨 같은 자바 객체는 super.publish로 원래 발행기에 맡긴다.
- 소켓 너머로는 발행된 언어 글자만 보낸다. 자바 객체는 같은 프로그램 안에서만 쓴다.
- PrintWriter의 두 번째 인자 true는 println할 때마다 바로 내보내라는 뜻이다.
- 인프라가 아니라 이벤트 발행기 옆 응용 계층에 둔다. 인프라에 두면 응용의 EventPublisher를 import해야 해서 layers 장의 규칙에 걸린다.

