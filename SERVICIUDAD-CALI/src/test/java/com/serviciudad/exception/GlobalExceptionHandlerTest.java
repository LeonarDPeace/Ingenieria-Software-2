package com.serviciudad.exception;

import com.serviciudad.application.dto.request.RegistrarPagoRequest;
import com.serviciudad.domain.exception.FacturaDuplicadaException;
import com.serviciudad.domain.exception.FacturaNoEncontradaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para GlobalExceptionHandler.
 * 
 * Verifica:
 * - Manejo correcto de excepciones de dominio
 * - Mapeo a códigos HTTP apropiados
 * - Formato de respuestas de error
 * - Sanitización de logs según entorno
 * 
 * @author Equipo ServiCiudad Cali
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Exception Handler: GlobalExceptionHandler - Tests Unitarios")
class GlobalExceptionHandlerTest {
    
    @Mock
    private Environment environment;
    
    @Mock
    private WebRequest webRequest;
    
    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;
    
    @BeforeEach
    void setUp() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/test");
    }
    
    @Test
    @DisplayName("Debe manejar FacturaNoEncontradaException con código 404")
    void debeManejarFacturaNoEncontradaException() {
        // Arrange
        FacturaNoEncontradaException exception = 
            new FacturaNoEncontradaException("Factura no encontrada: 123");
        
        // Act
        ResponseEntity<ErrorResponse> response = 
            exceptionHandler.handleFacturaNoEncontrada(exception, webRequest);
        
        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getError()).isEqualTo("Factura no encontrada");
        assertThat(response.getBody().getMessage()).isEqualTo("Factura no encontrada: 123");
        assertThat(response.getBody().getPath()).isEqualTo("/api/test");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }
    
    @Test
    @DisplayName("Debe manejar FacturaDuplicadaException con código 409")
    void debeManejarFacturaDuplicadaException() {
        // Arrange
        FacturaDuplicadaException exception = 
            new FacturaDuplicadaException("Factura duplicada para cliente 1234567890 en periodo 202501");
        
        // Act
        ResponseEntity<ErrorResponse> response = 
            exceptionHandler.handleFacturaDuplicada(exception, webRequest);
        
        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getError()).isEqualTo("Factura duplicada");
        assertThat(response.getBody().getMessage()).contains("duplicada");
        assertThat(response.getBody().getPath()).isEqualTo("/api/test");
    }
    
    @Test
    @DisplayName("Debe manejar IllegalArgumentException con código 400")
    void debeManejarIllegalArgumentException() {
        // Arrange
        IllegalArgumentException exception = 
            new IllegalArgumentException("Argumento inválido");
        
        // Act
        ResponseEntity<ErrorResponse> response = 
            exceptionHandler.handleIllegalArgument(exception, webRequest);
        
        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Argumento invalido");
        assertThat(response.getBody().getMessage()).isEqualTo("Argumento inválido");
    }
    
    @Test
    @DisplayName("Debe manejar MethodArgumentNotValidException con detalles de validación")
    void debeManejarMethodArgumentNotValidException() throws Exception {
        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);
        
        // Crear un MethodParameter mock válido
        java.lang.reflect.Method method = RegistrarPagoRequest.class.getMethod("getFacturaId");
        org.springframework.core.MethodParameter methodParameter = 
            new org.springframework.core.MethodParameter(method, -1);
        
        MethodArgumentNotValidException exception = 
            new MethodArgumentNotValidException(methodParameter, bindingResult);
        
        FieldError fieldError = new FieldError("request", "facturaId", 
            "El ID de la factura es obligatorio");
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));
        
        // Act
        ResponseEntity<ErrorResponse> response = 
            exceptionHandler.handleValidationErrors(exception, webRequest);
        
        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Error de validacion");
        assertThat(response.getBody().getMessage()).isEqualTo("Datos de entrada invalidos");
        assertThat(response.getBody().getDetalles()).isNotNull();
        assertThat(response.getBody().getDetalles()).containsKey("facturaId");
        assertThat(response.getBody().getDetalles().get("facturaId"))
            .isEqualTo("El ID de la factura es obligatorio");
    }
    
    @Test
    @DisplayName("Debe manejar Exception genérica con código 500 en entorno desarrollo")
    void debeManejarExceptionGenericaEnDesarrollo() {
        // Arrange
        Exception exception = new RuntimeException("Error inesperado");
        when(environment.getActiveProfiles()).thenReturn(new String[]{"dev"});
        
        // Act
        ResponseEntity<ErrorResponse> response = 
            exceptionHandler.handleGlobalException(exception, webRequest);
        
        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getError()).isEqualTo("Error interno del servidor");
        assertThat(response.getBody().getMessage())
            .contains("Ha ocurrido un error inesperado");
    }
    
    @Test
    @DisplayName("Debe manejar Exception genérica con código 500 en entorno producción")
    void debeManejarExceptionGenericaEnProduccion() {
        // Arrange
        Exception exception = new RuntimeException("Error inesperado");
        when(environment.getActiveProfiles()).thenReturn(new String[]{"prod"});
        
        // Act
        ResponseEntity<ErrorResponse> response = 
            exceptionHandler.handleGlobalException(exception, webRequest);
        
        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getError()).isEqualTo("Error interno del servidor");
        assertThat(response.getBody().getMessage())
            .contains("Ha ocurrido un error inesperado");
        // Verificar que no se expone información sensible
        assertThat(response.getBody().getMessage())
            .doesNotContain("RuntimeException")
            .doesNotContain("stack trace");
    }
    
    @Test
    @DisplayName("Debe sanitizar path removiendo prefijo 'uri='")
    void debeSanitizarPathRemoviendoPrefijoUri() {
        // Arrange
        when(webRequest.getDescription(false)).thenReturn("uri=/api/facturas/123");
        IllegalArgumentException exception = new IllegalArgumentException("Error");
        
        // Act
        ResponseEntity<ErrorResponse> response = 
            exceptionHandler.handleIllegalArgument(exception, webRequest);
        
        // Assert
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getPath()).isEqualTo("/api/facturas/123");
        assertThat(response.getBody().getPath()).doesNotContain("uri=");
    }
    
    @Test
    @DisplayName("Debe incluir timestamp en todas las respuestas de error")
    void debeIncluirTimestampEnTodasLasRespuestas() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("Error");
        
        // Act
        ResponseEntity<ErrorResponse> response = 
            exceptionHandler.handleIllegalArgument(exception, webRequest);
        
        // Assert
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTimestamp()).isNotNull();
        assertThat(response.getBody().getTimestamp()).isBeforeOrEqualTo(
            java.time.LocalDateTime.now());
    }
}
