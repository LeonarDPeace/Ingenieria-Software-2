# ARQUITECTURA TÉCNICA Y COMPONENTES
## SERVICIUDAD CONECTADA - DISEÑO DE MICROSERVICIOS

---

**Versión:** 1.0  
**Fecha:** Septiembre 2025  
**Documento:** 03 de 06 - Serie Documentación Técnica  
**Estado:** Diseño Técnico Aprobado  

---

## TABLA DE CONTENIDOS

1. [ARQUITECTURA GENERAL](#1-arquitectura-general)
2. [MICROSERVICIOS CORE](#2-microservicios-core)
3. [COMPONENTES DE INFRAESTRUCTURA](#3-componentes-de-infraestructura)
4. [ADAPTADORES LEGACY](#4-adaptadores-legacy)
5. [JUSTIFICACIÓN TECNOLÓGICA](#5-justificación-tecnológica)

---

## 1. ARQUITECTURA GENERAL

### 1.1 Diagrama de Arquitectura Mejorada

**Propósito del Diagrama:** Este diagrama proporciona la vista técnica completa de la arquitectura de microservicios ServiCiudad Conectada, mostrando la distribución por capas, patrones de comunicación y integración con sistemas legacy.

**Decisiones que Apoya:**
- Separación clara de responsabilidades por capa
- Patrón de comunicación asíncrona via Kafka
- Estrategia de integración con sistemas legacy
- Distribución de la lógica de negocio en dominios

**Cómo Leerlo en Entrevista:**
1. **Capa Superior:** Frontend (React Web + React Native Mobile)
2. **Capa Gateway:** API Gateway como punto de entrada único
3. **Capa Microservicios:** Servicios organizados por dominio de negocio
4. **Capa Datos:** Bases de datos especializadas por microservicio
5. **Capa Legacy:** Adaptadores para sistemas existentes

**Fuente:** `Graficos/Drawio/Proyecto_General/1. DiagramaMejorado_ServiCiudadConectada_Arquitectura.drawio`

### 1.2 Principios Arquitectónicos Aplicados

#### **Domain-Driven Design (DDD)**
```
Bounded Context: GESTIÓN CIUDADANOS
├── MS-Clientes
│   ├── Entities: Usuario, Perfil, Sesion
│   ├── Value Objects: Email, Telefono, Documento
│   ├── Aggregates: UsuarioAggregate
│   └── Domain Services: AutenticacionService

Bounded Context: SERVICIOS FINANCIEROS
├── MS-Pagos
│   ├── Entities: Payment, Transaction, SagaState
│   ├── Value Objects: Money, PaymentMethod, TransactionId
│   ├── Aggregates: PaymentAggregate
│   └── Domain Services: SagaOrchestrator
├── MS-Facturación
│   ├── Entities: Invoice, InvoiceItem
│   ├── Value Objects: BillingPeriod, TaxRate
│   └── Domain Services: ConsolidationService
```

#### **Hexagonal Architecture (Ports & Adapters)**
```
Aplicación por Microservicio:
├── Domain Layer (Core Business Logic)
│   ├── Entities
│   ├── Value Objects
│   ├── Domain Services
│   └── Repository Interfaces (Ports)
├── Application Layer (Use Cases)
│   ├── Command Handlers
│   ├── Query Handlers
│   ├── Application Services
│   └── DTOs
├── Infrastructure Layer (Adapters)
│   ├── REST Controllers (Inbound Adapters)
│   ├── Database Repositories (Outbound Adapters)
│   ├── External API Clients (Outbound Adapters)
│   └── Message Publishers/Consumers (Outbound Adapters)
```

### 1.3 Patrones de Comunicación

#### **Comunicación Síncrona (Request-Response)**
```
Patrón: REST APIs con OpenAPI 3.0
├── Cliente → API Gateway → Microservicio
├── Timeout: 30s general, 60s para legacy adapters
├── Circuit Breaker: Resilience4j implementation
├── Retry: Exponential backoff, max 3 attempts
└── Load Balancing: Round-robin with health checks

Casos de Uso Síncronos:
├── Consulta saldos (lectura crítica)
├── Autenticación/autorización (security)
├── Validaciones en tiempo real
└── APIs admin dashboard
```

#### **Comunicación Asíncrona (Event-Driven)**
```
Patrón: Event Sourcing + CQRS con Apache Kafka
├── Producer → Kafka Topic → Consumer
├── Partitioning: Por customer_id para ordering
├── Replication: Factor 3 para alta disponibilidad
├── Retention: 7 días para eventos business
└── Dead Letter Queue: Manejo de mensajes fallidos

Eventos Principales:
├── PagoCompletadoEvent (MS-Pagos → MS-Facturación)
├── UsuarioRegistradoEvent (MS-Clientes → MS-Notificaciones)
├── IncidenciaReportadaEvent (MS-Incidencias → MS-Notificaciones)
└── SistemaLegacyNoDisponibleEvent (Adapters → MS-Administración)
```

---

## 2. MICROSERVICIOS CORE

### 2.1 MS-Clientes (Gestión de Usuarios)

#### **Responsabilidades Principales:**
- Autenticación y autorización (OAuth2 + JWT)
- Gestión de perfiles de usuario
- Control de acceso basado en roles (RBAC)
- Sesiones y tokens de seguridad

#### **Stack Tecnológico:**
```
Backend Framework: Spring Boot 3.2.0
├── Security: Spring Security 6.x + OAuth2
├── Database: PostgreSQL 15.x
├── Cache: Redis 7.x (sessions + tokens)
├── Messaging: Kafka Producer/Consumer
└── Documentation: OpenAPI 3.0 + Swagger UI

Dependencias Clave:
├── spring-boot-starter-security
├── spring-boot-starter-oauth2-resource-server
├── spring-boot-starter-data-jpa
├── spring-boot-starter-cache
└── spring-kafka
```

#### **Modelo de Datos:**
```sql
-- Tabla principal de usuarios
CREATE TABLE usuarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    numero_identificacion VARCHAR(20) UNIQUE NOT NULL,
    tipo_documento VARCHAR(10) NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    telefono VARCHAR(15),
    fecha_nacimiento DATE,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_ultima_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Roles del sistema
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    descripcion TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Relación usuarios-roles (many-to-many)
CREATE TABLE usuario_roles (
    usuario_id UUID REFERENCES usuarios(id),
    rol_id INTEGER REFERENCES roles(id),
    fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (usuario_id, rol_id)
);

-- Sesiones activas
CREATE TABLE sesiones_usuario (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID REFERENCES usuarios(id),
    jwt_token_id VARCHAR(255) UNIQUE NOT NULL,
    ip_address INET,
    user_agent TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_expiracion TIMESTAMP NOT NULL,
    activa BOOLEAN DEFAULT TRUE
);
```

#### **APIs Principales:**
```yaml
# OpenAPI 3.0 Specification
/api/v1/auth:
  post:
    summary: Autenticar usuario
    operationId: authenticateUser
    requestBody:
      content:
        application/json:
          schema:
            type: object
            properties:
              email:
                type: string
                format: email
              password:
                type: string
                minLength: 8
    responses:
      200:
        description: Autenticación exitosa
        content:
          application/json:
            schema:
              type: object
              properties:
                access_token:
                  type: string
                refresh_token:
                  type: string
                expires_in:
                  type: integer
                user_info:
                  $ref: '#/components/schemas/UserInfo'

/api/v1/users/{userId}/profile:
  get:
    summary: Obtener perfil de usuario
    security:
      - BearerAuth: []
    parameters:
      - name: userId
        in: path
        required: true
        schema:
          type: string
          format: uuid
    responses:
      200:
        description: Perfil de usuario
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/UserProfile'
```

#### **Implementación Clave - Autenticación:**
```java
@RestController
@RequestMapping("/api/v1/auth")
@Validated
public class AuthenticationController {

    @Autowired
    private AuthenticationService authService;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(
            @Valid @RequestBody LoginRequest loginRequest) {
        
        // 1. Validar credenciales
        Authentication authentication = authService.authenticate(
            loginRequest.getEmail(), 
            loginRequest.getPassword()
        );
        
        // 2. Generar JWT tokens
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        
        // 3. Crear sesión
        UserSession session = sessionService.createSession(
            authentication.getPrincipal(),
            accessToken,
            loginRequest.getIpAddress(),
            loginRequest.getUserAgent()
        );
        
        // 4. Publicar evento
        eventPublisher.publishEvent(new UserLoggedInEvent(
            authentication.getName(),
            session.getId(),
            LocalDateTime.now()
        ));
        
        return ResponseEntity.ok(AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .expiresIn(jwtTokenProvider.getExpirationTime())
            .userInfo(UserInfo.from(authentication))
            .build());
    }
}
```

### 2.2 MS-Pagos (Patrones de Diseño Aplicados)

#### **Responsabilidades Principales:**
- Procesamiento de pagos unificados múltiples servicios
- Implementación Saga Pattern para transacciones distribuidas
- Strategy Pattern para múltiples métodos de pago
- Command Pattern para operaciones auditables
- Integración con pasarela PSE y sistemas legacy

#### **Stack Tecnológico:**
```
Backend Framework: Spring Boot 3.2.0
├── Saga Pattern: Axon Framework 4.8.x
├── Strategy Pattern: Payment method abstraction
├── Command Pattern: Auditable operations
├── Circuit Breaker: Resilience4j 2.0.x
├── Database: PostgreSQL 15.x + Event Store
├── Messaging: Kafka 3.5.x (Event-driven)
└── Monitoring: Micrometer + Prometheus

Dependencias Específicas:
├── axon-spring-boot-starter (Saga orchestration)
├── spring-boot-starter-webflux (Reactive patterns)
├── resilience4j-spring-boot2 (Circuit breaker)
└── spring-cloud-starter-openfeign (External APIs)
```

#### **Patrones de Diseño Implementados:**

##### **1. Strategy Pattern - Métodos de Pago**
```java
public interface PaymentStrategy {
    PaymentResult procesarPago(PaymentRequest request);
    boolean soporta(PaymentMethod method);
    List<PaymentMethod> getMetodosSoportados();
    PaymentValidationResult validarRequest(PaymentRequest request);
}

@Component
public class PSEPaymentStrategy implements PaymentStrategy {
    
    @Autowired
    private PSEGatewayClient pseClient;
    
    @Override
    public PaymentResult procesarPago(PaymentRequest request) {
        // Validaciones específicas PSE
        PaymentValidationResult validation = validarRequest(request);
        if (!validation.isValid()) {
            return PaymentResult.failed(validation.getErrors());
        }
        
        // Procesar con gateway PSE
        PSETransactionRequest pseRequest = PSETransactionRequest.builder()
            .bankCode(request.getBankCode())
            .accountType(request.getAccountType())
            .amount(request.getAmount())
            .reference(request.getReference())
            .build();
        
        PSETransactionResponse response = pseClient.procesarTransaccion(pseRequest);
        
        return PaymentResult.builder()
            .success(response.isApproved())
            .transactionId(response.getTransactionId())
            .authorizationCode(response.getAuthorizationCode())
            .build();
    }
    
    @Override
    public PaymentValidationResult validarRequest(PaymentRequest request) {
        List<String> errors = new ArrayList<>();
        
        if (request.getBankCode() == null) {
            errors.add("Código de banco es requerido para PSE");
        }
        
        if (request.getAmount().compareTo(BigDecimal.valueOf(20000000)) > 0) {
            errors.add("Monto máximo PSE excedido: $20,000,000");
        }
        
        return PaymentValidationResult.builder()
            .valid(errors.isEmpty())
            .errors(errors)
            .build();
    }
}

@Component
public class CreditCardPaymentStrategy implements PaymentStrategy {
    
    @Autowired
    private CreditCardProcessor cardProcessor;
    
    @Override
    public PaymentResult procesarPago(PaymentRequest request) {
        return cardProcessor.processPayment(
            request.getCardNumber(),
            request.getExpiryDate(), 
            request.getCvv(),
            request.getAmount()
        );
    }
    
    @Override
    public boolean soporta(PaymentMethod method) {
        return Arrays.asList(
            PaymentMethod.VISA,
            PaymentMethod.MASTERCARD,
            PaymentMethod.AMERICAN_EXPRESS
        ).contains(method);
    }
}

// Payment Processor con Strategy Pattern
@Service
public class PaymentProcessor {
    
    private final Map<PaymentMethod, PaymentStrategy> strategies;
    
    public PaymentProcessor(List<PaymentStrategy> strategies) {
        this.strategies = strategies.stream()
            .flatMap(strategy -> strategy.getMetodosSoportados().stream()
                .map(method -> Map.entry(method, strategy)))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
            ));
    }
    
    public PaymentResult procesar(PaymentRequest request) {
        PaymentStrategy strategy = strategies.get(request.getMethod());
        
        if (strategy == null) {
            throw new UnsupportedPaymentMethodException(
                "Método de pago no soportado: " + request.getMethod());
        }
        
        return strategy.procesarPago(request);
    }
}
```

##### **2. Command Pattern - Operaciones Auditables**
```java
public interface Command {
    void execute();
    void undo();
    String getCommandId();
    String getDescription();
    CommandStatus getStatus();
}

@Component
public class ProcesarPagoCommand extends AuditableCommand {
    
    private final PaymentRequest request;
    private PaymentResponse response;
    
    @Autowired
    private PaymentProcessor paymentProcessor;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    public ProcesarPagoCommand(String userId, PaymentRequest request) {
        super(userId);
        this.request = request;
    }
    
    @Override
    protected void doExecute() {
        // 1. Procesar pago
        PaymentResult result = paymentProcessor.procesar(request);
        
        if (!result.isSuccess()) {
            throw new PaymentProcessingException(
                "Payment failed: " + result.getMessage());
        }
        
        // 2. Crear registro de pago
        Payment payment = Payment.builder()
            .id(UUID.randomUUID().toString())
            .userId(userId)
            .amount(request.getAmount())
            .method(request.getMethod())
            .status(PaymentStatus.COMPLETED)
            .transactionId(result.getTransactionId())
            .build();
        
        paymentRepository.save(payment);
        
        this.response = PaymentResponse.from(payment);
    }
    
    @Override
    protected void doUndo() {
        if (response == null) {
            throw new IllegalStateException("Cannot undo: payment not executed");
        }
        
        // Crear reverso en gateway
        PaymentRefundRequest refundRequest = PaymentRefundRequest.builder()
            .originalTransactionId(response.getTransactionId())
            .amount(request.getAmount())
            .reason("Command undo operation")
            .build();
        
        PaymentResult refundResult = paymentProcessor.procesarReverso(refundRequest);
        
        if (!refundResult.isSuccess()) {
            throw new PaymentReversalException(
                "Failed to reverse payment: " + refundResult.getMessage());
        }
    }
}

@Component
public class CommandInvoker {
    
    @Autowired
    private CommandRepository commandRepository;
    
    @Transactional
    public <T extends Command> T execute(T command) {
        try {
            commandRepository.save(CommandEntity.from(command));
            command.execute();
            commandRepository.updateStatus(command.getCommandId(), command.getStatus());
            return command;
        } catch (Exception e) {
            commandRepository.updateStatus(command.getCommandId(), CommandStatus.FAILED);
            throw e;
        }
    }
}
```

#### **Arquitectura Hexagonal MS-Pagos:**

**Propósito del Diagrama:** Documenta la implementación de Clean Architecture específicamente para el microservicio de pagos, mostrando la separación entre lógica de negocio (dominio) y detalles de infraestructura con aplicación de patrones de diseño.

**Decisiones que Apoya:**
- Inversión de dependencias hacia el dominio
- Strategy Pattern para métodos de pago  
- Command Pattern para auditoría
- Interface Segregation Principle (ISP)
- Testabilidad mediante inyección de puertos

**Fuente:** `Graficos/Drawio/Microrservicio_Pagos/4. DiagramaArquitecturaHexagonal_MicroservicioPagos.drawio`

```
DOMAIN CORE (Hexágono Central):
├── Payment Entity (DDD)
├── SagaOrchestrator (Domain Service)
├── PaymentValidator (Domain Service)
├── PaymentStrategy Interface (Strategy Pattern)
└── Repository Interfaces (Ports - ISP)

APLICACIÓN LAYER:
├── PaymentCommandHandler (Command Pattern)
├── PaymentQueryHandler (CQRS)
├── SagaCommandHandler
└── PaymentApplicationService

PRIMARY PORTS (Entrada - ISP aplicado):
├── PaymentProcessingPort (solo procesamientos)
├── PaymentQueryPort (solo consultas)
├── PaymentAdminPort (solo administración)
└── EventHandlerPort (eventos Kafka)

SECONDARY PORTS (Salida - ISP aplicado):
├── PaymentRepositoryPort
├── PSEGatewayPort
├── LegacySystemPort
├── NotificationPort
└── AuditPort

ADAPTERS (Infraestructura):
├── REST Controllers → Primary Ports
├── Kafka Listeners → EventHandler Port
├── JPA Repositories → PaymentRepository Port
├── PSE HTTP Client → PSEGateway Port
├── Mainframe SNA → LegacySystem Port
└── SMTP/SMS → Notification Port

STRATEGY IMPLEMENTATIONS:
├── PSEPaymentStrategy
├── CreditCardPaymentStrategy
├── NequiPaymentStrategy
└── BankTransferPaymentStrategy
```

##### **3. Template Method Pattern - Procesamiento Base**
```java
public abstract class BaseServiceProcessor {
    
    @Autowired
    protected CacheService cacheService;
    
    @Autowired
    protected AuditService auditService;
    
    @Autowired
    protected MetricsService metricsService;
    
    // Template method - define el algoritmo
    public final <T> T procesarConsulta(String numeroServicio, Class<T> responseType) {
        
        Timer.Sample sample = Timer.start(metricsService.getMeterRegistry());
        String correlationId = UUID.randomUUID().toString();
        
        try {
            // 1. Validación común
            validarNumeroServicio(numeroServicio);
            
            // 2. Log inicio
            logInicioConsulta(numeroServicio, correlationId);
            
            // 3. Check cache (hook method)
            Optional<T> cached = verificarCache(numeroServicio, responseType);
            if (cached.isPresent()) {
                return cached.get();
            }
            
            // 4. Consulta legacy (abstract method)
            T resultado = consultarSistemaLegacy(numeroServicio, responseType);
            
            // 5. Validar respuesta (hook method)
            validarRespuesta(resultado);
            
            // 6. Actualizar cache (hook method)
            actualizarCache(numeroServicio, resultado);
            
            return resultado;
            
        } finally {
            sample.stop(Timer.builder("service.consulta.duration")
                .tag("service", getServiceName())
                .register(metricsService.getMeterRegistry()));
        }
    }
    
    // Abstract methods - específicos por servicio
    protected abstract <T> T consultarSistemaLegacy(String numeroServicio, Class<T> responseType);
    protected abstract String getServiceName();
    
    // Hook methods - pueden ser sobrescritos
    protected <T> Optional<T> verificarCache(String numeroServicio, Class<T> responseType) {
        String cacheKey = buildCacheKey(numeroServicio);
        return cacheService.get(cacheKey, responseType);
    }
    
    protected <T> void validarRespuesta(T resultado) {
        if (resultado == null) {
            throw new InvalidResponseException("Response cannot be null");
        }
    }
}

// Implementación específica para Energía
@Service
public class EnergiaService extends BaseServiceProcessor {
    
    @Autowired
    private MainframeAdapter mainframeAdapter;
    
    @Override
    protected <T> T consultarSistemaLegacy(String numeroServicio, Class<T> responseType) {
        if (responseType == SaldoResponse.class) {
            return responseType.cast(mainframeAdapter.consultarSaldoEnergia(numeroServicio));
        }
        throw new UnsupportedOperationException("Response type not supported: " + responseType);
    }
    
    @Override
    protected String getServiceName() {
        return "ENERGIA";
    }
}
```

##### **4. Decorator Pattern - Cross-cutting Concerns**
```java
// Core implementation - solo lógica de negocio
@Component("corePaymentProcessor")
public class CorePaymentProcessor implements PaymentProcessor {
    
    @Override
    public PaymentResponse procesarPago(PaymentRequest request) {
        // Solo lógica de negocio, sin logging ni métricas
        PaymentStrategy strategy = findStrategy(request.getMethod());
        PaymentResult result = strategy.procesarPago(request);
        
        Payment payment = Payment.from(request, result);
        paymentRepository.save(payment);
        
        return PaymentResponse.from(payment);
    }
}

// Logging Decorator
@Component
public class LoggingPaymentDecorator implements PaymentProcessor {
    
    private final PaymentProcessor delegate;
    
    public LoggingPaymentDecorator(@Qualifier("corePaymentProcessor") PaymentProcessor delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public PaymentResponse procesarPago(PaymentRequest request) {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        
        try {
            log.info("Iniciando procesamiento de pago: método={}, monto={}", 
                request.getMethod(), request.getAmount());
            
            PaymentResponse response = delegate.procesarPago(request);
            
            log.info("Pago procesado exitosamente: paymentId={}, status={}", 
                response.getPaymentId(), response.getStatus());
            
            return response;
            
        } catch (Exception e) {
            log.error("Error procesando pago: {}", e.getMessage(), e);
            throw e;
        } finally {
            MDC.clear();
        }
    }
}

// Metrics Decorator
@Component
public class MetricsPaymentDecorator implements PaymentProcessor {
    
    private final PaymentProcessor delegate;
    private final MeterRegistry meterRegistry;
    
    @Override
    public PaymentResponse procesarPago(PaymentRequest request) {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            PaymentResponse response = delegate.procesarPago(request);
            
            sample.stop(Timer.builder("payment.processing.duration")
                .tag("method", request.getMethod().toString())
                .tag("status", "success")
                .register(meterRegistry));
            
            Counter.builder("payment.processed")
                .tag("method", request.getMethod().toString())
                .register(meterRegistry)
                .increment();
            
            return response;
            
        } catch (Exception e) {
            sample.stop(Timer.builder("payment.processing.duration")
                .tag("method", request.getMethod().toString())
                .tag("status", "error")
                .register(meterRegistry));
            
            throw e;
        }
    }
}

// Configuration para inyección ordenada
@Configuration
public class PaymentProcessorConfiguration {
    
    @Bean
    @Primary
    public PaymentProcessor paymentProcessor(
            @Qualifier("corePaymentProcessor") PaymentProcessor core,
            MeterRegistry meterRegistry) {
        
        // Orden: Core → Logging → Metrics → Security
        PaymentProcessor loggingDecorator = new LoggingPaymentDecorator(core);
        PaymentProcessor metricsDecorator = new MetricsPaymentDecorator(loggingDecorator, meterRegistry);
        
        return metricsDecorator;
    }
}
```

#### **Saga Pattern Implementation:**
```java
@Saga
public class PagoUnificadoSaga {
    
    @Autowired
    private transient PaymentGateway paymentGateway;
    
    @Autowired
    private transient LegacySystemAdapter legacyAdapter;
    
    private String sagaId;
    private String paymentId;
    private List<String> completedSteps = new ArrayList<>();

    @SagaOrchestrationStart
    @SagaHandler
    public void handle(IniciarPagoUnificadoCommand command) {
        this.sagaId = command.getSagaId();
        this.paymentId = command.getPaymentId();
        
        // Paso 1: Procesar pago en PSE
        commandGateway.send(new ProcesarPagoPSECommand(
            paymentId, 
            command.getPaymentRequest()
        ));
    }
    
    @SagaHandler
    public void handle(PagoPSEProcesadoEvent event) {
        if (event.isExitoso()) {
            completedSteps.add("PSE_PAYMENT");
            
            // Paso 2: Actualizar sistemas legacy en paralelo
            if (event.getServicios().contains(TipoServicio.ENERGIA)) {
                commandGateway.send(new ActualizarSaldoEnergiaCommand(
                    paymentId, 
                    event.getMontoEnergia()
                ));
            }
            
            if (event.getServicios().contains(TipoServicio.ACUEDUCTO)) {
                commandGateway.send(new ActualizarSaldoAcueductoCommand(
                    paymentId, 
                    event.getMontoAcueducto()
                ));
            }
        } else {
            // Saga termina en fallo
            commandGateway.send(new FinalizarSagaCommand(
                sagaId, 
                SagaStatus.FAILED, 
                event.getError()
            ));
        }
    }
    
    @SagaHandler
    public void handle(SaldoEnergiaActualizadoEvent event) {
        if (event.isExitoso()) {
            completedSteps.add("ENERGY_UPDATE");
            verificarFinalizacionSaga();
        } else {
            // Ejecutar compensaciones
            ejecutarCompensaciones();
        }
    }
    
    private void ejecutarCompensaciones() {
        // Compensar en orden inverso
        Collections.reverse(completedSteps);
        
        for (String step : completedSteps) {
            switch (step) {
                case "PSE_PAYMENT":
                    commandGateway.send(new RevertirPagoPSECommand(paymentId));
                    break;
                case "ENERGY_UPDATE":
                    commandGateway.send(new RevertirSaldoEnergiaCommand(paymentId));
                    break;
                // ... otros casos
            }
        }
    }
    
    @EndSaga
    private void verificarFinalizacionSaga() {
        if (todosPasosCompletados()) {
            commandGateway.send(new FinalizarSagaCommand(
                sagaId, 
                SagaStatus.COMPLETED, 
                null
            ));
        }
    }
}
```

### 2.3 MS-Facturación (Agregación de Datos)

#### **Responsabilidades Principales:**
- Consolidación de facturas de múltiples servicios
- Generación de PDFs con información unificada
- Cálculo de totales, descuentos e impuestos
- Distribución de facturas vía email

#### **Patrón de Agregación:**
```java
@Service
@Transactional(readOnly = true)
public class FacturacionService {
    
    @Autowired
    private List<ServiceBillingProvider> billingProviders;
    
    @Autowired
    private InvoiceTemplateEngine templateEngine;
    
    @Autowired
    private PdfGenerationService pdfService;

    public FacturaUnificada generarFacturaUnificada(String clienteId, BillingPeriod periodo) {
        
        // 1. Obtener datos de facturación de cada servicio
        List<ServicioBilling> servicios = billingProviders.stream()
            .map(provider -> provider.getBillingData(clienteId, periodo))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        
        // 2. Calcular totales consolidados
        BigDecimal subtotal = servicios.stream()
            .map(ServicioBilling::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        BigDecimal descuentos = servicios.stream()
            .map(ServicioBilling::getDescuentos)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        BigDecimal impuestos = calcularImpuestosConsolidados(servicios);
        BigDecimal total = subtotal.subtract(descuentos).add(impuestos);
        
        // 3. Construir factura unificada
        return FacturaUnificada.builder()
            .clienteId(clienteId)
            .periodo(periodo)
            .servicios(servicios)
            .subtotal(subtotal)
            .descuentos(descuentos)
            .impuestos(impuestos)
            .total(total)
            .fechaGeneracion(LocalDateTime.now())
            .build();
    }
    
    @Async
    public CompletableFuture<byte[]> generarPDF(FacturaUnificada factura) {
        
        // 1. Renderizar template HTML
        String htmlContent = templateEngine.render("factura-unificada", 
            FacturaTemplateModel.from(factura));
        
        // 2. Convertir a PDF
        byte[] pdfBytes = pdfService.generateFromHtml(htmlContent);
        
        // 3. Almacenar en S3/storage
        String storageKey = storageService.store(pdfBytes, 
            "facturas/" + factura.getClienteId() + "/" + factura.getPeriodo());
        
        // 4. Publicar evento
        eventPublisher.publishEvent(new FacturaPDFGeneradaEvent(
            factura.getId(),
            storageKey,
            LocalDateTime.now()
        ));
        
        return CompletableFuture.completedFuture(pdfBytes);
    }
}
```

### 2.4 MS-Incidencias (Workflow Engine)

#### **Responsabilidades Principales:**
- Gestión del ciclo de vida de tickets de soporte
- Workflow de aprobaciones y escalamientos
- SLA tracking y alertas automáticas
- Integración con sistemas de campo

#### **Workflow Implementation:**
```java
@Entity
@Table(name = "incidencias")
public class Incidencia {
    
    @Id
    private String id;
    
    @Enumerated(EnumType.STRING)
    private TipoIncidencia tipo;
    
    @Enumerated(EnumType.STRING)
    private EstadoIncidencia estado;
    
    @Enumerated(EnumType.STRING)
    private PrioridadIncidencia prioridad;
    
    private String clienteId;
    private String descripcion;
    private String tecnicoAsignado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaLimiteResolucion;
    
    // State machine methods
    public void asignarTecnico(String tecnicoId) {
        if (this.estado != EstadoIncidencia.CREADA) {
            throw new InvalidStateTransitionException(
                "Solo se puede asignar técnico a incidencias en estado CREADA");
        }
        this.tecnicoAsignado = tecnicoId;
        this.estado = EstadoIncidencia.ASIGNADA;
    }
    
    public void iniciarTrabajo() {
        if (this.estado != EstadoIncidencia.ASIGNADA) {
            throw new InvalidStateTransitionException(
                "Solo se puede iniciar trabajo en incidencias ASIGNADAS");
        }
        this.estado = EstadoIncidencia.EN_PROGRESO;
    }
    
    public void resolver(String solucion) {
        if (this.estado != EstadoIncidencia.EN_PROGRESO) {
            throw new InvalidStateTransitionException(
                "Solo se pueden resolver incidencias EN_PROGRESO");
        }
        this.estado = EstadoIncidencia.RESUELTA;
        // Publicar evento para notificación al cliente
    }
}

// SLA Monitoring
@Component
@Scheduled(fixedDelay = 300000) // Cada 5 minutos
public class SLAMonitoringService {
    
    @Autowired
    private IncidenciaRepository incidenciaRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    public void verificarSLAs() {
        LocalDateTime ahora = LocalDateTime.now();
        
        List<Incidencia> incidenciasVencidas = incidenciaRepository
            .findByEstadoInAndFechaLimiteResolucionBefore(
                Arrays.asList(EstadoIncidencia.CREADA, EstadoIncidencia.ASIGNADA, EstadoIncidencia.EN_PROGRESO),
                ahora
            );
            
        for (Incidencia incidencia : incidenciasVencidas) {
            // Escalar automáticamente
            incidencia.escalar();
            
            // Notificar supervisor
            notificationService.notificarSLAVencido(incidencia);
        }
    }
}
```

### 2.5 MS-Notificaciones (Multi-Channel)

#### **Responsabilidades Principales:**
- Envío de notificaciones via múltiples canales (Email, SMS, Push, WhatsApp)
- Gestión de preferencias de usuario
- Template engine para personalización
- Retry logic y fallback strategies

#### **Multi-Channel Implementation:**
```java
@Service
public class NotificationService {
    
    private final Map<NotificationChannel, NotificationProvider> providers;
    
    public NotificationService(List<NotificationProvider> providers) {
        this.providers = providers.stream()
            .collect(Collectors.toMap(
                NotificationProvider::getChannel,
                Function.identity()
            ));
    }
    
    @Async
    @Retryable(value = {NotificationException.class}, maxAttempts = 3)
    public CompletableFuture<NotificationResult> enviarNotificacion(NotificationRequest request) {
        
        // 1. Obtener preferencias del usuario
        UserNotificationPreferences preferences = 
            preferencesService.getPreferences(request.getUserId());
        
        // 2. Determinar canales apropiados
        List<NotificationChannel> channels = determinarCanales(
            request.getType(), 
            preferences
        );
        
        // 3. Enviar por cada canal en paralelo
        List<CompletableFuture<ChannelResult>> futures = channels.stream()
            .map(channel -> enviarPorCanal(request, channel))
            .collect(Collectors.toList());
        
        // 4. Consolidar resultados
        CompletableFuture<NotificationResult> result = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        ).thenApply(v -> {
            List<ChannelResult> results = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
            return NotificationResult.from(results);
        });
        
        return result;
    }
    
    private CompletableFuture<ChannelResult> enviarPorCanal(
            NotificationRequest request, 
            NotificationChannel channel) {
        
        NotificationProvider provider = providers.get(channel);
        if (provider == null) {
            return CompletableFuture.completedFuture(
                ChannelResult.failed(channel, "Provider not available"));
        }
        
        return provider.send(request)
            .exceptionally(throwable -> 
                ChannelResult.failed(channel, throwable.getMessage()));
    }
}

// Email Provider Implementation
@Component
public class EmailNotificationProvider implements NotificationProvider {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private TemplateEngine templateEngine;
    
    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.EMAIL;
    }
    
    @Override
    @CircuitBreaker(name = "email-service")
    public CompletableFuture<ChannelResult> send(NotificationRequest request) {
        
        try {
            // 1. Renderizar template
            String htmlContent = templateEngine.process(
                request.getTemplate(),
                request.getTemplateVariables()
            );
            
            // 2. Construir mensaje
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(request.getRecipient());
            helper.setSubject(request.getSubject());
            helper.setText(htmlContent, true);
            
            // 3. Enviar
            mailSender.send(message);
            
            return CompletableFuture.completedFuture(
                ChannelResult.success(getChannel(), message.getMessageID()));
                
        } catch (Exception e) {
            return CompletableFuture.completedFuture(
                ChannelResult.failed(getChannel(), e.getMessage()));
        }
    }
}
```

---

## 3. COMPONENTES DE INFRAESTRUCTURA

### 3.1 API Gateway (Spring Cloud Gateway)

#### **Responsabilidades:**
- Punto de entrada único para todas las APIs
- Routing dinámico a microservicios
- Rate limiting y throttling
- Autenticación y autorización centralizada
- Logging y métricas consolidadas

#### **Configuración:**
```yaml
# application.yml
spring:
  cloud:
    gateway:
      routes:
        - id: ms-clientes
          uri: lb://ms-clientes
          predicates:
            - Path=/api/v1/auth/**, /api/v1/users/**
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
            - name: CircuitBreaker
              args:
                name: ms-clientes-cb
                fallbackUri: forward:/fallback/clientes
                
        - id: ms-pagos
          uri: lb://ms-pagos
          predicates:
            - Path=/api/v1/pagos/**
          filters:
            - name: AuthenticationFilter
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 5
                redis-rate-limiter.burstCapacity: 10
            - name: Retry
              args:
                retries: 3
                methods: GET,POST
                backoff:
                  firstBackoff: 50ms
                  maxBackoff: 500ms

  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${JWT_ISSUER_URI:http://localhost:8080/auth}
          
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

#### **Custom Filters:**
```java
@Component
public class AuthenticationFilter implements GatewayFilter, Ordered {
    
    @Autowired
    private JwtTokenValidator jwtValidator;
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        
        ServerHttpRequest request = exchange.getRequest();
        
        // Extraer token del header Authorization
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }
        
        String token = authHeader.substring(7);
        
        // Validar token JWT
        return jwtValidator.validate(token)
            .flatMap(claims -> {
                // Agregar información del usuario al request
                ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", claims.getSubject())
                    .header("X-User-Roles", String.join(",", claims.getRoles()))
                    .build();
                    
                return chain.filter(exchange.mutate()
                    .request(modifiedRequest)
                    .build());
            })
            .onErrorResume(throwable -> unauthorized(exchange));
    }
    
    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }
    
    @Override
    public int getOrder() {
        return -100; // Ejecutar antes que otros filtros
    }
}
```

### 3.2 Event Bus (Apache Kafka) + Observer Pattern + Chain of Responsibility

#### **Configuración de Tópicos con Patrones de Diseño:**
```yaml
# Configuración Kafka
kafka:
  bootstrap-servers: ${KAFKA_BROKERS:localhost:9092}
  topics:
    pago-eventos:
      name: serviciudad.pagos.eventos
      partitions: 6
      replication-factor: 3
      config:
        retention.ms: 604800000  # 7 días
        compression.type: gzip
        
    usuario-eventos:
      name: serviciudad.usuarios.eventos
      partitions: 3
      replication-factor: 3
      
    incidencia-eventos:
      name: serviciudad.incidencias.eventos
      partitions: 3
      replication-factor: 3
      
    notificacion-eventos:
      name: serviciudad.notificaciones.eventos
      partitions: 6
      replication-factor: 3

  consumer:
    group-id: ${CONSUMER_GROUP:serviciudad-${spring.application.name}}
    auto-offset-reset: earliest
    key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
    properties:
      spring.json.trusted.packages: "com.serviciudad.eventos"
      
  producer:
    key-serializer: org.apache.kafka.common.serialization.StringSerializer
    value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    properties:
      enable.idempotence: true
      acks: all
      retries: 3
```

#### **Event Publishing con Observer Pattern:**
```java
@Service
public class EventPublisher {
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @Autowired
    private List<EventObserver> eventObservers;
    
    @Value("${kafka.topics.pago-eventos.name}")
    private String pagoEventosTopic;
    
    // Observer Pattern para notificaciones de publicación
    private final List<EventObserver> observers = new ArrayList<>();
    
    @PostConstruct
    public void init() {
        eventObservers.forEach(this::addObserver);
    }
    
    public void addObserver(EventObserver observer) {
        observers.add(observer);
    }
    
    private void notifyObservers(EventPublishResult result) {
        observers.forEach(observer -> {
            try {
                observer.onEventPublished(result);
            } catch (Exception e) {
                log.warn("Error notificando observer de eventos: {}", e.getMessage());
            }
        });
    }
    
    @Async
    public CompletableFuture<SendResult<String, Object>> publishPagoEvent(PagoEvent event) {
        
        long startTime = System.currentTimeMillis();
        
        // Partitioning por cliente para ordenamiento
        String partitionKey = event.getClienteId();
        
        // Headers para trazabilidad y versionado
        ProducerRecord<String, Object> record = new ProducerRecord<>(
            pagoEventosTopic,
            partitionKey,
            event
        );
        
        record.headers().add("event-type", event.getClass().getSimpleName().getBytes());
        record.headers().add("event-version", "1.0".getBytes());
        record.headers().add("correlation-id", MDC.get("correlationId").getBytes());
        record.headers().add("source-service", "ms-pagos".getBytes());
        record.headers().add("timestamp", String.valueOf(System.currentTimeMillis()).getBytes());
        
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(record);
        
        // Observer Pattern - notificar resultado de publicación
        future.whenComplete((result, throwable) -> {
            long duration = System.currentTimeMillis() - startTime;
            
            EventPublishResult publishResult = EventPublishResult.builder()
                .eventType(event.getClass().getSimpleName())
                .topic(pagoEventosTopic)
                .partition(result != null ? result.getRecordMetadata().partition() : -1)
                .offset(result != null ? result.getRecordMetadata().offset() : -1)
                .duration(duration)
                .success(throwable == null)
                .error(throwable != null ? throwable.getMessage() : null)
                .correlationId(MDC.get("correlationId"))
                .build();
            
            notifyObservers(publishResult);
        });
        
        return future;
    }
}

// Observer Pattern para eventos publicados
public interface EventObserver {
    void onEventPublished(EventPublishResult result);
}

@Component
@Slf4j
public class EventMetricsObserver implements EventObserver {
    
    @Autowired
    private MeterRegistry meterRegistry;
    
    @Override
    public void onEventPublished(EventPublishResult result) {
        // Métricas de eventos publicados
        Counter.builder("kafka.events.published")
            .tag("event-type", result.getEventType())
            .tag("topic", result.getTopic())
            .tag("success", String.valueOf(result.isSuccess()))
            .register(meterRegistry)
            .increment();
            
        // Tiempo de publicación
        Timer.builder("kafka.events.publish.duration")
            .tag("event-type", result.getEventType())
            .register(meterRegistry)
            .record(result.getDuration(), TimeUnit.MILLISECONDS);
    }
}

@Component
@Slf4j
public class EventAuditObserver implements EventObserver {
    
    @Autowired
    private EventAuditRepository auditRepository;
    
    @Override
    @Async
    public void onEventPublished(EventPublishResult result) {
        EventAuditLog auditLog = EventAuditLog.builder()
            .eventType(result.getEventType())
            .topic(result.getTopic())
            .partition(result.getPartition())
            .offset(result.getOffset())
            .correlationId(result.getCorrelationId())
            .success(result.isSuccess())
            .errorMessage(result.getError())
            .publishedAt(LocalDateTime.now())
            .duration(result.getDuration())
            .build();
            
        auditRepository.save(auditLog);
    }
}

// Chain of Responsibility para procesamiento de eventos
@Component
@KafkaListener(topics = "${kafka.topics.pago-eventos.name}")
public class PagoEventListener {
    
    @Autowired
    private List<EventProcessor> eventProcessors;
    
    private EventProcessorChain processorChain;
    
    @PostConstruct
    public void initChain() {
        // Construir cadena de procesadores
        processorChain = EventProcessorChain.builder()
            .addProcessor(new ValidationEventProcessor())
            .addProcessor(new SecurityEventProcessor())
            .addProcessor(new BusinessEventProcessor())
            .addProcessor(new NotificationEventProcessor())
            .build();
    }
    
    @KafkaHandler
    public void handle(PagoCompletadoEvent event, 
                      @Header("correlation-id") String correlationId,
                      @Header("event-type") String eventType) {
        
        MDC.put("correlationId", correlationId);
        log.info("Procesando evento pago completado: {}", event.getPaymentId());
        
        try {
            // Chain of Responsibility para procesamiento
            EventProcessingContext context = EventProcessingContext.builder()
                .event(event)
                .correlationId(correlationId)
                .eventType(eventType)
                .receivedAt(LocalDateTime.now())
                .build();
                
            processorChain.process(context);
            
        } catch (Exception e) {
            log.error("Error procesando evento pago: {}", e.getMessage(), e);
            // Enviar a DLQ (Dead Letter Queue)
            handleProcessingError(event, correlationId, e);
        } finally {
            MDC.clear();
        }
    }
}

// Chain of Responsibility Pattern para eventos
public abstract class EventProcessor {
    
    protected EventProcessor nextProcessor;
    
    public void setNext(EventProcessor nextProcessor) {
        this.nextProcessor = nextProcessor;
    }
    
    public void process(EventProcessingContext context) {
        if (canProcess(context)) {
            doProcess(context);
        }
        
        if (nextProcessor != null) {
            nextProcessor.process(context);
        }
    }
    
    protected abstract boolean canProcess(EventProcessingContext context);
    protected abstract void doProcess(EventProcessingContext context);
}

@Component
public class ValidationEventProcessor extends EventProcessor {
    
    @Override
    protected boolean canProcess(EventProcessingContext context) {
        return context.getEvent() != null;
    }
    
    @Override
    protected void doProcess(EventProcessingContext context) {
        log.debug("Validando evento: {}", context.getEventType());
        
        // Validar estructura del evento
        if (context.getEvent() instanceof PagoCompletadoEvent) {
            PagoCompletadoEvent event = (PagoCompletadoEvent) context.getEvent();
            
            if (event.getPaymentId() == null || event.getPaymentId().isEmpty()) {
                throw new EventValidationException("PaymentId es requerido");
            }
            
            if (event.getAmount() == null || event.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new EventValidationException("Monto debe ser mayor a cero");
            }
        }
        
        context.addProcessingStep("VALIDATION_COMPLETED");
    }
}

@Component
public class BusinessEventProcessor extends EventProcessor {
    
    @Autowired
    private FacturacionService facturacionService;
    
    @Override
    protected boolean canProcess(EventProcessingContext context) {
        return context.hasProcessingStep("VALIDATION_COMPLETED");
    }
    
    @Override
    protected void doProcess(EventProcessingContext context) {
        log.debug("Procesando lógica de negocio para evento: {}", context.getEventType());
        
        if (context.getEvent() instanceof PagoCompletadoEvent) {
            PagoCompletadoEvent event = (PagoCompletadoEvent) context.getEvent();
            
            // Actualizar facturación después de pago exitoso
            facturacionService.marcarFacturaComoPagada(
                event.getFacturaId(), 
                event.getPaymentId(),
                event.getAmount()
            );
            
            context.addProcessingStep("BUSINESS_LOGIC_COMPLETED");
        }
    }
}
```
            facturacionService.actualizarFacturacion(
                event.getClienteId(),
                event.getServicios(),
                event.getMontoTotal()
            );
            
        } catch (Exception e) {
            log.error("Error procesando evento pago: {}", event.getPaymentId(), e);
            // Dead letter queue handling
            throw new KafkaRetryableException(
                "Failed to process payment event", e);
        }
    }
    
    @DltHandler
    public void handleDlt(PagoCompletadoEvent event, Exception exception) {
        log.error("Evento pago enviado a DLT: {}", event.getPaymentId(), exception);
        
        // Notificar equipo de soporte
        alertService.notifyPaymentEventFailure(event, exception);
    }
}
```

### 3.3 Bases de Datos Especializadas

#### **PostgreSQL - Datos Transaccionales**
```sql
-- Configuración master database
-- Microservicios: MS-Clientes, MS-Pagos, MS-Incidencias

-- Read Replicas Configuration
CREATE PUBLICATION serviciudad_replication FOR ALL TABLES;

-- Optimizaciones performance
CREATE INDEX CONCURRENTLY idx_usuarios_email ON usuarios USING btree (email);
CREATE INDEX CONCURRENTLY idx_pagos_cliente_fecha ON pagos USING btree (cliente_id, fecha_creacion);
CREATE INDEX CONCURRENTLY idx_incidencias_estado_prioridad ON incidencias USING btree (estado, prioridad);

-- Partitioning para tabla de auditoría
CREATE TABLE audit_logs (
    id BIGSERIAL,
    timestamp TIMESTAMP NOT NULL,
    user_id UUID,
    action VARCHAR(50),
    resource VARCHAR(100),
    details JSONB
) PARTITION BY RANGE (timestamp);

-- Particiones mensuales automáticas
CREATE TABLE audit_logs_y2025m09 PARTITION OF audit_logs
FOR VALUES FROM ('2025-09-01') TO ('2025-10-01');
```

#### **MongoDB - Documentos y Logs**
```javascript
// Configuración MS-Notificaciones y MS-Administración
// Base de datos: serviciudad_documents

// Colección: notification_templates
{
  "_id": ObjectId("..."),
  "name": "pago_completado_email",
  "type": "EMAIL",
  "subject": "Pago procesado exitosamente - ServiCiudad",
  "template": {
    "html": "<!DOCTYPE html>...",
    "variables": ["clienteName", "amount", "services", "transactionId"]
  },
  "version": "1.0",
  "active": true,
  "created_at": ISODate("2025-09-01T00:00:00Z")
}

// Colección: system_logs
{
  "_id": ObjectId("..."),
  "timestamp": ISODate("2025-09-05T10:30:00Z"),
  "level": "INFO",
  "service": "ms-pagos",
  "correlation_id": "550e8400-e29b-41d4-a716-446655440000",
  "message": "Payment processed successfully",
  "metadata": {
    "payment_id": "pmt_123456789",
    "client_id": "usr_987654321", 
    "amount": 150000,
    "duration_ms": 2340
  }
}

// Índices para performance
db.notification_templates.createIndex({ "name": 1, "active": 1 });
db.system_logs.createIndex({ "timestamp": -1, "service": 1 });
db.system_logs.createIndex({ "correlation_id": 1 });

// TTL para logs (retención 90 días)
db.system_logs.createIndex(
  { "timestamp": 1 }, 
  { expireAfterSeconds: 7776000 }
);
```

#### **Redis - Cache y Sesiones**
```bash
# Configuración Redis Cluster (3 masters, 3 replicas)

# Namespace: cache (TTL corto)
redis-cli SET "cache:saldos:usr_123:energia" '{"saldo": 45000, "fecha": "2025-09-05T10:30:00Z"}' EX 900  # 15 min

# Namespace: sessions (TTL medio)
redis-cli HSET "session:sess_abc123" user_id usr_456 ip_address 192.168.1.100 created_at 2025-09-05T10:00:00Z
redis-cli EXPIRE "session:sess_abc123" 1800  # 30 min

# Namespace: circuit_breaker (estado resilience4j)
redis-cli SET "cb:mainframe-adapter:state" "CLOSED" EX 300

# Configuración memory optimization
config set maxmemory 2gb
config set maxmemory-policy allkeys-lru
config set save "900 1 300 10 60 10000"  # Persistence snapshots
```

---

## 4. ADAPTADORES LEGACY Y PATRONES APLICADOS

### 4.1 Mainframe IBM Z Adapter (Circuit Breaker + Adapter Patterns)

#### **Desafíos Técnicos:**
- Protocolo SNA/LU6.2 legacy
- Formato de datos EBCDIC
- Latencia alta (30-45 segundos)
- Ventana de mantenimiento dominical
- Limitaciones de conexiones concurrentes

#### **Patrones de Diseño Aplicados:**

##### **1. Adapter Pattern + Interface Segregation**
```java
// ISP: Interfaces segregadas por responsabilidad
@FunctionalInterface
public interface SaldoConsultaService {
    CompletableFuture<SaldoResponse> consultarSaldo(String numeroServicio);
}

@FunctionalInterface  
public interface PagoProcesamientoService {
    CompletableFuture<PagoResponse> procesarPago(PagoRequest request);
}

public interface HistorialService {
    CompletableFuture<List<TransaccionResponse>> obtenerHistorial(
        String numeroServicio, DateRange periodo);
}

// Adapter Pattern implementation
@Component
@Slf4j
public class MainframeAdapter implements SaldoConsultaService, HistorialService {
    
    @Autowired
    private SNAConnectionPool snaConnectionPool;
    
    @Autowired
    private EBCDICMessageTranslator messageTranslator;
    
    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;
    
    @CircuitBreaker(name = "mainframe", fallbackMethod = "fallbackConsultaSaldo")
    @Retry(name = "mainframe")
    @TimeLimiter(name = "mainframe")
    public CompletableFuture<SaldoResponse> consultarSaldo(String numeroServicio) {
        
        return CompletableFuture.supplyAsync(() -> {
            
            SNAConnection connection = null;
            try {
                // 1. Obtener conexión del pool
                connection = snaConnectionPool.getConnection();
                
                // 2. Construir mensaje COBOL
                CobolMessage request = CobolMessageBuilder.create()
                    .programa("BALPGM01")  // Programa COBOL consulta saldos
                    .campo("CUST-ID", numeroServicio.padLeft(12, '0'))
                    .campo("TRANS-TYPE", "BAL")
                    .build();
                
                // 3. Convertir a EBCDIC
                byte[] ebcdicRequest = messageTranslator.toEBCDIC(request);
                
                // 4. Enviar a mainframe
                byte[] ebcdicResponse = connection.send(ebcdicRequest);
                
                // 5. Traducir respuesta
                CobolResponse response = messageTranslator.fromEBCDIC(ebcdicResponse);
                
                // 6. Mapear a domain object
                return mapToSaldoResponse(response);
                
            } catch (SNAException e) {
                throw new LegacySystemException("Mainframe communication failed", e);
            } finally {
                if (connection != null) {
                    snaConnectionPool.returnConnection(connection);
                }
            }
        });
    }
    
    // Fallback method for Circuit Breaker
    public CompletableFuture<SaldoResponse> fallbackConsultaSaldo(String numeroServicio, Exception ex) {
        log.warn("Mainframe no disponible, consultando cache: {}", numeroServicio);
        
        return cacheService.getLastKnownBalance(numeroServicio)
            .map(cachedBalance -> SaldoResponse.builder()
                .numeroServicio(numeroServicio)
                .saldoActual(cachedBalance.getSaldo())
                .fuente("CACHE")
                .disclaimer("Datos del " + cachedBalance.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .build())
            .orElse(SaldoResponse.noDisponible(numeroServicio));
    }
    
    @Override
    public CompletableFuture<List<TransaccionResponse>> obtenerHistorial(
            String numeroServicio, DateRange periodo) {
        // Implementación específica para histórico mainframe
        return CompletableFuture.supplyAsync(() -> {
            // Lógica para obtener histórico del mainframe
            return obtenerHistorialMainframe(numeroServicio, periodo);
        });
    }
}

// Oracle Adapter - implementa diferentes interfaces
@Component
public class OracleAdapter implements SaldoConsultaService, FacturacionService {
    
    @Override
    public CompletableFuture<SaldoResponse> consultarSaldo(String numeroServicio) {
        // Implementación Oracle específica
    }
    
    @Override
    public CompletableFuture<FacturaResponse> generarFactura(String numeroServicio) {
        // Oracle Reports integration
    }
}

// PSE Adapter - solo maneja pagos
@Component
public class PSEAdapter implements PagoProcesamientoService {
    
    @Override
    public CompletableFuture<PagoResponse> procesarPago(PagoRequest request) {
        // Implementación PSE específica
    }
}
```

##### **2. Factory Pattern para Adaptadores**
```java
@Component
public class LegacyAdapterFactory {
    
    private final Map<TipoSistema, LegacySystemAdapter> adapters;
    
    public LegacyAdapterFactory(List<LegacySystemAdapter> adapters) {
        this.adapters = adapters.stream()
            .collect(Collectors.toMap(
                LegacySystemAdapter::getTipoSistema,
                Function.identity()
            ));
    }
    
    public LegacySystemAdapter createAdapter(TipoSistema tipo) {
        LegacySystemAdapter adapter = adapters.get(tipo);
        if (adapter == null) {
            throw new UnsupportedSystemException("Sistema no soportado: " + tipo);
        }
        return adapter;
    }
    
    public List<LegacySystemAdapter> getAdaptersForService(TipoServicio servicio) {
        return adapters.values().stream()
            .filter(adapter -> adapter.soportaServicio(servicio))
            .collect(Collectors.toList());
    }
}
```
```

#### **Message Translator EBCDIC:**
```java
@Component
public class EBCDICMessageTranslator {
    
    private static final Charset EBCDIC = Charset.forName("IBM500");
    private static final Charset ASCII = StandardCharsets.UTF_8;
    
    public byte[] toEBCDIC(CobolMessage message) {
        
        // Layout fixed-field COBOL
        StringBuilder cobolRecord = new StringBuilder();
        
        // Header (20 bytes)
        cobolRecord.append(message.getPrograma().padRight(8, ' '));
        cobolRecord.append(message.getVersion().padRight(4, ' '));
        cobolRecord.append(String.format("%08d", message.getLength()));
        
        // Fields según copybook COBOL
        for (CobolField field : message.getFields()) {
            switch (field.getType()) {
                case CHAR:
                    cobolRecord.append(field.getValue().padRight(field.getLength(), ' '));
                    break;
                case NUMERIC:
                    cobolRecord.append(field.getValue().padLeft(field.getLength(), '0'));
                    break;
                case PACKED:
                    cobolRecord.append(formatPackedDecimal(field.getValue(), field.getLength()));
                    break;
            }
        }
        
        // Padding final hasta longitud fija (512 bytes)
        while (cobolRecord.length() < 512) {
            cobolRecord.append(' ');
        }
        
        // Convertir ASCII → EBCDIC
        return cobolRecord.toString().getBytes(EBCDIC);
    }
    
    public CobolResponse fromEBCDIC(byte[] ebcdicData) {
        
        // Convertir EBCDIC → ASCII
        String asciiData = new String(ebcdicData, EBCDIC);
        
        // Parsear campos según copybook de respuesta
        CobolResponse response = new CobolResponse();
        
        // Return code (2 bytes)
        String returnCode = asciiData.substring(0, 2);
        response.setReturnCode(returnCode);
        
        if ("00".equals(returnCode)) {
            // Datos exitosos
            response.setNumeroServicio(asciiData.substring(2, 14).trim());
            response.setSaldoActual(parsePackedDecimal(asciiData.substring(14, 22)));
            response.setFechaUltimoPago(parseCobolDate(asciiData.substring(22, 30)));
            response.setEstadoServicio(asciiData.substring(30, 32).trim());
        } else {
            // Error del mainframe
            response.setErrorMessage(asciiData.substring(32, 132).trim());
        }
        
        return response;
    }
    
    private BigDecimal parsePackedDecimal(String packedField) {
        // Lógica específica para packed decimals COBOL
        // Formato: 9(7)V99 COMP-3
        try {
            long value = Long.parseLong(packedField.replace("}", "").replace("{", ""));
            return BigDecimal.valueOf(value, 2); // 2 decimales implícitos
        } catch (NumberFormatException e) {
            log.warn("Error parsing packed decimal: {}", packedField);
            return BigDecimal.ZERO;
        }
    }
    
    private LocalDate parseCobolDate(String cobolDate) {
        // Formato: YYYYMMDD
        try {
            return LocalDate.parse(cobolDate.trim(), DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (DateTimeParseException e) {
            log.warn("Error parsing COBOL date: {}", cobolDate);
            return LocalDate.now();
        }
    }
}
```

### 4.2 Oracle Solaris Adapter (Observer Pattern + Builder Pattern)

#### **Características:**
- Conexión via Oracle Net Services
- Stored procedures PL/SQL  
- Connection pooling optimizado
- Transaction management con XA
- Observer Pattern para auditoría y métricas
- Builder Pattern para respuestas complejas

#### **Implementation con Patrones de Diseño:**
```java
@Repository
@Transactional
public class OracleSolarisAdapter implements LegacySystemAdapter {
    
    @Autowired
    private JdbcTemplate oracleJdbcTemplate;
    
    @Autowired
    private SimpleJdbcCall jdbcCall;
    
    @Autowired
    private List<TransaccionObserver> observers;
    
    @Autowired
    private ConsultaSaldoResponseBuilder responseBuilder;
    
    // Observer Pattern para notificaciones
    private final List<TransaccionObserver> transaccionObservers = new ArrayList<>();
    
    @PostConstruct
    public void init() {
        observers.forEach(this::addObserver);
    }
    
    public void addObserver(TransaccionObserver observer) {
        transaccionObservers.add(observer);
    }
    
    private void notifyObservers(TransaccionEvent event) {
        transaccionObservers.forEach(observer -> {
            try {
                observer.onTransaccionProcessed(event);
            } catch (Exception e) {
                log.warn("Error notificando observer: {}", e.getMessage());
            }
        });
    }
    
    @CircuitBreaker(name = "oracle-solaris")
    @Retry(name = "oracle-solaris")
    public CompletableFuture<ConsultaSaldoResponse> consultarSaldoAcueducto(String numeroServicio) {
        
        return CompletableFuture.supplyAsync(() -> {
            
            long startTime = System.currentTimeMillis();
            
            try {
                // Llamar stored procedure PL/SQL
                MapSqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue("p_numero_servicio", numeroServicio)
                    .addValue("p_fecha_consulta", Date.valueOf(LocalDate.now()));
                
                jdbcCall.withProcedureName("PKG_ACUEDUCTO.SP_CONSULTAR_SALDO");
                
                Map<String, Object> result = jdbcCall.execute(parameters);
                
                // Mapear resultado PL/SQL
                String returnCode = (String) result.get("p_return_code");
                
                if ("SUCCESS".equals(returnCode)) {
                    
                    // Builder Pattern para construir respuesta compleja
                    ConsultaSaldoResponse response = responseBuilder
                        .numeroServicio(numeroServicio)
                        .saldoActual((BigDecimal) result.get("p_saldo_actual"))
                        .fechaUltimaActualizacion(((Date) result.get("p_fecha_ultima_actualizacion")).toLocalDate().atStartOfDay())
                        .consumoPromedio((BigDecimal) result.get("p_consumo_promedio"))
                        .factorCorreccion((BigDecimal) result.get("p_factor_correccion"))
                        .tarifaActual((BigDecimal) result.get("p_tarifa_actual"))
                        .diasVencimiento((Integer) result.get("p_dias_vencimiento"))
                        .fuente("ORACLE_SOLARIS")
                        .build();
                    
                    // Observer Pattern - notificar consulta exitosa
                    long duration = System.currentTimeMillis() - startTime;
                    TransaccionEvent event = TransaccionEvent.builder()
                        .tipo(TipoTransaccion.CONSULTA_SALDO)
                        .numeroServicio(numeroServicio)
                        .timestamp(LocalDateTime.now())
                        .duracion(duration)
                        .estado("EXITOSO")
                        .saldoConsultado(response.getSaldoActual())
                        .build();
                    
                    notifyObservers(event);
                    
                    return response;
                    
                } else {
                    String errorMessage = (String) result.get("p_error_message");
                    
                    // Notificar error de negocio
                    TransaccionEvent errorEvent = TransaccionEvent.builder()
                        .tipo(TipoTransaccion.CONSULTA_SALDO)
                        .numeroServicio(numeroServicio)
                        .timestamp(LocalDateTime.now())
                        .duracion(System.currentTimeMillis() - startTime)
                        .estado("ERROR_NEGOCIO")
                        .error(errorMessage)
                        .build();
                    
                    notifyObservers(errorEvent);
                    
                    throw new LegacySystemException("Oracle procedure failed: " + errorMessage);
                }
                
            } catch (DataAccessException e) {
                log.error("Error consultando Oracle Solaris: {}", e.getMessage());
                
                // Notificar error técnico
                TransaccionEvent errorEvent = TransaccionEvent.builder()
                    .tipo(TipoTransaccion.CONSULTA_SALDO)
                    .numeroServicio(numeroServicio)
                    .timestamp(LocalDateTime.now())
                    .duracion(System.currentTimeMillis() - startTime)
                    .estado("ERROR_TECNICO")
                    .error(e.getMessage())
                    .build();
                
                notifyObservers(errorEvent);
                
                throw new LegacySystemException("Oracle Solaris query failed", e);
            }
        });
    }
}

// Builder Pattern para respuestas complejas de Oracle
@Component
public class ConsultaSaldoResponseBuilder {
    
    private String numeroServicio;
    private BigDecimal saldoActual;
    private LocalDateTime fechaUltimaActualizacion;
    private BigDecimal consumoPromedio;
    private BigDecimal factorCorreccion;
    private BigDecimal tarifaActual;
    private Integer diasVencimiento;
    private String fuente;
    
    public ConsultaSaldoResponseBuilder numeroServicio(String numeroServicio) {
        this.numeroServicio = numeroServicio;
        return this;
    }
    
    public ConsultaSaldoResponseBuilder saldoActual(BigDecimal saldoActual) {
        this.saldoActual = saldoActual;
        return this;
    }
    
    public ConsultaSaldoResponseBuilder fechaUltimaActualizacion(LocalDateTime fecha) {
        this.fechaUltimaActualizacion = fecha;
        return this;
    }
    
    public ConsultaSaldoResponseBuilder consumoPromedio(BigDecimal consumoPromedio) {
        this.consumoPromedio = consumoPromedio;
        return this;
    }
    
    public ConsultaSaldoResponseBuilder factorCorreccion(BigDecimal factor) {
        this.factorCorreccion = factor;
        return this;
    }
    
    public ConsultaSaldoResponseBuilder tarifaActual(BigDecimal tarifa) {
        this.tarifaActual = tarifa;
        return this;
    }
    
    public ConsultaSaldoResponseBuilder diasVencimiento(Integer dias) {
        this.diasVencimiento = dias;
        return this;
    }
    
    public ConsultaSaldoResponseBuilder fuente(String fuente) {
        this.fuente = fuente;
        return this;
    }
    
    public ConsultaSaldoResponse build() {
        // Validaciones de Builder
        Objects.requireNonNull(numeroServicio, "Número de servicio es requerido");
        Objects.requireNonNull(saldoActual, "Saldo actual es requerido");
        
        // Calcular valores derivados
        BigDecimal saldoPendiente = calcularSaldoPendiente();
        EstadoServicio estado = determinarEstadoServicio();
        
        ConsultaSaldoResponse response = ConsultaSaldoResponse.builder()
            .numeroServicio(numeroServicio)
            .saldoActual(saldoActual)
            .saldoPendiente(saldoPendiente)
            .fechaUltimaActualizacion(fechaUltimaActualizacion)
            .consumoPromedio(consumoPromedio)
            .factorCorreccion(factorCorreccion)
            .tarifaActual(tarifaActual)
            .diasVencimiento(diasVencimiento)
            .estadoServicio(estado)
            .fuente(fuente)
            .fechaConsulta(LocalDateTime.now())
            .build();
        
        // Reset builder para siguiente uso
        reset();
        
        return response;
    }
    
    private BigDecimal calcularSaldoPendiente() {
        if (diasVencimiento != null && diasVencimiento > 0) {
            return saldoActual.multiply(factorCorreccion != null ? factorCorreccion : BigDecimal.ONE);
        }
        return BigDecimal.ZERO;
    }
    
    private EstadoServicio determinarEstadoServicio() {
        if (diasVencimiento == null) return EstadoServicio.ACTIVO;
        
        if (diasVencimiento > 60) return EstadoServicio.SUSPENDIDO;
        if (diasVencimiento > 30) return EstadoServicio.MORA_ALTA;
        if (diasVencimiento > 0) return EstadoServicio.MORA_BAJA;
        
        return EstadoServicio.ACTIVO;
    }
    
    private void reset() {
        this.numeroServicio = null;
        this.saldoActual = null;
        this.fechaUltimaActualizacion = null;
        this.consumoPromedio = null;
        this.factorCorreccion = null;
        this.tarifaActual = null;
        this.diasVencimiento = null;
        this.fuente = null;
    }
            }
        });
    }
    
    @Transactional(rollbackFor = Exception.class)
    public CompletableFuture<ActualizacionSaldoResponse> actualizarSaldoAcueducto(
            String numeroServicio, 
            BigDecimal montoPago) {
        
        return CompletableFuture.supplyAsync(() -> {
            
            MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("p_numero_servicio", numeroServicio)
                .addValue("p_monto_pago", montoPago)
                .addValue("p_fecha_pago", new Date(System.currentTimeMillis()))
                .addValue("p_canal_pago", "DIGITAL");
            
            jdbcCall.withProcedureName("PKG_ACUEDUCTO.SP_APLICAR_PAGO");
            
            Map<String, Object> result = jdbcCall.execute(parameters);
            
            String returnCode = (String) result.get("p_return_code");
            
            if ("SUCCESS".equals(returnCode)) {
                return ActualizacionSaldoResponse.builder()
                    .numeroServicio(numeroServicio)
                    .exitoso(true)
                    .nuevoSaldo((BigDecimal) result.get("p_nuevo_saldo"))
                    .transactionId((String) result.get("p_transaction_id"))
                    .build();
            } else {
                throw new LegacySystemException(
                    "Payment update failed: " + result.get("p_error_message"));
            }
        });
    }
}
```

---

## 5. JUSTIFICACIÓN TECNOLÓGICA

### 5.1 Evaluación de Alternativas por Componente

#### **Backend Framework**

```
Alternativa          | Pros                    | Contras                | Decisión
---------------------|-------------------------|------------------------|----------
Spring Boot          | ✅ Ecosistema maduro    | ❌ Overhead JVM       | ✅ ELEGIDO
                     | ✅ Microservices ready  | ❌ Startup time       |
                     | ✅ Amplia documentación | ❌ Memory footprint   |
                     | ✅ Equipo familiarizado|                       |

