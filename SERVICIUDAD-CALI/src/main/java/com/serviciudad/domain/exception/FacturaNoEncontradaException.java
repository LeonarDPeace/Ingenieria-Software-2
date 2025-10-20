package com.serviciudad.domain.exception;

/**
 * Excepción de dominio lanzada cuando no se encuentra una factura solicitada.
 * 
 * Esta excepción se utiliza en la capa de dominio para indicar que una
 * operación falló porque la factura buscada no existe en el sistema.
 * 
 * @author Equipo ServiCiudad
 * @since 2.0.0
 */
public class FacturaNoEncontradaException extends RuntimeException {

    /**
     * Crea una nueva excepción con un mensaje descriptivo.
     * 
     * @param mensaje Descripción del error
     */
    public FacturaNoEncontradaException(String mensaje) {
        super(mensaje);
    }

    /**
     * Crea una nueva excepción con mensaje y causa.
     * 
     * @param mensaje Descripción del error
     * @param causa Causa raíz de la excepción
     */
    public FacturaNoEncontradaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
