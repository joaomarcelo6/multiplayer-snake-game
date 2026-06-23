package com.snakegame.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.snakegame.SnakeGame;

import java.util.LinkedList;

/*
 * Represents a snake in the game.
 * The body is stored as a linked list of grid cells, where the head is always the first element.
 * This structure allows adding the new head at the beginning and removing the tail at the end efficiently.
 */
public class Snake {

    public enum Direction { UP, DOWN, LEFT, RIGHT }

    private final String playerName;
    private final Color  bodyColor;
    private final Color  headColor;

    private final LinkedList<GridCell> body = new LinkedList<>();

    private Direction currentDir;
    private Direction bufferedDir; // direction requested by the player, applied in the next step

    private boolean alive   = true;
    private boolean growing = false; // signals that the snake should grow on the next move
    private int     score   = 0;

    /*
     * Creates a snake with initial position, size, and direction.
     * The body is built behind the head, in the opposite direction of the initial movement.
     */
    public Snake(int startCol, int startRow, int startLen,
                 Direction startDir, Color bodyColor, Color headColor, String name) {
        this.bodyColor   = bodyColor;
        this.headColor   = headColor;
        this.playerName  = name;
        this.currentDir  = startDir;
        this.bufferedDir = startDir;

        // Calculates the offset to build the body behind the head
        int dc = (startDir == Direction.LEFT) ? 1 : (startDir == Direction.RIGHT) ? -1 : 0;
        int dr = (startDir == Direction.UP)   ? -1 : (startDir == Direction.DOWN) ? 1  : 0;

        for (int i = 0; i < startLen; i++) {
            body.add(new GridCell(startCol + dc * i, startRow + dr * i));
        }
    }

    /*
     * Registers a direction change requested by the player.
     * Movements in the opposite direction to the current one are ignored,
     * because a snake cannot turn around on itself.
     */
    public void setDirection(Direction newDir) {
        if (isOpposite(newDir, currentDir)) return;
        bufferedDir = newDir;
    }

    /*
     * Advances the snake one step on the grid.
     * The new head is calculated based on the current direction.
     * If the snake is at the edges, it reappears on the opposite side (wrap-around).
     * If it is not growing, the last cell of the body is removed.
     */
    public GridCell move(int gridCols, int gridRows) {
        currentDir = bufferedDir;

        GridCell head = body.getFirst();
        int nx = head.col + dx(currentDir);
        int ny = head.row + dy(currentDir);

        // Wrap-around: when exiting one side, reappears on the opposite side
        nx = ((nx % gridCols) + gridCols) % gridCols;
        ny = ((ny % gridRows) + gridRows) % gridRows;

        GridCell newHead = new GridCell(nx, ny);
        body.addFirst(newHead);

        if (growing) {
            growing = false; // snake grew: keeps the tail intact
        } else {
            body.removeLast(); // normal movement: removes the tail to maintain size
        }

        return newHead;
    }

    /* Marks that the snake should grow on the next step and increments the score. */
    public void grow() {
        growing = true;
        score++;
    }

    /* Checks if the snake's head collides with any segment of its own body. */
    public boolean selfCollides() {
        GridCell head = body.getFirst();
        for (int i = 1; i < body.size(); i++) {
            if (body.get(i).equals(head)) return true;
        }
        return false;
    }

    /* Checks if any cell of the body (including the head) occupies the given position. */
    public boolean occupies(GridCell cell) {
        for (GridCell c : body) {
            if (c.equals(cell)) return true;
        }
        return false;
    }

    /*
     * Draws the snake on the screen using geometric shapes.
     * The head is highlighted with a different color and gets eyes
     * positioned according to the current direction of movement.
     */
    public void render(ShapeRenderer sr, int offsetY) {
        int s   = SnakeGame.CELL_SIZE;
        int pad = 2; // space between cells to give a visual separation effect

        // Draws the body (from the neck to the tail)
        sr.setColor(bodyColor);
        for (int i = 1; i < body.size(); i++) {
            GridCell c = body.get(i);
            sr.rect(c.col * s + pad, offsetY + c.row * s + pad, s - pad * 2, s - pad * 2);
        }

        // Draws the head with a different color
        GridCell head = body.getFirst();
        sr.setColor(headColor);
        sr.rect(head.col * s + pad, offsetY + head.row * s + pad, s - pad * 2, s - pad * 2);

        // Draws the eyes on the head
        sr.setColor(Color.WHITE);
        drawEyes(sr, head, offsetY, s);
    }

    /* Positions two small circles as eyes, facing the direction of movement. */
    private void drawEyes(ShapeRenderer sr, GridCell head, int offsetY, int s) {
        float cx = head.col * s + s / 2f;
        float cy = offsetY + head.row * s + s / 2f;
        float er = s * 0.12f; // eye radius
        float eo = s * 0.18f; // distance from eyes to the center of the head

        switch (currentDir) {
            case RIGHT: sr.circle(cx + eo, cy + eo, er); sr.circle(cx + eo, cy - eo, er); break;
            case LEFT:  sr.circle(cx - eo, cy + eo, er); sr.circle(cx - eo, cy - eo, er); break;
            case UP:    sr.circle(cx + eo, cy + eo, er); sr.circle(cx - eo, cy + eo, er); break;
            case DOWN:  sr.circle(cx + eo, cy - eo, er); sr.circle(cx - eo, cy - eo, er); break;
        }
    }

    // Returns the horizontal displacement for a given direction
    private static int dx(Direction d) {
        return d == Direction.RIGHT ? 1 : d == Direction.LEFT ? -1 : 0;
    }

    // Returns the vertical displacement for a given direction
    private static int dy(Direction d) {
        return d == Direction.UP ? 1 : d == Direction.DOWN ? -1 : 0;
    }

    // Checks if two directions are opposite to each other
    private static boolean isOpposite(Direction a, Direction b) {
        return (a == Direction.UP    && b == Direction.DOWN)
            || (a == Direction.DOWN  && b == Direction.UP)
            || (a == Direction.LEFT  && b == Direction.RIGHT)
            || (a == Direction.RIGHT && b == Direction.LEFT);
    }

    public GridCell getHead()             { return body.getFirst(); }
    public LinkedList<GridCell> getBody() { return body; }
    public int     getScore()             { return score; }
    public boolean isAlive()              { return alive; }
    public void    kill()                 { alive = false; }
    public String  getPlayerName()        { return playerName; }
    public Color   getBodyColor()         { return bodyColor; }
}