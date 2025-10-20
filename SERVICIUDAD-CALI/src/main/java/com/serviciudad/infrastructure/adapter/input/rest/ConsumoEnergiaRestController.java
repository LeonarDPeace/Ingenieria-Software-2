package com.serviciudad.infrastructure.adapter.input.rest;

import com.serviciudad.application.dto.response.ConsumoEnergiaResponse;
import com.serviciudad.domain.model.ConsumoEnergiaModel;
import com.serviciudad.domain.port.input.ConsultarConsumoEnergiaUseCase;
import com.serviciudad.domain.valueobject.ClienteId;
import com.serviciudad.domain.valueobject.Periodo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/consumos-energia")
@RequiredArgsConstructor
@Validated
public class ConsumoEnergiaRestController {

    private final ConsultarConsumoEnergiaUseCase consultarConsumoEnergiaUseCase;

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ConsumoEnergiaResponse>> consultarConsumosPorCliente(
        @PathVariable
        @jakarta.validation.constraints.Pattern(
            regexp = "^\\d{10}$",
            message = "El ClienteId debe ser un número de 10 dígitos"
        )
        @jakarta.validation.constraints.NotBlank(message = "El ClienteId es obligatorio")
        String clienteId
    ) {
        ClienteId id = ClienteId.of(clienteId);
        List<ConsumoEnergiaModel> consumos = consultarConsumoEnergiaUseCase.consultarConsumosPorCliente(id);
        
        List<ConsumoEnergiaResponse> responses = consumos.stream()
            .map(c -> ConsumoEnergiaResponse.builder()
                .clienteId(c.getClienteId().getValor())
                .periodo(c.getPeriodo().getValor())
                .consumo(java.math.BigDecimal.valueOf(c.getConsumo().getKilovatiosHora()))
                .valorPagar(c.getValorPagar().getMonto())
                .fechaLectura(c.getFechaLectura())
                .estrato(c.getEstrato())
                .consumoAlto(c.tieneConsumoAlto())
                .consumoBajo(c.tieneConsumoBajo())
                .build())
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/cliente/{clienteId}/periodo/{periodo}")
    public ResponseEntity<List<ConsumoEnergiaResponse>> consultarConsumosPorPeriodo(
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
        List<ConsumoEnergiaModel> consumos = consultarConsumoEnergiaUseCase.consultarConsumosPorPeriodo(id, p);
        
        List<ConsumoEnergiaResponse> responses = consumos.stream()
            .map(c -> ConsumoEnergiaResponse.builder()
                .clienteId(c.getClienteId().getValor())
                .periodo(c.getPeriodo().getValor())
                .consumo(java.math.BigDecimal.valueOf(c.getConsumo().getKilovatiosHora()))
                .valorPagar(c.getValorPagar().getMonto())
                .fechaLectura(c.getFechaLectura())
                .estrato(c.getEstrato())
                .consumoAlto(c.tieneConsumoAlto())
                .consumoBajo(c.tieneConsumoBajo())
                .build())
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/cliente/{clienteId}/elevados")
    public ResponseEntity<List<ConsumoEnergiaResponse>> consultarConsumosElevados(
        @PathVariable
        @jakarta.validation.constraints.Pattern(
            regexp = "^\\d{10}$",
            message = "El ClienteId debe ser un número de 10 dígitos"
        )
        @jakarta.validation.constraints.NotBlank(message = "El ClienteId es obligatorio")
        String clienteId
    ) {
        ClienteId id = ClienteId.of(clienteId);
        List<ConsumoEnergiaModel> consumos = consultarConsumoEnergiaUseCase.consultarConsumosElevados(id);
        
        List<ConsumoEnergiaResponse> responses = consumos.stream()
            .map(c -> ConsumoEnergiaResponse.builder()
                .clienteId(c.getClienteId().getValor())
                .periodo(c.getPeriodo().getValor())
                .consumo(java.math.BigDecimal.valueOf(c.getConsumo().getKilovatiosHora()))
                .valorPagar(c.getValorPagar().getMonto())
                .fechaLectura(c.getFechaLectura())
                .estrato(c.getEstrato())
                .consumoAlto(c.tieneConsumoAlto())
                .consumoBajo(c.tieneConsumoBajo())
                .build())
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
}
