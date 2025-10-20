package com.serviciudad.application.usecase;

import com.serviciudad.domain.model.ConsumoEnergiaModel;
import com.serviciudad.domain.model.DeudaConsolidada;
import com.serviciudad.domain.model.EstadoFactura;
import com.serviciudad.domain.model.FacturaAcueducto;
import com.serviciudad.domain.port.output.ConsumoEnergiaReaderPort;
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
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ConsultarDeudaUseCaseImpl.
 * 
 * Cobertura de pruebas:
 * - Consulta de deuda consolidada con facturas y consumos
 * - Cliente sin deudas
 * - Cliente con facturas vencidas
 * - Cliente solo con facturas pagadas
 * - Cliente con alertas de vencimiento
 * - Cálculo correcto de estadísticas
 * - Manejo de listas vacías
 * - Validación de cálculos monetarios
 * 
 * @author Equipo ServiCiudad Cali
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Use Case: Consultar Deuda - Tests Unitarios")
class ConsultarDeudaUseCaseImplTest {

    @Mock
    private FacturaRepositoryPort facturaRepository;

    @Mock
    private ConsumoEnergiaReaderPort consumoEnergiaReader;

    @InjectMocks
    private ConsultarDeudaUseCaseImpl useCase;

    private ClienteId clienteId;
    private FacturaAcueducto facturaPendiente;
    private FacturaAcueducto facturaVencida;
    private FacturaAcueducto facturaPagada;
    private ConsumoEnergiaModel consumoEnergia1;
    private ConsumoEnergiaModel consumoEnergia2;

    @BeforeEach
    void setUp() {
        clienteId = new ClienteId("1234567890");

        // Factura pendiente
        facturaPendiente = FacturaAcueducto.builder()
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

        // Factura vencida
        facturaVencida = FacturaAcueducto.builder()
            .id(new FacturaId(2L))
            .clienteId(clienteId)
            .periodo(Periodo.of("202412"))
            .consumo(ConsumoAgua.of(20))
            .valorPagar(Dinero.of(new BigDecimal("120000.00")))
            .fechaVencimiento(LocalDate.now().minusDays(10))
            .fechaCreacion(LocalDateTime.now().minusDays(40))
            .fechaActualizacion(LocalDateTime.now())
            .estado(EstadoFactura.VENCIDA)
            .build();

        // Factura pagada
        facturaPagada = FacturaAcueducto.builder()
            .id(new FacturaId(3L))
            .clienteId(clienteId)
            .periodo(Periodo.of("202411"))
            .consumo(ConsumoAgua.of(18))
            .valorPagar(Dinero.of(new BigDecimal("105000.00")))
            .fechaVencimiento(LocalDate.now().minusDays(30))
            .fechaCreacion(LocalDateTime.now().minusDays(60))
            .fechaActualizacion(LocalDateTime.now().minusDays(25))
            .estado(EstadoFactura.PAGADA)
            .build();

        // Consumos de energía
        consumoEnergia1 = ConsumoEnergiaModel.builder()
            .clienteId(clienteId)
            .periodo(Periodo.of("202501"))
            .consumo(ConsumoEnergia.of(350))
            .valorPagar(Dinero.of(new BigDecimal("180000.00")))
            .fechaLectura(LocalDate.now().minusDays(5))
            .estrato("3")
            .build();

        consumoEnergia2 = ConsumoEnergiaModel.builder()
            .clienteId(clienteId)
            .periodo(Periodo.of("202412"))
            .consumo(ConsumoEnergia.of(420))
            .valorPagar(Dinero.of(new BigDecimal("215000.00")))
            .fechaLectura(LocalDate.now().minusDays(35))
            .estrato("3")
            .build();
    }

    @Test
    @DisplayName("Debe consultar deuda consolidada exitosamente")
    void debeConsultarDeudaConsolidada() {
        // Arrange
        List<FacturaAcueducto> facturas = Arrays.asList(facturaPendiente, facturaVencida, facturaPagada);
        List<ConsumoEnergiaModel> consumos = Arrays.asList(consumoEnergia1, consumoEnergia2);

        when(facturaRepository.findByClienteId(clienteId)).thenReturn(facturas);
        when(consumoEnergiaReader.findByClienteId(clienteId)).thenReturn(consumos);

        // Act
        DeudaConsolidada resultado = useCase.consultarDeudaConsolidada(clienteId);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getClienteId()).isEqualTo(clienteId);
        assertThat(resultado.getFacturasAcueducto()).hasSize(3);
        assertThat(resultado.getConsumosEnergia()).hasSize(2);
        
