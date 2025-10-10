# PLAN DE DESARROLLO - ENTREGABLE CORTE 2
## FASE 2: IMPLEMENTACIÓN SPRING BOOT BASE

---

**Proyecto:** ServiCiudad Cali - Sistema de Consulta Unificada  
**Fase:** 2 - Configuración Base de Spring Boot  
**Fecha:** Octubre 2025

---

## 🎯 OBJETIVO DE LA FASE 2

Configurar la aplicación base de Spring Boot con todas las dependencias necesarias, estructura de paquetes Java, configuración de perfiles y conexión a PostgreSQL funcional.

---

## 📦 CONFIGURACIÓN MAVEN (pom.xml)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>
    
    <groupId>com.serviciudad</groupId>
    <artifactId>serviciudad-api</artifactId>
    <version>1.0.0</version>
    <name>ServiCiudad API</name>
    <description>Sistema de Consulta Unificada para ServiCiudad Cali</description>
    
    <properties>
        <java.version>17</java.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <lombok.version>1.18.30</lombok.version>
        <springdoc.version>2.3.0</springdoc.version>
    </properties>
    
    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        
        <!-- PostgreSQL Driver -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        
        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
            <optional>true</optional>
        </dependency>
        
        <!-- OpenAPI/Swagger Documentation -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>${springdoc.version}</version>
        </dependency>
        
        <!-- Testing Dependencies -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope>
        </dependency>
        
        <!-- DevTools (opcional, útil en desarrollo) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
            
            <!-- JaCoCo para cobertura de código -->
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>0.8.11</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>prepare-agent</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>report</id>
                        <phase>test</phase>
                        <goals>
                            <goal>report</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## ⚙️ CONFIGURACIÓN DE SPRING BOOT

### 1. application.yml (Configuración Principal)

