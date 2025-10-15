package com.serviciudad.adapter.exception;

/**
 * Excepcion lanzada cuando ocurre un error al leer archivos legacy.
 * 
 * Esta excepcion es parte del patron Adapter y se utiliza para
 * encapsular errores especificos de lectura de archivos.
 */
public class ArchivoLecturaException extends RuntimeException {

    public ArchivoLecturaException(String mensaje) {
        super(mensaje);
    }

    public ArchivoLecturaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
