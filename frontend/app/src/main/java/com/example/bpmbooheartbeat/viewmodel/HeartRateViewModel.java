// viewmodel/HeartRateViewModel.java
package com.example.bpmbooheartbeat.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.bpmbooheartbeat.data.model.HeartRateRecord;
import com.example.bpmbooheartbeat.data.repository.HeartRateRepository;

import java.util.Calendar;
import java.util.List;

public class HeartRateViewModel extends AndroidViewModel {

    private final HeartRateRepository repository;
    private final LiveData<List<HeartRateRecord>> allRecords;

    public HeartRateViewModel(@NonNull Application application) {
        super(application);
        repository = new HeartRateRepository(application);
        allRecords = repository.getAllRecords();
    }

    public void saveRecord(HeartRateRecord record) {
        repository.insert(record);
    }

    public void deleteRecord(HeartRateRecord record) {
        repository.delete(record);
    }

    public LiveData<List<HeartRateRecord>> getAllRecords() {
        return allRecords;
    }

    // Convenience: last 7 days
    public LiveData<List<HeartRateRecord>> getWeeklyRecords() {
        long sevenDaysAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);
        return repository.getRecordsSince(sevenDaysAgo);
    }

    public LiveData<Double> getWeeklyAverage() {
        long sevenDaysAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);
        return repository.getAverageBpmSince(sevenDaysAgo);
    }

    public LiveData<Integer> getWeeklyMax() {
        long sevenDaysAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);
        return repository.getMaxBpmSince(sevenDaysAgo);
    }

    public LiveData<Integer> getWeeklyMin() {
        long sevenDaysAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);
        return repository.getMinBpmSince(sevenDaysAgo);
    }
}