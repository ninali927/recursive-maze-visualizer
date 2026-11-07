package app;

import io.github.ninali927.maze.generators.RecursiveBacktrackingGenerator;
import io.github.ninali927.maze.structures.Maze;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A wrapper around RecursiveBacktrackingGenerator that sends
 * messages to the frontend to visualize the generation process step-by-step.
 */
public class VisualizingRecursiveBacktrackingGenerator extends RecursiveBacktrackingGenerator {

    private final Consumer<AlgorithmMessage> onStep;

    public VisualizingRecursiveBacktrackingGenerator(Consumer<AlgorithmMessage> onStep) {
        super(); // Assumes a default constructor exists
        this.onStep = onStep;
    }

    @Override
    public void generate(Maze maze, int startRow, int startCol) {
        // Initialize maze as walls
        for (int r = 0; r < maze.getHeight(); r++) {
            for (int c = 0; c < maze.getWidth(); c++) {
                maze.setWall(r, c, true);
            }
        }
        maze.resetVisited();
        carve(maze, startRow, startCol);
        maze.resetVisited();
    }

    private void carve(Maze maze, int r, int c) {
        // Mark cell as visited and make it a passage
        maze.getCell(r, c).setVisited(true);
        maze.getCell(r, c).setWall(false);
        sendStepUpdate(r, c, "passage");

        try {
            // Use reflection to call the private getUnvisitedNeighbors method
            Method getNeighborsMethod = RecursiveBacktrackingGenerator.class.getDeclaredMethod("getUnvisitedNeighbors", Maze.class, int.class, int.class);
            getNeighborsMethod.setAccessible(true);
            java.util.List<int[]> neighbors = (java.util.List<int[]>) getNeighborsMethod.invoke(this, maze, r, c);

            // Use reflection to call the private shuffle method
            Method shuffleMethod = RecursiveBacktrackingGenerator.class.getDeclaredMethod("shuffle", java.util.List.class);
            shuffleMethod.setAccessible(true);
            shuffleMethod.invoke(this, neighbors);

            for (int[] neighborCoords : neighbors) {
                int nr = neighborCoords[0];
                int nc = neighborCoords[1];

                if (!maze.getCell(nr, nc).isVisited()) {
                    // Remove wall between current and neighbor
                    int wallR = r + (nr - r) / 2;
                    int wallC = c + (nc - c) / 2;
                    maze.getCell(wallR, wallC).setWall(false);
                    sendStepUpdate(wallR, wallC, "passage");

                    // Recurse
                    carve(maze, nr, nc);
                }
            }
        } catch (Exception e) {
            // If reflection fails, it's a critical error in our visualization logic.
            e.printStackTrace();
            throw new RuntimeException("Failed to visualize generator due to reflection error", e);
        }
    }

    private void sendStepUpdate(int row, int col, String type) {
        AlgorithmMessage stepMessage = new AlgorithmMessage("generation_step", "Carving cell");
        Map<String, Object> data = new HashMap<>();
        data.put("row", row);
        data.put("col", col);
        data.put("type", type);
        stepMessage.data = data;
        onStep.accept(stepMessage);
    }
}