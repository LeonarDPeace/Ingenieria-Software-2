package com.serviciudad.integration;

import com.serviciudad.domain.FacturaAcueducto;
import com.serviciudad.dto.FacturaRequestDTO;
import com.serviciudad.dto.FacturaResponseDTO;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
@DisplayName("Facturas API - Tests de Integracion E2E")
class FacturaAcueductoIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
        .withDatabaseName("serviciudad_test")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FacturaAcueductoRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/facturas debe crear factura y retornar 201")
    void debeCrearFacturaExitosamente() {
        FacturaRequestDTO request = crearFacturaRequest();

        ResponseEntity<FacturaResponseDTO> response = restTemplate.postForEntity(
            "/api/facturas",
            request,
            FacturaResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getIdCliente()).isEqualTo(request.getIdCliente());
    }

    @Test
    @DisplayName("GET /api/facturas debe retornar todas las facturas")
    void debeObtenerTodasFacturas() {
        repository.save(crearFacturaEntidad("0001234567"));
        repository.save(crearFacturaEntidad("0007654321"));

        ResponseEntity<FacturaResponseDTO[]> response = restTemplate.getForEntity(
            "/api/facturas",
            FacturaResponseDTO[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("GET /api/facturas/{id} debe retornar factura por ID")
    void debeObtenerFacturaPorId() {
        FacturaAcueducto factura = repository.save(crearFacturaEntidad("0001234567"));

        ResponseEntity<FacturaResponseDTO> response = restTemplate.getForEntity(
            "/api/facturas/" + factura.getId(),
            FacturaResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(factura.getId());
    }

    @Test
    @DisplayName("GET /api/facturas/{id} debe retornar 404 cuando no existe")
    void debeRetornar404CuandoFacturaNoExiste() {
        ResponseEntity<FacturaResponseDTO> response = restTemplate.getForEntity(
            "/api/facturas/999",
            FacturaResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("PUT /api/facturas/{id} debe actualizar factura")
    void debeActualizarFactura() {
        FacturaAcueducto factura = repository.save(crearFacturaEntidad("0001234567"));
        FacturaRequestDTO request = crearFacturaRequest();
        request.setConsumoMetrosCubicos(20);
        request.setValorPagar(new BigDecimal("120000.00"));

        restTemplate.put(
            "/api/facturas/" + factura.getId(),
            request
        );

        FacturaAcueducto actualizada = repository.findById(factura.getId()).orElseThrow();
        assertThat(actualizada.getConsumoMetrosCubicos()).isEqualTo(20);
    }

    @Test
    @DisplayName("DELETE /api/facturas/{id} debe eliminar factura")
    void debeEliminarFactura() {
        FacturaAcueducto factura = repository.save(crearFacturaEntidad("0001234567"));

        restTemplate.delete("/api/facturas/" + factura.getId());

        assertThat(repository.findById(factura.getId())).isEmpty();
    }

    @Test
    @DisplayName("GET /api/facturas/cliente/{id} debe retornar facturas del cliente")
    void debeObtenerFacturasPorCliente() {
        String idCliente = "0001234567";
        repository.save(crearFacturaEntidad(idCliente));
        repository.save(crearFacturaEntidad(idCliente));
        repository.save(crearFacturaEntidad("0007654321"));

        ResponseEntity<FacturaResponseDTO[]> response = restTemplate.getForEntity(
            "/api/facturas/cliente/" + idCliente,
            FacturaResponseDTO[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    @DisplayName("GET /api/facturas/vencidas debe retornar facturas vencidas")
    void debeObtenerFacturasVencidas() {
        FacturaAcueducto facturaVencida = crearFacturaEntidad("0001234567");
        facturaVencida.setFechaVencimiento(LocalDate.now().minusDays(5));
        facturaVencida.setEstado("VENCIDA");
        repository.save(facturaVencida);

        ResponseEntity<FacturaResponseDTO[]> response = restTemplate.getForEntity(
            "/api/facturas/vencidas",
            FacturaResponseDTO[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("POST /api/facturas/{id}/pagar debe registrar pago")
    void debeRegistrarPago() {
        FacturaAcueducto factura = crearFacturaEntidad("0001234567");
        factura.setEstado("PENDIENTE");
        factura = repository.save(factura);

        ResponseEntity<FacturaResponseDTO> response = restTemplate.postForEntity(
            "/api/facturas/" + factura.getId() + "/pagar",
            null,
            FacturaResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEstado()).isEqualTo("PAGADA");
        assertThat(response.getBody().isPagada()).isTrue();
    }

    @Test
    @DisplayName("POST /api/facturas con datos invalidos debe retornar 400")
    void debeRetornar400ConDatosInvalidos() {
        FacturaRequestDTO request = new FacturaRequestDTO();
        request.setIdCliente("");
        request.setConsumoMetrosCubicos(-5);

        ResponseEntity<FacturaResponseDTO> response = restTemplate.postForEntity(
            "/api/facturas",
            request,
            FacturaResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private FacturaRequestDTO crearFacturaRequest() {
        FacturaRequestDTO dto = new FacturaRequestDTO();
        dto.setIdCliente("0001234567");
        dto.setNombreCliente("Juan Perez");
        dto.setPeriodo("202510");
        dto.setConsumoMetrosCubicos(15);
        dto.setValorPagar(new BigDecimal("95000.00"));
        dto.setFechaVencimiento(LocalDate.now().plusDays(15));
        return dto;
    }

    private FacturaAcueducto crearFacturaEntidad(String idCliente) {
        FacturaAcueducto factura = new FacturaAcueducto();
        factura.setIdCliente(idCliente);
        factura.setNombreCliente("Juan Perez");
        factura.setPeriodo("202510");
        factura.setConsumoMetrosCubicos(15);
        factura.setValorPagar(new BigDecimal("95000.00"));
        factura.setFechaEmision(LocalDate.now());
        factura.setFechaVencimiento(LocalDate.now().plusDays(15));
        factura.setEstado("PENDIENTE");
        return factura;
    }
}
