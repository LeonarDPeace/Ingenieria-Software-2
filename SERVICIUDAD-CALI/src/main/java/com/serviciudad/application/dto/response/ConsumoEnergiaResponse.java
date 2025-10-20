package com.serviciudad.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsumoEnergiaResponse {
    
    private String clienteId;
    private String periodo;
    private BigDecimal consumo;
    private BigDecimal valorPagar;
    private LocalDate fechaLectura;
    private String estrato;
    private boolean consumoAlto;
    private boolean consumoBajo;
}
