package com.saman.ga.jssp.parser;

import com.saman.ga.jssp.TestInstances;
import com.saman.ga.jssp.model.JsspInstance;
import com.saman.ga.jssp.model.Operation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsplibParserTest {

    @Test
    void parsesFt06HeaderAndOperations() {
        JsspInstance instance = TestInstances.ft06();

        assertEquals("ft06", instance.name());
        assertEquals(6, instance.numberOfJobs());
        assertEquals(6, instance.numberOfMachines());
        assertEquals(36, instance.totalOperations());

        Operation firstOperation = instance.job(0).operation(0);
        assertEquals(0, firstOperation.jobId());
        assertEquals(0, firstOperation.operationIndex());
        assertEquals(2, firstOperation.machineId());
        assertEquals(1, firstOperation.duration());

        Operation lastOperation = instance.job(5).operation(5);
        assertEquals(5, lastOperation.jobId());
        assertEquals(5, lastOperation.operationIndex());
        assertEquals(2, lastOperation.machineId());
        assertEquals(1, lastOperation.duration());
    }
}
