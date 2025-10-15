package com.serviciudad.service;

import com.serviciudad.domain.FacturaAcueducto;
import com.serviciudad.dto.FacturaRequestDTO;
import com.serviciudad.dto.FacturaResponseDTO;
import com.serviciudad.exception.FacturaNoEncontradaException;
import com.serviciudad.repository.FacturaAcueductoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FacturaAcueductoService - Tests Unitarios")
class FacturaAcueductoServiceTest {

    @Mock
    private FacturaAcueductoRepository repository;

    @Mock
    private FacturaMapper mapper;

    @InjectMocks
    private FacturaAcueductoService service;

    @Test
    @DisplayName("Debe obtener todas las facturas exitosamente")
    void debeObtenerTodasFacturas() {
        FacturaAcueducto factura1 = crearFacturaEjemplo(1L, "0001234567");
        FacturaAcueducto factura2 = crearFacturaEjemplo(2L, "0007654321");
        
        when(repository.findAll()).thenReturn(Arrays.asList(factura1, factura2));
        when(mapper.toResponseDTO(any())).thenReturn(crearResponseDTO());

        List<FacturaResponseDTO> result = service.obtenerTodas();

        assertThat(result).hasSize(2);
        verify(repository, times(1)).findAll();
        verify(mapper, times(2)).toResponseDTO(any());
    }

