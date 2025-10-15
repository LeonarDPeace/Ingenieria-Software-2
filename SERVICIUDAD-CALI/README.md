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

## 📡 Endpoints de la API

### Endpoint Principal

```http
GET /api/v1/clientes/{clienteId}/deuda-consolidada
```

**Ejemplo de Respuesta:**

```json
{
  "clienteId": "0001234567",
  "nombreCliente": "Juan Pérez Gómez",
  "fechaConsulta": "2025-10-15T16:30:00",
  "resumenDeuda": {
    "energia": {
      "periodo": "202510",
      "consumo": "1500 kWh",
      "valorPagar": 180000.50,
      "estado": "PENDIENTE"
    },
    "acueducto": {
      "periodo": "202510",
      "consumo": "15 m³",
      "valorPagar": 95000.00,
      "estado": "PENDIENTE"
    }
  },
  "totalAPagar": 275000.50
}
```

### Otros Endpoints

- `GET /actuator/health` - Estado de la aplicación
- `GET /swagger-ui.html` - Documentación interactiva (OpenAPI)
- `GET /actuator/metrics` - Métricas de la aplicación

---

## Testing

### Ejecutar Tests Unitarios

```bash
mvn test
```

### Ejecutar Tests de Integración

```bash
mvn verify
```

### Reporte de Cobertura

```bash
mvn clean test jacoco:report

# Ver reporte en: target/site/jacoco/index.html
```

---

## Documentación Adicional

- **[INFORME.md](INFORME.md)** - Justificación técnica detallada de patrones de diseño
- **[Colección Postman](postman/)** - Ejemplos de requests para testing

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