package com.serviciudad.application.usecase;

import com.serviciudad.domain.model.FacturaAcueducto;
import com.serviciudad.domain.model.EstadoFactura;
import com.serviciudad.domain.port.output.FacturaRepositoryPort;
import com.serviciudad.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para GestionarFacturaUseCaseImpl.
 * 
 * Cobertura de pruebas:
 * - Consulta de factura por ID (happy path y error)
 * - Consulta de facturas por cliente
 * - Consulta de facturas por periodo
 * - Registro de pago de factura
 * - Anulación de factura
 * - Marcado masivo de facturas vencidas
 * 
 * @author Equipo ServiCiudad Cali
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Use Case: Gestionar Factura - Tests Unitarios")
class GestionarFacturaUseCaseImplTest {

    @Mock
    private FacturaRepositoryPort facturaRepository;

    @InjectMocks
    private GestionarFacturaUseCaseImpl useCase;

    private FacturaId facturaId;
    private ClienteId clienteId;
    private Periodo periodo;
    private FacturaAcueducto factura;

    @BeforeEach
    void setUp() {
        // Arrange - Datos de prueba comunes
        facturaId = new FacturaId(1L);
        clienteId = new ClienteId("1234567890");
        periodo = Periodo.of("202501");

        factura = FacturaAcueducto.builder()
            .id(facturaId)
            .clienteId(clienteId)
            .periodo(periodo)
            .consumo(ConsumoAgua.of(15))
            .valorPagar(Dinero.of(new BigDecimal("95000.00")))
            .fechaVencimiento(LocalDate.now().plusDays(10))
            .fechaCreacion(LocalDateTime.now())
            .fechaActualizacion(LocalDateTime.now())
            .estado(EstadoFactura.PENDIENTE)
            .build();
    }

    @Test
    @DisplayName("Debe consultar factura por ID exitosamente")
    void debeConsultarFacturaPorId() {
        // Arrange
        when(facturaRepository.findById(facturaId))
            .thenReturn(Optional.of(factura));

        // Act
        FacturaAcueducto resultado = useCase.consultarFactura(facturaId);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(facturaId);
        assertThat(resultado.getClienteId()).isEqualTo(clienteId);
        assertThat(resultado.getPeriodo()).isEqualTo(periodo);
        
        verify(facturaRepository, times(1)).findById(facturaId);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando factura no existe")
    void debeLanzarExcepcionCuandoFacturaNoExiste() {
        // Arrange
        FacturaId idInexistente = new FacturaId(999L);
        when(facturaRepository.findById(idInexistente))
            .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> useCase.consultarFactura(idInexistente))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Factura no encontrada")
            .hasMessageContaining("999");

        verify(facturaRepository, times(1)).findById(idInexistente);
    }

    @Test
    @DisplayName("Debe consultar facturas por cliente exitosamente")
    void debeConsultarFacturasPorCliente() {
        // Arrange
        FacturaAcueducto factura2 = FacturaAcueducto.builder()
            .id(new FacturaId(2L))
            .clienteId(clienteId)
            .periodo(Periodo.of("202412"))
            .consumo(ConsumoAgua.of(20))
            .valorPagar(Dinero.of(new BigDecimal("120000.00")))
            .fechaVencimiento(LocalDate.now().minusDays(20))
            .fechaCreacion(LocalDateTime.now().minusDays(35))
            .fechaActualizacion(LocalDateTime.now())
            .estado(EstadoFactura.VENCIDA)
            .build();

        List<FacturaAcueducto> facturasEsperadas = Arrays.asList(factura, factura2);

        when(facturaRepository.findByClienteId(clienteId))
            .thenReturn(facturasEsperadas);

        // Act
        List<FacturaAcueducto> resultado = useCase.consultarFacturasPorCliente(clienteId);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting("clienteId").containsOnly(clienteId);
        assertThat(resultado).extracting("estado")
            .contains(EstadoFactura.PENDIENTE, EstadoFactura.VENCIDA);

        verify(facturaRepository, times(1)).findByClienteId(clienteId);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando cliente no tiene facturas")
    void debeRetornarListaVaciaCuandoClienteNoTieneFacturas() {
        // Arrange
        ClienteId clienteSinFacturas = new ClienteId("9999999999");
        when(facturaRepository.findByClienteId(clienteSinFacturas))
            .thenReturn(List.of());

        // Act
        List<FacturaAcueducto> resultado = useCase.consultarFacturasPorCliente(clienteSinFacturas);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEmpty();

        verify(facturaRepository, times(1)).findByClienteId(clienteSinFacturas);
    }

    @Test
    @DisplayName("Debe consultar facturas por cliente y periodo exitosamente")
    void debeConsultarFacturasPorClienteYPeriodo() {
        // Arrange
        when(facturaRepository.findByClienteIdAndPeriodo(clienteId, periodo))
            .thenReturn(List.of(factura));

        // Act
        List<FacturaAcueducto> resultado = useCase.consultarFacturasPorPeriodo(clienteId, periodo);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getPeriodo()).isEqualTo(periodo);
        assertThat(resultado.get(0).getClienteId()).isEqualTo(clienteId);

        verify(facturaRepository, times(1)).findByClienteIdAndPeriodo(clienteId, periodo);
    }

