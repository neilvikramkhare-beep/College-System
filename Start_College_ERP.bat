@echo off
title College Administration ERP Launcher
color 0B

echo ===================================================
echo     Starting College Administration ERP System
echo ===================================================
echo.

echo [1/3] Setting Java 17 Environment...
set JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

cd /d "%~dp0"

echo [2/3] Launching Java Spring Boot Server in the background...
start "College ERP Backend Server" cmd /c "title College ERP Backend && color 0A && echo Server is running. Do not close this window! && mvn spring-boot:run"

echo [3/3] Waiting for the server to initialize (approx 10 seconds)...
timeout /t 12 /nobreak >nul

echo.
echo Server should be ready! Opening the dashboard in your default browser...
start http://localhost:8080/

echo.
echo Launch sequence complete. You can close this launcher window.
timeout /t 5 >nul
