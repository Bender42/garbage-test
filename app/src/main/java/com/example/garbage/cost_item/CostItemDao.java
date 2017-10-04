package com.example.garbage.cost_item;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.garbage.SQLiteHelper;
import com.example.garbage.wallet.Wallet;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CostItemDao {

    private SQLiteHelper dbHelper;

    public CostItemDao(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public List<CostItem> getCostItems(int expenditureId, Map<Integer, Wallet> wallets) {
        List<CostItem> costItems = new LinkedList<>();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(
                CostItem.COST_ITEM_TABLE_NAME,
                null,
                "expenditure = ?",
                new String[] {String.valueOf(expenditureId)},
                null,
                null,
                String.format("%s DESC", CostItem.TIME_COLUMN_NAME));
        if (cursor.moveToFirst()) {
            do {
                CostItem costItem = new CostItem(cursor);
                Integer costItemWalletId = costItem.getWalletId();
                costItem.setWalletName(wallets.get(costItemWalletId).getName());
                costItem.setWalletCurrency(wallets.get(costItemWalletId).getCurrency());
                costItems.add(costItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
        return costItems;
    }

    public List<CostItem> getCostItems(int walletId) {
        List<CostItem> costItems = new LinkedList<>();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(
                CostItem.COST_ITEM_TABLE_NAME,
                null,
                "wallet = ?",
                new String[] {String.valueOf(walletId)},
                null,
                null,
                String.format("%s DESC", CostItem.TIME_COLUMN_NAME));
        if (cursor.moveToFirst()) {
            do {
                costItems.add(new CostItem(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
        return costItems;
    }
}
