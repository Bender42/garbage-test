package com.example.garbage.income_item;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.garbage.SQLiteHelper;
import com.example.garbage.wallet.Wallet;

import java.math.BigDecimal;

public class IncomeItem extends Wallet {

    public IncomeItem() {
        super();
    }

    public IncomeItem(Cursor cursor) {
        super(cursor);
    }

    public IncomeItem(int id, Context context) {
        super(id, context);
    }

    @Override
    public boolean isComplete() {
        return getName() != null && getName().length() > 0 &&
                getCurrency() != null && getCurrency().length() > 0;
    }

    @Override
    public boolean post(Context context) {
        SQLiteHelper dbHelper = new SQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME_COLUMN_NAME, getName());
        contentValues.put(CURRENCY_COLUMN_NAME, getCurrency());
        contentValues.put(ICON_COLUMN_NAME, getIcon());
        contentValues.put(STATUS_COLUMN_NAME, "active");
        contentValues.put(IS_INCOME_ITEM_COLUMN_NAME, "1");
        database.insert(WALLET_TABLE_NAME, null, contentValues);
        dbHelper.close();
        return true;
    }

    public void updateIncomeItem(IncomeItem incomeItem) {
        if (incomeItem == null) {
            setId(null);
            setName(null);
            setAmount((BigDecimal) null);
            setCurrency(null);
            setIcon(null);
            setIsIncomeItem(null);
        } else {
            setId(incomeItem.getId());
            setName(incomeItem.getName());
            setAmount(incomeItem.getAmount());
            setCurrency(incomeItem.getCurrency());
            setIcon(incomeItem.getIcon());
            setIsIncomeItem(incomeItem.getIsIncomeItem());
        }
    }
}
