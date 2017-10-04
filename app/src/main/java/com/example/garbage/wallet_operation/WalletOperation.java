package com.example.garbage.wallet_operation;

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
import java.util.Objects;

import static com.example.garbage.tools.GarbageTools.convertAmountToInt;
import static com.example.garbage.tools.GarbageTools.convertIntToAmount;

public class WalletOperation implements IWalletOperation {

    public static String WALLET_OPERATION_TABLE_NAME = "wallet_operation";
    public static String ID_COLUMN_NAME = "id";
    public static String FROM_WALLET_COLUMN_NAME = "from_wallet";
    public static String TO_WALLET_COLUMN_NAME = "to_wallet";
    public static String AMOUNT_COLUMN_NAME = "amount";
    public static String NAME_COLUMN_NAME = "name";
    public static String TIME_COLUMN_NAME = "time";

    private Integer id;
    private String name;
    private BigDecimal amount;
    private Integer fromWalletId;
    private Integer toWalletId;
    private Long time;

    public WalletOperation() {
    }

    public WalletOperation(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(ID_COLUMN_NAME);
        int nameIndex = cursor.getColumnIndex(NAME_COLUMN_NAME);
        int amountIndex = cursor.getColumnIndex(AMOUNT_COLUMN_NAME);
        int fromWalletIndex = cursor.getColumnIndex(FROM_WALLET_COLUMN_NAME);
        int toWalletIndex = cursor.getColumnIndex(TO_WALLET_COLUMN_NAME);
        int timeIndex = cursor.getColumnIndex(TIME_COLUMN_NAME);
        this.id = cursor.getInt(idIndex);
        this.name = cursor.getString(nameIndex);
        this.amount = convertIntToAmount(cursor.getInt(amountIndex));
        this.fromWalletId = cursor.getInt(fromWalletIndex);
        this.toWalletId = cursor.getInt(toWalletIndex);
        this.time = cursor.getLong(timeIndex);
    }

    public boolean isComplete() {
        return amount != null && BigDecimal.ZERO.compareTo(amount) != 1 &&
                toWalletId != null && time != null;
    }

    /**
     * TODO добавить поддержку транзакций
     * Создаем запись элемента операции между кошельками, либо над одним кошельком (пополнение)
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
            walletOperationContentValues.put(FROM_WALLET_COLUMN_NAME, this.fromWalletId);
            walletOperationContentValues.put(TO_WALLET_COLUMN_NAME, this.toWalletId);
            walletOperationContentValues.put(AMOUNT_COLUMN_NAME, convertAmountToInt(amount));
            walletOperationContentValues.put(NAME_COLUMN_NAME, name);
            walletOperationContentValues.put(TIME_COLUMN_NAME, time);
            database.insert(WALLET_OPERATION_TABLE_NAME, null, walletOperationContentValues);

            if (fromWalletId != null) {
                ContentValues fromWalletContentValues = new ContentValues();
                BigDecimal fromWalletResultAmount = fromWallet.getAmount().subtract(amount);
                fromWalletContentValues.put(Wallet.AMOUNT_COLUMN_NAME, convertAmountToInt(fromWalletResultAmount));
                database.update(Wallet.WALLET_TABLE_NAME, fromWalletContentValues, "id = ?", new String[]{String.valueOf(fromWalletId)});
            }

            ContentValues toWalletContentValues = new ContentValues();
            BigDecimal toWalletResultAmount = toWallet.getAmount().add(amount);
            toWalletContentValues.put(Wallet.AMOUNT_COLUMN_NAME, convertAmountToInt(toWalletResultAmount));
            database.update(Wallet.WALLET_TABLE_NAME, toWalletContentValues, "id = ?", new String[]{String.valueOf(toWalletId)});

            dbHelper.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * TODO добавить поддержку транзакций
     * Удаляем запись с элементом операции между кошельками
     * Увеличиваем баланс кошелька с которого списывались средства (если таковой был)
     * Уменьшение баланса с кошелька на который перечислялись средства
     *
     * @param wallets список всех кошельков
     */
    public boolean delete(Context context, Map<Integer, Wallet> wallets) {
        try {
            SQLiteHelper dbHelper = new SQLiteHelper(context);
            SQLiteDatabase database = dbHelper.getWritableDatabase();

            database.delete(WALLET_OPERATION_TABLE_NAME, "id = ?", new String[]{String.valueOf(getId())});

            if (fromWalletId != 0) {
                ContentValues fromWalletContentValues = new ContentValues();
                Wallet fromWallet = wallets.get(fromWalletId);
                BigDecimal fromWalletResultAmount = fromWallet.getAmount().add(getAmount());
                fromWalletContentValues.put(Wallet.AMOUNT_COLUMN_NAME, convertAmountToInt(fromWalletResultAmount));
                database.update(Wallet.WALLET_TABLE_NAME, fromWalletContentValues, "id = ?", new String[]{String.valueOf(fromWalletId)});
            }

            ContentValues toWalletContentValues = new ContentValues();
            Wallet toWallet = wallets.get(toWalletId);
            BigDecimal toWalletResultAmount = toWallet.getAmount().subtract(amount);
            toWalletContentValues.put(Wallet.AMOUNT_COLUMN_NAME, convertAmountToInt(toWalletResultAmount));
            database.update(Wallet.WALLET_TABLE_NAME, toWalletContentValues, "id = ?", new String[]{String.valueOf(toWalletId)});

            dbHelper.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isAddingAmount(Wallet currentWallet) {
        return Objects.equals(getToWalletId(), currentWallet.getId());
    }

    @Override
    public String getDescription(Wallet currentWallet, Map<Integer, Wallet> wallets, Map<Integer, Expenditure> expenditures) {
        if (isAddingAmount(currentWallet)) {
            if (getFromWalletId() == 0) {
                return "пополнение";
            } else {
                return String.format("перевод из %s", wallets.get(getFromWalletId()).getName());
            }
        } else {
            return String.format("перевод в %s", wallets.get(getToWalletId()).getName());
        }
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFromWalletId() {
        return fromWalletId;
    }

    public void setFromWalletId(Integer fromWalletId) {
        this.fromWalletId = fromWalletId;
    }

    public Integer getToWalletId() {
        return toWalletId;
    }

    public void setToWalletId(Integer toWalletId) {
        this.toWalletId = toWalletId;
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

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}