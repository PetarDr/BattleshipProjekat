package com.example.battleshipprojekat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    private static boolean jeliSeBirajuBrodovi = false;

    public Button btn00;
    public Button btn01;
    public Button btn02;
    public Button btn03;
    public Button btn04;
    public Button btn05;
    public Button btn06;
    public Button btn07;
    public Button btn08;
    public Button btn09;
    public Button btn010;

    public Button btn10;
    public Button btn11;
    public Button btn12;
    public Button btn13;
    public Button btn14;
    public Button btn15;
    public Button btn16;
    public Button btn17;
    public Button btn18;
    public Button btn19;
    public Button btn110;

    public Button btn20;
    public Button btn21;
    public Button btn22;
    public Button btn23;
    public Button btn24;
    public Button btn25;
    public Button btn26;
    public Button btn27;
    public Button btn28;
    public Button btn29;
    public Button btn210;

    public Button btn30;
    public Button btn31;
    public Button btn32;
    public Button btn33;
    public Button btn34;
    public Button btn35;
    public Button btn36;
    public Button btn37;
    public Button btn38;
    public Button btn39;
    public Button btn310;

    public Button btn40;
    public Button btn41;
    public Button btn42;
    public Button btn43;
    public Button btn44;
    public Button btn45;
    public Button btn46;
    public Button btn47;
    public Button btn48;
    public Button btn49;
    public Button btn410;

    public Button btn50;
    public Button btn51;
    public Button btn52;
    public Button btn53;
    public Button btn54;
    public Button btn55;
    public Button btn56;
    public Button btn57;
    public Button btn58;
    public Button btn59;
    public Button btn510;

    public Button btn60;
    public Button btn61;
    public Button btn62;
    public Button btn63;
    public Button btn64;
    public Button btn65;
    public Button btn66;
    public Button btn67;
    public Button btn68;
    public Button btn69;
    public Button btn610;

    public Button btn70;
    public Button btn71;
    public Button btn72;
    public Button btn73;
    public Button btn74;
    public Button btn75;
    public Button btn76;
    public Button btn77;
    public Button btn78;
    public Button btn79;
    public Button btn710;

    public Button btn80;
    public Button btn81;
    public Button btn82;
    public Button btn83;
    public Button btn84;
    public Button btn85;
    public Button btn86;
    public Button btn87;
    public Button btn88;
    public Button btn89;
    public Button btn810;

    public Button btn90;
    public Button btn91;
    public Button btn92;
    public Button btn93;
    public Button btn94;
    public Button btn95;
    public Button btn96;
    public Button btn97;
    public Button btn98;
    public Button btn99;
    public Button btn910;

    public Button btn100;
    public Button btn101;
    public Button btn102;
    public Button btn103;
    public Button btn104;
    public Button btn105;
    public Button btn106;
    public Button btn107;
    public Button btn108;
    public Button btn109;
    public Button btn1010;

    @FXML
    private void otvoriOdabirBrodica() {
        if (jeliSeBirajuBrodovi){
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("odabir_brodova.fxml"));
            Scene scene = new Scene(loader.load(), 600, 400);
            Stage newStage = new Stage();
            newStage.setTitle("Odabir Brodića");
            newStage.setScene(scene);
            newStage.setResizable(false);
            newStage.setX(1120);
            newStage.setY(220);

            jeliSeBirajuBrodovi = true;
            newStage.setOnCloseRequest(event -> {
                jeliSeBirajuBrodovi = false;
            });
            newStage.show();
            newStage.setAlwaysOnTop(true);
            newStage.toFront();
            newStage.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String fxmlPath = url.getPath();
        if (fxmlPath.contains("hello-view.fxml")) {
            otvoriOdabirBrodica();
        }
    }

    public void stvoriBrod2(ActionEvent actionEvent) {
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