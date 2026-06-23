package com.snakegame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.snakegame.SnakeGame;

/*
 * Screen displayed when the match ends.
 * Shows the final scores of both players, declares the winner
 * and offers the options to play again, return to the menu, or exit.
 */
public class GameOverScreen implements Screen {

    private final SnakeGame game;
    private final ShapeRenderer sr = new ShapeRenderer();

    private final int    scoreP1;
    private final int    scoreP2;
    private final String winner; // "P1", "P2" or "TIE"

    private static final String[] ITEMS = {"PLAY AGAIN", "MAIN MENU", "EXIT"};
    private int selectedIndex = 0;

    private float animTime = 0f;

    public GameOverScreen(SnakeGame game, int scoreP1, int scoreP2, String winner) {
        this.game    = game;
        this.scoreP1 = scoreP1;
        this.scoreP2 = scoreP2;
        this.winner  = winner;
    }

    @Override
    public void render(float delta) {
        animTime += delta;

        Gdx.gl.glClearColor(0.04f, 0.05f, 0.04f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        int W = Gdx.graphics.getWidth();
        int H = Gdx.graphics.getHeight();

        drawPanel(W, H);

        game.batch.begin();
        drawResults(W, H);
        drawMenuItems(W, H);
        game.batch.end();

        handleInput();
    }

    private void drawPanel(int W, int H) {
        int panelW = 460, panelH = 340;
        int px = W / 2 - panelW / 2, py = H / 2 - panelH / 2;

        // Panel background
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(new Color(0f, 0f, 0f, 0.7f));
        sr.rect(0, 0, W, H);
        sr.setColor(new Color(0.08f, 0.12f, 0.08f, 1f));
        sr.rect(px, py, panelW, panelH);
        sr.end();

        // Green border around the panel
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.GREEN);
        sr.rect(px, py, panelW, panelH);
        sr.end();

        // Highlight behind the selected item
        sr.begin(ShapeRenderer.ShapeType.Filled);
        float menuBaseY = py + 120;
        float itemGap   = 48;
        sr.setColor(new Color(0.1f, 0.4f, 0.1f, 0.5f));
        sr.rect(W / 2f - 150, menuBaseY - selectedIndex * itemGap - 18, 300, 36);
        sr.end();
    }

    /* Displays the title, the winner, and the scores of both players. */
    private void drawResults(int W, int H) {
        int panelH = 340;
        int py     = H / 2 - panelH / 2;
        int px     = W / 2 - 230;

        game.fontLarge.setColor(Color.RED);
        game.fontLarge.draw(game.batch, "GAME OVER", W / 2f - approxHalf("GAME OVER", 3f), py + panelH - 30);

        // Winner color and message vary according to the result
        String winMsg;
        Color  winColor;
        if ("TIE".equals(winner)) {
            winMsg = "TIE!"; winColor = Color.YELLOW;
        } else if ("P1".equals(winner)) {
            winMsg = "PLAYER 1 WINS!"; winColor = new Color(0.2f, 1f, 0.2f, 1f);
        } else {
            winMsg = "PLAYER 2 WINS!"; winColor = new Color(0.2f, 0.7f, 1f, 1f);
        }
        game.fontMedium.setColor(winColor);
        game.fontMedium.draw(game.batch, winMsg, W / 2f - approxHalf(winMsg, 2f), py + panelH - 70);

        game.fontSmall.setColor(new Color(0.2f, 1f, 0.2f, 1f));
        game.fontSmall.draw(game.batch, "P1: " + scoreP1 + " points", px + 40, py + panelH - 120);

        game.fontSmall.setColor(new Color(0.2f, 0.7f, 1f, 1f));
        game.fontSmall.draw(game.batch, "P2: " + scoreP2 + " points", px + 240, py + panelH - 120);
    }

    private void drawMenuItems(int W, int H) {
        int py       = H / 2 - 170;
        float menuY  = py + 120;
        float itemGap = 48;

        for (int i = 0; i < ITEMS.length; i++) {
            float y = menuY - i * itemGap;
            boolean sel = (i == selectedIndex);
            if (sel) {
                game.fontSmall.setColor(Color.GREEN);
                String label = (int)(animTime * 2) % 2 == 0 ? "> " + ITEMS[i] + " <" : ITEMS[i];
                game.fontSmall.draw(game.batch, label, W / 2f - approxHalf(label, 1.5f), y);
            } else {
                game.fontSmall.setColor(new Color(0.5f, 0.7f, 0.5f, 1f));
                game.fontSmall.draw(game.batch, ITEMS[i], W / 2f - approxHalf(ITEMS[i], 1.5f), y);
            }
        }
    }

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
                case 0: game.setScreen(new GameScreen(game));  break; // restarts with default config
                case 1: game.setScreen(new MenuScreen(game));  break;
                case 2: Gdx.app.exit();                        break;
            }
        }
    }

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