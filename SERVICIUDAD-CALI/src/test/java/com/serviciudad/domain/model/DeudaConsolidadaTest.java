package com.serviciudad.domain.model;

import com.serviciudad.domain.valueobject.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para el modelo DeudaConsolidada (Aggregate).
 * 
 * Verifica:
 * - Construcción del agregado con cálculos
 * - Generación de alertas según estado de facturas
 * - Cálculo de estadísticas
 * - Consultas de deuda y facturas vencidas
 * 
 * @author Equipo ServiCiudad Cali
 * @version 1.0
 */
@DisplayName("Domain Model: DeudaConsolidada (Aggregate) - Tests Unitarios")
class DeudaConsolidadaTest {
    
    @Test
    @DisplayName("Debe construir deuda consolidada vacía sin facturas ni consumos")
    void debeConstruirDeudaVacia() {
        // Arrange
        ClienteId clienteId = new ClienteId("1234567890");
        List<FacturaAcueducto> facturasVacias = new ArrayList<>();
        List<ConsumoEnergiaModel> consumosVacios = new ArrayList<>();
        
        // Act
        DeudaConsolidada deuda = DeudaConsolidada.construir(clienteId, facturasVacias, consumosVacios);
        
        // Assert
        assertThat(deuda).isNotNull();
        assertThat(deuda.tieneDeuda()).isFalse();
        assertThat(deuda.tieneFacturasVencidas()).isFalse();
        assertThat(deuda.getTotalGeneral()).isEqualTo(Dinero.cero());
        assertThat(deuda.getAlertas()).isEmpty();
    }
    
    @Test
    @DisplayName("Debe calcular deuda total de acueducto con facturas pendientes")
    void debeCalcularDeudaAcueducto() {
        // Arrange
        ClienteId clienteId = new ClienteId("1234567890");
        List<FacturaAcueducto> facturas = crearFacturasAcueducto();
        List<ConsumoEnergiaModel> consumosVacios = new ArrayList<>();
        
        // Act
        DeudaConsolidada deuda = DeudaConsolidada.construir(clienteId, facturas, consumosVacios);
        
        // Assert
        assertThat(deuda.tieneDeuda()).isTrue();
        assertThat(deuda.getDeudaTotalAcueducto()).isEqualTo(Dinero.of(new BigDecimal("200000.00")));
    }
    
    @Test
    @DisplayName("Debe calcular deuda total de energía con consumos pendientes")
    void debeCalcularDeudaEnergia() {
        // Arrange
        ClienteId clienteId = new ClienteId("1234567890");
        List<FacturaAcueducto> facturasVacias = new ArrayList<>();
        List<ConsumoEnergiaModel> consumos = crearConsumosEnergia();
        
        // Act
        DeudaConsolidada deuda = DeudaConsolidada.construir(clienteId, facturasVacias, consumos);
        
        // Assert
        assertThat(deuda.tieneDeuda()).isTrue();
        assertThat(deuda.getDeudaTotalEnergia()).isEqualTo(Dinero.of(new BigDecimal("500000.00")));
    }
    
    @Test
    @DisplayName("Debe calcular deuda total consolidada")
    void debeCalcularDeudaTotalConsolidada() {
        // Arrange
        ClienteId clienteId = new ClienteId("1234567890");
        List<FacturaAcueducto> facturas = crearFacturasAcueducto();
        List<ConsumoEnergiaModel> consumos = crearConsumosEnergia();
        
        // Act
        DeudaConsolidada deuda = DeudaConsolidada.construir(clienteId, facturas, consumos);
        
        // Assert
        Dinero esperado = Dinero.of(new BigDecimal("700000.00"));
        assertThat(deuda.getTotalGeneral()).isEqualTo(esperado);
    }
    
    @Test
    @DisplayName("Debe detectar facturas vencidas correctamente")
    void debeDetectarFacturasVencidas() {
        // Arrange
        ClienteId clienteId = new ClienteId("1234567890");
        List<FacturaAcueducto> facturas = crearFacturasConVencidas();
        List<ConsumoEnergiaModel> consumosVacios = new ArrayList<>();
        
        // Act
        DeudaConsolidada deuda = DeudaConsolidada.construir(clienteId, facturas, consumosVacios);
        
        // Assert
        assertThat(deuda.tieneFacturasVencidas()).isTrue();
        assertThat(deuda.getFacturasVencidas()).hasSize(2);
    }
    
    @Test
    @DisplayName("Debe generar alertas para facturas vencidas")
    void debeGenerarAlertasParaFacturasVencidas() {
        // Arrange
        ClienteId clienteId = new ClienteId("1234567890");
        List<FacturaAcueducto> facturas = crearFacturasConVencidas();
        List<ConsumoEnergiaModel> consumosVacios = new ArrayList<>();
        
        // Act
        DeudaConsolidada deuda = DeudaConsolidada.construir(clienteId, facturas, consumosVacios);
        
        // Assert
        assertThat(deuda.getAlertas()).isNotEmpty();
        assertThat(deuda.getAlertas()).anyMatch(alerta -> alerta.contains("vencida"));
    }
    
