@echo off
set JAVA_HOME=C:\Users\Lenovo\.jdks\jdk-17\jdk-17.0.16+8
set PATH=%JAVA_HOME%\bin;%PATH%

cd /d C:\Users\Lenovo\Desktop\cxode\springboot

echo === Building with JDK 17 ===
call mvnw.cmd clean package -DskipTests -q

echo.
echo === Starting Spring Boot ===
java -jar target\springboot-0.0.1-SNAPSHOT.jar
