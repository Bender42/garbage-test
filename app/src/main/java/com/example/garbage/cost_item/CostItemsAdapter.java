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
        holder.ivName.setText(costItems.get(position).getName());
        holder.ivAmount.setText(String.valueOf(costItems.get(position).getAmount()));
        holder.ivWalletCurrency.setText(costItems.get(position).getWalletCurrency());
        holder.itemWalletName.setText(costItems.get(position).getWalletName());
    }

    @Override
    public int getItemCount() {
        return costItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView ivName;
        public TextView ivAmount;
        public TextView ivWalletCurrency;
        public TextView itemWalletName;

        public ViewHolder(View itemView) {
            super(itemView);
            ivName = (TextView) itemView.findViewById(R.id.tv_cost_item_view_name);
            ivAmount = (TextView) itemView.findViewById(R.id.tv_cost_item_view_amount);
            ivWalletCurrency = (TextView) itemView.findViewById(R.id.tv_cost_item_view_wallet_currency);
            itemWalletName = (TextView) itemView.findViewById(R.id.tv_cost_item_view_wallet_name);
        }
    }
}
