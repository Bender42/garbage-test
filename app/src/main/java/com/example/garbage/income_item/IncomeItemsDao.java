package com.example.garbage.income_item;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.garbage.database.SQLiteHelper;
import com.example.garbage.wallet.Wallet;
import com.example.garbage.wallet_operation.WalletOperation;
import com.example.garbage.wallet_operation.WalletOperationsDao;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

public class IncomeItemsDao {

    private Context context;
    private final SQLiteHelper dbHelper;

    public IncomeItemsDao(Context context) {
        this.context = context;
        this.dbHelper = new SQLiteHelper(context);
    }

    public List<IncomeItem> getActiveIncomeItemsWithAmounts(Long fromTime, Long toTime) {
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
            WalletOperationsDao walletOperationsDao = new WalletOperationsDao(context);
            do {
                IncomeItem incomeItem = new IncomeItem(cursor);
                incomeItem.setAmount(getIncomeItemAmount(incomeItem.getId(), fromTime, toTime, walletOperationsDao));
                incomeItems.add(incomeItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
        return incomeItems;
    }

    private BigDecimal getIncomeItemAmount(Integer incomeItemId, Long fromTime, Long toTime, WalletOperationsDao walletOperationsDao) {
        List<WalletOperation> walletOperationList = walletOperationsDao.getWalletOperations(incomeItemId, fromTime, toTime);
        return getFullAmount(walletOperationList);
    }

    private BigDecimal getFullAmount(List<WalletOperation> list) {
        BigDecimal result = BigDecimal.ZERO;
        for (WalletOperation walletOperation : list) {
            result = result.add(walletOperation.getAmount());
        }
        return result;
    }
}
