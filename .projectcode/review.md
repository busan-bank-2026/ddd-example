# 따라치기 코드 리뷰


## project-setup T1

### src/ddd/Main.java

#### 개념 폴더 구조
##### 무엇
- 자바 프로젝트는 코드를 두는 src 폴더와 컴파일 결과가 쌓이는 bin 폴더로 나뉜다.
- src 아래 폴더 이름은 패키지 이름과 같아야 한다. 그래서 src/ddd 폴더의 파일은 첫 줄이 package ddd다.
- 패키지는 클래스를 묶는 이름표이고, 폴더는 그 이름표가 디스크에 놓인 자리다. 둘은 같은 것을 두 쪽에서 본 모습이다.
##### 왜
- 폴더와 패키지가 어긋나면 이클립스가 빨간 줄로 알려 주고 실행되지 않는다.
- 컴파일러도 폴더 길로 클래스를 찾는다. 명령줄의 javac --source-path src는 ddd.Order를 src/ddd/Order.java 자리에서 찾는다.
##### 쉬운 방법과 비교
- 학생이 먼저 떠올릴 쉬운 방법은 package 줄 없이 src 바로 아래에 파일을 두는 기본 패키지다.
- 지금 1장은 Main 하나뿐이라 기본 패키지로도 똑같이 돈다.
- 차이는 10장에서 클래스를 ddd.domain, ddd.application, ddd.infrastructure로 나눌 때 드러난다. 자바는 이름 있는 패키지에서 기본 패키지의 클래스를 import할 수 없다.
- 17장은 다시 ddd.order, ddd.catalog, ddd.member, ddd.shared로 나누고, 19장은 외부 결제 모듈을 ddd 밖 legacypay 패키지에 둔다. 처음부터 ddd 패키지에 있어야 옮길 때 package 줄과 import만 바꾸면 된다.
- 파일 하나로 문법만 연습하고 버릴 코드라면 기본 패키지가 더 단순하다.
##### 헷갈리는 개념
- 폴더 구조는 10장의 계층 구조와 다르다. 폴더는 자바가 클래스를 찾는 자리이고, 계층은 어느 클래스가 어느 클래스를 써도 되는지 정한 약속이다. 폴더 나누기는 그 약속을 눈에 보이게 할 뿐이다.
- 17장의 바운디드 컨텍스트도 패키지로 나누지만 같은 것이 아니다. 패키지는 클래스를 담는 그릇이고, 바운디드 컨텍스트는 같은 말이 같은 뜻으로 통하는 범위다.
##### 이 예제에서는
- 이 강의는 ddd 폴더 하나가 이클립스 프로젝트 하나이고, 학생 코드는 모두 src/ddd 아래에 쌓인다.

#### 코드
- 이클립스에서는 src 폴더를 오른쪽 클릭하고 New, Class를 고른 뒤 Package에 ddd, Name에 Main을 넣으면 이 파일이 생긴다.
- public static void main(String[] args)는 프로그램이 시작하는 자리다. 이 메서드가 있어야 Run으로 실행할 수 있다.
- System.out.println은 콘솔에 한 줄을 찍는다. 먼저 프로그램이 도는지만 확인한다.
- 이클립스에서는 Main.java를 연 채 Ctrl+F11을 누르거나 Run As, Java Application을 고르면 아래 Console 창에 글이 찍힌다.

## project-setup T2

### .classpath

#### 개념 이클립스 실행
##### 무엇
- 이클립스는 폴더를 그냥 열지 않고, 폴더 안의 .project와 .classpath를 읽어 자바 프로젝트로 알아본다.
- 이 파일들은 학생이 직접 치는 코드가 아니라, 이클립스에서 New, Java Project로 새 프로젝트를 만들면 자동으로 생기는 설정 파일과 같다.
##### 왜
- 이 두 파일이 없으면 이클립스는 ddd 폴더를 자바 프로젝트로 보지 않아 컴파일도 실행도 하지 않는다.
- 설정 파일을 붙여넣기로 받으면 모두의 자바 버전, 인코딩, 들여쓰기가 같아져서 강의 화면과 학생 화면이 같게 돈다.
##### 쉬운 방법과 비교
- 첫째 쉬운 방법은 이클립스에서 New, Java Project로 직접 만드는 것이다. 지금 1장에서는 이것으로도 Main이 돈다.
- 다만 자바 버전과 인코딩을 손으로 맞춰야 한다. 인코딩이 MS949로 남으면 한글 출력이 깨지는데, 2장부터 출력은 거의 모두 한글이다.
- 둘째 쉬운 방법은 이클립스 없이 명령줄에서 javac -d bin --source-path src src/ddd/Main.java로 컴파일하고 java -cp bin ddd.Main으로 실행하는 것이다. 이 방법은 22장까지 모든 장에서 된다.
- 이클립스가 나은 곳은 코드를 고칠 때다. 3장은 Alt+Shift+R 한 번으로 메서드 이름과 부르는 곳을 함께 바꾸고, 6장부터는 Ctrl+Shift+O로 import를 채운다.
- 10장과 17장처럼 클래스를 다른 패키지로 옮기면 import가 한꺼번에 깨진다. 이클립스는 저장할 때마다 다시 컴파일해 깨진 곳을 빨간 줄로 바로 보여 준다.
- 22장은 ShippingApp과 OrderApp 두 프로그램을 함께 띄운다. 이클립스에서는 Run을 두 번 누르고 콘솔 창에서 두 출력을 바꿔 본다.
- 같은 명령을 여러 번 자동으로 돌릴 때는 명령줄이 더 단순하다. 이 강의의 행동 테스트도 이클립스에 붙이지 않고 명령줄로 돌린다.
##### 이 예제에서는
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
##### 무엇
- 빈약한 도메인 모델(Anemic Domain Model)은 객체에 데이터 칸과 getter, setter만 있고 업무 규칙은 하나도 없는 모양이다.
- 규칙은 서비스 클래스가 모두 들고, 객체는 서비스가 값을 넣고 빼는 상자로만 쓰인다.
##### 왜
- 처음에는 단순해 보이지만, setter가 열려 있어서 서비스를 거치지 않으면 누구나 상태를 아무 값으로 바꿀 수 있다.
- 그래서 결제 안 한 주문이 배송 중이 되는 것 같은 규칙 위반을 막을 곳이 없다.
- 기능이 늘수록 같은 검사가 여러 서비스에 흩어지고, 주문 규칙을 알려면 주문이 아니라 서비스 코드를 뒤져야 한다.
##### 쉬운 방법과 비교
- 이 모양 자체가 학생이 가장 먼저 떠올리는 쉬운 방법이다. 서비스 하나에 규칙을 다 모은다.
- 지금 2장에서는 이것으로 충분하다. 주문을 바꾸는 길이 OrderService 하나이고 Main도 서비스만 부르므로 규칙이 다 지켜진다.
- 첫 차이는 4장 첫 장면에서 난다. Main이 order2.setStatus("배송 중")을 직접 부르면 서비스의 결제 검사를 건너뛰고 결제 없이 배송: 배송 중이 찍힌다.
- 뒤 장에서 주문을 바꾸는 곳은 9장 주문 응용 서비스, 21장 결제가 실패했을 때 부르는 주문 취소하기처럼 늘어난다. 규칙이 서비스에 있으면 길이 늘 때마다 같은 검사를 다시 챙겨야 하고, 규칙이 Order 안에 있으면 어느 길로 와도 같은 검사를 지난다.
- 업무 규칙이 거의 없고 값을 넣고 꺼내기만 하는 일이라면 이 모양이 더 단순하다.
##### 헷갈리는 개념
- 데이터만 담는 클래스가 모두 문제는 아니다. 9장 PlaceOrderCommand나 20장 OrderSummary처럼 값을 옮기거나 보여 주기만 하는 클래스는 규칙이 없어야 맞다.
- 여기서 Order가 문제인 것은 업무의 중심 개념인데도 규칙을 갖지 않기 때문이다.
- 9장의 응용 서비스(Application Service)도 이름은 서비스지만 하는 일이 다르다. 응용 서비스는 주문을 꺼내고 시키고 저장하는 순서만 맡고 규칙은 주문에 둔다. 여기 OrderService는 규칙까지 든다.
##### 이 예제에서는
- Order가 상자이고 OrderService가 규칙을 든다. 4장 rich-entity에서 규칙을 Order 안으로 옮겨 고친다.

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
##### 무엇
- 유비쿼터스 언어(Ubiquitous Language)는 업무 담당자와 개발자가 회의, 문서, 코드에서 똑같이 쓰는 하나의 말이다.
##### 왜
- 앞 장 코드는 createOrder, updatePayment, deleteOrder처럼 데이터를 고치는 말로 이름을 지었다.
- 업무에서는 주문을 만든다, 지운다고 하지 않고 주문한다, 취소한다고 한다. 코드와 업무 말이 다르면 서로 번역해야 하고 그 사이에 오해가 생긴다.
- 특히 deleteOrder는 주문이 지워진다고 읽히지만 실제로는 취소 상태로 남는다. 이름이 업무와 다른 뜻을 말한다.
##### 쉬운 방법과 비교
- 쉬운 방법은 용어집 없이 메서드 이름만 바꾸는 것이다. 지금 3장은 이름 네 개만 바뀌고 출력도 같으므로 이것으로도 된다.
- 차이는 말의 뜻이 바뀔 때 드러난다. 6장에서 주문 금액의 뜻이 가격 곱하기 수량에서 항목 금액의 합으로 바뀌지만 코드 이름 totalPrice는 그대로다. 이름만 봐서는 바뀐 것을 모르고, 용어집의 뜻 칸이 알려 준다.
- 13장은 주문 금액 totalPrice, 할인 금액 discountPrice, 결제 금액 paymentPrice가 함께 나온다. 셋을 그냥 가격이라고 부르면 할인을 빼기 전 돈인지 뺀 뒤 돈인지 헷갈린다.
- 18장은 주문의 배송하기 ship과 배송 컨텍스트의 배송 Shipment가 같은 배송이라는 말을 쓴다. 용어집이 둘의 뜻을 따로 적어 가른다.
- 혼자 짧게 만드는 연습 코드라면 이름을 잘 짓는 것으로 충분하다. 용어집은 모델이 바뀔 때마다 함께 고쳐야 하는 비용이 있다.
##### 헷갈리는 개념
- 영어 이름을 잘 짓는 것과 다르다. 업무 쪽 사람이 쓰는 말을 먼저 정하고, 코드 이름을 그 말에 맞춘다.
- 용어집은 유비쿼터스 언어를 적어 둔 도구일 뿐이다. 말 자체는 회의와 코드에서 똑같이 쓰일 때 살아 있다.
- 17장의 바운디드 컨텍스트(Bounded Context)는 이 말이 같은 뜻으로 통하는 범위다. 이 강의는 용어집을 컨텍스트마다 나누지 않고 하나로 둔다.
##### 이 예제에서는
- glossary.md 용어집에 업무 말과 코드 이름을 나란히 적고, 다음 단계부터 메서드 이름을 그대로 바꾼다.

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
##### 무엇
- 엔티티(Entity)는 식별자(Identifier)로 알아보는 객체다. 내용이 바뀌어도 식별자가 같으면 같은 것으로 본다.
##### 왜
- 주문은 결제, 배송을 거치며 상태가 계속 바뀐다. 내용으로 같은지를 보면 상태가 바뀐 순간 다른 주문이 되어 버린다.
- 그래서 주문마다 바뀌지 않는 번호를 주고, 같은지는 번호로만 본다.
- 앞 장의 Order는 번호가 없어 같은 주문인지 가릴 방법이 없었고, 빈 객체를 만든 뒤 setter로 채워서 칸이 빈 주문도 생길 수 있었다.
##### 쉬운 방법과 비교
- 쉬운 방법은 번호 없이 자바 객체 그 자체로 알아보는 것이다. equals를 재정의하지 않으면 자바는 같은 객체인지(==)로 비교한다.
- 지금 4장에서는 이것으로도 된다. Main이 order 변수 하나를 계속 들고 결제, 배송, 취소를 하므로 늘 같은 객체다.
- 차이는 8장 주문 저장소에서 드러난다. 저장소는 저장할 때와 꺼낼 때 복사본을 쓰므로 같은 주문을 두 번 꺼내면 객체가 둘이다. 둘이 같은 주문인지는 번호로만 안다.
- 16장은 같은 주문을 두 번 꺼낸 두 복사본이 서로 덮어쓰는 문제를 다룬다. 저장소는 번호로 저장된 주문을 찾아 버전을 비교한다.
- 주문자 이름을 식별자로 쓰면 안 된다. 11장부터 Main에서 2번 회원이 주문을 여러 번 한다. 한 사람이 주문 여러 개를 가지므로 주문에는 주문만의 번호가 있어야 한다.
- 이 예제 코드에는 Order의 equals를 직접 부르는 곳이 없다. 저장소는 Map의 키로 번호를 바로 쓴다. equals와 hashCode는 주문을 Set이나 Map 키에 넣을 때를 위한 약속이다.
- 내용이 같으면 같은 것으로 봐도 되는 것은 번호를 주지 않는 쪽이 낫다. 5장의 돈과 주소가 그렇다.
##### 헷갈리는 개념
- 다음 장의 값 객체는 반대로 내용이 같으면 같다. 같음의 기준이 엔티티와 값 객체를 가르는 첫 번째 차이다.
- 식별자는 데이터베이스 기본 키와 다르다. 기본 키는 저장하는 방법이고, 식별자는 업무에서 이것이 그것인지 가리는 기준이다. 이 예제에는 데이터베이스가 없어도 주문 번호가 있다.
##### 이 예제에서는
- Order가 엔티티이고 long id가 식별자다. equals와 hashCode가 id만 본다.
- 뒤 장의 Member, Product, Shipment도 같은 모양의 엔티티다. Shipment는 주문 번호로 알아본다.

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
##### 무엇
- 행동 메서드는 엔티티가 자기 상태를 바꾸는 업무 일을 하는 메서드다. 그 일의 규칙도 함께 지킨다.
##### 왜
- 앞 장까지는 규칙이 OrderService에 있어서, 서비스를 거치지 않고 setStatus를 부르면 결제 없이 배송이 되었다.
- 규칙을 그 규칙이 지키는 데이터 곁인 Order 안에 두면, 어느 길로 오든 주문이 스스로 거절한다.
##### 쉬운 방법과 비교
- 첫째 쉬운 방법은 setStatus 안에 검사를 넣는 것이다. 지금 4장에서는 배송 중으로 바꿀 때 결제 완료인지, 취소로 바꿀 때 배송 중이 아닌지 보면 똑같이 막힌다.
- 하지만 setStatus는 무슨 일을 하는지 말하지 않는다. 13장 결제하기는 pay(discountPrice)로 상태를 바꾸면서 할인 금액도 함께 정한다. setter로는 두 칸을 함께 바꾸는 업무 일을 이름으로 드러낼 수 없다.
- 둘째 쉬운 방법은 setStatus에서 public을 빼서 서비스만 부르게 하는 것이다. 지금은 Main, Order, OrderService가 모두 ddd 패키지라서 public을 빼도 Main이 그대로 부를 수 있다.
- 10장에서 서비스가 ddd.application, 주문이 ddd.domain으로 갈라지면 반대로 서비스도 그 setter를 부를 수 없다. 접근 제한자로는 서비스만 통과시키는 문을 만들 수 없다.
- 규칙이 하나도 없는 칸이라면 setter가 더 단순하다. 다만 이 예제의 주문 상태에는 늘 규칙이 붙는다.
##### 헷갈리는 개념
- setter와 다르다. setter는 값을 그대로 넣지만 행동 메서드는 업무 이름을 갖고, 지금 상태에서 해도 되는 일인지 먼저 본다.
- 9장의 응용 서비스와 다르다. 응용 서비스는 주문을 꺼내 행동 메서드를 부르고 저장하는 순서만 맡는다. 이 장에서 OrderService가 시키기만 하게 된 것이 그 첫 모습이다.
- 13장의 도메인 서비스(Domain Service)와 다르다. DiscountPolicy는 회원과 주문을 함께 읽어 할인 금액을 계산만 하고, 주문 상태를 바꾸는 일은 여전히 주문의 pay가 한다.
- 6장의 애그리거트 루트는 행동 메서드가 자기 칸을 넘어 안쪽 객체까지 함께 맞추는 경우다.
##### 이 예제에서는
- pay, ship, cancel이 행동 메서드다. 메서드 이름은 용어집의 업무 말 그대로다.
- 이 장의 pay는 아직 검사가 없어 결제 완료인 주문을 다시 결제해도 거절하지 않는다. 7장에서 상태가 바뀌는 길을 정하며 막는다.

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
##### 무엇
- 값 객체(Value Object)는 식별자 없이 담긴 값으로만 알아보는 객체다. 금액이 같은 두 돈은 같은 돈이다.
##### 왜
- 앞 단계에서는 금액이 그냥 int라서 음수도 들어갔다.
- 돈을 객체로 만들고 만들 때 스스로 0원 이상인지 검사하면, 돈을 쓰는 어디에서도 음수 금액이 생기지 않는다.
##### 쉬운 방법과 비교
- Order 생성자에서 if (price < 0)로 막아도 지금 5장에서는 똑같이 음수 주문이 거절된다.
- 차이는 금액이 나오는 곳이 늘어날 때 드러난다. 6장 주문 항목 금액, 11장 상품 가격, 13장 할인 금액과 결제 금액, 14장 배송비다.
- int로 두면 이 클래스마다 같은 if를 복사해야 하고 하나만 빠뜨려도 음수가 새어 들어온다.
- 13장의 주문 금액 빼기 할인 금액처럼 계산해서 나온 금액도 따로 검사해야 한다. Money는 계산 결과도 new Money를 거쳐 저절로 검사된다.
- 가격과 수량이 둘 다 int면 생성자에 둘을 바꿔 넣어도 컴파일된다. Money와 int면 컴파일러가 잡는다.
- 금액이 한 곳에만 나오고 계산도 없다면 int 검사가 더 단순하다. 클래스를 늘리는 데도 비용이 있다.
- 이 예제에도 일부러 int를 쓰는 곳이 있다. 19장 외부 결제 모듈은 금액을 int로 받아서 어댑터가 amount()로 꺼내 넘기고, 20장 OrderSummary는 보여 주기만 하는 읽기 모델이라 결제 금액을 int로 둔다.
- 18장 배송 컨텍스트의 Shipment도 주소를 Address가 아닌 String으로 둔다. 메시지 글자에서 읽은 주소를 저장만 하고, 주문 컨텍스트의 클래스를 가져다 쓰지 않기 때문이다.
##### 헷갈리는 개념
- 엔티티와 같음의 기준이 다르다. 주문은 번호로 알아보지만, 돈에는 번호가 없고 금액이 같으면 바꿔 써도 된다.
- 그냥 int를 감싼 포장과도 다르다. 0원 이상이라는 규칙과 곱하기, 더하기 같은 돈의 일을 함께 갖는다.
- 값 객체 검사는 값 하나로 판단할 수 있는 규칙만 맡는다. 주문 금액이 항목 금액의 합과 같아야 한다처럼 여러 칸을 함께 봐야 하는 규칙은 6장 애그리거트가 맡는다.
##### 이 예제에서는
- Money가 값 객체이고, 주문의 가격과 주문 금액이 Money다. 다음 단계의 Address도 같은 모양의 값 객체다.

