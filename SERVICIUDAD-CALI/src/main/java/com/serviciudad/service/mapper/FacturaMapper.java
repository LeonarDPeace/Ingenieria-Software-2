package com.serviciudad.service.mapper;

import com.serviciudad.domain.FacturaAcueducto;
import com.serviciudad.dto.FacturaAcueductoDTO;
import com.serviciudad.dto.FacturaRequestDTO;
import com.serviciudad.dto.FacturaResponseDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversiones entre entidad FacturaAcueducto y DTOs.
 * 
 * Implementa el patron Mapper para separar la logica de conversion
 * de objetos de dominio a objetos de transferencia.
 */
@Component
public class FacturaMapper {

    /**
     * Convierte una entidad FacturaAcueducto a DTO.
     * 
     * @param entidad Entidad de dominio
     * @return DTO con datos de la entidad
     */
    public FacturaAcueductoDTO toDTO(FacturaAcueducto entidad) {
        if (entidad == null) {
            return null;
        }

        return FacturaAcueductoDTO.builder()
                .id(entidad.getId())
                .idCliente(entidad.getIdCliente())
                .periodo(entidad.getPeriodo())
                .consumoM3(entidad.getConsumoM3())
                .valorPagar(entidad.getValorPagar())
                .estado(entidad.getEstado())
                .fechaVencimiento(entidad.getFechaVencimiento())
                .fechaCreacion(entidad.getFechaCreacion())
                .fechaActualizacion(entidad.getFechaActualizacion())
                .vencida(entidad.isVencida())
                .diasHastaVencimiento(entidad.getDiasHastaVencimiento())
                .build();
    }

    /**
     * Convierte una entidad FacturaAcueducto a ResponseDTO.
     * 
     * @param entidad Entidad de dominio
     * @return ResponseDTO con datos enriquecidos
     */
    public FacturaResponseDTO toResponseDTO(FacturaAcueducto entidad) {
        if (entidad == null) {
            return null;
        }

        return FacturaResponseDTO.builder()
                .id(entidad.getId())
                .idCliente(entidad.getIdCliente())
                .periodo(entidad.getPeriodo())
                .consumoM3(entidad.getConsumoM3())
                .valorPagar(entidad.getValorPagar())
                .estado(entidad.getEstado())
                .fechaVencimiento(entidad.getFechaVencimiento())
                .fechaCreacion(entidad.getFechaCreacion())
                .fechaActualizacion(entidad.getFechaActualizacion())
                .vencida(entidad.isVencida())
                .pagada(entidad.isPagada())
                .diasHastaVencimiento(entidad.getDiasHastaVencimiento())
                .mensaje(generarMensaje(entidad))
                .build();
    }

    /**
     * Convierte un RequestDTO a entidad FacturaAcueducto.
     * 
     * @param dto DTO con datos de entrada
     * @return Entidad de dominio
     */
    public FacturaAcueducto toEntity(FacturaRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return FacturaAcueducto.builder()
                .idCliente(dto.getIdCliente())
                .periodo(dto.getPeriodo())
                .consumoM3(dto.getConsumoM3())
                .valorPagar(dto.getValorPagar())
                .estado(dto.getEstado() != null ? dto.getEstado() : FacturaAcueducto.EstadoFactura.PENDIENTE)
                .fechaVencimiento(dto.getFechaVencimiento())
                .build();
    }

    /**
     * Actualiza una entidad existente con datos de un RequestDTO.
     * 
     * @param entidad Entidad existente
     * @param dto DTO con nuevos datos
     */
    public void updateEntityFromDTO(FacturaAcueducto entidad, FacturaRequestDTO dto) {
        if (entidad == null || dto == null) {
            return;
        }

        if (dto.getIdCliente() != null) {
            entidad.setIdCliente(dto.getIdCliente());
        }
        if (dto.getPeriodo() != null) {
            entidad.setPeriodo(dto.getPeriodo());
        }
        if (dto.getConsumoM3() != null) {
            entidad.setConsumoM3(dto.getConsumoM3());
        }
        if (dto.getValorPagar() != null) {
            entidad.setValorPagar(dto.getValorPagar());
        }
        if (dto.getEstado() != null) {
            entidad.setEstado(dto.getEstado());
        }
        if (dto.getFechaVencimiento() != null) {
            entidad.setFechaVencimiento(dto.getFechaVencimiento());
        }
    }

    /**
     * Genera un mensaje informativo segun el estado de la factura.
     * 
     * @param entidad Factura
     * @return Mensaje informativo
     */
    private String generarMensaje(FacturaAcueducto entidad) {
        if (entidad.isPagada()) {
            return "Factura pagada";
        }
        
        if (entidad.isVencida()) {
            long diasVencidos = Math.abs(entidad.getDiasHastaVencimiento());
            return String.format("Factura vencida hace %d dias", diasVencidos);
        }
        
        long diasRestantes = entidad.getDiasHastaVencimiento();
        if (diasRestantes > 0 && diasRestantes <= 5) {
            return String.format("Factura proxima a vencer en %d dias", diasRestantes);
        }
        
        if (diasRestantes > 5) {
            return String.format("Factura pendiente - Vence en %d dias", diasRestantes);
        }
        
        return "Factura pendiente de pago";
    }
}
