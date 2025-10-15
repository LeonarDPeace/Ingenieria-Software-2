package com.serviciudad.exception;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Clase para estructurar las respuestas de error de la API.
 * 
 * Proporciona un formato consistente para todos los errores
 * retornados por los endpoints.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ErrorResponse {

    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> detalles;
}
