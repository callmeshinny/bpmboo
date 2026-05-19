package com.example.bpmbooheartbeat.data.api;

import java.util.List;

public class ApiResponse {
    public boolean success;
    public String message;
    public String token;
    public String email;

    public UserData user;

    public List<HeartRateRecordData> data;
    public HeartRateRecordData record;
    public StatsData stats;
    public InsightResponseData insightData;
    public Object rawData;

    public static class UserData {
        public String id;
        public String _id;

        public String name;
        public String email;
        public String fullName;
        public String phone;
        public String dob;
        public String gender;
        public String emergencyName;
        public String emergencyPhone;
        public String avatarUrl;
        public String role;

        public boolean isVerified;
        public boolean isEmailVerified;

        public String createdAt;
        public String updatedAt;

        public String getSafeId() {
            if (_id != null && !_id.isEmpty()) {
                return _id;
            }

            return id;
        }

        public String getSafeName() {
            if (fullName != null && !fullName.isEmpty()) {
                return fullName;
            }

            return name;
        }
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