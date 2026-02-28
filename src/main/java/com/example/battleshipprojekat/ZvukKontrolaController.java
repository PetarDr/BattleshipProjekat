package com.example.battleshipprojekat;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;

import java.net.URL;
import java.util.ResourceBundle;

public class ZvukKontrolaController implements Initializable {
    @FXML
    private Button btnMute;
    @FXML private Slider sliderVolume;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sliderVolume.setValue(MuzikaManager.getVolume());
        osveziBtnTekst();

        sliderVolume.valueProperty().addListener((obs, old, val) -> {
            MuzikaManager.setVolume(val.doubleValue());
            osveziBtnTekst();
        });
    }

    @FXML
    private void toggleMute() {
        MuzikaManager.toggleMute();
        osveziBtnTekst();
    }

    private void osveziBtnTekst() {
        btnMute.setText(MuzikaManager.isMuted() ? "🔇" : "🔊");
    }

}