package com.serviciudad.domain.model;

import com.serviciudad.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para el modelo ConsumoEnergiaModel.
 * 
 * Verifica:
 * - Clasificación de consumo (alto/bajo)
 * - Comparaciones de periodo y cliente
 * - Validación de estrato
 * 
 * @author Equipo ServiCiudad Cali
 * @version 1.0
 */
@DisplayName("Domain Model: ConsumoEnergiaModel - Tests Unitarios")
class ConsumoEnergiaModelTest {
    
    private ConsumoEnergiaModel consumo;
    
    @BeforeEach
    void setUp() {
        consumo = ConsumoEnergiaModel.builder()
                .clienteId(new ClienteId("1234567890"))
                .periodo(Periodo.of("202501"))
                .consumo(ConsumoEnergia.of(350))
                .valorPagar(Dinero.of(new BigDecimal("250000.00")))
                .fechaLectura(LocalDate.now())
                .estrato("3")
                .build();
    }
    
    @Test
    @DisplayName("Debe identificar consumo alto correctamente")
    void debeIdentificarConsumoAlto() {
        // Arrange
        ConsumoEnergiaModel consumoAlto = ConsumoEnergiaModel.builder()
                .clienteId(new ClienteId("1234567890"))
                .periodo(Periodo.of("202501"))
                .consumo(ConsumoEnergia.of(600))
                .valorPagar(Dinero.of(new BigDecimal("400000.00")))
                .fechaLectura(LocalDate.now())
                .estrato("3")
                .build();
        
        // Act & Assert
        assertThat(consumoAlto.tieneConsumoAlto()).isTrue();
        assertThat(consumoAlto.tieneConsumoBajo()).isFalse();
    }
    
    @Test
    @DisplayName("Debe identificar consumo bajo correctamente")
    void debeIdentificarConsumoBajo() {
        // Arrange
        ConsumoEnergiaModel consumoBajo = ConsumoEnergiaModel.builder()
                .clienteId(new ClienteId("1234567890"))
                .periodo(Periodo.of("202501"))
                .consumo(ConsumoEnergia.of(50))
                .valorPagar(Dinero.of(new BigDecimal("30000.00")))
                .fechaLectura(LocalDate.now())
                .estrato("1")
                .build();
        
        // Act & Assert
        assertThat(consumoBajo.tieneConsumoBajo()).isTrue();
        assertThat(consumoBajo.tieneConsumoAlto()).isFalse();
    }
    
    @Test
    @DisplayName("Debe identificar consumo normal (ni alto ni bajo)")
    void debeIdentificarConsumoNormal() {
        // Arrange
        ConsumoEnergiaModel consumoNormal = ConsumoEnergiaModel.builder()
                .clienteId(new ClienteId("1234567890"))
                .periodo(Periodo.of("202501"))
                .consumo(ConsumoEnergia.of(300))
                .valorPagar(Dinero.of(new BigDecimal("200000.00")))
                .fechaLectura(LocalDate.now())
                .estrato("2")
                .build();
        
        // Act & Assert
        assertThat(consumoNormal.tieneConsumoAlto()).isFalse();
        assertThat(consumoNormal.tieneConsumoBajo()).isFalse();
    }
    
    @Test
    @DisplayName("Debe comparar estrato correctamente (case-insensitive)")
    void debeCompararEstratoCorrectamente() {
        // Act & Assert
        assertThat(consumo.perteneceAEstrato("3")).isTrue();
        assertThat(consumo.perteneceAEstrato("3")).isTrue();
        assertThat(consumo.perteneceAEstrato("2")).isFalse();
        assertThat(consumo.perteneceAEstrato("4")).isFalse();
    }
    
    @Test
    @DisplayName("Debe comparar periodo correctamente")
    void debeCompararPeriodoCorrectamente() {
        // Arrange
        ConsumoEnergiaModel consumoMismoPeriodo = ConsumoEnergiaModel.builder()
                .clienteId(new ClienteId("9876543210"))
                .periodo(Periodo.of("202501"))
                .consumo(ConsumoEnergia.of(200))
                .valorPagar(Dinero.of(new BigDecimal("150000.00")))
                .fechaLectura(LocalDate.now())
                .estrato("2")
                .build();
        
        ConsumoEnergiaModel consumoOtroPeriodo = ConsumoEnergiaModel.builder()
                .clienteId(new ClienteId("1234567890"))
                .periodo(Periodo.of("202412"))
                .consumo(ConsumoEnergia.of(200))
                .valorPagar(Dinero.of(new BigDecimal("150000.00")))
                .fechaLectura(LocalDate.now())
                .estrato("2")
                .build();
        
        // Act & Assert
        assertThat(consumo.esDelMismoPeriodoQue(consumoMismoPeriodo)).isTrue();
        assertThat(consumo.esDelMismoPeriodoQue(consumoOtroPeriodo)).isFalse();
    }
    
    @Test
    @DisplayName("Debe comparar cliente correctamente")
    void debeCompararClienteCorrectamente() {
        // Arrange
        ConsumoEnergiaModel consumoMismoCliente = ConsumoEnergiaModel.builder()
                .clienteId(new ClienteId("1234567890"))
                .periodo(Periodo.of("202412"))
                .consumo(ConsumoEnergia.of(200))
                .valorPagar(Dinero.of(new BigDecimal("150000.00")))
                .fechaLectura(LocalDate.now())
                .estrato("2")
                .build();
        
        ConsumoEnergiaModel consumoOtroCliente = ConsumoEnergiaModel.builder()
                .clienteId(new ClienteId("9876543210"))
                .periodo(Periodo.of("202501"))
                .consumo(ConsumoEnergia.of(200))
                .valorPagar(Dinero.of(new BigDecimal("150000.00")))
                .fechaLectura(LocalDate.now())
                .estrato("2")
                .build();
        
        // Act & Assert
        assertThat(consumo.esDelMismoClienteQue(consumoMismoCliente)).isTrue();
        assertThat(consumo.esDelMismoClienteQue(consumoOtroCliente)).isFalse();
    }
}
