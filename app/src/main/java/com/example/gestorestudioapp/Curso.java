package com.example.gestorestudioapp;

public class Curso {
    private String nombre;
    private String categoria;
    private String frecuencia;
    private String proximaSesion;

    public Curso(String nombre, String categoria, String frecuencia, String proximaSesion) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.frecuencia = frecuencia;
        this.proximaSesion = proximaSesion;
    }

    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public String getFrecuencia() { return frecuencia; }
    public String getProximaSesion() { return proximaSesion; }
}
