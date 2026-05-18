package com.example.bpmbooheartbeat.ui.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.bpmbooheartbeat.R;
import com.example.bpmbooheartbeat.data.api.ApiRequest;
import com.example.bpmbooheartbeat.data.api.ApiResponse;
import com.example.bpmbooheartbeat.data.api.RetrofitClient;
import com.example.bpmbooheartbeat.utils.AuthPreferences;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OnboardingFragment extends Fragment {
    private AuthPreferences authPrefs;
    private boolean isLoginMode = true;

    private View authFormSection;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtPasswordConfirm;
    private EditText edtFullName;
    private MaterialButton btnSubmit;
    private TextView tvToggleMode;
    private ProgressBar progressBar;

    private View registerOnlySection;
    private EditText edtPhone;
    private EditText edtEmergencyName;
    private EditText edtEmergencyPhone;

    private TextInputLayout layoutEmail;
    private TextInputLayout layoutPassword;
    private TextInputLayout layoutPasswordConfirm;
    private TextInputLayout layoutFullName;
    private TextInputLayout layoutPhone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_onboarding, container, false);
        authPrefs = new AuthPreferences(requireContext());

        if (authPrefs.isLoggedIn()) {
            Navigation.findNavController(root).navigate(R.id.measureFragment);
            return root;
        }

        bindViews(root);
        setupListeners();
        showLoginMode();
        return root;
    }

    private void bindViews(View root) {
        authFormSection = root.findViewById(R.id.authFormSection);
        edtEmail = root.findViewById(R.id.edtEmail);
        edtPassword = root.findViewById(R.id.edtPassword);
        edtPasswordConfirm = root.findViewById(R.id.edtPasswordConfirm);
        edtFullName = root.findViewById(R.id.edtFullName);
        btnSubmit = root.findViewById(R.id.btnSubmit);
        tvToggleMode = root.findViewById(R.id.tvToggleMode);
        progressBar = root.findViewById(R.id.progressBar);

        layoutEmail = root.findViewById(R.id.layoutEmail);
        layoutPassword = root.findViewById(R.id.layoutPassword);
        layoutPasswordConfirm = root.findViewById(R.id.layoutPasswordConfirm);
        layoutFullName = root.findViewById(R.id.layoutFullName);
        layoutPhone = root.findViewById(R.id.layoutPhone);

        registerOnlySection = root.findViewById(R.id.registerOnlySection);
        edtPhone = root.findViewById(R.id.edtPhone);
        edtEmergencyName = root.findViewById(R.id.edtEmergencyName);
        edtEmergencyPhone = root.findViewById(R.id.edtEmergencyPhone);
    }

    private void setupListeners() {
        btnSubmit.setOnClickListener(v -> {
            if (isLoginMode) {
                handleLogin();
            } else {
                handleRegister();
            }
        });

        tvToggleMode.setOnClickListener(v -> toggleAuthMode());
    }

    private void showLoginMode() {
        isLoginMode = true;
        authFormSection.setVisibility(View.VISIBLE);
        tvToggleMode.setVisibility(View.VISIBLE);
        registerOnlySection.setVisibility(View.GONE);
        edtPasswordConfirm.setVisibility(View.GONE);
        edtFullName.setVisibility(View.GONE);
        btnSubmit.setText("Login");
        tvToggleMode.setText("Don't have an account? Sign Up");
    }

    private void showRegisterMode() {
        isLoginMode = false;
        authFormSection.setVisibility(View.VISIBLE);
        tvToggleMode.setVisibility(View.VISIBLE);
        registerOnlySection.setVisibility(View.VISIBLE);
        edtPasswordConfirm.setVisibility(View.VISIBLE);
        edtFullName.setVisibility(View.VISIBLE);
        btnSubmit.setText("Sign Up");
        tvToggleMode.setText("Already have an account? Login");
    }

    private void toggleAuthMode() {
        if (isLoginMode) {
            showRegisterMode();
        } else {
            showLoginMode();
        }
    }

    private void handleLogin() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);

        ApiRequest.LoginRequest request = new ApiRequest.LoginRequest(email, password);
        RetrofitClient.getApiService().login(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse body = response.body();
                    if (body.success && body.user != null) {
                        authPrefs.saveAuthData(body.token, body.user._id, body.user.email);
                        Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(requireView()).navigate(R.id.measureFragment);
                    } else {
                        Toast.makeText(requireContext(), body.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMessage = "Login failed";

                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorMessage = response.message();
                    }

                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearValidationErrors() {
        layoutEmail.setError(null);
        layoutPassword.setError(null);
        layoutPasswordConfirm.setError(null);
        layoutFullName.setError(null);
        layoutPhone.setError(null);
    }

    private boolean validateRegisterForm(String email,
                                         String password,
                                         String passwordConfirm,
                                         String fullName,
                                         String phone) {
        clearValidationErrors();
        boolean isValid = true;

        if (email.isEmpty()) {
            layoutEmail.setError("Required");
            isValid = false;
        }
        if (password.isEmpty()) {
            layoutPassword.setError("Required");
            isValid = false;
        }
        if (passwordConfirm.isEmpty()) {
            layoutPasswordConfirm.setError("Required");
            isValid = false;
        }
        if (fullName.isEmpty()) {
            layoutFullName.setError("Required");
            isValid = false;
        }
        if (phone.isEmpty()) {
            layoutPhone.setError("Required");
            isValid = false;
        }

        if (!isValid) {
            return false;
        }

        if (!password.equals(passwordConfirm)) {
            layoutPasswordConfirm.setError("Passwords do not match");
            return false;
        }

        if (password.length() < 6) {
            layoutPassword.setError("Password must be at least 6 characters");
            return false;
        }

        return true;
    }

    private void handleRegister() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String passwordConfirm = edtPasswordConfirm.getText().toString().trim();
        String fullName = edtFullName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String emergencyName = edtEmergencyName.getText().toString().trim();
        String emergencyPhone = edtEmergencyPhone.getText().toString().trim();

        if (!validateRegisterForm(email, password, passwordConfirm, fullName, phone)) {
            Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);

        ApiRequest.RegisterRequest request = new ApiRequest.RegisterRequest(
                email, password, fullName, phone, "", "", emergencyName, emergencyPhone, ""
        );

        RetrofitClient.getApiService().register(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse body = response.body();
                    if (body.success && body.user != null) {
                        authPrefs.saveAuthData(body.token, body.user._id, body.user.email);
                        Toast.makeText(requireContext(), "Registration successful!", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(requireView()).navigate(R.id.measureFragment);
                    } else {
                        Toast.makeText(requireContext(), body.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMessage = "Registration failed";

                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorMessage = response.message();
                    }

                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}