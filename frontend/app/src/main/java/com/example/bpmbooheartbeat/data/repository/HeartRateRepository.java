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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HeartRateRepository {

    private static final String TAG = "HeartRateRepository";

    private final HeartRateDao dao;
    private final AuthPreferences authPrefs;
    private final ExecutorService dbWriteExecutor = Executors.newSingleThreadExecutor();

    public HeartRateRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        this.dao = db.heartRateDao();
        this.authPrefs = new AuthPreferences(application);
    }

    public void insert(HeartRateRecord record) {
        dbWriteExecutor.execute(() -> {
            dao.insert(record);
            syncToBackend(record);
        });
    }

    private void syncToBackend(HeartRateRecord record) {
        String token = authPrefs.getToken();
        String userId = authPrefs.getUserId();

        Log.d(TAG, "Current logged-in userId: " + userId);
        Log.d(TAG, "Current token exists: " + (token != null && !token.isEmpty()));

        if (token == null || token.isEmpty() || userId == null || userId.isEmpty()) {
            Log.w(TAG, "Not logged in, skipping backend sync");
            return;
        }

        ApiRequest.HeartRateRecordRequest request = new ApiRequest.HeartRateRecordRequest(
                userId,
                record.bpmValue,
                record.feelingTag != null ? record.feelingTag : "Resting",
                record.notes != null ? record.notes : "",
                toIsoDate(record.timestamp)
        );

        RetrofitClient.getApiService()
                .createHeartRateRecord("Bearer " + token, request)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Heart rate record synced to MongoDB");
                        } else {
                            Log.e(TAG, "Failed to sync heart rate record: " + response.code() + " " + response.message());
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

    // Local Room fallback, kept for old code if needed
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

    // Main source for History + Analytics: MongoDB/Render backend
    public MutableLiveData<List<HeartRateRecord>> getRemoteRecords() {
        MutableLiveData<List<HeartRateRecord>> result = new MutableLiveData<>();

        String token = authPrefs.getToken();
        String userId = authPrefs.getUserId();

        if (token == null || token.isEmpty() || userId == null || userId.isEmpty()) {
            Log.w(TAG, "Not logged in, cannot fetch remote records");
            result.setValue(new ArrayList<>());
            return result;
        }

        RetrofitClient.getApiService()
                .getHeartRateRecords("Bearer " + token, userId)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                            List<HeartRateRecord> records = new ArrayList<>();

                            for (ApiResponse.HeartRateRecordData remoteRecord : response.body().data) {
                                HeartRateRecord record = convertRemoteRecord(remoteRecord);
                                records.add(record);
                            }

                            Log.d(TAG, "Remote records fetched from MongoDB: " + records.size());
                            result.setValue(records);
                        } else {
                            Log.e(TAG, "Failed to fetch remote records: " + response.code() + " " + response.message());
                            result.setValue(new ArrayList<>());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Log.e(TAG, "Error fetching remote records", t);
                        result.setValue(new ArrayList<>());
                    }
                });

        return result;
    }

    private HeartRateRecord convertRemoteRecord(ApiResponse.HeartRateRecordData remoteRecord) {
        long timestamp = parseTimestamp(remoteRecord.timestamp);

        HeartRateRecord record = new HeartRateRecord(
                remoteRecord.bpmValue,
                timestamp,
                30000
        );

        record.feelingTag = remoteRecord.feelingTag != null ? remoteRecord.feelingTag : "Resting";
        record.notes = remoteRecord.note != null ? remoteRecord.note : "";

        // Use timestamp as stable local ID for DiffUtil
        record.id = timestamp;

        return record;
    }

    private long parseTimestamp(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            return System.currentTimeMillis();
        }

        try {
            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                    Locale.US
            );
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            return format.parse(timestamp).getTime();
        } catch (Exception ignored) {
        }

        try {
            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss'Z'",
                    Locale.US
            );
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            return format.parse(timestamp).getTime();
        } catch (Exception ignored) {
        }

        return System.currentTimeMillis();
    }

    private String toIsoDate(long timestamp) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                    Locale.US
            );
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            return format.format(timestamp);
        } catch (Exception e) {
            return null;
        }
    }
}