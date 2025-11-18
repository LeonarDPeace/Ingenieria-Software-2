package com.serviciudad.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para el Value Object ConsumoAgua.
 * 
 * Verifica:
 * - Validación de metros cúbicos (no negativos, límites razonables)
 * - Factory methods
 * - Clasificación de consumo (alto, bajo, normal)
 * - Inmutabilidad
 * 
 * @author Equipo ServiCiudad Cali
 * @version 1.0
 */
@DisplayName("Value Object: ConsumoAgua - Tests Unitarios")
class ConsumoAguaTest {
    
    @Test
    @DisplayName("Debe crear ConsumoAgua válido con int")
    void debeCrearConsumoAguaValidoConInt() {
        // Arrange
        int metrosCubicos = 25;
        
        // Act
        ConsumoAgua consumo = new ConsumoAgua(metrosCubicos);
        
        // Assert
        assertThat(consumo).isNotNull();
        assertThat(consumo.getMetrosCubicos()).isEqualTo(metrosCubicos);
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si metros cúbicos es negativo")
    void debeLanzarExcepcionSiMetrosCubicosEsNegativo() {
        // Arrange
        int metrosNegativos = -10;
        
        // Act & Assert
        assertThatThrownBy(() -> new ConsumoAgua(metrosNegativos))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Consumo no puede ser negativo");
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si metros cúbicos excede límite")
    void debeLanzarExcepcionSiMetrosCubicosExcedeLimite() {
        // Arrange
        int metrosExcesivos = 10001;
        
        // Act & Assert
        assertThatThrownBy(() -> new ConsumoAgua(metrosExcesivos))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Consumo excede limite razonable");
    }
    
    @Test
    @DisplayName("Debe crear ConsumoAgua con método factory of()")
    void debeCrearConsumoAguaConMetodoFactory() {
        // Arrange
        int metrosCubicos = 30;
        
        // Act
        ConsumoAgua consumo = ConsumoAgua.of(metrosCubicos);
        
        // Assert
        assertThat(consumo).isNotNull();
        assertThat(consumo.getMetrosCubicos()).isEqualTo(metrosCubicos);
    }
    
    @Test
    @DisplayName("Debe crear ConsumoAgua cero")
    void debeCrearConsumoAguaCero() {
        // Arrange & Act
        ConsumoAgua consumo = ConsumoAgua.of(0);
        
        // Assert
        assertThat(consumo).isNotNull();
        assertThat(consumo.getMetrosCubicos()).isEqualTo(0);
    }
    
    @Test
    @DisplayName("Debe comparar dos consumos de agua correctamente")
    void debeCompararDosConsumosDeAguaCorrectamente() {
        // Arrange
        ConsumoAgua consumo1 = ConsumoAgua.of(20);
        ConsumoAgua consumo2 = ConsumoAgua.of(20);
        ConsumoAgua consumo3 = ConsumoAgua.of(30);
        
        // Act & Assert
        assertThat(consumo1).isEqualTo(consumo2);
        assertThat(consumo1.hashCode()).isEqualTo(consumo2.hashCode());
        assertThat(consumo1).isNotEqualTo(consumo3);
    }
    
    @Test
    @DisplayName("Debe detectar consumo alto (mayor a 50 m³)")
    void debeDetectarConsumoAlto() {
        // Arrange
        ConsumoAgua consumoAlto = ConsumoAgua.of(60);
        ConsumoAgua consumoNoAlto = ConsumoAgua.of(50);
        
        // Act & Assert
        assertThat(consumoAlto.esAlto()).isTrue();
        assertThat(consumoNoAlto.esAlto()).isFalse();
    }
    
    @Test
    @DisplayName("Debe detectar consumo bajo (menor a 10 m³)")
    void debeDetectarConsumoBajo() {
        // Arrange
        ConsumoAgua consumoBajo = ConsumoAgua.of(5);
        ConsumoAgua consumoNoBajo = ConsumoAgua.of(10);
        
        // Act & Assert
        assertThat(consumoBajo.esBajo()).isTrue();
        assertThat(consumoNoBajo.esBajo()).isFalse();
    }
    
    @Test
    @DisplayName("Debe detectar consumo normal (entre 10 y 50 m³)")
    void debeDetectarConsumoNormal() {
        // Arrange
        ConsumoAgua consumoNormal1 = ConsumoAgua.of(10);
        ConsumoAgua consumoNormal2 = ConsumoAgua.of(30);
        ConsumoAgua consumoNormal3 = ConsumoAgua.of(50);
        ConsumoAgua consumoBajo = ConsumoAgua.of(5);
        ConsumoAgua consumoAlto = ConsumoAgua.of(60);
        
        // Act & Assert
        assertThat(consumoNormal1.esNormal()).isTrue();
        assertThat(consumoNormal2.esNormal()).isTrue();
        assertThat(consumoNormal3.esNormal()).isTrue();
        assertThat(consumoBajo.esNormal()).isFalse();
        assertThat(consumoAlto.esNormal()).isFalse();
    }
}
