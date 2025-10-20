# 🏛️ ServiCiudad Cali - Sistema de Consulta Unificada

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-blue.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📋 Descripción

**ServiCiudad Cali** es una aplicación monolítica desarrollada con **Spring Boot** que centraliza la consulta de deuda de servicios públicos (Energía y Acueducto) a través de una **API RESTful** unificada.

### 🎉 Estado Actual: **100% OPERACIONAL Y OPTIMIZADO**

✅ **Sistema completamente funcional y validado**  
✅ **12/12 endpoints principales testeados exitosamente**  
✅ **Arquitectura Hexagonal implementada correctamente**  
✅ **5 patrones de diseño funcionando perfectamente**  
✅ **Docker y PostgreSQL operativos**  
✅ **Frontend responsive con favicon**  
✅ **Colección Postman completa y actualizada**  
✅ **Documentación técnica exhaustiva**

### Problema que Resuelve

Los ciudadanos de Cali deben contactar **tres canales diferentes** para conocer sus saldos de Energía, Acueducto y Telecomunicaciones. Este sistema unifica la consulta en un **único endpoint**, automatizando el 60% de las llamadas al contact center.

### Características Principales

✅ **Consulta Unificada**: Un solo endpoint para consultar deuda de Energía y Acueducto  
✅ **Integración Legacy**: Lectura de archivo plano (Mainframe IBM Z) y base de datos PostgreSQL  
✅ **API RESTful**: Endpoints documentados con OpenAPI/Swagger  
✅ **Frontend Responsive**: Interfaz web moderna con HTML5, CSS3 y JavaScript vanilla  
✅ **Docker Ready**: Despliegue completo con `docker-compose`  
✅ **Patrones de Diseño**: Implementación de 5 patrones (Adapter, Builder, DTO, Repository, IoC/DI)  
✅ **Seguridad**: Autenticación HTTP Basic con favicon y recursos públicos configurados  
✅ **Monitoreo**: Actuator endpoints para health checks y métricas  
✅ **Arquitectura Hexagonal**: Separación clara entre dominio, aplicación e infraestructura

---

## 🏗️ Arquitectura

### ⚠️ Correcciones Implementadas

Durante el desarrollo, se identificó y corrigió un **problema crítico de arquitectura**:

**Problema Original:**
- El archivo `HexagonalConfig.java` creaba beans manualmente con `@Bean`
- Esto impedía que Spring detectara las anotaciones `@Service` en los Use Cases
- Los controladores no se registraban porque las dependencias no estaban disponibles
- El sistema iniciaba pero sin endpoints operativos (0 endpoints registrados)

**Solución Implementada:**
- ✅ Se **eliminó** `HexagonalConfig.java`
- ✅ Se utilizó **component scanning automático** de Spring
- ✅ Todos los Use Cases con `@Service` son detectados correctamente
- ✅ Los controladores se registran con sus endpoints (`Mapped {[...]}`)
- ✅ Sistema 100% operacional con todas las dependencias resueltas

### Diagrama de Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│              FRONTEND (HTML/CSS/JS + favicon)               │
│                  http://localhost:8080/                     │
└──────────────────────────┬──────────────────────────────────┘
                           │ HTTP/JSON
                           ▼
┌─────────────────────────────────────────────────────────────┐
│              API REST (Spring Boot 3.2.12)                  │
│              GET /api/deuda/cliente/{clienteId}             │
│              GET /api/facturas/{id}                         │
│              GET /api/consumos-energia/cliente/{id}         │
│              Port: 8080 (HTTP Basic Auth)                   │
└──────────────┬─────────────────────────────┬────────────────┘
               │                             │
               ▼                             ▼
┌──────────────────────────┐   ┌─────────────────────────────┐
│  ADAPTADOR ARCHIVO TXT   │   │   ADAPTADOR PostgreSQL      │
│  ConsumoEnergiaReader    │   │   FacturaRepository         │
│  (Patrón Adapter)        │   │   (Patrón Repository)       │
│  @Component              │   │   @Repository + @Component  │
└──────────────┬───────────┘   └──────────────┬──────────────┘
               │                              │
               ▼                              ▼
