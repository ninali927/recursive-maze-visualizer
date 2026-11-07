package app;

import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import io.github.ninali927.maze.structures.Maze;
import io.github.ninali927.maze.traversal.MazeTraversal;

import java.util.function.Consumer;

/**
 * Controller for handling maze traversal visualizations.
 */
public class TraversalController {
    private static final Gson gson = new Gson();
    private Maze maze;

    public void attachMaze(Maze maze) {
        this.maze = maze;
    }

    public void handleTraversalMessage(WsContext ctx, AlgorithmMessage message) {
        if (maze == null) {
            ctx.send(gson.toJson(new AlgorithmMessage("error", "Create a maze first")));
            return;
        }

        if ("run_traversal".equals(message.type)) {
            String strategy = (String) message.data.get("strategy");
            runTraversal(ctx, strategy);
        }
    }

    private void runTraversal(WsContext ctx, String strategy) {
        new Thread(() -> {
            try {
                Consumer<AlgorithmMessage> onStep = (stepMessage) -> {
                    if (ctx.session.isOpen()) {
                        ctx.send(gson.toJson(stepMessage));
                        try {
                            Thread.sleep(75); // Slow down traversal visualization
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                };

                MazeTraversal traversal;
                if ("preorder".equals(strategy)) {
                    traversal = new VisualizingPreorderTraversal(onStep);
                } else if ("postorder".equals(strategy)) {
                    traversal = new VisualizingPostorderTraversal(onStep);
                } else {
                    throw new IllegalArgumentException("Unknown traversal strategy: " + strategy);
                }

                ctx.send(gson.toJson(new AlgorithmMessage("traversal_started", "Traversal started: " + strategy)));
                maze.resetVisited();

                traversal.traverse(maze, 0, 0); // Assuming start at 0,0

                if (ctx.session.isOpen()) {
                    ctx.send(gson.toJson(new AlgorithmMessage("traversal_complete", "Traversal finished!")));
                }

            } catch (UnsupportedOperationException e) {
                if (ctx.session.isOpen()) {
                    ctx.send(gson.toJson(new AlgorithmMessage("error", "Not implemented: " + e.getMessage())));
                }
            } catch (Exception e) {
                if (ctx.session.isOpen()) {
                    ctx.send(gson.toJson(new AlgorithmMessage("error", "An error occurred during traversal: " + e.getMessage())));
                }
                e.printStackTrace();
            }
        }).start();
    }
}
