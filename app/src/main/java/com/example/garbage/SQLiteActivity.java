package com.example.garbage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SQLiteActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextCurrency;

    private EditText editTextId;
    private EditText editTextIdName;
    private EditText editTextIdCurrency;

    private LinearLayout linearLayout;

    SQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite);

        editTextName = (EditText) findViewById(R.id.editTextSqlName);
        editTextCurrency = (EditText) findViewById(R.id.editTextSqlCurrency);

        Button buttonAdd = (Button) findViewById(R.id.buttonSqlAdd);
        buttonAdd.setOnClickListener(getSqlAddListener());
        Button buttonRead = (Button) findViewById(R.id.buttonSqlRead);
        buttonRead.setOnClickListener(getSqlReadListener());
        Button buttonClear = (Button) findViewById(R.id.buttonSqlClear);
        buttonClear.setOnClickListener(getSqlClearListener());
        Button buttonClearLog = (Button) findViewById(R.id.buttonSqlClearLog);
        buttonClearLog.setOnClickListener(getSqlClearLogListener());

        editTextId = (EditText) findViewById(R.id.editTextSqlId);
        editTextIdName = (EditText) findViewById(R.id.editTextSqlIdName);
        editTextIdCurrency = (EditText) findViewById(R.id.editTextSqlIdCurrency);

        Button buttonUpdate = (Button) findViewById(R.id.buttonSqlUpdate);
        buttonUpdate.setOnClickListener(getSqlUpdateListener());
        Button buttonDelete = (Button) findViewById(R.id.buttonSqlDelete);
        buttonDelete.setOnClickListener(getSqlDeleteListener());

        linearLayout = (LinearLayout) findViewById(R.id.sqlInfoLayout);

        dbHelper = new SQLiteHelper(this);
    }

    View.OnClickListener getSqlAddListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues contentValues = new ContentValues();
                String name = editTextName.getText().toString();
                String currency = editTextCurrency.getText().toString();
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                contentValues.put("name", name);
                contentValues.put("currency", currency);
                Long id = database.insert(SQLiteHelper.WALLET_TABLE_NAME, null, contentValues);
                TextView textViewAdd = new TextView(v.getContext());
                textViewAdd.setText(String.format("id inserted row = %s", id));
                linearLayout.addView(textViewAdd);
                dbHelper.close();
            }
        };
    }

    View.OnClickListener getSqlReadListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                Cursor cursor = database.query(SQLiteHelper.WALLET_TABLE_NAME, null, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    int idIndex = cursor.getColumnIndex("id");
                    int nameIndex = cursor.getColumnIndex("name");
                    int currencyIndex = cursor.getColumnIndex("currency");
                    do {
                        TextView textViewRead = new TextView(v.getContext());
                        textViewRead.setText(String.format(
                                "id = %s, name = %s, currency = %s",
                                cursor.getInt(idIndex),
                                cursor.getString(nameIndex),
                                cursor.getString(currencyIndex)));
                        linearLayout.addView(textViewRead);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                dbHelper.close();
            }
        };
    }

    View.OnClickListener getSqlClearListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                int countClear = database.delete(SQLiteHelper.WALLET_TABLE_NAME, null, null);
                TextView textViewClear = new TextView(v.getContext());
                textViewClear.setText(String.format("deleted row count = %s", countClear));
                linearLayout.addView(textViewClear);
                dbHelper.close();
            }
        };
    }

    View.OnClickListener getSqlClearLogListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.removeAllViews();
            }
        };
    }

    View.OnClickListener getSqlDeleteListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textId = editTextId.getText().toString();
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                int countDelete = database.delete(SQLiteHelper.WALLET_TABLE_NAME, "id = ?", new String[] { textId });
                TextView textViewDelete = new TextView(v.getContext());
                textViewDelete.setText(String.format("deleted row count = %s, deleted id = %s", countDelete, textId));
                linearLayout.addView(textViewDelete);
                dbHelper.close();
            }
        };
    }

    View.OnClickListener getSqlUpdateListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues contentValues = new ContentValues();
                String textId = editTextId.getText().toString();
                String nameId = editTextIdName.getText().toString();
                String currencyId = editTextIdCurrency.getText().toString();
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                contentValues.put("name", nameId);
                contentValues.put("currency", currencyId);
                int idUpdate = database.update(SQLiteHelper.WALLET_TABLE_NAME, contentValues, "id = ?", new String[] { textId });
                TextView textViewUpdate = new TextView(v.getContext());
                textViewUpdate.setText(String.format("updated row count = %s, updated id = %s", idUpdate, textId));
                linearLayout.addView(textViewUpdate);
                dbHelper.close();
            }
        };
    }
}
