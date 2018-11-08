package com.jose.energytracker;


public class Alimento {


    private String nombre;
    private int kcal;


    public Alimento(String nombre, int kcal) {
        this.nombre = nombre;
        this.kcal = kcal;
    }


    public String getNombre() {
        return this.nombre;
    }


    public int getKcal() {
        return this.kcal;
    }


    @Override
    public String toString() {
        return (this.nombre + " (" + this.kcal + " kcal)");
    }

}
