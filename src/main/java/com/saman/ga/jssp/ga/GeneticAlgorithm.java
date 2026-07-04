package com.saman.ga.jssp.ga;

import com.saman.ga.jssp.constraints.MakespanObjective;
import com.saman.ga.jssp.fitness.FitnessCalculator;
import com.saman.ga.jssp.model.JsspInstance;
import com.saman.ga.jssp.model.Schedule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * A small, readable GA implementation for the first prototype.
 *
 * Operators:
 * - initialization: random shuffle of the operation-based multiset
 * - selection: tournament selection
 * - crossover: count-preserving order crossover for repeated job ids
 * - mutation: random swap
 * - elitism: best individual survives each generation
 */
public final class GeneticAlgorithm {
    private final JsspInstance instance;
    private final GeneticAlgorithmConfig config;
    private final ScheduleDecoder decoder;
    private final FitnessCalculator fitnessCalculator;
    private final Random random;

    public GeneticAlgorithm(JsspInstance instance, GeneticAlgorithmConfig config) {
        this.instance = Objects.requireNonNull(instance, "instance");
        this.config = Objects.requireNonNull(config, "config");
        this.decoder = new ScheduleDecoder();
        this.fitnessCalculator = new FitnessCalculator(List.of(new MakespanObjective()));
        this.random = new Random(config.seed());
    }

    public GeneticAlgorithmResult run() {
        List<Individual> population = initializePopulation();
        population.sort(Comparator.comparingInt(Individual::fitness));

        Individual best = population.get(0);
        int generationFound = 0;

        for (int generation = 1; generation <= config.generations(); generation++) {
            List<Individual> nextGeneration = new ArrayList<>(config.populationSize());
            nextGeneration.add(best); // elitism

            while (nextGeneration.size() < config.populationSize()) {
                Individual parent1 = tournamentSelect(population);
                Individual parent2 = tournamentSelect(population);

                OperationBasedChromosome childChromosome;
                if (random.nextDouble() < config.crossoverRate()) {
                    childChromosome = crossover(parent1.chromosome(), parent2.chromosome());
                } else {
                    childChromosome = new OperationBasedChromosome(parent1.chromosome().genes());
                }

                if (random.nextDouble() < config.mutationRate()) {
                    childChromosome = mutate(childChromosome);
                }

                nextGeneration.add(evaluate(childChromosome));
            }

            population = nextGeneration;
            population.sort(Comparator.comparingInt(Individual::fitness));
            Individual generationBest = population.get(0);
            if (generationBest.fitness() < best.fitness()) {
                best = generationBest;
                generationFound = generation;
            }
        }

        Schedule bestSchedule = decoder.decode(best.chromosome(), instance);
        return new GeneticAlgorithmResult(best.chromosome(), bestSchedule, best.fitness(), generationFound);
    }

    private List<Individual> initializePopulation() {
        List<Individual> population = new ArrayList<>(config.populationSize());
        population.add(evaluate(OperationBasedChromosome.canonical(instance)));
        while (population.size() < config.populationSize()) {
            population.add(evaluate(OperationBasedChromosome.random(instance, random)));
        }
        return population;
    }

    private Individual evaluate(OperationBasedChromosome chromosome) {
        chromosome.validateFor(instance);

        Schedule schedule = decoder.decode(chromosome, instance);
        int fitness = fitnessCalculator.calculate(schedule, instance);

        return new Individual(chromosome, fitness);
    }

    private Individual tournamentSelect(List<Individual> population) {
        Individual best = null;
        for (int i = 0; i < config.tournamentSize(); i++) {
            Individual candidate = population.get(random.nextInt(population.size()));
            if (best == null || candidate.fitness() < best.fitness()) {
                best = candidate;
            }
        }
        return best;
    }

    /**
     * Count-preserving order crossover for chromosomes with repeated job ids.
     * It copies a random mask from parent A, then fills remaining positions with parent B's jobs in order.
     */
    OperationBasedChromosome crossover(OperationBasedChromosome parentA, OperationBasedChromosome parentB) {
        int length = parentA.length();
        int[] child = new int[length];
        boolean[] filled = new boolean[length];
        int[] selectedCounts = new int[instance.numberOfJobs()];
        int[] requiredCounts = requiredJobCounts();

        for (int i = 0; i < length; i++) {
            if (random.nextBoolean()) {
                int jobId = parentA.geneAt(i);
                child[i] = jobId;
                filled[i] = true;
                selectedCounts[jobId]++;
            }
        }

        int fillIndex = 0;
        for (int i = 0; i < length; i++) {
            int candidateJobId = parentB.geneAt(i);
            if (selectedCounts[candidateJobId] >= requiredCounts[candidateJobId]) {
                continue;
            }
            while (fillIndex < length && filled[fillIndex]) {
                fillIndex++;
            }
            if (fillIndex >= length) {
                break;
            }
            child[fillIndex] = candidateJobId;
            filled[fillIndex] = true;
            selectedCounts[candidateJobId]++;
        }

        OperationBasedChromosome chromosome = new OperationBasedChromosome(child);
        chromosome.validateFor(instance);
        return chromosome;
    }

    OperationBasedChromosome mutate(OperationBasedChromosome chromosome) {
        int[] genes = chromosome.genes();
        int first = random.nextInt(genes.length);
        int second = random.nextInt(genes.length);
        int tmp = genes[first];
        genes[first] = genes[second];
        genes[second] = tmp;
        return new OperationBasedChromosome(genes);
    }

    private int[] requiredJobCounts() {
        int[] counts = new int[instance.numberOfJobs()];
        for (int jobId = 0; jobId < instance.numberOfJobs(); jobId++) {
            counts[jobId] = instance.job(jobId).operationCount();
        }
        return counts;
    }

    private record Individual(OperationBasedChromosome chromosome, int fitness) {
    }
}
