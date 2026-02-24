package com.example.battleshipprojekat;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class OdabirController implements Initializable {

    private static OdabirController current;

    public static void notifyUpdate() {
        if (current != null) Platform.runLater(current::osveziUI);
    }

    @FXML
    private Button btn2;
    @FXML
    private Button btn3;
    @FXML
    private Button btn4;
    @FXML
    private Button btn5;
    @FXML
    private Button btnRotateLeft;
    @FXML
    private Button btnRotateRight;
    @FXML
    private Label lblZaPravac;
    @FXML
    private Label lblZaStatus;

    private final GameState stanjeIgre = GameState.INSTANCE;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        current = this;
        osveziUI();
    }

    @FXML
    private void selectShip2(ActionEvent e) {
        selectSize(2);
    }

    @FXML
    private void selectShip3(ActionEvent e) {
        selectSize(3);
    }

    @FXML
    private void selectShip4(ActionEvent e) {
        selectSize(4);
    }

    @FXML
    private void selectShip5(ActionEvent e) {
        selectSize(5);
    }

    private void selectSize(int size) {
        if (stanjeIgre.brodoviKojiTrebajuBivatiPostavljeni.getOrDefault(size, 0) <= 0) return;
        stanjeIgre.selectedShipSize = size;
        osveziUI();
    }

    @FXML
    private void rotateLeft(ActionEvent e) {
        Pravac[] pravci = Pravac.values();
        int id = stanjeIgre.pravac.ordinal();
        stanjeIgre.pravac = pravci[(id + 3) % 4];
        osveziUI();
    }

    @FXML
    private void rotateRight(ActionEvent e) {
        Pravac[] pravci = Pravac.values();
        int id = stanjeIgre.pravac.ordinal();
        stanjeIgre.pravac = pravci[(id + 1) % 4];
        osveziUI();
    }

    private void osveziUI() {
        if (lblZaPravac == null) return;

        // ti ne znas koliko mi je trebalo da nadjem mrtve strelice
        String pravacString = switch (stanjeIgre.pravac) {
            case SEVER -> "↑ Gore";
            case JUG -> "↓ Dole";
            case ISTOK -> "→ Desno";
            case ZAPAD -> "← Levo";
        };
        lblZaPravac.setText("Pravac: " + pravacString);

        if (stanjeIgre.allShipsPlaced()) {
            lblZaStatus.setText("Svi brodovi postavljeni! Igra počinje.");
            setAllDisabled(true);
            return;
        }

        StringBuilder sb = new StringBuilder("Preostalo: ");
        stanjeIgre.brodoviKojiTrebajuBivatiPostavljeni.forEach((size, count) -> {
            if (count > 0) sb.append("Vel.").append(size).append("×").append(count).append("  ");
        }); //append append append append append append append append append append append append append append
        if (stanjeIgre.selectedShipSize > 0)
            sb.append("\n▶ Izabran brod veličine ").append(stanjeIgre.selectedShipSize).append(" — klikni na mrezu!");
        else //append append append append append append append append append append append append append append
            sb.append("\n▶ Izaberi brod iznad");
        lblZaStatus.setText(sb.toString());

        // aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
        btn2.setDisable(stanjeIgre.brodoviKojiTrebajuBivatiPostavljeni.getOrDefault(2, 0) <= 0);
        btn3.setDisable(stanjeIgre.brodoviKojiTrebajuBivatiPostavljeni.getOrDefault(3, 0) <= 0);
        btn4.setDisable(stanjeIgre.brodoviKojiTrebajuBivatiPostavljeni.getOrDefault(4, 0) <= 0);
        btn5.setDisable(stanjeIgre.brodoviKojiTrebajuBivatiPostavljeni.getOrDefault(5, 0) <= 0);

        // zapravo je ovaj deo bio lak
        String selectedStyle = "-fx-font-weight: bold; -fx-border-color: orange; -fx-border-width: 2;";
        btn2.setStyle(stanjeIgre.selectedShipSize == 2 ? selectedStyle : "");
        btn3.setStyle(stanjeIgre.selectedShipSize == 3 ? selectedStyle : "");
        btn4.setStyle(stanjeIgre.selectedShipSize == 4 ? selectedStyle : "");
        btn5.setStyle(stanjeIgre.selectedShipSize == 5 ? selectedStyle : "");
    }

    private void setAllDisabled(boolean d) {
        btn2.setDisable(d);
        btn3.setDisable(d);
        btn4.setDisable(d);
        btn5.setDisable(d);
        btnRotateLeft.setDisable(d);
        btnRotateRight.setDisable(d);
    }
}
