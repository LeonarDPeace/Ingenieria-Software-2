package com.serviciudad.domain;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Modelo de dominio para Consumo de Energia.
 * 
 * Representa un registro de consumo de energia leido desde
 * el archivo legacy en formato COBOL de longitud fija.
 * 
 * Este modelo se utiliza en el patron Adapter para convertir
 * datos legacy a objetos del dominio.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ConsumoEnergia {

    private static final DateTimeFormatter PERIODO_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");
    private static final DateTimeFormatter FECHA_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private String idCliente;
    private String periodo;
    private Integer consumoKwh;
    private BigDecimal valorPagar;
    private LocalDate fechaVencimiento;

    /**
     * Crea un ConsumoEnergia desde una linea de texto en formato COBOL.
     * 
     * Formato esperado (78 caracteres):
     * - Posicion 1-10: ID Cliente (10 digitos)
     * - Posicion 11-16: Periodo YYYYMM (6 digitos)
     * - Posicion 17-23: Consumo kWh (7 digitos)
     * - Posicion 24-35: Valor a pagar (12 digitos con 2 decimales)
     * - Posicion 36-43: Fecha vencimiento YYYYMMDD (8 digitos)
     * 
     * @param linea Linea de texto en formato COBOL
     * @return ConsumoEnergia parseado
     * @throws IllegalArgumentException si el formato es invalido
     */
    public static ConsumoEnergia fromLineaCobol(String linea) {
        if (linea == null || linea.length() < 43) {
            throw new IllegalArgumentException("Linea de consumo invalida: longitud insuficiente");
        }

        try {
            String idCliente = linea.substring(0, 10).trim();
            String periodo = linea.substring(10, 16).trim();
            String consumoStr = linea.substring(16, 23).trim();
            String valorStr = linea.substring(23, 35).trim();
            String fechaStr = linea.substring(35, 43).trim();

            Integer consumoKwh = Integer.parseInt(consumoStr);
            
            BigDecimal valorPagar = new BigDecimal(valorStr).divide(new BigDecimal("100"));
            
            LocalDate fechaVencimiento = LocalDate.parse(fechaStr, FECHA_FORMATTER);

            return ConsumoEnergia.builder()
                    .idCliente(idCliente)
                    .periodo(periodo)
                    .consumoKwh(consumoKwh)
                    .valorPagar(valorPagar)
                    .fechaVencimiento(fechaVencimiento)
                    .build();

        } catch (Exception e) {
            throw new IllegalArgumentException("Error al parsear linea de consumo: " + e.getMessage(), e);
        }
    }

    /**
     * Valida que todos los campos obligatorios esten presentes.
     * 
     * @return true si el registro es valido
     */
    public boolean isValid() {
        return idCliente != null && !idCliente.isEmpty()
                && periodo != null && periodo.matches("\\d{6}")
                && consumoKwh != null && consumoKwh >= 0
                && valorPagar != null && valorPagar.compareTo(BigDecimal.ZERO) >= 0
                && fechaVencimiento != null;
    }

    /**
     * Convierte el consumo de kWh a m3 de agua (conversion aproximada).
     * Esta es una simplificacion para el ejemplo.
     * 
     * @return consumo aproximado en m3
     */
    public Integer convertirAMetrosCubicos() {
        return Math.round(consumoKwh / 10.0f);
    }
}
