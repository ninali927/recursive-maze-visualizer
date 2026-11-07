package io.github.ninali927.maze.traversal;

import io.github.ninali927.maze.structures.ArrayMaze;
import io.github.ninali927.maze.structures.Maze;
import io.github.ninali927.maze.structures.MazeCell;
import io.github.ninali927.maze.traversal.MazeTraversal;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Abstract base test class for MazeTraversal implementations.
 *
 * Concrete test classes should extend this and implement makeTraversal().
 */
public abstract class MazeTraversalTest {

    /**
     * Factory method that concrete test classes must implement.
     *
     * @return a new instance of the traversal strategy to test
     */
    protected abstract MazeTraversal makeTraversal();

    @Test
    public void testTraversalHasName() {
        MazeTraversal traversal = makeTraversal();
        assertNotNull(traversal.getName());
        assertFalse(traversal.getName().isEmpty());
    }

    @Test
    public void testTraverseSingleCell() {
        MazeTraversal traversal = makeTraversal();
        Maze maze = new ArrayMaze(1, 1);

        List<MazeCell> result = traversal.traverse(maze, 0, 0);

        assertEquals(1, result.size());
        assertEquals(maze.getCell(0, 0), result.get(0));
    }

    @Test
    public void testTraverseLinearPath() {
        MazeTraversal traversal = makeTraversal();
        Maze maze = new ArrayMaze(5, 1); // 5x1 horizontal corridor

        // All cells are open by default
        List<MazeCell> result = traversal.traverse(maze, 0, 0);

        assertEquals(5, result.size());
        // Should visit all cells
        Set<MazeCell> visitedSet = new HashSet<>(result);
        assertEquals(5, visitedSet.size(), "Should visit each cell exactly once");
    }

    @Test
    public void testTraverseWithWalls() {
        MazeTraversal traversal = makeTraversal();
        Maze maze = new ArrayMaze(3, 3);

        // Create a simple maze:
        //   O O W
        //   O W W
        //   O O O
        // Where O = open, W = wall
        maze.setWall(0, 2, true);
        maze.setWall(1, 1, true);
        maze.setWall(1, 2, true);

        List<MazeCell> result = traversal.traverse(maze, 0, 0);

        // Should visit 6 open cells
        assertEquals(6, result.size());
        Set<MazeCell> visitedSet = new HashSet<>(result);
        assertEquals(6, visitedSet.size(), "Should visit each open cell exactly once");
    }

    @Test
    public void testTraversalDoesNotModifyMazeState() {
        MazeTraversal traversal = makeTraversal();
        Maze maze = new ArrayMaze(3, 3);
        maze.setWall(1, 1, true);

        // Capture pre-traversal state
        boolean[][] preState = getMazeWallState(maze);

        traversal.traverse(maze, 0, 0);

        // Capture post-traversal state
        boolean[][] postState = getMazeWallState(maze);

        assertArrayEquals(preState, postState,
                "Traversal should not modify the maze wall structure.");
    }

    /**
     * Helper method to get the wall state of the maze.
     * @param maze the maze to inspect
     * @return a 2D boolean array representing wall states
     */
    public static boolean[][] getMazeWallState(Maze maze) {
        boolean[][] state = new boolean[maze.getHeight()][maze.getWidth()];
        for (int r = 0; r < maze.getHeight(); r++) {
            for (int c = 0; c < maze.getWidth(); c++) {
                state[r][c] = maze.isWall(r, c);
            }
        }
        return state;
    }

    //edge case
    @Test
    public void testStartIsWall() {
        MazeTraversal traversal = makeTraversal();
        Maze maze = new ArrayMaze(3, 3);
        maze.setWall(0, 0, true);

        List<MazeCell> result = traversal.traverse(maze, 0, 0);
        assertEquals(0, result.size(), "Starting from a wall can only give an empty path.");
    }

    //edge case
    @Test
    public void testAllWalls() {
        MazeTraversal traversal = makeTraversal();
        Maze maze = new ArrayMaze(3, 3);
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                maze.setWall(r, c, true);
            }
        }

        List<MazeCell> result = traversal.traverse(maze, 0, 0);
        assertEquals(0, result.size(), "Traversal on an all-wall maze gives an empty path.");
    }

    //different starting positions - middle
    @Test
    public void testTraverseFromMiddle() {
        MazeTraversal traversal = makeTraversal();
        Maze maze = new ArrayMaze(3, 3);
        List<MazeCell> result = traversal.traverse(maze, 1, 1);
        assertEquals(9, result.size(), "Should visit all 9 cells from the center");
        Set<MazeCell> visitedSet = new HashSet<>(result);
        assertEquals(9, visitedSet.size(), "Should visit all 9 cells exactly once");
    }

    //different starting position - corner
    @Test
    public void testTraversalFromCorner() {
        MazeTraversal traversal = makeTraversal();
        Maze maze = new ArrayMaze(4, 4); // 4x4 open maze

        List<MazeCell> topLeft = traversal.traverse(maze, 0, 0);
        List<MazeCell> topRight = traversal.traverse(maze, 0, 3);
        List<MazeCell> bottomLeft = traversal.traverse(maze, 3, 0);
        List<MazeCell> bottomRight = traversal.traverse(maze, 3, 3);

        // Expect all 16 open cells to be visited for each case
        assertEquals(16, new HashSet<>(topLeft).size(),
                "Traversal from top-left corner should visit all cells.");
        assertEquals(16, new HashSet<>(topRight).size(),
                "Traversal from top-right corner should visit all cells.");
        assertEquals(16, new HashSet<>(bottomLeft).size(),
                "Traversal from bottom-left corner should visit all cells.");
        assertEquals(16, new HashSet<>(bottomRight).size(),
                "Traversal from bottom-right corner should visit all cells.");
    }

    //disconnected region
    @Test
    public void testDisconnectedRegionsTraverse() {
        MazeTraversal traversal = makeTraversal();
        Maze maze = new ArrayMaze(3, 4); // 4 rows x 3 cols
        // Create a wall that separates the maze into two regions:
        //   O O O
        //   O O O
        //   W W W  
        //   O O O
        maze.setWall(2, 0, true);
        maze.setWall(2, 1, true);
        maze.setWall(2, 2, true);

        List<MazeCell> result1 = traversal.traverse(maze, 0, 0);
        Set<MazeCell> visitedSet1 = new HashSet<>(result1);
        assertEquals(6, visitedSet1.size(), "Should only visit connected region");

        List<MazeCell> result2 = traversal.traverse(maze, 3, 0);
        Set<MazeCell> visitedSet2 = new HashSet<>(result2);
        assertEquals(3, visitedSet2.size(), "Should only visit connected region");
    }

    //large maze
    @Test
    public void testLargeMazeTraverse() {
        MazeTraversal traversal = makeTraversal();
        Maze maze = new ArrayMaze(10, 10);
        List<MazeCell> result = traversal.traverse(maze, 0, 0);
        Set<MazeCell> visitedSet = new HashSet<>(result);
        assertEquals(100, visitedSet.size(), "Should visit all 100 cells exactly once");
    }

}
