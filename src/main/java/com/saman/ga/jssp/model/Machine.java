package com.saman.ga.jssp.model;

/**
 * A machine in a Job Shop Scheduling instance.
 */
public record Machine(int id) {
    public Machine {
        if (id < 0) {
            throw new IllegalArgumentException("Machine id must be non-negative");
        }
    }
}
