package com.serviciudad.infrastructure.adapter.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviciudad.application.dto.request.RegistrarPagoRequest;
import com.serviciudad.domain.model.EstadoFactura;
import com.serviciudad.domain.model.FacturaAcueducto;
import com.serviciudad.domain.port.input.GestionarFacturaUseCase;
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
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitarios para FacturaRestController.
 * 
 * Usa @WebMvcTest para cargar solo la capa web (controller + MockMvc).
 * Mockea el use case para aislar la lógica del controller.
 * 
 * @author Equipo ServiCiudad Cali
 * @version 1.0
 */
@WebMvcTest(FacturaRestController.class)
@Import(TestSecurityConfig.class)
@DisplayName("REST Controller: Factura - Tests Unitarios")
class FacturaRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GestionarFacturaUseCase gestionarFacturaUseCase;

    private FacturaAcueducto factura;
    private FacturaId facturaId;
    private ClienteId clienteId;
    private Periodo periodo;

    @BeforeEach
    void setUp() {
        facturaId = FacturaId.of(1L);
        clienteId = ClienteId.of("1234567890");
        periodo = Periodo.of("202501");

        factura = FacturaAcueducto.builder()
            .id(facturaId)
            .clienteId(clienteId)
            .periodo(periodo)
            .consumo(ConsumoAgua.of(15))
            .valorPagar(Dinero.of(new BigDecimal("95000.00")))
            .estado(EstadoFactura.PENDIENTE)
            .fechaVencimiento(LocalDate.of(2025, 1, 31))
            .fechaCreacion(LocalDateTime.of(2025, 1, 1, 10, 0))
            .fechaActualizacion(LocalDateTime.of(2025, 1, 1, 10, 0))
            .build();
    }

    @Test
    @DisplayName("GET /api/facturas/{facturaId} - Debe consultar factura por ID exitosamente")
    void debeConsultarFacturaPorId() throws Exception {
        // Arrange
        when(gestionarFacturaUseCase.consultarFactura(any(FacturaId.class)))
            .thenReturn(factura);

        // Act & Assert
        mockMvc.perform(get("/api/facturas/{facturaId}", 1L)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.clienteId").value("1234567890"))
            .andExpect(jsonPath("$.periodo").value("202501"))
            .andExpect(jsonPath("$.consumo").value(15))
            .andExpect(jsonPath("$.valorPagar").value(95000.00))
            .andExpect(jsonPath("$.estado").value("PENDIENTE"))
            .andExpect(jsonPath("$.fechaVencimiento").value("2025-01-31"))
            .andExpect(jsonPath("$.vencida").isBoolean())
            .andExpect(jsonPath("$.diasHastaVencimiento").isNumber());

        verify(gestionarFacturaUseCase, times(1)).consultarFactura(any(FacturaId.class));
    }

    @Test
    @DisplayName("GET /api/facturas/cliente/{clienteId} - Debe listar facturas por cliente")
    void debeListarFacturasPorCliente() throws Exception {
        // Arrange
        FacturaAcueducto factura2 = FacturaAcueducto.builder()
            .id(FacturaId.of(2L))
            .clienteId(clienteId)
            .periodo(Periodo.of("202502"))
            .consumo(ConsumoAgua.of(18))
            .valorPagar(Dinero.of(new BigDecimal("115000.00")))
            .estado(EstadoFactura.PAGADA)
            .fechaVencimiento(LocalDate.of(2025, 2, 28))
            .fechaCreacion(LocalDateTime.now())
            .fechaActualizacion(LocalDateTime.now())
            .build();

        when(gestionarFacturaUseCase.consultarFacturasPorCliente(any(ClienteId.class)))
            .thenReturn(List.of(factura, factura2));

        // Act & Assert
        mockMvc.perform(get("/api/facturas/cliente/{clienteId}", "1234567890")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].periodo").value("202501"))
            .andExpect(jsonPath("$[0].estado").value("PENDIENTE"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].periodo").value("202502"))
            .andExpect(jsonPath("$[1].estado").value("PAGADA"));

        verify(gestionarFacturaUseCase, times(1)).consultarFacturasPorCliente(any(ClienteId.class));
    }

    @Test
    @DisplayName("GET /api/facturas/cliente/{clienteId} - Debe retornar lista vacía cuando no hay facturas")
    void debeRetornarListaVaciaCuandoNoHayFacturas() throws Exception {
        // Arrange
        when(gestionarFacturaUseCase.consultarFacturasPorCliente(any(ClienteId.class)))
            .thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/facturas/cliente/{clienteId}", "9999999999")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/facturas/cliente/{clienteId}/periodo/{periodo} - Debe consultar facturas por periodo")
    void debeConsultarFacturasPorPeriodo() throws Exception {
        // Arrange
        when(gestionarFacturaUseCase.consultarFacturasPorPeriodo(any(ClienteId.class), any(Periodo.class)))
            .thenReturn(List.of(factura));

        // Act & Assert
        mockMvc.perform(get("/api/facturas/cliente/{clienteId}/periodo/{periodo}", "1234567890", "202501")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].periodo").value("202501"))
            .andExpect(jsonPath("$[0].clienteId").value("1234567890"));

        verify(gestionarFacturaUseCase, times(1))
            .consultarFacturasPorPeriodo(any(ClienteId.class), any(Periodo.class));
    }

    @Test
    @DisplayName("POST /api/facturas/pagar - Debe registrar pago de factura exitosamente")
    void debeRegistrarPagoExitosamente() throws Exception {
        // Arrange
        RegistrarPagoRequest request = new RegistrarPagoRequest(1L);
        
        doNothing().when(gestionarFacturaUseCase).registrarPagoFactura(any(FacturaId.class));

        // Act & Assert
        mockMvc.perform(post("/api/facturas/pagar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());

        verify(gestionarFacturaUseCase, times(1)).registrarPagoFactura(any(FacturaId.class));
    }

    @Test
    @DisplayName("POST /api/facturas/pagar - Debe validar facturaId no nulo")
    void debeValidarFacturaIdNoNulo() throws Exception {
        // Arrange
        RegistrarPagoRequest request = new RegistrarPagoRequest(null);

        // Act & Assert
        mockMvc.perform(post("/api/facturas/pagar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(gestionarFacturaUseCase, never()).registrarPagoFactura(any(FacturaId.class));
    }

    @Test
    @DisplayName("POST /api/facturas/pagar - Debe validar facturaId positivo")
    void debeValidarFacturaIdPositivo() throws Exception {
        // Arrange
        RegistrarPagoRequest request = new RegistrarPagoRequest(-1L);

        // Act & Assert
        mockMvc.perform(post("/api/facturas/pagar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(gestionarFacturaUseCase, never()).registrarPagoFactura(any(FacturaId.class));
    }

    @Test
    @DisplayName("POST /api/facturas/{facturaId}/anular - Debe anular factura exitosamente")
    void debeAnularFacturaExitosamente() throws Exception {
        // Arrange
        doNothing().when(gestionarFacturaUseCase).anularFactura(any(FacturaId.class));

        // Act & Assert
        mockMvc.perform(post("/api/facturas/{facturaId}/anular", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(gestionarFacturaUseCase, times(1)).anularFactura(any(FacturaId.class));
    }

    @Test
    @DisplayName("POST /api/facturas/marcar-vencidas - Debe marcar facturas vencidas")
    void debeMarcarFacturasVencidas() throws Exception {
        // Arrange
        doNothing().when(gestionarFacturaUseCase).marcarFacturasVencidas();

        // Act & Assert
        mockMvc.perform(post("/api/facturas/marcar-vencidas")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(gestionarFacturaUseCase, times(1)).marcarFacturasVencidas();
    }

    @Test
    @DisplayName("GET /api/facturas/{facturaId} - Debe incluir información de vencimiento")
    void debeIncluirInformacionVencimiento() throws Exception {
        // Arrange
        when(gestionarFacturaUseCase.consultarFactura(any(FacturaId.class)))
            .thenReturn(factura);

        // Act & Assert
        mockMvc.perform(get("/api/facturas/{facturaId}", 1L)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fechaVencimiento").exists())
            .andExpect(jsonPath("$.vencida").exists())
            .andExpect(jsonPath("$.diasHastaVencimiento").exists())
            .andExpect(jsonPath("$.fechaCreacion").exists())
            .andExpect(jsonPath("$.fechaActualizacion").exists());
    }
}
