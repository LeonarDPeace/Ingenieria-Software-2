package com.serviciudad.application.mapper;

import com.serviciudad.application.dto.response.ConsumoEnergiaResponse;
import com.serviciudad.application.dto.response.DeudaConsolidadaResponse;
import com.serviciudad.application.dto.response.EstadisticasResponse;
import com.serviciudad.application.dto.response.FacturaResponse;
import com.serviciudad.domain.model.ConsumoEnergiaModel;
import com.serviciudad.domain.model.DeudaConsolidada;
import com.serviciudad.domain.model.EstadisticasDeuda;
import com.serviciudad.domain.model.FacturaAcueducto;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class DeudaMapper {

    public static DeudaConsolidadaResponse toResponse(DeudaConsolidada deuda) {
        return DeudaConsolidadaResponse.builder()
            .clienteId(deuda.getClienteId().getValor())
            .fechaConsulta(deuda.getFechaConsulta())
            .deudaTotalAcueducto(deuda.getDeudaTotalAcueducto().getMonto())
            .deudaTotalEnergia(deuda.getDeudaTotalEnergia().getMonto())
            .totalGeneral(deuda.getTotalGeneral().getMonto())
            .facturasAcueducto(toFacturaResponseList(deuda.getFacturasAcueducto()))
            .consumosEnergia(toConsumoEnergiaResponseList(deuda.getConsumosEnergia()))
            .alertas(deuda.getAlertas())
            .estadisticas(toEstadisticasResponse(deuda.getEstadisticas()))
            .build();
    }

    private static List<FacturaResponse> toFacturaResponseList(List<FacturaAcueducto> facturas) {
        return facturas.stream()
            .map(DeudaMapper::toFacturaResponse)
            .collect(Collectors.toList());
    }

    private static FacturaResponse toFacturaResponse(FacturaAcueducto factura) {
        return FacturaResponse.builder()
            .id(factura.getId().getValor())
            .clienteId(factura.getClienteId().getValor())
            .periodo(factura.getPeriodo().getValor())
            .consumo(BigDecimal.valueOf(factura.getConsumo().getMetrosCubicos()))
            .valorPagar(factura.getValorPagar().getMonto())
            .estado(factura.getEstado().name())
            .fechaVencimiento(factura.getFechaVencimiento())
            .fechaCreacion(factura.getFechaCreacion())
            .fechaActualizacion(factura.getFechaActualizacion())
            .vencida(factura.isVencida())
            .diasHastaVencimiento(factura.diasHastaVencimiento())
            .build();
    }

    private static List<ConsumoEnergiaResponse> toConsumoEnergiaResponseList(List<ConsumoEnergiaModel> consumos) {
        return consumos.stream()
            .map(DeudaMapper::toConsumoEnergiaResponse)
            .collect(Collectors.toList());
    }

    private static ConsumoEnergiaResponse toConsumoEnergiaResponse(ConsumoEnergiaModel consumo) {
        return ConsumoEnergiaResponse.builder()
            .clienteId(consumo.getClienteId().getValor())
            .periodo(consumo.getPeriodo().getValor())
            .consumo(BigDecimal.valueOf(consumo.getConsumo().getKilovatiosHora()))
            .valorPagar(consumo.getValorPagar().getMonto())
            .fechaLectura(consumo.getFechaLectura())
            .estrato(consumo.getEstrato())
            .consumoAlto(consumo.tieneConsumoAlto())
            .consumoBajo(consumo.tieneConsumoBajo())
            .build();
    }

    private static EstadisticasResponse toEstadisticasResponse(EstadisticasDeuda estadisticas) {
        return EstadisticasResponse.builder()
            .totalFacturasAcueducto(estadisticas.getTotalFacturasAcueducto())
            .facturasVencidas(estadisticas.getFacturasVencidas())
            .facturasPendientes(estadisticas.getFacturasPendientes())
            .promedioConsumoAcueducto(estadisticas.getPromedioConsumoAcueducto())
            .deudaAcumuladaAcueducto(estadisticas.getDeudaAcumuladaAcueducto().getMonto())
            .totalConsumosEnergia(estadisticas.getTotalConsumosEnergia())
            .promedioConsumoEnergia(estadisticas.getPromedioConsumoEnergia())
            .deudaAcumuladaEnergia(estadisticas.getDeudaAcumuladaEnergia().getMonto())
            .porcentajeFacturasVencidas(estadisticas.porcentajeFacturasVencidas())
            .tieneDeudaSignificativa(estadisticas.tieneDeudaSignificativa())
            .tieneConsumoAcueductoElevado(estadisticas.tieneConsumoAcueductoElevado())
            .tieneConsumoEnergiaElevado(estadisticas.tieneConsumoEnergiaElevado())
            .build();
    }
}
