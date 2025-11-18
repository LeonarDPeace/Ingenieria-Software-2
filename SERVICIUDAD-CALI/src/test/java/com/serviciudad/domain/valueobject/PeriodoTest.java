package com.serviciudad.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para el Value Object Periodo.
 * 
 * Verifica:
 * - Validación de formato YYYYMM (6 dígitos)
 * - Validación de rangos (mes 1-12, año razonable)
 * - Métodos de extracción (getYear, getMonth)
 * - Inmutabilidad y comparación
 * 
 * @author Equipo ServiCiudad Cali
 * @version 1.0
 */
@DisplayName("Value Object: Periodo - Tests Unitarios")
class PeriodoTest {
    
    @Test
    @DisplayName("Debe crear Periodo válido con formato YYYYMM")
    void debeCrearPeriodoValido() {
        // Arrange
        String valor = "202501";
        
        // Act
        Periodo periodo = new Periodo(valor);
        
        // Assert
        assertThat(periodo).isNotNull();
        assertThat(periodo.getValor()).isEqualTo(valor);
        assertThat(periodo.getYear()).isEqualTo(2025);
        assertThat(periodo.getMonth()).isEqualTo(1);
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si Periodo es nulo")
    void debeLanzarExcepcionSiPeriodoEsNulo() {
        // Arrange, Act & Assert
        assertThatThrownBy(() -> new Periodo(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Periodo debe tener formato YYYYMM");
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si Periodo está vacío")
    void debeLanzarExcepcionSiPeriodoEstaVacio() {
        // Arrange, Act & Assert
        assertThatThrownBy(() -> new Periodo(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Periodo debe tener formato YYYYMM");
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si Periodo no tiene 6 dígitos")
    void debeLanzarExcepcionSiPeriodoNoTiene6Digitos() {
        // Arrange, Act & Assert - Menos de 6 dígitos
        assertThatThrownBy(() -> new Periodo("2025"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Periodo debe tener formato YYYYMM");
        
        // Más de 6 dígitos
        assertThatThrownBy(() -> new Periodo("2025011"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Periodo debe tener formato YYYYMM");
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si Periodo contiene caracteres no numéricos")
    void debeLanzarExcepcionSiPeriodoContieneNoNumericos() {
        // Arrange, Act & Assert - Letras
        assertThatThrownBy(() -> new Periodo("2025AB"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Periodo debe tener formato YYYYMM");
        
        // Caracteres especiales
        assertThatThrownBy(() -> new Periodo("2025-01"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Periodo debe tener formato YYYYMM");
    }
    
    @Test
    @DisplayName("Debe extraer año correctamente")
    void debeExtraerAnioCorrectamente() {
        // Arrange
        Periodo periodo = Periodo.of("202512");
        
        // Act
        int year = periodo.getYear();
        
        // Assert
        assertThat(year).isEqualTo(2025);
    }
    
    @Test
    @DisplayName("Debe extraer mes correctamente")
    void debeExtraerMesCorrectamente() {
        // Arrange
        Periodo periodo = Periodo.of("202512");
        
        // Act
        int month = periodo.getMonth();
        
        // Assert
        assertThat(month).isEqualTo(12);
    }
    
    @Test
    @DisplayName("Debe validar mes entre 1 y 12")
    void debeValidarMesEntre1Y12() {
        // Arrange, Act & Assert - Mes 0
        assertThatThrownBy(() -> new Periodo("202500"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Mes debe estar entre 1 y 12");
        
        // Mes 13
        assertThatThrownBy(() -> new Periodo("202513"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Mes debe estar entre 1 y 12");
    }
    
    @Test
    @DisplayName("Debe validar año razonable (2020-2030)")
    void debeValidarAnioRazonable() {
        // Arrange, Act & Assert - Año muy antiguo
        assertThatThrownBy(() -> new Periodo("201912"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Año debe estar entre 2020 y 2030");
        
        // Año futuro lejano
        assertThatThrownBy(() -> new Periodo("203101"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Año debe estar entre 2020 y 2030");
    }
    
    @Test
    @DisplayName("Debe crear Periodo con método factory of()")
    void debeCrearPeriodoConMetodoFactory() {
        // Arrange
        String valor = "202407";
        
        // Act
        Periodo periodo = Periodo.of(valor);
        
        // Assert
        assertThat(periodo).isNotNull();
        assertThat(periodo.getValor()).isEqualTo(valor);
        assertThat(periodo.getYear()).isEqualTo(2024);
        assertThat(periodo.getMonth()).isEqualTo(7);
    }
    
    @Test
    @DisplayName("Periodos con mismo valor deben ser iguales")
    void periodosConMismoValorDebenSerIguales() {
        // Arrange
        Periodo periodo1 = Periodo.of("202501");
        Periodo periodo2 = Periodo.of("202501");
        
        // Act & Assert
        assertThat(periodo1).isEqualTo(periodo2);
        assertThat(periodo1.hashCode()).isEqualTo(periodo2.hashCode());
    }
    
    @Test
    @DisplayName("Periodos con diferente valor deben ser diferentes")
    void periodosConDiferenteValorDebenSerDiferentes() {
        // Arrange
        Periodo periodo1 = Periodo.of("202501");
        Periodo periodo2 = Periodo.of("202502");
        
        // Act & Assert
        assertThat(periodo1).isNotEqualTo(periodo2);
    }
}