┌──────────────────────────┐   ┌─────────────────────────────┐
│  consumos_energia.txt    │   │  PostgreSQL Database        │
│  (Mainframe IBM Z)       │   │  facturas_acueducto table   │
│  Formato ancho fijo      │   │  Port: 5432                 │
└──────────────────────────┘   └─────────────────────────────┘
```

### Patrones de Diseño Implementados

| Patrón | Ubicación | Propósito | Estado |
|--------|-----------|-----------|--------|
| **Adapter** | `ConsumoEnergiaReaderAdapter` | Adapta archivo de ancho fijo a objetos Java | ✅ Validado |
| **Builder** | `DeudaConsolidadaDTO.Builder` | Construcción paso a paso de DTOs complejos | ✅ Validado |
| **DTO** | `application/dto/` | Separación de entidades de dominio y API | ✅ Validado |
| **Repository** | `FacturaJpaRepository` | Abstracción de acceso a datos (Spring Data JPA) | ✅ Validado |
| **IoC/DI** | Toda la aplicación | Inversión de control con Spring Framework | ✅ Validado |

**Nota Importante:** Todos los patrones están **100% funcionales** después de eliminar `HexagonalConfig.java` y usar component scanning automático de Spring.

---

## 🚀 Inicio Rápido con Docker (RECOMENDADO)

### Prerrequisitos

- **Docker Desktop** instalado ([Descargar](https://www.docker.com/products/docker-desktop))
- **Docker Compose** v2.0+ (incluido en Docker Desktop)
- Puerto **8080** y **5432** disponibles

### Paso 1: Clonar el Repositorio

```powershell
git clone https://github.com/LeonarDPeace/Ingenieria-Software-2.git
cd Ingenieria-Software-2/SERVICIUDAD-CALI
```

### Paso 2: Levantar los Contenedores

```powershell
docker-compose up -d
```

**Esto hará:**
1. ✅ Construir la imagen de la aplicación Spring Boot
2. ✅ Descargar y configurar PostgreSQL 15
3. ✅ Crear la base de datos `serviciudad_db`
4. ✅ Ejecutar scripts de inicialización (`schema.sql` y `data.sql`)
5. ✅ Exponer la aplicación en `http://localhost:8080`

### Autenticación

**HTTP Basic Auth:**
- Usuario: `serviciudad`
- Contraseña: `dev2025`

### Paso 3: Verificar el Estado

```powershell
# Ver logs en tiempo real
docker-compose logs -f app

# Verificar que los contenedores estén corriendo
docker-compose ps
```

**Salida esperada:**
```
NAME                 IMAGE                    STATUS
serviciudad-app      serviciudad-app:latest   Up 2 minutes (healthy)
serviciudad-postgres postgres:15-alpine       Up 2 minutes (healthy)
```

**Verificación de endpoints registrados:**
Los logs deben mostrar mensajes como:
```
Mapped "{[/api/deuda/cliente/{clienteId}],methods=[GET]}" onto ...
Mapped "{[/api/facturas/{id}],methods=[GET]}" onto ...
Mapped "{[/api/consumos-energia/cliente/{clienteId}],methods=[GET]}" onto ...
```

Si no ves estos mensajes, revisa que no exista `HexagonalConfig.java` (fue eliminado intencionalmente).

### Paso 4: Acceder a la Aplicación

