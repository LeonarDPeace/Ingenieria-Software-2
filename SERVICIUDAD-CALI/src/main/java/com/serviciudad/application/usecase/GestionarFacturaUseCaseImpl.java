package com.serviciudad.application.usecase;

import com.serviciudad.domain.model.FacturaAcueducto;
import com.serviciudad.domain.port.input.GestionarFacturaUseCase;
import com.serviciudad.domain.port.output.FacturaRepositoryPort;
import com.serviciudad.domain.valueobject.ClienteId;
import com.serviciudad.domain.valueobject.FacturaId;
import com.serviciudad.domain.valueobject.Periodo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GestionarFacturaUseCaseImpl implements GestionarFacturaUseCase {

    private final FacturaRepositoryPort facturaRepository;
    
    private static final int BATCH_SIZE = 100;

    @Override
    @Transactional(readOnly = true)
    public FacturaAcueducto consultarFactura(FacturaId facturaId) {
        return facturaRepository.findById(facturaId)
            .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada: " + facturaId.getValor()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacturaAcueducto> consultarFacturasPorCliente(ClienteId clienteId) {
        return facturaRepository.findByClienteId(clienteId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacturaAcueducto> consultarFacturasPorPeriodo(ClienteId clienteId, Periodo periodo) {
        return facturaRepository.findByClienteIdAndPeriodo(clienteId, periodo);
    }

    @Override
    @Transactional
    public void registrarPagoFactura(FacturaId facturaId) {
        FacturaAcueducto factura = consultarFactura(facturaId);
        factura.registrarPago();
        facturaRepository.save(factura);
    }

    @Override
    @Transactional
    public void anularFactura(FacturaId facturaId) {
        FacturaAcueducto factura = consultarFactura(facturaId);
        factura.anular();
        facturaRepository.save(factura);
    }

    @Override
    @Transactional
    public void marcarFacturasVencidas() {
        int pageNumber = 0;
        int totalProcesadas = 0;
        int totalActualizadas = 0;
        
        log.info("Iniciando proceso de marcado de facturas vencidas con paginación (tamaño de lote: {})", BATCH_SIZE);
        
        boolean hasMore;
        do {
            log.debug("Procesando lote {}, facturas procesadas hasta ahora: {}", pageNumber + 1, totalProcesadas);
            
            List<FacturaAcueducto> facturasPendientes = facturaRepository.findFacturasPendientes(pageNumber, BATCH_SIZE);
            
            if (facturasPendientes.isEmpty()) {
                log.debug("No se encontraron más facturas pendientes en el lote {}", pageNumber + 1);
                break;
            }
            
            List<FacturaAcueducto> facturasActualizadas = facturasPendientes.stream()
                .filter(factura -> {
                    boolean vencida = factura.estaVencida();
                    if (vencida) {
                        log.debug("Factura {} está vencida, marcando como vencida", factura.getId().getValor());
                    }
                    return vencida;
                })
                .peek(FacturaAcueducto::marcarComoVencida)
                .collect(Collectors.toList());
            
            if (!facturasActualizadas.isEmpty()) {
                facturaRepository.saveAll(facturasActualizadas);
                facturaRepository.flush();
                totalActualizadas += facturasActualizadas.size();
                log.debug("Lote {}: {} facturas marcadas como vencidas de {} procesadas", 
                         pageNumber + 1, facturasActualizadas.size(), facturasPendientes.size());
            }
            
            totalProcesadas += facturasPendientes.size();
            hasMore = facturaRepository.hasMoreFacturasPendientes(pageNumber, BATCH_SIZE);
            pageNumber++;
            
        } while (hasMore);
        
        log.info("Proceso finalizado. Total facturas procesadas: {}, Total facturas marcadas como vencidas: {}", 
                totalProcesadas, totalActualizadas);
    }
}
