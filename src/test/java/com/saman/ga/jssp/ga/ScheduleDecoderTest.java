package com.saman.ga.jssp.ga;

import com.saman.ga.jssp.TestInstances;
import com.saman.ga.jssp.model.JsspInstance;
import com.saman.ga.jssp.model.Schedule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScheduleDecoderTest {

    @Test
    void decodesOperationBasedChromosomeAndSchedulesEveryOperationOnce() {
        JsspInstance instance = TestInstances.ft06();
        OperationBasedChromosome chromosome = OperationBasedChromosome.canonical(instance);

        Schedule schedule = new ScheduleDecoder().decode(chromosome, instance);

        assertEquals(36, schedule.operations().size());
        assertTrue(schedule.schedulesEveryOperationExactlyOnce(instance));
        assertEquals(0, schedule.find(0, 0).startTime());
        assertEquals(1, schedule.find(0, 0).endTime());
        assertEquals(47, schedule.find(5, 5).startTime());
        assertEquals(48, schedule.find(5, 5).endTime());
    }
}
