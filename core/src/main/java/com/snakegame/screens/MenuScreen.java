package com.snakegame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.snakegame.SnakeGame;

/*
 * Main menu screen.
 * Displays navigation options and an animated decorative snake in the background.
 */
public class MenuScreen implements Screen {

    private final SnakeGame game;
    private final ShapeRenderer sr = new ShapeRenderer();

    private static final String[] ITEMS = {"NEW GAME", "HIGH SCORES", "INSTRUCTIONS", "EXIT"};
    private int selectedIndex = 0;

    private float animTime = 0f; // controls the animation of the decorative snake and the blinking cursor

    public MenuScreen(SnakeGame game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        animTime += delta;

        Gdx.gl.glClearColor(0.06f, 0.08f, 0.06f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        int W = Gdx.graphics.getWidth();
        int H = Gdx.graphics.getHeight();

        drawGrid(W, H);
        drawDecorativeSnake(W, H);

        game.batch.begin();
        drawTitle(W, H);
        drawMenuItems(W, H);
        drawFooterHint(W);
        game.batch.end();

        handleInput();
    }

    /* Draws subtle grid lines in the background to reinforce the game's grid theme. */
    private void drawGrid(int W, int H) {
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(new Color(0.12f, 0.15f, 0.12f, 1f));
        int cs = SnakeGame.CELL_SIZE;
        for (int x = 0; x <= W; x += cs) sr.line(x, 0, x, H);
        for (int y = 0; y <= H; y += cs) sr.line(0, y, W, y);
        sr.end();
    }

    /* Draws the game title and subtitle centered at the top. */
    private void drawTitle(int W, int H) {
        game.fontLarge.setColor(Color.GREEN);
        game.fontLarge.draw(game.batch, "SNAKE", W / 2f - approxHalf("SNAKE", 3f), H * 0.75f + 20);
        game.fontSmall.setColor(new Color(0.5f, 1f, 0.5f, 1f));
        game.fontSmall.draw(game.batch, "MULTIPLAYER", W / 2f - approxHalf("MULTIPLAYER", 1.5f), H * 0.75f - 12);
    }

    /* Draws the menu items, highlighting the selected one with a blinking cursor. */
    private void drawMenuItems(int W, int H) {
        for (int i = 0; i < ITEMS.length; i++) {
            float y = H * (0.48f - i * 0.09f);
            boolean isSelected = (i == selectedIndex);

            if (isSelected) {
                // Semi-transparent background behind the selected item
                game.batch.end();
                sr.begin(ShapeRenderer.ShapeType.Filled);
                float tw = approxHalf(ITEMS[i], 2f) * 2;
                sr.setColor(new Color(0.1f, 0.4f, 0.1f, 0.6f));
                sr.rect(W / 2f - tw / 2f - 20, y - 28, tw + 40, 40);
                sr.end();
                game.batch.begin();

                game.fontMedium.setColor(Color.GREEN);
                // The cursor blinks alternating between showing the arrows or not
                String label = (int)(animTime * 2) % 2 == 0 ? "> " + ITEMS[i] + " <" : ITEMS[i];
                game.fontMedium.draw(game.batch, label, W / 2f - approxHalf(label, 2f), y);
            } else {
                game.fontMedium.setColor(new Color(0.6f, 0.8f, 0.6f, 1f));
                game.fontMedium.draw(game.batch, ITEMS[i], W / 2f - approxHalf(ITEMS[i], 2f), y);
            }
        }
    }

    /* Navigation hint displayed at the bottom of the screen. */
    private void drawFooterHint(int W) {
        String hint = "UP/DOWN: change row   ENTER: confirm";
        game.fontSmall.setColor(new Color(0.4f, 0.6f, 0.4f, 1f));
        game.fontSmall.draw(game.batch, hint, W / 2f - approxHalf(hint, 1.5f), 28);
    }

    /* Processes pressed keys to navigate and select options. */
    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedIndex = (selectedIndex - 1 + ITEMS.length) % ITEMS.length;
            game.soundManager.playNavigate();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedIndex = (selectedIndex + 1) % ITEMS.length;
            game.soundManager.playNavigate();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.soundManager.playSelect();
            switch (selectedIndex) {
                case 0: game.setScreen(new SettingsScreen(game));     break;
                case 1: game.setScreen(new HighScoreScreen(game));    break;
                case 2: game.setScreen(new InstructionsScreen(game)); break;
                case 3: Gdx.app.exit();                               break;
            }
        }
    }

    /*
     * Draws an animated snake in the menu background using sine wave movement.
     */
    private void drawDecorativeSnake(int W, int H) {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(new Color(0.1f, 0.25f, 0.1f, 0.6f));
        int cs = SnakeGame.CELL_SIZE;
        for (int i = 0; i < 20; i++) {
            float t  = animTime * 0.6f + i * 0.5f;
            int   cx = (int)((MathUtils.sin(t) * 0.35f + 0.5f) * W / cs) * cs;
            int   cy = H - i * cs - cs;
            sr.rect(cx + 2, cy + 2, cs - 4, cs - 4);
        }
        sr.end();
    }

    /* Estimate of half the width of a text to center it on the screen. */
    private float approxHalf(String text, float scale) {
        return text.length() * 10f * scale / 2f;
    }

    @Override public void show()   {}
    @Override public void resize(int w, int h) {}
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}
    @Override public void dispose() { sr.dispose(); }
}