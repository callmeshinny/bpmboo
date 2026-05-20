package com.example.bpmbooheartbeat.ui.analytics;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.bpmbooheartbeat.R;
import com.example.bpmbooheartbeat.data.api.ApiResponse;
import com.example.bpmbooheartbeat.data.api.RetrofitClient;
import com.example.bpmbooheartbeat.data.model.HeartRateRecord;
import com.example.bpmbooheartbeat.utils.AuthPreferences;
import com.example.bpmbooheartbeat.utils.ProfileImageUtils;
import com.example.bpmbooheartbeat.viewmodel.HeartRateViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnalyticsFragment extends Fragment {

    private BarChart barChartWeekly;
    private HeartRateViewModel viewModel;

    private TextView tvAverageBpm;
    private TextView tvMaximumBpm;
    private TextView tvMinimumBpm;
    private TextView tvInsightContent;
    private TextView tvTrendIndicator;
    private ProgressBar pbInsightLoading;
    private LinearLayout llInsightError;
    private Button btnRetryInsight;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View root = inflater.inflate(R.layout.fragment_analytics, container, false);

        ImageView imgAvatarAnalytics = root.findViewById(R.id.imgAvatarAnalytics);
        TextView tvAppNameAnalytics = root.findViewById(R.id.tvAppNameAnalytics);

        ProfileImageUtils.loadAvatar(requireContext(), imgAvatarAnalytics);

        imgAvatarAnalytics.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.profileFragment)
        );

        barChartWeekly = root.findViewById(R.id.barChartWeekly);

        tvAverageBpm = root.findViewById(R.id.tvAverageBpm);
        tvMaximumBpm = root.findViewById(R.id.tvMaximumBpm);
        tvMinimumBpm = root.findViewById(R.id.tvMinimumBpm);
        tvInsightContent = root.findViewById(R.id.tvInsightContent);
        tvTrendIndicator = root.findViewById(R.id.tvTrendIndicator);
        pbInsightLoading = root.findViewById(R.id.pbInsightLoading);
        llInsightError = root.findViewById(R.id.llInsightError);
        btnRetryInsight = root.findViewById(R.id.btnRetryInsight);

        viewModel = new ViewModelProvider(requireActivity()).get(HeartRateViewModel.class);

        btnRetryInsight.setOnClickListener(v -> {
            fetchInsight();
            reloadRemoteAnalyticsData();
        });

        tvAppNameAnalytics.setOnClickListener(v -> {
            clearCharts();
            fetchInsight();
            reloadRemoteAnalyticsData();
        });

        reloadRemoteAnalyticsData();
        fetchInsight();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        View root = getView();

        if (root != null) {
            ImageView imgAvatarAnalytics = root.findViewById(R.id.imgAvatarAnalytics);
            ProfileImageUtils.loadAvatar(requireContext(), imgAvatarAnalytics);
        }

        reloadRemoteAnalyticsData();
        fetchInsight();
    }

    private void reloadRemoteAnalyticsData() {
        if (viewModel == null || !isAdded()) {
            return;
        }

        viewModel.getRemoteRecords().observe(getViewLifecycleOwner(), records -> {
            updateSummaryStats(records);
            setupWeeklyChart(records);
        });
    }

    private void fetchInsight() {
        AuthPreferences authPrefs = new AuthPreferences(requireContext());

        String userId = authPrefs.getUserId();
        String token = authPrefs.getToken();

        if (userId == null || userId.isEmpty() || token == null || token.isEmpty()) {
            showInsightError();
            return;
        }

        pbInsightLoading.setVisibility(View.VISIBLE);
        llInsightError.setVisibility(View.GONE);
        tvInsightContent.setText("Loading your personalized insight...");

        RetrofitClient.getApiService()
                .getInsightSummary("Bearer " + token, userId)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<ApiResponse> call,
                            @NonNull Response<ApiResponse> response
                    ) {
                        pbInsightLoading.setVisibility(View.GONE);

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().success
                                && response.body().insightData != null) {

                            ApiResponse.InsightResponseData insightData =
                                    response.body().insightData;

                            if (insightData.stats != null) {
                                String trend = insightData.stats.trend;

                                if ("ascending".equalsIgnoreCase(trend)) {
                                    tvTrendIndicator.setText("Trend: Increasing");
                                } else if ("descending".equalsIgnoreCase(trend)) {
                                    tvTrendIndicator.setText("Trend: Decreasing");
                                } else {
                                    tvTrendIndicator.setText("Trend: Stable");
                                }
                            }

                            if (insightData.insight != null && !insightData.insight.isEmpty()) {
                                tvInsightContent.setText(insightData.insight);
                            } else {
                                tvInsightContent.setText("No insight available yet.");
                            }
                        } else {
                            showInsightError();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                        pbInsightLoading.setVisibility(View.GONE);
                        showInsightError();
                    }
                });
    }

    private void showInsightError() {
        pbInsightLoading.setVisibility(View.GONE);
        llInsightError.setVisibility(View.VISIBLE);
        tvInsightContent.setText("Unable to load insight");
    }

    private void updateSummaryStats(List<HeartRateRecord> records) {
        if (tvAverageBpm == null || tvMaximumBpm == null || tvMinimumBpm == null) {
            return;
        }

        if (records == null || records.isEmpty()) {
            tvAverageBpm.setText("-- BPM");
            tvMaximumBpm.setText("-- BPM");
            tvMinimumBpm.setText("-- BPM");
            return;
        }

        int sum = 0;
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        int count = 0;

        for (HeartRateRecord record : records) {
            int bpm = record.bpmValue;

            if (bpm <= 0) continue;

            sum += bpm;
            max = Math.max(max, bpm);
            min = Math.min(min, bpm);
            count++;
        }

        if (count == 0) {
            tvAverageBpm.setText("-- BPM");
            tvMaximumBpm.setText("-- BPM");
            tvMinimumBpm.setText("-- BPM");
            return;
        }

        int average = Math.round(sum / (float) count);

        tvAverageBpm.setText(average + " BPM");
        tvMaximumBpm.setText(max + " BPM");
        tvMinimumBpm.setText(min + " BPM");
    }

    private void setupWeeklyChart(List<HeartRateRecord> records) {
        if (barChartWeekly == null) return;

        Map<Integer, List<Integer>> dailyBpmMap = new HashMap<>();

        for (int i = 0; i < 7; i++) {
            dailyBpmMap.put(i, new ArrayList<>());
        }

        Calendar today = Calendar.getInstance();

        if (records != null) {
            for (HeartRateRecord record : records) {
                int bpm = record.bpmValue;
                long timestamp = record.timestamp;

                if (bpm <= 0 || timestamp <= 0) continue;

                Calendar recordDate = Calendar.getInstance();
                recordDate.setTimeInMillis(timestamp);

                long diffMillis = today.getTimeInMillis() - recordDate.getTimeInMillis();
                int daysAgo = (int) (diffMillis / (1000 * 60 * 60 * 24));

                if (daysAgo >= 0 && daysAgo < 7) {
                    int chartIndex = 6 - daysAgo;

                    List<Integer> bpmList = dailyBpmMap.get(chartIndex);

                    if (bpmList != null) {
                        bpmList.add(bpm);
                    }
                }
            }
        }

        List<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            List<Integer> bpmList = dailyBpmMap.get(i);

            if (bpmList != null && !bpmList.isEmpty()) {
                int sum = 0;

                for (int bpm : bpmList) {
                    sum += bpm;
                }

                float avgBpm = sum / (float) bpmList.size();
                entries.add(new BarEntry(i, avgBpm));
            }
        }

        BarDataSet dataSet = new BarDataSet(entries, "Weekly BPM");
        dataSet.setColor(requireContext().getColor(R.color.secondary));
        dataSet.setValueTextColor(Color.TRANSPARENT);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.45f);

        barChartWeekly.setData(data);

        Description description = new Description();
        description.setText("");
        barChartWeekly.setDescription(description);

        barChartWeekly.getXAxis().setDrawGridLines(false);
        barChartWeekly.getAxisLeft().setDrawGridLines(false);
        barChartWeekly.getAxisRight().setEnabled(false);
        barChartWeekly.getLegend().setEnabled(false);
        barChartWeekly.setScaleEnabled(false);
        barChartWeekly.setTouchEnabled(false);
        barChartWeekly.invalidate();
    }

    private void clearCharts() {
        if (barChartWeekly != null) {
            barChartWeekly.clear();
            barChartWeekly.invalidate();
        }
    }
}