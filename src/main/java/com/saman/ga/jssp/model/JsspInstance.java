package com.saman.ga.jssp.model;

import java.util.List;
import java.util.Objects;

/**
 * Job Shop Scheduling Problem instance parsed from JSPLIB.
 */
public final class JsspInstance {
    private final String name;
    private final int numberOfJobs;
    private final int numberOfMachines;
    private final List<Job> jobs;

    public JsspInstance(String name, int numberOfJobs, int numberOfMachines, List<Job> jobs) {
        this.name = Objects.requireNonNull(name, "name");
        if (numberOfJobs <= 0) {
            throw new IllegalArgumentException("Number of jobs must be positive");
        }
        if (numberOfMachines <= 0) {
            throw new IllegalArgumentException("Number of machines must be positive");
        }
        Objects.requireNonNull(jobs, "jobs");
        if (jobs.size() != numberOfJobs) {
            throw new IllegalArgumentException("Job count does not match header");
        }
        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            if (job.id() != i) {
                throw new IllegalArgumentException("Jobs must be ordered by id starting at zero");
            }
            if (job.operationCount() != numberOfMachines) {
                throw new IllegalArgumentException("Each job must contain one operation per machine in this first version");
            }
            for (Operation operation : job.operations()) {
                if (operation.machineId() >= numberOfMachines) {
                    throw new IllegalArgumentException("Operation references machine outside header range");
                }
            }
        }
        this.numberOfJobs = numberOfJobs;
        this.numberOfMachines = numberOfMachines;
        this.jobs = List.copyOf(jobs);
    }

    public String name() {
        return name;
    }

    public int numberOfJobs() {
        return numberOfJobs;
    }

    public int numberOfMachines() {
        return numberOfMachines;
    }

    public List<Job> jobs() {
        return jobs;
    }

    public Job job(int jobId) {
        return jobs.get(jobId);
    }

    public int operationsPerJob() {
        return numberOfMachines;
    }

    public int totalOperations() {
        return numberOfJobs * numberOfMachines;
    }
}
