package com.serviciudad.integration;

import com.serviciudad.application.dto.request.ConsultarDeudaRequest;
import com.serviciudad.application.dto.response.DeudaConsolidadaResponse;
import com.serviciudad.infrastructure.adapter.output.persistence.jpa.entity.ConsumoEnergiaJpaEntity;
import com.serviciudad.infrastructure.adapter.output.persistence.jpa.entity.EstadoFacturaJpa;
import com.serviciudad.infrastructure.adapter.output.persistence.jpa.entity.FacturaJpaEntity;
import com.serviciudad.infrastructure.adapter.output.persistence.jpa.repository.ConsumoEnergiaJpaRepository;
import com.serviciudad.infrastructure.adapter.output.persistence.jpa.repository.FacturaJpaRepository;
import com.serviciudad.infrastructure.config.TestSecurityConfig;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests de integración E2E para Deuda Consolidada.
 * 
 * Usa @SpringBootTest + Testcontainers con PostgreSQL real.
 * Valida el flujo completo desde REST hasta BD.
 * 
 * @author Equipo ServiCiudad Cali
 * @version 1.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSecurityConfig.class)
@DisplayName("Integration Test: Deuda Consolidada E2E")
class DeudaConsolidadaIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FacturaJpaRepository facturaRepository;

    @Autowired
    private ConsumoEnergiaJpaRepository consumoEnergiaRepository;

    private final String clienteId = "1234567890";

    @BeforeEach
    void setUp() {
        // Limpiar BD antes de cada test
        facturaRepository.deleteAll();
        consumoEnergiaRepository.deleteAll();
    }

    @Test
    @SuppressWarnings("null")
    @DisplayName("E2E: Debe consultar deuda consolidada con facturas y consumos")
    void debeConsultarDeudaConsolidadaCompleta() {
        // Arrange - Crear datos de prueba en BD
        crearFacturasAcueducto();
        crearConsumosEnergia();

        ConsultarDeudaRequest request = new ConsultarDeudaRequest();
        request.setClienteId(clienteId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ConsultarDeudaRequest> entity = new HttpEntity<>(request, headers);

        // Act - Llamar endpoint REST
        ResponseEntity<DeudaConsolidadaResponse> response = restTemplate.postForEntity(
            "/api/deuda/consultar",
            entity,
            DeudaConsolidadaResponse.class
        );

        // Assert - Verificar respuesta
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        
        DeudaConsolidadaResponse deuda = response.getBody();
        assertThat(deuda.getClienteId()).isEqualTo(clienteId);
        assertThat(deuda.getTotalGeneral()).isGreaterThan(BigDecimal.ZERO);
        assertThat(deuda.getDeudaTotalAcueducto()).isEqualByComparingTo(new BigDecimal("190000.00"));
        assertThat(deuda.getDeudaTotalEnergia()).isEqualByComparingTo(new BigDecimal("360000.00"));
        assertThat(deuda.getFechaConsulta()).isNotNull();
    }

    @Test
    @SuppressWarnings("null")
    @DisplayName("E2E: Debe retornar deuda cero cuando no hay facturas pendientes")
    void debeRetornarDeudaCeroSinFacturasPendientes() {
        // Arrange - Crear facturas PAGADAS
        crearFacturasPagadas();

        ConsultarDeudaRequest request = new ConsultarDeudaRequest();
        request.setClienteId(clienteId);

        HttpEntity<ConsultarDeudaRequest> entity = new HttpEntity<>(request);

        // Act
        ResponseEntity<DeudaConsolidadaResponse> response = restTemplate.postForEntity(
            "/api/deuda/consultar",
            entity,
            DeudaConsolidadaResponse.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DeudaConsolidadaResponse deuda = response.getBody();
        assertThat(deuda.getTotalGeneral()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(deuda.getDeudaTotalAcueducto()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(deuda.getDeudaTotalEnergia()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @SuppressWarnings("null")
    @DisplayName("E2E: Debe incluir estadísticas en la deuda consolidada")
    void debeIncluirEstadisticasEnDeuda() {
        // Arrange
        crearFacturasAcueducto();

        ConsultarDeudaRequest request = new ConsultarDeudaRequest();
        request.setClienteId(clienteId);

        // Act
        ResponseEntity<DeudaConsolidadaResponse> response = restTemplate.postForEntity(
            "/api/deuda/consultar",
            new HttpEntity<>(request),
            DeudaConsolidadaResponse.class
        );

        // Assert
        assertThat(response.getBody()).isNotNull();
        DeudaConsolidadaResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getEstadisticas()).isNotNull();
        assertThat(body.getEstadisticas().getTotalFacturasAcueducto()).isGreaterThan(0);
    }

    @Test
    @SuppressWarnings("null")
    @DisplayName("E2E: Debe detectar facturas vencidas correctamente")
    void debeDetectarFacturasVencidas() {
        // Arrange - Crear factura vencida
        FacturaJpaEntity facturaVencida = FacturaJpaEntity.builder()
            .clienteId(clienteId)
            .periodo("202412")
            .consumo(20)
            .valorPagar(new BigDecimal("125000.00"))
            .estado(EstadoFacturaJpa.VENCIDA)
            .fechaVencimiento(LocalDate.now().minusDays(10))
            .fechaCreacion(LocalDateTime.now().minusMonths(1))
            .fechaActualizacion(LocalDateTime.now())
            .build();
        facturaRepository.save(facturaVencida);

        ConsultarDeudaRequest request = new ConsultarDeudaRequest();
        request.setClienteId(clienteId);

        // Act
        ResponseEntity<DeudaConsolidadaResponse> response = restTemplate.postForEntity(
            "/api/deuda/consultar",
            new HttpEntity<>(request),
            DeudaConsolidadaResponse.class
        );

        // Assert
        assertThat(response.getBody()).isNotNull();
        DeudaConsolidadaResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getAlertas()).isNotEmpty();
    }

    // ============ Helper Methods ============

    private void crearFacturasAcueducto() {
        FacturaJpaEntity factura1 = FacturaJpaEntity.builder()
            .clienteId(clienteId)
            .periodo("202501")
            .consumo(15)
            .valorPagar(new BigDecimal("95000.00"))
            .estado(EstadoFacturaJpa.PENDIENTE)
            .fechaVencimiento(LocalDate.now().plusDays(15))
            .fechaCreacion(LocalDateTime.now())
            .fechaActualizacion(LocalDateTime.now())
            .build();

        FacturaJpaEntity factura2 = FacturaJpaEntity.builder()
            .clienteId(clienteId)
            .periodo("202502")
            .consumo(18)
            .valorPagar(new BigDecimal("95000.00"))
            .estado(EstadoFacturaJpa.PENDIENTE)
            .fechaVencimiento(LocalDate.now().plusDays(45))
            .fechaCreacion(LocalDateTime.now())
            .fechaActualizacion(LocalDateTime.now())
            .build();

        facturaRepository.save(factura1);
        facturaRepository.save(factura2);
    }

    private void crearConsumosEnergia() {
        ConsumoEnergiaJpaEntity consumo1 = ConsumoEnergiaJpaEntity.builder()
            .clienteId(clienteId)
            .periodo("202501")
            .consumo(new BigDecimal("350"))
            .valorPagar(new BigDecimal("180000.00"))
            .fechaLectura(LocalDate.of(2025, 1, 15))
            .estrato("3")
            .build();

        ConsumoEnergiaJpaEntity consumo2 = ConsumoEnergiaJpaEntity.builder()
            .clienteId(clienteId)
            .periodo("202502")
            .consumo(new BigDecimal("320"))
            .valorPagar(new BigDecimal("180000.00"))
            .fechaLectura(LocalDate.of(2025, 2, 15))
            .estrato("3")
            .build();

        consumoEnergiaRepository.save(consumo1);
        consumoEnergiaRepository.save(consumo2);
    }

    private void crearFacturasPagadas() {
        FacturaJpaEntity facturaPagada = FacturaJpaEntity.builder()
            .clienteId(clienteId)
            .periodo("202412")
            .consumo(16)
            .valorPagar(new BigDecimal("100000.00"))
            .estado(EstadoFacturaJpa.PAGADA)
            .fechaVencimiento(LocalDate.now().minusMonths(1))
            .fechaCreacion(LocalDateTime.now().minusMonths(2))
            .fechaActualizacion(LocalDateTime.now())
            .build();

        facturaRepository.save(facturaPagada);
    }
}
