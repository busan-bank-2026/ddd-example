# project-setup 계획

## 파일
| 파일 | 새로 또는 고침 | 하는 일 |
|---|---|---|
| ddd/src/ddd/Main.java | 새로 | 시작 글을 콘솔에 보여준다 |
| ddd/.project | 새로 | 이클립스 프로젝트 |
| ddd/.classpath | 새로 | src, JRE, 출력 폴더 bin |
| ddd/.settings/org.eclipse.jdt.core.prefs | 새로 | Java 21로 컴파일하고 공백 4칸으로 포맷한다 |
| ddd/.settings/org.eclipse.core.resources.prefs | 새로 | 파일 인코딩을 UTF-8로 정한다 |
| test-lib/junit-platform-console-standalone.jar | 새로 | JUnit 5 실행기. Maven Central 1.11.4. 학생 프로젝트 밖에 둔다 |
| tests/ddd/ProjectSetupTest.java | 새로 | S1부터 S3까지 행동 테스트 |
| .gitignore | 고침 | ddd/bin, bin-test를 올리지 않는다 |
| .projectcode/contract.md | 고침 | 테스트 명령을 실제 한 줄씩 적는다 |

## 코드 계획
Main.java
- 패키지는 ddd다
- main 메서드 하나에서 시작 글 한 줄을 출력한다

.project
- 이름은 ddd, Java 빌더와 Java 성격을 넣는다
- tests 폴더는 이클립스에 붙이지 않는다. 학생은 tests 폴더를 받지 않는다

.classpath
- 소스 폴더 src
- JRE는 JavaSE-21
- 출력은 bin

.settings
- jdt.core.prefs는 Java 21로 컴파일하고 포맷을 공백 4칸으로 맞춘다
- resources.prefs는 프로젝트 인코딩을 UTF-8로 정한다. 작업공간 인코딩이 MS949여도 한글이 깨지지 않는다

ProjectSetupTest.java
- System.out을 잠시 바꿔 Main.main의 출력을 모은다
- S1 출력에 온라인 주문 프로그램을 시작합니다가 들어 있는지 확인한다
- S2 이클립스 파일에 프로젝트 이름, src, Java 21, UTF-8이 있고 tests 연결이 없는지 확인한다
- S3 JUnit jar가 학생 프로젝트 밖 test-lib에 있는지 확인한다
- 테스트는 패키지 폴더 tests/ddd에 둔다. 장 ID의 -는 패키지 이름이 될 수 없다
- 파일 경로는 ddd 폴더 기준으로 찾는다. 테스트 명령이 ddd 폴더에서 돈다

포맷
- 이클립스 포맷터를 공백 4칸으로 맞춘다. lint 도구는 두지 않는다

## 핵심 코드
public static void main(String[] args) {
    System.out.println("온라인 주문 프로그램을 시작합니다");
}

## 테스트 명령
cd ddd
javac -d bin --source-path src src/ddd/Main.java
javac -d ../bin-test -cp "bin;../test-lib/junit-platform-console-standalone.jar" ../tests/ddd/*.java
java -jar ../test-lib/junit-platform-console-standalone.jar execute -cp "bin;../bin-test" --scan-classpath
맥과 리눅스는 ; 대신 :를 쓴다

## 할 일 연결
| 할 일 | 파일 |
|---|---|
| S1 | Main.java, .gitignore |
| S2 | .project, .classpath, org.eclipse.jdt.core.prefs, org.eclipse.core.resources.prefs |
| S3 | junit-platform-console-standalone.jar, ProjectSetupTest.java, contract.md, .gitignore |

## 라이브러리
- 추가 없음. contract의 JUnit 5를 쓴다

## 다른 장 파일
- 없음
