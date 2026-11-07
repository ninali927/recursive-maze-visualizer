package io.github.ninali927.maze.generators;

import io.github.ninali927.maze.structures.Maze;
import io.github.ninali927.maze.structures.MazeCell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A maze generator that uses recursive backtracking to create perfect mazes.
 * This is a very common algorithm for generating simple and perfect mazes.
 *
 * The algorithm works by carving passages from a grid that starts full of walls.
 *
 * How it works on a grid:
 *   1. Start with a grid where every cell is a wall.
 *   2. Pick a starting cell, mark it as visited, and carve it out (i.e., make it a passage).
 *   3. Find a random, unvisited neighbor cell that is **two steps** away.
 *   4. Carve the wall cell **between** the current cell and the chosen neighbor.
 *   5. Move to the neighbor cell, mark it as visited, and repeat the process recursively.
 *   6. If the current cell has no unvisited neighbors to move to, backtrack to the
 *      previous cell and try to find a new neighbor from there.
 *
 * This approach guarantees a "perfect" maze: exactly one path exists between
 * any two cells, with no loops or isolated regions.
 *
 * Algorithm Characteristics:
 *   - Time Complexity: O(width × height) - visits each cell a constant number of times.
 *   - Space Complexity: O(width × height) in the worst case for the recursion stack.
 *   - Produces mazes with long, winding corridors and a low number of dead ends.
 */
public class RecursiveBacktrackingGenerator implements MazeGenerator {
    private final Random random;

    /**
     * Creates a new recursive backtracking generator with a random seed.
     */
    public RecursiveBacktrackingGenerator() {
        this.random = new Random();
    }

    /**
     * Creates a new recursive backtracking generator with a specified seed.
     *
     * @param seed the random seed for reproducible maze generation
     */
    public RecursiveBacktrackingGenerator(long seed) {
        // This constructor is used to assist in testing by providing a fixed random seed.
        this.random = new Random(seed);
    }

    @Override
    public String getName() {
        return "Recursive Backtracking";
    }

    @Override
    public void generate(Maze maze, int startRow, int startCol) {
        if (startRow < 0 || startRow >= maze.getHeight() || startCol < 0
                || startCol >= maze.getWidth()) {
            throw new IndexOutOfBoundsException("Start position out of bounds");
        }

        initializeMazeAsWalls(maze);
        maze.resetVisited();
        carve(maze, startRow, startCol);
        maze.resetVisited();
    }

    /**
     * Helper method to initialize the maze by marking all cells as walls.
     * This creates the initial state before the carving algorithm begins.
     *
     * @param maze the maze to initialize
     */
    private void initializeMazeAsWalls(Maze maze) {
        for (int r = 0; r < maze.getHeight(); r++) {
            for (int c = 0; c < maze.getWidth(); c++) {
                maze.setWall(r, c, true);
            }
        }
    }

    /**
     * Recursively carves passages starting from the given cell coordinates.
     * This is the core recursive method that implements the backtracking algorithm.
     *
     * @param maze the maze being generated
     * @param r the row index of the current cell
     * @param c the column index of the current cell
     */
    private void carve(Maze maze, int r, int c) {
        MazeCell current = maze.getCell(r, c);
        current.setVisited(true);
        maze.setWall(r, c, false);
        List<int[]> neighbors = getUnvisitedNeighbors(maze, r, c);
        shuffle(neighbors);
        for (int[] neighbor : neighbors) {
            int nr = neighbor[0];
            int nc = neighbor[1];
            MazeCell neighborCell = maze.getCell(nr, nc);
            if (!neighborCell.isVisited()) {
                int betweenCellr = r + (nr - r) / 2;
                int betweenCellc = c + (nc - c) / 2;
                maze.setWall(betweenCellr, betweenCellc, false);
                carve(maze, nr, nc);
            }
        }
    }

    /**
     * Returns all unvisited neighbors of the cell at (r, c) that are two steps away
     * in the cardinal directions (north, south, east, west).
     *
     * This method is key to the "move 2 steps" approach: it looks for neighbors
     * that are 2 cells away, which ensures there's always a wall cell in between
     * that can be carved out to create a passage.
     *
     * @param maze the maze being generated
     * @param r the row index of the current cell
     * @param c the column index of the current cell
     * @return a list of coordinate pairs [row, col] for each valid unvisited neighbor
     */
    private List<int[]> getUnvisitedNeighbors(Maze maze, int r, int c) {
        List<int[]> neighbors = new ArrayList<>();
        if (r - 2 >= 0) {
            MazeCell north = maze.getCell(r - 2, c);
            if (!north.isVisited()) {
                neighbors.add(new int[] {r - 2, c});
            }
        }
        if (r + 2 < maze.getHeight()) {
            MazeCell south = maze.getCell(r + 2, c);
            if (!south.isVisited()) {
                neighbors.add(new int[] {r + 2, c});
            }
        }

        if (c - 2 >= 0) {
            MazeCell west = maze.getCell(r, c - 2);
            if (!west.isVisited()) {
                neighbors.add(new int[] {r, c - 2});
            }
        }

        if (c + 2 < maze.getWidth()) {
            MazeCell east = maze.getCell(r, c + 2);
            if (!east.isVisited()) {
                neighbors.add(new int[] {r, c + 2});
            }
        }
        return neighbors;
    }

    /**
     * Helper method to shuffle a list of coordinate pairs randomly.
     * This ensures that the maze generation is randomized and produces
     * different mazes on each run.
     *
     * @param coords a list of coordinate pairs to shuffle in place
     */
    private void shuffle(List<int[]> coords) {
        Collections.shuffle(coords, random);
    }
}
