package com.example.bpmbooheartbeat.ui.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.bpmbooheartbeat.R;
import com.google.android.material.button.MaterialButton;

public class OnboardingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View root = inflater.inflate(R.layout.fragment_onboarding, container, false);

        MaterialButton btnGetStarted = root.findViewById(R.id.btnGetStarted);
        TextView tvSignUp = root.findViewById(R.id.tvSignUp);

        btnGetStarted.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.measureFragment)
        );

        tvSignUp.setOnClickListener(v ->
                Toast.makeText(
                        requireContext(),
                        "Sign up will be added later",
                        Toast.LENGTH_SHORT
                ).show()
        );

        return root;
    }
}