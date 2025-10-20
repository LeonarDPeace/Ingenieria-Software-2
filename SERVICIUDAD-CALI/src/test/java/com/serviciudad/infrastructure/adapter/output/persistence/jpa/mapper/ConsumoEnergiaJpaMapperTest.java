package com.serviciudad.infrastructure.adapter.output.persistence.jpa.mapper;

import com.serviciudad.domain.model.ConsumoEnergiaModel;
import com.serviciudad.domain.valueobject.*;
import com.serviciudad.infrastructure.adapter.output.persistence.jpa.entity.ConsumoEnergiaJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para ConsumoEnergiaJpaMapper.
 * 
 * Valida transformaciones bidireccionales Domain ↔ JPA Entity para consumos energía.
 * 
 * @author Equipo ServiCiudad Cali
 * @version 1.0
 */
@DisplayName("Mapper: ConsumoEnergiaJpa - Tests Unitarios")
class ConsumoEnergiaJpaMapperTest {

    @Test
    @DisplayName("Debe mapear correctamente de JPA Entity a Domain Model")
    void debeMappearDeJpaEntityADomain() {
        // Arrange
        LocalDate fechaLectura = LocalDate.of(2025, 1, 15);

        ConsumoEnergiaJpaEntity entity = ConsumoEnergiaJpaEntity.builder()
            .clienteId("1234567890")
            .periodo("202501")
            .consumo(new BigDecimal("350.00"))
            .valorPagar(new BigDecimal("180000.00"))
            .fechaLectura(fechaLectura)
            .estrato("3")
            .build();

        // Act
        ConsumoEnergiaModel consumo = ConsumoEnergiaJpaMapper.toDomain(entity);

        // Assert
        assertThat(consumo).isNotNull();
        assertThat(consumo.getClienteId().getValor()).isEqualTo("1234567890");
        assertThat(consumo.getPeriodo().getValor()).isEqualTo("202501");
        assertThat(consumo.getConsumo().getKilovatiosHora()).isEqualTo(350);
        assertThat(consumo.getValorPagar().getMonto()).isEqualByComparingTo(new BigDecimal("180000.00"));
        assertThat(consumo.getFechaLectura()).isEqualTo(fechaLectura);
        assertThat(consumo.getEstrato()).isEqualTo("3");
    }

