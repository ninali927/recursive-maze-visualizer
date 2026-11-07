package io.github.ninali927.maze.generators;

import io.github.ninali927.maze.generators.MazeGenerator;
import io.github.ninali927.maze.generators.RecursiveBacktrackingGenerator;

/**
 * Concrete test class for RecursiveBacktrackingGenerator.
 *
 * This extends the abstract MazeGeneratorTest class, inheriting all
 * the standard tests. We only need to implement the factory method.
 */
public class RecursiveBacktrackingGeneratorTest extends MazeGeneratorTest {

    @Override
    protected MazeGenerator makeGenerator() {
        return new RecursiveBacktrackingGenerator();
    }
}
