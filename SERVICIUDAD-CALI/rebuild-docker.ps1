# Script para reconstruir completamente el proyecto Docker
# Uso: .\rebuild-docker.ps1

Write-Host "Reconstruccion completa del contenedor Docker" -ForegroundColor Cyan
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host ""

# 1. Detener y eliminar contenedores existentes
Write-Host "1. Deteniendo contenedores..." -ForegroundColor Yellow
docker-compose down -v

if ($LASTEXITCODE -ne 0) {
    Write-Host "   Error al detener contenedores" -ForegroundColor Red
    exit 1
}
Write-Host "   Contenedores detenidos correctamente" -ForegroundColor Green

# 2. Limpiar imagenes antiguas del proyecto
Write-Host ""
Write-Host "2. Limpiando imagenes antiguas..." -ForegroundColor Yellow
$imageName = "serviciudad-cali-app"
$oldImages = docker images -q $imageName

if ($oldImages) {
    docker rmi -f $oldImages 2>$null
    Write-Host "   Imagenes antiguas eliminadas" -ForegroundColor Green
} else {
    Write-Host "   No hay imagenes antiguas" -ForegroundColor Gray
}

# 3. Construir nueva imagen sin cache
Write-Host ""
Write-Host "3. Construyendo nueva imagen..." -ForegroundColor Yellow
Write-Host "   (Esto puede tardar 2-3 minutos)" -ForegroundColor Gray
docker-compose build --no-cache

if ($LASTEXITCODE -ne 0) {
    Write-Host "   Error en la construccion" -ForegroundColor Red
    exit 1
}
Write-Host "   Imagen construida exitosamente" -ForegroundColor Green

# 4. Iniciar contenedores
Write-Host ""
Write-Host "4. Iniciando contenedores..." -ForegroundColor Yellow
docker-compose up -d

if ($LASTEXITCODE -ne 0) {
    Write-Host "   Error al iniciar contenedores" -ForegroundColor Red
    exit 1
}
Write-Host "   Contenedores iniciados correctamente" -ForegroundColor Green

# 5. Esperar a que la aplicacion este lista
Write-Host ""
Write-Host "5. Esperando a que la aplicacion este lista..." -ForegroundColor Yellow

$maxRetries = 30
$retryCount = 0
$apiReady = $false

while (-not $apiReady -and $retryCount -lt $maxRetries) {
    $retryCount++
    
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -TimeoutSec 2 -ErrorAction Stop
        
        if ($response.StatusCode -eq 200) {
            $apiReady = $true
            Write-Host "   Aplicacion lista y respondiendo" -ForegroundColor Green
        }
    } catch {
        Write-Host "   Intento $retryCount/$maxRetries..." -ForegroundColor Gray
        Start-Sleep -Seconds 1
    }
}

if (-not $apiReady) {
    Write-Host "   La aplicacion no respondio en 30 segundos" -ForegroundColor Yellow
    Write-Host "   Verificar logs: docker-compose logs -f app" -ForegroundColor White
    exit 1
}

# 6. Verificar estado final
Write-Host ""
Write-Host "6. Estado de contenedores:" -ForegroundColor Yellow
docker-compose ps

Write-Host ""
Write-Host "===============================================" -ForegroundColor Green
Write-Host "Reconstruccion completada exitosamente" -ForegroundColor Green
Write-Host "===============================================" -ForegroundColor Green
Write-Host ""
Write-Host "La aplicacion esta disponible en:" -ForegroundColor Cyan
Write-Host "  http://localhost:8080" -ForegroundColor White
Write-Host ""
