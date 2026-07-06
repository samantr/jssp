package com.saman.ga.jssp.ga;

import com.saman.ga.jssp.TestInstances;
import com.saman.ga.jssp.model.JsspInstance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneticAlgorithmSmokeTest {

    @Test
    void gaReturnsFeasibleSchedule() {
        JsspInstance instance = TestInstances.ft06();

        GeneticAlgorithmConfig config = new GeneticAlgorithmConfig(
                30,
                20,
                0.9,
                0.2,
                3,
                42L
        );

        GeneticAlgorithmResult result = new GeneticAlgorithm(instance, config).run();

        assertTrue(result.bestFitness() > 0);
        assertTrue(result.bestSchedule().makespan() > 0);
        assertTrue(result.bestSchedule().schedulesEveryOperationExactlyOnce(instance));
        assertTrue(result.bestSchedule().respectsPrecedence(instance));
        assertTrue(result.bestSchedule().hasNoMachineConflicts(instance));
    }
}