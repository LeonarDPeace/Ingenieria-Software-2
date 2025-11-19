# 🚀 Referencia Rápida - ServiCiudad Cali

> **Para:** Sustentación Ingeniería de Software II  
> **Fecha:** Noviembre 2025  
> **Duración:** 15 minutos

---

## 📖 ¿Qué archivo leer PRIMERO?

### ⭐ **ENTREGA_FINAL.md** (PRIORIDAD MÁXIMA)
- **Ubicación:** `SERVICIUDAD-CALI/ENTREGA_FINAL.md`
- **Tamaño:** 22.5 KB (lectura: ~10 minutos)
- **Contiene:**
  - ✅ Los 3 requisitos académicos explicados
  - ✅ Evidencias de cumplimiento 100%
  - ✅ Guía de demo paso a paso (15 min)
  - ✅ FAQ para profesores

**👉 Leer 15 minutos ANTES de la sustentación**

---

## ⚡ Comandos Rápidos para Demo

### 1️⃣ Demostrar Cobertura (87%)
```bash
# Ejecutar tests y generar reporte
mvn clean test jacoco:report

# Abrir reporte HTML
start target/site/jacoco/index.html
```
**Resultado esperado:** 87% coverage (199 tests passing)

---

### 2️⃣ Demostrar Pipeline CI/CD
```bash
# Abrir GitHub Actions en navegador
start https://github.com/[tu-repo]/actions

# O mostrar archivo local
code .github/workflows/ci-cd.yml
```
**Mostrar:** 8 jobs, job `canary-deploy` explícito (línea ~150)

---

### 3️⃣ Demostrar Canary Deployment
```powershell
# Iniciar infraestructura completa
cd deployment\canary
.\quick-start.ps1

# Verificar contenedores (debe mostrar 7)
docker ps

# Abrir dashboards
start http://localhost:3000  # Grafana (admin/admin)
start http://localhost:9090  # Prometheus
```
**Resultado esperado:** 7 contenedores, Grafana mostrando split 90/10

---

## 🎯 Los 3 Requisitos (Resumen)

### ✅ Requisito 1: Cobertura ≥ 80%
- **Alcanzado:** 87%
- **Tests:** 199 passing
- **Herramienta:** JaCoCo
- **Validación:** `mvn jacoco:check` (falla si < 80%)
- **Reporte:** `target/site/jacoco/index.html`

### ✅ Requisito 2: Pipeline CI/CD
- **Jobs:** 8 automatizados
- **Validación:** Automática en cada commit
- **Fallo:** Si coverage < 80%, build falla
- **Archivo:** `.github/workflows/ci-cd.yml`

### ✅ Requisito 3: Canary Deployment
- **Contenedores:** 7 (stable, canary, nginx, postgres, prometheus, grafana, alertmanager)
- **Split:** 90% stable / 10% canary
- **Job Pipeline:** `canary-deploy` (explícito en pipeline)
- **Rollback:** < 30 segundos
- **Monitoreo:** Prometheus + Grafana

---

## 💬 Respuestas Rápidas (FAQ)

### P: ¿Cómo garantizan el 80% de cobertura?
**R:** JaCoCo en `pom.xml` con threshold 0.80 → **falla el build** si baja

### P: ¿El pipeline valida la cobertura automáticamente?
**R:** Sí, step `mvn jacoco:check` retorna exit code 1 si < 80%

### P: ¿Dónde está el job de Canary en el pipeline?
**R:** `.github/workflows/ci-cd.yml` línea ~150, job `canary-deploy`

### P: ¿Qué pasa si Canary falla?
**R:** Rollback automático en < 30s, stable (90%) sigue corriendo = 0 downtime

### P: ¿Cómo funciona el split de tráfico?
**R:** Nginx con `split_clients` → hash de IP+UserAgent → 10% canary, 90% stable

---

## 📂 Estructura de Archivos (Simplificada)

```
SERVICIUDAD-CALI/
├── 📘 ENTREGA_FINAL.md ⭐        ← LEER PRIMERO
├── 📗 README.md                  ← Overview
├── 📕 INFORME.md                 ← Detalles técnicos
├── 📙 PLAN_TESTS_COBERTURA_85.md ← Testing
├── 📄 DOCUMENTACION_FINAL.md     ← Índice completo
│
├── .github/workflows/
│   └── ci-cd.yml                 ← Pipeline (8 jobs)
│
└── deployment/canary/
    ├── 📗 README.md              ← Guía Canary
    ├── 📋 DEPLOYMENT_CHECKLIST.md
    ├── quick-start.ps1           ← Demo rápida
    ├── deploy-canary.ps1
    └── rollback-canary.ps1
```

---

## ⏱️ Timeline de Demo (15 minutos)

| Min | Actividad | Comando/Acción |
|-----|-----------|----------------|
| 0-3 | Cobertura | `mvn jacoco:report` → mostrar 87% |
| 3-8 | Pipeline | Abrir GitHub Actions → mostrar 8 jobs + canary-deploy |
| 8-15 | Canary | `.\quick-start.ps1` → docker ps → Grafana localhost:3000 |

---

## 🎓 Checklist Pre-Sustentación

### 15 minutos antes
- [ ] Leer **ENTREGA_FINAL.md** completo
- [ ] Revisar FAQ de respuestas rápidas
- [ ] Docker Desktop iniciado y funcionando
- [ ] Terminal abierta en `SERVICIUDAD-CALI/`

### 5 minutos antes
- [ ] Ejecutar `mvn clean test` (verificar 199 passing)
- [ ] Verificar que puerto 3000, 8080, 9090 estén libres
- [ ] Tener navegador listo en pestañas:
  - GitHub Actions
  - localhost:3000 (Grafana)
  - target/site/jacoco/index.html

---

## 🔗 Enlaces Útiles

| Recurso | Ubicación |
|---------|-----------|
| **Documento Maestro** | `ENTREGA_FINAL.md` |
| **Índice Completo** | `DOCUMENTACION_FINAL.md` |
| **Guía Canary** | `deployment/canary/README.md` |
| **Pipeline Config** | `.github/workflows/ci-cd.yml` |
| **JaCoCo Config** | `pom.xml` (línea ~180) |

---

## 📊 Números Clave (Memorizar)

```
87%     - Coverage alcanzado
199     - Tests passing
8       - Jobs en pipeline
7       - Contenedores Docker
90/10   - Split traffic (stable/canary)
< 30s   - Tiempo de rollback
100%    - Cumplimiento requisitos
```

---

## 🚨 Troubleshooting Rápido

### Problema: Tests fallan
```bash
mvn clean
mvn test
```

### Problema: Docker no inicia
```powershell
# Reiniciar Docker Desktop
Restart-Service Docker
```

### Problema: Puerto ocupado
```powershell
# Ver qué usa el puerto
netstat -ano | findstr :8080
```

---

## ✅ Estado Final

```
╔══════════════════════════════════════════╗
║  ✅ Cobertura:     87%                   ║
║  ✅ Tests:         199 passing           ║
║  ✅ Pipeline:      8 jobs                ║
║  ✅ Canary:        Docker completo       ║
║  ✅ Documentación: Optimizada            ║
║                                          ║
║  🎯 LISTO PARA SUSTENTACIÓN             ║
╚══════════════════════════════════════════╝
```

---

**👉 Próximo paso:** Leer `ENTREGA_FINAL.md` ⭐

---

*Referencia rápida para Ingeniería de Software II*  
*Universidad Autónoma de Occidente - Noviembre 2025*
