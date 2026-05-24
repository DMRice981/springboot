@echo off
set JAVA_HOME=C:\Users\Lenovo\.jdks\jdk-17\jdk-17.0.16+8
cd /d C:\Users\Lenovo\Desktop\cxode\springboot
start /min "%JAVA_HOME%\bin\java.exe" -jar target\springboot-0.0.1-SNAPSHOT.jar > app.log 2>&1
