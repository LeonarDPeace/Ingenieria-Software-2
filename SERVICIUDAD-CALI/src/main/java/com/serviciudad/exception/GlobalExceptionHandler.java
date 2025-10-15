package com.serviciudad.exception;

import com.serviciudad.adapter.exception.ArchivoLecturaException;
import com.serviciudad.service.exception.FacturaDuplicadaException;
import com.serviciudad.service.exception.FacturaNoEncontradaException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
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
     * Maneja excepciones de lectura de archivos legacy.
     * 
     * @param ex Excepcion lanzada
     * @param request Peticion web
     * @return Respuesta con codigo 500
     */
    @ExceptionHandler(ArchivoLecturaException.class)
    public ResponseEntity<ErrorResponse> handleArchivoLectura(
            ArchivoLecturaException ex, WebRequest request) {
        log.error("Error al leer archivo: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Error de lectura de archivo")
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Maneja excepciones genericas no capturadas por otros handlers.
     * 
     * @param ex Excepcion lanzada
     * @param request Peticion web
     * @return Respuesta con codigo 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {
        log.error("Error inesperado: {}", ex.getMessage(), ex);
        
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
