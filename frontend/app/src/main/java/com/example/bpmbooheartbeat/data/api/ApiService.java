package com.example.bpmbooheartbeat.data.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ApiService {
    // Auth endpoints
    @POST("/api/auth/register")
    Call<ApiResponse> register(@Body ApiRequest.RegisterRequest request);

    @POST("/api/auth/login")
    Call<ApiResponse> login(@Body ApiRequest.LoginRequest request);

    // Profile endpoints
    @GET("/api/profile/{userId}")
    Call<ApiResponse> getProfile(@Path("userId") String userId, @Header("Authorization") String token);

    @PUT("/api/profile/{userId}")
    Call<ApiResponse> updateProfile(@Path("userId") String userId, @Header("Authorization") String token, @Body ApiRequest.RegisterRequest request);

    // Heart rate endpoints
    @POST("/api/heart-rates")
    Call<ApiResponse> createHeartRateRecord(@Header("Authorization") String token, @Body ApiRequest.HeartRateRecordRequest request);

    @GET("/api/heart-rates")
    Call<ApiResponse> getHeartRateRecords(@Header("Authorization") String token);

    @GET("/api/heart-rates/stats")
    Call<ApiResponse> getHeartRateStats(@Header("Authorization") String token, 
                                        @Query("period") String period);

    // Insight endpoints
    @GET("/api/insight")
    Call<ApiResponse> getInsight(@Header("Authorization") String token);
}