#### 개념 불변
##### 무엇
- 불변(Immutable)은 한 번 만든 객체의 값을 다시 바꿀 수 없다는 뜻이다.
##### 왜
- 돈이 바뀔 수 있으면 두 주문이 같은 돈 객체를 나눠 쓸 때 한쪽에서 바꾼 금액이 다른 주문까지 바꿔 버린다.
- 그래서 곱하기와 더하기는 원래 돈을 고치지 않고 새 돈을 만들어 돌려준다.
##### 쉬운 방법과 비교
- 쉬운 방법은 Money에 setAmount를 두거나, times가 자기 amount를 곱해 바꾸고 자기를 돌려주게 하는 것이다. new를 덜 하니 아껴 쓰는 것처럼 보인다.
- 지금 5장에서는 이것으로도 된다. times는 주문 생성자에서 한 번만 불리고, 돈을 나눠 쓰는 곳도 없다.
- 차이는 같은 계산을 여러 번 부를 때 드러난다. 6장 OrderLine.amount()는 price.times(quantity)다. times가 price를 바꾸면 주문 생성자에서 한 번, Main 출력에서 한 번 부를 때마다 항목 가격이 커진다.
- 13장 getPaymentPrice()는 totalPrice.minus(discountPrice)다. minus가 totalPrice를 바꾸면 14장 무료 배송 명세와 Main 출력이 결제 금액을 읽을 때마다 할인이 또 빠진다.
- 값이 계속 바뀌어야 하는 것은 값 객체가 아니라 엔티티의 칸으로 두고 바꾸는 쪽이 단순하다. 15장 Product의 재고 stock은 int 칸이고 decreaseStock이 그 칸을 바꾼다.
##### 헷갈리는 개념
- 엔티티 주문은 상태가 바뀌는 것이 자연스럽지만, 값 객체는 바뀌는 대신 새것으로 갈아 끼운다.
- final 칸과 다르다. 칸이 final이어도 그 안의 객체가 바뀔 수 있으면 불변이 아니다. Money는 record라 칸이 final이고 그 칸이 int라서 바꿀 길이 없다.
##### 이 예제에서는
- times와 plus가 새 Money를 돌려주고, 주문은 그 새 돈을 칸에 넣는다.

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
##### 무엇
- 애그리거트(Aggregate)는 함께 바뀌어야 하는 객체들을 한 묶음으로 보는 단위다. 묶음 밖에서는 이 묶음을 하나처럼 다룬다.
- 루트(Aggregate Root)는 그 묶음의 대표 엔티티다. 밖에서는 루트만 부를 수 있고, 안쪽 객체는 루트를 거쳐서만 바뀐다.
##### 왜
- 주문 하나에 항목이 여러 개가 되면, 항목 목록과 주문 금액이 늘 함께 맞아야 한다. 이 단계 코드처럼 목록을 그대로 내주면 밖에서 항목만 더해 합계가 틀어진다.
##### 쉬운 방법과 비교
- 쉬운 방법은 주문 금액을 칸에 두지 않고 getTotalPrice()가 부를 때마다 항목 금액을 더해 돌려주는 것이다. 저장한 합계가 없으니 어긋날 합계도 없다.
- 지금 6장의 합계 문제는 이것으로도 풀린다.
- 하지만 목록이 밖에 열려 있으면 결제한 주문에도 항목이 더해진다. 13장은 결제할 때 할인 금액을 정하고, 19장부터는 그 결제 금액을 결제 창구에 넘긴다. 결제 뒤 항목이 늘면 이미 치른 금액과 주문의 결제 금액이 달라진다.
- 묶음을 정하면 저장과 잠금의 단위도 정해진다. 8장 저장소는 주문 항목을 따로 저장하지 않고 주문째로 저장하고, 16장 버전도 주문에만 둔다. 항목만 따로 바꾸는 길이 없으니 주문 버전 하나로 항목 변경까지 잡힌다.
- 묶음은 클수록 좋은 것이 아니다. 11장은 회원과 상품을 주문 안에 넣지 않고 번호로만 가리킨다. 상품 가격을 바꾸는 일까지 주문을 거치게 할 이유가 없기 때문이다.
- 함께 맞춰야 할 객체가 없다면 묶음을 따질 일도 없다. 항목 하나에 합계 계산도 없던 5장까지의 주문이 그랬다.
##### 헷갈리는 개념
- 앞 장의 엔티티 행동 메서드는 자기 칸 하나의 규칙을 지켰다. 루트는 그보다 넓게 안쪽 객체들까지 묶어 서로 맞는지 지킨다.
- 루트는 9장 응용 서비스와 다르다. 응용 서비스는 주문을 꺼내 루트에게 시키고 저장할 뿐이고, 항목과 금액을 맞추는 일은 루트가 한다.
- 루트는 13장 도메인 서비스와도 다르다. 도메인 서비스는 주문과 회원처럼 두 애그리거트를 함께 읽어 계산할 때 쓰고, 한 애그리거트 안의 규칙은 루트에 둔다.
- 애그리거트 안쪽 객체는 업무 영역을 나누는 하위 도메인과 다르다. 안쪽 객체는 한 묶음 안의 부품이고, 하위 도메인(Subdomain)은 17장에서 배울 주문, 상품, 회원 같은 업무 영역이다.
- OrderLine은 번호가 없는 record라 값 객체 모양이지만, 혼자 저장되거나 밖에서 바뀌지 않고 주문을 거쳐서만 다뤄진다.
##### 이 예제에서는
- 주문과 주문 항목이 한 애그리거트이고, Order가 루트, OrderLine이 안쪽 객체다.

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
##### 무엇
- 불변식(Invariant)은 객체가 어떤 순간에도 늘 참이어야 하는 조건이다.
- 주문의 불변식은 주문 금액이 항목 금액의 합과 같다는 것, 그리고 주문 접수인 주문에만 항목이 더해진다는 것이다.
##### 왜
- 앞 단계에서는 목록을 밖에 내주어 이 조건이 깨졌다. 지키는 방법은 목록을 바꾸는 길을 루트 한 곳으로 모으는 것이다.
##### 쉬운 방법과 비교
- 쉬운 방법은 OrderService에 항목 더하기를 만들고, 그 안에서 상태를 검사한 뒤 목록에 더하고 합계를 다시 계산하는 것이다. 2장처럼 서비스가 규칙을 드는 모양이다.
- Main이 늘 서비스만 부른다면 지금 6장에서도 이것으로 된다.
- 하지만 getOrderLines()가 고칠 수 있는 목록을 내주는 한, 서비스를 거치지 않는 add를 막지 못한다. 이 장 첫 문제 장면이 바로 그 길이다.
- 또 다른 쉬운 방법은 저장하기 전에 isValid() 같은 검사를 한 번 부르는 것이다. 8장부터 주문은 저장소에 저장되므로 거기서 걸러도 된다고 생각하기 쉽다.
- 차이는 잘못된 주문이 저장 전까지 살아서 쓰인다는 점이다. 13장 결제하기는 저장하기 전에 주문 금액으로 할인 금액을 정하고, 19장은 저장하기 전에 결제 창구에 돈을 넘긴다.
- Collections.unmodifiableList(orderLines)로 내줘도 add는 막힌다. 다만 그것은 복사본이 아니라 주문 안 목록을 비춰 보는 창이라, 뒤에 주문이 항목을 더하면 먼저 꺼낸 목록에도 보인다. 이 예제는 꺼낸 순간의 목록을 주려고 List.copyOf를 쓴다.
- 항목 하나만 보고 판단할 수 있는 조건은 루트까지 올릴 필요 없이 그 객체의 생성자 검사로 충분하다. 다음 단계 OrderLine의 수량 1개 이상이 그렇다.
##### 헷갈리는 개념
- 앞 장의 불변과 이름이 비슷하지만 다르다. 불변은 값이 안 바뀌는 성질이고, 불변식은 값이 바뀌어도 지켜져야 하는 조건이다.
- 한 애그리거트 안의 불변식은 바뀌는 그 순간에 지킨다. 15장의 재고처럼 다른 애그리거트와 맞추는 일은 그 자리에서 지키지 않고 이벤트로 나중에 맞춘다. 15장의 트랜잭션 경계와 최종 일관성이 이 차이에서 출발한다.
##### 이 예제에서는
- addOrderLine이 상태를 검사하고, 목록에 더하는 일과 금액을 더하는 일을 한 메서드에서 함께 한다.
- 7장에서 상태 검사가 OrderStatus.canChangeTo로 모이지만, 항목 더하기의 주문 접수 검사는 addOrderLine에 그대로 남는다.

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
##### 무엇
- 상태 전이(State Transition)는 지금 상태에서 갈 수 있는 다음 상태가 정해져 있다는 규칙이다.
- 상태를 점으로, 갈 수 있는 길을 화살표로 그린 표라고 보면 된다.
##### 왜
- 앞 장까지는 배송은 결제 완료인지, 취소는 배송 중이 아닌지를 메서드마다 따로 확인했다.
- 그래서 결제에는 확인이 아예 없었고, 결제 완료인 주문을 또 결제해도 막히지 않았다.
- 같은 이유로 취소한 주문을 또 취소해도 막히지 않았다. 앞 장 cancel은 배송 중인지만 봤기 때문이다.
- 규칙을 상태 쪽 한 표에 모으면 어느 화살표가 빠졌는지 한눈에 보인다.
##### 쉬운 방법과 비교
- 앞 장처럼 pay 안에 if (status != OrderStatus.ORDERED)를 하나 더 써도 지금 T2의 두 번 결제는 똑같이 막힌다.
- 차이는 상태가 늘 때 드러난다. T3에서 배송 완료 DELIVERED가 더해진다.
- 앞 장 모양의 cancel은 배송 중일 때만 거절하므로 배송 완료된 주문도, 이미 취소한 주문도 취소된다. 새 상태를 더한 사람이 cancel까지 찾아가 고쳐야 한다는 것을 아무도 알려 주지 않는다.
- 표로 모으면 상수를 더하는 순간 switch 식이 컴파일 오류를 내서 새 상태의 화살표를 정하게 된다.
- 21장에서는 취소하면 OrderCanceled 이벤트로 재고를 다시 늘린다. 취소한 주문을 또 취소할 수 있었다면 재고가 두 번 늘었을 것이다.
- 상태가 두세 개이고 막는 규칙이 하나뿐이면 메서드 안 if 하나가 더 단순하다. 표는 상태와 화살표가 늘어날 때 값을 한다.
##### 헷갈리는 개념
- 행동 메서드와 다르다. 행동 메서드는 결제하라는 일을 받는 입구이고, 상태 전이는 그 일이 지금 상태에서 허락되는지 답하는 표다.
- 불변식과도 다르다. 불변식은 주문 금액이 항목 합과 같다처럼 늘 참이어야 하는 것이고, 상태 전이는 바뀌는 순간에만 따지는 규칙이다.
- 상태 패턴(State Pattern)과도 다르다. 상태 패턴은 상태마다 클래스를 하나씩 만들어 행동을 나눈다. 여기서는 enum 하나의 switch 식으로 충분하다.
##### 이 예제에서는
- OrderStatus의 canChangeTo가 상태 전이 표다.
- Order의 pay, ship, cancel은 첫 줄에서 이 표에 묻고, 안 되면 거절한다.

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
##### 무엇
- 리포지터리(Repository)는 애그리거트를 넣어 두고 식별자로 다시 꺼내는 곳이다.
- 어디에 어떻게 두는지는 숨기고 저장하기, 번호로 찾기라는 약속만 보여 준다.
##### 왜
- 앞 장까지 Main은 주문 객체를 변수로 들고 다녔다. 프로그램이 주문을 잊지 않으려면 누군가 계속 쥐고 있어야 했다.
- 실제 쇼핑몰은 주문 번호만 받고 주문을 다시 찾아 일을 시킨다. 그 찾는 곳이 리포지터리다.
##### 쉬운 방법과 비교
- 인터페이스 없이 OrderService 안에 Map<Long, Order> 칸을 두고 put과 get을 해도 지금 8장의 출력은 똑같다.
- 차이는 10장부터 드러난다. 10장에서 저장 방법을 인프라 계층으로 옮길 때 서비스 안 Map은 떼어 낼 자리가 없다. 인터페이스로 나눠 두면 MemoryOrderRepository 파일만 옮긴다.
- 11장에서 회원과 상품도 저장해야 한다. Map을 서비스마다 두면 꺼내고 넣는 코드가 여기저기 생긴다. 저장소 약속으로 두면 MemberRepository, ProductRepository가 같은 모양으로 붙는다.
- 16장의 낙관적 잠금은 저장할 때 버전을 비교한다. 이 검사는 MemoryOrderRepository의 save 한 곳에만 들어가고 응용 서비스는 고치지 않는다.
- 20장의 주문 목록 조회는 findByMemberId 하나를 약속에 더해서 해결한다.
- T3의 복사 저장도 저장소가 감싸고 있어서 가능하다. 그냥 Map이면 꺼낸 주문과 넣어 둔 주문이 같은 객체라 저장을 빠뜨린 버그가 숨는다.
- 주문을 한 번 만들고 바로 찍고 끝나는 짧은 프로그램이면 변수로 들고 다니는 쪽이 더 단순하다. 다시 찾을 일이 없으면 저장소는 짐이다.
##### 헷갈리는 개념
- 테이블마다 만드는 DAO와 다르다. 리포지터리는 애그리거트 루트마다 하나다. 주문 항목 저장소는 따로 없고 주문을 통째로 넣고 꺼낸다.
- 그냥 컬렉션과도 다르다. 컬렉션은 넣은 객체를 그대로 돌려주지만 리포지터리는 저장한 때의 모습을 돌려준다. T3에서 눈으로 본다.
##### 이 예제에서는
- OrderRepository 인터페이스가 약속이고 MemoryOrderRepository가 메모리에 두는 구현이다.

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
##### 무엇
- 응용 서비스(Application Service)는 유스케이스 하나를 처음부터 끝까지 순서대로 진행하는 클래스다.
- 하는 일은 셋뿐이다. 저장소에서 꺼내고, 도메인 객체에게 시키고, 저장한다. 업무 규칙은 갖지 않는다.
##### 왜
- 앞 장의 OrderService는 이미 이 모양이었지만 밖에서 도메인 객체를 받았다. 그래서 Main이 돈과 주소, 주문 항목을 직접 만들어야 했다.
- 이제 명령을 받아 도메인 객체로 바꾸는 일까지 응용 서비스가 한다. 유스케이스의 입구가 한 곳이 된다.
##### 쉬운 방법과 비교
- 앞 장처럼 Main이 new Money, new OrderLine을 만들어 넘겨도 지금 9장의 출력은 같다. 틀린 값은 어느 쪽이든 도메인이 같은 말로 거절한다.
- 차이는 유스케이스가 길어질 때 드러난다. 12장에서는 주문 항목의 이름과 가격을 상품 저장소에서 가져와야 한다. Main이 주문 항목을 만들면 Main이 상품 저장소를 알아야 하고, 가격을 마음대로 넣는 길도 남는다.
- 13장의 결제하기는 주문 꺼내기, 회원 꺼내기, 할인 계산, 결제, 저장으로 늘어난다. 15장에서 이벤트 발행이, 19장에서 외부 결제 부르기가, 21장에서 결제 거절 때 취소하기가 더해진다.
- 이 순서를 부르는 쪽이 직접 쓰면 부르는 곳마다 같은 순서를 복사해야 한다. 22장에서는 Main과 OrderApp 두 곳이 주문을 다루는데, 둘 다 OrderApplicationService를 만들어 같은 순서를 쓴다.
- 반대로 ship, deliver는 꺼내기, 시키기, 저장하기 세 줄뿐이라 지금은 그냥 건네주는 일만 한다. 유스케이스가 모두 이 정도로 짧고 부르는 곳이 하나면 Main이 저장소와 주문을 직접 다루는 쪽이 더 단순하다.
##### 헷갈리는 개념
- 엔티티의 행동 메서드와 다르다. 결제할 수 있는지는 주문이 판단하고, 응용 서비스는 결제하라고 시키기만 한다.
- 애그리거트 루트와도 다르다. 루트는 주문 항목과 주문 금액이 서로 맞게 지키고, 응용 서비스는 루트를 꺼내고 저장하는 바깥 일만 한다.
- 도메인 서비스(Domain Service)와도 다르다. 도메인 서비스는 한 애그리거트에 넣기 어색한 규칙을 계산만 하고, 꺼내기와 저장하기는 하지 않는다. 13장 discount에서 나온다.
- 규칙이 응용 서비스에 들어가면 2장의 빈약한 주문으로 돌아간다. if로 값을 검사하는 줄이 응용 서비스에 생기면 의심한다.
##### 이 예제에서는
- placeOrder가 명령을 주문 항목과 주소로 바꿔 주문을 만들고 저장하는 것이 응용 서비스의 일이다.

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
##### 무엇
- 계층 구조(Layered Architecture)는 코드를 맡은 일에 따라 층으로 나누고, 위층이 아래층만 쓰게 하는 구조다.
- 이 장의 층은 셋이다. 도메인 계층은 업무 규칙, 응용 계층은 유스케이스 순서, 인프라 계층은 저장 같은 기술을 맡는다.
- 도메인 계층은 다른 층을 모른다. 그래서 저장 방법이나 유스케이스가 바뀌어도 업무 규칙은 그대로다.
##### 왜
- 지금까지는 모든 클래스가 ddd 패키지 하나에 섞여 있었다. 어느 것이 업무 규칙이고 어느 것이 저장 방법인지 이름으로만 짐작해야 했다.
- 한 패키지 안에서는 Order가 MemoryOrderRepository를 써도 import 줄 하나 생기지 않는다. 잘못된 방향이 코드에 드러나지 않는다.
##### 쉬운 방법과 비교
- ddd 패키지 하나에 그대로 두어도 지금 10장의 출력은 한 글자도 다르지 않다. 이 장은 동작이 아니라 자리만 바꾼다.
- 차이는 클래스가 늘 때 드러난다. 11장에서 회원과 상품, 13장에서 할인, 14장에서 무료 배송 명세, 15장에서 이벤트가 더해져 9장에 11개이던 자바 파일이 15장에는 27개가 된다.
- 패키지로 나눠 두면 import 줄이 방향을 보여 준다. domain 패키지 파일에 ddd.application이나 ddd.infrastructure import가 보이면 바로 잘못된 것이다.
- 17장에서는 주문, 상품, 회원, 배송으로 먼저 나누고 그 안을 다시 domain, application, infrastructure로 나눈다. 이 장에서 만든 세 층이 컨텍스트마다 그대로 쓰인다.
- 클래스가 열 개 안쪽이고 저장 방법을 바꿀 일이 없으면 한 패키지가 더 단순하다. 패키지를 오가며 import를 맞추는 일도 비용이다.
##### 헷갈리는 개념
- 하위 도메인, 바운디드 컨텍스트와 다르다. 그것들은 주문, 배송처럼 업무 영역으로 나누는 것이고, 계층은 한 영역 안을 하는 일의 종류로 나누는 것이다. 17장에서 둘을 함께 쓴다.
- 패키지로 나눈다고 자바가 잘못된 import를 막아 주지는 않는다. 방향은 사람이 지키고 import 줄로 확인한다.
##### 이 예제에서는
- ddd.domain, ddd.application, ddd.infrastructure 세 패키지가 세 층이고, 이 단계에서 도메인 계층부터 만든다.

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
##### 무엇
- 의존성 역전(Dependency Inversion Principle)은 규칙을 가진 쪽이 약속을 정하고, 기술을 가진 쪽이 그 약속을 구현하게 하는 것이다.
##### 왜
- 보통 순서대로 짜면 업무 코드가 저장 코드를 부른다. 그러면 저장 방법을 바꿀 때 업무 코드까지 고쳐야 한다.
- 여기서는 도메인 계층이 주문 저장소 인터페이스를 갖고, 인프라 계층이 implements OrderRepository로 따른다.
- 그래서 import 화살표가 인프라에서 도메인으로 향한다. 도메인과 응용 계층에는 ddd.infrastructure import가 하나도 없다.
##### 쉬운 방법과 비교
- 쉬운 방법은 둘이다. 인터페이스 없이 응용 서비스가 MemoryOrderRepository를 바로 쓰거나, 인터페이스를 구현과 함께 ddd.infrastructure에 두는 것이다.
- 둘 다 지금 10장에서는 똑같이 돌아간다. 구현이 메모리 저장소 하나뿐이기 때문이다.
- 대신 응용 계층에 ddd.infrastructure import가 생긴다. 저장 방법을 바꾸면 응용 서비스도 함께 고쳐야 한다.
- 16장에서 낙관적 잠금의 버전 검사는 MemoryOrderRepository의 save에만 들어간다. 응용 서비스는 저장소 약속만 알아서 손대지 않는다.
- 19장이 가장 큰 장면이다. 외부 결제 legacypay는 우리 말과 다른 모양이다. 결제 약속 PaymentGateway를 order.domain에 두고, LegacyPaymentAdapter가 order.infrastructure에서 그 약속을 구현한다. 그래서 응용 서비스와 도메인에는 legacypay import가 없다.
- 바꿔 끼울 일이 없고 도메인이 쓰지 않는 기술이면 인터페이스가 짐이다. 15장의 EventPublisher는 응용 계층만 쓰는 클래스라 인터페이스 없이 응용 계층에 있고, 22장에서는 그 클래스를 상속한 SocketEventPublisher로 바꿔 끼운다.
##### 헷갈리는 개념
- 그냥 인터페이스를 쓰는 것과 다르다. 인터페이스를 어느 계층에 두느냐가 핵심이다. 인터페이스가 인프라에 있으면 도메인이 여전히 인프라를 알아야 한다.
- 의존성 주입과도 다르다. 의존성 주입은 Main이 new MemoryOrderRepository()를 만들어 생성자로 넣어 주는 방법이다. 의존성 역전은 약속을 누가 갖느냐의 문제이고, 주입은 구현을 누가 골라 넣느냐의 문제다.
##### 이 예제에서는
- 저장소를 DB로 바꿔도 인프라 계층에 새 구현을 더하고 Main의 new 한 줄만 바꾸면 된다.

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
##### 무엇
- 애그리거트 간 ID 참조는 한 애그리거트가 다른 애그리거트를 객체로 쥐지 않고 번호만 들고 있는 것이다.
- 회원 정보가 필요하면 쓰는 쪽이 회원 저장소에서 번호로 따로 꺼낸다.
##### 왜
- 주문이 Member 객체를 칸으로 쥐면 주문을 꺼낼 때 회원까지 딸려 오고, 주문을 저장할 때 회원도 함께 저장될 수 있다.
- 그러면 주문 애그리거트의 경계가 회원까지 번져 어디까지가 한 번에 꺼내고 저장하는 단위인지 흐려진다.
##### 쉬운 방법과 비교
- 쉬운 방법은 주문에 Member member 칸을, 주문 항목에 Product product 칸을 두는 것이다. 이름이 필요하면 order.getMember().getName()으로 바로 꺼낸다.
- 지금 11장에서도 그렇게 할 수는 있지만 바로 걸린다. 8장의 주문 저장소는 주문을 바이트로 복사한다. Member와 Product에도 Serializable을 붙여야 하고, 주문을 저장할 때마다 회원과 상품의 복사본이 주문 안에 함께 굳는다.
- 그러면 회원 저장소의 회원과 주문 안의 회원이 따로 논다. 회원 이름이 바뀌어도 주문 안 회원은 옛 이름이다.
- 15장에서는 주문 뒤 이벤트로 상품 재고를 줄인다. 주문 안에 상품이 들어 있으면 상품 저장소의 재고와 주문 안 상품의 재고가 두 개가 된다.
- 16장의 낙관적 잠금은 주문에만 버전을 둔다. 주문이 회원과 상품까지 쥐면 주문을 저장하며 회원과 상품도 함께 덮어쓸 수 있어 한 버전으로 지킬 수 없다.
- 17장에서 주문, 상품, 회원을 다른 컨텍스트로 나눈다. Order는 ddd.shared.Money 말고 다른 컨텍스트를 import 하지 않아서 그대로 옮겨진다. 18장 배송 컨텍스트도 메시지로 주문 번호만 받는다.
- 대신 번호만 들면 쓰는 쪽이 매번 꺼내야 한다. T2의 Main은 이름을 찍으려고 회원 저장소를 한 번 더 찾고, 20장의 OrderQueryService도 주문마다 회원을 꺼낸다.
- 늘 함께 만들어지고 함께 바뀌는 것은 객체로 쥐는 쪽이 맞다. 주문 항목이 그렇다. 따로 꺼내는 일이 번거롭다면 그것이 원래 한 애그리거트였는지 다시 본다.
##### 헷갈리는 개념
- 애그리거트 안쪽 객체와 다르다. 주문 항목은 주문 안에 들어가 주문과 함께 저장되고, 회원과 상품은 밖에 있어서 번호로만 가리킨다.
- 값 객체를 담는 것과도 다르다. 주문 항목의 상품 이름과 가격은 주문 때의 값을 복사한 것이고 상품 엔티티를 가리키는 것이 아니다. 상품을 가리키는 것은 productId 하나뿐이다.
##### 이 예제에서는
- 주문의 memberId와 주문 항목의 productId가 ID 참조다. 주문 코드에는 Member와 Product import가 없다.

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
##### 무엇
- 팩토리는 객체를 만드는 일이 복잡할 때 그 만드는 일만 맡는 도메인 객체다.
##### 왜
- 앞 장에서는 응용 서비스가 명령의 이름과 가격 숫자로 주문 항목을 조립해서, 주문하는 쪽이 아무 가격이나 넣을 수 있었다.
- 주문 항목을 바르게 만들려면 다른 애그리거트인 상품을 저장소에서 꺼내 지금 이름과 가격을 읽어야 한다.
##### 쉬운 방법과 비교
- 가장 쉬운 방법은 응용 서비스의 toOrderLine 안에서 productRepository.findById로 상품을 꺼내 new OrderLine을 만드는 것이다.
- 지금 12장에서는 이렇게 해도 출력이 같다. 주문하기와 주문 항목 더하기가 같은 private 메서드를 쓰니 상품이 없습니다 거절도 한 곳에 둔다.
- 솔직히 이 예제에서 OrderLineFactory를 쓰는 곳은 OrderApplicationService 하나뿐이라 동작의 차이는 없다.
- 차이는 규칙의 자리다. 주문 항목에는 상품의 지금 이름과 가격을 담는다는 것은 업무 규칙이다. 팩토리는 도메인 계층에 있어서 규칙이 도메인에 남고, 응용 서비스는 꺼내고 시키고 저장하는 일만 한다.
- 17장에서 주문과 상품을 다른 컨텍스트로 나누면 주문 쪽에서 상품 컨텍스트를 쓰는 곳은 order.domain에서 OrderLineFactory 하나다. 상품을 읽는 방법이 바뀌면 이 파일만 본다.
- 다른 쉬운 방법은 Order 생성자가 ProductRepository를 받아 스스로 상품을 찾는 것이다. 그러면 엔티티가 저장소를 칸으로 쥐게 되고, 8장의 복사 저장이 저장소까지 직렬화하려다 실패한다.
- 만드는 데 다른 애그리거트도 저장소도 필요 없으면 생성자로 충분하다. Money, Address, Order는 지금도 new로 만든다.
##### 헷갈리는 개념
- 생성자와 다르다. 생성자는 받은 값으로 자기 칸만 채우고 저장소를 모른다. 주문 항목 record가 저장소를 알게 되면 값 객체가 무거워진다.
- 응용 서비스와도 다르다. 상품의 지금 가격으로 주문 항목을 만든다는 것은 업무 규칙이라 도메인 계층에 둔다.
- 13장의 도메인 서비스와도 다르다. 둘 다 도메인 계층에 있고 자기 상태가 거의 없지만, 팩토리는 새 객체를 만들어 돌려주고 도메인 서비스는 할인 금액 같은 계산 결과를 돌려준다.
- 정적 팩토리 메서드와도 다르다. OrderLine.of(...) 같은 static 메서드는 만드는 방법에 이름을 붙일 뿐이고, 저장소처럼 바깥 것을 받아 쓰려면 별도 클래스가 낫다.
##### 이 예제에서는
- OrderLineFactory가 상품 번호와 수량을 받아 상품을 찾고 주문 항목을 만든다.

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
##### 무엇
- 도메인 서비스(Domain Service)는 업무 규칙인데 엔티티나 애그리거트 하나에 넣기 어색한 규칙을 계산하는 도메인 객체다.
- 도메인 서비스는 계산만 한다. 저장소를 쓰지 않고, 받은 객체를 바꾸지 않고, 결과 값을 돌려줄 뿐이다.
- 칸이 없어 기억하는 상태도 없다. 같은 회원과 주문을 넣으면 늘 같은 돈이 나온다.
##### 왜
- VIP는 주문 금액의 10%를 깎는다는 규칙은 회원의 등급과 주문의 금액을 함께 봐야 한다.
- 규칙이 두 애그리거트에 걸쳐 있어서, 회원과 주문 중 어느 한쪽이 혼자 답할 수 없다.
##### 쉬운 방법과 비교
- 가장 먼저 떠올릴 방법은 OrderApplicationService.pay 안에 if (member.getGrade() == MemberGrade.VIP)를 쓰고 10%를 바로 계산하는 것이다.
- 지금 13장에서는 이렇게 해도 할인: VIP 3100원, 결제 금액 27900원이 똑같이 찍힌다.
- 그러면 10%라는 업무 숫자가 응용 서비스에 들어간다. 2장 빈약한 주문에서 규칙이 서비스에 흩어진 모양으로 한 걸음 돌아간다.
- 21장 where-rules-live.md는 응용 서비스에 규칙을 두지 않는다고 정리한다. 할인 규칙이 응용 서비스에 있으면 이 표에 맞는 자리가 없다.
- 둘째로 떠올릴 방법은 Order.pay(Member member)로 회원을 넘겨 주문이 직접 할인하는 것이다.
- 이러면 주문이 Member를 알게 된다. 11장에서 다른 애그리거트는 번호로만 가리키기로 했는데, 주문 코드에 Member 타입이 들어온다.
- 17장에서 차이가 커진다. 컨텍스트를 나누면 주문 컨텍스트의 중심인 Order가 ddd.member.domain을 import하게 된다. 도메인 서비스로 두면 17장 시점에 회원을 아는 주문 쪽 클래스는 DiscountPolicy와 응용 서비스 둘뿐이고, Order의 import는 ddd.shared.Money 하나다.
- 셋째 방법은 Member.discount(Order order)처럼 회원에 두는 것이다. 17장에서 회원 컨텍스트가 주문 컨텍스트를 import하게 되어, 일반 하위 도메인인 회원이 핵심인 주문에 묶인다.
- 할인 규칙이 등급 하나, 쓰는 곳 하나이고 바뀔 일도 없다면 응용 서비스의 if 한 줄이 더 짧고 읽기 쉽다. 클래스 하나를 더 두는 데도 비용이 있다.
##### 헷갈리는 개념
- 12장 팩토리와도 다르다. 팩토리는 상품 저장소를 써서 주문 항목을 만들어 내고, 도메인 서비스는 저장소 없이 받은 객체를 읽어 값을 계산한다.
- 이름에 서비스가 붙었다고 모두 도메인 서비스는 아니다. 9장 응용 서비스와의 차이는 아래 규칙을 두는 네 자리에서 함께 정리한다.
##### 이 예제에서는
- DiscountPolicy의 discount가 도메인 서비스다.
- 회원을 꺼내는 일은 응용 서비스가 하고, DiscountPolicy는 꺼낸 회원과 주문을 받아 할인 금액 Money만 돌려준다.

