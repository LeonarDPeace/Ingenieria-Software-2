package com.serviciudad.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad de dominio para Factura de Acueducto.
 * 
 * Representa una factura del servicio de acueducto en el sistema
 * de deuda consolidada de ServiCiudad Cali.
 * 
 * Implementa el patron Repository a traves de Spring Data JPA.
 */
@Entity
@Table(
    name = "facturas_acueducto",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_cliente_periodo",
        columnNames = {"id_cliente", "periodo"}
    ),
    indexes = {
        @Index(name = "idx_facturas_id_cliente", columnList = "id_cliente"),
        @Index(name = "idx_facturas_periodo", columnList = "periodo"),
        @Index(name = "idx_facturas_estado", columnList = "estado"),
        @Index(name = "idx_facturas_fecha_vencimiento", columnList = "fecha_vencimiento")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class FacturaAcueducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID del cliente es obligatorio")
    @Size(min = 10, max = 10, message = "El ID del cliente debe tener 10 caracteres")
    @Column(name = "id_cliente", nullable = false, length = 10)
    private String idCliente;

    @NotNull(message = "El periodo es obligatorio")
    @Pattern(regexp = "\\d{6}", message = "El periodo debe tener formato YYYYMM")
    @Column(name = "periodo", nullable = false, length = 6)
    private String periodo;

    @NotNull(message = "El consumo es obligatorio")
    @Min(value = 0, message = "El consumo no puede ser negativo")
    @Column(name = "consumo_m3", nullable = false)
    private Integer consumoM3;

    @NotNull(message = "El valor a pagar es obligatorio")
    @DecimalMin(value = "0.0", message = "El valor a pagar no puede ser negativo")
    @Digits(integer = 10, fraction = 2, message = "El valor a pagar debe tener maximo 10 enteros y 2 decimales")
    @Column(name = "valor_pagar", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorPagar;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoFactura estado;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (estado == null) {
            estado = EstadoFactura.PENDIENTE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Verifica si la factura esta vencida.
     * 
     * @return true si la fecha de vencimiento es anterior a hoy
     */
    public boolean isVencida() {
        if (fechaVencimiento == null) {
            return false;
        }
        return fechaVencimiento.isBefore(LocalDate.now());
    }

    /**
     * Verifica si la factura esta pagada.
     * 
     * @return true si el estado es PAGADA
     */
    public boolean isPagada() {
        return estado == EstadoFactura.PAGADA;
    }

    /**
     * Calcula los dias hasta el vencimiento.
     * 
     * @return numero de dias hasta vencimiento, negativo si ya vencio
     */
    public long getDiasHastaVencimiento() {
        if (fechaVencimiento == null) {
            return 0;
        }
        return LocalDate.now().until(fechaVencimiento, java.time.temporal.ChronoUnit.DAYS);
    }

    /**
     * Estados posibles de una factura.
     */
    public enum EstadoFactura {
        PENDIENTE,
        PAGADA,
        VENCIDA,
        ANULADA
    }
}
