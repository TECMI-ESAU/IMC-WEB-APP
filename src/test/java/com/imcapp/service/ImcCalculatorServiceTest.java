package com.imcapp.service;

import com.imcapp.model.ClasificacionIMC;
import com.imcapp.model.ResultadoIMC;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Pruebas unitarias para la lógica de negocio de cálculo de IMC y clasificación OMS.
 */
public class ImcCalculatorServiceTest {

    private IImcCalculatorService calculatorService;

    @Before
    public void setUp() {
        calculatorService = new ImcCalculatorServiceImpl();
    }

    @Test
    public void testCalcularValorImcFormulaQuetelet() {
        // Peso: 70 kg, Estatura: 1.75 m -> IMC = 70 / (1.75 * 1.75) = 22.86
        double imc = calculatorService.calcularIMC(70.0, 1.75);
        assertEquals(22.86, imc, 0.01);
    }

    @Test
    public void testClasificacionBajoPeso() {
        // IMC < 18.5
        ClasificacionIMC clasificacion = calculatorService.evaluarClasificacion(16.5);
        assertEquals(ClasificacionIMC.BAJO_PESO, clasificacion);
        assertEquals("Bajo Peso", clasificacion.getDescripcion());
    }

    @Test
    public void testClasificacionPesoNormal() {
        // 18.5 <= IMC <= 24.99
        ClasificacionIMC clasificacion = calculatorService.evaluarClasificacion(22.86);
        assertEquals(ClasificacionIMC.NORMAL, clasificacion);
        assertEquals("Peso Normal (Saludable)", clasificacion.getDescripcion());
    }

    @Test
    public void testClasificacionSobrepeso() {
        // 25.0 <= IMC <= 29.99
        ClasificacionIMC clasificacion = calculatorService.evaluarClasificacion(27.5);
        assertEquals(ClasificacionIMC.SOBREPESO, clasificacion);
        assertEquals("Sobrepeso (Preobesidad)", clasificacion.getDescripcion());
    }

    @Test
    public void testClasificacionObesidadClaseI() {
        // 30.0 <= IMC <= 34.99
        ClasificacionIMC clasificacion = calculatorService.evaluarClasificacion(32.0);
        assertEquals(ClasificacionIMC.OBESIDAD_I, clasificacion);
        assertEquals("Obesidad Grado I", clasificacion.getDescripcion());
    }

    @Test
    public void testClasificacionObesidadClaseII() {
        // 35.0 <= IMC <= 39.99
        ClasificacionIMC clasificacion = calculatorService.evaluarClasificacion(37.5);
        assertEquals(ClasificacionIMC.OBESIDAD_II, clasificacion);
        assertEquals("Obesidad Grado II", clasificacion.getDescripcion());
    }

    @Test
    public void testClasificacionObesidadClaseIII() {
        // IMC >= 40.0
        ClasificacionIMC clasificacion = calculatorService.evaluarClasificacion(42.0);
        assertEquals(ClasificacionIMC.OBESIDAD_III, clasificacion);
        assertEquals("Obesidad Grado III", clasificacion.getDescripcion());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPesoNegativoOInvalidoLanzaExcepcion() {
        calculatorService.calcularIMC(0.0, 1.75);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEstaturaInvalidaLanzaExcepcion() {
        calculatorService.calcularIMC(70.0, 0.5);
    }

    @Test
    public void testGenerarResultadoCompleto() {
        ResultadoIMC resultado = calculatorService.generarResultado(70.0, 1.75);
        assertNotNull(resultado);
        assertEquals(22.86, resultado.getValorImc(), 0.01);
        assertEquals(ClasificacionIMC.NORMAL, resultado.getClasificacion());
        assertTrue(resultado.getPesoMinimoSaludable() > 0);
        assertTrue(resultado.getPesoMaximoSaludable() > resultado.getPesoMinimoSaludable());
    }
}
