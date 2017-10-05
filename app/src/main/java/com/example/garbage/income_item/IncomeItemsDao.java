package com.example.garbage.income_item;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.garbage.SQLiteHelper;
import com.example.garbage.wallet.Wallet;

import java.util.LinkedList;
import java.util.List;

public class IncomeItemsDao {

    private final SQLiteHelper dbHelper;

    public IncomeItemsDao(Context context) {
        this.dbHelper = new SQLiteHelper(context);
    }

    public List<IncomeItem> getActiveIncomeItems() {
        List<IncomeItem> incomeItems = new LinkedList<>();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(
                Wallet.WALLET_TABLE_NAME,
                null,
                String.format("%s = ? and %s = ?", Wallet.STATUS_COLUMN_NAME, Wallet.IS_INCOME_ITEM_COLUMN_NAME),
                new String[] {"active", "1"},
                null,
                null,
                null);
        if (cursor.moveToFirst()) {
            do {
                incomeItems.add(new IncomeItem(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
        return incomeItems;
    }
}
