module com.example.battleshipprojekat {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;


    opens com.example.battleshipprojekat to javafx.fxml;
    exports com.example.battleshipprojekat;
}