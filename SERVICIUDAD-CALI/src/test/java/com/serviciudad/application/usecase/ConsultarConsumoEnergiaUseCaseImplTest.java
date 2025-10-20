package com.serviciudad.application.usecase;

import com.serviciudad.domain.model.ConsumoEnergiaModel;
import com.serviciudad.domain.port.output.ConsumoEnergiaReaderPort;
import com.serviciudad.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ConsultarConsumoEnergiaUseCaseImpl.
 * 
 * Cobertura de pruebas:
 * - Consulta de consumos por cliente
 * - Consulta de consumos por periodo
 * - Consulta por cliente y periodo
 * - Manejo de listas vacías
 * - Verificación de filtros
 * - Validación de datos retornados
 * 
 * @author Equipo ServiCiudad Cali
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Use Case: Consultar Consumo Energía - Tests Unitarios")
class ConsultarConsumoEnergiaUseCaseImplTest {

    @Mock
    private ConsumoEnergiaReaderPort consumoEnergiaReader;

    @InjectMocks
    private ConsultarConsumoEnergiaUseCaseImpl useCase;

    private ClienteId clienteId;
    private Periodo periodo202501;
    private Periodo periodo202412;
    private ConsumoEnergiaModel consumo1;
    private ConsumoEnergiaModel consumo2;
    private ConsumoEnergiaModel consumo3;

    @BeforeEach
    void setUp() {
        clienteId = new ClienteId("1234567890");
        periodo202501 = Periodo.of("202501");
        periodo202412 = Periodo.of("202412");

        consumo1 = ConsumoEnergiaModel.builder()
            .clienteId(clienteId)
            .periodo(periodo202501)
            .consumo(ConsumoEnergia.of(350))
            .valorPagar(Dinero.of(new BigDecimal("180000.00")))
            .fechaLectura(LocalDate.now().minusDays(5))
            .estrato("3")
            .build();

        consumo2 = ConsumoEnergiaModel.builder()
            .clienteId(clienteId)
            .periodo(periodo202412)
            .consumo(ConsumoEnergia.of(420))
            .valorPagar(Dinero.of(new BigDecimal("215000.00")))
            .fechaLectura(LocalDate.now().minusDays(35))
            .estrato("3")
            .build();

        consumo3 = ConsumoEnergiaModel.builder()
            .clienteId(new ClienteId("9876543210"))
            .periodo(periodo202501)
            .consumo(ConsumoEnergia.of(280))
            .valorPagar(Dinero.of(new BigDecimal("145000.00")))
            .fechaLectura(LocalDate.now().minusDays(8))
            .estrato("2")
            .build();
    }

