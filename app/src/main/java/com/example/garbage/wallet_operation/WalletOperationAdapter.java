package com.example.garbage.wallet_operation;

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
import com.example.garbage.IWalletOperation;
import com.example.garbage.R;
import com.example.garbage.expenditure.Expenditure;
import com.example.garbage.wallet.Wallet;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WalletOperationAdapter extends RecyclerView.Adapter<WalletOperationAdapter.ViewHolder> {

    private List<IWalletOperation> operations;
    private IWallet currentWallet;
    private Map<Integer, IWallet> wallets = new LinkedHashMap<>();
    private Map<Integer, Expenditure> expenditures = new LinkedHashMap<>();

    private Context context;

    public WalletOperationAdapter(List<IWalletOperation> operations,
                                  Wallet currentWallet,
                                  Map<Integer, IWallet> wallets,
                                  Map<Integer, Expenditure> expenditures) {
        this.operations = operations;
        this.currentWallet = currentWallet;
        this.wallets = wallets;
        this.expenditures = expenditures;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_operation_view, parent, false);
        context = parent.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        IWalletOperation select = operations.get(position);
        holder.name.setText(select.getName());
        boolean isAddingAmount = select.isAddingAmount(currentWallet);
        int textColor = isAddingAmount ? 0xFF009900 : 0xFFe60000;
        if (isAddingAmount) {
            holder.amount.setText(String.format("+ %s", select.getAmount()));
        } else {
            holder.amount.setText(String.format("- %s", select.getAmount()));
        }
        holder.amount.setTextColor(textColor);
        holder.currency.setText(currentWallet.getCurrency());
        holder.currency.setTextColor(textColor);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(select.getTime());
        SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyyг. HH:mm");
        holder.time.setText(formatDate.format(calendar.getTime()));
        holder.description.setText(select.getDescription(currentWallet, wallets, expenditures));
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
                        IWalletOperation walletOperation = operations.get(position);
                        walletOperation.delete(context, wallets);
                        operations.remove(position);
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
        return operations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView amount;
        public TextView time;
        public TextView description;
        public TextView currency;
        public ImageButton menu;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_wallet_oper_view_name);
            amount = (TextView) itemView.findViewById(R.id.tv_wallet_oper_view_amount);
            time = (TextView) itemView.findViewById(R.id.tv_wallet_oper_view_date_time);
            description = (TextView) itemView.findViewById(R.id.tv_wallet_oper_view_description);
            currency = (TextView) itemView.findViewById(R.id.tv_wallet_oper_view_currency);
            menu = (ImageButton) itemView.findViewById(R.id.tv_wallet_oper_view_menu);
        }
    }
}
