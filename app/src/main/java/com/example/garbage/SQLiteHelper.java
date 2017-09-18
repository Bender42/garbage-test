package com.example.garbage;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static String WALLET_TABLE_NAME = "wallet";
    public static String CURRENCY_TABLE_NAME = "currency";
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
                "currency integer," +
                "amount decimal(10,2));", WALLET_TABLE_NAME));
        db.execSQL(String.format("create table %s (" +
                "id integer primary key autoincrement, " +
                "numeric_code varchar(10), " +
                "letter_code varchar(10), " +
                "icon integer, " +
                "name varchar(255));", CURRENCY_TABLE_NAME));
        db.execSQL(String.format("create table %s (" +
                "id integer primary key autoincrement, " +
                "name varchar(255), " +
                "expenditure integer, " +
                "time datetime, " +
                "amount decimal(10,2));", COST_ITEM_TABLE_NAME));
        db.execSQL(String.format("create table %s (" +
                "id integer primary key autoincrement, " +
                "name varchar(255), " +
                "icon integer);", EXPENDITURE_TABLE_NAME));
        db.execSQL(String.format("create table %s (" +
                "id integer primary key autoincrement, " +
                "wallet integer, " +
                "from_wallet integer," +
                "amount decimal(10,2)," +
                "description varchar(255));", WALLET_OPERATION_TABLE_NAME));
        insertCurrency(db, "810", "RUR", R.drawable.rub, "Рубль");
        insertCurrency(db, "840", "USD", R.drawable.usd, "Доллар");
        insertCurrency(db, "978", "EUR", R.drawable.eur, "Евро");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void insertCurrency(SQLiteDatabase db,
                                String numericCode,
                                String letterCode,
                                int icon,
                                String name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("numeric_code", numericCode);
        contentValues.put("letter_code", letterCode);
        contentValues.put("icon", icon);
        contentValues.put("name", name);
        db.insert(CURRENCY_TABLE_NAME, null, contentValues);
    }
}
