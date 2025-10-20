package com.serviciudad.infrastructure.adapter.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviciudad.application.dto.request.ConsultarDeudaRequest;
import com.serviciudad.application.mapper.DeudaMapper;
import com.serviciudad.domain.model.*;
import com.serviciudad.domain.port.input.ConsultarDeudaUseCase;
import com.serviciudad.domain.valueobject.*;
import com.serviciudad.infrastructure.config.TestSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitarios para DeudaRestController.
 * 
 * Usa @WebMvcTest para cargar solo la capa web (controller + MockMvc).
 * Mockea el use case para aislar la prueba del controller.
 * 
 * Cobertura:
 * - POST /api/deuda/consultar
 * - GET /api/deuda/cliente/{clienteId}
 * - Validación de inputs
 * - Manejo de errores
 * - Serialización JSON correcta
 * 
 * @author Equipo ServiCiudad Cali
 * @version 1.0
 */
@WebMvcTest(DeudaRestController.class)
@Import(TestSecurityConfig.class)
@DisplayName("REST Controller: Deuda - Tests MockMvc")
class DeudaRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ConsultarDeudaUseCase consultarDeudaUseCase;

    private DeudaConsolidada deudaMock;
    private ClienteId clienteId;

    @BeforeEach
    void setUp() {
        clienteId = new ClienteId("1234567890");

        // Mock de factura
        FacturaAcueducto factura = FacturaAcueducto.builder()
            .id(new FacturaId(1L))
            .clienteId(clienteId)
            .periodo(Periodo.of("202501"))
            .consumo(ConsumoAgua.of(15))
            .valorPagar(Dinero.of(new BigDecimal("95000.00")))
            .fechaVencimiento(LocalDate.now().plusDays(10))
            .fechaCreacion(LocalDateTime.now())
            .fechaActualizacion(LocalDateTime.now())
            .estado(EstadoFactura.PENDIENTE)
            .build();

        // Mock de consumo energía
        ConsumoEnergiaModel consumo = ConsumoEnergiaModel.builder()
            .clienteId(clienteId)
            .periodo(Periodo.of("202501"))
            .consumo(ConsumoEnergia.of(350))
            .valorPagar(Dinero.of(new BigDecimal("180000.00")))
            .fechaLectura(LocalDate.now())
            .estrato("3")
            .build();

        List<FacturaAcueducto> facturas = List.of(factura);
        List<ConsumoEnergiaModel> consumos = List.of(consumo);

        deudaMock = DeudaConsolidada.construir(clienteId, facturas, consumos);
    }

    @Test
    @DisplayName("POST /api/deuda/consultar - Debe retornar deuda consolidada exitosamente")
    void debeConsultarDeudaConPost() throws Exception {
        // Arrange
        ConsultarDeudaRequest request = new ConsultarDeudaRequest();
        request.setClienteId("1234567890");

        when(consultarDeudaUseCase.consultarDeudaConsolidada(any(ClienteId.class)))
            .thenReturn(deudaMock);

        // Act & Assert
        mockMvc.perform(post("/api/deuda/consultar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.clienteId").value("1234567890"))
            .andExpect(jsonPath("$.totalGeneral").value(275000.00))
            .andExpect(jsonPath("$.deudaTotalAcueducto").value(95000.00))
            .andExpect(jsonPath("$.deudaTotalEnergia").value(180000.00))
            .andExpect(jsonPath("$.facturasAcueducto").isArray())
            .andExpect(jsonPath("$.facturasAcueducto", hasSize(1)))
            .andExpect(jsonPath("$.consumosEnergia").isArray())
            .andExpect(jsonPath("$.consumosEnergia", hasSize(1)))
            .andExpect(jsonPath("$.fechaConsulta").exists())
            .andExpect(jsonPath("$.estadisticas").exists());
    }

    @Test
    @DisplayName("GET /api/deuda/cliente/{clienteId} - Debe retornar deuda por path variable")
    void debeConsultarDeudaPorPathVariable() throws Exception {
        // Arrange
        when(consultarDeudaUseCase.consultarDeudaConsolidada(any(ClienteId.class)))
            .thenReturn(deudaMock);

        // Act & Assert
        mockMvc.perform(get("/api/deuda/cliente/1234567890"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.clienteId").value("1234567890"))
            .andExpect(jsonPath("$.totalGeneral").exists())
            .andExpect(jsonPath("$.facturasAcueducto").isArray())
            .andExpect(jsonPath("$.consumosEnergia").isArray());
    }

    @Test
    @DisplayName("POST /api/deuda/consultar - Debe validar clienteId no nulo")
    void debeValidarClienteIdNoNulo() throws Exception {
        // Arrange
        ConsultarDeudaRequest request = new ConsultarDeudaRequest();
        request.setClienteId(null);

        // Act & Assert
        mockMvc.perform(post("/api/deuda/consultar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/deuda/consultar - Debe validar clienteId no vacío")
    void debeValidarClienteIdNoVacio() throws Exception {
        // Arrange
        ConsultarDeudaRequest request = new ConsultarDeudaRequest();
        request.setClienteId("");

        // Act & Assert
        mockMvc.perform(post("/api/deuda/consultar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe retornar deuda cero cuando cliente no tiene deudas")
    void debeRetornarDeudaCero() throws Exception {
        // Arrange
        DeudaConsolidada deudaCero = DeudaConsolidada.construir(
            clienteId,
            Collections.emptyList(),
            Collections.emptyList()
        );

        when(consultarDeudaUseCase.consultarDeudaConsolidada(any(ClienteId.class)))
            .thenReturn(deudaCero);

        // Act & Assert
        mockMvc.perform(get("/api/deuda/cliente/1234567890"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalGeneral").value(0))
            .andExpect(jsonPath("$.facturasAcueducto").isEmpty())
            .andExpect(jsonPath("$.consumosEnergia").isEmpty())
            .andExpect(jsonPath("$.alertas").isEmpty());
    }

    @Test
    @DisplayName("Debe incluir alertas cuando hay facturas vencidas")
    void debeIncluirAlertas() throws Exception {
        // Arrange
        FacturaAcueducto facturaVencida = FacturaAcueducto.builder()
            .id(new FacturaId(2L))
            .clienteId(clienteId)
            .periodo(Periodo.of("202412"))
            .consumo(ConsumoAgua.of(20))
            .valorPagar(Dinero.of(new BigDecimal("120000.00")))
            .fechaVencimiento(LocalDate.now().minusDays(10))
            .fechaCreacion(LocalDateTime.now())
            .fechaActualizacion(LocalDateTime.now())
            .estado(EstadoFactura.VENCIDA)
            .build();

        DeudaConsolidada deudaConAlertas = DeudaConsolidada.construir(
            clienteId,
            List.of(facturaVencida),
            Collections.emptyList()
        );

        when(consultarDeudaUseCase.consultarDeudaConsolidada(any(ClienteId.class)))
            .thenReturn(deudaConAlertas);

        // Act & Assert
        mockMvc.perform(get("/api/deuda/cliente/1234567890"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.alertas").isArray())
            .andExpect(jsonPath("$.alertas", not(empty())))
            .andExpect(jsonPath("$.alertas[0]", containsString("vencida")));
    }

    @Test
    @DisplayName("Debe serializar estadísticas correctamente")
    void debeSerializarEstadisticas() throws Exception {
        // Arrange
        when(consultarDeudaUseCase.consultarDeudaConsolidada(any(ClienteId.class)))
            .thenReturn(deudaMock);

        // Act & Assert
        mockMvc.perform(get("/api/deuda/cliente/1234567890"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estadisticas").exists())
            .andExpect(jsonPath("$.estadisticas.totalFacturasAcueducto").value(1))
            .andExpect(jsonPath("$.estadisticas.totalConsumosEnergia").value(1))
            .andExpect(jsonPath("$.estadisticas.deudaAcumuladaAcueducto").exists())
            .andExpect(jsonPath("$.estadisticas.deudaAcumuladaEnergia").exists());
    }

    @Test
    @DisplayName("Debe retornar Content-Type application/json")
    void debeRetornarContentTypeJson() throws Exception {
        // Arrange
        when(consultarDeudaUseCase.consultarDeudaConsolidada(any(ClienteId.class)))
            .thenReturn(deudaMock);

        // Act & Assert
        mockMvc.perform(get("/api/deuda/cliente/1234567890"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
