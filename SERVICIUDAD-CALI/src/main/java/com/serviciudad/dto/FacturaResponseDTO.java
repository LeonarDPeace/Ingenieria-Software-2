package com.serviciudad.dto;

import com.serviciudad.domain.FacturaAcueducto.EstadoFactura;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para respuestas de consulta de Factura de Acueducto.
 * 
 * Implementa el patron DTO para enviar datos enriquecidos a la capa de presentacion.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class FacturaResponseDTO {

    private Long id;
    private String idCliente;
    private String periodo;
    private Integer consumoM3;
    private BigDecimal valorPagar;
    private EstadoFactura estado;
    private LocalDate fechaVencimiento;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    
    private Boolean vencida;
    private Boolean pagada;
    private Long diasHastaVencimiento;
    
    private String mensaje;
}
