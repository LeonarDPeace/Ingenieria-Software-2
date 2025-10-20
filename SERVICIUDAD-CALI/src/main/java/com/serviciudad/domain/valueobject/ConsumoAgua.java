package com.serviciudad.domain.valueobject;

import lombok.Value;

@Value
public class ConsumoAgua {
    int metrosCubicos;

    public ConsumoAgua(int metrosCubicos) {
        if (metrosCubicos < 0) {
            throw new IllegalArgumentException("Consumo no puede ser negativo");
        }
        if (metrosCubicos > 10000) {
            throw new IllegalArgumentException("Consumo excede limite razonable");
        }
        this.metrosCubicos = metrosCubicos;
    }

    public static ConsumoAgua of(int metrosCubicos) {
        return new ConsumoAgua(metrosCubicos);
    }

    public boolean esAlto() {
        return metrosCubicos > 50;
    }

    public boolean esBajo() {
        return metrosCubicos < 10;
    }

    public boolean esNormal() {
        return metrosCubicos >= 10 && metrosCubicos <= 50;
    }
}
