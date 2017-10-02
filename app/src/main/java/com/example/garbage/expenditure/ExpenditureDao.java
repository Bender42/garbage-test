package com.example.garbage.expenditure;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.garbage.SQLiteHelper;

import java.util.LinkedHashMap;
import java.util.Map;

public class ExpenditureDao {

    private SQLiteHelper dbHelper;

    public ExpenditureDao(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public Map<Integer, Expenditure> getExpenditures() {
        Map<Integer, Expenditure> expenditures = new LinkedHashMap<>();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(Expenditure.EXPENDITURE_TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Expenditure expenditure = new Expenditure(cursor);
                expenditures.put(expenditure.getId(), new Expenditure(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
        return expenditures;
    }
}
