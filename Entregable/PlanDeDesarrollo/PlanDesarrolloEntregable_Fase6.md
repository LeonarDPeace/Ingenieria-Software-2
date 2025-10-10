# PLAN DE DESARROLLO - ENTREGABLE CORTE 2
## FASE 6: TESTING, CI/CD Y VALIDACIÓN FINAL

---

**Proyecto:** ServiCiudad Cali - Sistema de Consulta Unificada  
**Fase:** 6 - Testing Exhaustivo y Entrega Final  
**Fecha:** Octubre 2025

---

## 🎯 OBJETIVO DE LA FASE 6

Garantizar la **calidad del software** mediante testing exhaustivo, automatización CI/CD y validación completa de todos los entregables antes del release final.

---

## 🧪 TESTING UNITARIO (JUnit 5 + Mockito)

### 1. Tests del Adapter Pattern

```java
package com.serviciudad.adapter;

import com.serviciudad.adapter.exception.ArchivoEnergiaException;
import com.serviciudad.adapter.exception.ClienteNoEncontradoException;
import com.serviciudad.entity.FacturaEnergia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para ArchivoEnergiaAdapter
 * 
 * @DisplayName: Descripción legible del test
 * @Test: Marca el método como test case
 * AssertJ: Aserciones fluidas y legibles
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ArchivoEnergiaAdapter - Tests Unitarios")
class ArchivoEnergiaAdapterTest {
    
    @InjectMocks
    private ArchivoEnergiaAdapter adapter;
    
    @BeforeEach
    void setUp() {
        // Configurar ruta del archivo de prueba
        ReflectionTestUtils.setField(adapter, "archivoPath", 
            "src/test/resources/consumos_energia_test.txt");
    }
    
    @Test
    @DisplayName("Debe parsear correctamente línea de archivo energía")
    void debeParsearLineaCorrectamente() {
        // Given: Línea con formato COBOL
        String linea = "000123456720251000001500000180000.50";
        
        // When: Invocar método privado de parseo (usando reflection)
        FacturaEnergia resultado = adapter.consultarFactura("0001234567");
        
        // Then: Verificar campos parseados correctamente
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdCliente()).isEqualTo("0001234567");
        assertThat(resultado.getPeriodo()).isEqualTo("202510");
        assertThat(resultado.getConsumoKwh()).isEqualTo(1500);
        assertThat(resultado.getValorPagar())
            .isEqualByComparingTo(new BigDecimal("180000.50"));
    }
    
    @Test
    @DisplayName("Debe lanzar excepción cuando cliente no existe en archivo")
    void debeLanzarExcepcionClienteNoExiste() {
        // Given: Cliente que no existe
        String clienteInexistente = "9999999999";
        
        // When & Then: Verificar excepción
        assertThatThrownBy(() -> adapter.consultarFactura(clienteInexistente))
            .isInstanceOf(ClienteNoEncontradoException.class)
            .hasMessageContaining("Cliente no encontrado");
    }
    
    @Test
    @DisplayName("Debe lanzar excepción cuando archivo no existe")
    void debeLanzarExcepcionArchivoNoExiste() {
        // Given: Configurar ruta inválida
        ReflectionTestUtils.setField(adapter, "archivoPath", 
            "archivo_inexistente.txt");
        
        // When & Then: Verificar excepción
        assertThatThrownBy(() -> adapter.consultarFactura("0001234567"))
            .isInstanceOf(ArchivoEnergiaException.class)
            .hasMessageContaining("Error leyendo archivo");
    }
    
    @Test
    @DisplayName("Debe calcular correctamente el valor con decimales")
    void debeCalcularValorConDecimales() {
        // Given: Línea con valor decimal
        FacturaEnergia factura = adapter.consultarFactura("0001234567");
        
        // Then: Verificar precisión decimal
        assertThat(factura.getValorPagar().scale()).isEqualTo(2);
        assertThat(factura.getValorPagar())
            .isEqualByComparingTo("180000.50");
    }
}
```

### 2. Tests del Builder Pattern

