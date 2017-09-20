package com.example.garbage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.garbage.wallet.Wallet;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static String COST_ITEM_TABLE_NAME = "cost_item";
    public static String EXPENDITURE_TABLE_NAME = "expenditure";
    public static String WALLET_OPERATION_TABLE_NAME = "wallet_operation";

    public SQLiteHelper(Context context) {
        super(context, "myDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("create table %s (" +
                "id integer primary key autoincrement, " +
                "name varchar(255), " +
                "icon integer, " +
                "currency varchar(10)," +
                "amount integer);", Wallet.WALLET_TABLE_NAME));
        db.execSQL(String.format("create table %s (" +
                "id integer primary key autoincrement, " +
                "name varchar(255), " +
                "expenditure integer, " +
                "time datetime, " +
                "currency varchar(10)," +
                "amount integer);", COST_ITEM_TABLE_NAME));
        db.execSQL(String.format("create table %s (" +
                "id integer primary key autoincrement, " +
                "name varchar(255), " +
                "icon integer);", EXPENDITURE_TABLE_NAME));
        db.execSQL(String.format("create table %s (" +
                "id integer primary key autoincrement, " +
                "wallet integer, " +
                "from_wallet integer," +
                "amount integer," +
                "description varchar(255));", WALLET_OPERATION_TABLE_NAME));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
