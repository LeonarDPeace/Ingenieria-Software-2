package com.serviciudad.infrastructure.adapter.output.persistence;

import com.serviciudad.domain.model.ConsumoEnergiaModel;
import com.serviciudad.domain.port.output.ConsumoEnergiaReaderPort;
import com.serviciudad.domain.valueobject.ClienteId;
import com.serviciudad.domain.valueobject.Periodo;
import com.serviciudad.infrastructure.adapter.output.persistence.jpa.mapper.ConsumoEnergiaJpaMapper;
import com.serviciudad.infrastructure.adapter.output.persistence.jpa.repository.ConsumoEnergiaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ConsumoEnergiaReaderAdapter implements ConsumoEnergiaReaderPort {

    private static final BigDecimal UMBRAL_CONSUMO_ELEVADO = new BigDecimal("500");
    
    private final ConsumoEnergiaJpaRepository jpaRepository;

    @Override
    public List<ConsumoEnergiaModel> findByClienteId(ClienteId clienteId) {
        return jpaRepository.findByClienteId(clienteId.getValor())
            .stream()
            .map(ConsumoEnergiaJpaMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<ConsumoEnergiaModel> findByClienteIdAndPeriodo(ClienteId clienteId, Periodo periodo) {
        return jpaRepository.findByClienteIdAndPeriodo(clienteId.getValor(), periodo.getValor())
            .stream()
            .map(ConsumoEnergiaJpaMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<ConsumoEnergiaModel> findConsumosElevados(ClienteId clienteId) {
        return jpaRepository.findConsumosElevados(clienteId.getValor(), UMBRAL_CONSUMO_ELEVADO)
            .stream()
            .map(ConsumoEnergiaJpaMapper::toDomain)
            .collect(Collectors.toList());
    }
}
