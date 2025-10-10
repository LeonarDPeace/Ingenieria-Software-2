# PLAN DE DESARROLLO - ENTREGABLE CORTE 2
## FASE 4: PATRONES ADICIONALES Y DOCKERIZACIÓN

---

**Proyecto:** ServiCiudad Cali - Sistema de Consulta Unificada  
**Fase:** 4 - Expansión con Patrones Avanzados y Contenedores  
**Fecha:** Octubre 2025

---

## 🎯 OBJETIVO DE LA FASE 4

Expandir la aplicación con **patrones de diseño adicionales** para aumentar la resiliencia, escalabilidad y mantenibilidad. Dockerizar completamente la solución para facilitar deployment y pruebas.

---

## 🔐 DOCKERIZACIÓN COMPLETA

### 1. Dockerfile Multi-Stage

```dockerfile
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración Maven
COPY pom.xml .
COPY src ./src

# Compilar aplicación (skip tests para build más rápido)
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

# Metadata
LABEL maintainer="serviciudad@cali.gov.co"
LABEL version="1.0.0"
LABEL description="ServiCiudad API - Sistema de Consulta Unificada"

# Crear usuario no-root para seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Establecer directorio de trabajo
WORKDIR /app

# Copiar JAR desde stage de build
COPY --from=build /app/target/*.jar app.jar

# Copiar archivo de datos legacy
COPY --from=build /app/src/main/resources/consumos_energia.txt /app/data/consumos_energia.txt

# Cambiar propiedad de archivos
RUN chown -R appuser:appgroup /app

# Cambiar a usuario no-root
USER appuser

# Exponer puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Configuración de JVM
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Punto de entrada
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### 2. docker-compose.yml Completo

```yaml
version: '3.8'

