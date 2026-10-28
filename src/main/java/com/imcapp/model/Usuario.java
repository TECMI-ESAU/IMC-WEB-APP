package com.imcapp.model;

import java.io.Serializable;
import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idUsuario;
    private String nombreCompleto;
    private String username;
    private String passwordHash;
    private int edad;
    private String sexo;
    private double estatura;
    private Date fechaRegistro;

    public Usuario() {
    }

    public Usuario(int idUsuario, String nombreCompleto, String username, String passwordHash, int edad, String sexo, double estatura, Date fechaRegistro) {
        this.idUsuario = idUsuario;
        this.nombreCompleto = nombreCompleto;
        this.username = username;
        this.passwordHash = passwordHash;
        this.edad = edad;
        this.sexo = sexo;
        this.estatura = estatura;
        this.fechaRegistro = fechaRegistro;
    }

    public Usuario(String nombreCompleto, String username, String passwordHash, int edad, String sexo, double estatura) {
        this.nombreCompleto = nombreCompleto;
        this.username = username;
        this.passwordHash = passwordHash;
        this.edad = edad;
        this.sexo = sexo;
        this.estatura = estatura;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public double getEstatura() {
        return estatura;
    }

    public void setEstatura(double estatura) {
        this.estatura = estatura;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", username='" + username + '\'' +
                ", edad=" + edad +
                ", sexo='" + sexo + '\'' +
                ", estatura=" + estatura +
                '}';
    }
}
