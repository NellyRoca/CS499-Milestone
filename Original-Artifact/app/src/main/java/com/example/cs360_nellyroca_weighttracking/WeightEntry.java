/**
 * Updated to match DatabaseHelper
 */
package com.example.cs360_nellyroca_weighttracking;

public class WeightEntry {
    private int id;
    private String date;
    private String weight;

    public WeightEntry(int id, String date, String weight) {
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

    public String getWeight() {
        return weight;
    }
}