package io.github.ninali927.maze.traversal;

import io.github.ninali927.maze.structures.Maze;
import io.github.ninali927.maze.structures.MazeCell;

import java.util.ArrayList;
import java.util.List;

/**
 * A recursive backtracking algorithm for finding paths in a maze.
 *
 * Unlike iterative stack-based or queue-based search,
 * this implementation uses recursive backtracking to explore paths. This approach
 * can find:
 * - A single path from start to goal
 * - All possible paths from start to goal
 * - The shortest path (by comparing all paths)
 * - The longest path (useful for maze difficulty analysis)
 *
 * Algorithm:
 * 1. Start at the current cell and mark it as visited
 * 2. If current cell is the goal, we found a path
 * 3. Otherwise, try each unvisited neighbor recursively
 * 4. Backtrack by unmarking the cell as visited
 */
public class RecursivePathFinder {
    private Maze maze;

    /**
     * Creates a new recursive path finder for the given maze.
     *
     * @param maze the maze to search
     */
    public RecursivePathFinder(Maze maze) {
        this.maze = maze;
    }

    /**
     * Finds a single path from start to goal using recursive backtracking.
     *
     * @param start the starting cell
     * @param goal the goal cell
     * @return a list representing the path from start to goal, or empty if no path exists
     */
    public List<MazeCell> findPath(MazeCell start, MazeCell goal) {
        List<MazeCell> path = new ArrayList<>();
        maze.resetVisited();
        findPathHelper(start, goal, path);
        maze.resetVisited();
        return path;
    }

    private boolean findPathHelper(MazeCell current, MazeCell goal, List<MazeCell> path) {
        if (current == null || current.isVisited() || current.isWall()) {
            return false;
        }
        path.add(current);
        current.setVisited(true);

        if (current.equals(goal)) {
            return true;
        } else {
            for (MazeCell neighbor : maze.getNeighbors(current)) {
                if (!neighbor.isVisited() && !neighbor.isWall()) {
                    if (findPathHelper(neighbor, goal, path)) {
                        return true;
                    }
                }
            }
        }
        path.remove(path.size() - 1);
        current.setVisited(false);
        return false;
    }

    /**
     * Finds all possible paths from start to goal.
     *
     * This method explores every possible route through the maze,
     * which can be computationally expensive for large mazes with many paths.
     *
     * @param start the starting cell
     * @param goal the goal cell
     * @return a list of all paths, where each path is a list of cells
     */
    public List<List<MazeCell>> findAllPaths(MazeCell start, MazeCell goal) {
        List<List<MazeCell>> allPaths = new ArrayList<>();
        List<MazeCell> currentPath = new ArrayList<>();
        maze.resetVisited();
        findAllPathsHelper(start, goal, currentPath, allPaths);
        maze.resetVisited();
        return allPaths;
    }

    private void findAllPathsHelper(MazeCell current, MazeCell goal, List<MazeCell> currentPath,
            List<List<MazeCell>> allPaths) {
        if (current == null || current.isVisited() || current.isWall()) {
            return;
        }
        currentPath.add(current);
        current.setVisited(true);
        if (current.equals(goal)) {
            allPaths.add(new ArrayList<>(currentPath));
        } else {
            for (MazeCell neighbor : maze.getNeighbors(current)) {
                if (!neighbor.isVisited() && !neighbor.isWall()) {
                    findAllPathsHelper(neighbor, goal, currentPath, allPaths);
                }
            }
        }

        currentPath.remove(currentPath.size() - 1);
        current.setVisited(false);
    }

    /**
     * Finds the shortest path from start to goal by exploring all paths.
     *
     * This is a brute-force approach that finds all paths and returns the shortest.
     * For large mazes, an iterative breadth-first search is more efficient.
     *
     * @param start the starting cell
     * @param goal the goal cell
     * @return the shortest path, or an empty list if no path exists
     */
    public List<MazeCell> findShortestPath(MazeCell start, MazeCell goal) {
        List<List<MazeCell>> allPaths = findAllPaths(start, goal);
        if (allPaths.isEmpty()) {
            return new ArrayList<>();
        }
        List<MazeCell> shortest = allPaths.get(0);
        for (List<MazeCell> path : allPaths) {
            if (path.size() < shortest.size()) {
                shortest = path;
            }
        }
        return shortest;
    }
}
