package com.serviciudad.dto;

import com.serviciudad.domain.FacturaAcueducto.EstadoFactura;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para solicitudes de creacion/actualizacion de Factura de Acueducto.
 * 
 * Implementa el patron DTO para recibir datos desde la capa de presentacion.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class FacturaRequestDTO {

    @NotNull(message = "El ID del cliente es obligatorio")
    @Size(min = 10, max = 10, message = "El ID del cliente debe tener 10 caracteres")
    @Pattern(regexp = "\\d{10}", message = "El ID del cliente debe contener solo digitos")
    private String idCliente;

    @NotNull(message = "El periodo es obligatorio")
    @Pattern(regexp = "\\d{6}", message = "El periodo debe tener formato YYYYMM")
    private String periodo;

    @NotNull(message = "El consumo es obligatorio")
    @Min(value = 0, message = "El consumo no puede ser negativo")
    @Max(value = 999999, message = "El consumo debe ser menor a 1,000,000 m3")
    private Integer consumoM3;

    @NotNull(message = "El valor a pagar es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El valor a pagar debe ser mayor a 0")
    @DecimalMax(value = "999999999.99", message = "El valor a pagar excede el limite permitido")
    @Digits(integer = 10, fraction = 2, message = "El valor a pagar debe tener maximo 10 enteros y 2 decimales")
    private BigDecimal valorPagar;

    private EstadoFactura estado;

    @Future(message = "La fecha de vencimiento debe ser futura")
    private LocalDate fechaVencimiento;
}
