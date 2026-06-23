package com.snakegame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.snakegame.SnakeGame;
import com.snakegame.entities.Food;
import com.snakegame.entities.GridCell;
import com.snakegame.entities.Snake;
import com.snakegame.entities.Snake.Direction;
import com.snakegame.screens.SettingsScreen.GameConfig;

import java.util.Arrays;

/*
 * Main screen of the game. Contains the gameplay loop:
 * 1. Reads player input
 * 2. Updates the game state at regular intervals (ticks)
 * 3. Draws everything on the screen
 * * Snake movement does not occur every frame, but every "tick",
 * allowing speed control independent of the FPS.
 */
public class GameScreen implements Screen {

    private final SnakeGame  game;
    private final GameConfig cfg;
    private final ShapeRenderer sr = new ShapeRenderer();

    private Snake snake1;
    private Snake snake2;
    private Food  food;

    private static final float MIN_TICK  = 0.065f; // maximum allowed speed
    private static final float SPEED_INC = 0.003f; // acceleration applied for each food consumed

    private float tickInterval; // current time between snake steps
    private float tickAccum = 0f; // time accumulator since the last step

    private boolean gamePaused = false;
    private float   pauseFlash = 0f; // controls the blinking of the "PAUSED" text

    /* Main constructor: receives settings from SettingsScreen. */
    public GameScreen(SnakeGame game, GameConfig cfg) {
        this.game = game;
        this.cfg  = cfg;
        init();
    }

    /* Convenience constructor: uses default settings when restarting without settings. */
    public GameScreen(SnakeGame game) {
        this(game, GameConfig.defaults());
    }

    /* Initializes the snakes and food based on the match settings. */
    private void init() {
        tickInterval = cfg.initialTick;
        tickAccum    = 0f;

        int cols = cfg.gridCols;
        int rows = cfg.gridRows;

        // Player 1 starts on the left side, moving to the right (green)
        snake1 = new Snake(3, rows / 2, 4, Direction.RIGHT,
                new Color(0.2f, 0.9f, 0.2f, 1f), new Color(0.0f, 1.0f, 0.0f, 1f), "P1");

        // Player 2 starts on the right side, moving to the left (blue)
        snake2 = new Snake(cols - 4, rows / 2, 4, Direction.LEFT,
                new Color(0.2f, 0.6f, 1.0f, 1f), new Color(0.0f, 0.8f, 1.0f, 1f), "P2");

        food = new Food(Arrays.asList(snake1, snake2), cols, rows);
    }

    @Override
    public void render(float delta) {
        handleInput();

        if (!gamePaused) {
            // Accumulates passed time and advances the game in discrete steps
            tickAccum += delta;
            while (tickAccum >= tickInterval) {
                tickAccum -= tickInterval;
                update();
            }
        } else {
            pauseFlash += delta;
        }

        draw();
    }

