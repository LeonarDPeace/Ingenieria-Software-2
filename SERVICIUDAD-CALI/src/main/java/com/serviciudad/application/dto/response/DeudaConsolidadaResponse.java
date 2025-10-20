package com.serviciudad.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeudaConsolidadaResponse {
    
    private String clienteId;
    private LocalDateTime fechaConsulta;
    
    private BigDecimal deudaTotalAcueducto;
    private BigDecimal deudaTotalEnergia;
    private BigDecimal totalGeneral;
    
    private List<FacturaResponse> facturasAcueducto;
    private List<ConsumoEnergiaResponse> consumosEnergia;
    
    private List<String> alertas;
    private EstadisticasResponse estadisticas;
}
