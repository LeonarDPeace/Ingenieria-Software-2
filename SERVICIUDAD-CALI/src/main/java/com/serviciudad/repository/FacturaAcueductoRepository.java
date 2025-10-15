package com.serviciudad.repository;

import com.serviciudad.domain.FacturaAcueducto;
import com.serviciudad.domain.FacturaAcueducto.EstadoFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository para la entidad FacturaAcueducto.
 * 
 * Implementa el patron Repository utilizando Spring Data JPA.
 * Proporciona metodos de consulta personalizados para las operaciones
 * de negocio sobre facturas de acueducto.
 */
@Repository
public interface FacturaAcueductoRepository extends JpaRepository<FacturaAcueducto, Long> {

    /**
     * Busca todas las facturas de un cliente especifico.
     * 
     * @param idCliente ID del cliente
     * @return Lista de facturas del cliente
     */
    List<FacturaAcueducto> findByIdCliente(String idCliente);

    /**
     * Busca todas las facturas de un periodo especifico.
     * 
     * @param periodo Periodo en formato YYYYMM
     * @return Lista de facturas del periodo
     */
    List<FacturaAcueducto> findByPeriodo(String periodo);

    /**
     * Busca una factura especifica de un cliente en un periodo.
     * 
     * @param idCliente ID del cliente
     * @param periodo Periodo en formato YYYYMM
     * @return Optional con la factura si existe
     */
    Optional<FacturaAcueducto> findByIdClienteAndPeriodo(String idCliente, String periodo);

    /**
     * Busca facturas por estado.
     * 
     * @param estado Estado de la factura
     * @return Lista de facturas con el estado especificado
     */
    List<FacturaAcueducto> findByEstado(EstadoFactura estado);

    /**
     * Busca facturas de un cliente con un estado especifico.
     * 
     * @param idCliente ID del cliente
     * @param estado Estado de la factura
     * @return Lista de facturas
     */
    List<FacturaAcueducto> findByIdClienteAndEstado(String idCliente, EstadoFactura estado);

    /**
     * Busca facturas vencidas (fecha de vencimiento anterior a la fecha actual).
     * 
     * @param fechaActual Fecha de referencia
     * @return Lista de facturas vencidas
     */
    @Query("SELECT f FROM FacturaAcueducto f WHERE f.fechaVencimiento < :fechaActual AND f.estado <> 'PAGADA'")
    List<FacturaAcueducto> findFacturasVencidas(@Param("fechaActual") LocalDate fechaActual);

    /**
     * Busca facturas proximas a vencer (dentro de los proximos N dias).
     * 
     * @param fechaInicio Fecha inicial
     * @param fechaFin Fecha final
     * @return Lista de facturas proximas a vencer
     */
    @Query("SELECT f FROM FacturaAcueducto f WHERE f.fechaVencimiento BETWEEN :fechaInicio AND :fechaFin AND f.estado = 'PENDIENTE'")
    List<FacturaAcueducto> findFacturasProximasAVencer(
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin
    );

    /**
     * Calcula el total de deuda pendiente de un cliente.
     * 
     * @param idCliente ID del cliente
     * @return Total de deuda pendiente
     */
    @Query("SELECT COALESCE(SUM(f.valorPagar), 0) FROM FacturaAcueducto f WHERE f.idCliente = :idCliente AND f.estado <> 'PAGADA'")
    BigDecimal calcularDeudaTotalCliente(@Param("idCliente") String idCliente);

    /**
     * Cuenta el numero de facturas pendientes de un cliente.
     * 
     * @param idCliente ID del cliente
     * @return Numero de facturas pendientes
     */
    @Query("SELECT COUNT(f) FROM FacturaAcueducto f WHERE f.idCliente = :idCliente AND f.estado = 'PENDIENTE'")
    Long contarFacturasPendientesCliente(@Param("idCliente") String idCliente);

    /**
     * Busca facturas con valor a pagar mayor a un monto especifico.
     * 
     * @param monto Monto minimo
     * @return Lista de facturas
     */
    List<FacturaAcueducto> findByValorPagarGreaterThan(BigDecimal monto);

    /**
     * Busca facturas ordenadas por fecha de vencimiento ascendente.
     * 
     * @param idCliente ID del cliente
     * @return Lista de facturas ordenadas
     */
    List<FacturaAcueducto> findByIdClienteOrderByFechaVencimientoAsc(String idCliente);

    /**
     * Verifica si existe una factura para un cliente en un periodo.
     * 
     * @param idCliente ID del cliente
     * @param periodo Periodo en formato YYYYMM
     * @return true si existe la factura
     */
    boolean existsByIdClienteAndPeriodo(String idCliente, String periodo);

    /**
     * Elimina todas las facturas de un cliente.
     * 
     * @param idCliente ID del cliente
     */
    void deleteByIdCliente(String idCliente);
}
