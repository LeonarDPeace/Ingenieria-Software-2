# setup-tests.ps1
# Script de configuración automática del entorno de pruebas
# ServiCiudad Cali - Sistema de Consulta Unificada

Write-Host "🔧 Configurando entorno de pruebas ServiCiudad..." -ForegroundColor Cyan
Write-Host ("=" * 60)

# 1. Configurar JDK
Write-Host "`n🔍 Detectando JDK..." -ForegroundColor Yellow

$possibleJdkPaths = @(
    "C:\Program Files\Eclipse Adoptium\jdk-21.0.8.9-hotspot",
    "C:\Program Files\Eclipse Adoptium\jdk-21*",
    "C:\Program Files\Java\jdk-17*",
    "C:\Program Files\Java\jdk-21*",
    "C:\Program Files\OpenJDK\*"
)

$JDK_PATH = $null
foreach ($path in $possibleJdkPaths) {
    $resolved = Get-Item $path -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($resolved) {
        $JDK_PATH = $resolved.FullName
        break
    }
}

if ($JDK_PATH) {
    $env:JAVA_HOME = $JDK_PATH
    $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
    Write-Host "✅ JDK configurado: $JDK_PATH" -ForegroundColor Green
    
    # Verificar versión
    $javaVersion = java -version 2>&1 | Select-String "version"
    Write-Host "   $javaVersion" -ForegroundColor Gray
} else {
    Write-Host "❌ JDK no encontrado en ubicaciones conocidas" -ForegroundColor Red
    Write-Host "   Instala JDK 17+ desde: https://adoptium.net/" -ForegroundColor Yellow
    Write-Host "   O configura manualmente:" -ForegroundColor Yellow
    Write-Host '   $env:JAVA_HOME = "C:\ruta\a\tu\jdk"' -ForegroundColor Gray
    exit 1
}

# 2. Verificar Maven
Write-Host "`n🔍 Verificando Maven..." -ForegroundColor Yellow

try {
    $mvnVersion = mvn -version 2>&1 | Out-String
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Maven detectado" -ForegroundColor Green
        $mvnVersionLine = $mvnVersion -split "`n" | Select-Object -First 1
        Write-Host "   $mvnVersionLine" -ForegroundColor Gray
        
        # Verificar que Maven use el JDK correcto
        if ($mvnVersion -match "Java home: (.+)") {
            $mavenJavaHome = $matches[1].Trim()
            Write-Host "   Java home usado por Maven: $mavenJavaHome" -ForegroundColor Gray
        }
    } else {
        throw "Maven no responde"
    }
} catch {
    Write-Host "❌ Maven no encontrado o no funciona correctamente" -ForegroundColor Red
    Write-Host "   Instala Maven desde: https://maven.apache.org/download.cgi" -ForegroundColor Yellow
    Write-Host "   O usa mvnw (Maven Wrapper) si está disponible" -ForegroundColor Yellow
    exit 1
}

# 3. Verificar Docker (opcional para tests E2E)
Write-Host "`n🔍 Verificando Docker..." -ForegroundColor Yellow

try {
    $dockerVersion = docker --version 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Docker detectado: $dockerVersion" -ForegroundColor Green
        
        # Verificar que Docker está corriendo
        $dockerPs = docker ps 2>&1
        if ($LASTEXITCODE -eq 0) {
            Write-Host "   Docker daemon está corriendo ✓" -ForegroundColor Gray
        } else {
            Write-Host "⚠️  Docker instalado pero daemon no responde" -ForegroundColor Yellow
            Write-Host "   Inicia Docker Desktop para ejecutar tests E2E" -ForegroundColor Yellow
        }
    } else {
        throw "Docker no responde"
    }
} catch {
    Write-Host "⚠️  Docker no detectado" -ForegroundColor Yellow
    Write-Host "   Tests E2E (Testcontainers) se omitirán" -ForegroundColor Yellow
    Write-Host "   Instala Docker Desktop desde: https://www.docker.com/products/docker-desktop" -ForegroundColor Gray
}

# 4. Verificar que estamos en el directorio correcto
Write-Host "`n🔍 Verificando estructura del proyecto..." -ForegroundColor Yellow

if (Test-Path "pom.xml") {
    Write-Host "✅ pom.xml encontrado" -ForegroundColor Green
    
    # Leer información del proyecto
    [xml]$pom = Get-Content "pom.xml"
    $artifactId = $pom.project.artifactId
    $version = $pom.project.version
    Write-Host "   Proyecto: $artifactId v$version" -ForegroundColor Gray
} else {
    Write-Host "❌ pom.xml no encontrado" -ForegroundColor Red
    Write-Host "   Asegúrate de ejecutar este script desde el directorio raíz del proyecto" -ForegroundColor Yellow
    Write-Host "   cd 'ruta\a\SERVICIUDAD-CALI'" -ForegroundColor Gray
    exit 1
}

if (Test-Path "src/test/java") {
    $testFiles = Get-ChildItem -Path "src/test/java" -Filter "*Test.java" -Recurse
    Write-Host "✅ Directorio de tests encontrado ($($testFiles.Count) archivos)" -ForegroundColor Green
} else {
    Write-Host "⚠️  Directorio src/test/java no encontrado" -ForegroundColor Yellow
}

# 5. Limpiar compilaciones previas
Write-Host "`n🧹 Limpiando compilaciones previas..." -ForegroundColor Yellow

if (Test-Path "target") {
    try {
        mvn clean -q 2>&1 | Out-Null
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ Directorio target/ limpiado" -ForegroundColor Green
        } else {
            Write-Host "⚠️  mvn clean falló, intentando borrado manual..." -ForegroundColor Yellow
            Remove-Item -Path "target" -Recurse -Force -ErrorAction SilentlyContinue
            Write-Host "   Directorio target/ eliminado manualmente" -ForegroundColor Gray
        }
    } catch {
        Write-Host "⚠️  No se pudo limpiar target/" -ForegroundColor Yellow
    }
} else {
    Write-Host "   Directorio target/ no existe (primera ejecución)" -ForegroundColor Gray
}

# 6. Resumen final
Write-Host "`n" + ("=" * 60)
Write-Host "✅ ENTORNO CONFIGURADO CORRECTAMENTE" -ForegroundColor Green
Write-Host ("=" * 60)

Write-Host "`n📋 Próximos pasos:" -ForegroundColor Cyan
Write-Host "   1. Ejecutar todos los tests:" -ForegroundColor White
Write-Host "      .\run-all-tests.ps1" -ForegroundColor Gray
Write-Host "`n   2. Ejecutar solo tests unitarios (sin Docker):" -ForegroundColor White
Write-Host "      .\run-all-tests.ps1 -SkipIntegration" -ForegroundColor Gray
Write-Host "`n   3. Ejecutar con reporte de cobertura:" -ForegroundColor White
Write-Host "      .\run-all-tests.ps1 -Coverage -OpenReport" -ForegroundColor Gray
Write-Host "`n   4. Ejecutar tests específicos:" -ForegroundColor White
Write-Host '      mvn test -Dtest="*MapperTest"' -ForegroundColor Gray

Write-Host "`n📚 Documentación completa:" -ForegroundColor Cyan
Write-Host "   GUIA_PRUEBAS_EXHAUSTIVAS.md" -ForegroundColor Gray

Write-Host ""
exit 0
