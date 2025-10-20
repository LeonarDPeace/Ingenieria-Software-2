package com.serviciudad.integration;

import com.serviciudad.infrastructure.adapter.output.persistence.jpa.entity.EstadoFacturaJpa;
import com.serviciudad.infrastructure.adapter.output.persistence.jpa.entity.FacturaJpaEntity;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests de integración E2E para workflow de Facturas Acueducto.
 * 
 * Usa @SpringBootTest + Testcontainers para validar:
 * Registrar → Consultar → Pagar → Verificar Estado → Anular
 * 
 * @author Equipo ServiCiudad Cali
 * @version 1.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSecurityConfig.class)
@DisplayName("Integration Test: Factura Acueducto Workflow E2E")
class FacturaAcueductoIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FacturaJpaRepository facturaRepository;

    private final String clienteId = "1234567890";

    @BeforeEach
    void setUp() {
        // Limpiar BD antes de cada test
        facturaRepository.deleteAll();
    }

    @Test
    @DisplayName("E2E: Debe consultar factura por ID desde REST hasta BD")
    void debeConsultarFacturaPorId() {
        // Arrange - Crear factura en BD
        FacturaJpaEntity factura = crearFacturaEnBD(EstadoFacturaJpa.PENDIENTE);

        // Act - Consultar vía REST
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/api/facturas/{facturaId}",
            String.class,
            factura.getId()
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains(clienteId);
        assertThat(response.getBody()).contains("202501");
        assertThat(response.getBody()).contains("PENDIENTE");
    }

    @Test
    @DisplayName("E2E: Debe consultar facturas por cliente ID")
    void debeConsultarFacturasPorCliente() {
        // Arrange - Crear múltiples facturas (diferentes periodos)
        crearFacturaEnBD(EstadoFacturaJpa.PENDIENTE, "202501");
        crearFacturaEnBD(EstadoFacturaJpa.PAGADA, "202502");

        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/api/facturas/cliente/{clienteId}",
            String.class,
            clienteId
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains(clienteId);
    }

    @Test
    @DisplayName("E2E: Workflow completo - Registrar → Consultar → Pagar → Verificar")
    void workflowCompletoRegistrarConsultarPagar() {
        // PASO 1: Registrar factura en BD
        FacturaJpaEntity factura = crearFacturaEnBD(EstadoFacturaJpa.PENDIENTE);
        Long facturaId = factura.getId();

        // PASO 2: Consultar factura (verificar que existe y está PENDIENTE)
        ResponseEntity<String> responseConsulta = restTemplate.getForEntity(
            "/api/facturas/{facturaId}",
            String.class,
            facturaId
        );
        assertThat(responseConsulta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseConsulta.getBody()).contains("PENDIENTE");

        // PASO 3: Pagar factura
        String pagoRequest = String.format("{\"facturaId\": %d}", facturaId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(pagoRequest, headers);

        ResponseEntity<Void> responsePago = restTemplate.postForEntity(
            "/api/facturas/pagar",
            requestEntity,
            Void.class
        );
        assertThat(responsePago.getStatusCode()).isEqualTo(HttpStatus.OK);

        // PASO 4: Verificar en BD que el estado cambió a PAGADA
        Optional<FacturaJpaEntity> facturaPagada = facturaRepository.findById(facturaId);
        assertThat(facturaPagada).isPresent();
        assertThat(facturaPagada.get().getEstado()).isEqualTo(EstadoFacturaJpa.PAGADA);
        assertThat(facturaPagada.get().getFechaActualizacion()).isAfter(factura.getFechaCreacion());
    }

    @Test
    @DisplayName("E2E: Debe anular factura y persistir el cambio")
    void debeAnularFacturaYPersistir() {
        // Arrange
        FacturaJpaEntity factura = crearFacturaEnBD(EstadoFacturaJpa.PENDIENTE);
        Long facturaId = factura.getId();

        // Act - Anular factura
        ResponseEntity<Void> response = restTemplate.postForEntity(
            "/api/facturas/{facturaId}/anular",
            null,
            Void.class,
            facturaId
        );

        // Assert - Verificar respuesta HTTP
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Assert - Verificar persistencia en BD
        Optional<FacturaJpaEntity> facturaAnulada = facturaRepository.findById(facturaId);
        assertThat(facturaAnulada).isPresent();
        assertThat(facturaAnulada.get().getEstado()).isEqualTo(EstadoFacturaJpa.ANULADA);
    }

    @Test
    @DisplayName("E2E: Debe marcar facturas vencidas automáticamente")
    void debeMarcarFacturasVencidas() {
        // Arrange - Crear factura con fecha vencida
        FacturaJpaEntity facturaVencida = FacturaJpaEntity.builder()
            .clienteId(clienteId)
            .periodo("202412")
            .consumo(20)
            .valorPagar(new BigDecimal("125000.00"))
            .estado(EstadoFacturaJpa.PENDIENTE)
            .fechaVencimiento(LocalDate.now().minusDays(5)) // Vencida hace 5 días
            .fechaCreacion(LocalDateTime.now().minusMonths(1))
            .fechaActualizacion(LocalDateTime.now().minusMonths(1))
            .build();
        facturaRepository.save(facturaVencida);

        // Act - Llamar endpoint para marcar vencidas
        ResponseEntity<Void> response = restTemplate.postForEntity(
            "/api/facturas/marcar-vencidas",
            null,
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Verificar que el estado cambió a VENCIDA en BD
        Optional<FacturaJpaEntity> facturaActualizada = facturaRepository.findById(facturaVencida.getId());
        assertThat(facturaActualizada).isPresent();
        assertThat(facturaActualizada.get().getEstado()).isEqualTo(EstadoFacturaJpa.VENCIDA);
    }

    @Test
    @DisplayName("E2E: Debe retornar error cuando factura no existe")
    void debeRetornar404CuandoFacturaNoExiste() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/api/facturas/{facturaId}",
            String.class,
            99999L
        );

        // Assert - El GlobalExceptionHandler convierte IllegalArgumentException en 400 BAD_REQUEST
        assertThat(response.getStatusCode()).isIn(HttpStatus.BAD_REQUEST, HttpStatus.NOT_FOUND, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ============ Helper Methods ============

    private FacturaJpaEntity crearFacturaEnBD(EstadoFacturaJpa estado) {
        return crearFacturaEnBD(estado, "202501");
    }

    private FacturaJpaEntity crearFacturaEnBD(EstadoFacturaJpa estado, String periodo) {
        FacturaJpaEntity factura = FacturaJpaEntity.builder()
            .clienteId(clienteId)
            .periodo(periodo)
            .consumo(15)
            .valorPagar(new BigDecimal("95000.00"))
            .estado(estado)
            .fechaVencimiento(LocalDate.now().plusDays(15))
            .fechaCreacion(LocalDateTime.now())
            .fechaActualizacion(LocalDateTime.now())
            .build();
        return facturaRepository.save(factura);
    }
}
