package com.snakegame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.snakegame.screens.MenuScreen;
import com.snakegame.utils.HighScoreManager;
import com.snakegame.utils.SoundManager;

/*
 * Main game class. Extends LibGDX's Game,
 * which manages the application lifecycle and switching between screens.
 * Shared resources used by all screens are initialized here:
 * fonts, rendering batch, sounds, and scores.
 */
public class SnakeGame extends Game {

    // Shared resources accessible by all screens
    public SpriteBatch      batch;
    public BitmapFont       fontLarge;
    public BitmapFont       fontMedium;
    public BitmapFont       fontSmall;
    public HighScoreManager highScoreManager;
    public SoundManager     soundManager;

    // Grid dimensions in number of cells
    public static final int GRID_COLS  = 30;
    public static final int GRID_ROWS  = 22;
    public static final int CELL_SIZE  = 28; // size in pixels of each cell

    public static final int HUD_HEIGHT    = 60; // height of the top information bar
    public static final int SCREEN_WIDTH  = GRID_COLS * CELL_SIZE;
    public static final int SCREEN_HEIGHT = GRID_ROWS * CELL_SIZE + HUD_HEIGHT;

    @Override
    public void create() {
        batch            = new SpriteBatch();
        highScoreManager = new HighScoreManager();
        soundManager     = new SoundManager();

        // Standard LibGDX fonts with different scales for title, menus, and smaller texts
        fontLarge  = new BitmapFont();
        fontMedium = new BitmapFont();
        fontSmall  = new BitmapFont();
        fontLarge.getData().setScale(3.0f);
        fontMedium.getData().setScale(2.0f);
        fontSmall.getData().setScale(1.5f);

        // The game always starts at the main menu
        setScreen(new MenuScreen(this));
    }

    @Override
    public void render() {
        super.render(); // delegates rendering to the active screen
    }

    @Override
    public void dispose() {
        batch.dispose();
        fontLarge.dispose();
        fontMedium.dispose();
        fontSmall.dispose();
        soundManager.dispose();
    }
}