package com.serviciudad.domain.model;

import com.serviciudad.domain.valueobject.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class FacturaAcueducto {
    
    private FacturaId id;
    private ClienteId clienteId;
    private Periodo periodo;
    private ConsumoAgua consumo;
    private Dinero valorPagar;
    private EstadoFactura estado;
    private LocalDate fechaVencimiento;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public boolean estaVencida() {
        if (fechaVencimiento == null) {
            return false;
        }
        return fechaVencimiento.isBefore(LocalDate.now()) 
            && estado != EstadoFactura.PAGADA;
    }

    public void registrarPago() {
        if (estado == EstadoFactura.ANULADA) {
            throw new IllegalStateException("No se puede pagar una factura anulada");
        }
        this.estado = EstadoFactura.PAGADA;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void anular() {
        if (estado == EstadoFactura.PAGADA) {
            throw new IllegalStateException("No se puede anular una factura pagada");
        }
        this.estado = EstadoFactura.ANULADA;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void actualizarValor(Dinero nuevoValor) {
        if (estado == EstadoFactura.PAGADA) {
            throw new IllegalStateException("No se puede modificar una factura pagada");
        }
        if (nuevoValor.esNegativo()) {
            throw new IllegalArgumentException("El valor no puede ser negativo");
        }
        this.valorPagar = nuevoValor;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public long diasHastaVencimiento() {
        if (fechaVencimiento == null) {
            return 0;
        }
        return LocalDate.now().until(fechaVencimiento, java.time.temporal.ChronoUnit.DAYS);
    }

    public void marcarComoVencida() {
        if (estado == EstadoFactura.PENDIENTE && estaVencida()) {
            this.estado = EstadoFactura.VENCIDA;
            this.fechaActualizacion = LocalDateTime.now();
        }
    }

    public boolean isPagada() {
        return estado == EstadoFactura.PAGADA;
    }

    public boolean isVencida() {
        return estado == EstadoFactura.VENCIDA;
    }

    public boolean isPendiente() {
        return estado == EstadoFactura.PENDIENTE;
    }
}
