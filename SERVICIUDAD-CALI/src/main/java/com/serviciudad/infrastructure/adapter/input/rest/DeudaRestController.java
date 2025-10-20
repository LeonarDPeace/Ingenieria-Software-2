package com.serviciudad.infrastructure.adapter.input.rest;

import com.serviciudad.application.dto.request.ConsultarDeudaRequest;
import com.serviciudad.application.dto.response.DeudaConsolidadaResponse;
import com.serviciudad.application.mapper.DeudaMapper;
import com.serviciudad.domain.model.DeudaConsolidada;
import com.serviciudad.domain.port.input.ConsultarDeudaUseCase;
import com.serviciudad.domain.valueobject.ClienteId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deuda")
@RequiredArgsConstructor
@Validated
public class DeudaRestController {

    private final ConsultarDeudaUseCase consultarDeudaUseCase;

    @PostMapping("/consultar")
    public ResponseEntity<DeudaConsolidadaResponse> consultarDeuda(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del cliente a consultar",
            required = true
        )
        @Valid @RequestBody ConsultarDeudaRequest request
    ) {
        ClienteId clienteId = ClienteId.of(request.getClienteId());
        DeudaConsolidada deuda = consultarDeudaUseCase.consultarDeudaConsolidada(clienteId);
        DeudaConsolidadaResponse response = DeudaMapper.toResponse(deuda);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<DeudaConsolidadaResponse> consultarDeudaPorCliente(
        @PathVariable
        @jakarta.validation.constraints.Pattern(
            regexp = "^\\d{10}$",
            message = "El ClienteId debe ser un número de 10 dígitos"
        )
        @jakarta.validation.constraints.NotBlank(
            message = "El ClienteId es obligatorio"
        )
        String clienteId
    ) {
        ClienteId id = ClienteId.of(clienteId);
        DeudaConsolidada deuda = consultarDeudaUseCase.consultarDeudaConsolidada(id);
        DeudaConsolidadaResponse response = DeudaMapper.toResponse(deuda);
        return ResponseEntity.ok(response);
    }
}
