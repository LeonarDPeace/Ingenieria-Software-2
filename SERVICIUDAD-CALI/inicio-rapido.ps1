# ========================================
# ServiCiudad Cali - Script de Inicio Rapido
# ========================================
# 
# Este script automatiza:
# 1. Levantar Docker (app + PostgreSQL)
# 2. Verificar que la API este respondiendo
# 3. Abrir el frontend en el navegador
#
# Autor: Equipo ServiCiudad Cali
# Fecha: Octubre 2025
# ========================================

Write-Host "========================================" -ForegroundColor Cyan
Write-Host " ServiCiudad Cali - Inicio Rapido" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# ========================================
# 1. VERIFICAR DOCKER
# ========================================

Write-Host "[1/5] Verificando Docker Desktop..." -ForegroundColor Yellow

try {
    $dockerVersion = docker --version
    Write-Host "OK Docker encontrado: $dockerVersion" -ForegroundColor Green
} catch {
    Write-Host "ERROR: Docker no esta instalado o no esta en el PATH" -ForegroundColor Red
    Write-Host ""
    Write-Host "Solucion:" -ForegroundColor Yellow
    Write-Host "1. Descargar Docker Desktop: https://www.docker.com/products/docker-desktop" -ForegroundColor White
    Write-Host "2. Instalar y reiniciar el equipo" -ForegroundColor White
    Write-Host "3. Ejecutar este script nuevamente" -ForegroundColor White
    pause
    exit 1
}

# ========================================
# 2. NAVEGAR AL DIRECTORIO DEL PROYECTO
# ========================================

Write-Host ""
Write-Host "[2/5] Navegando al directorio del proyecto..." -ForegroundColor Yellow

$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptPath

Write-Host "OK Ubicacion: $scriptPath" -ForegroundColor Green

# ========================================
# 3. LEVANTAR DOCKER COMPOSE
# ========================================

Write-Host ""
Write-Host "[3/5] Levantando contenedores Docker..." -ForegroundColor Yellow

# Verificar si docker-compose.yml existe
if (-Not (Test-Path "docker-compose.yml")) {
    Write-Host "ERROR: No se encontro docker-compose.yml en este directorio" -ForegroundColor Red
    Write-Host "Ubicacion actual: $(Get-Location)" -ForegroundColor White
    pause
    exit 1
}

# Levantar contenedores en modo detached
Write-Host "Ejecutando: docker-compose up -d" -ForegroundColor White
docker-compose up -d

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Fallo docker-compose up" -ForegroundColor Red
    Write-Host ""
    Write-Host "Posibles causas:" -ForegroundColor Yellow
    Write-Host "1. Puerto 8080 o 5432 ya esta en uso" -ForegroundColor White
    Write-Host "2. Docker Desktop no esta corriendo" -ForegroundColor White
    Write-Host "3. Archivo docker-compose.yml corrupto" -ForegroundColor White
    Write-Host ""
    Write-Host "Solucion rapida:" -ForegroundColor Yellow
    Write-Host "docker-compose down" -ForegroundColor White
    Write-Host "docker-compose up -d --build" -ForegroundColor White
    pause
    exit 1
}

Write-Host "OK Contenedores levantados" -ForegroundColor Green

# Mostrar estado de contenedores
Write-Host ""
Write-Host "Estado de contenedores:" -ForegroundColor Cyan
docker-compose ps

# ========================================
# 4. ESPERAR A QUE LA API ESTE LISTA
# ========================================

Write-Host ""
Write-Host "[4/5] Esperando a que la API este lista..." -ForegroundColor Yellow

$maxRetries = 30
$retryCount = 0
$apiReady = $false

while (-not $apiReady -and $retryCount -lt $maxRetries) {
    $retryCount++
    
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -TimeoutSec 2 -ErrorAction Stop
        
        if ($response.StatusCode -eq 200) {
            $apiReady = $true
            Write-Host "OK API respondiendo correctamente" -ForegroundColor Green
            
            # Parsear y mostrar detalles
            $health = $response.Content | ConvertFrom-Json
            Write-Host "  Status: $($health.status)" -ForegroundColor White
        }
    } catch {
        Write-Host "  Intento $retryCount/$maxRetries - Esperando..." -ForegroundColor Gray
        Start-Sleep -Seconds 1
    }
}

if (-not $apiReady) {
    Write-Host "ADVERTENCIA: La API no respondio despues de 30 segundos" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Posibles causas:" -ForegroundColor Yellow
    Write-Host "1. La aplicacion esta iniciando (Spring Boot tarda 20-40 seg)" -ForegroundColor White
    Write-Host "2. Error en la aplicacion" -ForegroundColor White
    Write-Host ""
    Write-Host "Ver logs con:" -ForegroundColor Cyan
    Write-Host "docker-compose logs -f app" -ForegroundColor White
    Write-Host ""
    
    $continue = Read-Host "Desea continuar y abrir el frontend de todas formas? (S/N)"
    if ($continue -ne "S" -and $continue -ne "s") {
        Write-Host "Proceso cancelado" -ForegroundColor Yellow
        pause
        exit 0
    }
}

