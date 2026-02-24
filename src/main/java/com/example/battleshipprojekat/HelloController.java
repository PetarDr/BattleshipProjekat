package com.example.battleshipprojekat;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class HelloController implements Initializable {

    //da nisam skonto da treba gamestate iz najradnom yt tutorijala nebi nikad ovo uradili
    private final GameState statusIgre = GameState.INSTANCE;

    @FXML
    private GridPane aiGrid;
    @FXML
    private GridPane igracGrid;

    private static boolean daLiSeBirajuBrodići = false;
    private static Stage stageZaOdabirBrodića = null; // ovo treba da se zatvori

    // bojice njam njam :3 (OVO ZAMENITI SLIKAMA PRE ROKA)
    private static final String BOJA_NEPRIJATLJSKE_VODE = "-fx-background-color: #00cccc;";
    private static final String BOJA_NASE_VODE = "-fx-background-color: #001a66;";
    private static final String BOJA_BRODA = "-fx-background-color: #607d8b;";
    private static final String BOJA_BRODA_PLACEHOLDER = "-fx-background-color: #a5d6a7;";
    private static final String BOJA_LOSEG_STAVLJANJA = "-fx-background-color: #ef9a9a;";
    private static final String BOJA_POGOTKA = "-fx-background-color: #e53935;";
    private static final String BOJA_PROMASAJA = "-fx-background-color: #eceff1;";
    private static final String BOJA_POTOPLJENO = "-fx-background-color: #880e4f;";
    private static final String BTN_BASE = "-fx-border-color: #455a64; -fx-border-radius: 4; -fx-background-radius: 4;";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String path = url != null ? url.getPath() : "";
        if (path.contains("hello-view.fxml")) {
            buildGrids();
            otvoriOdabirBrodića();
        }
    }


    //e ovde vec postaje zajebano fala kurcu za indijce na yt
    private void buildGrids() {
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {

                Button aiPolja = makeBtn(BOJA_NEPRIJATLJSKE_VODE);
                final int row = r, col = c;
                aiPolja.setOnAction(e -> handleEnemyClick(row, col, aiPolja));
                aiPolja.setOnMouseEntered(e -> showPreviewEnemy(row, col));
                aiPolja.setOnMouseExited(e -> clearPreviewEnemy());
                aiGrid.add(aiPolja, c, r);
                statusIgre.aiDugmad[r][c] = aiPolja;

                Button igracPolja = makeBtn(BOJA_NASE_VODE);
                igracPolja.setOnAction(e -> handlePlayerClick(row, col));
                igracPolja.setOnMouseEntered(e -> showPreviewPlayer(row, col));
                igracPolja.setOnMouseExited(e -> clearPreviewPlayer());
                igracGrid.add(igracPolja, c, r);
                statusIgre.igracDugmad[r][c] = igracPolja;
            }
        }
    }

    private Button makeBtn(String color) {
        Button b = new Button();
        b.setPrefSize(52, 46);
        b.setStyle(color + BTN_BASE);
        return b;
    }

    private void otvoriOdabirBrodića() {
        if (daLiSeBirajuBrodići) return;
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("odabir_brodova.fxml"));
            Scene scene = new Scene(loader.load(), 600, 400);
            Stage stage = new Stage();
            stage.setTitle("Odabir brodića");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setX(1120);
            stage.setY(220);
            stageZaOdabirBrodića = stage;
            daLiSeBirajuBrodići = true;
            stage.setOnCloseRequest(e -> {
                daLiSeBirajuBrodići = false;
                stageZaOdabirBrodića = null;
            });
            stage.show();
            stage.setAlwaysOnTop(true);
            stage.toFront();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    //onaj kurac za stavljane brodova
    private void handlePlayerClick(int row, int col) {
        if (!statusIgre.vremeStavljanja) return;
        if (statusIgre.selectedShipSize == 0) return;
        if (!statusIgre.canPlace(row, col, statusIgre.selectedShipSize, statusIgre.pravac, statusIgre.igracTabla))
            return;

        statusIgre.postaviBrod(row, col, statusIgre.selectedShipSize, statusIgre.pravac, statusIgre.igracTabla);

        for (int[] cell : statusIgre.shipCells(row, col, statusIgre.selectedShipSize, statusIgre.pravac)) {
            statusIgre.igracDugmad[cell[0]][cell[1]].setStyle(BOJA_BRODA + BTN_BASE);
        }

        int remaining = statusIgre.brodoviKojiTrebajuBivatiPostavljeni.getOrDefault(statusIgre.selectedShipSize, 0);
        if (remaining > 0)
            statusIgre.brodoviKojiTrebajuBivatiPostavljeni.put(statusIgre.selectedShipSize, remaining - 1);
        statusIgre.selectedShipSize = 0;

        OdabirController.notifyUpdate();

        if (statusIgre.allShipsPlaced()) {
            statusIgre.vremeStavljanja = false;
            statusIgre.postaviAiBrodove();
            Platform.runLater(() -> {
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Igra počinje!");
                a.setHeaderText(null);
                a.setContentText("Svi brodovi su postavljeni! Pucaj na plavu (gornju) mrežu protivnika.");
                a.showAndWait();
                if (stageZaOdabirBrodića != null) {
                    stageZaOdabirBrodića.close();
                    stageZaOdabirBrodića = null;
                    daLiSeBirajuBrodići = false;
                }
            });
        }
    }

    //gadjanje na njihovu stranu
    private void handleEnemyClick(int row, int col, Button btn) {
        if (statusIgre.vremeStavljanja || statusIgre.gameOver || !statusIgre.igracPotez) return;
        int status = statusIgre.aiTabla[row][col];
        if (status == 2 || status == 3) return;
        // 0=prazan, 1=brod, 2=pogodak, 3=masi
        if (status == 1) {
            statusIgre.aiTabla[row][col] = 2;
            btn.setStyle(BOJA_POGOTKA + BTN_BASE);
            statusIgre.igracPogodci++;
            checkSunkEnemy(row, col);
        } else {
            statusIgre.aiTabla[row][col] = 3;
            btn.setStyle(BOJA_PROMASAJA + BTN_BASE);
        }
        clearPreviewEnemy();

        if (statusIgre.igracPogodci >= statusIgre.enemyShipsTotal) {
            statusIgre.gameOver = true;
            showResult("Čestitke! Pobijedio si! 🎉");
            return;
        }

        statusIgre.igracPotez = false;
        // malo vremena da ai puca
        new Thread(() -> {
            try {
                Thread.sleep(600);
            } catch (InterruptedException ignored) {
            }
            Platform.runLater(this::aiShoot);
        }).start();
    }


    private final List<int[]> aiTargetQueue = new ArrayList<>();
    private final Random rnd = new Random();

    private void aiShoot() {
        if (statusIgre.gameOver) return;
        // tamo de jos nije pucao
        List<int[]> available = new ArrayList<>();
        for (int red = 0; red < 10; red++)
            for (int kolona = 0; kolona < 10; kolona++)
                if (statusIgre.igracTabla[red][kolona] == 0 || statusIgre.igracTabla[red][kolona] == 1)
                    available.add(new int[]{red, kolona});
        if (available.isEmpty()) return;

        // pametnica moj mali
        int[] choice = aiNijeRetardiran(available);
        int r = choice[0], c = choice[1];
        if (statusIgre.igracTabla[r][c] == 1) {
            statusIgre.igracTabla[r][c] = 2;
            statusIgre.igracDugmad[r][c].setStyle(BOJA_POGOTKA + BTN_BASE);
            statusIgre.aiPogodci++;
        } else {
            statusIgre.igracTabla[r][c] = 3;
            statusIgre.igracDugmad[r][c].setStyle(BOJA_PROMASAJA + BTN_BASE);
        }

        if (statusIgre.aiPogodci >= statusIgre.playerShipsTotal) {
            statusIgre.gameOver = true;
            showResult("Izgubio si. Protivnik je potopio sve tvoje brodove.");
            return;
        }
        statusIgre.igracPotez = true;
    }

    private int[] aiNijeRetardiran(List<int[]> available) {
        // Look for cells adjacent to a hit that haven't been tried
        for (int[] cell : available) {
            int r = cell[0], c = cell[1];
            if (imaPored(r, c, statusIgre.igracTabla)) return cell;
        }
        return available.get(rnd.nextInt(available.size()));
    }

    private boolean imaPored(int r, int c, int[][] board) {
        int[][] okolnaPolja = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] d : okolnaPolja) {
            int noviRed = r + d[0], novaKolona = c + d[1];
            if (noviRed >= 0 && noviRed < 10 && novaKolona >= 0 && novaKolona < 10 && board[noviRed][novaKolona] == 2)
                return true;
        }
        return false;
    }


    private void checkSunkEnemy(int hitR, int hitC) {

        Set<String> pregledano = new HashSet<>();
        List<int[]> shipCells = new ArrayList<>();
        dubinaPrvoPregledZaHitove(hitR, hitC, statusIgre.aiTabla, pregledano, shipCells);

        // gleda je sve pogodjeno
        boolean sunk = shipCells.stream().allMatch(cell ->
                statusIgre.aiTabla[cell[0]][cell[1]] == 2);
        if (!sunk) return;

        // Mark ship cells dark and surrounding cells as miss
        for (int[] cell : shipCells) {
            statusIgre.aiDugmad[cell[0]][cell[1]].setStyle(BOJA_POTOPLJENO + BTN_BASE);
            for (int deltaRed = -1; deltaRed <= 1; deltaRed++) {
                for (int deltaKolona = -1; deltaKolona <= 1; deltaKolona++) {
                    int noviRed = cell[0] + deltaRed, novaCelija = cell[1] + deltaKolona;
                    if (noviRed >= 0 && noviRed < 10 && novaCelija >= 0 && novaCelija < 10 && statusIgre.aiTabla[noviRed][novaCelija] == 0) {
                        statusIgre.aiTabla[noviRed][novaCelija] = 3;
                        statusIgre.aiDugmad[noviRed][novaCelija].setStyle(BOJA_PROMASAJA + BTN_BASE);
                    }
                }
            }
        }
    }

    //all praise the indian overlords
    private void dubinaPrvoPregledZaHitove(int red, int kolona, int[][] board, Set<String> pregledano, List<int[]> out) {
        String key = red + "," + kolona;
        if (red < 0 || red >= 10 || kolona < 0 || kolona >= 10 || pregledano.contains(key)) return;
        if (board[red][kolona] != 1 && board[red][kolona] != 2) return;
        pregledano.add(key);
        out.add(new int[]{red, kolona});
        dubinaPrvoPregledZaHitove(red - 1, kolona, board, pregledano, out);
        dubinaPrvoPregledZaHitove(red + 1, kolona, board, pregledano, out);
        dubinaPrvoPregledZaHitove(red, kolona - 1, board, pregledano, out);
        dubinaPrvoPregledZaHitove(red, kolona + 1, board, pregledano, out);
    }

    public void RotirajBrodLevo(ActionEvent actionEvent) {

    }

    public void RotirajBrodDesno(ActionEvent actionEvent) {

    }

    public void pucaj(ActionEvent event) {
    }
}