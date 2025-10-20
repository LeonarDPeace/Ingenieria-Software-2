package com.serviciudad.infrastructure.adapter.output.persistence;

import com.serviciudad.domain.model.FacturaAcueducto;
import com.serviciudad.domain.port.output.FacturaRepositoryPort;
import com.serviciudad.domain.valueobject.ClienteId;
import com.serviciudad.domain.valueobject.FacturaId;
import com.serviciudad.domain.valueobject.Periodo;
import com.serviciudad.infrastructure.adapter.output.persistence.jpa.entity.EstadoFacturaJpa;
import com.serviciudad.infrastructure.adapter.output.persistence.jpa.entity.FacturaJpaEntity;
import com.serviciudad.infrastructure.adapter.output.persistence.jpa.mapper.FacturaJpaMapper;
import com.serviciudad.infrastructure.adapter.output.persistence.jpa.repository.FacturaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FacturaRepositoryAdapter implements FacturaRepositoryPort {

    private final FacturaJpaRepository jpaRepository;

    @Override
    public Optional<FacturaAcueducto> findById(FacturaId facturaId) {
        return jpaRepository.findById(facturaId.getValor())
            .map(FacturaJpaMapper::toDomain);
    }

    @Override
    public List<FacturaAcueducto> findByClienteId(ClienteId clienteId) {
        return jpaRepository.findByClienteId(clienteId.getValor())
            .stream()
            .map(FacturaJpaMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<FacturaAcueducto> findByClienteIdAndPeriodo(ClienteId clienteId, Periodo periodo) {
        return jpaRepository.findByClienteIdAndPeriodo(clienteId.getValor(), periodo.getValor())
            .stream()
            .map(FacturaJpaMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<FacturaAcueducto> findFacturasPendientes() {
        return jpaRepository.findByEstado(EstadoFacturaJpa.PENDIENTE)
            .stream()
            .map(FacturaJpaMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<FacturaAcueducto> findFacturasPendientes(int pageNumber, int pageSize) {
        Page<FacturaJpaEntity> page = jpaRepository.findByEstado(
            EstadoFacturaJpa.PENDIENTE, 
            PageRequest.of(pageNumber, pageSize)
        );
        
        return page.getContent()
            .stream()
            .map(FacturaJpaMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public boolean hasMoreFacturasPendientes(int pageNumber, int pageSize) {
        Page<FacturaJpaEntity> page = jpaRepository.findByEstado(
            EstadoFacturaJpa.PENDIENTE, 
            PageRequest.of(pageNumber, pageSize)
        );
        
        return page.hasNext();
    }

    @Override
    public List<FacturaAcueducto> findFacturasVencidas() {
        return jpaRepository.findByEstado(EstadoFacturaJpa.VENCIDA)
            .stream()
            .map(FacturaJpaMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public FacturaAcueducto save(FacturaAcueducto factura) {
        FacturaJpaEntity entity = FacturaJpaMapper.toJpaEntity(factura);
        FacturaJpaEntity savedEntity = jpaRepository.save(entity);
        return FacturaJpaMapper.toDomain(savedEntity);
    }

    @Override
    public void saveAll(List<FacturaAcueducto> facturas) {
        List<FacturaJpaEntity> entities = facturas.stream()
            .map(FacturaJpaMapper::toJpaEntity)
            .collect(Collectors.toList());
        jpaRepository.saveAll(entities);
    }

    @Override
    public void flush() {
        jpaRepository.flush();
    }
}