#### 개념 규칙을 두는 네 자리
##### 무엇
- 지금까지 규칙을 둔 자리는 넷이다. 엔티티 행동 메서드, 애그리거트 루트, 도메인 서비스, 응용 서비스다.
- 이 단계의 결제하기 한 번에 네 자리가 모두 나온다.
##### 왜
- 자리가 넷이 되면 새 규칙을 어디에 둘지 헷갈린다. 고르는 기준이 없으면 가장 쓰기 쉬운 응용 서비스에 쌓인다.
##### 쉬운 방법과 비교
- 쉬운 방법은 자리를 고르지 않고 모든 규칙을 응용 서비스 메서드 안에 if로 쓰는 것이다. 2장 OrderService가 그 모양이었다.
- 메서드 하나에서 위에서 아래로 다 보이니 처음에는 오히려 읽기 쉽다.
- 차이는 서비스를 거치지 않는 길이 생길 때 난다. 4장의 문제는 결제 검사가 서비스에만 있어서, setStatus를 바로 부르면 결제 없이 배송된 것이었다.
- 뒤 장에서는 자리가 더 늘어난다. 14장 명세, 15장 이벤트, 21장 사가가 더해지고 21장 where-rules-live.md가 일곱 자리를 한 표로 정리한다.
##### 헷갈리는 개념
- 엔티티 행동 메서드는 자기 상태를 자기 규칙으로 바꾼다. Order.pay가 결제할 수 있는 상태인지 따지고 결제 완료가 되는 것이다.
- 애그리거트 루트는 안쪽 객체들이 함께 맞도록 지킨다. Order.addOrderLine이 주문 항목을 더할 때 주문 금액을 같이 바꾸는 것이다.
- 행동 메서드와 루트는 같은 Order 클래스에 있어서 더 헷갈린다. 행동 메서드는 칸 하나의 규칙이고, 루트는 안쪽 객체까지 묶어 서로 맞는지 지키는 역할이다.
- 도메인 서비스는 애그리거트 하나에 넣기 어색한 규칙을 계산만 한다. DiscountPolicy가 회원과 주문을 보고 할인 금액을 내는 것이다.
- 응용 서비스는 규칙 없이 꺼내기, 시키기, 저장하기 순서만 정한다. OrderApplicationService.pay가 주문과 회원을 꺼내고 할인 정책과 주문에게 시키고 저장하는 것이다.
- 구분하는 질문은 둘이다. 이 규칙을 업무 사람이 말하는가, 그리고 한 애그리거트가 혼자 답할 수 있는가.
- 업무 사람이 말하지 않는 순서라면 응용 서비스, 한 애그리거트가 혼자 답하면 그 엔티티나 루트, 여러 애그리거트를 봐야 하면 도메인 서비스다.
##### 이 예제에서는
- OrderApplicationService.pay 안에서 응용 서비스가 주문과 회원을 꺼내고, DiscountPolicy가 계산하고, Order.pay가 상태를 바꾼다.

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
##### 무엇
- 명세(Specification)는 객체 하나가 어떤 조건을 만족하는지 참 거짓으로만 답하는 도메인 객체다.
- 조건에 업무 말로 이름을 붙인다. 무료 배송 조건은 무료 배송 명세라는 이름 하나로 읽힌다.
##### 왜
- 무료 배송 조건을 주문 안의 if 한 줄로 두면 50000이라는 숫자를 읽어야 무엇을 묻는지 안다.
- 조건과 그 조건에 따른 돈이 한 메서드에 섞인다. 조건이 늘면 if가 길어지고 돈을 정하는 줄이 묻힌다.
##### 쉬운 방법과 비교
- 가장 쉬운 방법은 Order.getShippingFee 안에 if (getPaymentPrice().amount() >= 50000)을 바로 쓰는 것이다.
- 지금 T1에서는 이것으로 충분하다. 조건이 하나라 클래스를 하나 더 만드는 것보다 짧고, 배송비: 3000원도 똑같이 찍힌다.
- 차이는 바로 다음 단계 14장 T2에서 난다. 제주는 무료 배송이 아니라는 조건이 붙는다.
- if로 두면 getShippingFee에 && !getAddress().value().startsWith("제주")가 붙는다. 명세로 두면 getShippingFee는 한 글자도 바뀌지 않고 명세만 바뀐다.
- 21장 where-rules-live.md의 고르는 순서도 참 거짓 조건을 다른 조건과 묶어 쓸 때 명세로 뺀다고 적는다.
- 조건이 하나뿐이고 한 곳에서만 묻는다면 주문 안의 if나 private boolean 메서드가 더 단순하다. 이 예제에서도 무료 배송 명세를 묻는 곳은 Order.getShippingFee 하나다.
##### 헷갈리는 개념
- 13장 도메인 서비스와 다르다. 도메인 서비스는 할인 금액 같은 값을 계산해 돌려주고, 명세는 참 거짓만 돌려준다.
- 그래서 0원이나 3000원 같은 돈은 명세가 아니라 질문한 쪽인 주문이 정한다.
- 5장 Money 생성자 같은 검사와도 다르다. 검사는 틀리면 예외를 던져 거절하고, 명세는 거절하지 않고 맞는지 아닌지만 답한다.
- 명세는 저장소에서 조건에 맞는 것을 골라낼 때도 쓰인다. 이 예제는 그렇게 쓰지 않고 주문 하나에 묻기만 한다.
##### 이 예제에서는
- FreeShippingSpecification이 주문을 받아 결제 금액이 50000원 이상인지 답하고, Order.getShippingFee가 그 답으로 0원이나 3000원을 정한다.

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
##### 무엇
- 명세가 같은 모양(인터페이스)을 따르면 작은 명세 여러 개를 and로 묶어 새 명세를 만들 수 있다.
- 묶은 결과도 다시 명세라서 또 묶을 수 있다.
##### 왜
- 무료 배송 조건이 금액과 지역 둘이 되었다. 두 조건은 이유도 바뀌는 때도 다르다.
- 제주 조건을 금액 조건 옆에 덧붙이면 두 조건이 한 메서드에 엉킨다.
##### 쉬운 방법과 비교
- 가장 쉬운 방법은 T1의 FreeShippingSpecification 한 클래스에 return 금액 조건 && !제주 조건;을 쓰는 것이다.
- 이 단계에서는 이것으로도 서울 0원, 제주 3000원이 똑같이 찍힌다. 클래스 셋과 인터페이스 하나보다 훨씬 짧다.
- 이 예제에서 금액 명세와 지역 명세는 무료 배송 명세 안에서만 쓰인다. 따로 떼어 다시 쓰는 곳은 없다. 솔직히 지금 크기에서는 && 한 줄이 더 단순하다.
- 조합이 나은 때는 조건이 세 개, 네 개로 늘거나 같은 조건을 다른 명세에서 다시 쓸 때다. 조건 하나를 클래스 하나로 더하고 and 하나만 붙이면 된다.
- 조건마다 따로 시험해 볼 수 있다는 것도 차이다. 금액 명세만, 지역 명세만 주문을 넣어 참 거짓을 볼 수 있다.
- 비용도 있다. and를 읽으려면 인터페이스의 default 메서드와 람다를 함께 알아야 한다. 학생에게는 개념보다 문법이 더 어려울 수 있다.
##### 헷갈리는 개념
- 상속과 다르다. FreeShippingSpecification이 다른 두 명세를 이어받는 것이 아니라, 두 명세 객체를 만들어 묶어 쓴다.
##### 이 예제에서는
- OrderSpecification이 모양이고, 무료 배송 명세가 금액 명세 and 지역 명세다.
- FreeShippingSpecification.isSatisfiedBy가 new FreeShippingPriceSpecification().and(new FreeShippingAreaSpecification())로 두 명세를 묶어 답한다.

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
##### 무엇
- 도메인 이벤트(Domain Event)는 도메인에서 이미 일어난 일을 이름 붙은 값으로 남긴 것이다.
- 이미 일어난 일이라 이름을 과거형으로 짓는다. OrderPlaced는 주문이 접수되었다는 뜻이다.
##### 왜
- 주문하면 재고가 줄어야 한다. 주문이 상품을 꺼내 재고를 줄이면 주문 애그리거트가 상품 애그리거트까지 바꾼다.
- 주문은 주문 접수됨이라는 사실만 남기고, 재고는 그 사실을 들은 쪽이 따로 줄인다.
##### 쉬운 방법과 비교
- 가장 쉬운 방법은 OrderApplicationService.placeOrder에서 주문을 저장한 다음 줄에 상품을 꺼내 decreaseStock을 부르고 상품을 저장하는 것이다.
- 지금 15장에서는 이렇게 해도 자바 책 재고가 8개가 된다. 이벤트 record, 발행기, 처리기가 모두 없어서 훨씬 짧다.
- 대신 주문 응용 서비스가 상품 저장소까지 받아야 하고, 주문하기 코드가 재고라는 다른 업무를 알게 된다.
- 17장에서 차이가 난다. 재고 줄이기 처리기는 상품 컨텍스트 ddd.catalog.application으로 간다. 직접 부르는 방식이면 재고를 줄이는 코드가 주문 컨텍스트에 남는다.
- 21장에서 결제가 실패하면 재고를 되돌려야 한다. 이벤트로 두었기 때문에 취소하기가 OrderCanceled를 넘기고, 상품 쪽은 IncreaseStockHandler를 Main에서 구독만 하면 된다.
- 18장에서는 같은 발행기가 배송 컨텍스트에 결제 완료 메시지를 나르고, 22장에서는 발행기를 바꿔 끼워 다른 프로그램으로 보낸다. 직접 부르기로는 다른 프로그램의 코드를 부를 수 없다.
- 쉬운 방법이 나은 때도 있다. 재고가 모자라면 주문 자체를 막아야 하는 업무라면 주문하는 순간 재고를 바로 봐야 한다.
- 이벤트로 나중에 맞추면 모자란 것을 주문 뒤에야 안다. 이 예제가 재고 모자란 주문 막기를 미룬 이유다.
##### 헷갈리는 개념
- 명령과 다르다. 주문하기 명령은 해 달라는 부탁이라 거절될 수 있고, 이벤트는 이미 일어난 일이라 거절하거나 바꾸지 않는다.
- 이벤트 발행기와도 다르다. 이벤트는 일어난 일을 담은 값이고, 발행기는 그 값을 받을 곳에 나르는 기술이다. 그래서 발행기는 붙여넣기로 받는다.
##### 이 예제에서는
- 주문이 만들어질 때 생성자 끝에서 OrderPlaced가 주문 번호와 주문 항목을 담아 남는다.

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
##### 무엇
- 트랜잭션(Transaction)은 함께 성공하거나 함께 실패해야 하는 일 한 묶음이고, 경계는 그 묶음이 어디서 끝나는지다.
- 이 예제는 메모리 저장소라 실패하면 되돌리는 진짜 트랜잭션이 없다. 저장 한 번을 트랜잭션 하나로 보고, 경계를 코드 순서로 보인다.
##### 왜
- 주문 저장과 재고 줄이기를 한 묶음으로 하면 주문과 상품 두 애그리거트를 한 번에 바꾼다. 하나가 실패하면 둘 다 되돌려야 해서 묶음이 커진다.
- 애그리거트 하나를 한 트랜잭션에서 바꾸는 것이 기본이다.
##### 쉬운 방법과 비교
- 쉬운 방법은 주문 저장과 재고 줄이기를 한 트랜잭션으로 묶는 것이다. 실제 DB를 쓰는 프로그램이라면 메서드 하나에 트랜잭션을 걸면 된다.
- 이 방법은 주문과 재고가 언제나 함께 맞는다는 장점이 있다. 주문은 있는데 재고는 그대로인 때가 없다.
- 비용은 묶음이 커지는 것이다. 인기 상품 하나를 여러 주문이 동시에 바꾸면 서로 기다리거나 충돌한다. 16장에서 같은 주문을 두 곳이 바꿀 때 한쪽이 거절되는 것을 보는데, 묶음이 클수록 이런 충돌이 잦아진다.
- 컨텍스트가 다른 프로그램이 되면 아예 한 트랜잭션으로 묶을 수 없다. 22장에서 배송 컨텍스트가 소켓 너머 다른 프로그램이 된다.
- 나눈 대가는 21장에서 나온다. 결제가 거절되어도 재고는 이미 줄어 있다. 한 트랜잭션이었다면 롤백 한 번이면 되지만, 나눴기 때문에 사가와 보상이 필요하다.
- 하나의 DB를 쓰는 작은 프로그램이고 주문과 재고가 잠깐도 어긋나면 안 되는 업무라면 한 트랜잭션이 더 단순하고 낫다.
##### 헷갈리는 개념
- 애그리거트 경계와 다르다. 애그리거트 경계는 일관성을 지키는 범위이고, 트랜잭션 경계는 한 번에 저장하는 범위다. 기본은 둘을 같게 맞추는 것이다.
- 메서드 하나가 곧 트랜잭션 하나는 아니다. placeOrder는 저장 뒤에 이벤트 넘기기까지 하지만 경계는 저장에서 끝난다.
##### 이 예제에서는
- 주문하기의 경계는 orderRepository.save에서 끝난다. 이벤트는 그 뒤에 발행기에 쌓이기만 하고, 재고 줄이기는 이 경계 밖에서 따로 저장한다.

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
##### 무엇
- 최종 일관성(Eventual Consistency)은 지금 이 순간에는 데이터가 서로 안 맞아도 조금 뒤에는 반드시 맞춰지는 것이다.
##### 왜
- 주문 저장과 재고 줄이기를 다른 트랜잭션으로 나누면 그 사이에 주문은 있는데 재고는 그대로인 때가 생긴다.
- 이 어긋남을 업무에서 받아들이기로 정한 것이 최종 일관성이다.
##### 쉬운 방법과 비교
- 쉬운 방법은 발행기가 쌓아 두지 않고 publish하는 자리에서 바로 처리기를 부르게 하는 것이다. 그러면 주문 뒤 줄에도 재고 8개가 찍혀 어긋난 때가 보이지 않는다.
- 이 예제의 발행기는 일부러 publish와 processEvents를 나눴다. 어긋난 때를 주문 뒤 줄과 이벤트 처리 뒤 줄 사이로 보이기 위해서다.
- 바로 불러도 주문과 상품은 따로 저장된다. 어긋난 때가 짧아질 뿐 없어지지 않는다.
- 18장에서 차이가 보인다. 앞에서 결제한 1번과 4번 주문의 결제 완료 메시지가 processEvents 때 한꺼번에 처리된다. 22장에서는 메시지가 소켓을 건너 다른 프로그램에 늦게 닿는다.
- 21장에서는 맞추다 실패하는 장면이 나온다. 결제 실패 뒤에도 노트북 재고가 9개로 줄어 있고, 사가로 10개로 되돌린다.
- 주문 직후 재고 수가 바로 맞아야 하는 업무라면 최종 일관성보다 한 트랜잭션이 낫다.
##### 헷갈리는 개념
- 즉시 일관성과 다르다. 애그리거트 안의 주문 금액과 주문 항목은 언제 봐도 맞아야 하지만, 주문과 재고는 잠깐 어긋나도 된다.
- 21장 사가와도 다르다. 최종 일관성은 성공한 일이 나중에 맞춰지는 것이고, 실패했을 때 되돌리는 길은 사가가 정한다.
##### 이 예제에서는
- 재고 줄이기 처리기 DecreaseStockHandler가 주문 접수됨 이벤트를 받아 나중에 재고를 맞춘다.
- 주문 뒤 줄의 재고 10개와 이벤트 처리 뒤 줄의 재고 8개 사이가 어긋난 때다.

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
##### 무엇
- 낙관적 잠금(Optimistic Lock)은 꺼낼 때는 잠그지 않고, 저장할 때 그사이 누가 먼저 바꿨는지 확인해 낡은 저장을 거절하는 방법이다.
- 비교에 쓰는 것이 버전이다. 저장할 때마다 1씩 올라서, 꺼낸 주문이 몇 번째 저장을 보고 바뀌었는지 알려 준다.
##### 왜
- 앞 단계처럼 같은 주문을 두 곳에서 들고 있으면 나중에 저장한 쪽이 먼저 저장한 내용을 말없이 지운다.
- orderB의 취소는 orderB 안에서는 결제 완료를 취소하는 것이라 규칙에 맞는다. 두 복사본이 서로를 모르면 애그리거트의 규칙도 이 덮어쓰기를 막지 못한다.
##### 쉬운 방법과 비교
- 첫째 쉬운 방법은 저장소가 복사하지 않고 같은 객체를 돌려주는 것이다. 그러면 orderA와 orderB가 같은 객체라 cancel이 배송 중인 주문은 취소할 수 없습니다로 거절된다.
- 하지만 실제 DB는 꺼낼 때마다 새 객체를 만든다. 8장 T3에서 저장소가 복사하게 한 것도 DB처럼 보이기 위해서였다. 이 방법은 메모리에서만 통한다.
- 둘째 쉬운 방법은 저장할 때 버전 대신 주문 상태를 비교하는 것이다. 이 단계의 장면은 저장된 상태가 배송 중이라 막힌다.
- 그런데 상태는 같은 채로 다른 칸이 바뀌면 못 막는다. 주문 접수인 주문을 두 곳에서 꺼내 각각 addOrderLine을 하면 둘 다 주문 접수라서, 나중 저장이 먼저 더한 항목을 지운다.
- 버전은 무엇이 바뀌었든 저장 횟수로 보기 때문에 이 경우도 거절한다.
- 잠금 자체가 필요 없는 때도 있다. 한 사람이 차례로 쓰는 프로그램이면 동시 수정이 없다. 이 예제의 Main도 한 줄씩 차례로 실행해서 장면을 일부러 만든 것이다.
##### 헷갈리는 개념
- 비관적 잠금(Pessimistic Lock)과 다르다. 비관적 잠금은 꺼낼 때 잠가서 다른 쪽이 기다리게 하고, 낙관적 잠금은 충돌이 드물다고 보고 저장할 때만 비교한다.
- 충돌이 잦으면 비관적 잠금이 낫다. 낙관적 잠금은 거절된 쪽이 다시 꺼내 다시 해야 하는데, 충돌이 잦으면 거절과 다시 하기가 계속 생긴다. 이 예제는 다시 하기도 비관적 잠금도 만들지 않는다.
- 6장 애그리거트 불변식과 다르다. 불변식은 주문 객체 하나 안이 늘 맞게 지키고, 낙관적 잠금은 같은 주문의 두 복사본이 저장소에서 부딪치는 것을 막는다.
- 버전은 주문 상태 같은 업무 말이 아니다. 업무 사람은 버전을 말하지 않는다. 저장 때문에 생긴 값이다.
##### 이 예제에서는
- 주문이 버전을 들고, 주문 저장소가 T3에서 저장된 버전과 비교한다.
- 주문 응용 서비스는 일마다 주문을 새로 꺼내고 바로 저장해서 버전이 늘 맞는다. 21장 결제 실패 때도 cancel이 주문을 다시 꺼내므로 버전 충돌 없이 취소된다.

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
##### 무엇
- 하위 도메인(Subdomain)은 가게의 업무를 나눈 것이다. 코드가 아니라 업무에서 먼저 정한다.
- 핵심(Core)은 이 가게가 남보다 잘해야 하는 업무, 지원(Supporting)은 핵심을 돕지만 가게에 맞게 직접 만드는 업무, 일반(Generic)은 어느 가게나 비슷해서 사다 써도 되는 업무다.
##### 왜
- 11장부터 회원, 상품, 주문 세 업무가 생겼다. 모든 업무에 같은 정성을 쏟을 수는 없다.
- 종류를 나누는 이유는 힘을 어디에 쏟을지 정하기 위해서다.
##### 쉬운 방법과 비교
- 쉬운 방법은 나누지 않고 모든 업무를 같은 방식으로 만드는 것이다.
- 17장 T1은 context-map.md 문서만 바뀌고 코드는 그대로다. 지금은 나누지 않아도 프로그램은 똑같이 돈다.
- 차이는 무엇을 공들여 만들고 무엇을 사다 쓸지 정할 때 난다. 핵심인 주문에는 할인, 명세, 이벤트, 버전처럼 규칙을 공들여 넣었고, 일반인 회원은 이름과 등급만 두고 회원 응용 서비스도 만들지 않는다.
- 19장에서 결제는 일반 하위 도메인이라 직접 만들지 않고 외부 결제 모듈 legacypay를 쓴다. 분류가 만들지 말자는 결정까지 이어진다.
- 업무가 하나뿐인 작은 프로그램이면 나눌 것이 없다. 이때는 분류표를 쓰는 시간만 든다.
##### 헷갈리는 개념
- 하위 도메인은 애그리거트 같은 객체 묶음이 아니다. 주문 하위 도메인 안에 주문 애그리거트, 할인 정책, 무료 배송 명세가 모두 들어 있다.
- 핵심이냐 아니냐는 기술이 어려운지가 아니라 가게가 남보다 잘해야 하는지로 정한다.
##### 이 예제에서는
- 주문이 핵심, 상품이 지원, 회원이 일반이다. 18장에 배송(지원), 19장에 결제(일반)가 더해진다.

