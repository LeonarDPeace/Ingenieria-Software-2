package com.serviciudad.domain.model;

import com.serviciudad.domain.valueobject.Dinero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class EstadisticasDeuda {
    
    private int totalFacturasAcueducto;
    private int facturasVencidas;
    private int facturasPendientes;
    private double promedioConsumoAcueducto;
    private Dinero deudaAcumuladaAcueducto;
    
    private int totalConsumosEnergia;
    private double promedioConsumoEnergia;
    private Dinero deudaAcumuladaEnergia;

    public boolean tieneDeudaSignificativa() {
        Dinero total = deudaAcumuladaAcueducto.sumar(deudaAcumuladaEnergia);
        return total.esMayorQue(Dinero.of(500000));
    }

    public double porcentajeFacturasVencidas() {
        if (totalFacturasAcueducto == 0) {
            return 0.0;
        }
        return (facturasVencidas * 100.0) / totalFacturasAcueducto;
    }

    public boolean tieneConsumoAcueductoElevado() {
        return promedioConsumoAcueducto > 30;
    }

    public boolean tieneConsumoEnergiaElevado() {
        return promedioConsumoEnergia > 300;
    }
}