# ========================================
# 5. INICIAR SERVIDOR HTTP Y ABRIR FRONTEND
# ========================================

Write-Host ""
Write-Host "[5/5] Iniciando servidor HTTP y abriendo frontend..." -ForegroundColor Yellow

$frontendPath = Join-Path $scriptPath "frontend\index.html"

if (-Not (Test-Path $frontendPath)) {
    Write-Host "ERROR: No se encontro frontend/index.html" -ForegroundColor Red
    Write-Host "Ruta esperada: $frontendPath" -ForegroundColor White
    pause
    exit 1
}

# Verificar si Python esta instalado
try {
    $pythonVersion = python --version 2>&1
    Write-Host "OK Python encontrado: $pythonVersion" -ForegroundColor Green
    
    # Iniciar servidor HTTP en segundo plano
    $frontendDir = Join-Path $scriptPath "frontend"
    Write-Host "Iniciando servidor HTTP en puerto 5500..." -ForegroundColor White
    
    # Crear un trabajo en segundo plano para el servidor HTTP
    $job = Start-Job -ScriptBlock {
        param($dir)
        Set-Location $dir
        python -m http.server 5500
    } -ArgumentList $frontendDir
    
    # Esperar 2 segundos para que el servidor inicie
    Start-Sleep -Seconds 2
    
    # Abrir navegador en localhost:5500
    Start-Process "http://localhost:5500/index.html"
    
    Write-Host "OK Servidor HTTP iniciado (Job ID: $($job.Id))" -ForegroundColor Green
    Write-Host "OK Frontend disponible en: http://localhost:5500/index.html" -ForegroundColor Green
    
} catch {
    Write-Host "ADVERTENCIA: Python no encontrado. Abriendo con file://" -ForegroundColor Yellow
    Write-Host "Nota: Puede haber problemas de CORS. Instale Python para mejor experiencia." -ForegroundColor Yellow
    Start-Process $frontendPath
    Write-Host "OK Frontend abierto (puede mostrar error de conexion Docker)" -ForegroundColor Yellow
}

# ========================================
# RESUMEN FINAL
# ========================================

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host " INICIADO EXITOSAMENTE" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "URLs importantes:" -ForegroundColor Yellow
Write-Host "  Frontend:      http://localhost:5500/index.html" -ForegroundColor White
Write-Host "  API Health:    http://localhost:8080/actuator/health" -ForegroundColor White
Write-Host "  Swagger UI:    http://localhost:8080/swagger-ui.html" -ForegroundColor White
Write-Host "  PostgreSQL:    localhost:5432" -ForegroundColor White
Write-Host ""
Write-Host "Credenciales de API:" -ForegroundColor Yellow
Write-Host "  Usuario:       serviciudad" -ForegroundColor White
Write-Host "  Contrasena:    dev2025" -ForegroundColor White
Write-Host ""
Write-Host "Clientes de prueba:" -ForegroundColor Yellow
Write-Host "  0001234567 - Juan Carlos Perez" -ForegroundColor White
Write-Host "  0002345678 - Maria Fernanda Lopez" -ForegroundColor White
Write-Host "  0003456789 - Carlos Alberto Gomez" -ForegroundColor White
Write-Host ""
Write-Host "Comandos utiles:" -ForegroundColor Yellow
Write-Host "  Ver logs app:       docker-compose logs -f app" -ForegroundColor White
Write-Host "  Ver logs BD:        docker-compose logs -f postgres" -ForegroundColor White
Write-Host "  Reiniciar:          docker-compose restart" -ForegroundColor White
Write-Host "  Detener todo:       docker-compose down" -ForegroundColor White
Write-Host "  Detener servidor:   Stop-Job -Id <Job ID>" -ForegroundColor White
Write-Host "  Borrar datos:       docker-compose down -v" -ForegroundColor White
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "IMPORTANTE: El servidor HTTP esta corriendo en segundo plano." -ForegroundColor Yellow
Write-Host "Para detenerlo, cierre esta ventana o ejecute:" -ForegroundColor Yellow
Write-Host "  Get-Job | Stop-Job" -ForegroundColor White
Write-Host ""

# Preguntar si desea ver los logs
$verLogs = Read-Host "Desea ver los logs de la aplicacion en tiempo real? (S/N)"

if ($verLogs -eq "S" -or $verLogs -eq "s") {
    Write-Host ""
    Write-Host "Mostrando logs (presione Ctrl+C para salir):" -ForegroundColor Cyan
    Write-Host ""
    docker-compose logs -f app
} else {
    Write-Host ""
    Write-Host "Listo! Puede cerrar esta ventana." -ForegroundColor Green
    Write-Host ""
    pause
}
