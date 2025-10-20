package com.serviciudad.exception;

import com.serviciudad.domain.exception.FacturaDuplicadaException;
import com.serviciudad.domain.exception.FacturaNoEncontradaException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para la API.
 * 
 * Captura y maneja todas las excepciones lanzadas por los controladores,
 * transformandolas en respuestas HTTP apropiadas con mensajes de error
 * estructurados.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @Autowired
    private Environment environment;

    /**
     * Maneja excepciones cuando una factura no es encontrada.
     * 
     * @param ex Excepcion lanzada
     * @param request Peticion web
     * @return Respuesta con codigo 404
     */
    @ExceptionHandler(FacturaNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleFacturaNoEncontrada(
            FacturaNoEncontradaException ex, WebRequest request) {
        log.error("Factura no encontrada: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Factura no encontrada")
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja excepciones cuando se intenta crear una factura duplicada.
     * 
     * @param ex Excepcion lanzada
     * @param request Peticion web
     * @return Respuesta con codigo 409
     */
    @ExceptionHandler(FacturaDuplicadaException.class)
    public ResponseEntity<ErrorResponse> handleFacturaDuplicada(
            FacturaDuplicadaException ex, WebRequest request) {
        log.error("Factura duplicada: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Factura duplicada")
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    /**
     * Maneja excepciones de validacion de datos.
     * 
     * @param ex Excepcion de validacion
     * @param request Peticion web
     * @return Respuesta con codigo 400 y detalles de los errores
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, WebRequest request) {
        log.error("Error de validacion: {}", ex.getMessage());
        
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String campo = ((FieldError) error).getField();
            String mensaje = error.getDefaultMessage();
            errores.put(campo, mensaje);
        });
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Error de validacion")
                .message("Datos de entrada invalidos")
                .path(request.getDescription(false).replace("uri=", ""))
                .detalles(errores)
                .build();
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones genericas no capturadas por otros handlers.
     * 
     * Implementa sanitización de logs según el entorno:
     * - PRODUCCIÓN: Solo loguea el mensaje de error (sin stack trace completo)
     * - DESARROLLO/TEST: Loguea stack trace completo para debugging
     * 
     * Esto previene la exposición de información sensible en logs de producción
     * como rutas de archivos, configuraciones internas, etc.
     * 
     * @param ex Excepcion lanzada
     * @param request Peticion web
     * @return Respuesta con codigo 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {
        
        // Verificar si estamos en producción
        boolean isProduction = Arrays.asList(environment.getActiveProfiles())
            .contains("prod");
        
        // Sanitizar logs según el entorno
        if (isProduction) {
            // PRODUCCIÓN: Solo mensaje (sin stack trace)
            log.error("Error inesperado: {} | Path: {} | Exception: {}",
                     ex.getMessage(),
                     request.getDescription(false).replace("uri=", ""),
                     ex.getClass().getSimpleName());
        } else {
            // DESARROLLO: Stack trace completo para debugging
            log.error("Error inesperado: {} | Path: {}",
                     ex.getMessage(),
                     request.getDescription(false).replace("uri=", ""),
                     ex);  // Incluye stack trace completo
        }
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Error interno del servidor")
                .message("Ha ocurrido un error inesperado. Por favor contacte al administrador.")
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Maneja excepciones de argumentos ilegales.
     * 
     * @param ex Excepcion lanzada
     * @param request Peticion web
     * @return Respuesta con codigo 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {
        log.error("Argumento ilegal: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Argumento invalido")
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
