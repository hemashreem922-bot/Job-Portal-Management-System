@echo off
title Job Portal Management System
echo ========================================================
echo   Job Portal Management System (Java Spring Boot + SQL)
echo ========================================================
echo.

IF NOT EXIST "%JAVA_HOME%\bin\java.exe" (
  IF EXIST "C:\Program Files\Java\jdk-26.0.2\bin\java.exe" (
    set "JAVA_HOME=C:\Program Files\Java\jdk-26.0.2"
  )
)

echo Starting application on http://localhost:8080 ...
echo Database: Embedded Persistent H2 SQL Database (Zero config required)
echo H2 Console: http://localhost:8080/h2-console
echo.
echo Press Ctrl+C at any time to stop the server.
echo.

call .\mvnw.cmd spring-boot:run

pause
