package com.example.bpmbooheartbeat.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bpmbooheartbeat.data.model.HeartRateRecord;
import com.example.bpmbooheartbeat.data.repository.HeartRateRepository;

import java.util.List;

public class HeartRateViewModel extends AndroidViewModel {

    private final HeartRateRepository repository;
    private final LiveData<List<HeartRateRecord>> allLocalRecords;

    public HeartRateViewModel(@NonNull Application application) {
        super(application);
        repository = new HeartRateRepository(application);
        allLocalRecords = repository.getAllRecords();
    }

    public void saveRecord(HeartRateRecord record) {
        repository.insert(record);
    }

    public void deleteRecord(HeartRateRecord record) {
        repository.delete(record);
    }

    // Old local data, kept as fallback
    public LiveData<List<HeartRateRecord>> getAllRecords() {
        return allLocalRecords;
    }

    // Main data source for History + Analytics: MongoDB
    public MutableLiveData<List<HeartRateRecord>> getRemoteRecords() {
        return repository.getRemoteRecords();
    }

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