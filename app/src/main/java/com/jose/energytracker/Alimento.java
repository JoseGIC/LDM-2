package com.jose.energytracker;


public class Alimento {


    private String nombre;
    private int kcal;
    private int uds;


    public Alimento(String nombre, int kcal) {
        this.nombre = nombre;
        this.kcal = kcal;
        this.uds = 1;
    }


    public Alimento(String nombre, int kcal, int uds) {
        this.nombre = nombre;
        this.kcal = kcal;
        this.uds = uds;
    }


    public Alimento(Alimento alimento) {
        this.nombre = alimento.getNombre();
        this.kcal = alimento.getKcal();
        this.uds = alimento.getUds();
    }


    public String getNombre() {
        return this.nombre;
    }


    public int getKcal() {
        return this.kcal;
    }


    public int getUds() {
        return this.uds;
    }


    public void setUds(int uds) {
        this.uds = uds;
    }


    public void incrementUds() {
        this.uds = this.uds + 1;
    }


    public void decrementUds() {
        this.uds = this.uds - 1;
    }


    @Override
    public String toString() {
        return (this.nombre + " (" + this.kcal + " kcal)  x" + this.uds);
    }


    public boolean equals(Alimento a) {
        return ((this.nombre.equals(a.getNombre())) && (this.kcal == a.getKcal()));
    }

}
