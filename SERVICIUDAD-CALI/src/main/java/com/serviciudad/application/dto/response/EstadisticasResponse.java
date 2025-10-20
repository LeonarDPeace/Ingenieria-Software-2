package com.serviciudad.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasResponse {
    
    private int totalFacturasAcueducto;
    private int facturasVencidas;
    private int facturasPendientes;
    private double promedioConsumoAcueducto;
    private BigDecimal deudaAcumuladaAcueducto;
    
    private int totalConsumosEnergia;
    private double promedioConsumoEnergia;
    private BigDecimal deudaAcumuladaEnergia;
    
    private double porcentajeFacturasVencidas;
    private boolean tieneDeudaSignificativa;
    private boolean tieneConsumoAcueductoElevado;
    private boolean tieneConsumoEnergiaElevado;
}
