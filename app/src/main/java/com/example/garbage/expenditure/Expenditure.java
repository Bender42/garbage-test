package com.example.garbage.expenditure;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.garbage.SQLiteHelper;
import com.example.garbage.tools.GarbageTools;
import com.example.garbage.wallet.Wallet;

import java.io.Serializable;

public class Expenditure implements Serializable {

    public static String EXPENDITURE_TABLE_NAME = "expenditure";
    public static String ID_COLUMN_NAME = "id";
    public static String NAME_COLUMN_NAME = "name";
    public static String ICON_COLUMN_NAME = "icon";

    private Integer id;
    private String name;
    private Integer icon;

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

    public ImageButton draw(ViewGroup view, Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        ImageButton buttonNewButton = new ImageButton(context);
        buttonNewButton.setId(getId());
        buttonNewButton.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_menu_add, context.getTheme()));
        buttonNewButton.setMinimumHeight(GarbageTools.convertDpsTpPixels(79, context));
        buttonNewButton.setMinimumWidth(GarbageTools.convertDpsTpPixels(79, context));
        linearLayout.addView(buttonNewButton);

        TextView textView = new TextView(context);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        textView.setText(getName());
        textView.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        linearLayout.addView(textView);

        view.addView(linearLayout);

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
}