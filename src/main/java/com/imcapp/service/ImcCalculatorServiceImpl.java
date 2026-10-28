package com.imcapp.service;

import com.imcapp.model.ClasificacionIMC;
import com.imcapp.model.ResultadoIMC;

public class ImcCalculatorServiceImpl implements IImcCalculatorService {

    public static final double MIN_ESTATURA = 1.00;
    public static final double MAX_ESTATURA = 2.50;
    public static final int MIN_EDAD = 15;

    @Override
    public double calcularIMC(double masaCorporalKg, double estaturaMetros) {
        if (!validarMasaCorporal(masaCorporalKg)) {
            throw new IllegalArgumentException("La masa corporal debe ser mayor a cero.");
        }
        if (!validarEstatura(estaturaMetros)) {
            throw new IllegalArgumentException("La estatura debe estar entre 1.0m y 2.5m.");
        }
        double imc = masaCorporalKg / (estaturaMetros * estaturaMetros);
        return Math.round(imc * 100.0) / 100.0;
    }

    @Override
    public ClasificacionIMC evaluarClasificacion(double imc) {
        if (imc < 18.50) {
            return ClasificacionIMC.BAJO_PESO;
        } else if (imc <= 24.99) {
            return ClasificacionIMC.NORMAL;
        } else if (imc <= 29.99) {
            return ClasificacionIMC.SOBREPESO;
        } else if (imc <= 34.99) {
            return ClasificacionIMC.OBESIDAD_I;
        } else if (imc <= 39.99) {
            return ClasificacionIMC.OBESIDAD_II;
        } else {
            return ClasificacionIMC.OBESIDAD_III;
        }
    }

    @Override
    public boolean validarEstatura(double estatura) {
        return estatura >= MIN_ESTATURA && estatura <= MAX_ESTATURA;
    }

    @Override
    public boolean validarEdad(int edad) {
        return edad >= MIN_EDAD;
    }

    @Override
    public boolean validarMasaCorporal(double masaCorporal) {
        return masaCorporal > 0;
    }

    @Override
    public ResultadoIMC generarResultado(double masaCorporal, double estatura) {
        double imc = calcularIMC(masaCorporal, estatura);
        ClasificacionIMC clasificacion = evaluarClasificacion(imc);

        double pesoMinimo = Math.round((18.5 * estatura * estatura) * 100.0) / 100.0;
        double pesoMaximo = Math.round((24.99 * estatura * estatura) * 100.0) / 100.0;

        String diagnostico = "Su estado actual es " + clasificacion.getDescripcion() + 
                ". Rango saludable para su estatura: " + pesoMinimo + " kg a " + pesoMaximo + " kg.";

        return new ResultadoIMC(masaCorporal, estatura, imc, clasificacion, diagnostico, pesoMinimo, pesoMaximo);
    }
}