services:
  #############################
  # PostgreSQL Database
  #############################
  postgres:
    image: postgres:15-alpine
    container_name: serviciudad-db
    restart: unless-stopped
    environment:
      POSTGRES_DB: serviciudad
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD:-admin123}
      PGDATA: /var/lib/postgresql/data/pgdata
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./docker/init-scripts:/docker-entrypoint-initdb.d:ro
    ports:
      - "5432:5432"
    networks:
      - serviciudad-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U admin -d serviciudad"]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 10s
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"

  #############################
  # Spring Boot Application
  #############################
  app:
    build:
      context: .
      dockerfile: docker/Dockerfile
    image: serviciudad-api:1.0.0
    container_name: serviciudad-app
    restart: unless-stopped
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_started
    environment:
      # Spring profiles
      SPRING_PROFILES_ACTIVE: prod
      
      # Database connection
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/serviciudad
      SPRING_DATASOURCE_USERNAME: admin
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD:-admin123}
      
      # JPA configuration
      SPRING_JPA_HIBERNATE_DDL_AUTO: validate
      SPRING_JPA_SHOW_SQL: "false"
      
      # Application configuration
      SERVICIUDAD_ENERGIA_ARCHIVO: /app/data/consumos_energia.txt
      
      # Redis cache
      SPRING_REDIS_HOST: redis
      SPRING_REDIS_PORT: 6379
      
      # Actuator
      MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE: health,info,metrics,prometheus
      
      # Logging
      LOGGING_LEVEL_ROOT: INFO
      LOGGING_LEVEL_COM_SERVICIUDAD: INFO
      
    ports:
      - "8080:8080"
    volumes:
      - app_logs:/app/logs
    networks:
      - serviciudad-network
    healthcheck:
      test: ["CMD", "wget", "--spider", "--quiet", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "5"

  #############################
  # Redis Cache
  #############################
  redis:
    image: redis:7-alpine
    container_name: serviciudad-cache
    restart: unless-stopped
    command: redis-server --maxmemory 256mb --maxmemory-policy allkeys-lru --appendonly yes
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    networks:
      - serviciudad-network
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 3s
      retries: 5
    logging:
      driver: "json-file"
      options:
        max-size: "5m"
        max-file: "2"

  #############################
  # pgAdmin (Database Management)
  #############################
  pgadmin:
    image: dpage/pgadmin4:latest
    container_name: serviciudad-pgadmin
    restart: unless-stopped
    environment:
      PGADMIN_DEFAULT_EMAIL: ${PGADMIN_EMAIL:-admin@serviciudad.com}
      PGADMIN_DEFAULT_PASSWORD: ${PGADMIN_PASSWORD:-admin123}
      PGADMIN_CONFIG_SERVER_MODE: 'False'
      PGADMIN_CONFIG_MASTER_PASSWORD_REQUIRED: 'False'
    ports:
      - "5050:80"
    volumes:
      - pgadmin_data:/var/lib/pgadmin
    networks:
      - serviciudad-network
    depends_on:
      - postgres
    logging:
      driver: "json-file"
      options:
        max-size: "5m"
        max-file: "2"

  #############################
  # Prometheus (Monitoring)
  #############################
  prometheus:
    image: prom/prometheus:latest
    container_name: serviciudad-prometheus
    restart: unless-stopped
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--web.console.libraries=/usr/share/prometheus/console_libraries'
      - '--web.console.templates=/usr/share/prometheus/consoles'
    ports:
      - "9090:9090"
    volumes:
      - ./docker/prometheus/prometheus.yml:/etc/prometheus/prometheus.yml:ro
      - prometheus_data:/prometheus
    networks:
      - serviciudad-network
    depends_on:
      - app
    logging:
      driver: "json-file"
      options:
        max-size: "5m"
        max-file: "2"

volumes:
  postgres_data:
    driver: local
  redis_data:
    driver: local
  pgadmin_data:
    driver: local
  prometheus_data:
    driver: local
  app_logs:
    driver: local

networks:
  serviciudad-network:
    driver: bridge
    name: serviciudad-network
```

### 3. .dockerignore

```
# Maven
target/
pom.xml.tag
pom.xml.releaseBackup
pom.xml.versionsBackup
pom.xml.next
release.properties

# IDE
.idea/
*.iml
.vscode/
.settings/
.classpath
.project

# Logs
*.log
logs/

# OS
.DS_Store
Thumbs.db

# Git
.git/
.gitignore

# Documentation
docs/
*.md
README.md

# Docker
docker-compose*.yml
Dockerfile
.dockerignore

# Tests
src/test/
```

### 4. Archivo .env (Ejemplo)

```env
# PostgreSQL Configuration
POSTGRES_PASSWORD=SecurePassword123!

# pgAdmin Configuration
PGADMIN_EMAIL=admin@serviciudad.com
PGADMIN_PASSWORD=AdminSecure456!

# Application Configuration
SPRING_PROFILES_ACTIVE=prod
```

### 5. Scripts de Inicialización PostgreSQL

**docker/init-scripts/01-create-database.sql**
```sql
-- Crear extensiones necesarias
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Verificar base de datos
SELECT 'Database serviciudad created successfully!' AS status;
```

**docker/init-scripts/02-insert-data.sql**
```sql
-- Insertar datos de ejemplo
INSERT INTO facturas_acueducto (id_cliente, periodo, consumo_m3, valor_pagar, estado) 
VALUES 
    ('0001234567', '202510', 15, 95000.00, 'PENDIENTE'),
    ('0009876543', '202510', 22, 132000.00, 'PENDIENTE'),
    ('0001122334', '202510', 8, 48000.00, 'PENDIENTE')
ON CONFLICT (id_cliente, periodo) DO NOTHING;

SELECT 'Sample data inserted successfully!' AS status;
```

### 6. Configuración Prometheus

**docker/prometheus/prometheus.yml**
```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'serviciudad-api'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['app:8080']
```

---

## 🔄 PATRÓN ADICIONAL 1: CIRCUIT BREAKER

### Problema a Resolver
El sistema legacy de energía puede fallar o volverse lento. Necesitamos prevenir cascadas de fallos y proporcionar fallbacks.

### Solución: Circuit Breaker Pattern con Resilience4j

#### 1.1 Dependencias Maven

```xml
<!-- Resilience4j -->
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
    <version>2.1.0</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

#### 1.2 Configuración Resilience4j

**application.yml**
```yaml
resilience4j:
  circuitbreaker:
    configs:
      default:
        sliding-window-size: 10
        minimum-number-of-calls: 5
        failure-rate-threshold: 50
        wait-duration-in-open-state: 60s
        permitted-number-of-calls-in-half-open-state: 3
        automatic-transition-from-open-to-half-open-enabled: true
        record-exceptions:
          - com.serviciudad.adapter.exception.ArchivoEnergiaException
          - java.io.IOException
    instances:
      servicioEnergia:
        base-config: default
        sliding-window-type: COUNT_BASED
        
  retry:
    configs:
      default:
        max-attempts: 3
        wait-duration: 1s
        exponential-backoff-multiplier: 2
        retry-exceptions:
          - java.io.IOException
    instances:
      servicioEnergia:
        base-config: default
        
  timelimiter:
    configs:
      default:
        timeout-duration: 5s
    instances:
      servicioEnergia:
        base-config: default
```

#### 1.3 Adapter con Circuit Breaker

```java
package com.serviciudad.adapter;

import com.serviciudad.entity.FacturaEnergia;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Adaptador resiliente para el sistema de energía
 * 
 * Patrones aplicados:
 * - Circuit Breaker: Previene cascada de fallos
 * - Retry: Reintentos automáticos con backoff exponencial
 * - Time Limiter: Timeout para operaciones lentas
 * - Cache: Fallback con datos en caché
 */
@Slf4j
@Component
public class ResilientArchivoEnergiaAdapter {
    
    @Autowired
    private ArchivoEnergiaAdapter adaptadorBase;
    
    /**
     * Consulta factura con resiliencia
     * 
     * Circuit Breaker States:
     * - CLOSED: Normal operation
     * - OPEN: Calls fail-fast without execution
     * - HALF_OPEN: Limited calls to test recovery
     * 
     * @param clienteId ID del cliente
     * @return CompletableFuture con la factura
     */
    @CircuitBreaker(name = "servicioEnergia", fallbackMethod = "consultarFacturaFallback")
    @Retry(name = "servicioEnergia")
    @TimeLimiter(name = "servicioEnergia")
    @Cacheable(value = "facturasEnergia", key = "#clienteId", unless = "#result == null")
    public CompletableFuture<FacturaEnergia> consultarFacturaAsync(String clienteId) {
        log.debug("Consultando factura de energía (async) para: {}", clienteId);
        
        return CompletableFuture.supplyAsync(() -> 
            adaptadorBase.consultarFactura(clienteId));
    }
    
    /**
     * Método de fallback cuando el circuit breaker está abierto
     * 
     * @param clienteId ID del cliente
     * @param ex Excepción que causó el fallback
     * @return CompletableFuture con datos mock o en caché
     */
    private CompletableFuture<FacturaEnergia> consultarFacturaFallback(
            String clienteId, 
            Exception ex) {
        
        log.warn("Circuit breaker OPEN para servicio de energía. Usando fallback. Error: {}", 
            ex.getMessage());
        
        // Retornar datos mock o del caché
        return CompletableFuture.completedFuture(
            FacturaEnergia.builder()
                .idCliente(clienteId)
                .periodo("202510")
                .consumoKwh(0)
                .valorPagar(java.math.BigDecimal.ZERO)
                .build()
        );
    }
}
```

#### 1.4 Configuración de Caché

```java
package com.serviciudad.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Configuración de caché con Redis
 * Utilizado como fallback en Circuit Breaker
 */
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(15))
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new StringRedisSerializer()))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new GenericJackson2JsonRedisSerializer()))
            .disableCachingNullValues();
        
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .transactionAware()
            .build();
    }
}
```

---

## 🎯 PATRÓN ADICIONAL 2: STRATEGY PATTERN

### Problema a Resolver
Necesitamos soportar múltiples fuentes de datos para consultas de servicios (archivo, API REST, base de datos) sin acoplar la lógica.

### Solución: Strategy Pattern

```java
package com.serviciudad.service.strategy;

import com.serviciudad.entity.FacturaServicio;
import com.serviciudad.enums.TipoServicio;

/**
 * Strategy interface para consulta de servicios
 * 
 * Patrón: Strategy Pattern
 * Permite seleccionar dinámicamente la estrategia de consulta
 */
public interface ConsultaServicioStrategy {
    
    /**
     * Consulta una factura de servicio
     * @param clienteId ID del cliente
     * @param tipoServicio Tipo de servicio
     * @return Factura del servicio
     */
    FacturaServicio consultar(String clienteId, TipoServicio tipoServicio);
    
    /**
     * Verifica si la estrategia está disponible
     * @return true si está disponible, false en caso contrario
     */
    boolean isDisponible();
    
    /**
     * Obtiene la prioridad de la estrategia
     * @return Prioridad (menor número = mayor prioridad)
     */
    int getPrioridad();
    
    /**
     * Tipo de servicio que soporta esta estrategia
     * @return Tipo de servicio
     */
    TipoServicio getTipoServicio();
}
```

```java
package com.serviciudad.service.strategy;

import com.serviciudad.entity.FacturaServicio;
import com.serviciudad.enums.TipoServicio;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Estrategia para consultar energía desde archivo legacy
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EnergiaArchivoStrategy implements ConsultaServicioStrategy {
    
    private final ArchivoEnergiaAdapter adapter;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    
    @Override
    public FacturaServicio consultar(String clienteId, TipoServicio tipoServicio) {
        log.info("Consultando energía desde archivo legacy");
        return adapter.consultarFactura(clienteId);
    }
    
    @Override
    public boolean isDisponible() {
        CircuitBreaker cb = circuitBreakerRegistry
            .circuitBreaker("servicioEnergia");
        
        return cb.getState() != CircuitBreaker.State.OPEN;
    }
    
    @Override
    public int getPrioridad() {
        return 1; // Prioridad alta (fuente primaria)
    }
    
    @Override
    public TipoServicio getTipoServicio() {
        return TipoServicio.ENERGIA;
    }
}
```

---

## 📊 TAREAS DE LA FASE 4

### Task 4.1: Dockerización
- ✅ Crear Dockerfile multi-stage optimizado
- ✅ Crear docker-compose.yml con todos los servicios
- ✅ Configurar healthchecks para todos los contenedores
- ✅ Scripts de inicialización de base de datos
- ✅ Configuración de volúmenes persistentes
- ✅ Variables de entorno con .env

### Task 4.2: Circuit Breaker Pattern
- ✅ Añadir dependencias Resilience4j
- ✅ Configurar circuit breaker, retry, time limiter
- ✅ Implementar `ResilientArchivoEnergiaAdapter`
- ✅ Configurar Redis para caché
- ✅ Métodos de fallback

### Task 4.3: Strategy Pattern
- ✅ Crear interface `ConsultaServicioStrategy`
- ✅ Implementar estrategias concretas
- ✅ Service orchestrator para selección de estrategia
- ✅ Tests de selección dinámica

### Task 4.4: Verificación Docker
```bash
# Construir y levantar servicios
docker-compose up --build -d

# Verificar contenedores
docker-compose ps

# Ver logs
docker-compose logs -f app

# Probar API
curl http://localhost:8080/api/v1/clientes/0001234567/deuda-consolidada

# Verificar métricas
curl http://localhost:8080/actuator/metrics

# Acceder a pgAdmin
# http://localhost:5050

# Acceder a Prometheus
# http://localhost:9090
```

---

## 📊 CRITERIOS DE ÉXITO FASE 4

- ✅ Docker Compose levanta todos los servicios
- ✅ Healthchecks de todos los contenedores en verde
- ✅ API responde correctamente desde Docker
- ✅ Circuit Breaker funciona (probar con archivo inválido)
- ✅ Redis caché funcionando como fallback
- ✅ Strategy Pattern selecciona fuente correcta
- ✅ pgAdmin conecta a PostgreSQL
- ✅ Prometheus recolecta métricas

---

## 🔜 PRÓXIMA FASE

**FASE 5: DOCUMENTACIÓN TÉCNICA EXTENSIVA**
- INFORME.md con justificación de patrones
- README.md completo con instrucciones
- SUSTENTACION.md para presentación
- DEPLOYMENT.md para montaje en pruebas

---

*Documento generado: Octubre 10, 2025*  
*Universidad Autónoma de Occidente - Ingeniería de Software 2*
