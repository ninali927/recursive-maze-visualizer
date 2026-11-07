package app;

import io.github.ninali927.maze.structures.Maze;
import io.github.ninali927.maze.structures.MazeCell;
import io.github.ninali927.maze.traversal.RecursivePathFinder;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A wrapper for RecursivePathFinder that visualizes the process.
 */
public class VisualizingRecursivePathFinder extends RecursivePathFinder {

    private final Consumer<AlgorithmMessage> onStep;
    private int visitedCount;
    private int maxDepth;

    public VisualizingRecursivePathFinder(Maze maze, Consumer<AlgorithmMessage> onStep) {
        super(maze);
        this.onStep = onStep;
    }

    @Override
    public List<MazeCell> findPath(MazeCell start, MazeCell goal) {
        getMaze().resetVisited();
        List<MazeCell> path = new java.util.ArrayList<>();
        this.visitedCount = 0;
        this.maxDepth = 0;
        findPathHelper(start, goal, path, 1); // Start with depth 1
        return path;
    }

    private boolean findPathHelper(MazeCell current, MazeCell goal, List<MazeCell> path, int depth) {
        path.add(current);
        current.setVisited(true);

        // Update metrics
        this.visitedCount++;
        if (depth > this.maxDepth) {
            this.maxDepth = depth;
        }

        sendStepUpdate(current, "step", depth);

        if (current.equals(goal)) {
            return true; // Path found
        }

        List<MazeCell> neighbors = getMaze().getNeighbors(current);
        for (MazeCell neighbor : neighbors) {
            if (!neighbor.isVisited()) {
                if (findPathHelper(neighbor, goal, path, depth + 1)) {
                    return true;
                }
            }
        }

        // Backtrack
        path.remove(path.size() - 1);
        sendStepUpdate(current, "backtrack", depth);
        return false;
    }

    private void sendStepUpdate(MazeCell cell, String type, int depth) {
        AlgorithmMessage stepMessage = new AlgorithmMessage("pathfinding_" + type, "Pathfinding update");
        Map<String, Object> data = ReflectionHelper.getCellData(cell);
        data.put("visitedCount", this.visitedCount);
        data.put("recursionDepth", depth);
        data.put("maxDepth", this.maxDepth);
        stepMessage.data = data;
        onStep.accept(stepMessage);
    }

    // Private helper to get the maze field from the superclass
    private Maze getMaze() {
        try {
            java.lang.reflect.Field mazeField = RecursivePathFinder.class.getDeclaredField("maze");
            mazeField.setAccessible(true);
            return (Maze) mazeField.get(this);
        } catch (Exception e) {
            throw new RuntimeException("Could not get maze from superclass", e);
        }
    }
}
