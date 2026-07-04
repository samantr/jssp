package com.saman.ga.jssp.ga;

import com.saman.ga.jssp.model.Schedule;

/**
 * Result returned by the GA runner.
 */
public record GeneticAlgorithmResult(
        OperationBasedChromosome bestChromosome,
        Schedule bestSchedule,
        int bestMakespan,
        int generationFound
) {
}
