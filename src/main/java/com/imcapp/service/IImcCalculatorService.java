package com.imcapp.service;

import com.imcapp.model.ClasificacionIMC;
import com.imcapp.model.ResultadoIMC;

public interface IImcCalculatorService {
    double calcularIMC(double masaCorporalKg, double estaturaMetros);
    ClasificacionIMC evaluarClasificacion(double imc);
    boolean validarEstatura(double estatura);
    boolean validarEdad(int edad);
    boolean validarMasaCorporal(double masaCorporal);
    ResultadoIMC generarResultado(double masaCorporal, double estatura);
}
