package com.example.bpmbooheartbeat.data.api;

import java.util.List;

public class ApiResponse {
    public boolean success;
    public String message;
    public String token;

    // Auth / Profile response
    public UserData user;

    // Heart-rate list response
    public List<HeartRateRecordData> data;

    // Heart-rate single record response, nếu backend trả record riêng
    public HeartRateRecordData record;

    // Stats response
    public StatsData stats;

    // Insight response
    public InsightResponseData insightData;

    // Some backend responses may return nested object as data
    public Object rawData;

    public static class UserData {
        public String _id;
        public String email;
        public String fullName;
        public String phone;
        public String dob;
        public String gender;
        public String emergencyName;
        public String emergencyPhone;
        public String avatarUrl;
        public String role;
        public boolean isEmailVerified;
        public String createdAt;
        public String updatedAt;
    }

    public static class HeartRateRecordData {
        public String _id;
        public String userId;
        public int bpmValue;
        public String timestamp;
        public String feelingTag;
        public String note;
        public String createdAt;
        public String updatedAt;
    }

    public static class StatsData {
        public double averageBpm;
        public int maxBpm;
        public int minBpm;
        public int totalRecords;
        public int count;
        public String trend;
    }

    public static class InsightResponseData {
        public StatsData stats;
        public String insight;
    }
}