# 🔧 Configuración del Pipeline CI/CD

## 📋 Tabla de Contenidos
- [Secrets Requeridos](#secrets-requeridos)
- [Environments de GitHub](#environments-de-github)
- [Configuración Paso a Paso](#configuración-paso-a-paso)
- [Validación de Configuración](#validación-de-configuración)

---

## 🔐 Secrets Requeridos

El pipeline CI/CD requiere los siguientes secrets configurados en el repositorio:

### 1. SONAR_TOKEN
**Propósito:** Análisis de calidad de código con SonarCloud  
**Dónde obtenerlo:** https://sonarcloud.io/account/security  
**Uso:** Job `code-quality`

### 2. DOCKER_USERNAME
**Propósito:** Autenticación en Docker Hub  
**Valor:** Tu nombre de usuario de Docker Hub  
**Uso:** Job `docker-build`

### 3. DOCKER_PASSWORD
**Propósito:** Autenticación en Docker Hub  
**Valor:** Token de acceso o contraseña de Docker Hub  
**Recomendación:** Usar Access Token en lugar de contraseña  
**Cómo crear token:** https://hub.docker.com/settings/security  
**Uso:** Job `docker-build`

### 4. GITHUB_TOKEN (Automático)
**Propósito:** Autenticación con GitHub API  
**Nota:** Este secret es automático, no requiere configuración manual

---

## 🌍 Environments de GitHub

El pipeline usa tres environments para despliegues:

### 1. Staging
- **URL:** https://staging.serviciudad.cali.gov.co
- **Propósito:** Validación pre-producción
- **Job:** `deploy-staging`

### 2. Canary
- **URL:** https://canary.serviciudad.cali.gov.co
- **Propósito:** Despliegue progresivo con 10% de tráfico
- **Job:** `canary-deploy`

### 3. Production
- **URL:** https://serviciudad.cali.gov.co
- **Propósito:** Entorno de producción
- **Job:** `deploy-production`

---

## 📝 Configuración Paso a Paso

### Paso 1: Configurar Secrets

#### En GitHub:
1. Ve a tu repositorio en GitHub
2. Click en **Settings** (⚙️)
3. En el menú lateral, click en **Secrets and variables** > **Actions**
4. Click en **New repository secret**

#### Para cada secret:

**SONAR_TOKEN:**
```
Name: SONAR_TOKEN
Value: [Tu token de SonarCloud]
```

**DOCKER_USERNAME:**
```
Name: DOCKER_USERNAME
Value: [Tu usuario de Docker Hub]
```

**DOCKER_PASSWORD:**
```
Name: DOCKER_PASSWORD
Value: [Tu token de Docker Hub]
```

### Paso 2: Configurar Environments

#### En GitHub:
1. Ve a tu repositorio en GitHub
2. Click en **Settings** (⚙️)
3. En el menú lateral, click en **Environments**
4. Click en **New environment**

#### Crear environment "staging":
```
Name: staging
Deployment protection rules: (opcional)
  ✓ Required reviewers: [opcional]
Environment variables:
  URL: https://staging.serviciudad.cali.gov.co
```

#### Crear environment "canary":
```
Name: canary
Deployment protection rules: (opcional)
  ✓ Required reviewers: [recomendado]
Environment variables:
  URL: https://canary.serviciudad.cali.gov.co
```

#### Crear environment "production":
```
Name: production
Deployment protection rules: (recomendado)
  ✓ Required reviewers: [recomendado]
  ✓ Wait timer: 5 minutes [opcional]
Environment variables:
  URL: https://serviciudad.cali.gov.co
```

### Paso 3: Habilitar Environments en el Pipeline

Una vez creados los environments, descomenta las secciones en `.github/workflows/ci-cd.yml`:

**En deploy-staging (líneas ~198-202):**
```yaml
environment:
  name: staging
  url: https://staging.serviciudad.cali.gov.co
```

**En canary-deploy (líneas ~228-232):**
```yaml
environment:
  name: canary
  url: https://canary.serviciudad.cali.gov.co
```

**En deploy-production (líneas ~300-304):**
```yaml
environment:
  name: production
  url: https://serviciudad.cali.gov.co
```

---

## ✅ Validación de Configuración

### Verificar Secrets Configurados

Puedes verificar que los secrets están configurados (sin ver sus valores):
1. Ve a **Settings** > **Secrets and variables** > **Actions**
2. Deberías ver:
   - ✅ `SONAR_TOKEN`
   - ✅ `DOCKER_USERNAME`
   - ✅ `DOCKER_PASSWORD`

### Verificar Environments

1. Ve a **Settings** > **Environments**
2. Deberías ver:
   - ✅ `staging`
   - ✅ `canary`
   - ✅ `production`

### Probar el Pipeline

1. Haz un push a la rama `main`:
```bash
git add .
git commit -m "test: Validar configuración CI/CD"
git push origin main
```

2. Ve a la pestaña **Actions** en GitHub
3. Verifica que los jobs se ejecuten correctamente:
   - ✅ **build-and-test** (siempre se ejecuta)
   - ✅ **code-quality** (requiere `SONAR_TOKEN`)
   - ✅ **docker-build** (requiere `DOCKER_USERNAME`, `DOCKER_PASSWORD`)
   - ✅ **security-scan** (depende de docker-build)
   - ✅ **deploy-staging** (requiere environment `staging`)
   - ✅ **canary-deploy** (requiere environment `canary`)
   - ✅ **deploy-production** (requiere environment `production`)

---

## 🔄 Flujo Completo del Pipeline

```
┌──────────────────┐
│  Push to main    │
└────────┬─────────┘
         │
         ▼
┌──────────────────────────────────┐
│  build-and-test                  │
│  • Compila código                │
│  • Ejecuta tests unitarios       │
│  • Ejecuta tests integración     │
│  • Genera reporte cobertura      │
└────────┬─────────────────────────┘
         │
         ▼
┌──────────────────────────────────┐
│  code-quality                    │
│  • Análisis SonarCloud           │
│  • Verifica calidad código       │
└────────┬─────────────────────────┘
         │
         ├─────────────────────────┐
         ▼                         ▼
┌──────────────────┐    ┌──────────────────┐
│  docker-build    │    │  security-scan   │
│  • Build imagen  │───▶│  • Trivy scan    │
│  • Push Docker   │    │  • Vulnerabilities│
└────────┬─────────┘    └──────────────────┘
         │
         ▼
┌──────────────────────────────────┐
│  deploy-staging                  │
│  • Despliega a staging           │
│  • Smoke tests                   │
└────────┬─────────────────────────┘
         │
         ▼
┌──────────────────────────────────┐
│  canary-deploy                   │
│  • Despliega 10% tráfico         │
│  • Monitorea métricas            │
│  • Valida performance            │
│  • Promoción o rollback          │
└────────┬─────────────────────────┘
         │
         ▼
┌──────────────────────────────────┐
│  deploy-production               │
│  • Despliega a producción        │
│  • Smoke tests                   │
│  • Notificaciones                │
└──────────────────────────────────┘
```

---

## 🚨 Troubleshooting

### Error: "SONAR_TOKEN not found"
**Solución:** Configura el secret `SONAR_TOKEN` siguiendo el [Paso 1](#paso-1-configurar-secrets)

### Error: "DOCKER_USERNAME not found"
**Solución:** Configura los secrets `DOCKER_USERNAME` y `DOCKER_PASSWORD` siguiendo el [Paso 1](#paso-1-configurar-secrets)

### Error: "Environment 'staging' not found"
**Solución:** Crea los environments siguiendo el [Paso 2](#paso-2-configurar-environments)

### Los jobs de deployment no se ejecutan
**Verifica:**
1. ✅ El push es a la rama `main`
2. ✅ Los jobs anteriores completaron exitosamente
3. ✅ Los environments están creados
4. ✅ Las secciones `environment` están descomentadas en el YAML

---

## 📚 Referencias

- [GitHub Actions - Secrets](https://docs.github.com/en/actions/security-guides/encrypted-secrets)
- [GitHub Actions - Environments](https://docs.github.com/en/actions/deployment/targeting-different-environments/using-environments-for-deployment)
- [SonarCloud Documentation](https://docs.sonarcloud.io/)
- [Docker Hub Access Tokens](https://docs.docker.com/docker-hub/access-tokens/)
- [GitHub Actions - Workflow Syntax](https://docs.github.com/en/actions/using-workflows/workflow-syntax-for-github-actions)

---

## 📞 Soporte

Para más información sobre la configuración del pipeline CI/CD:
- 📖 Ver: `SCRIPTS_GUIA.md` - Scripts de automatización
- 📘 Ver: `ENTREGA_FINAL.md` - Documentación completa del proyecto
- 🚀 Ver: `REFERENCIA_RAPIDA.md` - Comandos de demostración

---

**Última actualización:** Noviembre 2025  
**Versión:** 1.0  
**Proyecto:** ServiCiudad Cali
