package com.saman.ga.jssp.ga;

import com.saman.ga.jssp.TestInstances;
import com.saman.ga.jssp.constraints.ConstraintEvaluation;
import com.saman.ga.jssp.constraints.ConstraintFunction;
import com.saman.ga.jssp.constraints.ConstraintLevel;
import com.saman.ga.jssp.dynamic.MachineTimeLimitConstraintFunction;
import com.saman.ga.jssp.model.JsspInstance;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneticAlgorithmDynamicConstraintTest {

    @Test
    void bestFitnessIncludesDynamicPenalty() {
        JsspInstance instance = TestInstances.ft06();

        ConstraintFunction dynamicConstraint = new MachineTimeLimitConstraintFunction(
                "Machine 2 should not work after time 40",
                ConstraintLevel.SOFT,
                10,
                2,
                40
        );

        GeneticAlgorithmConfig config = new GeneticAlgorithmConfig(
                30,
                20,
                0.9,
                0.2,
                3,
                42L
        );

        GeneticAlgorithmResult result = new GeneticAlgorithm(
                instance,
                config,
                List.of(dynamicConstraint)
        ).run();

        ConstraintEvaluation evaluation = dynamicConstraint.evaluate(
                result.bestSchedule(),
                instance
        );

        int expectedFitness = result.bestSchedule().makespan() + evaluation.penalty();

        assertEquals(expectedFitness, result.bestFitness());
        assertTrue(result.bestSchedule().makespan() > 0);
        assertTrue(result.bestFitness() >= result.bestSchedule().makespan());
        assertTrue(result.bestSchedule().schedulesEveryOperationExactlyOnce(instance));
        assertTrue(result.bestSchedule().respectsPrecedence(instance));
        assertTrue(result.bestSchedule().hasNoMachineConflicts(instance));
    }
}