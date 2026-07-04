package com.saman.ga.jssp.fitness;

import com.saman.ga.jssp.ga.OperationBasedChromosome;
import com.saman.ga.jssp.ga.ScheduleDecoder;
import com.saman.ga.jssp.model.JsspInstance;
import com.saman.ga.jssp.model.Schedule;

import java.util.Objects;

/**
 * First-version fitness evaluator: fitness = makespan.
 * Lower is better.
 */
public final class MakespanFitnessEvaluator {
    private final ScheduleDecoder decoder;

    public MakespanFitnessEvaluator(ScheduleDecoder decoder) {
        this.decoder = Objects.requireNonNull(decoder, "decoder");
    }

    public int evaluate(OperationBasedChromosome chromosome, JsspInstance instance) {
        Schedule schedule = decoder.decode(chromosome, instance);
        return schedule.makespan();
    }
}
