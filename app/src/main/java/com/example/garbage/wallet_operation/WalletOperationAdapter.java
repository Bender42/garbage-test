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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WalletOperationAdapter extends RecyclerView.Adapter<WalletOperationAdapter.ViewHolder> {

    private List<WalletOperation> walletOperations;
    private Wallet currentWallet;
    private Map<Integer, Wallet> wallets = new LinkedHashMap<>();

    public WalletOperationAdapter(List<WalletOperation> walletOperations, Wallet currentWallet, Map<Integer, Wallet> wallets) {
        this.walletOperations = walletOperations;
        this.currentWallet = currentWallet;
        this.wallets = wallets;
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
        boolean isAddition = Objects.equals(select.getToWallet(), currentWallet.getId());
        int textColor = isAddition ? 0xFF009900 : 0xFFe60000;
        if (isAddition) {
            holder.amount.setText(String.format("+ %s", amount));
        } else {
            holder.amount.setText(String.format("- %s", amount));
        }
        holder.amount.setTextColor(textColor);
        holder.currency.setText(currentWallet.getCurrency());
        holder.currency.setTextColor(textColor);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(select.getTime());
        SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyyг. HH:mm");
        holder.time.setText(formatDate.format(calendar.getTime()));
        if (isAddition) {
            if (select.getFromWallet() == 0) {
                holder.description.setText(
                        String.format(
                                "пополнение %s",
                                currentWallet.getName()));
            } else {
                holder.description.setText(
                        String.format(
                                "из %s в %s",
                                wallets.get(select.getFromWallet()).getName(),
                                wallets.get(select.getToWallet()).getName()));
            }
        } else {
            holder.description.setText(
                    String.format(
                            "из %s в %s",
                            wallets.get(select.getFromWallet()).getName(),
                            wallets.get(select.getToWallet()).getName()));
        }
    }

    @Override
    public int getItemCount() {
        return walletOperations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView amount;
        public TextView time;
        public TextView description;
        public TextView currency;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_wallet_oper_view_name);
            amount = (TextView) itemView.findViewById(R.id.tv_wallet_oper_view_amount);
            time = (TextView) itemView.findViewById(R.id.tv_wallet_oper_view_date_time);
            description = (TextView) itemView.findViewById(R.id.tv_wallet_oper_view_description);
            currency = (TextView) itemView.findViewById(R.id.tv_wallet_oper_view_currency);
        }
    }
}
