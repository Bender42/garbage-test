package com.example.garbage.expenditure;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;

import com.example.garbage.cost_item.CostItem;
import com.example.garbage.cost_item.CostItemDao;
import com.example.garbage.database.SQLiteHelper;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExpenditureDao {

    private Context context;
    private SQLiteHelper dbHelper;

    public ExpenditureDao(Context context) {
        this.context = context;
        dbHelper = new SQLiteHelper(context);
    }

    public SparseArray<Expenditure> getAllExpenditures() {
        SparseArray<Expenditure> expenditures = new SparseArray<>();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(Expenditure.EXPENDITURE_TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Expenditure expenditure = new Expenditure(cursor);
                expenditures.put(expenditure.getId(), expenditure);
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
        return expenditures;
    }

    public Map<Integer, Expenditure> getActiveExpenditures() {
        Map<Integer, Expenditure> expenditures = new LinkedHashMap<>();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(
                Expenditure.EXPENDITURE_TABLE_NAME,
                null,
                String.format("%s = ?", Expenditure.STATUS_COLUMN_NAME),
                new String[] {"active"},
                null,
                null,
                null);
        if (cursor.moveToFirst()) {
            do {
                Expenditure expenditure = new Expenditure(cursor);
                expenditures.put(expenditure.getId(), expenditure);
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
        return expenditures;
    }

    public Map<Integer, Expenditure> getActiveExpendituresWithAmounts(Long fromTime, Long toTime) {
        Map<Integer, Expenditure> expenditures = new LinkedHashMap<>();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(
                Expenditure.EXPENDITURE_TABLE_NAME,
                null,
                String.format("%s = ?", Expenditure.STATUS_COLUMN_NAME),
                new String[] {"active"},
                null,
                null,
                null);
        if (cursor.moveToFirst()) {
            CostItemDao costItemDao = new CostItemDao(context);
            do {
                Expenditure expenditure = new Expenditure(cursor);
                expenditure.setAmount(getExpenditureAmount(expenditure.getId(), fromTime, toTime, costItemDao));
                expenditures.put(expenditure.getId(), expenditure);
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
        return expenditures;
    }

    private BigDecimal getExpenditureAmount(Integer expenditureId, Long fromTime, Long toTime, CostItemDao costItemDao) {
        List<CostItem> costItemList = costItemDao.getCostItems(expenditureId, null, fromTime, toTime);
        return getFullAmount(costItemList);
    }

    private BigDecimal getFullAmount(List<CostItem> list) {
        BigDecimal result = BigDecimal.ZERO;
        for (CostItem costItem : list) {
            result = result.add(costItem.getAmount());
        }
        return result;
    }
}
