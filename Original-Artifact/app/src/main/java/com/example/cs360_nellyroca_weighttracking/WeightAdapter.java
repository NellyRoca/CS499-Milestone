/**
 * Updated to Match DatabaseHelper
 */

package com.example.cs360_nellyroca_weighttracking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class WeightAdapter extends RecyclerView.Adapter<WeightAdapter.WeightViewHolder> {

    public interface OnDeleteClickListener {
        void onDeleteClick(int weightId);
    }

    public interface OnEditClickListener {
        void onEditClick(WeightEntry weightEntry);
    }

    private final List<WeightEntry> weightList;
    private final OnDeleteClickListener deleteClickListener;
    private final OnEditClickListener editClickListener;

    public WeightAdapter(List<WeightEntry> weightList,
                         OnDeleteClickListener deleteClickListener,
                         OnEditClickListener editClickListener) {
        this.weightList = weightList;
        this.deleteClickListener = deleteClickListener;
        this.editClickListener = editClickListener;
    }

    @NonNull
    @Override
    public WeightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_weight_entry, parent, false);
        return new WeightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeightViewHolder holder, int position) {
        WeightEntry item = weightList.get(position);

        holder.dateText.setText(item.getDate());
        holder.weightText.setText(item.getWeight());

        holder.deleteButton.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(item.getId());
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onEditClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return weightList.size();
    }

    public static class WeightViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;
        TextView weightText;
        Button deleteButton;

        public WeightViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.textDate);
            weightText = itemView.findViewById(R.id.textWeight);
            deleteButton = itemView.findViewById(R.id.buttonDelete);
        }
    }
}