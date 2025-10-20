package com.serviciudad.domain.valueobject;

import lombok.Value;

@Value
public class ClienteId {
    String valor;

    public ClienteId(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("ClienteId no puede ser nulo o vacio");
        }
        if (valor.length() != 10) {
            throw new IllegalArgumentException("ClienteId debe tener 10 caracteres");
        }
        if (!valor.matches("\\d{10}")) {
            throw new IllegalArgumentException("ClienteId debe ser numerico");
        }
        this.valor = valor;
    }

    public static ClienteId of(String valor) {
        return new ClienteId(valor);
    }
}
