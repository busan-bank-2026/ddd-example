# 프로젝트 규칙

## 기술

| 항목 | 선택 |
|---|---|
| 언어 | Java 21 |
| IDE | 이클립스. ddd에 .project와 .classpath를 넣는다 |
| 빌드 도구 | 없음 |
| 학생 코드 라이브러리 | 없음 |
| 저장소 | HashMap |
| 테스트 | JUnit 5 (test-lib/junit-platform-console-standalone.jar) |
| 메시지 형식 | 한 줄 글자. 타입|키=값|키=값 |
| 소켓 포트 | 9090. 22장의 배송 앱이 localhost에서 연다 |

## 폴더

| 무엇 | 폴더 |
|---|---|
| 학생 코드 | ddd |
| 행동 테스트 | tests/ddd. 패키지 ddd. 클래스 이름은 장 이름 + Test. 이클립스에 붙이지 않는다 |
| jar | 테스트용 JUnit jar는 test-lib |
| 컴파일 결과 | ddd/bin, bin-test. git에 올리지 않는다 |

## 실행

- 실행: cd ddd, javac -d bin --source-path src src/ddd/Main.java, java -cp bin ddd.Main
- 22장 소켓 실행: cd ddd, javac -d bin --source-path src src/ddd/*.java, 한 창에서 java -cp bin ddd.ShippingApp을 먼저 띄우고, 다른 창에서 java -cp bin ddd.OrderApp
- 테스트: 저장소 루트에서 아래 네 줄을 실행한다. 테스트 명령이 ddd 폴더에서 돈다. Main은 지금 장 장면만 쓰므로 Main에서 닿지 않는 클래스도 컴파일되게 src 아래 자바 파일을 모두 넘긴다. bash에서 실행한다. 이클립스가 쓰는 ddd/bin과 겹치지 않게 결과는 bin-test에 쓴다. 맥과 리눅스는 ; 대신 :를 쓴다

```
cd ddd
javac -d ../bin-test/main $(find src -name "*.java")
javac -d ../bin-test/test -cp "../bin-test/main;../test-lib/junit-platform-console-standalone.jar" ../tests/ddd/*.java
java -jar ../test-lib/junit-platform-console-standalone.jar execute -cp "../bin-test/main;../bin-test/test" --scan-classpath
```
- 빌드: javac -d bin --source-path src src/ddd/Main.java

## Main

- Main은 지금 장의 장면만 찍는다. 장이 바뀌면 앞 장 장면은 지운다
- 첫 줄은 ===== 장 이름 =====이다
- 장면마다 빈 줄과 [장면 이름] 줄로 나눈다
- 바뀐 값은 전 -> 후로 찍어 무엇이 바뀌었는지 보이게 한다

## 캡처

| 종류 | 주소나 명령 |
|---|---|
| run | cd ddd, javac -d bin --source-path src src/ddd/Main.java, java -cp bin ddd.Main |
| run 소켓 | cd ddd, javac로 src/ddd/*.java를 컴파일하고, bash로 ShippingApp을 뒤에서 띄워 출력을 파일에 모은 뒤 OrderApp을 실행하고, ShippingApp이 끝나면 그 출력을 보인다 |

## 이클립스

- ddd 폴더를 프로젝트로 연다
- main 클래스를 열고 Run을 누른다
- 22장은 받는 쪽 ShippingApp을 먼저 Run한다. 콘솔에 9090번 포트에서 메시지를 기다립니다가 보이면 보내는 쪽 OrderApp을 Run한다
- 22장은 콘솔이 둘이다. 콘솔 창의 Display Selected Console 버튼으로 두 앱의 출력을 바꿔 본다
