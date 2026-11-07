package io.github.ninali927.maze.traversal;

import io.github.ninali927.maze.structures.Maze;
import io.github.ninali927.maze.structures.MazeCell;

import java.util.ArrayList;
import java.util.List;

/**
 * A recursive post-order traversal of a maze.
 *
 * In post-order traversal:
 * 1. Recursively visit each unvisited neighbor in order (north, south, east, west)
 * 2. Visit the current cell (add it to the result) AFTER all neighbors
 *
 * This is analogous to post-order tree traversal where we process children
 * before processing the node itself.
 *
 * Use Cases:
 * - Processing cells after their descendants (e.g., cleanup operations)
 * - Computing aggregate properties (e.g., counting descendants)
 * - Dependency analysis where children must be processed before parents
 */
public class PostorderTraversal implements MazeTraversal {

    @Override
    public List<MazeCell> traverse(Maze maze, int startRow, int startCol) {
        List<MazeCell> result = new ArrayList<>();
        maze.resetVisited();
        MazeCell startCell = maze.getCell(startRow, startCol);
        traverseHelper(maze, startCell, result);
        return result;
    }

    private void traverseHelper(Maze maze, MazeCell current, List<MazeCell> result) {
        if (current == null || current.isVisited() || current.isWall()) {
            return;
        }
        current.setVisited(true);
        for (MazeCell neighbor : maze.getNeighbors(current)) {
            if (!neighbor.isVisited() && !neighbor.isWall()) {
                traverseHelper(maze, neighbor, result);
            }
        }
        result.add(current);
    }

    @Override
    public String getName() {
        return "Post-order Traversal";
    }
}
