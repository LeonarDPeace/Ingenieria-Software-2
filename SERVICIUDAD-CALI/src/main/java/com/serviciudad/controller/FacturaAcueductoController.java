package com.serviciudad.controller;

import com.serviciudad.dto.FacturaRequestDTO;
import com.serviciudad.dto.FacturaResponseDTO;
import com.serviciudad.service.DeudaConsolidadaBuilder;
import com.serviciudad.service.FacturaAcueductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador REST para la gestion de facturas de acueducto.
 * 
 * Expone endpoints para operaciones CRUD y consultas especializadas
 * sobre facturas de acueducto.
 */
@RestController
@RequestMapping("/api/facturas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Facturas de Acueducto", description = "API para gestion de facturas de acueducto")
public class FacturaAcueductoController {

    private final FacturaAcueductoService service;

    @Operation(summary = "Obtener todas las facturas", description = "Retorna la lista completa de facturas de acueducto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de facturas obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FacturaResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<FacturaResponseDTO>> obtenerTodas() {
        log.info("GET /api/facturas - Obteniendo todas las facturas");
        List<FacturaResponseDTO> facturas = service.obtenerTodas();
        return ResponseEntity.ok(facturas);
    }

    @Operation(summary = "Obtener factura por ID", description = "Retorna una factura especifica por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Factura encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FacturaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Factura no encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<FacturaResponseDTO> obtenerPorId(
            @Parameter(description = "ID de la factura", required = true)
            @PathVariable Long id) {
        log.info("GET /api/facturas/{} - Obteniendo factura por ID", id);
        FacturaResponseDTO factura = service.obtenerPorId(id);
        return ResponseEntity.ok(factura);
    }

