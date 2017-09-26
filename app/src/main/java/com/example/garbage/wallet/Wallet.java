package com.example.garbage.wallet;

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

import java.math.BigDecimal;

public class Wallet {

    public static String WALLET_TABLE_NAME = "wallet";
    public static String ID_COLUMN_NAME = "id";
    public static String NAME_COLUMN_NAME = "name";
    public static String AMOUNT_COLUMN_NAME = "amount";
    public static String CURRENCY_COLUMN_NAME = "currency";
    public static String ICON_COLUMN_NAME = "icon";

    private Integer id;
    private String name;
    private BigDecimal amount;
    private String currency;
    private Integer icon;

    public Wallet() {
    }

    public Wallet(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(ID_COLUMN_NAME);
        int nameIndex = cursor.getColumnIndex(NAME_COLUMN_NAME);
        int amountIndex = cursor.getColumnIndex(AMOUNT_COLUMN_NAME);
        int currencyIndex = cursor.getColumnIndex(CURRENCY_COLUMN_NAME);
        int iconIndex = cursor.getColumnIndex(ICON_COLUMN_NAME);
        this.id = cursor.getInt(idIndex);
        this.name = cursor.getString(nameIndex);
        this.amount = new BigDecimal(cursor.getInt(amountIndex)).divide(new BigDecimal(100)).setScale(2);
        this.currency = cursor.getString(currencyIndex);
        this.icon = cursor.getInt(iconIndex);
    }

    public Wallet(int id, Context context) {
        this.id = id;
        SQLiteHelper dbHelper = new SQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(
                WALLET_TABLE_NAME,
                new String[] {
                        NAME_COLUMN_NAME,
                        AMOUNT_COLUMN_NAME,
                        CURRENCY_COLUMN_NAME,
                        ICON_COLUMN_NAME
                },
                "id = ?",
                new String[] {String.valueOf(id)},
                null,
                null,
                null);
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(NAME_COLUMN_NAME);
            int amountIndex = cursor.getColumnIndex(AMOUNT_COLUMN_NAME);
            int currencyIndex = cursor.getColumnIndex(CURRENCY_COLUMN_NAME);
            int iconIndex = cursor.getColumnIndex(ICON_COLUMN_NAME);
            this.name = cursor.getString(nameIndex);
            this.amount = new BigDecimal(cursor.getInt(amountIndex)).divide(new BigDecimal(100)).setScale(2);
            this.currency = cursor.getString(currencyIndex);
            this.icon = cursor.getInt(iconIndex);
        }
        cursor.close();
        dbHelper.close();
    }

    public boolean isComplete() {
        return name != null && name.length() > 0 &&
                amount != null && BigDecimal.ZERO.compareTo(amount) != 1 &&
                currency != null && currency.length() > 0;
    }

    public boolean post(Context context) {
        SQLiteHelper dbHelper = new SQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME_COLUMN_NAME, name);
        contentValues.put(ICON_COLUMN_NAME, icon);
        contentValues.put(CURRENCY_COLUMN_NAME, currency);
        contentValues.put(AMOUNT_COLUMN_NAME, amount.multiply(new BigDecimal(100)).intValue());
        database.insert(WALLET_TABLE_NAME, null, contentValues);
        dbHelper.close();
        return true;
    }

    public int update(Context context) {
        SQLiteHelper dbHelper = new SQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME_COLUMN_NAME, name);
        contentValues.put(ICON_COLUMN_NAME, icon);
        contentValues.put(CURRENCY_COLUMN_NAME, currency);
        contentValues.put(AMOUNT_COLUMN_NAME, amount.multiply(new BigDecimal(100)).intValue());
        int countUpdate = database.update(WALLET_TABLE_NAME, contentValues, "id = ?", new String[] { String.valueOf(id) });
        dbHelper.close();
        return countUpdate;
    }

    public int delete(Context context) {
        SQLiteHelper dbHelper = new SQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int countDelete = database.delete(WALLET_TABLE_NAME, "id = ?", new String[] { String.valueOf(id) });
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setAmount(String amount) {
        this.amount = amount != null && amount.length() != 0 ? new BigDecimal(amount) : null;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }
}