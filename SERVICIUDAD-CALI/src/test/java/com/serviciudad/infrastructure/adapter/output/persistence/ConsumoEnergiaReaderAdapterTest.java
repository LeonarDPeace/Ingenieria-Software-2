package com.serviciudad.infrastructure.adapter.output.persistence;

import com.serviciudad.domain.model.ConsumoEnergiaModel;
import com.serviciudad.domain.valueobject.*;
import com.serviciudad.infrastructure.adapter.output.persistence.jpa.entity.ConsumoEnergiaJpaEntity;
import com.serviciudad.infrastructure.adapter.output.persistence.jpa.mapper.ConsumoEnergiaJpaMapper;
import com.serviciudad.infrastructure.adapter.output.persistence.jpa.repository.ConsumoEnergiaJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ConsumoEnergiaReaderAdapter.
 * 
 * Verifica la correcta adaptación entre Domain y JPA para consumos de energía.
 * 
 * @author Equipo ServiCiudad Cali
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Adapter: ConsumoEnergiaReader - Tests Unitarios")
class ConsumoEnergiaReaderAdapterTest {

    @Mock
    private ConsumoEnergiaJpaRepository jpaRepository;

    @InjectMocks
    private ConsumoEnergiaReaderAdapter adapter;

    private ConsumoEnergiaModel consumoDomain;
    private ConsumoEnergiaJpaEntity consumoEntity;
    private ClienteId clienteId;
    private Periodo periodo;

    @BeforeEach
    void setUp() {
        clienteId = ClienteId.of("1234567890");
        periodo = Periodo.of("202501");

        // Modelo de dominio
        consumoDomain = ConsumoEnergiaModel.builder()
            .clienteId(clienteId)
            .periodo(periodo)
            .consumo(ConsumoEnergia.of(350))
            .valorPagar(Dinero.of(new BigDecimal("180000.00")))
            .fechaLectura(LocalDate.of(2025, 1, 15))
            .estrato("3")
            .build();

        // Entidad JPA usando mapper real
        consumoEntity = ConsumoEnergiaJpaMapper.toJpaEntity(consumoDomain);
    }

    @Test
    @DisplayName("Debe encontrar consumos por cliente ID")
    void debeEncontrarConsumosPorClienteId() {
        // Arrange
        ConsumoEnergiaJpaEntity consumo2 = ConsumoEnergiaJpaEntity.builder()
            .clienteId("1234567890")
            .periodo("202502")
            .consumo(new BigDecimal("320"))
            .valorPagar(new BigDecimal("165000.00"))
            .fechaLectura(LocalDate.of(2025, 2, 15))
            .estrato("3")
            .build();

        when(jpaRepository.findByClienteId("1234567890"))
            .thenReturn(List.of(consumoEntity, consumo2));

        // Act
        List<ConsumoEnergiaModel> resultado = adapter.findByClienteId(clienteId);

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getClienteId().getValor()).isEqualTo("1234567890");
        assertThat(resultado.get(0).getPeriodo().getValor()).isEqualTo("202501");
        assertThat(resultado.get(0).getConsumo().getKilovatiosHora()).isEqualTo(350);
        assertThat(resultado.get(1).getPeriodo().getValor()).isEqualTo("202502");
        
        verify(jpaRepository, times(1)).findByClienteId("1234567890");
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay consumos para el cliente")
    void debeRetornarListaVaciaSinConsumos() {
        // Arrange
        when(jpaRepository.findByClienteId("9999999999"))
            .thenReturn(List.of());

        // Act
        List<ConsumoEnergiaModel> resultado = adapter.findByClienteId(ClienteId.of("9999999999"));

        // Assert
        assertThat(resultado).isEmpty();
        verify(jpaRepository).findByClienteId("9999999999");
    }