    @Test
    @DisplayName("Debe mapear correctamente de Domain Model a JPA Entity")
    void debeMappearDeDomainAJpaEntity() {
        // Arrange
        LocalDate fechaLectura = LocalDate.of(2025, 2, 20);

        ConsumoEnergiaModel consumo = ConsumoEnergiaModel.builder()
            .clienteId(ClienteId.of("9876543210"))
            .periodo(Periodo.of("202502"))
            .consumo(ConsumoEnergia.of(425))
            .valorPagar(Dinero.of(new BigDecimal("220000.00")))
            .fechaLectura(fechaLectura)
            .estrato("4")
            .build();

        // Act
        ConsumoEnergiaJpaEntity entity = ConsumoEnergiaJpaMapper.toJpaEntity(consumo);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getClienteId()).isEqualTo("9876543210");
        assertThat(entity.getPeriodo()).isEqualTo("202502");
        assertThat(entity.getConsumo()).isEqualByComparingTo(new BigDecimal("425"));
        assertThat(entity.getValorPagar()).isEqualByComparingTo(new BigDecimal("220000.00"));
        assertThat(entity.getFechaLectura()).isEqualTo(fechaLectura);
        assertThat(entity.getEstrato()).isEqualTo("4");
    }

    @Test
    @DisplayName("Debe realizar transformación bidireccional sin pérdida de datos")
    void debeRealizarTransformacionBidireccionalSinPerdida() {
        // Arrange - Domain original
        ConsumoEnergiaModel original = ConsumoEnergiaModel.builder()
            .clienteId(ClienteId.of("5555555555"))
            .periodo(Periodo.of("202503"))
            .consumo(ConsumoEnergia.of(500))
            .valorPagar(Dinero.of(new BigDecimal("260000.00")))
            .fechaLectura(LocalDate.of(2025, 3, 18))
            .estrato("2")
            .build();

        // Act - Domain → JPA → Domain
        ConsumoEnergiaJpaEntity entity = ConsumoEnergiaJpaMapper.toJpaEntity(original);
        ConsumoEnergiaModel resultado = ConsumoEnergiaJpaMapper.toDomain(entity);

        // Assert - Verificar identidad de datos
        assertThat(resultado.getClienteId().getValor()).isEqualTo(original.getClienteId().getValor());
        assertThat(resultado.getPeriodo().getValor()).isEqualTo(original.getPeriodo().getValor());
        assertThat(resultado.getConsumo().getKilovatiosHora()).isEqualTo(original.getConsumo().getKilovatiosHora());
        assertThat(resultado.getValorPagar().getMonto()).isEqualByComparingTo(original.getValorPagar().getMonto());
        assertThat(resultado.getFechaLectura()).isEqualTo(original.getFechaLectura());
        assertThat(resultado.getEstrato()).isEqualTo(original.getEstrato());
    }

    @Test
    @DisplayName("Debe manejar valores BigDecimal con decimales correctamente")
    void debeManejarBigDecimalConDecimales() {
        // Arrange
        ConsumoEnergiaJpaEntity entity = ConsumoEnergiaJpaEntity.builder()
            .clienteId("1111111111")
            .periodo("202501")
            .consumo(new BigDecimal("350.75"))
            .valorPagar(new BigDecimal("180500.50"))
            .fechaLectura(LocalDate.now())
            .estrato("3")
            .build();

        // Act
        ConsumoEnergiaModel consumo = ConsumoEnergiaJpaMapper.toDomain(entity);

        // Assert
        assertThat(consumo.getConsumo().getKilovatiosHora()).isEqualTo(350); // int trunca decimales
        assertThat(consumo.getValorPagar().getMonto()).isEqualByComparingTo(new BigDecimal("180500.50"));
    }

    @Test
    @DisplayName("Debe crear Value Objects inmutables correctamente")
    void debeCrearValueObjectsInmutables() {
        // Arrange
        ConsumoEnergiaJpaEntity entity = ConsumoEnergiaJpaEntity.builder()
            .clienteId("2222222222")
            .periodo("202504")
            .consumo(new BigDecimal("600"))
            .valorPagar(new BigDecimal("310000.00"))
            .fechaLectura(LocalDate.now())
            .estrato("5")
            .build();

        // Act
        ConsumoEnergiaModel consumo = ConsumoEnergiaJpaMapper.toDomain(entity);

        // Assert - Verificar que los Value Objects son instancias correctas
        assertThat(consumo.getClienteId()).isInstanceOf(ClienteId.class);
        assertThat(consumo.getPeriodo()).isInstanceOf(Periodo.class);
        assertThat(consumo.getConsumo()).isInstanceOf(ConsumoEnergia.class);
        assertThat(consumo.getValorPagar()).isInstanceOf(Dinero.class);
        
        // Verificar inmutabilidad (Lombok @Value)
        assertThat(consumo.getClienteId()).isNotNull();
        assertThat(consumo.getPeriodo()).isNotNull();
    }

    @Test
    @DisplayName("Debe manejar diferentes estratos correctamente")
    void debeManejarDiferentesEstratos() {
        // Test para diferentes estratos (1-6)
        String[] estratos = {"1", "2", "3", "4", "5", "6"};

        for (String estrato : estratos) {
            // Arrange
            ConsumoEnergiaJpaEntity entity = ConsumoEnergiaJpaEntity.builder()
                .clienteId("1234567890")
                .periodo("202501")
                .consumo(new BigDecimal("300"))
                .valorPagar(new BigDecimal("155000.00"))
                .fechaLectura(LocalDate.now())
                .estrato(estrato)
                .build();

            // Act
            ConsumoEnergiaModel consumo = ConsumoEnergiaJpaMapper.toDomain(entity);

            // Assert
            assertThat(consumo.getEstrato()).isEqualTo(estrato);
        }
    }

    @Test
    @DisplayName("Debe preservar precisión de valores monetarios")
    void debePreservarPrecisionValoresMonetarios() {
        // Arrange
        BigDecimal valorPagarOriginal = new BigDecimal("123456.78");
        
        ConsumoEnergiaModel original = ConsumoEnergiaModel.builder()
            .clienteId(ClienteId.of("3333333333"))
            .periodo(Periodo.of("202505"))
            .consumo(ConsumoEnergia.of(400))
            .valorPagar(Dinero.of(valorPagarOriginal))
            .fechaLectura(LocalDate.now())
            .estrato("3")
            .build();

        // Act - Domain → JPA → Domain
        ConsumoEnergiaJpaEntity entity = ConsumoEnergiaJpaMapper.toJpaEntity(original);
        ConsumoEnergiaModel resultado = ConsumoEnergiaJpaMapper.toDomain(entity);

        // Assert - Verificar precisión monetaria
        assertThat(resultado.getValorPagar().getMonto())
            .isEqualByComparingTo(valorPagarOriginal);
    }
}
