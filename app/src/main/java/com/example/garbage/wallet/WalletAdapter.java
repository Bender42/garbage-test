package com.example.garbage.wallet;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.garbage.R;

import java.util.List;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.ViewHolder> {

    private Activity activity;
    private List<Wallet> wallets;

    public WalletAdapter(List<Wallet> wallets, Activity activity) {
        this.wallets = wallets;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Wallet wallet = wallets.get(position);
        holder.name.setText(wallet.getName());
        holder.amount.setText(String.format("%s", wallet.getAmount()));
        holder.layout.setOnClickListener(getOnClickListener(wallet.getId()));
    }

    @Override
    public int getItemCount() {
        return wallets.size();
    }

    private View.OnClickListener getOnClickListener(final int id) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.setResult(Activity.RESULT_OK, new Intent().putExtra("walletId", id));
                activity.finish();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout layout;
        public TextView name;
        public TextView amount;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.ll_wallet_view);
            name = itemView.findViewById(R.id.tv_wallet_view_name);
            amount = itemView.findViewById(R.id.tv_wallet_view_amount);
        }
    }
}
