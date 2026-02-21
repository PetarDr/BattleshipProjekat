package com.example.battleshipprojekat;

public class Brod {
    int duzina;
    String klasa;
    char pravac;
    Koordinate koordinate;

    public Brod(int duzina, String klasa, char pravac, Koordinate koordinate) {
        this.duzina = duzina;
        this.klasa = klasa;
        this.pravac = pravac;
        this.koordinate = koordinate;
    }

    public Brod() {
    }

    public int getDuzina() {
        return duzina;
    }

    public String getKlasa() {
        return klasa;
    }

    public char getPravac() {
        return pravac;
    }

    public Koordinate getKoordinate() {
        return koordinate;
    }
}
