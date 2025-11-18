package com.serviciudad.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para el Value Object ClienteId.
 * 
 * Verifica:
 * - Validación de formato (10 dígitos numéricos)
 * - Inmutabilidad (Lombok @Value)
 * - Factory methods
 * - Equals/HashCode
 * 
 * @author Equipo ServiCiudad Cali
 * @version 1.0
 */
@DisplayName("Value Object: ClienteId - Tests Unitarios")
class ClienteIdTest {
    
    @Test
    @DisplayName("Debe crear ClienteId válido con 10 dígitos")
    void debeCrearClienteIdValido() {
        // Arrange
        String valor = "1234567890";
        
        // Act
        ClienteId clienteId = new ClienteId(valor);
        
        // Assert
        assertThat(clienteId).isNotNull();
        assertThat(clienteId.getValor()).isEqualTo(valor);
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si ClienteId es nulo")
    void debeLanzarExcepcionSiClienteIdEsNulo() {
        // Arrange, Act & Assert
        assertThatThrownBy(() -> new ClienteId(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ClienteId no puede ser nulo");
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si ClienteId está vacío")
    void debeLanzarExcepcionSiClienteIdEstaVacio() {
        // Arrange, Act & Assert
        assertThatThrownBy(() -> new ClienteId(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ClienteId no puede ser nulo o vacio");
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si ClienteId tiene espacios")
    void debeLanzarExcepcionSiClienteIdTieneEspacios() {
        // Arrange, Act & Assert
        assertThatThrownBy(() -> new ClienteId("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ClienteId no puede ser nulo o vacio");
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si ClienteId no tiene 10 caracteres")
    void debeLanzarExcepcionSiClienteIdNoTiene10Caracteres() {
        // Arrange, Act & Assert - Menos de 10 caracteres
        assertThatThrownBy(() -> new ClienteId("123"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ClienteId debe tener 10 caracteres");
        
        // Más de 10 caracteres
        assertThatThrownBy(() -> new ClienteId("12345678901"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ClienteId debe tener 10 caracteres");
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si ClienteId contiene caracteres no numéricos")
    void debeLanzarExcepcionSiClienteIdContieneNoNumericos() {
        // Arrange, Act & Assert - Letras
        assertThatThrownBy(() -> new ClienteId("123456789A"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ClienteId debe ser numerico");
        
        // Caracteres especiales
        assertThatThrownBy(() -> new ClienteId("12-3456789"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ClienteId debe ser numerico");
        
        // Espacios en medio
        assertThatThrownBy(() -> new ClienteId("12 3456789"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ClienteId debe ser numerico");
    }
    
    @Test
    @DisplayName("Debe crear ClienteId con método factory of()")
    void debeCrearClienteIdConMetodoFactory() {
        // Arrange
        String valor = "9876543210";
        
        // Act
        ClienteId clienteId = ClienteId.of(valor);
        
        // Assert
        assertThat(clienteId).isNotNull();
        assertThat(clienteId.getValor()).isEqualTo(valor);
    }
    
    @Test
    @DisplayName("Dos ClienteIds con mismo valor deben ser iguales")
    void dosClienteIdsConMismoValorDebenSerIguales() {
        // Arrange
        String valor = "1234567890";
        
        // Act
        ClienteId clienteId1 = new ClienteId(valor);
        ClienteId clienteId2 = new ClienteId(valor);
        
        // Assert
        assertThat(clienteId1).isEqualTo(clienteId2);
        assertThat(clienteId1.hashCode()).isEqualTo(clienteId2.hashCode());
    }
    
    @Test
    @DisplayName("Dos ClienteIds con diferente valor deben ser diferentes")
    void dosClienteIdsConDiferenteValorDebenSerDiferentes() {
        // Arrange & Act
        ClienteId clienteId1 = new ClienteId("1234567890");
        ClienteId clienteId2 = new ClienteId("0987654321");
        
        // Assert
        assertThat(clienteId1).isNotEqualTo(clienteId2);
    }
}
