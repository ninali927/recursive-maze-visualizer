package io.github.ninali927.maze.structures;

import java.util.List;

/**
 * Represents a rectangular grid maze composed of cells with up to four
 * orthogonal connections (north, south, east, west).
 *
 * Implementations should define their performance characteristics.
 */
public interface Maze {
    /**
     * Returns the number of columns in the maze.
     *
     * @return the maze width (columns), greater than zero
     */
    int getWidth();

    /**
     * Returns the number of rows in the maze.
     *
     * @return the maze height (rows), greater than zero
     */
    int getHeight();

    /**
     * Returns the cell at the specified row and column.
     *
     * @param row zero‑based row index
     * @param col zero‑based column index
     * @return the cell at (row, col)
     * @throws IndexOutOfBoundsException if (row, col) is outside the maze bounds
     */
    MazeCell getCell(int row, int col);

    /**
     * Returns whether the cell at (row, col) is marked as a wall (blocked).
     *
     * @param row zero‑based row index
     * @param col zero‑based column index
     * @return {@code true} if the cell is a wall; {@code false} otherwise
     * @throws IndexOutOfBoundsException if (row, col) is outside the maze bounds
     */
    boolean isWall(int row, int col);

    /**
     * Sets the wall state for the cell at (row, col).
     *
     * @param row zero‑based row index
     * @param col zero‑based column index
     * @param isWall {@code true} to mark the cell as a wall; {@code false} to mark it open
     * @throws IndexOutOfBoundsException if (row, col) is outside the maze bounds
     */
    void setWall(int row, int col, boolean isWall);

    /**
     * Returns the adjacent, non‑wall neighbors of the given cell in the maze.
     * The result contains at most four cells (north, south, east, west),
     * filtered to exclude walls. Neighbors are returned in the order:
     * north, south, east, west (when present and not walls).
     *
     * @param cell a cell obtained from this maze
     * @return a list of orthogonally adjacent, open (non‑wall) neighbors;
     * @throws IllegalArgumentException if the provided cell does not belong to this maze
     */
    List<MazeCell> getNeighbors(MazeCell cell);

    /**
     * Clears the visited flag on all cells in the maze.
     */
    void resetVisited();
}
