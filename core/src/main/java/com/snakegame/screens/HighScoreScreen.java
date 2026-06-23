package com.snakegame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.snakegame.SnakeGame;
import com.snakegame.utils.HighScoreManager.ScoreEntry;

import java.util.List;

/*
 * Screen that displays the best scores ever recorded.
 * Data is read from the HighScoreManager, which loads them from the local file.
 */
public class HighScoreScreen implements Screen {

    private final SnakeGame game;
    private final ShapeRenderer sr = new ShapeRenderer();

    public HighScoreScreen(SnakeGame game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.07f, 0.05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        int W = Gdx.graphics.getWidth();
        int H = Gdx.graphics.getHeight();

        int panelW = 480, panelH = 380;
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

        game.fontLarge.setColor(Color.GREEN);
        game.fontLarge.draw(game.batch, "HIGH SCORES", px + 60, py + panelH - 20);

        // Column headers
        game.fontSmall.setColor(new Color(0.5f, 0.8f, 0.5f, 1f));
        game.fontSmall.draw(game.batch, "RANK",  px + 30,  py + panelH - 80);
        game.fontSmall.draw(game.batch, "NAME",  px + 120, py + panelH - 80);
        game.fontSmall.draw(game.batch, "SCORE", px + 310, py + panelH - 80);

        List<ScoreEntry> scores = game.highScoreManager.getScores();

        // Special colors for the top three places (gold, silver, bronze)
        Color[] rowColors = {
            new Color(1f, 0.85f, 0f, 1f),
            new Color(0.8f, 0.8f, 0.8f, 1f),
            new Color(0.8f, 0.5f, 0.2f, 1f),
            new Color(0.6f, 0.8f, 0.6f, 1f),
            new Color(0.6f, 0.8f, 0.6f, 1f)
        };

        for (int i = 0; i < 5; i++) {
            float y = py + panelH - 120 - i * 44;
            game.fontSmall.setColor(i < rowColors.length ? rowColors[i] : Color.WHITE);

            if (i < scores.size()) {
                ScoreEntry e = scores.get(i);
                game.fontSmall.draw(game.batch, "#" + (i + 1), px + 30,  y);
                game.fontSmall.draw(game.batch, e.name,        px + 120, y);
                game.fontSmall.draw(game.batch, "" + e.score,  px + 310, y);
            } else {
                // Position not yet recorded
                game.fontSmall.setColor(new Color(0.4f, 0.4f, 0.4f, 1f));
                game.fontSmall.draw(game.batch, "#" + (i + 1), px + 30,  y);
                game.fontSmall.draw(game.batch, "---",          px + 120, y);
                game.fontSmall.draw(game.batch, "0",            px + 310, y);
            }
        }

        game.fontSmall.setColor(new Color(0.4f, 0.6f, 0.4f, 1f));
        game.fontSmall.draw(game.batch, "BACKSPACE or ESC to return", px + 40, py + 28);

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