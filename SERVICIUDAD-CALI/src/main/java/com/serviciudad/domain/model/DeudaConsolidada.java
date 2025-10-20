package com.serviciudad.domain.model;

import com.serviciudad.domain.valueobject.ClienteId;
import com.serviciudad.domain.valueobject.Dinero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class DeudaConsolidada {
    
    private ClienteId clienteId;
    private LocalDateTime fechaConsulta;
    private List<FacturaAcueducto> facturasAcueducto;
    private List<ConsumoEnergiaModel> consumosEnergia;
    private Dinero deudaTotalAcueducto;
    private Dinero deudaTotalEnergia;
    private Dinero totalGeneral;
    private List<String> alertas;
    private EstadisticasDeuda estadisticas;

    public static DeudaConsolidada construir(
            ClienteId clienteId, 
            List<FacturaAcueducto> facturasAcueducto,
            List<ConsumoEnergiaModel> consumosEnergia) {
        
        Dinero deudaAcueducto = calcularDeudaAcueducto(facturasAcueducto);
        Dinero deudaEnergia = calcularDeudaEnergia(consumosEnergia);
        Dinero total = deudaAcueducto.sumar(deudaEnergia);
        
        List<String> alertas = generarAlertas(facturasAcueducto);
        EstadisticasDeuda estadisticas = calcularEstadisticas(
            facturasAcueducto, 
            consumosEnergia,
            deudaAcueducto,
            deudaEnergia
        );

        return DeudaConsolidada.builder()
                .clienteId(clienteId)
                .fechaConsulta(LocalDateTime.now())
                .facturasAcueducto(facturasAcueducto)
                .consumosEnergia(consumosEnergia)
                .deudaTotalAcueducto(deudaAcueducto)
                .deudaTotalEnergia(deudaEnergia)
                .totalGeneral(total)
                .alertas(alertas)
                .estadisticas(estadisticas)
                .build();
    }

    private static Dinero calcularDeudaAcueducto(List<FacturaAcueducto> facturas) {
        return facturas.stream()
                .filter(f -> !f.isPagada())
                .map(FacturaAcueducto::getValorPagar)
                .reduce(Dinero.cero(), Dinero::sumar);
    }

    private static Dinero calcularDeudaEnergia(List<ConsumoEnergiaModel> consumos) {
        return consumos.stream()
                .map(ConsumoEnergiaModel::getValorPagar)
                .reduce(Dinero.cero(), Dinero::sumar);
    }

    private static List<String> generarAlertas(List<FacturaAcueducto> facturas) {
        List<String> alertas = new ArrayList<>();
        
        long facturasVencidas = facturas.stream()
                .filter(FacturaAcueducto::isVencida)
                .count();
        
        if (facturasVencidas > 0) {
            alertas.add("Tiene " + facturasVencidas + " factura(s) vencida(s)");
        }
        
        long facturasProximasVencer = facturas.stream()
                .filter(f -> f.isPendiente() && f.diasHastaVencimiento() <= 5 && f.diasHastaVencimiento() > 0)
                .count();
        
        if (facturasProximasVencer > 0) {
            alertas.add("Tiene " + facturasProximasVencer + " factura(s) proxima(s) a vencer");
        }
        
        return alertas;
    }

    private static EstadisticasDeuda calcularEstadisticas(
            List<FacturaAcueducto> facturasAcueducto,
            List<ConsumoEnergiaModel> consumosEnergia,
            Dinero deudaAcueducto,
            Dinero deudaEnergia) {
        
        int totalFacturasAcueducto = facturasAcueducto.size();
        int facturasVencidas = (int) facturasAcueducto.stream()
                .filter(FacturaAcueducto::isVencida)
                .count();
        int facturasPendientes = (int) facturasAcueducto.stream()
                .filter(FacturaAcueducto::isPendiente)
                .count();
        
        double promedioConsumoAcueducto = facturasAcueducto.stream()
                .mapToInt(f -> f.getConsumo().getMetrosCubicos())
                .average()
                .orElse(0.0);
        
        int totalConsumosEnergia = consumosEnergia.size();
        double promedioConsumoEnergia = consumosEnergia.stream()
                .mapToInt(c -> c.getConsumo().getKilovatiosHora())
                .average()
                .orElse(0.0);
        
        return EstadisticasDeuda.builder()
                .totalFacturasAcueducto(totalFacturasAcueducto)
                .facturasVencidas(facturasVencidas)
                .facturasPendientes(facturasPendientes)
                .promedioConsumoAcueducto(promedioConsumoAcueducto)
                .deudaAcumuladaAcueducto(deudaAcueducto)
                .totalConsumosEnergia(totalConsumosEnergia)
                .promedioConsumoEnergia(promedioConsumoEnergia)
                .deudaAcumuladaEnergia(deudaEnergia)
                .build();
    }

    public boolean tieneDeuda() {
        return !totalGeneral.esCero();
    }

    public boolean tieneFacturasVencidas() {
        return facturasAcueducto.stream().anyMatch(FacturaAcueducto::isVencida);
    }

    public List<FacturaAcueducto> getFacturasVencidas() {
        return facturasAcueducto.stream()
                .filter(FacturaAcueducto::isVencida)
                .collect(Collectors.toList());
    }
}
