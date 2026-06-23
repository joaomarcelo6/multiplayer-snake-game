package com.snakegame.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.snakegame.SnakeGame;

/*
 * Entry point of the desktop application.
 * Configures the window and starts the game.
 */
public class DesktopLauncher {

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Multiplayer Snake Game");
        config.setWindowedMode(SnakeGame.SCREEN_WIDTH, SnakeGame.SCREEN_HEIGHT);
        config.setResizable(false);
        config.setForegroundFPS(60);
        config.useVsync(true);

        new Lwjgl3Application(new SnakeGame(), config);
    }
}