package com.saman.ga.jssp.ga;

import com.saman.ga.jssp.TestInstances;
import com.saman.ga.jssp.constraints.ConstraintEvaluation;
import com.saman.ga.jssp.constraints.ConstraintFunction;
import com.saman.ga.jssp.dynamic.DynamicConstraint;
import com.saman.ga.jssp.dynamic.DynamicConstraintInterpreter;
import com.saman.ga.jssp.dynamic.MockConstraintGenerationAgent;
import com.saman.ga.jssp.model.JsspInstance;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneticAlgorithmMockAgentTest {

    @Test
    void gaUsesMockGeneratedDynamicConstraintInFitness() {
        JsspInstance instance = TestInstances.ft06();

        DynamicConstraint generatedConstraint = new MockConstraintGenerationAgent()
                .generate("Machine 2 should not work after time 40", instance);

        ConstraintFunction constraintFunction = new DynamicConstraintInterpreter()
                .interpret(generatedConstraint, instance);

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
                List.of(constraintFunction)
        ).run();

        ConstraintEvaluation evaluation = constraintFunction.evaluate(
                result.bestSchedule(),
                instance
        );

        int expectedFitness = result.bestSchedule().makespan() + evaluation.penalty();

        assertEquals(expectedFitness, result.bestFitness());
        assertTrue(result.bestFitness() >= result.bestSchedule().makespan());
        assertTrue(result.bestSchedule().schedulesEveryOperationExactlyOnce(instance));
        assertTrue(result.bestSchedule().respectsPrecedence(instance));
        assertTrue(result.bestSchedule().hasNoMachineConflicts(instance));
    }
}