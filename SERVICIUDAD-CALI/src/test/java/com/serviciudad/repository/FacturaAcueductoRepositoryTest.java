package com.serviciudad.repository;

import com.serviciudad.domain.FacturaAcueducto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("FacturaAcueductoRepository - Tests JPA")
class FacturaAcueductoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FacturaAcueductoRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Debe guardar y recuperar factura correctamente")
    void debeGuardarYRecuperarFactura() {
        FacturaAcueducto factura = crearFacturaEjemplo("0001234567");

        FacturaAcueducto guardada = repository.save(factura);
        entityManager.flush();
        entityManager.clear();

        Optional<FacturaAcueducto> recuperada = repository.findById(guardada.getId());

        assertThat(recuperada).isPresent();
        assertThat(recuperada.get().getIdCliente()).isEqualTo("0001234567");
        assertThat(recuperada.get().getValorPagar()).isEqualByComparingTo(new BigDecimal("95000.00"));
    }

    @Test
    @DisplayName("Debe encontrar facturas por ID de cliente")
    void debeEncontrarPorIdCliente() {
        String idCliente = "0001234567";
        repository.save(crearFacturaEjemplo(idCliente));
        repository.save(crearFacturaEjemplo(idCliente));
        repository.save(crearFacturaEjemplo("0007654321"));

        List<FacturaAcueducto> facturas = repository.findByIdCliente(idCliente);

        assertThat(facturas).hasSize(2);
        assertThat(facturas).allMatch(f -> f.getIdCliente().equals(idCliente));
    }

    @Test
    @DisplayName("Debe encontrar facturas por periodo")
    void debeEncontrarPorPeriodo() {
        String periodo = "202510";
        FacturaAcueducto factura1 = crearFacturaEjemplo("0001234567");
        factura1.setPeriodo(periodo);
        repository.save(factura1);

        FacturaAcueducto factura2 = crearFacturaEjemplo("0007654321");
        factura2.setPeriodo(periodo);
        repository.save(factura2);

        FacturaAcueducto factura3 = crearFacturaEjemplo("0009876543");
        factura3.setPeriodo("202509");
        repository.save(factura3);

        List<FacturaAcueducto> facturas = repository.findByPeriodo(periodo);

        assertThat(facturas).hasSize(2);
        assertThat(facturas).allMatch(f -> f.getPeriodo().equals(periodo));
    }

    @Test
    @DisplayName("Debe encontrar facturas vencidas")
    void debeEncontrarFacturasVencidas() {
        FacturaAcueducto facturaVencida = crearFacturaEjemplo("0001234567");
        facturaVencida.setFechaVencimiento(LocalDate.now().minusDays(5));
        facturaVencida.setEstado("VENCIDA");
        repository.save(facturaVencida);

        FacturaAcueducto facturaPendiente = crearFacturaEjemplo("0007654321");
        facturaPendiente.setFechaVencimiento(LocalDate.now().plusDays(10));
        facturaPendiente.setEstado("PENDIENTE");
        repository.save(facturaPendiente);

        List<FacturaAcueducto> vencidas = repository.findFacturasVencidas(LocalDate.now());

        assertThat(vencidas).hasSize(1);
        assertThat(vencidas.get(0).getFechaVencimiento()).isBefore(LocalDate.now());
    }

    @Test
    @DisplayName("Debe calcular deuda total de un cliente")
    void debeCalcularDeudaTotalCliente() {
        String idCliente = "0001234567";
        
        FacturaAcueducto factura1 = crearFacturaEjemplo(idCliente);
        factura1.setValorPagar(new BigDecimal("95000.00"));
        factura1.setEstado("PENDIENTE");
        repository.save(factura1);

        FacturaAcueducto factura2 = crearFacturaEjemplo(idCliente);
        factura2.setValorPagar(new BigDecimal("108000.00"));
        factura2.setEstado("PENDIENTE");
        repository.save(factura2);

        FacturaAcueducto factura3 = crearFacturaEjemplo(idCliente);
        factura3.setValorPagar(new BigDecimal("50000.00"));
        factura3.setEstado("PAGADA");
        repository.save(factura3);

        BigDecimal deudaTotal = repository.calcularDeudaTotalCliente(idCliente);

        assertThat(deudaTotal).isEqualByComparingTo(new BigDecimal("203000.00"));
    }

    @Test
    @DisplayName("Debe contar facturas pendientes de un cliente")
    void debeContarFacturasPendientes() {
        String idCliente = "0001234567";
        
        FacturaAcueducto factura1 = crearFacturaEjemplo(idCliente);
        factura1.setEstado("PENDIENTE");
        repository.save(factura1);

        FacturaAcueducto factura2 = crearFacturaEjemplo(idCliente);
        factura2.setEstado("PENDIENTE");
        repository.save(factura2);

        FacturaAcueducto factura3 = crearFacturaEjemplo(idCliente);
        factura3.setEstado("PAGADA");
        repository.save(factura3);

        Long cantidad = repository.contarFacturasPendientesCliente(idCliente);

        assertThat(cantidad).isEqualTo(2L);
    }

    @Test
    @DisplayName("Debe actualizar factura existente")
    void debeActualizarFactura() {
        FacturaAcueducto factura = repository.save(crearFacturaEjemplo("0001234567"));
        Long id = factura.getId();

        factura.setConsumoMetrosCubicos(20);
        factura.setValorPagar(new BigDecimal("120000.00"));
        repository.save(factura);

        entityManager.flush();
        entityManager.clear();

        FacturaAcueducto actualizada = repository.findById(id).orElseThrow();
        assertThat(actualizada.getConsumoMetrosCubicos()).isEqualTo(20);
        assertThat(actualizada.getValorPagar()).isEqualByComparingTo(new BigDecimal("120000.00"));
    }

    @Test
    @DisplayName("Debe eliminar factura")
    void debeEliminarFactura() {
        FacturaAcueducto factura = repository.save(crearFacturaEjemplo("0001234567"));
        Long id = factura.getId();

        repository.deleteById(id);
        entityManager.flush();

        Optional<FacturaAcueducto> eliminada = repository.findById(id);
        assertThat(eliminada).isEmpty();
    }

    @Test
    @DisplayName("Debe retornar lista vacia cuando no hay facturas")
    void debeRetornarListaVaciaCuandoNoHayFacturas() {
        List<FacturaAcueducto> facturas = repository.findByIdCliente("9999999999");
        
        assertThat(facturas).isEmpty();
    }

    @Test
    @DisplayName("Debe generar ID automaticamente")
    void debeGenerarIdAutomaticamente() {
        FacturaAcueducto factura = crearFacturaEjemplo("0001234567");
        assertThat(factura.getId()).isNull();

        FacturaAcueducto guardada = repository.save(factura);
        
        assertThat(guardada.getId()).isNotNull();
        assertThat(guardada.getId()).isGreaterThan(0L);
    }

    @Test
    @DisplayName("Debe validar unicidad de cliente y periodo")
    void debeValidarUnicidadClientePeriodo() {
        FacturaAcueducto factura1 = crearFacturaEjemplo("0001234567");
        factura1.setPeriodo("202510");
        repository.save(factura1);

        FacturaAcueducto factura2 = crearFacturaEjemplo("0001234567");
        factura2.setPeriodo("202510");

        assertThatThrownBy(() -> {
            repository.save(factura2);
            entityManager.flush();
        }).hasMessageContaining("constraint");
    }

    @Test
    @DisplayName("Debe persistir timestamps automaticamente")
    void debePersistirTimestamps() {
        FacturaAcueducto factura = crearFacturaEjemplo("0001234567");
        
        FacturaAcueducto guardada = repository.save(factura);
        entityManager.flush();

        assertThat(guardada.getCreatedAt()).isNotNull();
        assertThat(guardada.getUpdatedAt()).isNotNull();
    }

    private FacturaAcueducto crearFacturaEjemplo(String idCliente) {
        FacturaAcueducto factura = new FacturaAcueducto();
        factura.setIdCliente(idCliente);
        factura.setNombreCliente("Juan Perez");
        factura.setPeriodo("202510");
        factura.setConsumoMetrosCubicos(15);
        factura.setValorPagar(new BigDecimal("95000.00"));
        factura.setFechaEmision(LocalDate.now());
        factura.setFechaVencimiento(LocalDate.now().plusDays(15));
        factura.setEstado("PENDIENTE");
        return factura;
    }
}
