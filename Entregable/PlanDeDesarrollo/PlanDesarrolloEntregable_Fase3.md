# PLAN DE DESARROLLO - ENTREGABLE CORTE 2
## FASE 3: PATRONES DE DISEÑO OBLIGATORIOS

---

**Proyecto:** ServiCiudad Cali - Sistema de Consulta Unificada  
**Fase:** 3 - Implementación de Patrones de Diseño Core  
**Fecha:** Octubre 2025

---

## 🎯 OBJETIVO DE LA FASE 3

Implementar los **5 patrones de diseño obligatorios** del enunciado con código funcional, bien estructurado y completamente documentado. Cada patrón debe resolver un problema específico y estar justificado técnicamente.

---

## 📐 PATRÓN 1: ADAPTER PATTERN

### Problema a Resolver
El archivo plano del Mainframe IBM Z (`consumos_energia.txt`) tiene un formato COBOL de ancho fijo incompatible con objetos Java modernos. Necesitamos adaptar esta interfaz legacy a nuestra arquitectura.

### Solución: Adapter Pattern

#### 1.1 Interface Port (Target)

```java
package com.serviciudad.adapter;

import com.serviciudad.entity.FacturaEnergia;

/**
 * Interface port para el servicio de energía
 * Define el contrato que deben cumplir todas las implementaciones
 * 
 * Patrón: Adapter Pattern (Target Interface)
 */
public interface ServicioEnergiaPort {
    
    /**
     * Consulta la factura de energía de un cliente
     * 
     * @param clienteId Número de identificación del cliente
     * @return Factura de energía del cliente
     * @throws ClienteNoEncontradoException si el cliente no existe
     * @throws ArchivoEnergiaException si hay error leyendo el archivo
     */
    FacturaEnergia consultarFactura(String clienteId);
}
```

#### 1.2 Entidad FacturaEnergia

```java
package com.serviciudad.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entidad que representa una factura de energía
 * Esta clase modela el dominio, no el formato del archivo legacy
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacturaEnergia {
    
    private String idCliente;
    private String periodo;
    private Integer consumoKwh;
    private BigDecimal valorPagar;
    
    /**
     * Calcula el costo por kWh
     * @return Costo unitario por kilovatio-hora
     */
    public BigDecimal calcularCostoPorKwh() {
        if (consumoKwh == null || consumoKwh == 0) {
            return BigDecimal.ZERO;
        }
        return valorPagar.divide(new BigDecimal(consumoKwh), 2, BigDecimal.ROUND_HALF_UP);
    }
}
```

#### 1.3 Adapter Implementation

