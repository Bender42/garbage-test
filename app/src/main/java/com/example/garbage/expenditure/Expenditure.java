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

public class Expenditure {

    public static String EXPENDITURE_TABLE_NAME = "expenditure";
    private static String ID_COLUMN_NAME = "id";
    private static String NAME_COLUMN_NAME = "name";
    private static String ICON_COLUMN_NAME = "icon";

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

    public void draw(ViewGroup view, Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        ImageButton buttonNewButton = new ImageButton(context);
        buttonNewButton.setId(getId());
        buttonNewButton.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_menu_add, context.getTheme()));
        buttonNewButton.setMinimumHeight(GarbageTools.convertDpsTpPixels(80, context));
        buttonNewButton.setMinimumWidth(GarbageTools.convertDpsTpPixels(80, context));
        linearLayout.addView(buttonNewButton);

        TextView textView = new TextView(context);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        textView.setText(getName());
        textView.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        linearLayout.addView(textView);

        view.addView(linearLayout);
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