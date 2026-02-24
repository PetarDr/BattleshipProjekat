package com.example.battleshipprojekat;

public class Brod {

    int duzina;
    String klasa;
    Pravac pravac = Pravac.SEVER;
    Koordinate koordinate;

    public Brod(int duzina, String klasa, Pravac pravac, Koordinate koordinate) {
        this.duzina = duzina;
        this.klasa = klasa;
        this.pravac = pravac;
        this.koordinate = koordinate;
    }

    public Brod(Koordinate koordinate, String klasa, int duzina) {
        this.koordinate = koordinate;
        this.klasa = klasa;
        this.duzina = duzina;
    }

    public Brod() {
    }

    public int getDuzina() {
        return duzina;
    }

    public String getKlasa() {
        return klasa;
    }

    public Pravac getPravac() {
        return pravac;
    }

    public Koordinate getKoordinate() {
        return koordinate;
    }
}