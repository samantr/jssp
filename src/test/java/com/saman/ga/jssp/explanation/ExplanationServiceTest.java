package com.saman.ga.jssp.explanation;

import com.saman.ga.jssp.TestInstances;
import com.saman.ga.jssp.constraints.ConstraintFunction;
import com.saman.ga.jssp.constraints.ConstraintLevel;
import com.saman.ga.jssp.dynamic.MachineTimeLimitConstraintFunction;
import com.saman.ga.jssp.ga.OperationBasedChromosome;
import com.saman.ga.jssp.ga.ScheduleDecoder;
import com.saman.ga.jssp.model.JsspInstance;
import com.saman.ga.jssp.model.Schedule;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExplanationServiceTest {

    @Test
    void explainsDynamicConstraintViolationsWithOperationDetails() {
        JsspInstance instance = TestInstances.ft06();

        Schedule schedule = new ScheduleDecoder().decode(
                OperationBasedChromosome.canonical(instance),
                instance
        );

        ConstraintFunction constraint = new MachineTimeLimitConstraintFunction(
                "Machine 2 should not work after time 40",
                ConstraintLevel.SOFT,
                10,
                2,
                40
        );

        ConstraintExplanationReport report = new ExplanationService()
                .explain(schedule, instance, List.of(constraint));

        assertEquals(schedule.makespan(), report.scheduleMakespan());
        assertEquals(80, report.totalPenalty());
        assertEquals(1, report.violatedConstraintCount());
        assertEquals(1, report.totalViolationCount());

        String text = report.toText();

        assertTrue(text.contains("Constraint explanation report"));
        assertTrue(text.contains("Machine 2 should not work after time 40"));
        assertTrue(text.contains("Operation: J5-O5"));
        assertTrue(text.contains("Machine: 2"));
        assertTrue(text.contains("Penalty added: 80"));
    }
}