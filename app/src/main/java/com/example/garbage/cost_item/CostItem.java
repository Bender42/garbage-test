package com.example.garbage.cost_item;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.garbage.SQLiteHelper;

import java.math.BigDecimal;

public class CostItem {

    public static String COST_ITEM_TABLE_NAME = "cost_item";
    public static String ID_COLUMN_NAME = "id";
    public static String NAME_COLUMN_NAME = "name";
    public static String WALLET_COLUMN_NAME = "wallet";
    public static String EXPENDITURE_COLUMN_NAME = "expenditure";
    public static String TIME_COLUMN_NAME = "time";
    public static String AMOUNT_COLUMN_NAME = "amount";

    private Integer id;
    private String name;
    private Integer wallet;
    private Integer expenditure;
    private Long time;
    private BigDecimal amount;

    public CostItem() {
    }

    public boolean isComplete() {
        return amount != null && BigDecimal.ZERO.compareTo(amount) != 1 &&
                wallet != null && expenditure != null && time != null;
    }

    public boolean post(Context context) {
        SQLiteHelper dbHelper = new SQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME_COLUMN_NAME, name);
        contentValues.put(WALLET_COLUMN_NAME, wallet);
        contentValues.put(EXPENDITURE_COLUMN_NAME, expenditure);
        contentValues.put(TIME_COLUMN_NAME, time);
        contentValues.put(AMOUNT_COLUMN_NAME, amount.multiply(new BigDecimal(100)).intValue());
        database.insert(COST_ITEM_TABLE_NAME, null, contentValues);
        dbHelper.close();
        return true;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getWallet() {
        return wallet;
    }

    public void setWallet(Integer wallet) {
        this.wallet = wallet;
    }

    public Integer getExpenditure() {
        return expenditure;
    }

    public void setExpenditure(Integer expenditure) {
        this.expenditure = expenditure;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setAmount(String amount) {
        this.amount = amount != null && amount.length() != 0 ? new BigDecimal(amount) : null;
    }
}