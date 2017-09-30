package com.example.garbage.wallet_operation;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.garbage.R;
import com.example.garbage.wallet.Wallet;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class WalletOperationAdapter extends RecyclerView.Adapter<WalletOperationAdapter.ViewHolder> {

    private List<WalletOperation> walletOperations;
    private Wallet currentWallet;

    public WalletOperationAdapter(List<WalletOperation> walletOperations, Wallet currentWallet) {
        this.walletOperations = walletOperations;
        this.currentWallet = currentWallet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_operation_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WalletOperation select = walletOperations.get(position);
        holder.name.setText(select.getName());
        String amount = String.valueOf(select.getAmount());
        if (Objects.equals(select.getFromWallet(), currentWallet.getId())) {
            holder.amount.setText(String.format("- %s", amount));
            holder.amount.setTextColor(Color.RED);
        } else {
            holder.amount.setText(amount);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(select.getTime());
        SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyyг. HH:mm");
        holder.time.setText(formatDate.format(calendar.getTime()));
    }

    @Override
    public int getItemCount() {
        return walletOperations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView amount;
        public TextView time;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_wallet_oper_view_name);
            amount = (TextView) itemView.findViewById(R.id.tv_wallet_oper_view_amount);
            time = (TextView) itemView.findViewById(R.id.tv_wallet_oper_view_time);
        }
    }
}
