package com.example.bpmbooheartbeat.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.bpmbooheartbeat.R;
import com.example.bpmbooheartbeat.utils.ProfileImageUtils;
import com.example.bpmbooheartbeat.viewmodel.HeartRateViewModel;

public class HistoryFragment extends Fragment {

    private HeartRateAdapter adapter;
    private HeartRateViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View root = inflater.inflate(R.layout.fragment_history, container, false);

        ImageView imgAvatarHistory = root.findViewById(R.id.imgAvatarHistory);
        ProfileImageUtils.loadAvatar(requireContext(), imgAvatarHistory);

        TextView tvAppNameHistory = root.findViewById(R.id.tvAppNameHistory);

        tvAppNameHistory.setOnClickListener(v -> reloadCurrentTab(v, R.id.historyFragment));

        imgAvatarHistory.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.profileFragment)
        );

        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new HeartRateAdapter();
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(HeartRateViewModel.class);

        viewModel.getAllRecords().observe(getViewLifecycleOwner(), records -> {
            adapter.submitList(records);
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        View root = getView();
        if (root != null) {
            ImageView imgAvatarHistory = root.findViewById(R.id.imgAvatarHistory);
            ProfileImageUtils.loadAvatar(requireContext(), imgAvatarHistory);
        }
    }

    private void reloadCurrentTab(View view, int fragmentId) {
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(fragmentId, true)
                .build();

        Navigation.findNavController(view).navigate(fragmentId, null, navOptions);
    }
}