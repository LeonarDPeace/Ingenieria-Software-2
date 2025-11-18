package com.serviciudad.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para FacturaNoEncontradaException.
 * 
 * Verifica:
 * - Creación correcta de la excepción
 * - Mensaje de error
 * - Causa de la excepción
 * 
 * @author Equipo ServiCiudad Cali
 * @version 1.0
 */
@DisplayName("Domain Exception: FacturaNoEncontradaException - Tests Unitarios")
class FacturaNoEncontradaExceptionTest {
    
    @Test
    @DisplayName("Debe crear excepción con mensaje")
    void debeCrearExcepcionConMensaje() {
        // Arrange
        String mensaje = "Factura no encontrada: 123";
        
        // Act
        FacturaNoEncontradaException exception = 
            new FacturaNoEncontradaException(mensaje);
        
        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(mensaje);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
    
    @Test
    @DisplayName("Debe crear excepción con mensaje y causa")
    void debeCrearExcepcionConMensajeYCausa() {
        // Arrange
        String mensaje = "Factura no encontrada: 123";
        Throwable causa = new IllegalArgumentException("ID inválido");
        
        // Act
        FacturaNoEncontradaException exception = 
            new FacturaNoEncontradaException(mensaje, causa);
        
        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(mensaje);
        assertThat(exception.getCause()).isEqualTo(causa);
        assertThat(exception.getCause().getMessage()).isEqualTo("ID inválido");
    }
}
