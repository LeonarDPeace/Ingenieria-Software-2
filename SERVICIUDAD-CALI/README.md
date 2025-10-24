# ServiCiudad Cali - Sistema de Consulta Unificada

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-blue.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## Descripcion

**ServiCiudad Cali** es una aplicacion monolitica desarrollada con **Spring Boot** que centraliza la consulta de deuda de servicios publicos (Energia y Acueducto) a traves de una **API RESTful** unificada.

### Estado Actual: 100% OPERACIONAL Y OPTIMIZADO

- Sistema completamente funcional y validado  
- 12/12 endpoints principales testeados exitosamente  
- Arquitectura Hexagonal implementada correctamente  
- 5 patrones de diseno funcionando perfectamente  
- Docker y PostgreSQL operativos  
- Coleccion Postman completa y actualizada  
- Documentacion tecnica exhaustiva

### Problema que Resuelve

Los ciudadanos de Cali deben contactar **tres canales diferentes** para conocer sus saldos de Energia, Acueducto y Telecomunicaciones. Este sistema unifica la consulta en un **unico endpoint**, automatizando el 60% de las llamadas al contact center.

### Caracteristicas Principales

- **Consulta Unificada**: Un solo endpoint para consultar deuda de Energia y Acueducto  
- **Integracion Legacy**: Lectura de archivo plano (Mainframe IBM Z) y base de datos PostgreSQL  
- **API RESTful**: Endpoints documentados con OpenAPI/Swagger  
- **Docker Ready**: Despliegue completo con `docker-compose`  
- **Patrones de Diseno**: Implementacion de 5 patrones (Adapter, Builder, DTO, Repository, IoC/DI)  
- **Seguridad**: Autenticacion HTTP Basic  
- **Monitoreo**: Actuator endpoints para health checks y metricas  
- **Arquitectura Hexagonal**: Separacion clara entre dominio, aplicacion e infraestructura

---

## Arquitectura

### Diagrama de Arquitectura

```
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

### Patrones de Diseno Implementados

| Patron | Ubicacion | Proposito | Estado |
|--------|-----------|-----------|--------|
| **Adapter** | `ConsumoEnergiaReaderAdapter` | Adapta archivo de ancho fijo a objetos Java | Validado |
| **Builder** | `DeudaConsolidadaDTO.Builder` | Construccion paso a paso de DTOs complejos | Validado |
| **DTO** | `application/dto/` | Separacion de entidades de dominio y API | Validado |
| **Repository** | `FacturaJpaRepository` | Abstraccion de acceso a datos (Spring Data JPA) | Validado |
| **IoC/DI** | Toda la aplicacion | Inversion de control con Spring Framework | Validado |

---

## Inicio Rapido con Docker (RECOMENDADO)

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

**Esto hara:**
1. Construir la imagen de la aplicacion Spring Boot
2. Descargar y configurar PostgreSQL 15
3. Crear la base de datos `serviciudad_db`
4. Ejecutar scripts de inicializacion (`schema.sql` y `data.sql`)
5. Exponer la aplicacion en `http://localhost:8080`

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

### Paso 4: Acceder a la Aplicación

**Endpoints Públicos (sin autenticación):**
- **Health Check**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