#### 개념 바운디드 컨텍스트
##### 무엇
- 바운디드 컨텍스트(Bounded Context)는 한 모델과 한 말이 같은 뜻으로 통하는 코드의 경계다.
- 같은 말도 경계마다 뜻이 다르다. 상품 쪽의 상품은 지금 가격과 재고를 갖고, 주문 쪽의 주문 항목은 주문 때의 이름과 가격을 갖는다. 12장의 가격 바뀐 뒤 장면이 그 차이다.
##### 왜
- 앞 장까지는 ddd.domain 한 묶음에 회원, 상품, 주문이 섞여 있어서 어느 클래스가 어느 업무의 말인지 코드만 보고는 알 수 없었다.
##### 쉬운 방법과 비교
- 쉬운 방법은 10장 모양 그대로 ddd.domain, ddd.application, ddd.infrastructure 세 묶음에 모두 두는 것이다.
- 17장은 리팩터라 실행 출력 스물여섯 줄이 같다. 지금은 나누지 않아도 동작은 똑같다.
- 나누면 다른 컨텍스트를 쓰는 곳이 import로 드러난다. T3에서 DiscountPolicy에 import ddd.member.domain이, T4에서 OrderLineFactory에 import ddd.catalog.domain이 생긴다. 한 묶음일 때는 같은 패키지라 import 없이 숨어 있던 쓰임이다.
- 18장에서 차이가 커진다. 배송 컨텍스트의 Shipment는 주문의 Address를 가져다 쓰지 않고 주소를 String으로 갖는다. 같은 묶음이었다면 Address를 그대로 가져다 쓰기 쉬웠다.
- 22장에서는 배송 컨텍스트를 다른 프로그램 ShippingApp으로 떼어 낸다. ShippingApp이 import하는 것이 ddd.shipping뿐이라 떼어 낼 수 있었다.
- 같은 말이 어디서나 같은 뜻이고 한 팀이 만드는 작은 프로그램이면 한 모델이 더 단순하다. 나누면 18장처럼 메시지를 글자로 만들고 다시 잘라 읽는 번역 일이 생긴다.
##### 헷갈리는 개념
- 하위 도메인과 헷갈리기 쉽다. 하위 도메인은 업무를 나눈 것(문제 쪽)이고, 바운디드 컨텍스트는 그 업무를 푸는 모델의 경계(코드 쪽)다.
- 둘이 늘 하나씩 짝지어지지는 않는다. 이 프로그램은 알아보기 쉽게 하위 도메인 하나에 컨텍스트 하나를 둔다.
- 애그리거트와도 다르다. 애그리거트는 한 번에 바꾸고 저장하는 일관성 단위이고, 컨텍스트는 말과 모델의 경계다. 주문 컨텍스트 안에 주문 애그리거트가 들어 있다.
##### 이 예제에서는
- 주문 컨텍스트 ddd.order, 상품 컨텍스트 ddd.catalog, 회원 컨텍스트 ddd.member가 경계다.

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
##### 무엇
- 공유 커널(Shared Kernel)은 두 컨텍스트 이상이 함께 갖기로 합의한 작은 모델 조각이다.
- 바꾸려면 쓰는 컨텍스트가 모두 합의한다.
##### 왜
- 돈은 주문 금액에도 상품 가격에도 쓰인다. 컨텍스트를 나누면 돈을 어느 컨텍스트에 둘지 정해야 한다.
##### 쉬운 방법과 비교
- 첫째 쉬운 방법은 Money를 주문 컨텍스트 ddd.order.domain에 두고 상품이 import해 쓰는 것이다. 지금도 동작은 같다.
- 그러면 상품 컨텍스트가 돈 하나 때문에 주문 컨텍스트에 묶인다. 주문 쪽이 Money를 바꾸면 Product도 함께 깨진다.
- 둘째 쉬운 방법은 컨텍스트마다 Money를 하나씩 두는 것이다. 0원 이상이라는 규칙이 두 군데로 갈라지고, 12장 OrderLineFactory가 상품의 돈을 주문 항목의 돈으로 바꾸는 줄이 새로 생긴다.
- 공유하지 않는 길도 뒤 장에 나온다. 19장 외부 결제 모듈은 Money를 모르고, LegacyPaymentAdapter가 amount()로 원 단위 int로 바꿔 넘긴다.
- 두 컨텍스트를 다른 팀이 만들고 돈을 다루는 규칙이 서로 다르면 따로 두는 쪽이 낫다. 공유하면 바꿀 때마다 합의해야 한다.
##### 헷갈리는 개념
- 아무 클래스나 모아 두는 공통 폴더와 다르다. 함께 쓸수록 컨텍스트끼리 단단히 묶이므로 가능한 한 작게 둔다.
- 5장 값 객체와 다른 이야기다. Money는 여전히 값 객체이고, 공유 커널은 그 값 객체를 어느 컨텍스트가 함께 갖느냐의 이야기다.
##### 이 예제에서는
- ddd.shared 묶음의 Money 하나가 공유 커널이다. 주문과 상품 컨텍스트가 함께 쓴다.

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
##### 무엇
- 모듈(Module)은 함께 바뀌는 개념을 이름 하나로 묶는 단위다. 자바에서는 패키지가 모듈이다.
##### 왜
- 10장에서는 기술 계층으로만 묶어서 ddd.domain에 회원과 주문이 섞여 있었다. 회원 하나를 고치려면 주문 클래스 사이를 뒤져야 했다.
- 이제는 업무 묶음 ddd.member를 먼저 두고 그 안에 계층 묶음을 둔다.
##### 쉬운 방법과 비교
- 쉬운 방법은 10장처럼 계층을 바깥에 두는 것이다. ddd.domain 안에 회원과 주문을 함께 두거나, ddd.domain.member처럼 계층 아래에 업무를 둔다.
- 지금은 어느 쪽이든 컴파일되고 출력도 같다.
- 차이는 업무 하나를 고치거나 떼어 낼 때 난다. 계층이 바깥이면 회원 하나가 ddd.domain과 ddd.infrastructure 두 곳에 흩어진다. 업무가 바깥이면 ddd.member 한 폴더에 모인다.
- 18장에서 배송 컨텍스트를 더할 때 ddd.shipping 폴더 하나를 만들고 그 안에 세 계층을 둔다. 22장에서 배송을 다른 프로그램으로 떼어 낼 때도 ddd.shipping 폴더만 보면 된다.
- 업무가 하나뿐이고 클래스가 몇 개 안 되면 10장처럼 계층으로만 나누는 것이 더 찾기 쉽다.
##### 헷갈리는 개념
- 바운디드 컨텍스트와 다르다. 컨텍스트는 모델과 말의 경계이고, 모듈은 그 경계를 코드에 담는 그릇이다. 한 컨텍스트가 ddd.member.domain, ddd.member.infrastructure처럼 모듈 여러 개를 가진다.
- 자바 9의 module-info 모듈 시스템과도 다르다. 이 강의는 다른 컨텍스트의 클래스를 못 쓰게 막지 않고 패키지로만 나눈다.
##### 이 예제에서는
- ddd.member, ddd.catalog, ddd.order, ddd.shared 네 묶음이 모듈이다.

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
##### 무엇
- 공개 호스트 서비스(Open Host Service)는 한 컨텍스트가 바깥 컨텍스트들에 열어 두는 정해진 창구다.
- 바깥은 이 창구로만 우리 컨텍스트의 소식을 받고, 안쪽 클래스는 보지 않는다.
##### 왜
- 17장 컨텍스트 사이 표를 보면 상품 컨텍스트의 재고 줄이기 처리기가 주문 컨텍스트의 OrderPlaced와 OrderLine을 import로 바로 쓴다.
- 이렇게 이으면 주문이 OrderLine의 칸을 바꿀 때 상품 쪽 코드도 함께 고쳐야 한다.
- 받는 쪽이 하나 늘 때마다 주문 안쪽 클래스에 묶이는 곳도 하나씩 는다.
- 창구를 두면 바깥에 무엇을 어떤 모양으로 알릴지 이 클래스 한 곳에서 정하고 안쪽 모델은 숨긴다.
##### 쉬운 방법과 비교
- 가장 쉬운 방법은 결제하기 안에서 shipmentRepository.save(new Shipment(...))를 바로 부르는 것이다.
- 그다음 떠올릴 방법은 15장처럼 결제 완료됨 record를 만들어 발행하고, 배송 쪽 처리기가 그 record를 import해 받는 것이다.
- 지금 18장 Main만 보면 두 방법 모두 같은 배송 컨텍스트 줄을 찍는다. 한 프로그램 안이라 차이가 보이지 않는다.
- 바로 부르는 방법은 주문 응용 서비스가 배송 저장소를 알게 되고, 결제하기의 트랜잭션 경계가 배송까지 넓어진다. 15장에서 재고 줄이기를 떼어 낸 것과 같은 이유로 피한다.
- record를 import하는 방법은 22장에서 막힌다. 배송이 다른 프로그램인 ShippingApp으로 떨어져 나가면 소켓으로는 글자 한 줄만 건너간다.
- 22장의 SocketEventPublisher도 String 이벤트만 소켓으로 보내고, 자바 객체 이벤트는 같은 프로그램 안 발행기에 남긴다.
- 창구 클래스 없이 결제하기 안에서 메시지 글자를 바로 만들어도 지금은 결과가 같다. 내보내는 메시지가 하나뿐이기 때문이다.
- 내보내는 메시지 종류와 받는 쪽이 늘수록 메시지 모양을 한 곳에 모은 창구가 값을 한다.
- 받는 쪽이 같은 프로그램 안에 하나뿐이고 같은 팀이 함께 고친다면 record를 import하는 쪽이 더 단순하다. 이 예제도 재고 줄이기는 끝까지 그렇게 둔다.
##### 헷갈리는 개념
- 주문 응용 서비스와 다르다. 응용 서비스는 우리 컨텍스트 안의 유스케이스 순서를 맡고, 공개 호스트 서비스는 바깥에 내보낼 것만 맡는다.
- 도메인 서비스와도 다르다. 업무 규칙을 계산하지 않고, 이미 끝난 결과를 바깥이 읽을 모양으로 옮겨 내보낼 뿐이다.
##### 이 예제에서는
- OrderOpenHostService가 주문 컨텍스트의 창구다.
- 결제하기가 저장을 마친 뒤 publishOrderPaid로 결제 완료 메시지를 내보낸다.

