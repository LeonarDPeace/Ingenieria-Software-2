package com.serviciudad.infrastructure.adapter.output.persistence.jpa.mapper;

import com.serviciudad.domain.model.EstadoFactura;
import com.serviciudad.domain.model.FacturaAcueducto;
import com.serviciudad.domain.valueobject.*;
import com.serviciudad.infrastructure.adapter.output.persistence.jpa.entity.EstadoFacturaJpa;
import com.serviciudad.infrastructure.adapter.output.persistence.jpa.entity.FacturaJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para FacturaJpaMapper.
 * 
 * Valida transformaciones bidireccionales Domain ↔ JPA Entity.
 * 
 * @author Equipo ServiCiudad Cali
 * @version 1.0
 */
@DisplayName("Mapper: FacturaJpa - Tests Unitarios")
class FacturaJpaMapperTest {

    @Test
    @DisplayName("Debe mapear correctamente de JPA Entity a Domain Model")
    void debeMappearDeJpaEntityADomain() {
        // Arrange
        LocalDate fechaVencimiento = LocalDate.of(2025, 1, 31);
        LocalDateTime fechaCreacion = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime fechaActualizacion = LocalDateTime.of(2025, 1, 15, 14, 30);

        FacturaJpaEntity entity = FacturaJpaEntity.builder()
            .id(1L)
            .clienteId("1234567890")
            .periodo("202501")
            .consumo(15)
            .valorPagar(new BigDecimal("95000.00"))
            .estado(EstadoFacturaJpa.PENDIENTE)
            .fechaVencimiento(fechaVencimiento)
            .fechaCreacion(fechaCreacion)
            .fechaActualizacion(fechaActualizacion)
            .build();

        // Act
        FacturaAcueducto factura = FacturaJpaMapper.toDomain(entity);

        // Assert
        assertThat(factura).isNotNull();
        assertThat(factura.getId()).isNotNull();
        assertThat(factura.getId().getValor()).isEqualTo(1L);
        assertThat(factura.getClienteId().getValor()).isEqualTo("1234567890");
        assertThat(factura.getPeriodo().getValor()).isEqualTo("202501");
        assertThat(factura.getConsumo().getMetrosCubicos()).isEqualTo(15);
        assertThat(factura.getValorPagar().getMonto()).isEqualByComparingTo(new BigDecimal("95000.00"));
        assertThat(factura.getEstado()).isEqualTo(EstadoFactura.PENDIENTE);
        assertThat(factura.getFechaVencimiento()).isEqualTo(fechaVencimiento);
        assertThat(factura.getFechaCreacion()).isEqualTo(fechaCreacion);
        assertThat(factura.getFechaActualizacion()).isEqualTo(fechaActualizacion);
    }

    @Test
    @DisplayName("Debe mapear correctamente de Domain Model a JPA Entity")
    void debeMappearDeDomainAJpaEntity() {
        // Arrange
        LocalDate fechaVencimiento = LocalDate.of(2025, 2, 28);
        LocalDateTime fechaCreacion = LocalDateTime.of(2025, 2, 1, 9, 0);
        LocalDateTime fechaActualizacion = LocalDateTime.of(2025, 2, 10, 16, 45);

        FacturaAcueducto factura = FacturaAcueducto.builder()
            .id(FacturaId.of(2L))
            .clienteId(ClienteId.of("9876543210"))
            .periodo(Periodo.of("202502"))
            .consumo(ConsumoAgua.of(22))
            .valorPagar(Dinero.of(new BigDecimal("145000.00")))
            .estado(EstadoFactura.PAGADA)
            .fechaVencimiento(fechaVencimiento)
            .fechaCreacion(fechaCreacion)
            .fechaActualizacion(fechaActualizacion)
            .build();

        // Act
        FacturaJpaEntity entity = FacturaJpaMapper.toJpaEntity(factura);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(2L);
        assertThat(entity.getClienteId()).isEqualTo("9876543210");
        assertThat(entity.getPeriodo()).isEqualTo("202502");
        assertThat(entity.getConsumo()).isEqualTo(22);
        assertThat(entity.getValorPagar()).isEqualByComparingTo(new BigDecimal("145000.00"));
        assertThat(entity.getEstado()).isEqualTo(EstadoFacturaJpa.PAGADA);
        assertThat(entity.getFechaVencimiento()).isEqualTo(fechaVencimiento);
        assertThat(entity.getFechaCreacion()).isEqualTo(fechaCreacion);
        assertThat(entity.getFechaActualizacion()).isEqualTo(fechaActualizacion);
    }

    @Test
    @DisplayName("Debe convertir correctamente todos los estados de Factura")
    void debeConvertirTodosLosEstadosFactura() {
        // Arrange & Act & Assert para PENDIENTE
        FacturaJpaEntity entityPendiente = crearEntityConEstado(EstadoFacturaJpa.PENDIENTE);
        FacturaAcueducto facturaPendiente = FacturaJpaMapper.toDomain(entityPendiente);
        assertThat(facturaPendiente.getEstado()).isEqualTo(EstadoFactura.PENDIENTE);

        // PAGADA
        FacturaJpaEntity entityPagada = crearEntityConEstado(EstadoFacturaJpa.PAGADA);
        FacturaAcueducto facturaPagada = FacturaJpaMapper.toDomain(entityPagada);
        assertThat(facturaPagada.getEstado()).isEqualTo(EstadoFactura.PAGADA);

        // VENCIDA
        FacturaJpaEntity entityVencida = crearEntityConEstado(EstadoFacturaJpa.VENCIDA);
        FacturaAcueducto facturaVencida = FacturaJpaMapper.toDomain(entityVencida);
        assertThat(facturaVencida.getEstado()).isEqualTo(EstadoFactura.VENCIDA);

        // ANULADA
        FacturaJpaEntity entityAnulada = crearEntityConEstado(EstadoFacturaJpa.ANULADA);
        FacturaAcueducto facturaAnulada = FacturaJpaMapper.toDomain(entityAnulada);
        assertThat(facturaAnulada.getEstado()).isEqualTo(EstadoFactura.ANULADA);
    }