```java
package com.serviciudad.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DisplayName("DeudaConsolidadaDTO - Tests del Builder Pattern")
class DeudaConsolidadaDTOTest {
    
    @Test
    @DisplayName("Debe construir DTO con todos los campos")
    void debeConstruirDTOCompleto() {
        // Given: Datos de prueba
        LocalDateTime now = LocalDateTime.now();
        ResumenDeudaDTO resumen = ResumenDeudaDTO.builder()
            .energia(DetalleServicioDTO.builder().build())
            .acueducto(DetalleServicioDTO.builder().build())
            .build();
        
        // When: Usar Builder Pattern
        DeudaConsolidadaDTO dto = DeudaConsolidadaDTO.builder()
            .clienteId("0001234567")
            .nombreCliente("Juan Pérez")
            .fechaConsulta(now)
            .resumenDeuda(resumen)
            .totalAPagar(new BigDecimal("275000.50"))
            .build();
        
        // Then: Verificar construcción
        assertThat(dto).isNotNull();
        assertThat(dto.getClienteId()).isEqualTo("0001234567");
        assertThat(dto.getNombreCliente()).isEqualTo("Juan Pérez");
        assertThat(dto.getFechaConsulta()).isEqualTo(now);
        assertThat(dto.getResumenDeuda()).isEqualTo(resumen);
        assertThat(dto.getTotalAPagar()).isEqualByComparingTo("275000.50");
    }
    
    @Test
    @DisplayName("Debe construir DTO con campos opcionales nulos")
    void debeConstruirDTOConNulos() {
        // When: Construcción parcial
        DeudaConsolidadaDTO dto = DeudaConsolidadaDTO.builder()
            .clienteId("0001234567")
            .build();
        
        // Then: Verificar campos opcionales
        assertThat(dto.getClienteId()).isEqualTo("0001234567");
        assertThat(dto.getNombreCliente()).isNull();
        assertThat(dto.getFechaConsulta()).isNull();
    }
}
```

### 3. Tests del Service Layer con Mocks

```java
package com.serviciudad.service;

import com.serviciudad.adapter.ServicioEnergiaPort;
import com.serviciudad.entity.FacturaAcueducto;
import com.serviciudad.entity.FacturaEnergia;
import com.serviciudad.repository.FacturaAcueductoRepository;
import com.serviciudad.service.exception.FacturaNoEncontradaException;
import com.serviciudad.service.mapper.DeudaConsolidadaDTOMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests del Service Layer con mocks
 * 
 * @Mock: Crea mocks de dependencias
 * @InjectMocks: Inyecta mocks en la clase bajo test
 * when().thenReturn(): Configurar comportamiento de mocks
 * verify(): Verificar interacciones con mocks
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DeudaConsolidadaService - Tests Unitarios")
class DeudaConsolidadaServiceTest {
    
    @Mock
    private ServicioEnergiaPort servicioEnergia;
    
    @Mock
    private FacturaAcueductoRepository repositorioAcueducto;
    
    @Mock
    private DeudaConsolidadaDTOMapper mapper;
    
    @InjectMocks
    private DeudaConsolidadaService service;
    
    @Test
    @DisplayName("Debe consultar deuda consolidada exitosamente")
    void debeConsultarDeudaConsolidadaExitosamente() {
        // Given: Configurar mocks
        String clienteId = "0001234567";
        
        FacturaEnergia facturaEnergia = FacturaEnergia.builder()
            .idCliente(clienteId)
            .periodo("202510")
            .consumoKwh(1500)
            .valorPagar(new BigDecimal("180000.50"))
            .build();
        
        FacturaAcueducto facturaAcueducto = FacturaAcueducto.builder()
            .idCliente(clienteId)
            .periodo("202510")
            .consumoM3(15)
            .valorPagar(new BigDecimal("95000.00"))
            .build();
        
        when(servicioEnergia.consultarFactura(clienteId))
            .thenReturn(facturaEnergia);
        when(repositorioAcueducto.findByIdClienteAndPeriodo(anyString(), anyString()))
            .thenReturn(Optional.of(facturaAcueducto));
        when(mapper.toDTO(anyString(), anyString(), any(), any()))
            .thenReturn(DeudaConsolidadaDTO.builder().build());
        
        // When: Ejecutar servicio
        DeudaConsolidadaDTO resultado = service.consultarDeudaConsolidada(clienteId);
        
        // Then: Verificar resultado y interacciones
        assertThat(resultado).isNotNull();
        verify(servicioEnergia, times(1)).consultarFactura(clienteId);
        verify(repositorioAcueducto, times(1))
            .findByIdClienteAndPeriodo(anyString(), anyString());
        verify(mapper, times(1))
            .toDTO(anyString(), anyString(), any(), any());
    }
    
    @Test
    @DisplayName("Debe lanzar excepción cuando factura acueducto no existe")
    void debeLanzarExcepcionFacturaAcueductoNoExiste() {
        // Given: Factura de acueducto no existe
        String clienteId = "0001234567";
        
        when(servicioEnergia.consultarFactura(clienteId))
            .thenReturn(FacturaEnergia.builder().build());
        when(repositorioAcueducto.findByIdClienteAndPeriodo(anyString(), anyString()))
            .thenReturn(Optional.empty());
        
        // When & Then: Verificar excepción
        assertThatThrownBy(() -> service.consultarDeudaConsolidada(clienteId))
            .isInstanceOf(FacturaNoEncontradaException.class)
            .hasMessageContaining("acueducto");
    }
}
```

