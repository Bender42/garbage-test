package com.example.garbage.cost_item;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.garbage.IWallet;
import com.example.garbage.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CostItemsAdapter extends RecyclerView.Adapter<CostItemsAdapter.ViewHolder> {

    private List<CostItem> costItems;
    private Map<Integer, IWallet> wallets = new LinkedHashMap<>();

    private Context context;

    public CostItemsAdapter(List<CostItem> costItems, Map<Integer, IWallet> wallets) {
        this.costItems = costItems;
        this.wallets = wallets;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cost_item_view, parent, false);
        context = parent.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
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
        holder.menu.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 1, 0, "Удалить").setOnMenuItemClickListener(getOnMenuItemClickListener(position));
            }
        });
    }

    private MenuItem.OnMenuItemClickListener getOnMenuItemClickListener(final int position) {
        return new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 1:
                        CostItem costItem = costItems.get(position);
                        costItem.delete(context, wallets);
                        costItems.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, getItemCount());
                        return true;
                }
                return false;
            }
        };
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
        public ImageButton menu;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_cost_item_view_name);
            amount = (TextView) itemView.findViewById(R.id.tv_cost_item_view_amount);
            walletCurrency = (TextView) itemView.findViewById(R.id.tv_cost_item_view_currency);
            walletName = (TextView) itemView.findViewById(R.id.tv_cost_item_view_wallet_name);
            time = (TextView) itemView.findViewById(R.id.tv_cost_item_view_date_time);
            menu = (ImageButton) itemView.findViewById(R.id.tv_cost_item_view_menu);
        }
    }
}
