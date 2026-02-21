package com.example.battleshipprojekat;

public class Koordinate {
    int xKoordinata;
    int yKoordinata;

    public Koordinate() {
    }

    public Koordinate(int xKoordinata, int yKoordinata) {
        this.xKoordinata = xKoordinata;
        this.yKoordinata = yKoordinata;
    }

    public int getxKoordinata() {
        return xKoordinata;
    }

    public int getyKoordinata() {
        return yKoordinata;
    }

    @Override
    public String toString() {
        return "Koordinate{" +
                "xKoordinata=" + xKoordinata +
                ", yKoordinata=" + yKoordinata +
                '}';
    }
}
