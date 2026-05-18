package com.example.bpmbooheartbeat.ui.profile;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.bpmbooheartbeat.utils.AuthPreferences;
import com.example.bpmbooheartbeat.utils.LocaleHelper;
import com.example.bpmbooheartbeat.utils.ProfileImageUtils;

import java.util.Calendar;

public class ProfileFragment extends Fragment {

    private ImageView imgAvatarProfile;

    private TextView tvDateOfBirth;
    private TextView tvLanguage;
    private TextView tvLogout;
    private TextView tvDeleteAccount;
    private TextView tvBackProfile;

    private EditText edtFullName;
    private EditText edtPhoneNumber;
    private EditText edtEmail;
    private EditText edtEmergencyName;
    private EditText edtEmergencyPhone;

    private RadioGroup radioGenderGroup;

    private ActivityResultLauncher<String> pickImageLauncher;
    private AuthPreferences authPrefs;

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

        setupListeners();

        ProfileImageUtils.loadAvatar(
                requireContext(),
                imgAvatarProfile
        );

        return root;
    }

    private void bindViews(View root) {

        imgAvatarProfile = root.findViewById(R.id.imgAvatarProfile);

        tvDateOfBirth = root.findViewById(R.id.tvDateOfBirth);
        tvLanguage = root.findViewById(R.id.tvLanguage);
        tvLogout = root.findViewById(R.id.tvLogout);
        tvDeleteAccount = root.findViewById(R.id.tvDeleteAccount);
        tvBackProfile = root.findViewById(R.id.tvBackProfile);

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

                        ProfileImageUtils.saveAvatarUri(
                                requireContext(),
                                uri
                        );

                        imgAvatarProfile.setImageURI(uri);

                        Toast.makeText(
                                requireContext(),
                                "Profile image updated",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }

    private void setupListeners() {

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

        tvBackProfile.setOnClickListener(
                v -> Navigation.findNavController(v).navigateUp()
        );
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

                    clearProfileData();

                    Toast.makeText(
                            requireContext(),
                            "Account deleted",
                            Toast.LENGTH_SHORT
                    ).show();

                    Navigation.findNavController(requireView())
                            .navigate(R.id.onboardingFragment);
                })

                .setNegativeButton("Cancel", null)

                .show();
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
    }
}