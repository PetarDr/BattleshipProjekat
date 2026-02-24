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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String fxmlPath = url.getPath();
        if (fxmlPath.contains("hello-view.fxml")) {
            otvoriOdabirBrodica();
        }
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

    public void stvoriBrod3(ActionEvent actionEvent) {
    }
    public void stvoriBrod4(ActionEvent actionEvent) {
    }

    public void stvoriBrod5(ActionEvent actionEvent) {
    }

    public void RotirajBrodLevo(ActionEvent actionEvent) {

    }

    public void RotirajBrodDesno(ActionEvent actionEvent) {

    }

    public void pucaj(ActionEvent event) {
    }
}