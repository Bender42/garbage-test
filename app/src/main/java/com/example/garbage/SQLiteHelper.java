package com.example.garbage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.garbage.cost_item.CostItem;
import com.example.garbage.expenditure.Expenditure;
import com.example.garbage.wallet.Wallet;
import com.example.garbage.wallet_operation.WalletOperation;

public class SQLiteHelper extends SQLiteOpenHelper {

    public SQLiteHelper(Context context) {
        super(context, "myDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("create table %s (%s integer primary key autoincrement, %s varchar(255), %s integer, %s varchar(10), %s integer, %s varchar(255));",
                Wallet.WALLET_TABLE_NAME,
                Wallet.ID_COLUMN_NAME,
                Wallet.NAME_COLUMN_NAME,
                Wallet.ICON_COLUMN_NAME,
                Wallet.CURRENCY_COLUMN_NAME,
                Wallet.AMOUNT_COLUMN_NAME,
                Wallet.STATUS_COLUMN_NAME));
        db.execSQL(String.format("create table %s (%s integer primary key autoincrement, %s varchar(255), %s integer, %s integer, %s long, %s integer);",
                CostItem.COST_ITEM_TABLE_NAME,
                CostItem.ID_COLUMN_NAME,
                CostItem.NAME_COLUMN_NAME,
                CostItem.WALLET_COLUMN_NAME,
                CostItem.EXPENDITURE_COLUMN_NAME,
                CostItem.TIME_COLUMN_NAME,
                CostItem.AMOUNT_COLUMN_NAME));
        db.execSQL(String.format("create table %s (%s integer primary key autoincrement, %s varchar(255), %s integer);",
                Expenditure.EXPENDITURE_TABLE_NAME,
                Expenditure.ID_COLUMN_NAME,
                Expenditure.NAME_COLUMN_NAME,
                Expenditure.ICON_COLUMN_NAME));
        db.execSQL(String.format("create table %s (%s integer primary key autoincrement, %s integer, %s integer, %s integer, %s varchar(255), %s long);",
                WalletOperation.WALLET_OPERATION_TABLE_NAME,
                WalletOperation.ID_COLUMN_NAME,
                WalletOperation.FROM_WALLET_COLUMN_NAME,
                WalletOperation.TO_WALLET_COLUMN_NAME,
                WalletOperation.AMOUNT_COLUMN_NAME,
                WalletOperation.NAME_COLUMN_NAME,
                WalletOperation.TIME_COLUMN_NAME));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
