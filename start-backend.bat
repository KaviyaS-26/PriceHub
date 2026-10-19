@echo off
echo Starting PriceHub Spring Boot Backend on http://localhost:8080...
cd /d "%~dp0\backend"
call "..\maven\apache-maven-3.9.6\bin\mvn.cmd" spring-boot:run
pause
