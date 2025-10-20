package com.serviciudad.domain.port.input;

import com.serviciudad.domain.model.ConsumoEnergiaModel;
import com.serviciudad.domain.valueobject.ClienteId;
import com.serviciudad.domain.valueobject.Periodo;

import java.util.List;

public interface ConsultarConsumoEnergiaUseCase {
    
    List<ConsumoEnergiaModel> consultarConsumosPorCliente(ClienteId clienteId);
    
    List<ConsumoEnergiaModel> consultarConsumosPorPeriodo(ClienteId clienteId, Periodo periodo);
    
    List<ConsumoEnergiaModel> consultarConsumosElevados(ClienteId clienteId);
}
