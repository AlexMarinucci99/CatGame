@echo off
setlocal
where java >nul 2>&1
if errorlevel 1 (
  echo Java 21 is required. Install a Java 21 JDK and add java to PATH. 1>&2
  exit /b 1
)
where javac >nul 2>&1
if errorlevel 1 (
  echo A Java 21 JDK is required; javac was not found. 1>&2
  exit /b 1
)
for /f "tokens=2" %%V in ('javac -version 2^>^&1') do set "JDK_VERSION=%%V"
for /f "tokens=1 delims=." %%V in ("%JDK_VERSION%") do set "JDK_MAJOR=%%V"
if not defined JDK_MAJOR (
  echo Cannot determine the JDK version. Check javac and JAVA_HOME. 1>&2
  exit /b 1
)
if %JDK_MAJOR% LSS 21 (
  echo Java 21 or newer is required. Check java, javac and JAVA_HOME. 1>&2
  exit /b 1
)
where mvn >nul 2>&1
if errorlevel 1 (
  echo Apache Maven is required. Install Maven and add mvn to PATH. 1>&2
  exit /b 1
)
cd /d "%~dp0"
if errorlevel 1 (
  echo Cannot access the CatGame directory. 1>&2
  exit /b 1
)
call mvn -B -ntp -DskipTests package javafx:run
exit /b %ERRORLEVEL%
