package com.saman.ga.jssp.ga;

/**
 * Configuration for the minimal GA loop.
 */
public record GeneticAlgorithmConfig(
        int populationSize,
        int generations,
        double crossoverRate,
        double mutationRate,
        int tournamentSize,
        long seed
) {
    public GeneticAlgorithmConfig {
        if (populationSize < 2) {
            throw new IllegalArgumentException("Population size must be at least 2");
        }
        if (generations < 1) {
            throw new IllegalArgumentException("Generations must be at least 1");
        }
        if (crossoverRate < 0.0 || crossoverRate > 1.0) {
            throw new IllegalArgumentException("Crossover rate must be between 0 and 1");
        }
        if (mutationRate < 0.0 || mutationRate > 1.0) {
            throw new IllegalArgumentException("Mutation rate must be between 0 and 1");
        }
        if (tournamentSize < 2) {
            throw new IllegalArgumentException("Tournament size must be at least 2");
        }
    }

    public static GeneticAlgorithmConfig defaults() {
        return new GeneticAlgorithmConfig(100, 500, 0.90, 0.20, 3, 42L);
    }
}
