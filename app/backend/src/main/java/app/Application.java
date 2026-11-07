package app;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

import io.javalin.Javalin;
import io.javalin.websocket.WsContext;

/**
 * Main application class that sets up the web server and WebSocket connections.
 */
public class Application {
    private static final int PORT = 7001;
    private static final Gson gson = new Gson();

    // Store WebSocket connections for real-time updates
    private static final Map<String, WsContext> connections = new ConcurrentHashMap<>();

    // Store controllers per session
    
    private static final Map<String, MazeController> mazeControllers = new ConcurrentHashMap<>();
    private static final Map<String, GenerationController> generationControllers = new ConcurrentHashMap<>();
    private static final Map<String, TraversalController> traversalControllers = new ConcurrentHashMap<>();
    private static final Map<String, PathfindingController> pathfindingControllers = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            // Serve static files from frontend directory
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/";
                staticFiles.directory = "../frontend";
                staticFiles.location = io.javalin.http.staticfiles.Location.EXTERNAL;
            });

            // Enable CORS for development - Javalin 6.x way
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(corsRule -> corsRule.anyHost());
            });
        }).start(PORT);

        // Serve the main page
        app.get("/", ctx -> ctx.redirect("/index.html"));

        // WebSocket endpoint for real-time algorithm visualization
        app.ws("/algorithm", ws -> {
            ws.onConnect(ctx -> {
                String sessionId = ctx.sessionId();
                connections.put(sessionId, ctx);
                
                System.out.println("Client connected: " + sessionId);

                // Initialize controllers for this session
                mazeControllers.put(sessionId, new MazeController());
                generationControllers.put(sessionId, new GenerationController());
                traversalControllers.put(sessionId, new TraversalController());
                pathfindingControllers.put(sessionId, new PathfindingController());

                // Send initial state
                AlgorithmMessage welcome = new AlgorithmMessage("connected", "Maze Runner Visualizer ready");
                welcome.data = Map.of(
                    "serverInfo", "Maze Runner Visualizer Backend",
                    "version", "3.0",
                    "features", Map.of("maze", true)
                );
                ctx.send(gson.toJson(welcome));
            });

            ws.onMessage(ctx -> {
                try {
                    AlgorithmMessage message = gson.fromJson(ctx.message(), AlgorithmMessage.class);
                    handleAlgorithmMessage(ctx, message);
                } catch (Exception e) {
                    System.err.println("Error processing message: " + e.getMessage());
                    ctx.send(gson.toJson(new AlgorithmMessage("error", "Invalid message format")));
                }
            });

            ws.onClose(ctx -> {
                String sessionId = ctx.sessionId();
                connections.remove(sessionId);
                
                mazeControllers.remove(sessionId);
                generationControllers.remove(sessionId);
                traversalControllers.remove(sessionId);
                pathfindingControllers.remove(sessionId);
                System.out.println("Client disconnected: " + sessionId);
            });
        });

        // Start periodic ping to all clients every 30 seconds
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            AlgorithmMessage ping = new AlgorithmMessage("ping", "keepalive");
            ping.timestamp = System.currentTimeMillis();
            String json = gson.toJson(ping);
            connections.values().forEach(ctx -> {
                if (ctx.session.isOpen()) {
                    ctx.send(json);
                }
            });
        }, 30, 30, TimeUnit.SECONDS);

        System.out.println("Server started on http://localhost:" + PORT);
        System.out.println("Maze Runner Visualizer - Open your browser to view the visualization");
        System.out.println("Features: Maze generation, pre/post-order traversal, recursive pathfinding");
    }

    /**
     * Handle incoming WebSocket messages from the frontend.
     * Routes messages to appropriate handlers based on message type.
     */
    private static void handleAlgorithmMessage(WsContext ctx, AlgorithmMessage message) {
        String sessionId = ctx.sessionId();

        // Route messages to appropriate controllers
        if (isMazeMessage(message.type)) {
            MazeController mazeController = mazeControllers.get(sessionId);
            if (mazeController == null) {
                ctx.send(gson.toJson(new AlgorithmMessage("error", "No maze controller found for session")));
                return;
            }
            mazeController.handleMazeMessage(ctx, message);
        } else if (isGenerationMessage(message.type)) {
            GenerationController generationController = generationControllers.get(sessionId);
            if (generationController == null) {
                ctx.send(gson.toJson(new AlgorithmMessage("error", "No generation controller found for session")));
                return;
            }
            // Attach the maze instance from the MazeController
            MazeController mazeController = mazeControllers.get(sessionId);
            if (mazeController != null) {
                generationController.attachMaze(mazeController.getMaze());
            }
            generationController.handleGenerationMessage(ctx, message);
        } else if (isTraversalMessage(message.type)) {
            TraversalController traversalController = traversalControllers.get(sessionId);
            if (traversalController == null) {
                ctx.send(gson.toJson(new AlgorithmMessage("error", "No traversal controller found for session")));
                return;
            }
            // Attach the maze instance from the MazeController
            MazeController mazeController = mazeControllers.get(sessionId);
            if (mazeController != null) {
                traversalController.attachMaze(mazeController.getMaze());
            }
            traversalController.handleTraversalMessage(ctx, message);
        } else if (isPathfindingMessage(message.type)) {
            PathfindingController pathfindingController = pathfindingControllers.get(sessionId);
            if (pathfindingController == null) {
                ctx.send(gson.toJson(new AlgorithmMessage("error", "No pathfinding controller found for session")));
                return;
            }
            // Attach the maze instance from the MazeController
            MazeController mazeController = mazeControllers.get(sessionId);
            if (mazeController != null) {
                pathfindingController.attachMaze(mazeController.getMaze());
            }
            pathfindingController.handlePathfindingMessage(ctx, message);
        } else {
            switch (message.type) {
                case "pong":
                    // Handle ping/pong keepalive - no action needed
                    System.out.println("Received pong from client: " + sessionId);
                    break;
                default:
                    ctx.send(gson.toJson(new AlgorithmMessage("error", "Unknown command: " + message.type)));
            }
        }
    }

    /**
     * Check if a message type is related to maze operations.
     */
    private static boolean isMazeMessage(String messageType) {
        return messageType.equals("create_maze") ||
               messageType.equals("toggle_wall") ||
               messageType.equals("reset_maze") ||
               messageType.equals("get_maze_state");
    }

    private static boolean isGenerationMessage(String messageType) {
        return messageType.equals("run_generation");
    }

    private static boolean isTraversalMessage(String messageType) {
        return messageType.equals("run_traversal");
    }

    private static boolean isPathfindingMessage(String messageType) {
        return messageType.equals("run_pathfinding");
    }
}
