package io.github.ninali927.maze;

import io.github.ninali927.maze.structures.ArrayMaze;
import io.github.ninali927.maze.structures.Maze;
import io.github.ninali927.maze.structures.MazeCell;
import io.github.ninali927.maze.generators.MazeGenerator;
import io.github.ninali927.maze.generators.RecursiveBacktrackingGenerator;
import io.github.ninali927.maze.traversal.*;

import java.util.List;

/**
 * A demonstration program showing how maze generation and traversal work together.
 *
 * This class demonstrates:
 * - Generating mazes with different algorithms
 * - Traversing mazes in different orders
 * - Finding paths recursively
 * - Analyzing maze properties
 *
 * Run this class to see them in action.
 */
public class MazeGenerationDemo {

    public static void main(String[] args) {
        System.out.println("=== Maze Generation and Traversal Demo ===\n");

        // Create a maze
        int width = 11;
        int height = 11;
        Maze maze = new ArrayMaze(width, height);

        // Initialize all cells as walls
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                maze.setWall(r, c, true);
            }
        }

        System.out.println("Created a " + width + "x" + height + " maze");
        System.out.println();

        // Generate the maze
        MazeGenerator generator = new RecursiveBacktrackingGenerator(42); // Fixed seed
        System.out.println("Generating maze using: " + generator.getName());
        generator.generate(maze, 0, 0);
        System.out.println("Generation complete!\n");

        // Define start and goal cells
        MazeCell start = maze.getCell(0, 0);
        MazeCell goal = maze.getCell(height - 1, width - 1);

        // Print the maze, marking start and goal
        printMaze(maze, start, goal);

        // Count walls vs open cells
        int walls = 0;
        int open = 0;
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                if (maze.getCell(r, c).isWall()) {
                    walls++;
                } else {
                    open++;
                }
            }
        }
        System.out.println("Walls: " + walls + ", Open: " + open);
        System.out.println();

        // Demonstrate traversals
        System.out.println("=== Maze Traversals ===\n");

        MazeTraversal preorder = new PreorderTraversal();
        List<MazeCell> preorderResult = preorder.traverse(maze, 0, 0);
        System.out.println(preorder.getName() + " visited " + preorderResult.size() + " cells");

        MazeTraversal postorder = new PostorderTraversal();
        List<MazeCell> postorderResult = postorder.traverse(maze, 0, 0);
        System.out.println(postorder.getName() + " visited " + postorderResult.size() + " cells");
        System.out.println();

        // Demonstrate path finding
        System.out.println("=== Path Finding ===\n");

        RecursivePathFinder pathFinder = new RecursivePathFinder(maze);
        // Note: start and goal are defined above, before printing the maze

        System.out.println("Start cell is wall: " + start.isWall());
        System.out.println("Goal cell is wall: " + goal.isWall());

        List<MazeCell> path = pathFinder.findPath(start, goal);
        System.out.println("Found a path of length: " + path.size());

        // Note: findAllPaths can be expensive for mazes with many paths
        // For a perfect maze (like recursive backtracking generates), there's only 1 path
        System.out.println("Finding all paths...");
        List<List<MazeCell>> allPaths = pathFinder.findAllPaths(start, goal);
        System.out.println("Total number of paths: " + allPaths.size());

        if (!allPaths.isEmpty()) {
            List<MazeCell> shortestPath = pathFinder.findShortestPath(start, goal);
            System.out.println("Shortest path length: " + shortestPath.size());

            int longestPathLength = 0;
            for (List<MazeCell> p : allPaths) {
                if (p.size() > longestPathLength) {
                    longestPathLength = p.size();
                }
            }
            System.out.println("Longest path length: " + longestPathLength);
        }
        System.out.println();

        // Demonstrate analysis
        System.out.println("=== Maze Analysis ===\n");

        MazeAnalyzer analyzer = new MazeAnalyzer();

        int deadEnds = analyzer.countDeadEnds(maze);
        System.out.println("Dead ends: " + deadEnds);

        int junctions = analyzer.countJunctions(maze);
        System.out.println("Junctions: " + junctions);

        int corridors = analyzer.countCorridors(maze);
        System.out.println("Corridors: " + corridors);

        int longestFromStart = analyzer.findLongestPathLength(maze, start);
        System.out.println("Longest path from start: " + longestFromStart);

        double difficulty = analyzer.computeDifficulty(maze);
        System.out.println("Difficulty score: " + String.format("%.2f", difficulty));
    }

    /**
     * Prints a readable ASCII representation of the maze using box-drawing characters.
     * This method draws lines between cells to represent walls and marks the start (S)
     * and goal (G) cells.
     */
    private static void printMaze(Maze maze, MazeCell start, MazeCell goal) {
        int height = maze.getHeight();
        int width = maze.getWidth();

        for (int r = 0; r < height; r++) {
            // Print northern wall line for the current row
            System.out.print("+");
            for (int c = 0; c < width; c++) {
                boolean passageUp = (r > 0) && !maze.isWall(r, c) && !maze.isWall(r - 1, c);
                System.out.print(passageUp ? "   " : "---");
                System.out.print("+");
            }
            System.out.println();

            // Print cell contents and eastern walls for the current row
            System.out.print("|");
            for (int c = 0; c < width; c++) {
                MazeCell currentCell = maze.getCell(r, c);
                if (currentCell.isWall()) {
                    System.out.print("###");
                } else if (currentCell == start) {
                    System.out.print(" S ");
                } else if (currentCell == goal) {
                    System.out.print(" G ");
                } else {
                    System.out.print("   ");
                }
                boolean passageRight =
                        (c < width - 1) && !maze.isWall(r, c) && !maze.isWall(r, c + 1);
                System.out.print(passageRight ? " " : "|");
            }
            System.out.println();
        }

        // Print the final southern wall line at the bottom of the maze
        System.out.print("+");
        for (int c = 0; c < width; c++) {
            System.out.print("---+");
        }
        System.out.println();
    }
}