#### 개념 발행된 언어
##### 무엇
- 발행된 언어(Published Language)는 창구가 바깥과 주고받는 정해진 메시지 모양이다. 문서로 공개해 두고 지킨다.
- 공개 호스트 서비스가 어디로 알리는지라면, 발행된 언어는 무슨 모양으로 알리는지다. 둘은 짝으로 쓰인다.
##### 왜
- 받는 쪽이 주문 클래스를 몰라도 읽으려면 클래스 대신 함께 아는 약속이 있어야 한다.
- 모양을 문서로 정해 두면 주문 컨텍스트는 안쪽 모델을 바꿔도 이 모양만 지키면 된다.
##### 쉬운 방법과 비교
- 쉬운 방법은 Order 객체나 record OrderPaid(long orderId, Address address) 같은 자바 객체를 그대로 넘기는 것이다.
- 같은 프로그램 안이라면 record가 더 안전하다. 칸 이름과 타입을 컴파일러가 확인한다.
- 글자는 그렇지 않다. orderId와 address의 순서를 바꿔 보내도 컴파일은 되고, 처리기의 Long.parseLong이 실행 중에 깨진다.
- 그래도 글자를 고른 이유는 22장이다. 다른 프로그램으로 건너가는 것은 글자 한 줄이고, 자바 객체를 보내려면 받는 쪽도 그 클래스를 가져야 한다.
- Order를 그대로 넘기면 배송 쪽이 5장의 Address 값 객체와 주문 상태까지 알게 되어, 주문이 그 클래스를 바꿀 때 배송도 함께 깨진다.
- 실제 시스템은 JSON 같은 정해진 형식을 쓴다. 이 강의는 JSON을 다루지 않아서 키=값을 |로 이은 한 줄 글자를 쓴다.
- 이 한 줄 글자는 주소에 |가 들어가면 깨진다. 이 예제는 그 처리를 만들지 않는다.
##### 헷갈리는 개념
- 도메인 이벤트와 다르다. 주문 접수됨은 같은 프로그램 안에서 주고받는 자바 객체이고, 결제 완료 메시지는 주문 클래스를 모르는 쪽도 읽는 글자다.
- 공유 커널과도 다르다. 공유 커널은 코드를 함께 갖고 바꿀 때 모두 합의하지만, 발행된 언어는 메시지 모양만 함께 알아서 묶임이 훨씬 약하다.
- 유비쿼터스 언어와도 다르다. 유비쿼터스 언어는 한 컨텍스트 안에서 쓰는 말이고, 발행된 언어는 컨텍스트 사이에서 쓰기로 정한 말이다.
##### 이 예제에서는
- ORDER_PAID|orderId=1|address=서울시 강남구처럼 종류 뒤에 키=값을 |로 이은 한 줄 글자가 발행된 언어다.
- context-map.md의 공개 호스트 서비스와 발행된 언어 절이 이 모양을 공개한 문서다.

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
##### 무엇
- 17장에서 배운 경계가 같은 말을 다른 모델로 만든다는 것을 새 컨텍스트에서 처음 본다.
- 주문 컨텍스트의 배송은 주문 상태 배송 중일 뿐이고, 배송 컨텍스트의 배송은 주문 번호와 주소를 가진 자기 객체다.
##### 쉬운 방법과 비교
- 쉬운 방법은 배송을 따로 만들지 않고 주문 상태 배송 중을 그대로 배송으로 쓰는 것이다. 주문에는 이미 배송하기와 배송 주소가 있다.
- 지금 18장에서는 그걸로도 1번 주문이 어디로 가는지 알 수 있다.
- 차이는 배송 쪽 일이 늘 때 생긴다. 배송만의 일을 주문에 더할수록 핵심인 주문 애그리거트가 지원 업무로 커진다.
- 또 하나의 쉬운 방법은 Shipment가 주문의 Address를 import해 칸으로 갖는 것이다.
- 지금은 그래도 컴파일되고 출력도 같다. 22장 ShippingApp도 같은 이클립스 프로젝트에 있어서 import 자체는 된다.
- 하지만 그러면 배송은 메시지 글자를 다시 Address로 만들어야 하고, 빈 주소를 막는 주문 컨텍스트의 규칙에 묶인다. 주문이 Address를 바꾸면 배송도 고쳐야 한다.
- 배송 컨텍스트에 주소 규칙이 따로 필요해지면 그때 배송 쪽 값 객체를 만들면 된다. 지금처럼 규칙이 없으면 String이 더 단순하다.
##### 헷갈리는 개념
- 주문의 배송 중과 배송 컨텍스트의 배송은 이름이 같지만 다른 모델이다. 앞은 주문 상태 값 하나이고, 뒤는 주문 번호로 구별하는 엔티티다.
- 이 예제는 둘을 맞추지 않는다. 배송 컨텍스트가 배송 상태를 맡고 주문 상태와 맞추는 일은 미뤘다.
##### 이 예제에서는
- Shipment는 orderId와 address 두 칸을 숫자와 글자로 갖고, ddd.order를 하나도 import하지 않는다.
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
##### 무엇
- 부패 방지 계층(Anticorruption Layer)은 바깥 모델의 말이 우리 모델로 새어 들지 않게 경계에서 번역하는 층이다.
- 우리 쪽이 우리 말로 인터페이스를 정하고, 번역층이 그 인터페이스를 구현하면서 바깥 말로 바꿔 부른다.
##### 왜
- 외부 결제 모듈은 우리가 고칠 수 없고 영어 이름, 원 단위 int, 0000과 E101 같은 코드 글자로 말한다.
- 이 말이 주문 응용 서비스에 들어오면 우리 코드가 외부 모듈의 모양을 따라 바뀐다.
##### 쉬운 방법과 비교
- 쉬운 방법은 결제하기 안에서 legacyPayGateway.approve("DDD-SHOP", order.getPaymentPrice().amount())를 부르고, 코드가 0000이 아니면 예외를 던지는 것이다.
- 지금 19장에서는 그걸로도 한도 넘은 결제 줄이 똑같이 나온다. 외부 결제를 부르는 곳이 결제하기 하나뿐이라 if 몇 줄이면 된다.
- 그 대신 주문 응용 서비스가 legacypay를 import하고 가게 키와 E101을 안다. 거절 이유 글자도 응용 서비스가 직접 정한다.
- 21장에서 결제하기에 보상이 들어올 때 차이가 보인다. 어댑터가 있으면 result.approved() 하나만 보고 취소를 부르고, 바로 부르면 코드 글자 비교가 사가 흐름 한가운데에 섞인다.
- 22장 OrderApp도 같은 어댑터를 끼워 주문 응용 서비스를 만든다. 결제를 쓰는 프로그램이 늘어도 번역은 어댑터 한 곳이다.
- 외부 모듈이 새 코드를 더하거나 결제 회사를 바꾸면 어댑터만 고친다. 바로 불렀다면 0000을 비교하는 곳을 모두 찾아 고친다.
- 외부 모듈의 말이 우리 말과 거의 같고 부르는 곳도 하나라면 번역층 없이 바로 부르는 편이 낫다. 번역할 것이 없는데 층만 늘리면 읽을 파일만 많아진다.
##### 헷갈리는 개념
- 공개 호스트 서비스와 방향이 반대다. 공개 호스트 서비스는 내보내는 쪽이 정한 창구이고, 부패 방지 계층은 받아 쓰는 쪽이 자기를 지키려고 세우는 번역기다.
- 저장소 구현 같은 그냥 어댑터와도 다르다. 인터페이스를 구현하는 모양은 같지만, 부패 방지 계층은 금액 단위와 응답 코드처럼 모델 자체를 번역한다.
- 공유 커널과도 다르다. 공유 커널은 모델을 함께 갖고, 부패 방지 계층은 바깥 모델을 함께 갖지 않고 번역만 한다.
##### 이 예제에서는
- LegacyPaymentAdapter가 부패 방지 계층이다. legacypay를 import하는 주문 쪽 클래스는 이 파일 하나다.
- 주문 응용 서비스는 도메인에 둔 PaymentGateway와 PaymentResult만 안다.

