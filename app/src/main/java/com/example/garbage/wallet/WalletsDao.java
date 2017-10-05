package com.example.garbage.wallet;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.garbage.IWallet;
import com.example.garbage.SQLiteHelper;

import java.util.LinkedHashMap;
import java.util.Map;

public class WalletsDao {

    private final SQLiteHelper dbHelper;

    public WalletsDao(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public Map<Integer, IWallet> getAllWallets() {
        Map<Integer, IWallet> wallets = new LinkedHashMap<>();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(Wallet.WALLET_TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Wallet wallet = new Wallet(cursor);
                wallets.put(wallet.getId(), wallet);
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
        return wallets;
    }

    public Map<Integer, Wallet> getActiveWallets() {
        Map<Integer, Wallet> wallets = new LinkedHashMap<>();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(
                Wallet.WALLET_TABLE_NAME,
                null,
                String.format("%s = ? and %s = ?", Wallet.STATUS_COLUMN_NAME, Wallet.IS_INCOME_ITEM_COLUMN_NAME),
                new String[] {"active", "0"},
                null,
                null,
                null);
        if (cursor.moveToFirst()) {
            do {
                Wallet wallet = new Wallet(cursor);
                wallets.put(wallet.getId(), wallet);
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
        return wallets;
    }
}
