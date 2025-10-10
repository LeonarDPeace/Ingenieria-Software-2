# PLAN DE DESARROLLO - ENTREGABLE CORTE 2
## FASE 1: ESTRUCTURA DEL PROYECTO

---

**Proyecto:** ServiCiudad Cali - Sistema de Consulta Unificada  
**Entregable:** API RESTful Monolítica con Spring Boot  
**Fecha:** Octubre 2025  
**Equipo:** Eduard Criollo, Felipe Charria, Jhonathan Chicaiza, Emmanuel Mena, Juan Sebastian Castillo

---

## 📋 CONTEXTO DEL ENTREGABLE

### Diferencias entre Proyecto Principal y Entregable Corte 2

| Aspecto | Proyecto Principal (ProyectoFinal/) | Entregable Corte 2 |
|---------|-------------------------------------|-------------------|
| **Arquitectura** | Microservicios completos | Monolito modular |
| **Servicios** | Energía + Acueducto + Telecomunicaciones | Solo Energía + Acueducto |
| **Patrones** | 8+ patrones avanzados (Saga, Circuit Breaker, etc.) | 5 patrones básicos obligatorios |
| **Stack** | Spring Cloud, Kafka, K8s, MongoDB, Redis | Spring Boot, PostgreSQL |
| **Complejidad** | Producción empresarial | Prototipo funcional simplificado |

### Requerimientos del Enunciado

✅ **Funcionales:**
- Consulta unificada de saldos (Energía + Acueducto)
- Endpoint único: `GET /api/v1/clientes/{clienteId}/deuda-consolidada`
- Respuesta JSON consolidada con desglose por servicio
- Integración con archivo plano (Mainframe simulado) y PostgreSQL

✅ **Técnicos:**
- Spring Boot 3.x + Java 17+
- PostgreSQL para datos acueducto
- 5 patrones de diseño implementados y justificados:
  1. **Adapter Pattern** (integración archivo legacy)
  2. **Builder Pattern** (construcción DTOs complejos)
  3. **DTO Pattern** (separación entidades/respuestas)
  4. **Repository Pattern** (Spring Data JPA)
  5. **IoC/DI Pattern** (Spring Framework)

✅ **Entregables:**
- Código fuente en GitHub
- README.md con instrucciones
- INFORME.md con justificación patrones
- Colección Postman funcional

✅ **Expansión Permitida:**
- Docker y docker-compose
- Patrones adicionales (Circuit Breaker, Strategy, Observer, Command, Saga)
- Documentación extensiva para sustentación y deployment
- Tests unitarios e integración
- CI/CD pipeline

---

## 🎯 FASE 1: ESTRUCTURA DEL PROYECTO

### Objetivo
Crear la estructura de carpetas y archivos base del proyecto en el repositorio, preparando el entorno para el desarrollo organizado y profesional.

### Ubicación en Repositorio
```
ProyectoFinal/
├── Ingenieria-Software-2/
│   └── Entregable/  ← NUEVA CARPETA PARA ESTE ENTREGABLE
```

---

## 📁 ESTRUCTURA COMPLETA DEL PROYECTO

