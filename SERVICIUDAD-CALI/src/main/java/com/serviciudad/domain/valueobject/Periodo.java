package com.serviciudad.domain.valueobject;

import lombok.Value;

@Value
public class Periodo {
    String valor;

    public Periodo(String valor) {
        if (valor == null || !valor.matches("\\d{6}")) {
            throw new IllegalArgumentException("Periodo debe tener formato YYYYMM");
        }
        int year = Integer.parseInt(valor.substring(0, 4));
        int month = Integer.parseInt(valor.substring(4, 6));
        
        if (year < 2020 || year > 2030) {
            throw new IllegalArgumentException("Año debe estar entre 2020 y 2030");
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Mes debe estar entre 1 y 12");
        }
        this.valor = valor;
    }

    public static Periodo of(String valor) {
        return new Periodo(valor);
    }

    public int getYear() {
        return Integer.parseInt(valor.substring(0, 4));
    }

    public int getMonth() {
        return Integer.parseInt(valor.substring(4, 6));
    }
}
