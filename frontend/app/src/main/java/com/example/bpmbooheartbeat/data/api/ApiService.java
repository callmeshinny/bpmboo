package com.example.bpmbooheartbeat.data.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Header;

public interface ApiService {

    // Health check
    @GET("api/health")
    Call<ApiResponse> healthCheck();


    // Auth
    @POST("api/auth/register")
    Call<ApiResponse> register(
            @Body ApiRequest.RegisterRequest request
    );

    @POST("api/auth/login")
    Call<ApiResponse> login(
            @Body ApiRequest.LoginRequest request
    );


    // OTP for register / login
    @POST("api/otp/request")
    Call<ApiResponse> requestOtp(
            @Body ApiRequest.OtpRequest request
    );

    @POST("api/otp/verify")
    Call<ApiResponse> verifyOtp(
            @Body ApiRequest.VerifyOtpRequest request
    );


    // Heart-rate records
    @POST("api/heart-rates")
    Call<ApiResponse> createHeartRateRecord(
            @Header("Authorization") String token,
            @Body ApiRequest.HeartRateRecordRequest request
    );

    @GET("api/heart-rates/{userId}")
    Call<ApiResponse> getHeartRateRecords(
            @Header("Authorization") String token,
            @Path("userId") String userId
    );

    @GET("api/heart-rates/{userId}/stats")
    Call<ApiResponse> getHeartRateStats(
            @Header("Authorization") String token,
            @Path("userId") String userId
    );

    @DELETE("api/heart-rates/{recordId}")
    Call<ApiResponse> deleteHeartRateRecord(
            @Header("Authorization") String token,
            @Path("recordId") String recordId
    );


    // Profile
    @GET("api/profile/{userId}")
    Call<ApiResponse> getProfile(
            @Header("Authorization") String token,
            @Path("userId") String userId
    );

    @PUT("api/profile/{userId}")
    Call<ApiResponse> updateProfile(
            @Header("Authorization") String token,
            @Path("userId") String userId,
            @Body ApiRequest.ProfileRequest request
    );

    // Insight / Analytics
    @GET("api/insight/summary/{userId}")
    Call<ApiResponse> getInsightSummary(
            @Header("Authorization") String token,
            @Path("userId") String userId
    );

    // Avatar / Profile Image
    @POST("api/avatar/{userId}")
    Call<ApiResponse> uploadAvatar(
            @Header("Authorization") String token,
            @Path("userId") String userId,
            @Body ApiRequest.AvatarRequest request
    );

    @GET("api/avatar/{userId}")
    Call<ApiResponse> getAvatar(
            @Path("userId") String userId
    );
}