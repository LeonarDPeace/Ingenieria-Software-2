package com.serviciudad.service;

import com.serviciudad.domain.ConsumoEnergia;
import com.serviciudad.domain.FacturaAcueducto;
import com.serviciudad.dto.DeudaConsolidada;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("DeudaConsolidadaBuilder - Tests Unitarios")
class DeudaConsolidadaBuilderTest {

    private DeudaConsolidadaBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new DeudaConsolidadaBuilder();
    }

    @Test
    @DisplayName("Debe construir DeudaConsolidada con todos los campos")
    void debeConstruirDeudaConsolidadaCompleta() {
        String clienteId = "0001234567";
        String nombreCliente = "Juan Perez";
        List<FacturaAcueducto> facturas = crearFacturasEjemplo();
        List<ConsumoEnergia> consumos = crearConsumosEjemplo();

        DeudaConsolidada deuda = builder
            .conCliente(clienteId, nombreCliente)
            .conFacturas(facturas)
            .conConsumos(consumos)
            .calcularEstadisticas()
            .generarAlertas()
            .construir();

        assertThat(deuda).isNotNull();
        assertThat(deuda.getClienteId()).isEqualTo(clienteId);
        assertThat(deuda.getNombreCliente()).isEqualTo(nombreCliente);
        assertThat(deuda.getFechaConsulta()).isNotNull();
    }

    @Test
    @DisplayName("Debe calcular estadisticas correctamente")
    void debeCalcularEstadisticas() {
        List<FacturaAcueducto> facturas = crearFacturasEjemplo();
        List<ConsumoEnergia> consumos = crearConsumosEjemplo();

        DeudaConsolidada deuda = builder
            .conCliente("0001234567", "Juan Perez")
            .conFacturas(facturas)
            .conConsumos(consumos)
            .calcularEstadisticas()
            .construir();

        assertThat(deuda.getEstadisticas()).isNotNull();
        assertThat(deuda.getEstadisticas().getTotalFacturasAcueducto()).isEqualTo(facturas.size());
        assertThat(deuda.getEstadisticas().getDeudaAcumuladaAcueducto()).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Debe calcular total a pagar correctamente")
    void debeCalcularTotalAPagar() {
        List<FacturaAcueducto> facturas = crearFacturasEjemplo();
        List<ConsumoEnergia> consumos = crearConsumosEjemplo();

        DeudaConsolidada deuda = builder
            .conCliente("0001234567", "Juan Perez")
            .conFacturas(facturas)
            .conConsumos(consumos)
            .calcularEstadisticas()
            .construir();

        BigDecimal totalFacturas = facturas.stream()
            .map(FacturaAcueducto::getValorPagar)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalConsumos = consumos.stream()
            .map(ConsumoEnergia::getValorPagar)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalEsperado = totalFacturas.add(totalConsumos);

        assertThat(deuda.getTotalAPagar()).isEqualByComparingTo(totalEsperado);
    }

    @Test
    @DisplayName("Debe generar alerta de vencimiento proximo")
    void debeGenerarAlertaVencimientoProximo() {
        FacturaAcueducto facturaProximaVencer = new FacturaAcueducto();
        facturaProximaVencer.setIdCliente("0001234567");
        facturaProximaVencer.setFechaVencimiento(LocalDate.now().plusDays(3));
        facturaProximaVencer.setEstado("PENDIENTE");
        facturaProximaVencer.setValorPagar(new BigDecimal("95000.00"));

        DeudaConsolidada deuda = builder
            .conCliente("0001234567", "Juan Perez")
            .conFacturas(Arrays.asList(facturaProximaVencer))
            .generarAlertas()
            .construir();

        assertThat(deuda.getAlertas()).isNotEmpty();
        assertThat(deuda.getAlertas()).anyMatch(alerta -> 
            alerta.contains("VENCIMIENTO_PROXIMO") || alerta.contains("proxima"));
    }

    @Test
    @DisplayName("Debe generar alerta de facturas vencidas")
    void debeGenerarAlertaFacturasVencidas() {
        FacturaAcueducto facturaVencida = new FacturaAcueducto();
        facturaVencida.setIdCliente("0001234567");
        facturaVencida.setFechaVencimiento(LocalDate.now().minusDays(5));
        facturaVencida.setEstado("VENCIDA");
        facturaVencida.setValorPagar(new BigDecimal("95000.00"));

        DeudaConsolidada deuda = builder
            .conCliente("0001234567", "Juan Perez")
            .conFacturas(Arrays.asList(facturaVencida))
            .generarAlertas()
            .construir();

        assertThat(deuda.getAlertas()).isNotEmpty();
        assertThat(deuda.getAlertas()).anyMatch(alerta -> 
            alerta.contains("VENCIDA") || alerta.contains("vencida"));
    }

    @Test
    @DisplayName("Debe calcular promedio de consumo acueducto")
    void debeCalcularPromedioConsumoAcueducto() {
        List<FacturaAcueducto> facturas = crearFacturasEjemplo();

        DeudaConsolidada deuda = builder
            .conCliente("0001234567", "Juan Perez")
            .conFacturas(facturas)
            .calcularEstadisticas()
            .construir();

        double promedioEsperado = facturas.stream()
            .mapToInt(FacturaAcueducto::getConsumoMetrosCubicos)
            .average()
            .orElse(0.0);

        assertThat(deuda.getEstadisticas().getPromedioConsumoAcueducto())
            .isEqualTo(promedioEsperado);
    }

    @Test
    @DisplayName("Debe manejar listas vacias sin error")
    void debeManejarListasVacias() {
        DeudaConsolidada deuda = builder
            .conCliente("0001234567", "Juan Perez")
            .conFacturas(Arrays.asList())
            .conConsumos(Arrays.asList())
            .calcularEstadisticas()
            .generarAlertas()
            .construir();

        assertThat(deuda).isNotNull();
        assertThat(deuda.getTotalAPagar()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(deuda.getEstadisticas().getTotalFacturasAcueducto()).isZero();
    }

    @Test
    @DisplayName("Debe establecer fecha de consulta automaticamente")
    void debeEstablecerFechaConsulta() {
        LocalDateTime antes = LocalDateTime.now();

        DeudaConsolidada deuda = builder
            .conCliente("0001234567", "Juan Perez")
            .construir();

        LocalDateTime despues = LocalDateTime.now();

        assertThat(deuda.getFechaConsulta()).isNotNull();
        assertThat(deuda.getFechaConsulta()).isBetween(antes, despues);
    }

    @Test
    @DisplayName("Debe permitir construccion fluida")
    void debePermitirConstruccionFluida() {
        DeudaConsolidada deuda = builder
            .conCliente("0001234567", "Juan Perez")
            .conFacturas(crearFacturasEjemplo())
            .conConsumos(crearConsumosEjemplo())
            .calcularEstadisticas()
            .generarAlertas()
            .construir();

        assertThat(deuda).isNotNull();
        assertThat(deuda.getClienteId()).isNotBlank();
        assertThat(deuda.getNombreCliente()).isNotBlank();
    }

    private List<FacturaAcueducto> crearFacturasEjemplo() {
        FacturaAcueducto factura1 = new FacturaAcueducto();
        factura1.setId(1L);
        factura1.setIdCliente("0001234567");
        factura1.setNombreCliente("Juan Perez");
        factura1.setPeriodo("202510");
        factura1.setConsumoMetrosCubicos(15);
        factura1.setValorPagar(new BigDecimal("95000.00"));
        factura1.setFechaEmision(LocalDate.now());
        factura1.setFechaVencimiento(LocalDate.now().plusDays(15));
        factura1.setEstado("PENDIENTE");

        FacturaAcueducto factura2 = new FacturaAcueducto();
        factura2.setId(2L);
        factura2.setIdCliente("0001234567");
        factura2.setNombreCliente("Juan Perez");
        factura2.setPeriodo("202509");
        factura2.setConsumoMetrosCubicos(18);
        factura2.setValorPagar(new BigDecimal("108000.00"));
        factura2.setFechaEmision(LocalDate.now().minusMonths(1));
        factura2.setFechaVencimiento(LocalDate.now().minusDays(5));
        factura2.setEstado("VENCIDA");

        return Arrays.asList(factura1, factura2);
    }

    private List<ConsumoEnergia> crearConsumosEjemplo() {
        ConsumoEnergia consumo1 = new ConsumoEnergia();
        consumo1.setIdCliente("0001234567");
        consumo1.setNombreCliente("Juan Perez");
        consumo1.setPeriodo("202510");
        consumo1.setConsumoKwh(1500);
        consumo1.setValorPagar(new BigDecimal("180000.50"));
        consumo1.setFechaLectura(LocalDate.now());

        ConsumoEnergia consumo2 = new ConsumoEnergia();
        consumo2.setIdCliente("0001234567");
        consumo2.setNombreCliente("Juan Perez");
        consumo2.setPeriodo("202509");
        consumo2.setConsumoKwh(1400);
        consumo2.setValorPagar(new BigDecimal("168000.00"));
        consumo2.setFechaLectura(LocalDate.now().minusMonths(1));

        return Arrays.asList(consumo1, consumo2);
    }
}
