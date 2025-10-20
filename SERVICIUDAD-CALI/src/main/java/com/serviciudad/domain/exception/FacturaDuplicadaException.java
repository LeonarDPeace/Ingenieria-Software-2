package com.serviciudad.domain.exception;

/**
 * Excepción de dominio lanzada cuando se intenta crear una factura duplicada.
 * 
 * Esta excepción protege la invariante del dominio que establece que no puede
 * existir más de una factura para el mismo cliente en el mismo período.
 * 
 * @author Equipo ServiCiudad
 * @since 2.0.0
 */
public class FacturaDuplicadaException extends RuntimeException {

    /**
     * Crea una nueva excepción con un mensaje descriptivo.
     * 
     * @param mensaje Descripción del error
     */
    public FacturaDuplicadaException(String mensaje) {
        super(mensaje);
    }

    /**
     * Crea una nueva excepción con mensaje y causa.
     * 
     * @param mensaje Descripción del error
     * @param causa Causa raíz de la excepción
     */
    public FacturaDuplicadaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
