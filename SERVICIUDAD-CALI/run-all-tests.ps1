# run-all-tests.ps1
# Script maestro para ejecutar todas las pruebas del proyecto ServiCiudad
# Autor: Equipo ServiCiudad - Universidad Autónoma de Occidente
# Fecha: Octubre 17, 2025

param(
    [switch]$SkipIntegration,  # Omitir tests E2E con Docker/Testcontainers
    [switch]$Coverage,          # Generar reporte JaCoCo de cobertura
    [switch]$OpenReport,        # Abrir reporte HTML en navegador
    [switch]$Verbose            # Mostrar output detallado de Maven
)

$ErrorActionPreference = "Continue"

# Banner
Write-Host ""
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host " SERVICIUDAD-CALI - Suite de Pruebas Exhaustivas" -ForegroundColor Cyan
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host ""

# 1. Configurar entorno
Write-Host "PASO 1: Configuracion del entorno" -ForegroundColor Yellow
Write-Host ("─" * 60)

if (Test-Path "setup-tests.ps1") {
    & .\setup-tests.ps1
    if ($LASTEXITCODE -ne 0) {
        Write-Host "`n❌ Configuración falló. Abortando..." -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "⚠️  setup-tests.ps1 no encontrado, continuando sin validación..." -ForegroundColor Yellow
}

Write-Host ""

# 2. Definir grupos de tests
$testGroups = @(
    @{ 
        Name = "1. Mappers (Unit Tests)"; 
        Pattern = "*MapperTest"; 
        Time = 15;
        Description = "Transformaciones Domain - DTO - JPA"
    },
    @{ 
        Name = "2. Use Cases (Business Logic)"; 
        Pattern = "*UseCaseImplTest"; 
        Time = 30;
        Description = "Logica de negocio y orquestacion"
    },
    @{ 
        Name = "3. Adapters (Ports Implementation)"; 
        Pattern = "*AdapterTest"; 
        Time = 20;
        Description = "Implementacion de puertos (Repository, Reader)"
    },
    @{ 
        Name = "4. REST Controllers (API Layer)"; 
        Pattern = "*RestControllerTest"; 
        Time = 40;
        Description = "Endpoints HTTP, validaciones, error handling"
    }
)

if (-not $SkipIntegration) {
    $testGroups += @{ 
        Name = "5. Integration E2E (Testcontainers)"; 
        Pattern = "*IntegrationTest"; 
        Time = 90;
        Description = "Tests completos con PostgreSQL real en Docker"
    }
} else {
    Write-Host "Tests de integracion E2E omitidos (flag -SkipIntegration)" -ForegroundColor Yellow
    Write-Host ""
}

# 3. Ejecutar tests por grupos
Write-Host "PASO 2: Ejecutando tests por categorias" -ForegroundColor Yellow
Write-Host ("─" * 60)
Write-Host ""

$totalGroups = $testGroups.Count
$passedGroups = 0
$failedGroups = 0
$totalTime = 0
$results = @()

foreach ($group in $testGroups) {
    Write-Host "🔹 $($group.Name)" -ForegroundColor Cyan
    Write-Host "   $($group.Description)" -ForegroundColor Gray
    Write-Host "   Tiempo estimado: $($group.Time)s" -ForegroundColor DarkGray
    Write-Host ""
    
    $startTime = Get-Date
    
    # Construir comando Maven
    $mvnArgs = @("test", "-Dtest=$($group.Pattern)")
    
    if ($Coverage) {
        $mvnArgs += "jacoco:report"
    }
    
    if (-not $Verbose) {
        $mvnArgs += "-q"  # Quiet mode
    }
    
    # Ejecutar Maven
    try {
        if ($Verbose) {
            & mvn $mvnArgs
        } else {
            & mvn $mvnArgs 2>&1 | Out-Null
        }
        
        $exitCode = $LASTEXITCODE
        $elapsed = (Get-Date) - $startTime
        $elapsedSeconds = [math]::Round($elapsed.TotalSeconds, 1)
        $totalTime += $elapsedSeconds
        
        if ($exitCode -eq 0) {
            Write-Host "   ✅ PASSED ($elapsedSeconds s)" -ForegroundColor Green
            $passedGroups++
            $results += [PSCustomObject]@{
                Group = $group.Name
                Status = "✅ PASSED"
                Time = "$elapsedSeconds s"
            }
        } else {
            Write-Host "   ❌ FAILED (exit code: $exitCode)" -ForegroundColor Red
            $failedGroups++
            $results += [PSCustomObject]@{
                Group = $group.Name
                Status = "❌ FAILED"
                Time = "$elapsedSeconds s"
            }
        }
    } catch {
        Write-Host "   ❌ ERROR: $($_.Exception.Message)" -ForegroundColor Red
        $failedGroups++
        $results += [PSCustomObject]@{
            Group = $group.Name
            Status = "❌ ERROR"
            Time = "N/A"
        }
    }
    
    Write-Host ""
}

# 4. Generar reporte consolidado
Write-Host ""
Write-Host "╔════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║  📊 RESUMEN DE PRUEBAS                                    ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# Tabla de resultados
Write-Host "📋 Resultados por Categoría:" -ForegroundColor Yellow
Write-Host ""
$results | Format-Table -AutoSize

# Estadísticas
Write-Host ""
Write-Host "📈 Estadísticas Globales:" -ForegroundColor Yellow
Write-Host "   Total grupos ejecutados: $totalGroups"
Write-Host "   ✅ Passed: $passedGroups" -ForegroundColor Green
Write-Host "   ❌ Failed: $failedGroups" -ForegroundColor $(if ($failedGroups -gt 0) { "Red" } else { "Gray" })
Write-Host "   ⏱️  Tiempo total: $([math]::Round($totalTime, 1)) segundos" -ForegroundColor Cyan

# Porcentaje de éxito
$successRate = [math]::Round(($passedGroups / $totalGroups) * 100, 1)
$color = if ($successRate -eq 100) { "Green" } elseif ($successRate -ge 80) { "Yellow" } else { "Red" }
Write-Host "   🎯 Tasa de éxito: $successRate%" -ForegroundColor $color

# 5. Reporte de cobertura JaCoCo
if ($Coverage) {
    Write-Host ""
    Write-Host "📊 PASO 3: Reporte de Cobertura JaCoCo" -ForegroundColor Yellow
    Write-Host ("─" * 60)
    
    $reportPath = "target\site\jacoco\index.html"
    
    if (Test-Path $reportPath) {
        Write-Host "✅ Reporte generado: $reportPath" -ForegroundColor Green
        
        # Intentar extraer métricas del reporte XML
        $xmlReport = "target\site\jacoco\jacoco.xml"
        if (Test-Path $xmlReport) {
            try {
                [xml]$jacoco = Get-Content $xmlReport
                $counters = $jacoco.report.counter
                
                foreach ($counter in $counters) {
                    if ($counter.type -eq "LINE") {
                        $covered = [int]$counter.covered
                        $missed = [int]$counter.missed
                        $total = $covered + $missed
                        $percentage = [math]::Round(($covered / $total) * 100, 1)
                        
                        $color = if ($percentage -ge 80) { "Green" } elseif ($percentage -ge 70) { "Yellow" } else { "Red" }
                        Write-Host "   📏 Cobertura de líneas: $percentage% ($covered/$total)" -ForegroundColor $color
                    }
                    
                    if ($counter.type -eq "BRANCH") {
                        $covered = [int]$counter.covered
                        $missed = [int]$counter.missed
                        $total = $covered + $missed
                        $percentage = [math]::Round(($covered / $total) * 100, 1)
                        
                        $color = if ($percentage -ge 70) { "Green" } elseif ($percentage -ge 60) { "Yellow" } else { "Red" }
                        Write-Host "   🌿 Cobertura de branches: $percentage% ($covered/$total)" -ForegroundColor $color
                    }
                }
            } catch {
                Write-Host "⚠️  No se pudo parsear jacoco.xml" -ForegroundColor Yellow
            }
        }
        
        if ($OpenReport) {
            Write-Host ""
            Write-Host "🌐 Abriendo reporte en navegador..." -ForegroundColor Cyan
            Start-Process $reportPath
        } else {
            Write-Host ""
            Write-Host "💡 Para ver el reporte, ejecuta:" -ForegroundColor Cyan
            Write-Host "   Start-Process '$reportPath'" -ForegroundColor Gray
        }
    } else {
        Write-Host "❌ Reporte no generado en $reportPath" -ForegroundColor Red
        Write-Host "   Verifica que el plugin JaCoCo esté configurado en pom.xml" -ForegroundColor Yellow
    }
}

# 6. Recomendaciones finales
Write-Host ""
Write-Host "💡 Próximos pasos recomendados:" -ForegroundColor Yellow

if ($failedGroups -gt 0) {
    Write-Host "   1. Revisar logs de tests fallidos:" -ForegroundColor White
    Write-Host "      mvn test -Dtest=<PatternFallido>" -ForegroundColor Gray
    Write-Host "   2. Ejecutar en modo verbose para más detalles:" -ForegroundColor White
    Write-Host "      .\run-all-tests.ps1 -Verbose" -ForegroundColor Gray
} else {
    Write-Host "   ✅ Todos los tests pasaron exitosamente!" -ForegroundColor Green
    
    if (-not $Coverage) {
        Write-Host "   1. Generar reporte de cobertura:" -ForegroundColor White
        Write-Host "      .\run-all-tests.ps1 -Coverage -OpenReport" -ForegroundColor Gray
    }
    
    if ($SkipIntegration) {
        Write-Host "   2. Ejecutar tests E2E completos:" -ForegroundColor White
        Write-Host "      .\run-all-tests.ps1 -Coverage" -ForegroundColor Gray
    }
    
    Write-Host "   3. Revisar guía completa de pruebas:" -ForegroundColor White
    Write-Host "      code GUIA_PRUEBAS_EXHAUSTIVAS.md" -ForegroundColor Gray
}

Write-Host ""
Write-Host "📚 Documentación adicional:" -ForegroundColor Cyan
Write-Host "   - GUIA_PRUEBAS_EXHAUSTIVAS.md - Guía completa de testing" -ForegroundColor Gray
Write-Host "   - docs/revision_academica/03_CALIDAD_TESTING.md - Análisis académico" -ForegroundColor Gray
Write-Host "   - README.md - Documentación general del proyecto" -ForegroundColor Gray

# 7. Exit code
Write-Host ""
if ($failedGroups -eq 0) {
    Write-Host "════════════════════════════════════════════════════════════" -ForegroundColor Green
    Write-Host "✅ SUITE DE PRUEBAS COMPLETADA EXITOSAMENTE" -ForegroundColor Green
    Write-Host "════════════════════════════════════════════════════════════" -ForegroundColor Green
    exit 0
} else {
    Write-Host "════════════════════════════════════════════════════════════" -ForegroundColor Red
    Write-Host "❌ SUITE DE PRUEBAS COMPLETADA CON ERRORES" -ForegroundColor Red
    Write-Host "════════════════════════════════════════════════════════════" -ForegroundColor Red
    exit $failedGroups
}
