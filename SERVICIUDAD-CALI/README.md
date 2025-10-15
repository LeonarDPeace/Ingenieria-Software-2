# ServiCiudad Cali - Sistema de Consulta Unificada

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## Descripción

**ServiCiudad Cali** es una API RESTful monolítica que centraliza la consulta de deudas de servicios públicos (Energía y Acueducto) para la ciudad de Cali. El sistema integra sistemas legacy (archivos planos formato COBOL) con tecnologías modernas para proporcionar una experiencia unificada a los ciudadanos.

### Objetivo

Proporcionar un **endpoint único** para que los ciudadanos consulten su deuda consolidada de servicios públicos, eliminando la necesidad de acceder a múltiples sistemas y simplificando el proceso de pago.

---

## Arquitectura

- **Tipo:** Monolito modular
- **Framework:** Spring Boot 3.2.0
- **Lenguaje:** Java 17 LTS
- **Base de Datos:** PostgreSQL 15
- **Build Tool:** Maven 3.9+
- **Patrones de Diseño:**
  - **Adapter Pattern** - Integración con archivo legacy
  - **Repository Pattern** - Acceso a datos con Spring Data JPA
  - **Builder Pattern** - Construcción de DTOs complejos
  - **DTO Pattern** - Separación de capas
  - **IoC/DI Pattern** - Inversión de Control con Spring

---

## Estructura del Proyecto

```
SERVICIUDAD-CALI/
├── data/                          # Archivos de datos legacy
│   └── consumos_energia.txt
├── postman/                       # Colecciones de prueba
├── src/
│   ├── main/
│   │   ├── java/com/serviciudad/
│   │   │   ├── adapter/           # Patrón Adapter
│   │   │   ├── controller/        # REST Controllers
│   │   │   ├── domain/            # Entidades de dominio
│   │   │   ├── dto/               # Data Transfer Objects
│   │   │   ├── repository/        # Patrón Repository
│   │   │   ├── service/           # Lógica de negocio
│   │   │   ├── config/            # Configuración
│   │   │   └── exception/         # Manejo de excepciones
│   │   └── resources/
│   │       ├── application.yml    # Configuración principal
│   │       ├── schema.sql         # Schema de base de datos
│   │       └── data.sql           # Datos iniciales
│   └── test/                      # Tests unitarios e integración
├── target/                        # Artefactos compilados
├── .gitignore
├── pom.xml
├── README.md
└── INFORME.md                     # Justificación técnica
```

---

## Inicio Rápido

### Requisitos Previos

