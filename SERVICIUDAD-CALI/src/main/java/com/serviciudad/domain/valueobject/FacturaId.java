package com.serviciudad.domain.valueobject;

import lombok.Value;

@Value
public class FacturaId {
    Long valor;

    public FacturaId(Long valor) {
        if (valor == null || valor <= 0) {
            throw new IllegalArgumentException("FacturaId debe ser mayor a cero");
        }
        this.valor = valor;
    }

    public static FacturaId of(Long valor) {
        return new FacturaId(valor);
    }
}
