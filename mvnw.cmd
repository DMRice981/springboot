@REM Maven Wrapper startup script for Windows
@echo off
setlocal
set MVNW_LAUNCHER=mvnw.cmd
if exist "%~dp0.mvn\wrapper\maven-wrapper.properties" goto properties
set WRAPPER_JAR="%~dp0.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
if exist %WRAPPER_JAR% goto wrapper
powershell -Command "(New-Object Net.WebClient).DownloadFile('%WRAPPER_URL%', '%WRAPPER_JAR%')"
:wrapper
java %JAVA_OPTS% -classpath %WRAPPER_JAR% org.apache.maven.wrapper.MavenWrapperMain %*
goto end
:properties
java %JAVA_OPTS% -classpath "%~dp0.mvn\wrapper\maven-wrapper.jar" org.apache.maven.wrapper.MavenWrapperMain %*
:end
@endlocal
