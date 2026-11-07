package io.github.ninali927.maze.traversal;

import io.github.ninali927.maze.structures.ArrayMaze;
import io.github.ninali927.maze.structures.Maze;
import io.github.ninali927.maze.structures.MazeCell;
import io.github.ninali927.maze.traversal.MazeTraversal;
import io.github.ninali927.maze.traversal.PreorderTraversal;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Concrete test class for PreorderTraversal.
 */
public class PreorderTraversalTest extends MazeTraversalTest {

    @Override
    protected MazeTraversal makeTraversal() {
        return new PreorderTraversal();
    }

    @Test
    public void testPreorderHorizontalCorridor() {
        MazeTraversal traversal = makeTraversal();
        Maze maze = new ArrayMaze(5, 1); // 1 row x 5 cols
        List<MazeCell> result = traversal.traverse(maze, 0, 0);
        assertEquals(5, result.size());
        // (0,0), then (0,1), then (0,2), then (0,3), then (0,4)
        for (int i = 0; i < 5; i++) {
            assertEquals(maze.getCell(0, i), result.get(i));
        }
    }

    @Test
    public void testPreorderVerticalCorridor() {
        MazeTraversal traversal = makeTraversal();
        Maze maze = new ArrayMaze(1, 5); // 5 rows x 1 col
        List<MazeCell> result = traversal.traverse(maze, 0, 0);
        assertEquals(5, result.size());
        // (0,0), then (1,0), then (2,0), then (3,0), then (4,0)
        for (int i = 0; i < 5; i++) {
            assertEquals(maze.getCell(i, 0), result.get(i));
        }
    }

    @Test
    public void testPreorderOrdering() {
        //   O O O
        //   O O O
        // Expected (0,0), (1,0), (1,1), (0,1), (0,2), (1,2)
        MazeTraversal traversal = makeTraversal();
        Maze maze = new ArrayMaze(3, 2); // 2 rows x 3 cols
        List<MazeCell> result = traversal.traverse(maze, 0, 0);

        assertEquals(6, result.size(), "Should visit all 6 cells in preorder order.");
        assertEquals(maze.getCell(0, 0), result.get(0));
        assertEquals(maze.getCell(1, 0), result.get(1));
        assertEquals(maze.getCell(1, 1), result.get(2));
        assertEquals(maze.getCell(0, 1), result.get(3));
        assertEquals(maze.getCell(0, 2), result.get(4));
        assertEquals(maze.getCell(1, 2), result.get(5));
    }

    @Test
    public void testPreorderLShapedPath() {
        MazeTraversal traversal = makeTraversal();
        Maze maze = new ArrayMaze(3, 3);
        //   O W W
        //   O W W
        //   O O O
        maze.setWall(0, 1, true);
        maze.setWall(0, 2, true);
        maze.setWall(1, 1, true);
        maze.setWall(1, 2, true);

        List<MazeCell> result = traversal.traverse(maze, 0, 0);
        // Expected: (0,0), (1,0), (2,0), (2,1), (2,2)
        assertEquals(5, result.size(), "Should visit all open cells in L shape path.");

        assertEquals(maze.getCell(0, 0), result.get(0));
        assertEquals(maze.getCell(1, 0), result.get(1));
        assertEquals(maze.getCell(2, 0), result.get(2));
        assertEquals(maze.getCell(2, 1), result.get(3));
        assertEquals(maze.getCell(2, 2), result.get(4));
    }

    @Test
    public void testPreorderPlusShapedPath() {
        MazeTraversal traversal = makeTraversal();
        Maze maze = new ArrayMaze(3, 3);
        //   W O W
        //   O O O
        //   W O W
        maze.setWall(0, 0, true);
        maze.setWall(0, 2, true);
        maze.setWall(2, 0, true);
        maze.setWall(2, 2, true);

        List<MazeCell> result = traversal.traverse(maze, 1, 1);
        // Expected: (1,1), (0,1), (2,1), (1,2), (1,0)
        assertEquals(5, result.size(), "Should visit center and four open neighbors.");

        assertEquals(maze.getCell(1, 1), result.get(0));
        assertEquals(maze.getCell(0, 1), result.get(1));
        assertEquals(maze.getCell(2, 1), result.get(2));
        assertEquals(maze.getCell(1, 2), result.get(3));
        assertEquals(maze.getCell(1, 0), result.get(4));
    }

}
