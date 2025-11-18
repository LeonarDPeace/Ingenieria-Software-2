package com.serviciudad.domain.model;

import com.serviciudad.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para el modelo FacturaAcueducto.
 * 
 * Verifica:
 * - Lógica de negocio de estados (Pagada, Vencida, Pendiente, Anulada)
 * - Transiciones de estado válidas
 * - Cálculos de vencimiento
 * - Reglas de validación
 * 
 * @author Equipo ServiCiudad Cali
 * @version 1.0
 */
@DisplayName("Domain Model: FacturaAcueducto - Tests Unitarios")
class FacturaAcueductoTest {
    
    private FacturaAcueducto factura;
    
    @BeforeEach
    void setUp() {
        factura = FacturaAcueducto.builder()
                .id(FacturaId.of(1L))
                .clienteId(new ClienteId("1234567890"))
                .periodo(Periodo.of("202501"))
                .consumo(ConsumoAgua.of(25))
                .valorPagar(Dinero.of(new BigDecimal("100000.00")))
                .estado(EstadoFactura.PENDIENTE)
                .fechaVencimiento(LocalDate.now().plusDays(10))
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
    }
    
    @Test
    @DisplayName("Debe detectar factura vencida cuando fecha vencimiento es pasada")
    void debeDetectarFacturaVencida() {
        // Arrange
        FacturaAcueducto facturaVencida = FacturaAcueducto.builder()
                .id(FacturaId.of(2L))
                .clienteId(new ClienteId("1234567890"))
                .periodo(Periodo.of("202412"))
                .consumo(ConsumoAgua.of(20))
                .valorPagar(Dinero.of(new BigDecimal("50000.00")))
                .estado(EstadoFactura.PENDIENTE)
                .fechaVencimiento(LocalDate.now().minusDays(5))
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
        
        // Act & Assert
        assertThat(facturaVencida.estaVencida()).isTrue();
    }
    
    @Test
    @DisplayName("No debe detectar como vencida una factura sin fecha vencimiento")
    void noDebeDetectarVencidaSinFecha() {
        // Arrange
        FacturaAcueducto facturaSinFecha = FacturaAcueducto.builder()
                .id(FacturaId.of(3L))
                .clienteId(new ClienteId("1234567890"))
                .periodo(Periodo.of("202501"))
                .consumo(ConsumoAgua.of(20))
                .valorPagar(Dinero.of(new BigDecimal("50000.00")))
                .estado(EstadoFactura.PENDIENTE)
                .fechaVencimiento(null)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
        
        // Act & Assert
        assertThat(facturaSinFecha.estaVencida()).isFalse();
    }
    
    @Test
    @DisplayName("Factura pagada no debe estar vencida aunque fecha sea pasada")
    void facturaPagadaNoDebeEstarVencida() {
        // Arrange
        FacturaAcueducto facturaPagada = FacturaAcueducto.builder()
                .id(FacturaId.of(4L))
                .clienteId(new ClienteId("1234567890"))
                .periodo(Periodo.of("202412"))
                .consumo(ConsumoAgua.of(20))
                .valorPagar(Dinero.of(new BigDecimal("50000.00")))
                .estado(EstadoFactura.PAGADA)
                .fechaVencimiento(LocalDate.now().minusDays(5))
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
        
        // Act & Assert
        assertThat(facturaPagada.estaVencida()).isFalse();
    }
    
    @Test
    @DisplayName("Debe registrar pago correctamente")
    void debeRegistrarPagoCorrectamente() {
        // Arrange & Act
        factura.registrarPago();
        
        // Assert
        assertThat(factura.getEstado()).isEqualTo(EstadoFactura.PAGADA);
        assertThat(factura.isPagada()).isTrue();
        assertThat(factura.getFechaActualizacion()).isNotNull();
    }
    
    @Test
    @DisplayName("No debe poder pagar factura anulada")
    void noDebePagarFacturaAnulada() {
        // Arrange
        factura.anular();
        
        // Act & Assert
        assertThatThrownBy(() -> factura.registrarPago())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("No se puede pagar una factura anulada");
    }
    
    @Test
    @DisplayName("Debe anular factura correctamente")
    void debeAnularFacturaCorrectamente() {
        // Act
        factura.anular();
        
        // Assert
        assertThat(factura.getEstado()).isEqualTo(EstadoFactura.ANULADA);
    }
    
    @Test
    @DisplayName("No debe poder anular factura pagada")
    void noDebeAnularFacturaPagada() {
        // Arrange
        factura.registrarPago();
        
        // Act & Assert
        assertThatThrownBy(() -> factura.anular())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("No se puede anular una factura pagada");
    }
    
    @Test
    @DisplayName("Debe actualizar valor de factura pendiente")
    void debeActualizarValorFacturaPendiente() {
        // Arrange
        Dinero nuevoValor = Dinero.of(new BigDecimal("150000.00"));
        
        // Act
        factura.actualizarValor(nuevoValor);
        
        // Assert
        assertThat(factura.getValorPagar()).isEqualTo(nuevoValor);
    }
    
    @Test
    @DisplayName("No debe actualizar valor de factura pagada")
    void noDebeActualizarValorFacturaPagada() {
        // Arrange
        factura.registrarPago();
        Dinero nuevoValor = Dinero.of(new BigDecimal("150000.00"));
        
        // Act & Assert
        assertThatThrownBy(() -> factura.actualizarValor(nuevoValor))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("No se puede modificar una factura pagada");
    }
    
    @Test
    @DisplayName("No debe actualizar con valor negativo")
    void noDebeActualizarConValorNegativo() {
        // Arrange
        Dinero valorNegativo = Dinero.of(new BigDecimal("-50000.00"));
        
        // Act & Assert
        assertThatThrownBy(() -> factura.actualizarValor(valorNegativo))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("El valor no puede ser negativo");
    }
    
