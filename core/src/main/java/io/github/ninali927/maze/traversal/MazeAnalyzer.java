package io.github.ninali927.maze.traversal;

import io.github.ninali927.maze.structures.Maze;
import io.github.ninali927.maze.structures.MazeCell;

/**
 * A utility class for analyzing properties of generated mazes.
 *
 * This class uses recursive traversal techniques to compute various
 * statistics about a maze structure, such as:
 *   - Number of dead ends (cells with only one neighbor)
 *   - Number of junctions (cells with three or more neighbors)
 *   - Number of corridors (cells with exactly two neighbors)
 *   - Longest path in the maze
 *
 * These metrics are useful for comparing maze generation algorithms
 * and understanding maze difficulty.
 */
public class MazeAnalyzer {

    /**
     * Counts the number of dead-end cells in the maze.
     *
     * A dead end is an open cell with exactly one open neighbor.
     *
     * @param maze the maze to analyze
     * @return the number of dead-end cells
     */
    public int countDeadEnds(Maze maze) {
        int count = 0;
        for (int row = 0; row < maze.getHeight(); row++) {
            for (int col = 0; col < maze.getWidth(); col++) {
                MazeCell cell = maze.getCell(row, col);
                if (!cell.isWall()) {
                    int neighborCount = maze.getNeighbors(cell).size();
                    if (neighborCount == 1) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /**
     * Counts the number of junction cells in the maze.
     *
     * A junction is an open cell with three or more open neighbors.
     *
     * @param maze the maze to analyze
     * @return the number of junction cells
     */
    public int countJunctions(Maze maze) {
        int count = 0;
        for (int row = 0; row < maze.getHeight(); row++) {
            for (int col = 0; col < maze.getWidth(); col++) {
                MazeCell cell = maze.getCell(row, col);
                if (!cell.isWall()) {
                    int neighborCount = maze.getNeighbors(cell).size();
                    if (neighborCount >= 3) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /**
     * Counts the number of corridor cells in the maze.
     *
     * A corridor is an open cell with exactly two open neighbors.
     *
     * @param maze the maze to analyze
     * @return the number of corridor cells
     */
    public int countCorridors(Maze maze) {
        int count = 0;
        for (int row = 0; row < maze.getHeight(); row++) {
            for (int col = 0; col < maze.getWidth(); col++) {
                MazeCell cell = maze.getCell(row, col);
                if (!cell.isWall()) {
                    int neighborCount = maze.getNeighbors(cell).size();
                    if (neighborCount == 2) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /**
     * Finds the length of the longest path in the maze from a given starting cell.
     *
     * This uses recursive depth-first search to explore all possible paths
     * and returns the length of the longest one.
     *
     * @param maze the maze to analyze
     * @param start the starting cell
     * @return the length of the longest path from the start cell
     */
    public int findLongestPathLength(Maze maze, MazeCell start) {
        maze.resetVisited();
        return findLongestPathHelper(maze, start);
    }

    /**
     * Computes a "difficulty score" for the maze.
     *
     * The difficulty is based on multiple factors:
     *   - Fewer dead ends = harder (less obvious wrong turns)
     *   - More junctions = harder (more choices)
     *   - Longer maximum path = harder (more exploration needed)
     *
     * This is a simple heuristic and can be customized.
     *
     * @param maze the maze to analyze
     * @return a difficulty score (higher = more difficult)
     */
    public double computeDifficulty(Maze maze) {
        int deadEnds = countDeadEnds(maze);
        int junctions = countJunctions(maze);
        MazeCell start = maze.getCell(0, 0);
        int longestPath = findLongestPathLength(maze, start);

        return (junctions * 2.0) + longestPath - deadEnds;
    }

    /**
     * Recursive helper for finding the longest path from a cell.
     */
    private int findLongestPathHelper(Maze maze, MazeCell current) {
        current.setVisited(true);

        int maxLength = 0;
        for (MazeCell neighbor : maze.getNeighbors(current)) {
            if (!neighbor.isVisited()) {
                int pathLength = findLongestPathHelper(maze, neighbor);
                if (pathLength > maxLength) {
                    maxLength = pathLength;
                }
            }
        }

        current.setVisited(false);
        return maxLength + 1;
    }
}
