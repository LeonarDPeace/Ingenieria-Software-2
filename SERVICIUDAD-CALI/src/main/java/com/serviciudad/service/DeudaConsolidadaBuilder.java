package com.serviciudad.service;

import com.serviciudad.dto.FacturaResponseDTO;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder para construir respuestas de deuda consolidada.
 * 
 * Implementa el patron Builder para crear objetos complejos
 * de respuesta de forma fluida y legible.
 * 
 * Este patron permite construir objetos paso a paso y
 * separar la construccion de la representacion.
 */
@Getter
@ToString
public class DeudaConsolidadaBuilder {

    private String idCliente;
    private List<FacturaResponseDTO> facturas;
    private BigDecimal deudaTotal;
    private Integer totalFacturas;
    private Integer facturasPendientes;
    private Integer facturasPagadas;
    private Integer facturasVencidas;
    private FacturaResponseDTO proximaVencer;
    private String resumenEstado;
    private List<String> alertas;

    private DeudaConsolidadaBuilder() {
        this.facturas = new ArrayList<>();
        this.alertas = new ArrayList<>();
        this.deudaTotal = BigDecimal.ZERO;
        this.totalFacturas = 0;
        this.facturasPendientes = 0;
        this.facturasPagadas = 0;
        this.facturasVencidas = 0;
    }

    /**
     * Crea una nueva instancia del builder.
     * 
     * @return Nueva instancia de DeudaConsolidadaBuilder
     */
    public static DeudaConsolidadaBuilder crear() {
        return new DeudaConsolidadaBuilder();
    }

    /**
     * Establece el ID del cliente.
     * 
     * @param idCliente ID del cliente
     * @return Builder para encadenamiento
     */
    public DeudaConsolidadaBuilder conCliente(String idCliente) {
        this.idCliente = idCliente;
        return this;
    }

    /**
     * Agrega una lista de facturas.
     * 
     * @param facturas Lista de facturas
     * @return Builder para encadenamiento
     */
    public DeudaConsolidadaBuilder conFacturas(List<FacturaResponseDTO> facturas) {
        if (facturas != null) {
            this.facturas.addAll(facturas);
        }
        return this;
    }

    /**
     * Agrega una factura individual.
     * 
     * @param factura Factura a agregar
     * @return Builder para encadenamiento
     */
    public DeudaConsolidadaBuilder agregarFactura(FacturaResponseDTO factura) {
        if (factura != null) {
            this.facturas.add(factura);
        }
        return this;
    }

    /**
     * Calcula automaticamente las estadisticas de las facturas.
     * 
     * @return Builder para encadenamiento
     */
    public DeudaConsolidadaBuilder calcularEstadisticas() {
        this.totalFacturas = facturas.size();
        this.deudaTotal = BigDecimal.ZERO;
        this.facturasPendientes = 0;
        this.facturasPagadas = 0;
        this.facturasVencidas = 0;

        FacturaResponseDTO masProxima = null;
        long menorDias = Long.MAX_VALUE;

        for (FacturaResponseDTO factura : facturas) {
            if (factura.getPagada() != null && factura.getPagada()) {
                facturasPagadas++;
            } else {
                deudaTotal = deudaTotal.add(factura.getValorPagar());
                
                if (factura.getVencida() != null && factura.getVencida()) {
                    facturasVencidas++;
                } else {
                    facturasPendientes++;
                    
                    if (factura.getDiasHastaVencimiento() != null 
                            && factura.getDiasHastaVencimiento() < menorDias 
                            && factura.getDiasHastaVencimiento() > 0) {
                        menorDias = factura.getDiasHastaVencimiento();
                        masProxima = factura;
                    }
                }
            }
        }

        this.proximaVencer = masProxima;
        return this;
    }

    /**
     * Genera el resumen del estado de la deuda.
     * 
     * @return Builder para encadenamiento
     */
    public DeudaConsolidadaBuilder generarResumen() {
        StringBuilder resumen = new StringBuilder();
        
        resumen.append(String.format("Total facturas: %d", totalFacturas));
        
        if (facturasVencidas > 0) {
            resumen.append(String.format(" | Vencidas: %d", facturasVencidas));
        }
        
        if (facturasPendientes > 0) {
            resumen.append(String.format(" | Pendientes: %d", facturasPendientes));
        }
        
        if (facturasPagadas > 0) {
            resumen.append(String.format(" | Pagadas: %d", facturasPagadas));
        }
        
        if (deudaTotal.compareTo(BigDecimal.ZERO) > 0) {
            resumen.append(String.format(" | Deuda total: $%,.2f", deudaTotal));
        }

        this.resumenEstado = resumen.toString();
        return this;
    }

    /**
     * Genera alertas basadas en el estado de las facturas.
     * 
     * @return Builder para encadenamiento
     */
    public DeudaConsolidadaBuilder generarAlertas() {
        this.alertas.clear();

        if (facturasVencidas > 0) {
            alertas.add(String.format("Tiene %d factura(s) vencida(s)", facturasVencidas));
        }

        if (proximaVencer != null) {
            long dias = proximaVencer.getDiasHastaVencimiento();
            if (dias <= 5) {
                alertas.add(String.format("Factura proxima a vencer en %d dias - Periodo %s", 
                        dias, proximaVencer.getPeriodo()));
            }
        }

        if (deudaTotal.compareTo(new BigDecimal("500000")) > 0) {
            alertas.add(String.format("Deuda total elevada: $%,.2f", deudaTotal));
        }

        if (alertas.isEmpty()) {
            alertas.add("No hay alertas - Su cuenta esta al dia");
        }

        return this;
    }

    /**
     * Agrega una alerta personalizada.
     * 
     * @param alerta Mensaje de alerta
     * @return Builder para encadenamiento
     */
    public DeudaConsolidadaBuilder agregarAlerta(String alerta) {
        if (alerta != null && !alerta.isEmpty()) {
            this.alertas.add(alerta);
        }
        return this;
    }

    /**
     * Construye el objeto DeudaConsolidada final.
     * 
     * @return Objeto DeudaConsolidada construido
     */
    public DeudaConsolidada construir() {
        return new DeudaConsolidada(
                idCliente,
                facturas,
                deudaTotal,
                totalFacturas,
                facturasPendientes,
                facturasPagadas,
                facturasVencidas,
                proximaVencer,
                resumenEstado,
                alertas
        );
    }

    /**
     * Clase interna que representa la deuda consolidada.
     */
    @Getter
    @AllArgsConstructor
    @ToString
    public static class DeudaConsolidada {
        private final String idCliente;
        private final List<FacturaResponseDTO> facturas;
        private final BigDecimal deudaTotal;
        private final Integer totalFacturas;
        private final Integer facturasPendientes;
        private final Integer facturasPagadas;
        private final Integer facturasVencidas;
        private final FacturaResponseDTO proximaVencer;
        private final String resumenEstado;
        private final List<String> alertas;
    }
}
