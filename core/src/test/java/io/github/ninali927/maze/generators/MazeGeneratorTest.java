package io.github.ninali927.maze.generators;

import io.github.ninali927.maze.structures.ArrayMaze;
import io.github.ninali927.maze.structures.Maze;
import io.github.ninali927.maze.structures.MazeCell;
import io.github.ninali927.maze.generators.MazeGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Abstract base test class for MazeGenerator implementations.
 * 
 * Concrete test classes should extend this and implement makeGenerator().
 */
public abstract class MazeGeneratorTest {

    /**
     * Factory method that concrete test classes must implement.
     *
     * @return a new instance of the generator to test
     */
    protected abstract MazeGenerator makeGenerator();

    @Test
    public void testGeneratorHasName() {
        MazeGenerator generator = makeGenerator();
        assertNotNull(generator.getName());
        assertFalse(generator.getName().isEmpty());
    }

    @Test
    public void testGenerateCreatesOpenCells() {
        MazeGenerator generator = makeGenerator();
        Maze maze = new ArrayMaze(5, 5);

        // Initialize all as walls
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                maze.setWall(r, c, true);
            }
        }

        generator.generate(maze, 0, 0);

        // After generation, at least the starting cell should be open
        MazeCell start = maze.getCell(0, 0);
        assertFalse(start.isWall(), "Starting cell should be open after generation");
    }

    @Test
    public void testGeneratedMazeIsConnected() {
        MazeGenerator generator = makeGenerator();
        Maze maze = new ArrayMaze(10, 10);

        // Initialize all as walls
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                maze.setWall(r, c, true);
            }
        }

        generator.generate(maze, 0, 0);

        // Find all open cells
        Set<MazeCell> openCells = new HashSet<>();
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                MazeCell cell = maze.getCell(r, c);
                if (!cell.isWall()) {
                    openCells.add(cell);
                }
            }
        }

        assertTrue(openCells.size() > 0, "Maze should have at least one open cell");

        // Check that all open cells are reachable from the starting cell
        Set<MazeCell> reachable = findReachableCells(maze, maze.getCell(0, 0));

        assertEquals(openCells.size(), reachable.size(),
                "All open cells should be reachable from the starting cell (maze should be fully connected)");
    }

    @Test
    public void testGenerateFromDifferentStartPosition() {
        MazeGenerator generator = makeGenerator();
        Maze maze = new ArrayMaze(7, 7);

        // Initialize all as walls
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                maze.setWall(r, c, true);
            }
        }

        generator.generate(maze, 3, 3);

        MazeCell start = maze.getCell(3, 3);
        assertFalse(start.isWall(), "Starting cell should be open");

        // Check connectivity from this starting position
        Set<MazeCell> openCells = new HashSet<>();
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                MazeCell cell = maze.getCell(r, c);
                if (!cell.isWall()) {
                    openCells.add(cell);
                }
            }
        }

        Set<MazeCell> reachable = findReachableCells(maze, start);
        assertEquals(openCells.size(), reachable.size(),
                "All open cells should be reachable from the starting cell");
    }

    @Test
    public void testSmallMazeGeneration() {
        MazeGenerator generator = makeGenerator();
        Maze maze = new ArrayMaze(3, 3);

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                maze.setWall(r, c, true);
            }
        }

        generator.generate(maze, 0, 0);

        // Count open cells
        int openCount = 0;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (!maze.getCell(r, c).isWall()) {
                    openCount++;
                }
            }
        }

        assertTrue(openCount > 0, "Small maze should have at least some open cells");
    }

    @Test
    public void testLargeMazeGeneration() {
        MazeGenerator generator = makeGenerator();
        Maze maze = new ArrayMaze(20, 20);

        for (int r = 0; r < 20; r++) {
            for (int c = 0; c < 20; c++) {
                maze.setWall(r, c, true);
            }
        }

        // This should not crash or run out of stack space
        assertDoesNotThrow(() -> generator.generate(maze, 0, 0));

        // Verify it created a connected maze
        Set<MazeCell> openCells = new HashSet<>();
        for (int r = 0; r < 20; r++) {
            for (int c = 0; c < 20; c++) {
                MazeCell cell = maze.getCell(r, c);
                if (!cell.isWall()) {
                    openCells.add(cell);
                }
            }
        }

        Set<MazeCell> reachable = findReachableCells(maze, maze.getCell(0, 0));
        assertEquals(openCells.size(), reachable.size(),
                "Large maze should still be fully connected");
    }

    // Helper method to find all cells reachable from a start cell using BFS
    private Set<MazeCell> findReachableCells(Maze maze, MazeCell start) {
        Set<MazeCell> visited = new HashSet<>();
        Queue<MazeCell> queue = new LinkedList<>();

        if (start.isWall()) {
            return visited;
        }

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            MazeCell current = queue.poll();
            for (MazeCell neighbor : maze.getNeighbors(current)) {
                if (!visited.contains(neighbor) && !neighbor.isWall()) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        return visited;
    }

    @Test
    public void testGeneratorThrowsOnInvalidStart() {
        MazeGenerator generator = makeGenerator();
        Maze maze = new ArrayMaze(5, 5);
        assertThrows(IndexOutOfBoundsException.class, () -> generator.generate(maze, -1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> generator.generate(maze, 0, -1));
        assertThrows(IndexOutOfBoundsException.class, () -> generator.generate(maze, 5, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> generator.generate(maze, 0, 5));
    }

    @Test
    public void testGeneratorProducesPassages() {
        MazeGenerator generator = makeGenerator();
        Maze maze = new ArrayMaze(5, 5);
        generator.generate(maze, 0, 0);

        int wallCount = 0;
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                if (maze.isWall(r, c)) {
                    wallCount++;
                }
            }
        }
        assertTrue(wallCount < 25,
                "Generator should carve at least one passage besides the start cell.");
    }

    @Test
    public void testGeneratesDifferentMazesWithoutSeed() {
        MazeGenerator generator1 = makeGenerator();
        MazeGenerator generator2 = makeGenerator();

        Maze maze1 = new ArrayMaze(11, 11);
        generator1.generate(maze1, 0, 0);

        Maze maze2 = new ArrayMaze(11, 11);
        generator2.generate(maze2, 0, 0);

        // It's technically possible for two random mazes to be identical, but for an 11x11 maze,
        // the probability is astronomically low. This is a reliable test for randomness.
        assertFalse(areMazesIdentical(maze1, maze2),
                "Two separate generations should produce different mazes.");
    }

    // Helper to compare two mazes
    protected boolean areMazesIdentical(Maze maze1, Maze maze2) {
        if (maze1.getWidth() != maze2.getWidth() || maze1.getHeight() != maze2.getHeight()) {
            return false;
        }
        for (int r = 0; r < maze1.getHeight(); r++) {
            for (int c = 0; c < maze1.getWidth(); c++) {
                if (maze1.isWall(r, c) != maze2.isWall(r, c)) {
                    return false;
                }
            }
        }
        return true;
    }
}
