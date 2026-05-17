// ui/history/HeartRateAdapter.java
package com.example.bpmbooheartbeat.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bpmbooheartbeat.R;
import com.example.bpmbooheartbeat.data.model.HeartRateRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HeartRateAdapter extends ListAdapter<HeartRateRecord, HeartRateAdapter.ViewHolder> {

    public static final DiffUtil.ItemCallback<HeartRateRecord> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<HeartRateRecord>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull HeartRateRecord a,
                        @NonNull HeartRateRecord b
                ) {
                    return a.id == b.id;
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull HeartRateRecord a,
                        @NonNull HeartRateRecord b
                ) {
                    return a.bpmValue == b.bpmValue
                            && a.timestamp == b.timestamp;
                }
            };

    public HeartRateAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_heart_rate_record, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {
        HeartRateRecord record = getItem(position);
        holder.bind(record);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvBpm;
        private final TextView tvDate;
        private final TextView tvTag;

        private final SimpleDateFormat dateFormat =
                new SimpleDateFormat("dd/MM/yyyy  h:mm a", Locale.getDefault());

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvBpm = itemView.findViewById(R.id.tvBpmValue);
            tvDate = itemView.findViewById(R.id.tvTime);
            tvTag = itemView.findViewById(R.id.tvStatus);
        }

        void bind(HeartRateRecord record) {
            tvBpm.setText(String.valueOf(record.bpmValue));

            tvDate.setText(
                    dateFormat.format(new Date(record.timestamp))
            );

            tvTag.setText(
                    getLocalizedFeelingTag(record)
            );

            int color;

            if (record.bpmValue < 60) {
                color = 0xFF2196F3;
            } else if (record.bpmValue <= 100) {
                color = 0xFF4CAF50;
            } else if (record.bpmValue <= 140) {
                color = 0xFFFF9800;
            } else {
                color = 0xFFF44336;
            }

            tvBpm.setTextColor(color);
        }

        private String getLocalizedFeelingTag(HeartRateRecord record) {
            if (record.feelingTag == null || record.feelingTag.trim().isEmpty()) {
                return itemView.getContext().getString(R.string.resting).toUpperCase();
            }

            String tag = record.feelingTag.trim().toLowerCase(Locale.ROOT);

            switch (tag) {
                case "resting":
                    return itemView.getContext().getString(R.string.resting).toUpperCase();

                case "post exercise":
                case "post_exercise":
                    return itemView.getContext().getString(R.string.post_exercise).toUpperCase();

                case "stressed":
                    return itemView.getContext().getString(R.string.stressed).toUpperCase();

                default:
                    return record.feelingTag.toUpperCase(Locale.getDefault());
            }
        }
    }
}