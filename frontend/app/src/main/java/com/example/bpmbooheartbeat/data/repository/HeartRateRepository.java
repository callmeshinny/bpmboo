// data/repository/HeartRateRepository.java
package com.example.bpmbooheartbeat.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.bpmbooheartbeat.data.AppDatabase;
import com.example.bpmbooheartbeat.data.dao.HeartRateDao;
import com.example.bpmbooheartbeat.data.model.HeartRateRecord;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HeartRateRepository {

    private final HeartRateDao dao;
    // Single-thread executor: DB writes are sequential, preventing race conditions
    private final ExecutorService dbWriteExecutor = Executors.newSingleThreadExecutor();

    public HeartRateRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        this.dao = db.heartRateDao();
    }

    // INSERT — runs on background thread, fire-and-forget
    public void insert(HeartRateRecord record) {
        dbWriteExecutor.execute(() -> dao.insert(record));
    }

    public void delete(HeartRateRecord record) {
        dbWriteExecutor.execute(() -> dao.delete(record));
    }

    // READ — Room delivers LiveData on main thread automatically
    public LiveData<List<HeartRateRecord>> getAllRecords() {
        return dao.getAllRecords();
    }

    public LiveData<List<HeartRateRecord>> getRecordsSince(long startTime) {
        return dao.getRecordsSince(startTime);
    }

    public LiveData<Double> getAverageBpmSince(long startTime) {
        return dao.getAverageBpmSince(startTime);
    }

    public LiveData<Integer> getMaxBpmSince(long startTime) {
        return dao.getMaxBpmSince(startTime);
    }

    public LiveData<Integer> getMinBpmSince(long startTime) {
        return dao.getMinBpmSince(startTime);
    }
}