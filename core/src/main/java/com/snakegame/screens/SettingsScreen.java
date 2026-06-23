package com.snakegame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.snakegame.SnakeGame;

/*
 * Configuration screen displayed before each match.
 *
 * Additional feature:
 * Allows the player to choose the grid size and initial speed
 * before starting. Choices are packed into a GameConfig and
 * delivered to the GameScreen.
 */
public class SettingsScreen implements Screen {

    // Inner class: match configuration 

    /*
     * Stores the chosen configurations and transports them to the GameScreen.
     * It is a static inner class because it only makes sense to exist alongside SettingsScreen.
     */
    public static class GameConfig {
        public final int   gridCols;
        public final int   gridRows;
        public final float initialTick; // interval in seconds between each snake step

        public GameConfig(int gridCols, int gridRows, float initialTick) {
            this.gridCols    = gridCols;
            this.gridRows    = gridRows;
            this.initialTick = initialTick;
        }

        /* Default configuration used when the game restarts without passing through the settings screen. */
        public static GameConfig defaults() {
            return new GameConfig(30, 22, 0.15f);
        }
    }

    // Available options 

    private static final String[] GRID_LABELS  = { "SMALL (20x15)", "MEDIUM (30x22)", "LARGE (40x30)" };
    private static final int[]    GRID_COLS    = { 20, 30, 40 };
    private static final int[]    GRID_ROWS    = { 15, 22, 30 };

    private static final String[] SPEED_LABELS = { "SLOW", "NORMAL", "FAST" };
    private static final float[]  SPEED_TICKS  = { 0.22f, 0.15f, 0.09f };

    private final SnakeGame game;
    private final ShapeRenderer sr = new ShapeRenderer();

    private int gridChoice  = 1; // starts with the medium grid selected
    private int speedChoice = 1; // starts with normal speed selected
    private int focusRow    = 0; // which row of options is in focus: 0=grid, 1=speed, 2=start

    private float animTime = 0f;

    public SettingsScreen(SnakeGame game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        animTime += delta;

        Gdx.gl.glClearColor(0.06f, 0.08f, 0.06f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        int W = Gdx.graphics.getWidth();
        int H = Gdx.graphics.getHeight();

        drawPanel(W, H);

        game.batch.begin();
        game.fontMedium.setColor(Color.GREEN);
        drawCentered("SETTINGS", W, H / 2f + 190, 2f);

        // Grid size section — highlighted in yellow if in focus
        labelSection("GRID SIZE", W, H / 2f + 110, focusRow == 0);
        drawOptions(GRID_LABELS, gridChoice, W, H / 2f + 74);

        // Initial speed section
        labelSection("INITIAL SPEED", W, H / 2f - 10, focusRow == 1);
        drawOptions(SPEED_LABELS, speedChoice, W, H / 2f - 46);

        // Start button — blinks when in focus
        if (focusRow == 2) {
            game.fontMedium.setColor(Color.GREEN);
            String btn = (int)(animTime * 2) % 2 == 0 ? "> START GAME <" : "  START GAME  ";
            drawCentered(btn, W, H / 2f - 130, 2f);
        } else {
            game.fontMedium.setColor(new Color(0.5f, 0.7f, 0.5f, 1f));
            drawCentered("START GAME", W, H / 2f - 130, 2f);
        }

        game.batch.end();
        handleInput();
    }

    private void drawPanel(int W, int H) {
        int panelW = 750, panelH = 400; // Increased panel width to fit the text perfectly
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(new Color(0.07f, 0.11f, 0.07f, 1f));
        sr.rect(W / 2f - panelW / 2f, H / 2f - panelH / 2f, panelW, panelH);
        sr.end();
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.GREEN);
        sr.rect(W / 2f - panelW / 2f, H / 2f - panelH / 2f, panelW, panelH);
        sr.end();
    }

    private void labelSection(String label, int W, float y, boolean focused) {
        game.fontSmall.setColor(focused ? Color.YELLOW : new Color(0.6f, 0.8f, 0.6f, 1f));
        drawCentered(label, W, y, 1.5f);
    }

    /* Draws the options of a section, highlighting the currently selected option with brackets. */
    private void drawOptions(String[] labels, int selected, int W, float y) {
        float totalW = 0;
        for (String l : labels) totalW += l.length() * 10f * 1.5f + 40;
        float startX = W / 2f - totalW / 2f;
        for (int i = 0; i < labels.length; i++) {
            float lw = labels[i].length() * 10f * 1.5f;
            if (i == selected) {
                game.fontSmall.setColor(Color.GREEN);
                game.fontSmall.draw(game.batch, "[ " + labels[i] + " ]", startX, y);
            } else {
                game.fontSmall.setColor(new Color(0.35f, 0.5f, 0.35f, 1f));
                game.fontSmall.draw(game.batch, "  " + labels[i] + "  ", startX, y);
            }
            startX += lw + 40;
        }
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            focusRow = (focusRow - 1 + 3) % 3;
            game.soundManager.playNavigate();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            focusRow = (focusRow + 1) % 3;
            game.soundManager.playNavigate();
        }
        if (focusRow == 0) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT))
                gridChoice = (gridChoice - 1 + GRID_LABELS.length) % GRID_LABELS.length;
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT))
                gridChoice = (gridChoice + 1) % GRID_LABELS.length;
        }
        if (focusRow == 1) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT))
                speedChoice = (speedChoice - 1 + SPEED_LABELS.length) % SPEED_LABELS.length;
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT))
                speedChoice = (speedChoice + 1) % SPEED_LABELS.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (focusRow == 2) {
                // Builds the configuration object with the choices made and starts the game
                game.soundManager.playSelect();
                GameConfig cfg = new GameConfig(GRID_COLS[gridChoice], GRID_ROWS[gridChoice], SPEED_TICKS[speedChoice]);
                game.setScreen(new GameScreen(game, cfg));
            } else {
                game.soundManager.playNavigate();
                focusRow = (focusRow + 1) % 3;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
        }
    }

    private void drawCentered(String text, int W, float y, float scale) {
        float w = text.length() * 10f * scale;
        if (scale == 2f)      game.fontMedium.draw(game.batch, text, W / 2f - w / 2f, y);
        else if (scale == 3f) game.fontLarge.draw(game.batch, text, W / 2f - w / 2f, y);
        else                  game.fontSmall.draw(game.batch, text, W / 2f - w / 2f, y);
    }

    @Override public void show()   {}
    @Override public void resize(int w, int h) {}
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}
    @Override public void dispose() { sr.dispose(); }
}