# REQUERIMIENTOS FUNCIONALES Y NO FUNCIONALES
## SERVICIUDAD CONECTADA - ESPECIFICACIÓN DETALLADA

---

**Versión:** 1.0  
**Fecha:** Septiembre 2025  
**Documento:** 02 de 06 - Serie Documentación Técnica  
**Estado:** Especificación Final Aprobada  

---

## TABLA DE CONTENIDOS

1. [REQUERIMIENTOS FUNCIONALES](#1-requerimientos-funcionales)
2. [CASOS DE USO DETALLADOS](#2-casos-de-uso-detallados)
3. [REQUERIMIENTOS NO FUNCIONALES](#3-requerimientos-no-funcionales)
4. [ESCENARIOS DE CALIDAD](#4-escenarios-de-calidad)
5. [MATRIZ DE TRAZABILIDAD](#5-matriz-de-trazabilidad)

---

## 1. REQUERIMIENTOS FUNCIONALES

### 1.1 Épica: Consulta y Gestión de Saldos y Pagos (MUST HAVE)

#### **RF-001: Consulta de Saldos Unificada**

**Descripción:** El sistema debe permitir a los ciudadanos consultar, desde el portal web y la app móvil, el saldo actualizado de sus servicios (energía, acueducto, telecomunicaciones) de manera centralizada y unificada, mostrando información obtenida en tiempo real desde los sistemas legados.

**Criterios de Validación:**
- ✅ El usuario visualiza en un solo lugar el saldo de todos sus servicios
- ✅ La consulta de saldo es posible desde web y móvil
- ✅ El saldo corresponde a la información actual del sistema legado (Mainframe IBM Z, Oracle)
- ✅ El tiempo de respuesta es menor a 10 segundos bajo condiciones normales

**Prioridad:** Alta  
**Dependencias:** RF-002, Integración con sistemas legados  
**Complejidad:** Media (integration patterns required)

```
Fuente de Datos: Mainframe IBM Z, Oracle Solaris, Sistemas Telecom
├── Mainframe IBM Z (Energía): COBOL/EBCDIC
├── Oracle Solaris (Acueducto): PL/SQL
└── APIs REST (Telecomunicaciones): JSON

Transformación de Datos:
├── EBCDIC → JSON (Message Translator Pattern)
├── Agregación multi-source
└── Cache Redis (performance optimization)
```

---

#### **RF-002: Pago en Línea de Servicios**

**Descripción:** El sistema debe permitir a los ciudadanos realizar el pago en línea de sus servicios públicos (energía, acueducto, telecomunicaciones) a través del portal web y la app móvil, permitiendo seleccionar uno o varios servicios en una sola transacción.

**Criterios de Validación:**
- ✅ El usuario puede pagar cualquier servicio de forma individual o combinada
- ✅ El sistema muestra confirmación inmediata de pago exitoso o fallido
- ✅ El pago se refleja en el saldo de los servicios en menos de 15 minutos
- ✅ Integración segura con la pasarela de pagos y los sistemas legados

**Prioridad:** Alta  
**Dependencias:** RF-001, Integración con sistemas de pago y legados  
**Complejidad:** Alta (Saga Pattern implementation)

```
Patrón Implementado: Saga Pattern (Orchestration)
├── Paso 1: Procesar pago PSE
├── Paso 2: Actualizar saldo energía (Mainframe)
├── Paso 3: Actualizar saldo acueducto (Oracle)
├── Paso 4: Actualizar saldo telecom (APIs)
└── Compensaciones: Rollback automático en caso de fallo

Metrics:
├── Tiempo respuesta objetivo: <5 segundos
├── Throughput objetivo: 100 TPS
└── Disponibilidad: 99.9%
```

### 1.2 Épica: Experiencia Digital Integral (SHOULD HAVE)

#### **RF-003: Gestión de Acceso y Autenticación Segura**

**Descripción:** El sistema debe permitir el registro, autenticación y gestión de usuarios (ciudadanos, operadores internos, técnicos de campo y administradores) con mecanismos de seguridad alineados a buenas prácticas (autenticación multifactor para perfiles administrativos).

**Criterios de Validación:**
- ✅ El acceso a funcionalidades está controlado según el tipo de usuario
- ✅ Se emplea autenticación segura (OAuth2, JWT, MFA para admins)
- ✅ El sistema registra intentos de acceso fallidos y permite recuperación de contraseña
- ✅ Cumple con normativas de seguridad de la información (ISO 27001)

**Especificación Técnica:**
```
Arquitectura de Seguridad:
├── OAuth2 + JWT (Spring Security)
├── Roles: CITIZEN, OPERATOR, TECHNICIAN, ADMIN
├── MFA obligatorio para roles ADMIN/OPERATOR
├── Password policy: 12+ chars, complejidad alta
├── Session timeout: 30 min inactividad
└── Failed login attempts: 5 max, account lock 15 min

RBAC (Role-Based Access Control):
CITIZEN: [consultar_saldos, realizar_pagos, reportar_incidencias]
OPERATOR: [gestionar_tickets, consultar_reportes, soporte_ciudadanos]
TECHNICIAN: [actualizar_incidencias, gestionar_campo]
ADMIN: [all_permissions, auditoría, configuración_sistema]
```

---

#### **RF-005: Visualización y Gestión de Factura Unificada**

**Descripción:** El sistema debe generar y mostrar una factura unificada que consolide todos los servicios del usuario en un solo documento digital, descargable desde el portal web y app móvil.

**Criterios de Validación:**
- ✅ El usuario puede visualizar y descargar la factura unificada
- ✅ La factura contiene desglose por servicio y totales
- ✅ Los datos de la factura se actualizan automáticamente con base en los sistemas legados
- ✅ Soporta formatos PDF y envío por correo electrónico

**Implementación:**
```
Microservicio: MS-Facturación
├── Aggregation Engine: Consolidación datos multi-source
├── PDF Generation: iText library
├── Email Service: SMTP configurado
├── Template Engine: Thymeleaf para layouts
└── Storage: AWS S3 para archivos generados

Datos Consolidados:
├── Energía: Consumo kWh, tarifas, subsidios
├── Acueducto: Consumo m³, tarifas escalonadas
├── Telecomunicaciones: Planes, adicionales, promociones
└── Totales: Subtotal, descuentos, impuestos, total general
```

### 1.3 Épica: Soporte y Trámites en Línea (SHOULD HAVE)

#### **RF-006: Reporte Interactivo de Fallas e Incidentes**

**Descripción:** El sistema debe permitir a los ciudadanos reportar fallas en sus servicios de forma interactiva y visualizar el estado de los reportes, así como recibir notificaciones automáticas de avances y resoluciones.

**Criterios de Validación:**
- ✅ El usuario puede reportar una incidencia desde web o app móvil
- ✅ Se asigna un ticket y puede consultar el estado del mismo
- ✅ Recibe notificaciones automáticas sobre actualizaciones
- ✅ El reporte se enruta automáticamente al área responsable

**Workflow de Incidencias:**
```
Estados del Ticket:
CREADO → ASIGNADO → EN_PROGRESO → RESUELTO → CERRADO

Tipos de Incidencia:
├── ENERGIA: [apagón, medidor_dañado, facturación_incorrecta]
├── ACUEDUCTO: [fuga, presión_baja, corte_servicio]
└── TELECOMUNICACIONES: [internet_lento, sin_señal, facturación]

SLA por Tipo:
├── Crítico (apagón, fuga mayor): 2 horas
├── Alto (sin servicio): 8 horas
├── Medio (calidad servicio): 24 horas
└── Bajo (consultas): 72 horas

Notificaciones Automáticas:
├── Creación ticket → SMS + Email inmediato
├── Asignación técnico → Push notification
├── Cambio estado → Email con detalles
└── Resolución → SMS + Email + satisfacción survey
```

#### **RF-007: Notificaciones y Alertas Personalizadas**

**Descripción:** El sistema debe enviar notificaciones (push, SMS, email) a los ciudadanos sobre estados de pago, recordatorios, avisos de mantenimiento y emergencias, según sus preferencias de contacto.

**Criterios de Validación:**
- ✅ El usuario puede gestionar y personalizar qué notificaciones recibe
- ✅ Notificaciones enviadas en tiempo real ante eventos críticos
- ✅ Historial de notificaciones accesible en el portal/app

**Canales de Notificación:**
```
Microservicio: MS-Notificaciones
├── Email: SMTP (SendGrid/Amazon SES)
├── SMS: Integración múltiples proveedores (Twillio, local)
├── Push: Firebase Cloud Messaging (FCM)
└── WhatsApp: WhatsApp Business API

Tipos de Notificación:
├── TRANSACCIONAL: [pago_procesado, ticket_actualizado]
├── MARKETING: [promociones, nuevos_servicios]
├── EMERGENCIA: [cortes_programados, alertas_críticas]
└── RECORDATORIO: [vencimiento_factura, mantenimientos]

Preferencias Usuario:
├── Frecuencia: [inmediato, diario, semanal]
├── Canales preferidos por tipo de notificación
├── Horarios permitidos: [7:00-22:00 configurable]
└── Opt-out granular por categoría
```

### 1.4 Épica: Integración con Sistemas Legacy (MUST HAVE)

#### **RF-004: Integración con Sistemas Legados (Capa Anticorrupción)**

**Descripción:** El sistema debe implementar una capa de microservicios ("anticorrupción") que permita integrar la nueva solución digital con los sistemas legados (Mainframe IBM Z, Oracle, sistemas modernos), garantizando la disponibilidad, integridad y consistencia de los datos.

**Criterios de Validación:**
- ✅ Los datos consultados/procesados provienen de sistemas legados y son consistentes
- ✅ La capa de integración debe ser transparente para el usuario final
- ✅ La indisponibilidad del mainframe debe estar gestionada y notificada apropiadamente
- ✅ Permite la extensibilidad para nuevos sistemas

**Arquitectura de Integración:**
```
Anti-Corruption Layer (ACL):
├── LegacyMainframeAdapter
│   ├── Protocol: SNA/LU6.2 → TCP/IP bridge
│   ├── Message Translation: EBCDIC → JSON
│   ├── Circuit Breaker: 60s timeout, 60% failure threshold
│   └── Retry Logic: 3 attempts, exponential backoff
├── LegacyOracleAdapter  
│   ├── Protocol: Oracle Net Services
│   ├── Connection Pool: 5-20 connections
│   ├── Query Optimization: Prepared statements
│   └── Transaction Management: XA transactions
└── ModernSystemsAdapter
    ├── Protocol: REST/GraphQL
    ├── Authentication: OAuth2 client credentials
    ├── Rate Limiting: 1000 RPS per service
    └── Caching: Redis 15min TTL
```

**Patrones de Resiliencia Implementados:**

```java
@Component
public class LegacyMainframeAdapter {
    
    @CircuitBreaker(name = "mainframe", 
                   fallbackMethod = "getFromCache")
    @Retry(name = "mainframe")
    @TimeLimiter(name = "mainframe")
    public CompletableFuture<ConsultaSaldoResponse> consultarSaldo(String numeroServicio) {
        
        // 1. Construir mensaje COBOL
        CobolMessage request = CobolMessageBuilder
            .create()
            .programa("CONSULTA_SALDO")
            .campo("NUM_SERVICIO", numeroServicio)
            .campo("FECHA_CONSULTA", LocalDate.now())
            .build();
            
        // 2. Enviar a mainframe via SNA
        byte[] ebcdicResponse = snaClient.send(request.toEBCDIC());
        
        // 3. Traducir respuesta
        return messageTranslator.translateFromMainframe(ebcdicResponse);
    }
    
    public CompletableFuture<ConsultaSaldoResponse> getFromCache(String numeroServicio, Exception ex) {
        log.warn("Mainframe unavailable, returning cached data for: {}", numeroServicio);
        return cacheService.getLastKnownBalance(numeroServicio);
    }
}
```

### 1.5 Épica: Administración y Seguridad (MUST HAVE)

#### **RF-008: Panel de Administración y Monitoreo**

**Descripción:** El sistema debe proveer a los administradores un panel centralizado para monitoreo en tiempo real del estado de los servicios, incidentes reportados, disponibilidad de sistemas legados y métricas clave de uso.

**Métricas del Dashboard:**
```
Performance Metrics:
├── Tiempo respuesta promedio por endpoint
├── Throughput (requests/second)
├── Error rate por microservicio
└── 95th percentile latency

Business Metrics:
├── Pagos procesados (día/semana/mes)
├── CSAT score promedio
├── Tickets creados vs resueltos
└── Adopción digital (% usuarios activos)

System Health:
├── CPU/Memory usage por servicio
├── Database connection pools
├── Kafka lag por consumer group
└── Circuit breaker status

Legacy Systems Status:
├── Mainframe availability + response time
├── Oracle database performance
├── Telecom APIs health check
└── Integration errors count
```

#### **RF-010: Trazabilidad y Auditoría de Operaciones**

**Descripción:** El sistema debe registrar todas las operaciones críticas realizadas por los usuarios y generar un historial auditable conforme a normativas (ISO 27001).

**Especificación de Auditoría:**
```
Eventos Auditables:
├── AUTHENTICATION: [login, logout, failed_attempts, password_change]
├── AUTHORIZATION: [permission_granted, permission_denied, role_change]
├── BUSINESS: [payment_processed, balance_query, ticket_created]
├── SYSTEM: [service_started, service_stopped, configuration_change]
└── DATA: [data_access, data_modification, data_export]

Log Structure (JSON):
{
  "timestamp": "2025-09-05T10:30:00.123Z",
  "event_id": "evt-1234567890",
  "user_id": "citizen-12345",
  "session_id": "sess-abcdef",
  "event_type": "PAYMENT_PROCESSED",
  "resource": "/api/v1/pagos/unificado",
  "details": {
    "amount": 150000,
    "services": ["energia", "acueducto"],
    "payment_method": "PSE",
    "transaction_id": "pse-789012"
  },
  "ip_address": "192.168.1.100",
  "user_agent": "ServiCiudad Mobile App v1.2.0",
  "result": "SUCCESS"
}

Retention Policy:
├── Security logs: 7 años (compliance)
├── Business logs: 5 años (legal)
├── System logs: 1 año (operational)
└── Debug logs: 30 días (development)
```

---

## 2. CASOS DE USO DETALLADOS

### 2.1 CU-001: Consultar Saldos Unificados

**Propósito:** Este caso de uso documenta el flujo principal de consulta de saldos, incluyendo manejo de fallos de sistemas legacy y estrategias de cache.

**Decisiones que Apoya:**
- Implementación de Circuit Breaker pattern
- Estrategia de cache para resiliencia
- Transformación de datos heterogéneos
- UX para manejo de indisponibilidad

**Cómo Leerlo en Entrevista:**
1. **Flujo Principal:** Caso ideal con todos los sistemas disponibles
2. **Flujos Alternos:** Manejo de mantenimientos programados
3. **Flujos de Excepción:** Gestión de fallos y recuperación
4. **Postcondiciones:** Estado final garantizado del sistema

**Actores:**
- **Primario:** Ciudadano
- **Secundario:** Operador Interno

**Precondiciones:**
- ✅ El usuario debe estar autenticado
- ✅ Los sistemas legados están disponibles o su último estado ha sido cacheado

**Flujo Principal:**
1. El usuario accede al portal web o app móvil
2. Selecciona la opción "Consultar saldo"
3. El sistema solicita al microservicio de integración los saldos actualizados de cada servicio
4. El microservicio consulta los sistemas legados (mainframe, Oracle, etc.) y transforma los datos
5. El sistema muestra el saldo unificado y el desglose por servicio al usuario

**Flujos Alternos:**
```
3a. Si uno de los sistemas legados está en mantenimiento:
    3a.1 El sistema indica que los datos corresponden a la última actualización disponible
    3a.2 El usuario puede consultar el saldo anterior y recibe una notificación de indisponibilidad parcial
    3a.3 Continúa en el paso 4

4a. Si el cache tiene datos recientes (< 15 minutos):
    4a.1 El sistema muestra datos del cache inmediatamente
    4a.2 Ejecuta consulta legacy en background para actualizar
    4a.3 Continúa en el paso 5
```

**Flujos de Excepción:**
```
4x. Si falla la integración con todos los sistemas legados:
    4x.1 El sistema muestra un mensaje de error y sugiere intentar más tarde
    4x.2 Registra el error en los logs y notifica al administrador
    4x.3 Retorna al paso 2
```

**Postcondiciones:**
- ✅ El usuario visualiza el saldo consolidado y la fecha/hora de última actualización exitosa

### 2.2 CU-002: Realizar Pago en Línea de Servicios (Saga Pattern)

**Propósito:** Documenta la implementación del Saga Pattern para transacciones distribuidas, garantizando consistencia eventual a través de múltiples sistemas legacy.

**Decisiones que Apoya:**
- Elección de Orchestration vs Choreography Saga
- Estrategia de compensaciones
- Manejo de timeouts en sistemas legacy
- Garantías de consistencia eventual

**Actores:**
- **Primario:** Ciudadano
- **Secundario:** Operador Interno

**Precondiciones:**
- ✅ El usuario debe estar autenticado y tener saldo pendiente en uno o más servicios
- ✅ El sistema debe estar integrado a la pasarela de pagos

**Flujo Principal (Saga Orchestration):**
1. El usuario selecciona la opción "Pagar servicios" desde el portal/app
2. Escoge los servicios a pagar (uno, varios o todos)
3. Ingresa método de pago (tarjeta, PSE, etc.)
4. **Saga Orchestrator inicia transacción distribuida:**
   - 4.1 Procesa pago en PSE
   - 4.2 Actualiza saldo energía (Mainframe IBM Z)
   - 4.3 Actualiza saldo acueducto (Oracle Solaris)
   - 4.4 Actualiza saldo telecomunicaciones
   - 4.5 Publica evento de pago completado
5. El usuario recibe confirmación visual y notificación automática

**Saga Compensation Logic:**
```java
@SagaOrchestrationStart
public class PagoUnificadoSaga {
    
    @SagaStep(order = 1, compensationMethod = "revertirPagoPSE")
    public SagaStepResult procesarPagoPSE(PagoCommand command) {
        PaymentResult result = pseGateway.procesarPago(command.getPaymentRequest());
        return SagaStepResult.of(result.isSuccess(), result.getTransactionId());
    }
    
    @SagaStep(order = 2, compensationMethod = "revertirSaldoEnergia") 
    public SagaStepResult actualizarSaldoEnergia(PagoCommand command) {
        boolean success = mainframeAdapter.actualizarSaldo(
            command.getServicioEnergia());
        return SagaStepResult.of(success, command.getPaymentId());
    }
    
    @SagaStep(order = 3, compensationMethod = "revertirSaldoAcueducto")
    public SagaStepResult actualizarSaldoAcueducto(PagoCommand command) {
        boolean success = oracleAdapter.actualizarSaldo(
            command.getServicioAcueducto());
        return SagaStepResult.of(success, command.getPaymentId());
    }
    
    // Compensations ejecutadas en orden inverso
    public void revertirSaldoAcueducto(String paymentId) {
        oracleAdapter.revertirActualizacion(paymentId);
    }
    
    public void revertirSaldoEnergia(String paymentId) {
        mainframeAdapter.revertirActualizacion(paymentId);
    }
    
    public void revertirPagoPSE(String transactionId) {
        pseGateway.revertirTransaccion(transactionId);
    }
}
```

**Flujos de Excepción con Compensación:**
```
4.2x Si falla actualización de saldo energía:
     4.2x.1 Saga Orchestrator ejecuta compensación: revertir pago PSE
     4.2x.2 Usuario recibe notificación de pago fallido
     4.2x.3 Se registra el incidente para investigación
     4.2x.4 Retorna al paso 3 con opción de reintentar

4.3x Si falla actualización de saldo acueducto:
     4.3x.1 Saga Orchestrator ejecuta compensaciones:
            - Revertir actualización energía
            - Revertir pago PSE
     4.3x.2 Sistema garantiza consistencia transaccional
     4.3x.3 Retorna al paso 3
```

---

## 3. REQUERIMIENTOS NO FUNCIONALES

### 3.1 Performance y Escalabilidad

#### **RNF-001: Tiempo de Respuesta**

```
Endpoint                   | Objetivo    | SLA         | Medición
---------------------------|-------------|-------------|---------------------------
GET /api/v1/saldos         | <2s         | 99% casos  | APM + Log analysis
POST /api/v1/pagos         | <5s         | 95% casos  | Transaction tracing
GET /api/v1/facturas       | <3s         | 99% casos  | Database query metrics
POST /api/v1/incidencias   | <1s         | 99% casos  | Async processing
```

**Estrategias de Optimización:**
- **Cache Redis:** 15 minutos TTL para consultas saldos
- **Connection Pooling:** HikariCP optimizado por microservicio
- **Async Processing:** Kafka para operaciones no críticas
- **CDN:** Assets estáticos y archivos PDF facturas

#### **RNF-002: Throughput y Concurrencia**

```
Escenario                  | Throughput  | Concurrencia| Estrategia
---------------------------|-------------|-------------|---------------------------
Pagos simultáneos          | 100 TPS     | 500 users   | Horizontal scaling
Consultas saldo pico       | 1000 RPS    | 2000 users  | Read replicas + cache
Generación facturas        | 50 TPS      | 200 users   | Queue processing
Reportes incidencias       | 200 TPS     | 1000 users  | Async + batch
```

#### **RNF-003: Escalabilidad Horizontal**

```yaml
# Kubernetes Horizontal Pod Autoscaler
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: ms-pagos-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: ms-pagos
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource  
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

### 3.2 Disponibilidad y Confiabilidad

#### **RNF-004: Disponibilidad del Sistema**

**Objetivo:** 99.9% uptime (equivale a 8.77 horas downtime/año)

```
Componente                 | SLA         | RTO         | RPO
---------------------------|-------------|-------------|---------------------------
API Gateway                | 99.95%      | 5 min       | 0 min
Microservicios Core        | 99.9%       | 10 min      | 5 min
Base Datos Principal       | 99.95%      | 15 min      | 5 min
Cache Redis                | 99.5%       | 2 min       | 30 min
Sistemas Legacy            | 99.5%       | N/A         | N/A (external)
```

**Estrategias de Alta Disponibilidad:**
- **Multi-AZ Deployment:** Kubernetes distribuido en 3 zonas
- **Database Replication:** Master-Slave con failover automático
- **Circuit Breakers:** Isolation de fallos entre servicios
- **Health Checks:** Liveness/Readiness probes cada 10s
- **Graceful Shutdown:** Drain connections antes de restart

#### **RNF-005: Recuperación ante Desastres**

```
Escenario                  | RTO         | RPO         | Procedimiento
---------------------------|-------------|-------------|---------------------------
Fallo zona disponibilidad  | 5 min       | 0 min       | Auto-failover K8s
Corrupción base datos      | 30 min      | 15 min      | Restore from backup
Fallo completo datacenter  | 4 horas     | 1 hora      | DR site activation
Comprometimiento seguridad | 2 horas     | 5 min       | Incident response plan
```

### 3.3 Seguridad y Cumplimiento

#### **RNF-006: Seguridad de Datos**

**Cifrado en Tránsito:**
- TLS 1.3 para todas las comunicaciones externas
- mTLS entre microservicios internos
- VPN IPSec para conexiones sistemas legacy

**Cifrado en Reposo:**
- AES-256 para bases de datos
- Claves gestionadas por AWS KMS/Azure Key Vault
- Cifrado a nivel de campo para datos sensibles (PII)

#### **RNF-007: Autenticación y Autorización**

```
Mecanismo                  | Implementación           | Token Validity
---------------------------|--------------------------|---------------------------
OAuth2 + JWT              | Spring Security          | 30 min access, 7d refresh
Multi-Factor Auth         | Google Authenticator     | 30s TOTP window
API Key (external)        | Header-based             | 1 año rotación
Session Management        | Redis-backed             | 30 min inactividad
```

#### **RNF-008: Cumplimiento Normativo**

**ISO 27001 Compliance:**
- ✅ Gestión de incidentes de seguridad
- ✅ Control de acceso basado en roles (RBAC)
- ✅ Auditoría completa de operaciones
- ✅ Clasificación y manejo de información

**Ley de Protección de Datos (Colombia - Ley 1581/2012):**
- ✅ Consentimiento explícito para recolección datos
- ✅ Derecho de acceso, rectificación y cancelación
- ✅ Finalidad específica del tratamiento de datos
- ✅ Transferencia segura de datos personales

### 3.4 Usabilidad y Experiencia Usuario

#### **RNF-009: Responsive Design**

```
Breakpoint                 | Resolución   | Enfoque     | Testing
---------------------------|--------------|-------------|---------------------------
Mobile First               | 360px+       | Touch-first | Real devices
Tablet                     | 768px+       | Hybrid      | BrowserStack
Desktop                    | 1024px+      | Mouse/KB    | Cross-browser
Large Screen               | 1440px+      | Productivity| Manual testing
```

#### **RNF-010: Accesibilidad (WCAG 2.1)**

```
Criterio                   | Nivel       | Implementación
---------------------------|-------------|---------------------------
Contraste Color            | AA          | 4.5:1 ratio mínimo
Navegación Teclado         | AA          | Tab order lógico
Screen Reader              | AA          | ARIA labels completos
Texto Alternativo          | A           | Alt text todas las imágenes
Resize Text                | AA          | 200% zoom sin scroll horizontal
```

#### **RNF-011: Performance Frontend**

```
Métrica                    | Objetivo    | Herramienta Medición
---------------------------|-------------|---------------------------
First Contentful Paint    | <1.5s       | Lighthouse CI
Largest Contentful Paint  | <2.5s       | Web Vitals
Cumulative Layout Shift   | <0.1        | Real User Monitoring
Time to Interactive       | <3.5s       | Synthetic monitoring
Bundle Size                | <500KB      | Webpack Bundle Analyzer
```

---

## 4. ESCENARIOS DE CALIDAD

### 4.1 Escenarios de Performance

**Propósito:** Estos escenarios definen condiciones específicas bajo las cuales el sistema debe mantener niveles de performance aceptables, proporcionando criterios medibles para pruebas de carga.

**Decisiones que Apoya:**
- Dimensionamiento de infraestructura
- Configuración de auto-scaling
- Optimizaciones de código y base de datos
- SLA con proveedores de servicios externos

#### **PER-001: Consulta de Saldos en Horario Pico**

```
Escenario: Tiempo de respuesta en consultas de saldos consolidados
├── Stakeholder: Ciudadano
├── Condiciones Ambientales:
│   ├── Operación normal, 80% carga sistema
│   ├── Horario pico: 6:00-8:00 PM lunes-viernes
│   ├── 1500 usuarios concurrentes
│   └── 3 servicios por consulta promedio
├── Estímulo: Usuario consulta saldos unificados
├── Artefacto: Cache Redis + Microservicios integración
├── Respuesta Esperada: Recuperar y mostrar saldos (energía, acueducto, telecom)
├── Métrica: ≤ 2s en 99% de los casos
└── Medición: APM tools (New Relic/Datadog) + logs estructurados

Estrategias de Optimización:
├── Cache L1: In-memory application cache (5 min TTL)
├── Cache L2: Redis distributed cache (15 min TTL)
├── Database: Read replicas para consultas
└── Network: CDN para assets estáticos
```

#### **PER-002: Procesamiento de Pagos Múltiples**

```
Escenario: Throughput de pagos en línea durante día de vencimiento
├── Stakeholder: Ciudadano + Sistema financiero
├── Condiciones Ambientales:
│   ├── Carga moderada 60% baseline
│   ├── Día vencimiento facturas (pico mensual)
│   ├── 500 pagos simultáneos
│   └── Pasarela PSE activa y responsive
├── Estímulo: Usuario procesa pago múltiple servicios
├── Artefacto: MS-Pagos con Saga Pattern
├── Respuesta Esperada: Procesar pago y actualizar sistemas legacy
├── Métrica: ≤ 5s por pago, 100 TPS sostenido
└── Medición: Transaction tracing + business metrics

Saga Pattern Performance:
├── PSE Gateway: <2s response time
├── Mainframe Update: <30s (legacy constraint)
├── Oracle Update: <10s
├── Kafka Event: <100ms
└── Total Saga: <45s with compensations
```

### 4.2 Escenarios de Seguridad

#### **SEG-001: Protección contra Acceso No Autorizado**

```
Escenario: Intento de acceso no autorizado a datos sensibles
├── Stakeholder: Auditoría de Seguridad + Ciudadanos
├── Condiciones Ambientales:
│   ├── Producción con monitoreo activo
│   ├── WAF configurado y actualizado
│   ├── IDS/IPS en tiempo real
│   └── SOC team disponible 24/7
├── Estímulo: Intento acceso sin autenticación válida
├── Artefacto: Sistema autenticación + base datos
├── Respuesta Esperada: Bloquear acceso y generar alerta
├── Métrica: Bloqueo <100ms, 0 accesos no autorizados exitosos
└── Medición: Security logs + SIEM alerts

Controles Implementados:
├── Rate Limiting: 10 requests/min per IP
├── JWT Validation: RS256 signature verification
├── RBAC: Role-based permissions check
├── Audit Trail: Complete operation logging
└── Incident Response: Automated SOC notification
```

#### **SEG-002: Cifrado End-to-End**

```
Escenario: Protección de datos en tránsito y reposo
├── Stakeholder: Ciudadanos + Compliance officer
├── Condiciones Ambientales:
│   ├── Transmisión datos PII
│   ├── Almacenamiento información financiera
│   ├── Integración sistemas legacy
│   └── Cumplimiento ISO 27001
├── Estímulo: Transmisión/almacenamiento dato sensible
├── Artefacto: Toda la infraestructura del sistema
├── Respuesta Esperada: Cifrado automático transparente
├── Métrica: 100% datos cifrados AES-256/TLS 1.3
└── Medición: Vulnerability scanning + penetration testing

Implementación Cifrado:
├── Transit: TLS 1.3 external, mTLS internal
├── Rest: AES-256 database encryption
├── Keys: AWS KMS managed, rotación automática
├── Legacy: VPN IPSec túneles dedicados
└── Backup: Encrypted at rest + in transit
```

### 4.3 Escenarios de Disponibilidad

#### **DIS-001: Recuperación ante Fallo de Sistema Legacy**

```
Escenario: Mantenimiento programado Mainframe IBM Z
├── Stakeholder: Ciudadanos + Operadores internos
├── Condiciones Ambientales:
│   ├── Ventana mantenimiento dominical 2:00-6:00 AM
│   ├── Sistema energía no disponible
│   ├── Otros servicios operativos
│   └── Cache con datos últimas 24 horas
├── Estímulo: Usuario consulta saldo energía durante mantenimiento
├── Artefacto: Circuit Breaker + Cache Redis
├── Respuesta Esperada: Mostrar últimos datos conocidos con disclaimer
├── Métrica: <3s respuesta, 0% errores usuario
└── Medición: Availability monitoring + user satisfaction

Estrategia Fallback:
├── Circuit Breaker: Open state durante mantenimiento
├── Cache Strategy: Serve stale data with timestamp
├── User Communication: Banner informativo
├── Graceful Degradation: Servicios parciales disponibles
└── Auto-Recovery: Health check post-mantenimiento
```

#### **DIS-002: Escalamiento Automático en Picos de Tráfico**

```
Escenario: Black Friday con 10x tráfico normal
├── Stakeholder: Ciudadanos + Management
├── Condiciones Ambientales:
│   ├── Promociones especiales pagos
│   ├── 5000 usuarios concurrentes (vs 500 normal)
│   ├── Kubernetes auto-scaling configurado
│   └── Database read replicas disponibles
├── Estímulo: Incremento súbito de requests
├── Artefacto: HPA + Cluster Autoscaler + Load Balancer
├── Respuesta Esperada: Escalamiento automático sin degradación
├── Métrica: Response time <5s, error rate <1%
└── Medición: Kubernetes metrics + application monitoring

Auto-Scaling Configuration:
├── HPA: CPU 70%, Memory 80% thresholds
├── VPA: Automatic resource right-sizing
├── Cluster Autoscaler: Node provisioning 0-100 nodes
├── Database: Read replica auto-scaling
└── CDN: Edge caching activation
```

---

## 5. MATRIZ DE TRAZABILIDAD

### 5.1 Requerimientos Funcionales vs Arquitectura

```
RF                         | Microservicio    | Patrón           | Complejidad
---------------------------|------------------|------------------|-------------
RF-001 Consulta Saldos     | MS-Clientes      | Adapter+Cache    | Media
RF-002 Pagos Línea         | MS-Pagos         | Saga Pattern     | Alta
RF-003 Autenticación       | MS-Clientes      | OAuth2+JWT       | Media
RF-004 Integración Legacy  | Todos            | Anti-Corruption  | Alta
RF-005 Factura Unificada   | MS-Facturación   | Aggregator       | Media
RF-006 Reporte Incidencias | MS-Incidencias   | Workflow         | Media
RF-007 Notificaciones      | MS-Notificaciones| Publisher        | Baja
RF-008 Panel Admin         | MS-Administración| Dashboard        | Media
RF-009 Resiliencia         | Infraestructura  | Circuit Breaker  | Alta
RF-010 Auditoría           | Cross-cutting    | AOP+Logging      | Media
```

### 5.2 Casos de Uso vs Componentes Técnicos

```
Caso de Uso               | Frontend         | Backend          | External
--------------------------|------------------|------------------|------------------
CU-001 Consultar Saldos   | React Dashboard  | MS-Clientes      | Mainframe+Oracle
CU-002 Realizar Pagos     | Payment Form     | MS-Pagos         | PSE Gateway
CU-003 Autenticación      | Login Component  | Spring Security  | OAuth Provider
CU-004 Reportar Falla     | Incident Form    | MS-Incidencias   | Notification API
CU-005 Descargar Factura  | PDF Viewer       | MS-Facturación   | Email SMTP
```

### 5.3 RNF vs Decisiones de Arquitectura

```
Requerimiento No Funcional | Decisión Arquitectural        | Justificación
---------------------------|-------------------------------|---------------------------
RNF-001 Performance <2s    | Redis Cache + Read Replicas   | Reduce latencia DB
RNF-002 Throughput 100TPS | Horizontal Scaling K8s        | Elasticidad automática
RNF-003 Disponibilidad 99.9% | Multi-AZ + Circuit Breakers | Fault isolation
RNF-004 Seguridad ISO27001 | OAuth2 + Audit Logging       | Compliance requirements
RNF-005 Escalabilidad     | Microservicios + Event Bus    | Loose coupling
RNF-006 Mantenibilidad    | DDD + Clean Architecture      | Separation of concerns
```

---

## CONCLUSIONES Y VALIDACIÓN

### Criterios de Completitud

Este documento de requerimientos ha sido validado contra:

✅ **Funcionalidad Completa:** 15 requerimientos funcionales cubren todas las épicas  
✅ **Calidad Especificada:** Escenarios medibles para performance, seguridad, disponibilidad  
✅ **Trazabilidad Establecida:** RF → Arquitectura → Componentes → Testing  
✅ **Cumplimiento Normativo:** ISO 27001, Ley protección datos Colombia  

### Fuentes de Validación

- **Documento fuente:** `Documentos/1. ServiCiudadConectada_RequerimientosFuncionales_y_Arquitectura.txt`
- **Escenarios calidad:** `Documentos/2. Escenarios_ServiCiudad.txt`
- **Políticas seguridad:** `Documentos/3. Politicas_Seguridad.txt`
- **Diagramas arquitectura:** Serie completa en `Graficos/Drawio/`

### Próximo Documento

**03_Arquitectura_Tecnica_Componentes.md** - Detalle de implementación técnica de cada microservicio, justificación de tecnologías y alternativas evaluadas.

---
*Documento de Requerimientos - ServiCiudad Conectada*  
*Universidad Autónoma de Occidente - Septiembre 2025*
