package com.example.battleshipprojekat;

import javafx.scene.media.MediaPlayer;


public class MuzikaManager {
    //kreiranje media playera i drugih komponenti
    private static MediaPlayer player;
    private static double lastVolume = 0.4;
    private static boolean muted = false;

    //ovo treba da pusti zvuk
    public static void pusti(MediaPlayer mp) {
        if (player != null) player.stop();
        player = mp;
        player.setVolume(muted ? 0 : lastVolume);
        player.play();
    }

    //podesavanje jacine tona i mute
    public static void setVolume(double vol) {
        lastVolume = vol;
        muted = (vol == 0);
        if (player != null) player.setVolume(vol);
    }

    //getter
    public static double getVolume() {
        return muted ? 0 : lastVolume;
    }

    //mute togggle
    public static void toggleMute() {
        muted = !muted;
        if (player != null) player.setVolume(muted ? 0 : lastVolume);
    }

    public static boolean isMuted() { return muted; }

    //stop!!!
    public static void zaustavi() {
        if (player != null) player.stop();
    }
}