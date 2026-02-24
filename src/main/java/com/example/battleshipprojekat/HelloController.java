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
    private void otvoriOdabirBrodica() {
        if (jeliSeBirajuBrodovi){
            return;
        }
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
            Stage newStage = new Stage();
            newStage.setTitle("Odabir Brodića");
            newStage.setScene(scene);
            newStage.setResizable(false);
            newStage.setX(1120);
            newStage.setY(220);
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



            jeliSeBirajuBrodovi = true;
            newStage.setOnCloseRequest(event -> {
                jeliSeBirajuBrodovi = false;
            });
        }
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