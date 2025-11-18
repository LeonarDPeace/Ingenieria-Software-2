package com.serviciudad.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para FacturaDuplicadaException.
 * 
 * Verifica:
 * - Creación correcta de la excepción
 * - Mensaje de error
 * - Causa de la excepción
 * 
 * @author Equipo ServiCiudad Cali
 * @version 1.0
 */
@DisplayName("Domain Exception: FacturaDuplicadaException - Tests Unitarios")
class FacturaDuplicadaExceptionTest {
    
    @Test
    @DisplayName("Debe crear excepción con mensaje")
    void debeCrearExcepcionConMensaje() {
        // Arrange
        String mensaje = "Factura duplicada para cliente 1234567890 en periodo 202501";
        
        // Act
        FacturaDuplicadaException exception = 
            new FacturaDuplicadaException(mensaje);
        
        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(mensaje);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
    
    @Test
    @DisplayName("Debe crear excepción con mensaje y causa")
    void debeCrearExcepcionConMensajeYCausa() {
        // Arrange
        String mensaje = "Factura duplicada para cliente 1234567890 en periodo 202501";
        Throwable causa = new IllegalStateException("Estado de base de datos inconsistente");
        
        // Act
        FacturaDuplicadaException exception = 
            new FacturaDuplicadaException(mensaje, causa);
        
        // Assert
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(mensaje);
        assertThat(exception.getCause()).isEqualTo(causa);
        assertThat(exception.getCause().getMessage())
            .isEqualTo("Estado de base de datos inconsistente");
    }
}