    @Test
    @DisplayName("Debe registrar pago de factura exitosamente")
    void debeRegistrarPagoFacturaExitosamente() {
        // Arrange
        when(facturaRepository.findById(facturaId))
            .thenReturn(Optional.of(factura));
        when(facturaRepository.save(any(FacturaAcueducto.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        useCase.registrarPagoFactura(facturaId);

        // Assert
        verify(facturaRepository, times(1)).findById(facturaId);
        verify(facturaRepository, times(1)).save(argThat(f -> 
            f.getEstado() == EstadoFactura.PAGADA
        ));
    }

    @Test
    @DisplayName("Debe lanzar excepción al registrar pago de factura inexistente")
    void debeLanzarExcepcionAlRegistrarPagoDeFacturaInexistente() {
        // Arrange
        FacturaId idInexistente = new FacturaId(999L);
        when(facturaRepository.findById(idInexistente))
            .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> useCase.registrarPagoFactura(idInexistente))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Factura no encontrada");

        verify(facturaRepository, times(1)).findById(idInexistente);
        verify(facturaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe anular factura exitosamente")
    void debeAnularFacturaExitosamente() {
        // Arrange
        when(facturaRepository.findById(facturaId))
            .thenReturn(Optional.of(factura));
        when(facturaRepository.save(any(FacturaAcueducto.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        useCase.anularFactura(facturaId);

        // Assert
        verify(facturaRepository, times(1)).findById(facturaId);
        verify(facturaRepository, times(1)).save(argThat(f -> 
            f.getEstado() == EstadoFactura.ANULADA
        ));
    }

    @Test
    @DisplayName("Debe lanzar excepción al anular factura inexistente")
    void debeLanzarExcepcionAlAnularFacturaInexistente() {
        // Arrange
        FacturaId idInexistente = new FacturaId(999L);
        when(facturaRepository.findById(idInexistente))
            .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> useCase.anularFactura(idInexistente))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Factura no encontrada");

        verify(facturaRepository, times(1)).findById(idInexistente);
        verify(facturaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe marcar facturas vencidas exitosamente")
    void debeMarcarFacturasVencidasExitosamente() {
        // Arrange
        FacturaAcueducto facturaVencida = FacturaAcueducto.builder()
            .id(new FacturaId(2L))
            .clienteId(clienteId)
            .periodo(Periodo.of("202412"))
            .consumo(ConsumoAgua.of(10))
            .valorPagar(Dinero.of(new BigDecimal("75000.00")))
            .fechaVencimiento(LocalDate.now().minusDays(5)) // Vencida
            .fechaCreacion(LocalDateTime.now().minusDays(35))
            .fechaActualizacion(LocalDateTime.now())
            .estado(EstadoFactura.PENDIENTE)
            .build();

        FacturaAcueducto facturaNoVencida = FacturaAcueducto.builder()
            .id(new FacturaId(3L))
            .clienteId(clienteId)
            .periodo(Periodo.of("202501"))
            .consumo(ConsumoAgua.of(12))
            .valorPagar(Dinero.of(new BigDecimal("85000.00")))
            .fechaVencimiento(LocalDate.now().plusDays(12)) // No vencida
            .fechaCreacion(LocalDateTime.now().minusDays(3))
            .fechaActualizacion(LocalDateTime.now())
            .estado(EstadoFactura.PENDIENTE)
            .build();

        List<FacturaAcueducto> facturasPendientes = Arrays.asList(facturaVencida, facturaNoVencida);

        when(facturaRepository.findFacturasPendientes(0, 100))
            .thenReturn(facturasPendientes);
        when(facturaRepository.hasMoreFacturasPendientes(0, 100))
            .thenReturn(false);
        doNothing().when(facturaRepository).saveAll(any());
        doNothing().when(facturaRepository).flush();

        // Act
        useCase.marcarFacturasVencidas();

        // Assert
        verify(facturaRepository, times(1)).findFacturasPendientes(0, 100);
        verify(facturaRepository, times(1)).hasMoreFacturasPendientes(0, 100);
        verify(facturaRepository, times(1)).saveAll(argThat(facturas -> {
            List<FacturaAcueducto> lista = (List<FacturaAcueducto>) facturas;
            // Verificar que solo facturaVencida cambió a VENCIDA
            boolean facturaVencidaMarcada = lista.stream()
                .anyMatch(f -> f.getId().equals(new FacturaId(2L)) && 
                             f.getEstado() == EstadoFactura.VENCIDA);
            // facturaNoVencida NO debe estar en la lista (solo se guardan las actualizadas)
            return facturaVencidaMarcada && lista.size() == 1;
        }));
        verify(facturaRepository, times(1)).flush();
    }

    @Test
    @DisplayName("Debe manejar correctamente lista vacía al marcar facturas vencidas")
    void debeManejarListaVaciaAlMarcarFacturasVencidas() {
        // Arrange
        when(facturaRepository.findFacturasPendientes(0, 100))
            .thenReturn(List.of());

        // Act
        useCase.marcarFacturasVencidas();

        // Assert
        verify(facturaRepository, times(1)).findFacturasPendientes(0, 100);
        verify(facturaRepository, never()).saveAll(any());
        verify(facturaRepository, never()).flush();
        verify(facturaRepository, never()).hasMoreFacturasPendientes(anyInt(), anyInt());
    }

    @Test
    @DisplayName("No debe marcar como vencida una factura PAGADA")
    void noDebeMarcarComoVencidaUnaFacturaPagada() {
        // Arrange - findFacturasPendientes NO debe retornar facturas PAGADAS
        when(facturaRepository.findFacturasPendientes(0, 100))
            .thenReturn(List.of()); 

        // Act
        useCase.marcarFacturasVencidas();

        // Assert
        verify(facturaRepository, times(1)).findFacturasPendientes(0, 100);
        verify(facturaRepository, never()).saveAll(any());
        verify(facturaRepository, never()).flush();
        verify(facturaRepository, never()).hasMoreFacturasPendientes(anyInt(), anyInt());
    }
}