---

## 🔗 TESTING DE INTEGRACIÓN

### 1. Test de Integración con @SpringBootTest

```java
package com.serviciudad.integration;

import com.serviciudad.dto.DeudaConsolidadaDTO;
import com.serviciudad.entity.FacturaAcueducto;
import com.serviciudad.repository.FacturaAcueductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests de integración end-to-end
 * 
 * @SpringBootTest: Levanta contexto completo de Spring
 * @Testcontainers: Usa contenedores Docker para tests
 * TestRestTemplate: Cliente HTTP para tests
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@DisplayName("Deuda Consolidada - Tests de Integración E2E")
class DeudaConsolidadaIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("serviciudad_test")
        .withUsername("test")
        .withPassword("test");
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private FacturaAcueductoRepository repository;
    
    @BeforeEach
    void setUp() {
        // Limpiar y preparar datos de prueba
        repository.deleteAll();
        
        FacturaAcueducto factura = FacturaAcueducto.builder()
            .idCliente("0001234567")
            .periodo("202510")
            .consumoM3(15)
            .valorPagar(new BigDecimal("95000.00"))
            .estado("PENDIENTE")
            .build();
        
        repository.save(factura);
    }
    
    @Test
    @DisplayName("GET /api/v1/clientes/{id}/deuda-consolidada debe retornar 200 y DTO completo")
    void debeConsultarDeudaConsolidadaExitosamente() {
        // Given: Cliente con facturas
        String clienteId = "0001234567";
        String url = "/api/v1/clientes/" + clienteId + "/deuda-consolidada";
        
        // When: Hacer request HTTP
        ResponseEntity<DeudaConsolidadaDTO> response = 
            restTemplate.getForEntity(url, DeudaConsolidadaDTO.class);
        
        // Then: Verificar respuesta
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        
        DeudaConsolidadaDTO dto = response.getBody();
        assertThat(dto.getClienteId()).isEqualTo(clienteId);
        assertThat(dto.getNombreCliente()).isNotBlank();
        assertThat(dto.getFechaConsulta()).isNotNull();
        assertThat(dto.getResumenDeuda()).isNotNull();
        assertThat(dto.getResumenDeuda().getEnergia()).isNotNull();
        assertThat(dto.getResumenDeuda().getAcueducto()).isNotNull();
        assertThat(dto.getTotalAPagar()).isGreaterThan(BigDecimal.ZERO);
    }
    
    @Test
    @DisplayName("GET con cliente inexistente debe retornar 404")
    void debeRetornar404ParaClienteInexistente() {
        // Given: Cliente que no existe
        String clienteInexistente = "9999999999";
        String url = "/api/v1/clientes/" + clienteInexistente + "/deuda-consolidada";
        
        // When: Hacer request
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        // Then: Verificar 404
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    
    @Test
    @DisplayName("Debe integrar correctamente Adapter (energía) + Repository (acueducto)")
    void debeIntegrarAdapterYRepository() {
        // Given: Cliente con datos en ambos sistemas
        String clienteId = "0001234567";
        
        // When: Consultar deuda consolidada
        ResponseEntity<DeudaConsolidadaDTO> response = restTemplate.getForEntity(
            "/api/v1/clientes/" + clienteId + "/deuda-consolidada",
            DeudaConsolidadaDTO.class);
        
        // Then: Verificar integración de fuentes
        DeudaConsolidadaDTO dto = response.getBody();
        
        // Datos de energía (desde archivo via Adapter)
        assertThat(dto.getResumenDeuda().getEnergia().getPeriodo()).isEqualTo("202510");
        assertThat(dto.getResumenDeuda().getEnergia().getConsumo()).contains("kWh");
        
        // Datos de acueducto (desde PostgreSQL via Repository)
        assertThat(dto.getResumenDeuda().getAcueducto().getPeriodo()).isEqualTo("202510");
        assertThat(dto.getResumenDeuda().getAcueducto().getConsumo()).contains("m³");
        
        // Total consolidado
        BigDecimal totalEsperado = dto.getResumenDeuda().getEnergia().getValorPagar()
            .add(dto.getResumenDeuda().getAcueducto().getValorPagar());
        assertThat(dto.getTotalAPagar()).isEqualByComparingTo(totalEsperado);
    }
}
```

