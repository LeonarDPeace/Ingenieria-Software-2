package com.serviciudad.application.usecase;

import com.serviciudad.domain.model.ConsumoEnergiaModel;
import com.serviciudad.domain.model.DeudaConsolidada;
import com.serviciudad.domain.model.FacturaAcueducto;
import com.serviciudad.domain.port.input.ConsultarDeudaUseCase;
import com.serviciudad.domain.port.output.ConsumoEnergiaReaderPort;
import com.serviciudad.domain.port.output.FacturaRepositoryPort;
import com.serviciudad.domain.valueobject.ClienteId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultarDeudaUseCaseImpl implements ConsultarDeudaUseCase {

    private final FacturaRepositoryPort facturaRepository;
    private final ConsumoEnergiaReaderPort consumoEnergiaReader;

    @Override
    @Transactional(readOnly = true)
    public DeudaConsolidada consultarDeudaConsolidada(ClienteId clienteId) {
        List<FacturaAcueducto> facturas = facturaRepository.findByClienteId(clienteId);
        List<ConsumoEnergiaModel> consumos = consumoEnergiaReader.findByClienteId(clienteId);
        
        return DeudaConsolidada.construir(clienteId, facturas, consumos);
    }
}
