package com.serviciudad.infrastructure.adapter.output.persistence.jpa.mapper;

import com.serviciudad.domain.model.ConsumoEnergiaModel;
import com.serviciudad.domain.valueobject.ClienteId;
import com.serviciudad.domain.valueobject.ConsumoEnergia;
import com.serviciudad.domain.valueobject.Dinero;
import com.serviciudad.domain.valueobject.Periodo;
import com.serviciudad.infrastructure.adapter.output.persistence.jpa.entity.ConsumoEnergiaJpaEntity;

public class ConsumoEnergiaJpaMapper {

    public static ConsumoEnergiaModel toDomain(ConsumoEnergiaJpaEntity entity) {
        return ConsumoEnergiaModel.builder()
            .clienteId(ClienteId.of(entity.getClienteId()))
            .periodo(Periodo.of(entity.getPeriodo()))
            .consumo(ConsumoEnergia.of(entity.getConsumo().intValue()))
            .valorPagar(Dinero.of(entity.getValorPagar()))
            .fechaLectura(entity.getFechaLectura())
            .estrato(entity.getEstrato())
            .build();
    }

    public static ConsumoEnergiaJpaEntity toJpaEntity(ConsumoEnergiaModel consumo) {
        return ConsumoEnergiaJpaEntity.builder()
            .clienteId(consumo.getClienteId().getValor())
            .periodo(consumo.getPeriodo().getValor())
            .consumo(new java.math.BigDecimal(consumo.getConsumo().getKilovatiosHora()))
            .valorPagar(consumo.getValorPagar().getMonto())
            .fechaLectura(consumo.getFechaLectura())
            .estrato(consumo.getEstrato())
            .build();
    }
}
