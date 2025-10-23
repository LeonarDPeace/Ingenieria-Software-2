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
9. [Cambios Implementados](#cambios-implementados)

---

## Arquitectura General

### Visión General del Monolito

ServiCiudad Cali es una **aplicación monolítica** construida con **Spring Boot 3.x** y **Java 17**, diseñada para centralizar la consulta de deuda de servicios públicos (Energía y Acueducto) a través de una **API RESTful** unificada.

### Estructura de Capas (Arquitectura Hexagonal Validada)

```
┌─────────────────────────────────────────────────────────────┐
│                   CAPA DE PRESENTACIÓN                      │
│  (REST Controllers + DTOs + Frontend HTML/CSS/JS)          │
│  - DeudaRestController.java (@RestController) ✅           │
│  - FacturaRestController.java (@RestController) ✅         │
│  - ConsumoEnergiaRestController.java (@RestController) ✅  │
│  - frontend/index.html + styles.css + app.js ✅            │
│  - favicon.svg (recurso público) ✅                         │
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
**Ubicacion:** `infrastructure/adapter/output/persistence/ConsumoEnergiaReaderAdapter.java`

### 2. **Patron Builder** 
**Ubicacion:** `application/dto/response/DeudaConsolidadaDTO.java`

### 3. **Patron Data Transfer Object (DTO)** 
**Ubicacion:** `application/dto/` (response & request packages)

### 4. **Patron Repository** (Provisto por Spring)
**Ubicacion:** `infrastructure/adapter/output/persistence/jpa/FacturaJpaRepository.java`

### 5. **Inversion de Control / Inyeccion de Dependencias** (Provisto por Spring)
**Ubicacion:** Toda la aplicacion usa `@Autowired`, `@Service`, `@Component`, `@RestController`

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
@Component
public class ConsumoEnergiaReaderAdapter implements ConsumoEnergiaReaderPort {
    
    @Override
    public List<ConsumoEnergia> obtenerConsumosPorCliente(String clienteId) {
        List<ConsumoEnergia> consumos = new ArrayList<>();
        
        try (BufferedReader reader = Files.newBufferedReader(archivoPath)) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                // ADAPTACIÓN: Parseo de ancho fijo a objeto Java
                String clienteIdArchivo = linea.substring(0, 10).trim();
                
                if (clienteIdArchivo.equals(clienteId)) {
                    String periodo = linea.substring(10, 16);
                    int consumoKwh = Integer.parseInt(linea.substring(16, 24).trim());
                    double valorPagar = Double.parseDouble(linea.substring(24, 36).trim());
                    
                    // Creación del objeto de dominio
                    ConsumoEnergia consumo = new ConsumoEnergia(
                        clienteId, periodo, consumoKwh, valorPagar
                    );
                    consumos.add(consumo);
                }
            }
        }
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
public class DeudaConsolidadaDTO {
    private String clienteId;
    private String nombreCliente;
    private LocalDateTime fechaConsulta;
    private List<FacturaAcueductoDTO> facturasAcueducto;
    private List<ConsumoEnergiaDTO> consumosEnergia;
    private EstadisticasDTO estadisticas;
    private List<String> alertas;
    private double totalAPagar;
    
    // Constructor privado: Solo el Builder puede crear instancias
    private DeudaConsolidadaDTO() {}
    
    // Clase Builder interna
    public static class Builder {
        private final DeudaConsolidadaDTO dto = new DeudaConsolidadaDTO();
        
        public Builder clienteId(String clienteId) {
            dto.clienteId = clienteId;
            return this;
        }
        
        public Builder nombreCliente(String nombreCliente) {
            dto.nombreCliente = nombreCliente;
            return this;
        }
        
        public Builder facturasAcueducto(List<FacturaAcueductoDTO> facturas) {
            dto.facturasAcueducto = facturas;
            return this;
        }
        
        // ... otros métodos builder ...
        
        public DeudaConsolidadaDTO build() {
            // Validaciones antes de construir
            if (dto.clienteId == null) {
                throw new IllegalStateException("clienteId es requerido");
            }
            return dto;
        }
    }
}
```

#### **Uso en el Código**

```java
// ✅ CÓDIGO LEGIBLE Y MANTENIBLE
DeudaConsolidadaDTO respuesta = new DeudaConsolidadaDTO.Builder()
    .clienteId(clienteId)
    .nombreCliente("Juan Pérez")
    .fechaConsulta(LocalDateTime.now())
    .facturasAcueducto(facturasDTO)
    .consumosEnergia(consumosDTO)
    .estadisticas(estadisticasDTO)
    .alertas(alertasList)
    .totalAPagar(totalAPagar)
    .build();
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
public class FacturaAcueducto {
    private Long id;                    // ID interno de BD
    private String clienteId;
    private String periodo;
    private int consumoMetrosCubicos;
    private double valorPagar;
    private LocalDate fechaEmision;     // Campo interno
    private LocalDate fechaVencimiento;
    private EstadoFactura estado;
    private Long auditId;               // Campo interno
    // ... muchos otros campos internos ...
}
```

**DTO para la API:**
```java
public class FacturaAcueductoDTO {
    private Long id;                    // Solo para referencia
    private String periodo;
    private int consumoMetrosCubicos;
    private double valorPagar;
    private LocalDate fechaVencimiento;
    private String estado;              // Simplificado a String
    private Integer diasHastaVencimiento; // Campo calculado
    
    // Sin campos internos como auditId, fechaEmision, etc.
}
```

#### **Mapeo Entidad → DTO**

```java
@Component
public class FacturaMapper {
    
    public FacturaAcueductoDTO toDTO(FacturaAcueducto entidad) {
        return FacturaAcueductoDTO.builder()
            .id(entidad.getId())
            .periodo(entidad.getPeriodo())
            .consumoMetrosCubicos(entidad.getConsumoMetrosCubicos())
            .valorPagar(entidad.getValorPagar())
            .fechaVencimiento(entidad.getFechaVencimiento())
            .estado(entidad.getEstado().toString())
            .diasHastaVencimiento(calcularDiasHastaVencimiento(entidad))
            .build();
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
// ❌ CÓDIGO REPETITIVO Y PROPENSO A ERRORES
public List<FacturaAcueducto> findByClienteId(String clienteId) {
    List<FacturaAcueducto> facturas = new ArrayList<>();
    String sql = "SELECT * FROM facturas_acueducto WHERE cliente_id = ?";
    
    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, clienteId);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            FacturaAcueducto factura = new FacturaAcueducto();
            factura.setId(rs.getLong("id"));
            factura.setClienteId(rs.getString("cliente_id"));
            // ... mapeo manual de 10+ campos ...
            facturas.add(factura);
        }
    } catch (SQLException e) {
        throw new RuntimeException("Error al consultar facturas", e);
    }
    return facturas;
}
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
// ❌ ALTO ACOPLAMIENTO Y DIFICIL DE TESTEAR
public class DeudaRestController {
    
    // Acoplamiento directo a implementaciones concretas
    private ConsultarDeudaConsolidadaUseCase useCase = 
        new ConsultarDeudaConsolidadaUseCase(
            new FacturaRepositoryAdapter(
                new FacturaJpaRepository()  // ¿Cómo lo creo?
            ),
            new ConsumoEnergiaReaderAdapter()
        );
    
    public ResponseEntity<DeudaConsolidadaDTO> consultarDeuda(String clienteId) {
        return ResponseEntity.ok(useCase.ejecutar(clienteId));
    }
}
```

**Problemas:**
- ❌ El controlador conoce TODAS las dependencias transitivas
- ❌ Imposible testear sin BD real
- ❌ Si cambiamos una implementación, hay que modificar TODA la cadena

#### **Solución Implementada con Spring IoC/DI**

**1. Definir Interfaces (Puertos):**
```java
public interface FacturaRepositoryPort {
    List<FacturaAcueducto> obtenerFacturasPorCliente(String clienteId);
}

public interface ConsumoEnergiaReaderPort {
    List<ConsumoEnergia> obtenerConsumosPorCliente(String clienteId);
}
```

**2. Implementaciones como Componentes:**
```java
@Component
public class FacturaRepositoryAdapter implements FacturaRepositoryPort {
    @Autowired
    private FacturaJpaRepository jpaRepository;
    
    @Override
    public List<FacturaAcueducto> obtenerFacturasPorCliente(String clienteId) {
        return jpaRepository.findByClienteId(clienteId)
            .stream()
            .map(this::toDomain)
            .toList();
    }
}

@Component
public class ConsumoEnergiaReaderAdapter implements ConsumoEnergiaReaderPort {
    // Implementación de lectura de archivo
}
```

**3. Use Case con Inyección de Dependencias:**
```java
@Service
public class ConsultarDeudaConsolidadaUseCase {
    
    private final FacturaRepositoryPort facturaRepository;
    private final ConsumoEnergiaReaderPort energiaReader;
    
    // ✅ Spring inyecta las dependencias automáticamente
    @Autowired
    public ConsultarDeudaConsolidadaUseCase(
        FacturaRepositoryPort facturaRepository,
        ConsumoEnergiaReaderPort energiaReader
    ) {
        this.facturaRepository = facturaRepository;
        this.energiaReader = energiaReader;
    }
    
    public DeudaConsolidada ejecutar(String clienteId) {
        List<FacturaAcueducto> facturas = facturaRepository.obtenerFacturasPorCliente(clienteId);
        List<ConsumoEnergia> consumos = energiaReader.obtenerConsumosPorCliente(clienteId);
        
        // Lógica de negocio...
        return new DeudaConsolidada(facturas, consumos);
    }
}
```

**4. Controlador con Inyección:**
```java
@RestController
@RequestMapping("/api/deuda")
public class DeudaRestController {
    
    private final ConsultarDeudaConsolidadaUseCase consultarDeudaUseCase;
    
    // ✅ Spring inyecta automáticamente el use case
    @Autowired
    public DeudaRestController(ConsultarDeudaConsolidadaUseCase consultarDeudaUseCase) {
        this.consultarDeudaUseCase = consultarDeudaUseCase;
    }
    
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<DeudaConsolidadaDTO> consultarDeuda(
        @PathVariable String clienteId
    ) {
        DeudaConsolidada resultado = consultarDeudaUseCase.ejecutar(clienteId);
        return ResponseEntity.ok(mapper.toDTO(resultado));
    }
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

#### **Ejemplo de Test con DI**

```java
@ExtendWith(MockitoExtension.class)
class ConsultarDeudaConsolidadaUseCaseTest {
    
    @Mock
    private FacturaRepositoryPort facturaRepository;
    
    @Mock
    private ConsumoEnergiaReaderPort energiaReader;
    
    @InjectMocks
    private ConsultarDeudaConsolidadaUseCase useCase;
    
    @Test
    void deberiaConsultarDeudaCorrectamente() {
        // Given
        when(facturaRepository.obtenerFacturasPorCliente("0001234567"))
            .thenReturn(List.of(facturaMock));
        when(energiaReader.obtenerConsumosPorCliente("0001234567"))
            .thenReturn(List.of(consumoMock));
        
        // When
        DeudaConsolidada resultado = useCase.ejecutar("0001234567");
        
        // Then
        assertNotNull(resultado);
        verify(facturaRepository).obtenerFacturasPorCliente("0001234567");
        verify(energiaReader).obtenerConsumosPorCliente("0001234567");
    }
}
```

---

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

## Cumplimiento de Requisitos

### Requisitos Funcionales

| Requisito | Estado | Implementacion | Validado |
|-----------|--------|----------------|----------|
| Lectura de archivo plano de energia | Completo | `ConsumoEnergiaReaderAdapter.java` | 200 OK |
| Consulta a BD PostgreSQL (Acueducto) | Completo | `FacturaRepositoryAdapter.java` | 200 OK |
| Endpoint `/api/deuda/cliente/{clienteId}` | Completo | `DeudaRestController.java` | 200 OK |
| Endpoint `/api/facturas/cliente/{clienteId}` | Completo | `FacturaRestController.java` | 200 OK |
| Endpoint `/api/consumos-energia/cliente/{id}` | Completo | `ConsumoEnergiaRestController.java` | 200 OK |
| Respuesta JSON consolidada | Completo | `DeudaConsolidadaDTO.java` | Validado |
| Calculo de total a pagar | Completo | `ConsultarDeudaConsolidadaUseCase.java` | Validado |
| Frontend funcional | Completo | `index.html` + `styles.css` + `app.js` | 200 OK |
| Favicon | Completo | `favicon.svg` | 200 OK |

### Requisitos Técnicos

| Patron | Obligatorio | Estado | Ubicacion | Validado |
|--------|-------------|--------|-----------|----------|
| Adapter | Si | Implementado | `ConsumoEnergiaReaderAdapter` | Funcionando |
| Builder | Si | Implementado | `DeudaConsolidadaDTO.Builder` | Funcionando |
| DTO | Si | Implementado | `application/dto/` | Funcionando |
| Repository (Spring) | Si | Implementado | `FacturaJpaRepository` | Funcionando |
| IoC/DI (Spring) | Si | Implementado | Toda la aplicacion | Funcionando |

### Entregables

| Entregable | Estado | Ubicacion | Validado |
|------------|--------|-----------|----------|
| Codigo fuente en GitHub | Completo | [Repositorio](https://github.com/LeonarDPeace/Ingenieria-Software-2) | Actualizado |
| README.md con instrucciones | Completo | `README.md` | Actualizado |
| INFORME.md con justificacion | Completo | Este documento | Actualizado |
| Coleccion Postman | Completo | `postman/ServiCiudad_API.postman_collection.json` | Actualizado |
| Guias de Postman | Completo | `postman/GUIA_*.md` (4 guias) | Creadas |
| Frontend funcional | Completo | `frontend/index.html` + `styles.css` + `app.js` | 200 OK |
| Favicon | Completo | `frontend/favicon.svg` | 200 OK |
| Docker Compose | Completo | `docker-compose.yml` | Operativo |
| Tests automatizados | Completo | `src/test/java/` | Disponibles |

---

## Validacion y Pruebas

### Pruebas Manuales Realizadas

| Endpoint | Metodo | Auth | Estado | Respuesta |
|----------|--------|------|--------|-----------|
| `/` | GET | No | Validado | 200 OK - HTML Frontend |
| `/favicon.svg` | GET | No | Validado | 200 OK - SVG Icon |
| `/actuator/health` | GET | No | Validado | 200 OK - {"status":"UP"} |
| `/api/facturas/1` | GET | Si | Validado | 200 OK - JSON Factura |
| `/api/facturas/cliente/0001234567` | GET | Si | Validado | 200 OK - Array Facturas |
| `/api/deuda/cliente/0001234567` | GET | Si | Validado | 200 OK - Deuda Consolidada |
| `/api/consumos-energia/cliente/0001234567` | GET | Si | Validado | 200 OK - Array Consumos |

**Comandos de validacion ejecutados:**
```powershell
# Frontend
curl http://localhost:8080/

# Favicon
curl http://localhost:8080/favicon.svg

# Health check
curl http://localhost:8080/actuator/health

# Endpoints con autenticacion
curl -u serviciudad:dev2025 http://localhost:8080/api/facturas/1
curl -u serviciudad:dev2025 http://localhost:8080/api/facturas/cliente/0001234567
curl -u serviciudad:dev2025 http://localhost:8080/api/deuda/cliente/0001234567
curl -u serviciudad:dev2025 http://localhost:8080/api/consumos-energia/cliente/0001234567
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

- Autenticacion HTTP Basic (serviciudad:dev2025)
- Validacion de entrada con Bean Validation
- Recursos publicos configurados (/, /favicon.svg, /actuator/health)
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

**Fecha de Finalizacion:** Enero 2025  
**Version:** 1.0.0 - Produccion  