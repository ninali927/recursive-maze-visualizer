package io.github.ninali927.maze.generators;

import io.github.ninali927.maze.structures.Maze;

/**
 * An abstract interface for maze generation algorithms.
 *
 * A maze generator creates a valid maze where there exists at least one path
 * between any two open cells. Implementations may use different algorithmic
 * strategies (recursive, iterative, etc.) to achieve this goal.
 *
 * The generation process typically starts from a maze where all cells are walls,
 * then carves out paths according to the specific algorithm's rules.
 */
public interface MazeGenerator {
    /**
     * Generates a maze by modifying the provided maze instance.
     *
     * This method starts from a given cell and carves out passages to create
     * a fully connected maze. The starting cell should be within the maze bounds.
     *
     * Pre-condition: The maze should typically have all cells
     * marked as walls before generation begins, though specific implementations
     * may have different requirements.
     *
     * Post-condition: The maze will have a connected network
     * of open passages such that any open cell is reachable from any other open cell.
     *
     * @param maze the maze to generate passages in
     * @param startRow the row index of the starting cell
     * @param startCol the column index of the starting cell
     * @throws IndexOutOfBoundsException if the start position is outside maze bounds
     */
    void generate(Maze maze, int startRow, int startCol);

    /**
     * Returns the name of this generation algorithm.
     *
     * Useful for debugging, visualization, and algorithm comparison.
     *
     * @return a human-readable name for this generation algorithm
     */
    String getName();
}