Quarkus              | ✅ Startup rápido       | ❌ Ecosistema menor    | ❌ Descartado
                     | ✅ Memory eficiente     | ❌ Curva aprendizaje  |
                     | ✅ GraalVM native       | ❌ Compatibilidad libs|

.NET Core            | ✅ Performance         | ❌ Licencias Microsoft | ❌ Descartado
                     | ✅ Async/await nativo   | ❌ Expertise team     |
                     | ✅ Memory management    | ❌ Linux tooling      |
```

**Justificación Spring Boot:** 
- Ecosistema maduro con Spring Cloud para microservicios
- Expertise del equipo existente reduce time-to-market
- Amplia integración con herramientas empresariales
- Soporte robusto para Resilience4j y Circuit Breaker patterns

#### **Base de Datos Principal**

```
Alternativa          | Pros                    | Contras                | Decisión
---------------------|-------------------------|------------------------|----------
PostgreSQL           | ✅ ACID completo        | ❌ Single-master      | ✅ ELEGIDO
                     | ✅ JSON support nativo  | ❌ Write scaling      |
                     | ✅ Extensiones maduras  |                       |
                     | ✅ Costo-efectivo      |                       |

MySQL                | ✅ Amplio soporte       | ❌ JSON limitado       | ❌ Descartado
                     | ✅ Read replicas fácil  | ❌ Funcionalidades    |
                     | ✅ Performance reads    | ❌ Transaccional      |

