# 🐤 ServiCiudad Cali - Canary Deployment

## 📋 Tabla de Contenidos

- [Introducción](#introducción)
- [Arquitectura](#arquitectura)
- [Requisitos Previos](#requisitos-previos)
- [Instalación](#instalación)
- [Guía de Uso](#guía-de-uso)
- [Monitoreo](#monitoreo)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)

---

## 🎯 Introducción

Este directorio contiene la configuración completa para realizar **despliegues canario progresivos** de ServiCiudad Cali. El despliegue canario permite:

- ✅ **Reducir riesgos** exponiendo la nueva versión solo a un porcentaje de usuarios
- ✅ **Detectar problemas temprano** antes de afectar a todos los usuarios
- ✅ **Rollback instantáneo** si se detectan anomalías
- ✅ **Monitoreo comparativo** entre versiones stable y canary

---

## 🏗️ Arquitectura

```
┌─────────────────────────────────────────────────────┐
│                  USUARIOS (100%)                    │
└────────────────────┬────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────┐
│          NGINX LOAD BALANCER (Canary Routing)      │
│  • Split Traffic: 90% Stable / 10% Canary          │
│  • Headers: X-Deployment-Version                   │
└──────────────┬──────────────────────┬───────────────┘
               │                      │
         90%   ▼                      ▼   10%
┌──────────────────────┐    ┌──────────────────────┐
│  SERVICIUDAD STABLE  │    │  SERVICIUDAD CANARY  │
│    v1.0.0 (9 pods)   │    │   v2.0.0 (1 pod)     │
└──────────┬───────────┘    └──────────┬───────────┘
           │                           │
           └───────────┬───────────────┘
                       ▼
           ┌─────────────────────┐
           │   POSTGRESQL DB     │
           │   (Shared)          │
           └─────────────────────┘

┌─────────────────────────────────────────────────────┐
│              MONITORING STACK                       │
│  • Prometheus: Métricas                            │
│  • Grafana: Dashboards                             │
│  • AlertManager: Alertas                           │
└─────────────────────────────────────────────────────┘
```

---

## ✅ Requisitos Previos

### Software Requerido

- **Docker** 20.10+
- **Docker Compose** 2.0+
- **PowerShell** 5.1+ (Windows)
- **Git** 2.30+

### Recursos de Sistema

- **CPU:** 4 cores (mínimo), 8 cores (recomendado)
- **RAM:** 8 GB (mínimo), 16 GB (recomendado)
- **Disco:** 20 GB libres

### Verificar Instalación

```powershell
# Verificar Docker
docker --version

# Verificar Docker Compose
docker-compose --version

# Verificar PowerShell
$PSVersionTable.PSVersion
```

---

## 🚀 Instalación

### 1. Preparar Configuración

```powershell
# Navegar al directorio de despliegue
cd "d:\Google Drive\Universidad\7mo Semestre\Ing. de Software 2\ProyectoFinal\Ingenieria-Software-2\SERVICIUDAD-CALI\deployment\canary"

# Copiar archivo de variables de entorno
Copy-Item .env.example .env

# Editar .env con tus configuraciones
notepad .env
```

### 2. Construir Imágenes Docker

```powershell
# Construir versión estable
cd ..\..\
docker build -t serviciudad:1.0.0 .

# Construir versión canary (nueva)
docker build -t serviciudad:2.0.0 .
```

### 3. Verificar Imágenes

```powershell
docker images serviciudad
```

Salida esperada:
```
REPOSITORY     TAG       IMAGE ID       SIZE
serviciudad    2.0.0     abc123def      350MB
serviciudad    1.0.0     xyz789ghi      340MB
```

---

## 📖 Guía de Uso

### Despliegue Canario Básico

#### **Fase 1: Canary 10% (1 hora de monitoreo)**

```powershell
cd deployment\canary
.\deploy-canary.ps1 -CanaryVersion "2.0.0" -CanaryPercent 10 -WaitMinutes 60
```

**Qué hace:**
- Despliega 1 réplica canary (10%) y 9 réplicas stable (90%)
- Configura Nginx para enviar 10% del tráfico a canary
- Ejecuta health checks y smoke tests
- Muestra URLs de monitoreo

**Monitorear por 1 hora:**
- ✅ Tasa de errores estable
- ✅ Latencia comparable
- ✅ Sin alertas críticas

---

#### **Fase 2: Canary 25% (2 horas de monitoreo)**

```powershell
.\deploy-canary.ps1 -CanaryVersion "2.0.0" -CanaryPercent 25 -WaitMinutes 120
```

**Validar:**
- ✅ Métricas de negocio correctas
- ✅ Feedback de usuarios positivo
- ✅ Logs sin errores críticos

---

#### **Fase 3: Canary 50% (4 horas de monitoreo)**

```powershell
.\deploy-canary.ps1 -CanaryVersion "2.0.0" -CanaryPercent 50 -WaitMinutes 240
```

**Validar:**
- ✅ Carga en base de datos estable
- ✅ Sin degradación de performance
- ✅ Satisfacción de usuarios

---

#### **Fase 4: Full Rollout 100%**

```powershell
.\deploy-canary.ps1 -CanaryVersion "2.0.0" -CanaryPercent 100
```

**Resultado:**
- ✅ 100% del tráfico en nueva versión
- ✅ Versión antigua disponible para rollback por 24h

---

### Despliegue Automatizado

Para despliegue automático con promoción progresiva:

```powershell
.\deploy-canary.ps1 `
    -CanaryVersion "2.0.0" `
    -CanaryPercent 10 `
    -WaitMinutes 30 `
    -AutoPromote
```

**Funcionalidad:**
- Monitorea métricas automáticamente
- Promueve a siguiente fase si métricas OK
- Detiene y alerta si detecta anomalías

---

### Rollback de Emergencia

Si se detectan problemas en la versión canary:

```powershell
.\rollback-canary.ps1
```

**Qué hace:**
1. Detiene todas las réplicas canary
2. Escala stable a 100%
3. Reconfigura Nginx para enviar todo el tráfico a stable
4. Verifica health de servicios

**Tiempo de ejecución:** < 30 segundos

---

### Modo Dry-Run

Para validar el despliegue sin ejecutar cambios:

```powershell
.\deploy-canary.ps1 -CanaryVersion "2.0.0" -CanaryPercent 10 -DryRun
```

**Útil para:**
- Validar scripts antes de producción
- Entrenar equipo nuevo
- Probar configuraciones

---

## 📊 Monitoreo

### Dashboards Disponibles

| Dashboard | URL | Descripción |
|-----------|-----|-------------|
| **Prometheus** | http://localhost:9090 | Métricas raw y queries |
| **Grafana** | http://localhost:3000 | Visualizaciones comparativas |
| **AlertManager** | http://localhost:9093 | Estado de alertas |
| **Nginx Status** | http://localhost/nginx-health | Health del load balancer |

### Credenciales

```
Grafana:
  Usuario: admin
  Contraseña: admin123 (cambiar en .env)
```

### Métricas Clave a Monitorear

#### 1. **Tasa de Errores**

```promql
# Error rate canary vs stable
sum(rate(http_server_requests_seconds_count{deployment="canary",status=~"5.."}[5m])) 
/ 
sum(rate(http_server_requests_seconds_count{deployment="canary"}[5m]))
```

**Umbral:** Canary no debe superar stable en > 50%

#### 2. **Latencia P95**

```promql
# P95 latency
histogram_quantile(0.95, 
  sum(rate(http_server_requests_seconds_bucket{deployment="canary"}[5m])) by (le)
)
```

**Umbral:** < 2 segundos

#### 3. **Uso de Memoria**

```promql
# Memory usage
container_memory_usage_bytes{name=~".*canary.*"} / 1024 / 1024
```

**Umbral:** < 1 GB

#### 4. **Throughput**

```promql
# Requests per second
sum(rate(http_server_requests_seconds_count{deployment="canary"}[5m]))
```

**Validar:** Canary recibe ~10% del tráfico total

---

### Alertas Configuradas

| Alerta | Severidad | Descripción | Acción |
|--------|-----------|-------------|--------|
| `CanaryHighErrorRate` | 🔴 Critical | Canary > 50% más errores que stable | **Rollback inmediato** |
| `CanaryHighLatency` | 🟡 Warning | P95 > 2s | Investigar |
| `CanaryHighMemory` | 🟡 Warning | Uso > 85% | Investigar |
| `ServiceDown` | 🔴 Critical | Servicio no responde | Rollback / Investigar |

---

## 🔧 Troubleshooting

### Problema: "Docker no está disponible"

**Síntomas:**
```
❌ Docker no está disponible
```

**Solución:**
```powershell
# Iniciar Docker Desktop
Start-Process "C:\Program Files\Docker\Docker\Docker Desktop.exe"

# Esperar 30 segundos
Start-Sleep -Seconds 30

# Verificar
docker ps
```

---

### Problema: "Health checks fallando"

**Síntomas:**
```
❌ Timeout esperando health checks
```

**Solución:**
```powershell
# Ver logs de servicios
docker-compose -f docker-compose-canary.yml logs serviciudad-canary

# Verificar base de datos
docker-compose -f docker-compose-canary.yml logs postgres

# Revisar configuración
docker inspect serviciudad-canary
```

---

### Problema: "Canary no recibe tráfico"

**Síntomas:**
- Prometheus muestra 0 requests en canary
- Grafana dashboard vacío para canary

**Solución:**
```powershell
# Verificar configuración Nginx
docker-compose -f docker-compose-canary.yml exec nginx-lb cat /etc/nginx/nginx.conf | Select-String "split_clients"

# Recargar Nginx
docker-compose -f docker-compose-canary.yml exec nginx-lb nginx -s reload

# Verificar logs de Nginx
docker-compose -f docker-compose-canary.yml logs nginx-lb
```

---

### Problema: "Métricas no aparecen en Grafana"

**Solución:**
```powershell
# Verificar Prometheus está scrapeando
# Abrir: http://localhost:9090/targets
# Verificar que serviciudad-canary esté "UP"

# Reiniciar Prometheus
docker-compose -f docker-compose-canary.yml restart prometheus

# Verificar logs
docker-compose -f docker-compose-canary.yml logs prometheus
```

---

## ❓ FAQ

### ¿Cuánto tiempo debo monitorear cada fase?

**Recomendación:**
- **10%:** 1 hora mínimo
- **25%:** 2 horas mínimo
- **50%:** 4 horas mínimo
- **100%:** 24 horas de observación post-despliegue

### ¿Puedo hacer rollback después del 100%?

**Sí**, pero con limitaciones:
- Mantener imagen stable disponible por 24-48h
- Considerar migraciones de base de datos (pueden ser irreversibles)
- Comunicar al equipo antes de eliminar versión anterior

### ¿Qué pasa con la base de datos compartida?

**Estrategia de migraciones:**

1. **Migraciones compatibles hacia atrás** (backward-compatible)
   - Agregar columnas como nullable
   - No eliminar columnas en misma versión
   
2. **Proceso de 3 fases:**
   - Fase 1: Agregar nueva columna (nullable)
   - Fase 2: Desplegar código que usa ambas columnas
   - Fase 3: Eliminar columna antigua (próximo despliegue)

### ¿Cómo afecta el caché?

**Recomendaciones:**
- Usar headers `X-Deployment-Version` para invalidación selectiva
- TTL de caché corto durante despliegue (< 5 minutos)
- Considerar cache warming para canary

### ¿Puedo usar esto en desarrollo/staging?

**Sí, es recomendado:**
```powershell
# Testing en staging
$env:ENVIRONMENT = "staging"
.\deploy-canary.ps1 -CanaryVersion "2.0.0-rc1" -CanaryPercent 50
```

---

## 📚 Recursos Adicionales

- **Prometheus Query Examples:** [prometheus/queries.md](./docs/prometheus-queries.md)
- **Grafana Dashboard JSON:** [grafana/dashboards/canary-comparison.json](./grafana/dashboards/canary-comparison.json)
- **Alert Rules:** [prometheus/alerts/canary-alerts.yml](./prometheus/alerts/canary-alerts.yml)

---

## 🤝 Soporte

Para reportar problemas o sugerencias:

1. **Issues:** Crear issue en GitHub con label `canary-deployment`
2. **Slack:** Canal `#serviciudad-devops`
3. **Email:** devops-team@example.com

---

## 📝 Licencia

MIT License - ServiCiudad Cali © 2025
