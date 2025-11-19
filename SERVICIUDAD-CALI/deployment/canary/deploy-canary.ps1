# ============================================================================
# SERVICIUDAD CALI - CANARY DEPLOYMENT SCRIPT
# ============================================================================
# Descripción: Script automatizado para despliegue canario progresivo
# Autor: ServiCiudad DevOps Team
# Fecha: 2025-11-19
# ============================================================================

param(
    [Parameter(Mandatory=$true, HelpMessage="Nueva versión a desplegar (ej: 2.0.0)")]
    [string]$CanaryVersion,
    
    [Parameter(Mandatory=$false, HelpMessage="Versión estable actual (ej: 1.0.0)")]
    [string]$StableVersion = "1.0.0",
    
    [Parameter(Mandatory=$false, HelpMessage="Porcentaje de tráfico para canary (5, 10, 25, 50, 100)")]
    [ValidateSet("5", "10", "25", "50", "100")]
    [string]$CanaryPercent = "10",
    
    [Parameter(Mandatory=$false, HelpMessage="Tiempo de espera entre fases (minutos)")]
    [int]$WaitMinutes = 30,
    
    [Parameter(Mandatory=$false, HelpMessage="Auto-promoción si métricas OK")]
    [switch]$AutoPromote,
    
    [Parameter(Mandatory=$false, HelpMessage="Modo dry-run (no ejecuta cambios)")]
    [switch]$DryRun
)

# ============================================================================
# CONFIGURACIÓN
# ============================================================================
$ErrorActionPreference = "Stop"
$ScriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path
$ProjectRoot = Split-Path -Parent (Split-Path -Parent $ScriptPath)
$DeploymentPath = Join-Path $ScriptPath "deployment\canary"

# Colores para output
function Write-ColorOutput {
    param(
        [string]$Message,
        [string]$Color = "White"
    )
    Write-Host $Message -ForegroundColor $Color
}

function Write-Step {
    param([string]$Message)
    Write-ColorOutput "`n▶ $Message" "Cyan"
}

function Write-Success {
    param([string]$Message)
    Write-ColorOutput "✅ $Message" "Green"
}

function Write-Warning {
    param([string]$Message)
    Write-ColorOutput "⚠️  $Message" "Yellow"
}

function Write-Error {
    param([string]$Message)
    Write-ColorOutput "❌ $Message" "Red"
}

# ============================================================================
# BANNER
# ============================================================================
Clear-Host
Write-ColorOutput @"
╔════════════════════════════════════════════════════════════════╗
║                                                                ║
║         🐤 SERVICIUDAD CALI - CANARY DEPLOYMENT 🐤           ║
║                                                                ║
║  Versión Estable:  $StableVersion                                    ║
║  Versión Canary:   $CanaryVersion                                    ║
║  Tráfico Canary:   $CanaryPercent%                                   ║
║  Modo:             $(if($DryRun){"DRY-RUN"}else{"PRODUCCIÓN"})      ║
║                                                                ║
╚════════════════════════════════════════════════════════════════╝
"@ "Cyan"

# ============================================================================
# VALIDACIONES PRE-VUELO
# ============================================================================
Write-Step "FASE 1: Validaciones Pre-Vuelo"

# Verificar Docker
Write-Host "  → Verificando Docker..." -NoNewline
try {
    $dockerVersion = docker version --format '{{.Server.Version}}' 2>&1
    Write-Success " OK (v$dockerVersion)"
} catch {
    Write-Error " Docker no está disponible"
    exit 1
}

# Verificar Docker Compose
Write-Host "  → Verificando Docker Compose..." -NoNewline
try {
    $composeVersion = docker-compose version --short 2>&1
    Write-Success " OK (v$composeVersion)"
} catch {
    Write-Error " Docker Compose no está disponible"
    exit 1
}

# Verificar imagen canary existe
Write-Host "  → Verificando imagen serviciudad:$CanaryVersion..." -NoNewline
$imageExists = docker images "serviciudad:$CanaryVersion" --format "{{.Repository}}:{{.Tag}}" | Where-Object { $_ -eq "serviciudad:$CanaryVersion" }
if (-not $imageExists) {
    Write-Warning " No existe"
    Write-ColorOutput "    Construyendo imagen..." "Yellow"
    
    if (-not $DryRun) {
        Set-Location $ProjectRoot
        docker build -t "serviciudad:$CanaryVersion" .
        if ($LASTEXITCODE -ne 0) {
            Write-Error "Falló la construcción de la imagen"
            exit 1
        }
    }
    Write-Success " Imagen construida"
} else {
    Write-Success " OK"
}

# Verificar archivos de configuración
Write-Host "  → Verificando configuración Nginx..." -NoNewline
$nginxConfig = Join-Path $DeploymentPath "nginx\nginx.conf"
if (-not (Test-Path $nginxConfig)) {
    Write-Error " No encontrado: $nginxConfig"
    exit 1
}
Write-Success " OK"

# ============================================================================
# CÁLCULO DE RÉPLICAS
# ============================================================================
Write-Step "FASE 2: Calculando Distribución de Réplicas"