- **Java 17 o superior** ([Descargar OpenJDK](https://adoptium.net/))
- **Maven 3.9+** ([Descargar Maven](https://maven.apache.org/download.cgi))
- **PostgreSQL 15** ([Descargar PostgreSQL](https://www.postgresql.org/download/))
- **Docker y Docker Compose** (opcional, recomendado)

### Opción 1: Ejecución con Docker (Recomendado)

```bash
# Clonar repositorio
git clone https://github.com/LeonarDPeace/Ingenieria-Software-2.git
cd Ingenieria-Software-2/SERVICIUDAD-CALI

# Levantar servicios con Docker Compose
docker-compose up -d

# Verificar que la aplicación esté corriendo
curl http://localhost:8080/actuator/health
```

### Opción 2: Ejecución Local

#### 1. Configurar Base de Datos

```sql
-- Crear base de datos
CREATE DATABASE serviciudad_db;

-- Crear usuario
CREATE USER serviciudad_user WITH PASSWORD 'serviciudad_pass';
GRANT ALL PRIVILEGES ON DATABASE serviciudad_db TO serviciudad_user;
```

#### 2. Compilar el Proyecto

```bash
# Compilar y ejecutar tests
mvn clean package

# Saltar tests (no recomendado)
mvn clean package -DskipTests
```

#### 3. Ejecutar la Aplicación

```bash
# Usando Maven
mvn spring-boot:run

# O ejecutando el JAR generado
java -jar target/serviciudad-cali-1.0.0.jar
```

#### 4. Verificar Instalación

```bash
# Health check
curl http://localhost:8080/actuator/health

# Consultar deuda consolidada (ejemplo)
curl http://localhost:8080/api/v1/clientes/0001234567/deuda-consolidada
```

---

## Endpoints REST Completos

### Tabla de Endpoints - Facturas de Acueducto

| Metodo | Ruta | Descripcion | Auth |
|--------|------|-------------|------|
| GET | `/api/facturas` | Listar todas las facturas | No |
| GET | `/api/facturas/{id}` | Obtener factura por ID | No |
| POST | `/api/facturas` | Crear nueva factura | No |
| PUT | `/api/facturas/{id}` | Actualizar factura existente | No |
| DELETE | `/api/facturas/{id}` | Eliminar factura | No |
| GET | `/api/facturas/cliente/{idCliente}` | Facturas de un cliente | No |
| GET | `/api/facturas/cliente/{id}/deuda-consolidada` | Deuda total (Acueducto + Energia) | No |
| GET | `/api/facturas/periodo/{periodo}` | Facturas por periodo (YYYYMM) | No |
| GET | `/api/facturas/vencidas` | Obtener facturas vencidas | No |
| GET | `/api/facturas/cliente/{id}/deuda-total` | Calcular deuda total cliente | No |
| GET | `/api/facturas/cliente/{id}/pendientes` | Contar facturas pendientes | No |
| POST | `/api/facturas/{id}/pagar` | Registrar pago de factura | No |

### Tabla de Endpoints - Consumos de Energia

| Metodo | Ruta | Descripcion | Auth |
|--------|------|-------------|------|
| GET | `/api/consumos-energia` | Leer todos los consumos del archivo | No |
| GET | `/api/consumos-energia/cliente/{id}` | Consumos de un cliente especifico | No |
| GET | `/api/consumos-energia/periodo/{periodo}` | Consumos por periodo (YYYYMM) | No |
| GET | `/api/consumos-energia/cliente/{id}/periodo/{p}` | Consumo especifico cliente+periodo | No |
| GET | `/api/consumos-energia/validar-archivo` | Validar formato archivo COBOL | No |
| GET | `/api/consumos-energia/count` | Contar total de registros | No |

### Ejemplos de Uso con curl

#### 1. Crear Factura de Acueducto

```bash
curl -X POST http://localhost:8080/api/facturas \
  -H "Content-Type: application/json" \
  -d '{
    "idCliente": "0006789012",
    "nombreCliente": "Pedro Pablo Sanchez",
    "periodo": "202510",
    "consumoMetrosCubicos": 18,
    "valorPagar": 108000.00,
    "fechaVencimiento": "2025-10-25"
  }'
```

**Response 201 Created:**
```json
{
  "id": 21,
  "idCliente": "0006789012",
  "nombreCliente": "Pedro Pablo Sanchez",
  "periodo": "202510",
  "consumoMetrosCubicos": 18,
  "valorPagar": 108000.00,
  "fechaEmision": "2025-10-15",
  "fechaVencimiento": "2025-10-25",
  "estado": "PENDIENTE",
  "vencida": false,
  "pagada": false,
  "diasHastaVencimiento": 10,
  "mensaje": "Factura pendiente de pago"
}
```

#### 2. Consultar Deuda Consolidada (Acueducto + Energia)

```bash
curl http://localhost:8080/api/facturas/cliente/0001234567/deuda-consolidada
```

**Response 200 OK:**
```json
{
  "clienteId": "0001234567",
  "nombreCliente": "Juan Carlos Perez",
  "fechaConsulta": "2025-10-15T16:30:45",
  "facturasAcueducto": [
    {
      "id": 1,
      "periodo": "202510",
      "consumoMetrosCubicos": 18,
      "valorPagar": 108000.00,
      "estado": "PENDIENTE",
      "fechaVencimiento": "2025-10-25",
      "diasHastaVencimiento": 10
    }
  ],
  "consumosEnergia": [
    {
      "periodo": "202510",
      "consumoKwh": 1500,
      "valorPagar": 180000.50,
      "fechaLectura": "2025-10-01"
    }
  ],
  "estadisticas": {
    "totalFacturasAcueducto": 1,
    "totalConsumoAcueducto": 18,
    "deudaAcumuladaAcueducto": 108000.00,
    "promedioConsumoAcueducto": 18.0,
    "totalConsumoEnergia": 1500,
    "deudaAcumuladaEnergia": 180000.50
  },
  "alertas": [
    "VENCIMIENTO_PROXIMO: Tiene 1 factura(s) proximas a vencer"
  ],
  "totalAPagar": 288000.50
}
```

#### 3. Actualizar Factura

```bash
curl -X PUT http://localhost:8080/api/facturas/1 \
  -H "Content-Type: application/json" \
  -d '{
    "consumoMetrosCubicos": 20,
    "valorPagar": 120000.00,
    "fechaVencimiento": "2025-10-30"
  }'
```

#### 4. Registrar Pago de Factura

```bash
curl -X POST http://localhost:8080/api/facturas/1/pagar
```

**Response 200 OK:**
```json
{
  "id": 1,
  "estado": "PAGADA",
  "fechaPago": "2025-10-15",
  "mensaje": "Factura pagada exitosamente"
}
```

#### 5. Listar Consumos de Energia (Archivo Legacy)

```bash
curl http://localhost:8080/api/consumos-energia
```

**Response 200 OK:**
```json
[
  {
    "idCliente": "0001234567",
    "nombreCliente": "Juan Carlos Perez",
    "periodo": "202510",
    "consumoKwh": 1500,
    "valorPagar": 180000.50,
    "fechaLectura": "2025-10-01",
    "lineaOriginal": "00012345671500000018000050202510...",
    "valido": true
  }
]
```

#### 6. Validar Formato Archivo COBOL

```bash
curl http://localhost:8080/api/consumos-energia/validar-archivo
```

**Response 200 OK:**
```json
{
  "archivoValido": true,
  "totalRegistros": 10,
  "registrosValidos": 10,
  "registrosInvalidos": 0,
  "errores": []
}
```

#### 7. Obtener Facturas Vencidas

```bash
curl http://localhost:8080/api/facturas/vencidas
```

#### 8. Eliminar Factura

```bash
curl -X DELETE http://localhost:8080/api/facturas/21
```

**Response 204 No Content**

### Documentacion Interactiva (Swagger UI)

Una vez iniciada la aplicacion, accede a:

```
http://localhost:8080/swagger-ui.html
```

Funcionalidades:
- Explorar todos los endpoints documentados
- Ejecutar requests directamente desde el navegador
- Ver esquemas JSON de Request/Response
- Validaciones y ejemplos integrados

---

## Deployment

### Opcion 1: Docker Compose (Produccion Recomendada)

#### Paso 1: Configurar Variables de Entorno

Copiar y editar `.env.example`:

```bash
cp .env.example .env
```

Contenido `.env`:

```env
# PostgreSQL
POSTGRES_DB=serviciudad_db
POSTGRES_USER=serviciudad_user
POSTGRES_PASSWORD=serviciudad_pass_2025

# Spring Boot
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080

# JVM
JAVA_OPTS=-Xms512m -Xmx1024m -XX:+UseG1GC

# Archivo Legacy
ENERGIA_ARCHIVO_PATH=./data/consumos_energia.txt
```

#### Paso 2: Levantar Servicios

```bash
# Construir y levantar
docker-compose up -d

# Ver logs
docker-compose logs -f

# Verificar health
curl http://localhost:8080/actuator/health
```

#### Comandos Utiles Docker

```bash
# Detener servicios
docker-compose down

# Rebuild despues de cambios
docker-compose up -d --build

# Ver logs especificos
docker-compose logs -f app
docker-compose logs -f postgres

# Acceder a contenedor
docker-compose exec app sh
docker-compose exec postgres psql -U serviciudad_user -d serviciudad_db
```

### Opcion 2: Deployment Local

#### 1. Configurar PostgreSQL

```sql
CREATE DATABASE serviciudad_db;
CREATE USER serviciudad_user WITH PASSWORD 'serviciudad_pass';
GRANT ALL PRIVILEGES ON DATABASE serviciudad_db TO serviciudad_user;
```

#### 2. Configurar application.yml

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/serviciudad_db
    username: serviciudad_user
    password: serviciudad_pass
```

#### 3. Compilar y Ejecutar

```bash
# Compilar
mvn clean package

# Ejecutar con perfil dev
mvn spring-boot:run

# O con JAR
java -jar target/serviciudad-cali-1.0.0.jar
```

### Perfiles de Spring

#### Development (dev)

```yaml
# application-dev.yml
spring:
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: create-drop
logging:
  level:
    root: DEBUG
```

Activar: `mvn spring-boot:run -Dspring-boot.run.profiles=dev`

#### Production (prod)

```yaml
# application-prod.yml
spring:
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: none
logging:
  level:
    root: WARN
```

Activar: `java -jar app.jar --spring.profiles.active=prod`

---

## Monitoreo con Spring Actuator

### Endpoints de Monitoreo

| Endpoint | Descripcion | URL |
|----------|-------------|-----|
| `/actuator` | Lista de endpoints | http://localhost:8080/actuator |
| `/actuator/health` | Health check | http://localhost:8080/actuator/health |
| `/actuator/health/liveness` | Liveness probe (K8s) | http://localhost:8080/actuator/health/liveness |
| `/actuator/health/readiness` | Readiness probe (K8s) | http://localhost:8080/actuator/health/readiness |
| `/actuator/info` | Informacion app | http://localhost:8080/actuator/info |
| `/actuator/metrics` | Metricas generales | http://localhost:8080/actuator/metrics |
| `/actuator/metrics/{name}` | Metrica especifica | http://localhost:8080/actuator/metrics/jvm.memory.used |
| `/actuator/prometheus` | Formato Prometheus | http://localhost:8080/actuator/prometheus |

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

**Response (UP):**

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 215989518336,
        "threshold": 10485760
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

### Metricas Clave

```bash
# Memoria JVM
curl http://localhost:8080/actuator/metrics/jvm.memory.used

# HTTP Requests
curl http://localhost:8080/actuator/metrics/http.server.requests

# Conexiones Base Datos
curl http://localhost:8080/actuator/metrics/hikaricp.connections.active
```

### Integracion con Prometheus

Configurar `prometheus.yml`:

```yaml
scrape_configs:
  - job_name: 'serviciudad'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
```

Importar dashboards Grafana:
- Dashboard ID: 12900 (JVM Micrometer)
- Dashboard ID: 11378 (Spring Boot Statistics)

---

## Testing

### Tests Unitarios

```bash
# Todos los tests
mvn test

# Test especifico
mvn test -Dtest=FacturaAcueductoServiceTest

# Metodo especifico
mvn test -Dtest=FacturaAcueductoServiceTest#debeCalcularDeudaTotal
```

### Tests de Integracion

```bash
mvn verify
```

### Reporte de Cobertura (JaCoCo)

```bash
# Generar reporte
mvn clean test jacoco:report

# Abrir en navegador
# Windows
start target/site/jacoco/index.html

# Linux/macOS
open target/site/jacoco/index.html
```

**Metricas Objetivo:**
- Cobertura lineas: >80%
- Cobertura branches: >70%

---

## Solucion de Problemas

### Error: "Connection refused" PostgreSQL

**Sintoma:**
```
org.postgresql.util.PSQLException: Connection to localhost:5432 refused
```

**Soluciones:**

1. Verificar PostgreSQL corriendo:

```bash
# Linux
sudo systemctl status postgresql

# Windows
services.msc  # Buscar "PostgreSQL"
```

2. Verificar credenciales en `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/serviciudad_db
    username: serviciudad_user
    password: serviciudad_pass
```

3. Probar conexion manual:

```bash
psql -h localhost -U serviciudad_user -d serviciudad_db
```

### Error: "Port 8080 already in use"

**Sintoma:**
```
Port 8080 was already in use
```

**Soluciones:**

1. Cambiar puerto en `application.yml`:

```yaml
server:
  port: 8081
```

2. Matar proceso:

```bash
# Windows PowerShell
Get-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess | Stop-Process

# Linux/macOS
lsof -ti:8080 | xargs kill -9
```

### Error: "Cannot find consumos_energia.txt"

**Sintoma:**
```
ArchivoLecturaException: Error leyendo archivo
```

**Soluciones:**

1. Verificar archivo existe:

```bash
ls data/consumos_energia.txt
```

2. Verificar ruta en `application.yml`:

```yaml
energia:
  archivo:
    path: ./data/consumos_energia.txt
```

3. Usar ruta absoluta:

```yaml
energia:
  archivo:
    path: D:/ruta/completa/consumos_energia.txt
```

### Error: "No compiler provided" (Maven)

**Sintoma:**
```
No compiler is provided. Perhaps you are running on a JRE rather than a JDK?
```

**Solucion:**

Configurar `JAVA_HOME` a JDK (no JRE):

```powershell
# Windows PowerShell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.8.9-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Verificar
mvn -version
```

### Logs de Debugging

Habilitar logs detallados en `application.yml`:

```yaml
logging:
  level:
    root: DEBUG
    com.serviciudad: DEBUG
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
```

Ver logs:

```bash
tail -f logs/serviciudad.log
```

---

## Documentacion Adicional

- **[INFORME.md](INFORME.md)** - Justificacion tecnica completa de patrones de diseno
- **[Diagrama Arquitectura](diagrams/arquitectura_serviciudad.drawio.xml)** - Diagrama Draw.io editable
- **[Coleccion Postman](postman/)** - Requests de prueba

---

## Equipo de Desarrollo

| Nombre | Código | Rol |
|--------|--------|-----|
| **Eduard Criollo Yule** | 2220335 | Project Manager |
| **Felipe Charria Caicedo** | 2216033 | Integration Specialist |
| **Jhonathan Chicaiza Herrera** | 2215286 | Backend Developer |
| **Emmanuel Mena** | 2230574 | Full Stack Developer |
| **Juan Sebastian Castillo** | 2231921 | Frontend Developer |

---

## 🎓 Contexto Académico

**Universidad:** Universidad Autónoma de Occidente  
**Facultad:** Ingeniería  
**Programa:** Ingeniería de Software  
**Curso:** Ingeniería de Software 2
**Año:** 2025  

---

## Solución de Problemas

### Error: "Connection refused" a PostgreSQL

```bash
# Verificar que PostgreSQL esté corriendo
sudo systemctl status postgresql  # Linux
# o
pg_ctl status  # Windows

# Verificar credenciales en application.yml
```

### Error: "Port 8080 already in use"

```bash
# Cambiar puerto en application.yml
server:
  port: 8081
```

### Error: "Cannot find consumos_energia.txt"

```bash
# Verificar que el archivo exista en data/
ls data/consumos_energia.txt

# Verificar path en application.yml
energia:
  archivo:
    path: ./data/consumos_energia.txt
```

---

## Licencia

Este proyecto está bajo la Licencia MIT. Ver [LICENSE](LICENSE) para más detalles.

---

## Contribuciones

Este es un proyecto académico. Para consultas o sugerencias, contactar al equipo de desarrollo.

---

## Contacto

- **Repositorio:** [github.com/LeonarDPeace/Ingenieria-Software-2](https://github.com/LeonarDPeace/Ingenieria-Software-2)
- **Universidad:** [UAO - Universidad Autónoma de Occidente](https://www.uao.edu.co/)