    @Test
    @DisplayName("Debe consultar consumos por cliente exitosamente")
    void debeConsultarConsumosPorCliente() {
        // Arrange
        List<ConsumoEnergiaModel> consumosEsperados = Arrays.asList(consumo1, consumo2);
        when(consumoEnergiaReader.findByClienteId(clienteId))
            .thenReturn(consumosEsperados);

        // Act
        List<ConsumoEnergiaModel> resultado = useCase.consultarConsumosPorCliente(clienteId);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(2);
        assertThat(resultado).containsExactlyInAnyOrder(consumo1, consumo2);
        
        verify(consumoEnergiaReader, times(1)).findByClienteId(clienteId);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando cliente no tiene consumos")
    void debeRetornarListaVaciaCuandoClienteNoTieneConsumos() {
        // Arrange
        ClienteId clienteSinConsumos = new ClienteId("0000000000");
        when(consumoEnergiaReader.findByClienteId(clienteSinConsumos))
            .thenReturn(Collections.emptyList());

        // Act
        List<ConsumoEnergiaModel> resultado = useCase.consultarConsumosPorCliente(clienteSinConsumos);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEmpty();
        
        verify(consumoEnergiaReader, times(1)).findByClienteId(clienteSinConsumos);
    }

    @Test
    @DisplayName("Debe consultar consumos por cliente y periodo exitosamente")
    void debeConsultarConsumosPorClienteYPeriodo() {
        // Arrange
        List<ConsumoEnergiaModel> consumosEsperados = List.of(consumo1);
        when(consumoEnergiaReader.findByClienteIdAndPeriodo(clienteId, periodo202501))
            .thenReturn(consumosEsperados);

        // Act
        List<ConsumoEnergiaModel> resultado = useCase.consultarConsumosPorPeriodo(clienteId, periodo202501);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(1);
        assertThat(resultado).containsExactly(consumo1);
        assertThat(resultado.get(0).getPeriodo()).isEqualTo(periodo202501);
        assertThat(resultado.get(0).getClienteId()).isEqualTo(clienteId);
        
        verify(consumoEnergiaReader, times(1))
            .findByClienteIdAndPeriodo(clienteId, periodo202501);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando periodo no tiene consumos")
    void debeRetornarListaVaciaCuandoPeriodoNoTieneConsumos() {
        // Arrange
        Periodo periodoSinConsumos = Periodo.of("202311");
        when(consumoEnergiaReader.findByClienteIdAndPeriodo(clienteId, periodoSinConsumos))
            .thenReturn(Collections.emptyList());

        // Act
        List<ConsumoEnergiaModel> resultado = useCase.consultarConsumosPorPeriodo(clienteId, periodoSinConsumos);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEmpty();
        
        verify(consumoEnergiaReader, times(1))
            .findByClienteIdAndPeriodo(clienteId, periodoSinConsumos);
    }

    @Test
    @DisplayName("Debe consultar consumos elevados exitosamente")
    void debeConsultarConsumosElevados() {
        // Arrange
        ConsumoEnergiaModel consumoAlto = ConsumoEnergiaModel.builder()
            .clienteId(clienteId)
            .periodo(Periodo.of("202502"))
            .consumo(ConsumoEnergia.of(850)) // Consumo elevado
            .valorPagar(Dinero.of(new BigDecimal("425000.00")))
            .fechaLectura(LocalDate.now().minusDays(2))
            .estrato("3")
            .build();

        List<ConsumoEnergiaModel> consumosElevados = List.of(consumoAlto);
        when(consumoEnergiaReader.findConsumosElevados(clienteId))
            .thenReturn(consumosElevados);

        // Act
        List<ConsumoEnergiaModel> resultado = useCase.consultarConsumosElevados(clienteId);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getConsumo().getKilovatiosHora()).isGreaterThan(500);
        
        verify(consumoEnergiaReader, times(1)).findConsumosElevados(clienteId);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay consumos elevados")
    void debeRetornarListaVaciaCuandoNoHayConsumosElevados() {
        // Arrange
        when(consumoEnergiaReader.findConsumosElevados(clienteId))
            .thenReturn(Collections.emptyList());

        // Act
        List<ConsumoEnergiaModel> resultado = useCase.consultarConsumosElevados(clienteId);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEmpty();
        
        verify(consumoEnergiaReader, times(1)).findConsumosElevados(clienteId);
    }

    @Test
    @DisplayName("Debe validar que consumos retornados tienen datos correctos")
    void debeValidarDatosCorrectosEnConsumos() {
        // Arrange
        List<ConsumoEnergiaModel> consumosEsperados = Arrays.asList(consumo1, consumo2);
        when(consumoEnergiaReader.findByClienteId(clienteId))
            .thenReturn(consumosEsperados);

        // Act
        List<ConsumoEnergiaModel> resultado = useCase.consultarConsumosPorCliente(clienteId);

        // Assert
        assertThat(resultado).allMatch(c -> c.getClienteId() != null);
        assertThat(resultado).allMatch(c -> c.getPeriodo() != null);
        assertThat(resultado).allMatch(c -> c.getConsumo() != null);
        assertThat(resultado).allMatch(c -> c.getValorPagar() != null);
        assertThat(resultado).allMatch(c -> !c.getValorPagar().esNegativo());
        assertThat(resultado).allMatch(c -> c.getEstrato() != null);
    }

    @Test
    @DisplayName("Debe verificar métodos de dominio en consumos")
    void debeVerificarMetodosDeDominio() {
        // Arrange
        List<ConsumoEnergiaModel> consumos = List.of(consumo1);
        when(consumoEnergiaReader.findByClienteIdAndPeriodo(clienteId, periodo202501))
            .thenReturn(consumos);

        // Act
        List<ConsumoEnergiaModel> resultado = useCase.consultarConsumosPorPeriodo(clienteId, periodo202501);

        // Assert
        assertThat(resultado).isNotEmpty();
        ConsumoEnergiaModel consumo = resultado.get(0);
        
        assertThat(consumo.perteneceAEstrato("3")).isTrue();
        assertThat(consumo.perteneceAEstrato("2")).isFalse();
        assertThat(consumo.esDelMismoPeriodoQue(consumo1)).isTrue();
        assertThat(consumo.esDelMismoPeriodoQue(consumo2)).isFalse();
        assertThat(consumo.esDelMismoClienteQue(consumo1)).isTrue();
        assertThat(consumo.esDelMismoClienteQue(consumo3)).isFalse();
    }
}