    @Test
    @DisplayName("Debe obtener factura por ID exitosamente")
    void debeObtenerFacturaPorId() {
        Long id = 1L;
        FacturaAcueducto factura = crearFacturaEjemplo(id, "0001234567");
        FacturaResponseDTO responseDTO = crearResponseDTO();

        when(repository.findById(id)).thenReturn(Optional.of(factura));
        when(mapper.toResponseDTO(factura)).thenReturn(responseDTO);

        FacturaResponseDTO result = service.obtenerPorId(id);

        assertThat(result).isNotNull();
        assertThat(result.getIdCliente()).isEqualTo("0001234567");
        verify(repository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando factura no existe")
    void debeLanzarExcepcionFacturaNoExiste() {
        Long id = 999L;
        
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenerPorId(id))
            .isInstanceOf(FacturaNoEncontradaException.class)
            .hasMessageContaining("Factura no encontrada");
        
        verify(repository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe crear factura exitosamente")
    void debeCrearFactura() {
        FacturaRequestDTO requestDTO = crearRequestDTO();
        FacturaAcueducto factura = crearFacturaEjemplo(1L, "0001234567");
        FacturaResponseDTO responseDTO = crearResponseDTO();

        when(mapper.toEntity(requestDTO)).thenReturn(factura);
        when(repository.save(any())).thenReturn(factura);
        when(mapper.toResponseDTO(factura)).thenReturn(responseDTO);

        FacturaResponseDTO result = service.crear(requestDTO);

        assertThat(result).isNotNull();
        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("Debe actualizar factura exitosamente")
    void debeActualizarFactura() {
        Long id = 1L;
        FacturaRequestDTO requestDTO = crearRequestDTO();
        FacturaAcueducto facturaExistente = crearFacturaEjemplo(id, "0001234567");
        FacturaResponseDTO responseDTO = crearResponseDTO();

        when(repository.findById(id)).thenReturn(Optional.of(facturaExistente));
        when(repository.save(any())).thenReturn(facturaExistente);
        when(mapper.toResponseDTO(any())).thenReturn(responseDTO);

        FacturaResponseDTO result = service.actualizar(id, requestDTO);

        assertThat(result).isNotNull();
        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("Debe eliminar factura exitosamente")
    void debeEliminarFactura() {
        Long id = 1L;
        FacturaAcueducto factura = crearFacturaEjemplo(id, "0001234567");

        when(repository.findById(id)).thenReturn(Optional.of(factura));
        doNothing().when(repository).deleteById(id);

        service.eliminar(id);

        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Debe obtener facturas por cliente")
    void debeObtenerFacturasPorCliente() {
        String idCliente = "0001234567";
        List<FacturaAcueducto> facturas = Arrays.asList(
            crearFacturaEjemplo(1L, idCliente),
            crearFacturaEjemplo(2L, idCliente)
        );

        when(repository.findByIdCliente(idCliente)).thenReturn(facturas);
        when(mapper.toResponseDTO(any())).thenReturn(crearResponseDTO());

        List<FacturaResponseDTO> result = service.obtenerPorCliente(idCliente);

        assertThat(result).hasSize(2);
        verify(repository, times(1)).findByIdCliente(idCliente);
    }

    @Test
    @DisplayName("Debe calcular deuda total correctamente")
    void debeCalcularDeudaTotal() {
        String idCliente = "0001234567";
        BigDecimal deudaTotal = new BigDecimal("250000.00");

        when(repository.calcularDeudaTotalCliente(idCliente)).thenReturn(deudaTotal);

        BigDecimal result = service.calcularDeudaTotal(idCliente);

        assertThat(result).isEqualByComparingTo(deudaTotal);
        verify(repository, times(1)).calcularDeudaTotalCliente(idCliente);
    }

    @Test
    @DisplayName("Debe obtener facturas vencidas")
    void debeObtenerFacturasVencidas() {
        List<FacturaAcueducto> facturasVencidas = Arrays.asList(
            crearFacturaEjemplo(1L, "0001234567"),
            crearFacturaEjemplo(2L, "0007654321")
        );

        when(repository.findFacturasVencidas(any())).thenReturn(facturasVencidas);
        when(mapper.toResponseDTO(any())).thenReturn(crearResponseDTO());

        List<FacturaResponseDTO> result = service.obtenerFacturasVencidas();

        assertThat(result).hasSize(2);
        verify(repository, times(1)).findFacturasVencidas(any());
    }

    @Test
    @DisplayName("Debe contar facturas pendientes")
    void debeContarFacturasPendientes() {
        String idCliente = "0001234567";
        Long cantidad = 3L;

        when(repository.contarFacturasPendientesCliente(idCliente)).thenReturn(cantidad);

        Long result = service.contarFacturasPendientes(idCliente);

        assertThat(result).isEqualTo(cantidad);
        verify(repository, times(1)).contarFacturasPendientesCliente(idCliente);
    }

    @Test
    @DisplayName("Debe registrar pago de factura")
    void debeRegistrarPago() {
        Long id = 1L;
        FacturaAcueducto factura = crearFacturaEjemplo(id, "0001234567");
        factura.setEstado("PENDIENTE");

        when(repository.findById(id)).thenReturn(Optional.of(factura));
        when(repository.save(any())).thenReturn(factura);
        when(mapper.toResponseDTO(any())).thenReturn(crearResponseDTO());

        FacturaResponseDTO result = service.pagarFactura(id);

        assertThat(result).isNotNull();
        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(any());
    }

    private FacturaAcueducto crearFacturaEjemplo(Long id, String idCliente) {
        FacturaAcueducto factura = new FacturaAcueducto();
        factura.setId(id);
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

    private FacturaRequestDTO crearRequestDTO() {
        FacturaRequestDTO dto = new FacturaRequestDTO();
        dto.setIdCliente("0001234567");
        dto.setNombreCliente("Juan Perez");
        dto.setPeriodo("202510");
        dto.setConsumoMetrosCubicos(15);
        dto.setValorPagar(new BigDecimal("95000.00"));
        dto.setFechaVencimiento(LocalDate.now().plusDays(15));
        return dto;
    }

    private FacturaResponseDTO crearResponseDTO() {
        FacturaResponseDTO dto = new FacturaResponseDTO();
        dto.setId(1L);
        dto.setIdCliente("0001234567");
        dto.setNombreCliente("Juan Perez");
        dto.setPeriodo("202510");
        dto.setConsumoMetrosCubicos(15);
        dto.setValorPagar(new BigDecimal("95000.00"));
        dto.setEstado("PENDIENTE");
        dto.setVencida(false);
        dto.setPagada(false);
        dto.setDiasHastaVencimiento(15);
        return dto;
    }
}
