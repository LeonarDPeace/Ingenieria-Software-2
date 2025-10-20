package com.serviciudad.domain.model;

public enum EstadoFactura {
    PENDIENTE,
    PAGADA,
    VENCIDA,
    ANULADA;

    public boolean esPagada() {
        return this == PAGADA;
    }

    public boolean esActiva() {
        return this == PENDIENTE || this == VENCIDA;
    }

    public boolean permiteModificacion() {
        return this == PENDIENTE || this == VENCIDA;
    }

    public boolean esVencida() {
        return this == VENCIDA;
    }

    public boolean esPendiente() {
        return this == PENDIENTE;
    }
}
