package io.github.ninali927.maze.traversal;

import io.github.ninali927.maze.structures.ArrayMaze;
import io.github.ninali927.maze.structures.Maze;
import io.github.ninali927.maze.structures.MazeCell;
import io.github.ninali927.maze.traversal.RecursivePathFinder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for RecursivePathFinder.
 */
public class RecursivePathFinderTest {

    @Test
    public void testFindPathInStraightLine() {
        Maze maze = new ArrayMaze(5, 1); // 5x1 corridor
        RecursivePathFinder finder = new RecursivePathFinder(maze);

        MazeCell start = maze.getCell(0, 0);
        MazeCell goal = maze.getCell(0, 4);

        List<MazeCell> path = finder.findPath(start, goal);

        assertEquals(5, path.size());
        assertEquals(start, path.get(0));
        assertEquals(goal, path.get(4));
    }

    @Test
    public void testFindPathStartEqualsGoal() {
        Maze maze = new ArrayMaze(3, 3);
        RecursivePathFinder finder = new RecursivePathFinder(maze);

        MazeCell start = maze.getCell(1, 1);

        List<MazeCell> path = finder.findPath(start, start);

        assertEquals(1, path.size());
        assertEquals(start, path.get(0));
    }

    @Test
    public void testFindAllPathsSimpleMaze() {
        // Create a small maze with multiple paths
        Maze maze = new ArrayMaze(2, 2); // 2x2 grid, fully connected

        RecursivePathFinder finder = new RecursivePathFinder(maze);

        MazeCell start = maze.getCell(0, 0);
        MazeCell goal = maze.getCell(1, 1);

        List<List<MazeCell>> allPaths = finder.findAllPaths(start, goal);

        assertTrue(allPaths.size() >= 2, "Should find multiple paths in a 2x2 grid");

        // All paths should start with start and end with goal
        for (List<MazeCell> path : allPaths) {
            assertEquals(start, path.get(0));
            assertEquals(goal, path.get(path.size() - 1));
        }
    }

    @Test
    public void testPathFinderDoesNotModifyMaze() {
        Maze maze = new ArrayMaze(4, 4);
        maze.setWall(1, 1, true);
        maze.setWall(2, 2, true);
        RecursivePathFinder finder = new RecursivePathFinder(maze);

        boolean[][] preState = MazeTraversalTest.getMazeWallState(maze);

        finder.findPath(maze.getCell(0, 0), maze.getCell(3, 3));
        finder.findAllPaths(maze.getCell(0, 0), maze.getCell(3, 3));

        boolean[][] postState = MazeTraversalTest.getMazeWallState(maze);

        assertArrayEquals(preState, postState,
                "Pathfinding should not modify the maze wall structure.");
    }
}
