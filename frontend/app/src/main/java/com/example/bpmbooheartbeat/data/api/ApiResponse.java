package com.example.bpmbooheartbeat.data.api;

import java.util.List;

public class ApiResponse {
    public boolean success;
    public String message;
    public String token;
    public UserData user;
    public List<HeartRateRecordData> data;

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
        public int count;
    }
}