#### 코드
- private static final String MERCHANT_KEY는 가게 키 상수다. 외부 모듈만 쓰는 값이라 어댑터만 알고 주문은 모른다.
- amount.amount()로 돈 값 객체를 외부가 쓰는 원 단위 int로 바꾼다.
- 받은 코드 글자를 if로 하나씩 결제 결과로 번역한다. 0000은 승인, E101은 결제 한도를 넘었습니다, 그 밖은 모두 결제에 실패했습니다다.
- 모르는 코드도 마지막 return에서 거절로 받는다. 외부가 새 코드를 더해도 주문 쪽은 승인 아니면 거절 둘만 본다.

## payment-acl T3

### context-map.md

#### 개념 컨텍스트 맵
##### 무엇
- 컨텍스트 맵(Context Map)은 바운디드 컨텍스트들이 서로 어떤 관계로 이어지는지 그린 지도다.
- 누가 누구를 쓰는지뿐 아니라 어떤 방식으로 잇는지, 관계의 종류를 적는다.
##### 왜
- 컨텍스트만 나누고 관계를 적지 않으면 누가 누구 모델을 따르고 누가 번역하는지 코드를 다 읽어야 안다.
- 그러면 다음 사람이 외부 말을 주문 안으로 끌어들이기 쉽다.
##### 쉬운 방법과 비교
- 쉬운 방법은 지도 없이 import를 찾아보는 것이다. ddd.order를 import하는 클래스를 검색하면 누가 주문을 쓰는지 나온다.
- 상품과 회원 줄은 그걸로 찾힌다. 17장까지는 import 검색이 지도 노릇을 했다.
- 배송 줄은 찾히지 않는다. 18장부터 배송은 ddd.order를 하나도 import하지 않고 ORDER_PAID 글자로만 이어진다.
- 결제 줄도 import만으로는 반만 보인다. LegacyPaymentAdapter가 legacypay를 import한다는 것은 나오지만, 그것이 외부 말을 막는 번역층이라는 것은 나오지 않는다.
- 지도는 문서라서 코드보다 늦어질 수 있다. 컨텍스트 사이 의존이 새로 생기면 지도도 함께 고친다.
- 컨텍스트가 둘셋이고 모두 import로 잇는 작은 프로그램이라면 지도 없이 import를 보는 편이 더 정확하다.
##### 헷갈리는 개념
- 클래스 그림이나 폴더 구조도와 다르다. 클래스가 아니라 컨텍스트 사이의 관계 종류를 적는다.
- 같은 파일 위쪽의 하위 도메인 표와도 다르다. 하위 도메인 표는 업무를 나눈 것이고, 컨텍스트 맵은 그 경계들이 어떻게 이어지는지다.
##### 이 예제에서는
- 관계가 네 가지 있다. 공유 커널(돈), 상대 클래스를 그대로 가져다 쓰는 관계(주문이 상품과 회원을 읽음), 공개 호스트 서비스와 발행된 언어(주문이 배송에 메시지), 부패 방지 계층(주문이 외부 결제를 번역)이다.
- 같은 코드를 함께 갖는 공유 커널이 가장 단단히 묶인다. 메시지로 잇는 배송과 번역으로 막는 외부 결제는 상대 클래스를 하나도 쓰지 않는다.
- context-map.md 한 장이 이 예제의 컨텍스트 맵이다.

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
##### 무엇
- CQRS(Command Query Responsibility Segregation, 명령과 조회 나누기)는 바꾸는 쪽(명령)과 읽는 쪽(조회)을 다른 모델로 나누는 것이다.
- 명령 쪽은 규칙을 지키는 애그리거트로 바꾸고, 조회 쪽은 화면에 맞춘 단순한 값으로 읽는다.
##### 왜
- 주문 애그리거트는 규칙을 지키며 바꾸기에 맞춘 모양이다. 목록 한 줄에 필요한 회원 이름은 갖고 있지 않다.
- 목록 화면 한 줄을 만들려고 주문이나 주문 응용 서비스에 메서드를 더하면 화면이 바뀔 때마다 규칙을 지키는 클래스를 건드린다.
- 명령과 조회는 바뀌는 이유가 다르다. 그래서 따로 둔다.
##### 쉬운 방법과 비교
- 쉬운 방법은 주문 응용 서비스에 List<Order>를 돌려주는 메서드를 하나 더하고, Main이 주문마다 회원을 꺼내 찍는 것이다.
- Main 맨 앞의 주문 줄이 바로 그렇게 찍는다. orderService.findOrder로 주문을 받고 memberRepository에서 회원 이름을 따로 찾는다.
- 20장의 주문 목록 줄도 그 방법으로 똑같이 찍을 수 있다. 같은 주문 저장소에서 읽으니 속도도 같다.
- 차이는 받은 쪽이 무엇을 할 수 있느냐다. Order를 받으면 pay, cancel 같은 행동 메서드를 부를 수 있다. Main의 저장 안 한 취소 장면이 findOrder로 받은 주문에 cancel을 부른다.
- OrderSummary는 숫자와 글자만 가진 record라서 받은 쪽이 아무것도 바꿀 수 없다.
- 회원 이름을 붙이는 일도 화면마다 되풀이하지 않고 조회 서비스 한 곳에서 한다.
- 이 예제 안에서는 조회 화면이 하나뿐이라 뒤 장에서 차이가 더 커지지 않는다.
- 화면이 하나이고 행동 메서드를 잘못 부를 걱정이 없다면 Order를 바로 돌려주는 편이 클래스가 둘 적다.
- 조회가 많고 무거워지면 읽기 전용 저장소를 따로 두고 이벤트로 채우는 큰 CQRS로 간다. 이 예제는 그것을 만들지 않는다.
##### 헷갈리는 개념
- 응용 서비스를 그냥 둘로 쪼갠 것과 다르다. 조회 쪽은 규칙도 저장도 없고, 돌려주는 모델도 화면에 맞춘 다른 모양이다.
- CQS(Command Query Separation)와도 다르다. CQS는 메서드 하나가 바꾸기와 묻기 중 하나만 하라는 메서드 단위 규칙이고, CQRS는 모델 단위 나눔이다.
- 이 예제의 placeOrder는 주문을 만들면서 주문 번호도 돌려준다. CQRS를 써도 메서드마다 CQS를 엄격히 지키지는 않는다.
- 9장의 PlaceOrderCommand도 도메인 타입 없이 단순한 값만 가진 record다. 그것은 명령에 들어가는 입력이고, OrderSummary는 조회에서 나오는 결과다.
- 이벤트 소싱과 함께 이야기될 때가 많지만 따로 쓸 수 있다. 이 예제는 이벤트 소싱을 하지 않는다.
##### 이 예제에서는
- OrderQueryService와 OrderSummary가 조회 쪽, OrderApplicationService와 Order가 명령 쪽이다.
- 저장소는 주문 저장소 하나를 함께 쓰고 모델만 나눈 가장 작은 모양이다.

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
##### 무엇
- 사가(Saga)는 따로 저장되는 여러 단계를 하나의 업무 흐름으로 잇고, 한 단계가 실패하면 앞에서 끝난 단계를 하나씩 되돌리는 방법이다.
##### 왜
- 앞 단계에서 결제가 거절되어도 주문 접수와 재고 줄이기가 이미 저장되어 남았다. 결제 실패 뒤 줄이 노트북 재고 9개, 주문 접수였다.
- 15장에서 주문 저장과 재고 줄이기를 다른 트랜잭션으로 나눴기 때문에 한 번에 없던 일로 돌릴 수 없다.
##### 쉬운 방법과 비교
- 첫째 쉬운 방법은 결제하기의 거절 블록에서 productRepository로 상품을 꺼내 재고를 바로 더하는 것이다.
- 출력은 같게 만들 수 있다. 그러나 주문 응용 서비스가 상품 애그리거트까지 바꾸어 15장에서 나눈 트랜잭션 경계가 다시 합쳐지고, 17장에서 나눈 주문 컨텍스트가 상품 저장소를 쓰게 된다.
- 둘째 쉬운 방법은 재고를 주문 접수됨이 아니라 결제가 끝난 뒤에 줄이는 것이다. 그러면 결제가 거절되어도 되돌릴 재고가 없다.
- 이 방법이 더 나을 때도 있다. 되돌리기가 하나 줄어 흐름이 단순하다.
- 대신 주문하고 결제하기 전까지 재고가 잡혀 있지 않아 마지막 하나를 두 사람이 함께 주문할 수 있다. 이 예제는 재고가 모자란 주문 막기를 미뤄서 이 차이가 출력에 나오지 않는다.
- 이 예제는 15장의 주문 접수됨과 재고 줄이기 처리기를 그대로 두고, 반대 일을 하는 이벤트와 처리기를 짝으로 더했다.
##### 헷갈리는 개념
- 한 트랜잭션의 롤백과 다르다. 롤백은 아직 저장하지 않은 묶음을 버리는 것이고, 사가는 이미 저장된 단계를 새 일로 되돌린다.
- 최종 일관성만으로는 부족하다. 최종 일관성은 성공한 일이 나중에 맞춰지는 것이고, 사가는 실패했을 때 되돌리는 길까지 정한다.
- 사가를 맡는 조정 클래스를 따로 두는 방법(오케스트레이션)과 이벤트만으로 단계를 잇는 방법(코레오그래피)이 있다. 이 예제는 결제하기가 취소하기를 바로 부르고 재고 되돌리기는 이벤트로 잇는 섞인 모양이다.
- 16장 낙관적 잠금과도 다르다. 낙관적 잠금은 한 애그리거트에 두 저장이 부딪히는 것을 막고, 사가는 여러 애그리거트에 걸친 흐름의 실패를 되돌린다.
##### 이 예제에서는
- 주문 접수, 재고 줄이기, 결제가 사가의 단계다.
- 결제하기의 if (!result.approved()) 블록이 실패를 받아 보상을 시작하는 곳이다.

