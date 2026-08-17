package com.example.cs360_nellyroca_weighttracking;

/**
 * Represents a single weight entry retrieved from
 * the SQLite weights table.
 *
 * Weight values are stored as doubles so that they
 * can be used for calculations such as progress,
 * averages, and comparisons.
 */
public class WeightEntry {

    private final int id;
    private final String date;
    private final double weight;

    public WeightEntry(
            int id,
            String date,
            double weight) {

        this.id = id;
        this.date = date;
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public double getWeight() {
        return weight;
    }
}