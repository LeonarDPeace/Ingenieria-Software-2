package com.serviciudad.service.exception;

/**
 * Excepcion lanzada cuando no se encuentra una factura.
 */
public class FacturaNoEncontradaException extends RuntimeException {

    public FacturaNoEncontradaException(String mensaje) {
        super(mensaje);
    }

    public FacturaNoEncontradaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
