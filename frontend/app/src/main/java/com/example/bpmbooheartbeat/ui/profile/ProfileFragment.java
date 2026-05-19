package com.example.bpmbooheartbeat.ui.profile;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.bpmbooheartbeat.R;
import com.example.bpmbooheartbeat.data.api.ApiRequest;
import com.example.bpmbooheartbeat.data.api.ApiResponse;
import com.example.bpmbooheartbeat.data.api.RetrofitClient;
import com.example.bpmbooheartbeat.utils.AuthPreferences;
import com.example.bpmbooheartbeat.utils.LocaleHelper;
import com.example.bpmbooheartbeat.utils.ProfileImageUtils;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private ImageView imgAvatarProfile;

    private TextView tvDateOfBirth;
    private TextView tvLanguage;
    private TextView tvLogout;
    private TextView tvDeleteAccount;

    private EditText edtFullName;
    private EditText edtPhoneNumber;
    private EditText edtEmail;
    private EditText edtEmergencyName;
    private EditText edtEmergencyPhone;

    private RadioGroup radioGenderGroup;

    private ActivityResultLauncher<String> pickImageLauncher;
    private AuthPreferences authPrefs;

    private String currentAvatarUrl = "";

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View root = inflater.inflate(
                R.layout.fragment_profile,
                container,
                false
        );

        authPrefs = new AuthPreferences(requireContext());

        bindViews(root);
        setupImagePicker();
        setupListeners(root);

        ProfileImageUtils.loadAvatar(
                requireContext(),
                imgAvatarProfile
        );

        loadProfileFromBackend();

        return root;
    }

    private void bindViews(View root) {
        imgAvatarProfile = root.findViewById(R.id.imgAvatarProfile);

        tvDateOfBirth = root.findViewById(R.id.tvDateOfBirth);
        tvLanguage = root.findViewById(R.id.tvLanguage);
        tvLogout = root.findViewById(R.id.tvLogout);
        tvDeleteAccount = root.findViewById(R.id.tvDeleteAccount);

        edtFullName = root.findViewById(R.id.edtFullName);
        edtPhoneNumber = root.findViewById(R.id.edtPhoneNumber);
        edtEmail = root.findViewById(R.id.edtEmail);
        edtEmergencyName = root.findViewById(R.id.edtEmergencyName);
        edtEmergencyPhone = root.findViewById(R.id.edtEmergencyPhone);

        radioGenderGroup = root.findViewById(R.id.radioGenderGroup);
    }

    private void setupImagePicker() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        String base64Image = ProfileImageUtils.convertImageToBase64(
                                requireContext(),
                                uri
                        );

                        if (base64Image != null) {
                            uploadAvatarToServer(base64Image);
                        } else {
                            Toast.makeText(
                                    requireContext(),
                                    "Failed to process image",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                }
        );
    }

    private void setupListeners(View root) {
        imgAvatarProfile.setOnClickListener(
                v -> pickImageLauncher.launch("image/*")
        );

        tvDateOfBirth.setOnClickListener(
                v -> showDatePicker()
        );

        tvLanguage.setOnClickListener(
                v -> showLanguageDialog()
        );

        tvLogout.setOnClickListener(v -> {
            authPrefs.clearAuthData();

            Toast.makeText(
                    requireContext(),
                    "Logged out",
                    Toast.LENGTH_SHORT
            ).show();

            Navigation.findNavController(v)
                    .navigate(R.id.onboardingFragment);
        });

        tvDeleteAccount.setOnClickListener(
                v -> showDeleteConfirmDialog()
        );

        int saveButtonId = getResources().getIdentifier(
                "btnSaveProfile",
                "id",
                requireContext().getPackageName()
        );

        if (saveButtonId != 0) {
            View btnSaveProfile = root.findViewById(saveButtonId);

            if (btnSaveProfile != null) {
                btnSaveProfile.setOnClickListener(v -> updateProfileToBackend());
            }
        }
    }

    private void loadProfileFromBackend() {
        String userId = authPrefs.getUserId();
        String rawToken = authPrefs.getToken();

        if (userId == null || userId.isEmpty() || rawToken == null || rawToken.isEmpty()) {
            Toast.makeText(
                    requireContext(),
                    "User session not found",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String token = "Bearer " + rawToken;

        RetrofitClient.getApiService()
                .getProfile(token, userId)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse apiResponse = response.body();

                            if (apiResponse.success && apiResponse.user != null) {
                                fillProfileForm(apiResponse.user);
                            } else {
                                Toast.makeText(
                                        requireContext(),
                                        apiResponse.message != null
                                                ? apiResponse.message
                                                : "Failed to load profile",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        } else {
                            Toast.makeText(
                                    requireContext(),
                                    "Failed to load profile",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Toast.makeText(
                                requireContext(),
                                "Profile error: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void fillProfileForm(ApiResponse.UserData user) {
        edtFullName.setText(user.fullName != null ? user.fullName : "");
        edtPhoneNumber.setText(user.phone != null ? user.phone : "");
        edtEmail.setText(user.email != null ? user.email : "");

        if (user.dob != null && !user.dob.isEmpty()) {
            tvDateOfBirth.setText(user.dob);
            tvDateOfBirth.setTextColor(
                    requireContext().getColor(R.color.text_primary)
            );
        } else {
            tvDateOfBirth.setText(getString(R.string.select_date_of_birth));
        }

        selectGender(user.gender);

        edtEmergencyName.setText(
                user.emergencyName != null ? user.emergencyName : ""
        );

        edtEmergencyPhone.setText(
                user.emergencyPhone != null ? user.emergencyPhone : ""
        );

        currentAvatarUrl = user.avatarUrl != null ? user.avatarUrl : "";

        if (!currentAvatarUrl.isEmpty()) {
            ProfileImageUtils.loadAvatar(
                    requireContext(),
                    imgAvatarProfile,
                    currentAvatarUrl
            );
        }
    }

    private void updateProfileToBackend() {
        String userId = authPrefs.getUserId();
        String rawToken = authPrefs.getToken();

        if (userId == null || userId.isEmpty() || rawToken == null || rawToken.isEmpty()) {
            Toast.makeText(
                    requireContext(),
                    "User session not found",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String fullName = edtFullName.getText().toString().trim();
        String phone = edtPhoneNumber.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String dob = tvDateOfBirth.getText().toString().trim();
        String gender = getSelectedGender();
        String emergencyName = edtEmergencyName.getText().toString().trim();
        String emergencyPhone = edtEmergencyPhone.getText().toString().trim();

        if (!validateProfileForm(fullName, phone, email, dob, gender)) {
            return;
        }

        ApiRequest.ProfileRequest request = new ApiRequest.ProfileRequest(
                fullName,
                phone,
                email,
                dob,
                gender,
                emergencyName,
                emergencyPhone,
                currentAvatarUrl
        );

        String token = "Bearer " + rawToken;

        RetrofitClient.getApiService()
                .updateProfile(token, userId, request)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse apiResponse = response.body();

                            if (apiResponse.success) {
                                Toast.makeText(
                                        requireContext(),
                                        "Profile updated successfully",
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadProfileFromBackend();
                            } else {
                                Toast.makeText(
                                        requireContext(),
                                        apiResponse.message != null
                                                ? apiResponse.message
                                                : "Failed to update profile",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        } else {
                            Toast.makeText(
                                    requireContext(),
                                    "Failed to update profile",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Toast.makeText(
                                requireContext(),
                                "Update error: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private boolean validateProfileForm(
            String fullName,
            String phone,
            String email,
            String dob,
            String gender
    ) {
        if (fullName.isEmpty()) {
            edtFullName.setError("Full name is required");
            return false;
        }

        if (phone.isEmpty()) {
            edtPhoneNumber.setError("Phone number is required");
            return false;
        }

        if (email.isEmpty()) {
            edtEmail.setError("Email is required");
            return false;
        }

        if (dob.isEmpty() || dob.equals(getString(R.string.select_date_of_birth))) {
            Toast.makeText(
                    requireContext(),
                    "Date of birth is required",
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        }

        if (gender.isEmpty()) {
            Toast.makeText(
                    requireContext(),
                    "Please select gender",
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        }

        return true;
    }

    private String getSelectedGender() {
        int checkedId = radioGenderGroup.getCheckedRadioButtonId();

        if (checkedId == -1) {
            return "";
        }

        RadioButton selectedButton = radioGenderGroup.findViewById(checkedId);

        if (selectedButton == null) {
            return "";
        }

        String gender = selectedButton.getText().toString().trim();

        if (gender.equalsIgnoreCase("Male")) {
            return "Male";
        }

        if (gender.equalsIgnoreCase("Female")) {
            return "Female";
        }

        if (gender.equalsIgnoreCase("Other")) {
            return "Other";
        }

        return gender;
    }

    private void selectGender(String gender) {
        if (gender == null || gender.isEmpty()) {
            radioGenderGroup.clearCheck();
            return;
        }

        String targetGender = gender.trim();

        for (int i = 0; i < radioGenderGroup.getChildCount(); i++) {
            View child = radioGenderGroup.getChildAt(i);

            if (child instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) child;
                String text = radioButton.getText().toString().trim();

                if (text.equalsIgnoreCase(targetGender)) {
                    radioButton.setChecked(true);
                    return;
                }
            }
        }

        radioGenderGroup.clearCheck();
    }

    private void uploadAvatarToServer(String avatarBase64) {
        String userId = authPrefs.getUserId();
        String rawToken = authPrefs.getToken();

        if (userId == null || userId.isEmpty() || rawToken == null || rawToken.isEmpty()) {
            Toast.makeText(
                    requireContext(),
                    "User session not found",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String token = "Bearer " + rawToken;

        ApiRequest.AvatarRequest request = new ApiRequest.AvatarRequest(avatarBase64);

        RetrofitClient.getApiService()
                .uploadAvatar(token, userId, request)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse apiResponse = response.body();

                            if (apiResponse.success) {
                                currentAvatarUrl = avatarBase64;

                                ProfileImageUtils.saveAvatarUrl(
                                        requireContext(),
                                        avatarBase64
                                );

                                ProfileImageUtils.loadAvatar(
                                        requireContext(),
                                        imgAvatarProfile,
                                        avatarBase64
                                );

                                Toast.makeText(
                                        requireContext(),
                                        "Avatar updated successfully",
                                        Toast.LENGTH_SHORT
                                ).show();
                            } else {
                                Toast.makeText(
                                        requireContext(),
                                        apiResponse.message != null
                                                ? apiResponse.message
                                                : "Failed to upload avatar",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        } else {
                            Toast.makeText(
                                    requireContext(),
                                    "Upload failed",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Toast.makeText(
                                requireContext(),
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = String.format(
                            "%02d/%02d/%04d",
                            dayOfMonth,
                            month + 1,
                            year
                    );

                    tvDateOfBirth.setText(selectedDate);

                    tvDateOfBirth.setTextColor(
                            requireContext().getColor(R.color.text_primary)
                    );
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private void showLanguageDialog() {
        String[] languages = {
                "English",
                "Tiếng Việt"
        };

        new AlertDialog.Builder(requireContext())
                .setTitle("Choose Language")
                .setItems(languages, (dialog, which) -> {
                    if (which == 0) {
                        LocaleHelper.saveLanguage(
                                requireContext(),
                                "en"
                        );
                    } else {
                        LocaleHelper.saveLanguage(
                                requireContext(),
                                "vi"
                        );
                    }

                    requireActivity().recreate();
                })
                .show();
    }

    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Account")
                .setMessage(
                        "Are you sure? Deleting your account will permanently remove all data associated with this user."
                )
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteAccountFromBackend();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAccountFromBackend() {
        String rawToken = authPrefs.getToken();

        if (rawToken == null || rawToken.isEmpty()) {
            Toast.makeText(
                    requireContext(),
                    "User session not found",
                    Toast.LENGTH_SHORT
            ).show();

            clearProfileData();

            Navigation.findNavController(requireView())
                    .navigate(R.id.onboardingFragment);

            return;
        }

        String token = "Bearer " + rawToken;

        RetrofitClient.getApiService()
                .deleteAccount(token)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().success) {
                            clearProfileData();

                            Toast.makeText(
                                    requireContext(),
                                    "Account deleted successfully",
                                    Toast.LENGTH_SHORT
                            ).show();

                            Navigation.findNavController(requireView())
                                    .navigate(R.id.onboardingFragment);
                        } else {
                            Toast.makeText(
                                    requireContext(),
                                    "Failed to delete account",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Toast.makeText(
                                requireContext(),
                                "Delete error: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void clearProfileData() {
        authPrefs.clearAuthData();

        edtFullName.setText("");
        edtPhoneNumber.setText("");
        edtEmail.setText("");
        edtEmergencyName.setText("");
        edtEmergencyPhone.setText("");

        tvDateOfBirth.setText(
                getString(R.string.select_date_of_birth)
        );

        radioGenderGroup.clearCheck();
        currentAvatarUrl = "";
    }
}