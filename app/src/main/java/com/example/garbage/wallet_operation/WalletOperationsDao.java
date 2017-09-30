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
                "from_wallet = ? or to_wallet = ?",
                new String[] {String.valueOf(walletId), String.valueOf(walletId)},
                null,
                null,
                null);
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
