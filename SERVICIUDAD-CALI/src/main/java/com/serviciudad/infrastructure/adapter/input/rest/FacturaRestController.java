package com.serviciudad.infrastructure.adapter.input.rest;

import com.serviciudad.application.dto.request.RegistrarPagoRequest;
import com.serviciudad.application.dto.response.FacturaResponse;
import com.serviciudad.domain.model.FacturaAcueducto;
import com.serviciudad.domain.port.input.GestionarFacturaUseCase;
import com.serviciudad.domain.valueobject.ClienteId;
import com.serviciudad.domain.valueobject.FacturaId;
import com.serviciudad.domain.valueobject.Periodo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/facturas")
@RequiredArgsConstructor
@Validated
public class FacturaRestController {

    private final GestionarFacturaUseCase gestionarFacturaUseCase;

    @GetMapping("/{facturaId}")
    public ResponseEntity<FacturaResponse> consultarFactura(
        @PathVariable
        @jakarta.validation.constraints.Positive(message = "El FacturaId debe ser un número positivo")
        Long facturaId
    ) {
        FacturaId id = FacturaId.of(facturaId);
        FacturaAcueducto factura = gestionarFacturaUseCase.consultarFactura(id);
        
        FacturaResponse response = FacturaResponse.builder()
            .id(factura.getId().getValor())
            .clienteId(factura.getClienteId().getValor())
            .periodo(factura.getPeriodo().getValor())
            .consumo(java.math.BigDecimal.valueOf(factura.getConsumo().getMetrosCubicos()))
            .valorPagar(factura.getValorPagar().getMonto())
            .estado(factura.getEstado().name())
            .fechaVencimiento(factura.getFechaVencimiento())
            .fechaCreacion(factura.getFechaCreacion())
            .fechaActualizacion(factura.getFechaActualizacion())
            .vencida(factura.estaVencida())
            .diasHastaVencimiento(factura.diasHastaVencimiento())
            .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<FacturaResponse>> consultarFacturasPorCliente(
        @PathVariable
        @jakarta.validation.constraints.Pattern(
            regexp = "^\\d{10}$",
            message = "El ClienteId debe ser un número de 10 dígitos"
        )
        @jakarta.validation.constraints.NotBlank(message = "El ClienteId es obligatorio")
        String clienteId
    ) {
        ClienteId id = ClienteId.of(clienteId);
        List<FacturaAcueducto> facturas = gestionarFacturaUseCase.consultarFacturasPorCliente(id);
        
        List<FacturaResponse> responses = facturas.stream()
            .map(f -> FacturaResponse.builder()
                .id(f.getId().getValor())
                .clienteId(f.getClienteId().getValor())
                .periodo(f.getPeriodo().getValor())
                .consumo(java.math.BigDecimal.valueOf(f.getConsumo().getMetrosCubicos()))
                .valorPagar(f.getValorPagar().getMonto())
                .estado(f.getEstado().name())
                .fechaVencimiento(f.getFechaVencimiento())
                .fechaCreacion(f.getFechaCreacion())
                .fechaActualizacion(f.getFechaActualizacion())
                .vencida(f.estaVencida())
                .diasHastaVencimiento(f.diasHastaVencimiento())
                .build())
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/cliente/{clienteId}/periodo/{periodo}")
    public ResponseEntity<List<FacturaResponse>> consultarFacturasPorPeriodo(
        @PathVariable
        @jakarta.validation.constraints.Pattern(
            regexp = "^\\d{10}$",
            message = "El ClienteId debe ser un número de 10 dígitos"
        )
        @jakarta.validation.constraints.NotBlank(message = "El ClienteId es obligatorio")
        String clienteId,
        @PathVariable
        @jakarta.validation.constraints.Pattern(
            regexp = "^\\d{6}$",
            message = "El período debe tener formato YYYYMM (6 dígitos)"
        )
        @jakarta.validation.constraints.NotBlank(message = "El período es obligatorio")
        String periodo
    ) {
        ClienteId id = ClienteId.of(clienteId);
        Periodo p = Periodo.of(periodo);
        List<FacturaAcueducto> facturas = gestionarFacturaUseCase.consultarFacturasPorPeriodo(id, p);
        
        List<FacturaResponse> responses = facturas.stream()
            .map(f -> FacturaResponse.builder()
                .id(f.getId().getValor())
                .clienteId(f.getClienteId().getValor())
                .periodo(f.getPeriodo().getValor())
                .consumo(java.math.BigDecimal.valueOf(f.getConsumo().getMetrosCubicos()))
                .valorPagar(f.getValorPagar().getMonto())
                .estado(f.getEstado().name())
                .fechaVencimiento(f.getFechaVencimiento())
                .fechaCreacion(f.getFechaCreacion())
                .fechaActualizacion(f.getFechaActualizacion())
                .vencida(f.estaVencida())
                .diasHastaVencimiento(f.diasHastaVencimiento())
                .build())
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/pagar")
    public ResponseEntity<Void> registrarPago(
        @Valid @RequestBody RegistrarPagoRequest request
    ) {
        FacturaId facturaId = FacturaId.of(request.getFacturaId());
        gestionarFacturaUseCase.registrarPagoFactura(facturaId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{facturaId}/anular")
    public ResponseEntity<Void> anularFactura(
        @PathVariable Long facturaId
    ) {
        FacturaId id = FacturaId.of(facturaId);
        gestionarFacturaUseCase.anularFactura(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/marcar-vencidas")
    public ResponseEntity<Void> marcarFacturasVencidas() {
        gestionarFacturaUseCase.marcarFacturasVencidas();
        return ResponseEntity.ok().build();
    }
}
