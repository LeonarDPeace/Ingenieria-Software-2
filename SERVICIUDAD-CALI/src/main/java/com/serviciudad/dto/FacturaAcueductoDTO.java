package com.serviciudad.dto;

import com.serviciudad.domain.FacturaAcueducto.EstadoFactura;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para transferencia de datos de Factura de Acueducto.
 * 
 * Implementa el patron DTO (Data Transfer Object) para desacoplar
 * la capa de presentacion de la capa de dominio.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class FacturaAcueductoDTO {

    private Long id;

    @NotNull(message = "El ID del cliente es obligatorio")
    @Size(min = 10, max = 10, message = "El ID del cliente debe tener 10 caracteres")
    private String idCliente;

    @NotNull(message = "El periodo es obligatorio")
    @Pattern(regexp = "\\d{6}", message = "El periodo debe tener formato YYYYMM")
    private String periodo;

    @NotNull(message = "El consumo es obligatorio")
    @Min(value = 0, message = "El consumo no puede ser negativo")
    private Integer consumoM3;

    @NotNull(message = "El valor a pagar es obligatorio")
    @DecimalMin(value = "0.0", message = "El valor a pagar no puede ser negativo")
    private BigDecimal valorPagar;

    @NotNull(message = "El estado es obligatorio")
    private EstadoFactura estado;

    private LocalDate fechaVencimiento;
    
    private LocalDateTime fechaCreacion;
    
    private LocalDateTime fechaActualizacion;

    private Boolean vencida;
    
    private Long diasHastaVencimiento;
}
