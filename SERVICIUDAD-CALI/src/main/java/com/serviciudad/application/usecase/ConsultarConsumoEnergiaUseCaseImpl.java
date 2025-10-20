package com.serviciudad.application.usecase;

import com.serviciudad.domain.model.ConsumoEnergiaModel;
import com.serviciudad.domain.port.input.ConsultarConsumoEnergiaUseCase;
import com.serviciudad.domain.port.output.ConsumoEnergiaReaderPort;
import com.serviciudad.domain.valueobject.ClienteId;
import com.serviciudad.domain.valueobject.Periodo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultarConsumoEnergiaUseCaseImpl implements ConsultarConsumoEnergiaUseCase {

    private final ConsumoEnergiaReaderPort consumoEnergiaReader;

    @Override
    @Transactional(readOnly = true)
    public List<ConsumoEnergiaModel> consultarConsumosPorCliente(ClienteId clienteId) {
        return consumoEnergiaReader.findByClienteId(clienteId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsumoEnergiaModel> consultarConsumosPorPeriodo(ClienteId clienteId, Periodo periodo) {
        return consumoEnergiaReader.findByClienteIdAndPeriodo(clienteId, periodo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsumoEnergiaModel> consultarConsumosElevados(ClienteId clienteId) {
        return consumoEnergiaReader.findConsumosElevados(clienteId);
    }
}
