package com.snakegame.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.snakegame.SnakeGame;

import java.util.List;
import java.util.Random;

/*
 * Represents the food item on the grid.
 * The food always occupies an empty cell and is repositioned each time a snake consumes it.
 */
public class Food {

    private GridCell position;
    private static final Random rng = new Random();

    public Food(List<Snake> snakes, int cols, int rows) {
        spawn(snakes, cols, rows);
    }

    /*
     * Chooses a random position on the grid that is not occupied by any snake.
     * The loop ensures that the food never spawns inside a player's body.
     */
    public void spawn(List<Snake> snakes, int cols, int rows) {
        GridCell candidate;
        do {
            candidate = new GridCell(rng.nextInt(cols), rng.nextInt(rows));
        } while (occupiedByAny(candidate, snakes));
        position = candidate;
    }

    /* Checks if the candidate cell is occupied by any of the snakes. */
    private boolean occupiedByAny(GridCell cell, List<Snake> snakes) {
        for (Snake s : snakes) if (s.occupies(cell)) return true;
        return false;
    }

    /*
     * Draws the food as a red circle with a white glow,
     * giving it the appearance of an apple.
     */
    public void render(ShapeRenderer sr, int offsetY) {
        int s = SnakeGame.CELL_SIZE;
        float cx = position.col * s + s / 2f;
        float cy = offsetY + position.row * s + s / 2f;
        float r  = s / 2f - 3f;

        // Semi-transparent outer ring for a glow effect
        sr.setColor(new Color(1f, 0.3f, 0.3f, 0.4f));
        sr.circle(cx, cy, r + 4f);

        // Main body of the apple
        sr.setColor(Color.RED);
        sr.circle(cx, cy, r);

        // Light reflection on the top left corner
        sr.setColor(new Color(1f, 0.7f, 0.7f, 1f));
        sr.circle(cx - r * 0.25f, cy + r * 0.25f, r * 0.3f);
    }

    public GridCell getPosition() { return position; }
}