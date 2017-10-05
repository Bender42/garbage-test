package com.example.garbage.income_item;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.garbage.R;

public class AddIncomeItemActivity extends AppCompatActivity {

    private IncomeItem incomeItem = new IncomeItem();

    private EditText etIncomeItemName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income_item);
        etIncomeItemName = (EditText) findViewById(R.id.et_add_income_item_name);

        ImageButton ibAddIncomeItem = (ImageButton) findViewById(R.id.ib_add_income_item);
        ibAddIncomeItem.setOnClickListener(getAddOnClickListener(this));

        initSpinnerCurrency();
    }

    private View.OnClickListener getAddOnClickListener(final Context context) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incomeItem.setName(etIncomeItemName.getText().toString());
                if (incomeItem.isComplete()) {
                    if (incomeItem.post(context)) {
                        setResult(RESULT_OK, new Intent());
                        finish();
                    }
                }
            }
        };
    }

    private void initSpinnerCurrency() {
        Spinner spinnerCurrency = (Spinner) findViewById(R.id.s_income_item_currency);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.currency, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrency.setAdapter(adapter);
        spinnerCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                incomeItem.setCurrency(((AppCompatTextView) view).getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}
