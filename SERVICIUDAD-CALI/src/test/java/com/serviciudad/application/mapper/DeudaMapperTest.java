package com.serviciudad.application.mapper;

import com.serviciudad.application.dto.response.DeudaConsolidadaResponse;
import com.serviciudad.application.dto.response.FacturaResponse;
import com.serviciudad.domain.model.*;
import com.serviciudad.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para DeudaMapper.
 * 
 * Verifica transformaciones Domain → DTO correctamente.
 * 
 * @author Equipo ServiCiudad Cali
 * @version 1.0
 */
@DisplayName("Mapper: Deuda - Tests Unitarios")
class DeudaMapperTest {

    private DeudaConsolidada deuda;
    private ClienteId clienteId;

    @BeforeEach
    void setUp() {
        clienteId = new ClienteId("1234567890");

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

        ConsumoEnergiaModel consumo = ConsumoEnergiaModel.builder()
            .clienteId(clienteId)
            .periodo(Periodo.of("202501"))
            .consumo(ConsumoEnergia.of(350))
            .valorPagar(Dinero.of(new BigDecimal("180000.00")))
            .fechaLectura(LocalDate.now())
            .estrato("3")
            .build();

        deuda = DeudaConsolidada.construir(clienteId, List.of(factura), List.of(consumo));
    }

    @Test
    @DisplayName("Debe mapear DeudaConsolidada a Response correctamente")
    void debeMapearDeudaConsolidadaAResponse() {
        // Act
        DeudaConsolidadaResponse response = DeudaMapper.toResponse(deuda);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getClienteId()).isEqualTo("1234567890");
        assertThat(response.getTotalGeneral()).isEqualByComparingTo(new BigDecimal("275000.00"));
        assertThat(response.getDeudaTotalAcueducto()).isEqualByComparingTo(new BigDecimal("95000.00"));
        assertThat(response.getDeudaTotalEnergia()).isEqualByComparingTo(new BigDecimal("180000.00"));
        assertThat(response.getFechaConsulta()).isNotNull();
    }

    @Test
    @DisplayName("Debe mapear lista de facturas correctamente")
    void debeMapearListaFacturas() {
        // Act
        DeudaConsolidadaResponse response = DeudaMapper.toResponse(deuda);

        // Assert
        assertThat(response.getFacturasAcueducto()).hasSize(1);
        FacturaResponse facturaResp = response.getFacturasAcueducto().get(0);
        assertThat(facturaResp.getId()).isEqualTo(1L);
        assertThat(facturaResp.getPeriodo()).isEqualTo("202501");
        assertThat(facturaResp.getEstado()).isEqualTo("PENDIENTE");
    }

    @Test
    @DisplayName("Debe mapear deuda vacía correctamente")
    void debeMapearDeudaVacia() {
        // Arrange
        DeudaConsolidada deudaVacia = DeudaConsolidada.construir(
            clienteId,
            Collections.emptyList(),
            Collections.emptyList()
        );

        // Act
        DeudaConsolidadaResponse response = DeudaMapper.toResponse(deudaVacia);

        // Assert
        assertThat(response.getTotalGeneral()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.getFacturasAcueducto()).isEmpty();
        assertThat(response.getConsumosEnergia()).isEmpty();
    }
}
