package com.serviciudad.service;

import com.serviciudad.domain.FacturaAcueducto;
import com.serviciudad.dto.FacturaAcueductoDTO;
import com.serviciudad.dto.FacturaRequestDTO;
import com.serviciudad.dto.FacturaResponseDTO;
import com.serviciudad.repository.FacturaAcueductoRepository;
import com.serviciudad.service.exception.FacturaDuplicadaException;
import com.serviciudad.service.exception.FacturaNoEncontradaException;
import com.serviciudad.service.mapper.FacturaMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestion de facturas de acueducto.
 * 
 * Implementa los patrones:
 * - IoC/DI: Inyeccion de dependencias via constructor
 * - Repository: Acceso a datos mediante repository
 * - DTO: Transferencia de datos con objetos especializados
 * - Builder: Construccion de respuestas complejas
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FacturaAcueductoService {

    private final FacturaAcueductoRepository repository;
    private final FacturaMapper mapper;

    /**
     * Obtiene todas las facturas.
     * 
     * @return Lista de facturas
     */
    public List<FacturaResponseDTO> obtenerTodas() {
        log.info("Obteniendo todas las facturas");
        
        return repository.findAll()
                .stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una factura por su ID.
     * 
     * @param id ID de la factura
     * @return Factura encontrada
     * @throws FacturaNoEncontradaException si no existe
     */
    public FacturaResponseDTO obtenerPorId(Long id) {
        log.info("Buscando factura con ID: {}", id);
        
        FacturaAcueducto factura = repository.findById(id)
                .orElseThrow(() -> new FacturaNoEncontradaException("Factura no encontrada con ID: " + id));
        
        return mapper.toResponseDTO(factura);
    }

    /**
     * Obtiene todas las facturas de un cliente.
     * 
     * @param idCliente ID del cliente
     * @return Lista de facturas del cliente
     */
    public List<FacturaResponseDTO> obtenerPorCliente(String idCliente) {
        log.info("Obteniendo facturas del cliente: {}", idCliente);
        
        return repository.findByIdCliente(idCliente)
                .stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene las facturas de un periodo especifico.
     * 
     * @param periodo Periodo en formato YYYYMM
     * @return Lista de facturas del periodo
     */
    public List<FacturaResponseDTO> obtenerPorPeriodo(String periodo) {
        log.info("Obteniendo facturas del periodo: {}", periodo);
        
        return repository.findByPeriodo(periodo)
                .stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una factura especifica de un cliente en un periodo.
     * 
     * @param idCliente ID del cliente
     * @param periodo Periodo en formato YYYYMM
     * @return Factura encontrada
     * @throws FacturaNoEncontradaException si no existe
     */
    public FacturaResponseDTO obtenerPorClienteYPeriodo(String idCliente, String periodo) {
        log.info("Buscando factura del cliente {} en periodo {}", idCliente, periodo);
        
        FacturaAcueducto factura = repository.findByIdClienteAndPeriodo(idCliente, periodo)
                .orElseThrow(() -> new FacturaNoEncontradaException(
                        String.format("Factura no encontrada para cliente %s en periodo %s", idCliente, periodo)));
        
        return mapper.toResponseDTO(factura);
    }

    /**
     * Crea una nueva factura.
     * 
     * @param dto Datos de la factura a crear
     * @return Factura creada
     * @throws FacturaDuplicadaException si ya existe una factura para ese cliente y periodo
     */
    @Transactional
    public FacturaResponseDTO crear(FacturaRequestDTO dto) {
        log.info("Creando nueva factura para cliente {} en periodo {}", dto.getIdCliente(), dto.getPeriodo());
        
        if (repository.existsByIdClienteAndPeriodo(dto.getIdCliente(), dto.getPeriodo())) {
            throw new FacturaDuplicadaException(
                    String.format("Ya existe una factura para el cliente %s en el periodo %s", 
                            dto.getIdCliente(), dto.getPeriodo()));
        }
        
        FacturaAcueducto factura = mapper.toEntity(dto);
        FacturaAcueducto guardada = repository.save(factura);
        
        log.info("Factura creada exitosamente con ID: {}", guardada.getId());
        return mapper.toResponseDTO(guardada);
    }

    /**
     * Actualiza una factura existente.
     * 
     * @param id ID de la factura a actualizar
     * @param dto Nuevos datos de la factura
     * @return Factura actualizada
     * @throws FacturaNoEncontradaException si no existe
     */
    @Transactional
    public FacturaResponseDTO actualizar(Long id, FacturaRequestDTO dto) {
        log.info("Actualizando factura con ID: {}", id);
        
        FacturaAcueducto factura = repository.findById(id)
                .orElseThrow(() -> new FacturaNoEncontradaException("Factura no encontrada con ID: " + id));
        
        mapper.updateEntityFromDTO(factura, dto);
        FacturaAcueducto actualizada = repository.save(factura);
        
        log.info("Factura actualizada exitosamente");
        return mapper.toResponseDTO(actualizada);
    }

    /**
     * Elimina una factura.
     * 
     * @param id ID de la factura a eliminar
     * @throws FacturaNoEncontradaException si no existe
     */
    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando factura con ID: {}", id);
        
        if (!repository.existsById(id)) {
            throw new FacturaNoEncontradaException("Factura no encontrada con ID: " + id);
        }
        
        repository.deleteById(id);
        log.info("Factura eliminada exitosamente");
    }

    /**
     * Obtiene la deuda consolidada de un cliente.
     * Utiliza el patron Builder para construir la respuesta.
     * 
     * @param idCliente ID del cliente
     * @return Deuda consolidada con estadisticas y alertas
     */
    public DeudaConsolidadaBuilder.DeudaConsolidada obtenerDeudaConsolidada(String idCliente) {
        log.info("Calculando deuda consolidada del cliente: {}", idCliente);
        
        List<FacturaResponseDTO> facturas = obtenerPorCliente(idCliente);
        
        return DeudaConsolidadaBuilder.crear()
                .conCliente(idCliente)
                .conFacturas(facturas)
                .calcularEstadisticas()
                .generarResumen()
                .generarAlertas()
                .construir();
    }

    /**
     * Obtiene las facturas vencidas.
     * 
     * @return Lista de facturas vencidas
     */
    public List<FacturaResponseDTO> obtenerFacturasVencidas() {
        log.info("Obteniendo facturas vencidas");
        
        return repository.findFacturasVencidas(LocalDate.now())
                .stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene las facturas proximas a vencer en los proximos N dias.
     * 
     * @param dias Numero de dias
     * @return Lista de facturas proximas a vencer
     */
    public List<FacturaResponseDTO> obtenerProximasAVencer(int dias) {
        log.info("Obteniendo facturas proximas a vencer en {} dias", dias);
        
        LocalDate hoy = LocalDate.now();
        LocalDate fechaLimite = hoy.plusDays(dias);
        
        return repository.findFacturasProximasAVencer(hoy, fechaLimite)
                .stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Calcula la deuda total de un cliente.
     * 
     * @param idCliente ID del cliente
     * @return Total de deuda pendiente
     */
    public BigDecimal calcularDeudaTotal(String idCliente) {
        log.info("Calculando deuda total del cliente: {}", idCliente);
        
        return repository.calcularDeudaTotalCliente(idCliente);
    }

    /**
     * Cuenta las facturas pendientes de un cliente.
     * 
     * @param idCliente ID del cliente
     * @return Numero de facturas pendientes
     */
    public Long contarFacturasPendientes(String idCliente) {
        log.info("Contando facturas pendientes del cliente: {}", idCliente);
        
        return repository.contarFacturasPendientesCliente(idCliente);
    }
}