### 2. Tests de Repository con @DataJpaTest

```java
package com.serviciudad.repository;

import com.serviciudad.entity.FacturaAcueducto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests del Repository con base de datos en memoria (H2)
 * 
 * @DataJpaTest: Configura slice de Spring para tests de JPA
 * Usa H2 in-memory automáticamente
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("FacturaAcueductoRepository - Tests de Persistencia")
class FacturaAcueductoRepositoryTest {
    
    @Autowired
    private FacturaAcueductoRepository repository;
    
    @Test
    @DisplayName("Debe guardar y recuperar factura correctamente")
    void debeGuardarYRecuperarFactura() {
        // Given: Nueva factura
        FacturaAcueducto factura = FacturaAcueducto.builder()
            .idCliente("0001234567")
            .periodo("202510")
            .consumoM3(15)
            .valorPagar(new BigDecimal("95000.00"))
            .estado("PENDIENTE")
            .build();
        
        // When: Guardar
        FacturaAcueducto guardada = repository.save(factura);
        
        // Then: Verificar persistencia
        assertThat(guardada.getId()).isNotNull();
        
        Optional<FacturaAcueducto> recuperada = repository.findById(guardada.getId());
        assertThat(recuperada).isPresent();
        assertThat(recuperada.get().getIdCliente()).isEqualTo("0001234567");
    }
    
    @Test
    @DisplayName("Debe encontrar factura por cliente y periodo")
    void debeEncontrarPorClienteYPeriodo() {
        // Given: Factura guardada
        FacturaAcueducto factura = FacturaAcueducto.builder()
            .idCliente("0001234567")
            .periodo("202510")
            .consumoM3(15)
            .valorPagar(new BigDecimal("95000.00"))
            .build();
        repository.save(factura);
        
        // When: Buscar por query derivada
        Optional<FacturaAcueducto> encontrada = repository
            .findByIdClienteAndPeriodo("0001234567", "202510");
        
        // Then: Verificar resultado
        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().getConsumoM3()).isEqualTo(15);
    }
}
```

---

## 📬 COLECCIÓN POSTMAN

### Estructura de la Colección

