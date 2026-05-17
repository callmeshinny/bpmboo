// data/model/HeartRateRecord.java
package com.example.bpmbooheartbeat.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.Nullable;

@Entity(tableName = "heart_rate_records")
public class HeartRateRecord {

    @PrimaryKey(autoGenerate = true)
    public long id;

    // Core measurement fields
    public int bpmValue;
    public long timestamp;           // System.currentTimeMillis() at measurement end
    public int measurementDurationMs; // Actual duration (handles early stops)

    // Quality & context (nullable = future expansion without migration)
    @Nullable
    public Float confidenceScore;    // 0.0–1.0, derived from signal variance

    @Nullable
    public String feelingTag;        // "RESTING", "POST_EXERCISE", "STRESSED"

    @Nullable
    public String notes;             // Free-text user annotation

    // Future-proofing: reserved columns for biometric expansion
    // Room ignores these if null; no schema migration needed for nullable adds
    @Nullable
    public Integer systolicBP;       // Future: Blood pressure estimation
    @Nullable
    public Integer diastolicBP;

    // Constructor for mandatory fields only
    public HeartRateRecord(int bpmValue, long timestamp, int measurementDurationMs) {
        this.bpmValue = bpmValue;
        this.timestamp = timestamp;
        this.measurementDurationMs = measurementDurationMs;
    }
}