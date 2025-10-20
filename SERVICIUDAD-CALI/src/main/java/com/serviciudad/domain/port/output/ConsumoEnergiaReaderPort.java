package com.serviciudad.domain.port.output;

import com.serviciudad.domain.model.ConsumoEnergiaModel;
import com.serviciudad.domain.valueobject.ClienteId;
import com.serviciudad.domain.valueobject.Periodo;

import java.util.List;

public interface ConsumoEnergiaReaderPort {
    
    List<ConsumoEnergiaModel> findByClienteId(ClienteId clienteId);
    
    List<ConsumoEnergiaModel> findByClienteIdAndPeriodo(ClienteId clienteId, Periodo periodo);
    
    List<ConsumoEnergiaModel> findConsumosElevados(ClienteId clienteId);
}
