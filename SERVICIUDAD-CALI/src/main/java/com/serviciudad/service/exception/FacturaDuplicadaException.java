package com.serviciudad.service.exception;

/**
 * Excepcion lanzada cuando se intenta crear una factura duplicada.
 */
public class FacturaDuplicadaException extends RuntimeException {

    public FacturaDuplicadaException(String mensaje) {
        super(mensaje);
    }

    public FacturaDuplicadaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
