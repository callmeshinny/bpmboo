package com.example.bpmbooheartbeat.processors;

import java.util.List;

public class HeartRateCalculator {

    public static int calculateBPM(List<Double> signalValues, double durationSeconds) {
        // Not enough data
        if (signalValues.size() < 50) return 0;

        int peaks = 0;
        // Simple Peak Detection: Moving Average / Local Maxima
        for (int i = 1; i < signalValues.size() - 1; i++) {
            double prev = signalValues.get(i - 1);
            double current = signalValues.get(i);
            double next = signalValues.get(i + 1);

            // Basic thresholding: current must be higher than neighbors
            // and higher than a minimum signal threshold (to ignore noise)
            if (current > prev && current > next && current > 150) {
                peaks++;
            }
        }

        // Formula: (Count / seconds) * 60
        return (int) ((peaks / durationSeconds) * 60);
    }
}