```
ProyectoFinal/Ingenieria-Software-2/Entregable/
│
├── src/
│   ├── main/
│   │   ├── java/com/serviciudad/
│   │   │   ├── adapter/              # Patrón Adapter (archivo energía)
│   │   │   │   ├── ArchivoEnergiaAdapter.java
│   │   │   │   ├── ServicioEnergiaPort.java
│   │   │   │   └── exception/
│   │   │   │       ├── ArchivoEnergiaException.java
│   │   │   │       └── ClienteNoEncontradoException.java
│   │   │   │
│   │   │   ├── builder/              # Patrón Builder (construcción DTOs)
│   │   │   │   ├── DeudaConsolidadaDTOBuilder.java
│   │   │   │   └── ResumenDeudaDTOBuilder.java
│   │   │   │
│   │   │   ├── dto/                  # Data Transfer Objects
│   │   │   │   ├── DeudaConsolidadaDTO.java
│   │   │   │   ├── ResumenDeudaDTO.java
│   │   │   │   ├── DetalleServicioDTO.java
│   │   │   │   └── ErrorResponseDTO.java
│   │   │   │
│   │   │   ├── entity/               # Entidades JPA
│   │   │   │   ├── FacturaAcueducto.java
│   │   │   │   └── FacturaEnergia.java
│   │   │   │
│   │   │   ├── repository/           # Patrón Repository (Spring Data)
│   │   │   │   └── FacturaAcueductoRepository.java
│   │   │   │
│   │   │   ├── service/              # Lógica de negocio
│   │   │   │   ├── DeudaConsolidadaService.java
│   │   │   │   ├── ServicioEnergiaService.java
│   │   │   │   └── mapper/
│   │   │   │       └── DeudaConsolidadaDTOMapper.java
│   │   │   │
│   │   │   ├── controller/           # REST Controllers
│   │   │   │   ├── DeudaConsolidadaController.java
│   │   │   │   └── advice/
│   │   │   │       └── GlobalExceptionHandler.java
│   │   │   │
│   │   │   ├── config/               # Configuración Spring
│   │   │   │   ├── DatabaseConfig.java
│   │   │   │   ├── OpenApiConfig.java
│   │   │   │   └── CorsConfig.java
│   │   │   │
│   │   │   └── ServiCiudadApplication.java  # Main class
│   │   │
│   │   └── resources/
│   │       ├── application.yml       # Configuración principal
│   │       ├── application-dev.yml   # Perfil desarrollo
│   │       ├── application-prod.yml  # Perfil producción
│   │       ├── data.sql              # Datos iniciales PostgreSQL
│   │       ├── schema.sql            # Schema base de datos
│   │       └── consumos_energia.txt  # Archivo plano legacy (COBOL-style)
│   │
│   └── test/
│       ├── java/com/serviciudad/
│       │   ├── adapter/
│       │   │   └── ArchivoEnergiaAdapterTest.java
│       │   ├── service/
│       │   │   └── DeudaConsolidadaServiceTest.java
│       │   ├── controller/
│       │   │   └── DeudaConsolidadaControllerTest.java
│       │   └── integration/
│       │       └── DeudaConsolidadaIntegrationTest.java
│       │
│       └── resources/
│           ├── application-test.yml
│           └── consumos_energia_test.txt
│
├── docker/
│   ├── Dockerfile                    # Dockerfile multi-stage para app
│   ├── docker-compose.yml            # Orquestación de servicios
│   └── init-scripts/
│       ├── 01-create-database.sql
│       └── 02-insert-data.sql
│
├── postman/
│   ├── ServiCiudad_API.postman_collection.json
│   ├── ServiCiudad_ENV_Local.postman_environment.json
│   └── ServiCiudad_ENV_Docker.postman_environment.json
│
├── docs/
│   ├── INFORME.md                    # Justificación técnica de patrones
│   ├── SUSTENTACION.md               # Guía para presentación oral
│   ├── DEPLOYMENT.md                 # Montaje en entorno de pruebas
│   ├── arquitectura/
│   │   ├── diagrama_componentes.png
│   │   ├── diagrama_clases.png
│   │   └── diagrama_secuencia.png
│   └── ejemplos/
│       ├── ejemplo_request.json
│       └── ejemplo_response.json
│
├── scripts/
│   ├── setup-local.sh                # Script configuración local (Linux/Mac)
│   ├── setup-local.ps1               # Script configuración local (Windows)
│   ├── integration-tests.sh          # Tests de integración
│   └── backup-db.sh                  # Backup base de datos
│
├── .github/
│   └── workflows/
│       ├── ci-pipeline.yml           # CI/CD con GitHub Actions
│       └── docker-build.yml          # Build automático de imágenes Docker
│
├── .gitignore
├── README.md                         # Documentación principal
├── pom.xml                           # Configuración Maven
└── LICENSE
```

---

## 📝 ARCHIVOS BASE INICIALES

### 1. `.gitignore`
```gitignore
# Compiled class file
*.class

# Log files
*.log

# BlueJ files
*.ctxt

# Mobile Tools for Java (J2ME)
.mtj.tmp/

# Package Files
*.jar
*.war
*.nar
*.ear
*.zip
*.tar.gz
*.rar

# Virtual machine crash logs
hs_err_pid*

# IDE
.idea/
*.iml
*.iws
*.ipr
.vscode/
.settings/
.classpath
.project

# Maven
target/
pom.xml.tag
pom.xml.releaseBackup
pom.xml.versionsBackup
pom.xml.next
release.properties
dependency-reduced-pom.xml
buildNumber.properties
.mvn/timing.properties
.mvn/wrapper/maven-wrapper.jar

# Spring Boot
*.log
spring-boot-devtools.properties

# OS
.DS_Store
Thumbs.db

# Docker
docker-compose.override.yml

# Environment variables
.env
.env.local
```