```json
{
  "info": {
    "name": "ServiCiudad API",
    "description": "Colección completa de endpoints para consulta unificada de servicios",
    "version": "1.0.0"
  },
  "variable": [
    {
      "key": "base_url",
      "value": "http://localhost:8080",
      "type": "string"
    },
    {
      "key": "cliente_id_existente",
      "value": "0001234567",
      "type": "string"
    }
  ],
  "item": [
    {
      "name": "Health Check",
      "request": {
        "method": "GET",
        "url": "{{base_url}}/actuator/health"
      },
      "event": [
        {
          "listen": "test",
          "script": {
            "exec": [
              "pm.test('Status code is 200', function () {",
              "    pm.response.to.have.status(200);",
              "});",
              "",
              "pm.test('Response has status UP', function () {",
              "    var jsonData = pm.response.json();",
              "    pm.expect(jsonData.status).to.eql('UP');",
              "});"
            ]
          }
        }
      ]
    },
    {
      "name": "Consultar Deuda Consolidada - Exitoso",
      "request": {
        "method": "GET",
        "url": "{{base_url}}/api/v1/clientes/{{cliente_id_existente}}/deuda-consolidada"
      },
      "event": [
        {
          "listen": "test",
          "script": {
            "exec": [
              "pm.test('Status code is 200', function () {",
              "    pm.response.to.have.status(200);",
              "});",
              "",
              "pm.test('Response has required fields', function () {",
              "    var jsonData = pm.response.json();",
              "    pm.expect(jsonData).to.have.property('clienteId');",
              "    pm.expect(jsonData).to.have.property('nombreCliente');",
              "    pm.expect(jsonData).to.have.property('fechaConsulta');",
              "    pm.expect(jsonData).to.have.property('resumenDeuda');",
              "    pm.expect(jsonData).to.have.property('totalAPagar');",
              "});",
              "",
              "pm.test('Total matches sum of services', function () {",
              "    var data = pm.response.json();",
              "    var energiaTotal = parseFloat(data.resumenDeuda.energia.valorPagar);",
              "    var acueductoTotal = parseFloat(data.resumenDeuda.acueducto.valorPagar);",
              "    var expectedTotal = energiaTotal + acueductoTotal;",
              "    pm.expect(parseFloat(data.totalAPagar)).to.eql(expectedTotal);",
              "});"
            ]
          }
        }
      ]
    },
    {
      "name": "Consultar Deuda - Cliente No Existe",
      "request": {
        "method": "GET",
        "url": "{{base_url}}/api/v1/clientes/9999999999/deuda-consolidada"
      },
      "event": [
        {
          "listen": "test",
          "script": {
            "exec": [
              "pm.test('Status code is 404', function () {",
              "    pm.response.to.have.status(404);",
              "});",
              "",
              "pm.test('Error message is present', function () {",
              "    var jsonData = pm.response.json();",
              "    pm.expect(jsonData).to.have.property('message');",
              "});"
            ]
          }
        }
      ]
    }
  ]
}
```

---

## 🤖 CI/CD CON GITHUB ACTIONS

### Workflow de CI/CD

```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  build-and-test:
    name: Build and Test
    runs-on: ubuntu-latest
    
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_DB: serviciudad_test
          POSTGRES_USER: test
          POSTGRES_PASSWORD: test
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 5432:5432
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven
      
      - name: Build with Maven
        run: mvn -B clean package --file pom.xml
      
      - name: Run unit tests
        run: mvn test
      
      - name: Run integration tests
        run: mvn verify -P integration-tests
      
      - name: Generate coverage report
        run: mvn jacoco:report
      
      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          files: ./target/site/jacoco/jacoco.xml
      
      - name: SonarQube Scan
        uses: sonarsource/sonarqube-scan-action@master
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
          SONAR_HOST_URL: ${{ secrets.SONAR_HOST_URL }}
  
  docker-build:
    name: Build Docker Image
    needs: build-and-test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v3
      
      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v2
      
      - name: Login to Docker Hub
        uses: docker/login-action@v2
        with:
          username: ${{ secrets.DOCKER_USERNAME }}
          password: ${{ secrets.DOCKER_PASSWORD }}
      
      - name: Build and push Docker image
        uses: docker/build-push-action@v4
        with:
          context: .
          file: ./docker/Dockerfile
          push: true
          tags: |
            serviciudad/api:latest
            serviciudad/api:${{ github.sha }}
          cache-from: type=registry,ref=serviciudad/api:buildcache
          cache-to: type=registry,ref=serviciudad/api:buildcache,mode=max
  
  deploy-staging:
    name: Deploy to Staging
    needs: docker-build
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    
    steps:
      - name: Deploy to staging environment
        run: |
          echo "Deploying to staging..."
          # Add deployment commands here
```

---

## ✅ CHECKLIST FINAL DE VALIDACIÓN

