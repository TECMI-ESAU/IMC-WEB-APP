package com.imcapp.model;

import java.io.Serializable;
import java.util.Locale;
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
    private RegistroIMC registroPersistido;

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

    public double getMasaCorporalKg() {
        return masaCorporal;
    }

    public void setMasaCorporalKg(double masaCorporalKg) {
        this.masaCorporal = masaCorporalKg;
    }

    public double getEstatura() {
        return estatura;
    }

    public void setEstatura(double estatura) {
        this.estatura = estatura;
    }

    public double getEstaturaMetros() {
        return estatura;
    }

    public void setEstaturaMetros(double estaturaMetros) {
        this.estatura = estaturaMetros;
    }

    public double getValorImc() {
        return valorImc;
    }

    public void setValorImc(double valorImc) {
        this.valorImc = valorImc;
    }

    public String getValorImcFormateado() {
        return String.format(Locale.US, "%.2f", valorImc);
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

    public RegistroIMC getRegistroPersistido() {
        return registroPersistido;
    }

    public void setRegistroPersistido(RegistroIMC registroPersistido) {
        this.registroPersistido = registroPersistido;
    }
}
