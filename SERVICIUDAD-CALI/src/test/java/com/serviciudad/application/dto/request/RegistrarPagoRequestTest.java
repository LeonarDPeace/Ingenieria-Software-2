package com.serviciudad.application.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para RegistrarPagoRequest.
 * 
 * Verifica:
 * - Validaciones de Jakarta Bean Validation
 * - Creación correcta del DTO
 * - Getters y Setters
 * 
 * @author Equipo ServiCiudad Cali
 * @version 1.0
 */
@DisplayName("DTO Request: RegistrarPagoRequest - Tests Unitarios")
class RegistrarPagoRequestTest {
    
    private Validator validator;
    
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    @DisplayName("Debe crear DTO válido con facturaId positivo")
    void debeCrearDtoValidoConFacturaIdPositivo() {
        // Arrange
        RegistrarPagoRequest request = new RegistrarPagoRequest(123L);
        
        // Act
        Set<ConstraintViolation<RegistrarPagoRequest>> violations = 
            validator.validate(request);
        
        // Assert
        assertThat(violations).isEmpty();
        assertThat(request.getFacturaId()).isEqualTo(123L);
    }
    
    @Test
    @DisplayName("Debe fallar validación cuando facturaId es nulo")
    void debeFallarValidacionCuandoFacturaIdEsNulo() {
        // Arrange
        RegistrarPagoRequest request = new RegistrarPagoRequest(null);
        
        // Act
        Set<ConstraintViolation<RegistrarPagoRequest>> violations = 
            validator.validate(request);
        
        // Assert
        assertThat(violations).hasSize(1);
        ConstraintViolation<RegistrarPagoRequest> violation = violations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("El ID de la factura es obligatorio");
        assertThat(violation.getPropertyPath().toString()).isEqualTo("facturaId");
    }
    
    @Test
    @DisplayName("Debe fallar validación cuando facturaId es cero")
    void debeFallarValidacionCuandoFacturaIdEsCero() {
        // Arrange
        RegistrarPagoRequest request = new RegistrarPagoRequest(0L);
        
        // Act
        Set<ConstraintViolation<RegistrarPagoRequest>> violations = 
            validator.validate(request);
        
        // Assert
        assertThat(violations).hasSize(1);
        ConstraintViolation<RegistrarPagoRequest> violation = violations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("El ID de la factura debe ser positivo");
        assertThat(violation.getPropertyPath().toString()).isEqualTo("facturaId");
    }
    
    @Test
    @DisplayName("Debe fallar validación cuando facturaId es negativo")
    void debeFallarValidacionCuandoFacturaIdEsNegativo() {
        // Arrange
        RegistrarPagoRequest request = new RegistrarPagoRequest(-10L);
        
        // Act
        Set<ConstraintViolation<RegistrarPagoRequest>> violations = 
            validator.validate(request);
        
        // Assert
        assertThat(violations).hasSize(1);
        ConstraintViolation<RegistrarPagoRequest> violation = violations.iterator().next();
        assertThat(violation.getMessage()).isEqualTo("El ID de la factura debe ser positivo");
        assertThat(violation.getPropertyPath().toString()).isEqualTo("facturaId");
    }
    
    @Test
    @DisplayName("Debe permitir usar constructor sin argumentos")
    void debePermitirUsarConstructorSinArgumentos() {
        // Act
        RegistrarPagoRequest request = new RegistrarPagoRequest();
        request.setFacturaId(456L);
        
        // Assert
        assertThat(request.getFacturaId()).isEqualTo(456L);
    }
    
    @Test
    @DisplayName("Debe tener métodos equals y hashCode correctos (Lombok @Data)")
    void debeTenerMetodosEqualsYHashCodeCorrectos() {
        // Arrange
        RegistrarPagoRequest request1 = new RegistrarPagoRequest(100L);
        RegistrarPagoRequest request2 = new RegistrarPagoRequest(100L);
        RegistrarPagoRequest request3 = new RegistrarPagoRequest(200L);
        
        // Assert
        assertThat(request1).isEqualTo(request2);
        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
        assertThat(request1).isNotEqualTo(request3);
    }
}
