// data/dao/HeartRateDao.java
package com.example.bpmbooheartbeat.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;

import com.example.bpmbooheartbeat.data.model.HeartRateRecord;
import java.util.List;

@Dao
public interface HeartRateDao {

    @Insert
    void insert(HeartRateRecord record);

    // Returns all records, newest first — LiveData means UI auto-updates
    @Query("SELECT * FROM heart_rate_records ORDER BY timestamp DESC")
    LiveData<List<HeartRateRecord>> getAllRecords();

    // Analytics queries — called from Repository, not UI directly
    @Query("SELECT * FROM heart_rate_records WHERE timestamp >= :startTime ORDER BY timestamp ASC")
    LiveData<List<HeartRateRecord>> getRecordsSince(long startTime);

    @Query("SELECT AVG(bpmValue) FROM heart_rate_records WHERE timestamp >= :startTime")
    LiveData<Double> getAverageBpmSince(long startTime);

    @Query("SELECT MAX(bpmValue) FROM heart_rate_records WHERE timestamp >= :startTime")
    LiveData<Integer> getMaxBpmSince(long startTime);

    @Query("SELECT MIN(bpmValue) FROM heart_rate_records WHERE timestamp >= :startTime")
    LiveData<Integer> getMinBpmSince(long startTime);

    @Delete
    void delete(HeartRateRecord record);

    @Query("DELETE FROM heart_rate_records")
    void deleteAll();
}