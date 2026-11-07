package io.github.ninali927.maze.traversal;

import io.github.ninali927.maze.structures.Maze;
import io.github.ninali927.maze.structures.MazeCell;

import java.util.List;

/**
 * An abstract interface for traversing all cells in a maze.
 *
 * Different traversal strategies visit cells in different orders, analogous
 * to tree traversal algorithms (pre-order, post-order, etc.).
 *
 * Traversals are useful for:
 * - Analyzing maze properties (counting passages, dead ends, junctions)
 * - Applying transformations to all cells
 * - Generating maze statistics
 * - Debugging and visualization
 */
public interface MazeTraversal {
    /**
     * Traverses the maze starting from the specified cell and returns
     * the cells in the order they were visited.
     *
     * The exact order depends on the specific traversal strategy
     * implemented by concrete classes.
     *
     * @param maze the maze to traverse
     * @param startRow the row index of the starting cell
     * @param startCol the column index of the starting cell
     * @return a list of cells in the order they were visited
     * @throws IndexOutOfBoundsException if start position is out of bounds
     */
    List<MazeCell> traverse(Maze maze, int startRow, int startCol);

    /**
     * Returns the name of this traversal strategy.
     *
     * @return a human-readable name for this traversal algorithm
     */
    String getName();
}
