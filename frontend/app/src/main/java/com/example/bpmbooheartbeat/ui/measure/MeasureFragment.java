package com.example.bpmbooheartbeat.ui.measure;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.bpmbooheartbeat.R;
import com.example.bpmbooheartbeat.data.model.HeartRateRecord;
import com.example.bpmbooheartbeat.processors.ImageProcessor;
import com.example.bpmbooheartbeat.utils.ProfileImageUtils;
import com.example.bpmbooheartbeat.viewmodel.HeartRateViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MeasureFragment extends Fragment {

    private LineChart chart;
    private final List<Double> signalData = new ArrayList<>();

    private ExecutorService cameraExecutor;

    private Button btnStartStop;
    private TextView tvBPM;

    private boolean isRecording = false;
    private long startTime = 0;

    private final int WARM_UP_DELAY = 3000;
    private final int MEASUREMENT_DURATION = 30000;

    private HeartRateViewModel viewModel;

    private ProcessCameraProvider cameraProvider;
    private Camera currentCamera;

    private TelephonyManager telephonyManager;
    private TelephonyCallback telephonyCallback;
    private PhoneStateListener legacyPhoneStateListener;

    private ActivityResultLauncher<String> phoneStatePermissionLauncher;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View root = inflater.inflate(R.layout.fragment_measure, container, false);

        viewModel = new ViewModelProvider(requireActivity())
                .get(HeartRateViewModel.class);

        chart = root.findViewById(R.id.chart);
        tvBPM = root.findViewById(R.id.tvBPM);
        btnStartStop = root.findViewById(R.id.btnStartStop);

        TextView tvAppName = root.findViewById(R.id.tvAppName);
        ImageView imgAvatar = root.findViewById(R.id.imgAvatar);

        ProfileImageUtils.loadAvatar(requireContext(), imgAvatar);

        tvAppName.setOnClickListener(v -> reloadCurrentTab(v, R.id.measureFragment));

        imgAvatar.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.profileFragment)
        );

        btnStartStop.setOnClickListener(v -> {
            if (!isRecording) {
                startRecording();
            } else {
                stopRecording();
            }
        });

        phoneStatePermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        registerPhoneStateListener();
                    } else {
                        Toast.makeText(
                                requireContext(),
                                "Phone-call interruption detection is disabled. The app will still reset if it is paused.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );

        setupChart();

        cameraExecutor = Executors.newSingleThreadExecutor();

        requestPhoneStatePermissionIfNeeded();

        return root;
    }

    private void setupChart() {
        if (chart == null) return;

        chart.setData(new LineData());
        chart.getDescription().setText("Heart Rate Signal");
        chart.setNoDataText("Waiting for heartbeat data...");
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.invalidate();
    }

    private void startRecording() {
        isRecording = true;
        startTime = System.currentTimeMillis();

        tvBPM.setText("--");
        tvBPM.setTextSize(30);

        btnStartStop.setText("Stop Measurement");

        signalData.clear();

        if (chart != null && chart.getData() != null) {
            chart.getData().clearValues();
            chart.invalidate();
        }
    }

    private void stopRecording() {
        isRecording = false;

        long endTime = System.currentTimeMillis();

        int actualDuration =
                (int) (endTime - startTime - WARM_UP_DELAY);

        requireActivity().runOnUiThread(() -> {
            btnStartStop.setText("Start Measurement");

            int bpm = calculateBPM(signalData);

            if (bpm > 40 && bpm < 200) {
                tvBPM.setText(String.valueOf(bpm));
                tvBPM.setTextSize(32);

                HeartRateRecord record =
                        new HeartRateRecord(
                                bpm,
                                endTime,
                                actualDuration
                        );

                viewModel.saveRecord(record);
            } else {
                tvBPM.setText("Retry");
                tvBPM.setTextSize(22);
            }
        });
    }

    private void stopMeasurementDueToInterruption(String reason) {
        if (!isRecording) {
            return;
        }

        isRecording = false;
        startTime = 0;
        signalData.clear();

        turnOffTorch();

        if (chart != null && chart.getData() != null) {
            chart.getData().clearValues();
            chart.invalidate();
        }

        if (isAdded()) {
            requireActivity().runOnUiThread(() -> {
                tvBPM.setText("--");
                tvBPM.setTextSize(32);
                btnStartStop.setText("Start Measurement");

                Toast.makeText(
                        requireContext(),
                        reason + " Measurement was interrupted. Please start again.",
                        Toast.LENGTH_LONG
                ).show();
            });
        }
    }

    private void resetMeasurementUI() {
        isRecording = false;
        startTime = 0;

        tvBPM.setText("--");
        tvBPM.setTextSize(32);

        btnStartStop.setText("Start Measurement");

        signalData.clear();

        if (chart != null && chart.getData() != null) {
            chart.getData().clearValues();
            chart.invalidate();
        }
    }

    private int calculateBPM(List<Double> data) {
        if (data.size() < 10) return 0;

        int count = 0;

        for (int i = 1; i < data.size() - 1; i++) {
            if (data.get(i) > data.get(i - 1)
                    && data.get(i) > data.get(i + 1)) {
                count++;
            }
        }

        double minutes = MEASUREMENT_DURATION / 60000.0;

        return (int) (count / minutes / 2);
    }

    @Override
    public void onResume() {
        super.onResume();
        startCamera();
        registerPhoneStateListener();
    }

    @Override
    public void onPause() {
        if (isRecording) {
            stopMeasurementDueToInterruption("");
        }

        turnOffTorch();

        super.onPause();
    }

    @Override
    public void onDestroyView() {
        unregisterPhoneStateListener();
        turnOffTorch();

        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }

        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }

        super.onDestroyView();
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();

                View root = getView();

                if (root == null) return;

                PreviewView viewFinder = root.findViewById(R.id.viewFinder);

                if (viewFinder != null) {
                    preview.setSurfaceProvider(viewFinder.getSurfaceProvider());
                }

                ImageAnalysis imageAnalysis =
                        new ImageAnalysis.Builder()
                                .setBackpressureStrategy(
                                        ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                                )
                                .build();

                imageAnalysis.setAnalyzer(
                        cameraExecutor,
                        new ImageProcessor(avgRed -> {
                            if (isAdded()) {
                                requireActivity().runOnUiThread(
                                        () -> updateChart(avgRed)
                                );
                            }
                        })
                );

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();

                currentCamera =
                        cameraProvider.bindToLifecycle(
                                getViewLifecycleOwner(),
                                cameraSelector,
                                preview,
                                imageAnalysis
                        );

                if (currentCamera.getCameraInfo().hasFlashUnit()) {
                    currentCamera.getCameraControl().enableTorch(true);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void turnOffTorch() {
        try {
            if (currentCamera != null
                    && currentCamera.getCameraInfo().hasFlashUnit()) {
                currentCamera.getCameraControl().enableTorch(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateChart(double value) {
        if (!isRecording) return;

        long elapsed =
                System.currentTimeMillis() - startTime;

        if (elapsed < WARM_UP_DELAY) {
            tvBPM.setText("--");
            tvBPM.setTextSize(30);
            return;
        }

        if (elapsed >=
                (MEASUREMENT_DURATION + WARM_UP_DELAY)) {
            stopRecording();
            return;
        }

        int secondsLeft =
                (int) (
                        (MEASUREMENT_DURATION
                                + WARM_UP_DELAY
                                - elapsed) / 1000
                );

        tvBPM.setText(String.valueOf(secondsLeft));
        tvBPM.setTextSize(32);

        if (chart != null) {
            LineData data = chart.getData();

            if (data != null) {
                ILineDataSet set = data.getDataSetByIndex(0);

                if (set == null) {
                    LineDataSet newSet =
                            new LineDataSet(
                                    null,
                                    "Live PPG Data"
                            );

                    newSet.setDrawCircles(false);
                    newSet.setColor(Color.RED);
                    newSet.setLineWidth(2f);

                    data.addDataSet(newSet);

                    set = newSet;
                }

                data.addEntry(
                        new Entry(
                                set.getEntryCount(),
                                (float) value
                        ),
                        0
                );

                data.notifyDataChanged();

                chart.notifyDataSetChanged();

                chart.setVisibleXRangeMaximum(120);

                chart.moveViewToX(data.getEntryCount());
            }
        }

        signalData.add(value);
    }

    private void requestPhoneStatePermissionIfNeeded() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED) {
            registerPhoneStateListener();
            return;
        }

        phoneStatePermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE);
    }

    private void registerPhoneStateListener() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_PHONE_STATE
        ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        telephonyManager =
                (TelephonyManager) requireContext()
                        .getSystemService(Context.TELEPHONY_SERVICE);

        if (telephonyManager == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (telephonyCallback != null) {
                return;
            }

            telephonyCallback = new TelephonyCallback() {
            };

            TelephonyCallback.CallStateListener callStateListener =
                    new TelephonyCallback.CallStateListener() {
                        @Override
                        public void onCallStateChanged(int state) {
                            handleCallStateChanged(state);
                        }
                    };

            telephonyCallback = new CallStateTelephonyCallback(callStateListener);

            telephonyManager.registerTelephonyCallback(
                    ContextCompat.getMainExecutor(requireContext()),
                    telephonyCallback
            );
        } else {
            if (legacyPhoneStateListener != null) {
                return;
            }

            legacyPhoneStateListener = new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String phoneNumber) {
                    handleCallStateChanged(state);
                }
            };

            telephonyManager.listen(
                    legacyPhoneStateListener,
                    PhoneStateListener.LISTEN_CALL_STATE
            );
        }
    }

    private void unregisterPhoneStateListener() {
        try {
            if (telephonyManager == null) {
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (telephonyCallback != null) {
                    telephonyManager.unregisterTelephonyCallback(telephonyCallback);
                    telephonyCallback = null;
                }
            } else {
                if (legacyPhoneStateListener != null) {
                    telephonyManager.listen(
                            legacyPhoneStateListener,
                            PhoneStateListener.LISTEN_NONE
                    );
                    legacyPhoneStateListener = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleCallStateChanged(int state) {
        if (state == TelephonyManager.CALL_STATE_RINGING
                || state == TelephonyManager.CALL_STATE_OFFHOOK) {
            stopMeasurementDueToInterruption("Phone call detected.");
        }
    }

    private void reloadCurrentTab(View view, int fragmentId) {
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(fragmentId, true)
                .build();

        Navigation.findNavController(view).navigate(fragmentId, null, navOptions);
    }

    private static class CallStateTelephonyCallback
            extends TelephonyCallback
            implements TelephonyCallback.CallStateListener {

        private final TelephonyCallback.CallStateListener listener;

        CallStateTelephonyCallback(TelephonyCallback.CallStateListener listener) {
            this.listener = listener;
        }

        @Override
        public void onCallStateChanged(int state) {
            listener.onCallStateChanged(state);
        }
    }
}