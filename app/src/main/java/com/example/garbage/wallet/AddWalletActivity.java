package com.example.garbage.wallet;

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

import java.math.BigDecimal;

public class AddWalletActivity extends AppCompatActivity {

    private Wallet wallet = new Wallet();

    private EditText editTextWalletName;
    private EditText editTextWalletAmount;
    private ImageButton imageButtonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wallet);
        editTextWalletName = (EditText) findViewById(R.id.edit_text_wallet_name);
        editTextWalletAmount = (EditText) findViewById(R.id.edit_text_wallet_amount);

        imageButtonAdd = (ImageButton) findViewById(R.id.image_button_add);
        imageButtonAdd.setOnClickListener(getAddOnClickListener());

        initSpinnerCurrency();
    }

    private View.OnClickListener getAddOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wallet.setName(editTextWalletName.getText().toString());
                wallet.setAmount(new BigDecimal(editTextWalletAmount.getText().toString()));
                setResult(RESULT_OK, new Intent());
                finish();
            }
        };
    }

    private void initSpinnerCurrency() {
        Spinner spinnerCurrency = (Spinner) findViewById(R.id.spinner_currency);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.currency, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrency.setAdapter(adapter);
        spinnerCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                wallet.setCurrency(((AppCompatTextView) view).getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}