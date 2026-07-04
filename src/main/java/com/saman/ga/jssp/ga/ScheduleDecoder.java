package com.saman.ga.jssp.ga;

import com.saman.ga.jssp.model.Job;
import com.saman.ga.jssp.model.JsspInstance;
import com.saman.ga.jssp.model.Operation;
import com.saman.ga.jssp.model.Schedule;
import com.saman.ga.jssp.model.ScheduledOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Decodes an operation-based chromosome into a feasible schedule by construction.
 *
 * For each gene/job id, the decoder schedules the next unscheduled operation of that job at the earliest
 * feasible start time respecting:
 * - previous operation completion time for the same job
 * - machine availability
 * - operation duration
 */
public final class ScheduleDecoder {

    public Schedule decode(OperationBasedChromosome chromosome, JsspInstance instance) {
        Objects.requireNonNull(chromosome, "chromosome");
        Objects.requireNonNull(instance, "instance");
        chromosome.validateFor(instance);

        int[] nextOperationIndexByJob = new int[instance.numberOfJobs()];
        int[] jobReadyTime = new int[instance.numberOfJobs()];
        int[] machineReadyTime = new int[instance.numberOfMachines()];
        List<ScheduledOperation> scheduledOperations = new ArrayList<>(instance.totalOperations());

        for (int geneIndex = 0; geneIndex < chromosome.length(); geneIndex++) {
            int jobId = chromosome.geneAt(geneIndex);
            Job job = instance.job(jobId);
            int operationIndex = nextOperationIndexByJob[jobId];
            Operation operation = job.operation(operationIndex);

            int startTime = Math.max(jobReadyTime[jobId], machineReadyTime[operation.machineId()]);
            int endTime = startTime + operation.duration();

            scheduledOperations.add(new ScheduledOperation(operation, startTime, endTime));

            nextOperationIndexByJob[jobId]++;
            jobReadyTime[jobId] = endTime;
            machineReadyTime[operation.machineId()] = endTime;
        }

        return new Schedule(scheduledOperations);
    }
}
