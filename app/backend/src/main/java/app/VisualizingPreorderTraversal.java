package app;

import io.github.ninali927.maze.structures.Maze;
import io.github.ninali927.maze.structures.MazeCell;
import io.github.ninali927.maze.traversal.PreorderTraversal;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A wrapper for PreorderTraversal that visualizes the process.
 */
public class VisualizingPreorderTraversal extends PreorderTraversal {

    private final Consumer<AlgorithmMessage> onStep;
    private int visitedCount;
    private int maxDepth;

    public VisualizingPreorderTraversal(Consumer<AlgorithmMessage> onStep) {
        this.onStep = onStep;
    }

    @Override
    public List<MazeCell> traverse(Maze maze, int startRow, int startCol) {
        // Override the main public method to start the visualization-enabled helper
        List<MazeCell> result = new java.util.ArrayList<>();
        maze.resetVisited();
        this.visitedCount = 0;
        this.maxDepth = 0;
        MazeCell start = maze.getCell(startRow, startCol);
        preorderHelper(maze, start, result, 1);
        return result;
    }

    private void preorderHelper(Maze maze, MazeCell cell, List<MazeCell> result, int depth) {
        if (cell == null || cell.isVisited()) {
            return;
        }

        cell.setVisited(true);
        result.add(cell);

        // Update metrics
        this.visitedCount++;
        if (depth > this.maxDepth) {
            this.maxDepth = depth;
        }
        sendStepUpdate(cell, depth);

        List<MazeCell> neighbors = maze.getNeighbors(cell);
        for (MazeCell neighbor : neighbors) {
            preorderHelper(maze, neighbor, result, depth + 1);
        }
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
