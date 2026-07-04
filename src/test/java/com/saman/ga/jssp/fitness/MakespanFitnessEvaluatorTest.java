package com.saman.ga.jssp.fitness;

import com.saman.ga.jssp.TestInstances;
import com.saman.ga.jssp.ga.OperationBasedChromosome;
import com.saman.ga.jssp.ga.ScheduleDecoder;
import com.saman.ga.jssp.model.JsspInstance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MakespanFitnessEvaluatorTest {

    @Test
    void evaluatesCanonicalFt06ChromosomeAsMakespan60() {
        JsspInstance instance = TestInstances.ft06();
        OperationBasedChromosome chromosome = OperationBasedChromosome.canonical(instance);

        int fitness = new MakespanFitnessEvaluator(new ScheduleDecoder()).evaluate(chromosome, instance);

        assertEquals(60, fitness);
    }
}
