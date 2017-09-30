package com.example.garbage.cost_item;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.garbage.R;

import java.util.List;

public class CostItemsAdapter extends RecyclerView.Adapter<CostItemsAdapter.ViewHolder> {

    private List<CostItem> costItems;

    public CostItemsAdapter(List<CostItem> costItems) {
        this.costItems = costItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cost_item_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CostItem select = costItems.get(position);
        holder.name.setText(select.getName());
        holder.amount.setText(String.valueOf(select.getAmount()));
        holder.walletCurrency.setText(select.getWalletCurrency());
        holder.walletName.setText(select.getWalletName());
    }

    @Override
    public int getItemCount() {
        return costItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView amount;
        public TextView walletCurrency;
        public TextView walletName;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_cost_item_view_name);
            amount = (TextView) itemView.findViewById(R.id.tv_cost_item_view_amount);
            walletCurrency = (TextView) itemView.findViewById(R.id.tv_cost_item_view_wallet_currency);
            walletName = (TextView) itemView.findViewById(R.id.tv_cost_item_view_wallet_name);
        }
    }
}