Oracle Database      | ✅ Funcionalidades      | ❌ Costo alto         | ❌ Descartado
                     | ✅ Performance          | ❌ Complejidad        |
                     | ✅ Integración legacy   | ❌ Licenciamiento     |
```

**Justificación PostgreSQL:**
- JSONB nativo para documentos flexibles (notificaciones, logs)
- Extensiones PostGIS para futuras funcionalidades geográficas
- Read replicas para escalamiento horizontal lecturas
- Partitioning automático para tablas de auditoría

#### **Event Streaming**

```
Alternativa          | Pros                    | Contras                | Decisión
---------------------|-------------------------|------------------------|----------
Apache Kafka         | ✅ Throughput alto      | ❌ Complejidad ops    | ✅ ELEGIDO
                     | ✅ Durabilidad          | ❌ Learning curve     |
                     | ✅ Ecosystem completo   | ❌ Resource intensive |
                     | ✅ Industry standard    |                       |

RabbitMQ             | ✅ Setup simple         | ❌ Throughput menor   | ❌ Descartado
                     | ✅ Message routing      | ❌ Scaling horizontal |
                     | ✅ Management UI        | ❌ Persistence perf   |

AWS SQS/SNS          | ✅ Fully managed        | ❌ Vendor lock-in     | ❌ Descartado
                     | ✅ Auto-scaling         | ❌ Latencia variable  |
                     | ✅ High availability    | ❌ Cost at scale      |
