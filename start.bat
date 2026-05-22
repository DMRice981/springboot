@echo off
REM Spring Boot Application Startup Script
echo Starting Spring Boot Application...

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo Error: Java is not installed or not in PATH
    pause
    exit /b 1
)

REM Navigate to project directory
cd /d "%~dp0"

REM Check if Maven is available
where mvn >nul 2>&1
if errorlevel 1 (
    echo Error: Maven is not installed or not in PATH
    echo Please install Maven or use Maven Wrapper (mvnw.cmd)
    pause
    exit /b 1
)

REM Clean and compile the project
echo Compiling project...
call mvn clean compile

REM Run Spring Boot application
echo Starting application on port 8081...
call mvn spring-boot:run

pause
