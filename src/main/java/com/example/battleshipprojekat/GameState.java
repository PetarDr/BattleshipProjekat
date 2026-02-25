package com.example.battleshipprojekat;

import javafx.scene.control.Button;
import java.util.*;

public class GameState {

    public static final GameState INSTANCE = new GameState();

    // 0=prazan, 1=brod, 2=pogodak, 3=masi
    public int[][] igracTabla = new int[10][10];
    public int[][] aiTabla = new int[10][10];

    public Button[][] igracDugmad = new Button[10][10];
    public Button[][] aiDugmad = new Button[10][10];

    // jebem li ti hashmape pola sata sam radio ovo sranje
    public Map<Integer, Integer> brodoviKojiTrebajuBivatiPostavljeni = new LinkedHashMap<>();

    public int selectedShipSize = 0;
    public Pravac pravac = Pravac.SEVER;

    public boolean vremeStavljanja = true;
    public boolean igracPotez = true;
    public boolean gameOver     = false;

    public int playerShipsTotal = 0;
    public int enemyShipsTotal  = 0;
    public int igracPogodci = 0;
    public int aiPogodci = 0;

    private GameState() { reset(); }

    public void reset() {
        igracTabla = new int[10][10];
        aiTabla = new int[10][10];
        brodoviKojiTrebajuBivatiPostavljeni = new LinkedHashMap<>();
        brodoviKojiTrebajuBivatiPostavljeni.put(5, 1);
        brodoviKojiTrebajuBivatiPostavljeni.put(4, 1);
        brodoviKojiTrebajuBivatiPostavljeni.put(3, 2);
        brodoviKojiTrebajuBivatiPostavljeni.put(2, 2);
        selectedShipSize = 0;
        pravac = Pravac.SEVER;
        vremeStavljanja = true;
        igracPotez = true;
        gameOver     = false;
        playerShipsTotal = 5+4+3+3+2+2;
        enemyShipsTotal  = 5+4+3+3+2+2;
        igracPogodci = 0;
        aiPogodci = 0;
    }

    /** Returns list of (row,col) cells a ship of given size would occupy */
    public List<int[]> shipCells(int row, int col, int size, Pravac dir) {
        List<int[]> cells = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int red = row, kolona = col;
            switch (dir) {
                case SEVER -> red = row - i;
                case JUG   -> red = row + i;
                case ISTOK -> kolona = col + i;
                case ZAPAD -> kolona = col - i;
            }
            cells.add(new int[]{red, kolona});
        }
        return cells;
    }

    public boolean canPlace(int row, int col, int size, Pravac dir, int[][] board) {
        List<int[]> cells = shipCells(row, col, size, dir);
        for (int[] cell : cells) {
            int red = cell[0], kolona = cell[1];
            if (red < 0 || red >= 10 || kolona < 0 || kolona >= 10) return false;
            if (board[red][kolona] != 0) return false;
            // kancer cu da dobijem od ovoga
            for (int x = -1; x <= 1; x++)
                for (int y = -1; y <= 1; y++) {
                    int noviRed = red + x, novaKolona = kolona + y;
                    if (noviRed >= 0 && noviRed < 10 && novaKolona >= 0 && novaKolona < 10 && board[noviRed][novaKolona] == 1)
                        return false;
                }
        }
        return true;
    }

    public void postaviBrod(int row, int col, int size, Pravac dir, int[][] board) {
        for (int[] cell : shipCells(row, col, size, dir)) {
            board[cell[0]][cell[1]] = 1;
        }
    }


    public void postaviAiBrodove() {
        Random rand = new Random();
        int[] sizes = {5, 4, 3, 3, 2, 2};
        Pravac[] pravci = Pravac.values();
        for (int size : sizes) {
            boolean placed = false;
            while (!placed) {
                int red = rand.nextInt(10);
                int kolona = rand.nextInt(10);
                Pravac d = pravci[rand.nextInt(4)];
                if (canPlace(red, kolona, size, d, aiTabla)) {
                    postaviBrod(red, kolona, size, d, aiTabla);
                    placed = true;
                }
            }
        }
    }

    public boolean allShipsPlaced() {
        for (int v : brodoviKojiTrebajuBivatiPostavljeni.values()) if (v > 0) return false;
        return true;
    }
}
