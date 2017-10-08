package com.example.garbage.wallet_operation;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.garbage.database.SQLiteHelper;
import com.google.common.base.Joiner;

import java.util.LinkedList;
import java.util.List;

public class WalletOperationsDao {

    private SQLiteHelper dbHelper;

    public WalletOperationsDao(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public List<WalletOperation> getWalletOperations(int walletId, Long fromTime, Long toTime) {
        List<WalletOperation> walletOperations = new LinkedList<>();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(
                WalletOperation.WALLET_OPERATION_TABLE_NAME,
                null,
                getSelection(walletId, fromTime, toTime),
                getSelectionArgs(walletId, fromTime, toTime),
                null,
                null,
                String.format("%s DESC", WalletOperation.TIME_COLUMN_NAME));
        if (cursor.moveToFirst()) {
            do {
                WalletOperation walletOperation = new WalletOperation(cursor);
                walletOperations.add(walletOperation);
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
        return walletOperations;
    }

    private String getSelection(Integer walletId, Long fromTime, Long toTime) {
        List<String> result = new LinkedList<>();
        if (walletId != null) {
            result.add(String.format(
                    "(%s = ? or %s = ?)",
                    WalletOperation.FROM_WALLET_ID_COLUMN_NAME,
                    WalletOperation.TO_WALLET_ID_COLUMN_NAME));
        }
        if (fromTime != null) {
            result.add(String.format("and ? <= %s", WalletOperation.TIME_COLUMN_NAME));
        }
        if (toTime != null) {
            result.add(String.format("and %s <= ?", WalletOperation.TIME_COLUMN_NAME));
        }
        return Joiner.on(" ").join(result);
    }

    private String[] getSelectionArgs(Integer walletId, Long fromTime, Long toTime) {
        List<String> result = new LinkedList<>();
        if (walletId != null) {
            result.add(String.valueOf(walletId));
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
