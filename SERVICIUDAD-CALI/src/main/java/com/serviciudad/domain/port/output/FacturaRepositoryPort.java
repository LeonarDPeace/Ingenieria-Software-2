package com.serviciudad.domain.port.output;

import com.serviciudad.domain.model.FacturaAcueducto;
import com.serviciudad.domain.valueobject.ClienteId;
import com.serviciudad.domain.valueobject.FacturaId;
import com.serviciudad.domain.valueobject.Periodo;

import java.util.List;
import java.util.Optional;

public interface FacturaRepositoryPort {
    
    Optional<FacturaAcueducto> findById(FacturaId facturaId);
    
    List<FacturaAcueducto> findByClienteId(ClienteId clienteId);
    
    List<FacturaAcueducto> findByClienteIdAndPeriodo(ClienteId clienteId, Periodo periodo);
    
    List<FacturaAcueducto> findFacturasPendientes();
    
    List<FacturaAcueducto> findFacturasPendientes(int pageNumber, int pageSize);
    
    boolean hasMoreFacturasPendientes(int pageNumber, int pageSize);
    
    List<FacturaAcueducto> findFacturasVencidas();
    
    FacturaAcueducto save(FacturaAcueducto factura);
    
    void saveAll(List<FacturaAcueducto> facturas);
    
    void flush();
}
