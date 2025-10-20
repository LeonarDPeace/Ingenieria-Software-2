package com.serviciudad.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarPagoRequest {
    
    @NotNull(message = "El ID de la factura es obligatorio")
    @Positive(message = "El ID de la factura debe ser positivo")
    private Long facturaId;
}
