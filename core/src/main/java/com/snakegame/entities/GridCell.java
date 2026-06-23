package com.snakegame.entities;

import java.util.Objects;

/*
 * Represents a position on the game grid, defined by column and row.
 * It is immutable: once created, the position cannot be changed.
 * This prevents bugs where a cell would be accidentally modified by another part of the code.
 */
public final class GridCell {

    public final int col; // horizontal position on the grid
    public final int row; // vertical position on the grid

    public GridCell(int col, int row) {
        this.col = col;
        this.row = row;
    }

    /*
     * Two cells are equal when they occupy exactly the same position on the grid.
     * This is essential for detecting collisions: we compare positions, not object references.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GridCell)) return false;
        GridCell gc = (GridCell) o;
        return col == gc.col && row == gc.row;
    }

    @Override
    public int hashCode() {
        return Objects.hash(col, row);
    }

    @Override
    public String toString() {
        return "GridCell(" + col + ", " + row + ")";
    }
}