package com.serviciudad.controller;

import com.serviciudad.adapter.AdaptadorArchivoEnergia;
import com.serviciudad.domain.ConsumoEnergia;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la lectura de consumos de energia desde archivos legacy.
 * 
 * Utiliza el patron Adapter para leer archivos en formato COBOL
 * y exponerlos a traves de endpoints REST modernos.
 */
@RestController
@RequestMapping("/api/consumos-energia")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Consumos de Energia", description = "API para lectura de archivos legacy de energia")
public class ConsumoEnergiaController {

    private final AdaptadorArchivoEnergia adaptador;

    @Operation(summary = "Obtener todos los consumos", description = "Lee y retorna todos los consumos del archivo legacy")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consumos leidos exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConsumoEnergia.class))),
            @ApiResponse(responseCode = "500", description = "Error al leer el archivo", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<ConsumoEnergia>> obtenerTodos() {
        log.info("GET /api/consumos-energia - Leyendo todos los consumos");
        List<ConsumoEnergia> consumos = adaptador.leerConsumos();
        return ResponseEntity.ok(consumos);
    }

    @Operation(summary = "Obtener consumos por cliente", description = "Lee y retorna los consumos de un cliente especifico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consumos del cliente obtenidos exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConsumoEnergia.class))),
            @ApiResponse(responseCode = "500", description = "Error al leer el archivo", content = @Content)
    })
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<ConsumoEnergia>> obtenerPorCliente(
            @Parameter(description = "ID del cliente (10 digitos)", required = true)
            @PathVariable String idCliente) {
        log.info("GET /api/consumos-energia/cliente/{} - Leyendo consumos por cliente", idCliente);
        List<ConsumoEnergia> consumos = adaptador.leerConsumosPorCliente(idCliente);
        return ResponseEntity.ok(consumos);
    }

    @Operation(summary = "Obtener consumos por periodo", description = "Lee y retorna los consumos de un periodo especifico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consumos del periodo obtenidos exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConsumoEnergia.class))),
            @ApiResponse(responseCode = "500", description = "Error al leer el archivo", content = @Content)
    })
    @GetMapping("/periodo/{periodo}")
    public ResponseEntity<List<ConsumoEnergia>> obtenerPorPeriodo(
            @Parameter(description = "Periodo en formato YYYYMM", required = true, example = "202510")
            @PathVariable String periodo) {
        log.info("GET /api/consumos-energia/periodo/{} - Leyendo consumos por periodo", periodo);
        List<ConsumoEnergia> consumos = adaptador.leerConsumosPorPeriodo(periodo);
        return ResponseEntity.ok(consumos);
    }

    @Operation(summary = "Buscar consumo especifico", description = "Busca un consumo de un cliente en un periodo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consumo encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConsumoEnergia.class))),
            @ApiResponse(responseCode = "404", description = "Consumo no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error al leer el archivo", content = @Content)
    })
    @GetMapping("/cliente/{idCliente}/periodo/{periodo}")
    public ResponseEntity<ConsumoEnergia> buscarConsumo(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable String idCliente,
            @Parameter(description = "Periodo en formato YYYYMM", required = true)
            @PathVariable String periodo) {
        log.info("GET /api/consumos-energia/cliente/{}/periodo/{} - Buscando consumo especifico", idCliente, periodo);
        ConsumoEnergia consumo = adaptador.buscarConsumo(idCliente, periodo);
        
        if (consumo == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(consumo);
    }

    @Operation(summary = "Validar archivo", description = "Verifica si el archivo de consumos existe y es legible")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado de validacion del archivo",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/validar-archivo")
    public ResponseEntity<ArchivoStatus> validarArchivo() {
        log.info("GET /api/consumos-energia/validar-archivo - Validando archivo de consumos");
        boolean valido = adaptador.validarArchivo();
        long registros = valido ? adaptador.contarRegistros() : 0;
        
        ArchivoStatus status = new ArchivoStatus(valido, registros);
        return ResponseEntity.ok(status);
    }

    @Operation(summary = "Contar registros", description = "Retorna el numero total de registros en el archivo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Numero de registros obtenido",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Error al leer el archivo", content = @Content)
    })
    @GetMapping("/count")
    public ResponseEntity<Long> contarRegistros() {
        log.info("GET /api/consumos-energia/count - Contando registros del archivo");
        long count = adaptador.contarRegistros();
        return ResponseEntity.ok(count);
    }

    public record ArchivoStatus(boolean valido, long registros) {}
}
