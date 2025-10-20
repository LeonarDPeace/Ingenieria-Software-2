package com.serviciudad.domain.port.input;

import com.serviciudad.domain.model.DeudaConsolidada;
import com.serviciudad.domain.valueobject.ClienteId;

public interface ConsultarDeudaUseCase {
    
    DeudaConsolidada consultarDeudaConsolidada(ClienteId clienteId);
}