    @Test
    @DisplayName("Debe calcular días hasta vencimiento correctamente")
    void debeCalcularDiasHastaVencimiento() {
        // Arrange
        FacturaAcueducto facturaConVencimiento = FacturaAcueducto.builder()
                .id(FacturaId.of(5L))
                .clienteId(new ClienteId("1234567890"))
                .periodo(Periodo.of("202501"))
                .consumo(ConsumoAgua.of(20))
                .valorPagar(Dinero.of(new BigDecimal("50000.00")))
                .estado(EstadoFactura.PENDIENTE)
                .fechaVencimiento(LocalDate.now().plusDays(7))
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
        
        // Act
        long dias = facturaConVencimiento.diasHastaVencimiento();
        
        // Assert
        assertThat(dias).isEqualTo(7);
    }
    
    @Test
    @DisplayName("Debe retornar 0 días si no hay fecha vencimiento")
    void debeRetornarCeroDiasSinFechaVencimiento() {
        // Arrange
        FacturaAcueducto facturaSinFecha = FacturaAcueducto.builder()
                .id(FacturaId.of(6L))
                .clienteId(new ClienteId("1234567890"))
                .periodo(Periodo.of("202501"))
                .consumo(ConsumoAgua.of(20))
                .valorPagar(Dinero.of(new BigDecimal("50000.00")))
                .estado(EstadoFactura.PENDIENTE)
                .fechaVencimiento(null)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
        
        // Act
        long dias = facturaSinFecha.diasHastaVencimiento();
        
        // Assert
        assertThat(dias).isEqualTo(0);
    }
    
    @Test
    @DisplayName("Debe marcar factura pendiente como vencida")
    void debeMarcarFacturaPendienteComoVencida() {
        // Arrange
        FacturaAcueducto facturaVenciendo = FacturaAcueducto.builder()
                .id(FacturaId.of(7L))
                .clienteId(new ClienteId("1234567890"))
                .periodo(Periodo.of("202412"))
                .consumo(ConsumoAgua.of(20))
                .valorPagar(Dinero.of(new BigDecimal("50000.00")))
                .estado(EstadoFactura.PENDIENTE)
                .fechaVencimiento(LocalDate.now().minusDays(1))
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
        
        // Act
        facturaVenciendo.marcarComoVencida();
        
        // Assert
        assertThat(facturaVenciendo.isVencida()).isTrue();
        assertThat(facturaVenciendo.getEstado()).isEqualTo(EstadoFactura.VENCIDA);
    }
    
    @Test
    @DisplayName("No debe marcar como vencida si no cumple condiciones")
    void noDebeMarcarComoVencidaSiNoCumpleCondiciones() {
        // Arrange - Factura pendiente pero no vencida
        FacturaAcueducto facturaNormal = FacturaAcueducto.builder()
                .id(FacturaId.of(8L))
                .clienteId(new ClienteId("1234567890"))
                .periodo(Periodo.of("202501"))
                .consumo(ConsumoAgua.of(20))
                .valorPagar(Dinero.of(new BigDecimal("50000.00")))
                .estado(EstadoFactura.PENDIENTE)
                .fechaVencimiento(LocalDate.now().plusDays(5))
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
        
        // Act
        facturaNormal.marcarComoVencida();
        
        // Assert
        assertThat(facturaNormal.isPendiente()).isTrue();
        assertThat(facturaNormal.isVencida()).isFalse();
    }
    
    @Test
    @DisplayName("Debe identificar correctamente estados isPagada, isVencida, isPendiente")
    void debeIdentificarEstadosCorrectamente() {
        // Arrange
        FacturaAcueducto facturaPendiente = FacturaAcueducto.builder()
                .id(FacturaId.of(9L))
                .clienteId(new ClienteId("1234567890"))
                .periodo(Periodo.of("202501"))
                .consumo(ConsumoAgua.of(20))
                .valorPagar(Dinero.of(new BigDecimal("50000.00")))
                .estado(EstadoFactura.PENDIENTE)
                .fechaVencimiento(LocalDate.now().plusDays(5))
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
        
        FacturaAcueducto facturaPagada = FacturaAcueducto.builder()
                .id(FacturaId.of(10L))
                .clienteId(new ClienteId("1234567890"))
                .periodo(Periodo.of("202501"))
                .consumo(ConsumoAgua.of(20))
                .valorPagar(Dinero.of(new BigDecimal("50000.00")))
                .estado(EstadoFactura.PAGADA)
                .fechaVencimiento(LocalDate.now().plusDays(5))
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
        
        FacturaAcueducto facturaVencida = FacturaAcueducto.builder()
                .id(FacturaId.of(11L))
                .clienteId(new ClienteId("1234567890"))
                .periodo(Periodo.of("202501"))
                .consumo(ConsumoAgua.of(20))
                .valorPagar(Dinero.of(new BigDecimal("50000.00")))
                .estado(EstadoFactura.VENCIDA)
                .fechaVencimiento(LocalDate.now().minusDays(5))
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
        
        // Act & Assert
        assertThat(facturaPendiente.isPendiente()).isTrue();
        assertThat(facturaPendiente.isPagada()).isFalse();
        assertThat(facturaPendiente.isVencida()).isFalse();
        
        assertThat(facturaPagada.isPagada()).isTrue();
        assertThat(facturaPagada.isPendiente()).isFalse();
        assertThat(facturaPagada.isVencida()).isFalse();
        
        assertThat(facturaVencida.isVencida()).isTrue();
        assertThat(facturaVencida.isPagada()).isFalse();
        assertThat(facturaVencida.isPendiente()).isFalse();
    }
}
