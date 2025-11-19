# ============================================================================
# SERVICIUDAD CALI - CANARY ROLLBACK SCRIPT
# ============================================================================
# Descripción: Script para revertir despliegue canario en caso de problemas
# Autor: ServiCiudad DevOps Team
# Fecha: 2025-11-19
# ============================================================================

param(
    [Parameter(Mandatory=$false, HelpMessage="Confirmar rollback sin preguntar")]
    [switch]$Force
)

$ErrorActionPreference = "Stop"
$ScriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path

# Colores
function Write-ColorOutput {
    param([string]$Message, [string]$Color = "White")
    Write-Host $Message -ForegroundColor $Color
}

Clear-Host
Write-ColorOutput @"
╔════════════════════════════════════════════════════════════════╗
║                                                                ║
║         🔄 SERVICIUDAD CALI - CANARY ROLLBACK 🔄             ║
║                                                                ║
║  ATENCIÓN: Esta acción revertirá el despliegue canario       ║
║            y dirigirá 100% del tráfico a la versión estable   ║
║                                                                ║
╚════════════════════════════════════════════════════════════════╝
"@ "Yellow"

# Confirmar acción
if (-not $Force) {
    Write-Host "`n¿Desea continuar con el rollback? (S/N): " -NoNewline -ForegroundColor Yellow
    $confirm = Read-Host
    
    if ($confirm -ne "S" -and $confirm -ne "s") {
        Write-ColorOutput "`n❌ Rollback cancelado" "Red"
        exit 0
    }
}

Write-ColorOutput "`n▶ Ejecutando rollback..." "Cyan"

# Detener servicios canary
Write-Host "  → Deteniendo servicios canary..." -NoNewline
Set-Location (Join-Path $ScriptPath "deployment\canary")
docker-compose -f docker-compose-canary.yml stop serviciudad-canary 2>&1 | Out-Null
Write-ColorOutput " OK" "Green"

# Escalar stable a 100%
Write-Host "  → Escalando stable a 100%..." -NoNewline
docker-compose -f docker-compose-canary.yml up -d --scale serviciudad-stable=10 --scale serviciudad-canary=0 2>&1 | Out-Null
Write-ColorOutput " OK" "Green"

# Actualizar Nginx
Write-Host "  → Actualizando configuración Nginx..." -NoNewline
$nginxConfig = Join-Path $ScriptPath "deployment\canary\nginx\nginx.conf"
$nginxContent = Get-Content $nginxConfig -Raw
$nginxContent = $nginxContent -replace 'split_clients.*\{[\s\S]*?\}', @"
split_clients "`${remote_addr}`${http_user_agent}`${date_gmt}" `$backend_pool {
        0%      canary;   # 0% tráfico a canary
        *       stable;   # 100% tráfico a stable
    }
"@
Set-Content -Path $nginxConfig -Value $nginxContent -NoNewline
docker-compose -f docker-compose-canary.yml exec nginx nginx -s reload 2>&1 | Out-Null
Write-ColorOutput " OK" "Green"

# Verificar health
Write-Host "  → Verificando servicios..." -NoNewline
Start-Sleep -Seconds 5
try {
    $health = Invoke-RestMethod -Uri "http://localhost/actuator/health" -Method GET -TimeoutSec 5
    if ($health.status -eq "UP") {
        Write-ColorOutput " OK" "Green"
    } else {
        Write-ColorOutput " WARNING" "Yellow"
    }
} catch {
    Write-ColorOutput " ERROR" "Red"
}

Write-ColorOutput @"

╔════════════════════════════════════════════════════════════════╗
║                   ✅ ROLLBACK COMPLETADO ✅                   ║
╠════════════════════════════════════════════════════════════════╣
║  Estado:           Versión estable activa                      ║
║  Tráfico:          100% → Stable                              ║
║  Canary:           Detenido                                   ║
╚════════════════════════════════════════════════════════════════╝

📋 ACCIONES POST-ROLLBACK:

  1. Revisar logs para identificar causa:
     • docker-compose -f docker-compose-canary.yml logs serviciudad-canary
  
  2. Analizar métricas en Grafana: http://localhost:3000
  
  3. Investigar issues reportados
  
  4. Corregir problemas antes de reintentar despliegue

"@ "Cyan"
