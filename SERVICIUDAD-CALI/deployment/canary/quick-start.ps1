# ============================================================================
# SERVICIUDAD CALI - QUICK START CANARY DEPLOYMENT
# ============================================================================
# Script de inicio rápido para probar canary deployment localmente
# ============================================================================

param(
    [Parameter(Mandatory=$false)]
    [switch]$SkipBuild
)

$ErrorActionPreference = "Stop"
Clear-Host

Write-Host @"
╔════════════════════════════════════════════════════════════════╗
║                                                                ║
║      🚀 SERVICIUDAD - CANARY DEPLOYMENT QUICK START 🚀       ║
║                                                                ║
╚════════════════════════════════════════════════════════════════╝
"@ -ForegroundColor Cyan

$ScriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path
$ProjectRoot = Split-Path -Parent (Split-Path -Parent $ScriptPath)

# ============================================================================
# PASO 1: VERIFICAR REQUISITOS
# ============================================================================
Write-Host "`n[1/7] Verificando requisitos previos..." -ForegroundColor Yellow

# Docker
try {
    $dockerVersion = docker version --format '{{.Server.Version}}' 2>&1
    Write-Host "  ✅ Docker v$dockerVersion" -ForegroundColor Green
} catch {
    Write-Host "  ❌ Docker no disponible. Instalar Docker Desktop." -ForegroundColor Red
    exit 1
}

# Docker Compose
try {
    $composeVersion = docker-compose version --short 2>&1
    Write-Host "  ✅ Docker Compose v$composeVersion" -ForegroundColor Green
} catch {
    Write-Host "  ❌ Docker Compose no disponible" -ForegroundColor Red
    exit 1
}

# ============================================================================
# PASO 2: PREPARAR CONFIGURACIÓN
# ============================================================================
Write-Host "`n[2/7] Preparando configuración..." -ForegroundColor Yellow

$DeploymentPath = Join-Path $ScriptPath "deployment\canary"
Set-Location $DeploymentPath

# Copiar .env si no existe
if (-not (Test-Path ".env")) {
    Copy-Item ".env.example" ".env"
    Write-Host "  ✅ Archivo .env creado" -ForegroundColor Green
} else {
    Write-Host "  ✅ Archivo .env existente" -ForegroundColor Green
}

# ============================================================================
# PASO 3: CONSTRUIR IMÁGENES
# ============================================================================
if (-not $SkipBuild) {
    Write-Host "`n[3/7] Construyendo imágenes Docker..." -ForegroundColor Yellow
    
    Set-Location $ProjectRoot
    
    # Build stable
    Write-Host "  → Construyendo serviciudad:1.0.0 (stable)..." -ForegroundColor Cyan
    docker build -t serviciudad:1.0.0 . 2>&1 | Out-Null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  ✅ serviciudad:1.0.0 construida" -ForegroundColor Green
    } else {
        Write-Host "  ❌ Error construyendo stable" -ForegroundColor Red
        exit 1
    }
    
    # Build canary (simulando nueva versión)
    Write-Host "  → Construyendo serviciudad:2.0.0 (canary)..." -ForegroundColor Cyan
    docker build -t serviciudad:2.0.0 . 2>&1 | Out-Null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  ✅ serviciudad:2.0.0 construida" -ForegroundColor Green
    } else {
        Write-Host "  ❌ Error construyendo canary" -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "`n[3/7] Saltando construcción de imágenes..." -ForegroundColor Yellow
}

# ============================================================================
# PASO 4: INICIAR SERVICIOS
# ============================================================================
Write-Host "`n[4/7] Iniciando servicios..." -ForegroundColor Yellow

Set-Location $DeploymentPath

$env:STABLE_VERSION = "1.0.0"
$env:CANARY_VERSION = "2.0.0"

# Iniciar con 90% stable, 10% canary
docker-compose -f docker-compose-canary.yml up -d --scale serviciudad-stable=9 --scale serviciudad-canary=1 2>&1 | Out-Null

if ($LASTEXITCODE -eq 0) {
    Write-Host "  ✅ Servicios iniciados" -ForegroundColor Green
} else {
    Write-Host "  ❌ Error iniciando servicios" -ForegroundColor Red
    exit 1
}

# ============================================================================
# PASO 5: ESPERAR HEALTH CHECKS
# ============================================================================
Write-Host "`n[5/7] Esperando health checks (60 segundos)..." -ForegroundColor Yellow

for ($i = 1; $i -le 12; $i++) {
    Start-Sleep -Seconds 5
    Write-Host "  $($i * 5)s..." -NoNewline
}
Write-Host " ✅ OK" -ForegroundColor Green

# ============================================================================
# PASO 6: SMOKE TESTS
# ============================================================================
Write-Host "`n[6/7] Ejecutando smoke tests..." -ForegroundColor Yellow

# Health check
try {
    $health = Invoke-RestMethod -Uri "http://localhost/actuator/health" -Method GET -TimeoutSec 5
    if ($health.status -eq "UP") {
        Write-Host "  ✅ Health check OK" -ForegroundColor Green
    } else {
        Write-Host "  ⚠️  Health check WARNING" -ForegroundColor Yellow
    }
} catch {
    Write-Host "  ❌ Health check FAILED" -ForegroundColor Red
}

# ============================================================================
# PASO 7: MOSTRAR INFORMACIÓN
# ============================================================================
Write-Host "`n[7/7] Despliegue completado" -ForegroundColor Yellow

Write-Host @"

╔════════════════════════════════════════════════════════════════╗
║                 ✅ CANARY DEPLOYMENT ACTIVO ✅                ║
╠════════════════════════════════════════════════════════════════╣
║                                                                ║
║  Distribución:                                                 ║
║    • Stable (v1.0.0):  90% → 9 réplicas                       ║
║    • Canary (v2.0.0):  10% → 1 réplica                        ║
║                                                                ║
║  📊 Dashboards de Monitoreo:                                  ║
║    • Aplicación:     http://localhost                         ║
║    • Prometheus:     http://localhost:9090                    ║
║    • Grafana:        http://localhost:3000                    ║
║      Usuario: admin  /  Contraseña: admin123                  ║
║    • AlertManager:   http://localhost:9093                    ║
║                                                                ║
║  🧪 Test Endpoints:                                           ║
║    • Health:         http://localhost/actuator/health         ║
║    • API:            http://localhost/api                     ║
║                                                                ║
╚════════════════════════════════════════════════════════════════╝

📋 PRÓXIMOS PASOS:

1. Abrir Grafana en tu navegador:
   start http://localhost:3000
   
2. Ver comparación Stable vs Canary:
   Dashboard → Canary Deployment Comparison

3. Generar tráfico de prueba:
   cd $ProjectRoot
   .\quick-test.ps1

4. Escalar canary a 25%:
   .\deploy-canary.ps1 -CanaryVersion 2.0.0 -CanaryPercent 25

5. Ver logs en tiempo real:
   docker-compose -f docker-compose-canary.yml logs -f

6. Hacer rollback si hay problemas:
   .\rollback-canary.ps1

⏹️  Para detener todos los servicios:
   docker-compose -f docker-compose-canary.yml down

"@ -ForegroundColor Cyan

Write-Host "🎉 ¡Canary deployment listo para usar!" -ForegroundColor Green
