package io.github.ninali927.maze.structures;

/**
 * An interface representing a single cell within a maze.
 * This serves as a public abstraction for any underlying implementation.
 */
public interface MazeCell {
    /**
     * Checks if this cell is a wall.
     * @return true if the cell is a wall, false otherwise.
     */
    boolean isWall();

    /**
     * Sets the wall state of this cell.
     * @param isWall true to make the cell a wall, false to make it a path.
     */
    void setWall(boolean isWall);

    /**
     * Checks if this cell has been marked as visited during a search.
     * @return true if the cell has been visited, false otherwise.
     */
    boolean isVisited();

    /**
     * Sets the visited state of this cell.
     * @param visited true to mark the cell as visited, false otherwise.
     */
    void setVisited(boolean visited);
}
