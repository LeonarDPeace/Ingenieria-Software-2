# Script para ejecutar tests con cobertura JaCoCo
# Configura JAVA_HOME y ejecuta Maven

Write-Host "Configurando entorno Java..." -ForegroundColor Cyan
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.8.9-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

Write-Host "Verificando Java..." -ForegroundColor Cyan
java -version

Write-Host "`nVerificando Maven..." -ForegroundColor Cyan
mvn -version

Write-Host "`nEjecutando tests con cobertura JaCoCo..." -ForegroundColor Green
mvn clean test jacoco:report

Write-Host "`nTests completados. Reporte en: target\site\jacoco\index.html" -ForegroundColor Green
