package io.github.ninali927.maze.structures;

import java.util.ArrayList;
import java.util.List;

/**
 * An array-based implementation of a rectangular maze.
 */
public class ArrayMaze implements Maze {
    private final int width;
    private final int height;
    private final ArrayMazeCell[][] grid;

    // A private, nested implementation of the MazeCell interface for the ArrayMaze.
    // This is a simple data object that holds the state for a single cell.
    private static class ArrayMazeCell implements MazeCell {
        private final int row;
        private final int col;
        private boolean isWall;
        private boolean isVisited;

        public ArrayMazeCell(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public boolean isWall() {
            return isWall;
        }

        @Override
        public void setWall(boolean isWall) {
            this.isWall = isWall;
        }

        @Override
        public boolean isVisited() {
            return isVisited;
        }

        @Override
        public void setVisited(boolean visited) {
            this.isVisited = visited;
        }
    }

    /**
     * Initializes a maze of the given width and height.
     * All cells are initially open (not walls) and unvisited.
     * 
     * @param width the number of columns in the maze, greater than zero
     * @param height the number of rows in the maze, greater than zero
     * @throws IllegalArgumentException if width or height is not positive
     */
    public ArrayMaze(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive");
        }
        this.width = width;
        this.height = height;
        this.grid = new ArrayMazeCell[height][width];

        // Initialize the grid with cell objects
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                grid[r][c] = new ArrayMazeCell(r, c);
            }
        }
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public MazeCell getCell(int row, int col) {
        validateBounds(row, col);
        return grid[row][col];
    }

    @Override
    public boolean isWall(int row, int col) {
        return getCell(row, col).isWall();
    }

    @Override
    public void setWall(int row, int col, boolean isWall) {
        getCell(row, col).setWall(isWall);
    }

    @Override
    public List<MazeCell> getNeighbors(MazeCell cell) {
        if (!(cell instanceof ArrayMazeCell)) {
            throw new IllegalArgumentException("Cell is not from this ArrayMaze implementation.");
        }
        ArrayMazeCell arrayCell = (ArrayMazeCell) cell;
        List<MazeCell> neighbors = new ArrayList<>(4);
        int r = arrayCell.row;
        int c = arrayCell.col;

        // North
        if (r > 0 && !grid[r - 1][c].isWall) {
            neighbors.add(grid[r - 1][c]);
        }
        // South
        if (r < height - 1 && !grid[r + 1][c].isWall) {
            neighbors.add(grid[r + 1][c]);
        }
        // East
        if (c < width - 1 && !grid[r][c + 1].isWall) {
            neighbors.add(grid[r][c + 1]);
        }
        // West
        if (c > 0 && !grid[r][c - 1].isWall) {
            neighbors.add(grid[r][c - 1]);
        }

        return neighbors;
    }

    @Override
    public void resetVisited() {
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                grid[r][c].setVisited(false);
            }
        }
    }

    // Validates that the given row and column are within the maze bounds.
    private void validateBounds(int row, int col) {
        if (row < 0 || row >= height || col < 0 || col >= width) {
            throw new IndexOutOfBoundsException("Cell out of bounds: (" + row + "," + col + ")");
        }
    }
}
