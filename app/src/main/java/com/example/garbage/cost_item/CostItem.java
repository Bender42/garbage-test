package com.example.garbage.cost_item;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.garbage.SQLiteHelper;
import com.example.garbage.wallet.Wallet;

import java.math.BigDecimal;

import static com.example.garbage.tools.GarbageTools.convertAmountToInt;

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

    /**
     * TODO добавить поддержку транзакций
     * Создаем запись элемента расхода с последующим уменьшением баланса кошелька списания
     *
     * @param targetWallet  кошелек списания
     */
    public boolean post(Context context, Wallet targetWallet) {
        try {
            SQLiteHelper dbHelper = new SQLiteHelper(context);
            SQLiteDatabase database = dbHelper.getWritableDatabase();

            ContentValues costItemContentValues = new ContentValues();
            costItemContentValues.put(NAME_COLUMN_NAME, name);
            costItemContentValues.put(WALLET_COLUMN_NAME, wallet);
            costItemContentValues.put(EXPENDITURE_COLUMN_NAME, expenditure);
            costItemContentValues.put(TIME_COLUMN_NAME, time);
            costItemContentValues.put(AMOUNT_COLUMN_NAME, convertAmountToInt(amount));
            database.insert(COST_ITEM_TABLE_NAME, null, costItemContentValues);

            ContentValues walletContentValues = new ContentValues();
            BigDecimal walletResultAmount = targetWallet.getAmount().subtract(amount);
            walletContentValues.put(Wallet.AMOUNT_COLUMN_NAME, convertAmountToInt(walletResultAmount));
            database.update(Wallet.WALLET_TABLE_NAME, walletContentValues, "id = ?", new String[]{String.valueOf(targetWallet.getId())});

            dbHelper.close();
            return true;
        } catch (Exception e) {
            return false;
        }
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