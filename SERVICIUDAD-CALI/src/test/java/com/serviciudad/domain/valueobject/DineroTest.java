package com.serviciudad.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para el Value Object Dinero.
 * 
 * Verifica:
 * - Validación de montos (no nulo)
 * - Redondeo a 2 decimales
 * - Operaciones aritméticas (suma, resta)
 * - Comparaciones (mayor, menor, igual)
 * - Factory methods
 * - Inmutabilidad
 * 
 * @author Equipo ServiCiudad Cali
 * @version 1.0
 */
@DisplayName("Value Object: Dinero - Tests Unitarios")
class DineroTest {
    
    @Test
    @DisplayName("Debe crear Dinero válido con BigDecimal")
    void debeCrearDineroValidoConBigDecimal() {
        // Arrange
        BigDecimal monto = new BigDecimal("100.50");
        
        // Act
        Dinero dinero = new Dinero(monto);
        
        // Assert
        assertThat(dinero).isNotNull();
        assertThat(dinero.getMonto()).isEqualByComparingTo(new BigDecimal("100.50"));
    }
    
    @Test
    @DisplayName("Debe crear Dinero con 2 decimales redondeados")
    void debeCrearDineroConDosDecimalesRedondeados() {
        // Arrange
        BigDecimal montoConMuchasDecimales = new BigDecimal("100.456789");
        
        // Act
        Dinero dinero = new Dinero(montoConMuchasDecimales);
        
        // Assert
        assertThat(dinero.getMonto()).isEqualByComparingTo(new BigDecimal("100.46"));
        assertThat(dinero.getMonto().scale()).isEqualTo(2);
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si monto es nulo")
    void debeLanzarExcepcionSiMontoEsNulo() {
        // Arrange, Act & Assert
        assertThatThrownBy(() -> new Dinero(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Monto no puede ser nulo");
    }
    
    @Test
    @DisplayName("Debe crear Dinero con método factory of(BigDecimal)")
    void debeCrearDineroConMetodoFactoryBigDecimal() {
        // Arrange
        BigDecimal monto = new BigDecimal("250.75");
        
        // Act
        Dinero dinero = Dinero.of(monto);
        
        // Assert
        assertThat(dinero).isNotNull();
        assertThat(dinero.getMonto()).isEqualByComparingTo(monto);
    }
    
    @Test
    @DisplayName("Debe crear Dinero con método factory of(double)")
    void debeCrearDineroConMetodoFactoryDouble() {
        // Arrange
        double monto = 150.99;
        
        // Act
        Dinero dinero = Dinero.of(monto);
        
        // Assert
        assertThat(dinero).isNotNull();
        assertThat(dinero.getMonto()).isEqualByComparingTo(new BigDecimal("150.99"));
    }
    
    @Test
    @DisplayName("Debe crear Dinero con método factory of(String)")
    void debeCrearDineroConMetodoFactoryString() {
        // Arrange
        String monto = "1000.50";
        
        // Act
        Dinero dinero = Dinero.of(monto);
        
        // Assert
        assertThat(dinero).isNotNull();
        assertThat(dinero.getMonto()).isEqualByComparingTo(new BigDecimal("1000.50"));
    }
    
    @Test
    @DisplayName("Debe crear Dinero cero con método cero()")
    void debeCrearDineroCeroConMetodoCero() {
        // Act
        Dinero dinero = Dinero.cero();
        
        // Assert
        assertThat(dinero).isNotNull();
        assertThat(dinero.getMonto()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(dinero.esCero()).isTrue();
    }
    
    @Test
    @DisplayName("Debe detectar si Dinero es negativo")
    void debeDetectarSiDineroEsNegativo() {
        // Arrange
        Dinero dineroPositivo = Dinero.of(new BigDecimal("100.00"));
        Dinero dineroNegativo = Dinero.of(new BigDecimal("-50.00"));
        Dinero dineroCero = Dinero.cero();
        
        // Act & Assert
        assertThat(dineroPositivo.esNegativo()).isFalse();
        assertThat(dineroNegativo.esNegativo()).isTrue();
        assertThat(dineroCero.esNegativo()).isFalse();
    }
    
    @Test
    @DisplayName("Debe detectar si Dinero es cero")
    void debeDetectarSiDineroEsCero() {
        // Arrange
        Dinero dineroCero = Dinero.cero();
        Dinero dineroNoZero = Dinero.of(new BigDecimal("0.01"));
        
        // Act & Assert
        assertThat(dineroCero.esCero()).isTrue();
        assertThat(dineroNoZero.esCero()).isFalse();
    }
    
    @Test
    @DisplayName("Debe sumar dos cantidades de Dinero")
    void debeSumarDosCantidadesDeDinero() {
        // Arrange
        Dinero dinero1 = Dinero.of(new BigDecimal("100.50"));
        Dinero dinero2 = Dinero.of(new BigDecimal("50.25"));
        
        // Act
        Dinero resultado = dinero1.sumar(dinero2);
        
        // Assert
        assertThat(resultado.getMonto()).isEqualByComparingTo(new BigDecimal("150.75"));
    }
    
    @Test
    @DisplayName("Debe restar dos cantidades de Dinero")
    void debeRestarDosCantidadesDeDinero() {
        // Arrange
        Dinero dinero1 = Dinero.of(new BigDecimal("100.50"));
        Dinero dinero2 = Dinero.of(new BigDecimal("30.25"));
        
        // Act
        Dinero resultado = dinero1.restar(dinero2);
        
        // Assert
        assertThat(resultado.getMonto()).isEqualByComparingTo(new BigDecimal("70.25"));
    }
    
    @Test
    @DisplayName("Debe comparar si un Dinero es mayor que otro")
    void debeCompararSiUnDineroEsMayorQueOtro() {
        // Arrange
        Dinero dineroMayor = Dinero.of(new BigDecimal("200.00"));
        Dinero dineroMenor = Dinero.of(new BigDecimal("100.00"));
        
        // Act & Assert
        assertThat(dineroMayor.esMayorQue(dineroMenor)).isTrue();
        assertThat(dineroMenor.esMayorQue(dineroMayor)).isFalse();
    }
    
    @Test
    @DisplayName("Debe comparar si un Dinero es menor que otro")
    void debeCompararSiUnDineroEsMenorQueOtro() {
        // Arrange
        Dinero dineroMenor = Dinero.of(new BigDecimal("100.00"));
        Dinero dineroMayor = Dinero.of(new BigDecimal("200.00"));
        
        // Act & Assert
        assertThat(dineroMenor.esMenorQue(dineroMayor)).isTrue();
        assertThat(dineroMayor.esMenorQue(dineroMenor)).isFalse();
    }
    
    @Test
    @DisplayName("Debe comparar si dos Dineros son iguales")
    void debeCompararSiDosDinerossonIguales() {
        // Arrange
        Dinero dinero1 = Dinero.of(new BigDecimal("100.00"));
        Dinero dinero2 = Dinero.of(new BigDecimal("100.00"));
        Dinero dinero3 = Dinero.of(new BigDecimal("150.00"));
        
        // Act & Assert
        assertThat(dinero1.esIgualA(dinero2)).isTrue();
        assertThat(dinero1.esIgualA(dinero3)).isFalse();
    }
    
    @Test
    @DisplayName("Operaciones aritméticas deben mantener 2 decimales")
    void operacionesAritmeticasDebenMantenerDosDecimales() {
        // Arrange
        Dinero dinero1 = Dinero.of(new BigDecimal("10.333"));
        Dinero dinero2 = Dinero.of(new BigDecimal("5.666"));
        
        // Act
        Dinero suma = dinero1.sumar(dinero2);
        Dinero resta = dinero1.restar(dinero2);
        
        // Assert
        assertThat(suma.getMonto().scale()).isEqualTo(2);
        assertThat(resta.getMonto().scale()).isEqualTo(2);
    }
    
    @Test
    @DisplayName("Dinero debe ser inmutable")
    void dineroDebeSerInmutable() {
        // Arrange
        Dinero dineroOriginal = Dinero.of(new BigDecimal("100.00"));
        BigDecimal montoOriginal = dineroOriginal.getMonto();
        
        // Act - Realizar operaciones
        Dinero dineroSumado = dineroOriginal.sumar(Dinero.of(new BigDecimal("50.00")));
        
        // Assert - El original no debe cambiar
        assertThat(dineroOriginal.getMonto()).isEqualByComparingTo(montoOriginal);
        assertThat(dineroSumado).isNotEqualTo(dineroOriginal);
    }
}
