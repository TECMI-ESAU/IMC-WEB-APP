package com.imcapp.service;

import com.imcapp.dao.IRegistroImcDAO;
import com.imcapp.model.ClasificacionIMC;
import com.imcapp.model.RegistroIMC;
import com.imcapp.model.ResultadoIMC;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Pruebas unitarias para la lógica de negocio de cálculo de IMC y clasificación OMS.
 */
public class ImcCalculatorServiceTest {

    private IImcCalculatorService calculatorService;

    // Stub en memoria para DAO de prueba
    private static class MockRegistroImcDAO implements IRegistroImcDAO {
        private int idCounter = 1;
        private RegistroIMC ultimoGuardado;

        @Override
        public boolean insertar(RegistroIMC registro) {
            registro.setIdRegistro(idCounter++);
            registro.setFechaRegistro(new Date());
            this.ultimoGuardado = registro;
            return true;
        }

        @Override
        public List<RegistroIMC> listarPorUsuario(int idUsuario) {
            return null;
        }

        @Override
        public RegistroIMC buscarPorId(int idRegistro) {
            return ultimoGuardado;
        }
    }

    @Before
    public void setUp() {
        calculatorService = new ImcCalculatorServiceImpl(new MockRegistroImcDAO());
    }

    @Test
    public void testCalcularValorImcFormulaQuetelet() {
        // Peso: 70 kg, Estatura: 1.75 m -> IMC = 70 / (1.75 * 1.75) = 22.857...
        double imc = calculatorService.calcularValorImc(70.0, 1.75);
        assertEquals(22.86, imc, 0.01);
    }

    @Test
    public void testClasificacionBajoPeso() {
        // Peso: 45 kg, Estatura: 1.75 m -> IMC = 14.69 (< 18.5)
        double imc = calculatorService.calcularValorImc(45.0, 1.75);
        ClasificacionIMC clasificacion = calculatorService.clasificarImc(imc);
        assertEquals(ClasificacionIMC.BAJO_PESO, clasificacion);
        assertEquals("Bajo peso", clasificacion.getEtiqueta());
    }

    @Test
    public void testClasificacionPesoNormal() {
        // Peso: 68 kg, Estatura: 1.70 m -> IMC = 23.53 (18.5 - 24.9)
        double imc = calculatorService.calcularValorImc(68.0, 1.70);
        ClasificacionIMC clasificacion = calculatorService.clasificarImc(imc);
        assertEquals(ClasificacionIMC.PESO_NORMAL, clasificacion);
        assertEquals("Peso normal", clasificacion.getEtiqueta());
    }

    @Test
    public void testClasificacionSobrepeso() {
        // Peso: 80 kg, Estatura: 1.70 m -> IMC = 27.68 (25.0 - 29.9)
        double imc = calculatorService.calcularValorImc(80.0, 1.70);
        ClasificacionIMC clasificacion = calculatorService.clasificarImc(imc);
        assertEquals(ClasificacionIMC.SOBREPESO, clasificacion);
        assertEquals("Sobrepeso", clasificacion.getEtiqueta());
    }

    @Test
    public void testClasificacionObesidadClaseI() {
        // Peso: 95 kg, Estatura: 1.70 m -> IMC = 32.87 (30.0 - 34.9)
        double imc = calculatorService.calcularValorImc(95.0, 1.70);
        ClasificacionIMC clasificacion = calculatorService.clasificarImc(imc);
        assertEquals(ClasificacionIMC.OBESIDAD_I, clasificacion);
    }

    @Test
    public void testClasificacionObesidadClaseII() {
        // Peso: 110 kg, Estatura: 1.70 m -> IMC = 38.06 (35.0 - 39.9)
        double imc = calculatorService.calcularValorImc(110.0, 1.70);
        ClasificacionIMC clasificacion = calculatorService.clasificarImc(imc);
        assertEquals(ClasificacionIMC.OBESIDAD_II, clasificacion);
    }

    @Test
    public void testClasificacionObesidadClaseIII() {
        // Peso: 130 kg, Estatura: 1.70 m -> IMC = 44.98 (>= 40.0)
        double imc = calculatorService.calcularValorImc(130.0, 1.70);
        ClasificacionIMC clasificacion = calculatorService.clasificarImc(imc);
        assertEquals(ClasificacionIMC.OBESIDAD_III, clasificacion);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPesoNegativoOInvalidoLanzaExcepcion() {
        calculatorService.calcularValorImc(0.0, 1.75);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEstaturaInvalidaLanzaExcepcion() {
        calculatorService.calcularValorImc(70.0, 0.5);
    }

    @Test
    public void testCalcularYRegistrarCompleto() {
        ResultadoIMC resultado = calculatorService.calcularYRegistrar(1, 75.0, 1.80);
        assertNotNull(resultado);
        assertEquals(23.15, resultado.getValorImc(), 0.01);
        assertEquals(ClasificacionIMC.PESO_NORMAL, resultado.getClasificacion());
        assertNotNull(resultado.getRegistroPersistido());
        assertEquals(1, resultado.getRegistroPersistido().getIdRegistro());
    }
}
