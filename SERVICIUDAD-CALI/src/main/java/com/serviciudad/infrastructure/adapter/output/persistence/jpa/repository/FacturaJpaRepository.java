package com.serviciudad.infrastructure.adapter.output.persistence.jpa.repository;

import com.serviciudad.infrastructure.adapter.output.persistence.jpa.entity.EstadoFacturaJpa;
import com.serviciudad.infrastructure.adapter.output.persistence.jpa.entity.FacturaJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacturaJpaRepository extends JpaRepository<FacturaJpaEntity, Long> {
    
    List<FacturaJpaEntity> findByClienteId(String clienteId);
    
    List<FacturaJpaEntity> findByClienteIdAndPeriodo(String clienteId, String periodo);
    
    List<FacturaJpaEntity> findByEstado(EstadoFacturaJpa estado);
    
    Page<FacturaJpaEntity> findByEstado(EstadoFacturaJpa estado, Pageable pageable);
}
