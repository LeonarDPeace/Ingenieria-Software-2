package com.serviciudad.domain.valueobject;

import lombok.Value;

@Value
public class ConsumoEnergia {
    int kilovatiosHora;

    public ConsumoEnergia(int kilovatiosHora) {
        if (kilovatiosHora < 0) {
            throw new IllegalArgumentException("Consumo energia no puede ser negativo");
        }
        if (kilovatiosHora > 100000) {
            throw new IllegalArgumentException("Consumo energia excede limite razonable");
        }
        this.kilovatiosHora = kilovatiosHora;
    }

    public static ConsumoEnergia of(int kilovatiosHora) {
        return new ConsumoEnergia(kilovatiosHora);
    }

    public boolean esAlto() {
        return kilovatiosHora > 500;
    }

    public boolean esBajo() {
        return kilovatiosHora < 100;
    }

    public boolean esNormal() {
        return kilovatiosHora >= 100 && kilovatiosHora <= 500;
    }
}
