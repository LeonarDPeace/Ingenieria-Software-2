# INFORME TECNICO - ServiCiudad Cali

## Sistema de Consulta Unificada de Deuda Consolidada

**Fecha:** Octubre 2025  
**Curso:** Ingenieria de Software 2  
**Universidad:** Universidad Autonoma de Occidente  
**Equipo:** Eduard Criollo, Felipe Charria, Jhonathan Chicaiza, Emmanuel Mena, Juan Sebastian Castillo

---

## Tabla de Contenidos

1. [Problema Critico Resuelto](#problema-critico-resuelto)
2. [Arquitectura General](#arquitectura-general)
3. [Patrones de Diseno Implementados](#patrones-de-diseno-implementados)
4. [Justificacion Tecnica](#justificacion-tecnica)
5. [Decisiones de Arquitectura](#decisiones-de-arquitectura)
6. [Cumplimiento de Requisitos](#cumplimiento-de-requisitos)
7. [Validacion y Pruebas](#validacion-y-pruebas)
8. [Scripts de Automatizacion](#scripts-de-automatizacion)

---

## Arquitectura General

### Visión General del Monolito

ServiCiudad Cali es una **aplicación monolítica** construida con **Spring Boot 3.x** y **Java 17**, diseñada para centralizar la consulta de deuda de servicios públicos (Energía y Acueducto) a través de una **API RESTful** unificada.

### Estructura de Capas (Arquitectura Hexagonal Validada)

```
┌─────────────────────────────────────────────────────────────┐
│                   CAPA DE PRESENTACIÓN                      │
│  (REST Controllers + DTOs)                                 │
│  - DeudaRestController.java (@RestController) ✅           │
│  - FacturaRestController.java (@RestController) ✅         │
│  - ConsumoEnergiaRestController.java (@RestController) ✅  │
└─────────────────────────────────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    CAPA DE APLICACIÓN                       │
│  (Use Cases + DTOs + Mappers)                              │
│  - ConsultarDeudaConsolidadaUseCase.java (@Service) ✅     │
│  - ConsultarFacturasPorClienteUseCase.java (@Service) ✅   │
│  - ConsultarConsumosPorClienteUseCase.java (@Service) ✅   │
│  - DeudaConsolidadaDTO (Builder Pattern) ✅                │
│  - ResponseMapper.java ✅                                   │
└─────────────────────────────────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                     CAPA DE DOMINIO                         │
│  (Entidades de Negocio + Puertos + Lógica)                 │
│  - FacturaAcueducto.java ✅                                 │
│  - ConsumoEnergia.java ✅                                   │
│  - DeudaConsolidada.java ✅                                 │
│  - Ports (Interfaces para Inversión de Dependencias) ✅    │
└─────────────────────────────────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                 CAPA DE INFRAESTRUCTURA                     │
│  (Adaptadores + Implementaciones de Puertos)               │
│                                                             │
│  ┌──────────────────────┐  ┌──────────────────────┐       │
│  │  ADAPTADOR ARCHIVO   │  │   ADAPTADOR JPA      │       │
│  │  ConsumoEnergiaReader│  │  FacturaRepository   │       │
│  │  Adapter (@Component)│  │  Adapter (@Component)│       │
│  │  ✅ VALIDADO         │  │  ✅ VALIDADO         │       │
│  └──────────────────────┘  └──────────────────────┘       │
│           ▼                          ▼                      │
│  ┌──────────────────────┐  ┌──────────────────────┐       │
│  │  Archivo TXT Plano   │  │  PostgreSQL Database │       │
│  │  consumos_energia.txt│  │  serviciudad_db      │       │
│  │  (Mainframe Legacy)  │  │  Port: 5432          │       │
│  │  VALIDADO            │  │  VALIDADO            │       │
│  └──────────────────────┘  └──────────────────────┘       │
└─────────────────────────────────────────────────────────────┘
```

### Arquitectura Hexagonal (Ports & Adapters)

Aunque es un monolito, implementamos **Arquitectura Hexagonal** para:

1. **Separar la lógica de negocio de la infraestructura**
   - El dominio NO conoce detalles de bases de datos ni archivos
   
2. **Facilitar el testing**
   - Podemos probar el dominio sin levantar infraestructura
   
3. **Preparar para migración a microservicios**
   - Los puertos definen contratos claros para futura descomposición

---

## Patrones de Diseno Implementados

Se implementaron **5 patrones de diseño** según los requisitos del proyecto:

### 1. **Patron Adapter** 

#### Ubicaciones en el Código

**Archivo Principal:**
```
src/main/java/com/serviciudad/infrastructure/adapter/output/persistence/
└── ConsumoEnergiaReaderAdapter.java (líneas 1-45)
```

**Archivos Relacionados:**
- **Puerto (Interface):** `domain/port/output/ConsumoEnergiaReaderPort.java` (líneas 8-14)
- **Entidad JPA:** `infrastructure/adapter/output/persistence/jpa/entity/ConsumoEnergiaJpaEntity.java`
- **Repositorio JPA:** `infrastructure/adapter/output/persistence/jpa/repository/ConsumoEnergiaJpaRepository.java`
- **Mapper:** `infrastructure/adapter/output/persistence/jpa/mapper/ConsumoEnergiaJpaMapper.java`

#### Funcionamiento

El patrón Adapter convierte la interfaz de una clase en otra interfaz que los clientes esperan. En nuestro caso, `ConsumoEnergiaReaderAdapter` adapta la tecnología JPA/PostgreSQL a la interfaz de dominio `ConsumoEnergiaReaderPort`.

**Flujo de Adaptación:**
1. El Use Case solicita datos mediante el puerto: `ConsumoEnergiaReaderPort.findByClienteId(clienteId)`
2. El Adapter recibe la llamada y traduce los parámetros de Value Objects de dominio a tipos primitivos
3. Delega la consulta al repositorio JPA: `jpaRepository.findByClienteId(clienteId.getValor())`
4. Recibe entidades JPA (`ConsumoEnergiaJpaEntity`) desde PostgreSQL
5. Convierte las entidades JPA a modelos de dominio usando el Mapper: `ConsumoEnergiaJpaMapper.toDomain()`
6. Retorna `List<ConsumoEnergiaModel>` al Use Case

**Beneficios Implementados:**
- El dominio NO conoce que usamos JPA ni PostgreSQL
- Podemos cambiar de JPA a MongoDB sin tocar el dominio
- El Use Case trabaja con objetos de dominio puros, no con entidades JPA
- Facilita testing con mocks del puerto

---

### 2. **Patron Builder** 

#### Ubicaciones en el Código

**Archivos con @Builder (Lombok):**
```
src/main/java/com/serviciudad/application/dto/response/
├── DeudaConsolidadaResponse.java (líneas 10-12: @Builder)
├── FacturaResponse.java (@Builder)
├── ConsumoEnergiaResponse.java (@Builder)
└── EstadisticasResponse.java (@Builder)
```

**Uso en Use Cases:**
- `application/usecase/ConsultarDeudaUseCaseImpl.java` (líneas 85-95)
- `application/mapper/DeudaMapper.java` (líneas 28-40)

#### Funcionamiento

El patrón Builder permite construir objetos complejos paso a paso usando un patrón fluido y legible. Utilizamos la anotación `@Builder` de Lombok que genera automáticamente la clase Builder interna.

**Ejemplo de Construcción:**
```java
// En ConsultarDeudaUseCaseImpl.java (líneas 85-95)
DeudaConsolidadaResponse response = DeudaConsolidadaResponse.builder()
    .clienteId(clienteId.getValor())
    .fechaConsulta(LocalDateTime.now())
    .deudaTotalAcueducto(totalAcueducto)
    .deudaTotalEnergia(totalEnergia)
    .totalGeneral(totalGeneral)
    .facturasAcueducto(facturasDTO)
    .consumosEnergia(consumosDTO)
    .alertas(alertas)
    .estadisticas(estadisticas)
    .build();
```

**Ventajas de Uso:**
- **Legibilidad:** Cada línea indica claramente qué campo se asigna
- **Flexibilidad:** Podemos omitir campos opcionales sin sobrecargar constructores
- **Inmutabilidad:** Una vez construido con `.build()`, el objeto no se modifica
- **Mantenibilidad:** Agregar un campo nuevo no rompe el código existente
- **Type Safety:** El compilador valida que los tipos sean correctos

---

### 3. **Patron Data Transfer Object (DTO)** 

#### Ubicaciones en el Código

**DTOs de Request (Entrada):**
```
src/main/java/com/serviciudad/application/dto/request/
├── ConsultarDeudaRequest.java (validaciones con Bean Validation)
└── RegistrarPagoRequest.java
```

**DTOs de Response (Salida):**
```
src/main/java/com/serviciudad/application/dto/response/
├── DeudaConsolidadaResponse.java (DTO principal, usa Builder)
├── FacturaResponse.java (factura simplificada para API)
├── ConsumoEnergiaResponse.java (consumo simplificado)
└── EstadisticasResponse.java (estadísticas calculadas)
```

**Mappers (Entidad ↔ DTO):**
```
src/main/java/com/serviciudad/application/mapper/
└── DeudaMapper.java (convierte modelos de dominio a DTOs de respuesta)
```

**Uso en Controladores:**
```
src/main/java/com/serviciudad/infrastructure/adapter/input/rest/
├── DeudaRestController.java (líneas 35-42: recibe/retorna DTOs)
├── FacturaRestController.java (líneas 28-35: recibe/retorna DTOs)
└── ConsumoEnergiaRestController.java (líneas 25-32: recibe/retorna DTOs)
```

#### Funcionamiento

El patrón DTO define objetos que transportan datos entre procesos sin lógica de negocio. Separa completamente la representación API (DTOs) de las entidades de dominio y JPA.

**Capas de Separación:**
```
Cliente REST
    ↓ JSON
[DTO de Response] ←── Mapper ←── [Modelo de Dominio] ←── Adapter ←── [Entidad JPA] ←─ BD
    ↑ JSON                              ↑
[DTO de Request] ───→ Controller ───→ Use Case
```

**Flujo Completo (Ejemplo: Consultar Deuda):**
1. **Cliente envía:** `GET /api/deuda/cliente/1001234567` con Authorization header
2. **Controller recibe:** Request sin DTO (solo path param `clienteId`)
3. **Use Case consulta:** Llama a puertos, obtiene `DeudaConsolidada` (modelo de dominio)
4. **Mapper convierte:** `DeudaConsolidada` → `DeudaConsolidadaResponse` (DTO)
5. **Controller retorna:** JSON serializado de `DeudaConsolidadaResponse`

**Beneficios Implementados:**
- **Seguridad:** No exponemos IDs internos de base de datos
- **Desacoplamiento:** Cambios en BD no afectan la API
- **Optimización:** Enviamos solo datos necesarios (reduce payload)
- **Versionado:** Podemos tener DTOv1 y DTOv2 sin cambiar entidades
- **Validación:** DTOs de Request usan Bean Validation (`@NotNull`, `@Pattern`)

---

### 4. **Patron Repository** (Provisto por Spring Data JPA)

#### Ubicaciones en el Código

**Interfaces Repository (Spring Data JPA):**
```
src/main/java/com/serviciudad/infrastructure/adapter/output/persistence/jpa/repository/
├── FacturaJpaRepository.java (líneas 12-20: extends JpaRepository)
└── ConsumoEnergiaJpaRepository.java (extends JpaRepository)
```

**Puerto de Dominio (Interface):**
```
src/main/java/com/serviciudad/domain/port/output/
├── FacturaRepositoryPort.java (interfaz de dominio)
└── ConsumoEnergiaReaderPort.java (interfaz de dominio)
```

**Adaptadores que Implementan Puertos:**
```
src/main/java/com/serviciudad/infrastructure/adapter/output/persistence/
├── FacturaRepositoryAdapter.java (implementa FacturaRepositoryPort)
└── ConsumoEnergiaReaderAdapter.java (implementa ConsumoEnergiaReaderPort)
```

**Entidades JPA:**
```
src/main/java/com/serviciudad/infrastructure/adapter/output/persistence/jpa/entity/
├── FacturaJpaEntity.java (@Entity, @Table)
├── ConsumoEnergiaJpaEntity.java (@Entity)
└── EstadoFacturaJpa.java (@Enumerated)
```

#### Funcionamiento

El patrón Repository abstrae el acceso a datos proporcionando una interfaz similar a una colección de objetos en memoria. Spring Data JPA genera automáticamente la implementación basándose en los nombres de los métodos.

**Ejemplo de Repository (FacturaJpaRepository):**
```java
// ═══════════════════════════════════════════════════════════════
// @Repository: Marca esta interfaz como componente de acceso a datos
// ═══════════════════════════════════════════════════════════════
// Spring Data JPA generará automáticamente la implementación en runtime
// NO necesitamos escribir una clase que implemente esta interfaz
@Repository
public interface FacturaJpaRepository extends JpaRepository<FacturaJpaEntity, Long> {
    //                                              ↑                   ↑          ↑
    //                                              |                   |          |
    //                        JpaRepository: Interfaz genérica          |          |
    //                        proporcionada por Spring Data       Tipo de la   Tipo del ID
    //                                                             Entidad JPA  (Long)
    
    // ═══════════════════════════════════════════════════════════════
    // QUERY METHODS: Spring Data JPA genera SQL automáticamente
    // ═══════════════════════════════════════════════════════════════
    // Basándose en el NOMBRE del método, Spring genera la query SQL
    
    // ───────────────────────────────────────────────────────────────
    // MÉTODO 1: Buscar facturas por ID de cliente
    // ───────────────────────────────────────────────────────────────
    // findBy: Prefijo que indica búsqueda (SELECT)
    // ClienteId: Campo de la entidad FacturaJpaEntity
    // Spring genera automáticamente:
    //   SELECT * FROM facturas_acueducto WHERE cliente_id = ?
    // Parámetro: String clienteId → se pasa como valor del WHERE
    // Retorno: List<FacturaJpaEntity> → Puede estar vacía si no hay resultados
    List<FacturaJpaEntity> findByClienteId(String clienteId);
    
    // ───────────────────────────────────────────────────────────────
    // MÉTODO 2: Buscar facturas por cliente Y periodo
    // ───────────────────────────────────────────────────────────────
    // And: Operador lógico que combina dos condiciones
    // Spring genera automáticamente:
    //   SELECT * FROM facturas_acueducto 
    //   WHERE cliente_id = ? AND periodo = ?
    // Orden de parámetros: Debe coincidir con el orden en el nombre del método
    List<FacturaJpaEntity> findByClienteIdAndPeriodo(String clienteId, String periodo);
    
    // ───────────────────────────────────────────────────────────────
    // MÉTODO 3: Buscar facturas por estado
    // ───────────────────────────────────────────────────────────────
    // Estado: Campo de tipo Enum en la entidad
    // EstadoFacturaJpa: Enum con valores (PENDIENTE, PAGADA, VENCIDA)
    // Spring genera automáticamente:
    //   SELECT * FROM facturas_acueducto WHERE estado = ?
    // JPA convierte el Enum a String automáticamente (@Enumerated)
    List<FacturaJpaEntity> findByEstado(EstadoFacturaJpa estado);
    
    // ───────────────────────────────────────────────────────────────
    // MÉTODO 4: Paginación automática
    // ───────────────────────────────────────────────────────────────
    // Page<T>: Objeto que contiene resultados paginados + metadatos
    // Pageable: Parámetro que especifica página, tamaño, ordenamiento
    // Uso: findByEstado(PENDIENTE, PageRequest.of(0, 10))
    //      ↑ Retorna primeras 10 facturas pendientes (página 0)
    // Spring genera automáticamente:
    //   SELECT * FROM facturas_acueducto 
    //   WHERE estado = ? 
    //   LIMIT 10 OFFSET 0
    // Page incluye: contenido, total de elementos, total de páginas, etc.
    Page<FacturaJpaEntity> findByEstado(EstadoFacturaJpa estado, Pageable pageable);
    
    // ═══════════════════════════════════════════════════════════════
    // MÉTODOS HEREDADOS de JpaRepository (disponibles automáticamente)
    // ═══════════════════════════════════════════════════════════════
    // save(entity): Inserta o actualiza (detecta automáticamente)
    // findById(id): Busca por clave primaria, retorna Optional<T>
    // findAll(): Retorna todas las entidades (usar con cuidado)
    // delete(entity): Elimina una entidad
    // count(): Cuenta total de registros
    // existsById(id): Verifica si existe un registro con ese ID
    // ... y muchos más métodos CRUD predefinidos
}
```

**Flujo de Acceso a Datos:**
1. **Use Case** solicita datos: `facturaRepository.obtenerFacturasPorCliente(clienteId)`
2. **Adapter** traduce a JPA: `jpaRepository.findByClienteId(clienteId.getValor())`
3. **Spring Data JPA** genera SQL: `SELECT * FROM facturas_acueducto WHERE cliente_id = ?`
4. **Hibernate** ejecuta query en PostgreSQL
5. **JPA** mapea ResultSet a `FacturaJpaEntity`
6. **Adapter** convierte a dominio: `FacturaJpaMapper.toDomain(entity)`
7. **Use Case** recibe: `List<FacturaAcueducto>` (modelos de dominio)

**Beneficios Implementados:**
- **Eliminación de Boilerplate:** No escribimos SQL repetitivo
- **Type Safety:** Errores en nombres de métodos se detectan en compilación
- **Abstracción de Proveedor:** Podemos cambiar de PostgreSQL a MySQL sin código
- **Testing Fácil:** Usamos bases en memoria (H2) para tests
- **Optimización:** JPA usa caché, lazy loading, batch fetching automáticamente

---

### 5. **Inversion de Control / Inyeccion de Dependencias** (Provisto por Spring Framework)

#### Ubicaciones en el Código

**Configuración Spring:**
```
src/main/java/com/serviciudad/infrastructure/config/
├── SecurityConfig.java (líneas 57-99: @Bean definitions)
├── WebConfig.java (@Configuration, @Bean)
├── OpenApiConfig.java (@Configuration)
└── DatabaseConfig.java (@Configuration)
```

**Use Cases con DI:**
```
src/main/java/com/serviciudad/application/usecase/
├── ConsultarDeudaUseCaseImpl.java (líneas 25-31: @Autowired constructor)
├── GestionarFacturaUseCaseImpl.java (@Service con DI)
└── ConsultarConsumoEnergiaUseCaseImpl.java (@Service con DI)
```

**Adaptadores con DI:**
```
src/main/java/com/serviciudad/infrastructure/adapter/
├── output/persistence/FacturaRepositoryAdapter.java (líneas 18-22: @Autowired)
├── output/persistence/ConsumoEnergiaReaderAdapter.java (@RequiredArgsConstructor de Lombok)
└── input/rest/*.java (todos usan @Autowired en constructores)
```

**Controllers con DI:**
```
src/main/java/com/serviciudad/infrastructure/adapter/input/rest/
├── DeudaRestController.java (líneas 22-26: DI del Use Case)
├── FacturaRestController.java (@Autowired Use Case)
└── ConsumoEnergiaRestController.java (@Autowired Use Case)
```

#### Funcionamiento

La Inversión de Control (IoC) invierte el flujo de control: en lugar de que la aplicación cree sus dependencias, el contenedor de Spring las crea e inyecta. La Inyección de Dependencias (DI) es el mecanismo mediante el cual se entregan las dependencias.

**Ciclo de Vida con Spring IoC:**

```
1. ESCANEO DE COMPONENTES
   Spring escanea todas las clases anotadas:
   - @Component, @Service, @Repository, @RestController
   - @Configuration, @Bean

2. CREACIÓN DE BEANS
   Spring crea instancias (beans) en el Application Context:
   [FacturaJpaRepository] ← Spring Data genera implementación
   [FacturaRepositoryAdapter] ← Spring instancia @Component
   [ConsultarDeudaUseCaseImpl] ← Spring instancia @Service
   [DeudaRestController] ← Spring instancia @RestController

3. RESOLUCIÓN DE DEPENDENCIAS
   Spring analiza constructores con @Autowired:
   
   DeudaRestController(
       ConsultarDeudaUseCase useCase ← Spring inyecta
   )
   
   ConsultarDeudaUseCaseImpl(
       FacturaRepositoryPort facturaRepo ← Spring inyecta Adapter
       ConsumoEnergiaReaderPort energiaReader ← Spring inyecta Adapter
   )

4. INYECCIÓN
   Spring conecta los beans automáticamente:
   DeudaRestController → ConsultarDeudaUseCaseImpl → Adapters → Repositories

5. GESTIÓN DEL CICLO DE VIDA
   Spring gestiona creación, inicialización (@PostConstruct) y destrucción (@PreDestroy)
```

**Ejemplo Real del Código:**

```java
// ═══════════════════════════════════════════════════════════════
// CAPA DE PRESENTACIÓN: DeudaRestController.java (líneas 22-26)
// ═══════════════════════════════════════════════════════════════

// @RestController: Combina @Controller + @ResponseBody
// Indica que esta clase maneja peticiones HTTP REST y retorna JSON automáticamente
@RestController

// @RequestMapping: Define el path base para todos los endpoints de este controlador
// Todos los métodos heredarán este prefijo: /api/deuda
@RequestMapping("/api/deuda")

// @RequiredArgsConstructor: Anotación de Lombok que genera automáticamente:
//   - Constructor con parámetros para TODOS los campos marcados como "final"
//   - Spring detecta este constructor y realiza inyección de dependencias
@RequiredArgsConstructor
public class DeudaRestController {
    
    // ═══════════════════════════════════════════════════════════════
    // INYECCIÓN DE DEPENDENCIAS: El controlador NO crea el Use Case
    // ═══════════════════════════════════════════════════════════════
    
    // final: La referencia no puede cambiar una vez asignada (inmutabilidad)
    // ConsultarDeudaUseCase: INTERFAZ (puerto de entrada)
    // Spring inyecta automáticamente una implementación (ConsultarDeudaUseCaseImpl)
    // ¿Cómo? Lombok genera: public DeudaRestController(ConsultarDeudaUseCase use) {...}
    //        Spring detecta el constructor y busca un @Service que implemente la interfaz
    private final ConsultarDeudaUseCase consultarDeudaUseCase; // ← Spring inyecta
    
    // ═══════════════════════════════════════════════════════════════
    // ENDPOINT REST: Maneja peticiones GET a /api/deuda/cliente/{id}
    // ═══════════════════════════════════════════════════════════════
    
    // @GetMapping: Mapea peticiones HTTP GET al método
    // {clienteId}: Variable de path que se extraerá de la URL
    // Ejemplo: GET /api/deuda/cliente/1001234567 → clienteId = "1001234567"
    @GetMapping("/cliente/{clienteId}")
    
    // ResponseEntity<T>: Clase de Spring que representa respuesta HTTP completa
    // Permite controlar status code, headers, body
    // <DeudaConsolidadaResponse>: Tipo genérico, indica qué va en el body
    // @PathVariable: Extrae el valor de {clienteId} de la URL
    public ResponseEntity<DeudaConsolidadaResponse> consultarDeuda(
        @PathVariable String clienteId
    ) {
        // ───────────────────────────────────────────────────────────
        // DELEGACIÓN: El controlador NO tiene lógica de negocio
        // ───────────────────────────────────────────────────────────
        
        // consultarDeudaUseCase.ejecutar(): Delega al Use Case (capa de aplicación)
        // El controlador SOLO coordina: recibe request → llama use case → retorna response
        // Principio de Responsabilidad Única: El controlador solo maneja HTTP
        
        // ResponseEntity.ok(): Crea respuesta con status 200 OK
        // El objeto DeudaConsolidadaResponse se serializa automáticamente a JSON
        return ResponseEntity.ok(consultarDeudaUseCase.ejecutar(clienteId));
    }
}
```

```java
// ═══════════════════════════════════════════════════════════════
// CAPA DE APLICACIÓN: ConsultarDeudaUseCaseImpl.java (líneas 25-31)
// ═══════════════════════════════════════════════════════════════

// @Service: Marca esta clase como componente de lógica de negocio
// Spring la registra como Bean y la inyectará donde sea necesaria
@Service

// @RequiredArgsConstructor: Lombok genera constructor con campos final
// Mismo mecanismo que en el controlador para inyección de dependencias
@RequiredArgsConstructor
public class ConsultarDeudaUseCaseImpl implements ConsultarDeudaUseCase {
    
    // ═══════════════════════════════════════════════════════════════
    // INYECCIÓN DE PUERTOS (INTERFACES): Inversión de Dependencias
    // ═══════════════════════════════════════════════════════════════
    
    // final: Inmutabilidad, la referencia no cambiará
    // FacturaRepositoryPort: INTERFAZ de dominio (puerto de salida)
    // Spring inyecta: FacturaRepositoryAdapter (implementación concreta)
    // CLAVE: El Use Case NO conoce la implementación, solo la interfaz
    private final FacturaRepositoryPort facturaRepository; // ← Spring inyecta Adapter
    
    // ConsumoEnergiaReaderPort: Otra interfaz de dominio
    // Spring inyecta: ConsumoEnergiaReaderAdapter (lee archivo mainframe)
    private final ConsumoEnergiaReaderPort energiaReader; // ← Spring inyecta Adapter
    
    // ═══════════════════════════════════════════════════════════════
    // LÓGICA DE NEGOCIO: Orquestación de servicios
    // ═══════════════════════════════════════════════════════════════
    
    // @Override: Indica que implementamos método de la interfaz
    // Esto garantiza que cumplimos el contrato definido en ConsultarDeudaUseCase
    @Override
    public DeudaConsolidada ejecutar(String clienteIdString) {
        
        // ───────────────────────────────────────────────────────────
        // PASO 1: Validación y conversión a Value Object
        // ───────────────────────────────────────────────────────────
        // ClienteId: Value Object que encapsula y valida el ID del cliente
        // new ClienteId(): Lanzará excepción si el formato es inválido
        ClienteId clienteId = new ClienteId(clienteIdString);
        
        // ───────────────────────────────────────────────────────────
        // PASO 2: Consultar facturas (desde BD PostgreSQL)
        // ───────────────────────────────────────────────────────────
        // facturaRepository.obtenerFacturasPorCliente(): Llamada al puerto
        // En runtime, ejecuta: FacturaRepositoryAdapter → JPA → PostgreSQL
        // El Use Case NO sabe que usa JPA ni PostgreSQL (bajo acoplamiento)
        List<FacturaAcueducto> facturas = facturaRepository.obtenerFacturasPorCliente(clienteId);
        
        // ───────────────────────────────────────────────────────────
        // PASO 3: Consultar consumos (desde archivo mainframe)
        // ───────────────────────────────────────────────────────────
        // energiaReader.findByClienteId(): Llamada al puerto
        // En runtime, ejecuta: ConsumoEnergiaReaderAdapter → BufferedReader → TXT
        // El Use Case NO sabe que lee un archivo (abstracción completa)
        List<ConsumoEnergiaModel> consumos = energiaReader.findByClienteId(clienteId);
        
        // ───────────────────────────────────────────────────────────
        // PASO 4: Consolidar información (lógica de negocio)
        // ───────────────────────────────────────────────────────────
        // DeudaConsolidada: Entidad de dominio que agrega facturas + consumos
        // Constructor aplica reglas de negocio:
        //   - Calcula totales
        //   - Genera alertas (facturas vencidas, consumo alto, etc.)
        //   - Calcula estadísticas
        return new DeudaConsolidada(facturas, consumos);
    }
}
```

**Beneficios Implementados:**
- **Bajo Acoplamiento:** Controlador NO conoce implementaciones concretas (solo interfaces)
- **Alta Testabilidad:** Podemos inyectar mocks en tests unitarios
- **Configuración Centralizada:** `@Configuration` para cambiar implementaciones
- **Singleton por Defecto:** Una instancia por bean (eficiencia de memoria)
- **Lifecycle Management:** Spring inicializa beans en orden de dependencias
- **Hot Swap:** En desarrollo, Spring DevTools recarga beans automáticamente

---

## Justificacion Tecnica de Patrones

### 1. Patron Adapter

#### **Problema a Resolver**
El **Sistema de Energía (Mainframe IBM Z)** genera un archivo plano de ancho fijo (`consumos_energia.txt`) con formato legacy incompatible:

```
// Formato: id_cliente(10), periodo(6), consumo_kwh(8), valor_pagar(12)
000123456720251000001500000180000.50
```

Nuestra aplicación moderna trabaja con **objetos Java** (`ConsumoEnergia`), no con strings de ancho fijo.

#### **Solución Implementada**

```java
// @Component: Anotación de Spring que marca esta clase como un Bean gestionado
// Spring creará una instancia automáticamente y la inyectará donde sea necesaria
@Component
public class ConsumoEnergiaReaderAdapter implements ConsumoEnergiaReaderPort {
    
    // Implementación del método definido en el puerto (interfaz de dominio)
    // Este método es el CONTRATO que el dominio espera, independiente de la tecnología
    @Override
    public List<ConsumoEnergia> obtenerConsumosPorCliente(String clienteId) {
        // ArrayList: Estructura de datos dinámica que crece según necesidad
        // Almacenará todos los consumos del cliente encontrados en el archivo
        List<ConsumoEnergia> consumos = new ArrayList<>();
        
        // try-with-resources: Garantiza que el archivo se cierre automáticamente
        // BufferedReader: Lee el archivo línea por línea eficientemente (usa buffer interno)
        // Files.newBufferedReader(): Método de Java NIO que maneja encoding automáticamente
        try (BufferedReader reader = Files.newBufferedReader(archivoPath)) {
            String linea;
            
            // Bucle que lee línea por línea hasta el final del archivo (null = EOF)
            while ((linea = reader.readLine()) != null) {
                // ═══════════════════════════════════════════════════════════════
                // ADAPTACIÓN: Parseo de formato de ancho fijo a objeto Java
                // ═══════════════════════════════════════════════════════════════
                
                // substring(inicio, fin): Extrae caracteres desde posición 0 hasta 9 (10 chars)
                // Formato mainframe: Posiciones 0-9 contienen el ID del cliente (ej: "1001234567")
                // trim(): Elimina espacios en blanco al inicio y final
                String clienteIdArchivo = linea.substring(0, 10).trim();
                
                // Filtro: Solo procesamos líneas que pertenezcan al cliente buscado
                // Esto evita cargar en memoria consumos de otros clientes
                if (clienteIdArchivo.equals(clienteId)) {
                    
                    // Extracción de campos con posiciones fijas (formato legacy):
                    // Posiciones 10-15: Periodo en formato YYYYMM (ej: "202510" = Oct 2025)
                    String periodo = linea.substring(10, 16);
                    
                    // Posiciones 16-23: Consumo en kWh (ej: "00001500" = 1500 kWh)
                    // Integer.parseInt(): Convierte String numérico a tipo primitivo int
                    // trim(): Importante para evitar NumberFormatException por espacios
                    int consumoKwh = Integer.parseInt(linea.substring(16, 24).trim());
                    
                    // Posiciones 24-35: Valor a pagar (ej: "000180000.50" = $180,000.50)
                    // Double.parseDouble(): Convierte String a número decimal (punto flotante)
                    double valorPagar = Double.parseDouble(linea.substring(24, 36).trim());
                    
                    // ═══════════════════════════════════════════════════════════════
                    // Creación del objeto de dominio (Clean Architecture)
                    // ═══════════════════════════════════════════════════════════════
                    // ConsumoEnergia: Entidad de dominio que representa el concepto de negocio
                    // NO es una entidad JPA ni un DTO, es un objeto PURO de dominio
                    ConsumoEnergia consumo = new ConsumoEnergia(
                        clienteId,    // ID del cliente (ya validado por el if)
                        periodo,      // Periodo de facturación (String en formato YYYYMM)
                        consumoKwh,   // Cantidad de energía consumida (entero positivo)
                        valorPagar    // Monto a pagar (decimal con 2 decimales)
                    );
                    
                    // Agregar el objeto convertido a la lista de resultados
                    consumos.add(consumo);
                }
            }
        } // El BufferedReader se cierra automáticamente aquí (try-with-resources)
        
        // Retornamos la lista completa de consumos del cliente
        // Puede estar vacía si el cliente no tiene consumos registrados
        return consumos;
    }
}
```

#### **Beneficios**

**Desacoplamiento:** La logica de negocio NO conoce el formato de archivo  
**Testabilidad:** Podemos crear un `MockConsumoEnergiaReaderAdapter` para pruebas  
**Mantenibilidad:** Si el formato cambia, solo modificamos el adaptador  
**Principio de Responsabilidad Unica:** El adaptador se encarga SOLO de la conversion

---

### 2. Patron Builder

#### **Problema a Resolver**
La respuesta JSON `DeudaConsolidadaDTO` es **compleja** y se construye desde **múltiples fuentes**:

- Datos del cliente
- Lista de facturas de acueducto
- Lista de consumos de energía
- Estadísticas calculadas
- Alertas generadas dinámicamente

Construir este objeto con **constructor tradicional** sería:
```java
// ❌ CÓDIGO DIFÍCIL DE LEER Y MANTENER
new DeudaConsolidadaDTO(
    clienteId, 
    nombreCliente, 
    fechaConsulta, 
    facturasAcueducto, 
    consumosEnergia, 
    estadisticas, 
    alertas, 
    totalAPagar
); // ¿En qué orden van? ¿Qué pasa si agrego un campo?
```

#### **Solución Implementada**

```java
// Clase DTO (Data Transfer Object) que se envía como respuesta JSON al cliente
// Representa una consulta completa de deuda consolidada con múltiples servicios
public class DeudaConsolidadaDTO {
    
    // ═══════════════════════════════════════════════════════════════
    // ATRIBUTOS: Campos que se serializarán a JSON
    // ═══════════════════════════════════════════════════════════════
    
    // Identificación del cliente (ej: "1001234567")
    private String clienteId;
    
    // Nombre completo del cliente para mostrar en UI (ej: "Juan Pérez")
    private String nombreCliente;
    
    // LocalDateTime: Clase de Java 8+ para fecha/hora (thread-safe, inmutable)
    // Indica cuándo se realizó la consulta de deuda
    private LocalDateTime fechaConsulta;
    
    // List<FacturaAcueductoDTO>: Lista genérica que contiene facturas pendientes
    // Cada DTO es un objeto simplificado (no expone estructura interna de BD)
    private List<FacturaAcueductoDTO> facturasAcueducto;
    
    // Lista de consumos eléctricos del cliente
    // Proviene del sistema legacy de energía (archivo mainframe)
    private List<ConsumoEnergiaDTO> consumosEnergia;
    
    // Objeto que contiene estadísticas calculadas (promedios, máximos, tendencias)
    private EstadisticasDTO estadisticas;
    
    // Lista de alertas generadas dinámicamente según reglas de negocio
    // Ej: ["Factura vencida desde hace 30 días", "Consumo 50% superior al promedio"]
    private List<String> alertas;
    
    // Total consolidado a pagar (suma de todas las deudas pendientes)
    // double: Tipo primitivo para decimales (suficiente para montos monetarios)
    private double totalAPagar;
    
    // ═══════════════════════════════════════════════════════════════
    // CONSTRUCTOR PRIVADO: Patrón de seguridad del Builder
    // ═══════════════════════════════════════════════════════════════
    // private: Solo el Builder interno puede llamar a este constructor
    // Esto garantiza que SOLO se puedan crear instancias usando el Builder
    // Previene: new DeudaConsolidadaDTO() ← ESTO NO COMPILARÁ
    private DeudaConsolidadaDTO() {}
    
    // ═══════════════════════════════════════════════════════════════
    // CLASE BUILDER INTERNA (Patrón Builder)
    // ═══════════════════════════════════════════════════════════════
    // static: No necesita una instancia externa de DeudaConsolidadaDTO para existir
    // class Builder: Clase interna que construye instancias paso a paso
    public static class Builder {
        
        // final: La referencia no puede cambiar, siempre apunta al mismo DTO
        // Creamos el DTO vacío que iremos poblando con los métodos builder
        private final DeudaConsolidadaDTO dto = new DeudaConsolidadaDTO();
        
        // ═══════════════════════════════════════════════════════════════
        // MÉTODOS FLUIDOS (Fluent API Pattern)
        // ═══════════════════════════════════════════════════════════════
        // Cada método asigna un campo y retorna "this" (el propio Builder)
        // Esto permite encadenar llamadas: builder.campo1(...).campo2(...).campo3(...)
        
        // Método para asignar el ID del cliente
        public Builder clienteId(String clienteId) {
            dto.clienteId = clienteId;  // Asignación directa al DTO interno
            return this;                 // Retorna el Builder para permitir encadenamiento
        }
        
        // Método para asignar el nombre del cliente
        public Builder nombreCliente(String nombreCliente) {
            dto.nombreCliente = nombreCliente;
            return this;  // "return this" es la clave del patrón fluido
        }
        
        // Método para asignar la lista de facturas
        // List<FacturaAcueductoDTO>: Tipo genérico que garantiza type-safety
        public Builder facturasAcueducto(List<FacturaAcueductoDTO> facturas) {
            dto.facturasAcueducto = facturas;
            return this;  // Permite continuar encadenando: .facturas(...).consumos(...)
        }
        
        // ... otros métodos builder siguen el mismo patrón ...
        
        // ═══════════════════════════════════════════════════════════════
        // MÉTODO build(): Finaliza la construcción y retorna el DTO
        // ═══════════════════════════════════════════════════════════════
        // Este método se llama al final de la cadena fluida
        // Realiza validaciones finales antes de crear el objeto
        public DeudaConsolidadaDTO build() {
            
            // ───────────────────────────────────────────────────────────
            // VALIDACIONES: Garantizan que el objeto esté en estado válido
            // ───────────────────────────────────────────────────────────
            // Validación de campos obligatorios
            // throw: Lanza una excepción si el cliente no fue asignado
            // IllegalStateException: Indica que el objeto está en estado inválido
            if (dto.clienteId == null) {
                throw new IllegalStateException("clienteId es requerido");
            }
            
            // Podríamos agregar más validaciones aquí:
            // - Validar formato del clienteId
            // - Verificar que las listas no sean null
            // - Validar rangos de valores (totalAPagar >= 0)
            
            // Si todas las validaciones pasan, retornamos el DTO construido
            // A partir de aquí, el DTO es inmutable (si usamos final en los campos)
            return dto;
        }
    }
}
```

#### **Uso en el Código**

```java
// ═══════════════════════════════════════════════════════════════
// ✅ CÓDIGO LEGIBLE Y MANTENIBLE con PATRÓN BUILDER
// ═══════════════════════════════════════════════════════════════

// new DeudaConsolidadaDTO.Builder(): Crea una instancia del Builder
// El Builder es una clase interna estática, por eso se accede con punto (.)
DeudaConsolidadaDTO respuesta = new DeudaConsolidadaDTO.Builder()
    
    // Cada llamada asigna un campo y retorna el mismo Builder
    // Esto permite encadenar múltiples llamadas en una sintaxis fluida
    
    // Asigna el ID del cliente (variable proveniente de parámetro)
    .clienteId(clienteId)
    
    // Asigna el nombre del cliente (String literal)
    .nombreCliente("Juan Pérez")
    
    // LocalDateTime.now(): Obtiene fecha/hora actual del sistema
    // Se registra el momento exacto de la consulta para auditoría
    .fechaConsulta(LocalDateTime.now())
    
    // facturasDTO: Variable que contiene List<FacturaAcueductoDTO>
    // Esta lista fue construida previamente mapeando entidades de dominio a DTOs
    .facturasAcueducto(facturasDTO)
    
    // consumosDTO: Lista de consumos de energía ya convertidos a DTOs
    // Proviene del adaptador que lee el archivo mainframe
    .consumosEnergia(consumosDTO)
    
    // estadisticasDTO: Objeto con métricas calculadas (promedios, tendencias)
    // Se construyó analizando las facturas y consumos del cliente
    .estadisticas(estadisticasDTO)
    
    // alertasList: Lista de Strings con mensajes de alerta generados
    // Ej: ["Consumo alto detectado", "Factura próxima a vencer"]
    .alertas(alertasList)
    
    // totalAPagar: Suma total de todas las deudas (double)
    // Calculado previamente sumando valores de facturas pendientes
    .totalAPagar(totalAPagar)
    
    // .build(): Método final que realiza validaciones y retorna el DTO construido
    // Si falta un campo obligatorio (ej: clienteId), lanzará IllegalStateException
    .build();

// Ventajas de esta sintaxis:
// 1. LEGIBILIDAD: Cada línea dice exactamente qué campo se asigna
// 2. ORDEN FLEXIBLE: Podemos cambiar el orden de las líneas sin problemas
// 3. CAMPOS OPCIONALES: Podemos omitir líneas para campos que no son obligatorios
// 4. MANTENIBLE: Agregar un nuevo campo no rompe el código existente
// 5. TYPE-SAFE: El compilador valida tipos en tiempo de compilación
```

#### **Beneficios**

**Legibilidad:** Cada linea dice exactamente que campo se esta asignando  
**Flexibilidad:** Podemos omitir campos opcionales sin sobrecarga de constructores  
**Validacion:** El metodo `build()` valida antes de crear el objeto  
**Inmutabilidad:** Una vez creado, el DTO no se puede modificar  
**Mantenibilidad:** Agregar un campo nuevo no rompe el codigo existente

---

### 3. Patron Data Transfer Object (DTO)

#### **Problema a Resolver**

**Nunca debemos exponer entidades de dominio directamente al cliente** por:

1. **Seguridad:** Las entidades pueden tener campos sensibles (contraseñas, datos internos)
2. **Acoplamiento:** Si cambiamos la estructura de BD, se rompe la API
3. **Sobrecarga de datos:** El cliente recibe campos que no necesita
4. **Violación de SRP:** Las entidades de dominio no deben saber cómo serializarse a JSON

#### **Solución Implementada**

**Entidad de Dominio:**
```java
// ═══════════════════════════════════════════════════════════════
// ENTIDAD DE DOMINIO: Modelo interno de la aplicación
// ═══════════════════════════════════════════════════════════════
// Esta clase representa el concepto de negocio "Factura de Acueducto"
// Contiene TODA la información que necesita la aplicación internamente
public class FacturaAcueducto {
    
    // Long: Tipo wrapper (puede ser null), representa ID autoincremental de BD
    // ID interno de base de datos (ej: 12345)
    // Este valor NO debe exponerse al cliente (seguridad)
    private Long id;
    
    // String: Identificación del cliente (ej: "1001234567")
    private String clienteId;
    
    // Periodo de facturación en formato YYYYMM (ej: "202510" = Octubre 2025)
    private String periodo;
    
    // int: Tipo primitivo (más eficiente que Integer)
    // Consumo de agua medido en metros cúbicos (ej: 25 m³)
    private int consumoMetrosCubicos;
    
    // double: Monto a pagar en pesos (ej: 45000.50)
    private double valorPagar;
    
    // LocalDate: Clase de Java 8+ para fechas sin hora (inmutable, thread-safe)
    // Fecha en que se emitió la factura (ej: 2025-10-01)
    // Campo INTERNO: No lo exponemos en la API (no es relevante para el cliente)
    private LocalDate fechaEmision;
    
    // Fecha límite de pago (ej: 2025-10-15)
    // SÍ es relevante para el cliente (necesita saber cuándo vence)
    private LocalDate fechaVencimiento;
    
    // EstadoFactura: Enum con valores (PENDIENTE, PAGADA, VENCIDA, ANULADA)
    // Representa el estado actual de la factura según reglas de negocio
    private EstadoFactura estado;
    
    // Long: ID de auditoría para trazabilidad interna
    // Campo INTERNO: Solo para logs y auditorías (no exponer al cliente)
    private Long auditId;
    
    // ... muchos otros campos internos ...
    // Ej: createAt, updatedAt, createdBy, version (optimistic locking), etc.
}
```

**DTO para la API:**
```java
// ═══════════════════════════════════════════════════════════════
// DTO (Data Transfer Object): Modelo para comunicación externa
// ═══════════════════════════════════════════════════════════════
// Esta clase define QUÉ datos se envían al cliente REST
// Contiene SOLO información relevante para el consumidor de la API
public class FacturaAcueductoDTO {
    
    // Long: ID de factura (solo para referencia, si el cliente necesita citarla)
    // Podríamos usar un UUID público diferente al ID de BD para mayor seguridad
    private Long id;
    
    // Periodo de facturación (ej: "202510")
    // El cliente necesita saber a qué periodo corresponde la factura
    private String periodo;
    
    // Consumo en metros cúbicos (ej: 25)
    // Información útil para que el cliente compare con periodos anteriores
    private int consumoMetrosCubicos;
    
    // Valor a pagar (ej: 45000.50)
    // DATO CRÍTICO: El cliente necesita saber cuánto debe pagar
    private double valorPagar;
    
    // LocalDate: Fecha de vencimiento (ej: 2025-10-15)
    // DATO CRÍTICO: El cliente necesita saber cuándo vence para evitar mora
    private LocalDate fechaVencimiento;
    
    // String: Estado simplificado como texto (ej: "PENDIENTE", "PAGADA")
    // Convertimos el Enum a String para simplificar el JSON
    // El cliente no necesita la complejidad de un objeto Enum
    private String estado;
    
    // Integer: Tipo wrapper (puede ser null si ya venció)
    // CAMPO CALCULADO: No existe en la entidad de dominio original
    // Se calcula en tiempo real (fechaVencimiento - LocalDate.now())
    // Ej: 5 (faltan 5 días), -10 (venció hace 10 días)
    private Integer diasHastaVencimiento;
    
    // ═══════════════════════════════════════════════════════════════
    // NOTA IMPORTANTE: Campos que NO incluimos (por seguridad/simplicidad)
    // ═══════════════════════════════════════════════════════════════
    // ❌ auditId: Dato interno de auditoría (no relevante para el cliente)
    // ❌ fechaEmision: El cliente solo necesita saber cuándo vence, no cuándo se emitió
    // ❌ createdAt, updatedAt: Metadatos internos de BD
    // ❌ version: Campo de optimistic locking (JPA)
}
```

#### **Mapeo Entidad → DTO**

```java
// @Component: Spring gestiona esta clase como un Bean singleton
// Todos los controladores compartirán la misma instancia del Mapper
@Component
public class FacturaMapper {
    
    // ═══════════════════════════════════════════════════════════════
    // MÉTODO DE CONVERSIÓN: Transforma entidad de dominio a DTO de API
    // ═══════════════════════════════════════════════════════════════
    // Recibe: FacturaAcueducto (modelo interno completo)
    // Retorna: FacturaAcueductoDTO (modelo simplificado para API)
    public FacturaAcueductoDTO toDTO(FacturaAcueducto entidad) {
        
        // Usamos el patrón Builder del DTO para construcción fluida
        return FacturaAcueductoDTO.builder()
            
            // ───────────────────────────────────────────────────────────
            // MAPEO DIRECTO: Copiamos campos tal cual están
            // ───────────────────────────────────────────────────────────
            
            // entidad.getId(): Obtiene el ID de la entidad (Long)
            // Getter: Método público que accede al campo privado
            .id(entidad.getId())
            
            // Periodo (ej: "202510")
            .periodo(entidad.getPeriodo())
            
            // Consumo en metros cúbicos (ej: 25)
            .consumoMetrosCubicos(entidad.getConsumoMetrosCubicos())
            
            // Valor a pagar (ej: 45000.50)
            .valorPagar(entidad.getValorPagar())
            
            // Fecha de vencimiento (LocalDate)
            .fechaVencimiento(entidad.getFechaVencimiento())
            
            // ───────────────────────────────────────────────────────────
            // TRANSFORMACIONES: Convertimos tipos complejos a simples
            // ───────────────────────────────────────────────────────────
            
            // entidad.getEstado(): Retorna EstadoFactura (Enum)
            // .toString(): Convierte el Enum a String (ej: PENDIENTE → "PENDIENTE")
            // Ventaja: El JSON será más simple, solo un String en vez de un objeto
            .estado(entidad.getEstado().toString())
            
            // ───────────────────────────────────────────────────────────
            // CAMPOS CALCULADOS: Agregamos información derivada
            // ───────────────────────────────────────────────────────────
            
            // calcularDiasHastaVencimiento(): Método privado que calcula días
            // Este campo NO existe en la entidad, lo calculamos en tiempo real
            // Ej: Si vence en 5 días → retorna 5
            //     Si venció hace 10 días → retorna -10
            .diasHastaVencimiento(calcularDiasHastaVencimiento(entidad))
            
            // .build(): Finaliza la construcción y retorna el DTO completo
            .build();
    }
    
    // ═══════════════════════════════════════════════════════════════
    // MÉTODO AUXILIAR: Calcula días hasta vencimiento
    // ═══════════════════════════════════════════════════════════════
    // private: Solo el mapper puede usar este método (encapsulación)
    // Integer: Retorna wrapper (puede ser null si no hay fecha de vencimiento)
    private Integer calcularDiasHastaVencimiento(FacturaAcueducto factura) {
        
        // Obtenemos la fecha de vencimiento de la factura
        LocalDate fechaVencimiento = factura.getFechaVencimiento();
        
        // Guard clause: Si no hay fecha de vencimiento, retornamos null
        if (fechaVencimiento == null) {
            return null;
        }
        
        // LocalDate.now(): Obtiene la fecha actual del sistema
        LocalDate hoy = LocalDate.now();
        
        // ChronoUnit.DAYS.between(): Calcula diferencia en días entre dos fechas
        // Resultado positivo: Aún no vence (ej: 5 días restantes)
        // Resultado negativo: Ya venció (ej: -10 días de atraso)
        // (int): Cast necesario porque between() retorna long
        return (int) ChronoUnit.DAYS.between(hoy, fechaVencimiento);
    }
}
```

#### **Beneficios**

**Seguridad:** No exponemos estructura interna de la BD  
**Desacoplamiento:** Cambios en BD no afectan la API  
**Optimizacion:** Enviamos solo datos necesarios (reduce ancho de banda)  
**Transformacion:** Podemos calcular campos adicionales (diasHastaVencimiento)  
**Versionado:** Podemos tener DTOv1, DTOv2 sin cambiar entidades

---

### 4. Patron Repository (Provisto por Spring Data JPA)

#### **Problema a Resolver**

Acceder a bases de datos con **JDBC puro** requiere:

```java
// ═══════════════════════════════════════════════════════════════
// ❌ CÓDIGO REPETITIVO Y PROPENSO A ERRORES (Sin Spring Data JPA)
// ═══════════════════════════════════════════════════════════════
// Este código muestra POR QUÉ necesitamos Spring Data JPA
// Cada consulta simple requiere 30-40 líneas de código boilerplate

public List<FacturaAcueducto> findByClienteId(String clienteId) {
    
    // ArrayList: Estructura para almacenar resultados
    List<FacturaAcueducto> facturas = new ArrayList<>();
    
    // String SQL: Query escrita manualmente (propenso a errores de sintaxis)
    // ?: Parámetro posicional (PreparedStatement previene SQL Injection)
    String sql = "SELECT * FROM facturas_acueducto WHERE cliente_id = ?";
    
    // ───────────────────────────────────────────────────────────────
    // PASO 1: Gestión manual de recursos (Connection, Statement)
    // ───────────────────────────────────────────────────────────────
    // try-with-resources: Garantiza cierre automático de recursos
    // Connection: Representa conexión activa a la base de datos
    // dataSource.getConnection(): Obtiene conexión del pool
    try (Connection conn = dataSource.getConnection();
         
         // PreparedStatement: Query precompilada con parámetros
         // Previene SQL Injection y mejora performance (query cacheada)
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        // ───────────────────────────────────────────────────────────
        // PASO 2: Asignar parámetros manualmente
        // ───────────────────────────────────────────────────────────
        // setString(índice, valor): Asigna valor al ? en posición 1
        // Índice base-1 (no base-0 como arrays): Primera ? es índice 1
        stmt.setString(1, clienteId);
        
        // ───────────────────────────────────────────────────────────
        // PASO 3: Ejecutar query y obtener ResultSet
        // ───────────────────────────────────────────────────────────
        // executeQuery(): Ejecuta SELECT, retorna ResultSet
        // ResultSet: Cursor sobre los resultados (similar a Iterator)
        ResultSet rs = stmt.executeQuery();
        
        // ───────────────────────────────────────────────────────────
        // PASO 4: Mapeo manual de ResultSet a objetos Java
        // ───────────────────────────────────────────────────────────
        // while: Itera sobre cada fila del resultado
        // rs.next(): Mueve cursor a siguiente fila, retorna false si no hay más
        while (rs.next()) {
            
            // Crear objeto Java vacío (patrón JavaBean)
            FacturaAcueducto factura = new FacturaAcueducto();
            
            // ───────────────────────────────────────────────────────
            // MAPEO CAMPO POR CAMPO (TEDIOSO Y PROPENSO A ERRORES)
            // ───────────────────────────────────────────────────────
            
            // rs.getLong("columna"): Extrae valor de columna como Long
            // "id": Nombre de columna en BD (debe coincidir exactamente)
            // Problema: Si renombramos columna, código se rompe en runtime
            factura.setId(rs.getLong("id"));
            
            // rs.getString("columna"): Extrae valor como String
            // Mapeo de snake_case (BD) a camelCase (Java)
            factura.setClienteId(rs.getString("cliente_id"));
            
            // ... mapeo manual de 10+ campos ...
            // ❌ PROBLEMA 1: Código repetitivo para cada campo
            // ❌ PROBLEMA 2: Si agregamos campo, olvidamos mapearlo → BUG
            // ❌ PROBLEMA 3: Si cambia tipo en BD, error en runtime (no en compilación)
            // ❌ PROBLEMA 4: Difícil de mantener con entidades grandes (20+ campos)
            
            // Agregar objeto mapeado a la lista
            facturas.add(factura);
        }
        
    } catch (SQLException e) {
        // ───────────────────────────────────────────────────────────
        // PASO 5: Manejo manual de excepciones SQL
        // ───────────────────────────────────────────────────────────
        // SQLException: Excepción checked (obligatorio capturar)
        // Problemas posibles:
        //   - Conexión perdida
        //   - Query mal formada
        //   - Tabla no existe
        //   - Tipo de dato incompatible
        // RuntimeException: Envolvemos la excepción checked
        throw new RuntimeException("Error al consultar facturas", e);
    }
    
    // Retornar lista completa (puede estar vacía)
    return facturas;
}

// ═══════════════════════════════════════════════════════════════
// PROBLEMAS DE ESTE ENFOQUE:
// ═══════════════════════════════════════════════════════════════
// 1. ❌ 30-40 líneas para una consulta simple
// 2. ❌ Código duplicado en cada método (conexión, mapeo, manejo errores)
// 3. ❌ Errores de SQL solo se detectan en runtime (no en compilación)
// 4. ❌ Mapeo manual tedioso (10+ campos por entidad)
// 5. ❌ Difícil de testear (requiere BD real o mocks complejos)
// 6. ❌ No hay optimizaciones (caché, lazy loading, batch fetching)
// 7. ❌ Violación del principio DRY (Don't Repeat Yourself)
```

#### **Solución Implementada con Spring Data JPA**

```java
@Repository
public interface FacturaJpaRepository extends JpaRepository<FacturaJpaEntity, Long> {
    
    // ✅ SPRING GENERA AUTOMÁTICAMENTE LA IMPLEMENTACIÓN
    List<FacturaJpaEntity> findByClienteId(String clienteId);
    
    List<FacturaJpaEntity> findByClienteIdAndPeriodo(String clienteId, String periodo);
    
    @Query("SELECT f FROM FacturaJpaEntity f WHERE f.clienteId = :clienteId AND f.estado = 'PENDIENTE'")
    List<FacturaJpaEntity> findPendientesByCliente(@Param("clienteId") String clienteId);
}
```

#### **Cómo Funciona**

1. **Spring Data JPA** analiza el nombre del método (`findByClienteId`)
2. Genera automáticamente la query SQL: `SELECT * FROM facturas_acueducto WHERE cliente_id = ?`
3. Mapea automáticamente `ResultSet` → Entidad JPA
4. Gestiona transacciones, conexiones, excepciones

#### **Beneficios**

**Eliminacion de Boilerplate:** No escribimos SQL repetitivo  
**Type Safety:** Errores de compilacion en vez de runtime  
**Abstraccion del Proveedor:** Cambiamos de PostgreSQL a MySQL sin cambiar codigo  
**Testing:** Podemos usar bases de datos en memoria (H2) para tests  
**Optimizacion:** JPA usa cache de segundo nivel, lazy loading, batch fetching  
**Mantenibilidad:** Agregar un campo nuevo es solo agregar una columna a la entidad

---

### 5. Inversion de Control / Inyeccion de Dependencias (Provisto por Spring)

#### **Problema a Resolver (Sin IoC)**

```java
// ═══════════════════════════════════════════════════════════════
// ❌ ALTO ACOPLAMIENTO Y DIFÍCIL DE TESTEAR (Sin Spring IoC/DI)
// ═══════════════════════════════════════════════════════════════
// Este código muestra POR QUÉ necesitamos Inversión de Control
// El controlador está acoplado a TODAS las implementaciones concretas

public class DeudaRestController {
    
    // ───────────────────────────────────────────────────────────────
    // PROBLEMA 1: Acoplamiento directo a implementaciones concretas
    // ───────────────────────────────────────────────────────────────
    // El controlador CREA sus propias dependencias usando "new"
    // Esto viola el principio de Inversión de Dependencias (DIP)
    
    // new: Operador que crea una nueva instancia (acoplamiento fuerte)
    // ConsultarDeudaConsolidadaUseCase: Clase concreta (no interfaz)
    // El controlador debe conocer CÓMO construir el use case
    private ConsultarDeudaConsolidadaUseCase useCase = 
        
        // Constructor del Use Case requiere dos dependencias:
        new ConsultarDeudaConsolidadaUseCase(
            
            // ───────────────────────────────────────────────────────
            // PROBLEMA 2: Dependencias transitivas en cascada
            // ───────────────────────────────────────────────────────
            // El controlador debe saber que el Use Case necesita un Adapter
            // Y que el Adapter necesita un Repository
            // Y que el Repository necesita... ¿qué? (problema recursivo)
            
            // Dependencia 1: Adapter de facturas
            // new: El controlador crea el adapter directamente
            new FacturaRepositoryAdapter(
                
                // El Adapter necesita un JpaRepository
                // ❌ PROBLEMA: FacturaJpaRepository es una INTERFAZ
                // No podemos hacer "new" de una interfaz!
                // ¿Cómo obtenemos una implementación?
                // ¿Quién configura la conexión a BD?
                // ¿Cómo inyectamos el DataSource?
                new FacturaJpaRepository()  // ← ESTO NO COMPILA!
                //  ↑
                //  |
                //  └─ FacturaJpaRepository es una interfaz de Spring Data
                //     Spring la implementa automáticamente en runtime
                //     NO podemos crear una instancia con "new"
            ),
            
            // Dependencia 2: Adapter de consumos de energía
            // new: El controlador crea el adapter directamente
            // ¿Cómo le pasamos la ruta del archivo?
            // ¿Quién gestiona la configuración?
            new ConsumoEnergiaReaderAdapter()
            //  ↑
            //  |
            //  └─ Este adapter necesita conocer la ruta del archivo mainframe
            //     ¿Dónde está esa configuración? ¿Hardcodeada?
        );
    
    // ───────────────────────────────────────────────────────────────
    // PROBLEMA 3: Testing imposible sin BD real
    // ───────────────────────────────────────────────────────────────
    // Para testear este controlador:
    //   1. Necesitamos PostgreSQL corriendo
    //   2. Necesitamos el archivo mainframe en el filesystem
    //   3. No podemos usar mocks (dependencias creadas con "new")
    //   4. Tests lentos (acceso a BD y archivos reales)
    
    public ResponseEntity<DeudaConsolidadaDTO> consultarDeuda(String clienteId) {
        // El método es simple, pero las dependencias son un desastre
        return ResponseEntity.ok(useCase.ejecutar(clienteId));
    }
}

// ═══════════════════════════════════════════════════════════════
// RESUMEN DE PROBLEMAS:
// ═══════════════════════════════════════════════════════════════
// 1. ❌ ALTO ACOPLAMIENTO: Controlador conoce implementaciones concretas
//    - Si cambiamos FacturaRepositoryAdapter por FacturaMongoAdapter,
//      hay que modificar el código del controlador
// 
// 2. ❌ VIOLACIÓN DIP: Depende de concreciones, no de abstracciones
//    - Debería depender de interfaces (FacturaRepositoryPort)
//    - No de implementaciones (FacturaRepositoryAdapter)
// 
// 3. ❌ DEPENDENCIAS TRANSITIVAS: Controlador conoce toda la cadena
//    - Controller → UseCase → Adapter → Repository → DataSource
//    - Si cambia algo en el fondo, se rompe el controlador
// 
// 4. ❌ TESTING IMPOSIBLE: No podemos inyectar mocks
//    - Las dependencias se crean con "new" (no reemplazables)
//    - Necesitamos infraestructura real para cada test
// 
// 5. ❌ CONFIGURACIÓN HARDCODEADA: ¿Dónde está la config?
//    - Ruta del archivo de energía
//    - Credenciales de BD
//    - Timeouts, pools de conexiones, etc.
// 
// 6. ❌ VIOLACIÓN DE SRP: Controlador hace demasiadas cosas
//    - Maneja HTTP (responsabilidad correcta)
//    - Crea dependencias (debería delegar a un contenedor)
//    - Conoce detalles de infraestructura (violación de arquitectura)
```

**Problemas:**
- ❌ El controlador conoce TODAS las dependencias transitivas
- ❌ Imposible testear sin BD real
- ❌ Si cambiamos una implementación, hay que modificar TODA la cadena

#### **Solución Implementada con Spring IoC/DI**

**1. Definir Interfaces (Puertos):**
```java
// ═══════════════════════════════════════════════════════════════
// PASO 1: Definir CONTRATOS (Interfaces) - Arquitectura Hexagonal
// ═══════════════════════════════════════════════════════════════
// Estas interfaces viven en la CAPA DE DOMINIO (core del negocio)
// Son "PUERTOS" que definen QUÉ necesita el dominio, no CÓMO se implementa

// Interface: Contrato que define métodos sin implementación
// public: Accesible desde cualquier capa
// FacturaRepositoryPort: Nombre descriptivo que indica propósito
//   - Port: Indica que es un puerto de arquitectura hexagonal
//   - Repository: Indica que maneja persistencia de datos
public interface FacturaRepositoryPort {
    
    // Método que el dominio NECESITA para obtener facturas
    // List<FacturaAcueducto>: Retorna lista de entidades de DOMINIO (no JPA)
    // String clienteId: Parámetro simple, no depende de frameworks
    // 
    // NOTA CLAVE: Esta interfaz NO menciona:
    //   - JPA, Hibernate, JDBC (tecnología de BD)
    //   - PostgreSQL, MySQL, MongoDB (tipo de BD)
    //   - Implementación concreta (cómo se obtienen los datos)
    List<FacturaAcueducto> obtenerFacturasPorCliente(String clienteId);
}

// Puerto para lectura de consumos de energía
// Similar al anterior, define el contrato sin implementación
public interface ConsumoEnergiaReaderPort {
    
    // Método para obtener consumos desde origen de datos (archivo, BD, API, etc.)
    // El dominio NO sabe ni le importa de dónde vienen los datos
    List<ConsumoEnergia> obtenerConsumosPorCliente(String clienteId);
}
```

**2. Implementaciones como Componentes:**
```java
// ═══════════════════════════════════════════════════════════════
// PASO 2: Implementar los PUERTOS como ADAPTADORES
// ═══════════════════════════════════════════════════════════════
// Estos adaptadores viven en la CAPA DE INFRAESTRUCTURA
// Son el "CÓMO" que implementa el "QUÉ" definido por los puertos

// @Component: Anotación de Spring que marca esta clase como Bean
// Spring escanea, crea una instancia y la registra en el Application Context
// Esta instancia será INYECTADA automáticamente donde se necesite
@Component
public class FacturaRepositoryAdapter implements FacturaRepositoryPort {
    //                                 ↑
    //                                 |
    //                      Implementa la interfaz del dominio
    //                      Spring sabe que este Bean puede satisfacer
    //                      dependencias de tipo FacturaRepositoryPort
    
    // ───────────────────────────────────────────────────────────────
    // INYECCIÓN DE DEPENDENCIA: El Adapter necesita un JpaRepository
    // ───────────────────────────────────────────────────────────────
    // @Autowired: Indica a Spring que debe inyectar esta dependencia
    // Spring busca un Bean compatible con FacturaJpaRepository
    // Como es una interfaz de Spring Data, Spring genera la implementación
    @Autowired
    private FacturaJpaRepository jpaRepository;
    
    // ───────────────────────────────────────────────────────────────
    // IMPLEMENTACIÓN del método definido en el puerto
    // ───────────────────────────────────────────────────────────────
    // @Override: Garantiza que implementamos correctamente el contrato
    @Override
    public List<FacturaAcueducto> obtenerFacturasPorCliente(String clienteId) {
        
        // PASO 1: Consultar entidades JPA desde BD
        // jpaRepository.findByClienteId(): Método de Spring Data JPA
        // Retorna: List<FacturaJpaEntity> (entidades JPA, no de dominio)
        return jpaRepository.findByClienteId(clienteId)
            
            // PASO 2: Convertir Stream de JPA Entities a Domain Models
            // .stream(): Convierte List a Stream para procesamiento funcional
            .stream()
            
            // PASO 3: Mapear cada entidad JPA a modelo de dominio
            // .map(this::toDomain): Transforma cada FacturaJpaEntity
            //   - this::toDomain: Referencia a método (Method Reference)
            //   - Equivalente a: .map(entity -> this.toDomain(entity))
            //   - toDomain(): Método privado que convierte JPA → Dominio
            .map(this::toDomain)
            
            // PASO 4: Recolectar resultados en una List inmutable
            // .toList(): Terminal operation que retorna List<FacturaAcueducto>
            .toList();
        
        // ═══════════════════════════════════════════════════════════════
        // RESPONSABILIDAD DEL ADAPTER: Traducir entre capas
        // ═══════════════════════════════════════════════════════════════
        // Entrada: String (tipo primitivo del dominio)
        // Proceso: JpaRepository consulta BD y retorna entidades JPA
        // Conversión: Mapper transforma entidades JPA a modelos de dominio
        // Salida: List<FacturaAcueducto> (objetos puros de dominio)
    }
    
    // Método privado toDomain() (no mostrado):
    // private FacturaAcueducto toDomain(FacturaJpaEntity entity) {
    //     return new FacturaAcueducto(
    //         entity.getId(),
    //         entity.getClienteId(),
    //         // ... mapeo campo por campo
    //     );
    // }
}

// ───────────────────────────────────────────────────────────────
// SEGUNDO ADAPTER: Lectura de archivo mainframe
// ───────────────────────────────────────────────────────────────
// @Component: Registra este adapter en el contenedor de Spring
@Component
public class ConsumoEnergiaReaderAdapter implements ConsumoEnergiaReaderPort {
    //                                       ↑
    //                                       |
    //                          Implementa el puerto de dominio
    //                          Spring lo inyectará donde se necesite
    
    // Implementación de lectura de archivo
    // Este adapter lee el archivo mainframe y convierte líneas a objetos
    // (Código completo mostrado en sección anterior)
    
    // @Autowired (si necesita): Path del archivo, configuración, etc.
    // @Value("${serviciudad.energia.archivo-path}"): Inyecta config desde YML
}

// ═══════════════════════════════════════════════════════════════
// VENTAJAS DE ESTE DISEÑO:
// ═══════════════════════════════════════════════════════════════
// 1. ✅ BAJO ACOPLAMIENTO: Dominio solo conoce interfaces (puertos)
// 2. ✅ ALTA COHESIÓN: Cada adapter tiene una responsabilidad clara
// 3. ✅ TESTEABLE: Podemos crear MockFacturaRepositoryAdapter para tests
// 4. ✅ FLEXIBLE: Cambiar de JPA a MongoDB solo requiere nuevo adapter
// 5. ✅ INYECCIÓN AUTOMÁTICA: Spring conecta todo sin código manual
```

**3. Use Case con Inyección de Dependencias:**
```java
// ═══════════════════════════════════════════════════════════════
// PASO 3: Use Case con INVERSIÓN DE CONTROL completa
// ═══════════════════════════════════════════════════════════════
// Esta clase vive en la CAPA DE APLICACIÓN
// Orquesta la lógica de negocio usando los puertos (interfaces)

// @Service: Anotación de Spring para componentes de lógica de negocio
// Similar a @Component, pero semánticamente indica "servicio de aplicación"
// Spring registra esta clase como Bean y gestiona su ciclo de vida
@Service
public class ConsultarDeudaConsolidadaUseCase {
    
    // ═══════════════════════════════════════════════════════════════
    // DEPENDENCIAS: El Use Case necesita dos puertos
    // ═══════════════════════════════════════════════════════════════
    
    // final: La referencia no puede cambiar (inmutabilidad)
    // Beneficios:
    //   - Thread-safe: No hay riesgo de modificación concurrente
    //   - Diseño claro: Las dependencias se definen al crear la instancia
    //   - Facilita testing: Constructor injection permite mocks fáciles
    
    // FacturaRepositoryPort: INTERFAZ de dominio (no implementación)
    // El Use Case NO conoce si es JPA, MongoDB, archivo, o API REST
    // Solo sabe que puede llamar a .obtenerFacturasPorCliente()
    private final FacturaRepositoryPort facturaRepository;
    
    // ConsumoEnergiaReaderPort: Otra INTERFAZ de dominio
    // Podría leer de archivo, BD, caché, API externa, etc.
    // El Use Case no lo sabe ni le importa (bajo acoplamiento)
    private final ConsumoEnergiaReaderPort energiaReader;
    
    // ═══════════════════════════════════════════════════════════════
    // CONSTRUCTOR: Punto de inyección de dependencias
    // ═══════════════════════════════════════════════════════════════
    
    // @Autowired: Indica a Spring que debe inyectar dependencias aquí
    // NOTA: En Spring 4.3+, @Autowired es OPCIONAL en constructores únicos
    //       Spring lo detecta automáticamente
    @Autowired
    public ConsultarDeudaConsolidadaUseCase(
        // Parámetro 1: Spring busca un Bean que implemente FacturaRepositoryPort
        //              Encuentra: FacturaRepositoryAdapter (@Component)
        //              Inyecta: La instancia singleton del adapter
        FacturaRepositoryPort facturaRepository,
        
        // Parámetro 2: Spring busca un Bean que implemente ConsumoEnergiaReaderPort
        //              Encuentra: ConsumoEnergiaReaderAdapter (@Component)
        //              Inyecta: La instancia singleton del adapter
        ConsumoEnergiaReaderPort energiaReader
    ) {
        // Asignación de dependencias a campos finales
        // this.campo: Referencia al campo de la instancia
        // = parámetro: Valor inyectado por Spring
        this.facturaRepository = facturaRepository;
        this.energiaReader = energiaReader;
        
        // ═══════════════════════════════════════════════════════════
        // MAGIA DE SPRING: ¿Cómo resuelve las dependencias?
        // ═══════════════════════════════════════════════════════════
        // 1. Spring escanea todas las clases con @Component/@Service
        // 2. Crea un grafo de dependencias:
        //    - ConsultarDeudaUseCase necesita FacturaRepositoryPort
        //    - FacturaRepositoryAdapter implementa FacturaRepositoryPort
        //    - FacturaRepositoryAdapter necesita FacturaJpaRepository
        //    - Spring Data genera FacturaJpaRepository automáticamente
        // 3. Resuelve dependencias en orden correcto (bottom-up):
        //    - Crea FacturaJpaRepository (generado por Spring Data)
        //    - Crea FacturaRepositoryAdapter (inyecta JpaRepository)
        //    - Crea ConsumoEnergiaReaderAdapter
        //    - Crea ConsultarDeudaUseCase (inyecta ambos adapters)
        // 4. Almacena beans en Application Context (contenedor IoC)
    }
    
    // ═══════════════════════════════════════════════════════════════
    // LÓGICA DE NEGOCIO: Método público del Use Case
    // ═══════════════════════════════════════════════════════════════
    public DeudaConsolidada ejecutar(String clienteId) {
        
        // PASO 1: Consultar facturas usando el puerto inyectado
        // facturaRepository: Referencia a FacturaRepositoryAdapter (inyectado)
        // .obtenerFacturasPorCliente(): Método definido en la interfaz
        // En runtime ejecuta: Adapter → JPA → PostgreSQL → Mapper → Domain
        List<FacturaAcueducto> facturas = facturaRepository.obtenerFacturasPorCliente(clienteId);
        
        // PASO 2: Consultar consumos usando el puerto inyectado
        // energiaReader: Referencia a ConsumoEnergiaReaderAdapter (inyectado)
        // .obtenerConsumosPorCliente(): Método definido en la interfaz
        // En runtime ejecuta: Adapter → BufferedReader → Archivo TXT → Parser → Domain
        List<ConsumoEnergia> consumos = energiaReader.obtenerConsumosPorCliente(clienteId);
        
        // PASO 3: Consolidar información (lógica de negocio pura)
        // DeudaConsolidada: Entidad de dominio que procesa los datos
        // Constructor aplica reglas de negocio:
        //   - Suma totales de facturas y consumos
        //   - Genera alertas (facturas vencidas, consumo anormal)
        //   - Calcula estadísticas (promedios, tendencias)
        //   - Valida consistencia de datos
        return new DeudaConsolidada(facturas, consumos);
        
        // ═══════════════════════════════════════════════════════════
        // VENTAJAS DE ESTE DISEÑO:
        // ═══════════════════════════════════════════════════════════
        // ✅ TESTEABLE: Podemos inyectar mocks en tests:
        //    new ConsultarDeudaUseCase(mockRepo, mockReader)
        //
        // ✅ FLEXIBLE: Cambiar implementaciones sin tocar el Use Case:
        //    - FacturaMongoAdapter en vez de FacturaJpaAdapter
        //    - ConsumoApiAdapter en vez de ConsumoFileAdapter
        //
        // ✅ BAJO ACOPLAMIENTO: Use Case no conoce detalles técnicos:
        //    - No sabe que usa JPA
        //    - No sabe que lee archivos
        //    - Solo conoce interfaces (contratos)
        //
        // ✅ PRINCIPIO DE INVERSIÓN DE DEPENDENCIAS (DIP):
        //    - Módulos de alto nivel (Use Case) no dependen de bajo nivel (Adapters)
        //    - Ambos dependen de abstracciones (Interfaces/Puertos)
    }
}
```

**4. Controlador con Inyección:**
```java
// ═══════════════════════════════════════════════════════════════
// PASO 4: Controlador REST con Inyección de Dependencias
// ═══════════════════════════════════════════════════════════════
// Esta clase vive en la CAPA DE PRESENTACIÓN (infraestructura/input)
// Responsabilidad ÚNICA: Manejar peticiones HTTP REST

// @RestController: Combina @Controller + @ResponseBody
// Indica que:
//   1. Esta clase maneja peticiones HTTP
//   2. Los métodos retornan datos (no vistas HTML)
//   3. Spring serializa automáticamente objetos a JSON
@RestController

// @RequestMapping: Define el path base para todos los endpoints
// "/api/deuda": Prefijo común para operaciones de deuda
// URL completas serán: /api/deuda/cliente/{id}, /api/deuda/resumen, etc.
@RequestMapping("/api/deuda")
public class DeudaRestController {
    
    // ═══════════════════════════════════════════════════════════════
    // ÚNICA DEPENDENCIA: El Use Case (capa de aplicación)
    // ═══════════════════════════════════════════════════════════════
    
    // final: Inmutabilidad, la referencia no cambiará
    // ConsultarDeudaConsolidadaUseCase: Componente de lógica de negocio
    // 
    // NOTA CLAVE: El controlador NO conoce:
    //   - Repositorios (facturaRepository, energiaReader)
    //   - Adapters (JPA, archivo)
    //   - Base de datos (PostgreSQL)
    //   - Formato de archivo (mainframe)
    // Solo conoce el Use Case (capa de aplicación)
    private final ConsultarDeudaConsolidadaUseCase consultarDeudaUseCase;
    
    // ═══════════════════════════════════════════════════════════════
    // CONSTRUCTOR: Único punto de inyección
    // ═══════════════════════════════════════════════════════════════
    
    // @Autowired: Spring busca un Bean compatible e inyecta
    // OPCIONAL: En Spring 4.3+ no es necesario si solo hay un constructor
    @Autowired
    public DeudaRestController(
        // Parámetro: Spring busca un Bean de tipo ConsultarDeudaConsolidadaUseCase
        //            Encuentra: La instancia singleton anotada con @Service
        //            Inyecta: El use case con TODAS sus dependencias ya resueltas
        ConsultarDeudaConsolidadaUseCase consultarDeudaUseCase
    ) {
        // Asignación simple al campo final
        this.consultarDeudaUseCase = consultarDeudaUseCase;
        
        // ═══════════════════════════════════════════════════════════
        // GRAFO DE DEPENDENCIAS COMPLETO (resuelto por Spring):
        // ═══════════════════════════════════════════════════════════
        // DeudaRestController
        //   ↓ depende de
        // ConsultarDeudaUseCase
        //   ↓ depende de
        // FacturaRepositoryAdapter + ConsumoEnergiaReaderAdapter
        //   ↓ dependen de
        // FacturaJpaRepository (generado por Spring Data)
        //
        // Spring resuelve TODA esta cadena automáticamente
        // El controlador solo recibe el use case listo para usar
    }
    
    // ═══════════════════════════════════════════════════════════════
    // ENDPOINT REST: Consultar deuda de un cliente
    // ═══════════════════════════════════════════════════════════════
    
    // @GetMapping: Maneja peticiones HTTP GET
    // "/cliente/{clienteId}": Path relativo (se combina con @RequestMapping)
    // URL completa: GET /api/deuda/cliente/1001234567
    // {clienteId}: Variable de path (path variable)
    @GetMapping("/cliente/{clienteId}")
    
    // ResponseEntity<T>: Envoltorio de respuesta HTTP con control total
    // Permite configurar:
    //   - Status code (200, 404, 500, etc.)
    //   - Headers (Content-Type, Cache-Control, etc.)
    //   - Body (el objeto DeudaConsolidadaDTO)
    //
    // <DeudaConsolidadaDTO>: Tipo genérico del body
    // Spring lo serializa automáticamente a JSON usando Jackson
    public ResponseEntity<DeudaConsolidadaDTO> consultarDeuda(
        
        // @PathVariable: Extrae valor de la variable de path {clienteId}
        // Ejemplo: GET /api/deuda/cliente/1001234567
        //          → clienteId = "1001234567"
        // String clienteId: Tipo del parámetro (Spring lo convierte automáticamente)
        @PathVariable String clienteId
    ) {
        // ───────────────────────────────────────────────────────────
        // PASO 1: Delegar al Use Case (Separación de Responsabilidades)
        // ───────────────────────────────────────────────────────────
        // consultarDeudaUseCase.ejecutar(): Llama a la capa de aplicación
        // El controlador NO tiene lógica de negocio, solo coordina
        // 
        // Flujo interno (el controlador NO lo sabe):
        //   1. Use Case consulta facturas (Adapter → JPA → PostgreSQL)
        //   2. Use Case consulta consumos (Adapter → File → TXT)
        //   3. Use Case consolida datos (lógica de negocio)
        //   4. Use Case retorna DeudaConsolidada (modelo de dominio)
        DeudaConsolidada resultado = consultarDeudaUseCase.ejecutar(clienteId);
        
        // ───────────────────────────────────────────────────────────
        // PASO 2: Convertir modelo de dominio a DTO de respuesta
        // ───────────────────────────────────────────────────────────
        // mapper.toDTO(): Transforma DeudaConsolidada (dominio) a DTO (API)
        // Nota: El código mostrado asume que "mapper" está inyectado
        //       En código real sería:
        //       private final DeudaMapper mapper;
        //       @Autowired public DeudaRestController(..., DeudaMapper mapper)
        
        // ───────────────────────────────────────────────────────────
        // PASO 3: Retornar respuesta HTTP 200 OK con el DTO en el body
        // ───────────────────────────────────────────────────────────
        // ResponseEntity.ok(): Factory method que crea respuesta con status 200
        // El DTO se serializa automáticamente a JSON:
        //   Content-Type: application/json
        //   Body: { "clienteId": "1001234567", "facturas": [...], ... }
        return ResponseEntity.ok(mapper.toDTO(resultado));
        
        // Alternativas para otros casos:
        // - ResponseEntity.notFound().build()           → 404 Not Found
        // - ResponseEntity.status(500).body(error)      → 500 Internal Error
        // - ResponseEntity.created(location).body(dto)  → 201 Created
        // - ResponseEntity.badRequest().body(error)     → 400 Bad Request
    }
    
    // ═══════════════════════════════════════════════════════════════
    // RESPONSABILIDADES DEL CONTROLADOR (Principio SRP):
    // ═══════════════════════════════════════════════════════════════
    // ✅ 1. Mapear URLs a métodos Java (@GetMapping, @PostMapping)
    // ✅ 2. Extraer datos de la petición (@PathVariable, @RequestBody)
    // ✅ 3. Validar entrada básica (Bean Validation con @Valid)
    // ✅ 4. Delegar lógica al Use Case
    // ✅ 5. Convertir resultado a DTO
    // ✅ 6. Construir respuesta HTTP (status, headers, body)
    //
    // ❌ NO hace:
    // ❌ 1. Lógica de negocio (suma totales, valida reglas)
    // ❌ 2. Acceso a base de datos (SQL, JPA queries)
    // ❌ 3. Lectura de archivos
    // ❌ 4. Llamadas a APIs externas
    // ❌ 5. Cálculos complejos
    //
    // VENTAJA: Controlador simple, fácil de testear con MockMvc
}
```

#### **Cómo Funciona Spring IoC**

```
┌────────────────────────────────────────────────────────────┐
│           SPRING APPLICATION CONTEXT (CONTENEDOR)          │
│                                                            │
│  ┌────────────────┐  ┌─────────────────────────────┐     │
│  │ @RestController│  │        @Service             │     │
│  │ DeudaRest      │  │  ConsultarDeudaUseCase      │     │
│  │ Controller     │──│                             │     │
│  └────────────────┘  └─────────────────────────────┘     │
│                              ▼                             │
│                      ┌────────────────┐                    │
│                      │   @Component   │                    │
│                      │FacturaRepo     │                    │
│                      │Adapter         │                    │
│                      └────────────────┘                    │
│                              ▼                             │
│                      ┌────────────────┐                    │
│                      │  @Repository   │                    │
│                      │ FacturaJpa     │                    │
│                      │ Repository     │                    │
│                      └────────────────┘                    │
└────────────────────────────────────────────────────────────┘
```

1. **Spring escanea** todas las clases anotadas con `@Component`, `@Service`, `@Repository`, `@RestController`
2. **Crea instancias** (beans) de cada una
3. **Analiza las dependencias** (parámetros de constructores con `@Autowired`)
4. **Inyecta automáticamente** las dependencias necesarias
5. **Gestiona el ciclo de vida** (creación, inicialización, destrucción)

#### **Beneficios**

**Bajo Acoplamiento:** El controlador NO conoce las implementaciones concretas  
**Alta Testabilidad:** Podemos inyectar mocks en tests unitarios  
**Alta Cohesion:** Cada clase se enfoca en SU responsabilidad  
**Configuracion Centralizada:** `@Configuration` para cambiar implementaciones  
**Singleton por Defecto:** Una sola instancia de cada bean (eficiencia de memoria)  
**Lifecycle Management:** Spring inicializa en el orden correcto de dependencias


## Decisiones de Arquitectura

### 1. **Arquitectura Hexagonal (Ports & Adapters)**

Aunque es un monolito, organizamos el código en capas hexagonales:

**Ventajas:**
- Independencia de frameworks
- Testabilidad sin infraestructura
- Facilita migracion futura a microservicios
- Logica de negocio pura en el dominio

**Estructura:**
```
src/main/java/com/serviciudad/
├── domain/                    # Núcleo de negocio (NO depende de nada)
│   ├── model/                 # Entidades de dominio
│   ├── port/                  # Interfaces (puertos)
│   └── valueobject/           # Value Objects
├── application/               # Casos de uso
│   ├── usecase/               # Lógica de aplicación
│   ├── dto/                   # DTOs de entrada/salida
│   └── mapper/                # Mappers entre capas
└── infrastructure/            # Adaptadores
    ├── adapter/
    │   ├── input/             # Controladores REST
    │   └── output/            # Implementaciones de puertos
    │       ├── persistence/   # JPA, bases de datos
    │       └── file/          # Lectura de archivos
    └── config/                # Configuración Spring
```

### 2. **Separación de Concerns con DTOs**

**Request DTOs:**
- Validan entrada del usuario
- Usan Bean Validation (`@NotNull`, `@Pattern`)

**Response DTOs:**
- Estructura optimizada para el cliente
- No exponen detalles internos

**Domain Entities:**
- Lógica de negocio pura
- Independientes de la capa de presentación

### 3. **Configuración Externalizada**

Usamos `application.yml` para:
- Conexión a base de datos
- Ruta del archivo de energía
- Configuraciones de seguridad
- Perfiles (dev, prod)

```yaml
spring:
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
  datasource:
    url: jdbc:postgresql://${POSTGRES_HOST:localhost}:${POSTGRES_PORT:5432}/${POSTGRES_DB:serviciudad_db}
    username: ${POSTGRES_USER:serviciudad_user}
    password: ${POSTGRES_PASSWORD:serviciudad_pass}

serviciudad:
  energia:
    archivo-path: ${ENERGIA_ARCHIVO_PATH:data/consumos_energia.txt}
```

**Beneficios:**
- Cambiar configuracion sin recompilar
- Variables de entorno para Docker
- Perfiles para dev/test/prod

---


## Validacion y Pruebas

### Pruebas Manuales Realizadas

| Endpoint | Metodo | Auth | Estado | Respuesta |
|----------|--------|------|--------|-----------|
| `/actuator/health` | GET | No | Validado | 200 OK - {"status":"UP"} |
| `/api/facturas/1` | GET | Si | Validado | 200 OK - JSON Factura |
| `/api/facturas/cliente/0001234567` | GET | Si | Validado | 200 OK - Array Facturas |
| `/api/deuda/cliente/0001234567` | GET | Si | Validado | 200 OK - Deuda Consolidada |
| `/api/consumos-energia/cliente/0001234567` | GET | Si | Validado | 200 OK - Array Consumos |

**Comandos de validacion ejecutados:**
```powershell
# Health check
curl http://localhost:8080/actuator/health

# Endpoints con autenticacion (usar credenciales configuradas)
curl -u <username>:<password> http://localhost:8080/api/facturas/1
curl -u <username>:<password> http://localhost:8080/api/facturas/cliente/0001234567
curl -u <username>:<password> http://localhost:8080/api/deuda/cliente/0001234567
curl -u <username>:<password> http://localhost:8080/api/consumos-energia/cliente/0001234567
```

### Verificacion de Build

**Compilacion exitosa:**
```
[INFO] Building jar: /app/target/serviciudad-deuda-consolidada-1.0.0.jar
[INFO] BUILD SUCCESS
[INFO] Total time:  22.815 s
```

**Endpoints registrados:**
```
Mapped "{[/api/deuda/cliente/{clienteId}],methods=[GET]}" onto ...
Mapped "{[/api/facturas/{id}],methods=[GET]}" onto ...
Mapped "{[/api/facturas/cliente/{clienteId}],methods=[GET]}" onto ...
Mapped "{[/api/consumos-energia/cliente/{clienteId}],methods=[GET]}" onto ...
```

### Cobertura de Tests

**Tests Disponibles:**
- Tests Unitarios: Use Cases con Mockito
- Tests de Integracion: Controladores REST con MockMvc
- Tests de Adaptadores: Repositorios y File Readers

### Performance (Mediciones en Desarrollo)

- Consulta de deuda consolidada: ~300ms (con PostgreSQL local)
- Lectura de archivo energía: ~50ms (archivo de 100 líneas)
- Serialización JSON: ~20ms (respuesta típica)
- Tiempo de inicio de aplicación: ~8 segundos

### Seguridad Implementada

- Autenticacion HTTP Basic
- Validacion de entrada con Bean Validation
- Recursos publicos configurados (/actuator/health, /swagger-ui/**, /v3/api-docs/**)
- Endpoints de API protegidos (/api/**)

---

## Scripts de Automatizacion

El proyecto incluye 4 scripts de PowerShell para facilitar la evaluacion y uso del sistema:

### 1. inicio-rapido.ps1

Script de inicio completo del sistema con un solo comando.

**Uso:**
```powershell
.\inicio-rapido.ps1
```

**Funcionalidad:**
- Verifica que Docker este instalado y corriendo
- Levanta los contenedores (app + PostgreSQL)
- Espera a que la API este lista (health check)
- Abre el frontend en el navegador automaticamente

**Tiempo estimado:** 30-60 segundos

### 2. run-all-tests.ps1

Suite completa de pruebas con reportes detallados.

**Uso:**
```powershell
.\run-all-tests.ps1 -Coverage -OpenReport
```

**Funcionalidad:**
- Ejecuta tests unitarios por categoria
- Ejecuta tests de integracion
- Genera reporte de cobertura JaCoCo
- Muestra estadisticas detalladas

**Parametros:**
- `-Coverage`: Genera reporte de cobertura
- `-OpenReport`: Abre el reporte en navegador
- `-SkipIntegration`: Omite tests de integracion

**Tiempo estimado:** 2-5 minutos

### 3. quick-test.ps1

Testing rapido durante desarrollo.

**Uso:**
```powershell
.\quick-test.ps1 -TestPattern "*MapperTest"
.\quick-test.ps1 -Watch
```

**Funcionalidad:**
- Ejecuta tests especificos por patron
- Modo watch para re-ejecutar en cambios
- Deteccion rapida de errores

**Tiempo estimado:** 10-30 segundos

### 4. rebuild-docker.ps1

Reconstruccion completa de contenedores Docker.

**Uso:**
```powershell
.\rebuild-docker.ps1
```

**Funcionalidad:**
- Detiene y elimina contenedores existentes
- Limpia imagenes antiguas del proyecto
- Construye imagen sin cache
- Inicia contenedores y verifica estado

**Tiempo estimado:** 2-3 minutos

### Beneficios de los Scripts

**Para Evaluadores:**
- Inicio del sistema con un solo comando
- Validacion rapida de funcionalidad
- Sin necesidad de conocimientos de Docker

**Para Desarrollo:**
- Testing continuo durante desarrollo
- Feedback inmediato de cambios
- Automatizacion de tareas repetitivas

### Recomendaciones de Uso

**Primera vez:**
```powershell
# Iniciar el sistema
.\inicio-rapido.ps1

# Esperar a que abra el navegador y verificar funcionamiento
```

**Durante evaluacion:**
```powershell
# Si se necesita reiniciar limpiamente
.\rebuild-docker.ps1

# Para verificar tests
.\run-all-tests.ps1 -Coverage
```

---

## Equipo de Desarrollo

| Nombre | Rol | Responsabilidades |
|--------|-----|-------------------|
| **Eduard Criollo Yule** | Project Manager & Backend | Arquitectura, Use Cases, Coordinación |
| **Felipe Charria Caicedo** | Legacy Integration Specialist | Adaptador de archivo plano, Integración mainframe |
| **Jhonathan Chicaiza Herrera** | Backend Developer | Repositorios JPA, Entidades de dominio |
| **Emmanuel Mena** | Full Stack Developer | Controllers REST, Frontend, Integración |
| **Juan Sebastian Castillo** | Frontend Developer | UI/UX, Estilos, JavaScript, Favicon |

---

**Universidad Autonoma de Occidente**  
**Ingenieria de Software 2 - Octubre 2025**  
**Proyecto: ServiCiudad Cali - Sistema de Consulta Unificada**

---

**Version:** 1.0.0 - Produccion  