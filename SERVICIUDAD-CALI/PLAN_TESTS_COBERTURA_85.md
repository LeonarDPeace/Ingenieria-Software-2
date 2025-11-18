# PLAN DE TESTS UNITARIOS - COBERTURA 85%

**Proyecto:** ServiCiudad Cali - Sistema de Consulta Unificada  
**Objetivo:** Alcanzar 85% de cobertura de código con tests unitarios  
**Arquitectura:** Hexagonal (Ports & Adapters)  
**Framework:** JUnit 5 + Mockito + AssertJ  
**Fecha:** Noviembre 2025

---

## 📋 ÍNDICE

1. [Análisis de Cobertura Actual](#1-análisis-de-cobertura-actual)
2. [Estrategia de Testing](#2-estrategia-de-testing)
3. [Tests Faltantes por Capa](#3-tests-faltantes-por-capa)
4. [Plan de Implementación](#4-plan-de-implementación)
5. [Casos de Prueba Detallados](#5-casos-de-prueba-detallados)
6. [Ejecución y Validación](#6-ejecución-y-validación)

---

## 1. ANÁLISIS DE COBERTURA ACTUAL

### 1.1 Tests Existentes (28 archivos)

#### ✅ Capa de Aplicación (Use Cases)
- `ConsultarDeudaUseCaseImplTest.java` - **COMPLETO** ✓
- `GestionarFacturaUseCaseImplTest.java` - **COMPLETO** ✓
- `ConsultarConsumoEnergiaUseCaseImplTest.java` - **COMPLETO** ✓
- `DeudaMapperTest.java` - **COMPLETO** ✓

#### ✅ Capa de Infraestructura (Adaptadores)
- `DeudaRestControllerTest.java` - **COMPLETO** ✓
- `FacturaRestControllerTest.java` - **COMPLETO** ✓
- `ConsumoEnergiaRestControllerTest.java` - **COMPLETO** ✓
- `FacturaRepositoryAdapterTest.java` - **COMPLETO** ✓
- `ConsumoEnergiaReaderAdapterTest.java` - **COMPLETO** ✓
- `FacturaJpaMapperTest.java` - **COMPLETO** ✓
- `ConsumoEnergiaJpaMapperTest.java` - **COMPLETO** ✓

#### ✅ Tests de Integración
- `DeudaConsolidadaIntegrationTest.java` - **COMPLETO** ✓
- `FacturaAcueductoIntegrationTest.java` - **COMPLETO** ✓
- `AbstractIntegrationTest.java` - **BASE** ✓

**Cobertura Estimada Actual:** ~60-65%

---

### 1.2 Áreas Sin Cobertura

#### ❌ Capa de Dominio (Value Objects) - **CRÍTICO**
- `ClienteId.java` - **SIN TESTS** ⚠️
- `Periodo.java` - **SIN TESTS** ⚠️
- `Dinero.java` - **SIN TESTS** ⚠️
- `ConsumoAgua.java` - **SIN TESTS** ⚠️
- `ConsumoEnergia.java` - **SIN TESTS** ⚠️
- `FacturaId.java` - **SIN TESTS** ⚠️

#### ❌ Modelos de Dominio
- `DeudaConsolidada.java` - **PARCIALMENTE TESTEADO** (solo indirectamente)
- `FacturaAcueducto.java` - **PARCIALMENTE TESTEADO** (solo indirectamente)
- `ConsumoEnergiaModel.java` - **PARCIALMENTE TESTEADO** (solo indirectamente)
- `EstadisticasDeuda.java` - **SIN TESTS** ⚠️

#### ❌ Capa de Infraestructura (Configuración)
- `SecurityConfig.java` - **SIN TESTS** ⚠️
- `WebConfig.java` - **SIN TESTS** ⚠️
- `RateLimitInterceptor.java` - **SIN TESTS** ⚠️
- `OpenApiConfig.java` - **SIN TESTS** ⚠️
- `DatabaseConfig.java` - **SIN TESTS** ⚠️
- `CorsConfig.java` - **SIN TESTS** ⚠️

#### ❌ Excepciones y Manejo de Errores
- `GlobalExceptionHandler.java` - **SIN TESTS** ⚠️
- `FacturaNoEncontradaException.java` - **SIN TESTS** ⚠️
- `FacturaDuplicadaException.java` - **SIN TESTS** ⚠️
- `ErrorResponse.java` - **SIN TESTS** ⚠️

#### ❌ DTOs (Request/Response)
- `ConsultarDeudaRequest.java` - **PARCIALMENTE TESTEADO**
- `RegistrarPagoRequest.java` - **SIN TESTS** ⚠️
- `DeudaConsolidadaResponse.java` - **PARCIALMENTE TESTEADO**
- `FacturaResponse.java` - **PARCIALMENTE TESTEADO**
- `ConsumoEnergiaResponse.java` - **PARCIALMENTE TESTEADO**
- `EstadisticasResponse.java` - **SIN TESTS** ⚠️

#### ❌ Controladores REST (Endpoints faltantes)
- `WebViewController.java` - **SIN TESTS** ⚠️

---

## 2. ESTRATEGIA DE TESTING

### 2.1 Principios de Testing

1. **NO REDUNDANCIA**: No crear tests que repitan verificaciones ya cubiertas
2. **ARQUITECTURA INMUTABLE**: No modificar la arquitectura hexagonal del proyecto
3. **TESTS AISLADOS**: Cada test debe ser independiente y no depender de otros
4. **MOCKS APROPIADOS**: Usar mocks solo para dependencias externas
5. **ASSERTIONS CLARAS**: Usar AssertJ para assertions legibles
6. **NOMBRADO DESCRIPTIVO**: Tests con nombres que describan el comportamiento

### 2.2 Priorización de Tests

#### **PRIORIDAD ALTA** (Crítico para llegar a 85%)
1. **Value Objects** (ClienteId, Periodo, Dinero, etc.) - 15% de cobertura
2. **Modelos de Dominio** (métodos de negocio) - 10% de cobertura
3. **GlobalExceptionHandler** - 5% de cobertura
4. **DTOs con validaciones** - 3% de cobertura

#### **PRIORIDAD MEDIA**
5. **Configuraciones Spring** (SecurityConfig, WebConfig, etc.) - 5% de cobertura
6. **Excepciones personalizadas** - 2% de cobertura

#### **PRIORIDAD BAJA** (Si sobra tiempo)
7. **Clases de configuración sin lógica** - 1% de cobertura
8. **DTOs simples sin lógica** - 1% de cobertura

**Total Adicional Necesario:** ~35% para llegar de 60% a 85%

### 2.3 Herramientas de Testing

```xml
<!-- Ya incluidas en pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<!-- JUnit 5, Mockito, AssertJ incluidos -->

<!-- JaCoCo para cobertura -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
</plugin>
```

**Comandos Útiles:**
```powershell
# Ejecutar todos los tests
mvn clean test

# Generar reporte de cobertura
mvn clean test jacoco:report

# Ver reporte en navegador
start target/site/jacoco/index.html
```

---

## 3. TESTS FALTANTES POR CAPA

### 3.1 Capa de Dominio - Value Objects

#### 📁 `ClienteIdTest.java`

**Ubicación:** `src/test/java/com/serviciudad/domain/valueobject/ClienteIdTest.java`

**Cobertura:** ~25 líneas de código → ~2% de cobertura total

**Casos de Prueba:**

```java
@DisplayName("Value Object: ClienteId - Tests Unitarios")
class ClienteIdTest {
    
    @Test
    @DisplayName("Debe crear ClienteId válido con 10 dígitos")
    void debeCrearClienteIdValido()
    
    @Test
    @DisplayName("Debe lanzar excepción si ClienteId es nulo")
    void debeLanzarExcepcionSiClienteIdEsNulo()
    
    @Test
    @DisplayName("Debe lanzar excepción si ClienteId está vacío")
    void debeLanzarExcepcionSiClienteIdEstaVacio()
    
    @Test
    @DisplayName("Debe lanzar excepción si ClienteId no tiene 10 caracteres")
    void debeLanzarExcepcionSiClienteIdNoTiene10Caracteres()
    
    @Test
    @DisplayName("Debe lanzar excepción si ClienteId contiene caracteres no numéricos")
    void debeLanzarExcepcionSiClienteIdContieneNoNumericos()
    
    @Test
    @DisplayName("Debe crear ClienteId con método factory of()")
    void debeCrearClienteIdConMetodoFactory()
    
    @Test
    @DisplayName("Dos ClienteIds con mismo valor deben ser iguales")
    void dosClienteIdsConMismoValorDebenSerIguales()
    
    @Test
    @DisplayName("Dos ClienteIds con diferente valor deben ser diferentes")
    void dosClienteIdsConDiferenteValorDebenSerDiferentes()
    
    @Test
    @DisplayName("ClienteId debe ser inmutable (Lombok @Value)")
    void clienteIdDebeSerInmutable()
}
```

---

#### 📁 `PeriodoTest.java`

**Ubicación:** `src/test/java/com/serviciudad/domain/valueobject/PeriodoTest.java`

**Cobertura:** ~30 líneas de código → ~2.5% de cobertura total

**Casos de Prueba:**

```java
@DisplayName("Value Object: Periodo - Tests Unitarios")
class PeriodoTest {
    
    @Test
    @DisplayName("Debe crear Periodo válido con formato YYYYMM")
    void debeCrearPeriodoValido()
    
    @Test
    @DisplayName("Debe lanzar excepción si Periodo es nulo")
    void debeLanzarExcepcionSiPeriodoEsNulo()
    
    @Test
    @DisplayName("Debe lanzar excepción si Periodo no tiene 6 dígitos")
    void debeLanzarExcepcionSiPeriodoNoTiene6Digitos()
    
    @Test
    @DisplayName("Debe lanzar excepción si Periodo contiene caracteres no numéricos")
    void debeLanzarExcepcionSiPeriodoContieneNoNumericos()
    
    @Test
    @DisplayName("Debe extraer año correctamente")
    void debeExtraerAnioCorrectamente()
    
    @Test
    @DisplayName("Debe extraer mes correctamente")
    void debeExtraerMesCorrectamente()
    
    @Test
    @DisplayName("Debe validar mes entre 1 y 12")
    void debeValidarMesEntre1Y12()
    
    @Test
    @DisplayName("Debe validar año razonable (2000-2099)")
    void debeValidarAnioRazonable()
    
    @Test
    @DisplayName("Debe crear Periodo con método factory of()")
    void debeCrearPeriodoConMetodoFactory()
    
    @Test
    @DisplayName("Periodos deben ser comparables")
    void periodosDebenSerComparables()
}
```

---

#### 📁 `DineroTest.java`

**Ubicación:** `src/test/java/com/serviciudad/domain/valueobject/DineroTest.java`

**Cobertura:** ~60 líneas de código → ~4% de cobertura total

**Casos de Prueba:**

```java
@DisplayName("Value Object: Dinero - Tests Unitarios")
class DineroTest {
    
    @Test
    @DisplayName("Debe crear Dinero válido con BigDecimal")
    void debeCrearDineroValidoConBigDecimal()
    
    @Test
    @DisplayName("Debe crear Dinero con 2 decimales redondeados")
    void debeCrearDineroConDosDecimalesRedondeados()
    
    @Test
    @DisplayName("Debe lanzar excepción si monto es nulo")
    void debeLanzarExcepcionSiMontoEsNulo()
    
    @Test
    @DisplayName("Debe crear Dinero con método factory of(BigDecimal)")
    void debeCrearDineroConMetodoFactoryBigDecimal()
    
    @Test
    @DisplayName("Debe crear Dinero con método factory of(double)")
    void debeCrearDineroConMetodoFactoryDouble()
    
    @Test
    @DisplayName("Debe crear Dinero con método factory of(String)")
    void debeCrearDineroConMetodoFactoryString()
    
    @Test
    @DisplayName("Debe crear Dinero cero con método cero()")
    void debeCrearDineroCeroConMetodoCero()
    
    @Test
    @DisplayName("Debe detectar si Dinero es negativo")
    void debeDetectarSiDineroEsNegativo()
    
    @Test
    @DisplayName("Debe detectar si Dinero es cero")
    void debeDetectarSiDineroEsCero()
    
    @Test
    @DisplayName("Debe sumar dos cantidades de Dinero")
    void debeSumarDosCantidadesDeDinero()
    
    @Test
    @DisplayName("Debe restar dos cantidades de Dinero")
    void debeRestarDosCantidadesDeDinero()
    
    @Test
    @DisplayName("Debe comparar si un Dinero es mayor que otro")
    void debeCompararSiUnDineroEsMayorQueOtro()
    
    @Test
    @DisplayName("Debe comparar si un Dinero es menor que otro")
    void debeCompararSiUnDineroEsMenorQueOtro()
    
    @Test
    @DisplayName("Debe comparar si dos Dineros son iguales")
    void debeCompararSiDosDinerossonIguales()
    
    @Test
    @DisplayName("Operaciones aritméticas deben mantener 2 decimales")
    void operacionesAritmeticasDebenMantenerDosDecimales()
    
    @Test
    @DisplayName("Dinero debe ser inmutable")
    void dineroDebeSerInmutable()
}
```

---

#### 📁 `ConsumoAguaTest.java`

**Ubicación:** `src/test/java/com/serviciudad/domain/valueobject/ConsumoAguaTest.java`

**Cobertura:** ~25 líneas de código → ~2% de cobertura total

**Casos de Prueba:**

```java
@DisplayName("Value Object: ConsumoAgua - Tests Unitarios")
class ConsumoAguaTest {
    
    @Test
    @DisplayName("Debe crear ConsumoAgua válido con metros cúbicos positivos")
    void debeCrearConsumoAguaValidoConMetrosCubicosPositivos()
    
    @Test
    @DisplayName("Debe lanzar excepción si metros cúbicos son negativos")
    void debeLanzarExcepcionSiMetrosCubicosNegativos()
    
    @Test
    @DisplayName("Debe lanzar excepción si metros cúbicos son cero")
    void debeLanzarExcepcionSiMetrosCubicosCero()
    
    @Test
    @DisplayName("Debe crear ConsumoAgua con método factory of()")
    void debeCrearConsumoAguaConMetodoFactory()
    
    @Test
    @DisplayName("Debe identificar consumo alto (>30 m³)")
    void debeIdentificarConsumoAlto()
    
    @Test
    @DisplayName("Debe identificar consumo bajo (<10 m³)")
    void debeIdentificarConsumoBajo()
    
    @Test
    @DisplayName("Debe identificar consumo normal (10-30 m³)")
    void debeIdentificarConsumoNormal()
    
    @Test
    @DisplayName("ConsumoAgua debe ser inmutable")
    void consumoAguaDebeSerInmutable()
}
```

---

#### 📁 `ConsumoEnergiaTest.java`

**Ubicación:** `src/test/java/com/serviciudad/domain/valueobject/ConsumoEnergiaTest.java`

**Cobertura:** ~25 líneas de código → ~2% de cobertura total

**Casos de Prueba:**

```java
@DisplayName("Value Object: ConsumoEnergia - Tests Unitarios")
class ConsumoEnergiaTest {
    
    @Test
    @DisplayName("Debe crear ConsumoEnergia válido con kWh positivos")
    void debeCrearConsumoEnergiaValidoConKwhPositivos()
    
    @Test
    @DisplayName("Debe lanzar excepción si kWh son negativos")
    void debeLanzarExcepcionSiKwhNegativos()
    
    @Test
    @DisplayName("Debe lanzar excepción si kWh son cero")
    void debeLanzarExcepcionSiKwhCero()
    
    @Test
    @DisplayName("Debe crear ConsumoEnergia con método factory of()")
    void debeCrearConsumoEnergiaConMetodoFactory()
    
    @Test
    @DisplayName("Debe identificar consumo alto (>500 kWh)")
    void debeIdentificarConsumoAlto()
    
    @Test
    @DisplayName("Debe identificar consumo bajo (<100 kWh)")
    void debeIdentificarConsumoBajo()
    
    @Test
    @DisplayName("Debe identificar consumo normal (100-500 kWh)")
    void debeIdentificarConsumoNormal()
    
    @Test
    @DisplayName("ConsumoEnergia debe ser inmutable")
    void consumoEnergiaDebeSerInmutable()
}
```

---

#### 📁 `FacturaIdTest.java`

**Ubicación:** `src/test/java/com/serviciudad/domain/valueobject/FacturaIdTest.java`

**Cobertura:** ~15 líneas de código → ~1.5% de cobertura total

**Casos de Prueba:**

```java
@DisplayName("Value Object: FacturaId - Tests Unitarios")
class FacturaIdTest {
    
    @Test
    @DisplayName("Debe crear FacturaId válido con Long positivo")
    void debeCrearFacturaIdValidoConLongPositivo()
    
    @Test
    @DisplayName("Debe lanzar excepción si FacturaId es nulo")
    void debeLanzarExcepcionSiFacturaIdEsNulo()
    
    @Test
    @DisplayName("Debe lanzar excepción si FacturaId es cero")
    void debeLanzarExcepcionSiFacturaIdEsCero()
    
    @Test
    @DisplayName("Debe lanzar excepción si FacturaId es negativo")
    void debeLanzarExcepcionSiFacturaIdEsNegativo()
    
    @Test
    @DisplayName("Debe crear FacturaId con método factory of()")
    void debeCrearFacturaIdConMetodoFactory()
    
    @Test
    @DisplayName("FacturaId debe ser inmutable")
    void facturaIdDebeSerInmutable()
}
```

**Total Value Objects:** ~15% de cobertura adicional

---

### 3.2 Capa de Dominio - Modelos

#### 📁 `DeudaConsolidadaTest.java`

**Ubicación:** `src/test/java/com/serviciudad/domain/model/DeudaConsolidadaTest.java`

**Cobertura:** ~40 líneas de código → ~3% de cobertura total

**Casos de Prueba:**

```java
@DisplayName("Domain Model: DeudaConsolidada - Tests Unitarios")
class DeudaConsolidadaTest {
    
    @Test
    @DisplayName("Debe construir DeudaConsolidada con facturas y consumos")
    void debeConstruirDeudaConsolidadaConFacturasYConsumos()
    
    @Test
    @DisplayName("Debe calcular deuda total acueducto correctamente")
    void debeCalcularDeudaTotalAcueductoCorrectamente()
    
    @Test
    @DisplayName("Debe calcular deuda total energía correctamente")
    void debeCalcularDeudaTotalEnergiaCorrectamente()
    
    @Test
    @DisplayName("Debe calcular total general correctamente")
    void debeCalcularTotalGeneralCorrectamente()
    
    @Test
    @DisplayName("Debe generar alertas para facturas vencidas")
    void debeGenerarAlertasParaFacturasVencidas()
    
    @Test
    @DisplayName("Debe generar alertas para facturas próximas a vencer")
    void debeGenerarAlertasParaFacturasProximasAVencer()
    
    @Test
    @DisplayName("Debe calcular estadísticas correctamente")
    void debeCalcularEstadisticasCorrectamente()
    
    @Test
    @DisplayName("Debe detectar si tiene deuda")
    void debeDetectarSiTieneDeuda()
    
    @Test
    @DisplayName("Debe detectar si tiene facturas vencidas")
    void debeDetectarSiTieneFacturasVencidas()
    
    @Test
    @DisplayName("Debe obtener lista de facturas vencidas")
    void debeObtenerListaDeFacturasVencidas()
    
    @Test
    @DisplayName("Debe manejar deuda cero cuando no hay facturas pendientes")
    void debeManejarDeudaCeroCuandoNoHayFacturasPendientes()
}
```

---

#### 📁 `FacturaAcueductoTest.java`

**Ubicación:** `src/test/java/com/serviciudad/domain/model/FacturaAcueductoTest.java`

**Cobertura:** ~50 líneas de código → ~4% de cobertura total

**Casos de Prueba:**

```java
@DisplayName("Domain Model: FacturaAcueducto - Tests Unitarios")
class FacturaAcueductoTest {
    
    @Test
    @DisplayName("Debe crear FacturaAcueducto con Builder")
    void debeCrearFacturaAcueductoConBuilder()
    
    @Test
    @DisplayName("Debe detectar si factura está vencida")
    void debeDetectarSiFacturaEstaVencida()
    
    @Test
    @DisplayName("Debe registrar pago y cambiar estado a PAGADA")
    void debeRegistrarPagoYCambiarEstadoAPagada()
    
    @Test
    @DisplayName("Debe anular factura y cambiar estado a ANULADA")
    void debeAnularFacturaYCambiarEstadoAAnulada()
    
    @Test
    @DisplayName("Debe actualizar valor de factura pendiente")
    void debeActualizarValorDeFacturaPendiente()
    
    @Test
    @DisplayName("No debe permitir actualizar valor de factura pagada")
    void noDebePermitirActualizarValorDeFacturaPagada()
    
    @Test
    @DisplayName("No debe permitir actualizar con valor negativo")
    void noDebePermitirActualizarConValorNegativo()
    
    @Test
    @DisplayName("Debe calcular días hasta vencimiento correctamente")
    void debeCalcularDiasHastaVencimientoCorrectamente()
    
    @Test
    @DisplayName("Debe marcar como vencida cuando corresponde")
    void debeMarcarComoVencidaCuandoCorresponde()
    
    @Test
    @DisplayName("Debe identificar si está pagada")
    void debeIdentificarSiEstaPagada()
    
    @Test
    @DisplayName("Debe identificar si está vencida")
    void debeIdentificarSiEstaVencida()
    
    @Test
    @DisplayName("Debe identificar si está pendiente")
    void debeIdentificarSiEstaPendiente()
}
```

---

#### 📁 `ConsumoEnergiaModelTest.java`

**Ubicación:** `src/test/java/com/serviciudad/domain/model/ConsumoEnergiaModelTest.java`

**Cobertura:** ~30 líneas de código → ~2.5% de cobertura total

**Casos de Prueba:**

```java
@DisplayName("Domain Model: ConsumoEnergiaModel - Tests Unitarios")
class ConsumoEnergiaModelTest {
    
    @Test
    @DisplayName("Debe crear ConsumoEnergiaModel con Builder")
    void debeCrearConsumoEnergiaModelConBuilder()
    
    @Test
    @DisplayName("Debe identificar si tiene consumo alto")
    void debeIdentificarSiTieneConsumoAlto()
    
    @Test
    @DisplayName("Debe identificar si tiene consumo bajo")
    void debeIdentificarSiTieneConsumoBajo()
    
    @Test
    @DisplayName("Debe verificar si pertenece a estrato específico")
    void debeVerificarSiPerteneceAEstratoEspecifico()
    
    @Test
    @DisplayName("Debe comparar si es del mismo periodo que otro consumo")
    void debeCompararSiEsDelMismoPeriodoQueOtroConsumo()
    
    @Test
    @DisplayName("Debe comparar si es del mismo cliente que otro consumo")
    void debeCompararSiEsDelMismoClienteQueOtroConsumo()
}
```

---

#### 📁 `EstadisticasDeudaTest.java`

**Ubicación:** `src/test/java/com/serviciudad/domain/model/EstadisticasDeudaTest.java`

**Cobertura:** ~20 líneas de código → ~1.5% de cobertura total

**Casos de Prueba:**

```java
@DisplayName("Domain Model: EstadisticasDeuda - Tests Unitarios")
class EstadisticasDeudaTest {
    
    @Test
    @DisplayName("Debe crear EstadisticasDeuda con Builder")
    void debeCrearEstadisticasDeudaConBuilder()
    
    @Test
    @DisplayName("Debe almacenar total de facturas correctamente")
    void debeAlmacenarTotalDeFacturasCorrectamente()
    
    @Test
    @DisplayName("Debe almacenar promedios de consumo correctamente")
    void debeAlmacenarPromediosDeConsumoCorrectamente()
    
    @Test
    @DisplayName("Debe almacenar deudas acumuladas correctamente")
    void debeAlmacenarDeudasAcumuladasCorrectamente()
}
```

**Total Modelos de Dominio:** ~11% de cobertura adicional

---

### 3.3 Capa de Infraestructura - Manejo de Errores

#### 📁 `GlobalExceptionHandlerTest.java`

**Ubicación:** `src/test/java/com/serviciudad/exception/GlobalExceptionHandlerTest.java`

**Cobertura:** ~60 líneas de código → ~5% de cobertura total

**Casos de Prueba:**

```java
@WebMvcTest(GlobalExceptionHandler.class)
@Import(TestSecurityConfig.class)
@DisplayName("Global Exception Handler - Tests Unitarios")
class GlobalExceptionHandlerTest {
    
    @Test
    @DisplayName("Debe manejar FacturaNoEncontradaException con 404")
    void debeManejarFacturaNoEncontradaExceptionCon404()
    
    @Test
    @DisplayName("Debe manejar FacturaDuplicadaException con 409")
    void debeManejarFacturaDuplicadaExceptionCon409()
    
    @Test
    @DisplayName("Debe manejar MethodArgumentNotValidException con 400")
    void debeManejarMethodArgumentNotValidExceptionCon400()
    
    @Test
    @DisplayName("Debe manejar IllegalArgumentException con 400")
    void debeManejarIllegalArgumentExceptionCon400()
    
    @Test
    @DisplayName("Debe manejar Exception genérica con 500")
    void debeManejarExceptionGenericaCon500()
    
    @Test
    @DisplayName("Debe incluir timestamp en ErrorResponse")
    void debeIncluirTimestampEnErrorResponse()
    
    @Test
    @DisplayName("Debe incluir mensaje descriptivo en ErrorResponse")
    void debeIncluirMensajeDescriptivoEnErrorResponse()
    
    @Test
    @DisplayName("Debe incluir path en ErrorResponse")
    void debeIncluirPathEnErrorResponse()
}
```

---

#### 📁 `FacturaNoEncontradaExceptionTest.java`

**Ubicación:** `src/test/java/com/serviciudad/domain/exception/FacturaNoEncontradaExceptionTest.java`

**Cobertura:** ~10 líneas de código → ~1% de cobertura total

**Casos de Prueba:**

```java
@DisplayName("Exception: FacturaNoEncontradaException - Tests Unitarios")
class FacturaNoEncontradaExceptionTest {
    
    @Test
    @DisplayName("Debe crear excepción con mensaje personalizado")
    void debeCrearExcepcionConMensajePersonalizado()
    
    @Test
    @DisplayName("Debe ser una RuntimeException")
    void debeSerUnaRuntimeException()
}
```

---

#### 📁 `FacturaDuplicadaExceptionTest.java`

**Ubicación:** `src/test/java/com/serviciudad/domain/exception/FacturaDuplicadaExceptionTest.java`

**Cobertura:** ~10 líneas de código → ~1% de cobertura total

**Casos de Prueba:**

```java
@DisplayName("Exception: FacturaDuplicadaException - Tests Unitarios")
class FacturaDuplicadaExceptionTest {
    
    @Test
    @DisplayName("Debe crear excepción con mensaje personalizado")
    void debeCrearExcepcionConMensajePersonalizado()
    
    @Test
    @DisplayName("Debe ser una RuntimeException")
    void debeSerUnaRuntimeException()
}
```

**Total Manejo de Errores:** ~7% de cobertura adicional

---

### 3.4 DTOs y Validaciones (Prioridad Media)

#### 📁 `RegistrarPagoRequestTest.java`

**Ubicación:** `src/test/java/com/serviciudad/application/dto/request/RegistrarPagoRequestTest.java`

**Cobertura:** ~15 líneas de código → ~1% de cobertura total

**Casos de Prueba:**

```java
@DisplayName("DTO Request: RegistrarPagoRequest - Tests Unitarios")
class RegistrarPagoRequestTest {
    
    private Validator validator;
    
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    @DisplayName("Debe validar request válido sin errores")
    void debeValidarRequestValidoSinErrores()
    
    @Test
    @DisplayName("Debe rechazar facturaId nulo")
    void debeRechazarFacturaIdNulo()
    
    @Test
    @DisplayName("Debe rechazar facturaId negativo")
    void debeRechazarFacturaIdNegativo()
    
    @Test
    @DisplayName("Debe crear DTO con Builder")
    void debeCrearDtoConBuilder()
}
```

**Total DTOs:** ~1% de cobertura adicional

---

### 3.5 Configuraciones (Prioridad Baja)

> **NOTA:** Las clases de configuración de Spring (SecurityConfig, WebConfig, etc.) 
> tienen poco valor en tests unitarios, ya que su comportamiento se valida mejor 
> en tests de integración. Solo se testean si sobra tiempo para llegar al 85%.

---

## 4. PLAN DE IMPLEMENTACIÓN

### Fase 1: Value Objects (Prioridad ALTA) - Semana 1

**Objetivo:** +15% de cobertura

| Test Class | Estimación | Tests |
|------------|-----------|-------|
| `ClienteIdTest.java` | 1 hora | 9 tests |
| `PeriodoTest.java` | 1 hora | 10 tests |
| `DineroTest.java` | 2 horas | 16 tests |
| `ConsumoAguaTest.java` | 1 hora | 8 tests |
| `ConsumoEnergiaTest.java` | 1 hora | 8 tests |
| `FacturaIdTest.java` | 0.5 horas | 6 tests |

**Total:** 6.5 horas, 57 tests

---

### Fase 2: Modelos de Dominio (Prioridad ALTA) - Semana 1-2

**Objetivo:** +11% de cobertura

| Test Class | Estimación | Tests |
|------------|-----------|-------|
| `DeudaConsolidadaTest.java` | 2 horas | 11 tests |
| `FacturaAcueductoTest.java` | 2.5 horas | 12 tests |
| `ConsumoEnergiaModelTest.java` | 1.5 horas | 6 tests |
| `EstadisticasDeudaTest.java` | 1 hora | 4 tests |

**Total:** 7 horas, 33 tests

---

### Fase 3: Manejo de Errores (Prioridad ALTA) - Semana 2

**Objetivo:** +7% de cobertura

| Test Class | Estimación | Tests |
|------------|-----------|-------|
| `GlobalExceptionHandlerTest.java` | 2.5 horas | 8 tests |
| `FacturaNoEncontradaExceptionTest.java` | 0.5 horas | 2 tests |
| `FacturaDuplicadaExceptionTest.java` | 0.5 horas | 2 tests |

**Total:** 3.5 horas, 12 tests

---

### Fase 4: DTOs (Prioridad MEDIA) - Semana 2

**Objetivo:** +1% de cobertura

| Test Class | Estimación | Tests |
|------------|-----------|-------|
| `RegistrarPagoRequestTest.java` | 1 hora | 4 tests |

**Total:** 1 hora, 4 tests

---

### Resumen de Esfuerzo

| Fase | Horas | Tests | Cobertura |
|------|-------|-------|-----------|
| Fase 1: Value Objects | 6.5h | 57 | +15% |
| Fase 2: Modelos Dominio | 7h | 33 | +11% |
| Fase 3: Manejo Errores | 3.5h | 12 | +7% |
| Fase 4: DTOs | 1h | 4 | +1% |
| **TOTAL** | **18h** | **106** | **+34%** |

**Cobertura Final Estimada:** 60% (actual) + 34% (nuevo) = **94%** ✓

**Margen de Seguridad:** 9% por encima del objetivo (85%)

---

## 5. CASOS DE PRUEBA DETALLADOS

### 5.1 Estructura de Test Típica

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("Descripción del componente - Tests Unitarios")
class ComponenteTest {
    
    // Mocks (solo si se necesitan dependencias)
    @Mock
    private DependenciaPort dependencia;
    
    // Sujeto bajo prueba
    @InjectMocks
    private ComponenteImpl componente;
    
    // Datos de prueba reutilizables
    private ValueObject dato;
    
    @BeforeEach
    void setUp() {
        // Inicializar datos comunes
        dato = new ValueObject("valor");
    }
    
    @Test
    @DisplayName("Debe [comportamiento esperado] cuando [condición]")
    void debeComportamientoEsperado() {
        // Arrange - Preparar datos y mocks
        when(dependencia.metodo()).thenReturn(resultado);
        
        // Act - Ejecutar método bajo prueba
        Resultado resultado = componente.metodoAPrueba(entrada);
        
        // Assert - Verificar comportamiento
        assertThat(resultado).isNotNull();
        assertThat(resultado.getValor()).isEqualTo(esperado);
        
        // Verify - Verificar interacciones con mocks
        verify(dependencia, times(1)).metodo();
    }
    
    @Test
    @DisplayName("Debe lanzar excepción cuando [condición inválida]")
    void debeLanzarExcepcionCuandoCondicionInvalida() {
        // Arrange & Act & Assert
        assertThatThrownBy(() -> componente.metodoAPrueba(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("esperado");
    }
}
```

---

### 5.2 Ejemplo Completo: ClienteIdTest

```java
package com.serviciudad.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para el Value Object ClienteId.
 * 
 * Verifica:
 * - Validación de formato (10 dígitos numéricos)
 * - Inmutabilidad (Lombok @Value)
 * - Factory methods
 * - Equals/HashCode
 * 
 * @author Equipo ServiCiudad Cali
 * @version 1.0
 */
@DisplayName("Value Object: ClienteId - Tests Unitarios")
class ClienteIdTest {
    
    @Test
    @DisplayName("Debe crear ClienteId válido con 10 dígitos")
    void debeCrearClienteIdValido() {
        // Arrange
        String valor = "1234567890";
        
        // Act
        ClienteId clienteId = new ClienteId(valor);
        
        // Assert
        assertThat(clienteId).isNotNull();
        assertThat(clienteId.getValor()).isEqualTo(valor);
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si ClienteId es nulo")
    void debeLanzarExcepcionSiClienteIdEsNulo() {
        // Arrange, Act & Assert
        assertThatThrownBy(() -> new ClienteId(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ClienteId no puede ser nulo");
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si ClienteId está vacío")
    void debeLanzarExcepcionSiClienteIdEstaVacio() {
        // Arrange, Act & Assert
        assertThatThrownBy(() -> new ClienteId(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ClienteId no puede ser nulo o vacio");
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si ClienteId no tiene 10 caracteres")
    void debeLanzarExcepcionSiClienteIdNoTiene10Caracteres() {
        // Arrange, Act & Assert
        assertThatThrownBy(() -> new ClienteId("123"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ClienteId debe tener 10 caracteres");
        
        assertThatThrownBy(() -> new ClienteId("12345678901"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ClienteId debe tener 10 caracteres");
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si ClienteId contiene caracteres no numéricos")
    void debeLanzarExcepcionSiClienteIdContieneNoNumericos() {
        // Arrange, Act & Assert
        assertThatThrownBy(() -> new ClienteId("123456789A"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ClienteId debe ser numerico");
        
        assertThatThrownBy(() -> new ClienteId("12-3456789"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ClienteId debe ser numerico");
    }
    
    @Test
    @DisplayName("Debe crear ClienteId con método factory of()")
    void debeCrearClienteIdConMetodoFactory() {
        // Arrange
        String valor = "9876543210";
        
        // Act
        ClienteId clienteId = ClienteId.of(valor);
        
        // Assert
        assertThat(clienteId).isNotNull();
        assertThat(clienteId.getValor()).isEqualTo(valor);
    }
    
    @Test
    @DisplayName("Dos ClienteIds con mismo valor deben ser iguales")
    void dosClienteIdsConMismoValorDebenSerIguales() {
        // Arrange
        String valor = "1234567890";
        
        // Act
        ClienteId clienteId1 = new ClienteId(valor);
        ClienteId clienteId2 = new ClienteId(valor);
        
        // Assert
        assertThat(clienteId1).isEqualTo(clienteId2);
        assertThat(clienteId1.hashCode()).isEqualTo(clienteId2.hashCode());
    }
    
    @Test
    @DisplayName("Dos ClienteIds con diferente valor deben ser diferentes")
    void dosClienteIdsConDiferenteValorDebenSerDiferentes() {
        // Arrange & Act
        ClienteId clienteId1 = new ClienteId("1234567890");
        ClienteId clienteId2 = new ClienteId("0987654321");
        
        // Assert
        assertThat(clienteId1).isNotEqualTo(clienteId2);
    }
    
    @Test
    @DisplayName("ClienteId debe ser inmutable (Lombok @Value)")
    void clienteIdDebeSerInmutable() {
        // Arrange
        ClienteId clienteId = new ClienteId("1234567890");
        
        // Act & Assert
        // Verificar que no hay setters (Lombok @Value genera clase final)
        assertThat(ClienteId.class.isFinal()).isTrue();
        
        // Verificar que solo hay getter, no setter
        assertThat(ClienteId.class.getDeclaredMethods())
            .extracting("name")
            .contains("getValor")
            .doesNotContain("setValor");
    }
}
```

---

## 6. EJECUCIÓN Y VALIDACIÓN

### 6.1 Comandos para Ejecutar Tests

```powershell
# Navegar al directorio del proyecto
cd "d:\Google Drive\Universidad\7mo Semestre\Ing. de Software 2\ProyectoFinal\Ingenieria-Software-2\SERVICIUDAD-CALI"

# Ejecutar todos los tests
mvn clean test

# Generar reporte de cobertura
mvn clean test jacoco:report

# Ver reporte en navegador
start target/site/jacoco/index.html
```

---

### 6.2 Verificación de Cobertura

#### Reporte JaCoCo

El plugin JaCoCo genera un reporte HTML en:
```
target/site/jacoco/index.html
```

**Métricas a Verificar:**

1. **Cobertura de Líneas (Line Coverage):** ≥ 85%
2. **Cobertura de Ramas (Branch Coverage):** ≥ 70%
3. **Cobertura por Paquete:**
   - `com.serviciudad.domain.valueobject` → 100%
   - `com.serviciudad.domain.model` → 90%
   - `com.serviciudad.application.usecase` → 90%
   - `com.serviciudad.infrastructure.adapter` → 85%

#### Ejemplo de Salida JaCoCo

```
+--------------------------------------------------+
| Package                        | Line Coverage  |
+--------------------------------------------------+
| com.serviciudad.domain         | 95.2% (238/250)|
| com.serviciudad.application    | 91.3% (185/203)|
| com.serviciudad.infrastructure | 84.7% (312/368)|
| com.serviciudad.config         | 65.0% (52/80)  |
| com.serviciudad.exception      | 100% (45/45)   |
+--------------------------------------------------+
| TOTAL                          | 87.1%          |
+--------------------------------------------------+
```

---

### 6.3 Validación de Calidad de Tests

#### Checklist de Revisión

- [ ] Todos los tests tienen nombres descriptivos (`@DisplayName`)
- [ ] No hay código duplicado entre tests
- [ ] Cada test verifica UN solo comportamiento
- [ ] Los tests son independientes (no dependen del orden)
- [ ] Se usan assertions claras y específicas
- [ ] Los mocks se usan apropiadamente (solo para dependencias externas)
- [ ] No se modificó la arquitectura del proyecto
- [ ] Los tests son rápidos (< 100ms por test unitario)

---

## 7. RESUMEN EJECUTIVO

### Objetivo

✅ **Alcanzar 85% de cobertura de código** con tests unitarios **sin modificar la arquitectura** del proyecto.

### Estrategia

1. **Priorizar Value Objects y Modelos de Dominio** (cobertura crítica)
2. **Tests independientes y no redundantes**
3. **4 fases de implementación** (18 horas totales)
4. **106 nuevos tests** distribuidos en 13 clases

### Distribución de Esfuerzo

| Categoría | Cobertura | Tests | Horas |
|-----------|-----------|-------|-------|
| Value Objects | +15% | 57 | 6.5h |
| Modelos Dominio | +11% | 33 | 7h |
| Manejo Errores | +7% | 12 | 3.5h |
| DTOs | +1% | 4 | 1h |
| **TOTAL** | **+34%** | **106** | **18h** |

### Resultado Esperado

**Cobertura Final:** 94% (9% por encima del objetivo)

### Próximos Pasos

1. Implementar Fase 1 (Value Objects)
2. Ejecutar tests y verificar cobertura parcial
3. Implementar Fase 2 (Modelos de Dominio)
4. Ejecutar tests y verificar cobertura acumulada
5. Implementar Fases 3 y 4
6. Validar cobertura total ≥ 85%

---

## 📌 NOTAS IMPORTANTES

### NO Hacer

❌ **NO modificar la arquitectura hexagonal**  
❌ **NO crear tests redundantes** (ya hay tests de integración)  
❌ **NO testear clases de configuración sin lógica**  
❌ **NO usar bases de datos reales** en tests unitarios  
❌ **NO crear tests lentos** (>100ms por test unitario)

### SÍ Hacer

✅ **Usar mocks para dependencias externas**  
✅ **Tests independientes y aislados**  
✅ **Nombres descriptivos** (`@DisplayName`)  
✅ **Assertions claras** (AssertJ)  
✅ **Verificar comportamiento**, no implementación  
✅ **Mantener principio DRY** (Don't Repeat Yourself)

---

**Fin del Plan de Tests**

*Documento generado por: GitHub Copilot*  
*Fecha: Noviembre 2025*  
*Versión: 1.0*
