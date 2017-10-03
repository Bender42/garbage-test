package com.example.garbage.cost_item;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.garbage.IWalletOperation;
import com.example.garbage.SQLiteHelper;
import com.example.garbage.expenditure.Expenditure;
import com.example.garbage.wallet.Wallet;

import java.math.BigDecimal;
import java.util.Map;

import static com.example.garbage.tools.GarbageTools.convertAmountToInt;
import static com.example.garbage.tools.GarbageTools.convertIntToAmount;

public class CostItem implements IWalletOperation {

    public static String COST_ITEM_TABLE_NAME = "cost_item";
    public static String ID_COLUMN_NAME = "id";
    public static String NAME_COLUMN_NAME = "name";
    public static String WALLET_COLUMN_NAME = "wallet";
    public static String EXPENDITURE_COLUMN_NAME = "expenditure";
    public static String TIME_COLUMN_NAME = "time";
    public static String AMOUNT_COLUMN_NAME = "amount";

    private Integer id;
    private String name;
    private BigDecimal amount;
    private Integer wallet;
    private Integer expenditure;
    private Long time;

    private String walletName;
    private String walletCurrency;

    public CostItem() {
    }

    public CostItem(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(ID_COLUMN_NAME);
        int nameIndex = cursor.getColumnIndex(NAME_COLUMN_NAME);
        int amountIndex = cursor.getColumnIndex(AMOUNT_COLUMN_NAME);
        int walletIndex = cursor.getColumnIndex(WALLET_COLUMN_NAME);
        int expenditureIndex = cursor.getColumnIndex(EXPENDITURE_COLUMN_NAME);
        int timeIndex = cursor.getColumnIndex(TIME_COLUMN_NAME);
        this.id = cursor.getInt(idIndex);
        this.name = cursor.getString(nameIndex);
        this.amount = convertIntToAmount(cursor.getInt(amountIndex));
        this.wallet = cursor.getInt(walletIndex);
        this.expenditure = cursor.getInt(expenditureIndex);
        this.time = cursor.getLong(timeIndex);
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

    @Override
    public boolean isAddingAmount(Wallet currentWallet) {
        return false;
    }

    @Override
    public String getDescription(Wallet currentWallet, Map<Integer, Wallet> wallets, Map<Integer, Expenditure> expenditures) {
        return String.format("оплата %s", expenditures.get(getExpenditure()).getName());
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
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

    @Override
    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setAmount(String amount) {
        this.amount = amount != null && amount.length() != 0 ? new BigDecimal(amount) : null;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getWalletCurrency() {
        return walletCurrency;
    }

    public void setWalletCurrency(String walletCurrency) {
        this.walletCurrency = walletCurrency;
    }
}