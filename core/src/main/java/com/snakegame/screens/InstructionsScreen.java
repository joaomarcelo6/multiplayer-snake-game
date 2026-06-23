package com.snakegame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.snakegame.SnakeGame;

/*
 * Instructions screen accessible from the main menu.
 * Explains the game rules and controls for each player.
 */
public class InstructionsScreen implements Screen {

    private final SnakeGame game;
    private final ShapeRenderer sr = new ShapeRenderer();

    // Instructions content.
    private static final String[] LINES = {
        "HOW TO PLAY",
        "",
        "Two players compete on the same keyboard.",
        "Eat the red food to grow and score points.",
        "Speed increases with every food eaten.",
        "Walls have wrap-around: exiting one side enters the other.",
        "",
        "A snake dies if it collides with:",
        "  • itself",
        "  • the other snake",
        "",
        "The player with the most points when a snake dies wins.",
        "",
        "CONTROLS",
        "",
        "  Player 1:  Arrow keys",
        "  Player 2:  WASD",
        "",
        "  ESC / P     Pause game",
        "",
        "Press BACKSPACE or ESC to return."
    };

    public InstructionsScreen(SnakeGame game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.07f, 0.05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        int W = Gdx.graphics.getWidth();
        int H = Gdx.graphics.getHeight();

        int panelW = 750, panelH = 520; // Expanded width to fit the English text perfectly
        int px = W / 2 - panelW / 2, py = H / 2 - panelH / 2;

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(new Color(0.07f, 0.10f, 0.07f, 1f));
        sr.rect(px, py, panelW, panelH);
        sr.end();
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.GREEN);
        sr.rect(px, py, panelW, panelH);
        sr.end();

        game.batch.begin();

        float lineH  = 22f;
        float startY = py + panelH - 28;

        // Each line gets a different color depending on its content
        for (int i = 0; i < LINES.length; i++) {
            String line = LINES[i];
            float y = startY - i * lineH;

            if (i == 0) {
                game.fontMedium.setColor(Color.GREEN);
                game.fontMedium.draw(game.batch, line, px + 30, y);
            } else if (line.equals("CONTROLS")) {
                game.fontSmall.setColor(Color.YELLOW);
                game.fontSmall.draw(game.batch, line, px + 30, y);
            } else if (line.startsWith("  Player")) {
                game.fontSmall.setColor(new Color(0.3f, 0.9f, 0.3f, 1f));
                game.fontSmall.draw(game.batch, line, px + 30, y);
            } else if (line.startsWith("  ESC")) {
                game.fontSmall.setColor(new Color(0.8f, 0.8f, 0.3f, 1f));
                game.fontSmall.draw(game.batch, line, px + 30, y);
            } else if (line.contains("BACKSPACE")) {
                game.fontSmall.setColor(new Color(0.4f, 0.6f, 0.4f, 1f));
                game.fontSmall.draw(game.batch, line, px + 30, y);
            } else {
                game.fontSmall.setColor(new Color(0.75f, 0.9f, 0.75f, 1f));
                game.fontSmall.draw(game.batch, line, px + 30, y);
            }
        }

        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)
         || Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)
         || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
        }
    }

    @Override public void show()   {}
    @Override public void resize(int w, int h) {}
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}
    @Override public void dispose() { sr.dispose(); }
}