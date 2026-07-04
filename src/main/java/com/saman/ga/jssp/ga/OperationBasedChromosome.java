package com.saman.ga.jssp.ga;

import com.saman.ga.jssp.model.JsspInstance;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

/**
 * Operation-based chromosome representation.
 *
 * Example for 3 jobs and 3 operations per job:
 * [0, 1, 2, 0, 1, 2, 0, 1, 2]
 *
 * The first occurrence of a job id maps to operation 0 of that job, the second occurrence to operation 1, etc.
 */
public final class OperationBasedChromosome {
    private final int[] genes;

    public OperationBasedChromosome(int[] genes) {
        Objects.requireNonNull(genes, "genes");
        if (genes.length == 0) {
            throw new IllegalArgumentException("Chromosome must not be empty");
        }
        this.genes = genes.clone();
    }

    public int length() {
        return genes.length;
    }

    public int geneAt(int index) {
        return genes[index];
    }

    public int[] genes() {
        return genes.clone();
    }

    public void validateFor(JsspInstance instance) {
        Objects.requireNonNull(instance, "instance");
        if (genes.length != instance.totalOperations()) {
            throw new IllegalArgumentException("Chromosome length must equal total operation count");
        }
        int[] counts = new int[instance.numberOfJobs()];
        for (int gene : genes) {
            if (gene < 0 || gene >= instance.numberOfJobs()) {
                throw new IllegalArgumentException("Invalid job id in chromosome: " + gene);
            }
            counts[gene]++;
        }
        for (int jobId = 0; jobId < counts.length; jobId++) {
            int required = instance.job(jobId).operationCount();
            if (counts[jobId] != required) {
                throw new IllegalArgumentException("Job " + jobId + " appears " + counts[jobId]
                        + " times, expected " + required);
            }
        }
    }

    public static OperationBasedChromosome canonical(JsspInstance instance) {
        int[] genes = new int[instance.totalOperations()];
        int index = 0;
        for (int operationIndex = 0; operationIndex < instance.operationsPerJob(); operationIndex++) {
            for (int jobId = 0; jobId < instance.numberOfJobs(); jobId++) {
                genes[index++] = jobId;
            }
        }
        return new OperationBasedChromosome(genes);
    }

    public static OperationBasedChromosome random(JsspInstance instance, Random random) {
        Objects.requireNonNull(instance, "instance");
        Objects.requireNonNull(random, "random");

        int[] genes = canonical(instance).genes();
        for (int i = genes.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int tmp = genes[i];
            genes[i] = genes[j];
            genes[j] = tmp;
        }
        return new OperationBasedChromosome(genes);
    }

    @Override
    public String toString() {
        return Arrays.toString(genes);
    }
}
