package app;

import io.github.ninali927.maze.structures.Maze;
import io.github.ninali927.maze.structures.MazeCell;
import io.github.ninali927.maze.traversal.PostorderTraversal;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A wrapper for PostorderTraversal that visualizes the process.
 */
public class VisualizingPostorderTraversal extends PostorderTraversal {

    private final Consumer<AlgorithmMessage> onStep;
    private int visitedCount;
    private int maxDepth;

    public VisualizingPostorderTraversal(Consumer<AlgorithmMessage> onStep) {
        this.onStep = onStep;
    }

    @Override
    public List<MazeCell> traverse(Maze maze, int startRow, int startCol) {
        List<MazeCell> result = new java.util.ArrayList<>();
        maze.resetVisited();
        this.visitedCount = 0;
        this.maxDepth = 0;
        MazeCell start = maze.getCell(startRow, startCol);
        postorderHelper(maze, start, result, 1);
        return result;
    }

    private void postorderHelper(Maze maze, MazeCell cell, List<MazeCell> result, int depth) {
        if (cell == null || cell.isVisited()) {
            return;
        }

        cell.setVisited(true);

        // Update metrics
        this.visitedCount++;
        if (depth > this.maxDepth) {
            this.maxDepth = depth;
        }

        List<MazeCell> neighbors = maze.getNeighbors(cell);
        for (MazeCell neighbor : neighbors) {
            postorderHelper(maze, neighbor, result, depth + 1);
        }

        result.add(cell);
        sendStepUpdate(cell, depth);
    }

    private void sendStepUpdate(MazeCell cell, int depth) {
        AlgorithmMessage stepMessage = new AlgorithmMessage("traversal_step", "Visiting cell");
        Map<String, Object> data = ReflectionHelper.getCellData(cell);
        data.put("visitedCount", this.visitedCount);
        data.put("recursionDepth", depth);
        data.put("maxDepth", this.maxDepth);
        stepMessage.data = data;
        onStep.accept(stepMessage);
    }
}