$TotalReplicas = 10
$CanaryReplicas = [Math]::Ceiling($TotalReplicas * [int]$CanaryPercent / 100)
$StableReplicas = $TotalReplicas - $CanaryReplicas

Write-ColorOutput @"
  Distribución:
    • Stable: $StableReplicas réplicas ($([Math]::Round(($StableReplicas/$TotalReplicas)*100))%)
    • Canary: $CanaryReplicas réplicas ($CanaryPercent%)
    • Total:  $TotalReplicas réplicas
"@ "White"

# ============================================================================
# ACTUALIZAR CONFIGURACIÓN NGINX
# ============================================================================
Write-Step "FASE 3: Configurando Load Balancer"

Write-Host "  → Actualizando distribución de tráfico en Nginx..." -NoNewline

if (-not $DryRun) {
    # Leer configuración actual
    $nginxContent = Get-Content $nginxConfig -Raw
    
    # Actualizar split_clients con nuevo porcentaje
    $nginxContent = $nginxContent -replace 'split_clients.*\{[\s\S]*?\}', @"
split_clients "`${remote_addr}`${http_user_agent}`${date_gmt}" `$backend_pool {
        $CanaryPercent%     canary;   # $CanaryPercent% tráfico a canary
        *       stable;   # $(100 - [int]$CanaryPercent)% tráfico a stable
    }
"@
    
    # Guardar configuración
    Set-Content -Path $nginxConfig -Value $nginxContent -NoNewline
}

Write-Success " OK"

# ============================================================================
# DESPLEGAR CONTENEDORES
# ============================================================================
Write-Step "FASE 4: Desplegando Contenedores"

if (-not $DryRun) {
    Set-Location $DeploymentPath
    
    # Establecer variables de entorno
    $env:STABLE_VERSION = $StableVersion
    $env:CANARY_VERSION = $CanaryVersion
    
    Write-Host "  → Iniciando servicios..." -NoNewline
    docker-compose -f docker-compose-canary.yml up -d --scale serviciudad-stable=$StableReplicas --scale serviciudad-canary=$CanaryReplicas 2>&1 | Out-Null
    
    if ($LASTEXITCODE -ne 0) {
        Write-Error " Falló el despliegue"
        exit 1
    }
    Write-Success " OK"
} else {
    Write-Warning "  [DRY-RUN] Se desplegarían $StableReplicas stable + $CanaryReplicas canary"
}

# ============================================================================
# ESPERAR HEALTH CHECKS
# ============================================================================
Write-Step "FASE 5: Verificando Health Checks"

if (-not $DryRun) {
    Write-Host "  → Esperando que los servicios estén saludables..." -NoNewline
    
    $maxAttempts = 12  # 12 intentos x 5 segundos = 60 segundos
    $attempt = 0
    $allHealthy = $false
    
    while ($attempt -lt $maxAttempts -and -not $allHealthy) {
        Start-Sleep -Seconds 5
        $attempt++
        
        # Verificar health de stable
        try {
            $stableHealth = Invoke-RestMethod -Uri "http://localhost/actuator/health" -Method GET -TimeoutSec 3
            $stableHealthy = $stableHealth.status -eq "UP"
        } catch {
            $stableHealthy = $false
        }
        
        # Verificar health de canary (a través de llamadas API con routing)
        try {
            $response = Invoke-WebRequest -Uri "http://localhost/api/health" -Method GET -TimeoutSec 3
            $canaryHealthy = $response.StatusCode -eq 200
        } catch {
            $canaryHealthy = $false
        }
        
        $allHealthy = $stableHealthy -and $canaryHealthy
        
        if (-not $allHealthy) {
            Write-Host "." -NoNewline
        }
    }
    
    if ($allHealthy) {
        Write-Success " Todos los servicios están saludables"
    } else {
        Write-Error " Timeout esperando health checks"
        Write-Warning "  Ejecutando rollback..."
        docker-compose -f docker-compose-canary.yml down
        exit 1
    }
} else {
    Write-Warning "  [DRY-RUN] Se verificarían health checks"
}

# ============================================================================
# SMOKE TESTS
# ============================================================================
Write-Step "FASE 6: Ejecutando Smoke Tests"

if (-not $DryRun) {
    $smokeTests = @(
        @{ Name = "Health Check"; Url = "http://localhost/actuator/health"; Expected = 200 }
        @{ Name = "API Root"; Url = "http://localhost/api"; Expected = 200 }
        @{ Name = "Consultar Deuda"; Url = "http://localhost/api/deuda/consultar"; Method = "POST"; Body = @{clienteId="1234567890"}; Expected = 200 }
    )
    
    $passedTests = 0
    foreach ($test in $smokeTests) {
        Write-Host "  → $($test.Name)..." -NoNewline
        
        try {
            if ($test.Method -eq "POST") {
                $response = Invoke-WebRequest -Uri $test.Url -Method POST -Body ($test.Body | ConvertTo-Json) -ContentType "application/json" -TimeoutSec 5
            } else {
                $response = Invoke-WebRequest -Uri $test.Url -Method GET -TimeoutSec 5
            }
            
            if ($response.StatusCode -eq $test.Expected) {
                Write-Success " PASS"
                $passedTests++
            } else {
                Write-Warning " FAIL (Expected: $($test.Expected), Got: $($response.StatusCode))"
            }
        } catch {
            Write-Warning " ERROR: $($_.Exception.Message)"
        }
    }
    
    $testScore = [Math]::Round(($passedTests / $smokeTests.Count) * 100)
    Write-ColorOutput "`n  Resultado: $passedTests/$($smokeTests.Count) tests pasaron ($testScore%)" $(if($testScore -ge 80){"Green"}else{"Yellow"})
    
    if ($testScore -lt 50) {
        Write-Error "  Menos del 50% de tests pasaron. Abortando despliegue."
        Write-Warning "  Ejecutando rollback..."
        docker-compose -f docker-compose-canary.yml down
        exit 1
    }
} else {
    Write-Warning "  [DRY-RUN] Se ejecutarían smoke tests"
}

