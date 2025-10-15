package com.serviciudad.adapter;

import com.serviciudad.adapter.exception.ArchivoLecturaException;
import com.serviciudad.domain.ConsumoEnergia;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adaptador para leer archivos legacy de consumos de energia.
 * 
 * Implementa el patron Adapter para convertir el formato COBOL
 * de longitud fija a objetos del dominio ConsumoEnergia.
 * 
 * Este patron permite integrar sistemas legacy sin modificar
 * el codigo existente del sistema principal.
 */
@Component
@Slf4j
public class AdaptadorArchivoEnergia {

    @Value("${energia.archivo.path}")
    private String rutaArchivo;

    /**
     * Lee todos los consumos de energia desde el archivo legacy.
     * 
     * @return Lista de consumos de energia
     * @throws ArchivoLecturaException si hay error al leer el archivo
     */
    public List<ConsumoEnergia> leerConsumos() {
        log.info("Iniciando lectura de archivo de consumos: {}", rutaArchivo);
        
        Path path = Paths.get(rutaArchivo);
        
        if (!Files.exists(path)) {
            throw new ArchivoLecturaException("El archivo no existe: " + rutaArchivo);
        }

        try {
            List<String> lineas = Files.readAllLines(path, StandardCharsets.UTF_8);
            log.info("Se leyeron {} lineas del archivo", lineas.size());
            
            return procesarLineas(lineas);
            
        } catch (IOException e) {
            log.error("Error al leer el archivo: {}", e.getMessage());
            throw new ArchivoLecturaException("Error al leer el archivo de consumos", e);
        }
    }

    /**
     * Lee los consumos de energia de un cliente especifico.
     * 
     * @param idCliente ID del cliente a buscar
     * @return Lista de consumos del cliente
     * @throws ArchivoLecturaException si hay error al leer el archivo
     */
    public List<ConsumoEnergia> leerConsumosPorCliente(String idCliente) {
        log.info("Buscando consumos del cliente: {}", idCliente);
        
        List<ConsumoEnergia> todosConsumos = leerConsumos();
        
        return todosConsumos.stream()
                .filter(consumo -> consumo.getIdCliente().equals(idCliente))
                .collect(Collectors.toList());
    }

    /**
     * Lee los consumos de energia de un periodo especifico.
     * 
     * @param periodo Periodo en formato YYYYMM
     * @return Lista de consumos del periodo
     * @throws ArchivoLecturaException si hay error al leer el archivo
     */
    public List<ConsumoEnergia> leerConsumosPorPeriodo(String periodo) {
        log.info("Buscando consumos del periodo: {}", periodo);
        
        List<ConsumoEnergia> todosConsumos = leerConsumos();
        
        return todosConsumos.stream()
                .filter(consumo -> consumo.getPeriodo().equals(periodo))
                .collect(Collectors.toList());
    }

    /**
     * Busca un consumo especifico de un cliente en un periodo.
     * 
     * @param idCliente ID del cliente
     * @param periodo Periodo en formato YYYYMM
     * @return ConsumoEnergia o null si no se encuentra
     * @throws ArchivoLecturaException si hay error al leer el archivo
     */
    public ConsumoEnergia buscarConsumo(String idCliente, String periodo) {
        log.info("Buscando consumo del cliente {} en periodo {}", idCliente, periodo);
        
        List<ConsumoEnergia> todosConsumos = leerConsumos();
        
        return todosConsumos.stream()
                .filter(consumo -> consumo.getIdCliente().equals(idCliente) 
                        && consumo.getPeriodo().equals(periodo))
                .findFirst()
                .orElse(null);
    }

    /**
     * Procesa las lineas del archivo y las convierte a objetos ConsumoEnergia.
     * 
     * @param lineas Lineas del archivo
     * @return Lista de consumos procesados
     */
    private List<ConsumoEnergia> procesarLineas(List<String> lineas) {
        List<ConsumoEnergia> consumos = new ArrayList<>();
        int lineaNumero = 0;

        for (String linea : lineas) {
            lineaNumero++;
            
            if (linea.trim().isEmpty()) {
                log.debug("Linea {} vacia, se omite", lineaNumero);
                continue;
            }

            try {
                ConsumoEnergia consumo = ConsumoEnergia.fromLineaCobol(linea);
                
                if (consumo.isValid()) {
                    consumos.add(consumo);
                    log.debug("Consumo procesado: {}", consumo);
                } else {
                    log.warn("Consumo invalido en linea {}: {}", lineaNumero, linea);
                }
                
            } catch (IllegalArgumentException e) {
                log.error("Error al parsear linea {}: {}", lineaNumero, e.getMessage());
            }
        }

        log.info("Se procesaron {} consumos validos de {} lineas", consumos.size(), lineaNumero);
        return consumos;
    }

    /**
     * Valida que el archivo exista y sea legible.
     * 
     * @return true si el archivo es valido
     */
    public boolean validarArchivo() {
        Path path = Paths.get(rutaArchivo);
        return Files.exists(path) && Files.isReadable(path) && Files.isRegularFile(path);
    }

    /**
     * Obtiene el numero total de registros en el archivo.
     * 
     * @return Numero de registros
     * @throws ArchivoLecturaException si hay error al leer el archivo
     */
    public long contarRegistros() {
        try {
            return Files.lines(Paths.get(rutaArchivo))
                    .filter(linea -> !linea.trim().isEmpty())
                    .count();
        } catch (IOException e) {
            throw new ArchivoLecturaException("Error al contar registros del archivo", e);
        }
    }
}
