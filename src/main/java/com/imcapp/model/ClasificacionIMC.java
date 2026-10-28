package com.imcapp.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ClasificacionIMC")
@XmlEnum
public enum ClasificacionIMC {
    @XmlEnumValue("Bajo Peso")
    BAJO_PESO("Bajo Peso", 0.00, 18.49, "#38bdf8", "Riesgo aumentado / Déficit nutricional"),

    @XmlEnumValue("Peso Normal")
    NORMAL("Peso Normal (Saludable)", 18.50, 24.99, "#22c55e", "Riesgo bajo / Promedio óptimo"),

    @XmlEnumValue("Sobrepeso")
    SOBREPESO("Sobrepeso (Preobesidad)", 25.00, 29.99, "#eab308", "Riesgo aumentado"),

    @XmlEnumValue("Obesidad Grado I")
    OBESIDAD_I("Obesidad Grado I", 30.00, 34.99, "#f97316", "Riesgo moderado"),

    @XmlEnumValue("Obesidad Grado II")
    OBESIDAD_II("Obesidad Grado II", 35.00, 39.99, "#ef4444", "Riesgo severo"),

    @XmlEnumValue("Obesidad Grado III")
    OBESIDAD_III("Obesidad Grado III", 40.00, 99.99, "#991b1b", "Riesgo muy alto / Mórbido");

    private final String descripcion;
    private final double limiteInferior;
    private final double limiteSuperior;
    private final String colorHex;
    private final String nivelRiesgo;

    ClasificacionIMC(String descripcion, double limiteInferior, double limiteSuperior, String colorHex, String nivelRiesgo) {
        this.descripcion = descripcion;
        this.limiteInferior = limiteInferior;
        this.limiteSuperior = limiteSuperior;
        this.colorHex = colorHex;
        this.nivelRiesgo = nivelRiesgo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getLimiteInferior() {
        return limiteInferior;
    }

    public double getLimiteSuperior() {
        return limiteSuperior;
    }

    public String getColorHex() {
        return colorHex;
    }

    public String getNivelRiesgo() {
        return nivelRiesgo;
    }
}
