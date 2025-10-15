package com.serviciudad.adapter;

import com.serviciudad.domain.ConsumoEnergia;
import com.serviciudad.exception.ArchivoLecturaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdaptadorArchivoEnergia - Tests Unitarios")
class AdaptadorArchivoEnergiaTest {

    @InjectMocks
    private AdaptadorArchivoEnergia adapter;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(adapter, "archivoPath",
            "src/test/resources/consumos_energia_test.txt");
    }

    @Test
    @DisplayName("Debe leer todos los consumos del archivo correctamente")
    void debeLeerTodosLosConsumos() {
        List<ConsumoEnergia> consumos = adapter.leerConsumos();

        assertThat(consumos).isNotEmpty();
        assertThat(consumos).hasSizeGreaterThan(0);
    }

    @Test
    @DisplayName("Debe parsear linea COBOL correctamente")
    void debeParsearLineaCorrectamente() {
        List<ConsumoEnergia> consumos = adapter.leerConsumos();
        
        assertThat(consumos).isNotEmpty();
        ConsumoEnergia primerConsumo = consumos.get(0);
        
        assertThat(primerConsumo.getIdCliente()).isNotBlank();
        assertThat(primerConsumo.getNombreCliente()).isNotBlank();
        assertThat(primerConsumo.getPeriodo()).matches("\\d{6}");
        assertThat(primerConsumo.getConsumoKwh()).isGreaterThan(0);
        assertThat(primerConsumo.getValorPagar()).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Debe filtrar consumos por cliente")
    void debeFilterConsumosPorCliente() {
        String idCliente = "0001234567";
        
        List<ConsumoEnergia> consumos = adapter.leerConsumosPorCliente(idCliente);
        
        assertThat(consumos).isNotEmpty();
        assertThat(consumos).allMatch(c -> c.getIdCliente().equals(idCliente));
    }

    @Test
    @DisplayName("Debe filtrar consumos por periodo")
    void debeFilterConsumosPorPeriodo() {
        String periodo = "202510";
        
        List<ConsumoEnergia> consumos = adapter.leerConsumosPorPeriodo(periodo);
        
        if (!consumos.isEmpty()) {
            assertThat(consumos).allMatch(c -> c.getPeriodo().equals(periodo));
        }
    }

    @Test
    @DisplayName("Debe buscar consumo especifico por cliente y periodo")
    void debeBuscarConsumoEspecifico() {
        String idCliente = "0001234567";
        String periodo = "202510";
        
        List<ConsumoEnergia> todos = adapter.leerConsumos();
        
        if (todos.stream().anyMatch(c -> 
            c.getIdCliente().equals(idCliente) && c.getPeriodo().equals(periodo))) {
            
            ConsumoEnergia consumo = adapter.buscarConsumo(idCliente, periodo);
            
            assertThat(consumo).isNotNull();
            assertThat(consumo.getIdCliente()).isEqualTo(idCliente);
            assertThat(consumo.getPeriodo()).isEqualTo(periodo);
        }
    }

    @Test
    @DisplayName("Debe validar que el archivo existe")
    void debeValidarArchivo() {
        boolean esValido = adapter.validarArchivo();
        
        assertThat(esValido).isTrue();
    }

    @Test
    @DisplayName("Debe contar registros correctamente")
    void debeContarRegistros() {
        int cantidad = adapter.contarRegistros();
        
        assertThat(cantidad).isGreaterThan(0);
        
        List<ConsumoEnergia> consumos = adapter.leerConsumos();
        assertThat(cantidad).isEqualTo(consumos.size());
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando archivo no existe")
    void debeLanzarExcepcionArchivoNoExiste() {
        ReflectionTestUtils.setField(adapter, "archivoPath",
            "archivo_inexistente.txt");
        
        assertThatThrownBy(() -> adapter.leerConsumos())
            .isInstanceOf(ArchivoLecturaException.class)
            .hasMessageContaining("Error leyendo archivo");
    }

    @Test
    @DisplayName("Debe retornar lista vacia cuando cliente no existe")
    void debeRetornarListaVaciaClienteNoExiste() {
        String clienteInexistente = "9999999999";
        
        List<ConsumoEnergia> consumos = adapter.leerConsumosPorCliente(clienteInexistente);
        
        assertThat(consumos).isEmpty();
    }

    @Test
    @DisplayName("Debe validar precision decimal en valores")
    void debeValidarPrecisionDecimal() {
        List<ConsumoEnergia> consumos = adapter.leerConsumos();
        
        assertThat(consumos).isNotEmpty();
        
        for (ConsumoEnergia consumo : consumos) {
            assertThat(consumo.getValorPagar().scale()).isLessThanOrEqualTo(2);
        }
    }

    @Test
    @DisplayName("Debe validar formato de periodo YYYYMM")
    void debeValidarFormatoPeriodo() {
        List<ConsumoEnergia> consumos = adapter.leerConsumos();
        
        assertThat(consumos).isNotEmpty();
        
        for (ConsumoEnergia consumo : consumos) {
            assertThat(consumo.getPeriodo())
                .matches("\\d{6}")
                .hasSize(6);
            
            int year = Integer.parseInt(consumo.getPeriodo().substring(0, 4));
            int month = Integer.parseInt(consumo.getPeriodo().substring(4, 6));
            
            assertThat(year).isBetween(2020, 2030);
            assertThat(month).isBetween(1, 12);
        }
    }
}
