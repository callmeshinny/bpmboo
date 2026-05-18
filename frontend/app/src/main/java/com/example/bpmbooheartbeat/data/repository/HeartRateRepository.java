// data/repository/HeartRateRepository.java
package com.example.bpmbooheartbeat.data.repository;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bpmbooheartbeat.data.AppDatabase;
import com.example.bpmbooheartbeat.data.api.ApiRequest;
import com.example.bpmbooheartbeat.data.api.ApiResponse;
import com.example.bpmbooheartbeat.data.api.RetrofitClient;
import com.example.bpmbooheartbeat.data.dao.HeartRateDao;
import com.example.bpmbooheartbeat.data.model.HeartRateRecord;
import com.example.bpmbooheartbeat.utils.AuthPreferences;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HeartRateRepository {

    private final HeartRateDao dao;
    private final AuthPreferences authPrefs;
    // Single-thread executor: DB writes are sequential, preventing race conditions
    private final ExecutorService dbWriteExecutor = Executors.newSingleThreadExecutor();
    private static final String TAG = "HeartRateRepository";

    public HeartRateRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        this.dao = db.heartRateDao();
        this.authPrefs = new AuthPreferences(application);
    }

    // INSERT — runs on background thread, fire-and-forget
    // Also syncs to backend if user is logged in
    public void insert(HeartRateRecord record) {
        dbWriteExecutor.execute(() -> {
            dao.insert(record);
            // Try to sync to backend
            syncToBackend(record);
        });
    }

    private void syncToBackend(HeartRateRecord record) {
        String token = authPrefs.getToken();
        if (token == null) {
            Log.w(TAG, "Not logged in, skipping backend sync");
            return;
        }

        String userId = authPrefs.getUserId();

        ApiRequest.HeartRateRecordRequest request = new ApiRequest.HeartRateRecordRequest(
                userId,
                record.bpmValue,
                record.feelingTag != null ? record.feelingTag : "Resting",
                record.notes != null ? record.notes : ""
        );

        RetrofitClient.getApiService().createHeartRateRecord(
                "Bearer " + token,
                request
        ).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Heart rate record synced to backend");
                } else {
                    Log.e(TAG, "Failed to sync heart rate record: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e(TAG, "Error syncing heart rate record", t);
            }
        });
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

    // Fetch records from backend (for syncing with server)
    public MutableLiveData<List<HeartRateRecord>> getRemoteRecords() {
        MutableLiveData<List<HeartRateRecord>> result = new MutableLiveData<>();
        String token = authPrefs.getToken();

        if (token == null) {
            Log.w(TAG, "Not logged in, cannot fetch remote records");
            result.setValue(null);
            return result;
        }

        RetrofitClient.getApiService().getHeartRateRecords("Bearer " + token)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            result.setValue(null); // Placeholder - frontend can convert if needed
                            Log.d(TAG, "Remote records fetched: " + response.body().data.size());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Log.e(TAG, "Error fetching remote records", t);
                        result.setValue(null);
                    }
                });

        return result;
    }
}