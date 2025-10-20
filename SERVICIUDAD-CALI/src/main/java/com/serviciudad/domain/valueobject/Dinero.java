package com.serviciudad.domain.valueobject;

import lombok.Value;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Value
public class Dinero {
    BigDecimal monto;

    public Dinero(BigDecimal monto) {
        if (monto == null) {
            throw new IllegalArgumentException("Monto no puede ser nulo");
        }
        this.monto = monto.setScale(2, RoundingMode.HALF_UP);
    }

    public static Dinero of(BigDecimal monto) {
        return new Dinero(monto);
    }

    public static Dinero of(double monto) {
        return new Dinero(BigDecimal.valueOf(monto));
    }

    public static Dinero of(String monto) {
        return new Dinero(new BigDecimal(monto));
    }

    public static Dinero cero() {
        return new Dinero(BigDecimal.ZERO);
    }

    public boolean esNegativo() {
        return monto.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean esCero() {
        return monto.compareTo(BigDecimal.ZERO) == 0;
    }

    public Dinero sumar(Dinero otro) {
        return new Dinero(this.monto.add(otro.monto));
    }

    public Dinero restar(Dinero otro) {
        return new Dinero(this.monto.subtract(otro.monto));
    }

    public boolean esMayorQue(Dinero otro) {
        return this.monto.compareTo(otro.monto) > 0;
    }

    public boolean esMenorQue(Dinero otro) {
        return this.monto.compareTo(otro.monto) < 0;
    }

    public boolean esIgualA(Dinero otro) {
        return this.monto.compareTo(otro.monto) == 0;
    }
}
