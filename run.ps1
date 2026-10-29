# Job Portal Management System PowerShell Launcher
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "  Job Portal Management System (Java Spring Boot + SQL)  " -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host ""

if (-not (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
    if (Test-Path "C:\Program Files\Java\jdk-26.0.2\bin\java.exe") {
        $env:JAVA_HOME = "C:\Program Files\Java\jdk-26.0.2"
    }
}

Write-Host "Launching Spring Boot application on http://localhost:8080 ..." -ForegroundColor Green
Write-Host "Database: Relational SQL Database (H2 file-persistent by default)" -ForegroundColor Yellow
Write-Host "H2 Console: http://localhost:8080/h2-console" -ForegroundColor Yellow
Write-Host ""

& .\mvnw.cmd spring-boot:run
