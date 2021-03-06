package com.example.garbage.wallet_operation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;

import com.example.garbage.IWallet;
import com.example.garbage.IWalletOperation;
import com.example.garbage.database.SQLiteHelper;
import com.example.garbage.expenditure.Expenditure;
import com.example.garbage.wallet.Wallet;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import static com.example.garbage.tools.GarbageTools.convertAmountToInt;
import static com.example.garbage.tools.GarbageTools.convertIntToAmount;

public class WalletOperation implements IWalletOperation, Serializable {

    public static String WALLET_OPERATION_TABLE_NAME = "wallet_operation";
    public static String ID_COLUMN_NAME = "id";
    public static String FROM_WALLET_ID_COLUMN_NAME = "from_wallet_id";
    public static String TO_WALLET_ID_COLUMN_NAME = "to_wallet_id";
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
        int fromWalletIdIndex = cursor.getColumnIndex(FROM_WALLET_ID_COLUMN_NAME);
        int toWalletIdIndex = cursor.getColumnIndex(TO_WALLET_ID_COLUMN_NAME);
        int timeIndex = cursor.getColumnIndex(TIME_COLUMN_NAME);
        this.id = cursor.getInt(idIndex);
        this.name = cursor.getString(nameIndex);
        this.amount = convertIntToAmount(cursor.getInt(amountIndex));
        this.fromWalletId = cursor.getInt(fromWalletIdIndex);
        this.toWalletId = cursor.getInt(toWalletIdIndex);
        this.time = cursor.getLong(timeIndex);
    }

    public boolean isComplete() {
        return amount != null &&
                BigDecimal.ZERO.compareTo(amount) != 1 &&
                fromWalletId != null &&
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
    public boolean post(Context context, IWallet fromWallet, Wallet toWallet) {
        try {
            SQLiteHelper dbHelper = new SQLiteHelper(context);
            SQLiteDatabase database = dbHelper.getWritableDatabase();

            ContentValues walletOperationContentValues = new ContentValues();
            walletOperationContentValues.put(FROM_WALLET_ID_COLUMN_NAME, fromWalletId);
            walletOperationContentValues.put(TO_WALLET_ID_COLUMN_NAME, toWalletId);
            walletOperationContentValues.put(AMOUNT_COLUMN_NAME, convertAmountToInt(amount));
            walletOperationContentValues.put(NAME_COLUMN_NAME, name);
            walletOperationContentValues.put(TIME_COLUMN_NAME, time);
            database.insert(WALLET_OPERATION_TABLE_NAME, null, walletOperationContentValues);

            if (!fromWallet.isIncomeItem()) {
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
    @Override
    public boolean delete(Context context, SparseArray<IWallet> wallets) {
        try {
            SQLiteHelper dbHelper = new SQLiteHelper(context);
            SQLiteDatabase database = dbHelper.getWritableDatabase();

            database.delete(WALLET_OPERATION_TABLE_NAME, "id = ?", new String[]{String.valueOf(getId())});

            IWallet fromWallet = wallets.get(fromWalletId);
            if (!fromWallet.isIncomeItem()) {
                ContentValues fromWalletContentValues = new ContentValues();
                BigDecimal fromWalletResultAmount = fromWallet.getAmount().add(getAmount());
                fromWalletContentValues.put(Wallet.AMOUNT_COLUMN_NAME, convertAmountToInt(fromWalletResultAmount));
                database.update(Wallet.WALLET_TABLE_NAME, fromWalletContentValues, "id = ?", new String[]{String.valueOf(fromWalletId)});
            }

            ContentValues toWalletContentValues = new ContentValues();
            IWallet toWallet = wallets.get(toWalletId);
            BigDecimal toWalletResultAmount = toWallet.getAmount().subtract(amount);
            toWalletContentValues.put(Wallet.AMOUNT_COLUMN_NAME, convertAmountToInt(toWalletResultAmount));
            database.update(Wallet.WALLET_TABLE_NAME, toWalletContentValues, "id = ?", new String[]{String.valueOf(toWalletId)});

            dbHelper.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean update(Context context, WalletOperation resultWalletOperation, SparseArray<IWallet> wallets) {
        try {
            SQLiteHelper dbHelper = new SQLiteHelper(context);
            SQLiteDatabase database = dbHelper.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(FROM_WALLET_ID_COLUMN_NAME, resultWalletOperation.getFromWalletId());
            contentValues.put(TO_WALLET_ID_COLUMN_NAME, resultWalletOperation.getToWalletId());
            contentValues.put(AMOUNT_COLUMN_NAME, convertAmountToInt(resultWalletOperation.getAmount()));
            contentValues.put(NAME_COLUMN_NAME, resultWalletOperation.getName());
            contentValues.put(TIME_COLUMN_NAME, resultWalletOperation.getTime());
            database.update(
                    WALLET_OPERATION_TABLE_NAME,
                    contentValues,
                    String.format("%s = ?", ID_COLUMN_NAME),
                    new String[] { String.valueOf(id) });

            if (Objects.equals(fromWalletId, resultWalletOperation.getFromWalletId())) {
                if (amount.compareTo(resultWalletOperation.getAmount()) != 0) {
                    IWallet wallet = wallets.get(fromWalletId);
                    if (!wallet.isIncomeItem()) {
                        BigDecimal subtractAmount = amount.subtract(resultWalletOperation.getAmount());
                        BigDecimal fromWalletResultAmount = wallet.getAmount().add(subtractAmount);
                        ((Wallet) wallet).updateAmount(context, fromWalletResultAmount);
                    }
                }
            } else {
                IWallet oldWallet = wallets.get(fromWalletId);
                if (!oldWallet.isIncomeItem()) {
                    BigDecimal fromOldWalletResultAmount = oldWallet.getAmount().add(amount);
                    ((Wallet) oldWallet).updateAmount(context, fromOldWalletResultAmount);
                }
                Wallet newWallet = (Wallet) wallets.get(resultWalletOperation.getFromWalletId());
                BigDecimal fromNewWalletResultAmount = newWallet.getAmount().subtract(resultWalletOperation.getAmount());
                newWallet.updateAmount(context, fromNewWalletResultAmount);
            }

            if (Objects.equals(toWalletId, resultWalletOperation.getToWalletId())) {
                if (amount.compareTo(resultWalletOperation.getAmount()) != 0) {
                    Wallet toWallet = (Wallet) wallets.get(toWalletId);
                    BigDecimal subtractAmount = amount.subtract(resultWalletOperation.getAmount());
                    BigDecimal toWalletResultAmount = toWallet.getAmount().subtract(subtractAmount);
                    toWallet.updateAmount(context, toWalletResultAmount);
                }
            } else {
                Wallet oldToWallet = (Wallet) wallets.get(toWalletId);
                BigDecimal oldToWalletResultAmount = oldToWallet.getAmount().subtract(amount);
                oldToWallet.updateAmount(context, oldToWalletResultAmount);

                Wallet newToWallet = (Wallet) wallets.get(resultWalletOperation.getToWalletId());
                BigDecimal newToWalletResultAmount = newToWallet.getAmount().add(resultWalletOperation.getAmount());
                newToWallet.updateAmount(context, newToWalletResultAmount);
            }

            dbHelper.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isAddingAmount(IWallet currentWallet) {
        if (currentWallet.isIncomeItem()) {
            return true;
        }
        return Objects.equals(getToWalletId(), currentWallet.getId());
    }

    @Override
    public String getDescription(IWallet currentWallet, SparseArray<IWallet> wallets, SparseArray<Expenditure> expenditures) {
        if (isAddingAmount(currentWallet)) {
            if (currentWallet.isIncomeItem()) {
                return String.format("пополнение %s", wallets.get(getToWalletId()).getName());
            } else {
                if (wallets.get(getFromWalletId()).isIncomeItem()) {
                    return String.format("пополнение из %s", wallets.get(getFromWalletId()).getName());
                } else {
                    return String.format("перевод из %s", wallets.get(getFromWalletId()).getName());
                }
            }
        } else {
            return String.format("перевод в %s", wallets.get(getToWalletId()).getName());
        }
    }

    public void update(WalletOperation source) {
        setId(source.getId());
        setFromWalletId(source.getFromWalletId());
        setToWalletId(source.getToWalletId());
        setAmount(source.getAmount());
        setName(source.getName());
        setTime(source.getTime());
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