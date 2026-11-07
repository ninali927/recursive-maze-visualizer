package io.github.ninali927.maze.traversal;

import io.github.ninali927.maze.structures.Maze;
import io.github.ninali927.maze.structures.MazeCell;

import java.util.ArrayList;
import java.util.List;

/**
 * A recursive pre-order traversal of a maze.
 *
 * In pre-order traversal:
 * 1. Visit the current cell (add it to the result)
 * 2. Recursively visit each unvisited neighbor in order (north, south, east, west)
 *
 * This is analogous to pre-order tree traversal where we process the node
 * before recursively processing its children.
 *
 * Use Cases:
 *   - Processing cells before their descendants
 *   - Creating a depth-first exploration pattern
 *   - Generating maze representations where parent information comes first
 */
public class PreorderTraversal implements MazeTraversal {

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
        result.add(current);
        for (MazeCell neighbor : maze.getNeighbors(current)) {
            if (!neighbor.isVisited() && !neighbor.isWall()) {
                traverseHelper(maze, neighbor, result);
            }
        }

    }

    @Override
    public String getName() {
        return "Pre-order Traversal";
    }
}
