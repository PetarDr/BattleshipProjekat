package com.example.battleshipprojekat;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class StartingController implements Initializable {
    public Button closeBtn;
    private MediaPlayer mediaPlayer;

    //ovo zatvara stari i otvara novi stage radi mislim
    public void start(ActionEvent event) throws IOException {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("glavni-view.fxml"));
            Scene scene = new Scene(root);

            Stage noviStage = new Stage();
            noviStage.setTitle("Brodići");
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Media media = new Media(getClass().getResource("/com/example/battleshipprojekat/Music/rideforthvictoriously.wav").toExternalForm());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setVolume(0.4);
            MuzikaManager.pusti(mediaPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
