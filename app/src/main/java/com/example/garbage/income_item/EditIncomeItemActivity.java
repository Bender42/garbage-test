package com.example.garbage.income_item;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.garbage.R;

public class EditIncomeItemActivity extends AppCompatActivity {

    private IncomeItem incomeItem;

    private EditText etIncomeItemName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_income_item);

        Intent intent = getIntent();
        int incomeItemId = intent.getIntExtra("incomeItemId", 0);
        if (incomeItemId == 0) {
            //TODO Не работает, приложение падает
            setResult(RESULT_CANCELED, new Intent());
            finish();
        }

        incomeItem = new IncomeItem(incomeItemId, this);

        etIncomeItemName = (EditText) findViewById(R.id.et_edit_income_item_name);
        etIncomeItemName.setText(incomeItem.getName());

        TextView tvIncomeItemCurrency = (TextView) findViewById(R.id.tv_edit_income_item_currency);
        tvIncomeItemCurrency.setText(incomeItem.getCurrency());

        Button bUpdateWallet = (Button) findViewById(R.id.b_update_income_item);
        bUpdateWallet.setOnClickListener(getUpdateOnClickListener(this));

        Button bDeleteWallet = (Button) findViewById(R.id.b_delete_income_item);
        bDeleteWallet.setOnClickListener(getDeleteOnClickListener(this));
    }

    private View.OnClickListener getUpdateOnClickListener(final Context context) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incomeItem.setName(etIncomeItemName.getText().toString());
                if (incomeItem.isComplete()) {
                    if (incomeItem.update(context) == 1) {
                        setResult(RESULT_OK, new Intent());
                        finish();
                    }
                }
            }
        };
    }

    private View.OnClickListener getDeleteOnClickListener(final Context context) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (incomeItem.archive(context) == 1) {
                    setResult(RESULT_OK, new Intent());
                    finish();
                }
            }
        };
    }
}
