# Arquitectura de ServiCiudad Conectada

## Visión Arquitectónica

La arquitectura de ServiCiudad Conectada está diseñada siguiendo los principios de **microservicios**, **diseño dirigido por dominios (DDD)** y **arquitectura hexagonal**, garantizando escalabilidad, mantenibilidad y resiliencia ante la integración con sistemas legacy complejos.

## Principios Arquitectónicos

### 1. Separación de Responsabilidades
Cada microservicio tiene una responsabilidad específica y bien definida, evitando el acoplamiento entre dominios de negocio.

### 2. Integración Anticorrupción
Los sistemas legacy se integran a través de adaptadores que actúan como **capa anticorrupción**, protegiendo la lógica de negocio moderna de las complejidades heredadas.

### 3. Resiliencia por Diseño
Implementación de patrones como **Circuit Breaker**, **Retry** y **Timeout** para garantizar que los fallos de sistemas legacy no comprometan la experiencia del usuario.

### 4. Escalabilidad Horizontal
Cada microservicio puede escalarse independientemente según la demanda y las características de carga específicas.

## Vista de Alto Nivel

```
┌─────────────────┐    ┌─────────────────┐
│   Portal Web    │    │   App Móvil     │
└─────────┬───────┘    └─────────┬───────┘
          │                      │
          └──────────┬───────────┘
                     │
           ┌─────────▼─────────┐
           │   API Gateway     │
           │  (Spring Cloud)   │
           └─────────┬─────────┘
                     │
    ┌────────────────┼────────────────┐
    │                │                │
┌───▼───┐    ┌───▼───┐    ┌────▼────┐
│MS-Auth│    │MS-Pay │    │MS-Bills │
└───┬───┘    └───┬───┘    └────┬────┘
    │            │             │
┌───▼───┐    ┌───▼───┐    ┌────▼────┐
│MS-Notif│   │MS-Incid│   │MS-Admin │
└───────┘    └───────┘    └─────────┘
                │
        ┌───────┼───────┐
        │       │       │
    ┌───▼───┐ ┌─▼─┐ ┌───▼────┐
    │Legacy │ │PSE│ │Modern  │
    │Systems│ │   │ │Systems │
    └───────┘ └───┘ └────────┘
```

## Microservicios

### MS-Clientes (Authentication & User Management)
**Responsabilidad**: Gestión de usuarios, autenticación y autorización

**Tecnologías**:
- Spring Security + OAuth2
- JWT para tokens de sesión
- PostgreSQL para persistencia
- Redis para caché de sesiones

**APIs Principales**:
- `POST /auth/login` - Autenticación de usuarios
- `POST /auth/register` - Registro de nuevos usuarios
- `GET /users/{id}` - Consulta de perfil de usuario
- `PUT /users/{id}` - Actualización de perfil

### MS-Pagos (Payment Processing)
**Responsabilidad**: Procesamiento de pagos unificados con transacciones distribuidas

**Tecnologías**:
- Spring Boot + Resilience4j
- PostgreSQL + Redis
- Apache Kafka para eventos
- Saga Pattern para transacciones distribuidas

**APIs Principales**:
- `POST /pagos/unificado` - Procesamiento de pago multi-servicio
- `GET /pagos/{id}/status` - Consulta de estado de pago
- `POST /pagos/{id}/cancel` - Cancelación de pago

**Integración Legacy**:
- **Mainframe IBM Z**: Adapter con Circuit Breaker
- **Oracle Solaris**: Message Translator para formatos
- **PSE**: Gateway de pagos electrónicos

### MS-Facturación (Billing Management)
**Responsabilidad**: Consolidación y generación de facturas unificadas

**Tecnologías**:
- Spring Boot + JasperReports
- PostgreSQL para facturación
- MongoDB para documentos
- S3-compatible storage para PDFs

**APIs Principales**:
- `GET /facturas/{clienteId}` - Factura unificada del cliente
- `POST /facturas/generate` - Generación de factura PDF
- `GET /facturas/{id}/download` - Descarga de factura

### MS-Notificaciones (Notification Service)
**Responsabilidad**: Gestión de comunicaciones multicanal

**Tecnologías**:
- Spring Boot + Apache Kafka
- Redis para templates
- Integración SMS, Email, Push

**APIs Principales**:
- `POST /notifications/send` - Envío de notificación
- `GET /notifications/{userId}` - Historial de notificaciones
- `PUT /notifications/preferences` - Configuración de preferencias

### MS-Incidencias (Incident Management)
**Responsabilidad**: Gestión de reportes y tickets de soporte

**Tecnologías**:
- Spring Boot + Workflow Engine
- PostgreSQL para tickets
- Content-Based Router para enrutamiento

**APIs Principales**:
- `POST /incidencias` - Creación de nuevo ticket
- `GET /incidencias/{id}` - Consulta de estado de ticket
- `PUT /incidencias/{id}/update` - Actualización de ticket

### MS-Administración (Admin & Audit)
**Responsabilidad**: Gestión administrativa y auditoría

**Tecnologías**:
- Spring Boot + Spring Data JPA
- PostgreSQL para auditoría
- Elasticsearch para logs

