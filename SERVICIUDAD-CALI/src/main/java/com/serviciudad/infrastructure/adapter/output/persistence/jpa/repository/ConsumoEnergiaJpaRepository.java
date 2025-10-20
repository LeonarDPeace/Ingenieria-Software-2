package com.serviciudad.infrastructure.adapter.output.persistence.jpa.repository;

import com.serviciudad.infrastructure.adapter.output.persistence.jpa.entity.ConsumoEnergiaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ConsumoEnergiaJpaRepository extends JpaRepository<ConsumoEnergiaJpaEntity, Long> {
    
    List<ConsumoEnergiaJpaEntity> findByClienteId(String clienteId);
    
    List<ConsumoEnergiaJpaEntity> findByClienteIdAndPeriodo(String clienteId, String periodo);
    
    @Query("SELECT c FROM ConsumoEnergiaJpaEntity c WHERE c.clienteId = :clienteId AND c.consumo > :umbral")
    List<ConsumoEnergiaJpaEntity> findConsumosElevados(@Param("clienteId") String clienteId, @Param("umbral") BigDecimal umbral);
}
