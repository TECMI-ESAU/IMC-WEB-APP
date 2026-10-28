package com.imcapp.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "resultadoIMC")
public class ResultadoIMC implements Serializable {

    private static final long serialVersionUID = 1L;

    private double masaCorporal;
    private double estatura;
    private double valorImc;
    private ClasificacionIMC clasificacion;
    private String mensajeDiagnostico;
    private double pesoMinimoSaludable;
    private double pesoMaximoSaludable;

    public ResultadoIMC() {
    }

    public ResultadoIMC(double masaCorporal, double estatura, double valorImc, ClasificacionIMC clasificacion, String mensajeDiagnostico, double pesoMinimoSaludable, double pesoMaximoSaludable) {
        this.masaCorporal = masaCorporal;
        this.estatura = estatura;
        this.valorImc = valorImc;
        this.clasificacion = clasificacion;
        this.mensajeDiagnostico = mensajeDiagnostico;
        this.pesoMinimoSaludable = pesoMinimoSaludable;
        this.pesoMaximoSaludable = pesoMaximoSaludable;
    }

    public double getMasaCorporal() {
        return masaCorporal;
    }

    public void setMasaCorporal(double masaCorporal) {
        this.masaCorporal = masaCorporal;
    }

    public double getEstatura() {
        return estatura;
    }

    public void setEstatura(double estatura) {
        this.estatura = estatura;
    }

    public double getValorImc() {
        return valorImc;
    }

    public void setValorImc(double valorImc) {
        this.valorImc = valorImc;
    }

    public ClasificacionIMC getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(ClasificacionIMC clasificacion) {
        this.clasificacion = clasificacion;
    }

    public String getMensajeDiagnostico() {
        return mensajeDiagnostico;
    }

    public void setMensajeDiagnostico(String mensajeDiagnostico) {
        this.mensajeDiagnostico = mensajeDiagnostico;
    }

    public double getPesoMinimoSaludable() {
        return pesoMinimoSaludable;
    }

    public void setPesoMinimoSaludable(double pesoMinimoSaludable) {
        this.pesoMinimoSaludable = pesoMinimoSaludable;
    }

    public double getPesoMaximoSaludable() {
        return pesoMaximoSaludable;
    }

    public void setPesoMaximoSaludable(double pesoMaximoSaludable) {
        this.pesoMaximoSaludable = pesoMaximoSaludable;
    }
}