```java
package com.serviciudad.adapter;

import com.serviciudad.adapter.exception.ArchivoEnergiaException;
import com.serviciudad.adapter.exception.ClienteNoEncontradoException;
import com.serviciudad.entity.FacturaEnergia;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Adaptador para el sistema legacy de energía (Mainframe IBM Z simulado)
 * 
 * Patrón: Adapter Pattern
 * Problema: Formato COBOL de ancho fijo incompatible con objetos Java
 * Solución: Adapta el archivo plano legacy a objetos del dominio
 * 
 * Formato del archivo:
 * - id_cliente (10 chars): ID con padding de ceros a la izquierda
 * - periodo (6 chars): YYYYMM
 * - consumo_kwh (8 chars): Consumo con padding de ceros
 * - valor_pagar (12 chars): Valor en centavos (dividir entre 100)
 * 
 * Ejemplo: "000123456720251000001500000180000.50"
 */
@Slf4j
@Component
public class ArchivoEnergiaAdapter implements ServicioEnergiaPort {
    
    @Value("${serviciudad.energia.archivo}")
    private String archivoPath;
    
    @Override
    public FacturaEnergia consultarFactura(String clienteId) {
        log.info("Consultando factura de energía para cliente: {}", clienteId);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoPath))) {
            return reader.lines()
                .filter(linea -> extraerClienteId(linea).equals(clienteId))
                .map(this::parsearLineaAnchofijo)
                .findFirst()
                .orElseThrow(() -> new ClienteNoEncontradoException(
                    "Cliente no encontrado en archivo de energía: " + clienteId));
                    
        } catch (IOException e) {
            log.error("Error leyendo archivo de energía: {}", archivoPath, e);
            throw new ArchivoEnergiaException(
                "Error leyendo archivo de energía", e);
        }
    }
    
    /**
     * Extrae el ID del cliente de una línea del archivo
     * Posiciones 0-9: ID cliente
     */
    private String extraerClienteId(String linea) {
        if (linea == null || linea.length() < 10) {
            return "";
        }
        return linea.substring(0, 10).trim();
    }
    
    /**
     * Parsea una línea del archivo de ancho fijo a objeto FacturaEnergia
     * 
     * Formato COBOL:
     * PIC X(10) - id_cliente    (posiciones 0-9)
     * PIC 9(6)  - periodo       (posiciones 10-15)
     * PIC 9(8)  - consumo_kwh   (posiciones 16-23)
     * PIC 9(10)V99 - valor_pagar (posiciones 24-35, incluye decimales)
     * 
     * @param linea Línea del archivo
     * @return Objeto FacturaEnergia parseado
     */
    private FacturaEnergia parsearLineaAnchofijo(String linea) {
        try {
            // Validar longitud mínima
            if (linea.length() < 36) {
                throw new IllegalArgumentException(
                    "Línea del archivo con formato inválido: " + linea);
            }
            
            // Extraer campos según posiciones fijas
            String idCliente = linea.substring(0, 10).trim();
            String periodo = linea.substring(10, 16).trim();
            String consumoStr = linea.substring(16, 24).trim();
            String valorStr = linea.substring(24, 36).trim();
            
            // Parsear consumo (entero)
            Integer consumoKwh = Integer.parseInt(consumoStr);
            
            // Parsear valor (dividir entre 100 para convertir centavos a pesos)
            BigDecimal valorPagar = new BigDecimal(valorStr)
                .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
            
            FacturaEnergia factura = FacturaEnergia.builder()
                .idCliente(idCliente)
                .periodo(periodo)
                .consumoKwh(consumoKwh)
                .valorPagar(valorPagar)
                .build();
            
            log.debug("Factura parseada exitosamente: {}", factura);
            return factura;
            
        } catch (NumberFormatException e) {
            log.error("Error parseando valores numéricos de la línea: {}", linea, e);
            throw new ArchivoEnergiaException(
                "Error en formato numérico del archivo", e);
        }
    }
}
```

#### 1.4 Excepciones Personalizadas

```java
package com.serviciudad.adapter.exception;

/**
 * Excepción lanzada cuando ocurre un error leyendo el archivo de energía
 */
public class ArchivoEnergiaException extends RuntimeException {
    public ArchivoEnergiaException(String message) {
        super(message);
    }
    
    public ArchivoEnergiaException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

```java
package com.serviciudad.adapter.exception;

/**
 * Excepción lanzada cuando un cliente no se encuentra en el sistema
 */
public class ClienteNoEncontradoException extends RuntimeException {
    public ClienteNoEncontradoException(String message) {
        super(message);
    }
}
```

---

## 🗄️ PATRÓN 2: REPOSITORY PATTERN (Spring Data JPA)

### Problema a Resolver
Necesitamos abstraer el acceso a datos de PostgreSQL sin escribir código SQL repetitivo (boilerplate). La aplicación debe poder cambiar la implementación de persistencia sin afectar la lógica de negocio.

### Solución: Repository Pattern (Provisto por Spring Data JPA)

#### 2.1 Entidad JPA - FacturaAcueducto

```java
package com.serviciudad.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa una factura de acueducto
 * 
 * Tabla: facturas_acueducto
 * Patrón: Repository Pattern (Entity)
 */
