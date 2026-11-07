# Recursive Maze Visualizer

A Java project that generates and solves grid mazes using recursion, with a browser-based visualizer that animates every step in real time.

## What it does

- **Generates perfect mazes** with recursive backtracking. Every open cell is reachable, and there is exactly one path between any two cells.
- **Traverses mazes** with recursive pre-order and post-order depth-first traversal (neighbor order: north, south, east, west).
- **Finds paths** with recursive backtracking: one path, all paths, or the shortest path by exhaustive search.
- **Analyzes mazes**: dead ends, junctions, corridors, longest path, and a simple difficulty score.
- **Visualizes it all** in a web UI. The backend streams each carve, visit, and backtrack over a WebSocket.

## What it demonstrates

**Pre-order vs. post-order.** Pre-order records a cell as soon as it is reached, so in a corridor `(0,0) → (0,1) → (0,2)` the cells light up left to right. Post-order records a cell only after all its neighbors are explored, so the same corridor lights up right to left as the recursion unwinds. Both are depth-first: they follow one route to its end before backtracking. A breadth-first search would instead expand outward, closest cells first.

**Odd vs. even maze dimensions.** The generator moves two cells at a time and carves the wall between, so it only lands on even indices (0, 2, 4, …). In an odd-sized maze (e.g. 11×11), the last row and column are even (index 10), so the whole grid gets carved. In an even-sized maze (e.g. 10×10), the last row and column are odd (index 9) and stay solid walls.

## Project layout

```
.
├── core/                       # Maze algorithms (plain Java, no dependencies)
│   └── src/
│       ├── main/java/io/github/ninali927/maze/
│       │   ├── structures/     # Maze, MazeCell, ArrayMaze
│       │   ├── generators/     # MazeGenerator, RecursiveBacktrackingGenerator
│       │   ├── traversal/      # Pre/post-order traversal, RecursivePathFinder, MazeAnalyzer
│       │   └── MazeGenerationDemo.java   # Console demo
│       └── test/java/...       # JUnit 5 tests for the algorithms
├── app/
│   ├── backend/                # Javalin web server + Gradle build (compiles core/ too)
│   └── frontend/               # Static HTML/CSS/JS visualizer
└── start-visualization.sh      # Starts the visualizer on http://localhost:7001
```

The backend's Gradle build compiles `core/` directly as an extra source set. The visualizer subclasses the core algorithms (`Visualizing*` classes) to emit a message at each step.

## Running it

Requires **JDK 25**. Gradle is downloaded automatically by the wrapper.

Start the visualizer:

```bash
bash start-visualization.sh
```

Then open <http://localhost:7001>. Press `Ctrl+C` to stop.

Run the tests:

```bash
cd app/backend
./gradlew test
```

The coverage report is written to `app/backend/build/reports/jacoco/index.html`.

Run the console demo (no build needed):

```bash
java core/src/main/java/io/github/ninali927/maze/MazeGenerationDemo.java
```

## Tech stack

- **Java 25**, **Gradle 9.1** (wrapper)
- **Javalin 6** (HTTP + WebSocket), **Gson**
- **JUnit 5**, **JaCoCo**
- **Frontend:** vanilla JavaScript, Alpine.js, htmx (WebSocket extension), custom CSS design system
