package com.example.garbage.expenditure;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.garbage.database.SQLiteHelper;
import com.example.garbage.wallet.Wallet;

import java.io.Serializable;
import java.math.BigDecimal;

import static com.example.garbage.tools.GarbageTools.convertDpsTpPixels;
import static com.example.garbage.tools.GarbageTools.getFormatAmount;

public class Expenditure implements Serializable {

    public static String EXPENDITURE_TABLE_NAME = "expenditure";
    public static String ID_COLUMN_NAME = "id";
    public static String NAME_COLUMN_NAME = "name";
    public static String ICON_COLUMN_NAME = "icon";

    private Integer id;
    private String name;
    private Integer icon;

    private BigDecimal amount;

    public Expenditure() {
    }

    public Expenditure(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(ID_COLUMN_NAME);
        int idName = cursor.getColumnIndex(NAME_COLUMN_NAME);
        int idIcon = cursor.getColumnIndex(ICON_COLUMN_NAME);
        this.id = cursor.getInt(idIndex);
        this.name = cursor.getString(idName);
        this.icon = cursor.getInt(idIcon);
    }

    public Expenditure(int id, Context context) {
        this.id = id;
        SQLiteHelper dbHelper = new SQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(
                EXPENDITURE_TABLE_NAME,
                new String[] {
                        Wallet.NAME_COLUMN_NAME,
                        Wallet.ICON_COLUMN_NAME
                },
                "id = ?",
                new String[] {String.valueOf(id)},
                null,
                null,
                null);
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(NAME_COLUMN_NAME);
            int iconIndex = cursor.getColumnIndex(ICON_COLUMN_NAME);
            this.name = cursor.getString(nameIndex);
            this.icon = cursor.getInt(iconIndex);
        }
        cursor.close();
        dbHelper.close();
    }

    public boolean isComplete() {
        return name != null && name.length() > 0;
    }

    public boolean post(Context context) {
        SQLiteHelper dbHelper = new SQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME_COLUMN_NAME, name);
        contentValues.put(ICON_COLUMN_NAME, icon);
        database.insert(EXPENDITURE_TABLE_NAME, null, contentValues);
        dbHelper.close();
        return true;
    }

    public int update(Context context) {
        SQLiteHelper dbHelper = new SQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME_COLUMN_NAME, name);
        contentValues.put(ICON_COLUMN_NAME, icon);
        int countUpdate = database.update(EXPENDITURE_TABLE_NAME, contentValues, "id = ?", new String[] { String.valueOf(id) });
        dbHelper.close();
        return countUpdate;
    }

    public int delete(Context context) {
        SQLiteHelper dbHelper = new SQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int countDelete = database.delete(EXPENDITURE_TABLE_NAME, "id = ?", new String[] { String.valueOf(id) });
        dbHelper.close();
        return countDelete;
    }

    public ImageButton draw(ViewGroup view, Context context, SparseArray<BigDecimal> amounts) {
        FrameLayout frameLayout = new FrameLayout(context);

        ImageButton buttonNewButton = new ImageButton(context);
        buttonNewButton.setId(getId());
        buttonNewButton.setMinimumHeight(convertDpsTpPixels(79, context));
        buttonNewButton.setMinimumWidth(convertDpsTpPixels(79, context));
        frameLayout.addView(buttonNewButton);

        TextView textViewName = new TextView(context);
        textViewName.setMinimumHeight(convertDpsTpPixels(79, context));
        textViewName.setMinimumWidth(convertDpsTpPixels(79, context));
        String name = String.format("%s\n\n%s", getName(), getFormatAmount(getAmount()));
        textViewName.setText(name);
        textViewName.setGravity(Gravity.CENTER);
        textViewName.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        textViewName.setTextSize(12);
        frameLayout.addView(textViewName);

        view.addView(frameLayout);

        return buttonNewButton;
    }

    public void updateExpenditure(Expenditure expenditure) {
        if (expenditure == null) {
            setId(null);
            setName(null);
            setIcon(null);
        } else {
            setId(expenditure.getId());
            setName(expenditure.getName());
            setIcon(expenditure.getIcon());
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}