        // Verificar cálculo de deuda acueducto (solo pendientes y vencidas)
        BigDecimal deudaAcueductoEsperada = new BigDecimal("95000.00")
                .add(new BigDecimal("120000.00"));
        assertThat(resultado.getDeudaTotalAcueducto().getMonto())
                .isEqualByComparingTo(deudaAcueductoEsperada);
        
        // Verificar cálculo de deuda energía
        BigDecimal deudaEnergiaEsperada = new BigDecimal("180000.00")
                .add(new BigDecimal("215000.00"));
        assertThat(resultado.getDeudaTotalEnergia().getMonto())
                .isEqualByComparingTo(deudaEnergiaEsperada);
        
        // Verificar total general
        BigDecimal totalEsperado = deudaAcueductoEsperada.add(deudaEnergiaEsperada);
        assertThat(resultado.getTotalGeneral().getMonto())
                .isEqualByComparingTo(totalEsperado);
        
        assertThat(resultado.tieneDeuda()).isTrue();
        assertThat(resultado.tieneFacturasVencidas()).isTrue();
        
        verify(facturaRepository, times(1)).findByClienteId(clienteId);
        verify(consumoEnergiaReader, times(1)).findByClienteId(clienteId);
    }

    @Test
    @DisplayName("Debe retornar deuda cero cuando no hay facturas pendientes")
    void debeRetornarDeudaCeroSinFacturasPendientes() {
        // Arrange
        List<FacturaAcueducto> facturas = List.of(facturaPagada);
        List<ConsumoEnergiaModel> consumos = Collections.emptyList();

        when(facturaRepository.findByClienteId(clienteId)).thenReturn(facturas);
        when(consumoEnergiaReader.findByClienteId(clienteId)).thenReturn(consumos);

        // Act
        DeudaConsolidada resultado = useCase.consultarDeudaConsolidada(clienteId);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getDeudaTotalAcueducto().esCero()).isTrue();
        assertThat(resultado.getDeudaTotalEnergia().esCero()).isTrue();
        assertThat(resultado.getTotalGeneral().esCero()).isTrue();
        assertThat(resultado.tieneDeuda()).isFalse();
        assertThat(resultado.tieneFacturasVencidas()).isFalse();
    }

    @Test
    @DisplayName("Debe manejar cliente sin historial de servicios")
    void debeManejarClienteSinHistorial() {
        // Arrange
        when(facturaRepository.findByClienteId(clienteId)).thenReturn(Collections.emptyList());
        when(consumoEnergiaReader.findByClienteId(clienteId)).thenReturn(Collections.emptyList());

        // Act
        DeudaConsolidada resultado = useCase.consultarDeudaConsolidada(clienteId);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getFacturasAcueducto()).isEmpty();
        assertThat(resultado.getConsumosEnergia()).isEmpty();
        assertThat(resultado.getTotalGeneral().esCero()).isTrue();
        assertThat(resultado.tieneDeuda()).isFalse();
        assertThat(resultado.getAlertas()).isEmpty();
    }

    @Test
    @DisplayName("Debe identificar facturas vencidas correctamente")
    void debeIdentificarFacturasVencidas() {
        // Arrange
        List<FacturaAcueducto> facturas = Arrays.asList(facturaPendiente, facturaVencida);
        List<ConsumoEnergiaModel> consumos = Collections.emptyList();

        when(facturaRepository.findByClienteId(clienteId)).thenReturn(facturas);
        when(consumoEnergiaReader.findByClienteId(clienteId)).thenReturn(consumos);

        // Act
        DeudaConsolidada resultado = useCase.consultarDeudaConsolidada(clienteId);

        // Assert
        assertThat(resultado.tieneFacturasVencidas()).isTrue();
        assertThat(resultado.getFacturasVencidas()).hasSize(1);
        assertThat(resultado.getFacturasVencidas().get(0).getId()).isEqualTo(facturaVencida.getId());
        assertThat(resultado.getAlertas())
                .anyMatch(alerta -> alerta.contains("vencida"));
    }

    @Test
    @DisplayName("Debe generar alertas para facturas próximas a vencer")
    void debeGenerarAlertasFacturasProximasVencer() {
        // Arrange - Factura que vence en 3 días
        FacturaAcueducto facturaProximaVencer = FacturaAcueducto.builder()
            .id(new FacturaId(4L))
            .clienteId(clienteId)
            .periodo(Periodo.of("202502"))
            .consumo(ConsumoAgua.of(12))
            .valorPagar(Dinero.of(new BigDecimal("80000.00")))
            .fechaVencimiento(LocalDate.now().plusDays(3))
            .fechaCreacion(LocalDateTime.now())
            .fechaActualizacion(LocalDateTime.now())
            .estado(EstadoFactura.PENDIENTE)
            .build();

        List<FacturaAcueducto> facturas = List.of(facturaProximaVencer);
        List<ConsumoEnergiaModel> consumos = Collections.emptyList();

        when(facturaRepository.findByClienteId(clienteId)).thenReturn(facturas);
        when(consumoEnergiaReader.findByClienteId(clienteId)).thenReturn(consumos);

        // Act
        DeudaConsolidada resultado = useCase.consultarDeudaConsolidada(clienteId);

        // Assert
        assertThat(resultado.getAlertas())
                .anyMatch(alerta -> alerta.contains("proxima") && alerta.contains("vencer"));
    }

    @Test
    @DisplayName("Debe calcular estadísticas correctamente")
    void debeCalcularEstadisticasCorrectamente() {
        // Arrange
        List<FacturaAcueducto> facturas = Arrays.asList(facturaPendiente, facturaVencida, facturaPagada);
        List<ConsumoEnergiaModel> consumos = Arrays.asList(consumoEnergia1, consumoEnergia2);

        when(facturaRepository.findByClienteId(clienteId)).thenReturn(facturas);
        when(consumoEnergiaReader.findByClienteId(clienteId)).thenReturn(consumos);

        // Act
        DeudaConsolidada resultado = useCase.consultarDeudaConsolidada(clienteId);

        // Assert
        assertThat(resultado.getEstadisticas()).isNotNull();
        assertThat(resultado.getEstadisticas().getTotalFacturasAcueducto()).isEqualTo(3);
        assertThat(resultado.getEstadisticas().getFacturasVencidas()).isEqualTo(1);
        assertThat(resultado.getEstadisticas().getFacturasPendientes()).isEqualTo(1);
        assertThat(resultado.getEstadisticas().getTotalConsumosEnergia()).isEqualTo(2);
        
        // Promedio consumo acueducto: (15 + 20 + 18) / 3 = 17.67
        assertThat(resultado.getEstadisticas().getPromedioConsumoAcueducto())
                .isCloseTo(17.67, within(0.01));
        
        // Promedio consumo energía: (350 + 420) / 2 = 385.0
        assertThat(resultado.getEstadisticas().getPromedioConsumoEnergia())
                .isCloseTo(385.0, within(0.01));
    }

    @Test
    @DisplayName("Debe calcular deuda solo de facturas NO pagadas")
    void debeCalcularDeudaSoloFacturasNoPagadas() {
        // Arrange
        List<FacturaAcueducto> facturas = Arrays.asList(facturaPendiente, facturaPagada);
        List<ConsumoEnergiaModel> consumos = Collections.emptyList();

        when(facturaRepository.findByClienteId(clienteId)).thenReturn(facturas);
        when(consumoEnergiaReader.findByClienteId(clienteId)).thenReturn(consumos);

        // Act
        DeudaConsolidada resultado = useCase.consultarDeudaConsolidada(clienteId);

        // Assert
        // Solo debe contar la factura pendiente, NO la pagada
        assertThat(resultado.getDeudaTotalAcueducto().getMonto())
                .isEqualByComparingTo(new BigDecimal("95000.00"));
        
        // Verificar que la factura pagada está en la lista pero no suma a la deuda
        assertThat(resultado.getFacturasAcueducto()).hasSize(2);
    }

    @Test
    @DisplayName("Debe manejar correctamente fechaConsulta")
    void debeManejarFechaConsulta() {
        // Arrange
        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        
        when(facturaRepository.findByClienteId(clienteId)).thenReturn(Collections.emptyList());
        when(consumoEnergiaReader.findByClienteId(clienteId)).thenReturn(Collections.emptyList());

        // Act
        DeudaConsolidada resultado = useCase.consultarDeudaConsolidada(clienteId);

        // Assert
        LocalDateTime despues = LocalDateTime.now().plusSeconds(1);
        assertThat(resultado.getFechaConsulta())
                .isAfter(antes)
                .isBefore(despues);
    }
}