**APIs Principales**:
- `GET /admin/metrics` - Métricas del sistema
- `GET /admin/audit` - Registros de auditoría
- `POST /admin/config` - Configuración del sistema

## Integración con Sistemas Legacy

### Mainframe IBM Z (Energía)
```java
@Component
@CircuitBreaker(name = "mainframe-energy")
public class MainframeEnergyAdapter {
    
    @Retryable(maxAttempts = 3)
    public BalanceResponse consultarSaldoEnergia(String clienteId) {
        // Terminal 3270 simulation
        // EBCDIC to JSON translation
        // Circuit breaker protection
    }
}
```

**Características**:
- **Protocolo**: Simulación de terminal 3270
- **Formato**: EBCDIC → JSON (Message Translator)
- **Resiliencia**: Circuit Breaker + Retry
- **Disponibilidad**: 96% (ventana mantenimiento dominical)

### Oracle Solaris (Acueducto)
```java
@Component
@TimeLimiter(name = "oracle-water")
public class OracleWaterAdapter {
    
    @Timeout(duration = 30, unit = ChronoUnit.SECONDS)
    public WaterBillResponse consultarFacturaAgua(String serviceId) {
        // Fixed-width text to JSON
        // PL/SQL procedure calls
        // Timeout protection
    }
}
```

**Características**:
- **Formato**: Texto ancho fijo → JSON
- **Latencia**: 15-30 segundos promedio
- **Resiliencia**: Timeout + Fallback
- **Disponibilidad**: 98%

### Sistemas Modernos (Telecomunicaciones)
```java
@Component
public class TelecomServiceAdapter {
    
    @LoadBalanced
    @RestTemplate
    public TelecomResponse consultarDatosTelecom(String phoneNumber) {
        // REST/SOAP API calls
        // Load balancing
        // Standard retry policies
    }
}
```

## Patrones de Integración

### 1. Adapter Pattern
Encapsula la complejidad de cada sistema legacy en adaptadores específicos.

### 2. Message Translator
Convierte formatos de datos legacy (EBCDIC, texto fijo) a JSON moderno.

### 3. Circuit Breaker
Protege contra fallos en cascada cuando los sistemas legacy están inactivos.

### 4. Content-Based Router
Enruta automáticamente solicitudes según el tipo de servicio o contenido.

### 5. Publish-Subscribe Channel
Desacopla eventos críticos (apagones, mantenimientos) del resto del sistema.

## Seguridad

### Autenticación y Autorización
- **OAuth2 + JWT**: Tokens seguros con expiración
- **RBAC**: Control de acceso basado en roles
- **MFA**: Autenticación multifactor para administradores

### Comunicación Segura
- **TLS 1.3**: Cifrado en tránsito
- **mTLS**: Autenticación mutua entre microservicios
- **API Keys**: Control de acceso a APIs externas

### Cumplimiento
- **ISO 27001**: Gestión de seguridad de la información
- **GDPR**: Protección de datos personales
- **PCI DSS**: Seguridad en procesamiento de pagos

## Monitoreo y Observabilidad

### Métricas de Aplicación
- **Prometheus**: Recolección de métricas
- **Grafana**: Visualización de dashboards
- **Actuator**: Health checks y métricas Spring Boot

### Logging y Tracing
- **ELK Stack**: Centralización de logs
- **Zipkin**: Trazabilidad distribuida
- **Structured Logging**: JSON format con correlation IDs

### Alertas
- **PagerDuty**: Alertas críticas 24/7
- **Slack Integration**: Notificaciones de equipo
- **SLA Monitoring**: Cumplimiento de acuerdos de servicio

## Performance y Escalabilidad

### Caché
- **Redis**: Caché de aplicación L1
- **CDN**: Contenido estático
- **Database Query Cache**: Optimización de consultas

### Escalabilidad
- **Horizontal Pod Autoscaler**: Kubernetes autoscaling
- **Database Read Replicas**: Distribución de carga de lectura
- **Load Balancing**: Distribución inteligente de tráfico

### Optimizaciones
- **Connection Pooling**: HikariCP para base de datos
- **Async Processing**: Kafka para operaciones no críticas
- **Bulk Operations**: Procesamiento por lotes eficiente

## Deployment Architecture

### Contenedores
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ms-pagos
spec:
  replicas: 3
  selector:
    matchLabels:
      app: ms-pagos
  template:
    spec:
      containers:
      - name: ms-pagos
        image: serviciudad/ms-pagos:latest
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
```

### Service Mesh
- **Istio**: Gestión de tráfico entre microservicios
- **Envoy Proxy**: Load balancing y circuit breaking
- **mTLS**: Seguridad automática entre servicios

## Disaster Recovery

### Backup Strategy
- **Database Backups**: Diario con retención de 30 días
- **Configuration Backups**: Versionado en Git
- **Disaster Recovery Site**: Replicación en región secundaria

### High Availability
- **Multi-AZ Deployment**: Distribución en múltiples zonas
- **Database Clustering**: PostgreSQL con réplicas
- **Graceful Degradation**: Funcionalidad reducida ante fallos

---

Esta arquitectura garantiza que ServiCiudad Conectada pueda evolucionar gradualmente desde los sistemas legacy hacia una plataforma moderna, manteniendo la continuidad del negocio y mejorando progresivamente la experiencia del usuario.