    @Test
    @DisplayName("Debe calcular estadísticas correctamente")
    void debeCalcularEstadisticas() {
        // Arrange
        ClienteId clienteId = new ClienteId("1234567890");
        List<FacturaAcueducto> facturas = crearFacturasConVencidas();
        List<ConsumoEnergiaModel> consumos = crearConsumosEnergia();
        
        // Act
        DeudaConsolidada deuda = DeudaConsolidada.construir(clienteId, facturas, consumos);
        
        // Assert
        assertThat(deuda.getEstadisticas()).isNotNull();
        assertThat(deuda.getEstadisticas().getTotalFacturasAcueducto()).isEqualTo(3);
        assertThat(deuda.getEstadisticas().getTotalConsumosEnergia()).isEqualTo(2);
    }
    
    @Test
    @DisplayName("Debe excluir facturas pagadas del cálculo de deuda")
    void debeExcluirFacturasPagadas() {
        // Arrange
        ClienteId clienteId = new ClienteId("1234567890");
        List<FacturaAcueducto> facturas = new ArrayList<>();
        facturas.add(crearFacturaPendiente(1L, "100000.00"));
        
        FacturaAcueducto facturaPagada = crearFacturaPendiente(2L, "200000.00");
        facturaPagada.registrarPago();
        facturas.add(facturaPagada);
        
        List<ConsumoEnergiaModel> consumosVacios = new ArrayList<>();
        
        // Act
        DeudaConsolidada deuda = DeudaConsolidada.construir(clienteId, facturas, consumosVacios);
        
        // Assert
        assertThat(deuda.getDeudaTotalAcueducto()).isEqualTo(Dinero.of(new BigDecimal("100000.00")));
    }
    
    @Test
    @DisplayName("Facturas anuladas se incluyen en cálculo de deuda (comportamiento actual)")
    void facturasAnuladasSeIncluyenEnDeuda() {
        // Arrange
        ClienteId clienteId = new ClienteId("1234567890");
        List<FacturaAcueducto> facturas = new ArrayList<>();
        facturas.add(crearFacturaPendiente(1L, "100000.00"));
        
        FacturaAcueducto facturaAnulada = crearFacturaPendiente(2L, "300000.00");
        facturaAnulada.anular();
        facturas.add(facturaAnulada);
        
        List<ConsumoEnergiaModel> consumosVacios = new ArrayList<>();
        
        // Act
        DeudaConsolidada deuda = DeudaConsolidada.construir(clienteId, facturas, consumosVacios);
        
        // Assert
        // Nota: El sistema actual incluye facturas anuladas en el cálculo de deuda
        // porque no están marcadas como "pagadas"
        assertThat(deuda.getDeudaTotalAcueducto()).isEqualTo(Dinero.of(new BigDecimal("400000.00")));
    }
    
    // ==== Métodos auxiliares de creación de datos de prueba ====
    
    private List<FacturaAcueducto> crearFacturasAcueducto() {
        List<FacturaAcueducto> facturas = new ArrayList<>();
        facturas.add(crearFacturaPendiente(1L, "100000.00"));
        facturas.add(crearFacturaPendiente(2L, "100000.00"));
        return facturas;
    }
    
    private List<FacturaAcueducto> crearFacturasConVencidas() {
        List<FacturaAcueducto> facturas = new ArrayList<>();
        facturas.add(crearFacturaVencida(1L, "100000.00", LocalDate.now().minusDays(10)));
        facturas.add(crearFacturaPendiente(2L, "100000.00"));
        facturas.add(crearFacturaVencida(3L, "150000.00", LocalDate.now().minusDays(5)));
        return facturas;
    }
    
    private FacturaAcueducto crearFacturaPendiente(Long id, String valor) {
        return FacturaAcueducto.builder()
                .id(FacturaId.of(id))
                .clienteId(new ClienteId("1234567890"))
                .periodo(Periodo.of("202501"))
                .consumo(ConsumoAgua.of(25))
                .valorPagar(Dinero.of(new BigDecimal(valor)))
                .estado(EstadoFactura.PENDIENTE)
                .fechaVencimiento(LocalDate.now().plusDays(10))
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
    }
    
    private FacturaAcueducto crearFacturaVencida(Long id, String valor, LocalDate fechaVencimiento) {
        return FacturaAcueducto.builder()
                .id(FacturaId.of(id))
                .clienteId(new ClienteId("1234567890"))
                .periodo(Periodo.of("202412"))
                .consumo(ConsumoAgua.of(30))
                .valorPagar(Dinero.of(new BigDecimal(valor)))
                .estado(EstadoFactura.VENCIDA)
                .fechaVencimiento(fechaVencimiento)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
    }
    
    private List<ConsumoEnergiaModel> crearConsumosEnergia() {
        List<ConsumoEnergiaModel> consumos = new ArrayList<>();
        consumos.add(ConsumoEnergiaModel.builder()
                .clienteId(new ClienteId("1234567890"))
                .periodo(Periodo.of("202501"))
                .consumo(ConsumoEnergia.of(350))
                .valorPagar(Dinero.of(new BigDecimal("250000.00")))
                .fechaLectura(LocalDate.now())
                .estrato("3")
                .build());
        
        consumos.add(ConsumoEnergiaModel.builder()
                .clienteId(new ClienteId("1234567890"))
                .periodo(Periodo.of("202412"))
                .consumo(ConsumoEnergia.of(400))
                .valorPagar(Dinero.of(new BigDecimal("250000.00")))
                .fechaLectura(LocalDate.now())
                .estrato("3")
                .build());
        
        return consumos;
    }
}
