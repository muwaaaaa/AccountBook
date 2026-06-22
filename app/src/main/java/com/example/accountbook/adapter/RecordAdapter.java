package com.example.accountbook.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accountbook.R;
import com.example.accountbook.database.Record;
import com.example.accountbook.utils.DateUtils;

import java.text.DecimalFormat;
import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {
    private List<Record> records;
    private OnItemClickListener listener;
    private DecimalFormat df = new DecimalFormat("#0.00");

    public interface OnItemClickListener {
        void onItemClick(Record record);
        void onItemLongClick(Record record);
    }

    public RecordAdapter(List<Record> records, OnItemClickListener listener) {
        this.records = records;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Record record = records.get(position);

        holder.categoryText.setText(record.getCategory());
        holder.noteText.setText(record.getNote() != null && !record.getNote().isEmpty() ? record.getNote() : "无备注");

        String amountStr = (record.getType().equals("income") ? "+" : "-") + "¥" + df.format(record.getAmount());
        holder.amountText.setText(amountStr);
        holder.amountText.setTextColor(record.getType().equals("income") ?
                holder.itemView.getContext().getColor(android.R.color.holo_green_dark) :
                holder.itemView.getContext().getColor(android.R.color.holo_red_dark));

        // 显示时间
        holder.timeText.setText(record.getTime());

        // 点击事件
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(record);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(record);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return records != null ? records.size() : 0;
    }

    public void updateData(List<Record> newRecords) {
        this.records = newRecords;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryText;
        TextView noteText;
        TextView amountText;
        TextView timeText;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryText = itemView.findViewById(R.id.tv_category);
            noteText = itemView.findViewById(R.id.tv_note);
            amountText = itemView.findViewById(R.id.tv_amount);
            timeText = itemView.findViewById(R.id.tv_time);
        }
    }
}