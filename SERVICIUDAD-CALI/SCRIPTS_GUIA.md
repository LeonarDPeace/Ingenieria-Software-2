# 🚀 Guía de Scripts PowerShell - ServiCiudad Cali

> **Actualizado:** Noviembre 19, 2025  
> **Versión:** 2.0  
> **Scripts totales:** 7 (4 principales + 3 canary)

---

## 📋 Índice

1. [Scripts Principales](#scripts-principales)
2. [Scripts Canary Deployment](#scripts-canary-deployment)
3. [Guía de Uso Rápida](#guía-de-uso-rápida)
4. [Troubleshooting](#troubleshooting)

---

## 🟢 Scripts Principales

### 1. `inicio-rapido.ps1` ⭐

**Propósito:** Iniciar el proyecto completo (Docker + Frontend) en un solo comando.

**Características:**
- ✅ Verifica Docker Desktop
- ✅ Levanta contenedores (app + PostgreSQL)
- ✅ Espera a que la API esté lista (health check)
- ✅ Inicia servidor HTTP para el frontend
- ✅ Abre navegador automáticamente

**Uso:**
```powershell
.\inicio-rapido.ps1
```

**Qué hace:**
1. Verifica que Docker esté instalado y corriendo
2. Navega al directorio del proyecto
3. Ejecuta `docker-compose up -d`
4. Espera hasta 30s a que `/actuator/health` responda
5. Inicia servidor Python HTTP en puerto 5500
6. Abre `http://localhost:5500/index.html` en el navegador

**Salida esperada:**
```
═══════════════════════════════════════════════════════════
  ServiCiudad Cali - Inicio Rápido v2.0
═══════════════════════════════════════════════════════════

[1/5] 🐳 Verificando Docker Desktop...
   ✅ Docker encontrado: Docker version 24.0.6

[2/5] 📂 Navegando al directorio del proyecto...
   ✅ Ubicación: D:\...\SERVICIUDAD-CALI

[3/5] 🐳 Levantando contenedores Docker...
   ✅ Contenedores levantados

[4/5] ⏳ Esperando a que la API esté lista...
   ✅ API respondiendo correctamente

[5/5] 🌐 Iniciando servidor HTTP y abriendo frontend...
   ✅ Servidor HTTP iniciado
   ✅ Frontend disponible en: http://localhost:5500/index.html

═══════════════════════════════════════════════════════════
 ✅ INICIADO EXITOSAMENTE
═══════════════════════════════════════════════════════════
```

**Comandos relacionados:**
```powershell
# Ver logs de la aplicación
docker-compose logs -f app

# Detener contenedores
docker-compose down

# Detener servidor HTTP
Get-Job | Stop-Job
```

---

### 2. `quick-test.ps1`

**Propósito:** Ejecutar tests rápidos durante desarrollo.

**Características:**
- ✅ Ejecuta tests específicos por patrón
- ✅ Modo watch (re-ejecuta al detectar cambios)
- ✅ Fail-fast (detiene al primer error)
- ✅ Output limpio y cronometrado

**Uso básico:**
```powershell
# Ejecutar todos los tests
.\quick-test.ps1

# Ejecutar tests específicos
.\quick-test.ps1 "*MapperTest"

# Modo watch (re-ejecuta automáticamente)
.\quick-test.ps1 -Watch

# Fail-fast (detiene al primer error)
.\quick-test.ps1 -FailFast
```

**Ejemplos:**
```powershell
# Tests de mappers
.\quick-test.ps1 "*MapperTest"

# Tests de use cases
.\quick-test.ps1 "*UseCaseImplTest"

# Tests de controllers
.\quick-test.ps1 "*RestControllerTest"

# Modo watch + fail-fast
.\quick-test.ps1 "*MapperTest" -Watch -FailFast
```

**Salida esperada:**
```
⚡ Quick Test Runner
────────────────────────────────────────────────────────────
Patrón: *MapperTest
Hora: 14:23:45

[INFO] Running tests...
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0

────────────────────────────────────────────────────────────
✅ Tests completados en 3.2s
```

---

### 3. `rebuild-docker.ps1`

**Propósito:** Reconstruir completamente la imagen Docker (limpieza total).

**Características:**
- ✅ Detiene todos los contenedores
- ✅ Elimina imágenes antiguas
- ✅ Construye imagen sin caché
- ✅ Inicia contenedores frescos
- ✅ Verifica que la aplicación esté lista

**Uso:**
```powershell
.\rebuild-docker.ps1
```

**Cuándo usar:**
- Después de cambios en `Dockerfile`
- Después de cambios en dependencias Maven
- Cuando hay problemas de caché
- Para un inicio completamente limpio

**Qué hace:**
1. Ejecuta `docker-compose down -v` (elimina volúmenes)
2. Elimina imágenes antiguas del proyecto
3. Ejecuta `docker-compose build --no-cache`
4. Ejecuta `docker-compose up -d`
5. Espera a que `/actuator/health` responda

**Tiempo estimado:** 2-3 minutos

**Salida esperada:**
```
Reconstrucción completa del contenedor Docker
===============================================

1. Deteniendo contenedores...
   ✅ Contenedores detenidos correctamente

2. Limpiando imágenes antiguas...
   ✅ Imágenes antiguas eliminadas

3. Construyendo nueva imagen...
   (Esto puede tardar 2-3 minutos)
   ✅ Imagen construida exitosamente

4. Iniciando contenedores...
   ✅ Contenedores iniciados correctamente

5. Esperando a que la aplicación esté lista...
   ✅ Aplicación lista y respondiendo

===============================================
✅ Reconstrucción completada exitosamente
===============================================
```

---

### 4. `run-all-tests.ps1` ⭐

**Propósito:** Ejecutar suite completa de tests (87% coverage).

**Características:**
- ✅ Tests organizados por categorías
- ✅ Reporte de cobertura JaCoCo
- ✅ Estadísticas detalladas
- ✅ Modo verbose opcional
- ✅ Skip tests de integración opcionales

**Uso básico:**
```powershell
# Ejecutar todos los tests
.\run-all-tests.ps1

# Con reporte de cobertura
.\run-all-tests.ps1 -Coverage

# Abrir reporte HTML automáticamente
.\run-all-tests.ps1 -Coverage -OpenReport

# Omitir tests de integración (más rápido)
.\run-all-tests.ps1 -SkipIntegration

# Modo verbose (más detalles)
.\run-all-tests.ps1 -Verbose
```

**Grupos de tests ejecutados:**
1. **Mappers (Unit Tests)** - 15s
   - Transformaciones Domain ↔ DTO ↔ JPA

2. **Use Cases (Business Logic)** - 30s
   - Lógica de negocio y orquestación

3. **Adapters (Ports Implementation)** - 20s
   - Implementación de puertos (Repository, Reader)

4. **REST Controllers (API Layer)** - 40s
   - Endpoints HTTP, validaciones, error handling

5. **Integration E2E (Testcontainers)** - 90s
   - Tests completos con PostgreSQL real en Docker

**Salida esperada:**
```
================================================================
 SERVICIUDAD-CALI - Suite de Pruebas Exhaustivas
================================================================

PASO 1: Configuración del entorno
────────────────────────────────────────────────────────────

PASO 2: Ejecutando tests por categorías
────────────────────────────────────────────────────────────

🔹 1. Mappers (Unit Tests)
   Transformaciones Domain - DTO - JPA
   Tiempo estimado: 15s

   ✅ PASSED (14.3 s)

🔹 2. Use Cases (Business Logic)
   Lógica de negocio y orquestación
   Tiempo estimado: 30s

   ✅ PASSED (28.7 s)

... (continúa con los demás grupos)

╔════════════════════════════════════════════════════════════╗
║  📊 RESUMEN DE PRUEBAS                                    ║
╚════════════════════════════════════════════════════════════╝

📋 Resultados por Categoría:

Group                         Status      Time
-----                         ------      ----
1. Mappers (Unit Tests)       ✅ PASSED   14.3 s
2. Use Cases (Business Logic) ✅ PASSED   28.7 s
3. Adapters (Ports)           ✅ PASSED   19.2 s
4. REST Controllers           ✅ PASSED   38.9 s
5. Integration E2E            ✅ PASSED   87.4 s

📈 Estadísticas Globales:
   Total grupos ejecutados: 5
   ✅ Passed: 5
   ❌ Failed: 0
   ⏱️  Tiempo total: 188.5 segundos
   🎯 Tasa de éxito: 100%

📊 PASO 3: Reporte de Cobertura JaCoCo
────────────────────────────────────────────────────────────
✅ Reporte generado: target\site\jacoco\index.html
   📏 Cobertura de líneas: 87% (1234/1420)
   🌿 Cobertura de branches: 82% (456/556)

════════════════════════════════════════════════════════════
✅ SUITE DE PRUEBAS COMPLETADA EXITOSAMENTE
════════════════════════════════════════════════════════════
```

**Reporte de cobertura:**
- Ubicación: `target/site/jacoco/index.html`
- Cobertura actual: **87%**
- Objetivo: ≥ 80%

---

## 🐤 Scripts Canary Deployment

### 5. `deployment/canary/deploy-canary.ps1` ⭐

**Propósito:** Despliegue canario progresivo (Blue-Green con split traffic).

**Características:**
- ✅ Despliegue gradual (5% → 10% → 25% → 50% → 100%)
- ✅ Validaciones pre-vuelo
- ✅ Health checks automáticos
- ✅ Smoke tests integrados
- ✅ Auto-promoción opcional
- ✅ Modo dry-run

**Uso:**
```powershell
# Despliegue canario básico (10% tráfico)
.\deployment\canary\deploy-canary.ps1 -CanaryVersion "2.0.0"

# Despliegue con porcentaje personalizado
.\deployment\canary\deploy-canary.ps1 -CanaryVersion "2.0.0" -CanaryPercent 25

# Con auto-promoción (si métricas OK)
.\deployment\canary\deploy-canary.ps1 -CanaryVersion "2.0.0" -AutoPromote

# Modo dry-run (no ejecuta cambios)
.\deployment\canary\deploy-canary.ps1 -CanaryVersion "2.0.0" -DryRun

# Despliegue completo con espera personalizada
.\deployment\canary\deploy-canary.ps1 `
    -CanaryVersion "2.0.0" `
    -CanaryPercent 10 `
    -WaitMinutes 60 `
    -AutoPromote
```

**Parámetros:**
- `-CanaryVersion` (obligatorio): Nueva versión a desplegar (ej: "2.0.0")
- `-StableVersion` (opcional): Versión estable actual (default: "1.0.0")
- `-CanaryPercent` (opcional): % de tráfico para canary (5, 10, 25, 50, 100)
- `-WaitMinutes` (opcional): Tiempo de espera entre fases (default: 30 min)
- `-AutoPromote` (switch): Auto-promoción si métricas OK
- `-DryRun` (switch): Modo simulación (no ejecuta cambios)

**Fases del despliegue:**
1. **Validaciones Pre-Vuelo** - Verifica Docker, Docker Compose, imagen canary
2. **Cálculo de Réplicas** - Determina distribución stable/canary
3. **Configurar Load Balancer** - Actualiza nginx.conf con split traffic
4. **Desplegar Contenedores** - Escala stable y canary
5. **Health Checks** - Verifica que ambas versiones estén saludables
6. **Smoke Tests** - Ejecuta tests básicos en canary
7. **Monitoreo Activo** - Prometheus + Grafana + AlertManager

**Salida esperada:**
```
╔════════════════════════════════════════════════════════════════╗
║                                                                ║
║         🐤 SERVICIUDAD CALI - CANARY DEPLOYMENT 🐤           ║
║                                                                ║
║  Versión Estable:  1.0.0                                      ║
║  Versión Canary:   2.0.0                                      ║
║  Tráfico Canary:   10%                                        ║
║  Modo:             PRODUCCIÓN                                 ║
║                                                                ║
╚════════════════════════════════════════════════════════════════╝

▶ FASE 1: Validaciones Pre-Vuelo
  → Verificando Docker... ✅ OK (v24.0.6)
  → Verificando Docker Compose... ✅ OK (v2.23.0)
  → Verificando imagen canary... ✅ OK

▶ FASE 2: Cálculo de Réplicas
  → Total réplicas: 10
  → Stable: 9 (90%)
  → Canary: 1 (10%)

▶ FASE 3: Configurar Load Balancer
  → Actualizando nginx.conf... ✅ OK

▶ FASE 4: Desplegar Contenedores
  → Escalando stable=9, canary=1... ✅ OK

▶ FASE 5: Health Checks (60s)
  → Stable: ✅ Healthy
  → Canary: ✅ Healthy

▶ FASE 6: Smoke Tests
  → Test 1: GET /actuator/health → ✅ PASS (200 OK)
  → Test 2: GET /api → ✅ PASS (200 OK)
  → Test 3: POST /api/deuda/consultar → ✅ PASS (200 OK)

▶ FASE 7: Monitoreo Activo
  → Prometheus scraping... ✅ OK
  → Grafana dashboard... ✅ OK (http://localhost:3000)
  → AlertManager... ✅ OK

╔════════════════════════════════════════════════════════════════╗
║  ✅ DESPLIEGUE CANARIO COMPLETADO EXITOSAMENTE               ║
╚════════════════════════════════════════════════════════════════╝
```

---

### 6. `deployment/canary/quick-start.ps1`

**Propósito:** Iniciar infraestructura Canary completa (desarrollo).

**Características:**
- ✅ Construye imágenes (stable + canary)
- ✅ Inicia 7 contenedores (stable, canary, nginx, postgres, prometheus, grafana, alertmanager)
- ✅ Ejecuta smoke tests
- ✅ Muestra URLs de dashboards

**Uso:**
```powershell
cd deployment\canary
.\quick-start.ps1
```

**Qué inicia:**
1. `serviciudad-stable` (9 réplicas) - Versión 1.0.0
2. `serviciudad-canary` (1 réplica) - Versión 2.0.0
3. `nginx-lb` - Load balancer (split 90/10)
4. `postgres` - Base de datos compartida
5. `prometheus` - Métricas
6. `grafana` - Dashboard de monitoreo
7. `alertmanager` - Alertas

**Tiempo estimado:** 2-3 minutos

**URLs generadas:**
- Frontend: http://localhost:8080
- Grafana: http://localhost:3000 (admin/admin)
- Prometheus: http://localhost:9090
- AlertManager: http://localhost:9093

---

### 7. `deployment/canary/rollback-canary.ps1` 🚨

**Propósito:** Rollback de emergencia (< 30 segundos).

**Características:**
- ✅ Detiene canary inmediatamente
- ✅ Escala stable al 100%
- ✅ Reconfigura Nginx
- ✅ Verifica health del stable

**Uso:**
```powershell
cd deployment\canary
.\rollback-canary.ps1
```

**Cuándo usar:**
- Error rate > 150% de baseline
- Latency P95 > 3 segundos
- Alertas críticas activas
- Feedback negativo de usuarios

**Qué hace:**
1. Detiene todos los pods canary
2. Escala stable a 10 réplicas (100%)
3. Actualiza `nginx.conf` para 100% stable
4. Recarga Nginx sin downtime
5. Verifica health de stable

**Tiempo de ejecución:** < 30 segundos

**Salida esperada:**
```
🚨 ROLLBACK CANARIO - EMERGENCIA
═══════════════════════════════════════════════════════════

▶ PASO 1: Deteniendo Canary
  → Escalando canary=0... ✅ OK

▶ PASO 2: Escalando Stable
  → Escalando stable=10... ✅ OK

▶ PASO 3: Reconfigurando Load Balancer
  → Actualizando nginx.conf (100% stable)... ✅ OK
  → Recargando Nginx... ✅ OK

▶ PASO 4: Verificando Health
  → Stable: ✅ Healthy (10/10 réplicas)

╔════════════════════════════════════════════════════════════════╗
║  ✅ ROLLBACK COMPLETADO (23s)                                 ║
║  🔄 Sistema operando 100% en versión estable                  ║
╚════════════════════════════════════════════════════════════════╝
```

---

## 🚀 Guía de Uso Rápida

### Escenario 1: Desarrollo Local

```powershell
# 1. Iniciar proyecto
.\inicio-rapido.ps1

# 2. Hacer cambios en el código...

# 3. Ejecutar tests rápidos
.\quick-test.ps1 "*MapperTest"

# 4. Si hay problemas con Docker
.\rebuild-docker.ps1
```

### Escenario 2: Antes de Commit

```powershell
# Ejecutar suite completa con cobertura
.\run-all-tests.ps1 -Coverage -OpenReport

# Verificar que coverage >= 87%
# Verificar que todos los tests pasen
```

### Escenario 3: Despliegue Canario

```powershell
# 1. Iniciar infraestructura Canary
cd deployment\canary
.\quick-start.ps1

# 2. Desplegar nueva versión (10% tráfico)
.\deploy-canary.ps1 -CanaryVersion "2.0.0" -CanaryPercent 10

# 3. Monitorear en Grafana (http://localhost:3000)

# 4. Si todo OK, aumentar a 25%
.\deploy-canary.ps1 -CanaryVersion "2.0.0" -CanaryPercent 25

# 5. Si hay problemas, rollback
.\rollback-canary.ps1
```

### Escenario 4: CI/CD Pipeline

```yaml
# .github/workflows/ci-cd.yml ya configurado
# Solo hacer push a main para ejecutar pipeline automático
```

---

## 🔧 Troubleshooting

### Problema: Docker no está instalado

**Síntoma:**
```
❌ ERROR: Docker no está instalado o no está en el PATH
```

**Solución:**
1. Descargar Docker Desktop: https://www.docker.com/products/docker-desktop
2. Instalar y reiniciar el equipo
3. Verificar: `docker --version`

---

### Problema: Puerto 8080 ya en uso

**Síntoma:**
```
Error: bind: address already in use
```

**Solución:**
```powershell
# Ver qué proceso usa el puerto
netstat -ano | findstr :8080

# Detener contenedores existentes
docker-compose down

# O matar el proceso
Stop-Process -Id <PID>
```

---

### Problema: Tests fallan

**Síntoma:**
```
❌ Tests fallaron (exit code: 1)
```

**Solución:**
```powershell
# Ejecutar en modo verbose para ver detalles
.\run-all-tests.ps1 -Verbose

# Ejecutar un test específico
mvn test -Dtest=ClienteMapperTest

# Limpiar y rebuildar
mvn clean install
```

---

### Problema: Canary no responde

**Síntoma:**
```
⚠️  La API no respondió después de 30 segundos
```

**Solución:**
```powershell
# Ver logs del contenedor canary
docker-compose -f deployment/canary/docker-compose-canary.yml logs -f serviciudad-canary

# Verificar health manualmente
curl http://localhost:8080/actuator/health

# Rollback si es necesario
cd deployment\canary
.\rollback-canary.ps1
```

---

### Problema: Servidor HTTP Python no inicia

**Síntoma:**
```
ADVERTENCIA: Python no encontrado
```

**Solución:**
1. Instalar Python: https://www.python.org/downloads/
2. Verificar: `python --version`
3. Reiniciar PowerShell
4. Ejecutar `.\inicio-rapido.ps1` nuevamente

---

## 📚 Referencias

### Documentación Relacionada
- **README.md** - Documentación general del proyecto
- **ENTREGA_FINAL.md** - Documento maestro de sustentación
- **REFERENCIA_RAPIDA.md** - Comandos rápidos para demo
- **deployment/canary/README.md** - Guía completa de Canary

### Comandos Docker Útiles
```powershell
# Ver contenedores activos
docker ps

# Ver logs
docker-compose logs -f app

# Detener todo
docker-compose down

# Detener y eliminar volúmenes
docker-compose down -v

# Rebuildar sin caché
docker-compose build --no-cache

# Ver uso de recursos
docker stats
```

### Comandos Maven Útiles
```powershell
# Compilar sin tests
mvn clean install -DskipTests

# Ejecutar tests específicos
mvn test -Dtest=<TestName>

# Generar reporte JaCoCo
mvn jacoco:report

# Verificar cobertura
mvn jacoco:check
```

---

## 📊 Resumen de Scripts

| Script | Propósito | Tiempo | Prioridad |
|--------|-----------|--------|-----------|
| **inicio-rapido.ps1** | Iniciar proyecto completo | 30-60s | 🔴 ALTA |
| **quick-test.ps1** | Tests rápidos desarrollo | 5-30s | 🟡 MEDIA |
| **rebuild-docker.ps1** | Reconstrucción Docker | 2-3min | 🟢 BAJA |
| **run-all-tests.ps1** | Suite completa (87%) | 3-5min | 🔴 ALTA |
| **deploy-canary.ps1** | Despliegue canario | 5-10min | 🔴 ALTA |
| **quick-start.ps1** | Infraestructura Canary | 2-3min | 🟡 MEDIA |
| **rollback-canary.ps1** | Rollback emergencia | < 30s | 🔴 CRÍTICA |

---

## ✅ Checklist de Scripts

### Para Desarrollo
- [ ] `inicio-rapido.ps1` funciona correctamente
- [ ] `quick-test.ps1` ejecuta tests rápidos
- [ ] `rebuild-docker.ps1` reconstruye sin errores

### Para CI/CD
- [ ] `run-all-tests.ps1` pasa todos los tests (87%)
- [ ] Reporte JaCoCo genera correctamente
- [ ] Pipeline de GitHub Actions configurado

### Para Despliegue
- [ ] `deploy-canary.ps1` despliega correctamente
- [ ] `quick-start.ps1` inicia todos los servicios
- [ ] `rollback-canary.ps1` funciona en < 30s
- [ ] Monitoreo (Grafana) accesible

---

**🎓 Estado:** ✅ **SCRIPTS OPTIMIZADOS Y LISTOS PARA SUSTENTACIÓN**

**📅 Última actualización:** Noviembre 19, 2025  
**👥 Equipo:** ServiCiudad Cali - UAO

---

*Para más información, consultar DOCUMENTACION_FINAL.md y ENTREGA_FINAL.md*