```

**Justificación Apache Kafka:**
- Throughput requerido (1000+ events/sec en picos)
- Durabilidad crítica para eventos de pago
- Stream processing capabilities para analytics futuro
- Expertise en equipo de infraestructura existente

#### **Cache y Sesiones**

```
Alternativa          | Pros                    | Contras                | Decisión
---------------------|-------------------------|------------------------|----------
Redis                | ✅ Data structures      | ❌ Memory-only default| ✅ ELEGIDO
                     | ✅ Lua scripting        | ❌ Cluster complexity |
                     | ✅ Pub/sub capabilities | ❌ Single threaded    |
                     | ✅ Session store proven |                       |

Memcached            | ✅ Simple setup         | ❌ Solo key-value     | ❌ Descartado
                     | ✅ Memory efficient     | ❌ No persistence     |
                     | ✅ Multi-threaded       | ❌ Limited data types |

Hazelcast            | ✅ Java native          | ❌ Memory overhead    | ❌ Descartado
                     | ✅ Distributed compute  | ❌ Licensing cost     |
                     | ✅ IMDG capabilities    | ❌ Complexity         |
```

**Justificación Redis:**
- Múltiples data structures (strings, hashes, sets, sorted sets)
- Persistence options (RDB + AOF) para durabilidad crítica
- Pub/Sub para real-time notifications
- Battle-tested para session management a escala

### 5.2 Trade-offs Arquitectónicos

#### **Microservicios vs Monolito**

```
Aspecto              | Microservicios         | Monolito              | Decisión
---------------------|------------------------|-----------------------|----------
Desarrollo           | ❌ Complejidad inicial | ✅ Simple start      | Microservicios
Escalabilidad        | ✅ Granular           | ❌ Todo o nada       | (Legacy integration
Testing              | ❌ Integration complex | ✅ E2E simple        |  requires isolation)
Deployment           | ✅ Independent         | ❌ Big bang          |
Monitoring           | ❌ Distributed tracing | ✅ Single point      |
Team Autonomy        | ✅ Independent teams   | ❌ Coordination req  |
```

**Justificación:** La integración con múltiples sistemas legacy (Mainframe, Oracle, Telecom) requiere aislamiento de fallos que solo microservicios pueden proporcionar efectivamente.

#### **Saga Pattern vs 2PC**

```
Aspecto              | Saga Pattern           | Two-Phase Commit      | Decisión
---------------------|------------------------|-----------------------|----------
Disponibilidad       | ✅ Alta (no locks)     | ❌ Baja (locks)      | Saga Pattern
Consistencia         | ❌ Eventual           | ✅ Inmediata         | (Legacy systems
Complejidad          | ❌ Compensation logic  | ✅ Protocol standard  |  no soportan 2PC)
Legacy Support       | ✅ HTTP/API friendly   | ❌ Requiere XA       |
Performance          | ✅ No blocking         | ❌ Locks resources   |
```

**Justificación:** Sistemas legacy no soportan XA transactions, Saga pattern permite compensaciones manuales y mejor tolerancia a fallos.

#### **Pull vs Push Notifications**

```
Aspecto              | Pull (Polling)         | Push (WebSocket/SSE)  | Decisión
---------------------|------------------------|-----------------------|----------
Real-time            | ❌ Latencia polling    | ✅ Inmediato         | Híbrido
Scalability          | ✅ Stateless           | ❌ Connection state   | (Push crítico,
Battery Life         | ❌ Constant polling    | ✅ Event-driven      |  Pull backup)
Complexity           | ✅ Simple HTTP         | ❌ Connection mgmt    |
Reliability          | ✅ Self-healing        | ❌ Connection drops   |
```

**Justificación:** Push para notificaciones críticas (pagos, emergencias), Pull para actualizaciones de estado menos críticas.

---

## CONCLUSIONES ARQUITECTURA TÉCNICA

### Validación de Decisiones

✅ **Escalabilidad Horizontal:** Kubernetes + microservicios permite crecimiento elástico  
✅ **Resiliencia Legacy:** Circuit Breaker + Saga patterns manejan fallos sistemas antiguos  
✅ **Performance:** Cache multi-nivel + async processing cumple RNF <2s consultas  
✅ **Mantenibilidad:** Clean Architecture + DDD facilita evolución código  
✅ **Observabilidad:** Distributed tracing + metrics centralizadas  

### Próximo Documento

**04_Especificacion_APIs_Modelo_Datos.md** - Documentación completa de APIs REST, modelo de datos detallado y especificaciones de integración.

---
*Arquitectura Técnica - ServiCiudad Conectada*  
*Universidad Autónoma de Occidente - Septiembre 2025*
