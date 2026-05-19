package com.example.bpmbooheartbeat.ui.onboarding;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.bpmbooheartbeat.R;
import com.example.bpmbooheartbeat.data.api.ApiRequest;
import com.example.bpmbooheartbeat.data.api.ApiResponse;
import com.example.bpmbooheartbeat.data.api.RetrofitClient;
import com.example.bpmbooheartbeat.utils.AuthPreferences;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

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
    private EditText edtPhone;
    private EditText edtDob;
    private EditText edtEmergencyName;
    private EditText edtEmergencyPhone;

    private RadioGroup rgGender;

    private MaterialButton btnSubmit;
    private TextView tvToggleMode;
    private ProgressBar progressBar;

    private View registerOnlySection;

    private TextInputLayout layoutEmail;
    private TextInputLayout layoutPassword;
    private TextInputLayout layoutPasswordConfirm;
    private TextInputLayout layoutFullName;
    private TextInputLayout layoutPhone;
    private TextInputLayout layoutDob;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View root = inflater.inflate(R.layout.fragment_onboarding, container, false);

        authPrefs = new AuthPreferences(requireContext());

        bindViews(root);
        setupListeners();
        showLoginMode();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (authPrefs != null && authPrefs.isLoggedIn()) {
            Navigation.findNavController(view).navigate(R.id.measureFragment);
        }
    }

    private void bindViews(View root) {
        authFormSection = root.findViewById(R.id.authFormSection);

        edtEmail = root.findViewById(R.id.edtEmail);
        edtPassword = root.findViewById(R.id.edtPassword);
        edtPasswordConfirm = root.findViewById(R.id.edtPasswordConfirm);
        edtFullName = root.findViewById(R.id.edtFullName);
        edtPhone = root.findViewById(R.id.edtPhone);
        edtDob = root.findViewById(R.id.edtDob);
        edtEmergencyName = root.findViewById(R.id.edtEmergencyName);
        edtEmergencyPhone = root.findViewById(R.id.edtEmergencyPhone);

        rgGender = root.findViewById(R.id.rgGender);

        btnSubmit = root.findViewById(R.id.btnSubmit);
        tvToggleMode = root.findViewById(R.id.tvToggleMode);
        progressBar = root.findViewById(R.id.progressBar);

        layoutEmail = root.findViewById(R.id.layoutEmail);
        layoutPassword = root.findViewById(R.id.layoutPassword);
        layoutPasswordConfirm = root.findViewById(R.id.layoutPasswordConfirm);
        layoutFullName = root.findViewById(R.id.layoutFullName);
        layoutPhone = root.findViewById(R.id.layoutPhone);
        layoutDob = root.findViewById(R.id.layoutDob);

        registerOnlySection = root.findViewById(R.id.registerOnlySection);
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

        edtDob.setOnClickListener(v -> showDatePicker());
    }

    private void showLoginMode() {
        isLoginMode = true;

        authFormSection.setVisibility(View.VISIBLE);
        registerOnlySection.setVisibility(View.GONE);

        progressBar.setVisibility(View.GONE);
        btnSubmit.setEnabled(true);
        btnSubmit.setText("Login");
        tvToggleMode.setText("Don't have an account? Sign Up");
    }

    private void showRegisterMode() {
        isLoginMode = false;

        authFormSection.setVisibility(View.VISIBLE);
        registerOnlySection.setVisibility(View.VISIBLE);

        progressBar.setVisibility(View.GONE);
        btnSubmit.setEnabled(true);
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

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnSubmit.setEnabled(!isLoading);
        tvToggleMode.setEnabled(!isLoading);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    String dob = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                    edtDob.setText(dob);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private String getSelectedGender() {
        int checkedId = rgGender.getCheckedRadioButtonId();

        if (checkedId == R.id.rbMale) {
            return "Male";
        } else if (checkedId == R.id.rbFemale) {
            return "Female";
        } else if (checkedId == R.id.rbOther) {
            return "Other";
        }

        return "";
    }

    private void clearValidationErrors() {
        if (layoutEmail != null) layoutEmail.setError(null);
        if (layoutPassword != null) layoutPassword.setError(null);
        if (layoutPasswordConfirm != null) layoutPasswordConfirm.setError(null);
        if (layoutFullName != null) layoutFullName.setError(null);
        if (layoutPhone != null) layoutPhone.setError(null);
        if (layoutDob != null) layoutDob.setError(null);
    }

    private boolean validateLoginForm(String email, String password) {
        clearValidationErrors();

        boolean isValid = true;

        if (email.isEmpty()) {
            layoutEmail.setError("Email is required");
            isValid = false;
        }

        if (password.isEmpty()) {
            layoutPassword.setError("Password is required");
            isValid = false;
        }

        return isValid;
    }

    private boolean validateRegisterForm(
            String email,
            String password,
            String passwordConfirm,
            String fullName,
            String phone,
            String dob,
            String gender
    ) {
        clearValidationErrors();

        boolean isValid = true;

        if (email.isEmpty()) {
            layoutEmail.setError("Email is required");
            isValid = false;
        }

        if (password.isEmpty()) {
            layoutPassword.setError("Password is required");
            isValid = false;
        }

        if (passwordConfirm.isEmpty()) {
            layoutPasswordConfirm.setError("Confirm password is required");
            isValid = false;
        }

        if (fullName.isEmpty()) {
            layoutFullName.setError("Full name is required");
            isValid = false;
        }

        if (phone.isEmpty()) {
            layoutPhone.setError("Phone number is required");
            isValid = false;
        }

        if (dob.isEmpty() || dob.equals("Select date of birth")) {
            layoutDob.setError("Date of birth is required");
            isValid = false;
        }

        if (gender.isEmpty()) {
            Toast.makeText(requireContext(), "Please select gender", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (!isValid) {
            return false;
        }

        if (password.length() < 6) {
            layoutPassword.setError("Password must be at least 6 characters");
            return false;
        }

        if (!password.equals(passwordConfirm)) {
            layoutPasswordConfirm.setError("Passwords do not match");
            return false;
        }

        return true;
    }

    private String getErrorMessage(Response<ApiResponse> response, String fallback) {
        String errorMessage = fallback;

        try {
            if (response.errorBody() != null) {
                errorMessage = response.errorBody().string();
            } else if (response.message() != null) {
                errorMessage = response.message();
            }
        } catch (Exception e) {
            errorMessage = fallback;
        }

        return errorMessage;
    }

    private void handleLogin() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (!validateLoginForm(email, password)) {
            Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        ApiRequest.LoginRequest request = new ApiRequest.LoginRequest(email, password);

        RetrofitClient.getApiService().login(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                setLoading(false);

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
                    Toast.makeText(
                            requireContext(),
                            getErrorMessage(response, "Login failed"),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                setLoading(false);
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleRegister() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String passwordConfirm = edtPasswordConfirm.getText().toString().trim();
        String fullName = edtFullName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String dob = edtDob.getText().toString().trim();
        String gender = getSelectedGender();
        String emergencyName = edtEmergencyName.getText().toString().trim();
        String emergencyPhone = edtEmergencyPhone.getText().toString().trim();

        if (!validateRegisterForm(email, password, passwordConfirm, fullName, phone, dob, gender)) {
            Toast.makeText(requireContext(), "Please check your information", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        ApiRequest.RegisterRequest request = new ApiRequest.RegisterRequest(
                email,
                password,
                fullName,
                phone,
                dob,
                gender,
                emergencyName,
                emergencyPhone,
                ""
        );

        RetrofitClient.getApiService().register(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse body = response.body();

                    if (body.success && body.user != null) {
                        Toast.makeText(requireContext(), "Registration successful. Sending OTP...", Toast.LENGTH_SHORT).show();
                        sendOtpAfterRegister(body.token, body.user._id, body.user.email);
                    } else {
                        Toast.makeText(requireContext(), body.message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(
                            requireContext(),
                            getErrorMessage(response, "Registration failed"),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                setLoading(false);
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendOtpAfterRegister(String token, String userId, String email) {
        setLoading(true);

        ApiRequest.OtpRequest otpRequest = new ApiRequest.OtpRequest(email, "register");

        RetrofitClient.getApiService().requestOtp(otpRequest).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    Toast.makeText(requireContext(), "OTP sent to your email", Toast.LENGTH_SHORT).show();
                    showOtpDialog(token, userId, email);
                } else {
                    Toast.makeText(
                            requireContext(),
                            getErrorMessage(response, "Failed to send OTP"),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                setLoading(false);
                Toast.makeText(requireContext(), "OTP error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showOtpDialog(String token, String userId, String email) {
        final EditText otpInput = new EditText(requireContext());
        otpInput.setHint("Enter 6-digit OTP");
        otpInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        otpInput.setPadding(40, 24, 40, 24);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Verify your email")
                .setMessage("We sent a 6-digit OTP to " + email)
                .setView(otpInput)
                .setCancelable(false)
                .setPositiveButton("Verify", null)
                .setNegativeButton("Cancel", (d, which) -> d.dismiss())
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String otp = otpInput.getText().toString().trim();

                if (otp.isEmpty()) {
                    otpInput.setError("OTP is required");
                    return;
                }

                verifyOtp(token, userId, email, otp, dialog);
            });
        });

        dialog.show();
    }

    private void verifyOtp(
            String token,
            String userId,
            String email,
            String otp,
            AlertDialog dialog
    ) {
        setLoading(true);

        ApiRequest.VerifyOtpRequest verifyRequest =
                new ApiRequest.VerifyOtpRequest(email, otp, "register");

        RetrofitClient.getApiService().verifyOtp(verifyRequest).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    dialog.dismiss();

                    authPrefs.saveAuthData(token, userId, email);

                    Toast.makeText(requireContext(), "Email verified successfully!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigate(R.id.measureFragment);
                } else {
                    Toast.makeText(
                            requireContext(),
                            getErrorMessage(response, "Invalid OTP. Please try again."),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                setLoading(false);
                Toast.makeText(requireContext(), "Verify OTP error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}