    @Operation(summary = "Obtener facturas por cliente", description = "Retorna todas las facturas de un cliente especifico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facturas del cliente obtenidas exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FacturaResponseDTO.class)))
    })
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<FacturaResponseDTO>> obtenerPorCliente(
            @Parameter(description = "ID del cliente (10 digitos)", required = true)
            @PathVariable String idCliente) {
        log.info("GET /api/facturas/cliente/{} - Obteniendo facturas por cliente", idCliente);
        List<FacturaResponseDTO> facturas = service.obtenerPorCliente(idCliente);
        return ResponseEntity.ok(facturas);
    }

    @Operation(summary = "Obtener facturas por periodo", description = "Retorna todas las facturas de un periodo especifico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facturas del periodo obtenidas exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FacturaResponseDTO.class)))
    })
    @GetMapping("/periodo/{periodo}")
    public ResponseEntity<List<FacturaResponseDTO>> obtenerPorPeriodo(
            @Parameter(description = "Periodo en formato YYYYMM", required = true, example = "202510")
            @PathVariable String periodo) {
        log.info("GET /api/facturas/periodo/{} - Obteniendo facturas por periodo", periodo);
        List<FacturaResponseDTO> facturas = service.obtenerPorPeriodo(periodo);
        return ResponseEntity.ok(facturas);
    }

    @Operation(summary = "Obtener factura por cliente y periodo", description = "Retorna la factura de un cliente en un periodo especifico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Factura encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FacturaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Factura no encontrada", content = @Content)
    })
    @GetMapping("/cliente/{idCliente}/periodo/{periodo}")
    public ResponseEntity<FacturaResponseDTO> obtenerPorClienteYPeriodo(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable String idCliente,
            @Parameter(description = "Periodo en formato YYYYMM", required = true)
            @PathVariable String periodo) {
        log.info("GET /api/facturas/cliente/{}/periodo/{} - Obteniendo factura especifica", idCliente, periodo);
        FacturaResponseDTO factura = service.obtenerPorClienteYPeriodo(idCliente, periodo);
        return ResponseEntity.ok(factura);
    }

    @Operation(summary = "Crear nueva factura", description = "Crea una nueva factura de acueducto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Factura creada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FacturaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos", content = @Content),
            @ApiResponse(responseCode = "409", description = "Factura duplicada", content = @Content)
    })
    @PostMapping
    public ResponseEntity<FacturaResponseDTO> crear(
            @Parameter(description = "Datos de la factura a crear", required = true)
            @Valid @RequestBody FacturaRequestDTO dto) {
        log.info("POST /api/facturas - Creando nueva factura");
        FacturaResponseDTO factura = service.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(factura);
    }

    @Operation(summary = "Actualizar factura", description = "Actualiza una factura existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Factura actualizada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FacturaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Factura no encontrada", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<FacturaResponseDTO> actualizar(
            @Parameter(description = "ID de la factura", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nuevos datos de la factura", required = true)
            @Valid @RequestBody FacturaRequestDTO dto) {
        log.info("PUT /api/facturas/{} - Actualizando factura", id);
        FacturaResponseDTO factura = service.actualizar(id, dto);
        return ResponseEntity.ok(factura);
    }

    @Operation(summary = "Eliminar factura", description = "Elimina una factura por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Factura eliminada exitosamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Factura no encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la factura", required = true)
            @PathVariable Long id) {
        log.info("DELETE /api/facturas/{} - Eliminando factura", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener deuda consolidada", description = "Retorna el resumen de deuda consolidada de un cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deuda consolidada calculada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeudaConsolidadaBuilder.DeudaConsolidada.class)))
    })
    @GetMapping("/cliente/{idCliente}/deuda-consolidada")
    public ResponseEntity<DeudaConsolidadaBuilder.DeudaConsolidada> obtenerDeudaConsolidada(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable String idCliente) {
        log.info("GET /api/facturas/cliente/{}/deuda-consolidada - Calculando deuda consolidada", idCliente);
        DeudaConsolidadaBuilder.DeudaConsolidada deuda = service.obtenerDeudaConsolidada(idCliente);
        return ResponseEntity.ok(deuda);
    }

    @Operation(summary = "Obtener facturas vencidas", description = "Retorna todas las facturas vencidas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facturas vencidas obtenidas exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FacturaResponseDTO.class)))
    })
    @GetMapping("/vencidas")
    public ResponseEntity<List<FacturaResponseDTO>> obtenerVencidas() {
        log.info("GET /api/facturas/vencidas - Obteniendo facturas vencidas");
        List<FacturaResponseDTO> facturas = service.obtenerFacturasVencidas();
        return ResponseEntity.ok(facturas);
    }

    @Operation(summary = "Obtener facturas proximas a vencer", description = "Retorna facturas que vencen en los proximos N dias")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facturas proximas a vencer obtenidas exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FacturaResponseDTO.class)))
    })
    @GetMapping("/proximas-vencer")
    public ResponseEntity<List<FacturaResponseDTO>> obtenerProximasAVencer(
            @Parameter(description = "Numero de dias", example = "7")
            @RequestParam(defaultValue = "7") int dias) {
        log.info("GET /api/facturas/proximas-vencer?dias={} - Obteniendo facturas proximas a vencer", dias);
        List<FacturaResponseDTO> facturas = service.obtenerProximasAVencer(dias);
        return ResponseEntity.ok(facturas);
    }

    @Operation(summary = "Calcular deuda total de cliente", description = "Retorna el monto total de deuda de un cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deuda total calculada exitosamente",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/cliente/{idCliente}/deuda-total")
    public ResponseEntity<BigDecimal> calcularDeudaTotal(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable String idCliente) {
        log.info("GET /api/facturas/cliente/{}/deuda-total - Calculando deuda total", idCliente);
        BigDecimal deuda = service.calcularDeudaTotal(idCliente);
        return ResponseEntity.ok(deuda);
    }

    @Operation(summary = "Contar facturas pendientes", description = "Retorna el numero de facturas pendientes de un cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facturas pendientes contadas exitosamente",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/cliente/{idCliente}/pendientes/count")
    public ResponseEntity<Long> contarPendientes(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable String idCliente) {
        log.info("GET /api/facturas/cliente/{}/pendientes/count - Contando facturas pendientes", idCliente);
        Long count = service.contarFacturasPendientes(idCliente);
        return ResponseEntity.ok(count);
    }
}
