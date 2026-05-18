package com.example.bpmbooheartbeat.data.api;

public class ApiRequest {
    // Register request
    public static class RegisterRequest {
        public String email;
        public String password;
        public String fullName;
        public String phone;
        public String dob;
        public String gender;
        public String emergencyName;
        public String emergencyPhone;
        public String avatarUrl;

        public RegisterRequest(String email, String password, String fullName, String phone, 
                             String dob, String gender, String emergencyName, 
                             String emergencyPhone, String avatarUrl) {
            this.email = email;
            this.password = password;
            this.fullName = fullName;
            this.phone = phone;
            this.dob = dob;
            this.gender = gender;
            this.emergencyName = emergencyName;
            this.emergencyPhone = emergencyPhone;
            this.avatarUrl = avatarUrl;
        }
    }

    // Login request
    public static class LoginRequest {
        public String email;
        public String password;

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    // Heart rate record request
    public static class HeartRateRecordRequest {
        public int bpmValue;
        public String feelingTag;
        public String note;

        public HeartRateRecordRequest(int bpmValue, String feelingTag, String note) {
            this.bpmValue = bpmValue;
            this.feelingTag = feelingTag;
            this.note = note;
        }
    }
}
