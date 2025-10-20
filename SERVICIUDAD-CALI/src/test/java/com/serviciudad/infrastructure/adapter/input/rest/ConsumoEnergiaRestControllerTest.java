package com.serviciudad.infrastructure.adapter.input.rest;

import com.serviciudad.domain.model.ConsumoEnergiaModel;
import com.serviciudad.domain.port.input.ConsultarConsumoEnergiaUseCase;
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
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitarios para ConsumoEnergiaRestController.
 * 
 * Usa @WebMvcTest para cargar solo la capa web (controller + MockMvc).
 * Mockea el use case para aislar la lógica del controller.
 * 
 * @author Equipo ServiCiudad Cali
 * @version 1.0
 */
@WebMvcTest(ConsumoEnergiaRestController.class)
@Import(TestSecurityConfig.class)
@DisplayName("REST Controller: ConsumoEnergia - Tests Unitarios")
class ConsumoEnergiaRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConsultarConsumoEnergiaUseCase consultarConsumoEnergiaUseCase;

    private ConsumoEnergiaModel consumo1;
    private ConsumoEnergiaModel consumo2;
    private ConsumoEnergiaModel consumoElevado;
    private ClienteId clienteId;

    @BeforeEach
    void setUp() {
        clienteId = ClienteId.of("1234567890");

        consumo1 = ConsumoEnergiaModel.builder()
            .clienteId(clienteId)
            .periodo(Periodo.of("202501"))
            .consumo(ConsumoEnergia.of(350))
            .valorPagar(Dinero.of(new BigDecimal("180000.00")))
            .fechaLectura(LocalDate.of(2025, 1, 15))
            .estrato("3")
            .build();

        consumo2 = ConsumoEnergiaModel.builder()
            .clienteId(clienteId)
            .periodo(Periodo.of("202502"))
            .consumo(ConsumoEnergia.of(320))
            .valorPagar(Dinero.of(new BigDecimal("165000.00")))
            .fechaLectura(LocalDate.of(2025, 2, 15))
            .estrato("3")
            .build();

        consumoElevado = ConsumoEnergiaModel.builder()
            .clienteId(clienteId)
            .periodo(Periodo.of("202503"))
            .consumo(ConsumoEnergia.of(850)) // Consumo muy alto
            .valorPagar(Dinero.of(new BigDecimal("450000.00")))
            .fechaLectura(LocalDate.of(2025, 3, 15))
            .estrato("3")
            .build();
    }

    @Test
    @DisplayName("GET /api/consumos-energia/cliente/{clienteId} - Debe consultar consumos por cliente")
    void debeConsultarConsumosPorCliente() throws Exception {
        // Arrange
        when(consultarConsumoEnergiaUseCase.consultarConsumosPorCliente(any(ClienteId.class)))
            .thenReturn(List.of(consumo1, consumo2));

        // Act & Assert
        mockMvc.perform(get("/api/consumos-energia/cliente/{clienteId}", "1234567890")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].clienteId").value("1234567890"))
            .andExpect(jsonPath("$[0].periodo").value("202501"))
            .andExpect(jsonPath("$[0].consumo").value(350))
            .andExpect(jsonPath("$[0].valorPagar").value(180000.00))
            .andExpect(jsonPath("$[0].estrato").value("3"))
            .andExpect(jsonPath("$[1].periodo").value("202502"))
            .andExpect(jsonPath("$[1].consumo").value(320));

        verify(consultarConsumoEnergiaUseCase, times(1))
            .consultarConsumosPorCliente(any(ClienteId.class));
    }

    @Test
    @DisplayName("GET /api/consumos-energia/cliente/{clienteId} - Debe retornar lista vacía sin consumos")
    void debeRetornarListaVaciaSinConsumos() throws Exception {
        // Arrange
        when(consultarConsumoEnergiaUseCase.consultarConsumosPorCliente(any(ClienteId.class)))
            .thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/consumos-energia/cliente/{clienteId}", "9999999999")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/consumos-energia/cliente/{clienteId}/periodo/{periodo} - Debe consultar por periodo")
    void debeConsultarConsumosPorPeriodo() throws Exception {
        // Arrange
        when(consultarConsumoEnergiaUseCase.consultarConsumosPorPeriodo(
            any(ClienteId.class), any(Periodo.class)))
            .thenReturn(List.of(consumo1));

        // Act & Assert
        mockMvc.perform(get("/api/consumos-energia/cliente/{clienteId}/periodo/{periodo}",
                "1234567890", "202501")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].periodo").value("202501"))
            .andExpect(jsonPath("$[0].consumo").value(350));

        verify(consultarConsumoEnergiaUseCase, times(1))
            .consultarConsumosPorPeriodo(any(ClienteId.class), any(Periodo.class));
    }

    @Test
    @DisplayName("GET /api/consumos-energia/cliente/{clienteId}/elevados - Debe consultar consumos elevados")
    void debeConsultarConsumosElevados() throws Exception {
        // Arrange
        when(consultarConsumoEnergiaUseCase.consultarConsumosElevados(any(ClienteId.class)))
            .thenReturn(List.of(consumoElevado));

        // Act & Assert
        mockMvc.perform(get("/api/consumos-energia/cliente/{clienteId}/elevados", "1234567890")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].consumo").value(850))
            .andExpect(jsonPath("$[0].valorPagar").value(450000.00));

        verify(consultarConsumoEnergiaUseCase, times(1))
            .consultarConsumosElevados(any(ClienteId.class));
    }

    @Test
    @DisplayName("Debe incluir indicadores de consumo alto/bajo en respuesta")
    void debeIncluirIndicadoresConsumo() throws Exception {
        // Arrange
        when(consultarConsumoEnergiaUseCase.consultarConsumosPorCliente(any(ClienteId.class)))
            .thenReturn(List.of(consumo1));

        // Act & Assert
        mockMvc.perform(get("/api/consumos-energia/cliente/{clienteId}", "1234567890")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].consumoAlto").isBoolean())
            .andExpect(jsonPath("$[0].consumoBajo").isBoolean());
    }

    @Test
    @DisplayName("Debe incluir todos los campos requeridos en la respuesta")
    void debeIncluirTodosCamposRequeridos() throws Exception {
        // Arrange
        when(consultarConsumoEnergiaUseCase.consultarConsumosPorCliente(any(ClienteId.class)))
            .thenReturn(List.of(consumo1));

        // Act & Assert
        mockMvc.perform(get("/api/consumos-energia/cliente/{clienteId}", "1234567890")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].clienteId").exists())
            .andExpect(jsonPath("$[0].periodo").exists())
            .andExpect(jsonPath("$[0].consumo").exists())
            .andExpect(jsonPath("$[0].valorPagar").exists())
            .andExpect(jsonPath("$[0].fechaLectura").exists())
            .andExpect(jsonPath("$[0].estrato").exists())
            .andExpect(jsonPath("$[0].consumoAlto").exists())
            .andExpect(jsonPath("$[0].consumoBajo").exists());
    }

    @Test
    @DisplayName("Debe retornar Content-Type JSON correctamente")
    void debeRetornarContentTypeJson() throws Exception {
        // Arrange
        when(consultarConsumoEnergiaUseCase.consultarConsumosPorCliente(any(ClienteId.class)))
            .thenReturn(List.of(consumo1));

        // Act & Assert
        mockMvc.perform(get("/api/consumos-energia/cliente/{clienteId}", "1234567890"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", containsString("application/json")));
    }

    @Test
    @DisplayName("Debe serializar fechas en formato ISO-8601")
    void debeSerializarFechasCorrectamente() throws Exception {
        // Arrange
        when(consultarConsumoEnergiaUseCase.consultarConsumosPorCliente(any(ClienteId.class)))
            .thenReturn(List.of(consumo1));

        // Act & Assert
        mockMvc.perform(get("/api/consumos-energia/cliente/{clienteId}", "1234567890")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].fechaLectura").value("2025-01-15"));
    }
}
