package com.example.garbage.wallet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.garbage.IWallet;
import com.example.garbage.database.SQLiteHelper;

import java.io.Serializable;
import java.math.BigDecimal;

import static com.example.garbage.tools.GarbageTools.convertAmountToInt;
import static com.example.garbage.tools.GarbageTools.convertDpsTpPixels;
import static com.example.garbage.tools.GarbageTools.convertIntToAmount;

public class Wallet implements IWallet, Serializable {

    public static String WALLET_TABLE_NAME = "wallet";
    public static String ID_COLUMN_NAME = "id";
    public static String NAME_COLUMN_NAME = "name";
    public static String AMOUNT_COLUMN_NAME = "amount";
    public static String CURRENCY_COLUMN_NAME = "currency";
    public static String ICON_COLUMN_NAME = "icon";
    public static String STATUS_COLUMN_NAME = "status";
    public static String IS_INCOME_ITEM_COLUMN_NAME = "is_income_item";

    private Integer id;
    private String name;
    private BigDecimal amount;
    private String currency;
    private Integer icon;
    private String status;
    private Integer isIncomeItem;

    public Wallet() {
    }

    public Wallet(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(ID_COLUMN_NAME);
        int nameIndex = cursor.getColumnIndex(NAME_COLUMN_NAME);
        int amountIndex = cursor.getColumnIndex(AMOUNT_COLUMN_NAME);
        int currencyIndex = cursor.getColumnIndex(CURRENCY_COLUMN_NAME);
        int iconIndex = cursor.getColumnIndex(ICON_COLUMN_NAME);
        int statusIndex = cursor.getColumnIndex(STATUS_COLUMN_NAME);
        int isIncomeItem = cursor.getColumnIndex(IS_INCOME_ITEM_COLUMN_NAME);
        this.id = cursor.getInt(idIndex);
        this.name = cursor.getString(nameIndex);
        this.amount = convertIntToAmount(cursor.getInt(amountIndex));
        this.currency = cursor.getString(currencyIndex);
        this.icon = cursor.getInt(iconIndex);
        this.status = cursor.getString(statusIndex);
        this.isIncomeItem = cursor.getInt(isIncomeItem);
    }

    public Wallet(int id, Context context) {
        this.id = id;
        SQLiteHelper dbHelper = new SQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(
                WALLET_TABLE_NAME,
                null,
                String.format("%s = ?", ID_COLUMN_NAME),
                new String[] {String.valueOf(id)},
                null,
                null,
                null);
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(NAME_COLUMN_NAME);
            int amountIndex = cursor.getColumnIndex(AMOUNT_COLUMN_NAME);
            int currencyIndex = cursor.getColumnIndex(CURRENCY_COLUMN_NAME);
            int iconIndex = cursor.getColumnIndex(ICON_COLUMN_NAME);
            int statusIndex = cursor.getColumnIndex(STATUS_COLUMN_NAME);
            int isIncomeItem = cursor.getColumnIndex(IS_INCOME_ITEM_COLUMN_NAME);
            this.name = cursor.getString(nameIndex);
            this.amount = convertIntToAmount(cursor.getInt(amountIndex));
            this.currency = cursor.getString(currencyIndex);
            this.icon = cursor.getInt(iconIndex);
            this.status = cursor.getString(statusIndex);
            this.isIncomeItem = cursor.getInt(isIncomeItem);
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
        contentValues.put(AMOUNT_COLUMN_NAME, convertAmountToInt(amount));
        contentValues.put(CURRENCY_COLUMN_NAME, currency);
        contentValues.put(ICON_COLUMN_NAME, icon);
        contentValues.put(STATUS_COLUMN_NAME, "active");
        contentValues.put(IS_INCOME_ITEM_COLUMN_NAME, "0");
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
        contentValues.put(AMOUNT_COLUMN_NAME, convertAmountToInt(amount));
        int countUpdate = database.update(
                WALLET_TABLE_NAME,
                contentValues,
                String.format("%s = ?", ID_COLUMN_NAME),
                new String[] { String.valueOf(id) }
        );
        dbHelper.close();
        return countUpdate;
    }

    public int archive(Context context) {
        SQLiteHelper dbHelper = new SQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATUS_COLUMN_NAME, "archive");
        int countArchive = database.update(WALLET_TABLE_NAME, contentValues, "id = ?", new String[] { String.valueOf(id) });
        dbHelper.close();
        return countArchive;
    }

    public int delete(Context context) {
        SQLiteHelper dbHelper = new SQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int countDelete = database.delete(WALLET_TABLE_NAME, "id = ?", new String[] { String.valueOf(id) });
        dbHelper.close();
        return countDelete;
    }

    public ImageButton draw(ViewGroup view, Context context) {
        FrameLayout frameLayout = new FrameLayout(context);

        ImageButton buttonNewButton = new ImageButton(context);
        buttonNewButton.setId(getId());
        buttonNewButton.setMinimumHeight(convertDpsTpPixels(79, context));
        buttonNewButton.setMinimumWidth(convertDpsTpPixels(79, context));
        frameLayout.addView(buttonNewButton);

        TextView textViewName = new TextView(context);
        frameLayout.addView(textViewName);
        textViewName.setMinimumHeight(convertDpsTpPixels(79, context));
        textViewName.setMinimumWidth(convertDpsTpPixels(79, context));
        textViewName.setText(getName());
        textViewName.setGravity(Gravity.CENTER);
        textViewName.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        textViewName.setTextSize(12);

        view.addView(frameLayout);

        return buttonNewButton;
    }

    public void updateWallet(Wallet wallet) {
        if (wallet == null) {
            setId(null);
            setName(null);
            setAmount((BigDecimal) null);
            setCurrency(null);
            setIcon(null);
            setIsIncomeItem(null);
        } else {
            setId(wallet.getId());
            setName(wallet.getName());
            setAmount(wallet.getAmount());
            setCurrency(wallet.getCurrency());
            setIcon(wallet.getIcon());
            setIsIncomeItem(wallet.getIsIncomeItem());
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean isIncomeItem() {
        return 1 == isIncomeItem;
    }

    public Integer getIsIncomeItem() {
        return isIncomeItem;
    }

    public void setIsIncomeItem(Integer isIncomeItem) {
        this.isIncomeItem = isIncomeItem;
    }
}