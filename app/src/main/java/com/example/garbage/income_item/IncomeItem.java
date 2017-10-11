package com.example.garbage.income_item;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.garbage.database.SQLiteHelper;
import com.example.garbage.wallet.Wallet;

import java.math.BigDecimal;

import static com.example.garbage.tools.GarbageTools.convertDpsTpPixels;
import static com.example.garbage.tools.GarbageTools.getFormatAmount;

public class IncomeItem extends Wallet {

    public IncomeItem() {
        super();
    }

    public IncomeItem(Cursor cursor) {
        super(cursor);
    }

    public IncomeItem(int id, Context context) {
        super(id, context);
    }

    @Override
    public boolean isComplete() {
        return getName() != null && getName().length() > 0 &&
                getCurrency() != null && getCurrency().length() > 0;
    }

    @Override
    public boolean post(Context context) {
        SQLiteHelper dbHelper = new SQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME_COLUMN_NAME, getName());
        contentValues.put(CURRENCY_COLUMN_NAME, getCurrency());
        contentValues.put(ICON_COLUMN_NAME, getIcon());
        contentValues.put(STATUS_COLUMN_NAME, "active");
        contentValues.put(IS_INCOME_ITEM_COLUMN_NAME, "1");
        database.insert(WALLET_TABLE_NAME, null, contentValues);
        dbHelper.close();
        return true;
    }

    @Override
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
        textViewName.setText(String.format("%s\n\n%s", getName(), getFormatAmount(getAmount())));
        textViewName.setGravity(Gravity.CENTER);
        textViewName.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        textViewName.setTextSize(12);

        view.addView(frameLayout);

        return buttonNewButton;
    }

    public void updateIncomeItem(IncomeItem incomeItem) {
        if (incomeItem == null) {
            setId(null);
            setName(null);
            setAmount((BigDecimal) null);
            setCurrency(null);
            setIcon(null);
            setIsIncomeItem(null);
        } else {
            setId(incomeItem.getId());
            setName(incomeItem.getName());
            setAmount(incomeItem.getAmount());
            setCurrency(incomeItem.getCurrency());
            setIcon(incomeItem.getIcon());
            setIsIncomeItem(incomeItem.getIsIncomeItem());
        }
    }
}