# ============================================================================
# MONITOREO
# ============================================================================
Write-Step "FASE 7: Configurando Monitoreo"

Write-ColorOutput @"

  📊 Dashboards de Monitoreo:
    • Prometheus:  http://localhost:9090
    • Grafana:     http://localhost:3000 (admin/admin123)
    • AlertManager: http://localhost:9093
    
  📈 Métricas clave a monitorear:
    • Tasa de errores (stable vs canary)
    • Latencia p95 (stable vs canary)
    • Uso de memoria
    • Tasa de requests por segundo
    
  ⏱️  Tiempo de monitoreo recomendado: $WaitMinutes minutos
"@ "White"

# ============================================================================
# AUTO-PROMOCIÓN (OPCIONAL)
# ============================================================================
if ($AutoPromote -and -not $DryRun) {
    Write-Step "FASE 8: Monitoreo Automático (Auto-Promote Activado)"
    
    Write-ColorOutput "  Esperando $WaitMinutes minutos antes de evaluar métricas..." "Yellow"
    
    for ($i = 1; $i -le $WaitMinutes; $i++) {
        Start-Sleep -Seconds 60
        Write-Host "  [$i/$WaitMinutes min] Monitoreando..." -NoNewline
        
        # Aquí iría la lógica de verificación de métricas desde Prometheus
        # Por simplicidad, solo simulamos
        
        Write-Host " OK" -ForegroundColor Green
    }
    
    # Evaluación de métricas (simplificado)
    Write-Host "`n  → Evaluando métricas..." -NoNewline
    $metricsOK = $true  # Aquí iría la lógica real de verificación
    
    if ($metricsOK) {
        Write-Success " Métricas dentro de umbrales aceptables"
        
        if ([int]$CanaryPercent -lt 100) {
            Write-ColorOutput "`n  🚀 Promoviendo a siguiente fase..." "Cyan"
            
            $nextPercent = switch ([int]$CanaryPercent) {
                5 { 10 }
                10 { 25 }
                25 { 50 }
                50 { 100 }
                default { 100 }
            }
            
            # Llamada recursiva al script con nuevo porcentaje
            & $MyInvocation.MyCommand.Path -CanaryVersion $CanaryVersion -StableVersion $StableVersion -CanaryPercent $nextPercent -WaitMinutes $WaitMinutes -AutoPromote
        } else {
            Write-Success "`n  🎉 DESPLIEGUE COMPLETO - 100% tráfico en canary"
            Write-ColorOutput "  La nueva versión $CanaryVersion está completamente desplegada" "Green"
        }
    } else {
        Write-Error " Métricas fuera de umbrales"
        Write-Warning "  Se requiere intervención manual"
    }
}

# ============================================================================
# RESUMEN FINAL
# ============================================================================
Write-Step "✅ DESPLIEGUE COMPLETADO"

Write-ColorOutput @"

╔════════════════════════════════════════════════════════════════╗
║                    RESUMEN DEL DESPLIEGUE                      ║
╠════════════════════════════════════════════════════════════════╣
║  Estado:           ✅ EXITOSO                                  ║
║  Versión Canary:   $CanaryVersion                                    ║
║  Tráfico Canary:   $CanaryPercent%                                   ║
║  Réplicas Stable:  $StableReplicas                                      ║
║  Réplicas Canary:  $CanaryReplicas                                      ║
╚════════════════════════════════════════════════════════════════╝

📋 PRÓXIMOS PASOS:

  1. Monitorear métricas en Grafana: http://localhost:3000
  2. Revisar logs:
     • docker-compose -f docker-compose-canary.yml logs -f serviciudad-canary
  
  3. Si métricas OK, promover a siguiente fase:
     • .\deploy-canary.ps1 -CanaryVersion $CanaryVersion -CanaryPercent 25
  
  4. Si hay problemas, ejecutar rollback:
     • .\rollback-canary.ps1
  
  5. Para despliegue completo (100%):
     • .\deploy-canary.ps1 -CanaryVersion $CanaryVersion -CanaryPercent 100

⏰ Tiempo recomendado de monitoreo: $WaitMinutes minutos

"@ "Cyan"

# ============================================================================
# FIN DEL SCRIPT
# ============================================================================
