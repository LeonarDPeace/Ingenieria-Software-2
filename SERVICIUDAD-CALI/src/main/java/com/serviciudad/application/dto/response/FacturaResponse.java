package com.serviciudad.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacturaResponse {
    
    private Long id;
    private String clienteId;
    private String periodo;
    private BigDecimal consumo;
    private BigDecimal valorPagar;
    private String estado;
    private LocalDate fechaVencimiento;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private boolean vencida;
    private long diasHastaVencimiento;
}
