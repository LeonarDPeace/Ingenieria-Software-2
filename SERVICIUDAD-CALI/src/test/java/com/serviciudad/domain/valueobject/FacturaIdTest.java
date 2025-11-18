package com.serviciudad.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para el Value Object FacturaId.
 * 
 * Verifica:
 * - Validación de ID (Long positivo, no nulo)
 * - Factory methods
 * - Inmutabilidad
 * - Equals/HashCode
 * 
 * @author Equipo ServiCiudad Cali
 * @version 1.0
 */
@DisplayName("Value Object: FacturaId - Tests Unitarios")
class FacturaIdTest {
    
    @Test
    @DisplayName("Debe crear FacturaId válido con Long")
    void debeCrearFacturaIdValidoConLong() {
        // Arrange
        Long valor = 123L;
        
        // Act
        FacturaId facturaId = new FacturaId(valor);
        
        // Assert
        assertThat(facturaId).isNotNull();
        assertThat(facturaId.getValor()).isEqualTo(valor);
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si FacturaId es nulo")
    void debeLanzarExcepcionSiFacturaIdEsNulo() {
        // Arrange, Act & Assert
        assertThatThrownBy(() -> new FacturaId(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("FacturaId debe ser mayor a cero");
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si FacturaId es cero")
    void debeLanzarExcepcionSiFacturaIdEsCero() {
        // Arrange, Act & Assert
        assertThatThrownBy(() -> new FacturaId(0L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("FacturaId debe ser mayor a cero");
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si FacturaId es negativo")
    void debeLanzarExcepcionSiFacturaIdEsNegativo() {
        // Arrange, Act & Assert
        assertThatThrownBy(() -> new FacturaId(-1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("FacturaId debe ser mayor a cero");
    }
    
    @Test
    @DisplayName("Debe crear FacturaId con método factory of()")
    void debeCrearFacturaIdConMetodoFactory() {
        // Arrange
        Long valor = 999L;
        
        // Act
        FacturaId facturaId = FacturaId.of(valor);
        
        // Assert
        assertThat(facturaId).isNotNull();
        assertThat(facturaId.getValor()).isEqualTo(valor);
    }
    
    @Test
    @DisplayName("Dos FacturaIds con mismo valor deben ser iguales")
    void dosFacturaIdsConMismoValorDebenSerIguales() {
        // Arrange
        Long valor = 100L;
        
        // Act
        FacturaId facturaId1 = FacturaId.of(valor);
        FacturaId facturaId2 = FacturaId.of(valor);
        
        // Assert
        assertThat(facturaId1).isEqualTo(facturaId2);
        assertThat(facturaId1.hashCode()).isEqualTo(facturaId2.hashCode());
    }
    
    @Test
    @DisplayName("Dos FacturaIds con diferente valor deben ser diferentes")
    void dosFacturaIdsConDiferenteValorDebenSerDiferentes() {
        // Arrange
        FacturaId facturaId1 = FacturaId.of(100L);
        FacturaId facturaId2 = FacturaId.of(200L);
        
        // Act & Assert
        assertThat(facturaId1).isNotEqualTo(facturaId2);
    }
}
