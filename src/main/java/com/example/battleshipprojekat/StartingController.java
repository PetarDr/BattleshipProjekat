package com.example.battleshipprojekat;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class StartingController {
    public Button closeBtn;

    //ovo zatvara stari i otvara novi stage radi mislim
    public void start(ActionEvent event) throws IOException {

        try{
            Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
            Scene scene = new Scene(root);

            Stage noviStage = new Stage();
            noviStage.setTitle("Potapanje Brodova");
            noviStage.setScene(scene);
            noviStage.setResizable(false);
            noviStage.show();

            Stage trenutniStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            trenutniStage.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //samo close button
    public void napusti(ActionEvent event) {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }
}
