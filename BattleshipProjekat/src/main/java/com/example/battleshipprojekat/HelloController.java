package com.example.battleshipprojekat;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    private static boolean jeliSeBirajuBrodovi = false;

    public void pucanje(){
        System.out.println("ubicu se");
    }
    @FXML
    private void otvoriOdabirBrodica() {
        if (jeliSeBirajuBrodovi){
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("odabir_brodova.fxml"));
            Scene scene = new Scene(loader.load(), 400, 400);
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
}