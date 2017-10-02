package com.example.garbage.cost_item;

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

public class CostItemsAdapter extends RecyclerView.Adapter<CostItemsAdapter.ViewHolder> {

    private List<CostItem> costItems;
    private Map<Integer, Wallet> wallets = new LinkedHashMap<>();

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
        holder.amount.setText(String.format("- %s", select.getAmount()));
        holder.amount.setTextColor(0xFFe60000);
        holder.walletCurrency.setText(select.getWalletCurrency());
        holder.walletCurrency.setTextColor(0xFFe60000);
        holder.walletName.setText(String.format("оплата из %s", select.getWalletName()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(select.getTime());
        SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyyг. HH:mm");
        holder.time.setText(formatDate.format(calendar.getTime()));
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
        public TextView time;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_cost_item_view_name);
            amount = (TextView) itemView.findViewById(R.id.tv_cost_item_view_amount);
            walletCurrency = (TextView) itemView.findViewById(R.id.tv_cost_item_view_currency);
            walletName = (TextView) itemView.findViewById(R.id.tv_cost_item_view_wallet_name);
            time = (TextView) itemView.findViewById(R.id.tv_cost_item_view_date_time);
        }
    }
}
