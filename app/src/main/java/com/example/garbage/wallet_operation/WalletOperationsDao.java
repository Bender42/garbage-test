package com.example.garbage.wallet_operation;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.garbage.SQLiteHelper;

import java.util.LinkedList;
import java.util.List;

public class WalletOperationsDao {

    private SQLiteHelper dbHelper;

    public WalletOperationsDao(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public List<WalletOperation> getWalletOperations(int walletId) {
        List<WalletOperation> walletOperations = new LinkedList<>();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(
                WalletOperation.WALLET_OPERATION_TABLE_NAME,
                null,
                String.format("%s = ? or %s = ?", WalletOperation.FROM_WALLET_ID_COLUMN_NAME, WalletOperation.TO_WALLET_ID_COLUMN_NAME),
                new String[] {String.valueOf(walletId), String.valueOf(walletId)},
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
}
