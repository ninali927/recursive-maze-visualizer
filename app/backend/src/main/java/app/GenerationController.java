package app;

import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import io.github.ninali927.maze.structures.Maze;

import java.util.function.Consumer;

/**
 * Controller for handling maze generation visualization.
 */
public class GenerationController {
    private static final Gson gson = new Gson();
    private Maze maze;

    public void attachMaze(Maze maze) {
        this.maze = maze;
    }

    public void handleGenerationMessage(WsContext ctx, AlgorithmMessage message) {
        if (maze == null) {
            ctx.send(gson.toJson(new AlgorithmMessage("error", "Create a maze first")));
            return;
        }

        if ("run_generation".equals(message.type)) {
            runGeneration(ctx);
        }
    }

    private void runGeneration(WsContext ctx) {
        // Run the generation in a background thread to not block the server
        new Thread(() -> {
            try {
                // Consumer to send step-by-step updates to the frontend
                Consumer<AlgorithmMessage> onStep = (stepMessage) -> {
                    if (ctx.session.isOpen()) {
                        ctx.send(gson.toJson(stepMessage));
                        // Add a small delay to make the visualization watchable
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                };

                // Create the visualizing wrapper for the generator
                VisualizingRecursiveBacktrackingGenerator generator =
                    new VisualizingRecursiveBacktrackingGenerator(onStep);

                ctx.send(gson.toJson(new AlgorithmMessage("generation_started", "Maze generation started...")));

                // Get start coordinates from the maze (or default to 0,0)
                int startRow = 0; // You could pass this from the frontend if needed
                int startCol = 0;

                // Run the generator
                generator.generate(maze, startRow, startCol);

                // Send the final maze state upon completion
                if (ctx.session.isOpen()) {
                    AlgorithmMessage completeMessage = new AlgorithmMessage("generation_complete", "Maze generation finished!");
                    java.util.Map<String, Object> data = new java.util.HashMap<>();
                    data.put("finalMaze", maze);
                    completeMessage.data = data;
                    ctx.send(gson.toJson(completeMessage));
                }

            } catch (UnsupportedOperationException e) {
                if (ctx.session.isOpen()) {
                    ctx.send(gson.toJson(new AlgorithmMessage("error", "Not implemented: " + e.getMessage())));
                }
            } catch (Exception e) {
                if (ctx.session.isOpen()) {
                    ctx.send(gson.toJson(new AlgorithmMessage("error", "An error occurred during generation: " + e.getMessage())));
                }
                e.printStackTrace();
            }
        }).start();
    }
}