    @Test
    @DisplayName("Debe encontrar consumos por cliente y periodo")
    void debeEncontrarConsumosPorClienteYPeriodo() {
        // Arrange
        when(jpaRepository.findByClienteIdAndPeriodo("1234567890", "202501"))
            .thenReturn(List.of(consumoEntity));

        // Act
        List<ConsumoEnergiaModel> resultado = adapter.findByClienteIdAndPeriodo(clienteId, periodo);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getClienteId().getValor()).isEqualTo("1234567890");
        assertThat(resultado.get(0).getPeriodo().getValor()).isEqualTo("202501");
        assertThat(resultado.get(0).getValorPagar().getMonto()).isEqualByComparingTo(new BigDecimal("180000.00"));
        
        verify(jpaRepository).findByClienteIdAndPeriodo("1234567890", "202501");
    }

    @Test
    @DisplayName("Debe encontrar consumos elevados usando umbral correcto")
    void debeEncontrarConsumosElevados() {
        // Arrange
        ConsumoEnergiaJpaEntity consumoElevado = ConsumoEnergiaJpaEntity.builder()
            .clienteId("1234567890")
            .periodo("202503")
            .consumo(new BigDecimal("850"))
            .valorPagar(new BigDecimal("450000.00"))
            .fechaLectura(LocalDate.of(2025, 3, 15))
            .estrato("3")
            .build();

        when(jpaRepository.findConsumosElevados(eq("1234567890"), any(BigDecimal.class)))
            .thenReturn(List.of(consumoElevado));

        // Act
        List<ConsumoEnergiaModel> resultado = adapter.findConsumosElevados(clienteId);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getConsumo().getKilovatiosHora()).isEqualTo(850);
        assertThat(resultado.get(0).getValorPagar().getMonto()).isEqualByComparingTo(new BigDecimal("450000.00"));
        
        verify(jpaRepository).findConsumosElevados(eq("1234567890"), eq(new BigDecimal("500")));
    }

    @Test
    @DisplayName("Debe mapear correctamente todos los campos del dominio")
    void debeMappearTodosCamposCorrectamente() {
        // Arrange
        when(jpaRepository.findByClienteId("1234567890"))
            .thenReturn(List.of(consumoEntity));

        // Act
        List<ConsumoEnergiaModel> resultado = adapter.findByClienteId(clienteId);

        // Assert
        ConsumoEnergiaModel consumo = resultado.get(0);
        assertThat(consumo.getClienteId()).isNotNull();
        assertThat(consumo.getPeriodo()).isNotNull();
        assertThat(consumo.getConsumo()).isNotNull();
        assertThat(consumo.getValorPagar()).isNotNull();
        assertThat(consumo.getFechaLectura()).isNotNull();
        assertThat(consumo.getEstrato()).isNotNull();
        
        assertThat(consumo.getClienteId().getValor()).isEqualTo("1234567890");
        assertThat(consumo.getPeriodo().getValor()).isEqualTo("202501");
        assertThat(consumo.getConsumo().getKilovatiosHora()).isEqualTo(350);
        assertThat(consumo.getFechaLectura()).isEqualTo(LocalDate.of(2025, 1, 15));
        assertThat(consumo.getEstrato()).isEqualTo("3");
    }

    @Test
    @DisplayName("Debe usar mapper estático para transformaciones")
    void debeUsarMapperEstaticoParaTransformaciones() {
        // Arrange
        when(jpaRepository.findByClienteId("1234567890"))
            .thenReturn(List.of(consumoEntity));

        // Act
        List<ConsumoEnergiaModel> resultado = adapter.findByClienteId(clienteId);

        // Assert - Verificar que el mapper estático funcionó correctamente
        assertThat(resultado).hasSize(1);
        ConsumoEnergiaModel consumo = resultado.get(0);
        
        // Verificar transformación correcta de Value Objects
        assertThat(consumo.getClienteId()).isInstanceOf(ClienteId.class);
        assertThat(consumo.getPeriodo()).isInstanceOf(Periodo.class);
        assertThat(consumo.getConsumo()).isInstanceOf(ConsumoEnergia.class);
        assertThat(consumo.getValorPagar()).isInstanceOf(Dinero.class);
    }
}
