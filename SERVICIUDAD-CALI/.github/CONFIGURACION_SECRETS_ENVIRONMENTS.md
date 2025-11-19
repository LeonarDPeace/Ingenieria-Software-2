# 🔐 Guía de Configuración de Secrets y Environments

> **Repositorio:** `LeonarDPeace/Ingenieria-Software-2`  
> **Fecha:** Noviembre 19, 2025  
> **Proyecto:** SERVICIUDAD-CALI

---

## 📋 Tabla de Contenidos

1. [Acceso a Configuración](#1-acceso-a-configuración)
2. [Configurar Secrets](#2-configurar-secrets)
3. [Configurar Environments](#3-configurar-environments)
4. [Verificación](#4-verificación)
5. [Troubleshooting](#5-troubleshooting)

---

## 1️⃣ Acceso a Configuración

### Paso 1: Abrir el Repositorio Correcto
```
https://github.com/LeonarDPeace/Ingenieria-Software-2
```

### Paso 2: Ir a Settings
1. Click en la pestaña **"Settings"** (arriba a la derecha)
2. En el menú lateral izquierdo, busca la sección **"Security"**

---

## 2️⃣ Configurar Secrets

### 🔑 Secrets Necesarios

GitHub usa estos secrets automáticamente, pero algunos requieren configuración manual:

#### A. Secrets que YA EXISTEN (automáticos)
- ✅ `GITHUB_TOKEN` - Ya existe automáticamente, NO requiere configuración

#### B. Secrets OPCIONALES (solo si usas estas integraciones)

##### 1. SonarCloud (análisis de código)
**Solo si quieres usar SonarCloud:**

**Ubicación:** Settings → Secrets and variables → Actions → "New repository secret"

| Nombre | Valor | Dónde obtenerlo |
|--------|-------|----------------|
| `SONAR_TOKEN` | `[tu-token]` | https://sonarcloud.io/account/security |

**Pasos para obtener SONAR_TOKEN:**
1. Ir a https://sonarcloud.io
2. Login con tu cuenta GitHub
3. My Account → Security
4. Generate token → Copiar

**⚠️ NOTA:** Si no usas SonarCloud, puedes **comentar el job `code-quality`** en el workflow.

##### 2. Docker Hub (para despliegue con Docker)
**Solo si quieres publicar imágenes en Docker Hub:**

**Ubicación:** Settings → Secrets and variables → Actions → "New repository secret"

| Nombre | Valor | Dónde obtenerlo |
|--------|-------|----------------|
| `DOCKER_USERNAME` | `tu-usuario-dockerhub` | Tu cuenta Docker Hub |
| `DOCKER_PASSWORD` | `tu-token-dockerhub` | Docker Hub → Account Settings → Security → New Access Token |

**Pasos para obtener DOCKER credentials:**
1. Ir a https://hub.docker.com
2. Login con tu cuenta
3. Account Settings → Security → New Access Token
4. Dar nombre al token (ej: "github-actions")
5. Copiar el token generado

**⚠️ NOTA:** Si no usas Docker Hub, el job `docker-build` fallará pero el resto del pipeline funcionará.

---

### 📝 Cómo Agregar un Secret

1. Ve a: **Settings → Secrets and variables → Actions**
2. Click en **"New repository secret"**
3. **Name:** Nombre exacto del secret (ej: `SONAR_TOKEN`)
4. **Secret:** Pega el valor del token
5. Click en **"Add secret"**

**Ejemplo visual:**
```
Settings
  └── Security
      └── Secrets and variables
          └── Actions
              └── New repository secret
                  ├── Name: SONAR_TOKEN
                  └── Secret: ••••••••••••••
```

---

## 3️⃣ Configurar Environments

### 🌍 Environments Necesarios

El workflow requiere 3 environments:

| Environment | Propósito | Protección |
|-------------|-----------|------------|
| `staging` | Despliegue de pruebas | Sin protección |
| `canary` | Despliegue canary progresivo | Requiere aprobación manual |
| `production` | Despliegue producción | Requiere aprobación manual |

---

### 📝 Cómo Crear Environments

#### Ubicación
**Settings → Environments → "New environment"**

---

#### Environment 1: `staging`

1. Click en **"New environment"**
2. **Name:** `staging`
3. Click **"Configure environment"**
4. **Deployment protection rules:**
   - ❌ NO marcar "Required reviewers" (despliegue automático)
5. Click **"Save protection rules"**

**Configuración de `staging`:**
```yaml
Name: staging
Protection rules: None (despliegue automático)
Secrets: (ninguno necesario por ahora)
```

---

#### Environment 2: `canary`

1. Click en **"New environment"**
2. **Name:** `canary`
3. Click **"Configure environment"**
4. **Deployment protection rules:**
   - ✅ Marcar **"Required reviewers"**
   - Seleccionarte a ti mismo como reviewer
   - Wait timer: 0 minutos (opcional)
5. Click **"Save protection rules"**

**Configuración de `canary`:**
```yaml
Name: canary
Protection rules:
  - Required reviewers: [tu-usuario]
  - Wait timer: 0 minutes
Secrets: (ninguno necesario por ahora)
```

---

#### Environment 3: `production`

1. Click en **"New environment"**
2. **Name:** `production`
3. Click **"Configure environment"**
4. **Deployment protection rules:**
   - ✅ Marcar **"Required reviewers"**
   - Seleccionarte a ti mismo como reviewer
   - ✅ Marcar **"Prevent self-review"** (opcional, buena práctica)
   - Wait timer: 5 minutos (opcional, recomendado)
5. Click **"Save protection rules"**

**Configuración de `production`:**
```yaml
Name: production
Protection rules:
  - Required reviewers: [tu-usuario]
  - Prevent self-review: Yes (opcional)
  - Wait timer: 5 minutes (opcional)
Secrets: (ninguno necesario por ahora)
```

---

### 🎯 Ejemplo Visual de Configuración

```
Settings
  └── Environments
      ├── staging (sin protección)
      ├── canary (requiere aprobación)
      └── production (requiere aprobación + wait time)
```

---

## 4️⃣ Verificación

### ✅ Checklist de Configuración

#### Secrets (Mínimos Necesarios)
- [x] `GITHUB_TOKEN` - Ya existe automáticamente ✅
- [ ] `SONAR_TOKEN` - Solo si usas SonarCloud (opcional)
- [ ] `DOCKER_USERNAME` - Solo si usas Docker Hub (opcional)
- [ ] `DOCKER_PASSWORD` - Solo si usas Docker Hub (opcional)

#### Environments (Requeridos)
- [ ] `staging` - Sin protección
- [ ] `canary` - Con aprobación manual
- [ ] `production` - Con aprobación manual

---

### 🧪 Cómo Verificar que Está Configurado

#### Verificar Secrets
```
1. Ve a: Settings → Secrets and variables → Actions
2. Debes ver los secrets configurados (valor oculto)
```

#### Verificar Environments
```
1. Ve a: Settings → Environments
2. Debes ver 3 environments:
   - staging
   - canary
   - production
```

#### Verificar en Actions
```
1. Ve a: Actions tab
2. Selecciona el workflow "CI/CD Pipeline"
3. Si hay un run fallido por secrets/environments, ahora debe funcionar
```

---

## 5️⃣ Troubleshooting

### ❌ Error: "Context access might be invalid: SONAR_TOKEN"

**Causa:** El secret `SONAR_TOKEN` no está configurado.

**Soluciones:**
1. **Opción A (Recomendada para desarrollo):** Comentar el job `code-quality` en el workflow:
   ```yaml
   # Comentar todo el job code-quality temporalmente
   # code-quality:
   #   name: Code Quality Analysis
   #   runs-on: ubuntu-latest
   #   needs: build-and-test
   #   ...
   ```

2. **Opción B:** Configurar SonarCloud y agregar el secret `SONAR_TOKEN`

---

### ❌ Error: "Context access might be invalid: DOCKER_USERNAME"

**Causa:** Los secrets de Docker no están configurados.

**Soluciones:**
1. **Opción A (Recomendada para desarrollo):** Modificar el job `docker-build` para que NO requiera push:
   ```yaml
   docker-build:
     # Cambiar condición para que no se ejecute en temp-config
     if: github.event_name == 'push' && github.ref == 'refs/heads/main'
   ```

2. **Opción B:** Configurar Docker Hub y agregar los secrets

---

### ❌ Error: "Value 'staging' is not valid"

**Causa:** El environment `staging` no está creado.

**Solución:**
1. Ve a Settings → Environments
2. Click "New environment"
3. Nombre: `staging`
4. Configure environment → Save

Repite para `canary` y `production`.

---

### ✅ Configuración Mínima para Testing

Si solo quieres que el pipeline **ejecute tests y genere coverage**, esta es la configuración mínima:

#### Secrets Necesarios:
- ✅ `GITHUB_TOKEN` (ya existe)

#### Environments Necesarios:
- ✅ `staging`
- ✅ `canary`
- ✅ `production`

#### Jobs que Funcionarán:
1. ✅ `build-and-test` - Tests + Coverage (87%)
2. ❌ `code-quality` - Requiere SONAR_TOKEN (comentar si no usas)
3. ❌ `docker-build` - Requiere Docker secrets (solo se ejecuta en main)
4. ❌ `security-scan` - Depende de docker-build
5. ⚠️ `deploy-staging` - Requiere environment staging
6. ⚠️ `canary-deploy` - Requiere environment canary
7. ⚠️ `deploy-production` - Requiere environment production
8. ✅ `cleanup` - No requiere configuración especial

---

## 📌 Configuración Recomendada para SUSTENTACIÓN

Para demostrar el pipeline en la sustentación, necesitas **mínimo**:

### ✅ Configuración Básica (Suficiente para Demo)

#### 1. Crear los 3 Environments
- `staging` (sin protección)
- `canary` (con aprobación)
- `production` (con aprobación)

#### 2. Comentar Jobs Opcionales en el Workflow
```yaml
# Comentar job code-quality (línea ~90)
# code-quality:
#   name: Code Quality Analysis
#   ...

# Modificar docker-build para que no se ejecute en temp-config (línea ~133)
docker-build:
  if: github.event_name == 'push' && github.ref == 'refs/heads/main'
  # Solo se ejecutará cuando hagas merge a main
```

#### 3. Resultado
Con esta configuración, el pipeline ejecutará:
- ✅ Build and Test (con coverage 87%)
- ✅ Los demás jobs quedarán "skipped" (no es un error)

---

## 🎓 Para la Sustentación

### Lo que DEBES mostrar:
1. ✅ **Tests ejecutándose automáticamente** (build-and-test job)
2. ✅ **Coverage 87%** en los reportes
3. ✅ **Pipeline se dispara con cada commit**
4. ✅ **3 Environments configurados** (staging, canary, production)

### Lo que es OPCIONAL (pero suma puntos):
- 🟡 SonarCloud configurado (análisis de calidad)
- 🟡 Docker build funcionando
- 🟡 Despliegues a staging/canary/production

---

## 📝 Resumen Rápido

### Configuración en 5 Pasos:

1. **Settings → Environments → New environment**
   - Crear: `staging`, `canary`, `production`

2. **Configurar protecciones:**
   - `staging`: Sin protección
   - `canary`: Requiere aprobación
   - `production`: Requiere aprobación

3. **(Opcional) Settings → Secrets → New repository secret**
   - `SONAR_TOKEN` si usas SonarCloud
   - `DOCKER_USERNAME` y `DOCKER_PASSWORD` si usas Docker Hub

4. **Hacer un commit para disparar el pipeline:**
   ```bash
   git commit --allow-empty -m "test: Verificar pipeline con environments"
   git push origin temp-config
   ```

5. **Verificar en Actions:**
   - Ve a la pestaña Actions
   - Verifica que el pipeline se ejecuta
   - Los jobs opcionales pueden fallar (normal sin secrets)

---

## 🔗 Enlaces Útiles

- **Tu repositorio:** https://github.com/LeonarDPeace/Ingenieria-Software-2
- **Settings:** https://github.com/LeonarDPeace/Ingenieria-Software-2/settings
- **Actions:** https://github.com/LeonarDPeace/Ingenieria-Software-2/actions
- **Environments:** https://github.com/LeonarDPeace/Ingenieria-Software-2/settings/environments
- **Secrets:** https://github.com/LeonarDPeace/Ingenieria-Software-2/settings/secrets/actions

---

## ✅ Checklist Final

Antes de la sustentación, verifica:

- [ ] Repositorio correcto: `LeonarDPeace/Ingenieria-Software-2`
- [ ] Workflow en: `.github/workflows/ci-cd.yml` (raíz)
- [ ] 3 Environments creados (staging, canary, production)
- [ ] Pipeline se ejecuta automáticamente con commits
- [ ] Job `build-and-test` pasa exitosamente (87% coverage)
- [ ] Jobs opcionales comentados si no tienes secrets

---

*Guía creada: Noviembre 19, 2025*  
*Universidad Autónoma de Occidente - Ingeniería de Software II*
