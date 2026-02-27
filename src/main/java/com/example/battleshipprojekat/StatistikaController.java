package com.example.battleshipprojekat;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class StatistikaController implements Initializable {
    public Label label1;
    public Label label2;
    public Label label3;
    public Label label4;
    public Label label5;
    public Button btnClose;

    public void start(ActionEvent event) {
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

    public void napusti(ActionEvent event) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
