# project-setup 코드 요약

## T1 시작 글을 보여주는 Main 만들기
커밋: fb23439

| 파일 | 변경 |
|---|---|
| ddd/src/ddd/Main.java | 새로 |

```java
// ddd/src/ddd/Main.java
package ddd;

public class Main {

    public static void main(String[] args) {
        System.out.println("온라인 주문 프로그램을 시작합니다");
    }
}
```

## T2 이클립스 프로젝트로 열기
커밋: 61f13de

| 파일 | 변경 |
|---|---|
| ddd/.project | 새로 |
| ddd/.classpath | 새로 |
| ddd/.settings/org.eclipse.jdt.core.prefs | 새로 |
| ddd/.settings/org.eclipse.core.resources.prefs | 새로 |

```xml
<!-- ddd/.project -->
<?xml version="1.0" encoding="UTF-8"?>
<projectDescription>
    <name>ddd</name>
    <comment></comment>
    <projects>
    </projects>
    <buildSpec>
        <buildCommand>
            <name>org.eclipse.jdt.core.javabuilder</name>
            <arguments>
            </arguments>
        </buildCommand>
    </buildSpec>
    <natures>
        <nature>org.eclipse.jdt.core.javanature</nature>
    </natures>
</projectDescription>
```

```xml
<!-- ddd/.classpath -->
<?xml version="1.0" encoding="UTF-8"?>
<classpath>
    <classpathentry kind="src" path="src"/>
    <classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-21"/>
    <classpathentry kind="output" path="bin"/>
</classpath>
```

```properties
# ddd/.settings/org.eclipse.jdt.core.prefs
eclipse.preferences.version=1
org.eclipse.jdt.core.compiler.codegen.targetPlatform=21
org.eclipse.jdt.core.compiler.compliance=21
org.eclipse.jdt.core.compiler.release=enabled
org.eclipse.jdt.core.compiler.source=21
org.eclipse.jdt.core.formatter.tabulation.char=space
org.eclipse.jdt.core.formatter.tabulation.size=4
org.eclipse.jdt.core.formatter.indentation.size=4
```

```properties
# ddd/.settings/org.eclipse.core.resources.prefs
eclipse.preferences.version=1
encoding/<project>=UTF-8
```

## T3 테스트 실행기 붙이기
커밋: 59ab1a2

| 파일 | 변경 |
|---|---|
| ddd/lib/junit-platform-console-standalone.jar | 새로. JUnit 1.11.4 jar 파일 하나 |
| ddd/.project | 고침 |
| ddd/.classpath | 고침 |

```diff
diff --git a/ddd/.classpath b/ddd/.classpath
index 6f52706..043d67b 100644
--- a/ddd/.classpath
+++ b/ddd/.classpath
@@ -1,6 +1,8 @@
 <?xml version="1.0" encoding="UTF-8"?>
 <classpath>
     <classpathentry kind="src" path="src"/>
+    <classpathentry kind="src" path="tests"/>
+    <classpathentry kind="lib" path="lib/junit-platform-console-standalone.jar"/>
     <classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-21"/>
     <classpathentry kind="output" path="bin"/>
 </classpath>
diff --git a/ddd/.project b/ddd/.project
index 548767b..4c0b4e9 100644
--- a/ddd/.project
+++ b/ddd/.project
@@ -14,4 +14,11 @@
     <natures>
         <nature>org.eclipse.jdt.core.javanature</nature>
     </natures>
+    <linkedResources>
+        <link>
+            <name>tests</name>
+            <type>2</type>
+            <locationURI>PARENT-1-PROJECT_LOC/tests</locationURI>
+        </link>
+    </linkedResources>
 </projectDescription>
```

## T4 이클립스에서 tests 연결 빼기
커밋: 1ee2df4

| 파일 | 변경 |
|---|---|
| ddd/.project | 고침 |
| ddd/.classpath | 고침 |

```diff
diff --git a/ddd/.classpath b/ddd/.classpath
index 043d67b..6f52706 100644
--- a/ddd/.classpath
+++ b/ddd/.classpath
@@ -1,8 +1,6 @@
 <?xml version="1.0" encoding="UTF-8"?>
 <classpath>
     <classpathentry kind="src" path="src"/>
-    <classpathentry kind="src" path="tests"/>
-    <classpathentry kind="lib" path="lib/junit-platform-console-standalone.jar"/>
     <classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-21"/>
     <classpathentry kind="output" path="bin"/>
 </classpath>
diff --git a/ddd/.project b/ddd/.project
index 4c0b4e9..548767b 100644
--- a/ddd/.project
+++ b/ddd/.project
@@ -14,11 +14,4 @@
     <natures>
         <nature>org.eclipse.jdt.core.javanature</nature>
     </natures>
-    <linkedResources>
-        <link>
-            <name>tests</name>
-            <type>2</type>
-            <locationURI>PARENT-1-PROJECT_LOC/tests</locationURI>
-        </link>
-    </linkedResources>
 </projectDescription>
```

## T5 테스트용 파일을 학생 프로젝트 밖으로 빼기
커밋: 3bbd71f

| 파일 | 변경 |
|---|---|
| ddd/lib/junit-platform-console-standalone.jar | 지움. 저장소 루트 test-lib로 옮김 |
