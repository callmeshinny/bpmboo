package com.example.bpmbooheartbeat.processors;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import java.nio.ByteBuffer;

public class ImageProcessor implements ImageAnalysis.Analyzer{

    // Interface to communicate with MainActivity
    public interface OnColorDetectedListener {
        void onColorDetected(double averageRed);
    }

    // Listener to communicate with MainActivity
    // private - ImageProcessor keeps the relationship to itself

    private final OnColorDetectedListener listener;

    public ImageProcessor(OnColorDetectedListener listenerArgument) {
        this.listener = listenerArgument;
    }

    @Override
    public void analyze(@NonNull ImageProxy image) {
        // Image format is typically YUV_420_888
        // For PPG, we focus on the Red component.
        // In a dark/covered environment with flash, the luminance (Y) effectively tracks red intensity
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);

        long total = 0;
        for (byte b: data) {
            total += (b & 0xFF);
        }

        double avgRed = (double) total / data.length;

        // Return average to MainActivity to update UI/Chart
        listener.onColorDetected(avgRed);

        image.close();
    }
}
