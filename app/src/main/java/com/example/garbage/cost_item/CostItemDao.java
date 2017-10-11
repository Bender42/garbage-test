package com.example.garbage.cost_item;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.garbage.IWallet;
import com.example.garbage.database.SQLiteHelper;
import com.google.common.base.Joiner;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CostItemDao {

    private SQLiteHelper dbHelper;

    public CostItemDao(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public List<CostItem> getCostItems(int expenditureId, Map<Integer, IWallet> wallets, Long fromTime, Long toTime) {
        List<CostItem> costItems = new LinkedList<>();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(
                CostItem.COST_ITEM_TABLE_NAME,
                null,
                getSelectionExpenditure(expenditureId, fromTime, toTime),
                getSelectionExpenditureArgs(expenditureId, fromTime, toTime),
                null,
                null,
                String.format("%s DESC", CostItem.TIME_COLUMN_NAME));
        if (cursor.moveToFirst()) {
            do {
                CostItem costItem = new CostItem(cursor);
                Integer costItemWalletId = costItem.getWalletId();
                if (wallets != null) {
                    costItem.setWalletName(wallets.get(costItemWalletId).getName());
                    costItem.setWalletCurrency(wallets.get(costItemWalletId).getCurrency());
                }
                costItems.add(costItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
        return costItems;
    }

    public List<CostItem> getCostItems(int walletId, Long fromTime, Long toTime) {
        List<CostItem> costItems = new LinkedList<>();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(
                CostItem.COST_ITEM_TABLE_NAME,
                null,
                getSelectionWallet(walletId, fromTime, toTime),
                getSelectionWalletArgs(walletId, fromTime, toTime),
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

    private String getSelectionExpenditure(Integer expenditureId, Long fromTime, Long toTime) {
        List<String> result = new LinkedList<>();
        if (expenditureId != null) {
            result.add(String.format("%s = ?", CostItem.EXPENDITURE_ID_COLUMN_NAME));
        }
        if (fromTime != null) {
            result.add(String.format("and ? <= %s", CostItem.TIME_COLUMN_NAME));
        }
        if (toTime != null) {
            result.add(String.format("and %s <= ?", CostItem.TIME_COLUMN_NAME));
        }
        return Joiner.on(" ").join(result);
    }

    private String getSelectionWallet(Integer wallet, Long fromTime, Long toTime) {
        List<String> result = new LinkedList<>();
        if (wallet != null) {
            result.add(String.format("%s = ?", CostItem.WALLET_ID_COLUMN_NAME));
        }
        if (fromTime != null) {
            result.add(String.format("and ? <= %s", CostItem.TIME_COLUMN_NAME));
        }
        if (toTime != null) {
            result.add(String.format("and %s <= ?", CostItem.TIME_COLUMN_NAME));
        }
        return Joiner.on(" ").join(result);
    }

    private String[] getSelectionExpenditureArgs(Integer expenditureId, Long fromTime, Long toTime) {
        List<String> result = new LinkedList<>();
        if (expenditureId != null) {
            result.add(String.valueOf(expenditureId));
        }
        if (fromTime != null) {
            result.add(String.valueOf(fromTime));
        }
        if (toTime != null) {
            result.add(String.valueOf(toTime));
        }
        return result.toArray(new String[0]);
    }

    private String[] getSelectionWalletArgs(Integer walletId, Long fromTime, Long toTime) {
        List<String> result = new LinkedList<>();
        if (walletId != null) {
            result.add(String.valueOf(walletId));
        }
        if (fromTime != null) {
            result.add(String.valueOf(fromTime));
        }
        if (toTime != null) {
            result.add(String.valueOf(toTime));
        }
        return result.toArray(new String[0]);
    }
}
