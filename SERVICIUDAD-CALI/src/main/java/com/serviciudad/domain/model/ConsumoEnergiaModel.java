package com.serviciudad.domain.model;

import com.serviciudad.domain.valueobject.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class ConsumoEnergiaModel {
    
    private ClienteId clienteId;
    private Periodo periodo;
    private ConsumoEnergia consumo;
    private Dinero valorPagar;
    private LocalDate fechaLectura;
    private String estrato;

    public boolean tieneConsumoAlto() {
        return consumo.esAlto();
    }

    public boolean tieneConsumoBajo() {
        return consumo.esBajo();
    }

    public boolean perteneceAEstrato(String estratoBuscado) {
        return this.estrato != null && this.estrato.equalsIgnoreCase(estratoBuscado);
    }

    public boolean esDelMismoPeriodoQue(ConsumoEnergiaModel otro) {
        return this.periodo.equals(otro.periodo);
    }

    public boolean esDelMismoClienteQue(ConsumoEnergiaModel otro) {
        return this.clienteId.equals(otro.clienteId);
    }
}
