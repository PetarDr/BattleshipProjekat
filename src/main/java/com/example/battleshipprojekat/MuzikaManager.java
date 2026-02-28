package com.example.battleshipprojekat;

import javafx.scene.media.MediaPlayer;

public class MuzikaManager {
    private static MediaPlayer player;
    private static double lastVolume = 0.4;
    private static boolean muted = false;

    public static void pusti(MediaPlayer mp) {
        if (player != null) player.stop();
        player = mp;
        player.setVolume(muted ? 0 : lastVolume);
        player.play();
    }

    public static void setVolume(double vol) {
        lastVolume = vol;
        muted = (vol == 0);
        if (player != null) player.setVolume(vol);
    }

    public static double getVolume() {
        return muted ? 0 : lastVolume;
    }

    public static void toggleMute() {
        muted = !muted;
        if (player != null) player.setVolume(muted ? 0 : lastVolume);
    }

    public static boolean isMuted() { return muted; }

    public static void zaustavi() {
        if (player != null) player.stop();
    }
}