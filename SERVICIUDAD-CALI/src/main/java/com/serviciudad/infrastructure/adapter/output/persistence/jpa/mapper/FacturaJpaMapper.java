package com.serviciudad.infrastructure.adapter.output.persistence.jpa.mapper;

import com.serviciudad.domain.model.EstadoFactura;
import com.serviciudad.domain.model.FacturaAcueducto;
import com.serviciudad.domain.valueobject.*;
import com.serviciudad.infrastructure.adapter.output.persistence.jpa.entity.EstadoFacturaJpa;
import com.serviciudad.infrastructure.adapter.output.persistence.jpa.entity.FacturaJpaEntity;

public class FacturaJpaMapper {

    public static FacturaAcueducto toDomain(FacturaJpaEntity entity) {
        return FacturaAcueducto.builder()
            .id(FacturaId.of(entity.getId()))
            .clienteId(ClienteId.of(entity.getClienteId()))
            .periodo(Periodo.of(entity.getPeriodo()))
            .consumo(ConsumoAgua.of(entity.getConsumo().intValue()))
            .valorPagar(Dinero.of(entity.getValorPagar()))
            .estado(toEstadoFacturaDomain(entity.getEstado()))
            .fechaVencimiento(entity.getFechaVencimiento())
            .fechaCreacion(entity.getFechaCreacion())
            .fechaActualizacion(entity.getFechaActualizacion())
            .build();
    }

    public static FacturaJpaEntity toJpaEntity(FacturaAcueducto factura) {
        return FacturaJpaEntity.builder()
            .id(factura.getId().getValor())
            .clienteId(factura.getClienteId().getValor())
            .periodo(factura.getPeriodo().getValor())
            .consumo(factura.getConsumo().getMetrosCubicos())
            .valorPagar(factura.getValorPagar().getMonto())
            .estado(toEstadoFacturaJpa(factura.getEstado()))
            .fechaVencimiento(factura.getFechaVencimiento())
            .fechaCreacion(factura.getFechaCreacion())
            .fechaActualizacion(factura.getFechaActualizacion())
            .build();
    }

    private static EstadoFactura toEstadoFacturaDomain(EstadoFacturaJpa estadoJpa) {
        return EstadoFactura.valueOf(estadoJpa.name());
    }

    private static EstadoFacturaJpa toEstadoFacturaJpa(EstadoFactura estado) {
        return EstadoFacturaJpa.valueOf(estado.name());
    }
}