### 2. `README.md` (Plantilla inicial)
```markdown
# ServiCiudad Cali - Sistema de Consulta Unificada

## 📋 Descripción

API RESTful monolítica que centraliza la consulta de saldos de servicios públicos (Energía y Acueducto) para la ciudad de Cali, integrando sistemas legacy con tecnologías modernas.

## 🎯 Objetivo

Proporcionar un endpoint único para que los ciudadanos consulten su deuda consolidada de servicios, eliminando la necesidad de acceder a múltiples sistemas.

## 🏗️ Arquitectura

- **Tipo:** Monolito modular
- **Framework:** Spring Boot 3.2.0
- **Lenguaje:** Java 17
- **Base de Datos:** PostgreSQL 15
- **Patrones:** Adapter, Builder, DTO, Repository, IoC/DI

## 🚀 Inicio Rápido

### Requisitos Previos
- Java 17+
- Maven 3.8+ o Gradle 7+
- Docker y Docker Compose
- PostgreSQL 15 (si ejecución local sin Docker)

### Opción 1: Con Docker (Recomendado)
```bash
# Clonar repositorio
git clone https://github.com/LeonarDPeace/Ingenieria-Software-2.git
cd Ingenieria-Software-2/Entregable

# Levantar servicios
docker-compose up -d

# Verificar
curl http://localhost:8080/actuator/health
```

### Opción 2: Ejecución Local
```bash
# Compilar
mvn clean package

# Ejecutar
java -jar target/serviciudad-api-1.0.0.jar
```

## 📚 Documentación

- [INFORME.md](docs/INFORME.md) - Justificación técnica de patrones
- [SUSTENTACION.md](docs/SUSTENTACION.md) - Guía de presentación
- [DEPLOYMENT.md](docs/DEPLOYMENT.md) - Despliegue en producción

## 👥 Equipo

- Eduard Criollo Yule (2220335) - Project Manager
- Felipe Charria Caicedo (2216033) - Integration Specialist
- Jhonathan Chicaiza Herrera (2215286) - Backend Developer
- Emmanuel Mena (2230574) - Full Stack Developer
- Juan Sebastian Castillo (2231921) - Frontend Developer

## 📄 Licencia

MIT License - Universidad Autónoma de Occidente 2025
```

---

## ✅ TAREAS DE LA FASE 1

### Task 1.1: Crear estructura de carpetas base
```bash
# Navegar al repositorio
cd "D:\OneDrive - Universidad Autonoma de Occidente\Universidad\7mo Semestre\Ing. de Software 2\ProyectoFinal\Ingenieria-Software-2"

# Crear carpeta principal del entregable
mkdir Entregable

# Crear estructura de carpetas
cd Entregable
mkdir -p src/main/java/com/serviciudad/{adapter,builder,dto,entity,repository,service,controller,config}
mkdir -p src/main/resources
mkdir -p src/test/java/com/serviciudad/{adapter,service,controller,integration}
mkdir -p src/test/resources
mkdir -p docker/init-scripts
mkdir -p postman
mkdir -p docs/arquitectura
mkdir -p docs/ejemplos
mkdir -p scripts
mkdir -p .github/workflows
```

### Task 1.2: Crear archivos de configuración base
- `.gitignore`
- `README.md` (plantilla inicial)
- `LICENSE` (MIT)

### Task 1.3: Inicializar proyecto Maven
```bash
# Crear pom.xml base con dependencias Spring Boot
```

### Task 1.4: Crear archivo de datos legacy
```bash
# Crear consumos_energia.txt con formato COBOL-style
```

---

## 📊 CRITERIOS DE ÉXITO FASE 1

- ✅ Estructura de carpetas completa creada
- ✅ Archivos de configuración base (.gitignore, README, LICENSE)
- ✅ Proyecto Maven inicializado con dependencias correctas
- ✅ Archivo de datos legacy (consumos_energia.txt) creado
- ✅ Todo commitado en rama develop del repositorio
- ✅ Documentación de estructura en README.md

---

## 🔜 PRÓXIMA FASE

**FASE 2: IMPLEMENTACIÓN SPRING BOOT BASE**
- Configuración de Spring Boot application
- Configuración de conexión PostgreSQL
- Estructura de paquetes Java
- Configuración de perfiles (dev, prod, test)

---

*Documento generado: Octubre 10, 2025*  
*Universidad Autónoma de Occidente - Ingeniería de Software 2*
