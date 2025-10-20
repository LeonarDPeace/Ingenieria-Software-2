package com.serviciudad.domain.port.input;

import com.serviciudad.domain.model.FacturaAcueducto;
import com.serviciudad.domain.valueobject.ClienteId;
import com.serviciudad.domain.valueobject.FacturaId;
import com.serviciudad.domain.valueobject.Periodo;

import java.util.List;

public interface GestionarFacturaUseCase {
    
    FacturaAcueducto consultarFactura(FacturaId facturaId);
    
    List<FacturaAcueducto> consultarFacturasPorCliente(ClienteId clienteId);
    
    List<FacturaAcueducto> consultarFacturasPorPeriodo(ClienteId clienteId, Periodo periodo);
    
    void registrarPagoFactura(FacturaId facturaId);
    
    void anularFactura(FacturaId facturaId);
    
    void marcarFacturasVencidas();
}
