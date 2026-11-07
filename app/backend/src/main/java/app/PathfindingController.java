package app;

import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import io.github.ninali927.maze.structures.Maze;
import io.github.ninali927.maze.structures.MazeCell;
import io.github.ninali927.maze.traversal.RecursivePathFinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Controller for handling pathfinding visualizations.
 */
public class PathfindingController {
    private static final Gson gson = new Gson();
    private Maze maze;

    public void attachMaze(Maze maze) {
        this.maze = maze;
    }

    public void handlePathfindingMessage(WsContext ctx, AlgorithmMessage message) {
        if (maze == null) {
            ctx.send(gson.toJson(new AlgorithmMessage("error", "Create and generate a maze first")));
            return;
        }

        if ("run_pathfinding".equals(message.type)) {
            runPathfinding(ctx, message);
        }
    }

    private void runPathfinding(WsContext ctx, AlgorithmMessage message) {
        new Thread(() -> {
            try {
                Consumer<AlgorithmMessage> onStep = (stepMessage) -> {
                    if (ctx.session.isOpen()) {
                        ctx.send(gson.toJson(stepMessage));
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                };

                VisualizingRecursivePathFinder pathFinder = new VisualizingRecursivePathFinder(maze, onStep);

                ctx.send(gson.toJson(new AlgorithmMessage("pathfinding_started", "Pathfinding started...")));
                maze.resetVisited();

                // Get start/goal from the message, with defaults
                int startRow = ((Number) message.data.getOrDefault("startRow", 0)).intValue();
                int startCol = ((Number) message.data.getOrDefault("startCol", 0)).intValue();
                int goalRow = ((Number) message.data.getOrDefault("goalRow", maze.getHeight() - 1)).intValue();
                int goalCol = ((Number) message.data.getOrDefault("goalCol", maze.getWidth() - 1)).intValue();

                MazeCell start = maze.getCell(startRow, startCol);
                MazeCell goal = maze.getCell(goalRow, goalCol);

                List<MazeCell> path = pathFinder.findPath(start, goal);

                if (ctx.session.isOpen()) {
                    AlgorithmMessage completeMessage = new AlgorithmMessage("pathfinding_complete", "Pathfinding finished!");
                    Map<String, Object> data = new HashMap<>();
                    data.put("found", !path.isEmpty());
                    List<int[]> pathCoords = new ArrayList<>();
                    for (MazeCell cell : path) {
                        pathCoords.add(new int[]{(int)ReflectionHelper.getFieldValue(cell, "row"), (int)ReflectionHelper.getFieldValue(cell, "col")});
                    }
                    data.put("path", pathCoords);
                    data.put("pathLength", path.size());
                    completeMessage.data = data;
                    ctx.send(gson.toJson(completeMessage));
                }

            } catch (Exception e) {
                if (ctx.session.isOpen()) {
                    ctx.send(gson.toJson(new AlgorithmMessage("error", "An error occurred during pathfinding: " + e.getMessage())));
                }
                e.printStackTrace();
            }
        }).start();
    }
}
