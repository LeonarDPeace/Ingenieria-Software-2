package com.serviciudad.infrastructure.adapter.output.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "consumo_energia")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsumoEnergiaJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "cliente_id", nullable = false, length = 10)
    private String clienteId;
    
    @Column(name = "periodo", nullable = false, length = 6)
    private String periodo;
    
    @Column(name = "consumo", nullable = false, precision = 10, scale = 2)
    private BigDecimal consumo;
    
    @Column(name = "valor_pagar", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorPagar;
    
    @Column(name = "fecha_lectura", nullable = false)
    private LocalDate fechaLectura;
    
    @Column(name = "estrato", nullable = false, length = 2)
    private String estrato;
}