#### 개념 보상
##### 무엇
- 보상(Compensation)은 끝난 단계를 지우지 않고 반대 일을 한 번 더 해서 되돌리는 것이다.
##### 왜
- 이미 저장된 주문과 이미 줄어든 재고는 롤백으로 돌릴 수 없어서 반대 일을 새로 해야 한다.
##### 쉬운 방법과 비교
- 쉬운 방법은 거절된 주문을 주문 저장소에서 지우는 것이다. 지금 OrderRepository에는 지우는 메서드가 없어서 하나 더해야 한다.
- 지우면 주문이 있었다는 사실도 사라진다. Main의 한도 넘은 결제 장면은 거절 뒤 주문을 다시 꺼내 상태를 찍는데, 지웠다면 주문이 없습니다 예외가 난다.
- 지우기는 이벤트도 남기지 않는다. 재고 되돌리기 처리기가 받을 주문 취소됨이 없으니 재고 되돌리기를 따로 불러야 한다.
- 새 보상 기능을 만들지 않고 4장부터 있던 주문 취소하기를 그대로 쓴 것도 쉬운 길이다. 이 장에서는 취소가 주문 취소됨을 더 남길 뿐이다.
- 기록할 필요가 없는 임시 데이터라면 지우는 편이 단순하다. 주문처럼 업무 기록이 되는 것은 보상으로 남긴다.
##### 헷갈리는 개념
- 롤백은 없던 일로 만들지만 보상은 흔적을 남긴다. 취소된 주문과 주문 취소됨 이벤트가 그 흔적이다.
- 보상도 한 번에 끝나지 않는다. 주문 취소는 바로 저장되지만 재고는 이벤트를 처리한 뒤에 되돌아오는 최종 일관성이다.
##### 이 예제에서는
- 주문 접수의 보상이 주문 취소하기, 재고 줄이기의 보상이 IncreaseStockHandler의 재고 되돌리기다.
- 결제는 실패한 단계라서 보상이 없다.

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
##### 무엇
- 이 문서는 지금까지 배운 자리마다 어떤 규칙을 두는지 한 장에 모은 것이다.
- 새 규칙이 생기면 고르는 순서의 질문을 위에서부터 차례로 물어 둘 곳을 고른다.
##### 왜
- 고르는 기준이 없으면 새 규칙은 가장 쓰기 쉬운 응용 서비스에 쌓인다. 그러면 2장의 빈약한 주문으로 돌아간다.
- 13장에서 규칙을 두는 네 자리를 배웠다. 이 표는 거기에 5장의 값 객체, 14장의 명세, 15장의 이벤트, 21장의 사가를 더하고, 엔티티 행동 메서드와 애그리거트 루트를 애그리거트 한 줄로 묶었다.
##### 쉬운 방법과 비교
- 쉬운 방법은 모든 규칙을 응용 서비스에 두는 것이다. 2장이 그 모양이었고, 모두 응용 서비스를 거쳐 부른다면 지금도 출력은 같게 만들 수 있다.
- 차이는 응용 서비스를 거치지 않는 코드에서 난다. Main의 음수 금액 장면은 productRepository.save(new Product(4, "할인 책", new Money(-15000), 10))로 응용 서비스를 거치지 않는데도 Money가 막는다.
- 동시 수정 장면도 orderA.ship()을 주문에 바로 부르지만 상태 규칙이 주문 안에 있어 그대로 지켜진다.
- 규칙을 쓰는 곳이 하나뿐이고 바뀔 일도 없다면 표를 따라 클래스를 새로 만들기보다 지금 있는 가까운 클래스에 두는 편이 낫다.
##### 헷갈리는 개념
- 공개 호스트 서비스, 결제 어댑터, 주문 조회 서비스는 표에 없다. 업무 규칙이 아니라 바깥과 잇거나 옮겨 담는 일만 하기 때문이다.
- 응용 서비스 줄은 규칙을 두는 곳이 아니라 규칙을 두지 않는 곳이다. 남는 순서만 맡는다는 뜻으로 표의 맨 끝에 있다.
##### 이 예제에서는
- 이 예제의 모든 업무 규칙이 표의 일곱 줄 중 하나에 들어간다.
- 사가 절의 표는 21장에서 만든 단계와 보상을 그 일을 맡는 클래스와 짝지어 적는다.

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
##### 무엇
- 메시지를 어떻게 나를지는 도메인이 아니라 바꿔 끼울 수 있는 기술이다.
- 발행기 자리를 쓰는 쪽은 그대로 두고, 그 자리에 들어가는 객체만 바꿔 메시지가 가는 길을 바꾼다.
##### 왜
- 지금까지 결제 완료 메시지는 같은 프로그램 안의 발행기를 거쳐 갔다.
- 배송을 다른 프로그램으로 떼어 내도 주문과 배송 코드를 고치지 않을 수 있는지가 문제다.
##### 쉬운 방법과 비교
- 쉬운 방법은 OrderOpenHostService의 publishOrderPaid 안에서 new Socket("localhost", 9090)을 열어 글자를 바로 보내는 것이다.
- OrderApp만 보면 그걸로도 된다. 배송 앱 콘솔에 같은 메시지가 찍힌다.
- 그러면 Main이 깨진다. Main도 같은 공개 호스트 서비스를 쓰므로 배송 앱을 띄우지 않고 Main을 실행하면 결제하기에서 연결 오류가 난다.
- 지금은 Main과 OrderApp이 같은 주문 응용 서비스와 공개 호스트 서비스를 쓰고, 다른 것은 넣어 주는 발행기 한 줄뿐이다. 그래서 Main은 앞 장 출력 그대로다.
- 메시지가 늘 소켓으로만 가고 같은 프로그램 안에서 돌릴 일이 없다면 바꿔 끼울 자리를 만들 필요가 없다. 그때는 바로 보내는 편이 단순하다.
##### 헷갈리는 개념
- 8장과 10장에서 저장소를 바꿔 끼우던 것과 같은 생각이지만 모양은 다르다. 주문 저장소는 인터페이스를 구현해 끼우고, 소켓 발행기는 클래스인 EventPublisher를 이어받아 끼운다.
- 발행된 언어가 바뀐 것이 아니다. 소켓으로 가는 것도 18장의 ORDER_PAID 글자 그대로다. 길만 바뀌고 말은 그대로라서 배송 쪽 처리기를 고치지 않는다.
- 실제 메시지 큐 제품과 다르다. 소켓은 받는 쪽이 꺼져 있으면 메시지가 사라지고, 메시지 큐는 모아 두었다가 다시 보낸다.
##### 이 예제에서는
- OrderApp은 EventPublisher 자리에 new SocketEventPublisher("localhost", 9090)를 넣는다.
- ShippingApp은 Main과 같은 OrderPaidMessageHandler에 소켓으로 받은 글자를 넘긴다.

#### 코드
- 붙여넣기 코드다. extends EventPublisher로 이벤트 발행기를 이어받아, 주문 응용 서비스가 EventPublisher를 받는 자리에 그대로 들어간다.
- publish를 @Override로 고쳐 쓴다. 글자 이벤트는 send로 소켓에 보내고, 주문 접수됨 같은 자바 객체는 super.publish로 원래 발행기에 맡긴다.
- 소켓 너머로는 발행된 언어 글자만 보낸다. 자바 객체는 같은 프로그램 안에서만 쓴다.
- PrintWriter의 두 번째 인자 true는 println할 때마다 바로 내보내라는 뜻이다.
- 인프라가 아니라 이벤트 발행기 옆 응용 계층에 둔다. 인프라에 두면 응용의 EventPublisher를 import해야 해서 layers 장의 규칙에 걸린다.