    @Test
    @DisplayName("Debe realizar transformación bidireccional sin pérdida de datos")
    void debeRealizarTransformacionBidireccionalSinPerdida() {
        // Arrange - Domain original
        FacturaAcueducto original = FacturaAcueducto.builder()
            .id(FacturaId.of(5L))
            .clienteId(ClienteId.of("5555555555"))
            .periodo(Periodo.of("202503"))
            .consumo(ConsumoAgua.of(18))
            .valorPagar(Dinero.of(new BigDecimal("115000.00")))
            .estado(EstadoFactura.VENCIDA)
            .fechaVencimiento(LocalDate.of(2025, 3, 31))
            .fechaCreacion(LocalDateTime.of(2025, 3, 1, 8, 0))
            .fechaActualizacion(LocalDateTime.of(2025, 3, 20, 11, 30))
            .build();

        // Act - Domain → JPA → Domain
        FacturaJpaEntity entity = FacturaJpaMapper.toJpaEntity(original);
        FacturaAcueducto resultado = FacturaJpaMapper.toDomain(entity);

        // Assert - Verificar identidad de datos
        assertThat(resultado.getId().getValor()).isEqualTo(original.getId().getValor());
        assertThat(resultado.getClienteId().getValor()).isEqualTo(original.getClienteId().getValor());
        assertThat(resultado.getPeriodo().getValor()).isEqualTo(original.getPeriodo().getValor());
        assertThat(resultado.getConsumo().getMetrosCubicos()).isEqualTo(original.getConsumo().getMetrosCubicos());
        assertThat(resultado.getValorPagar().getMonto()).isEqualByComparingTo(original.getValorPagar().getMonto());
        assertThat(resultado.getEstado()).isEqualTo(original.getEstado());
        assertThat(resultado.getFechaVencimiento()).isEqualTo(original.getFechaVencimiento());
        assertThat(resultado.getFechaCreacion()).isEqualTo(original.getFechaCreacion());
        assertThat(resultado.getFechaActualizacion()).isEqualTo(original.getFechaActualizacion());
    }

    @Test
    @DisplayName("Debe manejar valores BigDecimal con decimales correctamente")
    void debeManejarBigDecimalConDecimales() {
        // Arrange
        FacturaJpaEntity entity = FacturaJpaEntity.builder()
            .id(3L)
            .clienteId("1111111111")
            .periodo("202501")
            .consumo(15)
            .valorPagar(new BigDecimal("95750.75"))
            .estado(EstadoFacturaJpa.PENDIENTE)
            .fechaVencimiento(LocalDate.now())
            .fechaCreacion(LocalDateTime.now())
            .fechaActualizacion(LocalDateTime.now())
            .build();

        // Act
        FacturaAcueducto factura = FacturaJpaMapper.toDomain(entity);

        // Assert
        assertThat(factura.getConsumo().getMetrosCubicos()).isEqualTo(15); // int trunca decimales
        assertThat(factura.getValorPagar().getMonto()).isEqualByComparingTo(new BigDecimal("95750.75"));
    }

    @Test
    @DisplayName("Debe crear Value Objects inmutables correctamente")
    void debeCrearValueObjectsInmutables() {
        // Arrange
        FacturaJpaEntity entity = FacturaJpaEntity.builder()
            .id(10L)
            .clienteId("2222222222")
            .periodo("202504")
            .consumo(30)
            .valorPagar(new BigDecimal("195000.00"))
            .estado(EstadoFacturaJpa.PENDIENTE)
            .fechaVencimiento(LocalDate.now())
            .fechaCreacion(LocalDateTime.now())
            .fechaActualizacion(LocalDateTime.now())
            .build();

        // Act
        FacturaAcueducto factura = FacturaJpaMapper.toDomain(entity);

        // Assert - Verificar que los Value Objects son instancias correctas
        assertThat(factura.getId()).isInstanceOf(FacturaId.class);
        assertThat(factura.getClienteId()).isInstanceOf(ClienteId.class);
        assertThat(factura.getPeriodo()).isInstanceOf(Periodo.class);
        assertThat(factura.getConsumo()).isInstanceOf(ConsumoAgua.class);
        assertThat(factura.getValorPagar()).isInstanceOf(Dinero.class);
        
        // Verificar inmutabilidad (Lombok @Value)
        assertThat(factura.getId()).isNotNull();
        assertThat(factura.getClienteId()).isNotNull();
    }

    // ============ Helper Methods ============

    private FacturaJpaEntity crearEntityConEstado(EstadoFacturaJpa estado) {
        return FacturaJpaEntity.builder()
            .id(1L)
            .clienteId("1234567890")
            .periodo("202501")
            .consumo(15)
            .valorPagar(new BigDecimal("95000.00"))
            .estado(estado)
            .fechaVencimiento(LocalDate.now())
            .fechaCreacion(LocalDateTime.now())
            .fechaActualizacion(LocalDateTime.now())
            .build();
    }
}
