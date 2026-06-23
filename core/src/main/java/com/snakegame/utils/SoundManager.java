package com.snakegame.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

/*
 * Manages all sound effects in the game.
 *
 * Sound files must be in the assets/sounds/ folder.
 * If a file is not found, the game continues normally without that sound,
 * preventing the lack of audio from causing errors.
 *
 * Expected files: eat.wav, death.wav, navigate.wav, select.wav
 */
public class SoundManager {

    private Sound soundEat;
    private Sound soundDeath;
    private Sound soundNavigate;
    private Sound soundSelect;

    private boolean muted = false;

    public SoundManager() {
        soundEat      = tryLoad("sounds/eat.wav");
        soundDeath    = tryLoad("sounds/death.wav");
        soundNavigate = tryLoad("sounds/navigate.wav");
        soundSelect   = tryLoad("sounds/select.wav");
    }

    public void playEat()      { play(soundEat);      }
    public void playDeath()    { play(soundDeath);    }
    public void playNavigate() { play(soundNavigate); }
    public void playSelect()   { play(soundSelect);   }

    /* Plays a sound only if the game is not muted and the sound was loaded successfully. */
    private void play(Sound s) {
        if (!muted && s != null) s.play(0.8f);
    }

    public void toggleMute() { muted = !muted; }
    public boolean isMuted() { return muted; }

    public void dispose() {
        disposeIfNotNull(soundEat);
        disposeIfNotNull(soundDeath);
        disposeIfNotNull(soundNavigate);
        disposeIfNotNull(soundSelect);
    }

    /*
     * Attempts to load a sound file. If the file does not exist or fails,
     * returns null instead of throwing an error, keeping the game functional.
     */
    private static Sound tryLoad(String path) {
        try {
            if (Gdx.files.internal(path).exists()) {
                return Gdx.audio.newSound(Gdx.files.internal(path));
            }
        } catch (Exception e) {
            Gdx.app.log("SoundManager", "Could not load " + path);
        }
        return null;
    }

    private static void disposeIfNotNull(Sound s) {
        if (s != null) s.dispose();
    }
}