package com.serviciudad.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para el Value Object ConsumoEnergia.
 * 
 * Verifica:
 * - Validación de kilovatios hora (no negativos, límites razonables)
 * - Factory methods
 * - Clasificación de consumo (alto, bajo, normal)
 * - Inmutabilidad
 * 
 * @author Equipo ServiCiudad Cali
 * @version 1.0
 */
@DisplayName("Value Object: ConsumoEnergia - Tests Unitarios")
class ConsumoEnergiaTest {
    
    @Test
    @DisplayName("Debe crear ConsumoEnergia válido con int")
    void debeCrearConsumoEnergiaValidoConInt() {
        // Arrange
        int kilovatiosHora = 300;
        
        // Act
        ConsumoEnergia consumo = new ConsumoEnergia(kilovatiosHora);
        
        // Assert
        assertThat(consumo).isNotNull();
        assertThat(consumo.getKilovatiosHora()).isEqualTo(kilovatiosHora);
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si kilovatios hora es negativo")
    void debeLanzarExcepcionSiKilovatiosHoraEsNegativo() {
        // Arrange
        int kwNegativo = -100;
        
        // Act & Assert
        assertThatThrownBy(() -> new ConsumoEnergia(kwNegativo))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Consumo energia no puede ser negativo");
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si kilovatios hora excede límite")
    void debeLanzarExcepcionSiKilovatiosHoraExcedeLimite() {
        // Arrange
        int kwExcesivo = 100001;
        
        // Act & Assert
        assertThatThrownBy(() -> new ConsumoEnergia(kwExcesivo))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Consumo energia excede limite razonable");
    }
    
    @Test
    @DisplayName("Debe crear ConsumoEnergia con método factory of()")
    void debeCrearConsumoEnergiaConMetodoFactory() {
        // Arrange
        int kilovatiosHora = 450;
        
        // Act
        ConsumoEnergia consumo = ConsumoEnergia.of(kilovatiosHora);
        
        // Assert
        assertThat(consumo).isNotNull();
        assertThat(consumo.getKilovatiosHora()).isEqualTo(kilovatiosHora);
    }
    
    @Test
    @DisplayName("Debe crear ConsumoEnergia cero")
    void debeCrearConsumoEnergiaCero() {
        // Arrange & Act
        ConsumoEnergia consumo = ConsumoEnergia.of(0);
        
        // Assert
        assertThat(consumo).isNotNull();
        assertThat(consumo.getKilovatiosHora()).isEqualTo(0);
    }
    
    @Test
    @DisplayName("Debe comparar dos consumos de energía correctamente")
    void debeCompararDosConsumosDeEnergiaCorrectamente() {
        // Arrange
        ConsumoEnergia consumo1 = ConsumoEnergia.of(300);
        ConsumoEnergia consumo2 = ConsumoEnergia.of(300);
        ConsumoEnergia consumo3 = ConsumoEnergia.of(500);
        
        // Act & Assert
        assertThat(consumo1).isEqualTo(consumo2);
        assertThat(consumo1.hashCode()).isEqualTo(consumo2.hashCode());
        assertThat(consumo1).isNotEqualTo(consumo3);
    }
    
    @Test
    @DisplayName("Debe detectar consumo alto (mayor a 500 kWh)")
    void debeDetectarConsumoAlto() {
        // Arrange
        ConsumoEnergia consumoAlto = ConsumoEnergia.of(600);
        ConsumoEnergia consumoNoAlto = ConsumoEnergia.of(500);
        
        // Act & Assert
        assertThat(consumoAlto.esAlto()).isTrue();
        assertThat(consumoNoAlto.esAlto()).isFalse();
    }
    
    @Test
    @DisplayName("Debe detectar consumo bajo (menor a 100 kWh)")
    void debeDetectarConsumoBajo() {
        // Arrange
        ConsumoEnergia consumoBajo = ConsumoEnergia.of(50);
        ConsumoEnergia consumoNoBajo = ConsumoEnergia.of(100);
        
        // Act & Assert
        assertThat(consumoBajo.esBajo()).isTrue();
        assertThat(consumoNoBajo.esBajo()).isFalse();
    }
    
    @Test
    @DisplayName("Debe detectar consumo normal (entre 100 y 500 kWh)")
    void debeDetectarConsumoNormal() {
        // Arrange
        ConsumoEnergia consumoNormal1 = ConsumoEnergia.of(100);
        ConsumoEnergia consumoNormal2 = ConsumoEnergia.of(300);
        ConsumoEnergia consumoNormal3 = ConsumoEnergia.of(500);
        ConsumoEnergia consumoBajo = ConsumoEnergia.of(50);
        ConsumoEnergia consumoAlto = ConsumoEnergia.of(600);
        
        // Act & Assert
        assertThat(consumoNormal1.esNormal()).isTrue();
        assertThat(consumoNormal2.esNormal()).isTrue();
        assertThat(consumoNormal3.esNormal()).isTrue();
        assertThat(consumoBajo.esNormal()).isFalse();
        assertThat(consumoAlto.esNormal()).isFalse();
    }
}