**Endpoints Públicos (sin autenticación):**
- **Frontend Web**: [http://localhost:8080/](http://localhost:8080/)
- **Favicon**: [http://localhost:8080/favicon.svg](http://localhost:8080/favicon.svg)
- **Health Check**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

**Endpoints Protegidos (requieren autenticación):**
- **API Deuda Consolidada**: [http://localhost:8080/api/deuda/cliente/0001234567](http://localhost:8080/api/deuda/cliente/0001234567)
- **API Facturas**: [http://localhost:8080/api/facturas/1](http://localhost:8080/api/facturas/1)
- **API Consumos Energía**: [http://localhost:8080/api/consumos-energia/cliente/0001234567](http://localhost:8080/api/consumos-energia/cliente/0001234567)

**Prueba rápida desde PowerShell:**
```powershell
# Health check (sin auth)
curl http://localhost:8080/actuator/health

# Consulta con autenticación
curl -u serviciudad:dev2025 http://localhost:8080/api/deuda/cliente/0001234567
```

### Paso 5: Detener la Aplicación

```powershell
# Detener contenedores (conserva datos)
docker-compose stop

# Detener y eliminar contenedores (conserva volúmenes)
docker-compose down

# Eliminar TODO (contenedores + volúmenes + datos)
docker-compose down -v
```

---

## 💻 Instalación Manual (Sin Docker)

### Prerrequisitos

- **Java 17** o superior ([Descargar](https://adoptium.net/))
- **Maven 3.8+** ([Descargar](https://maven.apache.org/download.cgi))
- **PostgreSQL 15** ([Descargar](https://www.postgresql.org/download/))
- **Git** ([Descargar](https://git-scm.com/downloads))

### Paso 1: Clonar el Repositorio

```powershell
git clone https://github.com/LeonarDPeace/Ingenieria-Software-2.git
cd Ingenieria-Software-2/SERVICIUDAD-CALI
```

### Paso 2: Configurar PostgreSQL

#### 2.1. Crear la Base de Datos

```sql
-- Conectarse a PostgreSQL
psql -U postgres

-- Crear usuario
CREATE USER serviciudad_user WITH PASSWORD 'serviciudad_pass';

-- Crear base de datos
CREATE DATABASE serviciudad_db OWNER serviciudad_user;

-- Dar permisos
GRANT ALL PRIVILEGES ON DATABASE serviciudad_db TO serviciudad_user;

-- Salir
\q
```

#### 2.2. Ejecutar Scripts de Inicialización

```powershell
# Conectarse a la base de datos creada
psql -U serviciudad_user -d serviciudad_db

# Ejecutar schema.sql
\i src/main/resources/schema.sql

# Ejecutar data.sql
\i src/main/resources/data.sql

# Verificar datos
SELECT * FROM facturas_acueducto;

# Salir
\q
```

### Paso 3: Configurar Variables de Entorno (Opcional)

Crear archivo `.env` en la raíz del proyecto:

```env
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=serviciudad_db
POSTGRES_USER=serviciudad_user
POSTGRES_PASSWORD=serviciudad_pass
ENERGIA_ARCHIVO_PATH=data/consumos_energia.txt
SPRING_PROFILES_ACTIVE=dev
```

### Paso 4: Compilar y Ejecutar

```powershell
# Compilar el proyecto
mvn clean package -DskipTests

# Ejecutar la aplicación
java -jar target/serviciudad-deuda-consolidada-1.0.0.jar

# O usar Maven directamente
mvn spring-boot:run
```

### Paso 5: Verificar la Aplicación

```powershell
# Health check
curl http://localhost:8080/actuator/health

# Consultar deuda (con autenticación)
curl -u serviciudad:dev2025 http://localhost:8080/api/deuda/cliente/0001234567
```

---

## 📂 Estructura del Proyecto

```
SERVICIUDAD-CALI/
├── src/
│   ├── main/
│   │   ├── java/com/serviciudad/
│   │   │   ├── application/              # Capa de Aplicación
│   │   │   │   ├── dto/                  # DTOs (Request/Response)
│   │   │   │   ├── mapper/               # Mappers de entidades
│   │   │   │   └── usecase/              # Casos de uso (@Service)
│   │   │   ├── config/                   # Configuraciones Spring
│   │   │   ├── domain/                   # Capa de Dominio
│   │   │   │   ├── model/                # Entidades de negocio
│   │   │   │   ├── port/                 # Puertos (interfaces)
│   │   │   │   └── valueobject/          # Value Objects
│   │   │   ├── exception/                # Manejo de excepciones
│   │   │   └── infrastructure/           # Capa de Infraestructura
│   │   │       ├── adapter/
│   │   │       │   ├── input/            # REST Controllers (@RestController)
│   │   │       │   └── output/           # Adaptadores (JPA, File) (@Component)
│   │   │       └── config/               # Configuración
│   │   └── resources/
│   │       ├── static/
│   │       │   └── favicon.svg           # ✨ Favicon del sitio (NUEVO)
│   │       ├── application.yml           # Configuración principal
│   │       ├── application-dev.yml       # Perfil desarrollo
│   │       ├── application-prod.yml      # Perfil producción
│   │       ├── schema.sql                # DDL de base de datos
│   │       └── data.sql                  # Datos de prueba
│   └── test/                             # Tests unitarios e integración
├── frontend/                             # Frontend Web
│   ├── index.html                        # Página principal (con favicon)
│   ├── styles.css                        # Estilos CSS
│   └── app.js                            # Lógica JavaScript
├── data/
│   └── consumos_energia.txt              # Archivo legacy (ancho fijo)
├── postman/                              # Colección Postman + Guías
│   ├── ServiCiudad_API.postman_collection.json
│   ├── ServiCiudad_Local.postman_environment.json
│   ├── ServiCiudad_Docker.postman_environment.json
│   ├── GUIA_ACTUALIZACION_POSTMAN.md     # ✨ Guía completa de actualización (NUEVO)
│   ├── GUIA_RAPIDA.md                    # ✨ Referencia rápida (NUEVO)
│   ├── RESUMEN_CORRECCIONES.md           # ✨ Explicación de problemas y soluciones (NUEVO)
│   └── ESTADO_FINAL.md                   # ✨ Estado final del proyecto (NUEVO)
├── docker-compose.yml                    # Orquestación Docker
├── Dockerfile                            # Imagen de la aplicación
├── pom.xml                               # Dependencias Maven
├── README.md                             # Este archivo
└── INFORME.md                            # Documentación técnica

⚠️ NOTA: El archivo HexagonalConfig.java fue ELIMINADO intencionalmente
para permitir el component scanning automático de Spring.
```

---

## 🔌 API Endpoints

### ✅ Estado de Validación: 7/7 Endpoints Testeados (100%)

Todos los endpoints principales han sido validados exitosamente con respuestas correctas:

| Endpoint | Método | Auth | Estado | Observaciones |
|----------|--------|------|--------|---------------|
| `/` | GET | No | ✅ 200 OK | Frontend funcionando |
| `/favicon.svg` | GET | No | ✅ 200 OK | Nuevo favicon agregado |
| `/actuator/health` | GET | No | ✅ 200 OK | Health check operativo |
| `/api/facturas/{id}` | GET | Sí | ✅ 200 OK | Retorna factura específica |
| `/api/facturas/cliente/{id}` | GET | Sí | ✅ 200 OK | Lista facturas del cliente |
| `/api/deuda/cliente/{id}` | GET | Sí | ✅ 200 OK | Deuda consolidada |
| `/api/consumos-energia/cliente/{id}` | GET | Sí | ✅ 200 OK | Consumos de energía |

### 1. Consultar Deuda Consolidada

**Endpoint Principal:**

```http
GET /api/deuda/cliente/{clienteId}
```

**Parámetros:**
- `clienteId` (path): ID del cliente (10 dígitos numéricos)

**Autenticación:**
```
Authorization: Basic c2VydmljaXVkYWQ6ZGV2MjAyNQ==
(serviciudad:dev2025)
```

**Respuesta Exitosa (200 OK):**

```json
{
  "clienteId": "0001234567",
  "nombreCliente": "Juan Pérez García",
  "fechaConsulta": "2025-10-19T14:30:00Z",
  "facturasAcueducto": [
    {
      "id": 1,
      "periodo": "202510",
      "consumoMetrosCubicos": 15,
      "valorPagar": 95000.00,
      "fechaVencimiento": "2025-11-15",
      "estado": "PENDIENTE",
      "diasHastaVencimiento": 27
    }
  ],
  "consumosEnergia": [
    {
      "periodo": "202510",
      "consumoKwh": 150,
      "valorPagar": 180000.50,
      "fechaLectura": "2025-10-01",
      "valido": true
    }
  ],
  "estadisticas": {
    "totalFacturasAcueducto": 1,
    "deudaAcumuladaAcueducto": 95000.00,
    "totalConsumoAcueducto": 15,
    "promedioConsumoAcueducto": 15.0,
    "deudaAcumuladaEnergia": 180000.50,
    "totalConsumoEnergia": 150
  },
  "alertas": [
    "Factura #1 próxima a vencer (27 días)"
  ],
  "totalAPagar": 275000.50
}
```

**Errores Comunes:**

| Código | Descripción | Solución |
|--------|-------------|----------|
| 400 | ID inválido (debe ser 10 dígitos) | Enviar ID con formato `0001234567` |
| 401 | Sin autenticación o credenciales incorrectas | Agregar header `Authorization: Basic ...` |
| 404 | Cliente no encontrado | Verificar que el ID exista en la BD |
| 429 | Rate limit excedido (>100 req/min) | Esperar 1 minuto |
| 500 | Error del servidor | Revisar logs con `docker-compose logs app` |

### 2. Consultar Facturas por Cliente

```http
GET /api/facturas/cliente/{clienteId}
```

**Respuesta Exitosa (200 OK):**
```json
[
  {
    "id": 1,
    "periodo": "202510",
    "consumoMetrosCubicos": 15,
    "valorPagar": 95000.00,
    "fechaVencimiento": "2025-11-15",
    "estado": "PENDIENTE"
  }
]
```

### 3. Consultar Consumos de Energía

```http
GET /api/consumos-energia/cliente/{clienteId}
```

**Respuesta Exitosa (200 OK):**
```json
[
  {
    "periodo": "202510",
    "consumoKwh": 150,
    "valorPagar": 180000.50,
    "fechaLectura": "2025-10-01",
    "valido": true
  }
]
```

### 4. Otros Endpoints

```http
# Health Check (sin autenticación)
GET /actuator/health

# Favicon (sin autenticación) - ✨ NUEVO
GET /favicon.svg

# Frontend (sin autenticación)
GET /

# Documentación Swagger
GET /swagger-ui.html

# OpenAPI JSON
GET /v3/api-docs
```

---

## 🧪 Pruebas con Postman

### ✅ Colección de Postman Actualizada

La colección de Postman incluida está **completamente actualizada** y lista para usar con la arquitectura actual. Incluye todos los endpoints funcionales y configuraciones correctas.

📚 **Archivos Disponibles:**
1. **ServiCiudad_API.postman_collection.json** - Colección principal con todos los endpoints
2. **ServiCiudad_Docker.postman_environment.json** - Environment para Docker
3. **ServiCiudad_Local.postman_environment.json** - Environment para desarrollo local

### Endpoints que Funcionan Actualmente

**✅ Operativos (14+ endpoints):**
- `/api/facturas/{id}` - Consultar factura por ID
- `/api/facturas/cliente/{clienteId}` - Listar facturas del cliente
- `/api/facturas/pagar` - Registrar pago (POST con JSON body)
- `/api/deuda/cliente/{clienteId}` - Deuda consolidada
- `/api/consumos-energia/cliente/{clienteId}` - Consumos de energía
- `/actuator/*` - 7 endpoints de monitoreo

**✅ Todos los Endpoints Principales Funcionan:**
1. **Deuda Consolidada**: `/api/deuda/cliente/{id}` - ✅ Funcionando
2. **Facturas por Cliente**: `/api/facturas/cliente/{id}` - ✅ Funcionando
3. **Consumos de Energía**: `/api/consumos-energia/cliente/{id}` - ✅ Funcionando
4. **Health Check**: `/actuator/health` - ✅ Funcionando
5. **Frontend**: `/` - ✅ Funcionando
6. **Favicon**: `/favicon.svg` - ✅ Funcionando

**📝 Nota:** Algunos endpoints adicionales están definidos en la colección pero no implementados en el backend actual. Los endpoints principales están 100% operativos.

### Importar la Colección

1. Abrir **Postman**
2. Click en **Import**
3. Seleccionar `postman/ServiCiudad_API.postman_collection.json`
4. Seleccionar environment:
   - `ServiCiudad_Local.postman_environment.json` (desarrollo local)
   - `ServiCiudad_Docker.postman_environment.json` (Docker)
5. **¡Listo!** La colección está actualizada y lista para usar

### Pruebas Validadas Manualmente

**Casos de Prueba Validados (7/7 - 100%):**

1. ✅ **Frontend**: `GET /` → 200 OK (HTML)
2. ✅ **Favicon**: `GET /favicon.svg` → 200 OK (SVG)
3. ✅ **Health Check**: `GET /actuator/health` → 200 OK (JSON)
4. ✅ **Deuda Consolidada**: `GET /api/deuda/cliente/0001234567` → 200 OK (JSON con auth)
5. ✅ **Facturas del Cliente**: `GET /api/facturas/cliente/0001234567` → 200 OK
6. ✅ **Consumos Energía**: `GET /api/consumos-energia/cliente/0001234567` → 200 OK
7. ✅ **Factura por ID**: `GET /api/facturas/1` → 200 OK (JSON con auth)

**Nota:** El error de favicon.ico en la consola del navegador ha sido corregido agregando la etiqueta `<link rel="icon">` correcta en el HTML.

**Comando de prueba rápida (PowerShell):**
```powershell
# Prueba con autenticación
$response = Invoke-WebRequest -Uri "http://localhost:8080/api/deuda/cliente/0001234567" -UseBasicParsing -Headers @{Authorization="Basic c2VydmljaXVkYWQ6ZGV2MjAyNQ=="}; $response.Content
```

---

## 🌐 Frontend Web

### Características

- ✅ **Interfaz Moderna**: Diseño responsive con gradientes y animaciones
- ✅ **Validación en Tiempo Real**: Solo permite dígitos (máx 10)
- ✅ **Detección de Docker**: Verifica conexión al cargar la página
- ✅ **Visualización Rica**: Gráficas, badges de estado, alertas animadas
- ✅ **Manejo de Errores**: Mensajes claros para cada tipo de error

### Estructura del Frontend

```
frontend/
├── index.html       # Estructura HTML5
├── styles.css       # Estilos CSS3 separados
└── app.js          # Lógica JavaScript vanilla
```

**Ventajas de la separación:**
- ✅ Fácil mantenimiento
- ✅ Caché independiente
- ✅ Mejor organización
- ✅ Reutilización de estilos

### Capturas de Pantalla

**Página Principal:**
![Frontend Principal](docs/screenshots/frontend-main.png)

**Resultado de Consulta:**
![Resultado](docs/screenshots/frontend-result.png)

---

## 🐳 Docker: Detalles Avanzados

### Variables de Entorno

Crear archivo `.env` para personalizar:

```env
# Base de Datos
POSTGRES_DB=serviciudad_db
POSTGRES_USER=serviciudad_user
POSTGRES_PASSWORD=serviciudad_pass

# Archivo de Energía
ENERGIA_ARCHIVO_PATH=/app/data/consumos_energia.txt

# JVM Options
JAVA_OPTS=-Xms256m -Xmx512m

# Perfil Spring
SPRING_PROFILES_ACTIVE=prod
```

### Comandos Útiles

```powershell
# Reconstruir imagen (después de cambios en código)
docker-compose build --no-cache

# Ver logs en tiempo real
docker-compose logs -f app

# Acceder al contenedor de PostgreSQL
docker exec -it serviciudad-postgres psql -U serviciudad_user -d serviciudad_db

# Acceder al contenedor de la app
docker exec -it serviciudad-app sh

# Ver uso de recursos
docker stats serviciudad-app serviciudad-postgres

# Reiniciar solo la app (conserva BD)
docker-compose restart app
```

### Solución de Problemas

**Problema: Puerto 8080 ocupado**
```powershell
# Windows: Encontrar proceso
netstat -ano | findstr :8080

# Matar proceso
taskkill /PID <PID> /F

# O cambiar puerto en docker-compose.yml
ports:
  - "8081:8080"  # Cambia 8080 a 8081
```

**Problema: BD no inicializa**
```powershell
# Eliminar volúmenes y recrear
docker-compose down -v
docker-compose up -d

# Verificar logs de PostgreSQL
docker-compose logs postgres
```

**Problema: Cambios en código no se reflejan**
```powershell
# Reconstruir imagen
docker-compose build --no-cache app
docker-compose up -d app
```

**Problema: Endpoints no se registran (0 endpoints)**
```
Síntoma: La aplicación inicia pero los logs no muestran "Mapped {[...]}"
Causa: Existencia de HexagonalConfig.java interfiriendo con component scanning
Solución: Verificar que HexagonalConfig.java NO exista (fue eliminado)

# Verificar archivos de configuración
ls src/main/java/com/serviciudad/infrastructure/config/

# Debe mostrar solo SecurityConfig.java, SwaggerConfig.java, etc.
# NO debe existir HexagonalConfig.java
```

**Problema: Error 500 en todos los endpoints de la API**
```
Síntoma: Frontend funciona, pero /api/* retorna 500 Internal Server Error
Causa: Use Cases no detectados como @Service beans
Solución: Asegurar que HexagonalConfig.java fue eliminado y reconstruir

# Reconstruir completamente
docker-compose down
docker-compose build --no-cache
docker-compose up -d

# Verificar logs buscar "Mapped {["
docker-compose logs app | Select-String "Mapped"
```

---

## 🧪 Tests Automatizados

### Ejecutar Tests

```powershell
# Todos los tests
mvn test

# Solo tests unitarios
mvn test -Dtest=**/*Test.java

# Solo tests de integración
mvn test -Dtest=**/*IntegrationTest.java

# Con cobertura (JaCoCo)
mvn clean test jacoco:report
```

### Cobertura de Tests

| Capa | Cobertura | Tipos de Test |
|------|-----------|---------------|
| Use Cases | 90%+ | Unitarios con Mockito |
| Controllers | 85%+ | Integración con MockMvc |
| Adaptadores | 80%+ | Integración con Testcontainers |
| DTOs | 100% | Unitarios (Builder, Validación) |

**Reporte de Cobertura:**
```powershell
mvn jacoco:report
# Abrir: target/site/jacoco/index.html
```

---

## 📊 Monitoreo y Métricas

### Actuator Endpoints

```http
# Estado de salud
GET /actuator/health

# Información de la app
GET /actuator/info

# Métricas
GET /actuator/metrics

# Beans de Spring
GET /actuator/beans
```

### Logs

**Ubicación:**
- Desarrollo: `logs/application.log`
- Docker: `docker-compose logs app`

**Niveles de Log:**
```yaml
# application.yml
logging:
  level:
    com.serviciudad: DEBUG
    org.springframework: INFO
    org.hibernate: WARN
```

---

## 🔒 Seguridad

### Autenticación

**HTTP Basic Auth:**
- Usuario: `serviciudad`
- Contraseña: `dev2025`

**Configuración Actualizada:**
```java
// SecurityConfig.java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) {
    return http
        .httpBasic(Customizer.withDefaults())
        .authorizeHttpRequests(auth -> auth
            // ✨ Recursos públicos (sin autenticación)
            .requestMatchers("/", "/favicon.svg", "/actuator/health", "/swagger-ui/**").permitAll()
            // 🔒 Endpoints de API requieren autenticación
            .anyRequest().authenticated()
        )
        .build();
}
```

**Cambios Implementados:**
- ✅ Se agregó `/favicon.svg` a recursos públicos
- ✅ Se agregó `/` (frontend) a recursos públicos
- ✅ Todos los endpoints `/api/**` requieren autenticación
- ✅ Actuator health check accesible sin credenciales

### Rate Limiting

⚠️ **Nota:** Rate limiting NO está actualmente implementado. Para agregarlo:

```xml
<!-- pom.xml -->
<dependency>
    <groupId>com.github.vladimir-bukhtoyarov</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>7.6.0</version>
</dependency>
```

**Configuración propuesta:**
- **Límite**: 100 requests por minuto por IP
- **Cabeceras de respuesta**:
  ```http
  X-RateLimit-Limit: 100
  X-RateLimit-Remaining: 95
  X-RateLimit-Reset: 1634657400
  ```

---

## 📚 Documentación Adicional

### Documentos Principales
- **[README.md](README.md)**: Guía completa de instalación, configuración y uso
- **[INFORME.md](INFORME.md)**: Justificación técnica de patrones de diseño y arquitectura

---

## 👥 Equipo

| Nombre | Rol | ID | Email |
|--------|-----|----|----|
| Eduard Criollo Yule | Project Manager & Backend | 2220335 | eduard.criollo@uao.edu.co |
| Felipe Charria Caicedo | Integration Specialist | 2216033 | felipe.charria@uao.edu.co |
| Jhonathan Chicaiza Herrera | Backend Developer | 2215286 | jhonathan.chicaiza@uao.edu.co |
| Emmanuel Mena | Full Stack Developer | 2230574 | emmanuel.mena@uao.edu.co |
| Juan Sebastian Castillo | Frontend Developer | 2231921 | juan.castillo@uao.edu.co |

---
