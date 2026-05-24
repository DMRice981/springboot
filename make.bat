@echo off
set "JAVA_HOME=C:\Users\Lenovo\.jdks\jdk-17\jdk-17.0.16+8"
set "PATH=%JAVA_HOME%\bin;%PATH%"
cd /d C:\Users\Lenovo\Desktop\cxode\springboot
echo Building with JDK 17... > build-log.txt
"%JAVA_HOME%\bin\java.exe" -classpath ".mvn\wrapper\maven-wrapper.jar" "-Dmaven.multiModuleProjectDirectory=%CD%" org.apache.maven.wrapper.MavenWrapperMain clean package -DskipTests >> build-log.txt 2>&1
echo Exit: %ERRORLEVEL% >> build-log.txt
