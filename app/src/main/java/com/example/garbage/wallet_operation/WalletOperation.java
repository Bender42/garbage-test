package com.example.garbage.wallet_operation;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.garbage.SQLiteHelper;
import com.example.garbage.wallet.Wallet;

import java.math.BigDecimal;

import static com.example.garbage.tools.GarbageTools.convertAmountToInt;

public class WalletOperation {

    public static String WALLET_OPERATION_TABLE_NAME = "wallet_operation";
    public static String ID_COLUMN_NAME = "id";
    public static String FROM_WALLET_COLUMN_NAME = "from_wallet";
    public static String TO_WALLET_COLUMN_NAME = "to_wallet";
    public static String AMOUNT_COLUMN_NAME = "amount";
    public static String NAME_COLUMN_NAME = "name";
    public static String TIME_COLUMN_NAME = "time";

    private Integer id;
    private Integer fromWallet;
    private Integer toWallet;
    private BigDecimal amount;
    private String name;
    private Long time;

    public WalletOperation() {
    }

    public boolean isComplete() {
        return amount != null && BigDecimal.ZERO.compareTo(amount) != 1 &&
                toWallet != null && time != null;
    }

    /**
     * TODO добавить поддержку транзакций
     * Создаем запись элемента операции между кошельками, либо над однтм кошельком (пополнение)
     * с последующим увеличением баланса кошелька пополнения
     * и уменьшением баланса кошелька списания (если таковой имеется)
     *
     * @param fromWallet кошелек с которого перечисляют средства
     * @param toWallet   кошлеек на который перечисляют средства
     */
    public boolean post(Context context, Wallet fromWallet, Wallet toWallet) {
        try {
            SQLiteHelper dbHelper = new SQLiteHelper(context);
            SQLiteDatabase database = dbHelper.getWritableDatabase();

            ContentValues walletOperationContentValues = new ContentValues();
            walletOperationContentValues.put(FROM_WALLET_COLUMN_NAME, this.fromWallet);
            walletOperationContentValues.put(TO_WALLET_COLUMN_NAME, this.toWallet);
            walletOperationContentValues.put(AMOUNT_COLUMN_NAME, convertAmountToInt(amount));
            walletOperationContentValues.put(NAME_COLUMN_NAME, name);
            walletOperationContentValues.put(TIME_COLUMN_NAME, time);
            database.insert(WALLET_OPERATION_TABLE_NAME, null, walletOperationContentValues);

            if (fromWallet != null) {
                ContentValues fromWalletContentValues = new ContentValues();
                BigDecimal fromWalletResultAmount = fromWallet.getAmount().subtract(amount);
                fromWalletContentValues.put(Wallet.AMOUNT_COLUMN_NAME, convertAmountToInt(fromWalletResultAmount));
                database.update(Wallet.WALLET_TABLE_NAME, fromWalletContentValues, "id = ?", new String[]{String.valueOf(fromWallet.getId())});
            }

            ContentValues toWalletContentValues = new ContentValues();
            BigDecimal toWalletResultAmount = toWallet.getAmount().add(amount);
            toWalletContentValues.put(Wallet.AMOUNT_COLUMN_NAME, convertAmountToInt(toWalletResultAmount));
            database.update(Wallet.WALLET_TABLE_NAME, toWalletContentValues, "id = ?", new String[]{String.valueOf(toWallet.getId())});

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

    public Integer getFromWallet() {
        return fromWallet;
    }

    public void setFromWallet(Integer fromWallet) {
        this.fromWallet = fromWallet;
    }

    public Integer getToWallet() {
        return toWallet;
    }

    public void setToWallet(Integer toWallet) {
        this.toWallet = toWallet;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}