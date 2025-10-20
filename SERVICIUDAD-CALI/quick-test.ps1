# quick-test.ps1
# Script para ejecutar tests rápidos durante desarrollo
# Útil para validación rápida de cambios sin ejecutar suite completa

param(
    [Parameter(Position=0)]
    [string]$TestPattern = "",  # Patrón de test a ejecutar (ej: "*MapperTest")
    
    [switch]$Watch,             # Modo watch (re-ejecutar al detectar cambios)
    [switch]$FailFast           # Detener al primer error
)

function Run-Tests {
    param([string]$Pattern)
    
    Clear-Host
    Write-Host "⚡ Quick Test Runner" -ForegroundColor Cyan
    Write-Host ("─" * 60)
    Write-Host "Patrón: $Pattern" -ForegroundColor Yellow
    Write-Host "Hora: $(Get-Date -Format 'HH:mm:ss')" -ForegroundColor Gray
    Write-Host ""
    
    $startTime = Get-Date
    
    if ($Pattern) {
        mvn test -Dtest="$Pattern" -DfailIfNoTests=false
    } else {
        mvn test
    }
    
    $elapsed = (Get-Date) - $startTime
    $exitCode = $LASTEXITCODE
    
    Write-Host ""
    Write-Host ("─" * 60)
    if ($exitCode -eq 0) {
        Write-Host "✅ Tests completados en $([math]::Round($elapsed.TotalSeconds, 1))s" -ForegroundColor Green
    } else {
        Write-Host "❌ Tests fallaron (exit code: $exitCode)" -ForegroundColor Red
        
        if ($FailFast) {
            exit $exitCode
        }
    }
    Write-Host ""
    
    return $exitCode
}

# Ejecución inicial
$exitCode = Run-Tests -Pattern $TestPattern

# Modo watch
if ($Watch) {
    Write-Host "👁️  Modo watch activado - Presiona Ctrl+C para salir" -ForegroundColor Cyan
    Write-Host "   Monitoreando: src/main/java, src/test/java" -ForegroundColor Gray
    Write-Host ""
    
    $watcher = New-Object System.IO.FileSystemWatcher
    $watcher.Path = (Get-Location).Path
    $watcher.IncludeSubdirectories = $true
    $watcher.Filter = "*.java"
    $watcher.NotifyFilter = [System.IO.NotifyFilters]::LastWrite
    
    $action = {
        $path = $Event.SourceEventArgs.FullPath
        $changeType = $Event.SourceEventArgs.ChangeType
        Write-Host "🔄 Detectado cambio: $changeType - $path" -ForegroundColor Yellow
        Start-Sleep -Milliseconds 500  # Debounce
        Run-Tests -Pattern $TestPattern
    }
    
    Register-ObjectEvent -InputObject $watcher -EventName Changed -Action $action
    
    try {
        while ($true) {
            Start-Sleep -Seconds 1
        }
    } finally {
        $watcher.Dispose()
    }
}

exit $exitCode
