package com.imcapp.model;

import java.io.Serializable;
import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "registroIMC")
@XmlType(propOrder = {"idRegistro", "idUsuario", "masaCorporal", "estatura", "valorImc", "clasificacion", "fechaMedicion"})
public class RegistroIMC implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idRegistro;
    private int idUsuario;
    private double masaCorporal;
    private double estatura;
    private double valorImc;
    private String clasificacion;
    private Date fechaMedicion;

    public RegistroIMC() {
    }

    public RegistroIMC(int idRegistro, int idUsuario, double masaCorporal, double estatura, double valorImc, String clasificacion, Date fechaMedicion) {
        this.idRegistro = idRegistro;
        this.idUsuario = idUsuario;
        this.masaCorporal = masaCorporal;
        this.estatura = estatura;
        this.valorImc = valorImc;
        this.clasificacion = clasificacion;
        this.fechaMedicion = fechaMedicion;
    }

    public RegistroIMC(int idUsuario, double masaCorporal, double estatura, double valorImc, String clasificacion) {
        this.idUsuario = idUsuario;
        this.masaCorporal = masaCorporal;
        this.estatura = estatura;
        this.valorImc = valorImc;
        this.clasificacion = clasificacion;
        this.fechaMedicion = new Date();
    }

    public int getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(int idRegistro) {
        this.idRegistro = idRegistro;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
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

    public String getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(String clasificacion) {
        this.clasificacion = clasificacion;
    }

    public Date getFechaMedicion() {
        return fechaMedicion;
    }

    public void setFechaMedicion(Date fechaMedicion) {
        this.fechaMedicion = fechaMedicion;
    }

    public Date getFechaRegistro() {
        return fechaMedicion;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaMedicion = fechaRegistro;
    }

    @Override
    public String toString() {
        return "RegistroIMC{" +
                "idRegistro=" + idRegistro +
                ", idUsuario=" + idUsuario +
                ", masaCorporal=" + masaCorporal +
                ", estatura=" + estatura +
                ", valorImc=" + valorImc +
                ", clasificacion='" + clasificacion + '\'' +
                ", fechaMedicion=" + fechaMedicion +
                '}';
    }
}
