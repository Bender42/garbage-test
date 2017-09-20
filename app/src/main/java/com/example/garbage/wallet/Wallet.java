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
    private static String ID_COLUMN_NAME = "id";
    private static String NAME_COLUMN_NAME = "name";
    private static String AMOUNT_COLUMN_NAME = "amount";
    private static String CURRENCY_COLUMN_NAME = "currency";
    private static String ICON_COLUMN_NAME = "icon";

    private Integer id;
    private String name;
    private BigDecimal amount;
    private String currency;
    private Integer iconId;

    public Wallet() {
    }

    public Wallet(Integer id, String name, BigDecimal amount, String currency, Integer iconId) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.currency = currency;
        this.iconId = iconId;
    }

    public Wallet(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(ID_COLUMN_NAME);
        int idName = cursor.getColumnIndex(NAME_COLUMN_NAME);
        int idAmount = cursor.getColumnIndex(AMOUNT_COLUMN_NAME);
        int idCurrency = cursor.getColumnIndex(CURRENCY_COLUMN_NAME);
        int idIcon = cursor.getColumnIndex(ICON_COLUMN_NAME);
        this.id = cursor.getInt(idIndex);
        this.name = cursor.getString(idName);
        this.amount = new BigDecimal(cursor.getInt(idAmount)).divide(new BigDecimal(100)).setScale(2);
        this.currency = cursor.getString(idCurrency);
        this.iconId = cursor.getInt(idIcon);
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

    public Integer getIconId() {
        return iconId;
    }

    public void setIconId(Integer iconId) {
        this.iconId = iconId;
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
        contentValues.put(ICON_COLUMN_NAME, iconId);
        contentValues.put(CURRENCY_COLUMN_NAME, currency);
        contentValues.put(AMOUNT_COLUMN_NAME, amount.multiply(new BigDecimal(100)).intValue());
        database.insert(WALLET_TABLE_NAME, null, contentValues);
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
}