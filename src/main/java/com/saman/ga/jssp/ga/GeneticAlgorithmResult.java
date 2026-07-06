package com.saman.ga.jssp.ga;

import com.saman.ga.jssp.model.Schedule;

/**
 * Result returned by the GA runner.
 *
 * bestFitness is the minimized GA objective value.
 * In the basic version it equals makespan.
 * With dynamic constraints it equals makespan + penalties.
 */
public record GeneticAlgorithmResult(
        OperationBasedChromosome bestChromosome,
        Schedule bestSchedule,
        int bestFitness,
        int generationFound
) {
}