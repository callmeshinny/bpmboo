package com.example.bpmbooheartbeat.data.api;

public class ApiRequest {

    public static class LoginRequest {
        public String email;
        public String password;

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    public static class RegisterRequest {
        public String name;
        public String email;
        public String password;

        // Các field này giữ lại nếu backend sau này dùng profile,
        // nhưng register backend hiện tại chỉ cần name, email, password.
        public String phone;
        public String dob;
        public String gender;
        public String emergencyName;
        public String emergencyPhone;
        public String avatarUrl;

        public RegisterRequest(
                String email,
                String password,
                String fullName,
                String phone,
                String dob,
                String gender,
                String emergencyName,
                String emergencyPhone,
                String avatarUrl
        ) {
            this.name = fullName;
            this.email = email;
            this.password = password;
            this.phone = phone;
            this.dob = dob;
            this.gender = gender;
            this.emergencyName = emergencyName;
            this.emergencyPhone = emergencyPhone;
            this.avatarUrl = avatarUrl;
        }
    }

    public static class HeartRateRecordRequest {
        public String userId;
        public int bpmValue;
        public String feelingTag;
        public String note;
        public String timestamp;

        public HeartRateRecordRequest(int bpmValue, String feelingTag, String note) {
            this.userId = null;
            this.bpmValue = bpmValue;
            this.feelingTag = feelingTag;
            this.note = note;
            this.timestamp = null;
        }

        public HeartRateRecordRequest(
                String userId,
                int bpmValue,
                String feelingTag,
                String note
        ) {
            this.userId = userId;
            this.bpmValue = bpmValue;
            this.feelingTag = feelingTag;
            this.note = note;
            this.timestamp = null;
        }

        public HeartRateRecordRequest(
                String userId,
                int bpmValue,
                String feelingTag,
                String note,
                String timestamp
        ) {
            this.userId = userId;
            this.bpmValue = bpmValue;
            this.feelingTag = feelingTag;
            this.note = note;
            this.timestamp = timestamp;
        }
    }

    public static class ProfileRequest {
        public String fullName;
        public String phone;
        public String email;
        public String dob;
        public String gender;
        public String emergencyName;
        public String emergencyPhone;
        public String avatarUrl;

        public ProfileRequest(
                String fullName,
                String phone,
                String dob,
                String gender,
                String emergencyName,
                String emergencyPhone,
                String avatarUrl
        ) {
            this.fullName = fullName;
            this.phone = phone;
            this.email = null;
            this.dob = dob;
            this.gender = gender;
            this.emergencyName = emergencyName;
            this.emergencyPhone = emergencyPhone;
            this.avatarUrl = avatarUrl;
        }

        public ProfileRequest(
                String fullName,
                String phone,
                String email,
                String dob,
                String gender,
                String emergencyName,
                String emergencyPhone,
                String avatarUrl
        ) {
            this.fullName = fullName;
            this.phone = phone;
            this.email = email;
            this.dob = dob;
            this.gender = gender;
            this.emergencyName = emergencyName;
            this.emergencyPhone = emergencyPhone;
            this.avatarUrl = avatarUrl;
        }
    }

    public static class OtpRequest {
        public String email;
        public String purpose;

        public OtpRequest(String email) {
            this.email = email;
            this.purpose = "register";
        }

        public OtpRequest(String email, String purpose) {
            this.email = email;
            this.purpose = purpose;
        }
    }

    public static class VerifyOtpRequest {
        public String email;
        public String otp;
        public String purpose;

        public VerifyOtpRequest(String email, String otp) {
            this.email = email;
            this.otp = otp;
            this.purpose = "register";
        }

        public VerifyOtpRequest(String email, String otp, String purpose) {
            this.email = email;
            this.otp = otp;
            this.purpose = purpose;
        }
    }

    public static class AvatarRequest {
        public String avatarBase64;

        public AvatarRequest(String avatarBase64) {
            this.avatarBase64 = avatarBase64;
        }
    }
}