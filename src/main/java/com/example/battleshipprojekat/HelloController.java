package com.example.battleshipprojekat;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
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
    private static final String BTN_BASE = "-fx-border-color: #455a64; -fx-border-radius: 4; -fx-background-radius: 4;" +
            "-fx-padding: 0 0 0 0; -fx-background-size: 100% 100%;";

    private static Image slika_voda = null;
    private static Image slika_voda2 = null;
    private static Image slika_eksplozija = null;
    private final Random rnd = new Random();

    // Kada AI pogodi brod, ovde cuvamo koordinate prvog pogotka
    private int aiPrviPogodakRed = -1;
    private int aiPrviPogodakKol = -1;

    // Trenutni smer kojim AI gadja (0=gore, 1=dole, 2=levo, 3=desno), -1 = random
    private int aiTrenutniSmer = -1;

    // Zadnje polje koje je AI gadao unutar broda (za nastavak u smeru)
    private int aiPoslednjiPogodakRed = -1;
    private int aiPoslednjiPogodakKol = -1;

    // Lista smerova koje jos nismo probali
    private final List<Integer> aiSmeroviZaProbati = new ArrayList<>();

    // Smerovi: 0=gore, 1=dole, 2=levo, 3=desno
    private static final int[][] DELTA = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String path = url != null ? url.getPath() : "";
        if (path.contains("hello-view.fxml")) {
            ucitajSlikeAkoNisuUcitane();
            buildGrids();
            otvoriOdabirBrodića();
            osveziBrojacePowerupa();
        }
    }

    private void ucitajSlikeAkoNisuUcitane() {
        if (slika_voda == null) {
            slika_voda = new Image(
                    getClass().getResourceAsStream("/com/example/battleshipprojekat/Images/voda.jpg")
            );
        }
        if (slika_voda2 == null) {
            slika_voda2 = new Image(
                    getClass().getResourceAsStream("/com/example/battleshipprojekat/Images/voda2.jpg")
            );
        }
        if (slika_eksplozija == null) {
            slika_eksplozija = new Image(
                    getClass().getResourceAsStream("/com/example/battleshipprojekat/Images/eksplozija.jpg")
            );
        }
    }

    private ImageView napraviVodaView() {
        ImageView iv = new ImageView(slika_voda);
        iv.setFitHeight(36); iv.setFitWidth(52);
        iv.setOpacity(0.5); iv.setPreserveRatio(false);
        return iv;
    }

    private ImageView napraviVoda2View() {
        ImageView iv = new ImageView(slika_voda2);
        iv.setFitHeight(36); iv.setFitWidth(52);
        iv.setOpacity(0.5); iv.setPreserveRatio(false);
        return iv;
    }

    private ImageView napraviEksplozijuView() {
        ImageView iv = new ImageView(slika_eksplozija);
        iv.setFitHeight(36); iv.setFitWidth(52);
        iv.setOpacity(0.5); iv.setPreserveRatio(false);
        return iv;
    }

    // Bomba: gadja 3x3 oblast oko izabranog polja
    @FXML
    private void aktivirajBombu() {
        if (brojBombi <= 0 || statusIgre.vremeStavljanja || statusIgre.gameOver || !statusIgre.igracPotez) return;
        aktivniPowerup = aktivniPowerup != null && aktivniPowerup.equals("BOMBA") ? null : "BOMBA";
        osveziBrojacePowerupa();
    }

    // Radar: otkriva 2x2 oblast na neprijateljevoj tabli bez gadjanja
    @FXML
    private void aktivirajRadar() {
        if (brojRadara <= 0 || statusIgre.vremeStavljanja || statusIgre.gameOver || !statusIgre.igracPotez) return;
        aktivniPowerup = aktivniPowerup != null && aktivniPowerup.equals("RADAR") ? null : "RADAR";
        osveziBrojacePowerupa();
    }

    // Artiljerija: gadja celu kolonu
    @FXML
    private void aktivirajArtiljeriju() {
        if (brojArtiljerije <= 0 || statusIgre.vremeStavljanja || statusIgre.gameOver || !statusIgre.igracPotez) return;
        aktivniPowerup = aktivniPowerup != null && aktivniPowerup.equals("ARTILJERIJA") ? null : "ARTILJERIJA";
        osveziBrojacePowerupa();
    }

    // Mina: postavlja skrivenu minu na nasoj tabli koja ce osatetiti AI kada pogodi to polje
    @FXML
    private void aktivirajMinu() {
        if (brojMina <= 0 || statusIgre.vremeStavljanja || statusIgre.gameOver || !statusIgre.igracPotez) return;
        aktivniPowerup = aktivniPowerup != null && aktivniPowerup.equals("MINA") ? null : "MINA";
        osveziBrojacePowerupa();
    }

    private void osveziBrojacePowerupa() {
        if (btnBomba != null) {
            btnBomba.setText("Bomba (" + brojBombi + ")");
            btnBomba.setStyle(aktivniPowerup != null && aktivniPowerup.equals("BOMBA")
                    ? "-fx-background-color: #ff9800; -fx-font-weight: bold;" : "");
        }
        if (btnRadar != null) {
            btnRadar.setText("Radar (" + brojRadara + ")");
            btnRadar.setStyle(aktivniPowerup != null && aktivniPowerup.equals("RADAR")
                    ? "-fx-background-color: #ff9800; -fx-font-weight: bold;" : "");
        }
        if (btnArtiljerija != null) {
            btnArtiljerija.setText("Artiljerija (" + brojArtiljerije + ")");
            btnArtiljerija.setStyle(aktivniPowerup != null && aktivniPowerup.equals("ARTILJERIJA")
                    ? "-fx-background-color: #ff9800; -fx-font-weight: bold;" : "");
        }
        if (btnMina != null) {
            btnMina.setText("Mina (" + brojMina + ")");
            btnMina.setStyle(aktivniPowerup != null && aktivniPowerup.equals("MINA")
                    ? "-fx-background-color: #ff9800; -fx-font-weight: bold;" : "");
        }
        if (lblPowerupStatus != null) {
            lblPowerupStatus.setText(aktivniPowerup != null
                    ? "Aktivan powerup: " + aktivniPowerup + " — klikni na neprijateljevu mrezu!"
                    : "");
        }
    }

    private boolean izvrsiPowerup(int row, int col) {
        if (aktivniPowerup == null) return false;
        switch (aktivniPowerup) {
            case "BOMBA" -> {
                brojBombi--;
                for (int deltaRed = -1; deltaRed <= 1; deltaRed++) {
                    for (int deltaKolona = -1; deltaKolona <= 1; deltaKolona++) {
                        int red = row + deltaRed, kolona = col + deltaKolona;
                        if (red < 0 || red >= 10 || kolona < 0 || kolona >= 10) continue;
                        int st = statusIgre.aiTabla[red][kolona];
                        if (st == 2 || st == 3) continue;
                        if (st == 1) {
                            statusIgre.aiTabla[red][kolona] = 2;
                            statusIgre.aiDugmad[red][kolona].setStyle(BOJA_POGOTKA + BTN_BASE);
                            statusIgre.igracPogodci++;
                            checkSunkEnemy(red, kolona);
                        } else {
                            statusIgre.aiTabla[red][kolona] = 3;
                            statusIgre.aiDugmad[red][kolona].setStyle(BOJA_PROMASAJA + BTN_BASE);
                        }
                    }
                }
            }
            case "RADAR" -> {
                brojRadara--;
                for (int deltaRed = 0; deltaRed <= 1; deltaRed++) {
                    for (int deltaKolona = 0; deltaKolona <= 1; deltaKolona++) {
                        int red = row + deltaRed, kolona = col + deltaKolona;
                        if (red < 0 || red >= 10 || kolona < 0 || kolona >= 10) continue;
                        if (statusIgre.aiTabla[red][kolona] == 1) {
                            statusIgre.aiDugmad[red][kolona].setStyle("-fx-background-color: #66bb6a;" + BTN_BASE);
                        }
                    }
                }
                new Thread(() -> {
                    try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
                    Platform.runLater(() -> {
                        clearPreviewEnemy();
                        osveziBrojacePowerupa();
                    });
                }).start();
            }
            case "ARTILJERIJA" -> {
                brojArtiljerije--;
                for (int red = 0; red < 10; red++) {
                    int st = statusIgre.aiTabla[red][col];
                    if (st == 2 || st == 3) continue;
                    if (st == 1) {
                        statusIgre.aiTabla[red][col] = 2;
                        statusIgre.aiDugmad[red][col].setStyle(BOJA_POGOTKA + BTN_BASE);
                        statusIgre.igracPogodci++;
                        checkSunkEnemy(red, col);
                    } else {
                        statusIgre.aiTabla[red][col] = 3;
                        statusIgre.aiDugmad[red][col].setStyle(BOJA_PROMASAJA + BTN_BASE);
                    }
                }
            }
            case "MINA" -> {
                // Ovde samo deaktivujemo - mina se postavlja u handlePlayerClick
                brojMina--;
                // Oznacimo polje kao minu (vrednost 4 = mina)
                if (statusIgre.igracTabla[row][col] == 0) {
                    statusIgre.igracTabla[row][col] = 4;
                    statusIgre.igracDugmad[row][col].setStyle("-fx-background-color: #ff5722;" + BTN_BASE);
                }
            }
        }

        aktivniPowerup = null;
        osveziBrojacePowerupa();
        return true;
    }


    //e ovde vec postaje zajebano fala kurcu za indijce na yt
    private void buildGrids() {

        for (int red = 0; red < 10; red++) {
            for (int kolona = 0; kolona < 10; kolona++) {
                Button aiPolja = makeBtn("");
                aiPolja.setGraphic(napraviVodaView());
                final int row = red, col = kolona;
                aiPolja.setOnAction(e -> handleEnemyClick(row, col, aiPolja));
                aiPolja.setOnMouseEntered(e -> showPreviewEnemy(row, col));
                aiPolja.setOnMouseExited(e -> clearPreviewEnemy());
                aiGrid.add(aiPolja, kolona, red);
                statusIgre.aiDugmad[red][kolona] = aiPolja;

                Button igracPolja = makeBtn(BOJA_NASE_VODE);
                igracPolja.setGraphic(napraviVoda2View());
                igracPolja.setOnAction(e -> handlePlayerClick(row, col));
                igracPolja.setOnMouseEntered(e -> showPreviewPlayer(row, col));
                igracPolja.setOnMouseExited(e -> clearPreviewPlayer());
                igracGrid.add(igracPolja, kolona, red);
                statusIgre.igracDugmad[red][kolona] = igracPolja;
            }
        }
    }

    private Button makeBtn(String color) {
        Button b = new Button();
        b.setPrefSize(54, 46);
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
            statusIgre.igracDugmad[cell[0]][cell[1]].setGraphic(null);
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
                if (stageZaOdabirBrodića != null) {
                    stageZaOdabirBrodića.close();
                    stageZaOdabirBrodića = null;
                    daLiSeBirajuBrodići = false;
                }
                a.showAndWait();
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
            showResult("Čestitke! Pobedio si!");
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


    private void aiShoot() {
        if (statusIgre.gameOver) return;

        int[] choice = odaberiBrojZaAi();
        int r = choice[0], c = choice[1];

        if (statusIgre.igracTabla[r][c] == 1) {
            // POGODAK
            statusIgre.igracTabla[r][c] = 2;
            statusIgre.igracDugmad[r][c].setStyle(BOJA_POGOTKA + BTN_BASE);
            statusIgre.aiPogodci++;

            if (aiPrviPogodakRed == -1) {
                // Ovo je prvi pogodak na novom brodu - udjemo u target mode
                aiPrviPogodakRed = r;
                aiPrviPogodakKol = c;
                aiPoslednjiPogodakRed = r;
                aiPoslednjiPogodakKol = c;
                aiSmeroviZaProbati.clear();
                List<Integer> smjerovi = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
                Collections.shuffle(smjerovi, rnd);
                aiSmeroviZaProbati.addAll(smjerovi);
                aiTrenutniSmer = aiSmeroviZaProbati.removeFirst();
            } else {
                // Nastavljamo u istom smeru
                aiPoslednjiPogodakRed = r;
                aiPoslednjiPogodakKol = c;
            }

            // Provjeri je li brod potopljen
            if (checkSunkPlayer(r, c)) {
                resetAiTargeting();
            }

        } else {
            // PROMASAJ
            statusIgre.igracTabla[r][c] = 3;
            statusIgre.igracDugmad[r][c].setStyle(BOJA_PROMASAJA + BTN_BASE);

            if (aiPrviPogodakRed != -1) {
                if (!aiSmeroviZaProbati.isEmpty()) {
                    aiTrenutniSmer = aiSmeroviZaProbati.removeFirst();
                } else {
                    aiTrenutniSmer = -1;
                }
                aiPoslednjiPogodakRed = aiPrviPogodakRed;
                aiPoslednjiPogodakKol = aiPrviPogodakKol;
            }
        }

        if (statusIgre.aiPogodci >= statusIgre.playerShipsTotal) {
            statusIgre.gameOver = true;
            showResult("Izgubio si. Protivnik je potopio sve tvoje brodove.");
            return;
        }
        statusIgre.igracPotez = true;
    }

    /**
     * Bira polje za AI da gadja.
     * Ako je u target mode (zna gde je brod), gadja u trenutnom smeru od zadnjeg pogotka.
     * Ako nema validnog polja u tom smeru, proba sledeci smer.
     * Ako nema vise smerova, pada nazad na random.
     */
    private int[] odaberiBrojZaAi() {
        if (aiPrviPogodakRed != -1 && aiTrenutniSmer != -1) {
            int[] sledece = sledecePoljeuSmeru();
            if (sledece != null) {
                return sledece;
            }

            while (!aiSmeroviZaProbati.isEmpty()) {
                aiTrenutniSmer = aiSmeroviZaProbati.removeFirst();
                aiPoslednjiPogodakRed = aiPrviPogodakRed;
                aiPoslednjiPogodakKol = aiPrviPogodakKol;
                sledece = sledecePoljeuSmeru();
                if (sledece != null) {
                    return sledece;
                }
            }
            resetAiTargeting();
        }
        return randomPolje();
    }

    /**
     * Vraca sljedece polje u trenutnom smeru od zadnjeg pogotka,
     * ili null ako je to polje van table ili vec gadjano.
     */
    private int[] sledecePoljeuSmeru() {
        int noviRed = aiPoslednjiPogodakRed + DELTA[aiTrenutniSmer][0];
        int novaKolona = aiPoslednjiPogodakKol + DELTA[aiTrenutniSmer][1];
        if (noviRed < 0 || noviRed >= 10 || novaKolona < 0 || novaKolona >= 10) return null;
        int stanje = statusIgre.igracTabla[noviRed][novaKolona];
        if (stanje == 2 || stanje == 3) return null; // vec gadjano
        return new int[]{noviRed, novaKolona};
    }

    private int[] randomPolje() {
        List<int[]> available = new ArrayList<>();
        for (int red = 0; red < 10; red++)
            for (int kolona = 0; kolona < 10; kolona++)
                if (statusIgre.igracTabla[red][kolona] == 0 || statusIgre.igracTabla[red][kolona] == 1)
                    available.add(new int[]{red, kolona});
        return available.get(rnd.nextInt(available.size()));
    }

    private void resetAiTargeting() {
        aiPrviPogodakRed = -1;
        aiPrviPogodakKol = -1;
        aiPoslednjiPogodakRed = -1;
        aiPoslednjiPogodakKol = -1;
        aiTrenutniSmer = -1;
        aiSmeroviZaProbati.clear();
    }

    private boolean checkSunkPlayer(int hitR, int hitC) {
        Set<String> pregledano = new HashSet<>();
        List<int[]> shipCells = new ArrayList<>();
        dubinaPrvoPregledZaHitove(hitR, hitC, statusIgre.igracTabla, pregledano, shipCells);

        boolean sunk = shipCells.stream().allMatch(cell ->
                statusIgre.igracTabla[cell[0]][cell[1]] == 2);
        if (!sunk) return false;

        for (int[] cell : shipCells) {
            ImageView potopljen = new ImageView(new Image(
                    getClass().getResourceAsStream("/com/example/battleshipprojekat/Images/eksplozija.jpg")
            ));
            potopljen.setFitHeight(36);
            potopljen.setFitWidth(52);
            potopljen.setOpacity(0.5);
            potopljen.setPreserveRatio(false);

            statusIgre.igracDugmad[cell[0]][cell[1]].setStyle(BTN_BASE);
            statusIgre.igracDugmad[cell[0]][cell[1]].setGraphic(potopljen);

            for (int deltaRed = -1; deltaRed <= 1; deltaRed++) {
                for (int deltaKolona = -1; deltaKolona <= 1; deltaKolona++) {
                    int noviRed = cell[0] + deltaRed, novaCelija = cell[1] + deltaKolona;
                    if (noviRed >= 0 && noviRed < 10 && novaCelija >= 0 && novaCelija < 10 && statusIgre.igracTabla[noviRed][novaCelija] == 0) {
                        statusIgre.igracTabla[noviRed][novaCelija] = 3;
                        statusIgre.igracDugmad[noviRed][novaCelija].setStyle(BOJA_PROMASAJA + BTN_BASE);
                    }
                }
            }
        }
        return true;
    }

    private void checkSunkEnemy(int hitR, int hitC) {

        Set<String> pregledano = new HashSet<>();
        List<int[]> shipCells = new ArrayList<>();
        dubinaPrvoPregledZaHitove(hitR, hitC, statusIgre.aiTabla, pregledano, shipCells);

        boolean sunk = shipCells.stream().allMatch(cell ->
                statusIgre.aiTabla[cell[0]][cell[1]] == 2);
        if (!sunk) return;


        for (int[] cell : shipCells) {
            ImageView potopljen = new ImageView(new Image(
                    getClass().getResourceAsStream("/com/example/battleshipprojekat/Images/eksplozija.jpg")
            ));
            potopljen.setFitHeight(36);
            potopljen.setFitWidth(52);
            potopljen.setOpacity(0.5);
            potopljen.setPreserveRatio(false);

            statusIgre.aiDugmad[cell[0]][cell[1]].setStyle(BTN_BASE);
            statusIgre.aiDugmad[cell[0]][cell[1]].setGraphic(potopljen);
            for (int deltaRed = -1; deltaRed <= 1; deltaRed++) {
                for (int deltaKolona = -1; deltaKolona <= 1; deltaKolona++) {
                    int noviRed = cell[0] + deltaRed, novaCelija = cell[1] + deltaKolona;
                    if (noviRed >= 0 && noviRed < 10 && novaCelija >= 0 && novaCelija < 10 && statusIgre.aiTabla[noviRed][novaCelija] == 0) {
                        statusIgre.aiTabla[noviRed][novaCelija] = 3;
                        statusIgre.aiDugmad[noviRed][novaCelija].setStyle(BOJA_PROMASAJA + BTN_BASE);
                    }
                }
            }
        }
    }

    //all praise the indian overlords
    private void dubinaPrvoPregledZaHitove(int red, int kolona, int[][] board, Set<String> pregledano, List<int[]> out) {
        String key = red + "," + kolona;
        if (red < 0 || red >= 10 || kolona < 0 || kolona >= 10 || pregledano.contains(key)) return;
        if (board[red][kolona] != 1 && board[red][kolona] != 2) return;
        pregledano.add(key);
        out.add(new int[]{red, kolona});
        dubinaPrvoPregledZaHitove(red - 1, kolona, board, pregledano, out);
        dubinaPrvoPregledZaHitove(red + 1, kolona, board, pregledano, out);
        dubinaPrvoPregledZaHitove(red, kolona - 1, board, pregledano, out);
        dubinaPrvoPregledZaHitove(red, kolona + 1, board, pregledano, out);
    }

    //ona sranja za postavljanje dal je dobro ili ne
    private void showPreviewPlayer(int row, int col) {
        if (!statusIgre.vremeStavljanja || statusIgre.selectedShipSize == 0) return;
        boolean valid = statusIgre.canPlace(row, col, statusIgre.selectedShipSize, statusIgre.pravac, statusIgre.igracTabla);
        for (int[] cell : statusIgre.shipCells(row, col, statusIgre.selectedShipSize, statusIgre.pravac)) {
            int red = cell[0], kolona = cell[1];
            if (red < 0 || red >= 10 || kolona < 0 || kolona >= 10) continue;
            if (statusIgre.igracTabla[red][kolona] == 0)
                statusIgre.igracDugmad[red][kolona].setStyle((valid ? BOJA_BRODA_PLACEHOLDER : BOJA_LOSEG_STAVLJANJA) + BTN_BASE);
        }
    }

    private void clearPreviewPlayer() {
        for (int red = 0; red < 10; red++)
            for (int kolona = 0; kolona < 10; kolona++)
                if (statusIgre.igracTabla[red][kolona] == 0)
                    statusIgre.igracDugmad[red][kolona].setStyle(BOJA_NASE_VODE + BTN_BASE);
                else if (statusIgre.igracTabla[red][kolona] == 1) {
                    statusIgre.igracDugmad[red][kolona].setStyle(BOJA_BRODA + BTN_BASE);
                    statusIgre.igracDugmad[red][kolona].setGraphic(null);
                }
    }

    private void showPreviewEnemy(int row, int col) {
        if (statusIgre.vremeStavljanja || statusIgre.gameOver || !statusIgre.igracPotez) return;
        int state = statusIgre.aiTabla[row][col];
        if (state != 0 && state != 1) return;
        statusIgre.aiDugmad[row][col].setStyle("-fx-background-color: #ffeb3b;" + BTN_BASE);
    }

    private void clearPreviewEnemy() {
        for (int red = 0; red < 10; red++)
            for (int kolona = 0; kolona < 10; kolona++) {
                int status = statusIgre.aiTabla[red][kolona];
                if (status == 0 || status == 1)
                    statusIgre.aiDugmad[red][kolona].setStyle(BOJA_NEPRIJATLJSKE_VODE + BTN_BASE);
            }
    }

    private void showResult(String msg) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Kraj igre");
            a.setHeaderText(null);
            a.setContentText(msg);
            a.showAndWait();


            statusIgre.reset();
            resetAiTargeting();

            for (int red = 0; red < 10; red++)
                for (int kolona = 0; kolona < 10; kolona++) {

                    ImageView neprijateljskaVoda = new ImageView(new Image(
                            getClass().getResourceAsStream("/com/example/battleshipprojekat/Images/voda.jpg")
                    ));
                    neprijateljskaVoda.setFitHeight(46);
                    neprijateljskaVoda.setFitWidth(52);
                    neprijateljskaVoda.setOpacity(0.5);
                    neprijateljskaVoda.setPreserveRatio(false);

                    ImageView nasaVoda = new ImageView(new Image(
                            getClass().getResourceAsStream("/com/example/battleshipprojekat/Images/voda2.jpg")
                    ));
                    nasaVoda.setFitHeight(46);
                    nasaVoda.setFitWidth(52);
                    nasaVoda.setOpacity(0.5);
                    nasaVoda.setPreserveRatio(false);

                    statusIgre.igracDugmad[red][kolona].setStyle(BOJA_NASE_VODE + BTN_BASE);
                    statusIgre.aiDugmad[red][kolona].setStyle(BOJA_NEPRIJATLJSKE_VODE + BTN_BASE);
                    statusIgre.aiDugmad[red][kolona].setGraphic(napraviVodaView());
                    statusIgre.igracDugmad[red][kolona].setGraphic(napraviVoda2View());
                }
            otvoriOdabirBrodića();
        });
    }
}