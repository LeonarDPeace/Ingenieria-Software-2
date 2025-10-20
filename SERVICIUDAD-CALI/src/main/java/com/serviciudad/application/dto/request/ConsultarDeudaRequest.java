package com.serviciudad.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultarDeudaRequest {
    
    @NotBlank(message = "El ID del cliente es obligatorio")
    @Pattern(regexp = "^\\d{10}$", message = "El ID del cliente debe tener exactamente 10 dígitos")
    private String clienteId;
}