```yaml
spring:
  application:
    name: serviciudad-api
  
  profiles:
    active: dev
  
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect
  
  sql:
    init:
      mode: always
      
server:
  port: 8080
  servlet:
    context-path: /
  error:
    include-message: always
    include-binding-errors: always
    include-stacktrace: never
    
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
      
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: method
    tags-sorter: alpha
    
logging:
  level:
    root: INFO
    com.serviciudad: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

### 2. application-dev.yml (Perfil Desarrollo)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/serviciudad
    username: admin
    password: admin123
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 5
      minimum-idle: 2
      connection-timeout: 20000
      
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: update
      
serviciudad:
  energia:
    archivo: src/main/resources/consumos_energia.txt
    
logging:
  level:
    com.serviciudad: DEBUG
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### 3. application-prod.yml (Perfil Producción)

```yaml
spring:
  datasource:
    url: ${DATABASE_URL:jdbc:postgresql://postgres:5432/serviciudad}
    username: ${DATABASE_USERNAME:admin}
    password: ${DATABASE_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: validate
      
serviciudad:
  energia:
    archivo: /app/data/consumos_energia.txt
    
logging:
  level:
    com.serviciudad: INFO
    org.springframework.web: WARN
```

### 4. application-test.yml (Perfil Tests)

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
    
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    
  h2:
    console:
      enabled: true
      
serviciudad:
  energia:
    archivo: src/test/resources/consumos_energia_test.txt
```

---

## 🗄️ CONFIGURACIÓN DE BASE DE DATOS

### 1. schema.sql (Esquema de Base de Datos)

```sql
-- Tabla de facturas de acueducto
CREATE TABLE IF NOT EXISTS facturas_acueducto (
    id SERIAL PRIMARY KEY,
    id_cliente VARCHAR(20) NOT NULL,
    periodo VARCHAR(6) NOT NULL,
    consumo_m3 INTEGER NOT NULL,
    valor_pagar DECIMAL(12, 2) NOT NULL,
    fecha_emision TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) DEFAULT 'PENDIENTE',
    CONSTRAINT uk_cliente_periodo UNIQUE (id_cliente, periodo)
);

-- Índices para optimización
CREATE INDEX idx_facturas_acueducto_cliente ON facturas_acueducto(id_cliente);
CREATE INDEX idx_facturas_acueducto_periodo ON facturas_acueducto(periodo);
CREATE INDEX idx_facturas_acueducto_estado ON facturas_acueducto(estado);

-- Tabla de auditoría (opcional)
CREATE TABLE IF NOT EXISTS auditoria_consultas (
    id SERIAL PRIMARY KEY,
    cliente_id VARCHAR(20) NOT NULL,
    fecha_consulta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(50),
    user_agent TEXT,
    tiempo_respuesta_ms INTEGER
);

-- Comentarios en tablas
COMMENT ON TABLE facturas_acueducto IS 'Almacena las facturas del servicio de acueducto';
COMMENT ON COLUMN facturas_acueducto.id_cliente IS 'Número de identificación del cliente';
COMMENT ON COLUMN facturas_acueducto.periodo IS 'Periodo de facturación en formato YYYYMM';
COMMENT ON COLUMN facturas_acueducto.consumo_m3 IS 'Consumo de agua en metros cúbicos';
COMMENT ON COLUMN facturas_acueducto.valor_pagar IS 'Valor a pagar por el servicio';
```

### 2. data.sql (Datos de Ejemplo)

```sql
-- Insertar datos de ejemplo para pruebas
INSERT INTO facturas_acueducto (id_cliente, periodo, consumo_m3, valor_pagar, estado) 
VALUES 
    ('0001234567', '202510', 15, 95000.00, 'PENDIENTE'),
    ('0009876543', '202510', 22, 132000.00, 'PENDIENTE'),
    ('0001122334', '202510', 8, 48000.00, 'PENDIENTE'),
    ('0005566778', '202510', 18, 108000.00, 'PENDIENTE'),
    ('0002233445', '202510', 12, 72000.00, 'PENDIENTE')
ON CONFLICT (id_cliente, periodo) DO NOTHING;

-- Datos de periodos anteriores
INSERT INTO facturas_acueducto (id_cliente, periodo, consumo_m3, valor_pagar, estado) 
VALUES 
    ('0001234567', '202509', 14, 89000.00, 'PAGADA'),
    ('0009876543', '202509', 20, 120000.00, 'PAGADA'),
    ('0001122334', '202509', 9, 54000.00, 'PAGADA')
ON CONFLICT (id_cliente, periodo) DO NOTHING;
```

### 3. consumos_energia.txt (Archivo Legacy COBOL-style)

```
000123456720251000001500000180000.50
000987654320251000002250000270000.75
000112233420251000000800000096000.00
000556677820251000001800000216000.25
000223344520251000001200000144000.00
```

**Formato del archivo:**
- `id_cliente` (10 caracteres): ID del cliente con padding de ceros
- `periodo` (6 caracteres): YYYYMM
- `consumo_kwh` (8 caracteres): Consumo en kWh con padding de ceros
- `valor_pagar` (12 caracteres): Valor en centavos (dividir entre 100)

---

## 🏗️ ESTRUCTURA DE PAQUETES JAVA

### 1. Clase Principal - ServiCiudadApplication.java

```java
package com.serviciudad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Aplicación principal de ServiCiudad API
 * 
 * Sistema de consulta unificada de servicios públicos para la ciudad de Cali.
 * Integra sistemas legacy (Mainframe COBOL) con tecnologías modernas (Spring Boot).
 * 
 * @author Equipo ServiCiudad
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.serviciudad.repository")
public class ServiCiudadApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiCiudadApplication.class, args);
    }
}
```

### 2. Configuración de Base de Datos - DatabaseConfig.java

```java
package com.serviciudad.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuración de base de datos y JPA
 */
@Configuration
@EnableTransactionManagement
@EnableJpaAuditing
public class DatabaseConfig {
    // Spring Boot auto-configura el DataSource basado en application.yml
    // Esta clase está preparada para configuraciones adicionales si se requieren
}
```

### 3. Configuración de OpenAPI - OpenApiConfig.java

```java
package com.serviciudad.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de documentación OpenAPI/Swagger
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI serviciudadOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("ServiCiudad API")
                .description("API RESTful para consulta unificada de servicios públicos en Cali")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Equipo ServiCiudad")
                    .email("serviciudad@cali.gov.co")
                    .url("https://github.com/LeonarDPeace/Ingenieria-Software-2"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")));
    }
}
```

### 4. Configuración de CORS - CorsConfig.java

```java
package com.serviciudad.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * Configuración de CORS para permitir peticiones desde frontend
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(Arrays.asList("*"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
```

### 5. Manejo Global de Excepciones - GlobalExceptionHandler.java

```java
package com.serviciudad.controller.advice;

import com.serviciudad.dto.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

/**
 * Manejador global de excepciones para toda la aplicación
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGlobalException(
            Exception ex, WebRequest request) {
        
        log.error("Error no controlado: ", ex);
        
        ErrorResponseDTO error = ErrorResponseDTO.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Internal Server Error")
            .message("Ha ocurrido un error inesperado. Por favor, intente más tarde.")
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
        
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

### 6. DTO de Error - ErrorResponseDTO.java

```java
package com.serviciudad.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para respuestas de error estandarizadas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime timestamp;
    
    private int status;
    private String error;
    private String message;
    private String path;
}
```

---

## ✅ TAREAS DE LA FASE 2

### Task 2.1: Configurar Maven
- ✅ Crear `pom.xml` con todas las dependencias
- ✅ Configurar plugins (Maven, JaCoCo)
- ✅ Verificar compilación: `mvn clean compile`

### Task 2.2: Configurar Spring Boot
- ✅ Crear archivos `application.yml` (todos los perfiles)
- ✅ Configurar conexión PostgreSQL
- ✅ Configurar logging
- ✅ Configurar actuator endpoints

### Task 2.3: Configurar Base de Datos
- ✅ Crear `schema.sql` con DDL
- ✅ Crear `data.sql` con datos de ejemplo
- ✅ Crear archivo `consumos_energia.txt`

### Task 2.4: Crear Clases de Configuración Java
- ✅ `ServiCiudadApplication.java` (main)
- ✅ `DatabaseConfig.java`
- ✅ `OpenApiConfig.java`
- ✅ `CorsConfig.java`
- ✅ `GlobalExceptionHandler.java`
- ✅ `ErrorResponseDTO.java`

### Task 2.5: Verificar Aplicación
```bash
# Levantar PostgreSQL con Docker
docker run --name postgres-serviciudad -e POSTGRES_PASSWORD=admin123 -e POSTGRES_DB=serviciudad -e POSTGRES_USER=admin -p 5432:5432 -d postgres:15

# Ejecutar aplicación
mvn spring-boot:run

# Verificar health check
curl http://localhost:8080/actuator/health

# Verificar Swagger UI
# Abrir en navegador: http://localhost:8080/swagger-ui.html
```

---

## 📊 CRITERIOS DE ÉXITO FASE 2

- ✅ Maven configurado y compilación exitosa
- ✅ Aplicación Spring Boot arranca sin errores
- ✅ Conexión a PostgreSQL funcional
- ✅ Schema y datos de ejemplo creados automáticamente
- ✅ Actuator endpoints respondiendo
- ✅ Swagger UI accesible en `/swagger-ui.html`
- ✅ Logs estructurados funcionando correctamente
- ✅ Archivo legacy `consumos_energia.txt` creado y accesible

---

## 🔜 PRÓXIMA FASE

**FASE 3: PATRONES DE DISEÑO OBLIGATORIOS**
- Patrón Adapter (archivo energía)
- Patrón Repository (Spring Data JPA)
- Patrón Builder (DTOs)
- Patrón DTO (separación entidades/respuestas)
- Patrón IoC/DI (Spring Framework)

---

*Documento generado: Octubre 10, 2025*  
*Universidad Autónoma de Occidente - Ingeniería de Software 2*
