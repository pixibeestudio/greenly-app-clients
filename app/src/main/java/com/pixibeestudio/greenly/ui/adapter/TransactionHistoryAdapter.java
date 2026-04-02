package com.pixibeestudio.greenly.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.model.WalletProfileResponse.TransactionHistory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.ViewHolder> {

    private List<TransactionHistory> historyList = new ArrayList<>();

    public void setHistoryList(List<TransactionHistory> historyList) {
        this.historyList = historyList;
        notifyDataSetDataSetChanged();
    }

    private void notifyDataSetDataSetChanged() {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionHistory item = historyList.get(position);
        holder.tvTransactionTitle.setText(item.getTitle());
        holder.tvTransactionTime.setText(item.getTime());

        DecimalFormat df = new DecimalFormat("#,###");
        String formattedAmount = df.format(item.getAmount()) + "đ";

        if ("INCOME".equalsIgnoreCase(item.getType())) {
            holder.ivTransactionType.setImageResource(R.drawable.ic_arrow_up_green);
            holder.tvTransactionAmount.setText("+ " + formattedAmount);
            holder.tvTransactionAmount.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
        } else if ("WITHDRAWAL".equalsIgnoreCase(item.getType())) {
            holder.ivTransactionType.setImageResource(R.drawable.ic_arrow_down_red);
            holder.tvTransactionAmount.setText("- " + formattedAmount);
            holder.tvTransactionAmount.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.ivTransactionType.setImageResource(R.drawable.ic_cash); // Default icon
            holder.tvTransactionAmount.setText(formattedAmount);
            holder.tvTransactionAmount.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return historyList != null ? historyList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivTransactionType;
        TextView tvTransactionTitle, tvTransactionTime, tvTransactionAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTransactionType = itemView.findViewById(R.id.ivTransactionType);
            tvTransactionTitle = itemView.findViewById(R.id.tvTransactionTitle);
            tvTransactionTime = itemView.findViewById(R.id.tvTransactionTime);
            tvTransactionAmount = itemView.findViewById(R.id.tvTransactionAmount);
        }
    }
}