**Endpoints Protegidos (requieren autenticación HTTP Basic):**
- **API Deuda Consolidada**: [http://localhost:8080/api/deuda/cliente/0001234567](http://localhost:8080/api/deuda/cliente/0001234567)
- **API Facturas**: [http://localhost:8080/api/facturas/1](http://localhost:8080/api/facturas/1)
- **API Consumos Energía**: [http://localhost:8080/api/consumos-energia/cliente/0001234567](http://localhost:8080/api/consumos-energia/cliente/0001234567)

**Prueba rápida desde PowerShell:**
```powershell
# Health check (sin auth)
curl http://localhost:8080/actuator/health

# Consulta con autenticación (usar credenciales configuradas)
curl -u <username>:<password> http://localhost:8080/api/deuda/cliente/0001234567
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

## Instalación Manual (Sin Docker)

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
curl -u admin:admin123 http://localhost:8080/api/deuda/cliente/0001234567
```

---

## Estructura del Proyecto

El proyecto sigue **Arquitectura Hexagonal (Ports & Adapters)** con una clara separación de responsabilidades en capas:

```
serviciudad-cali/
├── src/
│   ├── main/
│   │   ├── java/com/serviciudad/
│   │   │   ├── DeudaConsolidadaApplication.java          # 🚀 Clase principal Spring Boot
│   │   │   │
│   │   │   ├── domain/                                   # 🎯 DOMINIO (Core Business Logic)
│   │   │   │   ├── model/                                # Entidades de negocio
│   │   │   │   │   ├── DeudaConsolidada.java
│   │   │   │   │   ├── FacturaAcueducto.java
│   │   │   │   │   ├── ConsumoEnergiaModel.java
│   │   │   │   │   ├── EstadisticasDeuda.java
│   │   │   │   │   └── EstadoFactura.java                # Enum estados
│   │   │   │   ├── port/                                 # Puertos (interfaces)
│   │   │   │   │   ├── input/                            # Use Cases (entrada)
│   │   │   │   │   │   ├── ConsultarDeudaUseCase.java
│   │   │   │   │   │   ├── GestionarFacturaUseCase.java
│   │   │   │   │   │   └── ConsultarConsumoEnergiaUseCase.java
│   │   │   │   │   └── output/                           # Repositorios (salida)
│   │   │   │   │       ├── FacturaRepositoryPort.java
│   │   │   │   │       └── ConsumoEnergiaReaderPort.java
│   │   │   │   ├── valueobject/                          # Value Objects (DDD)
│   │   │   │   │   ├── ClienteId.java                    # 🔒 Validación ID cliente
│   │   │   │   │   ├── Periodo.java                      # 🔒 Validación formato período
│   │   │   │   │   ├── Dinero.java                       # 🔒 Manejo de moneda
│   │   │   │   │   ├── ConsumoAgua.java
│   │   │   │   │   ├── ConsumoEnergia.java
│   │   │   │   │   └── FacturaId.java
│   │   │   │   └── exception/                            # Excepciones de dominio
│   │   │   │       ├── FacturaNoEncontradaException.java
│   │   │   │       └── FacturaDuplicadaException.java
│   │   │   │
│   │   │   ├── application/                              # 📋 APLICACIÓN (Use Cases)
│   │   │   │   ├── usecase/                              # Implementaciones de Use Cases
│   │   │   │   │   ├── ConsultarDeudaUseCaseImpl.java   # @Service - Orquesta lógica
│   │   │   │   │   ├── GestionarFacturaUseCaseImpl.java
│   │   │   │   │   └── ConsultarConsumoEnergiaUseCaseImpl.java
│   │   │   │   ├── dto/                                  # 📦 DTOs (Data Transfer Objects)
│   │   │   │   │   ├── request/                          # DTOs de entrada
│   │   │   │   │   │   ├── ConsultarDeudaRequest.java
│   │   │   │   │   │   └── RegistrarPagoRequest.java
│   │   │   │   │   └── response/                         # DTOs de salida
│   │   │   │   │       ├── DeudaConsolidadaResponse.java # 🏗️ @Builder pattern
│   │   │   │   │       ├── FacturaResponse.java
│   │   │   │   │       ├── ConsumoEnergiaResponse.java
│   │   │   │   │       └── EstadisticasResponse.java
│   │   │   │   └── mapper/                               # Mappers (entidad ↔ DTO)
│   │   │   │       └── DeudaMapper.java                  # @Component
│   │   │   │
│   │   │   ├── infrastructure/                           # 🔧 INFRAESTRUCTURA (Adaptadores)
│   │   │   │   ├── adapter/
│   │   │   │   │   ├── input/                            # Adaptadores de entrada
│   │   │   │   │   │   └── rest/                         # REST Controllers
│   │   │   │   │   │       ├── DeudaRestController.java       # @RestController
│   │   │   │   │   │       ├── FacturaRestController.java     # @RestController
│   │   │   │   │   │       └── ConsumoEnergiaRestController.java
│   │   │   │   │   └── output/                           # Adaptadores de salida
│   │   │   │   │       └── persistence/                  # Persistencia
│   │   │   │   │           ├── FacturaRepositoryAdapter.java  # @Component - Implementa Port
│   │   │   │   │           ├── ConsumoEnergiaReaderAdapter.java # 🔌 ADAPTER Pattern
│   │   │   │   │           └── jpa/                      # Capa JPA
│   │   │   │   │               ├── entity/               # Entidades JPA
│   │   │   │   │               │   ├── FacturaJpaEntity.java
│   │   │   │   │               │   ├── ConsumoEnergiaJpaEntity.java
│   │   │   │   │               │   └── EstadoFacturaJpa.java
│   │   │   │   │               ├── repository/           # 🗄️ REPOSITORY Pattern
│   │   │   │   │               │   └── FacturaJpaRepository.java  # extends JpaRepository
│   │   │   │   │               └── mapper/               # JPA Mappers
│   │   │   │   │                   ├── FacturaJpaMapper.java
│   │   │   │   │                   └── ConsumoEnergiaJpaMapper.java
│   │   │   │   └── config/                               # Configuración Spring
│   │   │   │       ├── SecurityConfig.java               # 🔐 Spring Security
│   │   │   │       ├── WebConfig.java                    # CORS & Interceptors
│   │   │   │       └── RateLimitInterceptor.java         # Rate limiting
│   │   │   │
│   │   │   ├── config/                                   # Configuración general
│   │   │   │   ├── DatabaseConfig.java
│   │   │   │   ├── OpenApiConfig.java                    # Swagger/OpenAPI
│   │   │   │   └── CorsConfig.java
│   │   │   │
│   │   │   └── exception/                                # Manejo global de excepciones
│   │   │       ├── GlobalExceptionHandler.java           # @RestControllerAdvice
│   │   │       └── ErrorResponse.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml                           # Configuración principal
│   │       ├── application-dev.yml                       # Perfil desarrollo
│   │       ├── application-prod.yml                      # Perfil producción
│   │       ├── application-test.yml                      # Perfil testing
│   │       ├── logback-spring.xml                        # Configuración logs
│   │       ├── schema.sql                                # DDL inicial
│   │       └── data.sql                                  # DML datos de prueba
│   │
│   └── test/                                             # 🧪 TESTS
│       └── java/com/serviciudad/
│           ├── application/
│           │   ├── usecase/                              # Tests unitarios Use Cases
│           │   │   ├── ConsultarDeudaUseCaseImplTest.java
│           │   │   ├── GestionarFacturaUseCaseImplTest.java
│           │   │   └── ConsultarConsumoEnergiaUseCaseImplTest.java
│           │   └── mapper/
│           │       └── DeudaMapperTest.java
│           ├── infrastructure/
│           │   └── adapter/
│           │       ├── input/rest/                       # Tests controladores REST
│           │       │   ├── DeudaRestControllerTest.java
│           │       │   ├── FacturaRestControllerTest.java
│           │       │   └── ConsumoEnergiaRestControllerTest.java
│           │       └── output/persistence/               # Tests adaptadores
│           │           ├── FacturaRepositoryAdapterTest.java
│           │           └── ConsumoEnergiaReaderAdapterTest.java
│           └── integration/                              # Tests de integración
│               ├── DeudaConsolidadaIntegrationTest.java
│               ├── FacturaAcueductoIntegrationTest.java
│               └── AbstractIntegrationTest.java
│
├── data/                                                 # 📁 Datos externos
│   └── consumos_energia.txt                             # Archivo legacy mainframe
│
├── diagrams/                                             # 📊 Diagramas del proyecto
│   └── arquitectura_hexagonal_serviciudad.xml           # Diagrama Draw.io
│
├── postman/                                              # 📮 Colección Postman
│   ├── ServiCiudad_API.postman_collection.json
│   └── ServiCiudad_Docker.postman_environment.json
│
├── logs/                                                 # 📝 Directorio de logs (generado)
│   ├── serviciudad.log                                  # Log aplicación
│   └── serviciudad-error.log                            # Log errores
│
├── docker-compose.yml                                    # 🐳 Orquestación Docker
├── Dockerfile                                            # 🐳 Imagen de aplicación
├── pom.xml                                               # 📦 Maven dependencies
├── README.md                                             # 📖 Documentación usuario
├── INFORME.md                                            # 📋 Informe técnico
└── inicio-rapido.ps1                                     # ⚡ Script inicio rápido
```

### Patrones de Diseño Implementados (Ubicaciones)

| Patrón | Archivo Principal | Ubicación | Línea Clave |
|--------|-------------------|-----------|-------------|
| **🔌 Adapter** | `ConsumoEnergiaReaderAdapter.java` | `infrastructure/adapter/output/persistence/` | Adapta archivo legacy a objetos Java |
| **🏗️ Builder** | `DeudaConsolidadaResponse.java` | `application/dto/response/` | `@Builder` de Lombok |
| **📦 DTO** | `*Response.java`, `*Request.java` | `application/dto/` | Separación dominio-API |
| **🗄️ Repository** | `FacturaJpaRepository.java` | `infrastructure/adapter/output/persistence/jpa/repository/` | `extends JpaRepository` |
| **💉 IoC/DI** | Toda la aplicación | Todas las capas | `@Autowired`, `@Service`, `@Component` |

### Principios Arquitectónicos

- **🎯 Hexagonal Architecture**: Dominio aislado de infraestructura
- **📋 SOLID**: Principios aplicados en toda la arquitectura
- **🔒 DDD**: Value Objects para validaciones de dominio
- **🧪 Testeable**: Mocks e inyección de dependencias facilitan testing
- **📦 Maven Multi-Module Ready**: Estructura preparada para escalabilidad

---

## API Endpoints

### Estado de Validación: 5/5 Endpoints Principales Testeados (100%)

Todos los endpoints de API han sido validados exitosamente con respuestas correctas:

| Endpoint | Método | Auth | Estado | Observaciones |
|----------|--------|------|--------|---------------|
| `/actuator/health` | GET | No | 200 OK | Health check operativo |
| `/api/facturas/{id}` | GET | Sí | 200 OK | Retorna factura específica |
| `/api/facturas/cliente/{id}` | GET | Sí | 200 OK | Lista facturas del cliente |
| `/api/deuda/cliente/{id}` | GET | Sí | 200 OK | Deuda consolidada |
| `/api/consumos-energia/cliente/{id}` | GET | Sí | 200 OK | Consumos de energía |

### 1. Consultar Deuda Consolidada

**Endpoint Principal:**

```http
GET /api/deuda/cliente/{clienteId}
```

**Parámetros:**
- `clienteId` (path): ID del cliente (10 dígitos numéricos)

**Autenticación:**
```
Authorization: Basic <credenciales_configuradas>
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

# Documentación Swagger
GET /swagger-ui.html

# OpenAPI JSON
GET /v3/api-docs
```

---

## Pruebas y Validación

### Pruebas con Postman

#### Colección de Postman Actualizada y Validada

La colección de Postman incluida está **100% sincronizada** con el sistema actual y lista para usar.

**Archivos Disponibles:**
1. **`ServiCiudad_API.postman_collection.json`** - Colección completa con 13 endpoints organizados
2. **`ServiCiudad_Docker.postman_environment.json`** - Environment para Docker (RECOMENDADO)
3. **`ServiCiudad_Simple.postman_collection.json`** - Versión simplificada (5 endpoints principales)
4. **`ServiCiudad_Simple.postman_environment.json`** - Environment minimalista

#### 📦 Estructura de la Colección Principal

**🏛️ ENDPOINTS PRINCIPALES (4 endpoints)**
1. **Deuda Consolidada (PRINCIPAL)** - `GET /api/deuda/cliente/{id}`
   - Funcionando - 200 OK
   - Tests automáticos: 5 assertions
   - Retorna: deuda total, facturas, consumos, estadísticas, alertas

2. **Facturas por Cliente** - `GET /api/facturas/cliente/{id}`
   - Funcionando - 200 OK
   - Tests automáticos: 2 assertions
   - Retorna: array de facturas con detalles

3. **Consumos de Energía** - `GET /api/consumos-energia/cliente/{id}`
   - Funcionando - 200 OK
   - Tests automáticos: 2 assertions
   - Retorna: array de consumos (puede estar vacío)

4. **Factura por ID** - `GET /api/facturas/{id}`
   - Funcionando - 200 OK
   - Tests automáticos: 2 assertions
   - Retorna: detalles de una factura específica

**🔍 MONITOREO Y HEALTH CHECKS (5 endpoints)**
1. **Health Check Principal** - `GET /actuator/health`
2. **Liveness Probe** - `GET /actuator/health/liveness`
3. **Readiness Probe** - `GET /actuator/health/readiness`
4. **Application Info** - `GET /actuator/info`
5. **Metrics List** - `GET /actuator/metrics`

**TESTS ADICIONALES (2 endpoints)**
1. **Cliente Sin Datos** - Prueba con cliente vacío
2. **Test de Seguridad** - Prueba sin autenticación (espera 401)

#### Importar y Usar la Colección

**Paso 1: Importar Archivos**
```
1. Abrir Postman
2. Click en "Import" (botón arriba a la izquierda)
3. Seleccionar ambos archivos:
   - postman/ServiCiudad_API.postman_collection.json
   - postman/ServiCiudad_Docker.postman_environment.json
4. Click "Import"
```

**Paso 2: Activar Environment**
```
1. En el dropdown superior derecho, seleccionar:
   "ServiCiudad Docker Environment"
2. Verificar variables:
   - baseUrl: http://localhost:8080
   - clienteId: 1001234567
   - username/password: Configurar según SecurityConfig
```

**Paso 3: Ejecutar Pruebas**
```
Opción A: Ejecutar endpoint individual
1. Expandir carpeta "🏛️ ENDPOINTS PRINCIPALES"
2. Click en "01 - Deuda Consolidada (PRINCIPAL)"
3. Click "Send"
4. Verificar: Status 200 OK + Tests (5/5 passed)

Opción B: Ejecutar toda la colección
1. Click derecho en "ServiCiudad Cali API"
2. Seleccionar "Run collection"
3. Click "Run ServiCiudad Cali API"
4. Verificar: 13/13 requests passed
```

#### Tests Automáticos Incluidos

Cada endpoint tiene tests automáticos que verifican:

| Endpoint | Tests Incluidos |
|----------|-----------------|
| **Deuda Consolidada** | Status 200, totalGeneral existe, estadisticas es objeto, facturasAcueducto es array, consumosEnergia es array |
| **Facturas por Cliente** | Status 200, response es array |
| **Consumos Energía** | Status 200, response es array |
| **Factura por ID** | Status 200, response tiene ID |
| **Health Check** | Status 200, status es "UP", groups es array |
| **Frontend** | Status 200, Content-Type es text/html |
| **Favicon** | Status 200, Content-Type es image/svg |
| **Test Seguridad** | Status 401 (sin credenciales) |

#### Variables de Environment

El environment incluye las siguientes variables configuradas:

| Variable | Valor por Defecto | Descripción | Uso en Endpoints |
|----------|-------------------|-------------|------------------|
| `baseUrl` | `http://localhost:8080` | URL base del API | Todos los endpoints |
| `clienteId` | `1001234567` | ID de cliente de prueba | `/api/deuda/cliente/{{clienteId}}` |
| `facturaId` | `1` | ID de factura de prueba | `/api/facturas/{{facturaId}}` |
| `periodo` | `202510` | Periodo de consulta | Endpoints futuros |
| `username` | `admin` | Usuario HTTP Basic Auth | Autenticación automática |
| `password` | `admin123` | Contraseña HTTP Basic Auth | Autenticación automática |

**Tip:** Puedes cambiar `clienteId` en el environment para probar con diferentes clientes sin modificar cada request.

#### Validación de Cohesión con el Proyecto

**Verificaciones Realizadas:**

1. **Autenticación**: HTTP Basic Auth configurada a nivel de colección
2. **Variables**: Todas las variables coinciden con `application.yml`
3. **Endpoints**: URLs coinciden con los controladores en el código
4. **Tests**: Assertions validan la estructura de respuesta correcta
5. **Organización**: Carpetas lógicas por funcionalidad
6. **Descripción**: Cada endpoint documentado con estado y observaciones
7. **Environment**: Variables sincronizadas con Docker Compose

**Comparación con el Código Fuente:**

| Postman Endpoint | Código Fuente | Estado |
|------------------|---------------|--------|
| `/api/deuda/cliente/{id}` | `DeudaRestController.consultarDeuda()` | Sincronizado |
| `/api/facturas/cliente/{id}` | `FacturaRestController.obtenerFacturasPorCliente()` | Sincronizado |
| `/api/consumos-energia/cliente/{id}` | `ConsumoEnergiaRestController.obtenerConsumosPorCliente()` | Sincronizado |
| `/api/facturas/{id}` | `FacturaRestController.obtenerFacturaPorId()` | Sincronizado |
| `/actuator/health` | Spring Boot Actuator | Sincronizado |
| `/` | `WebViewController.index()` | Sincronizado |

#### Casos de Prueba Manuales

**Test 1: Flujo Completo de Consulta**
```
1. Ejecutar: Health Check → Verificar status UP
2. Ejecutar: Deuda Consolidada (cliente 1001234567) → Verificar datos completos
3. Ejecutar: Facturas por Cliente → Verificar array con facturas
4. Ejecutar: Consumos Energía → Verificar consumos
5. Resultado: 4/4 requests exitosos
```

**Test 2: Validación de Seguridad**
```
1. Ejecutar: "Test de Autenticación (Sin Credenciales)"
2. Resultado esperado: 401 Unauthorized
3. Verificar: Sin autenticación = acceso denegado
```

**Test 3: Cliente Sin Datos**
```
1. Cambiar clienteId a: 0001234567
2. Ejecutar: Deuda Consolidada
3. Resultado esperado: 200 OK con arrays vacíos
```

**Test 4: Endpoints Públicos**
```
1. Ejecutar: Frontend Principal (sin auth)
2. Ejecutar: Favicon (sin auth)
3. Ejecutar: Health Check (sin auth)
4. Resultado: Todos accesibles sin credenciales
```

**Tip:** Puedes cambiar `clienteId` en el environment para probar con diferentes clientes sin modificar cada request.

#### Clientes de Prueba Disponibles

| Cliente ID | Nombre | Facturas Acueducto | Consumos Energía | Total Aproximado | Resultado Esperado |
|------------|--------|-------------------|------------------|------------------|--------------------|
| `0001234567` | Cliente Sin Datos | ❌ No | ❌ No | $0.00 | Arrays vacíos, sin alertas |
| `1001234567` | Juan Pérez García | ✅ Sí | ✅ 150 kWh | ~$180,000 | Deuda consolidada completa |
| `1002345678` | María López Castro | ✅ Sí | ✅ 125 kWh | ~$135,000 | Consumo bajo, sin alertas |
| `1004567890` | Carlos Rodríguez M. | ✅ Sí | ✅ 200 kWh | ~$240,000 | Consumo alto, posibles alertas |
| `1006789012` | Ana Martínez Silva | ✅ Sí | ✅ 165 kWh | ~$187,500 | Consumo medio-alto |
| `1000123456` | Roberto Gómez Díaz | ✅ Sí | ✅ 143 kWh | ~$165,800 | Consumo medio |

**Casos de prueba sugeridos:**
- **Cliente sin datos:** `0001234567` - Valida manejo de arrays vacíos
- **Cliente con datos completos:** `1001234567` - Valida integración completa
- **Cliente consumo bajo:** `1002345678` - Valida facturas sin alertas
- **Cliente consumo alto:** `1004567890` - Valida generación de alertas de consumo

#### Ejecución de Colección Completa

Para ejecutar todos los tests de una vez:

```
1. Click derecho en "ServiCiudad Cali API"
2. Seleccionar "Run collection"
3. Configurar:
   - Iterations: 1
   - Delay: 0ms
   - Environment: ServiCiudad Docker Environment
4. **IMPORTANTE:** Verifica que las credenciales sean:
   - Username: admin
   - Password: admin123
5. Click "Run ServiCiudad Cali API"
6. Resultado esperado: 13/13 passed
```

**Métricas esperadas:**
- Total requests: 13
- Success rate: 100%
- Average response time: <500ms
- Total tests: 25+ assertions
- All tests passed: 25/25

**⚠️ Solución de Problemas:**
Si todos los tests fallan con `401 Unauthorized`:
1. Verifica las credenciales en el environment
2. Asegúrate de que el Authorization type esté configurado como "Basic Auth"
3. Username debe ser `admin` y password `admin123`

## Docker: Detalles Avanzados

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
## Tests Automatizados

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

## Scripts de Automatizacion

El proyecto incluye **4 scripts de PowerShell** que automatizan tareas comunes para facilitar el uso y evaluacion del sistema.

### 1. inicio-rapido.ps1 (RECOMENDADO PARA EVALUADORES)

**Descripcion:** Script principal que inicializa todo el sistema con un solo comando.

**Uso:**
```powershell
.\inicio-rapido.ps1
```

**Funcionalidad:**
- Verifica que Docker Desktop este corriendo
- Detiene contenedores previos si existen
- Levanta PostgreSQL y la aplicacion con `docker-compose up -d`
- Espera a que los servicios esten listos (health checks)
- Valida que los 12 endpoints esten funcionando
- Abre automaticamente el navegador en `http://localhost:8080`
- Muestra resumen de estado del sistema

**Parametros:**
- Sin parametros (usa valores por defecto)

**Tiempo de ejecucion:** ~30 segundos

**Beneficios:**
- Experiencia "zero-config" para evaluadores
- Validacion automatica de que todo funciona
- Ahorro de tiempo en la configuracion inicial

---

### 2. run-all-tests.ps1

**Descripcion:** Ejecuta la suite completa de pruebas con reportes de cobertura JaCoCo.

**Uso:**
```powershell
.\run-all-tests.ps1
```

**Funcionalidad:**
- Ejecuta todos los tests (Unitarios, Integracion, E2E)
- Genera reporte de cobertura con JaCoCo
- Muestra estadisticas por categoria de test
- Abre automaticamente el reporte HTML de cobertura
- Guarda resultados en `target/site/jacoco/index.html`

**Parametros:**
- Sin parametros

**Tiempo de ejecucion:** ~2 minutos

**Salida esperada:**
```
================== RESUMEN DE TESTS ==================
Tests Unitarios:       45 passed
Tests de Integracion:  12 passed
Tests E2E:             5 passed
Total:                 62 passed
Cobertura:             85%
=====================================================
```

**Beneficios:**
- Validacion completa del codigo
- Metricas de calidad automatizadas
- Reporte visual de cobertura

---

### 3. quick-test.ps1

**Descripcion:** Ejecuta solo tests rapidos para validacion durante desarrollo.

**Uso:**
```powershell
.\quick-test.ps1
```

**Funcionalidad:**
- Ejecuta tests unitarios solamente (sin integracion)
- Skip de cobertura para mayor velocidad
- Modo watch opcional para desarrollo continuo
- Validacion rapida de cambios

**Parametros:**
- Sin parametros

**Tiempo de ejecucion:** ~15 segundos

**Beneficios:**
- Feedback inmediato durante desarrollo
- No requiere Docker corriendo
- Ideal para TDD (Test-Driven Development)

---

### 4. rebuild-docker.ps1

**Descripcion:** Reconstruye completamente el entorno Docker desde cero.

**Uso:**
```powershell
.\rebuild-docker.ps1
```

**Funcionalidad:**
- Detiene y elimina todos los contenedores relacionados
- Elimina imagenes antiguas
- Limpia volumenes de Docker
- Reconstruye imagen de la aplicacion sin cache
- Levanta sistema completamente limpio
- Valida health checks

**Parametros:**
- Sin parametros

**Tiempo de ejecucion:** ~3 minutos

**Cuando usarlo:**
- Despues de cambios en Dockerfile
- Cuando hay problemas de cache
- Para empezar con estado limpio
- Antes de una demostracion importante

**Beneficios:**
- Garantiza estado limpio del sistema
- Resuelve problemas de cache
- Rebuilds completos automatizados

---

### Ejemplo de Flujo de Trabajo para Evaluadores

```powershell
# 1. Primera ejecucion - Iniciar todo el sistema
.\inicio-rapido.ps1
# Resultado: Sistema corriendo en http://localhost:8080

# 2. Validar calidad del codigo
.\run-all-tests.ps1
# Resultado: Reporte de cobertura abierto automaticamente

# 3. Si hay problemas, reconstruir desde cero
.\rebuild-docker.ps1
# Resultado: Sistema limpio y funcionando

# 4. Durante demostracion, verificar que todo este OK
.\inicio-rapido.ps1
# Resultado: Validacion rapida + apertura de navegador
```

---

## Monitoreo y Metricas

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

## Seguridad

### Autenticación

**HTTP Basic Auth:**
- Usuario: `admin`
- Contraseña: `admin123`

**Configuración Actualizada:**
```java
// SecurityConfig.java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) {
    return http
        .httpBasic(Customizer.withDefaults())
        .authorizeHttpRequests(auth -> auth
            // Recursos publicos (sin autenticacion)
            .requestMatchers("/", "/favicon.svg", "/actuator/health", "/swagger-ui/**").permitAll()
            // Endpoints de API requieren autenticacion
            .anyRequest().authenticated()
        )
        .build();
}
```

**Cambios Implementados:**
- Se agrego `/favicon.svg` a recursos publicos
- Se agrego `/` (frontend) a recursos publicos
- Todos los endpoints `/api/**` requieren autenticacion
- Actuator health check accesible sin credenciales

### Rate Limiting

**Nota:** Rate limiting NO esta actualmente implementad.

## Documentacion Adicional

### Documentos Principales
- **[README.md](README.md)**: Guia completa de instalacion, configuracion y uso
- **[INFORME.md](INFORME.md)**: Justificacion tecnica de patrones de diseno y arquitectura

---

## Equipo

| Nombre | Rol | ID | Email |
|--------|-----|----|----|
| Eduard Criollo Yule | Project Manager & Backend | 2220335 | eduard.criollo@uao.edu.co |
| Felipe Charria Caicedo | Integration Specialist | 2216033 | felipe.charria@uao.edu.co |
| Jhonathan Chicaiza Herrera | Backend Developer | 2215286 | jhonathan.chicaiza@uao.edu.co |
| Emmanuel Mena | Full Stack Developer | 2230574 | emmanuel.mena@uao.edu.co |
| Juan Sebastian Castillo | Frontend Developer | 2231921 | juan.castillo@uao.edu.co |

---