@Entity
@Table(name = "facturas_acueducto", 
       uniqueConstraints = @UniqueConstraint(
           columnNames = {"id_cliente", "periodo"},
           name = "uk_cliente_periodo"))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacturaAcueducto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "id_cliente", nullable = false, length = 20)
    private String idCliente;
    
    @Column(nullable = false, length = 6)
    private String periodo;
    
    @Column(name = "consumo_m3", nullable = false)
    private Integer consumoM3;
    
    @Column(name = "valor_pagar", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorPagar;
    
    @Column(name = "fecha_emision")
    private LocalDateTime fechaEmision;
    
    @Column(length = 20)
    @Builder.Default
    private String estado = "PENDIENTE";
    
    /**
     * Calcula el costo por metro cúbico
     * @return Costo unitario por m³
     */
    public BigDecimal calcularCostoPorM3() {
        if (consumoM3 == null || consumoM3 == 0) {
            return BigDecimal.ZERO;
        }
        return valorPagar.divide(new BigDecimal(consumoM3), 2, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * Hook de JPA ejecutado antes de persistir la entidad
     */
    @PrePersist
    protected void onCreate() {
        if (fechaEmision == null) {
            fechaEmision = LocalDateTime.now();
        }
    }
}
```

#### 2.2 Repository Interface

```java
package com.serviciudad.repository;

import com.serviciudad.entity.FacturaAcueducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para gestión de facturas de acueducto
 * 
 * Patrón: Repository Pattern (provisto por Spring Data JPA)
 * 
 * Ventajas:
 * - Abstrae la complejidad del acceso a datos
 * - Elimina código SQL boilerplate
 * - Facilita testing con mocks
 * - Proporciona operaciones CRUD sin implementación
 * - Soporta consultas derivadas del nombre del método
 * 
 * Spring Data JPA implementa automáticamente:
 * - save(), findById(), findAll(), delete(), etc.
 * - Consultas personalizadas basadas en convención de nombres
 * - Paginación y ordenamiento
 */
@Repository
public interface FacturaAcueductoRepository extends JpaRepository<FacturaAcueducto, Long> {
    
    /**
     * Busca factura por ID de cliente y periodo
     * Query derivada del nombre del método (Spring Data JPA convention)
     * 
     * @param idCliente ID del cliente
     * @param periodo Periodo en formato YYYYMM
     * @return Optional con la factura si existe
     */
    Optional<FacturaAcueducto> findByIdClienteAndPeriodo(String idCliente, String periodo);
    
    /**
     * Busca todas las facturas de un cliente
     * 
     * @param idCliente ID del cliente
     * @return Lista de facturas del cliente
     */
    List<FacturaAcueducto> findByIdCliente(String idCliente);
    
    /**
     * Busca facturas por estado
     * 
     * @param estado Estado de la factura (PENDIENTE, PAGADA, VENCIDA)
     * @return Lista de facturas con el estado especificado
     */
    List<FacturaAcueducto> findByEstado(String estado);
    
    /**
     * Busca facturas pendientes de un cliente
     * Query personalizada con @Query
     * 
     * @param idCliente ID del cliente
     * @return Lista de facturas pendientes
     */
    @Query("SELECT f FROM FacturaAcueducto f WHERE f.idCliente = :idCliente AND f.estado = 'PENDIENTE'")
    List<FacturaAcueducto> findFacturasPendientes(@Param("idCliente") String idCliente);
    
    /**
     * Verifica si existe una factura para un cliente en un periodo
     * 
     * @param idCliente ID del cliente
     * @param periodo Periodo en formato YYYYMM
     * @return true si existe, false en caso contrario
     */
    boolean existsByIdClienteAndPeriodo(String idCliente, String periodo);
}
```

---

## 🏗️ PATRÓN 3: BUILDER PATTERN

### Problema a Resolver
El DTO de respuesta `DeudaConsolidadaDTO` es complejo con múltiples campos obligatorios y opcionales. Su construcción puede volverse confusa con constructores telescópicos.

### Solución: Builder Pattern

#### 3.1 DeudaConsolidadaDTO

```java
package com.serviciudad.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para respuesta de deuda consolidada
 * 
 * Patrón: Builder Pattern (usando Lombok @Builder)
 * 
 * Problema: Construcción compleja con múltiples campos
 * Solución: Builder permite construcción fluida paso a paso
 * 
 * Ventajas del Builder Pattern:
 * - Construcción legible y fluida
 * - Inmutabilidad del objeto construido
 * - Validación centralizada
 * - Manejo de campos opcionales sin constructores telescópicos
 */
@Data
@Builder
public class DeudaConsolidadaDTO {
    
    private String clienteId;
    
    private String nombreCliente;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime fechaConsulta;
    
    private ResumenDeudaDTO resumenDeuda;
    
    @JsonProperty("totalAPagar")
    private BigDecimal totalAPagar;
    
    /**
     * Ejemplo de uso del Builder:
     * 
     * DeudaConsolidadaDTO dto = DeudaConsolidadaDTO.builder()
     *     .clienteId("0001234567")
     *     .nombreCliente("Juan Pérez")
     *     .fechaConsulta(LocalDateTime.now())
     *     .resumenDeuda(resumen)
     *     .totalAPagar(new BigDecimal("275000.50"))
     *     .build();
     */
}
```

#### 3.2 ResumenDeudaDTO

```java
package com.serviciudad.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO para resumen de deuda por servicio
 * 
 * Patrón: Builder Pattern
 */
@Data
@Builder
public class ResumenDeudaDTO {
    
    private DetalleServicioDTO energia;
    private DetalleServicioDTO acueducto;
    
    /**
     * Ejemplo de uso:
     * 
     * ResumenDeudaDTO resumen = ResumenDeudaDTO.builder()
     *     .energia(detalleEnergia)
     *     .acueducto(detalleAcueducto)
     *     .build();
     */
}
```

#### 3.3 DetalleServicioDTO

```java
package com.serviciudad.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO para detalle de un servicio específico
 * 
 * Patrón: Builder Pattern
 */
@Data
@Builder
public class DetalleServicioDTO {
    
    private String periodo;
    private String consumo;
    private BigDecimal valorPagar;
    
    /**
     * Ejemplo de uso:
     * 
     * DetalleServicioDTO energia = DetalleServicioDTO.builder()
     *     .periodo("202510")
     *     .consumo("150 kWh")
     *     .valorPagar(new BigDecimal("180000.50"))
     *     .build();
     */
}
```

---

## 📦 PATRÓN 4: DTO PATTERN

### Problema a Resolver
No debemos exponer directamente las entidades de base de datos en las APIs REST. Necesitamos separar la capa de presentación de la capa de persistencia.

### Solución: Data Transfer Object Pattern

#### 4.1 Mapper para Transformación

```java
package com.serviciudad.service.mapper;

import com.serviciudad.dto.DetalleServicioDTO;
import com.serviciudad.dto.DeudaConsolidadaDTO;
import com.serviciudad.dto.ResumenDeudaDTO;
import com.serviciudad.entity.FacturaAcueducto;
import com.serviciudad.entity.FacturaEnergia;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Mapper para transformar entidades del dominio a DTOs de respuesta
 * 
 * Patrón: DTO Pattern
 * 
 * Ventajas:
 * - Desacopla la capa de presentación de la capa de persistencia
 * - Permite controlar qué datos se exponen en las APIs
 * - Facilita versionado de APIs sin cambiar entidades
 * - Evita problemas de lazy loading de JPA en respuestas JSON
 */
@Component
public class DeudaConsolidadaDTOMapper {
    
    /**
     * Convierte entidades de factura a DTO consolidado
     * Utiliza Builder Pattern para construcción fluida
     * 
     * @param clienteId ID del cliente
     * @param nombreCliente Nombre del cliente
     * @param facturaEnergia Factura de energía
     * @param facturaAcueducto Factura de acueducto
     * @return DTO de deuda consolidada
     */
    public DeudaConsolidadaDTO toDTO(
            String clienteId,
            String nombreCliente,
            FacturaEnergia facturaEnergia,
            FacturaAcueducto facturaAcueducto) {
        
        // Mapear detalle de energía
        DetalleServicioDTO energiaDTO = mapearDetalleEnergia(facturaEnergia);
        
        // Mapear detalle de acueducto
        DetalleServicioDTO acueductoDTO = mapearDetalleAcueducto(facturaAcueducto);
        
        // Construir resumen con Builder Pattern
        ResumenDeudaDTO resumen = ResumenDeudaDTO.builder()
            .energia(energiaDTO)
            .acueducto(acueductoDTO)
            .build();
        
        // Calcular total a pagar
        BigDecimal total = calcularTotalAPagar(facturaEnergia, facturaAcueducto);
        
        // Construir DTO principal con Builder Pattern
        return DeudaConsolidadaDTO.builder()
            .clienteId(clienteId)
            .nombreCliente(nombreCliente)
            .fechaConsulta(LocalDateTime.now())
            .resumenDeuda(resumen)
            .totalAPagar(total)
            .build();
    }
    
    /**
     * Mapea factura de energía a DTO de detalle
     */
    private DetalleServicioDTO mapearDetalleEnergia(FacturaEnergia factura) {
        return DetalleServicioDTO.builder()
            .periodo(factura.getPeriodo())
            .consumo(factura.getConsumoKwh() + " kWh")
            .valorPagar(factura.getValorPagar())
            .build();
    }
    
    /**
     * Mapea factura de acueducto a DTO de detalle
     */
    private DetalleServicioDTO mapearDetalleAcueducto(FacturaAcueducto factura) {
        return DetalleServicioDTO.builder()
            .periodo(factura.getPeriodo())
            .consumo(factura.getConsumoM3() + " m³")
            .valorPagar(factura.getValorPagar())
            .build();
    }
    
    /**
     * Calcula el total a pagar sumando ambos servicios
     */
    private BigDecimal calcularTotalAPagar(
            FacturaEnergia energia, 
            FacturaAcueducto acueducto) {
        return energia.getValorPagar().add(acueducto.getValorPagar());
    }
}
```

---

## 🔌 PATRÓN 5: INVERSIÓN DE CONTROL / INYECCIÓN DE DEPENDENCIAS

### Problema a Resolver
Los componentes de la aplicación no deben crear sus dependencias manualmente (acoplamiento alto). Necesitamos bajo acoplamiento y alta cohesión.

### Solución: IoC/DI (Provisto por Spring Framework)

#### 5.1 Service Layer con DI

```java
package com.serviciudad.service;

import com.serviciudad.adapter.ServicioEnergiaPort;
import com.serviciudad.dto.DeudaConsolidadaDTO;
import com.serviciudad.entity.FacturaAcueducto;
import com.serviciudad.entity.FacturaEnergia;
import com.serviciudad.repository.FacturaAcueductoRepository;
import com.serviciudad.service.mapper.DeudaConsolidadaDTOMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Servicio de negocio para consultas de deuda consolidada
 * 
 * Patrón: Inversión de Control / Inyección de Dependencias (Spring IoC)
 * 
 * Ventajas del patrón IoC/DI:
 * - Bajo acoplamiento: Dependemos de abstracciones (interfaces), no implementaciones
 * - Alta cohesión: Cada componente tiene responsabilidad única
 * - Facilita testing: Podemos inyectar mocks fácilmente
 * - Configuración centralizada: Spring gestiona el ciclo de vida
 * - Principio de Inversión de Dependencias (DIP de SOLID)
 * 
 * @RequiredArgsConstructor de Lombok genera constructor con campos final
 * Spring automáticamente inyecta las dependencias por constructor
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeudaConsolidadaService {
    
    // Dependencias inyectadas por Spring (constructor injection)
    private final ServicioEnergiaPort servicioEnergia;           // Adapter
    private final FacturaAcueductoRepository repositorioAcueducto; // Repository
    private final DeudaConsolidadaDTOMapper mapper;              // Mapper
    
    /**
     * Consulta la deuda consolidada de un cliente
     * 
     * Orquesta la consulta de múltiples servicios:
     * 1. Consulta factura de energía (sistema legacy vía Adapter)
     * 2. Consulta factura de acueducto (PostgreSQL vía Repository)
     * 3. Mapea entidades a DTOs (Mapper con Builder Pattern)
     * 
     * @param clienteId ID del cliente
     * @return DTO con deuda consolidada
     */
    @Transactional(readOnly = true)
    public DeudaConsolidadaDTO consultarDeudaConsolidada(String clienteId) {
        log.info("Iniciando consulta de deuda consolidada para cliente: {}", clienteId);
        
        // 1. Consultar energía vía Adapter (sistema legacy)
        FacturaEnergia facturaEnergia = servicioEnergia.consultarFactura(clienteId);
        log.debug("Factura de energía consultada: {}", facturaEnergia);
        
        // 2. Consultar acueducto vía Repository (PostgreSQL)
        String periodoActual = obtenerPeriodoActual();
        FacturaAcueducto facturaAcueducto = repositorioAcueducto
            .findByIdClienteAndPeriodo(clienteId, periodoActual)
            .orElseThrow(() -> new FacturaNoEncontradaException(
                "Factura de acueducto no encontrada para cliente: " + clienteId 
                + " en periodo: " + periodoActual));
        log.debug("Factura de acueducto consultada: {}", facturaAcueducto);
        
        // 3. Mapear a DTO usando Builder Pattern
        String nombreCliente = obtenerNombreCliente(clienteId);
        DeudaConsolidadaDTO resultado = mapper.toDTO(
            clienteId, 
            nombreCliente, 
            facturaEnergia, 
            facturaAcueducto);
        
        log.info("Consulta de deuda consolidada exitosa para cliente: {}", clienteId);
        return resultado;
    }
    
    /**
     * Obtiene el periodo actual en formato YYYYMM
     */
    private String obtenerPeriodoActual() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
    }
    
    /**
     * Obtiene el nombre del cliente (mock por ahora)
     * En producción, consultaría MS-Clientes
     */
    private String obtenerNombreCliente(String clienteId) {
        // Mock: En el proyecto completo, esto vendría de MS-Clientes
        return "Juan Pérez";
    }
}
```

#### 5.2 Exception Personalizada

```java
package com.serviciudad.service.exception;

/**
 * Excepción lanzada cuando no se encuentra una factura
 */
public class FacturaNoEncontradaException extends RuntimeException {
    public FacturaNoEncontradaException(String message) {
        super(message);
    }
}
```

#### 5.3 REST Controller con DI

```java
package com.serviciudad.controller;

import com.serviciudad.dto.DeudaConsolidadaDTO;
import com.serviciudad.service.DeudaConsolidadaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para consultas de deuda consolidada
 * 
 * Patrón: Inversión de Control / Inyección de Dependencias
 * 
 * @RestController: Combina @Controller + @ResponseBody
 * @RequiredArgsConstructor: Genera constructor para inyección de dependencias
 * 
 * Spring IoC Container gestiona:
 * - Creación de instancias (@Component, @Service, @Repository)
 * - Inyección de dependencias (constructor, field, setter injection)
 * - Ciclo de vida de beans (singleton, prototype, request, session)
 * - Configuración centralizada
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
@Tag(name = "Deuda Consolidada", description = "APIs para consulta de saldos unificados")
public class DeudaConsolidadaController {
    
    // Inyección de dependencias por constructor (best practice)
    private final DeudaConsolidadaService deudaService;
    
    /**
     * Endpoint para consultar deuda consolidada de un cliente
     * 
     * GET /api/v1/clientes/{clienteId}/deuda-consolidada
     * 
     * @param clienteId ID del cliente (path parameter)
     * @return ResponseEntity con DeudaConsolidadaDTO
     */
    @Operation(
        summary = "Consultar deuda consolidada",
        description = "Obtiene el resumen consolidado de deuda de energía y acueducto para un cliente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Consulta exitosa"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{clienteId}/deuda-consolidada")
    public ResponseEntity<DeudaConsolidadaDTO> consultarDeudaConsolidada(
            @Parameter(description = "Número de identificación del cliente", required = true)
            @PathVariable String clienteId) {
        
        log.info("Request recibido: GET /api/v1/clientes/{}/deuda-consolidada", clienteId);
        
        DeudaConsolidadaDTO deuda = deudaService.consultarDeudaConsolidada(clienteId);
        
        log.info("Response exitoso para cliente: {}", clienteId);
        return ResponseEntity.ok(deuda);
    }
}
```

---

## ✅ TAREAS DE LA FASE 3

### Task 3.1: Implementar Patrón Adapter
- ✅ Crear interface `ServicioEnergiaPort`
- ✅ Implementar `ArchivoEnergiaAdapter`
- ✅ Crear entidad `FacturaEnergia`
- ✅ Crear excepciones personalizadas
- ✅ **Test**: Verificar parseo de archivo COBOL-style

### Task 3.2: Implementar Patrón Repository
- ✅ Crear entidad JPA `FacturaAcueducto`
- ✅ Crear interface `FacturaAcueductoRepository`
- ✅ Verificar queries derivadas
- ✅ **Test**: Verificar operaciones CRUD

### Task 3.3: Implementar Patrón Builder
- ✅ Crear `DeudaConsolidadaDTO` con @Builder
- ✅ Crear `ResumenDeudaDTO` con @Builder
- ✅ Crear `DetalleServicioDTO` con @Builder
- ✅ **Test**: Verificar construcción fluida

### Task 3.4: Implementar Patrón DTO
- ✅ Crear `DeudaConsolidadaDTOMapper`
- ✅ Implementar métodos de mapeo
- ✅ Separar entidades de DTOs
- ✅ **Test**: Verificar transformaciones

### Task 3.5: Implementar Patrón IoC/DI
- ✅ Crear `DeudaConsolidadaService` con DI
- ✅ Crear `DeudaConsolidadaController` con DI
- ✅ Documentar con @Service, @RestController
- ✅ Inyección por constructor con @RequiredArgsConstructor

### Task 3.6: Verificar Integración
```bash
# Ejecutar aplicación
mvn spring-boot:run

# Probar endpoint
curl http://localhost:8080/api/v1/clientes/0001234567/deuda-consolidada

# Verificar respuesta JSON
{
  "clienteId": "0001234567",
  "nombreCliente": "Juan Pérez",
  "fechaConsulta": "2025-10-10T15:30:00Z",
  "resumenDeuda": {
    "energia": {
      "periodo": "202510",
      "consumo": "1500 kWh",
      "valorPagar": 180000.50
    },
    "acueducto": {
      "periodo": "202510",
      "consumo": "15 m³",
      "valorPagar": 95000.00
    }
  },
  "totalAPagar": 275000.50
}
```

---

## 📊 CRITERIOS DE ÉXITO FASE 3

- ✅ **Adapter Pattern** implementado y funcional
- ✅ **Repository Pattern** con Spring Data JPA operativo
- ✅ **Builder Pattern** en todos los DTOs
- ✅ **DTO Pattern** separando entidades de respuestas
- ✅ **IoC/DI Pattern** con inyección por constructor
- ✅ Endpoint REST `/api/v1/clientes/{id}/deuda-consolidada` funcional
- ✅ Respuesta JSON cumple con especificación del enunciado
- ✅ Código limpio, documentado y siguiendo convenciones
- ✅ Logs informativos en todos los componentes

---

## 🔜 PRÓXIMA FASE

**FASE 4: EXPANSIÓN CON PATRONES ADICIONALES**
- Circuit Breaker Pattern (Resilience4j)
- Strategy Pattern (múltiples fuentes de datos)
- Observer Pattern (notificaciones)
- Command Pattern (auditoría)

---

*Documento generado: Octubre 10, 2025*  
*Universidad Autónoma de Occidente - Ingeniería de Software 2*
