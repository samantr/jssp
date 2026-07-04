package com.saman.ga.jssp.ga;

import com.saman.ga.jssp.TestInstances;
import com.saman.ga.jssp.model.JsspInstance;
import com.saman.ga.jssp.model.Schedule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MachineConflictTest {

    @Test
    void decodedScheduleHasNoMachineOverlaps() {
        JsspInstance instance = TestInstances.ft06();
        Schedule schedule = new ScheduleDecoder().decode(OperationBasedChromosome.canonical(instance), instance);

        assertTrue(schedule.hasNoMachineConflicts(instance));
    }
}
