package app;

import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import io.github.ninali927.maze.structures.ArrayMaze;
import io.github.ninali927.maze.structures.Maze;
import io.github.ninali927.maze.structures.MazeCell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for maze creation and editing.
 */
public class MazeController {
    private static final Gson gson = new Gson();

    private Maze maze; // Use the interface type

    Maze getMaze() {
        return maze;
    }

    public void handleMazeMessage(WsContext ctx, AlgorithmMessage message) {
        try {
            switch (message.type) {
                case "create_maze":
                    createMaze(ctx, message);
                    break;
                case "toggle_wall":
                    toggleWall(ctx, message);
                    break;
                case "reset_maze":
                    resetMaze(ctx);
                    break;
                case "get_maze_state":
                    sendMazeState(ctx);
                    break;
                default:
                    ctx.send(gson.toJson(new AlgorithmMessage("error", "Unknown maze command: " + message.type)));
            }
        } catch (UnsupportedOperationException e) {
            ctx.send(gson.toJson(new AlgorithmMessage("error", "Not implemented: " + e.getMessage())));
        } catch (Exception e) {
            ctx.send(gson.toJson(new AlgorithmMessage("error", "Maze error: " + e.getMessage())));
        }
    }

    private void createMaze(WsContext ctx, AlgorithmMessage message) {
        int width = ((Number) message.data.getOrDefault("width", 10)).intValue();
        int height = ((Number) message.data.getOrDefault("height", 10)).intValue();
        this.maze = new ArrayMaze(width, height);

        // The controller is responsible for setting start/goal, not the maze itself.
        // We can get these from the message now.
        int startRow = ((Number) message.data.getOrDefault("startRow", 0)).intValue();
        int startCol = ((Number) message.data.getOrDefault("startCol", 0)).intValue();
        int goalRow = ((Number) message.data.getOrDefault("goalRow", height - 1)).intValue();
        int goalCol = ((Number) message.data.getOrDefault("goalCol", width - 1)).intValue();

        AlgorithmMessage response = new AlgorithmMessage("maze_created", "Maze created");
        response.data = Map.of(
            "width", width,
            "height", height,
            "startRow", startRow,
            "startCol", startCol,
            "goalRow", goalRow,
            "goalCol", goalCol
        );
        ctx.send(gson.toJson(response));
    }

    private void toggleWall(WsContext ctx, AlgorithmMessage message) {
        if (maze == null) {
            ctx.send(gson.toJson(new AlgorithmMessage("error", "No maze created")));
            return;
        }
        int row = ((Number) message.data.get("row")).intValue();
        int col = ((Number) message.data.get("col")).intValue();
        boolean isWall = !maze.isWall(row, col);
        maze.setWall(row, col, isWall);

        AlgorithmMessage response = new AlgorithmMessage("wall_toggled", "Wall toggled");
        response.data = Map.of("row", row, "col", col, "isWall", isWall);
        ctx.send(gson.toJson(response));
    }

    private void resetMaze(WsContext ctx) {
        if (maze != null) {
            maze.resetVisited();
        }
        ctx.send(gson.toJson(new AlgorithmMessage("maze_reset", "Maze reset")));
    }

    private void sendMazeState(WsContext ctx) {
        if (maze == null) {
            ctx.send(gson.toJson(new AlgorithmMessage("error", "No maze created")));
            return;
        }
        Map<String, Object> state = new HashMap<>();
        state.put("width", maze.getWidth());
        state.put("height", maze.getHeight());
        List<Map<String, Object>> cells = new ArrayList<>();
        for (int r = 0; r < maze.getHeight(); r++) {
            for (int c = 0; c < maze.getWidth(); c++) {
                // getCell returns the interface, which is passed to the helper
                MazeCell cell = maze.getCell(r, c);
                cells.add(ReflectionHelper.getCellData(cell));
            }
        }
        state.put("cells", cells);
        AlgorithmMessage response = new AlgorithmMessage("maze_state", "Maze state");
        response.data = state;
        ctx.send(gson.toJson(response));
    }
}