### Código y Funcionalidad
- [ ] ✅ Aplicación compila sin errores (`mvn clean package`)
- [ ] ✅ Todos los tests unitarios pasan (cobertura >80%)
- [ ] ✅ Tests de integración exitosos
- [ ] ✅ Endpoint REST responde correctamente
- [ ] ✅ 5 patrones de diseño implementados y funcionando
- [ ] ✅ Integración con archivo legacy funcional
- [ ] ✅ Integración con PostgreSQL funcional
- [ ] ✅ Respuesta JSON cumple especificación del enunciado

### Docker y Deployment
- [ ] ✅ `docker-compose up` levanta todos los servicios
- [ ] ✅ Healthchecks en verde
- [ ] ✅ API accesible desde Docker
- [ ] ✅ Base de datos inicializada con datos de ejemplo
- [ ] ✅ Logs estructurados funcionando

### Documentación
- [ ] ✅ INFORME.md completo con justificaciones
- [ ] ✅ README.md con instrucciones claras
- [ ] ✅ SUSTENTACION.md preparada
- [ ] ✅ DEPLOYMENT.md con procedimientos
- [ ] ✅ Diagramas de arquitectura incluidos

### Testing
- [ ] ✅ Colección Postman exportada y funcional
- [ ] ✅ Tests automatizados en requests Postman
- [ ] ✅ Variables de entorno configuradas
- [ ] ✅ Casos de error documentados

### CI/CD
- [ ] ✅ GitHub Actions pipeline configurado
- [ ] ✅ Build automático funciona
- [ ] ✅ Tests automáticos pasan
- [ ] ✅ Docker build automático exitoso

### Entrega Final
- [ ] ✅ Código en GitHub (rama main)
- [ ] ✅ Release tag v1.0.0 creado
- [ ] ✅ README.md verificado desde perspectiva de nuevo usuario
- [ ] ✅ Todos los archivos entregables presentes

---

## 📊 MÉTRICAS FINALES ESPERADAS

| Métrica | Target | Herramienta |
|---------|--------|-------------|
| **Cobertura de código** | >80% | JaCoCo |
| **Tests unitarios** | 100% pasan | JUnit 5 |
| **Tests integración** | 100% pasan | @SpringBootTest |
| **Complejidad ciclomática** | <10 | SonarQube |
| **Deuda técnica** | <5% | SonarQube |
| **Vulnerabilidades** | 0 críticas | OWASP Dependency Check |
| **Build time** | <3 min | GitHub Actions |
| **Tiempo respuesta API** | <2s | Postman Tests |

---

## 🎉 ENTREGA FINAL

### Comando de Entrega
```bash
# 1. Verificar todo funciona
mvn clean verify
docker-compose up -d
curl http://localhost:8080/api/v1/clientes/0001234567/deuda-consolidada

# 2. Crear tag de release
git tag -a v1.0.0 -m "Release Entregable Corte 2 - ServiCiudad API"
git push origin v1.0.0

# 3. Generar release en GitHub con documentación
# Incluir: README, INFORME, colección Postman, archivos Docker
```

### Artefactos Entregables
1. **Código Fuente**: Repositorio GitHub completo
2. **README.md**: Instrucciones de instalación y uso
3. **INFORME.md**: Justificación técnica de patrones
4. **Colección Postman**: Exportada con tests automáticos
5. **Docker**: docker-compose.yml y Dockerfile
6. **Documentación Extra**: SUSTENTACION.md, DEPLOYMENT.md

---

## 🏆 CRITERIOS DE ÉXITO FINAL

✅ **Funcional:**
- API responde en <2s
- Integración legacy + PostgreSQL exitosa
- 5 patrones implementados correctamente

✅ **Calidad:**
- Cobertura de tests >80%
- Sin vulnerabilidades críticas
- Código limpio y documentado

✅ **Documentación:**
- README permite a cualquiera instalar y ejecutar
- INFORME justifica técnicamente todos los patrones
- SUSTENTACION prepara para defensa oral

✅ **Entrega:**
- Todo en GitHub con release tag
- Docker funcional
- Colección Postman completa

---

*Documento generado: Octubre 10, 2025*  
*Universidad Autónoma de Occidente - Ingeniería de Software 2*

**¡FIN DEL PLAN DE DESARROLLO!**
