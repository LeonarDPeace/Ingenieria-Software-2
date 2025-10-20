package com.serviciudad.infrastructure.adapter.output.persistence;

import com.serviciudad.domain.model.EstadoFactura;
import com.serviciudad.domain.model.FacturaAcueducto;
import com.serviciudad.domain.valueobject.*;
import com.serviciudad.infrastructure.adapter.output.persistence.jpa.entity.FacturaJpaEntity;
import com.serviciudad.infrastructure.adapter.output.persistence.jpa.mapper.FacturaJpaMapper;
import com.serviciudad.infrastructure.adapter.output.persistence.jpa.repository.FacturaJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para FacturaRepositoryAdapter.
 * 
 * Verifica la correcta adaptación entre Domain y JPA.
 * 
 * @author Equipo ServiCiudad Cali
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Adapter: FacturaRepository - Tests Unitarios")
class FacturaRepositoryAdapterTest {

    @Mock
    private FacturaJpaRepository jpaRepository;

    @InjectMocks
    private FacturaRepositoryAdapter adapter;

    private FacturaAcueducto facturaDomain;
    private FacturaJpaEntity facturaEntity;
    private ClienteId clienteId;

    @BeforeEach
    void setUp() {
        clienteId = new ClienteId("1234567890");

        facturaDomain = FacturaAcueducto.builder()
            .id(new FacturaId(1L))
            .clienteId(clienteId)
            .periodo(Periodo.of("202501"))
            .consumo(ConsumoAgua.of(15))
            .valorPagar(Dinero.of(new BigDecimal("95000.00")))
            .fechaVencimiento(LocalDate.now())
            .fechaCreacion(LocalDateTime.now())
            .fechaActualizacion(LocalDateTime.now())
            .estado(EstadoFactura.PENDIENTE)
            .build();

        // Usar el mapper real para crear la entidad
        facturaEntity = FacturaJpaMapper.toJpaEntity(facturaDomain);
    }

    @Test
    @DisplayName("Debe guardar factura correctamente")
    void debeGuardarFactura() {
        // Arrange
        when(jpaRepository.save(any(FacturaJpaEntity.class))).thenReturn(facturaEntity);

        // Act
        FacturaAcueducto resultado = adapter.save(facturaDomain);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId().getValor()).isEqualTo(1L);
        assertThat(resultado.getClienteId().getValor()).isEqualTo("1234567890");
        verify(jpaRepository, times(1)).save(any(FacturaJpaEntity.class));
    }

    @Test
    @DisplayName("Debe encontrar factura por ID")
    void debeEncontrarFacturaPorId() {
        // Arrange
        FacturaId id = new FacturaId(1L);
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(facturaEntity));

        // Act
        Optional<FacturaAcueducto> resultado = adapter.findById(id);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId().getValor()).isEqualTo(1L);
        assertThat(resultado.get().getClienteId().getValor()).isEqualTo("1234567890");
    }

    @Test
    @DisplayName("Debe encontrar facturas por cliente")
    void debeEncontrarFacturasPorCliente() {
        // Arrange
        when(jpaRepository.findByClienteId("1234567890")).thenReturn(List.of(facturaEntity));

        // Act
        List<FacturaAcueducto> resultado = adapter.findByClienteId(clienteId);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getClienteId().getValor()).isEqualTo("1234567890");
        verify(jpaRepository).findByClienteId("1234567890");
    }
}