    /* Reads pressed keys and updates snake directions and pause state. */
    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            gamePaused = !gamePaused;
        }

        // Player 1: arrow keys
        if (snake1.isAlive()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP))    snake1.setDirection(Direction.UP);
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN))  snake1.setDirection(Direction.DOWN);
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT))  snake1.setDirection(Direction.LEFT);
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) snake1.setDirection(Direction.RIGHT);
        }

        // Player 2: WASD
        if (snake2.isAlive()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.W)) snake2.setDirection(Direction.UP);
            if (Gdx.input.isKeyJustPressed(Input.Keys.S)) snake2.setDirection(Direction.DOWN);
            if (Gdx.input.isKeyJustPressed(Input.Keys.A)) snake2.setDirection(Direction.LEFT);
            if (Gdx.input.isKeyJustPressed(Input.Keys.D)) snake2.setDirection(Direction.RIGHT);
        }
    }

    /*
     * Advances the game state by one step:
     * moves snakes, checks collisions, and checks if food was consumed.
     */
    private void update() {
        if (!snake1.isAlive() && !snake2.isAlive()) return;

        int cols = cfg.gridCols, rows = cfg.gridRows;

        // Moves each living snake and stores the new head position
        GridCell head1 = snake1.isAlive() ? snake1.move(cols, rows) : null;
        GridCell head2 = snake2.isAlive() ? snake2.move(cols, rows) : null;

        // Checks if any snake collided with itself
        if (snake1.isAlive() && snake1.selfCollides()) { snake1.kill(); game.soundManager.playDeath(); }
        if (snake2.isAlive() && snake2.selfCollides()) { snake2.kill(); game.soundManager.playDeath(); }

        // Checks collision between snakes: one's head into the other's body
        if (snake1.isAlive() && head1 != null && snake2.occupies(head1)) { snake1.kill(); game.soundManager.playDeath(); }
        if (snake2.isAlive() && head2 != null && snake1.occupies(head2)) { snake2.kill(); game.soundManager.playDeath(); }

        // Checks if any snake ate the food
        GridCell foodPos = food.getPosition();
        if (snake1.isAlive() && snake1.getHead().equals(foodPos)) {
            snake1.grow();
            game.soundManager.playEat();
            food.spawn(Arrays.asList(snake1, snake2), cols, rows);
            speedUp();
        } else if (snake2.isAlive() && snake2.getHead().equals(foodPos)) {
            snake2.grow();
            game.soundManager.playEat();
            food.spawn(Arrays.asList(snake1, snake2), cols, rows);
            speedUp();
        }

        // If any snake died, the game ends
        if (!snake1.isAlive() || !snake2.isAlive()) goToGameOver();
    }

    /* Reduces the interval between steps to speed up the game, respecting the minimum limit. */
    private void speedUp() {
        tickInterval = Math.max(MIN_TICK, tickInterval - SPEED_INC);
    }

    /* Determines the winner, saves scores, and navigates to the game over screen. */
    private void goToGameOver() {
        String winner;
        if (!snake1.isAlive() && !snake2.isAlive()) {
            // Tie in points results in tie; otherwise, highest score wins
            winner = snake1.getScore() > snake2.getScore() ? "P1"
                   : snake2.getScore() > snake1.getScore() ? "P2" : "TIE";
        } else {
            winner = snake1.isAlive() ? "P1" : "P2";
        }
        game.highScoreManager.addScore("P1", snake1.getScore());
        game.highScoreManager.addScore("P2", snake2.getScore());
        game.setScreen(new GameOverScreen(game, snake1.getScore(), snake2.getScore(), winner));
    }

    /* Renders the grid, entities, and HUD. */
    private void draw() {
        Gdx.gl.glClearColor(0.05f, 0.07f, 0.05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        int W     = Gdx.graphics.getWidth();
        int H     = Gdx.graphics.getHeight();
        int cs    = SnakeGame.CELL_SIZE;
        int gridH = cfg.gridRows * cs;
        int hudH  = SnakeGame.HUD_HEIGHT;

        // HUD and grid background
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(new Color(0.08f, 0.12f, 0.08f, 1f));
        sr.rect(0, gridH, W, hudH);
        sr.setColor(new Color(0.07f, 0.09f, 0.07f, 1f));
        sr.rect(0, 0, W, gridH);
        sr.end();

        // Grid lines
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(new Color(0.10f, 0.13f, 0.10f, 1f));
        for (int x = 0; x <= W; x += cs)    sr.line(x, 0, x, gridH);
        for (int y = 0; y <= gridH; y += cs) sr.line(0, y, W, y);
        sr.end();

        // Entities
        sr.begin(ShapeRenderer.ShapeType.Filled);
        food.render(sr, 0);
        if (snake1.isAlive()) snake1.render(sr, 0);
        if (snake2.isAlive()) snake2.render(sr, 0);
        sr.end();

        game.batch.begin();
        drawHUD(W, gridH, hudH);
        game.batch.end();

        if (gamePaused) drawPauseOverlay(W, H);
    }

    /* Displays scores, current speed, and death indicators on the top bar. */
    private void drawHUD(int W, int gridH, int hudH) {
        float cy = gridH + hudH / 2f;

        game.fontSmall.setColor(new Color(0.2f, 1f, 0.2f, 1f));
        game.fontSmall.draw(game.batch, "P1 (ARROWS)  " + snake1.getScore(), 18, cy + 10);

        game.fontSmall.setColor(new Color(0.2f, 0.7f, 1f, 1f));
        game.fontSmall.draw(game.batch, snake2.getScore() + "  P2 (WASD)", W - 220, cy + 10);

        // Current speed percentage relative to the maximum possible
        float base = cfg.initialTick;
        float pct  = (base - tickInterval) / (base - MIN_TICK);
        game.fontSmall.setColor(Color.YELLOW);
        game.fontSmall.draw(game.batch, String.format("SPEED %d%%", (int)(pct * 100)), W / 2f - 60, cy + 10);

        // Configured grid size, visible during the match
        game.fontSmall.setColor(new Color(0.4f, 0.55f, 0.4f, 1f));
        game.fontSmall.draw(game.batch, cfg.gridCols + "x" + cfg.gridRows, W / 2f - 30, cy - 12);

        if (!snake1.isAlive()) { game.fontSmall.setColor(Color.RED); game.fontSmall.draw(game.batch, "[DEAD]", 18, cy - 14); }
        if (!snake2.isAlive()) { game.fontSmall.setColor(Color.RED); game.fontSmall.draw(game.batch, "[DEAD]", W - 130, cy - 14); }
    }

    /* Displays a dark overlay with the text "PAUSED" when the game is paused. */
    private void drawPauseOverlay(int W, int H) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(new Color(0, 0, 0, 0.55f));
        sr.rect(0, 0, W, H);
        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        game.batch.begin();
        if ((int)(pauseFlash * 2) % 2 == 0) {
            game.fontLarge.setColor(Color.YELLOW);
            game.fontLarge.draw(game.batch, "PAUSED", W / 2f - 90, H / 2f + 30);
        }
        game.fontSmall.setColor(Color.WHITE);
        game.fontSmall.draw(game.batch, "ESC or P to continue", W / 2f - 130, H / 2f - 20);
        game.batch.end();
    }

    @Override public void show()   {}
    @Override public void resize(int w, int h) {}
    @Override public void pause()  { gamePaused = true; }
    @Override public void resume() {}
    @Override public void hide()   {}
    @Override public void dispose() { sr.dispose(); }
}