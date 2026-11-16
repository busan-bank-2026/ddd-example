package ddd;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProjectSetupTest {

    @Test
    @DisplayName("S1 프로그램을 실행하면 콘솔에 시작 글을 보여준다")
    void s1() {
        PrintStream original = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
        try {
            Main.main(new String[0]);
        } finally {
            System.setOut(original);
        }

        assertTrue(out.toString(StandardCharsets.UTF_8).contains("온라인 주문 프로그램을 시작합니다"));
    }

    @Test
    @DisplayName("S2 이클립스에서 ddd 폴더를 프로젝트로 열고 Main을 실행할 수 있다")
    void s2() throws Exception {
        String project = Files.readString(Path.of(".project"));
        String classpath = Files.readString(Path.of(".classpath"));
        String prefs = Files.readString(Path.of(".settings/org.eclipse.jdt.core.prefs"));
        String resources = Files.readString(Path.of(".settings/org.eclipse.core.resources.prefs"));

        assertTrue(project.contains("<name>ddd</name>"));
        assertTrue(project.contains("org.eclipse.jdt.core.javanature"));
        assertTrue(classpath.contains("kind=\"src\" path=\"src\""));
        assertTrue(classpath.contains("JavaSE-21"));
        assertTrue(prefs.contains("org.eclipse.jdt.core.compiler.compliance=21"));
        assertTrue(resources.contains("encoding/<project>=UTF-8"));
        assertFalse(project.contains("PARENT-1-PROJECT_LOC/tests"));
        assertFalse(classpath.contains("path=\"tests\""));
    }

    @Test
    @DisplayName("S3 테스트 명령을 실행하면 행동 테스트 결과를 보여준다")
    void s3() {
        assertTrue(Files.exists(Path.of("../test-lib/junit-platform-console-standalone.jar")));
        assertFalse(Files.exists(Path.of("lib/junit-platform-console-standalone.jar")));
    }
}
