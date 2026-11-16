# 프로젝트 규칙

## 기술

| 항목 | 선택 |
|---|---|
| 언어 | Java 21 |
| IDE | 이클립스. ddd에 .project와 .classpath를 넣는다 |
| 빌드 도구 | 없음 |
| 학생 코드 라이브러리 | 없음 |
| 저장소 | HashMap. 23장만 Oracle XE 21c |
| 테스트 | JUnit 5 (ddd/lib/junit-platform-console-standalone.jar) |
| DB 드라이버 | 23장만 ojdbc11.jar (오라클 설치 폴더에서 복사) |
| 메시지 형식 | 한 줄 글자. 타입|키=값|키=값 |

## 폴더

| 무엇 | 폴더 |
|---|---|
| 학생 코드 | ddd |
| 행동 테스트 | tests |
| jar | ddd/lib. JUnit jar, 23장의 ojdbc jar |
| 컴파일 결과 | ddd/bin. git에 올리지 않는다 |

## 실행

- 실행: cd ddd, javac -d bin --source-path src src/ddd/Main.java, java -cp bin ddd.Main
- 테스트: tests를 bin-test로 컴파일한 뒤 java -jar ddd/lib/junit-platform-console-standalone.jar로 실행한다
- 빌드: javac -d bin --source-path src src/ddd/Main.java

## 캡처

| 종류 | 주소나 명령 |
|---|---|
| run | cd ddd, javac -d bin --source-path src src/ddd/Main.java, java -cp bin ddd.Main |

## 이클립스

- ddd 폴더를 프로젝트로 연다
- main 클래스를 열고 Run을 누른다
- 22장은 OrderApp을 먼저 실행하고 ShippingApp을 실행한다
- 테스트는 테스트 파일에서 Run As, JUnit Test를 고른다
